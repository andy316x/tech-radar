package com.ai.techradar.database.entities;

import java.util.Date;

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
	
	private Date addedDate;
	
	private User addedBy;

	private RadarMaturity radarMaturity;

	private RadarQuadrant radarQuadrant;

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
	
	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(final Date addedDate) {
		this.addedDate = addedDate;
	}
	
	@ManyToOne
	public User getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(final User addedBy) {
		this.addedBy = addedBy;
	}

	@ManyToOne
	public RadarMaturity getRadarMaturity() {
		return radarMaturity;
	}

	public void setRadarMaturity(final RadarMaturity radarMaturity) {
		this.radarMaturity = radarMaturity;
	}

	@ManyToOne
	public RadarQuadrant getRadarQuadrant() {
		return radarQuadrant;
	}

	public void setRadarQuadrant(final RadarQuadrant radarQuadrant) {
		this.radarQuadrant = radarQuadrant;
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