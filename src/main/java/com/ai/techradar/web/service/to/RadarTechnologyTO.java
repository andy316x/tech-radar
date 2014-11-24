package com.ai.techradar.web.service.to;

import com.ai.techradar.database.entities.MovementEnum;

public class RadarTechnologyTO {

	private Long id;

	private String technology;

	private String maturity;

	private String techGrouping;

	private MovementEnum movement;

	private int blipSize;

	private String url;

	private String description;

	private String detailUrl;

	private boolean customerStrategic;

	public RadarTechnologyTO() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}

	public String getTechGrouping() {
		return techGrouping;
	}

	public void setTechGrouping(String techGrouping) {
		this.techGrouping = techGrouping;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(MovementEnum movement) {
		this.movement = movement;
	}

	public int getBlipSize() {
		return blipSize;
	}

	public void setBlipSize(int blipSize) {
		this.blipSize = blipSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public boolean isCustomerStrategic() {
		return customerStrategic;
	}

	public void setCustomerStrategic(boolean customerStrategic) {
		this.customerStrategic = customerStrategic;
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