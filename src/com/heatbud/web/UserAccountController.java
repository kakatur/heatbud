/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.heatbud.aws.HeatbudDynamoDBUtil;
import com.heatbud.aws.HeatbudS3Util;
import com.heatbud.aws.HeatbudSESUtil;
import com.heatbud.entity.Entity;
import com.heatbud.entity.ContactUs;
import com.heatbud.entity.HeatbudS3Object;
import com.heatbud.entity.User;
import com.heatbud.util.Configuration;
import com.heatbud.util.HeatbudCommon;

/**
 * This is the core of the Heatbud User Account functionality.  It includes Login Page, Settings page
 * and general Heatbud pages not covered by other controllers. It's a Spring controller implemented
 * using annotations.  Methods for loading and storing data are initiated in this class.
 */
@Controller
public class UserAccountController {

	// Logger object
	private static final Logger logger = Logger.getLogger(UserAccountController.class.getName());
	// Heatbud properties
	private static Configuration config = Configuration.getInstance();
	// S3 client
	private static HeatbudS3Util s3 = new HeatbudS3Util();
	// SQS client
    private static AmazonSQS sqs = AmazonSQSClientBuilder.standard()
      	.withEndpointConfiguration(
      		new EndpointConfiguration("https://sqs.us-west-2.amazonaws.com", "us-west-2")
      	).build();
    // Common Functions client
    @Autowired
	private HeatbudCommon common;
	// DynamoDB client
    private static AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
      	.withEndpointConfiguration(
      		new EndpointConfiguration("https://dynamodb.us-west-2.amazonaws.com", "us-west-2")
      	).build();
	// DynamoDB Util
    @Autowired
	private HeatbudDynamoDBUtil dao;
	// SES Util
    @Autowired
	private HeatbudSESUtil ses;

    /**************************************************************************
     ***********************  HEALTH CHECK  ***********************************
     **************************************************************************/

