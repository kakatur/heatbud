/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.util;

import java.io.FileWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple class to manage loading the property file containing needed configuration data
 * from the package. Once loaded, the configuration is held in memory as a singleton.
 */
public class Configuration {

    private static Configuration configuration = new Configuration();    
    private Logger logger = Logger.getLogger(Configuration.class.getName());
    private Properties heatbudProperties = new Properties();

    private Configuration() {
        try {
        	heatbudProperties.load(this.getClass().getResourceAsStream("/heatbud.properties"));
        } catch ( Exception e ) {
            logger.log(Level.SEVERE, "Unable to load configuration: " + e.getMessage(), e);
        }
    }

    public static final Configuration getInstance () {
        return configuration;
    }

    /**
     * Returns the property value requested.
     */
    public String getProperty(String propertyName) {
        return heatbudProperties.getProperty(propertyName);
    }

    /**
     * Sets the property value requested and saves the file into the server.
     */
    public void setProperty(String propertyName, String propertyValue) {
        heatbudProperties.setProperty(propertyName, propertyValue);
        try {
        	heatbudProperties.store(new FileWriter("/var/lib/tomcat8/webapps/ROOT/WEB-INF/classes/heatbud.properties"), "none");
        } catch ( Exception e ) {
            logger.log(Level.SEVERE, "Unable to save configuration: " + e.getMessage(), e);
        }
    }

}