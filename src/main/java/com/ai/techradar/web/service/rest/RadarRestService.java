package com.ai.techradar.web.service.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechnologyTO;

@Path("service")
public class RadarRestService {

	@GET
	@Path("/")
	@Produces("application/json")
	public Response getRadars() {

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);

		final List<RadarTO> rs = new ArrayList<RadarTO>();
		for(final Radar radar : (List<Radar>)query.list()) {
			final RadarTO r = new RadarTO();
			r.setId(radar.getId());
			r.setFilename(radar.getFilename());
			r.setDateUploaded(radar.getDateUploaded());

			final List<TechnologyTO> ts = new ArrayList<TechnologyTO>();
			for(final Technology technology : radar.getTechnologies()) {
				final TechnologyTO t = new TechnologyTO();
				t.setTechnologyName(technology.getName());
				t.setQuadrantName(technology.getQuadrant());
				t.setArcName(technology.getArc());
				t.setMovement(technology.getMovement());
				t.setRadius(50);
				t.setTheta(45);
				t.setBlipSize(technology.getUsageCount());
				t.setUrl(technology.getUrl());
				ts.add(t);
			}
			r.setTechnologies(ts);

			rs.add(r);
		}

		session.getTransaction().commit();
		session.close();

		return Response.ok(rs).build();
	}

	@GET
	@Path("/{radarId}")
	@Produces("application/json")
	public Object getRadarById(@PathParam("radarId") final String radarId) {
		return Response.ok(readRadar(radarId)).build();
	}
	
	public static final RadarTO readRadar(final String radarId) {
		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		final Criteria query = session.createCriteria(Radar.class);
		query.add(Restrictions.eq("id", Long.parseLong(radarId)));

		final Radar radar = (Radar)query.uniqueResult();

		final RadarTO r = new RadarTO();
		r.setId(radar.getId());
		r.setFilename(radar.getFilename());
		r.setDateUploaded(radar.getDateUploaded());

		final List<TechnologyTO> ts = new ArrayList<TechnologyTO>();
		for(final Technology technology : radar.getTechnologies()) {
			final TechnologyTO t = new TechnologyTO();
			t.setTechnologyName(technology.getName());
			t.setQuadrantName(technology.getQuadrant());
			t.setMovement(technology.getMovement());
			t.setArcName(technology.getArc());
			t.setRadius(50);
			t.setTheta(45);
			t.setBlipSize(technology.getUsageCount());
			t.setUrl(technology.getUrl());
			ts.add(t);
		}
		r.setTechnologies(ts);

		session.getTransaction().commit();
		session.close();

		return r;
	}

	private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/Users/Andy/Desktop";

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	@Produces("text/html")
	public String uploadFile(
			@Context HttpServletResponse response,
	        @Context HttpServletRequest request,
			MultipartFormDataInput input) {

		final Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		try {
			String fileName = "";
			Map<String, List<InputPart>> formParts = input.getFormDataMap();
			List<InputPart> inPart = formParts.get("file");
			for (InputPart inputPart : inPart) {
				try {

					// Retrieve headers, read the Content-Disposition header to obtain the original name of the file
					MultivaluedMap<String, String> headers = inputPart.getHeaders();
					fileName = parseFileName(headers);
					// Handle the body of that part with an InputStream
					InputStream istream = inputPart.getBody(InputStream.class,null);

					final Radar r = new Radar();
					r.setDateUploaded(new Date());
					r.setFilename(fileName);

					final BufferedReader in = new BufferedReader(new InputStreamReader(istream));
					String line = null;

					final StringBuilder responseData = new StringBuilder();
					final List<Technology> technologies = new ArrayList<Technology>();
					while((line = in.readLine()) != null) {
						if(!line.trim().isEmpty()) {
							final String[] cells = line.split(",");
							if(cells.length==8) {
								final Technology technology = new Technology();

								final String name = readString(cells[0].trim());
								final String quadrant = readString(cells[1].trim());
								final String arc = readString(cells[2].trim());
								final MovementEnum movement = readMovement(cells[5].trim());
								final int usageCount = readInt(cells[6].trim());
								final String url = readString(cells[7].trim());

								technology.setName(name);
								technology.setQuadrant(quadrant);
								technology.setArc(arc);
								technology.setMovement(movement);
								technology.setUsageCount(usageCount);
								technology.setUrl(url);
								technology.setRadar(r);

								session.persist(technology);
								technologies.add(technology);
							}
						}
						responseData.append(line);
					}
					r.setTechnologies(technologies);

					fileName = SERVER_UPLOAD_LOCATION_FOLDER + fileName;

					session.persist(r);

					session.getTransaction().commit();
					session.close();
				} catch (final IOException e) {
					e.printStackTrace();
					session.getTransaction().commit();
					session.close();
				}
			}
			
			return "<html><head><meta http-equiv=\"refresh\" content=\"0; url=/radar/\" /></head></html>";

		}catch(Exception e) {
			return "error";
		}
	}

	private static String readString(final String str) {
		return str.substring(1, str.length()-1);
	}

	private static int readInt(final String str) {
		return Integer.parseInt(str.substring(1, str.length()-1));
	}

	private static MovementEnum readMovement(final String str) {
		return MovementEnum.valueOf(str.substring(1, str.length()-1));
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
