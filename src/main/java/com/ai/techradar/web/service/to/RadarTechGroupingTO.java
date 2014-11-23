package com.ai.techradar.web.service.to;

import java.util.List;


public class RadarTechGroupingTO {

	private Long id;
	
	private TechGroupingTO quadrant;

	private RadarTO radar;

	private List<RadarTechnologyTO> zs;

	public RadarTechGroupingTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public TechGroupingTO getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(final TechGroupingTO quadrant) {
		this.quadrant = quadrant;
	}

	public RadarTO getRadar() {
		return radar;
	}

	public void setRadar(final RadarTO radar) {
		this.radar = radar;
	}

	public List<RadarTechnologyTO> getZs() {
		return zs;
	}

	public void setZs(final List<RadarTechnologyTO> zs) {
		this.zs = zs;
	}
}