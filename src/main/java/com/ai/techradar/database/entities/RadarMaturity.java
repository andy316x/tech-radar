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
public class RadarMaturity {

	private Long id;
	
	private Maturity maturity;

	private Radar radar;

	private List<RadarTechnology> radarTechnologies;

	public RadarMaturity() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="RADAR_MATURITY_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@ManyToOne()
	public Maturity getMaturity() {
		return maturity;
	}

	public void setMaturity(final Maturity maturity) {
		this.maturity = maturity;
	}

	@ManyToOne
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

	@OneToMany(mappedBy="radarMaturity", cascade=CascadeType.ALL)
	public List<RadarTechnology> getRadarTechnologies() {
		return radarTechnologies;
	}

	public void setRadarTechnologies(final List<RadarTechnology> radarTechnologies) {
		this.radarTechnologies = radarTechnologies;
	}
	
}