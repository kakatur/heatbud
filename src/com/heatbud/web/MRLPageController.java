/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.authentication.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.heatbud.aws.HeatbudDynamoDBUtil;
import com.heatbud.aws.HeatbudS3Util;
import com.heatbud.aws.HeatbudSESUtil;
import com.heatbud.entity.Entity;
import com.heatbud.entity.Post;
import com.heatbud.entity.Comment;
import com.heatbud.entity.HeatbudS3Object;
import com.heatbud.entity.MyZone;
import com.heatbud.entity.S3File;
import com.heatbud.entity.TopZone;
import com.heatbud.entity.User;
import com.heatbud.util.Configuration;
import com.heatbud.util.HeatbudCommon;

/**
 * This is the core of Heatbud My Reading List Page functionality.  It's a Spring controller
 * implemented using annotations.  Methods for loading and storing data are initiated in this class.
 */
@Controller
public class MRLPageController {

	// Logger object
	private static final Logger logger = Logger.getLogger(MRLPageController.class.getName());
	// Heatbud properties
	private static Configuration config = Configuration.getInstance();
	// AWS credentials
	private static AWSCredentials creds = new BasicAWSCredentials(config.getProperty("accessKey"), config.getProperty("secretKey"));
	// SQS client
    private static AmazonSQS sqs = AmazonSQSClientBuilder.standard()
      	.withEndpointConfiguration(new EndpointConfiguration("https://sqs.us-west-2.amazonaws.com", "us-west-2"))
       	.withCredentials(new AWSStaticCredentialsProvider(creds))
      	.build();
    // Common Functions client
    @Autowired
	private HeatbudCommon common;
	// DynamoDB client
    @Autowired
	private HeatbudDynamoDBUtil dao;
	// S3 Util
    @Autowired
	private HeatbudS3Util s3;
	// SES client
    @Autowired
	private HeatbudSESUtil ses;
    // Authentication manager
    @Autowired
    @Qualifier("authenticationManager")
    protected AuthenticationManager authenticationManager;
	// UserAccountController
    @Autowired
	private UserAccountController ua;

    /**
     * Builds my-reading-list page from blog id (for backward compatibility - remove on 12/31/2017)
     */
	@RequestMapping(value="/blog/{name}", method={RequestMethod.GET})
	public String doBlog(@PathVariable String name, HttpServletRequest request, ModelMap map) {
		return doMRL (null, name, null, null, null, null, null, null, null, null, null, request, map);
	}

    /**
     * Builds my-reading-list page from post id
     */
	@RequestMapping(value="/post/{name}", method={RequestMethod.GET})
	public String doPost(@PathVariable String name, HttpServletRequest request, ModelMap map) {
		return doMRL (null, name, null, null, null, null, null, null, null, null, null, request, map);
	}

    /**
     * Builds my-reading-list page from zone id
     */
	@RequestMapping(value="/zone/{name}", method={RequestMethod.GET})
	public String doZone(@PathVariable String name, HttpServletRequest request, ModelMap map) {
		return doMRL (name, null, null, null, null, null, null, null, null, null, null, request, map);
	}

    /**
     * Builds my-reading-list page from zone id and forces the user to the zone home page instead of to the last read post
     */
	@RequestMapping(value="/zone-home/{name}", method={RequestMethod.GET})
	public String doZoneHome(@PathVariable String name, HttpServletRequest request, ModelMap map) {
		return doMRL (name, null, null, null, null, null, null, null, null, null, "YES", request, map);
	}

    /**
     * Builds my-reading-list page
     * @param forceZoneHome Navigate signed-in user to the Zone Home page (by default, they will be taken to the last viewed post)
     */
	@RequestMapping(value="/do/start", method={RequestMethod.GET, RequestMethod.POST})
	public String doMRL(String zoneId, String postId,
				String myZonesKeyRefreshZO, String myZonesKeyRefreshZoneId,
				String myZonesKeyAlternateRefreshZO, String myZonesKeyAlternateRefreshZoneId,
				String topZonesKeyRefreshZO, String topZonesKeyRefreshZoneId,
				String topZonesKeyAlternateRefreshZO, String topZonesKeyAlternateRefreshZoneId,
				String forceZoneHome, HttpServletRequest request, ModelMap map) {

		String userId = null;
		String topMostZoneId = null; // default zoneId for landing
    	String viewName = "/my-reading-list";
    	String pageType = "POST";
		try {

    		/*** If Mobile, update view name ***/
    		try {
	    		Device device = DeviceUtils.getCurrentDevice(request);
	    		if (device.isMobile()) {
	    			viewName = "/my-reading-list-mobile";
	    		}
    		} catch (Exception e) {
    			// if device is not specified, take default as desktop
    		}

    		/*** Get UserId from Spring Security Context ***/
			User user = null;
			try {
				user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				userId = user.getUserId();
			} catch (Exception e) {
				userId = "NULL";
			}
			map.addAttribute("userId", userId);

	    	/*** Fetch a page of My Zones ***/
			// defaults
			List<Map<String,AttributeValue>> myZonesMapList = new ArrayList<Map<String,AttributeValue>>();
	    	List<MyZone> myZonesList = new ArrayList<MyZone>();
	    	String myZonesKeyPrevZO = "NULL";
	    	String myZonesKeyPrevZoneId = "NULL";
	    	String myZonesKeyNextZO = "NULL";
	    	String myZonesKeyNextZoneId = "NULL";
	    	if ( !StringUtils.equals(userId,"NULL") ) {
				// fetch data from the database
		    	if ( StringUtils.equals(myZonesKeyRefreshZO,"NULL") || StringUtils.isBlank(myZonesKeyRefreshZO) ) {
			    	myZonesMapList = dao.getZones(userId, null, null, true, 26);
		    		myZonesKeyRefreshZO = "NULL";
		    		myZonesKeyRefreshZoneId = "NULL";
		    	} else {
			    	myZonesMapList = dao.getZones(userId, myZonesKeyRefreshZO, myZonesKeyRefreshZoneId, true, 26);
		    	}
		    	// extract data
		   		for (int i = 0; i < myZonesMapList.size(); i++) {
		   			Map<String,AttributeValue> item = myZonesMapList.get(i);
		   			// display index 0 to 24 to the user
		   			// index 25 is needed to decide whether to display the next page link or not
		   			if ( i < 25 ) {
		   				MyZone myZone = new MyZone();
		   				myZone.setZoneId(item.get("ZoneId").getS());
		   				myZone.setZoneName(item.get("ZoneName").getS());
	   					myZone.setUnreadCount(Long.parseLong(item.get("Posts").getN())-Long.parseLong(item.get("CurrentRO").getN()));
		   				myZonesList.add(myZone);
		   			}
		   			// index 0 will be the previous page key if not first page
		   			if ( i == 0 ) {
		   				if (!StringUtils.equals(myZonesKeyRefreshZO,"NULL")) {
		   					myZonesKeyPrevZO = item.get("ZoneOrder").getN();
		   					myZonesKeyPrevZoneId = item.get("ZoneId").getS();
		   				}
		   			}
		   			// index 24 will be the next page key if there are 26 records
		   			if ( i == 24 ) {
			   			if ( myZonesMapList.size() == 26) {
			   				myZonesKeyNextZO = item.get("ZoneOrder").getN();
			   				myZonesKeyNextZoneId = item.get("ZoneId").getS();
			   			}
		   			}
		   		}
	    	}
	    	map.addAttribute("myZonesList", myZonesList);
	    	map.addAttribute("myZonesKeyPrevZO", myZonesKeyPrevZO);
	    	map.addAttribute("myZonesKeyPrevZoneId", myZonesKeyPrevZoneId);
	    	map.addAttribute("myZonesKeyNextZO", myZonesKeyNextZO);
	    	map.addAttribute("myZonesKeyNextZoneId", myZonesKeyNextZoneId);
	    	map.addAttribute("myZonesKeyRefreshZO", myZonesKeyRefreshZO);
	    	map.addAttribute("myZonesKeyRefreshZoneId", myZonesKeyRefreshZoneId);
	    	map.addAttribute("myZonesKeyAlternateRefreshZO", myZonesKeyAlternateRefreshZO);
	    	map.addAttribute("myZonesKeyAlternateRefreshZoneId", myZonesKeyAlternateRefreshZoneId);

	    	/*** Fetch a page of Top Zones ***/
	    	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));
			List<Map<String,AttributeValue>> topZonesMapList;
	    	if ( StringUtils.equals(topZonesKeyRefreshZO,"NULL") || StringUtils.isBlank(topZonesKeyRefreshZO) ) {
		    	topZonesMapList = dao.getTopCharts("Z-"+dao.getAttribute("T", "generateTopChartsJobPeriod"), null, null, false);
	    		topZonesKeyRefreshZO = "NULL";
	    		topZonesKeyRefreshZoneId = "NULL";
	    	} else {
		    	topZonesMapList = dao.getTopCharts("Z-"+dao.getAttribute("T", "generateTopChartsJobPeriod"), topZonesKeyRefreshZoneId, topZonesKeyRefreshZO, false);
	    	}
	    	// extract data
	    	List<TopZone> topZonesList = new ArrayList<TopZone>();
	    	String topZonesKeyPrevZO = "NULL"; // default
	    	String topZonesKeyPrevZoneId = "NULL"; // default
	    	String topZonesKeyNextZO = "NULL"; // default
	    	String topZonesKeyNextZoneId = "NULL"; // default
	   		for (int i = 0; i < topZonesMapList.size(); i++) {
	   			Map<String,AttributeValue> item = topZonesMapList.get(i);
	   			// display index 0 to postsPerPage-1 to the user
	   			// index postsPerPage is needed to decide whether to display the next page link or not
	   			if ( i < postsPerPage ) {
	   				TopZone topZone = new TopZone();
	   				topZone.setZoneId(item.get("Id").getS());
	   				topZone.setZoneName(item.get("ZoneName").getS());
	   				topZonesList.add(topZone);
	   			}
	   			// index 0 will be the previous page key if not first page
	   			if ( i == 0 ) {
	   				if (!StringUtils.equals(topZonesKeyRefreshZO,"NULL")) {
	   					topZonesKeyPrevZO = item.get("HI").getN();
	   					topZonesKeyPrevZoneId = item.get("Id").getS();
	   				}
	   				topMostZoneId = item.get("Id").getS(); // default zone for landing
	   			}
	   			// index postsPerPage-1 will be the next page key if there are postsPerPage+1 records
	   			if ( topZonesMapList.size() == postsPerPage+1) {
		   			if ( i == postsPerPage-1 ) {
		   				topZonesKeyNextZO = item.get("HI").getN();
		   				topZonesKeyNextZoneId = item.get("Id").getS();
		   			}
	   			}
	   		}
	    	map.addAttribute("topZonesList", topZonesList);
	    	map.addAttribute("topZonesKeyPrevZO", topZonesKeyPrevZO);
	    	map.addAttribute("topZonesKeyPrevZoneId", topZonesKeyPrevZoneId);
	    	map.addAttribute("topZonesKeyNextZO", topZonesKeyNextZO);
	    	map.addAttribute("topZonesKeyNextZoneId", topZonesKeyNextZoneId);
	    	map.addAttribute("topZonesKeyRefreshZO", topZonesKeyRefreshZO);
	    	map.addAttribute("topZonesKeyRefreshZoneId", topZonesKeyRefreshZoneId);
	    	map.addAttribute("topZonesKeyAlternateRefreshZO", topZonesKeyAlternateRefreshZO);
	    	map.addAttribute("topZonesKeyAlternateRefreshZoneId", topZonesKeyAlternateRefreshZoneId);

	    	/*** Declare variables ***/
			String prevPostId = "NULL";
			String nextPostId = "NULL";
			Post post = new Post();
			Post prevPost = new Post();
			Post nextPost = new Post();
	    	long currentRO = 0;
	    	Map<String, AttributeValue> zoneStats = null;

