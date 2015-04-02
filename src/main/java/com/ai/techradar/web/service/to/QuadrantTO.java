package com.ai.techradar.web.service.to;

public class QuadrantTO {

	private Long id;

	private String name;
	
	private boolean techGrouping;

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

	public boolean isTechGrouping() {
		return techGrouping;
	}

	public void setTechGrouping(final boolean techGrouping) {
		this.techGrouping = techGrouping;
	}
	
}