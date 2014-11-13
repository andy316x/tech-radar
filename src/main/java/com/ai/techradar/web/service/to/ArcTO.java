package com.ai.techradar.web.service.to;

import java.util.List;

public class ArcTO {

	private Long id;

	private String name;

	private List<XTO> xs;

	public ArcTO() {
		
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

	public List<XTO> getXs() {
		return xs;
	}

	public void setXs(final List<XTO> xs) {
		this.xs = xs;
	}
}