	    	/*********** If PostId is the input ***********/
			if (StringUtils.isNotBlank(postId)) {
		    	/*** Validate the postId and get post metadata at the same time ***/
				post = dao.getPost(postId);
				if ( post == null ) {
			    	return "/do/notfound";
				}
				// get stats for the zone
				zoneId = post.getZoneId();
				if ( StringUtils.isBlank(zoneId) ) {
			        logger.log(Level.SEVERE,"Blank zoneId for postId: " + postId + " userId: " + userId);
				}
	   			zoneStats = dao.getZoneStats(zoneId);
		    	if ( !StringUtils.equals(userId,"NULL") ) {
					// if logged-in user, update/initialize RL based on input postId
		    		Map<String, AttributeValue> rlStats = dao.getRLStats(userId, zoneId);
		    		try { currentRO = Long.parseLong(rlStats.get("CurrentRO").getN()); } catch (Exception e) {}
		    		if ( currentRO == 0 ) {
		    			// user doesn't have a reading list in the zone
		    			// now, insert input postId into RL
		    			// it will usually go to currentRO=1, but might end up at another position if there was a concurrent transaction
		    			currentRO = dao.putRL(userId, zoneId, postId);
			    		// add some more posts to user's RL
						long maxRO = dao.expandRL(userId, zoneId, 10);
						if (maxRO == 0) maxRO = currentRO;
						// insert stats record into zones table
			    		if ( rlStats.isEmpty() ) {
			    			dao.putRLStats(userId, zoneId, dao.getPostsForZone(zoneId), currentRO, maxRO);
			    		} else {
			    			dao.putRLStats(userId, zoneId, 0, currentRO, maxRO); // don't update the posts if the record already exists
			    		}
		    		} else {
		    			// update RL with the input postId
						long newCurrentRO = dao.updateRL(userId, zoneId, currentRO, postId);
		    			// expand RL when the user has 2 posts left to read in the RL
						if ( newCurrentRO > currentRO ) { // to ensure that we don't call expandRL when the user navigates back to the posts that have already been read
				    		long maxRO = Long.parseLong(rlStats.get("MaxRO").getN());
			    			if ( newCurrentRO == maxRO-2 ) {
			    	   			/*** Register expandRL in SQS to be processed by cron job ***/
			    				JSONObject msgBody = new JSONObject();
			    				msgBody.put("category", "expandRL");
			    				msgBody.put("userId", userId);
			    				msgBody.put("zoneId", zoneId);
			    				sqs.sendMessage (
			    					new SendMessageRequest()
			    						.withQueueUrl( config.getProperty("processQueueSQS") )
			    						.withMessageBody(msgBody.toString())
			    				);
			    			}
						}
						// set currentRO to the newCurrentRO so we can fetch the post details.
						currentRO = newCurrentRO;
		    		}
			    	// get prev and next postIds from the RL based on currentRO
		    		List<Map<String,AttributeValue>> itemsRL = dao.getRL(userId, zoneId, currentRO);
					for (int i = 0; i < itemsRL.size(); i++) {
						Map<String,AttributeValue> item = itemsRL.get(i);
						int RO = Integer.parseInt(item.get("RO").getN());
						if ( RO < currentRO ) {
							prevPostId = item.get("PostId").getS();
						} else if ( RO > currentRO ) {
							nextPostId = item.get("PostId").getS();
						}
					}
		    	} else {
		    		// for guest - get currentRO of the postId in the RL for userId="guest"
					currentRO = dao.getRO4PostId("guest", zoneId, postId);
			    	// get prev and next postIds from the RL based on currentRO
		    		List<Map<String,AttributeValue>> itemsRL = dao.getRL("guest", zoneId, currentRO);
					for (int i = 0; i < itemsRL.size(); i++) {
						Map<String,AttributeValue> item = itemsRL.get(i);
						int RO = Integer.parseInt(item.get("RO").getN());
						if ( RO < currentRO ) {
							prevPostId = item.get("PostId").getS();
						} else if ( RO > currentRO ) {
							nextPostId = item.get("PostId").getS();
						}
					}
		    	}
	    	/*********** If ZoneId is the input ***********/
			} else {
		    	/*** Validate the zone and get zone stats at the same time ***/
				if (StringUtils.isNotBlank(zoneId)) {
		   			zoneStats = dao.getZoneStats(zoneId);
					if (zoneStats.size() == 0) {
				    	return "/do/notfound";
					}
					if ( !StringUtils.equals(userId,"NULL") ) {
						dao.setAttribute(userId, "lastVisitedZoneId", zoneId);
					}
				} else {
					if ( StringUtils.equals(userId,"NULL") ) {
						// if user is guest and input zone is null, set top zone as the current zone
						zoneId = topMostZoneId.toString();
			   			zoneStats = dao.getZoneStats(zoneId);
					} else {
						// if the user is logged-in, retrieve the last visited zone
						zoneId = dao.getAttribute(userId, "lastVisitedZoneId");
						if ( StringUtils.isBlank(zoneId) ) {
							// if the user has no last visited zone, set zone to top most zone
							zoneId = topMostZoneId.toString();
							dao.setAttribute(userId, "lastVisitedZoneId", zoneId);
				   			zoneStats = dao.getZoneStats(zoneId);
						} else {
							zoneStats = dao.getZoneStats(zoneId);
							if ( zoneStats.size() == 0 ) {
								// if the last visited zone doesn't exist, set zone to top most zone
								zoneId = topMostZoneId.toString();
								dao.setAttribute(userId, "lastVisitedZoneId", zoneId);
					   			zoneStats = dao.getZoneStats(zoneId);
							}
						}
					}
				}
		    	/*** If logged-in user, get currentRO in the zone ***/
		    	if ( !StringUtils.equals(userId,"NULL") ) {
		    		Map<String, AttributeValue> rlStats = dao.getRLStats(userId, zoneId);
		    		try { currentRO = Long.parseLong(rlStats.get("CurrentRO").getN()); } catch (Exception e) {}
		    	}
		    	/*** Show Zone Home Page when the ***/
		    	/*** 1. User has not logged-in (currentRO = 0) ***/
		    	/*** 2. Logged-in but never visited the zone (if favorited the zone, currentRO = 0, if not, currentRO = -1) ***/
		    	/*** 3. Logged-in and clicked Zone Home Page link (forceZoneHome = YES) ***/
	    		if ( currentRO <= 0 || StringUtils.equals(forceZoneHome,"YES") ) {
	    			// Since we need top three posts, set currentRO=2 (so we get posts 1,2,3)
	    			currentRO = 2;
	    			pageType = "ZONE";
	    		}
		    	/*** Get postId at the currentRO and one before and one after ***/
	    		List<Map<String,AttributeValue>> itemsRL = new ArrayList<Map<String,AttributeValue>>();
	    		if ( StringUtils.equals(userId, "NULL") || StringUtils.equals(pageType, "ZONE") ) {
	    			itemsRL = dao.getRL("guest", zoneId, currentRO);
	    		} else {
	    			itemsRL = dao.getRL(userId, zoneId, currentRO);
	    		}
				for (int i = 0; i < itemsRL.size(); i++) {
					Map<String,AttributeValue> item = itemsRL.get(i);
					int RO = Integer.parseInt(item.get("RO").getN());
					if ( RO < currentRO ) {
						prevPostId = item.get("PostId").getS();
					} else if ( RO == currentRO ) {
						postId = item.get("PostId").getS();
					} else if ( RO > currentRO ) {
						nextPostId = item.get("PostId").getS();
					}
				}
		    	/*** Get post metadata for the current postId ***/
				if ( StringUtils.isNotBlank(postId) ) {
					post = dao.getPost(postId);
				}
			}

   			/*** Fetch zone stats for Zone ***/
	    	map.addAttribute("zoneId", zoneId);
	    	map.addAttribute("zoneName", zoneStats.get("ZoneName").getS());
	    	map.addAttribute("zoneDesc", zoneStats.get("ZoneDesc").getS());
	    	map.addAttribute("zoneWho", zoneStats.get("ZoneWho").getS());
	    	map.addAttribute("posts", Long.parseLong(zoneStats.get("Posts").getN()));
	    	map.addAttribute("zoneHI", Long.parseLong(zoneStats.get("HI").getN()));
	    	// isadmin
	    	try {
	    		if ( zoneStats.get("Admins").getSS().contains(userId) ) {
	    			map.addAttribute("isAdmin", "true");
	    		} else {
		    		map.addAttribute("isAdmin", "false");
	    		}
	    	} catch (Exception e) {
	    		map.addAttribute("isAdmin", "false");
	    	}
	    	// the below attributes are needed only for the zone home page
			if (StringUtils.equals(pageType,"ZONE")) {
				// zone head shot
				String zoneHeadshot = "/resources/images/def-zone-image.png";
				try {
					zoneHeadshot = zoneStats.get("ZoneHeadshot").getS();
				} catch (Exception e) { }
		    	map.addAttribute("zoneHeadshot", zoneHeadshot);
		    	// comments
		    	map.addAttribute("comments", Long.parseLong(zoneStats.get("Comments").getN()));
		    	// admins
		    	List<Entity> admins = new ArrayList<Entity>();
		    	try {
			   		for (int i = 0; i < zoneStats.get("Admins").getSS().size(); i++) {
			   			String id = zoneStats.get("Admins").getSS().get(i);
		   				Entity blogger = dao.getEntity(id);
			   			admins.add(blogger);
			   		}
		    	} catch (Exception e) {}
		    	map.addAttribute("admins", admins);
		    	// admin requests
		    	try {
		    		if ( zoneStats.get("AdminRequests").getSS().contains(userId) ) {
		    			map.addAttribute("isRequestedAdmin", "true");
		    		} else {
			    		map.addAttribute("isRequestedAdmin", "false");
		    		}
		    	} catch (Exception e) {
		    		map.addAttribute("isRequestedAdmin", "false");
		    	}
		    	List<Entity> adminRequests = new ArrayList<Entity>();
		    	try {
			   		for (int i = 0; i < zoneStats.get("AdminRequests").getSS().size(); i++) {
			   			String id = zoneStats.get("AdminRequests").getSS().get(i);
		   				Entity blogger = dao.getEntity(id);
		   				adminRequests.add(blogger);
			   		}
		    	} catch (Exception e) {}
		    	map.addAttribute("adminRequests", adminRequests);
			}

			/*** Add post to Map ***/
			if ( !StringUtils.isBlank(postId) ) {
				map.addAttribute("post", post);
			}

			/*** Add prev and next post Ids to the map ***/
			map.addAttribute("prevPostId", prevPostId);
			map.addAttribute("nextPostId", nextPostId);

	    	if ( !StringUtils.equals(userId,"NULL") ) {
	    		String primaryPageId = dao.getPrimaryPageId(userId);
		    	map.addAttribute("primaryPageId", primaryPageId);
	    	}

			/*** If Zone Home Page, fetch prev and next post details and return ***/
			map.addAttribute("pageType", pageType);
			if (StringUtils.equals(pageType,"ZONE")) {
				if ( !StringUtils.equals(prevPostId, "NULL") ) {
					prevPost = dao.getPost(prevPostId);
					map.addAttribute("prevPost", prevPost);
				}
				if ( !StringUtils.equals(nextPostId, "NULL") ) {
					nextPost = dao.getPost(nextPostId);
					map.addAttribute("nextPost", nextPost);
				}
				return viewName;
			}

    		/*** Register post views if IPAddress is viewing the post for the first time ***/
			if ( StringUtils.equals(post.getPublishFlag(),"Y") ) {
				// get client IP address
				String ipAddress = null;
				ipAddress = request.getHeader("X-FORWARDED-FOR");
				if ( StringUtils.isBlank(ipAddress) ) {
					ipAddress = request.getRemoteAddr();
				}
				// check if the client machine has viewed this page already
				boolean viewed = dao.checkViewed(postId, ipAddress);
				if ( viewed ) {
					// Log IpAddress into catalina.out
					System.out.println("DUPLICATE REQUEST:" + postId + ":" + ipAddress );
				} else {
		   			/*** Register viewPost in SQS to be processed by cron job ***/
					JSONObject msgBody = new JSONObject();
					msgBody.put("category", "viewPost");
					msgBody.put("postId", postId);
					msgBody.put("zoneId", zoneId);
					msgBody.put("zoneName", zoneStats.get("ZoneName").getS());
					msgBody.put("bloggerId", post.getBloggerId());
					msgBody.put("pageId", post.getPageId() + " ");
					msgBody.put("ipAddress", ipAddress);
					sqs.sendMessage (
						new SendMessageRequest()
							.withQueueUrl( config.getProperty("processQueueSQS") )
							.withMessageBody(msgBody.toString())
					);
				}
			}

	    	/*** Get user's current vote ***/
	    	if ( !StringUtils.equals(userId,"NULL") ) {
				int currentVote = dao.getVote(userId, postId);
				map.addAttribute("currentVote", currentVote);
	    	}

	    	/*** Fetch Blogger information ***/
			if (StringUtils.isNotBlank(postId)) {
				Entity blogger = dao.getEntity(post.getBloggerId());
				map.addAttribute("blogger", blogger);
			}

	    	/*** Fetch first page of Comments ***/
	    	List<Comment> commentsList = new ArrayList<Comment>();
	    	String commentsKeyNext = "NULL"; // default
			if (StringUtils.isNotBlank(postId)) {
		    	List<Map<String,AttributeValue>> commentsMapList = dao.getComments(postId, null, false);
		   		for (int i = 0; i < commentsMapList.size(); i++) {
		   			Map<String,AttributeValue> item = commentsMapList.get(i);
		   			// display index 0 to 4 to the user
		   			// index 5 is needed to decide whether to display the next page link or not
		   			if ( i < 5 ) {
			   			Comment comment = new Comment();
			   			comment.setPostId(postId);
			   			comment.setCommentDate(item.get("CommentDate").getN());
			   			comment.setCommentText(item.get("CommentText").getS());
			   			comment.setCommenterId(item.get("CommenterId").getS());
			   			comment.setCommenterName(dao.getEntityName(comment.getCommenterId()));
			   			commentsList.add(comment);
		   			}
		   			// index 4 will be the next page key if there are 6 records
		   			if ( i == 4 ) {
			   			if ( commentsMapList.size() == 6) {
			   				commentsKeyNext = item.get("CommentDate").getN();
			   			}
		   			}
		   		}
			}
	   		// add variables to the map
	    	map.addAttribute("commentsList", commentsList);
	    	map.addAttribute("commentsKeyNext", commentsKeyNext);

    		/*** Get followerId of the user, if following comments ***/
	    	/*** Each user will have a different FollowerId for each post ***/
	    	if ( !StringUtils.equals(userId,"NULL") ) {
	    		String followerId = dao.getFollowerId(postId, user.getUsername());
		    	map.addAttribute("followerId", followerId);
			} else {
		    	map.addAttribute("followerId", "NULL");
	    	}
    		int cfCount = dao.getCommentFollowersCount(postId);
	    	map.addAttribute("cfCount", cfCount);

    		/*** Check if the user has commented on the post before ***/
	    	if ( !StringUtils.equals(userId,"NULL") ) {
	    		String checkCommenterIdExists = dao.checkCommenterIdExists(postId, user.getUserId());
		    	map.addAttribute("checkCommenterIdExists", checkCommenterIdExists);
	    	}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId + " postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

