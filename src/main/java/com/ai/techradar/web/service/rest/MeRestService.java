package com.ai.techradar.web.service.rest;

import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.UserService;
import com.ai.techradar.service.UserService.ServiceContactFailedException;
import com.ai.techradar.service.UserService.UserDoesNotExistException;
import com.ai.techradar.service.UserService.UserInfo;
import com.ai.techradar.util.AdminHandlerHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("me")
@Api(value="/me",description="Radar service")
public class MeRestService extends AbstractTechRadarRestService {

	private UserService userService = (UserService)SpringStarter.getContext().getBean("UserService");

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

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final UserTO user = new UserTO();

			final UserInfo userInfo = userService.getUserInfo(securityContext.getUserPrincipal().getName());

			user.setName(userInfo.getSurname() + ", " + userInfo.getGivenName());

			return Response.ok(user).build();

		} catch (UserDoesNotExistException e) {
			throw new WebApplicationException(e);
		} catch (ServiceContactFailedException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}
		
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
