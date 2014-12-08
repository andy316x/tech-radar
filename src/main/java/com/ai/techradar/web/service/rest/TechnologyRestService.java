package com.ai.techradar.web.service.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

import com.ai.techradar.service.ValidationException;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.ai.techradar.web.service.to.UserTechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("technology")
@Api(value="/technology",description="Radar service")
public class TechnologyRestService extends AbstractTechRadarRestService {

	@GET
	@Path("/")
	@ApiOperation(value="Get technologies",response=Response.class)
	@Produces("application/json")
	public Response getTechnologies(@Context SecurityContext securityContext) {

		try {

			final List<TechnologyTO> rs = getTechnologyService(getUser(securityContext)).getTechnologies();

			return Response.ok(rs).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new WebApplicationException(e);
		} catch (InstantiationException e) {
			throw new WebApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new WebApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new WebApplicationException(e);
		}

	}

	@GET
	@Path("/{technologyId}")
	@ApiOperation(value="Get technology by ID",response=Response.class)
	@Produces("application/json")
	public Response getRadarById(
			@Context SecurityContext securityContext,
			@PathParam("technologyId") final String technologyIdStr) {

		try {

			final Long id = Long.parseLong(technologyIdStr);

			final TechnologyTO technology = getTechnologyService(getUser(securityContext)).getTechnologyById(id);

			return Response.ok(technology).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new WebApplicationException(e);
		} catch (InstantiationException e) {
			throw new WebApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new WebApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new WebApplicationException(e);
		}

	}

	@POST
	@Path("/")
	@ApiOperation(value="Create a technology",response=Response.class)
	@Produces("application/json")
	public Response createTechnology(
			@Context SecurityContext securityContext,
			@ApiParam("the radar") final TechnologyTO technology) {

		try {

			final TechnologyTO newTechnology = getTechnologyService(getUser(securityContext)).createTechnology(technology);

			return Response.ok(newTechnology).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new WebApplicationException(e);
		} catch (InstantiationException e) {
			throw new WebApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new WebApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new WebApplicationException(e);
		}

	}

	@POST
	@Path("/{technologyId}/user")
	@ApiOperation(value="Rate technology",response=Response.class)
	@Produces("application/json")
	public Response setUserTechnology(
			@Context SecurityContext securityContext,
			@PathParam("technologyId") final String technologyIdStr,
			@ApiParam("the radar") final UserTechnologyTO userTechnology) {

		try {

			final Long id = Long.parseLong(technologyIdStr);

			final UserTechnologyTO newUserTechnology = getTechnologyService(getUser(securityContext)).setUserTechnology(id, userTechnology);

			return Response.ok(newUserTechnology).build();

		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new WebApplicationException(e);
		} catch (InstantiationException e) {
			throw new WebApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new WebApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new WebApplicationException(e);
		}

	}
	
	@GET
	@Path("/{technologyId}/user")
	@ApiOperation(value="Get users who use the technology",response=Response.class)
	@Produces("application/json")
	public Response getTechnologyUsers(
			@Context SecurityContext securityContext,
			@PathParam("technologyId") final String technologyIdStr) {

		try {

			final Long technologyId = Long.parseLong(technologyIdStr);

			final List<UserTechnologyTO> userTechnologies = getTechnologyService(getUser(securityContext)).getTechnologyUsers(technologyId);

			return Response.ok(userTechnologies).build();

		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new WebApplicationException(e);
		} catch (InstantiationException e) {
			throw new WebApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new WebApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new WebApplicationException(e);
		}

	}

}
