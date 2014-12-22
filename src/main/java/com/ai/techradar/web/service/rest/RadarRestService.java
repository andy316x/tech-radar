package com.ai.techradar.web.service.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.RadarTechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("radar")
@Api(value="/radar",description="Radar service")
public class RadarRestService extends AbstractTechRadarRestService {

	private RadarService radarService = (RadarService)SpringStarter.getContext().getBean("RadarService");

	@GET
	@Path("/")
	@ApiOperation(value="Get radars",response=Response.class)
	@Produces("application/json")
	public Response getRadars(@Context SecurityContext securityContext) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final List<RadarTO> rs = radarService.getRadars();

			return Response.ok(rs).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@GET
	@Path("/{radarId}")
	@ApiOperation(value="Get radar by ID",response=Response.class)
	@Produces("application/json")
	public Response getRadarById(
			@Context SecurityContext securityContext,
			@PathParam("radarId") final String radarIdStr) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final Long id = Long.parseLong(radarIdStr);

			final RadarTO radar = radarService.getRadarById(id);

			return Response.ok(radar).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@POST
	@Path("/")
	@ApiOperation(value="Create a radar",response=Response.class)
	@Produces("application/json")
	public Response createRadar(
			@Context SecurityContext securityContext,
			@ApiParam("the radar") final RadarTO radar) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final RadarTO newRadar = radarService.createRadar(radar);

			return Response.ok(newRadar).build();

		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@DELETE
	@Path("/{radarId}")
	@ApiOperation(value="Delete radar by ID",response=Response.class)
	@Produces("application/json")
	public Response deleteRadarById(
			@Context SecurityContext securityContext,
			@PathParam("radarId") final String radarIdStr) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final Long id = Long.parseLong(radarIdStr);

			radarService.deleteRadarById(id);

			return Response.ok().build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@POST
	@Path("/addtech/{radarId}")
	@ApiOperation(value="Add technologies to radar",response=Response.class)
	@Produces("application/json")
	public Response addTechnologiesToRadar(
			@Context SecurityContext securityContext,
			@PathParam("radarId") final Long radarId,
			@ApiParam("the radar") final List<RadarTechnologyTO> radarTechnologies) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final RadarTO newRadar = radarService.addTechnologiesToRadar(radarId, radarTechnologies);
			return Response.ok(newRadar).build();

		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

}
