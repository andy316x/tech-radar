package com.ai.techradar.web.service.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ai.techradar.database.entities.Maturity;
import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.RadarMaturity;
import com.ai.techradar.database.entities.RadarTechGrouping;
import com.ai.techradar.database.entities.RadarTechnology;
import com.ai.techradar.database.entities.TechGrouping;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.hibernate.HibernateUtil;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@SuppressWarnings("unchecked")
@Path("radar")
@Api(value="/radar",description="Radar service")
public class RadarRestService {

	private RadarService service = new RadarServiceImpl();

	@GET
	@Path("/")
	@ApiOperation(value="Get radars",response=Response.class)
	@Produces("application/json")
	public Response getRadars() {

		final List<RadarTO> rs = service.getRadars();

		return Response.ok(rs).build();
	}

	@GET
	@Path("/{radarId}")
	@ApiOperation(value="Get radar by ID",response=Response.class)
	@Produces("application/json")
	public Response getRadarById(@PathParam("radarId") final String radarIdStr) {

		final Long id = Long.parseLong(radarIdStr);

		final RadarTO radar = service.getRadarById(id);

		return Response.ok(radar).build();
	}

	@POST
	@Path("/")
	@ApiOperation(value="Create a radar",response=Response.class)
	@Produces("application/json")
	public Response createRadar(@ApiParam("the radar") final RadarTO radar) {

		try {
			final RadarTO newRadar = service.createRadar(radar);
			return Response.ok(newRadar).build();
		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		}

	}

	@DELETE
	@Path("/{radarId}")
	@ApiOperation(value="Delete radar by ID",response=Response.class)
	@Produces("application/json")
	public Response deleteRadarById(@PathParam("radarId") final String radarIdStr) {

		final Long id = Long.parseLong(radarIdStr);

		service.deleteRadarById(id);

		return Response.ok().build();
	}

