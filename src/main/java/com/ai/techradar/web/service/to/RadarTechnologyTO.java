package com.ai.techradar.web.service.to;

import com.ai.techradar.database.entities.MovementEnum;

public class RadarTechnologyTO {

	private Long id;
	
	private TechnologyTO technology;

	private RadarTO radar;

	private RadarMaturityTO x;

	private RadarTechGroupingTO y;

	private MovementEnum movement;

	public RadarTechnologyTO() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public TechnologyTO getTechnology() {
		return technology;
	}

	public void setTechnology(final TechnologyTO technology) {
		this.technology = technology;
	}

	public RadarTO getRadar() {
		return radar;
	}

	public void setRadar(final RadarTO radar) {
		this.radar = radar;
	}

	public RadarMaturityTO getX() {
		return x;
	}

	public void setX(final RadarMaturityTO x) {
		this.x = x;
	}

	public RadarTechGroupingTO getY() {
		return y;
	}

	public void setY(final RadarTechGroupingTO y) {
		this.y = y;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(final MovementEnum movement) {
		this.movement = movement;
	}
	
	public RadarTechnologyTO clone(){
		RadarTechnologyTO clone = new RadarTechnologyTO();
		clone.setId(id);
		clone.setMovement(movement);
		clone.setTechnology(technology);
		clone.setRadar(radar);
		clone.setX(x);
		clone.setY(y);
		
		return clone;
	}
}