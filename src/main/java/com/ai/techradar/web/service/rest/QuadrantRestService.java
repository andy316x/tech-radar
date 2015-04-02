package com.ai.techradar.web.service.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.QuadrantService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.QuadrantTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("quadrant")
@Api(value="/quadrant",description="Quadrant service")
public class QuadrantRestService extends AbstractTechRadarRestService {

	private QuadrantService quadrantService = (QuadrantService)SpringStarter.getContext().getBean("QuadrantService");

	@GET
	@Path("/")
	@ApiOperation(value="Get quadrants",response=Response.class)
	@Produces("application/json")
	public Response getQuadrants(@Context SecurityContext securityContext) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final List<QuadrantTO> rs = quadrantService.getQuadrants();

			return Response.ok(rs).build();

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
	@ApiOperation(value="Create a quadrant",response=Response.class)
	@Produces("application/json")
	public Response createQuadrant(
			@Context SecurityContext securityContext,
			@ApiParam("the quadrant") final QuadrantTO quadrant) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final QuadrantTO newQuadrant = quadrantService.createQuadrant(quadrant);

			return Response.ok(newQuadrant).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ValidationException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getValidations()).build();
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@PUT
	@Path("/{quadrantId}")
	@ApiOperation(value="Update a quadrant",response=Response.class)
	@Produces("application/json")
	public Response updateQuadrant(
			@Context SecurityContext securityContext,
			@PathParam("quadrantId") final Long quadrantId,
			@ApiParam("the quadrant") final QuadrantTO quadrant) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final QuadrantTO newQuadrant = quadrantService.updateQuadrant(quadrant);

			return Response.ok(newQuadrant).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@DELETE
	@Path("/{quadrantId}")
	@ApiOperation(value="Delete a quadrant",response=Response.class)
	@Produces("application/json")
	public Response deleteQuadrant(
			@Context SecurityContext securityContext,
			@PathParam("quadrantId") final Long quadrantId) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final Boolean result = quadrantService.deleteQuadrant(quadrantId);

			return Response.ok(result).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

}
