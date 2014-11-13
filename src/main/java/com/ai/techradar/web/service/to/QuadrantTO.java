package com.ai.techradar.web.service.to;

import java.util.List;


public class QuadrantTO {

	private Long id;

	private String name;

	private List<YTO> ys;

	public QuadrantTO() {
		
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

	public List<YTO> getYs() {
		return ys;
	}

	public void setYs(final List<YTO> ys) {
		this.ys = ys;
	}
}