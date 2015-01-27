package com.ai.techrader.mocked.drupal;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.SpringStarter;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Mocked REST Service to refresh the technology list in Tech Radar. It is expected that this service will eventually be provided by Drupal.
 */
@Path("drupal")
@Api(value = "/drupal", description = "Mocked Drupal service for updating technology list")
public class MockedDrupalTechnologyRestService
{
	private final FakeDrupalTechnologyService technologyService = (FakeDrupalTechnologyService) SpringStarter.getContext().getBean(
			"FakeDrupalTechnologyService");

	/**
	 * <p>
	 * Retrieves a list of technologies which may be used to update the current technology list in Tech Radar. Eventually this service
	 * should be provided by Drupal, but if this mocked service is used instead the technology list is retrieved from a file in the file
	 * system.
	 * <p>
	 * <p>
	 * TODO: This could be extended to only retrieve technologies that have been added/modified within a certain time period.
	 * </p>
	 *
	 * @param securityContext
	 *            the security context
	 * @return a response containing the technology list to use to update Tech Radar
	 */
	@GET
	@Path("/technology")
	@ApiOperation(value = "Get Drupal technologies", response = Response.class)
	@Produces("application/json")
	public Response getDrupalTechnologies(@Context final SecurityContext securityContext)
	{
		try
		{
			final List<DrupalTechnology> technologies = technologyService.getTechnologies();
			return Response.ok(technologies).build();
		}
		catch (final SecurityException e)
		{
			throw new WebApplicationException(e);
		}
		catch (final IllegalArgumentException e)
		{
			throw new WebApplicationException(e);
		}
	}
}
