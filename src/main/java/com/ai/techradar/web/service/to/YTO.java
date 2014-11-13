package com.ai.techradar.web.service.to;

import java.util.List;


public class YTO {

	private Long id;
	
	private QuadrantTO quadrant;

	private RadarTO radar;

	private List<ZTO> zs;

	public YTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public QuadrantTO getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(final QuadrantTO quadrant) {
		this.quadrant = quadrant;
	}

	public RadarTO getRadar() {
		return radar;
	}

	public void setRadar(final RadarTO radar) {
		this.radar = radar;
	}

	public List<ZTO> getZs() {
		return zs;
	}

	public void setZs(final List<ZTO> zs) {
		this.zs = zs;
	}
}