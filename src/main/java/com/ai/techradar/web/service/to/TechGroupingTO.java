package com.ai.techradar.web.service.to;

import java.util.List;


public class TechGroupingTO {

	private Long id;

	private String name;

	private List<RadarTechGroupingTO> ys;

	public TechGroupingTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<RadarTechGroupingTO> getYs() {
		return ys;
	}

	public void setYs(final List<RadarTechGroupingTO> ys) {
		this.ys = ys;
	}
}