package com.ai.techradar.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class UserTechnology {

	private Long id;

	private User user;

	private Technology technology;

	private SkillLevelEnum skillLevel;

	public UserTechnology() {
		// this form used by Hibernate
	}

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="USER_TECHNOLOGY_ID")
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	@ManyToOne
	public Technology getTechnology() {
		return technology;
	}

	public void setTechnology(final Technology technology) {
		this.technology = technology;
	}

	public SkillLevelEnum getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(final SkillLevelEnum skillLevel) {
		this.skillLevel = skillLevel;
	}

}