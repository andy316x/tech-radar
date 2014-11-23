package com.ai.techradar.web.service.to;

import java.util.List;


public class RadarMaturityTO {

	private Long id;
	
	private MaturityTO arc;

	private RadarTO radar;

	private List<RadarTechnologyTO> zs;

	public RadarMaturityTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public MaturityTO getArc() {
		return arc;
	}

	public void setArc(final MaturityTO arc) {
		this.arc = arc;
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