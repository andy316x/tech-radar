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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import com.ai.techradar.service.MaturityService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.MaturityTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("maturity")
@Api(value="/maturity",description="Maturity service")
public class MaturityRestService extends AbstractTechRadarRestService {

	private MaturityService maturityService = (MaturityService)SpringStarter.getContext().getBean("MaturityService");

	@GET
	@Path("/")
	@ApiOperation(value="Get maturities",response=Response.class)
	@Produces("application/json")
	public Response getMaturities(@Context SecurityContext securityContext) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final List<MaturityTO> rs = maturityService.getMaturities();

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
	@ApiOperation(value="Create a maturity",response=Response.class)
	@Produces("application/json")
	public Response createMaturity(
			@Context SecurityContext securityContext,
			@ApiParam("the radar") final MaturityTO maturity) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final MaturityTO newMaturity = maturityService.createMaturity(maturity);

			return Response.ok(newMaturity).build();

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
	@Path("/{maturityId}")
	@ApiOperation(value="Update a maturity",response=Response.class)
	@Produces("application/json")
	public Response updateMaturity(
			@Context SecurityContext securityContext,
			@PathParam("maturityId") final Long maturityId,
			@ApiParam("the maturity") final MaturityTO maturity) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final MaturityTO newMaturity = maturityService.updateMaturity(maturity);

			return Response.ok(newMaturity).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@DELETE
	@Path("/{maturityId}")
	@ApiOperation(value="Delete a maturity",response=Response.class)
	@Produces("application/json")
	public Response deleteMaturity(
			@Context SecurityContext securityContext,
			@PathParam("maturityId") final Long maturityId) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final Boolean result = maturityService.deleteMaturity(maturityId);

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
