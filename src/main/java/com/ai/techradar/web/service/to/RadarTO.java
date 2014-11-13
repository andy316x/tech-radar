package com.ai.techradar.web.service.to;

import java.util.Date;
import java.util.List;


public class RadarTO {

	private Long id;

	private String filename;

	private Date dateUploaded;

	private List<ZTO> zs;

	private List<XTO> xs;

	private List<YTO> ys;

	public RadarTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
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

	public List<ZTO> getZs() {
		return zs;
	}

	public void setZs(final List<ZTO> zs) {
		this.zs = zs;
	}

	public List<XTO> getXs() {
		return xs;
	}

	public void setXs(final List<XTO> xs) {
		this.xs = xs;
	}

	public List<YTO> getYs() {
		return ys;
	}

	public void setYs(final List<YTO> ys) {
		this.ys = ys;
	}
}
