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
public class Maturity {

	private Long id;

	private String name;

	private List<RadarMaturity> radarMaturities;

	public Maturity() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="MATURITY_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Index(name="maturityNameIndex")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@OneToMany(mappedBy="maturity", cascade=CascadeType.ALL)
	public List<RadarMaturity> getRadarMaturities() {
		return radarMaturities;
	}

	public void setRadarMaturities(final List<RadarMaturity> radarMaturities) {
		this.radarMaturities = radarMaturities;
	}

}