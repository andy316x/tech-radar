package com.ai.techradar.database.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Radar {

	private Long id;

	private String filename;

	private Date dateUploaded;

	private List<Technology> technologies;

	public Radar() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="RADAR_ID")
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

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<Technology> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(final List<Technology> technologies) {
		this.technologies = technologies;
	}

}
