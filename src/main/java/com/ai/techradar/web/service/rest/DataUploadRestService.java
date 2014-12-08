package com.ai.techradar.web.service.rest;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.ValidationException;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechGroupingTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("dataupload")
@Api(value="/dataupload",description="Bulk load data service")
public class DataUploadRestService extends AbstractTechRadarRestService {

	@POST
	@Path("/startapp")
	@ApiOperation(value="Bulk import data",response=Response.class)
	@Produces("application/json")
	public Response startApp(
			@Context SecurityContext securityContext,
			@ApiParam("the radar") final Data data) {

		try {

			if(data.getTechnologies()!=null) {
				for(final TechnologyTO technology : data.getTechnologies()) {
					getTechnologyService(getUser(securityContext)).createTechnology(technology);
				}
			}

			if(data.getMaturities()!=null) {
				for(final MaturityTO maturity : data.getMaturities()) {
					getMaturityService(getUser(securityContext)).createMaturity(maturity);
				}
			}

			if(data.getTechnologies()!=null) {
				for(final TechGroupingTO techGrouping : data.getTechGroupings()) {
					getTechGroupingService(getUser(securityContext)).createTechGrouping(techGrouping);
				}
			}

			if(data.getRadar()!=null) {
				try {
					getRadarService(getUser(securityContext)).createRadar(data.getRadar());
				} catch (final ValidationException ex) {
					ex.printStackTrace();
				}
			}

			return Response.ok(data).build();

		} catch (SecurityException e) {
			throw new WebApplicationException(e);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new WebApplicationException(e);
		} catch (InstantiationException e) {
			throw new WebApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new WebApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new WebApplicationException(e);
		}

	}

	public static class Data implements Serializable {

		private static final long serialVersionUID = -8462502804978330944L;

		private List<TechnologyTO> technologies;

		private List<MaturityTO> maturities;

		private List<TechGroupingTO> techGroupings;

		private RadarTO radar;

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

		public List<TechGroupingTO> getTechGroupings() {
			return techGroupings;
		}

		public void setTechGroupings(final List<TechGroupingTO> techGroupings) {
			this.techGroupings = techGroupings;
		}

		public RadarTO getRadar() {
			return radar;
		}

		public void setRadar(final RadarTO radar) {
			this.radar = radar;
		}

	}

}
