package com.ai.techradar.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Technology {

	private Long id;

	private String name;

	private String quadrant;

	private String arc;

	private MovementEnum movement;

	private int usageCount;

	private String url;

	private String description;

	private String detailUrl;

	private boolean customerStrategic;

	private Radar radar;

	public Technology() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="TECHNOLOGY_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getQuadrant() {
		return quadrant;
	}

	public void setQuadrant(final String quadrant) {
		this.quadrant = quadrant;
	}

	public String getArc() {
		return arc;
	}

	public void setArc(final String arc) {
		this.arc = arc;
	}

	public MovementEnum getMovement() {
		return movement;
	}

	public void setMovement(final MovementEnum movement) {
		this.movement = movement;
	}

	public int getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(final int usageCount) {
		this.usageCount = usageCount;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Column(name = "description", length = 4000)
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

	@ManyToOne
	@JoinColumn(name="RADAR_ID")
	public Radar getRadar() {
		return radar;
	}

	public void setRadar(final Radar radar) {
		this.radar = radar;
	}

}
