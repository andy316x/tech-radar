package com.ai.techradar.service.impl;

public class AbstractTechRadarService {
	
	private final String user;
	
	public AbstractTechRadarService(final String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

}
