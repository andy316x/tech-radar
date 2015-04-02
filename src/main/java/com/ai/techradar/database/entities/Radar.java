package com.ai.techradar.database.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

@Entity
public class Radar {

	private Long id;

	private String name;

	private String description;

	private BusinessUnit businessUnit;

	private boolean published;

	private Date lastPublishedDate;

	private boolean approved;

	private Date createdDate;

	private User createdBy;

	private int majorVersion;

	private int minorVersion;

	private List<RadarTechnology> radarTechnologies;

	private List<RadarMaturity> radarMaturities;

	private List<RadarQuadrant> radarQuadrants;

	public Radar() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="RADAR_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Index(name="radarNameIndex")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Column(name = "description", length = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@ManyToOne
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(final BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(final boolean published) {
		this.published = published;
	}

	public Date getLastPublishedDate() {
		return lastPublishedDate;
	}

	public void setLastPublishedDate(final Date lastPublishedDate) {
		this.lastPublishedDate = lastPublishedDate;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(final boolean approved) {
		this.approved = approved;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	@ManyToOne
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final User createdBy) {
		this.createdBy = createdBy;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(final int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(final int minorVersion) {
		this.minorVersion = minorVersion;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarTechnology> getRadarTechnologies() {
		return radarTechnologies;
	}

	public void setRadarTechnologies(final List<RadarTechnology> radarTechnologies) {
		this.radarTechnologies = radarTechnologies;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarMaturity> getRadarMaturities() {
		return radarMaturities;
	}

	public void setRadarMaturities(final List<RadarMaturity> radarMaturities) {
		this.radarMaturities = radarMaturities;
	}

	@OneToMany(mappedBy="radar", cascade=CascadeType.ALL)
	public List<RadarQuadrant> getRadarQuadrants() {
		return radarQuadrants;
	}

	public void setRadarQuadrants(final List<RadarQuadrant> radarQuadrants) {
		this.radarQuadrants = radarQuadrants;
	}

}
