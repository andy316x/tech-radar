package com.ai.techradar.web.service.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ai.techradar.service.MaturityService;
import com.ai.techradar.service.impl.MaturityServiceImpl;
import com.ai.techradar.web.service.to.MaturityTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@SuppressWarnings("unchecked")
@Path("maturity")
@Api(value="/maturity",description="Radar service")
public class MaturityRestService {

	private MaturityService service = new MaturityServiceImpl();

	@GET
	@Path("/")
	@ApiOperation(value="Get maturities",response=Response.class)
	@Produces("application/json")
	public Response getMaturities() {

		final List<MaturityTO> rs = service.getMaturities();

		return Response.ok(rs).build();
	}

	@POST
	@Path("/")
	@ApiOperation(value="Create a maturity",response=Response.class)
	@Produces("application/json")
	public Response createMaturity(@ApiParam("the radar") final MaturityTO maturity) {

		final MaturityTO newMaturity = service.createMaturity(maturity);

		return Response.ok(newMaturity).build();
	}

}
