/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.heatbud.entity.MarketPricing;

/**
 * The class to hold common variables and functions of Heatbud.
 */
public class HeatbudCommon {

    private Logger logger = Logger.getLogger(HeatbudCommon.class.getName());

    // HashSet has better performance than ArrayList
    public HashSet<MarketPricing> marketPricingSet = new HashSet<MarketPricing>(100);
    public Set<String> marketPricingCountriesSet = new TreeSet<String>();

    // Unwanted Posts
    public String[] unwantedPosts = {
    	"844-", "855-", "877-", "888-",
    	"avg-", "msn-", "email-", "yahoo-", "gmail-", "hotmail-",
    	"outlook-", "kaspersky-", "antivirus-", "mcafee-", "norton-",
		"tech-", "technical-", "dvdonlineshop-",
		"touristsafari-", "drilling-",
		"china-", "inverter-", "ups-", "power-supply", "led-"
    };

    // Blocked IP Addresses
    public String[] blockedIPs = {
    	"5.35.51.158",
    	"5.149.254.67",
    	"37.20.230.245",
       	"46.30.165.47",
       	"46.118.157.189",
       	"46.161.9.",
    	"69.12.66.248",
    	"79.110.17.2",
    	"79.177.116.229",
    	"82.162.177.46",
       	"83.22.35.230",
       	"83.29.11.234",
       	"83.29.213.52",
       	"83.29.215.95",
    	"85.14.243.42",
    	"85.195.118.42",
    	"89.109.57.221",
    	"91.79.9.145",
    	"91.197.147.222",
    	"91.200.12.",
    	"91.236.251.178",
    	"91.241.226.233",
    	"93.188.39.163",
    	"95.135.152.240",
    	"95.181.217.2",
    	"103.75.245.101",
    	"109.86.71.21",
    	"109.184.132.19",
    	"109.184.120.116",
    	"134.249.209.112",
    	"141.105.64.97",
    	"146.88.202.76",
    	"162.220.162.10",
    	"175.44.17.199",
    	"176.10.98.132",
    	"176.48.62.19",
    	"176.227.198.122",
    	"178.33.82.189",
    	"178.137.83.166",
    	"178.137.80.124",
    	"178.137.163.222",
    	"178.159.37.61",
    	"178.159.37.73",
    	"178.162.197.145",
    	"185.105.89.38",
    	"185.175.130.150",
    	"188.124.242.134",
    	"188.162.245.198",
    	"188.163.109.153",
    	"193.201.224.",
    	"194.68.44.120",
    	"195.154.209.149",
    	"195.154.231.57",
       	"195.184.208.195",
    	"209.90.225.194",
    	"209.90.225.218",
    	"209.90.225.220",
    	"209.90.225.221",
    	"216.244.65"
    };

    // Blocked Email Domains
    public String[] blockedEmailDomains = {
   		"tempmail.de",
   		"temp-mail.de",
   		"spybox.de",
   		"shitmail.de",
   		"sharklasers.com",
   		"pokemail.net",
   		"spam4.me",
   		"guerrillamail",
   		"carins.io"
    };

	// CRON JOBS INCREASE TABLE THROUGHPUTS AS NEEDED.
	// WHEN THOSE JOBS COMPLETE, TABLES RESET AS BELOW.
    public long resetReadCapacityPostActivity = 1L;
    public long resetWriteCapacityPostActivity = 2L;
    public long resetReadCapacityPostsByEntity = 1L;
    public long resetWriteCapacityPostsByEntity = 1L;
    public long resetReadCapacityRanking = 2L;
    public long resetWriteCapacityRanking = 2L;
    public long resetReadCapacityPosts = 4L;
    public long resetWriteCapacityPosts = 2L;
    public long resetReadCapacityTopCharts = 3L;
    public long resetWriteCapacityTopCharts = 5L;
    public long resetReadCapacityRList = 3L;
    public long resetWriteCapacityRList = 1L;

    public String validatePassword (String password) {

    	try {

    		if (StringUtils.isBlank(password)) {
    			return "Password cannot be blank.";
            } else {
	            if (password.length() < 6) {
	            	return "Password must have six or more characters.";
	            }
	            if ( StringUtils.containsWhitespace(password) ) {
	            	return "Password cannot contain a space.";
	            }
            }

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"password: " + password);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    	return "System error validating password.";
    	}
    	return "SUCCESS";
    }

    public String validateEmailAddress (String emailAddress) {

    	try {

			final String EMAIL_PATTERN =
        			"^[_a-z0-9-\\+]+(\\.[_a-z0-9-]+)*@"
        			+ "[a-z0-9-]+(\\.[a-z0-9]+)*(\\.[a-z]{2,})$";
			final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

			// check if email address is blank
    		if (StringUtils.isBlank(emailAddress)) {
    			return "Email Address cannot be blank.";

   			// check if email address follows the standard pattern
    		} else if ( !pattern.matcher(emailAddress).matches() ) {
               	return "Email Address is not valid.";

            // check if user is using a virtual email service
    		} else if ( StringUtils.indexOfAny(emailAddress, blockedEmailDomains) != -1 ) {
               	return "Vitrual email addresses are not allowed.";
    		}

    	} catch (Exception e) {
	    	logger.log(Level.SEVERE,"validateEmailAddress");
	        logger.log(Level.SEVERE,"emailAddress: " + emailAddress);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    	return "System error validating Email Address.";
    	}
    	return "SUCCESS";
    }

	public String getLongestString(List<String> list) {

		int maxLength = 0;
		String longestString = null;
		for (String s : list) {
			if (s.length() > maxLength) {
				maxLength = s.length();
				longestString = s;
			}
		}
		return longestString;

	}

}