package com.ai.techradar.database.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;


import org.hibernate.annotations.GenericGenerator;

@Entity
public class RadarTechGrouping {

	private Long id;
	
	private TechGrouping quadrant;

	private Radar radar;

	private List<RadarTechnology> zs;

	public RadarTechGrouping() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="Y_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="QUADRANT_ID")
	public TechGrouping getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(final TechGrouping quadrant) {
		this.quadrant = quadrant;
	}

	@ManyToOne
	@JoinColumn(name="RADAR_ID")
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

	@OneToMany(mappedBy="y", cascade=CascadeType.ALL)
	public List<RadarTechnology> getZs() {
		return zs;
	}

	public void setZs(final List<RadarTechnology> zs) {
		this.zs = zs;
	}
}