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
	
	private String name;

	private String filename;

	private Date dateUploaded;

	private List<RadarTechnology> zs;

	private List<RadarMaturity> xs;

	private List<RadarTechGrouping> ys;

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

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarTechnology> getZs() {
		return zs;
	}

	public void setZs(final List<RadarTechnology> zs) {
		this.zs = zs;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarMaturity> getXs() {
		return xs;
	}

	public void setXs(final List<RadarMaturity> xs) {
		this.xs = xs;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarTechGrouping> getYs() {
		return ys;
	}

	public void setYs(final List<RadarTechGrouping> ys) {
		this.ys = ys;
	}
}
