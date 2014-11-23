package com.ai.techradar.web.service.to;

import java.util.List;

public class MaturityTO {

	private Long id;

	private String name;

	private List<RadarMaturityTO> xs;

	public MaturityTO() {
		
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

	public List<RadarMaturityTO> getXs() {
		return xs;
	}

	public void setXs(final List<RadarMaturityTO> xs) {
		this.xs = xs;
	}
}