package com.ai.techradar.web.service.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.service.impl.TechGroupingServiceImpl;
import com.ai.techradar.web.service.to.TechGroupingTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@SuppressWarnings("unchecked")
@Path("techgrouping")
@Api(value="/techgrouping",description="Radar service")
public class TechGroupingRestService {

	private TechGroupingService service = new TechGroupingServiceImpl();

	@GET
	@Path("/")
	@ApiOperation(value="Get tech groupings",response=Response.class)
	@Produces("application/json")
	public Response getTechGroupings() {

		final List<TechGroupingTO> rs = service.getTechGroupings();

		return Response.ok(rs).build();
	}

	@POST
	@Path("/")
	@ApiOperation(value="Create a tech grouping",response=Response.class)
	@Produces("application/json")
	public Response createTechGrouping(@ApiParam("the tech grouping") final TechGroupingTO techGrouping) {

		final TechGroupingTO newTechGrouping = service.createTechGrouping(techGrouping);

		return Response.ok(newTechGrouping).build();
	}

}
