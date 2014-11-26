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
public class TechGrouping {

	private Long id;

	private String name;

	private List<RadarTechGrouping> radarTechGroupings;

	public TechGrouping() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="TECH_GROUPING_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Index(name="techGroupingNameIndex")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@OneToMany(mappedBy="techGrouping", cascade=CascadeType.ALL)
	public List<RadarTechGrouping> getRadarTechGroupings() {
		return radarTechGroupings;
	}

	public void setRadarTechGroupings(final List<RadarTechGrouping> radarTechGroupings) {
		this.radarTechGroupings = radarTechGroupings;
	}

}