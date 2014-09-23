package com.ai.techradar.web.service.to;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RadarTO implements Serializable {

	private static final long serialVersionUID = -5749316497100834520L;

	private long id;

	private String filename;

	private Date dateUploaded;

	private List<TechnologyTO> technologies;

	public RadarTO() {

	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public Date getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(final Date dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public List<TechnologyTO> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(final List<TechnologyTO> technologies) {
		this.technologies = technologies;
	}

}
