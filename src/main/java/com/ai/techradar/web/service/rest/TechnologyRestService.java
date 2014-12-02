package com.ai.techradar.web.service.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ai.techradar.service.TechnologyService;
import com.ai.techradar.service.impl.TechnologyServiceImpl;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("technology")
@Api(value="/technology",description="Radar service")
public class TechnologyRestService {

	private TechnologyService service = new TechnologyServiceImpl();

	@GET
	@Path("/")
	@ApiOperation(value="Get technologies",response=Response.class)
	@Produces("application/json")
	public Response getTechnologies() {

		final List<TechnologyTO> rs = service.getTechnologies();

		return Response.ok(rs).build();
	}

	@GET
	@Path("/{technologyId}")
	@ApiOperation(value="Get technology by ID",response=Response.class)
	@Produces("application/json")
	public Response getRadarById(@PathParam("technologyId") final String technologyIdStr) {

		final Long id = Long.parseLong(technologyIdStr);

		final TechnologyTO technology = service.getTechnologyById(id);

		return Response.ok(technology).build();
	}

	@POST
	@Path("/")
	@ApiOperation(value="Create a technology",response=Response.class)
	@Produces("application/json")
	public Response createTechnology(@ApiParam("the radar") final TechnologyTO technology) {

		final TechnologyTO newTechnology = service.createTechnology(technology);

		return Response.ok(newTechnology).build();
	}

}
