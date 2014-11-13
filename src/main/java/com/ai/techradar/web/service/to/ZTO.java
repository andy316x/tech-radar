package com.ai.techradar.web.service.to;

import com.ai.techradar.database.entities.MovementEnum;

public class ZTO {

	private Long id;
	
	private TechnologyTO technology;

	private RadarTO radar;

	private XTO x;

	private YTO y;

	private MovementEnum movement;

	public ZTO() {
		
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

	public XTO getX() {
		return x;
	}

	public void setX(final XTO x) {
		this.x = x;
	}

	public YTO getY() {
		return y;
	}

	public void setY(final YTO y) {
		this.y = y;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(final MovementEnum movement) {
		this.movement = movement;
	}
	
	public ZTO clone(){
		ZTO clone = new ZTO();
		clone.setId(id);
		clone.setMovement(movement);
		clone.setTechnology(technology);
		clone.setRadar(radar);
		clone.setX(x);
		clone.setY(y);
		
		return clone;
	}
}