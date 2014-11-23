package com.ai.techradar.web.service.to;

import java.util.Date;
import java.util.List;


public class RadarTO {

	private Long id;
	
	private String name;

	private String filename;

	private Date dateUploaded;

	private List<RadarTechnologyTO> zs;

	private List<RadarMaturityTO> xs;

	private List<RadarTechGroupingTO> ys;

	public RadarTO() {
		
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(final Date dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public List<RadarTechnologyTO> getZs() {
		return zs;
	}

	public void setZs(final List<RadarTechnologyTO> zs) {
		this.zs = zs;
	}

	public List<RadarMaturityTO> getXs() {
		return xs;
	}

	public void setXs(final List<RadarMaturityTO> xs) {
		this.xs = xs;
	}

	public List<RadarTechGroupingTO> getYs() {
		return ys;
	}

	public void setYs(final List<RadarTechGroupingTO> ys) {
		this.ys = ys;
	}
}
