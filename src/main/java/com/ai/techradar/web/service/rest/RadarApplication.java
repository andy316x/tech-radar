package com.ai.techradar.web.service.rest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RadarApplication extends Application {

	HashSet<Object> singletons = new HashSet<Object>();

	public RadarApplication() {
		singletons.add(new RadarRestService());
		singletons.add(new TechnologyRestService());
		singletons.add(new MaturityRestService());
		singletons.add(new TechGroupingRestService());
		singletons.add(new MeRestService());
		singletons.add(new SwaggerDocsRestService());
		
		singletons.add(new DataUploadRestService());
	}

	// Register provider here like exceptionhandler in resteasy
	@Override
	public Set<Class<?>> getClasses() {
		final HashSet<Class<?>> set = new HashSet<Class<?>>();
		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;  
	}

}
