package com.ai.techradar.web.service.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;

@Path("service")
public class RadarRestService {

	private RadarService service = new RadarServiceImpl();

	@GET
	@Path("/")
	@Produces("application/json")
	public Response getRadars() {

		final List<RadarTO> rs = service.getRadars();

		return Response.ok(rs).build();
	}

	@GET
	@Path("/{radarId}")
	@Produces("application/json")
	public Response getRadarById(@PathParam("radarId") final String radarIdStr) {

		final Long id = Long.parseLong(radarIdStr);

		final RadarTO radar = service.getRadarById(id);

		return Response.ok(radar).build();
	}

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	@Produces("text/html")
	public void uploadFile(
			@Context HttpServletResponse response,
			@Context HttpServletRequest request,
			MultipartFormDataInput input) {

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Serializable id = null;

		try {
			final Map<String, List<InputPart>> formParts = input.getFormDataMap();
			final List<InputPart> inPart = formParts.get("file");
			for (final InputPart inputPart : inPart) {
				try {

					// Retrieve headers, read the Content-Disposition header to obtain the original name of the file
					final MultivaluedMap<String, String> headers = inputPart.getHeaders();
					final String fileName = parseFileName(headers);
					// Handle the body of that part with an InputStream
					final InputStream istream = inputPart.getBody(InputStream.class,null);

					final Radar r = new Radar();
					r.setDateUploaded(new Date());
					r.setFilename(fileName);

					final BufferedReader in = new BufferedReader(new InputStreamReader(istream));

					final CSVParser parser = new CSVParser(in, CSVFormat.RFC4180.withHeader());
					final List<CSVRecord> list = parser.getRecords();

					// TODO validate columns

					final List<Technology> technologies = new ArrayList<Technology>();
					for(final CSVRecord record : list) {
						final Technology technology = new Technology();
						final String name = readString(record.get("Technology"));
						final String quadrant = readString(record.get("Quadrant"));
						final String arc = readString(record.get("Maturity"));
						final MovementEnum movement = readMovement(record.get("moved / no change"));
						final int usageCount = readInt(record.get("project Count"));
						final String url = readString(record.get("Product URL"));
						final String description = readString(record.get("Description"));
						final String detailUrl = readString(record.get("AI URL"));
						final boolean customerStrategic = readBoolean(record.get("Customer strategic"));

						technology.setName(name);
						technology.setQuadrant(quadrant);
						technology.setArc(arc);
						technology.setMovement(movement);
						technology.setUsageCount(usageCount);
						technology.setUrl(url);
						technology.setDescription(description);
						technology.setDetailUrl(detailUrl);
						technology.setCustomerStrategic(customerStrategic);
						technology.setRadar(r);

						session.persist(technology);
						technologies.add(technology);
					}
					r.setTechnologies(technologies);

					id = session.save(r);


				} catch (final IOException e) {
					e.printStackTrace();
					session.getTransaction().rollback();
				}
			}

			session.getTransaction().commit();
			session.close();

			request.setAttribute("result", id.toString());
			request.getRequestDispatcher("/radar.jsp").forward(request, response);

		}catch(final Exception e) {
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

	// Parse Content-Disposition header to get the original file name
	private String parseFileName(MultivaluedMap<String, String> headers) {
		String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
		for (String name : contentDispositionHeader) {
			if ((name.trim().startsWith("filename"))) {
				String[] tmp = name.split("=");
				String fileName = tmp[1].trim().replaceAll("\"","");
				return fileName;
			}
		}
		return "randomName";
	}

}
