package com.ai.techradar.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	
	private int theOrder;

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
	public Technology getTechnology() {
		return technology;
	}

	public void setTechnology(final Technology technology) {
		this.technology = technology;
	}

	@ManyToOne
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

	@ManyToOne
	public RadarMaturity getRadarMaturity() {
		return radarMaturity;
	}

	public void setRadarMaturity(final RadarMaturity radarMaturity) {
		this.radarMaturity = radarMaturity;
	}

	@ManyToOne
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

	public int getTheOrder() {
		return theOrder;
	}

	public void setTheOrder(int theOrder) {
		this.theOrder = theOrder;
	}

}