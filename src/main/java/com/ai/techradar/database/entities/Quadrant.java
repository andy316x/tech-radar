package com.ai.techradar.database.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

@Entity
public class Quadrant {

	private Long id;

	private String name;
	
	private boolean techGrouping;

	private List<RadarQuadrant> radarQuadrants;

	public Quadrant() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="QUADRANT_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Index(name="quadrantNameIndex")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isTechGrouping() {
		return techGrouping;
	}

	public void setTechGrouping(final boolean techGrouping) {
		this.techGrouping = techGrouping;
	}

	@OneToMany(mappedBy="quadrant", cascade=CascadeType.ALL)
	public List<RadarQuadrant> getRadarQuadrants() {
		return radarQuadrants;
	}

	public void setRadarQuadrants(final List<RadarQuadrant> radarQuadrants) {
		this.radarQuadrants = radarQuadrants;
	}

}