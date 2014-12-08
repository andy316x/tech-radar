package com.ai.techradar.database.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {

	private String username;

	private List<Radar> radars;

	private List<UserTechnology> technologies;

	public User() {
		// this form used by Hibernate
	}

	@Id
	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	@OneToMany(mappedBy="createdBy", cascade=CascadeType.ALL)
	public List<Radar> getRadars() {
		return radars;
	}

	public void setRadars(final List<Radar> radars) {
		this.radars = radars;
	}

	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	public List<UserTechnology> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(final List<UserTechnology> technologies) {
		this.technologies = technologies;
	}

}
