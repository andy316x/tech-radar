package com.ai.techradar.web.service.to;

import java.io.Serializable;

import com.ai.techradar.database.entities.MovementEnum;

public class TechnologyTO implements Serializable {

	private static final long serialVersionUID = -4856383955947759556L;

	private String technologyName;

	private String quadrantName;

	private String arcName;

	private MovementEnum movement;

	private int blipSize;

	private String url;

	private String description;

	private String detailUrl;

	private boolean customerStrategic;

	public TechnologyTO() {

	}

	public String getTechnologyName() {
		return technologyName;
	}

	public void setTechnologyName(final String technologyName) {
		this.technologyName = technologyName;
	}

	public String getQuadrantName() {
		return quadrantName;
	}

	public void setQuadrantName(final String quadrantName) {
		this.quadrantName = quadrantName;
	}

	public String getArcName() {
		return arcName;
	}

	public void setArcName(final String arcName) {
		this.arcName = arcName;
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

}