    /**
     * AWS Elastic Beanstalk checks your application's health by periodically
     * sending an HTTP HEAD request to a resource in your application. By
     * default, this is the root or default resource in your application,
     * but can be configured for each environment.
     *
     * Here, we report success as long as the app server is up, but skip
     * generating the whole page since this is a HEAD request only. You
     * can employ more sophisticated health checks in your application.
     *
     * @param response http servlet response
     */
    @RequestMapping(value="/do/healthcheck", method={RequestMethod.GET, RequestMethod.HEAD})
    public void doHealthCheck(HttpServletResponse response) {
        response.setContentLength(0);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**************************************************************************
     **************************  LOGIN PAGE  **********************************
     **************************************************************************/

    /**
     * Builds out login page
     * @param error optional message to be shown in the sign-in block
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/login", method={RequestMethod.GET, RequestMethod.POST})
    public String doLogin (@RequestParam(value="error", required=false) String error, @RequestParam(value="username", required=false) String username, HttpServletRequest request, ModelMap map) {
    	// data
    	if ( StringUtils.isNotBlank(error) ) {
    		map.addAttribute("error", error);
    		map.addAttribute("username", username);
    	}
    	User user = new User();
    	user.setSource("heatbud");
        map.addAttribute("user", user);
       	// mobile
   		try {
       		Device device = DeviceUtils.getCurrentDevice(request);
       		if (device.isMobile()) {
       			return "login-mobile";
       		}
   		} catch (Exception e) {}
   		// desktop
        return "login";
    }

    /**************************************************************************
     ***********************  UNFOLLOW COMMENTS  ******************************
     **************************************************************************/

    /**
     * Builds out Unfollow Comments page
     * @param postId postId
     * @param followerId unique id generated by System for each follower
     * @param map spring model for the request
     */
    @RequestMapping(value="/unfollow-comments/{postId}/{followerId}", method={RequestMethod.GET})
    public String doUnfollowComments (@PathVariable String postId, @PathVariable String followerId, ModelMap map) {

    	String postTitle = dao.getPostTitle(postId);
    	String retStatus = dao.unfollowComments(postId, followerId);

    	if ( StringUtils.equals(retStatus,"UNSUBSCRIBED") ) {
    		map.addAttribute("retMessage", "You have been successfully unsubscribed from the post - "+ postTitle);
    	} else {
    		if ( StringUtils.isBlank(postTitle) ) {
    			map.addAttribute("retMessage", "Post with Id=" + postId + " doesn't exist in our database.");
    		} else {
    			map.addAttribute("retMessage", "You are currently not subscribed to the post - "+ postTitle);
    		}
    	}

    	return "unfollow-comments";

    }

    /**************************************************************************
     *****************  OTHER GENERAL HEATBUD PAGES  **************************
     **************************************************************************/

    /**
     * Builds out About Us page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/about", method={RequestMethod.GET})
    public String doAbout (ModelMap map) {
    	return "about";
    }

    /**
     * Builds out the main Help Center page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/help", method={RequestMethod.GET})
    public String doHelpMain (HttpServletRequest request, ModelMap map) {
    	// mobile
		try {
    		Device device = DeviceUtils.getCurrentDevice(request);
    		if (device.isMobile()) {
    	    	map.put("navigation", "main");
    			return "help-mobile-navigation";
    		}
		} catch (Exception e) {}
		// desktop
    	return doHelp("main", "what", request, map);
    }

    /**
     * Builds out Help Center page for a given topic
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/help/{navigation}/{content}", method={RequestMethod.GET})
    public String doHelp (@PathVariable String navigation, @PathVariable String content, HttpServletRequest request, ModelMap map) {
    	// put variables into map
    	map.put("navigation", navigation);
    	map.put("content", content);
    	// mobile
		try {
    		Device device = DeviceUtils.getCurrentDevice(request);
    		if (device.isMobile()) {
    			return "help-mobile-content";
    		}
		} catch (Exception e) {}
		// desktop
    	return "help";
    }

    /**
     * Builds out main/why-1 page for Google and Bing Ads
     * @param map spring model for the request
     */
    @RequestMapping(value="/business/website", method={RequestMethod.GET})
    public String doBusinessWebsite (ModelMap map) {
    	map.put("navigation", "main");
    	map.put("content", "why-1");
    	return "business-website";
    }

    /**
     * Builds out main/why-1 page for Google and Bing Ads
     * @param map spring model for the request
     */
    @RequestMapping(value="/business/traffic", method={RequestMethod.GET})
    public String doBusinessTraffic (ModelMap map) {
    	map.put("navigation", "main");
    	map.put("content", "why-1");
    	return "business-traffic";
    }

    /**
     * Builds out main/pricing page for Google and Bing Ads
     * @param map spring model for the request
     */
    @RequestMapping(value="/make/money", method={RequestMethod.GET})
    public String doMakeMoney (ModelMap map) {
    	return "make-money";
    }

    /**
     * Builds out Privacy and Terms page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/privacy", method={RequestMethod.GET})
    public String doTerms (ModelMap map) {
    	return "privacy";
    }

    /**
     * Builds out Partnerships page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/partnerships", method={RequestMethod.GET})
    public String doPartnerships (ModelMap map) {
    	return "partnerships";
    }

    /**
     * Builds out Careers page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/careers", method={RequestMethod.GET})
    public String doCareers (ModelMap map) {
    	return "careers";
    }

    /**
     * Builds out Newsletters page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/newsletters", method={RequestMethod.GET})
    public String doNewsletters (ModelMap map) {
    	return "newsletters";
    }

    /**
     * Builds out Error page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/error", method={RequestMethod.GET})
    public String doError (ModelMap map) {
    	return "error";
    }

    /**
     * Builds out Maintenance page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/maintenance", method={RequestMethod.GET})
    public String doMaintenance (ModelMap map) {
    	return "maintenance";
    }

    /**
     * Builds out Page-Not-Found page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/notfound", method={RequestMethod.GET})
    public String doPageNotFound (ModelMap map) {
    	return "notfound";
    }

    /**************************************************************************
     **********************  CONTACT US PAGE  *********************************
     **************************************************************************/

    /**
     * Builds out contactUs page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/contact", method={RequestMethod.GET})
    public String doContactUs (ModelMap map) {
        map.addAttribute("contactUs", new ContactUs());
        return "contact";
    }

    /**
     * Processes data received from Contact Us form and sends email to Heatbud customer service.
     * @param contactUs details submitted by user
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/contact-submit", method={RequestMethod.POST})
    public ModelAndView doContactUsSubmit (@ModelAttribute(value="contactUs") ContactUs contactUs, ModelMap map, HttpServletRequest request) {

    	try {
        	// Set createDate
        	contactUs.setCreateDate(System.currentTimeMillis());

	        // Set Client IP Address
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if ( StringUtils.isBlank(ipAddress) ) ipAddress = request.getRemoteAddr();
        	contactUs.setIPAddress(ipAddress);

        	// Validate reCAPTCHA
        	String reCAPTCHAReturnCode = "true"; // assume true as the default
        	try {
	        	HttpURLConnection con = (HttpURLConnection) new URL("https://www.google.com/recaptcha/api/siteverify").openConnection();
	        	con.setRequestMethod("POST");
	        	con.setRequestProperty("secret", "6LcyckwUAAAAAKOBeySzNK736_nAXdHtoNZ0kRG-");
	        	con.setRequestProperty("response", request.getParameter("g-recaptcha-response"));
	        	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = in.readLine()) != null) {
	            	response.append(inputLine);
	            }
	            in.close();
	            JSONObject jsonResponse = new JSONObject(response.toString());
	            reCAPTCHAReturnCode = jsonResponse.getString("success");
        	} catch (Exception e) {}
    		if ( StringUtils.equals(reCAPTCHAReturnCode, "false") ) {
    	        logger.log(Level.SEVERE,"reCAPTCHA validation error from: " + ipAddress );
    	        logger.log(Level.SEVERE,"Subject: " + contactUs.getContactSubject() );
    	        logger.log(Level.SEVERE,"Email Address: " + contactUs.getContactEmail() );
    			contactUs.setError("Sorry, We don't allow robots to contact us.");
    			return new ModelAndView("contact", map);
    		}

    		// Validate IP address
    		if ( StringUtils.indexOfAny(ipAddress, common.blockedIPs) != -1 ) {
    	        logger.log(Level.INFO,"Blocked IP Address is attempting to contact us: " + ipAddress );
    	        logger.log(Level.INFO,"Subject: " + contactUs.getContactSubject() );
    			contactUs.setError("You have been blocked from contacting us.");
    			return new ModelAndView("contact", map);
    		}

    		// Validate Name
    		if ( StringUtils.isBlank(contactUs.getContactName()) ) {
    			contactUs.setError("Please enter your name.");
    			return new ModelAndView("contact", map);
    		}

    		// Validate Subject
    		if ( StringUtils.isBlank(contactUs.getContactSubject()) ) {
    			contactUs.setError("Subject cannot be blank.");
    			return new ModelAndView("contact", map);
    		}
			if ( StringUtils.containsAny(contactUs.getContactSubject(), new String[]{"http://", "https://", "www.", ".com" } ) ) {
    			contactUs.setError("ERROR: Backlinks not allowed in the contact form.");
    			return new ModelAndView("contact", map);
			}

    		// Validate Message Body
			if ( StringUtils.containsAny(contactUs.getContactMessage(), new String[]{"http://", "https://", "www.", ".com" } ) ) {
    			contactUs.setError("ERROR: Backlinks not allowed in the contact form.");
    			return new ModelAndView("contact", map);
			}

    		// Validate email address
			String valMessage = common.validateEmailAddress(contactUs.getContactEmail());
    		if ( valMessage != "SUCCESS" ) {
    			if ( StringUtils.equals(valMessage, "Vitrual email addresses are not allowed." ) ) {
           	        logger.log(Level.SEVERE,"Blocked Email Domain is attempting to contact us: " + contactUs.getContactEmail() );
    			}
    			contactUs.setError(valMessage);
    			return new ModelAndView("contact", map);
    		}

   			// Send Email
        	ses.sendContactUsEmail(contactUs.getContactName(), contactUs.getContactEmail(), contactUs.getContactSubject(), contactUs.getContactMessage(), contactUs.getIPAddress());

        	// Flush the input fields and display thanks message
        	contactUs.setContactName("");
        	contactUs.setContactEmail("");
        	contactUs.setContactSubject("");
        	contactUs.setContactMessage("");
        	contactUs.setError("Thanks for contacting us.");

    	} catch (Exception e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
        	contactUs.setError("Error sending email. Please try again.");
    	}

		return new ModelAndView("contact", map);
    }

    /**************************************************************************
     ***************  LOGIN PAGE - NEW ACCOUNT SIGNUP  ************************
     **************************************************************************/

    /**
     * Creates user account
     * @param user User object with form bound data
     */
    @RequestMapping(value="/do/signup", method={RequestMethod.POST})
    public ModelAndView doSignup (@ModelAttribute(value="user") User user, ModelMap map, HttpServletRequest request) {

    	try {

    		// Check if the IP address is blocked and set IP Address
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if ( StringUtils.isBlank(ipAddress) ) {
				ipAddress = request.getRemoteAddr();
			}
			if ( StringUtils.indexOfAny(ipAddress, common.blockedIPs) != -1 ) {
    	        logger.log(Level.SEVERE,"Blocked IP Address is attempting to register: " + ipAddress );
    			user.setError("You have been blocked from further registrations.");
       	    	map.addAttribute("user", user);
       	    	return new ModelAndView("login", map);
			}
        	user.setIPAddress(ipAddress);

    		// Trim email address and Convert to lower case
    		try {
    			user.setUsername(StringUtils.lowerCase(user.getUsername().trim()));
    		} catch (Exception e) {
    			user.setError("Email Address is not valid.");
    	    	map.addAttribute("user", user);
    	    	return new ModelAndView("login", map);
    		}

    		// Validate email address
			String valMessage = common.validateEmailAddress(user.getUsername());
    		if ( valMessage != "SUCCESS" ) {
    			if ( StringUtils.equals(valMessage, "Vitrual email addresses are not allowed." ) ) {
           	        logger.log(Level.SEVERE,"Blocked Email Domain is attempting to register: " + user.getUsername() );
    			}
    			user.setError(valMessage);
       	    	map.addAttribute("user", user);
       	    	return new ModelAndView("login", map);
    		} else {
      			User userDB = dao.getUser(user.getUsername());
           		if ( userDB != null ) {
           			user.setError("Email Address already registered.");
           	    	map.addAttribute("user", user);
           	    	return new ModelAndView("login", map);
           		}
    		}

    		// Validate first name
    		if (StringUtils.isBlank(user.getFirstName())) {
    			user.setError("First Name cannot be blank.");
    	    	map.addAttribute("user", user);
    	    	return new ModelAndView("login", map);
            }
    		if (StringUtils.equals(user.getFirstName(),"First Name")) {
    			user.setError("Please enter your First Name.");
    	    	map.addAttribute("user", user);
    	    	return new ModelAndView("login", map);
            }

    		// Validate last name
    		if (StringUtils.isBlank(user.getLastName())) {
    			user.setError("Last Name cannot be blank.");
    	    	map.addAttribute("user", user);
    	    	return new ModelAndView("login", map);
            }
    		if (StringUtils.equals(user.getLastName(),"Last Name")) {
    			user.setError("Please enter your Last Name.");
    	    	map.addAttribute("user", user);
    	    	return new ModelAndView("login", map);
            }

			// Trim password of white spaces
			user.setPassword(user.getPassword().trim());

            // Validate password
            String validateMessage = common.validatePassword(user.getPassword());
            if ( validateMessage != "SUCCESS" ) {
            	user.setError(validateMessage);
            	map.addAttribute("user", user);
            	return new ModelAndView("login", map);
            }

        	// Set createDate
        	user.setCreateDate(System.currentTimeMillis());

        	// Get City, State and Country based on the IP address
        	String country = null;
        	String state = null;
        	String city = null;
        	try {
				ipAddress = user.getIPAddress().split(",")[0];
	        	HttpURLConnection con = (HttpURLConnection) new URL("http://api.db-ip.com/v2/free/"+ipAddress).openConnection();
	        	con.setRequestMethod("GET");
	        	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = in.readLine()) != null) response.append(inputLine);
	            in.close();
	            JSONObject jsonResponse = new JSONObject(response.toString());
	            country = jsonResponse.getString("countryName");
	            try { state = jsonResponse.getString("stateProv"); } catch (Exception e) { state = country; }
	            try { city = jsonResponse.getString("city"); } catch (Exception e) { city = state; }
        	} catch (Exception e) {
                logger.log(Level.SEVERE,"Unable to fetch location information for Username=" + user.getUsername() + " UserId=" + user.getUserId() );
            	logger.log(Level.SEVERE,e.getMessage(),e);
        	}

        	// Set salt
        	UUID salt = UUID.randomUUID();
        	user.setSalt(salt.toString());

        	// Encode password
        	ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);
        	String encodedPassword = encoder.encodePassword(user.getPassword(),user.getSalt());
        	user.setPassword(encodedPassword);

        	// About
        	String about = null;
        	if ( !StringUtils.equals(user.getAbout(), "undefined") &&
        		 !StringUtils.equals(user.getAbout(), "null") &&
        		 StringUtils.isNotBlank(user.getAbout())
        	   ) {
        		about = user.getAbout();
        	}

