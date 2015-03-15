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

import com.ai.techradar.service.BusinessUnitService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.BusinessUnitTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("businessunit")
@Api(value="/businessunit",description="Business unit service")
public class BusinessUnitRestService extends AbstractTechRadarRestService {

	private BusinessUnitService businessUnitService = (BusinessUnitService)SpringStarter.getContext().getBean("BusinessUnitService");

	@GET
	@Path("/")
	@ApiOperation(value="Get business units",response=Response.class)
	@Produces("application/json")
	public Response getBusinessUnits(@Context SecurityContext securityContext) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final List<BusinessUnitTO> rs = businessUnitService.getBusinessUnits();

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
	@ApiOperation(value="Create a business unit",response=Response.class)
	@Produces("application/json")
	public Response createBusinessUnit(
			@Context SecurityContext securityContext,
			@ApiParam("the business unit") final BusinessUnitTO businessUnit) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final BusinessUnitTO newBusinessUnit = businessUnitService.createBusinessUnit(businessUnit);

			return Response.ok(newBusinessUnit).build();

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
	@Path("/{businessUnitId}")
	@ApiOperation(value="Update a business unit",response=Response.class)
	@Produces("application/json")
	public Response updateBusinessUnit(
			@Context SecurityContext securityContext,
			@PathParam("businessUnitId") final Long businessUnitId,
			@ApiParam("the business unit") final BusinessUnitTO businessUnit) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final BusinessUnitTO newBusinessUnit = businessUnitService.updateBusinessUnit(businessUnit);

			return Response.ok(newBusinessUnit).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	@DELETE
	@Path("/{businessUnitId}")
	@ApiOperation(value="Delete a business unit",response=Response.class)
	@Produces("application/json")
	public Response deleteBusinessUnit(
			@Context SecurityContext securityContext,
			@PathParam("businessUnitId") final Long businessUnitId) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final Boolean result = businessUnitService.deleteBusinessUnit(businessUnitId);

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
