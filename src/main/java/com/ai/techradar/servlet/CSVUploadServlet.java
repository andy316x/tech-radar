package com.ai.techradar.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.Maturity;
import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.TechGrouping;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;

public class CSVUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1199770769064383844L;

	private RadarService service = new RadarServiceImpl("");

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/html");

			final PrintWriter writer = response.getWriter();

			final ObjectMapper objmapper = new ObjectMapper();
			
			final Set<String> technologiesFound = new HashSet<String>();
			final Set<String> techGroupingsFound = new HashSet<String>();
			final Set<String> maturitiesFound = new HashSet<String>();

			try {
				final Session session = HibernateUtil.getSessionFactory().openSession();
				
				final UploadResponse uploadResponse = new UploadResponse();
				uploadResponse.setSuccess(true);
				uploadResponse.setErrors(new ArrayList<String>());
				
				final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				final Long id = getId(items);

				final RadarTO radar = service.getRadarById(id);
				radar.setTechnologies(new ArrayList<RadarTechnologyTO>());

				for (final FileItem item : items) {
					if (!item.isFormField()) {
						// Process form file field (input type="file").
						final InputStream istream = item.getInputStream();
						final BufferedReader in = new BufferedReader(new InputStreamReader(istream));

						final CSVParser parser = new CSVParser(in, CSVFormat.RFC4180.withHeader());
						final List<CSVRecord> list = parser.getRecords();
						
						try {
							
							Long i = new Long(1);
							for(final CSVRecord record : list) {
								final String name = readString(record.get("Technology"));
								final String quadrantName = readString(record.get("Quadrant"));
								final String arcName = readString(record.get("Maturity"));
								final MovementEnum movement = readMovement(record.get("moved / no change"));
								
								if(!StringUtils.isBlank(name)) {
									technologiesFound.add(name);
								} else {
									uploadResponse.getErrors().add("Mandatory field 'Technology' is blank");
								}
								
								if(!StringUtils.isBlank(quadrantName)) {
									techGroupingsFound.add(quadrantName);
								} else {
									uploadResponse.getErrors().add("Mandatory field 'Quadrant' is blank");
								}
								
								if(!StringUtils.isBlank(arcName)) {
									maturitiesFound.add(arcName);
								} else {
									uploadResponse.getErrors().add("Mandatory field 'Maturity' is blank");
								}

								final RadarTechnologyTO radarTechnology = new RadarTechnologyTO();
								radarTechnology.setId(i++);
								radarTechnology.setTechnology(name);
								radarTechnology.setTechGrouping(quadrantName);
								radarTechnology.setMaturity(arcName);
								radarTechnology.setMovement(movement);
								radar.getTechnologies().add(radarTechnology);
							}
							
						} catch(final IllegalArgumentException ex) {
							uploadResponse.getErrors().add("Mandatory column heading not found, expected \"Technology\", \"Quadrant\", \"Maturity\", \"moved / no change\"");
						}

						parser.close();
					}
				}
				
				checkTechnologiesExist(technologiesFound, uploadResponse.getErrors(), session);
				checkMaturitiesExist(maturitiesFound, uploadResponse.getErrors(), session);
				checkTechGroupingsExist(techGroupingsFound, uploadResponse.getErrors(), session);
				
				uploadResponse.setRadar(radar);
				if(!uploadResponse.getErrors().isEmpty()) {
					uploadResponse.setSuccess(false);
				}

				writer.append("<html>");
				writer.append("<body>");

				writer.append("  <script>");
				writer.append("    window.techRadarData = ");
				writer.append(objmapper.writeValueAsString(uploadResponse));
				writer.append(";");
				writer.append("  </script>");

				writer.append("</body>");
				writer.append("</html>");
				
				session.close();

			} catch (final FileUploadException e) {
				throw new ServletException("Cannot parse multipart request.", e);
			}

			response.flushBuffer();
		} catch (final Exception e) {
			// TODO log properly
			e.printStackTrace();
		}

	}
	
	private void checkTechnologiesExist(final Collection<String> technologies, final List<String> messages, final Session session) {
		final Criteria query = session.createCriteria(Technology.class);

		query.add(Restrictions.in("name", technologies));
		
		query.setProjection(Projections.property("name"));
		
		final Set<String> technologiesInDb = new HashSet<String>();
		for(final String techName : (List<String>)query.list()) {
			technologiesInDb.add(techName);
		}
		
		for(final String techName : technologies) {
			if(!technologiesInDb.contains(techName)) {
				messages.add("Technology with name '" + techName + "' could not be found");
			}
		}
	}
	
	private void checkMaturitiesExist(final Collection<String> maturities, final List<String> messages, final Session session) {
		final Criteria query = session.createCriteria(Maturity.class);

		query.add(Restrictions.in("name", maturities));
		
		query.setProjection(Projections.property("name"));
		
		final Set<String> maturitiesInDb = new HashSet<String>();
		for(final String maturityName : (List<String>)query.list()) {
			maturitiesInDb.add(maturityName);
		}
		
		for(final String maturityName : maturities) {
			if(!maturitiesInDb.contains(maturityName)) {
				messages.add("Maturity with name '" + maturityName + "' could not be found");
			}
		}
	}
	
	private void checkTechGroupingsExist(final Collection<String> techGroupings, final List<String> messages, final Session session) {
		final Criteria query = session.createCriteria(TechGrouping.class);

		query.add(Restrictions.in("name", techGroupings));
		
		query.setProjection(Projections.property("name"));
		
		final Set<String> techGroupingsInDb = new HashSet<String>();
		for(final String techGroupingName : (List<String>)query.list()) {
			techGroupingsInDb.add(techGroupingName);
		}
		
		for(final String techGroupingName : techGroupings) {
			if(!techGroupingsInDb.contains(techGroupingName)) {
				messages.add("Tech grouping with name '" + techGroupingName + "' could not be found");
			}
		}
	}
	
	public static class UploadResponse implements Serializable {
		private static final long serialVersionUID = 2606667506899689378L;
		private boolean success;
		private RadarTO radar;
		private List<String> errors;
		public UploadResponse() {
			
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public RadarTO getRadar() {
			return radar;
		}
		public void setRadar(RadarTO radar) {
			this.radar = radar;
		}
		public List<String> getErrors() {
			return errors;
		}
		public void setErrors(List<String> errors) {
			this.errors = errors;
		}
	}

	private static String readString(final String str) {
		if(str==null) {
			return null;
		}
		if(str.trim().length()==0) {
			return null;
		}

		return str;
	}

	private static int readInt(final String str) {
		if(str==null) {
			return 0;
		}
		if(str.trim().length()==0) {
			return 0;
		}
		
		try {
			return Integer.parseInt(str);
		} catch(final NumberFormatException ex) {
			// TODO we should real inform the user
			System.out.println("'" + str + "' is not a valid int");
		}
		
		return 0;
	}

	private static MovementEnum readMovement(final String str) {
		if(str==null) {
			return null;
		}
		if(str.trim().length()==0) {
			return null;
		}

		if(str.trim().substring(0, 1).equalsIgnoreCase("m")) {
			return MovementEnum.c;
		}
		if(str.trim().substring(0, 1).equalsIgnoreCase("n")) {
			return MovementEnum.t;
		}

		return null;
	}

	private static boolean readBoolean(final String str) {
		if(str==null) {
			return false;
		}
		if(str.trim().length()==0) {
			return false;
		}

		if(str.trim().substring(0, 1).equalsIgnoreCase("y")) {
			return true;
		}

		return false;
	}

	private static Long getId(final List<FileItem> items) {
		for (final FileItem item : items) {
			if (item.isFormField()) {
				final String fieldname = item.getFieldName();
				final String fieldvalue = item.getString();
				if(fieldname.equals("id")) {
					return Long.parseLong(fieldvalue);
				}
			}
		}
		return null;
	}

}