	@POST
	@Path("/addtech/{radarId}")
	@ApiOperation(value="Add technologies to radar",response=Response.class)
	@Produces("application/json")
	public Response addTechnologiesToRadar(
			@PathParam("radarId") final Long radarId,
			@ApiParam("the radar") final List<RadarTechnologyTO> radarTechnologies) {

		try {
			final RadarTO newRadar = service.addTechnologiesToRadar(radarId, radarTechnologies);
			return Response.ok(newRadar).build();
		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		}

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

		final Criteria technologyQuery = session.createCriteria(Technology.class);
		final Criteria arcQuery = session.createCriteria(Maturity.class);
		final Criteria quadrantQuery = session.createCriteria(TechGrouping.class);
		final List<Technology> technologies = technologyQuery.list();
		final List<Maturity> arcs = arcQuery.list();
		final List<TechGrouping> quadrants = quadrantQuery.list();

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

					final List<RadarMaturity> radarMaturities = new ArrayList<RadarMaturity>();
					final List<RadarTechGrouping> radarTechGroupings = new ArrayList<RadarTechGrouping>();
					final List<RadarTechnology> radarTechnologies = new ArrayList<RadarTechnology>();

					r.setRadarMaturities(radarMaturities);
					r.setRadarTechGroupings(radarTechGroupings);
					r.setRadarTechnologies(radarTechnologies);

					final BufferedReader in = new BufferedReader(new InputStreamReader(istream));

					final CSVParser parser = new CSVParser(in, CSVFormat.RFC4180.withHeader());
					final List<CSVRecord> list = parser.getRecords();

					// TODO validate columns

					for(final CSVRecord record : list) {
						final String name = readString(record.get("Technology"));
						final String quadrantName = readString(record.get("Quadrant"));
						final String arcName = readString(record.get("Maturity"));
						final MovementEnum movement = readMovement(record.get("moved / no change"));
						final int usageCount = readInt(record.get("project Count"));
						final String url = readString(record.get("Product URL"));
						final String description = readString(record.get("Description"));
						final String detailUrl = readString(record.get("AI URL"));
						final boolean customerStrategic = readBoolean(record.get("Customer strategic"));

						RadarMaturity radarMaturity = getRadarMaturity(arcName,arcs,r,radarMaturities,session);
						RadarTechGrouping radarTechGrouping = getY(quadrantName,quadrants,r,radarTechGroupings,session);

						Technology newTechnology = null;
						for(Technology technology: technologies){
							if(technology.getName().equals(name)){
								newTechnology = technology;
							}
						}

						// TODO currently won't update existing technologies etc.

						if(newTechnology == null){
							newTechnology = new Technology();
							newTechnology.setName(name);
							newTechnology.setUsageCount(usageCount);
							newTechnology.setUrl(url);
							newTechnology.setDescription(description);
							newTechnology.setDetailUrl(detailUrl);
							newTechnology.setCustomerStrategic(customerStrategic);
							newTechnology.setRadarTechnologies(new ArrayList<RadarTechnology>());
							technologies.add(newTechnology);
						}

						RadarTechnology radarTechnology = new RadarTechnology();
						radarTechnology.setTechnology(newTechnology);
						newTechnology.getRadarTechnologies().add(radarTechnology);
						radarTechnology.setRadar(r);
						r.getRadarTechnologies().add(radarTechnology);
						radarTechnology.setMovement(movement);
						radarTechnology.setRadarMaturity(radarMaturity);
						radarMaturity.getRadarTechnologies().add(radarTechnology);
						radarTechnology.setRadarTechGrouping(radarTechGrouping);
						radarTechGrouping.getRadarTechnologies().add(radarTechnology);

						session.persist(newTechnology);
						session.persist(radarMaturity);
						session.persist(radarTechGrouping);
						session.persist(radarTechnology);
					}

					id = session.save(r);
					parser.close();
				} catch (final IOException e) {
					e.printStackTrace();
					session.getTransaction().rollback();
				}
			}

			session.getTransaction().commit();
			session.close();

			//request.setAttribute("result", id.toString());
			//request.getRequestDispatcher("/index.jsp").forward(request, response);

			response.sendRedirect("/radar");

			//return Response.ok().build();

		}catch(final Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			//return Response.ok(sw.toString(), MediaType.TEXT_PLAIN).build();
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

	private RadarMaturity getRadarMaturity(String arcName, List<Maturity> arcs, Radar r, List<RadarMaturity> xs, Session session){
		Maturity newMaturity = null;
		RadarMaturity newRadarMaturity = null;
		for(Maturity arc: arcs){
			if(arc.getName().equals(arcName)){
				newMaturity = arc;
			}
		}

		if(newMaturity == null){
			newMaturity = new Maturity();
			newMaturity.setName(arcName);
			arcs.add(newMaturity);
			newRadarMaturity = new RadarMaturity();
			newRadarMaturity.setMaturity(newMaturity); 
			List<RadarMaturity> newRadarMaturities = new ArrayList<RadarMaturity>();
			newRadarMaturities.add(newRadarMaturity);
			newMaturity.setRadarMaturities(newRadarMaturities);
			newRadarMaturity.setRadar(r);
			r.getRadarMaturities().add(newRadarMaturity);
			newRadarMaturity.setRadarTechnologies(new ArrayList<RadarTechnology>());
		}else{ // Maturity exists but may not be added to this radar
			for(RadarMaturity x : xs){
				if(x.getMaturity().equals(newMaturity)){
					newRadarMaturity = x;
				}
			}	

			if(newRadarMaturity == null){
				newRadarMaturity = new RadarMaturity();
				newRadarMaturity.setMaturity(newMaturity);
				List<RadarMaturity> newRadarMaturities = new ArrayList<RadarMaturity>();
				newRadarMaturities.add(newRadarMaturity);
				newMaturity.setRadarMaturities(newRadarMaturities);
				newRadarMaturity.setRadar(r);
				r.getRadarMaturities().add(newRadarMaturity);
				newRadarMaturity.setRadarTechnologies(new ArrayList<RadarTechnology>());
			}
		}
		session.persist(newMaturity);
		return newRadarMaturity;
	}

	private RadarTechGrouping getY(String quadrantName, List<TechGrouping> quadrants, Radar r, List<RadarTechGrouping> ys, Session session){
		TechGrouping newTechGrouping = null;
		RadarTechGrouping newRadarTechGrouping = null;
		for(TechGrouping quadrant: quadrants){
			if(quadrant.getName().equals(quadrantName)){
				newTechGrouping = quadrant;
			}
		}

		if(newTechGrouping == null){
			newTechGrouping = new TechGrouping();
			newTechGrouping.setName(quadrantName);
			quadrants.add(newTechGrouping);
			newRadarTechGrouping = new RadarTechGrouping();
			newRadarTechGrouping.setTechGrouping(newTechGrouping);
			List<RadarTechGrouping> newRadarTechGroupings = new ArrayList<RadarTechGrouping>();
			newRadarTechGroupings.add(newRadarTechGrouping);
			newTechGrouping.setRadarTechGroupings(newRadarTechGroupings);
			newRadarTechGrouping.setRadar(r);
			r.getRadarTechGroupings().add(newRadarTechGrouping);
			newRadarTechGrouping.setRadarTechnologies(new ArrayList<RadarTechnology>());
		}else{ // Tech grouping exists but may not be added to this radar
			for(RadarTechGrouping y : ys){
				if(y.getTechGrouping().equals(newTechGrouping)){
					newRadarTechGrouping = y;
				}
			}

			if(newRadarTechGrouping == null){
				newRadarTechGrouping = new RadarTechGrouping();
				newRadarTechGrouping.setTechGrouping(newTechGrouping);
				List<RadarTechGrouping> newRadarTechGroupings = new ArrayList<RadarTechGrouping>();
				newRadarTechGroupings.add(newRadarTechGrouping);
				newTechGrouping.setRadarTechGroupings(newRadarTechGroupings);
				newRadarTechGrouping.setRadar(r); 
				r.getRadarTechGroupings().add(newRadarTechGrouping);
				newRadarTechGrouping.setRadarTechnologies(new ArrayList<RadarTechnology>());
			}
		}
		session.persist(newTechGrouping);
		return newRadarTechGrouping;
	}	
}
