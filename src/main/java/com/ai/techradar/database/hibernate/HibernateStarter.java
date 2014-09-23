package com.ai.techradar.database.hibernate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class HibernateStarter implements ServletContextListener {
	 
    public void contextInitialized(final ServletContextEvent event) {
    	// Just call the static initializer of that class 
    	HibernateUtil.getSessionFactory();
    }
 
    public void contextDestroyed(final ServletContextEvent event) {
    	// Free all resources
        HibernateUtil.getSessionFactory().close();
    }
    
}
