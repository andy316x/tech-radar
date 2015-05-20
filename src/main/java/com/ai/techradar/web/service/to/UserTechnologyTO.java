package com.ai.techradar.web.service.to;

import java.io.Serializable;

import com.ai.techradar.database.entities.SkillLevelEnum;

public class UserTechnologyTO implements Serializable {

	private static final long serialVersionUID = -7949844211235033682L;

	private String user;

	private String technology;

	private String techGroup;

	private SkillLevelEnum skillLevel;

	public UserTechnologyTO() {

	}

	public String getUser() {
		return user;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(final String technology) {
		this.technology = technology;
	}

	public SkillLevelEnum getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(final SkillLevelEnum skillLevel) {
		this.skillLevel = skillLevel;
	}

	public void setTechGroup(String techGroup) {
		this.techGroup = techGroup;
	}

	public String getTechGroup() {
		return techGroup;
	}
}