    	return viewName;

	}

    /**************************************************************************
     ************************** ZONE MODULE ***********************************
     **************************************************************************/

    /**
     * Query next page of My Zones
     * @param myZonesKeyNextZO ZoneOrder to fetch the next page of My Zones
     * @param myZonesKeyNextZoneId ZoneId to fetch the next page of My Zones
     * @return List of zones and new keys for previous and next pages
     */
    @RequestMapping(value="/zone/get-myzones-next", method={RequestMethod.POST})
    public @ResponseBody String doGetMyZonesNext(String myZonesKeyNextZO, String myZonesKeyNextZoneId) {

    	JSONObject myZonesInfo = new JSONObject();
    	JSONArray myZonesList = new JSONArray();
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();
			// get zones from database
			List<Map<String,AttributeValue>> myZonesMapList = dao.getZones(userId, myZonesKeyNextZO, myZonesKeyNextZoneId, true, 26);
			boolean isFirstPage = false;
			if ( StringUtils.equals(myZonesKeyNextZO,"NULL") ) isFirstPage = true;
	    	// defaults
	   		String myZonesKeyPrevZO = "NULL";
	   		String myZonesKeyPrevZoneId = "NULL";
	   		myZonesKeyNextZO = "NULL";
	   		myZonesKeyNextZoneId = "NULL";
	   		for (int i = 0; i < myZonesMapList.size(); i++) {
	   			Map<String,AttributeValue> item = myZonesMapList.get(i);
	   			// display index 0 to 24 to the user
	   			// index 25 is needed to decide whether to display the next page link or not
	   			if ( i < 25 ) {
	   				JSONObject myZone = new JSONObject();
	   				myZone.put("zoneId", item.get("ZoneId").getS());
	   				myZone.put("zoneName", item.get("ZoneName").getS());
	   				myZone.put("unreadCount", Long.parseLong(item.get("Posts").getN())-Long.parseLong(item.get("CurrentRO").getN()));
		   			myZonesList.put(myZone.toString());
	   			}
	   			// index 0 will be the previous page key
	   			if ( i == 0 ) {
	   				if ( !isFirstPage ) {
	   					myZonesKeyPrevZO = item.get("ZoneOrder").getN();
	   					myZonesKeyPrevZoneId = item.get("ZoneId").getS();
	   				}
	   			}
	   			// index 24 will be the next page key if there are 26 records
	   			if ( i == 24 ) {
		   			if ( myZonesMapList.size() == 26) {
		   				myZonesKeyNextZO = item.get("ZoneOrder").getN();
		   				myZonesKeyNextZoneId = item.get("ZoneId").getS();
		   			}
	   			}
	   		}
   			myZonesInfo.put("myZonesKeyPrevZO", myZonesKeyPrevZO);
   			myZonesInfo.put("myZonesKeyPrevZoneId", myZonesKeyPrevZoneId);
   			myZonesInfo.put("myZonesKeyNextZO", myZonesKeyNextZO);
   			myZonesInfo.put("myZonesKeyNextZoneId", myZonesKeyNextZoneId);
   			myZonesInfo.put("myZonesList", myZonesList);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/get-myzones-next");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return myZonesInfo.toString();
    }

    /**
     * Query previous page of My Zones
     * @param myZonesKeyPrevZO ZoneOrder to fetch the previous page of My Zones
     * @param myZonesKeyPrevZoneId ZoneId to fetch the previous page of My Zones
     * @return List of zone names and new keys for previous and next pages
     */
    @RequestMapping(value="/zone/get-myzones-prev", method={RequestMethod.POST})
    public @ResponseBody String doGetMyZonesPrev(String myZonesKeyPrevZO, String myZonesKeyPrevZoneId) {

    	JSONObject myZonesInfo = new JSONObject();
    	JSONArray myZonesList = new JSONArray();
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();
			// get zones from database
			List<Map<String,AttributeValue>> myZonesMapList = dao.getZones(userId, myZonesKeyPrevZO, myZonesKeyPrevZoneId, false, 26);
	    	// defaults
	   		myZonesKeyPrevZO = "NULL";
	   		myZonesKeyPrevZoneId = "NULL";
	   		String myZonesKeyNextZO = "NULL";
	   		String myZonesKeyNextZoneId = "NULL";
	   		String myZonesKeyRefreshZO = "NULL";
	   		String myZonesKeyRefreshZoneId = "NULL";
	   		for (int i = myZonesMapList.size()-1; i >= 0; i--) {
	   			Map<String,AttributeValue> item = myZonesMapList.get(i);
	   			// display index 0 to 24 to the user
	   			// index 25 is needed to decide whether to display the previous page link or not
	   			if ( i < 25 ) {
	   				JSONObject myZone = new JSONObject();
	   				myZone.put("zoneId", item.get("ZoneId").getS());
	   				myZone.put("zoneName", item.get("ZoneName").getS());
	   				myZone.put("unreadCount", Long.parseLong(item.get("Posts").getN())-Long.parseLong(item.get("CurrentRO").getN()));
		   			myZonesList.put(myZone.toString());
	   			}
	   			// index 0 will be the next page key
	   			if ( i == 0 ) {
	   				myZonesKeyNextZO = item.get("ZoneOrder").getN();
	   				myZonesKeyNextZoneId = item.get("ZoneId").getS();
	   			}
	   			// index 24 will be the previous page key if there are 26 records
	   			if ( i == 24 ) {
		   			if ( myZonesMapList.size() == 26) {
		   				myZonesKeyPrevZO = item.get("ZoneOrder").getN();
		   				myZonesKeyPrevZoneId = item.get("ZoneId").getS();
		   			}
	   			}
	   			// index 25 will be the next page key for page refresh
	   			if ( i == 25 ) {
	   				myZonesKeyRefreshZO = item.get("ZoneOrder").getN();
	   				myZonesKeyRefreshZoneId = item.get("ZoneId").getS();
	   			}
	   		}
   			myZonesInfo.put("myZonesKeyPrevZO", myZonesKeyPrevZO);
   			myZonesInfo.put("myZonesKeyPrevZoneId", myZonesKeyPrevZoneId);
   			myZonesInfo.put("myZonesKeyNextZO", myZonesKeyNextZO);
   			myZonesInfo.put("myZonesKeyNextZoneId", myZonesKeyNextZoneId);
   			myZonesInfo.put("myZonesKeyRefreshZO", myZonesKeyRefreshZO);
   			myZonesInfo.put("myZonesKeyRefreshZoneId", myZonesKeyRefreshZoneId);
   			myZonesInfo.put("myZonesList", myZonesList);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/get-myzones-prev");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

    	return myZonesInfo.toString();
    }

    /**
     * Query next page of Top Zones
     * @param topZonesKeyNextZO ZoneOrder to fetch the next page of Top Zones
     * @param topZonesKeyNextZoneId ZoneId to fetch the next page of Top Zones
     * @return List of zone names and new keys for previous and next pages
     */
    @RequestMapping(value="/zone/get-topzones-next", method={RequestMethod.POST})
    public @ResponseBody String doGetTopZonesNext(String topZonesKeyNextZO, String topZonesKeyNextZoneId) {

    	JSONObject topZonesInfo = new JSONObject();
    	JSONArray topZonesList = new JSONArray();
    	try {
        	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));
			// get zones from database
			List<Map<String,AttributeValue>> topZonesMapList = dao.getTopCharts("Z-"+dao.getAttribute("T", "generateTopChartsJobPeriod"), topZonesKeyNextZoneId, topZonesKeyNextZO, false);
			boolean isFirstPage = false;
			if ( StringUtils.equals(topZonesKeyNextZO,"NULL") ) isFirstPage = true;
	    	// defaults
	   		String topZonesKeyPrevZO = "NULL";
	   		String topZonesKeyPrevZoneId = "NULL";
	   		topZonesKeyNextZO = "NULL"; // reset
	   		topZonesKeyNextZoneId = "NULL"; // reset
	   		for (int i = 0; i < topZonesMapList.size(); i++) {
	   			Map<String,AttributeValue> item = topZonesMapList.get(i);
	   			// display index 0 to postsPerPage-1 to the user
	   			// index postsPerPage is needed to decide whether to display the next page link or not
	   			if ( i < postsPerPage ) {
	   				JSONObject topZone = new JSONObject();
	   				topZone.put("zoneId", item.get("Id").getS());
	   				topZone.put("zoneName", item.get("ZoneName").getS());
		   			topZonesList.put(topZone.toString());
	   			}
	   			// index 0 will be the previous page key
	   			if ( i == 0 ) {
	   				if ( !isFirstPage ) {
	   					topZonesKeyPrevZO = item.get("HI").getN();
	   					topZonesKeyPrevZoneId = item.get("Id").getS();
	   				}
	   			}
	   			// index postsPerPage-1 will be the next page key if there are postsPerPage+1 records
	   			if ( topZonesMapList.size() == postsPerPage+1) {
		   			if ( i == postsPerPage-1 ) {
		   				topZonesKeyNextZO = item.get("HI").getN();
		   				topZonesKeyNextZoneId = item.get("Id").getS();
		   			}
	   			}
	   		}
   			topZonesInfo.put("topZonesKeyPrevZO", topZonesKeyPrevZO);
   			topZonesInfo.put("topZonesKeyPrevZoneId", topZonesKeyPrevZoneId);
   			topZonesInfo.put("topZonesKeyNextZO", topZonesKeyNextZO);
   			topZonesInfo.put("topZonesKeyNextZoneId", topZonesKeyNextZoneId);
   			topZonesInfo.put("topZonesList", topZonesList);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/get-topzones-next");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return topZonesInfo.toString();
    }

    /**
     * Query previous page of Top Zones
     * @param topZonesKeyPrevZO ZoneOrder to fetch the previous page of Top Zones
     * @param topZonesKeyPrevZoneId ZoneId to fetch the previous page of Top Zones
     * @return List of zone names and new keys for previous and next pages
     */
    @RequestMapping(value="/zone/get-topzones-prev", method={RequestMethod.POST})
    public @ResponseBody String doGetTopZonesPrev(String topZonesKeyPrevZO, String topZonesKeyPrevZoneId) {

    	JSONObject topZonesInfo = new JSONObject();
    	JSONArray topZonesList = new JSONArray();
    	try {
        	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));
			// get zones from database
			List<Map<String,AttributeValue>> topZonesMapList = dao.getTopCharts("Z-"+dao.getAttribute("T", "generateTopChartsJobPeriod"), topZonesKeyPrevZoneId, topZonesKeyPrevZO, true);
	    	// defaults
	   		topZonesKeyPrevZO = "NULL";
	   		topZonesKeyPrevZoneId = "NULL";
	   		String topZonesKeyNextZO = "NULL";
	   		String topZonesKeyNextZoneId = "NULL";
	   		String topZonesKeyRefreshZO = "NULL";
	   		String topZonesKeyRefreshZoneId = "NULL";
	   		for (int i = topZonesMapList.size()-1; i >= 0; i--) {
	   			Map<String,AttributeValue> item = topZonesMapList.get(i);
	   			// display index 0 to postsPerPage-1 to the user
	   			// index postsPerPage is needed to decide whether to display the next page link or not
	   			if ( i < postsPerPage ) {
	   				JSONObject topZone = new JSONObject();
	   				topZone.put("zoneId", item.get("Id").getS());
	   				topZone.put("zoneName", item.get("ZoneName").getS());
		   			topZonesList.put(topZone.toString());
	   			}
	   			// index 0 will be the next page key
	   			if ( i == 0 ) {
	   				topZonesKeyNextZO = item.get("HI").getN();
	   				topZonesKeyNextZoneId = item.get("Id").getS();
	   			}
	   			// index postsPerPage-1 will be the previous page key if there are postsPerPage+1 records
	   			if ( topZonesMapList.size() == postsPerPage+1) {
		   			if ( i == postsPerPage-1 ) {
		   				topZonesKeyPrevZO = item.get("HI").getN();
		   				topZonesKeyPrevZoneId = item.get("Id").getS();
		   			}
	   			}
	   			// index postsPerPage will be the next page key for page refresh
	   			if ( i == postsPerPage ) {
	   				topZonesKeyRefreshZO = item.get("HI").getN();
	   				topZonesKeyRefreshZoneId = item.get("Id").getS();
	   			}
	   		}
   			topZonesInfo.put("topZonesKeyPrevZO", topZonesKeyPrevZO);
   			topZonesInfo.put("topZonesKeyPrevZoneId", topZonesKeyPrevZoneId);
   			topZonesInfo.put("topZonesKeyNextZO", topZonesKeyNextZO);
   			topZonesInfo.put("topZonesKeyNextZoneId", topZonesKeyNextZoneId);
   			topZonesInfo.put("topZonesKeyRefreshZO", topZonesKeyRefreshZO);
   			topZonesInfo.put("topZonesKeyRefreshZoneId", topZonesKeyRefreshZoneId);
   			topZonesInfo.put("topZonesList", topZonesList);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/get-topzones-prev");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

    	return topZonesInfo.toString();
    }

    /**
     * Favorite a zone from Top Zones into My Zones
     * @param zoneId Zone Id from Top Zones to favorite
     * @param zoneName Zone Name from Top Zones to favorite
     * @return First page of My Zones after adding the new favorite zone
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/favorite", method={RequestMethod.POST})
    public @ResponseBody String doFavoriteZone(String zoneId, String zoneName) {

    	String userId = null;
    	try {

	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Return error if Zone already exists ***/
			if (dao.checkIfMyZoneExists(userId, zoneId))
				return new JSONObject().put("ERROR", "Already exists in My Zones").toString();

			/*** Save zone in Zones table ***/
			dao.saveMyZone(userId, zoneId, zoneName);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/favorite");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return doGetMyZonesNext("NULL", "NULL");

    }

    /**
     * Delete a zone from My Zones
     * @param zoneId Id of the zone
     * @param myZonesKeyRefreshZO ZoneOrder for the page refresh
     * @param myZonesKeyRefreshZoneId Zone Id for the page refresh
     * @return Refreshed Page of My Zones after deleting the zone
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/delete-myzone", method={RequestMethod.POST})
    public @ResponseBody String doDeleteMyZone(String zoneId, String myZonesKeyRefreshZO, String myZonesKeyRefreshZoneId) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

    		/*** Delete zone from Zones table ***/
    		dao.deleteMyZone(userId, zoneId);
    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/delete-myzone");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
 
   		return doGetMyZonesNext(myZonesKeyRefreshZO, myZonesKeyRefreshZoneId);

    }

    /**
     * Create a zone
     * @param zoneName Zone Name
     * @param zoneDesc Zone Description
     * @param zoneWho Who can post posts (E=Everyone, A=Admins only)
     * @param force Create zone even if one with the same name exists?
     * @return JSON String with the list of Top Zones and keys for Previous and Next pages
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/create", method={RequestMethod.POST})
    public @ResponseBody String doCreateZone(String zoneName, String zoneDesc, String zoneWho, String force) {

    	// initialize zoneId to zoneName
		String zoneId = zoneName;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			if ( StringUtils.containsAny(zoneName, new String[]{"http://", "https://", "www.", ".com" } ) ) {
				return "ERROR: Backlinks not allowed in zone name.";
			}

			if ( StringUtils.containsAny(zoneDesc, new String[]{"http://", "https://", "www.", ".com" } ) ) {
				return "ERROR: Backlinks not allowed in zone description.";
			}

    		/*** Make zoneId URL-friendly ***/
			try {
	    		// convert to lower case
				zoneId = StringUtils.lowerCase(zoneId);
				// replace all non-alphanumeric characters (including spaces) with dashes
	    		zoneId = zoneId.replaceAll("[^a-z0-9]","-");
				// trim two or more subsequent dashes into one
	    		zoneId = zoneId.trim().replaceAll("-+", "-");
				// remove the first character if it's a dash
	    		zoneId = zoneId.startsWith("-") ? zoneId.substring(1) : zoneId;
				// remove the last character if it's a dash
	    		zoneId = zoneId.endsWith("-") ? zoneId.substring(0,zoneId.length()-1) : zoneId;
				// if the string is empty, generate a random id based on the system time
				if ( zoneId.length() == 0 ) zoneId = zoneId + (long) System.currentTimeMillis()/21600000;
			} catch (Exception e) {
				zoneId = zoneId + (long) System.currentTimeMillis()/21600000;
			}

			/*** Return a message if a close match already exists ***/
			if ( StringUtils.equals(force,"false") ) {
				String existingZoneName = dao.checkIfZoneExists(zoneId);
				if ( StringUtils.isNotBlank(existingZoneName) ) {
					return "EXISTS: A zone with similar name already exists - <a href='/zone/" + zoneId + "'>" + existingZoneName + "</a>. Create anyway?";
				}
			}

			/*** Save zone in Zones table ***/
			// Output zoneId will be different from the input if the zoneId already exists
    		zoneId = dao.createZone(zoneId, zoneName, zoneDesc, zoneWho, userId);

			/*** Create Zone Headshot from Zone Name ***/
			// initialize
	        int width = 1024;
	        int height = 536;
	        BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2d = bufImage.createGraphics();
	        // draw background
	        g2d.setColor(new Color(211, 237, 231));
	        g2d.fillRect(0, 0, width, height);
	        // draw zoneName
	        g2d.setColor(new Color(25, 115, 75));
	        g2d.setFont(new Font("Arial", Font.BOLD, 48));
	        int x = 0;
	        if ( StringUtils.length(zoneName) > 26 ) {
				// Create zoneName1 taking full words from zoneName up to 25 characters
		        String zoneName1 = zoneName.substring(0, StringUtils.substring(zoneName+" ",0,25).lastIndexOf(" "));
		        x = (width - g2d.getFontMetrics().stringWidth(zoneName1)) / 2;
	        	g2d.drawString(zoneName1, x, 150);
				// Create zoneName2 from the rest of the words
		        String zoneName2 = zoneName.substring(zoneName1.length()+1);
		        x = (width - g2d.getFontMetrics().stringWidth(zoneName2)) / 2;
	        	g2d.drawString(zoneName2, x, 250);
	        } else {
		        x = (width - g2d.getFontMetrics().stringWidth(zoneName)) / 2;
	        	g2d.drawString(zoneName, x, 200);
	        }
	        // draw heatbud line
	        String hb = "A Heatbud Zone";
	        g2d.setFont(new Font("Arial", Font.BOLD, 28));
	        x = (width - g2d.getFontMetrics().stringWidth(hb)) / 2;
	        g2d.drawString(hb, x, 350);
	        // dispose
	        g2d.dispose();

	        /*** Save Zone Headshot into S3 ***/
			HeatbudS3Object zoneHeadshot = new HeatbudS3Object();
			zoneHeadshot.setBucketName(config.getProperty("bucketNameZoneHeadshots"));
			zoneHeadshot.setKey(zoneId);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write( bufImage, "png", baos );
	        baos.flush();
	        zoneHeadshot.setData(baos.toByteArray());
    		zoneHeadshot.setContentType("image/jpeg");
    		zoneHeadshot.setCacheControl("max-age=36000");
    		s3.store(zoneHeadshot, false, CannedAccessControlList.PublicRead,true);
		    IOUtils.closeQuietly(baos);

			/*** Update zone headshot in Zones table ***/
    		dao.updateZoneHeadshot(zoneId, s3.getResourceUrl(zoneHeadshot.getBucketName(), zoneHeadshot.getKey()));

			/*** Ticker and Search ***/
			if ( !StringUtils.containsAny(zoneId, common.unwantedPosts) ) {
				// Insert into TopCharts table for Ticker
				dao.putCreateZoneTicker(userId, zoneId);
				// Insert into Search
				dao.putZoneSearch(zoneId);
			}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId=" + zoneId + " zoneName=" + zoneName + " force=" + force);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return zoneId;
    }

	/**
     * Updates Zone Name.
     * @param zoneId Zone Id
     * @param zoneName Zone Name
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/update-zone-name", method={RequestMethod.POST})
    public @ResponseBody String doUpdateZoneName (String zoneId, String zoneName) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Validate Zone Name ***/
			String zoneNameDecoded = StringUtils.trim(URLDecoder.decode(zoneName,"UTF-8"));
			if ( StringUtils.isBlank(zoneNameDecoded) ) {
				return "Zone Name cannot be Blank.";
			}

			/*** Save Zone Name in the database ***/
			dao.updateZoneName(zoneId, zoneNameDecoded);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/update-zone-name");
	        logger.log(Level.SEVERE,"UserId: " + userId + " Zone Name: " + zoneName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Zone Name could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates Zone Headshot.
     * @param zoneId Zone Id
     * @param zoneHeadshot Zone Headshot
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/update-zone-headshot", method={RequestMethod.POST})
    public @ResponseBody String doUpdateZoneHeadshot (String zoneId, String zoneHeadshot) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Validate Zone Headshot ***/
			String zoneHeadshotDecoded = URLDecoder.decode(zoneHeadshot,"UTF-8");
			if ( StringUtils.isBlank(zoneHeadshotDecoded) ) {
				return "Zone Headshot cannot be Blank.";
			}

    		/*** Create a Social Media version of Zone Headshot ***/
			HeatbudS3Object zoneHeadshotSocial = new HeatbudS3Object();
			zoneHeadshotSocial.setBucketName(config.getProperty("bucketNameImages"));
			zoneHeadshotSocial.setKey(StringUtils.substring(zoneHeadshotDecoded,49));
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			URL u = new URL(StringUtils.replace(zoneHeadshotDecoded,"/social/", "/images/"));
			InputStream is1 = u.openStream();
			Thumbnails.of(new ByteArrayInputStream(IOUtils.toByteArray(is1))).size(1024,536).outputQuality(1).outputFormat("jpg").toOutputStream(os);
			zoneHeadshotSocial.setData(os.toByteArray());
			zoneHeadshotSocial.setContentType("image/jpeg");
			zoneHeadshotSocial.setCacheControl("max-age=36000");
		    s3.store(zoneHeadshotSocial, false, CannedAccessControlList.PublicRead, false);
		    IOUtils.closeQuietly(is1);
		    IOUtils.closeQuietly(os);

			/*** Save Zone Headshot in the database ***/
			dao.updateZoneHeadshot(zoneId, zoneHeadshotDecoded);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/update-zone-headshot");
	        logger.log(Level.SEVERE,"UserId: " + userId + " zoneHeadshot: " + zoneHeadshot);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Zone Headshot could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates Zone Description.
     * @param zoneId Zone Id
     * @param zoneDesc Zone Description
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/update-zone-desc", method={RequestMethod.POST})
    public @ResponseBody String doUpdateZoneDesc (String zoneId, String zoneDesc) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Validate Zone Description ***/
			String zoneDescDecoded = URLDecoder.decode(zoneDesc,"UTF-8");
			if ( StringUtils.isBlank(zoneDescDecoded) ) {
				return "Zone Description cannot be Blank.";
			}

			/*** Save Zone Description in the database ***/
			dao.updateZoneDesc(zoneId, zoneDescDecoded);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/update-zone-desc");
	        logger.log(Level.SEVERE,"UserId: " + userId + " Zone Description: " + zoneDesc);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Zone Description could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates Zone Who.
     * @param zoneId Zone Id
     * @param zoneWho Zone Who
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/update-zone-who", method={RequestMethod.POST})
    public @ResponseBody String doUpdateZoneWho (String zoneId, String zoneWho) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Validate Zone Who ***/
			String zoneWhoDecoded = URLDecoder.decode(zoneWho,"UTF-8");
			if ( StringUtils.isBlank(zoneWhoDecoded) ) {
				return "Zone Who cannot be Blank.";
			}

			/*** Save Zone Who in the database ***/
			dao.updateZoneWho(zoneId, zoneWhoDecoded);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/zone/update-zone-who");
	        logger.log(Level.SEVERE,"UserId: " + userId + " Zone Who: " + zoneWho);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Zone Who could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Makes the user an admin of the zone.
     * @param zoneId Zone Id
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/become-admin", method={RequestMethod.POST})
    public @ResponseBody String doBecomeAdmin (String zoneId) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Add userId to Admins of the Zones table ***/
			dao.addZoneAdmin(zoneId, userId);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Couldn't make you the admin. Please reload the page and try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Send request to the current admins.
     * @param zoneId Zone Id
     * @param zoneName Zone Name
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/request-admin", method={RequestMethod.POST})
    public @ResponseBody String doRequestAdmin (String zoneId, String zoneName) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Add userId to AdminRequests of the Zones table ***/
			dao.addZoneAdminRequest(zoneId, userId);

			/*** Send Email to the current Admins ***/
			Map<String, AttributeValue> zoneStats = dao.getZoneStats(zoneId);
			for ( String admin : zoneStats.get("Admins").getSS() ) {
				Entity blogger = dao.getEntity(admin);
				ses.sendAdminRequestEmail(blogger.getEntityEmail(), blogger.getEntityName(),
					zoneId, zoneName, userId, user.getFirstName() + " " + user.getLastName() );
    		}

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Couldn't send request. Please reload the page and try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Approve admin.
     * @param zoneId zoneId
     * @param adminId userId of the Admin
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/approve-admin", method={RequestMethod.POST})
    public @ResponseBody String doApproveAdmin (String zoneId, String zoneName, String adminId) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Add adminId to the Admins of the Zones table and delete from AdminRequests ***/
			dao.approveZoneAdmin(zoneId, adminId);

			/*** Send Email to the Admin Approved ***/
			Entity blogger = dao.getEntity(adminId);
			ses.sendAdminApprovedEmail(blogger.getEntityEmail(), blogger.getEntityName(),
				zoneId, zoneName, userId, user.getFirstName() + " " + user.getLastName() );

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Couldn't send request. Please reload the page and try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Remove admin.
     * @param zoneId zoneId
     * @param adminId userId of the Admin
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/zone/remove-admin", method={RequestMethod.POST})
    public @ResponseBody String doRemoveAdmin (String zoneId, String zoneName, String adminId) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Remove adminId from the Admins of the Zones table ***/
			dao.removeZoneAdmin(zoneId, adminId);

			/*** Send Email to the Admin Removed ***/
			Entity blogger = dao.getEntity(adminId);
			ses.sendAdminRemovedEmail(blogger.getEntityEmail(), blogger.getEntityName(),
				zoneId, zoneName, userId, user.getFirstName() + " " + user.getLastName() );

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Couldn't send request. Please reload the page and try again.";
		}
   		return "SUCCESS";
    }

    /**************************************************************************
     *************************  POST MODULE  **********************************
     **************************************************************************/

	/**
     * Saves post data into DynamoDB and S3
     * @param request formdata with post metadata and content
     	* postId PostId (NEW if saving for the first time)
     	* postTitle
     	* postSummary
     	* zoneId
     	* pageId
     	* publishFlag Y or N indicating whether to publish post or not
     	* postContent
     * @return Generated postId if NEW
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/save", method={RequestMethod.POST})
    public @ResponseBody String doSavePost (MultipartHttpServletRequest request) {

    	JSONObject postInfo = new JSONObject();
    	String postIdInput = request.getParameter("postId");
    	String postId = null; // we will generate postId if the above input is NEW
    	String postHeadshot = request.getParameter("postHeadshot");
		String postTitle = request.getParameter("postTitle");
		String postSummary = request.getParameter("postSummary");
		String pageId = request.getParameter("pageId").trim();
		String postContent = request.getParameter("postContent");
		String publishFlag = request.getParameter("publishFlag");
		String priorPublishFlag = request.getParameter("priorPublishFlag");
    	try {
    		/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Validate Backlinks and Blocked IP addresses ***/
			if ( !StringUtils.equals(userId,"yopi") ) {
				if ( StringUtils.equals(publishFlag,"Y") || StringUtils.equals(priorPublishFlag,"Y") ) {
					if ( StringUtils.isBlank(pageId) ) {
						/*** Banklinks are allowed only for active pages. ***/
						if ( StringUtils.containsAny(postContent, new String[]{"http://", "https://", "www.", ".com" } ) ) {
			       	        logger.log(Level.INFO,"Backlinks in post content: " + postContent );
			       	        logger.log(Level.INFO,"PostId: " + postIdInput );
			    			postInfo.put("ERROR", "Backlinks are allowed only when the blog post is attached to an active page.<br/><br/>Visit <a href=\"/do/help/main/pricing\">Heatbud Pricing</a> for details.");
			    	    	return postInfo.toString();
						}
						if ( StringUtils.containsAny(postTitle, new String[]{"http://", "https://", "www.", ".com" } ) ) {
			       	        logger.log(Level.INFO,"Backlinks in post title: " + postTitle );
			       	        logger.log(Level.INFO,"PostId: " + postIdInput );
			    			postInfo.put("ERROR", "Backlinks are allowed only when the blog post is attached to an active page.<br/><br/>Visit <a href=\"/do/help/main/pricing\">Heatbud Pricing</a> for details.");
			    	    	return postInfo.toString();
						}
						if ( StringUtils.containsAny(postSummary, new String[]{"http://", "https://", "www.", ".com" } ) ) {
			       	        logger.log(Level.INFO,"Backlinks in post summary: " + postSummary );
			       	        logger.log(Level.INFO,"PostId: " + postIdInput );
			    			postInfo.put("ERROR", "Backlinks are allowed only when the blog post is attached to an active page.<br/><br/>Visit <a href=\"/do/help/main/pricing\">Heatbud Pricing</a> for details.");
			    	    	return postInfo.toString();
						}
					} else {
						/*** Can't publish blog posts for an inactive page. ***/
						if ( !dao.isPageActive(pageId) ) {
			    			postInfo.put("ERROR", "You cannot write blog posts for an inactive page.<br/><br/>Visit <a href=\\\"/do/help/main/pricing\\\">Heatbud Pricing</a> for details.");
			    	    	return postInfo.toString();
						}
					}
				}
				/*** Can't publish from a blocked IP address ***/
				String ipAddress = request.getHeader("X-FORWARDED-FOR");
				if ( StringUtils.isBlank(ipAddress) ) {
					ipAddress = request.getRemoteAddr();
				}
				if ( StringUtils.indexOfAny(ipAddress, common.blockedIPs) > 0 ) {
	    	        logger.log(Level.SEVERE,"Blocked IP Address attempting to post: " + postTitle );
	    	        logger.log(Level.SEVERE,"UserId: " + userId);
	    			postInfo.put("ERROR", "You're blocked from creating blog posts.");
	    	    	return postInfo.toString();
	    		}
			}

			/*** Generate postId if NEW ***/
    		if ( StringUtils.equals(postIdInput,"NEW") ) {
    			postId = dao.generatePostId(userId, request.getParameter("zoneId"), postTitle);
    			// return if there is an error
    			if ( StringUtils.equals(postId, "ERROR") ) {
	 				postInfo.put("ERROR", "Post could not be saved. Please try again.");
	 		   		return postInfo.toString();
    			}
    			// return if the postId is still new (which shouldn't happen)
    			if ( StringUtils.equals(postId, "NEW") ) {
	 		   		return postInfo.toString();
    			}
	    		postInfo.put("postId", postId);
    		} else {
    			postId = postIdInput.toString();
    			/*** Validate if logged-in user is the author of the post ***/
    			String bloggerId = dao.getPostBloggerId(postId);
    			if ( !StringUtils.equals(userId, bloggerId) ) {
	 				postInfo.put("ERROR", "You're not the Author of this post.");
    				return postInfo.toString();
	    		}
    		}

	    	/*** Read UTC time from System ***/
    		long postUpdateDate = System.currentTimeMillis();

    		/*** PostHeadshot ***/
    		if ( StringUtils.equals(request.getParameter("headshotChanged"), "Y") || StringUtils.equals(postIdInput,"NEW") ) {

    			if ( StringUtils.endsWith(postHeadshot,"/resources/images/def-post-image.png") ) {

    				try {
	            		/*** If no image has been uploaded by the user ***/
	            		//  Create image from postTitle and store it in bucketNamePostHeadshots
	            		//  Use this image for topCharts as well as Social
	    				// initialize
	    		        int width = 1024;
	    		        int height = 536;
	    		        BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    		        Graphics2D g2d = bufImage.createGraphics();
	    		        // draw background
	    		        g2d.setColor(new Color(211, 237, 231));
	    		        g2d.fillRect(0, 0, width, height);
	    		        // draw postTitle
	    		        g2d.setColor(new Color(25, 115, 75));
	    		        g2d.setFont(new Font("Arial", Font.BOLD, 48));
	    		        int x = 0;
	    		        if ( StringUtils.length(postTitle) > 50 ) {
	    					// Create postTitle1 taking full words from postTitle up to 25 characters
	    			        String postTitle1 = postTitle.substring(0, StringUtils.substring(postTitle+" ",0,25).lastIndexOf(" "));
	    			        x = (width - g2d.getFontMetrics().stringWidth(postTitle1)) / 2;
	    		        	g2d.drawString(postTitle1, x, 150);
	    					// Create postTitle2 taking full words from postTitle from 26th to 45th characters
	    			        String postTitle2 = postTitle.substring(StringUtils.substring(postTitle+" ",0,25).lastIndexOf(" ")+1, StringUtils.substring(postTitle+" ",0,45).lastIndexOf(" "));
	    			        x = (width - g2d.getFontMetrics().stringWidth(postTitle2)) / 2;
	    		        	g2d.drawString(postTitle2, x, 250);
	    					// Create postTitle3 from the rest of the words
	    			        String postTitle3 = postTitle.substring(StringUtils.substring(postTitle+" ",0,45).lastIndexOf(" ")+1);
	    			        x = (width - g2d.getFontMetrics().stringWidth(postTitle3)) / 2;
	    		        	g2d.drawString(postTitle3, x, 350);
	    		        } else if ( StringUtils.length(postTitle) > 26 ) {
	    					// Create postTitle1 taking full words from postTitle up to 25 characters
	    			        String postTitle1 = postTitle.substring(0, StringUtils.substring(postTitle+" ",0,25).lastIndexOf(" "));
	    			        x = (width - g2d.getFontMetrics().stringWidth(postTitle1)) / 2;
	    		        	g2d.drawString(postTitle1, x, 150);
	    					// Create postTitle2 from the rest of the words
	    			        String postTitle2 = postTitle.substring(StringUtils.substring(postTitle+" ",0,25).lastIndexOf(" ")+1);
	    			        x = (width - g2d.getFontMetrics().stringWidth(postTitle2)) / 2;
	    		        	g2d.drawString(postTitle2, x, 250);
	    		        } else {
	    			        x = (width - g2d.getFontMetrics().stringWidth(postTitle)) / 2;
	    		        	g2d.drawString(postTitle, x, 200);
	    		        }
	    		        // draw heatbud line
	    		        String hb = "A Heatbud Post";
	    		        g2d.setFont(new Font("Arial", Font.BOLD, 28));
	    		        x = (width - g2d.getFontMetrics().stringWidth(hb)) / 2;
	    		        g2d.drawString(hb, x, 450);
	    		        // dispose
	    		        g2d.dispose();

	    		        /*** Save Post Headshot into S3 ***/
	    				HeatbudS3Object postHeadshotHBO = new HeatbudS3Object();
	    				postHeadshotHBO.setBucketName(config.getProperty("bucketNamePostHeadshots"));
	    				postHeadshotHBO.setKey(postId);
	    		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		        ImageIO.write( bufImage, "png", baos );
	    		        baos.flush();
	    		        postHeadshotHBO.setData(baos.toByteArray());
	    	    		postHeadshotHBO.setContentType("image/jpeg");
	    	    		postHeadshotHBO.setCacheControl("max-age=36000");
	    	    		s3.store(postHeadshotHBO, false, CannedAccessControlList.PublicRead,true);
	    			    IOUtils.closeQuietly(baos);

    				} catch (Exception e) {
   						// continue saving the post content even if there are errors creating the headshot
   					}

    	    		postHeadshot = "https://postheadshots.s3.amazonaws.com/"+postId;
    	        	postInfo.put("postHeadshot", postHeadshot);

    			} else if ( StringUtils.startsWith(postHeadshot,"https://postheadshots.s3.amazonaws.com/") ) {

    				// do nothing; we already have a headshot created

    			} else {

    				try {

    					/*** If the user uploads an image ***/
	               		//  Image and the thumbnail should have already been existing in S3
	               		//  Create Social version of the image
	    				HeatbudS3Object imageThumb = new HeatbudS3Object();
						imageThumb.setBucketName(config.getProperty("bucketNameImages"));
				        imageThumb.setKey(StringUtils.substring(StringUtils.replace(postHeadshot,"/thumbs/","/social/"),49));
				        ByteArrayOutputStream os = new ByteArrayOutputStream();
				        URL u = new URL(StringUtils.replace(postHeadshot,"/thumbs/","/images/"));
				        InputStream is1 = u.openStream();
				        Thumbnails.of(new ByteArrayInputStream(IOUtils.toByteArray(is1))).size(1024,536).outputQuality(1).outputFormat("jpg").toOutputStream(os);
			    		imageThumb.setData(os.toByteArray());
			    		imageThumb.setContentType("image/jpeg");
			    		imageThumb.setCacheControl("max-age=36000");
			    		s3.store(imageThumb, false, CannedAccessControlList.PublicRead, false);
					    IOUtils.closeQuietly(is1);
					    IOUtils.closeQuietly(os);

					} catch (Exception e) {
   						// continue saving the post content even if there are errors creating the headshot
					}

    			}

    		}

	    	/*** Save post metadata into the Posts table ***/
    		if ( StringUtils.equals(postIdInput,"NEW") ) {
	    		Post post = new Post();
	    		post.setPostId(postId);
	    		post.setPostTitle(postTitle);
	    		post.setTags(generateTags(postTitle)); // generate tags from title
	    		post.setPostHeadshot(postHeadshot);
	    		post.setPostSummary(postSummary);
	    		post.setZoneId(request.getParameter("zoneId"));
	    		post.setZoneName(dao.getZoneName(request.getParameter("zoneId")));
	    		if ( StringUtils.isNotBlank(pageId) ) {
		    		post.setPageId(pageId);
		    		post.setPageName(dao.getEntityName(pageId));
    		    } else {
   		    		post.setPageId(null);
   		    		post.setPageName(null);
    		    }
	    		post.setUpdateDate(postUpdateDate);
        		post.setBloggerId(userId);
           		post.setBloggerName(dao.getEntityName(userId));
	    		post.setPublishFlag(publishFlag);
	    		post.setUpVotes(0);
	    		post.setDownVotes(0);
	    		post.setComments(0);
	    		post.setViews(0);
	    		post.setHi(0);
	        	dao.putPost(post);
    		} else {
    			dao.updatePost(postId,
   					postTitle,
   					generateTags(postTitle), // generate tags from title
   					postHeadshot,
   					postSummary,
   					publishFlag,
   					postUpdateDate
    			);
    		}

	    	/*** Create/ Update record in PostsByEntity table ***/
    		// Save post data into PostsByEntity table in two situations:
    		//		if postId = NEW i.e., saving for the first time
    		//		if publishFlag = Y i.e., published for the first time
    		// Subsequent saves don't change PostsByEntity table
    		if ( StringUtils.equals(postIdInput,"NEW") || StringUtils.equals(publishFlag,"Y") ) {
    			dao.savePostsByEntity(userId, publishFlag, postUpdateDate, postId);
	    		if ( StringUtils.isNotBlank(pageId) ) {
	    			dao.savePostsByEntity(pageId, publishFlag, postUpdateDate, postId);
	    		}
    		}

    		/*** If the post is being published ***/
    		if ( StringUtils.equals(publishFlag,"Y") ) {
        		// Make blogger the follower of comments
				String flag = dao.getNotificationFlag(user.getUsername(), "followWhenPublished");
				if ( StringUtils.equals(flag, "Y") ) {
					dao.followComments(postId, user.getUsername());
				}
	   			/*** Register publishPost in SQS to be processed by cron job ***/
				JSONObject msgBody = new JSONObject();
				msgBody.put("category", "publishPost");
				msgBody.put("postId", postId);
				msgBody.put("bloggerId", userId);
				msgBody.put("zoneId", request.getParameter("zoneId"));
				msgBody.put("pageId", pageId + " ");
				msgBody.put("postUpdateDate", postUpdateDate);
				sqs.sendMessage (
					new SendMessageRequest()
						.withQueueUrl( config.getProperty("processQueueSQS") )
						.withMessageBody(msgBody.toString())
				);
    		}

    		/*** Save post content into S3 ***/
    		HeatbudS3Object hso = new HeatbudS3Object();
    		hso.setBucketName(config.getProperty("bucketNamePosts"));
    		hso.setKey(postId);
    		hso.setData(postContent.getBytes());
    		hso.setContentType("text/html; charset=UTF-8");
    		hso.setCacheControl("max-age=36000");
    		s3.store(hso, false, CannedAccessControlList.PublicRead, true);

        	postInfo.put("SUCCESS", "Post has been saved successfully.");

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " PostTitle: " + postTitle);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        try {
	        	postInfo.put("ERROR", "Post could not be saved. Please try again.");
	        } catch (Exception e1) {
	        	// do nothing
	        }
		}

   		return postInfo.toString();

    }

    /**
     * Deletes a given post i.e., Sets publishedFlag to D in Posts and PostsByEntity tables.
     * @param postId PostId
     * @param zoneId ZoneId
     * @param pageId PageId
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/delete", method={RequestMethod.POST})
    public @ResponseBody String doDeletePost (String postId, String zoneId, String pageId) {

    	JSONObject postInfo = new JSONObject();
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Get EntityId from Posts table ***/
    		/*** For Security, never accept bloggerId from HTML or JavaScript ***/
			String bloggerId = dao.getPostBloggerId(postId);

			if ( !StringUtils.equals(userId, bloggerId) ) {
 				postInfo.put("ERROR", "You don't have permission to delete the post.");
 		   		return postInfo.toString();
			}

			long updateDate = System.currentTimeMillis();
			String publishFlag = dao.getPostPublishFlag(postId);

	    	/*** Set PublishFlag to D in Posts table ***/
   			dao.setPostDeleted(postId, updateDate);

			/*** Set PublishFlag to D in PostsByEntity table for the blogger ***/
   			dao.savePostsByEntity(bloggerId, "D", updateDate, postId);

			/*** Set PublishFlag to D in PostsByEntity table for the page ***/
   			if (StringUtils.isNotBlank(pageId)) {
   	   			dao.savePostsByEntity(pageId, "D", updateDate, postId);
   			}

   			/*** Register deletePost in SQS to be processed by cron job ***/
			JSONObject msgBody = new JSONObject();
			msgBody.put("category", "deletePost");
			msgBody.put("bloggerId", bloggerId);
			msgBody.put("zoneId", zoneId);
			msgBody.put("pageId", pageId + " ");
			msgBody.put("postId", postId);
			msgBody.put("publishFlag", publishFlag);
			sqs.sendMessage (
				new SendMessageRequest()
					.withQueueUrl( config.getProperty("processQueueSQS") )
					.withMessageBody(msgBody.toString())
			);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
 			try { postInfo.put("ERROR", "Post could not be deleted. Please try again."); } catch (Exception e1) {}
		}

   		return postInfo.toString();
    }

    /**
     * Purges a given post i.e., Completely Deletes from all dynamoDB tables and from S3.
     * @param postId PostId
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/action/purge", method={RequestMethod.POST})
    public @ResponseBody String doPurgePost (String postId) {

    	JSONObject postInfo = new JSONObject();
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			if ( !StringUtils.equals(userId, "kakatur") ) {
 				postInfo.put("MESSAGE", "You don't have permission to purge the post.");
 		   		return postInfo.toString();				
			}

			JSONObject msgBody = new JSONObject();
			msgBody.put("attributeName", "PostId");
			msgBody.put("attributeDataType", "S");
			msgBody.put("attributeValue", postId);
			msgBody.put("action", "RUN");
			msgBody.put("addlMessage", "");

			sqs.sendMessage(new SendMessageRequest()
				.withQueueUrl(config.getProperty("deleteDataQueueSQS"))
				.withMessageBody(msgBody.toString())
			);
			postInfo.put("MESSAGE", "Purged Successfully.");

 		} catch (Exception e) {
 			try {
 				postInfo.put("MESSAGE", "Post could not be purged. Please try again.");
 			} catch (Exception e1) {
 				// do nothing
 			}
	        logger.log(Level.SEVERE,"PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return postInfo.toString();
    }

	/**
     * Send request to feature the post at www.facebook.com/heatbud.
     * @param postId Post Id
     * @return Errors, if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/request-fb", method={RequestMethod.POST})
    public @ResponseBody String doRequestFB (String postId, HttpServletRequest request) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Update Posts table ***/
			dao.updatePostRequestFB(postId, "Y");

			/*** Get Client IP Address ***/
			String ipAddress = null;
			ipAddress = request.getHeader("X-FORWARDED-FOR");
			if ( StringUtils.isBlank(ipAddress) ) {
				ipAddress = request.getRemoteAddr();
			}

			/*** Send Email to Customer Service ***/
			ses.sendPostRequestFBEmail(postId, dao.getPostTitle(postId), ipAddress);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Couldn't send request. Please try again.";
		}
   		return "SUCCESS";
    }

    /**************************************************************************
     *************************  VOTE MODULE  **********************************
     **************************************************************************/

    /**
     * Registers vote on a post
     * @param postId postId
     * @param zoneId Id of the zone
     * @param zoneName Name of the zone
     * @param pageId pageId
     * @param newVote +1 or -1 representing new vote by user
     * @return ERROR message if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/vote", method={RequestMethod.POST})
    public @ResponseBody String doVotePost(String postId, String zoneId, String zoneName, String pageId, int newVote) {

    	JSONObject postInfo = new JSONObject();
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			// Check current vote of the user on the post
			int currentVote = dao.getVote(userId, postId);

			if (currentVote == newVote) {
	   			postInfo.put("ERROR", "You can only vote once per each post.");
	   	   		return postInfo.toString();
			} else {
				dao.putVote(userId, postId, newVote);
			}

			/*** Get EntityId from Posts table ***/
    		/*** For Security, never accept bloggerId from HTML or JavaScript ***/
			String bloggerId = dao.getPostBloggerId(postId);

   			/*** Register processVote in SQS to be processed by cron job ***/
			JSONObject msgBody = new JSONObject();
			msgBody.put("category", "processVote");
			msgBody.put("bloggerId", bloggerId);
			msgBody.put("postId", postId);
			msgBody.put("zoneId", zoneId);
			msgBody.put("zoneName", zoneName);
			msgBody.put("pageId", pageId + " ");
			msgBody.put("voterId", userId);
			msgBody.put("currentVote", currentVote);
			msgBody.put("newVote", newVote);
			sqs.sendMessage (
				new SendMessageRequest()
					.withQueueUrl( config.getProperty("processQueueSQS") )
					.withMessageBody(msgBody.toString())
			);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/vote");
	        logger.log(Level.SEVERE,"postId: " + postId + " newVote: " + newVote);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return postInfo.toString();

    }

    /**
     * Query related posts.
     * @param postId postId
     * @param postTitle postTitle
     * @param rpcPeriod Related Posts Calculated Period
     * @return List of Related Posts
     */
    @RequestMapping(value="/action/get-related-posts", method={RequestMethod.POST})
    public @ResponseBody String doGetRelatedPosts (String postId, String postTitle, long rpcPeriod) {

    	JSONObject relatedPostsInfo = new JSONObject();
    	JSONArray relatedPostsList = new JSONArray();
    	try {
    		// get related posts from database
	    	List<Map<String,AttributeValue>> relatedPostsMapList = dao.getRelatedPosts(postId, postTitle, rpcPeriod);
	   		for (int i = 0; i < relatedPostsMapList.size(); i++) {
	   			Map<String,AttributeValue> item = relatedPostsMapList.get(i);
	   			Post relatedPost = new Post();
	   			relatedPost.setPostId(item.get("RelatedPostId").getS());
	   			relatedPost.setPostTitle(item.get("PostTitle").getS());
	   			relatedPost.setPostHeadshot(item.get("PostHeadshot").getS());
	   			relatedPost.setPostSummary(item.get("PostSummary").getS());
	   			relatedPostsList.put(new JSONObject(relatedPost));
	   		}
	   		relatedPostsInfo.put("relatedPostsList", relatedPostsList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId="+postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return relatedPostsInfo.toString();
    }

    /**************************************************************************
     ***********************  COMMENT MODULE  *********************************
     **************************************************************************/

    /**
     * Query next page of Comments.
     * @param postId postId
     * @param commentsKeyNext CommentDate for the key (Literal string of 'NULL' if first page)
     * @return List of Comments and new keys for previous and next pages
     */
    @RequestMapping(value="/action/get-comments-next", method={RequestMethod.POST})
    public @ResponseBody String doGetCommentsNext(String postId, String commentsKeyNext) {

    	JSONObject commentsInfo = new JSONObject();
    	JSONArray commentsList = new JSONArray();
    	try {
    		// get comments from database
    		List<Map<String,AttributeValue>> commentsMapList = dao.getComments(postId, commentsKeyNext, false);
        	boolean isFirstPage = false;
    		if (StringUtils.equals(commentsKeyNext, "NULL")) isFirstPage = true;
	   		String commentsKeyPrev = "NULL"; // default
	   		commentsKeyNext = "NULL"; // reset
	   		/*** Process the Comments map retrieved from the database ***/
	   		for (int i = 0; i < commentsMapList.size(); i++) {
	   			Map<String,AttributeValue> item = commentsMapList.get(i);
	   			// display index 0 to 4 to the user
	   			// index 5 is needed to decide whether to display the next page link or not
	   			if ( i < 5 ) {
			   		Comment comment = new Comment();
			   		comment.setPostId(postId);
			   		comment.setCommentDate(item.get("CommentDate").getN());
			   		comment.setCommentText(item.get("CommentText").getS());
			   		comment.setCommenterId(item.get("CommenterId").getS());
		   			comment.setCommenterName(dao.getEntityName(comment.getCommenterId()));
		   			commentsList.put(new JSONObject(comment));
	   			}
	   			// index 0 will be the previous page key (if not the first page)
	   			if ( i == 0 ) {
	   				if ( !isFirstPage ) commentsKeyPrev = item.get("CommentDate").getN();
	   			}
	   			// index 4 will be the next page key if there are 6 records
				if ( i == 4 ) {
		   			if ( commentsMapList.size() == 6 ) {
		   				commentsKeyNext = item.get("CommentDate").getN();
					}
	   			}
	   		}
	   		commentsInfo.put("commentsKeyPrev", commentsKeyPrev);
	   		commentsInfo.put("commentsKeyNext", commentsKeyNext);
	   		commentsInfo.put("commentsList", commentsList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/get-comments-next");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return commentsInfo.toString();
    }

    /**
     * Query previous page of Comments.
     * @param postId postId
     * @param commentsKeyPrev CommentDate for the key to fetch previous page of Comments
     * @return List of Comments and new keys for previous and next pages
     */
    @RequestMapping(value="/action/get-comments-prev", method={RequestMethod.POST})
    public @ResponseBody String doGetCommentsPrev(String postId, String commentsKeyPrev) {

    	JSONObject commentsInfo = new JSONObject();
    	JSONArray commentsList = new JSONArray();
    	try {
			// get comments from database
	    	List<Map<String,AttributeValue>> commentsMapList = dao.getComments(postId, commentsKeyPrev, true);
	    	// defaults
	   		commentsKeyPrev = "NULL";
	   		String commentsKeyNext = "NULL";
	   		String commentsKeyRefresh = "NULL";
	   		/*** Process the Comments map retrieved from the database ***/
	   		for (int i = commentsMapList.size()-1; i >= 0; i--) {
	   			Map<String,AttributeValue> item = commentsMapList.get(i);
	   			// display index 0 to 4 to the user
	   			// index 5 is needed to decide whether to display the previous page link or not
	   			if ( i < 5 ) {
	   				Comment comment = new Comment();
		   			comment.setPostId(postId);
		   			comment.setCommentDate(item.get("CommentDate").getN());
		   			comment.setCommentText(item.get("CommentText").getS());
		   			comment.setCommenterId(item.get("CommenterId").getS());
		   			comment.setCommenterName(dao.getEntityName(comment.getCommenterId()));
		   			commentsList.put(new JSONObject(comment));
	   			}
	   			// index 0 will be the next page key
	   			if ( i == 0 ) {
	   				commentsKeyNext = item.get("CommentDate").getN();
	   			}
	   			// index 4 will be the previous page key if there are 6 records
	   			if ( i == 4 ) {
		   			if ( commentsMapList.size() == 6) {
		   				commentsKeyPrev = item.get("CommentDate").getN();
		   			}
	   			}
	   			// index 5 will be the next page key for page refresh
	   			if ( i == 5 ) {
	   				commentsKeyRefresh = item.get("CommentDate").getN();
	   			}
	   		}
	   		commentsInfo.put("commentsKeyPrev", commentsKeyPrev);
	   		commentsInfo.put("commentsKeyNext", commentsKeyNext);
	   		commentsInfo.put("commentsKeyRefresh", commentsKeyRefresh);
	   		commentsInfo.put("commentsList", commentsList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/get-comments-prev");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return commentsInfo.toString();
    }

    /**
     * Posts a comment on the blog post
     * @param postId postId
     * @param postTitle postTitle
     * @param publishFlag Whether the post has been published or not
     * @param zoneId Id of the zone
     * @param zoneName Name of the zone
     * @param pageId Id of the page
     * @param parentCommentDate (0 if no parent)
     * @param origCommentText Text of the original comment, if reply
     * @param commentText Text of the comment
     * @param thankedFlag '1' for not-thanked '2' for thanked
     * @email Email Address (if not registered or not signed-in)
     * @passwd Password (if not registered or not signed-in)
     * @return ERROR message if any
     */
    @RequestMapping(value="/action/post-comment", method={RequestMethod.POST})
    public @ResponseBody String doPostComment (String postId, String postTitle, String publishFlag,
    		String zoneId, String zoneName, String pageId,
    		long parentCommentDate, String origCommentText, String commentText, String thankedFlag,
    		String email, String passwd, HttpServletRequest request) {

    	JSONObject JSONData = new JSONObject();
    	try {

    		// validate email address
			String valMessage = common.validateEmailAddress(email);
    		if ( valMessage != "SUCCESS" ) {
    			JSONData.put("ERROR", valMessage);
    	    	return JSONData.toString();
    		}

    		// validate comment text
			if ( StringUtils.containsAny(commentText, new String[]{"http://", "https://", "www.", ".com" } ) ) {
       	        logger.log(Level.INFO,"Backlinks in comment text: " + commentText );
    			JSONData.put("ERROR", "Backlinks are not allowed in comments.");
    	    	return JSONData.toString();
			}

    		// handle different cases of authentication
    		String userId = null;
    		String commenterEmail = null;
    		boolean unconfirmedUser = false;
    		try {
    			// case 1 : user has logged-in
				User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				userId = user.getUserId();
				commenterEmail = user.getUsername();
    		} catch (Exception e) {
       			try {
       				// case 2 : user has not logged-in, but we are able to authenticate now
       				// authenticate
           			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, passwd);
           	        Authentication authentication = authenticationManager.authenticate(token);
           	     	// inject security context into the session
           	    	HttpSession session = request.getSession(true);
           	        SecurityContext securityContext = SecurityContextHolder.getContext();
           	     	securityContext.setAuthentication(authentication);
           	        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
           	        // retrieve user details
    				User user = (User) authentication.getPrincipal();
    				userId = user.getUserId();
    				commenterEmail = user.getUsername();
           	    } catch (Exception e1) {
           	    	// case 3 : we can't authenticate the user; let's check if registered or not
          			User userDB = dao.getUser(email);
            	    if ( userDB == null ) {
                   		// case 3a : email doesn't exist in our database
            	    	// Create user object
               			User user = new User();
               			user.setUsername(email);
               			user.setFirstName(StringUtils.substring(user.getUsername(),0,StringUtils.indexOf(user.getUsername(),"@")));
               			user.setLastName(user.getFirstName());
               			if ( StringUtils.isNotBlank(passwd) ) {
               				user.setPassword(passwd);
               			} else {
               				user.setPassword("On3C0mp13xPa33w0rd");
               			}
                    	// Create user
                    	String signupMessage = ua.doSignupWhenCommenting(user, request);
                    	if ( !StringUtils.equals(signupMessage, "SUCCESS") ) {
	            			JSONData.put("ERROR", signupMessage);
	            	    	return JSONData.toString();
                    	}
                    	unconfirmedUser = true;
        				userId = user.getUserId();
                    	// Insert into UnconfirmedComments table
                    	String unconfirmedCommentId = UUID.randomUUID().toString();
        				dao.putUnconfirmedComments(unconfirmedCommentId, zoneId, zoneName, postId, pageId,
        					publishFlag, parentCommentDate, origCommentText, commentText, thankedFlag,
       			    		user.getUsername(), user.getUserId()
       			    	);
        				// Send email
        				ses.sendConfirmCommentEmail(user.getFirstName(), user.getUsername(), user.getSalt(), user.getIPAddress(), unconfirmedCommentId, postId, postTitle, commentText);
               		} else if ( StringUtils.equals(userDB.getRole(),"ROLE_NONE") ) {
              			// case 3b : registered but unconfirmed user
                    	// Insert into UnconfirmedComments table
                    	unconfirmedUser = true;
                    	String unconfirmedCommentId = UUID.randomUUID().toString();
        				dao.putUnconfirmedComments(unconfirmedCommentId, zoneId, zoneName, postId, pageId,
        					publishFlag, parentCommentDate, origCommentText, commentText, thankedFlag,
       			    		userDB.getUsername(), userDB.getUserId()
       			    	);
        				// Send email
        				ses.sendConfirmCommentEmail(userDB.getFirstName(), userDB.getUsername(), userDB.getSalt(), userDB.getIPAddress(), unconfirmedCommentId, postId, postTitle, commentText);
               		} else {
              			// case 3c : registered and confirmed user
            			JSONData.put("ERROR", "Incorrect password.");
            	    	return JSONData.toString();
               		}
           	    }
    		}

    		/*** Comment Tracker Time ***/
    		long UTC = System.currentTimeMillis();
    		long commentTrackerTime = 0;
    		try {
    			/*** Validate that the comment tracker time is within two hours of the current time ***/
    			commentTrackerTime = Long.parseLong(dao.getAttribute(userId, "commentTrackerTime"));
        		if ( commentTrackerTime > UTC+2*3600*1000 ) {
        			JSONData.put("ERROR", "To avoid spam, we limit users to posting only four comments in a two-hour window.");
        	    	return JSONData.toString();
        		}
    			/*** Update commentTrackerTime in the database ***/
    			if ( commentTrackerTime < UTC ) {
    				dao.setAttribute(userId, "commentTrackerTime", UTC+30*60*1000+"");
    			} else {
    				dao.setAttribute(userId, "commentTrackerTime", commentTrackerTime+30*60*1000+"");
    			}
    		} catch (Exception e) {
    			// the user has not commented before
    		}

			/*** Validate that the user is not blocked for commenting ***/
   			String isCommenterBlocked = dao.getAttribute(userId, "isCommenterBlocked");
    		if ( StringUtils.equals(isCommenterBlocked, "YES") ) {
    			JSONData.put("ERROR", "You have been blocked for posting comments.");
    	    	return JSONData.toString();
    		}

    		/*** Exit if newUserCreated ***/
    		if ( unconfirmedUser ) {
				JSONData.put("ERROR", "ACTION REQUIRED: Please check your email and confirm the comment.");
		    	return JSONData.toString();
    		}

			/*** Get EntityId from Posts table ***/
    		/*** For Security, never accept bloggerId from HTML or JavaScript ***/
			String bloggerId = dao.getPostBloggerId(postId);

    		/*** Get commenter name ***/
			String commenterName = dao.getEntityName(userId);

			/*** Before saving the comment, check if the user was a former commenter ***/
			String checkCommenterIdExists = dao.checkCommenterIdExists(postId, userId);

			/*** Save comment into the database ***/
			dao.saveComment(postId, parentCommentDate, commentText, userId, thankedFlag, UTC);

    		/*** Make commenter a follower of future comments ***/
			String flag = dao.getNotificationFlag(commenterEmail, "followWhenCommented");
			if ( StringUtils.equals(flag, "Y") )
				dao.followComments(postId, commenterEmail);

   			/*** Register postComment in SQS to be processed by cron job ***/
			if ( StringUtils.equals(publishFlag, "Y") ) {
				JSONObject msgBody = new JSONObject();
				msgBody.put("category", "postComment");
				msgBody.put("postId", postId);
				msgBody.put("zoneId", zoneId);
				msgBody.put("zoneName", zoneName);
				msgBody.put("bloggerId", bloggerId);
				msgBody.put("pageId", pageId + " ");
				msgBody.put("origCommentText", origCommentText);
				msgBody.put("commenterId", userId);
				msgBody.put("commenterName", commenterName);
				msgBody.put("commenterEmail", commenterEmail);
				msgBody.put("commentText", commentText);
				msgBody.put("checkCommenterIdExists", checkCommenterIdExists);
				sqs.sendMessage (
					new SendMessageRequest()
						.withQueueUrl( config.getProperty("processQueueSQS") )
						.withMessageBody(msgBody.toString())
				);
			}

    		/*** Fetch first page of comments ***/
			return doGetCommentsNext(postId, "NULL");

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        try {
	        	JSONData.put("ERROR", "Unable to post comment");
	        } catch (JSONException j) {
	        	// do nothing
	        }
		}
    	return JSONData.toString();
    }

    /**
     * Confirms a comment on the post
     * @param unconfirmedCommentId
     */
    @RequestMapping(value="/confirm-comment/{unconfirmedCommentId}", method={RequestMethod.GET})
    public ModelAndView doConfirmComment (@PathVariable String unconfirmedCommentId) {

    	String postId = null;
    	try {

    		/*** Get record from UnconfirmedComments table ***/
    		Map<String,AttributeValue> unconfirmedComments = dao.getUnconfirmedComments(unconfirmedCommentId);

			/*** Get postId and userId ***/
    		postId = unconfirmedComments.get("PostId").getS();
    		String commenterId = unconfirmedComments.get("CommenterId").getS();

    		/*** Get EntityId from Posts table ***/
			/*** For Security, never accept bloggerId from HTML or JavaScript ***/
			String bloggerId = dao.getPostBloggerId(postId);

			/*** Get commenter name ***/
			String commenterName = dao.getEntityName(commenterId);

			/*** Before saving the comment, check if the user was a former commenter ***/
			String checkCommenterIdExists = dao.checkCommenterIdExists(postId, commenterId);

			/*** Save comment into the database ***/
    		long UTC = System.currentTimeMillis();
			dao.saveComment(postId, Long.parseLong(unconfirmedComments.get("ParentCommentDate").getN()),
				unconfirmedComments.get("CommentText").getS(), commenterId,
				unconfirmedComments.get("ThankedFlag").getS(), UTC);

			/*** Make commenter a follower of future comments ***/
			String flag = dao.getNotificationFlag(unconfirmedComments.get("CommenterEmail").getS(), "followWhenCommented");
			if ( StringUtils.equals(flag, "Y") )
				dao.followComments(postId, unconfirmedComments.get("CommenterEmail").getS());

			/*** Register postComment in SQS to be processed by cron job ***/
			if ( StringUtils.equals(unconfirmedComments.get("PublishFlag").getS(), "Y") ) {
				JSONObject msgBody = new JSONObject();
				msgBody.put("category", "postComment");
				msgBody.put("bloggerId", bloggerId);
				msgBody.put("zoneId", unconfirmedComments.get("ZoneId").getS());
				msgBody.put("zoneName", unconfirmedComments.get("ZoneName").getS());
				msgBody.put("postId", postId);
				try {
					msgBody.put("pageId", unconfirmedComments.get("PageId").getS() + " ");
				} catch (Exception e1) {
					msgBody.put("pageId", "");
				}
				try {
					msgBody.put("origCommentText", unconfirmedComments.get("OrigCommentText").getS());
				} catch (Exception e) {
					msgBody.put("origCommentText", "");
				}
				msgBody.put("commenterId", commenterId);
				msgBody.put("commenterName", commenterName);
				msgBody.put("commenterEmail", unconfirmedComments.get("CommenterEmail").getS());
				msgBody.put("commentText", unconfirmedComments.get("CommentText").getS());
				msgBody.put("checkCommenterIdExists", checkCommenterIdExists);
				sqs.sendMessage (
					new SendMessageRequest()
						.withQueueUrl( config.getProperty("processQueueSQS") )
						.withMessageBody(msgBody.toString())
				);
			}

			/*** Delete from UnconfirmedComments table ***/
			dao.deleteUnconfirmedComments (unconfirmedCommentId);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"unconfirmedCommentId: " + unconfirmedCommentId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return new ModelAndView("redirect:/do/commentconfirmed");
		}
		return new ModelAndView("redirect:/post/"+postId+"#postComments");

    }

    /**
     * Builds out Comment Already Confirmed page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/commentconfirmed", method={RequestMethod.GET})
    public String doCommentConfirmed (ModelMap map) {
    	return "commentconfirmed";
    }

    /**
     * Updates the comment
     * @param postId postId
     * @param commentDate
     * @param commentText Text of the comment
     * @return ERROR message if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/update-comment", method={RequestMethod.POST})
    public @ResponseBody String doUpdateComment(String postId, String commentDate, String commentText) {

    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Get CommenterId from Comments table ***/
			String commenterId = dao.getCommenterId(postId, commentDate);

			/*** Validate that logged-in user is the original Commenter ***/
			if ( !StringUtils.equals(userId, commenterId) ) {
				return "You are not allowed to update this comment.";
			}

    		// validate comment text
			if ( StringUtils.containsAny(commentText, new String[]{"http://", "https://", "www.", ".com" } ) ) {
       	        logger.log(Level.INFO,"Backlinks in updated comment text: " + commentText );
    			return "Backlinks are not allowed in comments.";
			}

			/*** Save comment into the database ***/
			dao.updateComment(postId, commentDate, commentText);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        	return "Unable to update the comment.";
		}
    	return "SUCCESS";
    }

    /**
     * Thanks the comment (and its replies)
     * @param postId postId
     * @param postTitle postTitle
     * @param bloggerId userId of the blogger
     * @param bloggerName name of the blogger
     * @param commentDate
     * @param thankFlag 2 for thank, 1 for unthank
     * @param commenterId userId of the commenter
     * @return return message
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/thank-comment", method={RequestMethod.POST})
    public @ResponseBody String doThankComment(String postId, String postTitle, String bloggerId, String bloggerName, String commentDate, String thankFlag, String commenterId) {

    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Validate that logged-in user is the blogger ***/
			if ( !StringUtils.equals(userId, bloggerId) ) {
				return "Only the author of the blog can thank the commenter.";
			}

			/*** Validate that the comment is not a reply ***/
			if ( !StringUtils.equals(StringUtils.substring(commentDate, 14, 27), "9999999999999") ) {
				return "Replies can't be thanked.";
			}

			/*** Update the data ***/
			dao.thankComment(postId, commentDate, thankFlag);

    		/*** Send Email ***/
			String commenterEmail = dao.getEntityEmail(commenterId);
			String commenterFirstName = dao.getFirstName(commenterEmail);
			String flag = dao.getNotificationFlag(commenterEmail, "notifyWhenThanked");
			if ( StringUtils.equals(flag, "Y") && StringUtils.equals(thankFlag, "2") )
				ses.sendCommentThankedEmail(commenterEmail, commenterFirstName, postId, postTitle, userId, bloggerName);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/thank-comment");
	        logger.log(Level.SEVERE,"postId: " + postId + " commentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        	return "Unable to thank the comment.";
		}
    	return "SUCCESS";
    }

    /**
     * Reports the comment as spam
     * @param postId postId
     * @param commentDate
     * @return return message
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/report-comment", method={RequestMethod.POST})
    public @ResponseBody String doReportComment(String postId, String commentDate, String commentText) {

    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

    		/*** Decode the content ***/
			String commentTextDecoded = URLDecoder.decode(commentText,"UTF-8");

			/*** Report comment to the customer service ***/
			ses.sendReportCommentEmail(postId, commentDate, userId, commentTextDecoded);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/report-comment");
	        logger.log(Level.SEVERE,"postId: " + postId + " commentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        	return "Unable to report the comment.";
		}
    	return "Thanks for reporting this comment as spam.";
    }

    /**
     * Deletes a given Comment on a post
     * @param zoneId zoneId
     * @param postId postId
     * @param commentDate commentDate
     * @param commentText
     * @param commentsKeyRefresh key to refresh the page
     * @return ERROR message if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/delete-comment", method={RequestMethod.POST})
    public @ResponseBody String doDeleteComment (String postId, String zoneId, String pageId, String commentDate, String commentText, String commentsKeyRefresh) {

    	JSONObject JSONData = new JSONObject();
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept UserId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Get bloggerId and commenterId of the post ***/
			String bloggerId = dao.getPostBloggerId(postId);
			String commenterId = dao.getCommenterId(postId, commentDate);

			/*** Verify if logged-in user is either the commenter or the blogger ***/
			if ( !StringUtils.equals(commenterId, userId) && !StringUtils.equals(bloggerId, userId) ) {
				JSONData.put("ERROR", "You are not allowed to delete this comment.");
		   		return JSONData.toString();
			}

    		/*** Decode the content ***/
			String commentTextDecoded = URLDecoder.decode(commentText,"UTF-8");

			/*** Delete comment from the database ***/
			dao.deleteComment(postId, commentDate);

			/*** Check if there are any more comments by this user on the post ***/
			String checkCommenterIdExists = dao.checkCommenterIdExists(postId, commenterId);

			/*** Send email to the commenter if someone else is deleting the comment (blogger, heatbud admin etc) ***/
			if ( !StringUtils.equals(userId, commenterId) ) {
				String commenterEmail = dao.getEntityEmail(commenterId);
				String commenterFirstName = dao.getFirstName(commenterEmail);
				ses.sendCommentReportedEmail(commenterEmail, commenterFirstName, postId, dao.getPostTitle(postId), commentTextDecoded);
			}

   			/*** Register deleteComment in SQS to be processed by cron job ***/
			JSONObject msgBody = new JSONObject();
			msgBody.put("category", "deleteComment");
			msgBody.put("bloggerId", bloggerId);
			msgBody.put("postId", postId);
			msgBody.put("zoneId", zoneId);
			msgBody.put("pageId", pageId + " ");
			msgBody.put("checkCommenterIdExists", checkCommenterIdExists);
			sqs.sendMessage (
				new SendMessageRequest()
					.withQueueUrl( config.getProperty("processQueueSQS") )
					.withMessageBody(msgBody.toString())
			);

    		/*** Refresh the comments list ***/
			return doGetCommentsNext(postId, commentsKeyRefresh);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId + " commentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        try {
	        	JSONData.put("ERROR", "Unable to delete the comment");
	        } catch (JSONException j) {
	        	// do nothing
	        }
		}
   		return JSONData.toString();

    }

    /**
     * Follow Comments on a post
     * @param postId postId
     * @return ERROR message if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/follow-comments", method={RequestMethod.POST})
    public @ResponseBody String doFollowComments (String postId) {

    	JSONObject postInfo = new JSONObject();
    	try {
	    	/*** Get Email Address from Spring Security Context ***/
    		/*** For Security, never accept Email Address from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String followerEmail = user.getUsername();

    		/*** Follow Comments ***/
			String followerId = dao.followComments(postId, followerEmail);
			postInfo.put("followerId", followerId);

    		/*** Get current followers count ***/
    		int cfCount = dao.getCommentFollowersCount(postId);
    		postInfo.put("cfCount", cfCount + "");

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return postInfo.toString();

    }

    /**
     * Unfollow Comments on a post
     * @param postId postId
     * @param followerId Unique Id generated by System for a follower
     * @return ERROR message if any
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/action/unfollow-comments", method={RequestMethod.POST})
    public @ResponseBody String doUnfollowComments (String postId, String followerId) {

    	JSONObject postInfo = new JSONObject();
    	try {

    		/*** Unfollow Comments ***/
    		dao.unfollowComments(postId, followerId);

			/*** Get current followers count ***/
    		int cfCount = dao.getCommentFollowersCount(postId);
    		postInfo.put("cfCount", cfCount + "");

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"/action/unfollow-comments");
	        logger.log(Level.SEVERE,"postId: " + postId + " followerId: " + followerId );
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return postInfo.toString();

    }

    /**************************************************************************
     *************************  IMAGE MODULE  *********************************
     **************************************************************************/

    /**
     * Builds the My Images page
     */
	@RequestMapping(value="/user/images", method={RequestMethod.GET, RequestMethod.POST})
	public String doImageBrowser(String CKEditorFuncNum, ModelMap map) {

		String userId = null;
		try {

	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();
	    	map.addAttribute("userId", userId);

			// This variable is needed to return the chosen picture
	    	map.addAttribute("CKEditorFuncNum", CKEditorFuncNum);

	    	// List folder names (along with path) from S3
	    	List<String> folders = s3.listFolders(config.getProperty("bucketNameImages"), userId+"/thumbs/");
	    	// List of folder names (removing the path)
	    	List<String> foldersList = new ArrayList<String>();
	    	for ( int i = 0; i < folders.size(); i++ ) {
		    	// Remove the path <userId>/thumbs/<foldername>/
	    		String folder = folders.get(i).split("/")[2];
	    		// add all folders except common (we handle this separately)
	    		if ( !StringUtils.equals(folder,"common") ) {
	    			foldersList.add(folder);
	    		}
	    	}
	    	map.addAttribute("foldersList", foldersList);

	    	// List image objects (along with path for image name) from the common folder
	    	List<S3File> images = s3.listFiles(config.getProperty("bucketNameImages"), userId+"/thumbs/common/");
	    	// List of image objects (removing path for image name)
	    	List<S3File> imagesList = new ArrayList<S3File>();
	    	for ( int i = 0; i < images.size(); i++ ) {
	    		S3File image = images.get(i);
		    	// remove the path <userId>/thumbs/<foldername>/<imagename>
	    		image.setName((image.getName()+"/").split("/")[3]);
	   			imagesList.add(image);
	    	}
	    	map.addAttribute("imagesList", imagesList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/images");
	        logger.log(Level.SEVERE,"userId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

		return "/user/images";

	}

	/**
     * Uploads image into S3
     * @param request MultipartHttpServletRequest as FormData object
     * @return success or error message
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="/user/upload-image", method={RequestMethod.POST})
    public @ResponseBody String doUploadImage (MultipartHttpServletRequest request) {

    	String origFileName = null;
    	String origFileContentType = null;
    	String fileName = null;
    	try {

    		/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Get file from the request object ***/
			Iterator<String> itr = request.getFileNames();
			String folderName = itr.next();
			MultipartFile mpf = request.getFile(folderName);
			origFileName = mpf.getOriginalFilename();
			origFileContentType = mpf.getContentType();

    		/*** Make filename URL-friendly ***/
			String name = null;
			String ext = ".jpg";
			try {
				// original file name
				fileName = origFileName;
	    		// convert to lower case
				fileName = StringUtils.lowerCase(fileName);
				// cut fileName into name and ext
				name = fileName.substring(0,fileName.lastIndexOf("."));
				ext = fileName.substring(fileName.lastIndexOf("."));
				// replace all non-alphanumeric characters (including spaces) with dashes
				name = name.replaceAll("[^a-z0-9]","-");
				// trim two or more subsequent dashes into one
				name = name.trim().replaceAll("-+", "-");
				// remove the first character if it's a dash
				name = name.startsWith("-") ? name.substring(1) : name;
				// remove the last character if it's a dash
				name = name.endsWith("-") ? name.substring(0,name.length()-1) : name;
				// word wrap until 30 characters
				name = name.substring(0, StringUtils.substring(name+"-",0,30).lastIndexOf("-"));
				// if the string is empty, generate a random id based on the system time
				if ( name.length() == 0 ) name = userId + (long) System.currentTimeMillis()/21600000;
				// combine name and ext to make the file name
				fileName = name + ext;
			} catch (Exception e) {
				fileName = userId + (long) System.currentTimeMillis()/21600000 + ext;
			}

    		/*** Save Image into S3 ***/
			HeatbudS3Object image = new HeatbudS3Object();
			image.setBucketName(config.getProperty("bucketNameImages"));
			// image will be stored as "<userid>/images/<filename>"
	        image.setKey(userId+"/images/"+folderName+"/"+fileName);
    		image.setData(mpf.getBytes());
    		image.setContentType(mpf.getContentType());
    		image.setCacheControl("max-age=36000");
    		String retMessage = s3.store(image, false, CannedAccessControlList.PublicRead, false);
    		if ( StringUtils.equals(retMessage, "OBJECT_EXISTS") ) {
    	        return "File name already exists. Choose another name.";
    		}

    		/*** Create Thumbnail for the Image and save it into S3 ***/
			// image thumb will be stored as "<userid>/thumbs/<filename>"
			HeatbudS3Object imageThumb = new HeatbudS3Object();
			imageThumb.setBucketName(config.getProperty("bucketNameImages"));
	        imageThumb.setKey(userId+"/thumbs/"+folderName+"/"+fileName);
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        Thumbnails.of(new ByteArrayInputStream(mpf.getBytes())).size(540,420).outputQuality(1).outputFormat("jpg").toOutputStream(os);
    		imageThumb.setData(os.toByteArray());
    		imageThumb.setContentType("image/jpeg");
    		imageThumb.setCacheControl("max-age=36000");
    		s3.store(imageThumb, false, CannedAccessControlList.PublicRead, false);
		    IOUtils.closeQuietly(os);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Original File Name: " + origFileName);
	        logger.log(Level.SEVERE,"Original File Content Type: " + origFileContentType);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        return "Problem uploading image.";
		}
        return "SUCCESS";
    }

    /**
     * Query list of folders for the logged-in user.
     * @return List of folders
     */
    @RequestMapping(value="/user/get-folders", method={RequestMethod.POST})
    public @ResponseBody String doGetFolders() {

    	JSONObject foldersInfo = new JSONObject();
    	JSONArray foldersList = new JSONArray();
    	String userId;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	// List folder names (along with path) from S3
	    	List<String> folders = s3.listFolders(config.getProperty("bucketNameImages"), userId+"/thumbs/");
	    	for ( int i = 0; i < folders.size(); i++ ) {
		    	// Remove the path <userId>/thumbs/<foldername>/
	    		String folder = folders.get(i).split("/")[2];
	    		// add all folders except common (we handle this separately)
	    		if ( !StringUtils.equals(folder,"common") ) {
	    			foldersList.put(folder);
	    		}
	    	}
	    	foldersInfo.put("foldersList", foldersList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/get-folders");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return foldersInfo.toString();
    }

    /**
     * Query list of images from the given folder.
     * @param folderName name of the folder
     * @return List of image objects
     */
    @RequestMapping(value="/user/get-images", method={RequestMethod.POST})
    public @ResponseBody String doGetImages(String folderName) {

    	JSONObject imagesInfo = new JSONObject();
    	JSONArray imagesList = new JSONArray();
    	String userId;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	// List images from the input folder
	    	List<S3File> images = s3.listFiles(config.getProperty("bucketNameImages"), userId+"/thumbs/"+folderName+"/");
	    	for ( int i = 0; i < images.size(); i++ ) {
	    		S3File image = images.get(i);
		    	// extract image name from <userId>/thumbs/<foldername>/<imagename>
	    		image.setName((image.getName()+"/").split("/")[3]);
	   			imagesList.put(new JSONObject(image));
	    	}
	    	imagesInfo.put("imagesList", imagesList);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/get-images");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return imagesInfo.toString();
    }

    /**
     * Deletes an image from S3.
     * @return Success or Error message
     */
    @RequestMapping(value="/user/delete-image", method={RequestMethod.POST})
    public @ResponseBody String doDeleteImage(String key) {

    	String retMessage = "Error deleteing " + key;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Delete Image ***/
    		s3.delete(config.getProperty("bucketNameImages"), userId+"/images/"+key);

    		/*** Delete Thumbnail ***/
    		s3.delete(config.getProperty("bucketNameImages"), userId+"/thumbs/"+key);

    		retMessage = "Deleted successfully";

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/delete-image");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return retMessage;
    }

    /**
     * Deletes a folder from S3. Recursively deletes all sub-folders and files.
     * @return Success or Error message
     */
    @RequestMapping(value="/user/delete-folder", method={RequestMethod.POST})
    public @ResponseBody String doDeleteFolder(String key) {

    	String retMessage = "Error deleteing " + key;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

			/*** Delete Image Folder ***/
    		s3.deleteRecursive(config.getProperty("bucketNameImages"), userId+"/images/"+key);

    		/*** Delete Thumbnail Folder ***/
    		s3.deleteRecursive(config.getProperty("bucketNameImages"), userId+"/thumbs/"+key);

    		retMessage = "Deleted successfully";

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/delete-folder");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

   		return retMessage;
    }

	private String generateTags(String postTitle) {

		String tags = "";
		try {
			for ( String word : StringUtils.split(postTitle) ) {
				if ( StringUtils.length(word) > 2 ) {
					if ( StringUtils.length(tags) == 0 ) {
						tags = word;
					} else {
						tags = tags + ", " + word;
					}
				}
			}
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"generateTags can't split: " + postTitle);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
		return tags;
	}

}
