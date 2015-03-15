package com.ai.techradar.web.service.to;

import java.util.Date;
import java.util.List;


public class RadarTO {

	private Long id;

	private String name;
	
	private String description;
	
	private String businessUnit;
	
	private boolean published;
	
	private Date lastPublishedDate;
	
	private boolean approved;

	private Date dateCreated;
	
	private String createdBy;
	
	private int majorVersion;
	
	private int minorVersion;

	private List<RadarTechnologyTO> technologies;

	private List<MaturityTO> maturities;

	private List<TechGroupingTO> techGroupings;

	public RadarTO() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Date getLastPublishedDate() {
		return lastPublishedDate;
	}

	public void setLastPublishedDate(Date lastPublishedDate) {
		this.lastPublishedDate = lastPublishedDate;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public List<RadarTechnologyTO> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(List<RadarTechnologyTO> technologies) {
		this.technologies = technologies;
	}

	public List<MaturityTO> getMaturities() {
		return maturities;
	}

	public void setMaturities(List<MaturityTO> maturities) {
		this.maturities = maturities;
	}

	public List<TechGroupingTO> getTechGroupings() {
		return techGroupings;
	}

	public void setTechGroupings(List<TechGroupingTO> techGroupings) {
		this.techGroupings = techGroupings;
	}

}
