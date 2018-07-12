/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.web;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.heatbud.aws.HeatbudDynamoDBUtil;
import com.heatbud.entity.Entity;
import com.heatbud.entity.Post;
import com.heatbud.entity.User;
import com.heatbud.entity.Zone;

/**
 * This is the core of Heatbud Search Page functionality.  It's a Spring controller
 * implemented using annotations.  Methods for loading and storing data are initiated in this class.
 */
@Controller
public class SearchPageController {

	// Logger object
	private static final Logger logger = Logger.getLogger(SearchPageController.class.getName());
	// DynamoDB client
    @Autowired
	private HeatbudDynamoDBUtil dao;

    /**
     * Request handler that builds the Search page
     */
    @RequestMapping(value="/do/search", method={RequestMethod.GET})
    public String doSearch (ModelMap map) {

    	try {

	    	/*** Get UserId from Spring Security Context ***/
			User user = null;
			String userId = null;
			try {
				user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				userId = user.getUserId();
			} catch (Exception e) {
				userId = "NULL";
			}
			map.addAttribute("userId", userId);
			map.addAttribute("type", "post");				// default
			map.addAttribute("confineBloggerId", "Any");	// default

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

    	return "search";

    }

    /**
     * Request handler that builds the Search Results page
     */
    @RequestMapping(value="/search/{confineBloggerId}/{type}/{terms}", method={RequestMethod.GET})
    public ModelAndView doSearch (@PathVariable String confineBloggerId, @PathVariable String type, @PathVariable String terms, ModelMap map) {

    	try {

	    	/*** Get UserId from Spring Security Context ***/
			User user = null;
			String userId = null;
			try {
				user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				userId = user.getUserId();
			} catch (Exception e) {
				userId = "NULL";
			}
			map.addAttribute("userId", userId);

    		/*** Fetch Search Results ***/
	    	List<Post> searchPostsList = new ArrayList<Post>();
	    	List<Zone> searchZonesList = new ArrayList<Zone>();
	    	List<Entity> searchEntitiesList = new ArrayList<Entity>();
	    	String typ = "PA";
	    	if ( StringUtils.equals(type, "zone")) {
	    		typ = "Z";
	    	} else if ( StringUtils.equals(type, "post")) {
	    		typ = "PO";
	    	} else if ( StringUtils.equals(type, "blogger")) {
	    		typ = "B";
	    	} else if ( StringUtils.equals(type, "page")) {
	    		typ = "PA";
	    	}
	    	List<String> searchResultList = dao.getSearchResults (confineBloggerId, typ, terms);
	    	String id = null;
	    	int results = searchResultList.size();
	    	if ( results > 75 ) results = 75;

    		/*** Process Search Page Results ***/
	   		for (int i = 0; i < results; i++) {
	   			id = searchResultList.get(i);
	   			if ( StringUtils.equals(type,"post") ) {
	   				Post post = dao.getPost(id);
	   				if ( post != null ) searchPostsList.add(post);
	   			} else if ( StringUtils.equals(type,"zone") ) {
	   				Map<String, AttributeValue> zoneStats = dao.getZoneStats(id);
	   				if ( !zoneStats.isEmpty() ) {
		   				Zone zone = new Zone();
		   				zone.setZoneId(zoneStats.get("ZoneId").getS());
		   				zone.setZoneName(zoneStats.get("ZoneName").getS());
		   				zone.setZoneDesc(zoneStats.get("ZoneDesc").getS());
		   				try {
		   					zone.setZoneHeadshot(zoneStats.get("ZoneHeadshot").getS());
		   				} catch (Exception e) {
		   					zone.setZoneHeadshot("/resources/images/def-zone-image.png");
		   				}
		   				zone.setPosts(Long.parseLong(zoneStats.get("Posts").getN()));
		   				zone.setComments(Long.parseLong(zoneStats.get("Comments").getN()));
			   			searchZonesList.add(zone);
	   				}
	   			} else {
	   				Entity entity = dao.getEntity(id);
	   				if ( entity != null ) searchEntitiesList.add(entity);
	   			}
	   		}

			map.addAttribute("confineBloggerId", confineBloggerId);
			map.addAttribute("type", type);
			map.addAttribute("terms", terms);
			map.addAttribute("searchPostsList", searchPostsList);
			map.addAttribute("searchZonesList", searchZonesList);
			map.addAttribute("searchEntitiesList", searchEntitiesList);

			// I want to keep getting emails on the search terms
	        System.out.println("SEARCH: Type=" + type + " confineBloggerId=" + confineBloggerId + " Terms=" + terms);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

	    return new ModelAndView("search", map);

    }

    /**
     * Query list of bloggers to populate the blogger box.
     * @param terms search terms
     * @return List of blogger objects
     */
    @RequestMapping(value="/search/get-blogger-box", method={RequestMethod.POST})
    public @ResponseBody String doGetBloggerBox (String terms) {

    	JSONObject bloggersInfo = new JSONObject();
    	JSONArray bloggersList = new JSONArray();
    	try {

    		/*** Get List of Bloggers matching the criteria ***/
	    	List<String> searchResultList = dao.getSearchResults("Any", "B", terms);
	    	int results = searchResultList.size();
	    	if ( results > 75 ) results = 75;
	   		for (int i = 0; i < results; i++) {
   				Entity entity = dao.getEntity(searchResultList.get(i));
	   			if ( entity != null ) bloggersList.put(new JSONObject(entity));
	   		}
	    	bloggersInfo.put("bloggersList", bloggersList);

		} catch (Exception e) {
	    	try { bloggersInfo.put("ERROR", "Unable to fetch search results."); } catch (Exception e1) {}
	        logger.log(Level.SEVERE,"terms="+terms);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return bloggersInfo.toString();
    }

}
