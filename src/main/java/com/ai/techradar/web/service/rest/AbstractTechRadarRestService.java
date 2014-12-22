package com.ai.techradar.web.service.rest;

import javax.ws.rs.core.SecurityContext;

public abstract class AbstractTechRadarRestService {

	protected String getUser(final SecurityContext securityContext) {
		if(securityContext.getUserPrincipal()!=null) {
			return securityContext.getUserPrincipal().getName();
		}
		return null;
	}

}