        	// Generate entityId in Entities table - And use it as userId for Users table
    		String userId = dao.generateEntityId (
    			user.getUsername(), user.getFirstName()+" "+user.getLastName(), user.getCreateDate(),
    			about, user.getFbId(), country, state, city);
    		user.setUserId(userId);

        	if ( StringUtils.equals(user.getSource(), "heatbud") ) {
	    		// Set role to ROLE_NONE to indicate that email address has yet not been verified
	    		user.setRole("ROLE_NONE");

	            // Now save user into the database and send verification email
	        	dao.saveUser(user);
	        	ses.sendVerificationEmail(user.getFirstName(), user.getUsername(), user.getSalt(), user.getIPAddress());

	        	// Display thank you message
	        	user.setError("Please check your Inbox (also Junk folder) for Email Verification Request.");
        	} else {
	    		// Set role to ROLE_USER to indicate that email address has been verified
	    		user.setRole("ROLE_USER");

	            // Now save user into the database and send welcome email
	        	dao.saveUser(user);
	        	ses.sendWelcomeEmail(user.getFirstName(), user.getUsername(), user.getUserId());

	        	// Update Contact in Entities table
	        	if ( !StringUtils.equals(user.getContact(), "undefined") &&
	        		 !StringUtils.equals(user.getContact(), "null") &&
	        		 StringUtils.isNotBlank(user.getContact())
	        	   ) {
		        	dao.updateEntityContact(user.getUserId(), user.getContact());
	        	}

	        	// Update TimeZone in Entities table
	        	if ( !StringUtils.equals(user.getTimeZone(), "undefined") &&
	        		 !StringUtils.equals(user.getTimeZone(), "null") &&
	        		 StringUtils.isNotBlank(user.getTimeZone())
	        	   ) {
		        	dao.updateEntityTimeZone(user.getUserId(), user.getTimeZone());
	        	}

	        	// Save Profile Photo
	        	if ( StringUtils.isNotBlank(user.getProfilePhoto()) ) {
	        		saveProfilePicture(user.getUserId(), user.getProfilePhoto(), "ProfilePhoto");
	        	}

	        	// Save Profile Background
	        	if ( StringUtils.isNotBlank(user.getProfileBG()) ) {
	        		saveProfilePicture(user.getUserId(), user.getProfileBG(), "ProfileBG");
	        	}

	        	// Enable Email Me
	        	dao.updateEnableEmail(user.getUserId(), "Y");

	        	// Create notification flags
	        	dao.createNotificationFlags(user.getUsername());

	        	// Display thank you message
	        	user.setError("");
	        	map.put("error", "Thanks for signing up. Please sign in to continue.");

	        	// Insert into Search
				dao.putBloggerSearch(user.getUserId(), user.getFirstName()+" "+user.getLastName());

	        	// Insert into TopCharts table for Ticker
				dao.putSignupTicker(user.getUserId());

        	}

        } catch (Exception e) {
            logger.log(Level.SEVERE,"Username=" + user.getUsername() + " UserId=" + user.getUserId() );
        	logger.log(Level.SEVERE,e.getMessage(),e);
        	user.setError("Unable to register. Please try again.");
        }

    	map.addAttribute("user", user);
    	return new ModelAndView("login", map);
    }

    /**
     * Creates user account when commenting
     * @param user User object
     * @return Success/ Error message
     */
    public String doSignupWhenCommenting (User user, HttpServletRequest request) {

    	String retMessage = "SUCCESS";
    	try {

    		// Check if the IP address is blocked and set IP Address
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if ( StringUtils.isBlank(ipAddress) ) {
				ipAddress = request.getRemoteAddr();
			}
			if ( StringUtils.indexOfAny(ipAddress, common.blockedIPs) != -1 ) {
    	        logger.log(Level.SEVERE,"Blocked IP Address is attempting to signup when commenting: " + ipAddress );
    			retMessage = "You have been blocked from further registrations.";
       	    	return retMessage;
			}
        	user.setIPAddress(ipAddress);

    		// Trim email address and Convert to lower case
    		try {
    			user.setUsername(StringUtils.lowerCase(user.getUsername().trim()));
    		} catch (Exception e) {
    			retMessage = "Email Address is not valid.";
       	    	return retMessage;
    		}

    		// Validate email address
			String valMessage = common.validateEmailAddress(user.getUsername());
    		if ( valMessage != "SUCCESS" ) {
    			if ( StringUtils.equals(valMessage, "Vitrual email addresses are not allowed." ) ) {
           	        logger.log(Level.SEVERE,"Blocked Email Domain is attempting to signup when commenting: " + user.getUsername() );
    			}
       	    	return valMessage;
    		}

			// Trim password of white spaces
			user.setPassword(user.getPassword().trim());

            // Validate password
            String validateMessage = common.validatePassword(user.getPassword());
            if ( validateMessage != "SUCCESS" ) {
    			retMessage = validateMessage;
       	    	return retMessage;
            }

        	// Set createDate
        	user.setCreateDate(System.currentTimeMillis());

        	// Get City, State and Country based on the IP address
        	String country = null;
        	String state = null;
        	String city = null;
        	try {
				ipAddress = user.getIPAddress().split(",")[0];
	        	HttpURLConnection con = (HttpURLConnection) new URL("http://api.db-ip.com/v2/free/"+ipAddress).openConnection();
	        	con.setRequestMethod("GET");
	        	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = in.readLine()) != null) response.append(inputLine);
	            in.close();
	            JSONObject jsonResponse = new JSONObject(response.toString());
	            country = jsonResponse.getString("countryName");
	            try { state = jsonResponse.getString("stateProv"); } catch (Exception e) { state = country; }
	            try { city = jsonResponse.getString("city"); } catch (Exception e) { city = state; }
        	} catch (Exception e) {
                logger.log(Level.SEVERE,"Unable to fetch location information for Username=" + user.getUsername() + " UserId=" + user.getUserId() );
            	logger.log(Level.SEVERE,e.getMessage(),e);
        	}

    		// Generate entityId in Entities table - And use it as userId for Users table
    		String userId = dao.generateEntityId (
    			user.getUsername(), user.getFirstName(), user.getCreateDate(),
    			null, null, country, state, city);
    		user.setUserId(userId);

        	// Set salt
        	UUID salt = UUID.randomUUID();
        	user.setSalt(salt.toString());

        	// Encode password
        	ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);
        	String encodedPassword = encoder.encodePassword(user.getPassword(), user.getSalt());
        	user.setPassword(encodedPassword);

    		// Set role to ROLE_NONE to indicate that email address has yet not been verified
    		user.setRole("ROLE_NONE");

            // Save user into the database
        	dao.saveUser(user);

        	// Create notification flags
        	dao.createNotificationFlags(user.getUsername());

        } catch (Exception e) {
            logger.log(Level.SEVERE,"Username=" + user.getUsername() + " UserId=" + user.getUserId() );
        	logger.log(Level.SEVERE,e.getMessage(),e);
			retMessage = "Unable to post the comment. Please try again.";
        }
    	return retMessage;

    }

    /**
     * Reads Profile Photo OR Profile Background from the given URL and saves it into S3
     * @param userId userId
     * @param url location of the picture
     * @param type Can be "ProfilePhoto" or "ProfileBG"
     */
    private void saveProfilePicture(String userId, String url, String type) {

    	try {

    		/*** Set fileName ***/
    		String fileName = null;
    		if ( StringUtils.equals(type, "ProfilePhoto") ) {
    			fileName = "photo";
    		} else {
    			fileName = "bg";
    		}

    		/*** Read picture into the Input Stream ***/
	        URL u = new URL(url);

    		/*** Save Image into S3 ***/
    		HeatbudS3Object image = new HeatbudS3Object();
			image.setBucketName(config.getProperty("bucketNameImages"));
			// image will be stored as "<userId>/images/<folderName>/<fileName>"
	        image.setKey(userId+"/images/profile/"+fileName);
	        InputStream is1 = u.openStream();
    		image.setData(IOUtils.toByteArray(is1));
    		image.setContentType("image/jpeg");
    		image.setCacheControl("max-age=36000");
    		s3.store(image, false, CannedAccessControlList.PublicRead, true);
		    IOUtils.closeQuietly(is1);

    		/*** Create Thumbnail and save it into S3 ***/
			HeatbudS3Object thumb = new HeatbudS3Object();
			thumb.setBucketName(config.getProperty("bucketNameImages"));
			// image will be stored as "<userId>/thumbs/<folderName>/<fileName>"
	        thumb.setKey(userId+"/thumbs/profile/"+fileName);
	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        InputStream is2 = u.openStream();
	        Thumbnails.of(new ByteArrayInputStream(IOUtils.toByteArray(is2))).size(540,420).outputQuality(1).outputFormat("jpg").toOutputStream(os);
    		thumb.setData(os.toByteArray());
    		thumb.setContentType("image/jpeg");
    		thumb.setCacheControl("max-age=36000");
    		s3.store(thumb, false, CannedAccessControlList.PublicRead, true);
		    IOUtils.closeQuietly(is2);
		    IOUtils.closeQuietly(os);

    		/*** Update Bloggers table ***/
    		// Use thumb for ProfilePhoto and the actual image for profileBG
    		if ( StringUtils.equals(type, "ProfilePhoto") ) {
    			dao.updateProfilePhoto(userId, "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userId+"/thumbs/profile/"+fileName);
    		} else {
    			dao.updateProfileBG(userId, "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userId+"/images/profile/"+fileName);
    		}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"saveProfilePicture");
	        logger.log(Level.SEVERE,"userId=" + userId + " URL=" + url + " type=" + type);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

    /**
     * Builds out email verification page for GET request method
     * @param salt verification code
     * @param username email address
     * @param map spring model for the request
     */
	@RequestMapping(value="/verify-email/{salt}/{username:.+}", method={RequestMethod.GET})
    public ModelAndView doVerifyEmailGET (@PathVariable String salt, @PathVariable String username, ModelMap map) {
		return doVerifyEmailPOST (salt, username, map);
	}

    /**
     * Builds out email verification page for POST request method
     * @param salt verification code
     * @param username email address
     * @param map spring model for the request
     */
	@RequestMapping(value="/do/verify-email", method={RequestMethod.POST})
    public ModelAndView doVerifyEmailPOST (String salt, String username, ModelMap map) {

		User user = new User();
	    user.setUsername(username);
	    user.setSalt(salt);
		User userDB;

		if ( StringUtils.isBlank(salt) || StringUtils.isBlank(username) ) {
			// If one or both of the parameters is null, take user to verifyEmail page
			map.addAttribute("user", user);
	        return new ModelAndView("verify-email", map);
		}

	    try {
	    	// query Users table
    		userDB = dao.getUser(username);
	    	if ( userDB == null ) {
	    		// user has not signed-up
	    		user.setError("Email Address not registered.");
	    	} else if (!StringUtils.equals(userDB.getSalt(), salt)) {
	    		// verification code doesn't match the database
	    		user.setError("Verification code and Email address do not match.");
	    	} else {
	    		// user has been verified. Set role to ROLE_USER
	    		userDB.setRole("ROLE_USER");
	            dao.saveUser(userDB);

	            // Send welcome email
	            ses.sendWelcomeEmail(userDB.getFirstName(), userDB.getUsername(), userDB.getUserId());

	        	// Enable Email Me
	        	dao.updateEnableEmail(userDB.getUserId(), "Y");

	        	// Create notification flags
	        	dao.createNotificationFlags(username);

	        	// Insert into Search
				dao.putBloggerSearch(userDB.getUserId(), userDB.getFirstName()+" "+userDB.getLastName());

	        	// Insert into TopCharts table for Ticker
				dao.putSignupTicker(userDB.getUserId());

	            // redirect user to the login page
	        	map.addAttribute("error", "Email verified. Please login to continue.");
				map.addAttribute("username", user.getUsername());
	            return new ModelAndView("redirect:/do/login", map);
	    	}

	    } catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	    // Email address couldn't be verified. Return to verifyEmail page
        map.addAttribute("user", user);
        return new ModelAndView("verify-email", map);

    }

    /**
     * Subscribes a given email address to the newsletter (and creates an account)
     * @param username email address
     * @param map spring model for the request
     */
    @RequestMapping(value="/action/subscribe", method={RequestMethod.POST})
    public @ResponseBody String doSubscribe (@PathVariable String username) {

		User user = new User();
	    try {
			// Trim email address and Convert to lower case
    		try {
    			user.setUsername(StringUtils.lowerCase(username.trim()));
    		} catch (Exception e) {
				return "Invalid Email Address.";
    		}

    		// Validate email address
			String valMessage = common.validateEmailAddress(user.getUsername());
    		if ( valMessage != "SUCCESS" ) {
    			if ( StringUtils.equals(valMessage, "Vitrual email addresses are not allowed." ) ) {
           	        logger.log(Level.SEVERE,"Blocked Email Domain is attempting to subscribe: " + user.getUsername() );
    			}
				return valMessage;
    		}

    		// Check if email already exists
   			User userDB = dao.getUser(user.getUsername());
       		if ( userDB != null ) {
 				return "Email Address already registered. Login and set your notifications.";
       		}

    		// Set First Name and Last Name
    		user.setFirstName(StringUtils.substring(username,0,StringUtils.indexOf(username,"@")));
    		user.setLastName(StringUtils.substring(username,0,StringUtils.indexOf(username,"@")));

    		// Set Role
    		user.setRole("ROLE_NONE");

        	// Set createDate
        	user.setCreateDate(System.currentTimeMillis());

    		// Generate entityId in Entities table - And use it as userId for Users table
    		String userId = dao.generateEntityId(user.getUsername(), user.getFirstName()+" "+user.getLastName(),
    			user.getCreateDate(), user.getFbId(), user.getAbout(), null, null, null);
    		user.setUserId(userId);

        	// Set password and salt
        	UUID salt = UUID.randomUUID();
        	user.setPassword(salt.toString());
        	user.setSalt(salt.toString());

        	// Save user into the database
            dao.saveUser(user);

	        // Create notification flags
	        dao.createNotificationFlags(user.getUsername());

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"username: " + username);
            logger.log(Level.SEVERE,e.getMessage(),e);
            return "Error subscribing. Please try again.";
    	}

	    return "We have created a password-less account in order to subscribe you to the Newsletter. Visit https://www.heatbud.com/do/forgot-password if you want to set a password.";

    }

    /**************************************************************************
     *************************  FORGOT PASSWORD  ******************************
     **************************************************************************/

    /**
     * Builds out forgot password page
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/forgot-password", method={RequestMethod.GET})
    public String doForgotPassword (ModelMap map) {
    	map.addAttribute("user", new User());
    	return "forgot-password";
    }

    /**
     * Processes Forgot Password form
     * @param user User object with user details
     * @param map spring model for the request
     */
    @RequestMapping(value="/do/forgot-password-submit", method={RequestMethod.POST})
    public ModelAndView doForgotPasswordSubmit (@ModelAttribute(value="user") User user, ModelMap map, HttpServletRequest request) {

    	try {
    		// query Users table
    		User userDB = dao.getUser(user.getUsername());
    		if ( userDB == null ) {
    			user.setError("Email address not registered.");
    		} else {
	    		// get Client IP Address
    			String ipAddress = request.getHeader("X-FORWARDED-FOR");
    			if ( StringUtils.isBlank(ipAddress) ) {  
    				ipAddress = request.getRemoteAddr();
    			}

    			if ( StringUtils.indexOfAny(ipAddress, common.blockedIPs) != -1 ) {
        	        logger.log(Level.SEVERE,"Blocked IP Address is attempting to reset password: " + ipAddress );
        			user.setError("You have been blocked from resetting the password.");
           	    	map.addAttribute("user", user);
           			return new ModelAndView("forgot-password", map);
    			}

	           	// Send Email with salt as verification code
	   			ses.sendForgotPasswordEmail(userDB.getFirstName(), userDB.getUsername(), userDB.getSalt(), ipAddress);

	   			// Display message
	   			user.setError("Please check your email for instructions on how to reset your password.");
    		}

    	} catch (Exception e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
        	user.setError("Error sending email. Please try again.");
    	}
		return new ModelAndView("forgot-password", map);
    }

    /**
     * Builds out reset password page. Without .+, the trailing text ".com" toward end of the email will disappear
     * @param map spring model for the request
     * @param salt verification code
     * @param username email address
     */
    @RequestMapping(value="/reset-password/{salt}/{username:.+}", method={RequestMethod.GET})
    public ModelAndView doResetPassword (@PathVariable String salt, @PathVariable String username, ModelMap map) {

    	// we're not resetting the password in this method
    	// we're only building the reset password page
		User user = new User();
		user.setUsername(username);
		user.setSalt(salt);
		map.addAttribute("user", user);
		return new ModelAndView("reset-password", map);

    }

    /**
     * Processes reset password page
     * @param user User object with user details
     * @param map spring model for the request
     * @param password2 re-enter'ed password
     */
    @RequestMapping(value="/do/reset-password-submit", method={RequestMethod.POST})
    public ModelAndView doResetPasswordSubmit (@ModelAttribute(value="user") User user, ModelMap map) {

		User userDB = null;

		try {
    		// Validate email address
    		if (StringUtils.isBlank(user.getUsername())) {
    			user.setError("Email Address cannot be blank.");
            }

			// Trim password of white spaces
			user.setPassword(user.getPassword().trim());

    		// Validate password
            String validateMessage = common.validatePassword(user.getPassword());
            if ( validateMessage != "SUCCESS" ) {
            	user.setError(validateMessage);
            }

    		if (StringUtils.isBlank(user.getError())) {
	    		// Query Users table
	    		userDB = dao.getUser(user.getUsername());
	    		if ( userDB == null ) {
	    			user.setError("Email Address not registered.");
	    		} else {
	    			// check if verification code matches
	    			if (!StringUtils.equals(userDB.getSalt(), user.getSalt())) {
	    				user.setError("Verification code and Email address do not match.");
	    			} else {
	    	        	// Encode user password
	    	        	ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);
	    	        	String encodedPassword = encoder.encodePassword(user.getPassword(),user.getSalt());
	    	        	userDB.setPassword(encodedPassword);

	    	        	if (!userDB.isEnabled()) {
	    	        		// user is attempting to reset password even before verifying the email address
	    	        		// it's okay, we will enable the user now and send welcome email
	    	        		userDB.setRole("ROLE_USER");
	    	        		ses.sendWelcomeEmail(userDB.getFirstName(), userDB.getUsername(), userDB.getUserId());
	    		        	// Enable Email Me
	    		        	dao.updateEnableEmail(userDB.getUserId(), "Y");
	    		        	// Create notification flags
	    		        	dao.createNotificationFlags(userDB.getUsername());
	    		        	// Insert into Search
	    					dao.putBloggerSearch(userDB.getUserId(), userDB.getFirstName()+" "+userDB.getLastName());
	    		        	// Insert into TopCharts table for Ticker
	    					dao.putSignupTicker(userDB.getUserId());
	    	        	}

	    	        	// save user object with the new password
	    	        	dao.saveUser(userDB);

	    	            // redirect user to the login page
	    	        	map.addAttribute("error", "Password reset. Please login to continue.");
	    				map.addAttribute("username", user.getUsername());
	    	            return new ModelAndView("redirect:login", map);
	    			}
	    		}
    		}

		} catch (Exception e) {
			logger.log(Level.SEVERE,e.getMessage(),e);
		}

		// errors found. return user to the reset password page
		map.addAttribute("user", user);
		return new ModelAndView("reset-password", map);

    }

    /**************************************************************************
     ************************  SETTINGS PAGE  *********************************
     **************************************************************************/

    /**
     * Builds the settings page
     */
	@RequestMapping(value="/user/settings", method={RequestMethod.GET})
	public void doSettings (ModelMap map) {

		String userId = null;
		try {
	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Add Users data to map ***/
			map.addAttribute("userId", userId);
			map.addAttribute("firstName", user.getFirstName());
			map.addAttribute("lastName", user.getLastName());
			map.addAttribute("emailAddress", user.getUsername());

   			/*** Add Bloggers data to map ***/
			Entity blogger = dao.getEntity(userId);
	    	map.addAttribute("fbId", blogger.getFbId());
	    	map.addAttribute("googleId", blogger.getGoogleId());

   			/*** Query CommentFollowers count ***/
   			int countFC = dao.getCFCount(user.getUsername());
	    	map.addAttribute("countFC", countFC);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/settings");
	        logger.log(Level.SEVERE,"userId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

	/**
     * Updates First Name in Users table.
     * @param firstName First Name
     * @return Errors, if any
     */
    @RequestMapping(value="/user/update-first-name", method={RequestMethod.POST})
    public @ResponseBody String doUpdateFirstName (String firstName) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();
			
	    	/*** Validate First Name ***/
			String firstNameDecoded = URLDecoder.decode(firstName,"UTF-8");
			if ( StringUtils.isBlank(firstNameDecoded) ) {
				return "First Name can not be Blank.";
			}

	    	/*** Save First Name in the database ***/
			user.setFirstName(firstNameDecoded);
			dao.saveUser(user);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + "First Name: " + firstName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "First Name could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates Last Name in Users table.
     * @param lastName Last Name
     * @return Errors, if any
     */
    @RequestMapping(value="/user/update-last-name", method={RequestMethod.POST})
    public @ResponseBody String doUpdateLastName (String lastName) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Validate Last Name ***/
			String lastNameDecoded = URLDecoder.decode(lastName,"UTF-8");
			if ( StringUtils.isBlank(lastNameDecoded) ) {
				return "Last Name can not be Blank.";
			}

			/*** Save Last Name in the database ***/
			user.setLastName(lastNameDecoded);
			dao.saveUser(user);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/update-last-name");
	        logger.log(Level.SEVERE,"UserId: " + userId + "Last Name: " + lastName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Last Name could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates EntityName in Entities and Posts tables.
     * @param entityId Entity Id (userId or pageId)
     * @param entityName Entity Name
     * @return Errors, if any
     */
    @RequestMapping(value="/user/update-entity-name", method={RequestMethod.POST})
    public @ResponseBody String doUpdateEntityName (String entityId, String entityName) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

    		/*** Check if user is allowed to edit the entity ***/
    		if ( !StringUtils.equals(userId, entityId) ) {
        		boolean isPageBlogger = dao.isPageBlogger (entityId, userId);
        		if ( !isPageBlogger ) {
           			return "You are not authorized to edit this page.";
        		}
    		}

	    	/*** Validate Entity Name ***/
			String entityNameDecoded = URLDecoder.decode(entityName,"UTF-8");
			if ( StringUtils.isBlank(entityNameDecoded) ) {
				return "Name can not be blank.";
			}

	    	/*** Save Entity Name in the database ***/
    		if ( StringUtils.equals(userId, entityId) ) {
    			dao.updateEntityName(entityId, "B", entityNameDecoded);
    		} else {
    			dao.updateEntityName(entityId, "P", entityNameDecoded);
   			}

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " Entity Name: " + entityName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Name could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates Email Address in Users, Entities, CommentFollowers and Notifications tables.
     * @param entityId entityId of the blogger or page
     * @param emailAddress Email Address
     * @return Errors, if any
     */
    @RequestMapping(value="/user/update-email-address", method={RequestMethod.POST})
    public @ResponseBody String doUpdateEmailAddress (String entityId, String emailAddress) {

    	String currentEmailAddress = null;
    	try {
	    	/*** Get user object from Spring Security context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String userId = user.getUserId();

	    	/*** If input entityId is null, take userId ***/
			if ( StringUtils.equals(entityId, "null") || StringUtils.isBlank(entityId) ) {
				entityId = userId;
			}

    		/*** Check if user is allowed to edit the entity ***/
    		if ( !StringUtils.equals(userId, entityId) ) {
        		boolean isPageBlogger = dao.isPageBlogger (entityId, userId);
        		if ( !isPageBlogger ) {
           			return "You are not authorized to edit this page.";
        		}
    		}

			/*** Decode new Email Address and Convert it to Lower Case ***/
			String newEmailAddress = null;
			try {
				newEmailAddress = StringUtils.lowerCase(URLDecoder.decode(emailAddress.trim(),"UTF-8"));
			} catch (Exception e) {
				return "Email Address is not valid.";
			}

    		/*** Validate new email address ***/
			String valMessage = common.validateEmailAddress(newEmailAddress);
    		if ( valMessage != "SUCCESS" ) {
    			if ( StringUtils.equals(valMessage, "Vitrual email addresses are not allowed." ) ) {
           	        logger.log(Level.SEVERE,"Blocked Email Domain is attempting to update settings: " + user.getUsername() );
    			}
    			return valMessage;
    		} else {
       			User userDB = dao.getUser(newEmailAddress);
           		if ( userDB != null ) {
           			return "Email Address already registered.";
           		}
    		}

	    	/*** Update Entities table ***/
			dao.updateEntityEmail(entityId, newEmailAddress);

			/*** Update Users, CommentFollowers and Notifications tables ***/
    		// Applicable only for Bloggers, not for Pages
    		if ( StringUtils.equals(userId, entityId) ) {

    			/*** Update Email Address in Users table ***/
    			// save current email address for updating other tables
				currentEmailAddress = user.getUsername();
				// email address is the hash key - so this update creates a new record
				user.setUsername(newEmailAddress);
				dao.saveUser(user);
				// delete old record
				dao.deleteUser(currentEmailAddress);

		    	/*** Update CommentFollowers table ***/
				dao.updateCFEmail(currentEmailAddress, newEmailAddress);

		    	/*** Update Notifications table ***/
				// email address is the hash key - so this update creates a new record
				dao.updateNotificationsEmail(currentEmailAddress, newEmailAddress);
				// delete old record
				dao.deleteNotificationFlags(currentEmailAddress);

    		}

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Current Email Address: " + currentEmailAddress + " New Email Address: " + emailAddress);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Email Address could not be updated. Please try again.";
		}

   		return "SUCCESS";
    }

	/**
     * Changes user's password.
     * @param currentPassword Current Password
     * @param newPassword New Password
     * @return The literal string SUCCESS for success, or the actual message for Errors
     */
    @RequestMapping(value="/user/change-password", method={RequestMethod.POST})
    public @ResponseBody String doChangePassword (String currentPassword, String newPassword) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			// Trim passwords of white spaces
			currentPassword = currentPassword.trim();
			newPassword = newPassword.trim();

			// Encode current password and check if it matches
        	ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);
        	String encodedPassword = encoder.encodePassword(currentPassword,user.getSalt());
            if ( !StringUtils.equals(encodedPassword,user.getPassword()) ) {
            	return "Current Password does not match.";
            }

        	// Validate new password
            String validateMessage = common.validatePassword(newPassword);
            if ( validateMessage != "SUCCESS" ) {
            	return validateMessage;
            }

        	// Check if New Password is different from the Current Password
            if ( StringUtils.equals(newPassword, currentPassword) ) {
            	return "New Password cannot be the same as the Current Password.";
            }

            // Encode and save new password
        	encodedPassword = encoder.encodePassword(newPassword,user.getSalt());
        	user.setPassword(encodedPassword);
			dao.saveUser(user);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/change-password");
	        logger.log(Level.SEVERE,"UserId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Password could not be changed. Please try again.";
		}

   		return "SUCCESS";
    }

	/**
     * Updates Facebook Id in Bloggers table.
     * @param fbId Facebook Id
     * @return Errors, if any
     */
    @RequestMapping(value="/user/update-fb-id", method={RequestMethod.POST})
    public @ResponseBody String doUpdateFbId (String fbId) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Decode Facebook Id ***/
			String fbIdDecoded = URLDecoder.decode(fbId,"UTF-8");

			/*** Save Facebook Id in the database ***/
			dao.updateFbId(userId, fbIdDecoded);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/update-fb-id");
	        logger.log(Level.SEVERE,"UserId: " + userId + "Facebook Id: " + fbId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Facebook Id could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

	/**
     * Updates Google Id in Bloggers table.
     * @param googleId Google Id
     * @return Errors, if any
     */
    @RequestMapping(value="/user/update-google-id", method={RequestMethod.POST})
    public @ResponseBody String doUpdateGoogleId (String googleId) {

    	String userId = null;
    	try {
	    	/*** Get UserId from Spring Security Context ***/
    		/*** For Security, never accept userId from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

	    	/*** Decode Google Id ***/
			String googleIdDecoded = URLDecoder.decode(googleId,"UTF-8");

			/*** Save Google Id in the database ***/
			dao.updateGoogleId(userId, googleIdDecoded);

 		} catch (Exception e) {
	        logger.log(Level.SEVERE,"/user/update-google-id");
	        logger.log(Level.SEVERE,"UserId: " + userId + "Google Id: " + googleId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Google Id could not be updated. Please try again.";
		}
   		return "SUCCESS";
    }

    /**************************************************************************
     *********************  DROP ACCOUNT PAGE  ********************************
     **************************************************************************/

    /**
     * Builds the drop account page
     */
	@RequestMapping(value="/user/drop", method={RequestMethod.GET})
	public void doDropAccount (ModelMap map) {

		String userId = null;
		try {

			/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

    /**
     * Drop a given user account
     * @return ERROR message, if any
     */
    @RequestMapping(value="/user/drop", method={RequestMethod.POST})
    public @ResponseBody String doDropAccount () {

    	String username = null;
    	try {

	    	/*** Get Email Address from Spring Security Context ***/
    		/*** For Security, never accept Email Address from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			username = user.getUsername();

   			/*** Register dropAccount in SQS to be processed by cron job ***/
			JSONObject msgBody = new JSONObject();
			msgBody.put("category", "dropAccount");
			msgBody.put("username", username);
			msgBody.put("userId", user.getUserId());
			sqs.sendMessage (
				new SendMessageRequest()
					.withQueueUrl( config.getProperty("processQueueSQS") )
					.withMessageBody(msgBody.toString())
			);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Username: " + username);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Error processing your request. Please try again.";
		}
   		return "SUCCESS";

    }

    /**
     * Builds the convert page
     */
	@RequestMapping(value="/user/convert", method={RequestMethod.GET})
	public void doConvert (ModelMap map) {

		String userId = null;
		try {
	    	/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Add User data to map ***/
			map.addAttribute("user", user);

   			/*** Add Blogger data to map ***/
			Entity blogger = dao.getEntity(userId);
	    	map.addAttribute("blogger", blogger);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

    /**
     * Convert business account into human account
     * @return ERROR message, if any
     */
    @RequestMapping(value="/user/convert-submit", method={RequestMethod.POST})
    public @ResponseBody String doConvertSubmit (
    		String bloggerUsername, String bloggerFirstname, String bloggerLastname, String bloggerURL,
    		String bloggerAbout, String bloggerContact, String bloggerFbId, String bloggerGoogleId,
    		String pageName, String pageAbout, String pageContact, String pageFbId, String pageGoogleId
    	) {

    	String username = null;
    	try {

    		// Current Users data (this email id will be converted to page)
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			username = user.getUsername();

    		// Validate email address
			String valMessage = common.validateEmailAddress(bloggerUsername);
    		if ( valMessage != "SUCCESS" ) {
    			if ( StringUtils.equals(valMessage, "Vitrual email addresses are not allowed." ) ) {
           	        logger.log(Level.SEVERE,"Blocked Email Domain is attempting to convert account: " + user.getUsername() );
    			}
	    		return valMessage;
    		}

            /*** Convert Entities table for Blogger ***/
			Map<String, AttributeValue> itemE = new HashMap<String, AttributeValue>();
			itemE.put("EntityId", new AttributeValue().withS(bloggerURL));
			itemE.put("EntityType", new AttributeValue().withS("B"));
			itemE.put("EntityName", new AttributeValue().withS(bloggerFirstname+" "+bloggerLastname));
			itemE.put("EntityEmail", new AttributeValue().withS(bloggerUsername));
			itemE.put("About", new AttributeValue().withS(bloggerAbout+" "));
			itemE.put("Contact", new AttributeValue().withS(bloggerContact+" "));
			itemE.put("EnableEmail", new AttributeValue().withS("N"));
			if ( StringUtils.isNotBlank(bloggerFbId) ) itemE.put("FbId", new AttributeValue().withS(bloggerFbId));
			if ( StringUtils.isNotBlank(bloggerGoogleId) ) itemE.put("GoogleId", new AttributeValue().withS(bloggerGoogleId));

			/*** condition to expect - entityId shouldn't already exist in the database ***/
	        Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
	        expected.put("EntityId", new ExpectedAttributeValue().withExists(false));

	        /*** request to insert ***/
	        PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("Entities")
		    	.withItem(itemE)
		    	.withExpected(expected);

	        /*** Return error if the entityId already exists ***/
	    	try {
	    		dynamoDBClient.putItem(putItemRequest);
	    	} catch (ConditionalCheckFailedException e) {
	    		return "Blog Site URL not available. Please choose another one.";
	    	}

            /*** Convert Entities table for Page ***/
            HashMap<String, AttributeValue> keyUpdateE1 = new HashMap<String, AttributeValue>();
            keyUpdateE1.put("EntityId", new AttributeValue().withS(user.getUserId()));

			Map<String, AttributeValueUpdate> updateItemsE1 = new HashMap<String, AttributeValueUpdate>();
		    updateItemsE1.put("EntityType",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS("P"))
		    		.withAction(AttributeAction.PUT)
		    	);
		    updateItemsE1.put("EntityName",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(pageName))
		    		.withAction(AttributeAction.PUT)
		    	);
		    updateItemsE1.put("About",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(pageAbout+" "))
		    		.withAction(AttributeAction.PUT)
		    	);
		    updateItemsE1.put("Contact",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(pageContact+" "))
	              	.withAction(AttributeAction.PUT)
		    	);
			if ( StringUtils.isNotBlank(bloggerFbId) )
			    updateItemsE1.put("FbId",
			    	new AttributeValueUpdate()
			    		.withValue(new AttributeValue().withS(pageFbId))
			    		.withAction(AttributeAction.PUT)
			    	);
			if ( StringUtils.isNotBlank(bloggerGoogleId) )
			    updateItemsE1.put("GoogleId",
			    	new AttributeValueUpdate()
			    		.withValue(new AttributeValue().withS(pageGoogleId))
			    		.withAction(AttributeAction.PUT)
			    	);
		    updateItemsE1.put("EnableEmail",
		    	new AttributeValueUpdate()
		               .withAction(AttributeAction.DELETE)
			   	);
		    updateItemsE1.put("",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
		    	);

		    UpdateItemRequest requestE1 = new UpdateItemRequest()
            	.withTableName("Entities")
            	.withKey(keyUpdateE1)
            	.withAttributeUpdates(updateItemsE1);
            dynamoDBClient.updateItem(requestE1);

            /*** Convert Zones table ***/
			// Prepare key for delete
			HashMap<String, AttributeValue> keyZ = new HashMap<String, AttributeValue>();
			keyZ.put("UserId", new AttributeValue().withS(user.getUserId()));

			// Conditions for Zones table
			HashMap<String, Condition> conditionsZ = new HashMap<String, Condition>();
			conditionsZ.put("UserId", new Condition()
				.withComparisonOperator(ComparisonOperator.EQ)
				.withAttributeValueList(new AttributeValue().withS(user.getUserId())));

			Map<String, AttributeValue> lastEvaluatedKeyZ = null;
			do {
				// Query Zones table
				QueryRequest requestZ = new QueryRequest()
					.withTableName("Zones")
					.withKeyConditions(conditionsZ)
					.withExclusiveStartKey(lastEvaluatedKeyZ);
				QueryResult resultZ = dynamoDBClient.query(requestZ);

				// Loop through each id
				for (Map<String, AttributeValue> itemZ : resultZ.getItems()) {

					// Delete current record
					keyZ.put("ZoneId", itemZ.get("ZoneId"));
					DeleteItemRequest deleteItemRequestZ = new DeleteItemRequest()
						.withTableName("Zones")
						.withKey(keyZ);
					dynamoDBClient.deleteItem(deleteItemRequestZ);

					// Add new record
					itemZ.put("UserId", new AttributeValue().withS(bloggerURL));
					PutItemRequest putItemRequestZ = new PutItemRequest()
						.withTableName("Zones")
						.withItem(itemZ);
					dynamoDBClient.putItem(putItemRequestZ);

		            /*** Convert RList table ***/
					// Prepare key for delete
					HashMap<String, AttributeValue> keyRL = new HashMap<String, AttributeValue>();
					keyRL.put("UserId-ZoneId", new AttributeValue().withS(user.getUserId()+"-"+itemZ.get("ZoneId").getS()));

					// Conditions for RList table
					HashMap<String, Condition> conditionsRL = new HashMap<String, Condition>();
					conditionsRL.put("UserId-ZoneId", new Condition()
						.withComparisonOperator(ComparisonOperator.EQ)
						.withAttributeValueList(new AttributeValue().withS(user.getUserId()+"-"+itemZ.get("ZoneId").getS())));

					Map<String, AttributeValue> lastEvaluatedKeyRL = null;
					do {
						// Query RList table
						QueryRequest requestRL = new QueryRequest()
							.withTableName("RList")
							.withKeyConditions(conditionsRL)
							.withExclusiveStartKey(lastEvaluatedKeyRL);
						QueryResult resultRL = dynamoDBClient.query(requestRL);

						// Loop through each id
						for (Map<String, AttributeValue> itemRL : resultRL.getItems()) {

							// Delete current record
							keyRL.put("RO", itemRL.get("RO"));
							DeleteItemRequest deleteItemRequestRL = new DeleteItemRequest()
								.withTableName("RList")
								.withKey(keyRL);
							dynamoDBClient.deleteItem(deleteItemRequestRL);

							// Add new record
							itemRL.put("UserId-ZoneId", new AttributeValue().withS(bloggerURL+"-"+itemZ.get("ZoneId").getS()));
							PutItemRequest putItemRequestRL = new PutItemRequest()
								.withTableName("RList")
								.withItem(itemRL);
							dynamoDBClient.putItem(putItemRequestRL);

						}

						lastEvaluatedKeyRL = resultRL.getLastEvaluatedKey();
						Thread.sleep(5000);
					} while (lastEvaluatedKeyRL != null);
					
				}

				lastEvaluatedKeyZ = resultZ.getLastEvaluatedKey();
				Thread.sleep(5000);
			} while (lastEvaluatedKeyZ != null);

   			/*** Convert TopCharts table ***/
			// Prepare key for delete
			HashMap<String, AttributeValue> keyT = new HashMap<String, AttributeValue>();
			keyT.put("Name", new AttributeValue().withS("B"));
			keyT.put("Id", new AttributeValue().withS(user.getUserId()));

			// Conditions for TopCharts table
			HashMap<String, Condition> conditionsT = new HashMap<String, Condition>();
			conditionsT.put("Name", new Condition()
				.withComparisonOperator(ComparisonOperator.EQ)
				.withAttributeValueList(new AttributeValue().withS("B")));
			conditionsT.put("Id", new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(user.getUserId())));

			Map<String, AttributeValue> lastEvaluatedKeyT = null;
			do {
				// Query TopCharts table
				QueryRequest requestT = new QueryRequest()
					.withTableName("TopCharts")
					.withKeyConditions(conditionsT)
					.withExclusiveStartKey(lastEvaluatedKeyT);
				QueryResult resultT = dynamoDBClient.query(requestT);

				// Loop through each id (there should be only one record for TopCharts)
				for (Map<String, AttributeValue> itemT : resultT.getItems()) {

					// Delete current record
					DeleteItemRequest deleteItemRequestT = new DeleteItemRequest()
						.withTableName("TopCharts")
						.withKey(keyT);
					dynamoDBClient.deleteItem(deleteItemRequestT);

					// Add new record
					itemT.put("Id", new AttributeValue().withS(bloggerURL));
					PutItemRequest putItemRequestT = new PutItemRequest()
						.withTableName("TopCharts")
						.withItem(itemT);
					dynamoDBClient.putItem(putItemRequestT);

					// Add new record for the Page
					Map<String, AttributeValue> itemTPI = new HashMap<String, AttributeValue>();
					itemTPI.put("Name", new AttributeValue().withS("PA"));
					itemTPI.put("Id", new AttributeValue().withS(user.getUserId()));
					itemTPI.put("HI", itemT.get("HI"));
					PutItemRequest putItemRequestTPI = new PutItemRequest()
						.withTableName("TopCharts")
						.withItem(itemTPI);
					dynamoDBClient.putItem(putItemRequestTPI);

				}

				lastEvaluatedKeyT = resultT.getLastEvaluatedKey();
			} while (lastEvaluatedKeyT != null);

			/*** Insert into PageBloggers table ***/
			Map<String, AttributeValue> itemPB = new HashMap<String, AttributeValue>();
			itemPB.put("PageId", new AttributeValue().withS(user.getUserId()));
			itemPB.put("Position", new AttributeValue().withN("0"));
			itemPB.put("BloggerId", new AttributeValue().withS(bloggerURL));
			itemPB.put("BloggerName", new AttributeValue().withS(bloggerFirstname+" "+bloggerLastname));
			itemPB.put("AdminFlag", new AttributeValue().withS("Y"));
			PutItemRequest putItemRequestPB = new PutItemRequest()
				.withTableName("PageBloggers")
				.withItem(itemPB);
			dynamoDBClient.putItem(putItemRequestPB);

   			/*** Convert PostsByEntity table ***/
		    for(String publishFlag : Arrays.asList("N", "P", "D")) {

		    	// Prepare key for delete
				HashMap<String, AttributeValue> keyPE = new HashMap<String, AttributeValue>();
				keyPE.put("EntityId-PublishFlag", new AttributeValue().withS(user.getUserId()+"-"+publishFlag));

				// Conditions for PostsByEntity table
				HashMap<String, Condition> conditionsPE = new HashMap<String, Condition>();
				conditionsPE.put("EntityId-PublishFlag", new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(user.getUserId()+"-"+publishFlag)));

				Map<String, AttributeValue> lastEvaluatedKeyPE = null;
				do {
					// Query PostsByEntity table
					QueryRequest requestPE = new QueryRequest()
						.withTableName("PostsByEntity")
						.withKeyConditions(conditionsPE)
						.withExclusiveStartKey(lastEvaluatedKeyPE);
					QueryResult resultPE = dynamoDBClient.query(requestPE);
	
					// Loop through each id
					for (Map<String, AttributeValue> itemPE : resultPE.getItems()) {

						if ( !StringUtils.equals(publishFlag, "P") ) {
							// Delete current record
							keyPE.put("PostId", itemPE.get("PostId"));
							DeleteItemRequest deleteItemRequestPE = new DeleteItemRequest()
								.withTableName("PostsByEntity")
								.withKey(keyPE);
							dynamoDBClient.deleteItem(deleteItemRequestPE);
						}

						// Copy record for Blogger
						Map<String, AttributeValue> itemPEI = new HashMap<String, AttributeValue>();
						itemPEI.put("EntityId-PublishFlag", new AttributeValue().withS(bloggerURL+"-"+publishFlag));
						itemPEI.put("PostId", itemPE.get("PostId"));
						itemPEI.put("UpdateDate", itemPE.get("UpdateDate"));
						PutItemRequest putItemRequestPEI = new PutItemRequest()
							.withTableName("TopCharts")
							.withItem(itemPEI);
						dynamoDBClient.putItem(putItemRequestPEI);

						// Update Posts table
			            HashMap<String, AttributeValue> keyUpdateP = new HashMap<String, AttributeValue>();
			            keyUpdateP.put("PostId", itemPE.get("PostId"));

						Map<String, AttributeValueUpdate> updateItemsP = new HashMap<String, AttributeValueUpdate>();
					    updateItemsP.put("PageId",
					    	new AttributeValueUpdate()
					    		.withValue(new AttributeValue().withS(user.getUserId()))
					    		.withAction(AttributeAction.PUT)
					    	);
					    updateItemsP.put("PageName",
					    	new AttributeValueUpdate()
					    		.withValue(new AttributeValue().withS(pageName))
					    		.withAction(AttributeAction.PUT)
					    	);
					    updateItemsP.put("BloggerId",
					    	new AttributeValueUpdate()
					    		.withValue(new AttributeValue().withS(bloggerURL))
					    		.withAction(AttributeAction.PUT)
					    	);
					    updateItemsP.put("BloggerName",
					    	new AttributeValueUpdate()
					    		.withValue(new AttributeValue().withS(bloggerFirstname+" "+bloggerLastname))
				              	.withAction(AttributeAction.PUT)
					    	);

					    UpdateItemRequest requestP = new UpdateItemRequest()
			            	.withTableName("Posts")
			            	.withKey(keyUpdateP)
			            	.withAttributeUpdates(updateItemsP);
			            dynamoDBClient.updateItem(requestP);

					}
	
					lastEvaluatedKeyPE = resultPE.getLastEvaluatedKey();
				} while (lastEvaluatedKeyPE != null);
		    }

   			/*** Convert Comments table ***/
			Map<String, AttributeValue> lastEvaluatedKeyC = null;
			do {

				ScanRequest scanRequestC = new ScanRequest()
					.withTableName("Comments")
					.withExclusiveStartKey(lastEvaluatedKeyC);
				ScanResult scanResultC = dynamoDBClient.scan(scanRequestC);

				for (Map<String, AttributeValue> itemC : scanResultC.getItems()) {

					itemC.put("CommenterId", new AttributeValue().withS(bloggerURL));
					PutItemRequest putItemRequestC = new PutItemRequest()
						.withTableName("Comments")
						.withItem(itemC);
					dynamoDBClient.putItem(putItemRequestC);

				}

				lastEvaluatedKeyC = scanResultC.getLastEvaluatedKey();
			} while (lastEvaluatedKeyC != null);

   			/*** Convert CommentFollowers table ***/
			// Conditions for CommentFollowers table
			HashMap<String, Condition> conditionsCF = new HashMap<String, Condition>();
			conditionsCF.put("Email", new Condition()
				.withComparisonOperator(ComparisonOperator.EQ)
				.withAttributeValueList(new AttributeValue().withS(user.getUsername())));

			Map<String, AttributeValue> lastEvaluatedKeyCF = null;
			do {
				// Query CommentFollowers table
				QueryRequest requestCF = new QueryRequest()
					.withTableName("CommentFollowers")
					.withIndexName("CFEmailIdx")
					.withKeyConditions(conditionsCF)
					.withExclusiveStartKey(lastEvaluatedKeyCF);
				QueryResult resultCF = dynamoDBClient.query(requestCF);

				// Loop through each id
				for (Map<String, AttributeValue> itemCF : resultCF.getItems()) {

					// Add new record
					itemCF.put("Email", new AttributeValue().withS(bloggerUsername));
					PutItemRequest putItemRequestCF = new PutItemRequest()
						.withTableName("CommentFollowers")
						.withItem(itemCF);
					dynamoDBClient.putItem(putItemRequestCF);

				}

				lastEvaluatedKeyCF = resultCF.getLastEvaluatedKey();
				Thread.sleep(5000);
			} while (lastEvaluatedKeyCF != null);

            /*** Convert Notifications table ***/
			// Prepare key for delete
			HashMap<String, AttributeValue> keyN = new HashMap<String, AttributeValue>();
			keyN.put("Email", new AttributeValue().withS(user.getUsername()));

			// Conditions for Notifications table
			HashMap<String, Condition> conditionsN = new HashMap<String, Condition>();
			conditionsN.put("Email", new Condition()
				.withComparisonOperator(ComparisonOperator.EQ)
				.withAttributeValueList(new AttributeValue().withS(user.getUsername())));

			Map<String, AttributeValue> lastEvaluatedKeyN = null;
			do {
				// Query Notifications table
				QueryRequest requestN = new QueryRequest()
					.withTableName("Notifications")
					.withKeyConditions(conditionsN)
					.withExclusiveStartKey(lastEvaluatedKeyN);
				QueryResult resultN = dynamoDBClient.query(requestN);

				// Loop through each id
				for (Map<String, AttributeValue> itemN : resultN.getItems()) {

					// Delete current record
					keyN.put("Type", itemN.get("Type"));
					DeleteItemRequest deleteItemRequestN = new DeleteItemRequest()
						.withTableName("Notifications")
						.withKey(keyN);
					dynamoDBClient.deleteItem(deleteItemRequestN);

					// Add new record
					itemN.put("Email", new AttributeValue().withS(bloggerUsername));
					PutItemRequest putItemRequestN = new PutItemRequest()
						.withTableName("Notifications")
						.withItem(itemN);
					dynamoDBClient.putItem(putItemRequestN);
					
				}

				lastEvaluatedKeyN = resultN.getLastEvaluatedKey();
				Thread.sleep(5000);
			} while (lastEvaluatedKeyN != null);

			/*** Convert Users table ***/
    		// Insert Users record for the blogger
			user.setUsername(bloggerUsername);
			user.setFirstName(bloggerFirstname);
			user.setLastName(bloggerLastname);
			user.setUserId(bloggerURL);
        	user.setCreateDate(System.currentTimeMillis());
			dao.saveUser(user);

			// Delete existing Users record
			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("Username", new AttributeValue().withS(username));
			DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
				.withTableName("Users").withKey(key);
			dynamoDBClient.deleteItem(deleteItemRequest);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Error processing your request. Please try again.";
		}
   		return "SUCCESS";

    }

    /**************************************************************************
     *********************  NOTIFICATIONS PAGE  *******************************
     **************************************************************************/

    /**
     * Builds the notifications page
     */
	@RequestMapping(value="/user/notifications", method={RequestMethod.GET})
	public void doNotifications (ModelMap map) {

		String userId = null;
		try {

			/*** Get UserId from Spring Security Context ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			userId = user.getUserId();

			/*** Query Notification Flags ***/
			List<Map<String,AttributeValue>> flags = dao.getNotificationFlags(user.getUsername());
	   		for (Map<String,AttributeValue> item : flags) {
	   			map.addAttribute(item.get("Type").getS(), item.get("Flag").getS());
	   		}

   			/*** Query CommentFollowers count ***/
   			int countFC = dao.getCFCount(user.getUsername());
	    	map.addAttribute("countFC", countFC);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

    /**
     * Save notification flags
     * @return ERROR message, if any
     */
    @RequestMapping(value="/user/save-notification-flags", method={RequestMethod.POST})
    public @ResponseBody String doSaveNotifications (String followWhenPublished, String followWhenCommented, String notifyWhenThanked, String remindDraftPost, String weeklyNewsLetter) {

    	String email = null;
    	try {

	    	/*** Get Email Address from Spring Security Context ***/
    		/*** For Security, never accept Email Address from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			email = user.getUsername();

			/*** Save notification flags into the database ***/
			dao.saveNotificationFlags(email, followWhenPublished, followWhenCommented, notifyWhenThanked, remindDraftPost, weeklyNewsLetter);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Error saving your preferences. Please try again.";
		}
   		return "SUCCESS";

    }

    /**
     * Unfollow All Comments for a given user
     * @return ERROR message, if any
     */
    @RequestMapping(value="/user/unfollow-all-comments", method={RequestMethod.POST})
    public @ResponseBody String doUnfollowComments () {

    	String email = null;
    	try {

	    	/*** Get Email Address from Spring Security Context ***/
    		/*** For Security, never accept Email Address from HTML or JavaScript ***/
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			email = user.getUsername();

			/*** Delete from CommentFollowers table ***/
			dao.unfollowAllComments(email);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			return "Error processing your request. Please try again.";
		}
   		return "SUCCESS";

    }

}
