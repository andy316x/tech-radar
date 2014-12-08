package com.ai.techradar.web.service.rest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.core.SecurityContext;

import com.ai.techradar.service.MaturityService;
import com.ai.techradar.service.RadarService;
import com.ai.techradar.service.TechGroupingService;
import com.ai.techradar.service.TechnologyService;

public abstract class AbstractTechRadarRestService {

	private static final String RADAR_SERVICE_CLASS = "com.ai.techradar.service.impl.RadarServiceImpl";

	protected RadarService getRadarService(final String user) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		final Class<?> clazz = Class.forName(RADAR_SERVICE_CLASS);
		final Constructor<?> ctor = clazz.getConstructor(String.class);
		return (RadarService)ctor.newInstance(new Object[] { user });
	}

	private static final String MATURITY_SERVICE_CLASS = "com.ai.techradar.service.impl.MaturityServiceImpl";

	protected MaturityService getMaturityService(final String user) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		final Class<?> clazz = Class.forName(MATURITY_SERVICE_CLASS);
		final Constructor<?> ctor = clazz.getConstructor(String.class);
		return (MaturityService)ctor.newInstance(new Object[] { user });
	}

	private static final String TECH_GROUPING_SERVICE_CLASS = "com.ai.techradar.service.impl.TechGroupingServiceImpl";

	protected TechGroupingService getTechGroupingService(final String user) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		final Class<?> clazz = Class.forName(TECH_GROUPING_SERVICE_CLASS);
		final Constructor<?> ctor = clazz.getConstructor(String.class);
		return (TechGroupingService)ctor.newInstance(new Object[] { user });
	}

	private static final String TECHNOLOGY_SERVICE_CLASS = "com.ai.techradar.service.impl.TechnologyServiceImpl";

	protected TechnologyService getTechnologyService(final String user) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		final Class<?> clazz = Class.forName(TECHNOLOGY_SERVICE_CLASS);
		final Constructor<?> ctor = clazz.getConstructor(String.class);
		return (TechnologyService)ctor.newInstance(new Object[] { user });
	}

	protected String getUser(final SecurityContext securityContext) {
		if(securityContext.getUserPrincipal()!=null) {
			return securityContext.getUserPrincipal().getName();
		}
		return null;
	}

}
