package com.ai.techradar.web.service.to;

import java.util.Date;
import java.util.List;


public class RadarTO {

	private Long id;

	private String name;

	private Date dateCreated;

	private List<RadarTechnologyTO> technologies;

	private List<MaturityTO> maturities;

	private List<TechGroupingTO> techGroupings;

	public RadarTO() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public List<RadarTechnologyTO> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(List<RadarTechnologyTO> technologies) {
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

}
