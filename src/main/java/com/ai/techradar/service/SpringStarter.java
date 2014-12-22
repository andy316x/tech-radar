package com.ai.techradar.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringStarter {

	private static final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

	public static ClassPathXmlApplicationContext getContext() {
		return ctx;
	}

}
