package com.ai.techradar.web.service.rest;

import java.io.Serializable;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ai.techradar.service.MaturityService;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.service.TechnologyService;
import com.ai.techradar.service.ValidationException;
import com.ai.techradar.service.impl.MaturityServiceImpl;
import com.ai.techradar.service.impl.RadarServiceImpl;
import com.ai.techradar.service.impl.TechGroupingServiceImpl;
import com.ai.techradar.service.impl.TechnologyServiceImpl;
import com.ai.techradar.web.service.to.MaturityTO;
import com.ai.techradar.web.service.to.RadarTO;
import com.ai.techradar.web.service.to.TechGroupingTO;
import com.ai.techradar.web.service.to.TechnologyTO;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("dataupload")
@Api(value="/dataupload",description="Bulk load data service")
public class DataUploadRestService {

	private RadarService radarService = new RadarServiceImpl();
	private TechnologyService technologyService = new TechnologyServiceImpl();
	private MaturityService maturityService = new MaturityServiceImpl();
	private TechGroupingService techGroupingService = new TechGroupingServiceImpl();

	@POST
	@Path("/startapp")
	@ApiOperation(value="Bulk import data",response=Response.class)
	@Produces("application/json")
	public Response startApp(@ApiParam("the radar") final Data data) {

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

		if(data.getTechnologies()!=null) {
			for(final TechGroupingTO techGrouping : data.getTechGroupings()) {
				techGroupingService.createTechGrouping(techGrouping);
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

		public void setTechnologies(List<TechnologyTO> technologies) {
			this.technologies = technologies;
		}

		public List<MaturityTO> getMaturities() {
			return maturities;
		}

		public void setMaturities(List<MaturityTO> maturities) {
			this.maturities = maturities;
		}

		public List<TechGroupingTO> getTechGroupings() {
			return techGroupings;
		}

		public void setTechGroupings(List<TechGroupingTO> techGroupings) {
			this.techGroupings = techGroupings;
		}

		public RadarTO getRadar() {
			return radar;
		}

		public void setRadar(RadarTO radar) {
			this.radar = radar;
		}

	}

}
