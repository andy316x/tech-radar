package com.ai.techradar.web.service.rest;

import java.io.Serializable;
import java.util.List;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.ai.techradar.service.MeService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.UserService;
import com.ai.techradar.service.UserService.ServiceContactFailedException;
import com.ai.techradar.service.UserService.UserDoesNotExistException;
import com.ai.techradar.service.UserService.UserInfo;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.UserTechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("me")
@Api(value="/me",description="Me service")
public class MeRestService extends AbstractTechRadarRestService {

	private UserService userService = (UserService)SpringStarter.getContext().getBean("UserService");
	
	private MeService meService = (MeService)SpringStarter.getContext().getBean("MeService");

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
		
		final String username = securityContext.getUserPrincipal().getName();

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(username);
		}

		try {

			final UserTO user = new UserTO();

			final UserInfo userInfo = userService.getUserInfo(username);

			user.setUid(username);
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
	
	@GET
	@Path("/skillLevel")
	@ApiOperation(value="Get my skill levels",response=Response.class)
	@Produces("application/json")
	public Response getMySkillLevels(
			@Context SecurityContext securityContext) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {

			final List<UserTechnologyTO> mySkillLevels = meService.getMySkillLevels();

			return Response.ok(mySkillLevels).build();

		} catch(final ValidationException ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getValidations()).build();
		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} finally {
			AdminHandlerHelper.logout();
		}

	}

	public static class UserTO implements Serializable {
		private static final long serialVersionUID = 216554152025525794L;
		private String uid;
		private String name;
		public String getUid() {
			return uid;
		}
		public void setUid(final String uid) {
			this.uid = uid;
		}
		public String getName() {
			return name;
		}
		public void setName(final String name) {
			this.name = name;
		}
	}

}
