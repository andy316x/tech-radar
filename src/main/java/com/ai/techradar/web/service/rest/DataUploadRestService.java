package com.ai.techradar.web.service.rest;

import java.io.Serializable;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.BusinessUnitService;
import com.ai.techradar.service.MaturityService;
import com.ai.techradar.service.QuadrantService;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.SpringStarter;
import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.service.TechnologyService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.util.AdminHandlerHelper;
import com.ai.techradar.web.service.to.BusinessUnitTO;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.QuadrantTO;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechGroupingTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("dataupload")
@Api(value="/dataupload",description="Bulk load data service")
public class DataUploadRestService extends AbstractTechRadarRestService {

	private TechnologyService technologyService = (TechnologyService)SpringStarter.getContext().getBean("TechnologyService");

	private MaturityService maturityService = (MaturityService)SpringStarter.getContext().getBean("MaturityService");

	private QuadrantService quadrantService = (QuadrantService)SpringStarter.getContext().getBean("QuadrantService");
	
	private TechGroupingService techGroupingService = (TechGroupingService)SpringStarter.getContext().getBean("TechGroupingService");

	private BusinessUnitService businessUnitService = (BusinessUnitService)SpringStarter.getContext().getBean("BusinessUnitService");

	private RadarService radarService = (RadarService)SpringStarter.getContext().getBean("RadarService");

	@POST
	@Path("/startapp")
	@ApiOperation(value="Bulk import data",response=Response.class)
	@Produces("application/json")
	public Response startApp(
			@Context SecurityContext securityContext,
			@ApiParam("the radar") final Data data) {

		if(securityContext.getUserPrincipal()!=null) {
			AdminHandlerHelper.login(securityContext.getUserPrincipal().getName());
		}

		try {
			
			if(data.getTechGroupings()!=null) {
				for(final TechGroupingTO techGrouping : data.getTechGroupings()) {
					techGroupingService.createTechGrouping(techGrouping);
				}
			}

			if(data.getTechnologies()!=null) {
				for(final TechnologyTO technology : data.getTechnologies()) {
					technologyService.createTechnology(technology);
				}
			}

			if(data.getMaturities()!=null) {
				for(final MaturityTO maturity : data.getMaturities()) {
					maturityService.createMaturity(maturity);
				}
			}

			if(data.getBusinessUnits()!=null) {
				for(final BusinessUnitTO businessUnit : data.getBusinessUnits()) {
					businessUnitService.createBusinessUnit(businessUnit);
				}
			}

			if(data.getTechnologies()!=null) {
				for(final QuadrantTO techGrouping : data.getQuadrants()) {
					quadrantService.createQuadrant(techGrouping);
				}
			}

			if(data.getRadar()!=null) {
				try {
					radarService.createRadar(data.getRadar());
				} catch (final ValidationException ex) {
					ex.printStackTrace();
				}
			}

			return Response.ok(data).build();

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

	public static class Data implements Serializable {

		private static final long serialVersionUID = -8462502804978330944L;
		
		private List<TechGroupingTO> techGroupings;

		private List<TechnologyTO> technologies;

		private List<MaturityTO> maturities;

		private List<QuadrantTO> quadrants;

		private List<BusinessUnitTO> businessUnits;

		private RadarTO radar;

		public List<TechGroupingTO> getTechGroupings() {
			return techGroupings;
		}

		public void setTechGroupings(final List<TechGroupingTO> techGroupings) {
			this.techGroupings = techGroupings;
		}

		public List<TechnologyTO> getTechnologies() {
			return technologies;
		}

		public void setTechnologies(final List<TechnologyTO> technologies) {
			this.technologies = technologies;
		}

		public List<MaturityTO> getMaturities() {
			return maturities;
		}

		public void setMaturities(final List<MaturityTO> maturities) {
			this.maturities = maturities;
		}

		public List<QuadrantTO> getQuadrants() {
			return quadrants;
		}

		public void setQuadrants(final List<QuadrantTO> quadrants) {
			this.quadrants = quadrants;
		}

		public List<BusinessUnitTO> getBusinessUnits() {
			return businessUnits;
		}

		public void setBusinessUnits(final List<BusinessUnitTO> businessUnits) {
			this.businessUnits = businessUnits;
		}

		public RadarTO getRadar() {
			return radar;
		}

		public void setRadar(final RadarTO radar) {
			this.radar = radar;
		}

	}

}
