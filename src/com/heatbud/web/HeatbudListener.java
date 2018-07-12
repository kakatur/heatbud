/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This will do required initialization steps for the first time that the
 * application is launched.
 */
public class HeatbudListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(HeatbudListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {

	}

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
	    try {
	        com.amazonaws.http.IdleConnectionReaper.shutdown();
	    } catch (Exception e) {
	        logger.log(Level.SEVERE,"contextDestroyed");
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
	}

}
