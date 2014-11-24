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

	private List<RadarTechnology> radarTechnologies;

	private List<RadarMaturity> radarMaturities;

	private List<RadarTechGrouping> radarTechGroupings;

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
	public List<RadarTechnology> getRadarTechnologies() {
		return radarTechnologies;
	}

	public void setRadarTechnologies(final List<RadarTechnology> radarTechnologies) {
		this.radarTechnologies = radarTechnologies;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarMaturity> getRadarMaturities() {
		return radarMaturities;
	}

	public void setRadarMaturities(final List<RadarMaturity> radarMaturities) {
		this.radarMaturities = radarMaturities;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarTechGrouping> getRadarTechGroupings() {
		return radarTechGroupings;
	}

	public void setRadarTechGroupings(final List<RadarTechGrouping> radarTechGroupings) {
		this.radarTechGroupings = radarTechGroupings;
	}
}
