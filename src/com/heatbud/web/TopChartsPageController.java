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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
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
import com.heatbud.entity.Ticker;
import com.heatbud.entity.User;
import com.heatbud.entity.Zone;
import com.heatbud.util.Configuration;

/**
 * This is the core of Heatbud Top Charts Page functionality.  It's a Spring controller
 * implemented using annotations.  Methods for loading and storing data are initiated in this class.
 */
@Controller
public class TopChartsPageController {

	// Logger object
	private static final Logger logger = Logger.getLogger(TopChartsPageController.class.getName());
	// Heatbud properties
	private static Configuration config = Configuration.getInstance();
	// DynamoDB client
    @Autowired
	private HeatbudDynamoDBUtil dao;

    /**
     * Request handler that builds the Top Charts page
     */
    @RequestMapping(value="/top/{topChartsName}", method={RequestMethod.GET})
    public String doTopCharts (@PathVariable String topChartsName, HttpServletRequest request, ModelMap map) {

    	String id = null;
    	String pageTitle = null;
    	String viewName = "top-charts";
    	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));
    	try {

    		/*** If Mobile, update view name ***/
    		try {
	    		Device device = DeviceUtils.getCurrentDevice(request);
	    		if (device.isMobile()) {
	    			viewName = "top-charts-mobile";
	    		}
    		} catch (Exception e) {
    			// if device is not specified, keep the default (desktop)
    		}

    		/*** Get UserId from Spring Security Context ***/
			String userId = null;
			try {
				User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				userId = user.getUserId();
			} catch (Exception e) {
				userId = "NULL";
			}
			map.addAttribute("userId", userId);

			/*** Get the latest period for which job was run previously ***/
			long generateTopChartsJobPeriod = Long.parseLong(dao.getAttribute("T", "generateTopChartsJobPeriod"));
	    	map.addAttribute("generateTopChartsJobPeriod", generateTopChartsJobPeriod);

    		/*** Map REST URL topChartsName to DATABASE recName & pageTitle ***/
	    	String recName;
    		if ( StringUtils.equals(topChartsName,"posts-trending-now") ) {
    			recName = "PO-" + generateTopChartsJobPeriod;
    			pageTitle = "TOP POSTS TRENDING NOW";
    		} else if ( StringUtils.equals(topChartsName,"zones-trending-now") ) {
    			recName = "Z-" + generateTopChartsJobPeriod;
    			pageTitle = "TOP ZONES TRENDING NOW";
    		} else if ( StringUtils.equals(topChartsName,"bloggers-trending-now") ) {
    			recName = "B-" + generateTopChartsJobPeriod;
    			pageTitle = "TOP BLOGGERS TRENDING NOW";
    		} else if ( StringUtils.equals(topChartsName,"pages-trending-now") ) {
    			recName = "PA-" + generateTopChartsJobPeriod;
    			pageTitle = "TOP PAGES TRENDING NOW";
    		} else if ( StringUtils.equals(topChartsName,"posts-all-time") ) {
    			recName = "PO";
    			pageTitle = "TOP POSTS ALL TIME";
    		} else if ( StringUtils.equals(topChartsName,"bloggers-all-time") ) {
    			recName = "B";
    			pageTitle = "TOP BLOGGERS ALL TIME";
    		} else if ( StringUtils.equals(topChartsName,"pages-all-time") ) {
    			recName = "PA";
    			pageTitle = "TOP PAGES ALL TIME";
    		} else if ( StringUtils.equals(topChartsName,"posts-just-published") ) {
    			recName = "J";
    			pageTitle = "JUST PUBLISHED POSTS";
    		} else {
    			// default
    			topChartsName = "bloggers-trending-now";
    			recName = "B-" + generateTopChartsJobPeriod;
    			pageTitle = "TOP BLOGGERS TRENDING NOW";
    		}

	    	/*** Put Chart Name and Page Title into the Map ***/
	    	map.addAttribute("topChartsName", topChartsName);
	    	map.addAttribute("pageTitle", pageTitle);

    		/*** Fetch first page of Top Charts ***/
	    	List<Post> topPostsList = new ArrayList<Post>();
	    	List<Zone> topZonesList = new ArrayList<Zone>();
	    	List<Entity> topEntitiesList = new ArrayList<Entity>();
	    	String topChartsKeyNextId = "NULL"; // default
	    	String topChartsKeyNextHI = "NULL"; // default
	    	List<Map<String,AttributeValue>> topChartsMapList = dao.getTopCharts(recName, null, null, false);

    		/*** Process the first page of Top Charts ***/
	   		for (int i = 0; i < topChartsMapList.size(); i++) {
	   			Map<String,AttributeValue> item = topChartsMapList.get(i);
	   			// display index 0 to postsPerPage-1 to the user
	   			// index postsPerPage is needed to decide whether to display the next page link or not
	   			if ( i < postsPerPage ) {
   					id = item.get("Id").getS();
		   			if ( StringUtils.startsWith(topChartsName,"post") ) {
		   				Post post = dao.getPost(id);
		   				if ( post != null ) {
	   						// hi from TopCharts becomes trending hi
		   					if ( StringUtils.contains(topChartsName, "trending") ) {
		   						post.setHiTrending(Long.parseLong(item.get("HI").getN()));
		   					}
				   			topPostsList.add(post);
		   				}
		   			} else if ( StringUtils.startsWith(topChartsName,"zone") ) {
		   				Zone zone = new Zone();
		   				Map<String, AttributeValue> zoneStats = dao.getZoneStats(id);
		   				zone.setZoneId(zoneStats.get("ZoneId").getS());
		   				zone.setZoneName(zoneStats.get("ZoneName").getS());
		   				zone.setZoneDesc(zoneStats.get("ZoneDesc").getS());
		   				zone.setZoneHeadshot(zoneStats.get("ZoneHeadshot").getS());
		   				zone.setPosts(Long.parseLong(zoneStats.get("Posts").getN()));
		   				zone.setComments(Long.parseLong(zoneStats.get("Comments").getN()));
			   			topZonesList.add(zone);
		   			} else {
		   				Entity entity = dao.getEntity(id);
		   				if ( entity != null ) {
	   						// hi from TopCharts becomes trending hi
		   					if ( StringUtils.contains(topChartsName, "trending") ) {
		   						entity.setHiTrending(Long.parseLong(item.get("HI").getN()));
		   					}
		   					topEntitiesList.add(entity);
		   				}
		   			}
	   			}
	   			// index postsPerPage-1 will be the next page key if there are postsPerPage+1 records
	   			if ( topChartsMapList.size() == postsPerPage+1) {
		   			if ( i == postsPerPage-1 ) {
		   				topChartsKeyNextId = item.get("Id").getS();
		   				topChartsKeyNextHI = item.get("HI").getN();
		   			}
	   			}
	   		}

    		/*** Add data to the Map ***/
	   		if ( StringUtils.startsWith(topChartsName,"post") ) {
	   			map.addAttribute("topChartsList", topPostsList);
   			} else if ( StringUtils.startsWith(topChartsName,"zone") ) {
	   			map.addAttribute("topChartsList", topZonesList);
	   		} else {
	   			map.addAttribute("topChartsList", topEntitiesList);
	   		}
	    	map.addAttribute("topChartsKeyNextId", topChartsKeyNextId);
	    	map.addAttribute("topChartsKeyNextHI", topChartsKeyNextHI);

    		/*** Fetch Tickers ***/
	    	List<Ticker> tickersList = new ArrayList<Ticker>();
	    	List<Map<String,AttributeValue>> tickersMapList = dao.getTickers();
	   		for (int i = 0; i < tickersMapList.size(); i++) {
	   			Ticker ticker = new Ticker();
	   			ticker.setTickerTime(Long.parseLong(tickersMapList.get(i).get("HI").getN()));
	   			ticker.setTickerDesc(tickersMapList.get(i).get("Id").getS());
	   			tickersList.add(ticker);
	   		}
	   		// add variables to the map
	    	map.addAttribute("tickersList", tickersList);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"topCharts: Name=" + topChartsName + " Id=" + id);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

    	return viewName;

    }

    /**
     * Request handler that builds the RSS feeds page for /rss
     * *** The main RSS page for Heatbud is https://www.heatbud.com/do/rss ***
     * We also support /rss just in case
     */
	@RequestMapping(value="/rss", method={RequestMethod.GET})
	public ModelAndView rss() {
		return doRss ();
	}

    /**
     * Request handler that builds the RSS feeds page
     * Use https://www.heatbud.com/do/rss for RSS feeds
     */
    @RequestMapping(value="/do/rss", method={RequestMethod.GET})
    public ModelAndView doRss () {

		ModelAndView mav = new ModelAndView("rssView");
    	try {
			/*** Get the latest period for which job was run previously ***/
   			long generateTopChartsJobPeriod = Long.parseLong(dao.getAttribute("T", "generateTopChartsJobPeriod"));

			/*** Retrieve post details and add them the list ***/
   			List<Post> topPostsList = new ArrayList<Post>();
	   		for (Map<String,AttributeValue> item : dao.getTopCharts("PO-" + generateTopChartsJobPeriod, null, null, false)) {
	   			String postId = item.get("Id").getS();
	   			if ( !StringUtils.contains(postId,"tech") && !StringUtils.contains(postId,"plumbing") ) {
	   				Post post = dao.getPost(postId);
	   				if ( post != null ) topPostsList.add(post);
	   			}
	   		}
			mav.addObject("feedContent", topPostsList);			
    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"TopChartsPageController.java - doRss");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
		return mav;

    }

    /**
     * Query next page of Top Charts. This method can be used for Posts, Zones, Bloggers or Pages.
     * @param generateTopChartsJobPeriod Top Charts Period corresponding to the first page
     * @param topChartsName REST URL name for the Top Charts
     * @param topChartsKeyNextId PostId or EntityId for the hash key to fetch next page of Top Charts
     * @param topChartsKeyNextHI HI for the range key to fetch next page of Top Charts
     * @return List of data elements and new keys for previous and next pages
     */
    @RequestMapping(value="/action/get-top-charts-next", method={RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String doGetTopChartsNext(long generateTopChartsJobPeriod, String topChartsName, String topChartsKeyNextId, String topChartsKeyNextHI) {

    	String id = null;
    	JSONObject topChartsInfo = new JSONObject();
    	JSONArray topChartsList = new JSONArray();
    	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));
    	try {

    		/*** Map REST URL name to DATABASE RECORD name ***/
	    	String recName;
    		if ( StringUtils.equals(topChartsName,"posts-trending-now") ) {
    			recName = "PO-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"zones-trending-now") ) {
    			recName = "Z-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"bloggers-trending-now") ) {
    			recName = "B-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"pages-trending-now") ) {
    			recName = "PA-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"posts-all-time") ) {
    			recName = "PO";
    		} else if ( StringUtils.equals(topChartsName,"bloggers-all-time") ) {
    			recName = "B";
    		} else if ( StringUtils.equals(topChartsName,"pages-all-time") ) {
    			recName = "PA";
    		} else if ( StringUtils.equals(topChartsName,"posts-just-published") ) {
    			recName = "J";
    		} else {
    			// default
    			topChartsName = "bloggers-trending-now";
    			recName = "B-" + generateTopChartsJobPeriod;
    		}

    		/*** Fetch next page of profile page Posts from the database ***/
    		if (StringUtils.equals(topChartsKeyNextId, "NULL")) topChartsKeyNextId = null;
       		if (StringUtils.equals(topChartsKeyNextHI, "NULL")) topChartsKeyNextHI = null;
       		List<Map<String,AttributeValue>> topChartsMapList = dao.getTopCharts(recName, topChartsKeyNextId, topChartsKeyNextHI, false);

	   		/*** Process the profile page posts map retrieved from the database ***/
	   		String topChartsKeyPrevId = "NULL"; // default
	   		String topChartsKeyPrevHI = "NULL"; // default
	   		topChartsKeyNextId = "NULL"; // reset
	   		topChartsKeyNextHI = "NULL"; // reset
	   		for (int i = 0; i < topChartsMapList.size(); i++) {
	   			Map<String,AttributeValue> item = topChartsMapList.get(i);
	   			// display index 0 to postsPerPage-1 to the user
	   			// index postsPerPage is needed to decide whether to display the next page link or not
	   			if ( i < postsPerPage ) {
   					id = item.get("Id").getS();
		   			if ( StringUtils.startsWith(topChartsName,"posts") ) {
		   				Post post = dao.getPost(id);
		   				if ( post != null ) {
	   						// hi from TopCharts becomes trending hi
		   					if ( StringUtils.contains(topChartsName, "trending") ) {
		   						post.setHiTrending(Long.parseLong(item.get("HI").getN()));
		   					}
				   			topChartsList.put(new JSONObject(post));
		   				}
		   			} else if ( StringUtils.startsWith(topChartsName,"zone") ) {
		   				Zone zone = new Zone();
		   				Map<String, AttributeValue> zoneStats = dao.getZoneStats(id);
		   				zone.setZoneId(zoneStats.get("ZoneId").getS());
		   				zone.setZoneName(zoneStats.get("ZoneName").getS());
		   				zone.setZoneDesc(zoneStats.get("ZoneDesc").getS());
		   				zone.setZoneHeadshot(zoneStats.get("ZoneHeadshot").getS());
		   				zone.setPosts(Long.parseLong(zoneStats.get("Posts").getN()));
		   				zone.setComments(Long.parseLong(zoneStats.get("Comments").getN()));
			   			topChartsList.put(new JSONObject(zone));
		   			} else {
		   				Entity entity = dao.getEntity(id);
		   				if ( entity != null ) {
	   						// hi from TopCharts becomes trending hi
		   					if ( StringUtils.contains(topChartsName, "trending") ) {
		   						entity.setHiTrending(Long.parseLong(item.get("HI").getN()));
		   					}
				   			topChartsList.put(new JSONObject(entity));
		   				}
		   			}
	   			}
	   			// index 0 will be the previous page key
	   			if ( i == 0 ) {
	   				topChartsKeyPrevId = item.get("Id").getS();
	   				topChartsKeyPrevHI = item.get("HI").getN();
	   			}
	   			// index postsPerPage-1 will be the next page key if there are postsPerPage+1 records
	   			if ( topChartsMapList.size() == postsPerPage+1) {
		   			if ( i == postsPerPage-1 ) {
		   				topChartsKeyNextId = item.get("Id").getS();
		   				topChartsKeyNextHI = item.get("HI").getN();
		   			}
	   			}
	   		}
	   		topChartsInfo.put("topChartsKeyPrevId", topChartsKeyPrevId);
	   		topChartsInfo.put("topChartsKeyPrevHI", topChartsKeyPrevHI);
	   		topChartsInfo.put("topChartsKeyNextId", topChartsKeyNextId);
	   		topChartsInfo.put("topChartsKeyNextHI", topChartsKeyNextHI);
	   		topChartsInfo.put("topChartsList", topChartsList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/get-top-charts-next");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return topChartsInfo.toString();
    }

    /**
     * Query previous page of Top Charts. This method can be used for Posts, Zones, Bloggers or Pages.
     * @param generateTopChartsJobPeriod Top Charts Period corresponding to the calling page
     * @param topChartsName REST URL name for the Top Charts
     * @param topChartsKeyPrevId PostId or EntityId for the hash key to fetch previous page of Top Charts
     * @param topChartsKeyPrevHI HI for the range key to fetch previous page of Top Charts
     * @return List of data elements and new keys for previous and next pages
     */
    @RequestMapping(value="/action/get-top-charts-previous", method={RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String doGetTopChartsPrevious(long generateTopChartsJobPeriod, String topChartsName, String topChartsKeyPrevId, String topChartsKeyPrevHI) {

    	String id = null;
    	JSONObject topChartsInfo = new JSONObject();
    	JSONArray topChartsList = new JSONArray();
    	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));
    	try {

    		/*** Map REST URL name to DATABASE RECORD name ***/
	    	String recName;
    		if ( StringUtils.equals(topChartsName,"posts-trending-now") ) {
    			recName = "PO-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"zones-trending-now") ) {
    			recName = "Z-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"bloggers-trending-now") ) {
    			recName = "B-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"pages-trending-now") ) {
    			recName = "PA-" + generateTopChartsJobPeriod;
    		} else if ( StringUtils.equals(topChartsName,"posts-all-time") ) {
    			recName = "PO";
    		} else if ( StringUtils.equals(topChartsName,"bloggers-all-time") ) {
    			recName = "B";
    		} else if ( StringUtils.equals(topChartsName,"pages-all-time") ) {
    			recName = "PA";
    		} else if ( StringUtils.equals(topChartsName,"posts-just-published") ) {
    			recName = "J";
    		} else {
    			// default
    			topChartsName = "bloggers-trending-now";
    			recName = "B-" + generateTopChartsJobPeriod;
    		}

    		/*** Fetch previous page of Draft Posts from the database ***/
    		if (StringUtils.equals(topChartsKeyPrevId, "NULL")) topChartsKeyPrevId = null;
       		if (StringUtils.equals(topChartsKeyPrevHI, "NULL")) topChartsKeyPrevHI = null;
       		List<Map<String,AttributeValue>> topChartsMapList = dao.getTopCharts(recName, topChartsKeyPrevId, topChartsKeyPrevHI, true);

	   		/*** Process the profile page posts map retrieved from the database ***/
	   		topChartsKeyPrevId = "NULL"; // default
	   		topChartsKeyPrevHI = "NULL"; // default
	   		String topChartsKeyNextId = "NULL"; // reset
	   		String topChartsKeyNextHI = "NULL"; // reset
	   		for (int i = topChartsMapList.size()-1; i >= 0; i--) {
	   			Map<String,AttributeValue> item = topChartsMapList.get(i);
	   			// display index 0 to postsPerPage-1 to the user
	   			// index postsPerPage is needed to decide whether to display the next page link or not
	   			if ( i < postsPerPage ) {
   					id = item.get("Id").getS();
		   			if ( StringUtils.startsWith(topChartsName,"post") ) {
		   				Post post = dao.getPost(id);
		   				if ( post != null ) {
	   						// hi from TopCharts becomes trending hi
		   					if ( StringUtils.contains(topChartsName, "trending") ) {
		   						post.setHiTrending(Long.parseLong(item.get("HI").getN()));
		   					}
				   			topChartsList.put(new JSONObject(post));
		   				}
		   			} else if ( StringUtils.startsWith(topChartsName,"zone") ) {
		   				Zone zone = new Zone();
		   				Map<String, AttributeValue> zoneStats = dao.getZoneStats(id);
		   				zone.setZoneId(zoneStats.get("ZoneId").getS());
		   				zone.setZoneName(zoneStats.get("ZoneName").getS());
		   				zone.setZoneDesc(zoneStats.get("ZoneDesc").getS());
		   				zone.setZoneHeadshot(zoneStats.get("ZoneHeadshot").getS());
		   				zone.setPosts(Long.parseLong(zoneStats.get("Posts").getN()));
		   				zone.setComments(Long.parseLong(zoneStats.get("Comments").getN()));
			   			topChartsList.put(new JSONObject(zone));
		   			} else {
		   				Entity entity = dao.getEntity(id);
		   				if ( entity != null ) {
	   						// hi from TopCharts becomes trending hi
		   					if ( StringUtils.contains(topChartsName, "trending") ) {
		   						entity.setHiTrending(Long.parseLong(item.get("HI").getN()));
		   					}
				   			topChartsList.put(new JSONObject(entity));
		   				}
		   			}
	   			}
	   			// index 0 will be the next page key
	   			if ( i == 0 ) {
	   				topChartsKeyNextId = item.get("Id").getS();
	   				topChartsKeyNextHI = item.get("HI").getN();
	   			}
	   			// index postsPerPage-1 will be the previous page key if there are postsPerPage+1 records
	   			if ( topChartsMapList.size() == postsPerPage+1) {
		   			if ( i == postsPerPage-1 ) {
		   				topChartsKeyPrevId = item.get("Id").getS();
		   				topChartsKeyPrevHI = item.get("HI").getN();
		   			}
	   			}
	   		}
	   		topChartsInfo.put("topChartsKeyPrevId", topChartsKeyPrevId);
	   		topChartsInfo.put("topChartsKeyPrevHI", topChartsKeyPrevHI);
	   		topChartsInfo.put("topChartsKeyNextId", topChartsKeyNextId);
	   		topChartsInfo.put("topChartsKeyNextHI", topChartsKeyNextHI);
	   		topChartsInfo.put("topChartsList", topChartsList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/get-top-charts-previous");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return topChartsInfo.toString();
    }

}
