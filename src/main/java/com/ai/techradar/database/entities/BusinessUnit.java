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
public class BusinessUnit {

	private Long id;

	private String name;

	private List<Radar> radars;

	public BusinessUnit() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="BUSINESS_UNIT_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Index(name="businessUnitNameIndex")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@OneToMany(mappedBy="businessUnit", cascade=CascadeType.ALL)
	public List<Radar> getRadars() {
		return radars;
	}

	public void setRadars(final List<Radar> radars) {
		this.radars = radars;
	}

}