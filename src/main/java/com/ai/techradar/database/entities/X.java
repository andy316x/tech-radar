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
public class X {

	private Long id;
	
	private Arc arc;

	private Radar radar;

	private List<Z> zs;

	public X() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="X_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="ARC_ID")
	public Arc getArc() {
		return arc;
	}

	public void setArc(final Arc arc) {
		this.arc = arc;
	}

	@ManyToOne
	@JoinColumn(name="RADAR_ID")
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

	@OneToMany(mappedBy="x", cascade=CascadeType.ALL)
	public List<Z> getZs() {
		return zs;
	}

	public void setZs(final List<Z> zs) {
		this.zs = zs;
	}
}