package com.ai.techradar.web.service.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.TechGroupingTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("techgrouping")
@Api(value="/techgrouping",description="Radar service")
public class TechGroupingRestService extends AbstractTechRadarRestService {
	
	private TechGroupingService techGroupingService = (TechGroupingService)SpringStarter.getContext().getBean("TechGroupingService");

	@GET
	@Path("/")
	@ApiOperation(value="Get tech groupings",response=Response.class)
	@Produces("application/json")
	public Response getTechGroupings(@Context SecurityContext securityContext) {
		
		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final List<TechGroupingTO> rs = techGroupingService.getTechGroupings();

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
	@ApiOperation(value="Create a tech grouping",response=Response.class)
	@Produces("application/json")
	public Response createTechGrouping(
			@Context SecurityContext securityContext,
			@ApiParam("the tech grouping") final TechGroupingTO techGrouping) {
		
		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final TechGroupingTO newTechGrouping = techGroupingService.createTechGrouping(techGrouping);

			return Response.ok(newTechGrouping).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

}
