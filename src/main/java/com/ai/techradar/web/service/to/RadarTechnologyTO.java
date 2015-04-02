package com.ai.techradar.web.service.to;

import java.util.Date;

import com.ai.techradar.database.entities.MovementEnum;

public class RadarTechnologyTO {

	private Long id;

	private String technology;

	private String maturity;
	
	private String quadrant;

	private String techGrouping;

	private Date addedDate;

	private String addedByUid;

	private String addedByFriendlyName;

	private MovementEnum movement;

	private int blipSize;

	private String url;

	private String description;

	private String detailUrl;

	private boolean customerStrategic;
	
	private Long radarId;
	
	private String radarName;

	public RadarTechnologyTO() {

	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(final String technology) {
		this.technology = technology;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(final String maturity) {
		this.maturity = maturity;
	}

	public String getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(final String quadrant) {
		this.quadrant = quadrant;
	}

	public String getTechGrouping() {
		return techGrouping;
	}

	public void setTechGrouping(final String techGrouping) {
		this.techGrouping = techGrouping;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(final Date addedDate) {
		this.addedDate = addedDate;
	}

	public String getAddedByUid() {
		return addedByUid;
	}

	public void setAddedByUid(final String addedByUid) {
		this.addedByUid = addedByUid;
	}

	public String getAddedByFriendlyName() {
		return addedByFriendlyName;
	}

	public void setAddedByFriendlyName(final String addedByFriendlyName) {
		this.addedByFriendlyName = addedByFriendlyName;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(final MovementEnum movement) {
		this.movement = movement;
	}

	public int getBlipSize() {
		return blipSize;
	}

	public void setBlipSize(final int blipSize) {
		this.blipSize = blipSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(final String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public boolean isCustomerStrategic() {
		return customerStrategic;
	}

	public void setCustomerStrategic(final boolean customerStrategic) {
		this.customerStrategic = customerStrategic;
	}

	public Long getRadarId() {
		return radarId;
	}

	public void setRadarId(final Long radarId) {
		this.radarId = radarId;
	}

	public String getRadarName() {
		return radarName;
	}

	public void setRadarName(final String radarName) {
		this.radarName = radarName;
	}

	public RadarTechnologyTO clone(){
		RadarTechnologyTO clone = new RadarTechnologyTO();
		clone.setId(id);
		clone.setMovement(movement);
		clone.setTechnology(technology);
		clone.setMaturity(maturity);
		clone.setTechGrouping(techGrouping);

		return clone;
	}
	
}