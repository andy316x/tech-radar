package com.ai.techradar.web.service.to;

import java.util.List;


public class XTO {

	private Long id;
	
	private ArcTO arc;

	private RadarTO radar;

	private List<ZTO> zs;

	public XTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public ArcTO getArc() {
		return arc;
	}

	public void setArc(final ArcTO arc) {
		this.arc = arc;
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