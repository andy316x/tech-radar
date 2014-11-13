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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ai.techradar.database.entities.MovementEnum;
import com.ai.techradar.database.entities.Radar;
import com.ai.techradar.database.entities.Technology;
import com.ai.techradar.database.entities.Quadrant;
import com.ai.techradar.database.entities.Arc;
import com.ai.techradar.database.entities.X;
import com.ai.techradar.database.entities.Y;
import com.ai.techradar.database.entities.Z;
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
		
		final Criteria technologyQuery = session.createCriteria(Technology.class);
		final Criteria arcQuery = session.createCriteria(Arc.class);
		final Criteria quadrantQuery = session.createCriteria(Quadrant.class);
		final List<Technology> technologies = technologyQuery.list();
		final List<Arc> arcs = arcQuery.list();
		final List<Quadrant> quadrants = quadrantQuery.list();

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
					
					final List<X> xs = new ArrayList<X>();
					final List<Y> ys = new ArrayList<Y>();
					final List<Z> zs = new ArrayList<Z>();
					
					r.setXs(xs);
					r.setYs(ys);
					r.setZs(zs);

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

						X x = getX(arcName,arcs,r,xs,session);
						Y y = getY(quadrantName,quadrants,r,ys,session);
						
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
							newTechnology.setZs(new ArrayList<Z>());
							technologies.add(newTechnology);
						}
						
						Z z = new Z();
						z.setTechnology(newTechnology); // z -> technology
						newTechnology.getZs().add(z); // technology -> z
						z.setRadar(r); // z -> r
						r.getZs().add(z); // r -> z
						z.setMovement(movement);
						z.setX(x); // z -> x
						x.getZs().add(z); // x -> z
						z.setY(y); // z -> y
						y.getZs().add(z); // y -> z

						session.persist(newTechnology);
						session.persist(x);
						session.persist(y);
						session.persist(z);
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
	
	private X getX(String arcName, List<Arc> arcs, Radar r, List<X> xs, Session session){
		Arc newArc = null;
		X newX = null;
		for(Arc arc: arcs){
			if(arc.getName().equals(arcName)){
				newArc = arc;
			}
		}
	
		if(newArc == null){
			newArc = new Arc();
			newArc.setName(arcName);
			arcs.add(newArc);
			newX = new X();
			newX.setArc(newArc); // x -> arc
			List<X> newArcXs = new ArrayList<X>();
			newArcXs.add(newX);
			newArc.setXs(newArcXs);  // arc -> x
			newX.setRadar(r); // x -> r
			r.getXs().add(newX); // r -> x
			newX.setZs(new ArrayList<Z>());
		}else{ // Arc exists but may not be added to this radar
			for(X x : xs){
				if(x.getArc().equals(newArc)){
					newX = x;
				}
			}	
		
			if(newX == null){
				newX = new X();
				newX.setArc(newArc); // x -> arc
				List<X> newArcXs = new ArrayList<X>();
				newArcXs.add(newX);
				newArc.setXs(newArcXs);  // arc -> x
				newX.setRadar(r); // x -> r
				r.getXs().add(newX); // r -> x
				newX.setZs(new ArrayList<Z>());
			}
		}
		session.persist(newArc);
		return newX;
	}

	private Y getY(String quadrantName, List<Quadrant> quadrants, Radar r, List<Y> ys, Session session){
		Quadrant newQuadrant = null;
		Y newY = null;
		for(Quadrant quadrant: quadrants){
			if(quadrant.getName().equals(quadrantName)){
				newQuadrant = quadrant;
			}
		}
		
		if(newQuadrant == null){
			newQuadrant = new Quadrant();
			newQuadrant.setName(quadrantName);
			quadrants.add(newQuadrant);
			newY = new Y();
			newY.setQuadrant(newQuadrant); // y -> quadrant
			List<Y> newQuadrantYs = new ArrayList<Y>();
			newQuadrantYs.add(newY);
			newQuadrant.setYs(newQuadrantYs);  // quadrant -> y
			newY.setRadar(r); // y -> r
			r.getYs().add(newY); // r -> y
			newY.setZs(new ArrayList<Z>());
		}else{ // Quadrant exists but may not be added to this radar
			for(Y y : ys){
				if(y.getQuadrant().equals(newQuadrant)){
					newY = y;
				}
			}
			
			if(newY == null){
				newY = new Y();
				newY.setQuadrant(newQuadrant); // y -> quadrant
				List<Y> newQuadrantYs = new ArrayList<Y>();
				newQuadrantYs.add(newY);
				newQuadrant.setYs(newQuadrantYs);  // quadrant -> y
				newY.setRadar(r); // y -> r
				r.getYs().add(newY); // r -> y
				newY.setZs(new ArrayList<Z>());
			}
		}
		session.persist(newQuadrant);
		return newY;
	}	
}
