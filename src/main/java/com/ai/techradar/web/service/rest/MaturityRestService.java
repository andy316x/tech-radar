package com.ai.techradar.web.service.rest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.web.service.to.MaturityTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("maturity")
@Api(value="/maturity",description="Radar service")
public class MaturityRestService extends AbstractTechRadarRestService {

	@GET
	@Path("/")
	@ApiOperation(value="Get maturities",response=Response.class)
	@Produces("application/json")
	public Response getMaturities(@Context SecurityContext securityContext) {

		try {

			final List<MaturityTO> rs = getMaturityService(getUser(securityContext)).getMaturities();

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

	@POST
	@Path("/")
	@ApiOperation(value="Create a maturity",response=Response.class)
	@Produces("application/json")
	public Response createMaturity(
			@Context SecurityContext securityContext,
			@ApiParam("the radar") final MaturityTO maturity) {

		try {

			final MaturityTO newMaturity = getMaturityService(getUser(securityContext)).createMaturity(maturity);

			return Response.ok(newMaturity).build();

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
