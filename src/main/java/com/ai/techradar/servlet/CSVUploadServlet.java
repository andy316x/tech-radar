package com.ai.techradar.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.ai.techradar.web.service.to.TechGroupingTO;

public class CSVUploadServlet extends HttpServlet {

	public static final String CUSTOMER_STRATEGIC_COLUMN_NAME = "Customer Strategic";
	public static final String AI_URL_COLUMN_NAME = "AI URL";
	public static final String DESCRIPTION_COLUMN_NAME = "Description";
	public static final String PRODUCT_URL_COLUMN_NAME = "Product URL";
	public static final String PROJECT_COUNT_COLUMN_NAME = "Project Count";
	public static final String MOVED_NO_CHANGE_COLUMN_NAME = "Moved / No Change";
	public static final String MATURITY_COLUMN_NAME = "Maturity";
	public static final String QUADRANT_COLUMN_NAME = "Quadrant";
	public static final String TECHNOLOGY_COLUMN_NAME = "Technology";

	public static final List<String> COLUMNS = Arrays.asList(
			CUSTOMER_STRATEGIC_COLUMN_NAME,
			AI_URL_COLUMN_NAME,
			DESCRIPTION_COLUMN_NAME,
			PRODUCT_URL_COLUMN_NAME,
			PROJECT_COUNT_COLUMN_NAME,
			MOVED_NO_CHANGE_COLUMN_NAME,
			MATURITY_COLUMN_NAME,
			QUADRANT_COLUMN_NAME,
			TECHNOLOGY_COLUMN_NAME);

	private static final long serialVersionUID = 1199770769064383844L;

	private RadarService service = (RadarService)SpringStarter.getContext().getBean("RadarService");

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/html");

			final PrintWriter writer = response.getWriter();

			final ObjectMapper objmapper = new ObjectMapper();

			final Set<String> technologiesFound = new HashSet<String>();

			try {
				final Session session = HibernateUtil.getSessionFactory().openSession();

				final UploadResponse uploadResponse = new UploadResponse();
				uploadResponse.setSuccess(true);
				uploadResponse.setErrors(new HashSet<String>());

				final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				final Long id = getId(items);

				final RadarTO radar = service.getRadarById(id);
				radar.setTechnologies(new ArrayList<RadarTechnologyTO>());

				final Map<String, String> techGroupings = new HashMap<String, String>();
				for(final TechGroupingTO tg : radar.getTechGroupings()) {
					techGroupings.put(tg.getName().toLowerCase(), tg.getName());
				}

				final Map<String, String> maturities = new HashMap<String, String>();
				for(final MaturityTO m : radar.getMaturities()) {
					maturities.put(m.getName().toLowerCase(), m.getName());
				}

				for (final FileItem item : items) {
					if (!item.isFormField()) {
						// Process form file field (input type="file").
						final InputStream istream = item.getInputStream();
						final BufferedReader in = new BufferedReader(new InputStreamReader(istream));

						final CSVParser parser = new CSVParser(in, CSVFormat.RFC4180.withHeader());
						final List<CSVRecord> list = parser.getRecords();

						Long i = new Long(1);
						for(final CSVRecord record : list) {
							final RadarTechnologyTO radarTechnology = new RadarTechnologyTO();
							radarTechnology.setId(i++);

							// Technology column
							try {
								final String name = readString(record.get(TECHNOLOGY_COLUMN_NAME));
								if(!StringUtils.isBlank(name)) {
									radarTechnology.setTechnology(name);
									technologiesFound.add(name);
								} else {
									uploadResponse.getErrors().add("Row " + i + " is missing mandatory field ' " + TECHNOLOGY_COLUMN_NAME + "'");
								}
							} catch(final IllegalArgumentException ex) {
								uploadResponse.getErrors().add("Mandatory column '" + TECHNOLOGY_COLUMN_NAME + "' does not exist in file");
							}

							// Tech grouping column
							try {
								final String quadrantName = readString(record.get(QUADRANT_COLUMN_NAME));
								if(!StringUtils.isBlank(quadrantName)) {
									final String canonicalTechGrouping = quadrantName.trim().toLowerCase();
									if(techGroupings.containsKey(canonicalTechGrouping)) {
										radarTechnology.setTechGrouping(techGroupings.get(canonicalTechGrouping));
									} else {
										uploadResponse.getErrors().add("Row " + i + " has tech grouping '" + quadrantName + "' that is not in the radar");
									}
								} else {
									uploadResponse.getErrors().add("Row " + i + " is missing mandatory field '" + QUADRANT_COLUMN_NAME + "'");
								}
							} catch(final IllegalArgumentException ex) {
								uploadResponse.getErrors().add("Mandatory column '" + QUADRANT_COLUMN_NAME + "' does not exist in file");
							}

							// Maturity column
							try {
								final String arcName = readString(record.get(MATURITY_COLUMN_NAME));
								if(!StringUtils.isBlank(arcName)) {
									final String canonicalMaturity = arcName.trim().toLowerCase();
									if(maturities.containsKey(canonicalMaturity)) {
										radarTechnology.setMaturity(maturities.get(canonicalMaturity));
									} else {
										uploadResponse.getErrors().add("Row " + i + " has maturity '" + arcName + "' that is not in the radar");
									}
								} else {
									uploadResponse.getErrors().add("Row " + i + " is missing mandatory field '" + MATURITY_COLUMN_NAME + "'");
								}
							} catch(final IllegalArgumentException ex) {
								uploadResponse.getErrors().add("Mandatory column '" + MATURITY_COLUMN_NAME + "' does not exist in file");
							}

							// Movement column
							try {
								final MovementEnum movement = readMovement(record.get(MOVED_NO_CHANGE_COLUMN_NAME));
								radarTechnology.setMovement(movement);
							} catch(final IllegalArgumentException ex) {
								uploadResponse.getErrors().add("Mandatory column '" + MOVED_NO_CHANGE_COLUMN_NAME + "' does not exist in file");
							}

							if(uploadResponse.getErrors().isEmpty()) {
								radar.getTechnologies().add(radarTechnology);
							}

						}

						parser.close();
					}
				}

				checkTechnologiesExist(technologiesFound, uploadResponse.getErrors(), session);

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

	private void checkTechnologiesExist(final Collection<String> technologies, final Set<String> messages, final Session session) {
		final Criteria query = session.createCriteria(Technology.class);

		query.add(Restrictions.in("name", technologies));

		query.setProjection(Projections.property("name"));

		final Set<String> technologiesInDb = new HashSet<String>();
		for(final String techName : (List<String>)query.list()) {
			technologiesInDb.add(techName);
		}

		for(final String techName : technologies) {
			if(!technologiesInDb.contains(techName)) {
				messages.add("Technology with name '" + techName + "' could not be found in tech radar");
			}
		}
	}

	public static class UploadResponse implements Serializable {
		private static final long serialVersionUID = 2606667506899689378L;
		private boolean success;
		private RadarTO radar;
		private Set<String> errors;
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
		public Set<String> getErrors() {
			return errors;
		}
		public void setErrors(Set<String> errors) {
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

	private static final String readVal(final CSVRecord record, final String key) {
		try {
			return record.get(key);
		} catch(final IllegalArgumentException ex) {
			return null;
		}
	}

}
