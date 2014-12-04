package com.ai.techradar.database.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class RadarTechGrouping {

	private Long id;

	private TechGrouping techGrouping;

	private Radar radar;

	private List<RadarTechnology> radarTechnologies;

	private int theOrder;

	public RadarTechGrouping() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="RADAR_TECH_GROUPING_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@ManyToOne
	public TechGrouping getTechGrouping() {
		return techGrouping;
	}

	public void setTechGrouping(final TechGrouping techGrouping) {
		this.techGrouping = techGrouping;
	}

	@ManyToOne
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

	@OneToMany(mappedBy="radarTechGrouping", cascade=CascadeType.ALL)
	public List<RadarTechnology> getRadarTechnologies() {
		return radarTechnologies;
	}

	public void setRadarTechnologies(final List<RadarTechnology> radarTechnologies) {
		this.radarTechnologies = radarTechnologies;
	}

	public int getTheOrder() {
		return theOrder;
	}

	public void setTheOrder(int theOrder) {
		this.theOrder = theOrder;
	}

}