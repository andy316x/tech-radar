package com.ai.techradar.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class RadarTechnology {

	private Long id;

	private Technology technology;

	private Radar radar;

	private RadarMaturity radarMaturity;

	private RadarTechGrouping radarTechGrouping;

	private MovementEnum movement;

	public RadarTechnology() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="RADAR_TECHNOLOGY_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="TECHNOLOGY_ID")
	public Technology getTechnology() {
		return technology;
	}

	public void setTechnology(final Technology technology) {
		this.technology = technology;
	}

	@ManyToOne
	@JoinColumn(name="RADAR_ID")
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

	@ManyToOne
	@JoinColumn(name="RADAR_MATURITY_ID")
	public RadarMaturity getRadarMaturity() {
		return radarMaturity;
	}

	public void setRadarMaturity(final RadarMaturity radarMaturity) {
		this.radarMaturity = radarMaturity;
	}

	@ManyToOne
	@JoinColumn(name="RADAR_TECH_GROUPING_ID")
	public RadarTechGrouping getRadarTechGrouping() {
		return radarTechGrouping;
	}

	public void setRadarTechGrouping(final RadarTechGrouping radarTechGrouping) {
		this.radarTechGrouping = radarTechGrouping;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(final MovementEnum movement) {
		this.movement = movement;
	}

}