package com.ai.techradar.web.service.rest;

import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("me")
@Api(value="/me",description="Radar service")
public class MeRestService extends AbstractTechRadarRestService {

	@GET
	@Path("/")
	@ApiOperation(value="User data",response=Response.class)
	@Produces("application/json")
	public Response getMe(
			@Context HttpHeaders headers, 
			@Context UriInfo uriInfo, 
			@Context Request request,
			@Context ServletConfig config, 
			@Context ServletContext context, 
			@Context SecurityContext securityContext) {

		final UserTO user = new UserTO();

		user.setName(securityContext.getUserPrincipal().getName());

		return Response.ok(user).build();
	}

	public static class UserTO implements Serializable {
		private static final long serialVersionUID = 216554152025525794L;
		private String name;

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}
	}

}
