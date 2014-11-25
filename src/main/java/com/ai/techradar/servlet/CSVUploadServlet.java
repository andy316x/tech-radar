package com.ai.techradar.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import org.codehaus.jackson.map.ObjectMapper;

import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;

public class CSVUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1199770769064383844L;

	private RadarService service = new RadarServiceImpl();

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/html");

			final PrintWriter writer = response.getWriter();

			final ObjectMapper objmapper = new ObjectMapper();

			try {
				final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

				final Long id = getId(items);

				final RadarTO radar = service.getRadarById(id);
				radar.setTechnologies(new ArrayList<RadarTechnologyTO>());

				for (final FileItem item : items) {
					if (!item.isFormField()) {
						// Process form file field (input type="file").
						final String fieldname = item.getFieldName();
						final String filename = FilenameUtils.getName(item.getName());
						final InputStream istream = item.getInputStream();
						final BufferedReader in = new BufferedReader(new InputStreamReader(istream));

						final CSVParser parser = new CSVParser(in, CSVFormat.RFC4180.withHeader());
						final List<CSVRecord> list = parser.getRecords();

						// TODO validate columns

						Long i = new Long(1);
						final StringBuilder strBuilder = new StringBuilder();
						strBuilder.append("\"technologies:\"[\n");
						for(final CSVRecord record : list) {
							final String name = readString(record.get("Technology"));
							strBuilder.append("   {\"name\": \"" + name + "\"}");
							if(i<list.size()) {
								strBuilder.append(",");
							}
							strBuilder.append("\n");
							final String quadrantName = readString(record.get("Quadrant"));
							final String arcName = readString(record.get("Maturity"));
							final MovementEnum movement = readMovement(record.get("moved / no change"));
							final int usageCount = readInt(record.get("project Count"));
							final String url = readString(record.get("Product URL"));
							final String description = readString(record.get("Description"));
							final String detailUrl = readString(record.get("AI URL"));
							final boolean customerStrategic = readBoolean(record.get("Customer strategic"));

							final RadarTechnologyTO radarTechnology = new RadarTechnologyTO();
							radarTechnology.setId(i++);
							radarTechnology.setTechnology(name);
							radarTechnology.setTechGrouping(quadrantName);
							radarTechnology.setMaturity(arcName);
							radarTechnology.setMovement(movement);
							radar.getTechnologies().add(radarTechnology);
						}
						strBuilder.append("]\n");
						System.out.println(strBuilder.toString());

						parser.close();
					}
				}

				writer.append("<html>");
				writer.append("<body>");

				writer.append("  <script>");
				writer.append("    window.techRadarData = ");
				writer.append(objmapper.writeValueAsString(radar));
				writer.append(";");
				writer.append("  </script>");

				writer.append("</body>");
				writer.append("</html>");

			} catch (final FileUploadException e) {
				throw new ServletException("Cannot parse multipart request.", e);
			}

			response.flushBuffer();
		} catch (Exception e) {
			// TODO log properly
			e.printStackTrace();
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
		return Integer.parseInt(str);
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
