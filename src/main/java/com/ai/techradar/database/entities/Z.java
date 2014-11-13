package com.ai.techradar.database.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import org.hibernate.annotations.GenericGenerator;

@Entity
public class Z {

	private Long id;
	
	private Technology technology;

	private Radar radar;

	private X x;

	private Y y;

	private MovementEnum movement;

	public Z() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="Z_ID")
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
	@JoinColumn(name="X_ID")
	public X getX() {
		return x;
	}

	public void setX(final X x) {
		this.x = x;
	}

	@ManyToOne
	@JoinColumn(name="Y_ID")
	public Y getY() {
		return y;
	}

	public void setY(final Y y) {
		this.y = y;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(final MovementEnum movement) {
		this.movement = movement;
	}
}