/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.heatbud.aws.HeatbudDynamoDBUtil;

/**
 * This is the authentication implementation for the Heatbud app.  It implements the
 * Spring UserDetails service to retrieve user information from DynamoDB.
 */
public class SpringAuthenticator implements UserDetailsService {

	// DynamoDB client
    @Autowired
	private HeatbudDynamoDBUtil dao;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if ( StringUtils.isBlank(username) ) {
			throw new UsernameNotFoundException("A blank username?!");
		}
		UserDetails details = dao.getUser(username);
		if (details == null) {
			throw new UsernameNotFoundException("Email not registered.");
		}
		return details;
	}

}
