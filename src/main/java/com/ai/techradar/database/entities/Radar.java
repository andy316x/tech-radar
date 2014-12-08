package com.ai.techradar.database.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

@Entity
public class Radar {

	private Long id;
	
	private String name;

	private Date createdDate;

	private User createdBy;

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

	@Index(name="radarNameIndex")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	@ManyToOne
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final User createdBy) {
		this.createdBy = createdBy;
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
