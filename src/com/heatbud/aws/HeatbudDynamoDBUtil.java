/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.aws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.UpdateTableRequest;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.heatbud.entity.Entity;
import com.heatbud.entity.MarketPricing;
import com.heatbud.entity.Order;
import com.heatbud.entity.PendingOrder;
import com.heatbud.entity.PendingPagePayment;
import com.heatbud.entity.Post;
import com.heatbud.entity.User;
import com.heatbud.util.Configuration;
import com.heatbud.util.HeatbudCommon;

public class HeatbudDynamoDBUtil {

	// Logger object
	private Logger logger = Logger.getLogger(HeatbudDynamoDBUtil.class.getName());
	// Heatbud properties
	private static Configuration config = Configuration.getInstance();
	// AWS credentials
	private static AWSCredentials creds = new BasicAWSCredentials(config.getProperty("accessKey"), config.getProperty("secretKey"));
    // Common Functions client
    @Autowired
	private HeatbudCommon common;
	// S3 Util
    @Autowired
	private HeatbudS3Util s3;
	// SES client
    @Autowired
	private HeatbudSESUtil ses;

	/*
	 * The DynamoDB client and mapper are thread safe so we only ever need one static instance each.
	 * While you can have multiple instances it is better to only have one each because they are
	 * relatively heavy weight classes.
	 */
    private static AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
    	.withEndpointConfiguration(new EndpointConfiguration("https://dynamodb.us-west-2.amazonaws.com", "us-west-2"))
       	.withCredentials(new AWSStaticCredentialsProvider(creds))
    	.build();
    private static DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);

    /**************************************************************************
     ************************** USER MODULE ***********************************
     **************************************************************************/

    /**
     * Saves user details into Users table.
     * @param user User object with user details
     */
    public void saveUser(User user) {
    	dynamoDBMapper.save(user);
    }

    /**
     * Generates UNIQUE EntityId based on the username and inserts into Entities table.
     * Suffixes a number if the entityId already exists in our system.
     * EntityId will also be inserted as UserId into Users table.
     * Since Username is the Email Address that should be kept as secure as possible,
     * EntityId/ UserId is used for UNIQUE identification of user across the system.
     * @param username Email address
     * @param entityName First name and Last name
     * @param createDate Create Date
     * @param fbId Facebook Id of the user
     * @param about About
     * @param country country name
     * @param state state name
     * @param city city name
     * @return generated entityId
     */
    public String generateEntityId (String username, String entityName, long createDate, String fbId,
    		String about, String country, String state, String city) {
        Boolean succeeded = false;
    	String entityId = null;
    	try {
    		/*** Generate EntityId from username and make it URL-friendly ***/
			try {
	    		// take left part of @ sign
				entityId = StringUtils.substring(username,0,StringUtils.indexOf(username,"@"));
				// replace all non-alphanumeric characters (including spaces) with dashes
				entityId = entityId.replaceAll("[^a-z0-9]","-");
				// trim two or more subsequent dashes into one
				entityId = entityId.trim().replaceAll("-+", "-");
				// remove the first character if it's a dash
				entityId = entityId.startsWith("-") ? entityId.substring(1) : entityId;
				// remove the last character if it's a dash
				entityId = entityId.endsWith("-") ? entityId.substring(0,entityId.length()-1) : entityId;
				// if the string is empty, generate a random id based on the system time
				if ( entityId.length() == 0 ) entityId = entityId + (long) System.currentTimeMillis()/21600000;
			} catch (Exception e) {
				entityId = entityId + (long) System.currentTimeMillis()/21600000;
			}

    		/*** item to insert ***/
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("EntityId", new AttributeValue().withS(entityId));
			item.put("EntityType", new AttributeValue().withS("B"));
			item.put("EntityName", new AttributeValue().withS(entityName));
			item.put("CreateDate", new AttributeValue().withN(createDate+""));
			item.put("EntityEmail", new AttributeValue().withS(username));
			item.put("EnableEmail", new AttributeValue().withS("N"));
			if ( StringUtils.isNotBlank(fbId) ) item.put("FbId", new AttributeValue().withS(fbId));
			if ( StringUtils.isNotBlank(about) ) item.put("About", new AttributeValue().withS(about));
			if ( StringUtils.isNotBlank(country) ) item.put("Country", new AttributeValue().withS(country));
			if ( StringUtils.isNotBlank(state) ) item.put("State", new AttributeValue().withS(state));
			if ( StringUtils.isNotBlank(city) ) item.put("City", new AttributeValue().withS(city));

			/*** condition to expect - entityId shouldn't already exist in the database ***/
	        Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
	        expected.put("EntityId", new ExpectedAttributeValue().withExists(false));

	        /*** request to insert ***/
	        PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("Entities")
		    	.withItem(item)
		    	.withExpected(expected);

	        /*** if the entityId already exists, suffix a number (in increments) and retry ***/
	        int i = 0;
	        String origEntityId = entityId; // save orig blogger id for retries
		    do {
		    	try {
		    		dynamoDBClient.putItem(putItemRequest);
		    		succeeded = true;
		    	} catch (ConditionalCheckFailedException e) {
		    		i++;
		    		entityId = origEntityId + i;
		    		item.put("EntityId", new AttributeValue().withS(entityId));
		    	}
		    } while (!succeeded && i<100); // fail after 100 times!

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"username=" + username + " entityName=" + entityName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
	    if (succeeded)
	    	return entityId;
	    else {
	        logger.log(Level.SEVERE,"Failed to generate entityId for " + username);
	    	return null;
	    }
    }

    /**
     * Creates an Entity record with the requested EntityId.
     * Returns error if requested EntityId already exists in our system.
     * EntityId = UserId of the user OR PageId of the page
     * UserId is also called BloggerId, CommenterId, ApproverId etc at other places depending on the context.
     * Since Username is the Email Address that should be kept as secure as possible,
     * EntityId is used for UNIQUE identification used across the system.
     * @param entityId User-requested Id for Blogger or Page URL
     * @param entityType B=Blogger; P=Page
     * @param entityEmail Email address
     * @param entityName Name of the blogger or page
     * @param about About information of the entity
     * @param fbId Facebook Id of the user or page
     * @return SUCCESS or Error Message
     */
    public String insertEntity (String entityId, String entityType, String entityEmail, String phone,
    	String entityName, String about, String fbId, String country, String state, String city) {

    	try {
    		/*** item to insert ***/
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("EntityId", new AttributeValue().withS(entityId));
			item.put("EntityType", new AttributeValue().withS(entityType));
			item.put("EntityName", new AttributeValue().withS(entityName));
			item.put("EntityEmail", new AttributeValue().withS(entityEmail));
			if ( StringUtils.isNotBlank(phone) ) item.put("Phone", new AttributeValue().withS(phone));
			item.put("CreateDate", new AttributeValue().withN(System.currentTimeMillis()+""));
            item.put("HI", new AttributeValue().withN("0"));
			if ( StringUtils.equals(entityType, "B") ) {
				item.put("EnableEmail", new AttributeValue().withS("N"));
			} else {
				item.put("EnableEmail", new AttributeValue().withS("Y"));
			}
			if ( StringUtils.isNotBlank(about) ) item.put("About", new AttributeValue().withS(about));
			if ( StringUtils.isNotBlank(fbId) ) item.put("FbId", new AttributeValue().withS(fbId));
			if ( StringUtils.isNotBlank(country) ) item.put("Country", new AttributeValue().withS(country));
			if ( StringUtils.isNotBlank(state) ) item.put("State", new AttributeValue().withS(state));
			if ( StringUtils.isNotBlank(city) ) item.put("City", new AttributeValue().withS(city));

			/*** condition to expect - entityId shouldn't already exist in the database ***/
	        Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
	        expected.put("EntityId", new ExpectedAttributeValue().withExists(false));

	        /*** request to insert ***/
	        PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("Entities")
		    	.withItem(item)
		    	.withExpected(expected);

	        /*** Return error if entityId already exists ***/
	    	try {
	    		dynamoDBClient.putItem(putItemRequest);
	    	} catch (ConditionalCheckFailedException e) {
	    		return "Page URL not available. Please choose another one.";
	    	}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " entityEmail=" + entityEmail + " entityName=" + entityName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
    	return "SUCCESS";

    }

    /**
     * Gets Entity Type for a given entityId from the Entities table.
     * @param entityId entityId
     * @return Entity Type
     */
	public String getEntityType (String entityId) {
		String entityType = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key)
			    .withAttributesToGet("EntityType");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			entityType = result.getItem().get("EntityType").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return entityType;
    }

    /**
     * Gets Entity Email for a given entityId from the Entities table.
     * @param entityId entityId
     * @return Entity Email Address
     */
	public String getEntityEmail (String entityId) {
		String entityEmail = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key)
			    .withAttributesToGet("EntityEmail");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			entityEmail = result.getItem().get("EntityEmail").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return entityEmail;
    }

    /**
     * Gets About for a given entityId from the Entities table.
     * @param entityId entityId
     * @return about About
     */
	public String getEntityAbout(String entityId) {
		String about = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key)
			    .withAttributesToGet("About");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			about = result.getItem().get("About").getS();

		} catch (NullPointerException npe) {
	        return about; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return about;
    }

    /**
     * Gets Entity ProfilePhoto for a given entityId from the Entities table.
     * @param entityId entityId
     * @return ProfilePhoto URL of the profile photo
     */
	public String getEntityProfilePhoto(String entityId) {
		String entityProfilePhoto = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key)
			    .withAttributesToGet("ProfilePhoto");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			entityProfilePhoto = result.getItem().get("ProfilePhoto").getS();

		} catch (NullPointerException npe) {
	        return entityProfilePhoto; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return entityProfilePhoto;
    }

    /**
     * Gets EnableEmail for a given entityId from the Entities table.
     * @param entityId entityId
     * @return EnableEmail Y or N indicating if the entity wants to receive emails from other bloggers
     */
	public String getEntityEnableEmail (String entityId) {
		String enableEmail = "N"; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key)
			    .withAttributesToGet("EnableEmail");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			enableEmail = result.getItem().get("EnableEmail").getS();

		} catch (NullPointerException npe) {
	        return enableEmail; // value doesn't exist, so return default value ("N")

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return enableEmail;
    }

    /**
     * Gets Primary Page Id for a given entityId from the Entities table.
     * @param entityId entityId
     * @return Primary Page Id
     */
	public String getPrimaryPageId (String entityId) {
		String entityPPId = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key)
			    .withAttributesToGet("PrimaryPageId");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			entityPPId = result.getItem().get("PrimaryPageId").getS();

		} catch (NullPointerException npe) {
	        return entityPPId; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return entityPPId;
    }

    /**
     * Checks if given bloggerId is one of the page bloggers for pageId
     * @param pageId Page Id
     * @param bloggerId Blogger Id
     * @return true or false
     */
	public boolean isPageBlogger (String pageId, String bloggerId) {

		boolean isPageBlogger = false; // default
		try {

	    	// Conditions for PageBloggers table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PageId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(pageId)));
			conditions.put("BloggerId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(bloggerId)));
			// Query request
	    	QueryRequest queryRequest = new QueryRequest()
                .withTableName("PageBloggers")
                .withKeyConditions(conditions)
                .withAttributesToGet("BloggerId");

		    try {
		        QueryResult result = dynamoDBClient.query(queryRequest);
		        if ( !result.getItems().isEmpty() ) isPageBlogger = true;
		    } catch (Exception e) {}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId=" + pageId + " bloggerId=" + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        isPageBlogger = false;
	    }
        return isPageBlogger;
    }

    /**
     * Checks if given bloggerId is one of the page admins for pageId
     * @param pageId Page Id
     * @param bloggerId Blogger Id
     * @return true or false
     */
	public boolean isPageAdmin (String pageId, String bloggerId) {

		boolean isPageAdmin = false; // default
		try {

	    	// Conditions for PageBloggers table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PageId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(pageId)));
			conditions.put("BloggerId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(bloggerId)));
			// Query request
	    	QueryRequest queryRequest = new QueryRequest()
                .withTableName("PageBloggers")
                .withKeyConditions(conditions)
                .withAttributesToGet("AdminFlag");

		    try {
		        QueryResult result = dynamoDBClient.query(queryRequest);
		        if ( StringUtils.equals(result.getItems().get(0).get("AdminFlag").getS(),"Y") ) {
		        	isPageAdmin = true;
		        }
		    } catch (Exception e) {}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId=" + pageId + " bloggerId=" + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        isPageAdmin = false;
	    }
        return isPageAdmin;
    }

	/**
     * Queries user details from Users table.
     * @param username username
     * @return User object with user details
     */
	public User getUser(String username) {
		User user = null; // default
		try {
			if ( username == null ) {
				return null;
			} else {
				user = dynamoDBMapper.load(User.class, StringUtils.lowerCase(username)); // email is stored in lower case
			}
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"username=" + username);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return user;
    }

    /**
     * Gets firstName for the given username from the Users table.
     * @param username email address of the user
     * @return firstName
     */
	public String getFirstName(String username) {
		String firstName = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("Username", new AttributeValue().withS(username));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Users")
			    .withKey(key)
			    .withAttributesToGet("FirstName");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			firstName = result.getItem().get("FirstName").getS();

		} catch (NullPointerException npe) {
	        return firstName; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"username=" + username);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return firstName;
    }

    /**
     * Delete from Users table.
     * @param username Email Address
     */
	public void deleteUser(String username) {

		try {
			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("Username", new AttributeValue().withS(username));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("Users")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Username: " + username);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**************************************************************************
     ************************** ZONE MODULE ***********************************
     **************************************************************************/

	/**
     * Queries "limit" number of previous or next zones.
     * First "limit-1" zones will be displayed on the page.
     * "limit"th zone will be used to decide whether or not to display next or previous page links.
     * @param userId userId
     * @param lastEvaluatedZO starting ZoneOrder for the next or previous page
     * @param lastEvaluatedZoneId starting ZoneId for the next or previous page
     * @param isForward true for Forward and false for Reverse
     * @return List of Map objects with Zone details
     */
	public List<Map<String,AttributeValue>> getZones(String userId, String lastEvaluatedZO,
			String lastEvaluatedZoneId, Boolean isForward, int limit) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();
		try {

    		/*** Prepare starting record for the next or previous page ***/
            if ( StringUtils.isBlank(lastEvaluatedZO) || StringUtils.equals(lastEvaluatedZO,"NULL") ) {
	       		lastEvaluatedKey = null;
            } else {
	       		lastEvaluatedKey.put("UserId", new AttributeValue().withS(userId));
	       		lastEvaluatedKey.put("ZoneOrder", new AttributeValue().withN(lastEvaluatedZO));
	       		lastEvaluatedKey.put("ZoneId", new AttributeValue().withS(lastEvaluatedZoneId));
            }

       		/*** Query Zones table ***/
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(userId));
        	keyConditions.put("UserId", hashKeyCondition);
        	Condition rangeKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.LT.toString())
        		.withAttributeValueList(new AttributeValue().withN("0"));
        	keyConditions.put("ZoneOrder", rangeKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Zones")
       			.withIndexName("ZoneOrderIdx")
       			.withKeyConditions(keyConditions)
       			.withScanIndexForward(isForward)
       			.withLimit(limit)
       			.withExclusiveStartKey(lastEvaluatedKey);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

    /**
     * Saves MyZones data into Zones table.
     * Input zone will be saved with the first ZoneOrder minus 1. This puts the input zone ahead of others.
     * @param userId userId
     * @param zoneId Id of the zone
     * @param zoneName Name of the zone
     */
	public void saveMyZone(String userId, String zoneId, String zoneName) {

		try {
            /*** Query the smallest zoneOrder from Zones table ***/
    		long zoneOrder = 0; // default
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(userId));
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("UserId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
	   			.withTableName("Zones")
	   			.withIndexName("ZoneOrderIdx")
	   			.withKeyConditions(keyConditions)
	   			.withLimit(1);

            QueryResult result = dynamoDBClient.query(queryRequest);
            // if there's a record, retrieve zoneOrder from it; otherwise zoneOrder will have default of zero
            if (result.getItems().size() == 1) {
            	zoneOrder = Long.parseLong(result.getItems().get(0).get("ZoneOrder").getN());
            }

            /*** Update ZoneOrder in Zones table or Insert new if the record doesn't exist ***/
            // define key
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("UserId", new AttributeValue().withS(userId));
            keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));
            // define update items
            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("ZoneName",
               	new AttributeValueUpdate()
               		.withValue(new AttributeValue().withS(zoneName))
               		.withAction(AttributeAction.PUT)
               	);
            updateItems.put("ZoneOrder",
            	new AttributeValueUpdate()
               		.withValue(new AttributeValue().withN( (zoneOrder-1)+"" )) // zoneOrder - 1
               		.withAction(AttributeAction.PUT)
               	);
            updateItems.put("Posts",
               	new AttributeValueUpdate()
               		.withValue(new AttributeValue().withN( (getPostsForZone(zoneId))+"" ))
               		.withAction(AttributeAction.PUT)
               	);
            updateItems.put("CurrentRO",
               	new AttributeValueUpdate()
               		.withValue(new AttributeValue().withN("0"))
               		.withAction(AttributeAction.ADD)
               	);

            // Make sure that no other session is updating/ inserting another record with the same ZoneOrder
            Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
            expected.put("ZoneOrder", new ExpectedAttributeValue()
            	.withExists(false));

            UpdateItemRequest request = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdate)
	        	.withAttributeUpdates(updateItems)
	        	.withExpected(expected);

	    	Boolean succeeded = false;
	    	int i=1;
			// if the ZoneOrder already exists (due to another concurrent transaction),
			// decrease the ZoneOrder by 1 and retry the operation
	    	do {
	    		try {
	                dynamoDBClient.updateItem(request);
	    			succeeded = true;
	    		} catch (ConditionalCheckFailedException e) {
	                i++;
	                updateItems.put("ZoneOrder",
                    	new AttributeValueUpdate()
                       		.withValue(new AttributeValue().withN( (zoneOrder-i)+"" ))
                       		.withAction(AttributeAction.PUT)
                       	);
	    		}
	    	} while (!succeeded && i<50); // fail after 50 attempts

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId=" + userId + " zoneId=" + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
	 * Creates a Zone.
     * Insert 1 : Zones table as MASTER data: Increment ZoneId if already exists.
     * Insert 2 : Admin Zones table
     * Insert 2 : Top Charts data for the generateTopChartsJobPeriod
     * @param zoneId Zone Id
     * @param zoneName Zone Name
     * @param zoneDesc Zone Description
     * @param zoneWho Who can post posts (E=Everyone, A=Admins only)
     * @param adminId userId of the user creating zone (will be the first admin for this zone)
     * @return ZoneId Created zoneId, which may be different from the input zoneId
     */
	public String createZone(String zoneId, String zoneName, String zoneDesc, String zoneWho, String adminId) {

		try {
            /*** INSERT 1: Zones table as MASTER data ***/
			// item to insert
			Map<String, AttributeValue> itemZ = new HashMap<String, AttributeValue>();
            itemZ.put("UserId", new AttributeValue().withS("M"));
            itemZ.put("ZoneId", new AttributeValue().withS(zoneId));
            itemZ.put("ZoneName", new AttributeValue().withS(zoneName));
            itemZ.put("ZoneDesc", new AttributeValue().withS(zoneDesc));
            itemZ.put("ZoneWho", new AttributeValue().withS(zoneWho));
            itemZ.put("Posts", new AttributeValue().withN("0"));
            itemZ.put("Votes", new AttributeValue().withN("0"));
            itemZ.put("Comments", new AttributeValue().withN("0"));
            itemZ.put("HI", new AttributeValue().withN("0"));
            itemZ.put("Admins", new AttributeValue().withSS(new HashSet<String>(Arrays.asList(adminId))));
			// expected condition - zoneId shouldn't already exist in the database
            Map<String, ExpectedAttributeValue> expectedZ = new HashMap<String, ExpectedAttributeValue>();
            expectedZ.put("ZoneId", new ExpectedAttributeValue()
            	.withExists(false));
	        // request to insert
	        PutItemRequest putItemRequestZ = new PutItemRequest()
	        	.withTableName("Zones")
		    	.withItem(itemZ)
		    	.withExpected(expectedZ);
			// if the ZoneId already exists (due to another concurrent transaction),
			// add a number at the end and retry
	    	Boolean succeeded = false;
	    	int i=0;
	    	String origZoneId = zoneId; // save original zoneId for retries
		    do {
		    	try {
	    			dynamoDBClient.putItem(putItemRequestZ);
		    		succeeded = true;
		    	} catch (ConditionalCheckFailedException e) {
	                i++;
	                zoneId = origZoneId + "-" + i;
	                itemZ.put("ZoneId", new AttributeValue().withS(zoneId));
		    	}
	    	} while (!succeeded && i<50); // fail after 50 attempts

            /*** INSERT 2: Admin Zones table ***/
			Map<String, AttributeValue> itemAZ = new HashMap<String, AttributeValue>();
            itemAZ.put("UserId", new AttributeValue().withS(adminId));
            itemAZ.put("ZoneId", new AttributeValue().withS(zoneId));
	        PutItemRequest putItemRequestAZ = new PutItemRequest()
	        	.withTableName("AdminZones")
		    	.withItem(itemAZ);
   			dynamoDBClient.putItem(putItemRequestAZ);

            /*** INSERT 3 : Top Zones data for the generateTopChartsJobPeriod ***/
			long generateTopChartsJobPeriod = Long.parseLong(getAttribute("T", "generateTopChartsJobPeriod"));
			Map<String, AttributeValue> itemT = new HashMap<String, AttributeValue>();
            itemT.put("Name", new AttributeValue().withS("Z-"+generateTopChartsJobPeriod));
            itemT.put("Id", new AttributeValue().withS(zoneId));
            itemT.put("ZoneName", new AttributeValue().withS(zoneName));
            // Put a large number in the HI column, so the new zone appears in the top of the list.
            itemT.put("HI", new AttributeValue().withN(System.currentTimeMillis()+""));
	        PutItemRequest putItemRequestT = new PutItemRequest()
	        	.withTableName("TopCharts")
		    	.withItem(itemT);
   			dynamoDBClient.putItem(putItemRequestT);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId=" + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

		return zoneId;
	}

    /**
     * Deletes a zone from MyZones.
     * @param userId userId for My Zones
     * @param zoneId Id of the zone
     */
	public void deleteMyZone(String userId, String zoneId) {

		try {
			// get current reading order
    		long currentRO = 0;
    		Map<String, AttributeValue> rlStats = getRLStats(userId, zoneId);
    		try { currentRO = Long.parseLong(rlStats.get("CurrentRO").getN()); } catch (Exception e) {}

    		if ( currentRO == 0 ) {
    			// reading list doesn't exist - delete record
	        	HashMap<String, AttributeValue> keyZ = new HashMap<String, AttributeValue>();
	        	keyZ.put("UserId", new AttributeValue().withS(userId));
	        	keyZ.put("ZoneId", new AttributeValue().withS(zoneId));

	        	DeleteItemRequest deleteItemRequestZ = new DeleteItemRequest()
		            .withTableName("Zones")
		   			.withKey(keyZ);
	        	dynamoDBClient.deleteItem(deleteItemRequestZ);
    		} else {
    			// reading exists - keep the record, delete ZoneOrder attribute
                HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
                keyUpdate.put("UserId", new AttributeValue().withS(userId));
                keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));

                Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
                updateItems.put("ZoneOrder",
               		new AttributeValueUpdate()
               			.withAction(AttributeAction.DELETE)
               		);

                UpdateItemRequest request = new UpdateItemRequest()
                	.withTableName("Zones")
                	.withKey(keyUpdate)
                	.withAttributeUpdates(updateItems);
                dynamoDBClient.updateItem(request);
	        }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Checks if Zone exists - to avoid duplicate zones when creating a new zone.
     * @param zoneId Id of the zone
     * @return Name of the Zone if exists; Null otherwise
     */
	public String checkIfZoneExists(String zoneId) {
		String zoneName = null;
		try {

			HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
			keyConditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("M")));
			keyConditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest queryRequest = new QueryRequest()
				.withTableName("Zones")
				.withKeyConditions(keyConditions);

			try {
				zoneName = dynamoDBClient.query(queryRequest).getItems().get(0).get("ZoneName").getS();
			} catch (Exception e) {
				// do nothing
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId=" + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return zoneName;
	}

    /**
     * Checks if My Zone exists - to avoid duplicate zones when adding a zone into MyZones.
     * @param userId userId
     * @param zoneId Id of the zone
     * @return Boolean indicating whether zone exists or not
     */
	public boolean checkIfMyZoneExists(String userId, String zoneId) {
		try {

			HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
			keyConditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId)));
			keyConditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest queryRequest = new QueryRequest()
				.withTableName("Zones")
				.withKeyConditions(keyConditions)
				.withAttributesToGet("ZoneOrder");

			int zoneOrder = -1; // default
			try {
				zoneOrder = Integer.parseInt(dynamoDBClient.query(queryRequest).getItems().get(0).get("ZoneOrder").getN());
			} catch (Exception e) {
				return false;
			}
			if ( zoneOrder == 0 ) return false;

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return true; // Either my zone exists OR there was an error
	}

    /**
     * Queries zone name.
     * @param zoneId Id of the zone
     * @return zoneName name of the zone
     */
	public String getZoneName(String zoneId) {
		String zoneName = null;
		try {
			HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();

			keyConditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("M")));

			keyConditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest request = new QueryRequest()
				.withTableName("Zones")
				.withAttributesToGet("ZoneName")
				.withKeyConditions(keyConditions);

			try {
				zoneName = dynamoDBClient.query(request).getItems().get(0).get("ZoneName").getS();
			} catch (Exception e) {
				// return empty map
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return zoneName;
	}

    /**
     * Queries zone description.
     * @param zoneId Id of the zone
     * @return zoneDesc Description of the zone
     */
	public String getZoneDesc (String zoneId) {
		String zoneDesc = null;
		try {
			HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();

			keyConditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("M")));

			keyConditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest request = new QueryRequest()
				.withTableName("Zones")
				.withAttributesToGet("ZoneDesc")
				.withKeyConditions(keyConditions);

			try {
				zoneDesc = dynamoDBClient.query(request).getItems().get(0).get("ZoneDesc").getS();
			} catch (Exception e) {
				// return empty map
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return zoneDesc;
	}

    /**
     * Queries zone stats.
     * @param zoneId Id of the zone
     */
	public Map<String, AttributeValue> getZoneStats(String zoneId) {
		Map<String, AttributeValue> retValue = new HashMap<String, AttributeValue>();
		try {
			HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();

			keyConditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("M")));

			keyConditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest request = new QueryRequest()
				.withTableName("Zones");

			request.setKeyConditions(keyConditions);
			try {
				retValue = dynamoDBClient.query(request).getItems().get(0);
			} catch (Exception e) {
				// return empty map
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return retValue;
	}

    /**
     * Gets the statistic "Posts" from the "Zones" table.
     * @param zoneId Id of the zone
     */
	public long getPostsForZone(String zoneId) {
		long posts = 0;
		try {
			HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();
			keyConditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("M")));
			keyConditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest request = new QueryRequest()
				.withTableName("Zones")
				.withKeyConditions(keyConditions);

			try {
				posts = Long.parseLong(dynamoDBClient.query(request).getItems().get(0).get("Posts").getN());
			} catch (Exception e) {
				// keep default value of zero
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return posts;
	}

	/**
     * Increment the given statistic in Zones table. Also increment HI if incrementHI is true.
     * @param userId userId or ALL
     * @param zoneId Id of the zone
     * @param statName name of the statistic to increment. Can be any number column.
     * @param incrementHI whether to increment HI or not
     * @param incrementBy number with which the stat needs to be incremented
     **/
	public void incrementZoneStat(String userId, String zoneId, String statName, boolean incrementHI, long incrementBy) {

    	try {
    		if ( StringUtils.equals(userId, "ALL") ) {
    	    	// Conditions for Zones table
    			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
    			conditions.put("ZoneId",
    				new Condition()
    					.withComparisonOperator(ComparisonOperator.EQ)
    					.withAttributeValueList(new AttributeValue().withS(zoneId)));

    		    Map<String, AttributeValue> lastEvaluatedKey = null;
    		    do {
    		    	// Query Zones table
    		    	QueryRequest queryRequest = new QueryRequest()
    	                .withTableName("Zones")
    	                .withIndexName("ZoneIdx")
    	                .withKeyConditions(conditions)
    	                .withExclusiveStartKey(lastEvaluatedKey);
    		        QueryResult result = dynamoDBClient.query(queryRequest);

    		        // Define Update Key and Update Request
		            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
		            keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));
		            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		            updateItems.put(statName,
		           		new AttributeValueUpdate()
		           			.withValue(new AttributeValue().withN(incrementBy+""))
		           			.withAction(AttributeAction.ADD)
		           		);
		            if ( incrementHI ) {
		            	int factor = 0;
		            	if ( StringUtils.equals(statName,"Posts") )
		            		factor = 10;
		            	else if ( StringUtils.equals(statName,"Votes") )
		            		factor = 2;
		            	else if ( StringUtils.equals(statName,"Comments") )
		            		factor = 3;
			            updateItems.put("HI",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withN(incrementBy*factor+""))
			           			.withAction(AttributeAction.ADD)
			           		);
		            }
		            UpdateItemRequest request = new UpdateItemRequest()
		            	.withTableName("Zones")
		            	.withKey(keyUpdate)
		            	.withAttributeUpdates(updateItems);

    		        // Loop through each userId and update
		            for (Map<String, AttributeValue> item : result.getItems()) {
   		        		keyUpdate.put("UserId", item.get("UserId"));
			            dynamoDBClient.updateItem(request);
    		        }

		            lastEvaluatedKey = result.getLastEvaluatedKey();
    		    } while (lastEvaluatedKey != null);

    		} else {

    			HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
	            keyUpdate.put("UserId", new AttributeValue().withS(userId));
	            keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));

	            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
	            updateItems.put(statName,
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withN(incrementBy+""))
	           			.withAction(AttributeAction.ADD)
	           		);
	            if ( incrementHI ) {
	            	int factor = 0;
	            	if ( StringUtils.equals(statName,"Posts") )
	            		factor = 10;
	            	else if ( StringUtils.equals(statName,"Votes") )
	            		factor = 2;
	            	else if ( StringUtils.equals(statName,"Comments") )
	            		factor = 3;
		            updateItems.put("HI",
		           		new AttributeValueUpdate()
		           			.withValue(new AttributeValue().withN(incrementBy*factor+""))
		           			.withAction(AttributeAction.ADD)
		           		);
	            }

	            UpdateItemRequest request = new UpdateItemRequest()
	            	.withTableName("Zones")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);

    		}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId + " statName: " + statName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Increments HeatIndex value in TopCharts table for ZONE. This data is used for Alltime TopCharts.
     * @param zoneId Id of the zone
     * @param incrementBy number with which HI needs to be incremented
     **/
	public void incrementZoneTopCharts (String zoneId, long incrementBy) {

    	try {

            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("Name", new AttributeValue().withS("Z"));
            keyUpdateTA.put("Id", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("HI",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("TopCharts")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA);
            dynamoDBClient.updateItem(requestTA);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Updates Zone Name in Zones and Posts tables.
     * @param zoneId Id of the zone
     * @param zoneName Zone Name
     **/
	public void updateZoneName (String zoneId, String zoneName) {

    	try {
    		/*** Update Zone Name in Zones table ***/
	    	// Conditions for Zones table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

		    Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
		    	// Query Zones table
		    	QueryRequest queryRequest = new QueryRequest()
	                .withTableName("Zones")
	                .withIndexName("ZoneIdx")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey);
		        QueryResult result = dynamoDBClient.query(queryRequest);

		        // Define Update Key and Update Request
	            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
	            keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));
	            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
	            updateItems.put("ZoneName",
               		new AttributeValueUpdate()
               			.withValue(new AttributeValue().withS(zoneName))
               			.withAction(AttributeAction.PUT)
               		);
	            UpdateItemRequest request = new UpdateItemRequest()
	            	.withTableName("Zones")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);

		        // Loop through each userId and update
	            for (Map<String, AttributeValue> item : result.getItems()) {
	            	long zoneOrder = 0;
	            	try {
	            		zoneOrder = Long.parseLong(item.get("ZoneOrder").getN());
	            	} catch (Exception e) {}
		        	if ( zoneOrder < 0 || StringUtils.equals(item.get("UserId").getS(), "M") ) {
		        		keyUpdate.put("UserId", new AttributeValue().withS(item.get("UserId").getS()));
			            dynamoDBClient.updateItem(request);
		        	}
		        }
		        lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

    		/*** Update Zone Name in Posts table. We get the list of posts from the Ranking table. ***/
    		Map<String, Condition> conditionsRanking = new HashMap<String, Condition>();
    		conditionsRanking.put("ZoneId",
    			new Condition()
	    			.withComparisonOperator(ComparisonOperator.EQ)
	    			.withAttributeValueList(new AttributeValue().withS(zoneId)));

    		QueryRequest requestRanking = new QueryRequest()
    			.withTableName("Ranking")
    			.withKeyConditions(conditionsRanking)
    			.withScanIndexForward(false)
    			.withAttributesToGet("PostId");

    		List<Map<String,AttributeValue>> itemsRanking = dynamoDBClient.query(requestRanking).getItems();

    		/*** Loop through PostIds ***/
    		for (int i = 0; i <= itemsRanking.size()-1; i++) {

	    		/*** Read PostId from Ranking ***/
	    		String postId = itemsRanking.get(i).get("PostId").getS();

	    		/*** Update Zone Name in Posts table ***/
	            HashMap<String, AttributeValue> keyUpdateB = new HashMap<String, AttributeValue>();
	            keyUpdateB.put("PostId", new AttributeValue().withS(postId));

	            Map<String, AttributeValueUpdate> updateItemsB = new HashMap<String, AttributeValueUpdate>();

	            updateItemsB.put("ZoneName",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(zoneName))
	           			.withAction(AttributeAction.PUT)
	           		);

	            UpdateItemRequest requestB = new UpdateItemRequest()
		        	.withTableName("Posts")
		        	.withKey(keyUpdateB)
		        	.withAttributeUpdates(updateItemsB);

	            dynamoDBClient.updateItem(requestB);
    		}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " zoneName: " + zoneName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Updates Zone Headshot in Zones table.
     * @param zoneId Id of the zone
     * @param zoneHeadshot Zone Headshot
     **/
	public void updateZoneHeadshot (String zoneId, String zoneHeadshot) {

    	try {
    		/*** Update Zone Headshot in Zones table where userId="M" ***/
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();

            updateItemsZ.put("ZoneHeadshot",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS(zoneHeadshot))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);

            dynamoDBClient.updateItem(requestZ);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " zoneHeadshot: " + zoneHeadshot);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Updates Zone Description in Zones table.
     * @param zoneId Id of the zone
     * @param zoneDesc Zone Description
     **/
	public void updateZoneDesc (String zoneId, String zoneDesc) {

    	try {
    		/*** Update Zone Description in Zones table where userId="M" ***/
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();

            updateItemsZ.put("ZoneDesc",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS(zoneDesc))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);

            dynamoDBClient.updateItem(requestZ);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " zoneDesc: " + zoneDesc);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Updates Zone Who in Zones table.
     * @param zoneId Id of the zone
     * @param zoneWho Zone Who
     **/
	public void updateZoneWho (String zoneId, String zoneWho) {

    	try {
    		/*** Update Zone Who in Zones table where userId="M" ***/
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();

            updateItemsZ.put("ZoneWho",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS(zoneWho))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);

            dynamoDBClient.updateItem(requestZ);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " zoneWho: " + zoneWho);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Adds adminId to Admins of the Zones table. Called by BecomeAdmin procedure.
     * @param zoneId zoneId
     * @param adminId userId of the admin
     **/
	public void addZoneAdmin (String zoneId, String adminId) {

    	try {
            /*** Update Zones table ***/
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();
            updateItemsZ.put("Admins",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withSS(adminId))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);
            dynamoDBClient.updateItem(requestZ);

            /*** Insert into AdminZones table ***/
			Map<String, AttributeValue> itemAZ = new HashMap<String, AttributeValue>();
            itemAZ.put("UserId", new AttributeValue().withS(adminId));
            itemAZ.put("ZoneId", new AttributeValue().withS(zoneId));
	        PutItemRequest putItemRequestAZ = new PutItemRequest()
	        	.withTableName("AdminZones")
		    	.withItem(itemAZ);
   			dynamoDBClient.putItem(putItemRequestAZ);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " adminId: " + adminId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Adds adminId to AdminRequests of the Zones table.
     * @param zoneId zoneId
     * @param adminId userId of the admin
     **/
	public void addZoneAdminRequest (String zoneId, String adminId) {

    	try {
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();
            updateItemsZ.put("AdminRequests",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withSS(adminId))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);
            dynamoDBClient.updateItem(requestZ);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " adminId: " + adminId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Adds adminId to Admins of the Zones table and deletes from AdminRequests.
     * @param zoneId zoneId
     * @param adminId userId of the admin
     **/
	public void approveZoneAdmin (String zoneId, String adminId) {

    	try {
    		/*** Add to Admins and remove from AdminRequests of the Zones table ***/
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();
            updateItemsZ.put("Admins",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withSS(adminId))
           			.withAction(AttributeAction.ADD)
           		);
            updateItemsZ.put("AdminRequests",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withSS(adminId))
           			.withAction(AttributeAction.DELETE)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);
            dynamoDBClient.updateItem(requestZ);

            /*** Insert into AdminZones table ***/
			Map<String, AttributeValue> itemAZ = new HashMap<String, AttributeValue>();
            itemAZ.put("UserId", new AttributeValue().withS(adminId));
            itemAZ.put("ZoneId", new AttributeValue().withS(zoneId));
	        PutItemRequest putItemRequestAZ = new PutItemRequest()
	        	.withTableName("AdminZones")
		    	.withItem(itemAZ);
   			dynamoDBClient.putItem(putItemRequestAZ);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " adminId: " + adminId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Removes adminId from Admins of the Zones table.
     * @param zoneId zoneId
     * @param adminId userId of the admin
     **/
	public void removeZoneAdmin (String zoneId, String adminId) {

    	try {
    		/*** Remove from Admins of the Zones table ***/
            HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
            keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
            keyUpdateZ.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();
            updateItemsZ.put("Admins",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withSS(adminId))
           			.withAction(AttributeAction.DELETE)
           		);

            UpdateItemRequest requestZ = new UpdateItemRequest()
	        	.withTableName("Zones")
	        	.withKey(keyUpdateZ)
	        	.withAttributeUpdates(updateItemsZ);
            dynamoDBClient.updateItem(requestZ);

            /*** Delete from AdminZones table ***/
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("UserId", new AttributeValue().withS(adminId));
            key.put("ZoneId", new AttributeValue().withS(zoneId));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("AdminZones")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " adminId: " + adminId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**************************************************************************
     **********************  RL STATS MODULE  *********************************
     **************************************************************************/

	/**
     * Gets RL stats record from Zones table.
     * @param userId userId
     * @param zoneId Id of the Zone
     * @return RL Stats record
	**/
	public Map<String,AttributeValue> getRLStats(String userId, String zoneId) {
		Map<String, AttributeValue> rlStats = new HashMap<String, AttributeValue>();
		try {
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId)));
			conditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			QueryRequest request = new QueryRequest()
				.withTableName("Zones")
				.withKeyConditions(conditions);

			try {
				rlStats = dynamoDBClient.query(request).getItems().get(0);
			} catch (Exception e) {
				// no record in the Zones table
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return rlStats;
	}

	/**
     * Sets Posts and/or Current RO and/or Max RO in the Zones table. A zero value skips the parameter.
     * @param userId userId
     * @param zoneId Id of the zone
     * @param posts number of posts in the zone
     * @param currentRO current reading order
     * @param maxRO maximum RO in the RL (will be the same as the count of posts in the RL)
	**/
	public void putRLStats(String userId, String zoneId, long posts, long currentRO, long maxRO) {

		try {

			HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("UserId", new AttributeValue().withS(userId));
            keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();

            if ( posts != 0 ) {
	            updateItems.put("Posts",
	            	new AttributeValueUpdate()
		            	.withValue(new AttributeValue().withN(posts+""))
		            	.withAction(AttributeAction.PUT)
		           	);
            }

            if ( currentRO != 0 ) {
	            updateItems.put("CurrentRO",
	            	new AttributeValueUpdate()
		            	.withValue(new AttributeValue().withN(currentRO+""))
		            	.withAction(AttributeAction.PUT)
		           	);
            }

            if ( maxRO != 0 ) {
	            updateItems.put("MaxRO",
	            	new AttributeValueUpdate()
	 	            	.withValue(new AttributeValue().withN(maxRO+""))
	  	            	.withAction(AttributeAction.PUT)
	  	           	);
            }

            if ( !updateItems.isEmpty() ) {
	            UpdateItemRequest request = new UpdateItemRequest()
	            	.withTableName("Zones")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId + " Posts: " + posts + " currentRO: " + currentRO + " maxRO: " + maxRO);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**************************************************************************
     ************************** RL MODULE *************************************
     **************************************************************************/

	/**
     * Fetches "logged in" user's reading list for a given zone. We fetch 1 record before & 1 record after the currentRO.
     * @param userId userId
     * @param zoneId Id of the Zone
     * @param currentRO current reading order
     * @return List of Map objects with RO's and PostId's
    */
	public List<Map<String,AttributeValue>> getRL(String userId, String zoneId, long currentRO) {
		QueryResult result = new QueryResult();
		try {
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(userId+"-"+zoneId));
        	Condition rangeKeyCondition = new Condition()
            	.withComparisonOperator(ComparisonOperator.BETWEEN.toString())
            	.withAttributeValueList(new AttributeValue().withN((currentRO-1)+""), new AttributeValue().withN((currentRO+1)+""));

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("UserId-ZoneId", hashKeyCondition);
            keyConditions.put("RO", rangeKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("RList")
       			.withKeyConditions(keyConditions)
       			.withAttributesToGet("RO","PostId");

       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId + " currentRO: " + currentRO);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

    /**
     * Puts a record in the RList table. RO will be computed as MaxRO+1.
     * @param userId userId
     * @param zoneId Id of the zone
     * @param postId postId
     * @return inserted RO
     */
	public long putRL(String userId, String zoneId, String postId) {

		long RO = 0;
		try {
			// Select Max RO + 1 from RList table
			RO = getMaxRO(userId, zoneId) + 1;

			// prepare item for inserting
    		Map<String, AttributeValue> itemRL = new HashMap<String, AttributeValue>();
            itemRL.put("UserId-ZoneId", new AttributeValue().withS(userId+"-"+zoneId));
            itemRL.put("RO", new AttributeValue().withN( RO+"" ));
            itemRL.put("PostId", new AttributeValue().withS(postId));

            // expected condition - ReadingOrder shouldn't exist in the database
            Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
            expected.put("RO", new ExpectedAttributeValue()
            	.withExists(false));

            PutItemRequest putItemRequest = new PutItemRequest()
    	    	.withTableName("RList")
    	    	.withItem(itemRL)
    	    	.withExpected(expected);

            Boolean succeeded = false;
    	    int i = 0;
    	    do {
    	    	try {
    	    		dynamoDBClient.putItem(putItemRequest);
    	    		succeeded = true;
    	    	} catch (ConditionalCheckFailedException e) {
    	    		// if the ReadingOrder already exists (due to another concurrent transaction),
    	    		// increase the ReadingOrder by 1 and retry the operation
    	           	i++;
    	           	RO++;
    	           	itemRL.put("RO", new AttributeValue().withN(RO+""));
    	    	}
    	    } while (!succeeded && i<50); // stop after 50 attempts to avoid infinite loop

    	    if ( !succeeded ) RO = 0;

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId + " postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return RO;

	}

	/**
     * Gets the reading order of a given post in user's RL.
     * @param userId userId
     * @param zoneId Id of the Zone
     * @param postId postId
     * @return RO of the postId in user's RL; 0 if not found
	**/
	public long getRO4PostId(String userId, String zoneId, String postId) {
		long RO = 0;
		try {
			HashMap<String, Condition> conditionsRL = new HashMap<String, Condition>();
			conditionsRL.put("UserId-ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-"+zoneId)));
			conditionsRL.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));

			QueryRequest requestRL = new QueryRequest()
				.withTableName("RList")
				.withIndexName("PostIdIdx")
				.withKeyConditions(conditionsRL)
				.withLimit(1)
	   			.withAttributesToGet("RO");

			try {
				RO = Long.parseLong(dynamoDBClient.query(requestRL).getItems().get(0).get("RO").getN());
			} catch (Exception e) {
				// keep default value "0" for RO
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId + " postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return RO;
	}

	/**
     * Gets postId for a given RO from the user's RL.
     * @param userId userId
     * @param zoneId Id of the Zone
     * @param RO reading order
     * @return postId postId of the post at the given RO; null if not found
	**/
	public String getPostId4RO(String userId, String zoneId, long RO) {
		String postId = null;
		try {
			HashMap<String, Condition> conditionsRL = new HashMap<String, Condition>();
			conditionsRL.put("UserId-ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-"+zoneId)));
			conditionsRL.put("RO",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withN( RO+"" )));

			QueryRequest requestRL = new QueryRequest()
				.withTableName("RList")
				.withKeyConditions(conditionsRL)
				.withLimit(1)
	   			.withAttributesToGet("PostId");

			List<Map<String,AttributeValue>> itemsRL = dynamoDBClient.query(requestRL).getItems();

			if ( itemsRL.size() > 0 )
				postId = itemsRL.get(0).get("PostId").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId + " RO: " + RO);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return postId;
	}

	/**
     * Gets the maximum reading order in user's RL for a given zone.
     * @param userId userId
     * @param zoneId Id of the Zone
     * @return Max RO of user's RL; Zero if RL is empty
	**/
	public long getMaxRO(String userId, String zoneId) {
		long RO = 0;
		try {
			HashMap<String, Condition> conditionsRL = new HashMap<String, Condition>();
			conditionsRL.put("UserId-ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-"+zoneId)));

			QueryRequest requestRL = new QueryRequest()
				.withTableName("RList")
				.withKeyConditions(conditionsRL)
       			.withScanIndexForward(false)
				.withLimit(1)
       			.withAttributesToGet("RO");

			try {
				RO = Long.parseLong(dynamoDBClient.query(requestRL).getItems().get(0).get("RO").getN());
			} catch (Exception e) {
				// keep default return value "0"
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId + " zoneId: " + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return RO;
	}

	/**
     * Expands RL by adding posts from the Ranking table.
     * @param userId userId
     * @param zoneId Id of the Zone
     * @param expandBy number of posts to add
     * @return maxRO that has been inserted; return 0 if no records have been inserted.
    */
	public long expandRL(String userId, String zoneId, int expandBy) {

		int insertCount = 0;
		long maxRO = 0;
		try {
			/*** Query Ranking table for the input zone in the reverse order of PostHI ***/
        	Map<String, Condition> conditionsRanking = new HashMap<String, Condition>();
			conditionsRanking.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

			Map<String, AttributeValue> lastEvaluatedKey = null;
			do
			{
	        	QueryRequest requestRanking = new QueryRequest()
	       			.withTableName("Ranking")
	       			.withIndexName("PostHIIdx")
	       			.withKeyConditions(conditionsRanking)
	       			.withExclusiveStartKey(lastEvaluatedKey)
	       			.withScanIndexForward(false)
	       			.withAttributesToGet("PostId");

	        	QueryResult result = dynamoDBClient.query(requestRanking);
	        	List<Map<String,AttributeValue>> itemsRanking = result.getItems();

	        	/*** Loop through PostIds until requested number of Posts have been inserted into RL ***/
	        	for (int i = 0; i < itemsRanking.size() && insertCount < expandBy; i++) {

	        		/*** Read PostId from Ranking ***/
	        		String postId = itemsRanking.get(i).get("PostId").getS();

	        		/*** If PostId doesn't exist in RL table, insert it now. ***/
	        		if (getRO4PostId(userId, zoneId, postId) == 0 ) {
	        			// Insert postId at the end of RList table
	        			maxRO = putRL(userId, zoneId, postId);
	        			insertCount++;
	        		}

	        	} // for loop of itemRanking

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
			} while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId + " ExpandBy: " + expandBy);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return maxRO;
	}

	/**
     * Update RL based on the input postId and currentRO
     * @param userId userId
     * @param zoneId Id of the Zone
     * @param currentRO Current Reading Order
     * @param postId PostId to add at currentRO
     * @return updated currentRO
     */
	public long updateRL(String userId, String zoneId, long currentRO, String postId) {

		try {
	    	/*** Get reading order from the RL that corresponds to input postId ***/
			long postIdRO = getRO4PostId(userId, zoneId, postId);

        	if ( postIdRO == 0 ) { // if input postId doesn't exist in RL

        		/*** Increment currentRO ***/
        		currentRO++;
        		long maxRO = currentRO;

    	    	/*** Get postId from the RL that corresponds to currentRO ***/
    			String replacedPostId = getPostId4RO(userId, zoneId, currentRO);

				/*** Put input postId at currentRO ***/
    			HashMap<String, AttributeValue> itemRL = new HashMap<String, AttributeValue>();
            	itemRL.put("UserId-ZoneId", new AttributeValue().withS(userId+"-"+zoneId));
	        	itemRL.put("RO", new AttributeValue().withN( currentRO + "" ));
	        	itemRL.put("PostId", new AttributeValue().withS(postId));
            	PutItemRequest putItemRequest = new PutItemRequest()
    	            .withTableName("RList")
    	   			.withItem(itemRL);
	        	dynamoDBClient.putItem(putItemRequest);

				/*** Put replaced postId at the end of RL if not blank ***/
	        	if ( StringUtils.isNotBlank(replacedPostId) ) {
	        		maxRO = putRL(userId, zoneId, replacedPostId);
	        	}

				/*** Save currentRO and maxRO ***/
        		putRLStats(userId, zoneId, 0, currentRO, maxRO);

        	} else if ( postIdRO <= currentRO ) { // if input postId exists at currentRO or before

        		/*** Reset currentRO only for now, not in the database ***/
        		currentRO = postIdRO;

        	} else if ( postIdRO == currentRO+1 ) { // if input postId exists at currentRO+1

        		/*** Increment currentRO and store it in the database ***/
        		currentRO++;
        		putRLStats(userId, zoneId, 0, currentRO, 0);

        	} else if ( postIdRO > currentRO+1 ) { // if input postId exists after currentRO+1

        		/*** Increment currentRO and store it in the database ***/
        		currentRO++;
        		putRLStats(userId, zoneId, 0, currentRO, 0);

        		/*** Get postId from the RL that corresponds to "updated" currentRO ***/
    			String replacedPostId = getPostId4RO(userId, zoneId, currentRO);

				/*** Put input postId at "updated" currentRO ***/
    			HashMap<String, AttributeValue> itemRL = new HashMap<String, AttributeValue>();
            	itemRL.put("UserId-ZoneId", new AttributeValue().withS(userId+"-"+zoneId));
	        	itemRL.put("RO", new AttributeValue().withN( currentRO + "" ));
	        	itemRL.put("PostId", new AttributeValue().withS(postId));
            	PutItemRequest putItemRequest = new PutItemRequest()
    	            .withTableName("RList")
    	   			.withItem(itemRL);
	        	dynamoDBClient.putItem(putItemRequest);

				/*** Put replaced postId at postIdRO ***/
	        	itemRL.put("RO", new AttributeValue().withN( postIdRO + "" ));
	        	itemRL.put("PostId", new AttributeValue().withS(replacedPostId));
	        	dynamoDBClient.putItem(putItemRequest);

			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " ZoneId: " + zoneId + " currentRO: " + currentRO + " postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return currentRO;

	}

	/**
	 * Adds the just published post into reading lists of those users who have reached
	 * the end of their reading lists in that zone. This method will be called
	 * asynchronously whenever a post is published.
	 * @param zone Id of the zone where the post is published
	 * @param postId Id of the post
	 */
    public void addPostToRLs(String zoneId, String postId) {

    	try {
	    	// Conditions for Zones table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(zoneId)));

		    Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
		    	// Query Zones table
		    	QueryRequest queryRequest = new QueryRequest()
	                .withTableName("Zones")
	                .withIndexName("ZoneIdx")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey);
		        QueryResult result = dynamoDBClient.query(queryRequest);

		        // Loop through each item
		        for (Map<String, AttributeValue> item : result.getItems()) {

		        	// Get userId for the record
		        	String userId = item.get("UserId").getS();

		        	if ( !StringUtils.equals(item.get("UserId").getS(), "M") ) {

						/*** Add to RL if unreadPostCount is 3 or less and PostId doesn't already exist in the RL ***/
			        	long unreadPostCount = 0;
			        	try {
			        		unreadPostCount = Long.parseLong(item.get("MaxRO").getN())-Long.parseLong(item.get("CurrentRO").getN());
			        	} catch (Exception e) {
			        		// keep the default value of zero
			        	}
			        	if ( unreadPostCount <= 3 && getRO4PostId(userId, zoneId, postId) == 0 ) {
			        		// Insert postId at the end of RL table
				        	long maxRO = putRL(userId, zoneId, postId);
			        		// Update RLStats : if MaxRO is 1, set CurrentRO to 1, or else don't update currentRO
				        	if ( maxRO == 1 ) {
			        			putRLStats(userId, zoneId, 0, 1, maxRO);
			        		} else {
			        			putRLStats(userId, zoneId, 0, 0, maxRO);
			        		}
			        	}

		        	}

		        }
		        lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId + " PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
    }

    /**
     * Deletes all records from the RList table for a given userId and Zone
     * @param userId userId
     * @param zoneId Id of the zone
     */
    public void deleteRL(String userId, String zoneId) {

		try {
			// Conditions for RList table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("UserId-ZoneId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-"+zoneId)));

		    Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
			    // Query RList table
			    QueryRequest request = new QueryRequest()
	                .withTableName("RList")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("RO");
		        QueryResult result = dynamoDBClient.query(request);

            	// Prepare key for delete
            	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            	key.put("UserId-ZoneId", new AttributeValue().withS(userId+"-"+zoneId));
	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("RList")
		   			.withKey(key);

		        // Loop through each record and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {
	            	key.put("RO", new AttributeValue().withN(item.get("RO").getN()));
		        	dynamoDBClient.deleteItem(deleteItemRequest);
	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId: " + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**************************************************************************
     ************************** POST MODULE ***********************************
     **************************************************************************/

    /**
     * Queries post details from Posts table
     * @param PostId PostId of the Post
     * @return Post object with post data
     */
	public Post getPost (String postId) {
		Post post = null; // default
		try {
        	post = dynamoDBMapper.load(Post.class, postId);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return post;
	}

    /**
     * Gets bloggerId for a given postId from the Posts table.
     * @param postId postId
     * @return bloggerId
     */
	public String getPostBloggerId (String postId) {
		String bloggerId = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("BloggerId");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			bloggerId = result.getItem().get("BloggerId").getS();

		} catch (NullPointerException npe) {
	        return bloggerId; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return bloggerId;
    }

    /**
     * Gets bloggerName for a given postId from the Posts table.
     * @param postId postId
     * @return bloggerName
     */
	public String getPostBloggerName (String postId) {
		String bloggerName = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("BloggerName");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			bloggerName = result.getItem().get("BloggerName").getS();

		} catch (NullPointerException npe) {
	        return bloggerName; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return bloggerName;
    }

    /**
     * Gets publishFlag for a given postId from the Posts table.
     * @param postId postId
     * @return publishFlag
     */
	public String getPostPublishFlag (String postId) {
		String publishFlag = "N"; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("PublishFlag");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			try {
				publishFlag = result.getItem().get("PublishFlag").getS();
			} catch (NullPointerException npe) {
				// keep the default
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return publishFlag;
    }

	/**
     * Gets UpVotes minus DownVotes for the given postId from the Posts table.
     * @param postId postId
     * @return Votes
     */
	public int getPostVotes (String postId) {
		int votes = 0; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("UpVotes", "DownVotes");

			Map<String,AttributeValue> item = dynamoDBClient.getItem(getItemRequest).getItem();
			votes = Integer.parseInt(item.get("UpVotes").getN()) - Integer.parseInt(item.get("DownVotes").getN());

		} catch (NullPointerException npe) {
	        return votes; // record doesn't exist, so return default value

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return votes;
    }

    /**
     * Gets zoneId for a given postId from the Posts table.
     * @param postId postId
     * @return zoneId
     */
	public String getPostZoneId (String postId) {
		String zoneId = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("ZoneId");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			zoneId = result.getItem().get("ZoneId").getS();

		} catch (NullPointerException npe) {
	        return zoneId; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return zoneId;
    }

    /**
     * Gets pageId for a given postId from the Posts table.
     * @param postId postId
     * @return pageId
     */
	public String getPostPageId (String postId) {
		String pageId = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("PageId");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			pageId = result.getItem().get("PageId").getS();

		} catch (NullPointerException npe) {
	        return pageId; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PageId=" + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return pageId;
    }

    /**
     * Gets postTitle for a given postId from the Posts table.
     * @param postId postId
     * @return postTitle
     */
	public String getPostTitle (String postId) {
		String postTitle = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("PostTitle");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			postTitle = result.getItem().get("PostTitle").getS();

		} catch (NullPointerException npe) {
	        return postTitle; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return postTitle;
    }

    /**
     * Gets summary for a given postId from the Posts table.
     * @param postId postId
     * @return postSummary
     */
	public String getPostSummary (String postId) {
		String postSummary = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Posts")
			    .withKey(key)
			    .withAttributesToGet("PostSummary");

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			try {
				postSummary = result.getItem().get("PostSummary").getS();
			} catch (NullPointerException npe) {
				// keep the default
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return postSummary;
    }

    /**
     * Generates UNIQUE PostId based on ZoneId & PostTitle and inserts into Posts table.
     * Suffixes a number if the PostId already exists in our system.
     * @param userId userId
     * @param zoneId zoneId
     * @param postTitle post title
     * @return generated postId, or ERROR if there is an error, or NEW if another session is already saving the post
     */
    public String generatePostId(String userId, String zoneId, String postTitle) {
        Boolean succeeded = false;
    	String postId = zoneId + "-" + postTitle; // initial value
    	try {
			/*** We are making use of lastNewPostDate to avoid creating in duplicate postIds for the same post. ***/
    		long UTC = System.currentTimeMillis();
    		long lastNewPostDate = 0;
    		try {
    			lastNewPostDate = Long.parseLong(getAttribute(userId, "lastNewPostDate"));
    		} catch (Exception e) {
    			// keep the default
    		}
    		if ( UTC < lastNewPostDate+30*1000 ) {
    			return "NEW";
    		} else {
    			/*** Insert/ Update lastNewPostDate in the database ***/
    			setAttribute(userId, "lastNewPostDate", UTC+"");
    		}

    		/*** Make postId URL-friendly ***/
			try {
	    		// convert to lower case
	    		postId = StringUtils.lowerCase(postId);
				// replace all non-alphanumeric characters (including spaces) with dashes
	    		postId = postId.replaceAll("[^a-z0-9]","-");
				// trim two or more subsequent dashes into one
		        postId = postId.trim().replaceAll("-+", "-");
				// remove the first character if it's a dash
		        postId = postId.startsWith("-") ? postId.substring(1) : postId;
				// remove the last character if it's a dash
		        postId = postId.endsWith("-") ? postId.substring(0,postId.length()-1) : postId;
				// word wrap until 80 characters
		        postId = postId.substring(0, StringUtils.substring(postId+"-",0,80).lastIndexOf("-"));
				// if the string is empty, generate a random id based on the system time
				if ( postId.length() == 0 ) postId = postId + (long) System.currentTimeMillis()/21600000;
			} catch (Exception e) {
				postId = postId + (long) System.currentTimeMillis()/21600000;
			}

    		/*** Insert into Posts table ***/
			// item to insert
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("PostId", new AttributeValue().withS(postId));
			// expected condition - postId shouldn't already exist in the database
	        Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
	        expected.put("PostId", new ExpectedAttributeValue().withExists(false));
	        // request to insert
	        PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("Posts")
		    	.withItem(item)
		    	.withExpected(expected);

	        /*** if the postId already exists, suffix a number (in increments) and retry ***/
	        int i = 0;
	        String origPostId = postId; // save orig post id for retries
		    do {
		    	try {
		    		dynamoDBClient.putItem(putItemRequest);
		    		succeeded = true;
		    	} catch (ConditionalCheckFailedException e) {
		    		i++;
		    		postId = origPostId+"-"+i;
		    		item.put("PostId", new AttributeValue().withS(postId));
		    	}
		    } while (!succeeded && i<100); // fail after 100 times!

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

    	if (succeeded) {
	    	return postId;
	    } else {
	        logger.log(Level.SEVERE,"Failed to generate postId for userId=" + userId + " zoneId="+ zoneId + " postTitle=" + postTitle);
	    	return "ERROR";
	    }

    }

    /**
     * Puts post metadata into Posts table.
     * @param post Post object with post metadata
     */
    public void putPost(Post post) {
		try {
			dynamoDBMapper.save(post);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

	/**
     * Puts a record into Top Charts table for Just Published posts.
     * @param postId postId
     * @param postUpdateDate Publish Date of the Post (will become HI attribute of TopCharts)
     */
	public void putJustPublished (String postId, long postUpdateDate) {

		try {

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("J"));
            item.put("Id", new AttributeValue().withS(postId));
            item.put("HI", new AttributeValue().withN(postUpdateDate+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId + " postUpdateDate=" + postUpdateDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Updates post data in Posts table.
     * @param postId postId
     * @param postTitle postTitle
     * @param tags Tags
     * @param postHeadshot Post Headshot URL
     * @param postSummary postSummary
     * @param publishFlag publishFlag
     * @param postUpdateDate postUpdateDate
     */
    public void updatePost(String postId, String postTitle, String tags, String postHeadshot, String postSummary, String publishFlag, long postUpdateDate) {
		try {
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("PostTitle",
            	new AttributeValueUpdate()
	            	.withValue(new AttributeValue().withS(postTitle))
	            	.withAction(AttributeAction.PUT)
	           	);
            if ( StringUtils.isNotBlank(tags) ) {
	            updateItems.put("Tags",
	            	new AttributeValueUpdate()
	       	           	.withValue(new AttributeValue().withS(tags))
	       	           	.withAction(AttributeAction.PUT)
	       	        );
            }
            updateItems.put("PostHeadshot",
                new AttributeValueUpdate()
       	         	.withValue(new AttributeValue().withS(postHeadshot))
       	          	.withAction(AttributeAction.PUT)
       	        );
            updateItems.put("PostSummary",
               	new AttributeValueUpdate()
   	            	.withValue(new AttributeValue().withS(postSummary))
   	            	.withAction(AttributeAction.PUT)
   	           	);

            if ( StringUtils.equals(publishFlag,"Y") ) {
                updateItems.put("PublishFlag",
                   	new AttributeValueUpdate()
       	            	.withValue(new AttributeValue().withS(publishFlag))
       	            	.withAction(AttributeAction.PUT)
       	           	);
                updateItems.put("UpdateDate",
                   	new AttributeValueUpdate()
       	            	.withValue(new AttributeValue().withN(postUpdateDate+""))
       	            	.withAction(AttributeAction.PUT)
       	           	);
            }

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Posts")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(request);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId + " postTitle: " + postTitle + " postSummary: " + postSummary + " publishFlag: " + publishFlag + " postUpdateDate: " + postUpdateDate );
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Updates post summary in Posts table.
     * @param postId postId
     * @param postSummary postSummary
     */
    public void updatePostSummary (String postId, String postSummary) {
		try {
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems =
            	new HashMap<String, AttributeValueUpdate>();

            updateItems.put("PostSummary",
               	new AttributeValueUpdate()
   	            	.withValue(new AttributeValue().withS(postSummary+" "))
   	            	.withAction(AttributeAction.PUT)
   	           	);

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Posts")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);

            dynamoDBClient.updateItem(request);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId + " postSummary: " + postSummary);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Updates post request FB in Posts table.
     * @param postId postId
     * @param requestFB RequestFB, can be Y, A or R
     */
    public void updatePostRequestFB (String postId, String requestFB) {
		try {
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();

            updateItems.put("RequestFB",
               	new AttributeValueUpdate()
   	            	.withValue(new AttributeValue().withS(requestFB))
   	            	.withAction(AttributeAction.PUT)
   	           	);

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Posts")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);

            dynamoDBClient.updateItem(request);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId + " requestFB: " + requestFB);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Resets UpVotes, DownVotes and Comments in Posts table.
     * @param postId postId
     */
    public void resetPostStats(String postId) {
		try {
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("UpVotes",
            	new AttributeValueUpdate()
	            	.withValue(new AttributeValue().withN("0"))
	            	.withAction(AttributeAction.PUT)
	           	);
            updateItems.put("DownVotes",
               	new AttributeValueUpdate()
   	            	.withValue(new AttributeValue().withN("0"))
   	            	.withAction(AttributeAction.PUT)
   	           	);
            updateItems.put("Comments",
               	new AttributeValueUpdate()
   	            	.withValue(new AttributeValue().withN("0"))
   	            	.withAction(AttributeAction.PUT)
   	           	);
            updateItems.put("Views",
               	new AttributeValueUpdate()
   	            	.withValue(new AttributeValue().withN("0"))
   	            	.withAction(AttributeAction.PUT)
   	           	);

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Posts")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(request);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId );
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

	/**
     * Increment the given statistic in Posts table. Also increment HI if incrementHI is true.
     * @param postId postId
     * @param statName statistic to increment. Can be Views, UpVotes, DownVotes or Comments
     * @param incrementHI whether to increment HI or not
     * @param incrementBy number with which the stat needs to be incremented
     **/
	public void incrementPostStat(String postId, String statName, boolean incrementHI, long incrementBy) {

    	try {
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put(statName,
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);
            if ( incrementHI ) {
            	int factor = 0;
            	if ( StringUtils.equals(statName,"Comments") )
            		factor = 3;
            	else if ( StringUtils.equals(statName,"DownVotes") )
            		factor = 2;
            	else if ( StringUtils.equals(statName,"UpVotes") )
            		factor = 2;
            	else if ( StringUtils.equals(statName,"Views") )
            		factor = 1;
	            updateItems.put("HI",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withN(incrementBy*factor+""))
	           			.withAction(AttributeAction.ADD)
	           		);
            }

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Posts")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " statName: " + statName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Increments HI in PostActivity table. Inserts a new record if PostId doesn't already exist.
     * @param postId postId
     * @param zoneId zoneId
     * @param zoneName zoneName
     * @param bloggerId bloggerId
     * @param pageId pageId (can be blank)
     * @param incrementBy number with which HI needs to be incremented
     **/
	public void incrementPostActivity (String postId, String zoneId, String zoneName,
			String bloggerId, String pageId, long incrementBy) {

    	try {
            long period = (long) System.currentTimeMillis()/21600000; // 6 hours

            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("Period", new AttributeValue().withN(period+""));
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("ZoneId",
           		new AttributeValueUpdate()
          			.withValue(new AttributeValue().withS(zoneId))
           			.withAction(AttributeAction.PUT)
           		);
            updateItems.put("ZoneName",
          		new AttributeValueUpdate()
          			.withValue(new AttributeValue().withS(zoneName))
           			.withAction(AttributeAction.PUT)
           		);
            updateItems.put("BloggerId",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS(bloggerId))
           			.withAction(AttributeAction.PUT)
           		);
            if ( StringUtils.isNotBlank(pageId) ) {
	            updateItems.put("PageId",
	          		new AttributeValueUpdate()
	         			.withValue(new AttributeValue().withS(pageId.trim()))
	          			.withAction(AttributeAction.PUT)
	          		);
            }
            updateItems.put("HI",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("PostActivity")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Check if the machine with given macId has viewed the post.
     * @param postId postId
     * @param macId Identifier for client machine (IP address for now)
     * @return true or false
	**/
	public boolean checkViewed(String postId, String macId) {
		boolean viewed = false; // default
		try {
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));
			conditions.put("MacId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(macId)));

			QueryRequest request = new QueryRequest()
				.withTableName("PostViews")
				.withKeyConditions(conditions)
				.withLimit(1)
	   			.withAttributesToGet("MacId");

			try {
				viewed = !(dynamoDBClient.query(request).getItems().isEmpty());
			} catch (Exception e) {
				// keep default value for viewed
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " MacId: " + macId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return viewed;
	}

	/**
     * Puts a record into PostViews table to keep track of the post views.
     * @param postId postId
     * @param macId Identifier for client machine (IP address for now)
     */
	public void putPostViews(String postId, String macId) {

		try {

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("PostId", new AttributeValue().withS(postId));
            item.put("MacId", new AttributeValue().withS(macId));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("PostViews")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId + " macId=" + macId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Deletes all records from the PostViews table for a given postId
     * @param postId postId
     */
    public void deleteViews (String postId) {

		try {
			// Conditions for Votes table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));

		    Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
			    // Query Votes table
			    QueryRequest request = new QueryRequest()
	                .withTableName("PostViews")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("MacId");
		        QueryResult result = dynamoDBClient.query(request);

            	// Prepare key for delete
            	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            	key.put("PostId", new AttributeValue().withS(postId));
	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("PostViews")
		   			.withKey(key);

		        // Loop through each vote and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {
	            	key.put("MacId", new AttributeValue().withS(item.get("MacId").getS()));
		        	dynamoDBClient.deleteItem(deleteItemRequest);
	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Increments HI value in TopCharts table for a given POST. This is needed for Alltime TopCharts.
     * @param postId postId
     * @param incrementBy number with which HI needs to be incremented
     **/
	public void incrementPostTopCharts (String postId, long incrementBy) {

    	try {
            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("Name", new AttributeValue().withS("PO"));
            keyUpdateTA.put("Id", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("HI",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("TopCharts")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA);
            dynamoDBClient.updateItem(requestTA);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Deletes a given Post from TopCharts table for Alltime stats.
     * @param postId postId
     * @return Post Heat Index for the deleted post
     **/
	public long deletePostTopCharts(String postId) {

		long hi = 0;
    	try {
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("Name", new AttributeValue().withS("PO"));
        	key.put("Id", new AttributeValue().withS(postId));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("TopCharts")
	   			.withKey(key)
	   			.withReturnValues("ALL_OLD");

        	DeleteItemResult result = dynamoDBClient.deleteItem(deleteItemRequest);
        	try {
        		hi = Long.parseLong(result.getAttributes().get("HI").getN());
        	} catch (Exception e) {
        		// do nothing
        	}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
    	return hi;

	}

	/**
     * Deletes a given Post from TopCharts table for Just Published.
     * @param postId postId
     **/
	public void deleteJustPublished(String postId) {

    	try {

        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("Name", new AttributeValue().withS("J"));
        	key.put("Id", new AttributeValue().withS(postId));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("TopCharts")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Set PublishFlag=D in Posts table.
     * @param postId PostId
     * @param updateDate Delete Date in UTC format
     **/
	public void setPostDeleted (String postId, long updateDate) {

    	try {
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();

            updateItems.put("PublishFlag",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS("D"))
           			.withAction(AttributeAction.PUT)
           		);

            updateItems.put("UpdateDate",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(updateDate+""))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Posts")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);

            dynamoDBClient.updateItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Deletes records from PostActivity table. Since Hash Key is Period, we start with the
     * current period and travel through the number of periods that will be processed by Top Charts.
     * @param postId postId
     **/
	public void deletePostActivity (String postId) {

		long deleteCount = 0;
    	try {

    		// Number of periods to read HI data
    		int topChartsProcessPeriodCount = Integer.parseInt(config.getProperty("topChartsProcessPeriodCount"));
    		// Get the current period
            long currentRunPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

            for (long i=currentRunPeriod; i>=currentRunPeriod-topChartsProcessPeriodCount; i--) {

	        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	        	key.put("Period", new AttributeValue().withN(i+""));
	        	key.put("PostId", new AttributeValue().withS(postId));

	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("PostActivity")
		   			.withKey(key);
	        	dynamoDBClient.deleteItem(deleteItemRequest);

	        	deleteCount++;
				if (deleteCount % 10 == 0) {
					logger.log(Level.INFO, "Deleted " + deleteCount + " rows from PostActivity.");
					Thread.sleep(10000);
				}

            }

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

    /**
     * Run Asynchronous operations needed to Delete a post.
     * @param bloggerId userId of the Blogger
     * @param zoneId zoneId
     * @param pageId pageId
     * @param postId postId
     */
	public void deletePostAsync(String bloggerId, String zoneId, String pageId, String postId, String publishFlag) {

		String stepName = null;

		try {
		    /*** Delete from TopCharts table for JustPublished ***/
			stepName = "Delete from JustPublished";
			deleteJustPublished(postId);

        	/*** Get Votes from Posts table ***/
			stepName = "Get from Posts";
			int votes = getPostVotes(postId); // UpVotes - DownVotes

        	/*** Delete from PostViews table ***/
			stepName = "Delete from PostViews";
			deleteViews(postId);

        	/*** Delete from Votes table ***/
			stepName = "Delete from Votes";
			deleteVotes(postId);

        	/*** Delete from Ranking table ***/
			stepName = "Delete from Ranking";
			deleteRanking(zoneId, postId);

		    /*** Delete from Comments table ***/
			stepName = "Delete from Comments";
			int comments = deleteComments(postId);

		    /*** Reset Post Stats ***/
			stepName = "Reset Post Stats";
			resetPostStats(postId);
			deletePostActivity(postId);
			long postHI = deletePostTopCharts(postId);

		    /*** Decrement Votes ***/
			stepName = "Decrement Votes";
			if ( votes != 0 ) {
				incrementZoneStat("M", zoneId, "Votes", true, -votes );
	  			if (StringUtils.isNotBlank(pageId)) {
	  				incrementEntityStat(pageId, "Votes", true, -votes );
	  			}
				incrementEntityStat(bloggerId, "Votes", true, -votes );
			}

		    /*** Decrement Comments ***/
			stepName = "Decrement Comments";
			if ( comments != 0 ) {
				incrementZoneStat("M", zoneId, "Comments", true, -comments );
	  			if (StringUtils.isNotBlank(pageId)) {
	  				incrementEntityStat(pageId, "Comments", true, -comments );
	  			}
				incrementEntityStat(bloggerId, "Comments", true, -comments );
			}

		    /*** Decrement postHI ***/
			stepName = "Decrement postHI";
			if ( postHI != 0 ) {
				incrementBloggerTopCharts(bloggerId, -postHI );
	  			if (StringUtils.isNotBlank(pageId)) {
	  				incrementPageTopCharts(pageId, -postHI );
	  			}
				incrementZoneTopCharts(zoneId, -postHI );
			}
			// if published, also subtract 10 from ZoneHI
			if ( StringUtils.equals(publishFlag,"Y") ) {
				incrementZoneTopCharts(zoneId, -10 );
				incrementZoneStat("M", zoneId, "HI", false, -10 );
			}

		    /*** Decrement Posts ***/
			stepName = "Decrement Posts";
			if ( StringUtils.equals(publishFlag,"Y") ) {
				incrementZoneStat("ALL", zoneId, "Posts", true, -1 );
	  			if (StringUtils.isNotBlank(pageId)) {
	  				incrementEntityStat(pageId, "Posts", false, -1 );
	  			}
				incrementEntityStat(bloggerId, "Posts", false, -1 );
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Step Name: " + stepName + " BloggerId: " + bloggerId + " PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Queries related posts.
     * @param postId postId
     * @param postTitle postTitle
     * @param RPCPeriod Related Posts Calculated Period
     * @return List of Map objects with Related Posts information
    */
	public List<Map<String,AttributeValue>> getRelatedPosts (String postId, String postTitle, long RPCPeriod) {

		QueryResult result = new QueryResult();
		try {

    		/*** Get the current period ***/
            long currentRunPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

    		/*** Get the lastSearchErrorPeriod ***/
            long lastSearchErrorPeriod = Long.parseLong(config.getProperty("lastSearchErrorPeriod"));

            /*** If RPCPeriod is more than 180 days old, then recalculate Related Posts ***/
            if ( currentRunPeriod > RPCPeriod+180*4 ) { // there are 4 periods in a day

            	try {
            		// Proceed only if there are no recent search errors
            		if ( currentRunPeriod > lastSearchErrorPeriod ) {
	                	/*** Delete existing related posts ***/
		    			// Conditions for RelatedPosts table
		    			HashMap<String, Condition> conditions1 = new HashMap<String, Condition>();
		    			conditions1.put("PostId",
		    				new Condition()
		    					.withComparisonOperator(ComparisonOperator.EQ)
		    					.withAttributeValueList(new AttributeValue().withS(postId)));
		
		    		    Map<String, AttributeValue> lastEvaluatedKey1 = null;
		    		    do {
		    			    // Query RelatedPosts table
		    			    QueryRequest request1 = new QueryRequest()
		    	                .withTableName("RelatedPosts")
		    	                .withKeyConditions(conditions1)
		    	                .withExclusiveStartKey(lastEvaluatedKey1)
		    	                .withAttributesToGet("RelatedPostId");
		    		        QueryResult result1 = dynamoDBClient.query(request1);
		
		                	// Prepare key for delete
		                	HashMap<String, AttributeValue> key1 = new HashMap<String, AttributeValue>();
		                	key1.put("PostId", new AttributeValue().withS(postId));
		    	        	DeleteItemRequest deleteItemRequest1 = new DeleteItemRequest()
		    		            .withTableName("RelatedPosts")
		    		   			.withKey(key1);
		
		    		        // Loop through each record and delete
		    	            for (Map<String, AttributeValue> item1 : result1.getItems()) {
		    	            	key1.put("RelatedPostId", new AttributeValue().withS(item1.get("RelatedPostId").getS()));
		    		        	dynamoDBClient.deleteItem(deleteItemRequest1);
		    	            }
		
		    	        	lastEvaluatedKey1 = result1.getLastEvaluatedKey();
		    		    } while (lastEvaluatedKey1 != null);
	
		    		    /*** Create related posts ***/
		    	    	List<String> searchResultList = getSearchResults ("Any", "PO", postTitle);
		    	    	int limit = 6; // limit 6 posts
		    	   		for (int i = 0; i < searchResultList.size() && i < limit; i++) {
		   	   				Post post = getPost(searchResultList.get(i));
		   	   				if ( post == null ) {
		   	   					limit++;
		   	   				} else if ( StringUtils.equals(post.getPostId(), postId) ) {
	   	   	   					limit++;
	   	   					} else {
			   	   				HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				   	         	item.put("PostId", new AttributeValue().withS(postId));
				   	         	item.put("RelatedPostId", new AttributeValue().withS(post.getPostId()));
				   	         	item.put("PostTitle", new AttributeValue().withS(post.getPostTitle()));
				   	         	item.put("PostHeadshot", new AttributeValue().withS(post.getPostHeadshot()));
				   	         	item.put("PostSummary", new AttributeValue().withS(post.getPostSummary()));
		
				   	         	PutItemRequest putItemRequest = new PutItemRequest()
				   	 	            .withTableName("RelatedPosts")
				   	 	   			.withItem(item);
				   	         	dynamoDBClient.putItem(putItemRequest);
		   	   				}
		    	   		}
            		}

	    	   		/*** Update Posts table ***/
	   	    		HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
	   	            keyUpdate.put("PostId", new AttributeValue().withS(postId));

	   	            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
	   	            updateItems.put("RPCPeriod",
	   	           		new AttributeValueUpdate()
	   	           			.withValue(new AttributeValue().withN(currentRunPeriod+""))
	   	           			.withAction(AttributeAction.PUT)
	   	           		);

	   	            UpdateItemRequest request = new UpdateItemRequest()
	   	            	.withTableName("Posts")
	   	            	.withKey(keyUpdate)
	   	            	.withAttributeUpdates(updateItems);
	   	            dynamoDBClient.updateItem(request);

            	} catch (Exception e) {
            		// if there are errors during delete/create related posts, we will log them
            		// but we will proceed with querying existing records for display
            		logger.log(Level.SEVERE,"postId: " + postId);
            		logger.log(Level.SEVERE,e.getMessage(),e);
   	            }
            }

        	/*** Query related posts ***/
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(postId));
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("PostId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("RelatedPosts")
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**************************************************************************
     ***********************  RANKING MODULE  *********************************
     **************************************************************************/

    /**
     * Initializes Ranking record for a new post with zero PostHI. This is needed for Sorting posts in a given zoneId.
     * @param zoneId Id of the Zone
     * @param postId postId being published
     */
    public void initializeRanking(String zoneId, String postId) {
		try {
			HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("ZoneId", new AttributeValue().withS(zoneId));
        	item.put("PostId", new AttributeValue().withS(postId));
        	item.put("PostHI", new AttributeValue().withN( 0+"" ));

        	PutItemRequest putItemRequest = new PutItemRequest()
	            .withTableName("Ranking")
	   			.withItem(item);

        	dynamoDBClient.putItem(putItemRequest);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId: " + zoneId + " postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Deletes record from Ranking table when a post is unpublished.
     * @param zoneId Id of the zone
     * @param postId postId
     */
	public void deleteRanking(String zoneId, String postId) {

		try {
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("ZoneId", new AttributeValue().withS(zoneId));
        	key.put("PostId", new AttributeValue().withS(postId));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("Ranking")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"ZoneId: " + zoneId + " PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Increments PostHI value in Ranking table. In case of Down Vote, pass a negative value for incrementBy.
     * @param zoneId Id of the zone
     * @param postId PostId
     * @param incrementBy number to be incremented (pass a negative value for Down Vote)
     **/
	public void incrementRanking(String zoneId, String postId, long incrementBy) {

    	try {

    		HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("ZoneId", new AttributeValue().withS(zoneId));
            keyUpdate.put("PostId", new AttributeValue().withS(postId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("PostHI",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Ranking")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " incrementBy: " + incrementBy);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**************************************************************************
     ************************** VOTE MODULE ***********************************
     **************************************************************************/

	/**
     * Gets current vote for a given user from Votes table.
     * @param userId userId
     * @param postId postId
     * @return Integer +1 or -1 representing current vote by user; 0 if not voted
     **/
	public int getVote (String userId, String postId) {
		int vote = 0; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));
			key.put("VoterId", new AttributeValue().withS(userId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Votes")
			    .withKey(key);

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			vote = Integer.parseInt(result.getItem().get("Vote").getN());

		} catch (NullPointerException npe) {
	        return vote; // record doesn't exist, return default value (0)

		} catch (NumberFormatException npe) {
	        return vote; // parseInt failed, return default value (0)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return vote;
	}

	/**
     * Put user's vote into Votes table.
     * @param userId UserId
     * @param postId PostId
     * @param newVote Integer +1 or -1 representing new vote by user
     **/
	public void putVote (String userId, String postId, int newVote) {

    	try {

	    	/*** Read UTC time from System ***/
    		long voteDate = System.currentTimeMillis();

    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("PostId", new AttributeValue().withS(postId));
        	item.put("VoterId", new AttributeValue().withS(userId));
        	item.put("Vote", new AttributeValue().withN( newVote+"" ));
        	item.put("VoteDate", new AttributeValue().withN( voteDate+"" ));

        	PutItemRequest request = new PutItemRequest()
	            .withTableName("Votes")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " PostId: " + postId + " Vote: " + newVote);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

    /**
     * Deletes all records from the Votes table for a given postId
     * @param postId postId
     */
    public void deleteVotes (String postId) {

		try {
			// Conditions for Votes table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));

		    Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
			    // Query Votes table
			    QueryRequest request = new QueryRequest()
	                .withTableName("Votes")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("VoterId");
		        QueryResult result = dynamoDBClient.query(request);

            	// Prepare key for delete
            	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            	key.put("PostId", new AttributeValue().withS(postId));
	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("Votes")
		   			.withKey(key);

		        // Loop through each vote and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {
	            	key.put("VoterId", new AttributeValue().withS(item.get("VoterId").getS()));
		        	dynamoDBClient.deleteItem(deleteItemRequest);
	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**************************************************************************
     ************************** PROFILE MODULE ********************************
     **************************************************************************/

    /**
     * Queries entity details from Entities table.
     * @param entityId Id of the blogger or page
     * @return Entity object with entity details
     */
	public Entity getEntity (String entityId) {
		entityId = StringUtils.lowerCase(entityId); // entityId is stored in lower case
		Entity entity = null; // default
		try {
			entity = dynamoDBMapper.load(Entity.class, entityId);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return entity;
    }

	/**
     * Updates Entities table with ProfileColor
     * @param entityId entityId
     * @param profileColor
     */
	public void updateProfileColor (String entityId, String profileColor) {

		try {

		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("ProfileColor",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(profileColor))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " profileColor=" + profileColor);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with ContactColor
     * @param entityId entityId
     * @param contactColor
     */
	public void updateContactColor (String entityId, String contactColor) {

		try {

		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("ContactColor",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(contactColor))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " contactColor=" + contactColor);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Email
     * @param entityId entityId
     * @param email Email Address
     */
	public void updateEmail (String entityId, String email) {

		try {

		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Email",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(email))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " email=" + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Phone
     * @param entityId entityId
     * @param phone phone
     */
	public void updatePhone (String entityId, String phone) {

		try {

		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Phone",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(phone))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " phone=" + phone);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Address
     * @param entityId entityId
     * @param address street address
     */
	public void updateAddress (String entityId, String address) {

		try {

		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Address",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(address))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " address=" + address);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Website
     * @param entityId entityId
     * @param webste website
     */
	public void updateWebsite (String entityId, String website) {

		try {

		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Website",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(website))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " website=" + website);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with ProfilePhoto
     * @param entityId entityId
     * @param profilePhoto URL of the Profile Photo
     */
	public void updateProfilePhoto(String entityId, String profilePhoto) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));
	
		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("ProfilePhoto",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(profilePhoto))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " profilePhoto=" + profilePhoto);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with ProfileBG
     * @param entityId entityId
     * @param profileBG URL of the Profile Background
     */
	public void updateProfileBG(String entityId, String profileBG) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));
	
		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("ProfileBG",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(profileBG))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " profileBG=" + profileBG);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Logo
     * @param entityId entityId
     * @param logo URL of the Logo
     */
	public void updateLogo (String entityId, String logo) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Logo",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(logo))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " logo=" + logo);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with EnableEmail
     * @param entityId entityId
     * @param enableEmail Y or N to denote enable or disable
     */
	public void updateEnableEmail(String entityId, String enableEmail) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("EnableEmail",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(enableEmail))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Contact
     * @param entityId entityId
     * @param contact Contact information of the blogger or the page
     */
	public void updateEntityContact (String entityId, String entityContact) {

		try {
		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Contact",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(entityContact))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with TimeZone
     * @param entityId entityId
     * @param timeZone Time Zone information of the blogger or the page
     */
	public void updateEntityTimeZone (String entityId, String entityTimeZone) {

		try {
		    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("TimeZone",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(entityTimeZone))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with EntityEmail
     * @param entityId entityId
     * @param EntityEmail Email Address of the blogger
     */
	public void updateEntityEmail(String entityId, String entityEmail) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("EntityEmail",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(entityEmail))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Facebook Id
     * @param entityId entityId
     * @param fbId Facebook Id
     */
	public void updateFbId(String entityId, String fbId) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    if ( StringUtils.isBlank(fbId) ) {
			    updateItems.put("FbId",
		    		new AttributeValueUpdate()
		                .withAction(AttributeAction.DELETE)
		    		);
		    } else {
			    updateItems.put("FbId",
			    	new AttributeValueUpdate()
			    		.withValue(new AttributeValue().withS(fbId))
			    		.withAction(AttributeAction.PUT)
		    		);
		    }

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Google Id
     * @param entityId entityId
     * @param googleId Google Id
     */
	public void updateGoogleId(String entityId, String googleId) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    if ( StringUtils.isBlank(googleId) ) {
		    	updateItems.put("GoogleId",
		    		new AttributeValueUpdate()
		    			.withAction(AttributeAction.DELETE)
		    		);
		    } else {
			    updateItems.put("GoogleId",
			    	new AttributeValueUpdate()
			    		.withValue(new AttributeValue().withS(googleId))
			    		.withAction(AttributeAction.PUT)
				);
		    }

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

    /**
     * Updates Entities table with Location Information
     * @param entityId entityId
     * @param country country name
     * @param state state name
     * @param city city name
     */
	public void updateEntityLocation (String entityId, String country, String state, String city) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("Country",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(country))
	               .withAction(AttributeAction.PUT)
		    	);
		    updateItems.put("State",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(state))
	                .withAction(AttributeAction.PUT)
	    		);
		    updateItems.put("City",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(city))
		            .withAction(AttributeAction.PUT)
		    	);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Updates Entities table with Bio
     * @param entityId entityId
     * @param about about
     * @param passion passion
     * @param achievements achievements
     * @param announcements announcements
     * @param contact contact
     */
	public void updateBio (String entityId, String about, String passion, String achievements, String announcements, String contact) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("About",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(about+" "))
	               .withAction(AttributeAction.PUT)
		    	);
		    updateItems.put("Passion",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(passion+" "))
	                .withAction(AttributeAction.PUT)
	    		);
		    updateItems.put("Achievements",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(achievements+" "))
		            .withAction(AttributeAction.PUT)
		    	);
		    updateItems.put("Announcements",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(announcements+" "))
	                .withAction(AttributeAction.PUT)
	    		);
		    updateItems.put("Contact",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(contact+" "))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Queries 6 previous or next posts for Profile page.
     * First 5 posts will be displayed on the page - 6th post will be used to decide whether or not to display next or previous page links.
     * @param bloggerId userId of the blogger
     * @param publishFlag Y to query Published posts, N to query Draft posts, D to query Deleted posts
     * @param lastEvaluatedBI starting Post Id for the next or previous page
     * @param lastEvaluatedUD starting Update Date for the next or previous page
     * @param isForward false for Forward and true for Reverse (since we want recently updated posts first)
     * @return List of Map objects with postId's
    */
	public List<Map<String,AttributeValue>> getPostsByEntity(String bloggerId, String publishFlag, String lastEvaluatedBI, String lastEvaluatedUD, Boolean isForward) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();

		try {
            if ( StringUtils.isBlank(lastEvaluatedUD) ) {
            	lastEvaluatedKey = null;
            } else {
	    		/*** Prepare starting record for the next or previous page ***/
	       		lastEvaluatedKey.put("EntityId-PublishFlag", new AttributeValue().withS(bloggerId+"-"+publishFlag));
	       		lastEvaluatedKey.put("PostId", new AttributeValue().withS(lastEvaluatedBI));
	       		lastEvaluatedKey.put("UpdateDate", new AttributeValue().withN(lastEvaluatedUD));
            }

        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(bloggerId+"-"+publishFlag));

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("EntityId-PublishFlag", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("PostsByEntity")
       			.withIndexName("UpdateDateIdx")
       			.withKeyConditions(keyConditions)
       			.withScanIndexForward(isForward)
       			.withLimit(6)
       			.withExclusiveStartKey(lastEvaluatedKey);

       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries 6 previous or next admin zones for Profile page.
     * First 5 zones will be displayed on the page - 6th zone will be used to decide whether or not to display next or previous page links.
     * @param bloggerId userId of the blogger
     * @param lastEvaluatedUI starting User Id for the next or previous page
     * @param lastEvaluatedZI starting Zone Id for the next or previous page
     * @param isForward true for Forward and false for Reverse (zones are sorted in the alphabetical order)
     * @return List of Map objects with zoneId's
    */
	public List<Map<String,AttributeValue>> getAdminZones(String bloggerId, String lastEvaluatedUI, String lastEvaluatedZI, Boolean isForward) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();

		try {
            if ( StringUtils.isBlank(lastEvaluatedUI) ) {
            	lastEvaluatedKey = null;
            } else {
	    		/*** Prepare starting record for the next or previous page ***/
	       		lastEvaluatedKey.put("UserId", new AttributeValue().withS(bloggerId));
	       		lastEvaluatedKey.put("ZoneId", new AttributeValue().withS(lastEvaluatedZI));
            }

        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(bloggerId));

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("UserId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("AdminZones")
       			.withKeyConditions(keyConditions)
       			.withScanIndexForward(isForward)
       			.withLimit(6)
       			.withExclusiveStartKey(lastEvaluatedKey)
       			.withAttributesToGet("ZoneId");

       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Saves PostId into PostsByEntity table, so it can be queried from Profile page
     * @param entityId EntityId
     * @param publishFlag Y or N or D
     * @param updateDate Create, Publish or Delete date in UTC format
     * @param postId postId of the post
     */
	public void savePostsByEntity (String entityId, String publishFlag, long updateDate, String postId) {

		try {
			// if publishFlag=D --> delete publishFlag=Y if exists
			if ( publishFlag.equals("D") ) {
				// delete the published post, if exists
	        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	        	key.put("EntityId-PublishFlag", new AttributeValue().withS(entityId+"-Y"));
	        	key.put("PostId", new AttributeValue().withS(postId));

	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("PostsByEntity")
		   			.withKey(key);
	        	dynamoDBClient.deleteItem(deleteItemRequest);
			}

			// if publishFlag=Y --> delete publishFlag=D if exists
			if ( publishFlag.equals("Y") ) {
				// delete the published post, if exists
	        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	        	key.put("EntityId-PublishFlag", new AttributeValue().withS(entityId+"-D"));
	        	key.put("PostId", new AttributeValue().withS(postId));

	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("PostsByEntity")
		   			.withKey(key);
	        	dynamoDBClient.deleteItem(deleteItemRequest);
			}

			// if publishFlag=Y or D --> delete publishFlag=N if exists
			if ( publishFlag.equals("Y") || publishFlag.equals("D") ) {
				// delete the draft, if exists
	        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	        	key.put("EntityId-PublishFlag", new AttributeValue().withS(entityId+"-N"));
	        	key.put("PostId", new AttributeValue().withS(postId));

	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("PostsByEntity")
		   			.withKey(key);
	        	dynamoDBClient.deleteItem(deleteItemRequest);
			}

			// insert into PostsByEntity
			HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("EntityId-PublishFlag", new AttributeValue().withS(entityId+"-"+publishFlag));
        	item.put("PostId", new AttributeValue().withS(postId));
        	item.put("UpdateDate", new AttributeValue().withN(updateDate+""));

        	PutItemRequest putItemRequest = new PutItemRequest()
	            .withTableName("PostsByEntity")
	   			.withItem(item);
        	dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Queries 6 previous or next pages for Profile page.
     * First 5 pages will be displayed on the page - 6th page will be used to decide whether or not to display next or previous page links.
     * @param bloggerId userId of the blogger
     * @param lastEvaluatedPosition starting Position for the next or previous page
     * @param isForward true for Forward and false for Reverse
     * @return List of Map objects with pageId's
    */
	public List<Map<String,AttributeValue>> getBloggerPages (String bloggerId, String lastEvaluatedBloggerPos, Boolean isForward) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();

		try {
            if ( StringUtils.isBlank(lastEvaluatedBloggerPos) ) {
            	lastEvaluatedKey = null;
            } else {
	    		/*** Prepare starting record for the next or previous page ***/
	       		lastEvaluatedKey.put("BloggerId", new AttributeValue().withS(bloggerId));
	       		lastEvaluatedKey.put("BloggerPos", new AttributeValue().withS(lastEvaluatedBloggerPos));
            }

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("BloggerId", new Condition()
            	.withComparisonOperator(ComparisonOperator.EQ.toString())
            	.withAttributeValueList(new AttributeValue().withS(bloggerId)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("PageBloggers")
       			.withIndexName("BloggerPosIdx")
       			.withKeyConditions(keyConditions)
       			.withScanIndexForward(isForward)
       			.withLimit(6)
       			.withExclusiveStartKey(lastEvaluatedKey)
       			.withAttributesToGet("BloggerPos", "PageId", "PageName", "AdminFlag");
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries the list of pages that the blogger has access to.
     * @param bloggerId userId of the blogger
     * @return List of Map objects with pageId's
    */
	public List<Map<String,AttributeValue>> getBloggerPages (String bloggerId) {

		QueryResult result = new QueryResult();
		try {
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("BloggerId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(bloggerId)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("PageBloggers")
       			.withIndexName("BloggerPosIdx")
       			.withKeyConditions(keyConditions)
       			.withAttributesToGet("BloggerPos","PageId","PageName");
       		result = dynamoDBClient.query(queryRequest);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();

	}

	/**
     * Increment the given statistic in Entities table. Also increment HI if incrementHI is true.
     * @param entityId entityId
     * @param statName statistic to increment. Can be Posts, Votes or Comments
     * @param incrementHI whether to increment HI or not
     * @param incrementBy number with which the stat needs to be incremented
     **/
	public void incrementEntityStat(String entityId, String statName, boolean incrementHI, long incrementBy) {

    	try {

    		HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("EntityId", new AttributeValue().withS(entityId));

            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put(statName,
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);
            if ( incrementHI ) {
            	int factor = 0;
            	if ( StringUtils.equals(statName,"Posts") )
            		factor = 0;
            	else if ( StringUtils.equals(statName,"Votes") )
            		factor = 2;
            	else if ( StringUtils.equals(statName,"Comments") )
            		factor = 3;
	            updateItems.put("HI",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withN(incrementBy*factor+""))
	           			.withAction(AttributeAction.ADD)
	           		);
            }

            UpdateItemRequest request = new UpdateItemRequest()
            	.withTableName("Entities")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"EntityId: " + entityId + " statName: " + statName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Increments HI value in TopCharts table for Blogger. This data is used for Alltime TopCharts.
     * @param bloggerId userId of the Blogger
     * @param incrementBy number with which HI needs to be incremented
     **/
	public void incrementBloggerTopCharts (String bloggerId, long incrementBy) {

    	try {

            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("Name", new AttributeValue().withS("B"));
            keyUpdateTA.put("Id", new AttributeValue().withS(bloggerId));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("HI",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("TopCharts")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA);
            dynamoDBClient.updateItem(requestTA);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Deletes a given Blogger from TopCharts table for Alltime stats.
     * @param bloggerId bloggerId
     **/
	public void deleteBloggerTopCharts (String bloggerId) {

    	try {
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("Name", new AttributeValue().withS("B"));
        	key.put("Id", new AttributeValue().withS(bloggerId));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("TopCharts")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**************************************************************************
     ***********************  PAGE MODULE  ************************************
     **************************************************************************/

	/**
     * Sets PrimaryPageId for the Entity.
     * @param entityId
     * @param pageId
     */
	public void setPrimaryPageId (String entityId, String pageId) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(entityId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("PrimaryPageId",
		    	new AttributeValueUpdate()
		    		.withValue(new AttributeValue().withS(pageId))
		               .withAction(AttributeAction.PUT)
		    	);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId=" + entityId + " pageId=" + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Checks if a page is active.
     * @param pageId Entity Id of the page
     * @return true or false depending on whether the page is active or not
     **/
	public boolean isPageActive (String entityId) {
		boolean isPageActive = false; // default
		try {

			// current date plus seven days of grace period
    		long currentDate = System.currentTimeMillis() - 7*24*60*60*1000L;

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key);

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			try {
				long endDate = Long.parseLong(result.getItem().get("EndDate").getN());
				if ( endDate == 1 || endDate > currentDate ) isPageActive = true;
			} catch (Exception e) {}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"EntityId: " + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return isPageActive;
	}

    /**
     * Inserts into PendingPagePayments table
     * @param paymentHandler Unique Id for Pending Page Payments
     * @param createdBy UserId of the person making the payment
     * @param pageId Page Id
     * @param productType Product Type
     * @param amount Amount
     * @param coupon Coupon
     * @param endDate End Date
     */
	public void putPendingPagePayments (String paymentHandler, String createdBy, String pageId,
			String productType, long amount, String coupon, long endDate) {

		try {

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		    item.put("PaymentHandler", new AttributeValue().withS(paymentHandler));
		    item.put("HandlerDate", new AttributeValue().withN(System.currentTimeMillis()+""));
		    item.put("CreatedBy", new AttributeValue().withS(createdBy));
		    item.put("PageId", new AttributeValue().withS(pageId));
		    item.put("ProductType", new AttributeValue().withS(productType));
		    item.put("Amount", new AttributeValue().withN(amount+""));
		    if ( StringUtils.isNotBlank(coupon) ) item.put("Coupon", new AttributeValue().withS(coupon));
		    item.put("EndDate", new AttributeValue().withN(endDate+""));
		    item.put("Status", new AttributeValue().withS("NONE"));
		    PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("PendingPagePayments")
		    	.withItem(item);
			dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"paymentHandler=" + paymentHandler + " createdBy=" + createdBy +
	        	" pageId=" + pageId + " productType=" + productType + " amount=" + amount +
	        	" coupon=" + coupon + " endDate=" + endDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Queries pending page payment details from PendingPagePayments table.
     * @param paymentHandler paymentHandler
     * @return PendingPagePayment object with pending page payment details
     */
	public PendingPagePayment getPendingPagePayment (String paymentHandler) {
		PendingPagePayment ppp = null; // default
		try {
        	ppp = dynamoDBMapper.load(PendingPagePayment.class, paymentHandler);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"paymentHandler=" + paymentHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return ppp;
    }

    /**
     * Processes the given Pending Page Payment for Success
     * @param paymentHandler paymentHandler
     */
	public String pagePaymentSuccess (String paymentHandler) {

		String pageId = "NULL";
		try {

			/*** Read from PendingPagePayments table ***/
			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PaymentHandler", new AttributeValue().withS(paymentHandler));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("PendingPagePayments")
			    .withKey(key);

			Map<String, AttributeValue> item = null;
			try {
				item = dynamoDBClient.getItem(getItemRequest).getItem();
				// pageId is needed by the calling controller method
				pageId = item.get("PageId").getS();
			} catch (Exception e) {
				return "Payment doesn't exist.";
			}

			try {
				if ( StringUtils.equals(item.get("Status").getS(), "SUCCESS") ) {
					return "Payment has already beed processed.";
				}
			} catch (Exception e) {
				// ignore if there is no Status column
			}

			/*** Derive start date ***/
    		long startDate = 0;
    		long currentUTC = System.currentTimeMillis();
			Entity page = getEntity(item.get("PageId").getS());
			if ( page.getEndDate() <= currentUTC ) {
    			startDate = currentUTC;
    		} else {
    			startDate = page.getEndDate();
    		}

			/*** Derive end date ***/
			long endDate = Long.parseLong(item.get("EndDate").getN());
			if ( endDate != 1 ) {
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTimeInMillis(startDate);
	    		if ( StringUtils.endsWith(item.get("ProductType").getS(), "monthly") ) {
	    			cal.add(Calendar.MONTH, 1);
	    		} else {
	    			cal.add(Calendar.YEAR, 1);
	    		}
	    		endDate = cal.getTimeInMillis();
			}

			/*** Insert into PagePayments table ***/
			HashMap<String, AttributeValue> item1 = new HashMap<String, AttributeValue>();
        	item1.put("PageId", item.get("PageId"));
        	item1.put("ProductType", item.get("ProductType"));
        	item1.put("Amount", item.get("Amount"));
        	try { item1.put("Coupon", item.get("Coupon")); } catch (Exception e) {}
        	item1.put("StartDate", new AttributeValue().withN(startDate+""));
        	item1.put("EndDate", new AttributeValue().withN(endDate+""));
        	item1.put("PaymentHandler", item.get("PaymentHandler"));

        	PutItemRequest putItemRequest = new PutItemRequest()
	            .withTableName("PagePayments")
	   			.withItem(item1);
        	dynamoDBClient.putItem(putItemRequest);

			/*** Update Entities table ***/
            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("EntityId", new AttributeValue().withS(pageId));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("EndDate",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(endDate+""))
           			.withAction(AttributeAction.PUT)
           		);
            updateItemsTA.put("ProductType",
              	new AttributeValueUpdate()
              		.withValue(item.get("ProductType"))
               		.withAction(AttributeAction.PUT)
               	);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("Entities")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA);
            dynamoDBClient.updateItem(requestTA);

			/*** Update PendingPagePayments table ***/
            HashMap<String, AttributeValue> keyUpdatePP = new HashMap<String, AttributeValue>();
            keyUpdatePP.put("PaymentHandler", new AttributeValue().withS(paymentHandler));

            Map<String, AttributeValueUpdate> updateItemsPP = new HashMap<String, AttributeValueUpdate>();
            updateItemsPP.put("Status",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS("SUCCESS"))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestPP = new UpdateItemRequest()
            	.withTableName("PendingPagePayments")
            	.withKey(keyUpdatePP)
            	.withAttributeUpdates(updateItemsPP);
            dynamoDBClient.updateItem(requestPP);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"paymentHandler=" + paymentHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

		return "SUCCESS:"+pageId;
	}

    /**
     * Processes the given Pending Page Payment for Error
     * @param paymentHandler paymentHandler
     */
	public String pagePaymentError (String paymentHandler) {

		String pageId = "NULL";
		try {

			/*** Update PendingPagePayments table ***/
            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("PaymentHandler", new AttributeValue().withS(paymentHandler));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("Status",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS("ERROR"))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("PendingPagePayments")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA)
   				.withReturnValues("ALL_OLD");
            UpdateItemResult result = dynamoDBClient.updateItem(requestTA);

            pageId = result.getAttributes().get("PageId").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"paymentHandler=" + paymentHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

		return pageId;
	}

    /**
     * Processes the given Pending Page Payment for Delete
     * @param paymentHandler paymentHandler
     */
	public String pagePaymentDelete (String paymentHandler) {

		String pageId = "NULL";
		try {

			/*** Update PendingPagePayments table ***/
            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("PaymentHandler", new AttributeValue().withS(paymentHandler));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("Status",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS("DELETED"))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("PendingPagePayments")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA)
   				.withReturnValues("ALL_OLD");
            UpdateItemResult result = dynamoDBClient.updateItem(requestTA);

            pageId = result.getAttributes().get("PageId").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"paymentHandler=" + paymentHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

		return pageId;
	}

	/**
     * Queries page payments.
     * @param pageId pageId
     * @return List of Page Payments
    */
	public List<Map<String,AttributeValue>> getPagePayments (String pageId) {

		QueryResult result = new QueryResult();
		try {

        	/*** Query page payments ***/
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(pageId));
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("PageId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("PagePayments")
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId: " + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries 6 previous or next bloggers for Profile page.
     * First 5 bloggers will be displayed on the page - 6th record will be used to decide whether or not to display next or previous page links.
     * @param bloggerId userId of the blogger
     * @param lastEvaluatedPosition starting Position for the next or previous page
     * @param isForward true for Forward and false for Reverse
     * @return List of Map objects with bloggerId's
    */
	public List<Map<String,AttributeValue>> getPageBloggers(String pageId, String lastEvaluatedPagePos, Boolean isForward) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();

		try {
            if ( StringUtils.isBlank(lastEvaluatedPagePos) ) {
            	lastEvaluatedKey = null;
            } else {
	    		/*** Prepare starting record for the next or previous page ***/
	       		lastEvaluatedKey.put("PageId", new AttributeValue().withS(pageId));
	       		lastEvaluatedKey.put("PagePos", new AttributeValue().withN(lastEvaluatedPagePos));
            }

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("PageId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(pageId)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("PageBloggers")
       			.withIndexName("PagePosIdx")
       			.withKeyConditions(keyConditions)
       			.withScanIndexForward(isForward)
       			.withLimit(6)
       			.withExclusiveStartKey(lastEvaluatedKey)
       			.withAttributesToGet("PagePos","BloggerId","BloggerName","AdminFlag");
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Inserts records into PageBloggers table when a page has been created
     * or when a blog post order has been placed.
     * @param pageId
     * @param pageName
     * @param bloggerId Blogger Id
     * @param bloggerName Blogger Name
     * @param adminFlag Admin Flag (N: Can blog for the page; Y: Can blog for the page as well as edit the page)
     */
	public void putPageBloggers (String pageId, String pageName, String bloggerId, String bloggerName, String adminFlag) {

		try {

			// Get max Page Position
            int pagePos = 0;
            try {
	           	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
	           	keyConditions.put("PageId", new Condition()
		           	.withComparisonOperator(ComparisonOperator.EQ.toString())
		           	.withAttributeValueList(new AttributeValue().withS(pageId)));
	           	QueryRequest queryRequest = new QueryRequest()
	       			.withTableName("PageBloggers")
	       			.withIndexName("PagePosIdx")
	       			.withKeyConditions(keyConditions)
	       			.withScanIndexForward(false)
	       			.withLimit(1)
	       			.withAttributesToGet("PagePos");
	       		pagePos = Integer.parseInt(dynamoDBClient.query(queryRequest).getItems().get(0).get("PagePos").getN());
            } catch (Exception e) {
            	// keep the default value for page position
            }

			// Get max Blogger Position
            int bloggerPos = 0;
            try {
	           	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
	           	keyConditions.put("BloggerId", new Condition()
		           	.withComparisonOperator(ComparisonOperator.EQ.toString())
		           	.withAttributeValueList(new AttributeValue().withS(bloggerId)));
	           	QueryRequest queryRequest = new QueryRequest()
	       			.withTableName("PageBloggers")
	       			.withIndexName("BloggerPosIdx")
	       			.withKeyConditions(keyConditions)
	       			.withScanIndexForward(false)
	       			.withLimit(1)
	       			.withAttributesToGet("BloggerPos");
	       		bloggerPos = Integer.parseInt(dynamoDBClient.query(queryRequest).getItems().get(0).get("BloggerPos").getN());
            } catch (Exception e) {
            	// keep the default value for page position
            }

			Map<String, AttributeValue> itemP = new HashMap<String, AttributeValue>();
            itemP.put("PageId", new AttributeValue().withS(pageId));
            itemP.put("BloggerId", new AttributeValue().withS(bloggerId));
            itemP.put("PagePos", new AttributeValue().withN((pagePos+1)+""));
            itemP.put("BloggerPos", new AttributeValue().withN((bloggerPos+1)+""));
            itemP.put("PageName", new AttributeValue().withS(pageName));
            itemP.put("BloggerName", new AttributeValue().withS(bloggerName));
            itemP.put("AdminFlag", new AttributeValue().withS(adminFlag));
            PutItemRequest putItemRequestP = new PutItemRequest()
            	.withTableName("PageBloggers")
            	.withItem(itemP);
            dynamoDBClient.putItem(putItemRequestP);

            if ( bloggerPos == 0 ) {
    			// Set PrimaryPageId in the Entity table
    			setPrimaryPageId(bloggerId, pageId);
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId=" + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Increments HI value in TopCharts table for PAGE. This data is used for Alltime TopCharts.
     * @param pageId userId of the Page
     * @param incrementBy number with which HI needs to be incremented
     **/
	public void incrementPageTopCharts (String pageId, long incrementBy) {

    	try {

            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("Name", new AttributeValue().withS("PA"));
            keyUpdateTA.put("Id", new AttributeValue().withS(pageId));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();

            updateItemsTA.put("HI",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(incrementBy+""))
           			.withAction(AttributeAction.ADD)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("TopCharts")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA);

            dynamoDBClient.updateItem(requestTA);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId: " + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**************************************************************************
     ************************** ENTITY MODULE *********************************
     **************************************************************************/

	/**
     * Gets Entity Name for a given entityId from Entities table.
     * @param entityId Id of Blogger or Page
     * @return EntityName
     **/
	public String getEntityName (String entityId) {
		String entityName = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("EntityId", new AttributeValue().withS(entityId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(key);

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			entityName = result.getItem().get("EntityName").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"EntityId: " + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return entityName;
	}

	/**
     * Updates Entity Name in Entities and Posts tables.
     * @param entityId Entity Id
     * @param entityType Entity Type
     * @param entityName Entity Name
     **/
	public void updateEntityName(String entityId, String entityType, String entityName) {

    	try {
			/*** Update EntityName in Entities table ***/
            HashMap<String, AttributeValue> keyUpdateA = new HashMap<String, AttributeValue>();
            keyUpdateA.put("EntityId", new AttributeValue().withS(entityId));

            Map<String, AttributeValueUpdate> updateItemsA = new HashMap<String, AttributeValueUpdate>();

            updateItemsA.put("EntityName",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS(entityName))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestA = new UpdateItemRequest()
            	.withTableName("Entities")
            	.withKey(keyUpdateA)
            	.withAttributeUpdates(updateItemsA);

            dynamoDBClient.updateItem(requestA);

            /*************** 	For publishFlag=Y	 ********************/
			/*** Update EntityName in Posts table based on PostId's from PostsByEntity table ***/
	    	// Conditions for PostsByEntity table
			HashMap<String, Condition> conditionsBBAY = new HashMap<String, Condition>();
			conditionsBBAY.put("EntityId-PublishFlag",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(entityId+"-Y")));

		    Map<String, AttributeValue> lastEvaluatedKeyBBAY = null;
		    do {
		    	// Query PostsByEntity table
		    	QueryRequest queryRequestBBAY = new QueryRequest()
	                .withTableName("PostsByEntity")
	                .withKeyConditions(conditionsBBAY)
	                .withExclusiveStartKey(lastEvaluatedKeyBBAY);
		        QueryResult resultBBAY = dynamoDBClient.query(queryRequestBBAY);

		        // Loop through each post
			    String postId;
		        for (Map<String, AttributeValue> itemBBAY : resultBBAY.getItems()) {

		        	// Get postId for the record
		        	postId = itemBBAY.get("PostId").getS();

		        	// Now, update Posts table
		            HashMap<String, AttributeValue> keyUpdateBY = new HashMap<String, AttributeValue>();
		            keyUpdateBY.put("PostId", new AttributeValue().withS(postId));

		            Map<String, AttributeValueUpdate> updateItemsBY = new HashMap<String, AttributeValueUpdate>();

		            if ( StringUtils.equals(entityType, "B") ) {
			            updateItemsBY.put("BloggerName",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withS(entityName))
			           			.withAction(AttributeAction.PUT)
			           		);
		            } else {
			            updateItemsBY.put("PageName",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withS(entityName))
			           			.withAction(AttributeAction.PUT)
			           		);
		            }

		            UpdateItemRequest requestBY = new UpdateItemRequest()
		            	.withTableName("Posts")
		            	.withKey(keyUpdateBY)
		            	.withAttributeUpdates(updateItemsBY);

		            dynamoDBClient.updateItem(requestBY);
		        }

		        lastEvaluatedKeyBBAY = resultBBAY.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyBBAY != null);

            /*************** 	For publishFlag=N	 ********************/
			/*** Update EntityName in Posts table based on PostId's from PostsByEntity table ***/
	    	// Conditions for PostsByEntity table
			HashMap<String, Condition> conditionsBBAN = new HashMap<String, Condition>();
			conditionsBBAN.put("EntityId-PublishFlag",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(entityId+"-N")));

		    Map<String, AttributeValue> lastEvaluatedKeyBBAN = null;
		    do {
		    	// Query PostsByEntity table
		    	QueryRequest queryRequestBBAN = new QueryRequest()
	                .withTableName("PostsByEntity")
	                .withKeyConditions(conditionsBBAN)
	                .withExclusiveStartKey(lastEvaluatedKeyBBAN);
		        QueryResult resultBBAN = dynamoDBClient.query(queryRequestBBAN);

		        // Loop through each post
			    String postId;
		        for (Map<String, AttributeValue> itemBBAN : resultBBAN.getItems()) {

		        	// Get postId for the record
		        	postId = itemBBAN.get("PostId").getS();

		        	// Now, update Posts table
		            HashMap<String, AttributeValue> keyUpdateBN = new HashMap<String, AttributeValue>();
		            keyUpdateBN.put("PostId", new AttributeValue().withS(postId));

		            Map<String, AttributeValueUpdate> updateItemsBN = new HashMap<String, AttributeValueUpdate>();

		            if ( StringUtils.equals(entityType, "B") ) {
			            updateItemsBN.put("BloggerName",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withS(entityName))
			           			.withAction(AttributeAction.PUT)
			           		);
		            } else {
			            updateItemsBN.put("PageName",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withS(entityName))
			           			.withAction(AttributeAction.PUT)
			           		);
		            }

		            UpdateItemRequest requestBN = new UpdateItemRequest()
		            	.withTableName("Posts")
		            	.withKey(keyUpdateBN)
		            	.withAttributeUpdates(updateItemsBN);

		            dynamoDBClient.updateItem(requestBN);
		        }

		        lastEvaluatedKeyBBAN = resultBBAN.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyBBAN != null);

            /*************** 	For publishFlag=D	 ********************/
			/*** Update EntityName in Posts table based on PostId's from PostsByEntity table ***/
	    	// Conditions for PostsByEntity table
			HashMap<String, Condition> conditionsBBAD = new HashMap<String, Condition>();
			conditionsBBAD.put("EntityId-PublishFlag",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(entityId+"-D")));

		    Map<String, AttributeValue> lastEvaluatedKeyBBAD = null;
		    do {
		    	// Query PostsByEntity table
		    	QueryRequest queryRequestBBAD = new QueryRequest()
	                .withTableName("PostsByEntity")
	                .withKeyConditions(conditionsBBAD)
	                .withExclusiveStartKey(lastEvaluatedKeyBBAD);
		        QueryResult resultBBAD = dynamoDBClient.query(queryRequestBBAD);

		        // Loop through each post
			    String postId;
		        for (Map<String, AttributeValue> itemBBAD : resultBBAD.getItems()) {

		        	// Get postId for the record
		        	postId = itemBBAD.get("PostId").getS();

		        	// Now, update Posts table
		            HashMap<String, AttributeValue> keyUpdateBD = new HashMap<String, AttributeValue>();
		            keyUpdateBD.put("PostId", new AttributeValue().withS(postId));

		            Map<String, AttributeValueUpdate> updateItemsBD = new HashMap<String, AttributeValueUpdate>();

		            if ( StringUtils.equals(entityType, "B") ) {
			            updateItemsBD.put("BloggerName",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withS(entityName))
			           			.withAction(AttributeAction.PUT)
			           		);
		            } else {
			            updateItemsBD.put("PageName",
			           		new AttributeValueUpdate()
			           			.withValue(new AttributeValue().withS(entityName))
			           			.withAction(AttributeAction.PUT)
			           		);
		            }

		            UpdateItemRequest requestBD = new UpdateItemRequest()
		            	.withTableName("Posts")
		            	.withKey(keyUpdateBD)
		            	.withAttributeUpdates(updateItemsBD);

		            dynamoDBClient.updateItem(requestBD);
		        }

		        lastEvaluatedKeyBBAD = resultBBAD.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyBBAD != null);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Entity Id: " + entityId + "Entity Name: " + entityName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Queries entity tags.
     * @param entityId entityId
     * @return List of Entity Tags
    */
	public List<Map<String,AttributeValue>> getEntityTags (String entityId) {

		QueryResult result = new QueryResult();
		try {

        	/*** Query entity tags ***/
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(entityId));
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("EntityId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Tags")
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId: " + entityId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries blogger pricing.
     * @param bloggerId bloggerId
     * @return List of Pricing Details
    */
	public List<Map<String,AttributeValue>> getBloggerPricing(String bloggerId) {

		QueryResult result = new QueryResult();
		try {

        	/*** Query blogger pricing ***/
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(bloggerId));
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("BloggerId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Pricing")
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Adds given Hash Tag to the given Entity.
     * @param entityId entityId
     * @param tag hash tag
     **/
	public void addEntityTag(String entityId, String tag) {

    	try {

    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("EntityId", new AttributeValue().withS(entityId));
        	item.put("Tag", new AttributeValue().withS(tag));

        	PutItemRequest request = new PutItemRequest()
	            .withTableName("Tags")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId: " + entityId + " tag: " + tag);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Deletes given Hash Tag from the given Entity.
     * @param entityId entityId
     * @param tag hash tag
     **/
	public void deleteEntityTag(String entityId, String tag) {

    	try {

            /*** Delete from Tags table ***/
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("EntityId", new AttributeValue().withS(entityId));
            key.put("Tag", new AttributeValue().withS(tag));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("Tags")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"entityId: " + entityId + " tag: " + tag);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Updates MarketPricing Tags for the bloggerId.
     * @param bloggerId bloggerId
     **/
	public void updateMarketPricingTags (String bloggerId) {

    	try {

        	/*** Get Concatenated Tags list from Tags table ***/
       		String tags = " ";
        	Map<String, Condition> keyConditionsT = new HashMap<String, Condition>();
        	keyConditionsT.put("EntityId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(bloggerId)));
        	QueryRequest queryRequestT = new QueryRequest()
       			.withTableName("Tags")
       			.withKeyConditions(keyConditionsT);
       		QueryResult queryResultT = dynamoDBClient.query(queryRequestT);
			for (Map<String,AttributeValue> itemT : queryResultT.getItems()) {
				if ( StringUtils.isBlank(tags) ) {
					tags = tags + itemT.get("Tag").getS();
				} else {
					tags = tags +  ", " + itemT.get("Tag").getS();
				}
			}

			/*** Query and Update MarketPricing table ***/
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("BloggerId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(bloggerId)));
        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("MarketPricing")
       			.withKeyConditions(keyConditions);
       		QueryResult queryResult = dynamoDBClient.query(queryRequest);

       		for (Map<String,AttributeValue> item : queryResult.getItems()) {

	            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
	            keyUpdate.put("BloggerId", new AttributeValue().withS(bloggerId));
	            keyUpdate.put("Position", item.get("Position"));
	            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
	            updateItems.put("Tags",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(tags))
	           			.withAction(AttributeAction.PUT)
	           		);
	            UpdateItemRequest request = new UpdateItemRequest()
	            	.withTableName("MarketPricing")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);

			}

			/*** Iterate and remove from MarketPricing memory structure ***/
       		Iterator<MarketPricing> iter = common.marketPricingSet.iterator();
       		Set<MarketPricing> newSet = new HashSet<MarketPricing>();
       		while (iter.hasNext()) {
       			MarketPricing mp = iter.next();
       			if ( StringUtils.equals(mp.getBloggerId(), bloggerId) ) {
       				iter.remove();
	    			mp.setTags(tags);
	    			newSet.add(mp);
       			}
       		}

       		/*** Insert from newSet into MarketPricing memory structure ***/
       		Iterator<MarketPricing> iter1 = newSet.iterator();
       		while (iter1.hasNext()) {
       			MarketPricing mp = iter1.next();
       			common.marketPricingSet.add(mp);
       		}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Adds Pricing Details to Blogger
     * @param bloggerId Blogger Id
     * @param postType Post Type
     * @param deliveryDays Delivery Days
     * @param price Price
     * @return position of the record
     **/
	public long addBloggerPricing(String bloggerId, String postType, long deliveryDays, long price) {

		long newPosition = 1;
    	try {

    		try {
    			HashMap<String, Condition> conditionsP = new HashMap<String, Condition>();
    			conditionsP.put("BloggerId",
    				new Condition()
    					.withComparisonOperator(ComparisonOperator.EQ)
    					.withAttributeValueList(new AttributeValue().withS(bloggerId)));
    			QueryRequest requestP = new QueryRequest()
    				.withTableName("Pricing")
    				.withKeyConditions(conditionsP)
    				.withScanIndexForward(false)
           			.withAttributesToGet("Position");
    			QueryResult resultP = dynamoDBClient.query(requestP);
   				newPosition = Long.parseLong(resultP.getItems().get(0).get("Position").getN()) + 1;
   			} catch (Exception e) {
   				// keep default return value "1"
   			}

    		// Insert into Pricing
    		HashMap<String, AttributeValue> itemP = new HashMap<String, AttributeValue>();
        	itemP.put("BloggerId", new AttributeValue().withS(bloggerId));
        	itemP.put("Position", new AttributeValue().withN(newPosition+""));
        	itemP.put("PostType", new AttributeValue().withS(postType));
        	itemP.put("DeliveryDays", new AttributeValue().withN(deliveryDays+""));
        	itemP.put("Price", new AttributeValue().withN(price+""));

        	PutItemRequest requestP = new PutItemRequest()
	            .withTableName("Pricing")
	   			.withItem(itemP);
        	dynamoDBClient.putItem(requestP);

    		// Insert into MarketPricing (for MarketPlace Search)
    		HashMap<String, AttributeValue> itemMP = new HashMap<String, AttributeValue>();
        	itemMP.put("BloggerId", new AttributeValue().withS(bloggerId));
        	itemMP.put("Position", new AttributeValue().withN(newPosition+""));

        	// Get Name, Country and About from Entities table
			HashMap<String, AttributeValue> keyE = new HashMap<String, AttributeValue>();
			keyE.put("EntityId", new AttributeValue().withS(bloggerId));
			GetItemRequest getItemRequestE = new GetItemRequest()
			    .withTableName("Entities")
			    .withKey(keyE);
			GetItemResult getItemResultE = dynamoDBClient.getItem(getItemRequestE);
        	itemMP.put("Name", getItemResultE.getItem().get("EntityName"));
        	itemMP.put("Country", getItemResultE.getItem().get("Country"));
        	itemMP.put("About", getItemResultE.getItem().get("About"));

        	// Get Concatenated Tags list from Tags table
       		String tags = " ";
        	Map<String, Condition> keyConditionsT = new HashMap<String, Condition>();
        	keyConditionsT.put("EntityId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(bloggerId)));
        	QueryRequest queryRequestT = new QueryRequest()
       			.withTableName("Tags")
       			.withKeyConditions(keyConditionsT);
       		QueryResult queryResultT = dynamoDBClient.query(queryRequestT);
			for (Map<String,AttributeValue> itemT : queryResultT.getItems()) {
				if ( StringUtils.isBlank(tags) ) {
					tags = tags + itemT.get("Tag").getS();
				} else {
					tags = tags +  ", " + itemT.get("Tag").getS();
				}
			}
			if ( StringUtils.isNotBlank(tags) ) {
				itemMP.put("Tags", new AttributeValue().withS(StringUtils.strip(tags," ,")));
			}

        	itemMP.put("PostType", new AttributeValue().withS(postType));
        	itemMP.put("DeliveryDays", new AttributeValue().withN(deliveryDays+""));
        	itemMP.put("Price", new AttributeValue().withN(price+""));

        	PutItemRequest requestMP = new PutItemRequest()
	            .withTableName("MarketPricing")
	   			.withItem(itemMP);
        	dynamoDBClient.putItem(requestMP);

    		// Insert into MarketPricing memory structure (for MarketPlace Search)
			MarketPricing mp = new MarketPricing();
			mp.setBloggerId(bloggerId);
			mp.setPosition(newPosition);
			mp.setName(getItemResultE.getItem().get("EntityName").getS());
			mp.setCountry(getItemResultE.getItem().get("Country").getS());
			try { mp.setAbout(getItemResultE.getItem().get("About").getS()); } catch (Exception e) {}
			mp.setTags(tags);
			mp.setPostType(postType);
			mp.setDeliveryDays(deliveryDays);
			mp.setPrice(price);
			common.marketPricingSet.add(mp);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId + " postType: " + postType);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        newPosition = 0;
	    }

    	return newPosition;

	}

	/**
     * Deletes Pricing Details from Blogger
     * @param bloggerId Blogger Id
     * @param position position
     **/
	public void deleteBloggerPricing(String bloggerId, long position) {

    	try {

            /*** Delete from MarketPricing table ***/
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("BloggerId", new AttributeValue().withS(bloggerId));
            key.put("Position", new AttributeValue().withN(position+""));

        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("Pricing")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

        	DeleteItemRequest deleteItemRequest1 = new DeleteItemRequest()
   	            .withTableName("MarketPricing")
   	   			.withKey(key);
           	dynamoDBClient.deleteItem(deleteItemRequest1);

    		// Delete from MarketPricing memory structure (used for MarketPlace Search)
			MarketPricing mp = new MarketPricing();
			mp.setBloggerId(bloggerId);
			mp.setPosition(position);
			common.marketPricingSet.remove(mp);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId + " position: " + position);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

    /**
     * Inserts into PendingOrders table
     * @param orderHandler Unique Id for Pending Orders
     * @param handlerDate Handler Date
     * @param createdBy User Id of the user who placed the order
     * @param pageId Page Id
     * @param bloggerId Blogger Id
     * @param postType Post Type
     * @param deliveryDays Delivery Days
     * @param price Price
     */
	public void putPendingOrders (String orderHandler, long handlerDate, String createdBy, String pageId,
			String bloggerId, String postType, long deliveryDays, long price) {

		try {

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		    item.put("OrderHandler", new AttributeValue().withS(orderHandler));
		    item.put("HandlerDate", new AttributeValue().withN(handlerDate+""));
		    item.put("CreatedBy", new AttributeValue().withS(createdBy));
		    item.put("PageId", new AttributeValue().withS(pageId));
		    item.put("BloggerId", new AttributeValue().withS(bloggerId));
		    item.put("PostType", new AttributeValue().withS(postType));
		    item.put("DeliveryDays", new AttributeValue().withN(deliveryDays+""));
		    item.put("Price", new AttributeValue().withN(price+""));
		    PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("PendingOrders")
		    	.withItem(item);
			dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderHandler=" + orderHandler + " handlerDate=" + handlerDate + " createdBy=" + createdBy + " pageId=" + pageId
	        	+ " bloggerId=" + bloggerId + " postType=" + postType + " deliveryDays=" + deliveryDays + " price=" + price);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Processes the given Pending Blog Post Order Payment for Success
     * @param orderHandler orderHandler
     */
	public String orderSuccess (String orderHandler, String stripeChargeId) {

		String bloggerId = null;
		try {

			/*** Read from PendingOrders table ***/
			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("OrderHandler", new AttributeValue().withS(orderHandler));
			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("PendingOrders")
			    .withKey(key);

			Map<String, AttributeValue> item = null;
			try {
				item = dynamoDBClient.getItem(getItemRequest).getItem();
	   			// BloggerId is needed by the calling controller method
	   			bloggerId = item.get("BloggerId").getS();
			} catch (Exception e) {
				return "Order doesn't exist.";
			}

			try {
				if ( StringUtils.equals(item.get("Status").getS(), "SUCCESS") ) {
					return "Order has already beed processed.";
				}
			} catch (Exception e) {
				// ignore if there is no Status column
			}

			// insert into Orders table
			createOrder (
   				orderHandler,
   				item.get("PageId").getS(),
   				item.get("BloggerId").getS(),
   				item.get("CreatedBy").getS(),
   				item.get("PostType").getS(),
   				Long.parseLong(item.get("DeliveryDays").getN()),
   				Long.parseLong(item.get("Price").getN()),
   				stripeChargeId
   			);

			/*** Update PendingOrders table ***/
            HashMap<String, AttributeValue> keyUpdatePP = new HashMap<String, AttributeValue>();
            keyUpdatePP.put("OrderHandler", new AttributeValue().withS(orderHandler));
            Map<String, AttributeValueUpdate> updateItemsPP = new HashMap<String, AttributeValueUpdate>();
            updateItemsPP.put("Status",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS("SUCCESS"))
           			.withAction(AttributeAction.PUT)
           		);
            UpdateItemRequest requestPP = new UpdateItemRequest()
            	.withTableName("PendingOrders")
            	.withKey(keyUpdatePP)
            	.withAttributeUpdates(updateItemsPP);
            dynamoDBClient.updateItem(requestPP);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderHandler=" + orderHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

		return "SUCCESS:"+bloggerId;
	}

    /**
     * Processes the given Pending Blog Post Order Payment for Error
     * @param orderHandler orderHandler
     */
	public void orderError (String orderHandler) {

		try {

			/*** Update PendingOrders table ***/
            HashMap<String, AttributeValue> keyUpdateTA = new HashMap<String, AttributeValue>();
            keyUpdateTA.put("OrderHandler", new AttributeValue().withS(orderHandler));

            Map<String, AttributeValueUpdate> updateItemsTA = new HashMap<String, AttributeValueUpdate>();
            updateItemsTA.put("Status",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withS("ERROR"))
           			.withAction(AttributeAction.PUT)
           		);

            UpdateItemRequest requestTA = new UpdateItemRequest()
            	.withTableName("PendingOrders")
            	.withKey(keyUpdateTA)
            	.withAttributeUpdates(updateItemsTA);
            dynamoDBClient.updateItem(requestTA);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderHandler=" + orderHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Creates an Order with Blogger Pricing
     * @param orderId Order Id, if available
     * @param pageId Page Id
     * @param bloggerId Blogger Id
     * @param buyerId Buyer Id
     * @param postType Post Type
     * @param deliveryDays Delivery Days
     * @param price Price
     * @return orderId
     **/
	public String createOrder (String orderId, String pageId, String bloggerId, String buyerId, String postType, long deliveryDays, long price, String stripeChargeId) {

    	try {

    		// generate order id if blank
    		if ( StringUtils.isBlank(orderId) ) {
    			orderId = UUID.randomUUID().toString();
    		}

    		// get page name and blogger name
    		String pageName = getEntityName(pageId);
    		String bloggerName = getEntityName(bloggerId);

    		// insert into Orders table
    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("OrderId", new AttributeValue().withS(orderId));
        	item.put("OrderDate", new AttributeValue().withN(System.currentTimeMillis()+""));
        	item.put("PageId", new AttributeValue().withS(pageId));
        	item.put("PageName", new AttributeValue().withS(pageName));
        	item.put("BloggerId", new AttributeValue().withS(bloggerId));
        	item.put("BloggerName", new AttributeValue().withS(bloggerName));
        	item.put("BuyerId", new AttributeValue().withS(buyerId));
        	item.put("BuyerName", new AttributeValue().withS(getEntityName(buyerId)));
        	item.put("PostType", new AttributeValue().withS(postType));
        	item.put("DeliveryDays", new AttributeValue().withN(deliveryDays+""));
        	item.put("Price", new AttributeValue().withN(price+""));
        	if ( !StringUtils.isBlank(stripeChargeId) ) {
        		item.put("StripeChargeId", new AttributeValue().withS(stripeChargeId));
        	}
        	PutItemRequest request = new PutItemRequest()
	            .withTableName("Orders")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

        	// insert into page bloggers and blogger pages
        	putPageBloggers(pageId, pageName, bloggerId, bloggerName, "N");

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId: " + pageId + " bloggerId: " + bloggerId + " postType: " + postType);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        orderId = "NULL";
	    }
    	return orderId;

	}

	/**
     * Queries pending order details from PendingOrders table.
     * @param orderHandler orderHandler
     * @return PendingOrder object with pending order details
     */
	public PendingOrder getPendingOrder (String orderHandler) {
		PendingOrder po = null; // default
		try {
        	po = dynamoDBMapper.load(PendingOrder.class, orderHandler);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderHandler=" + orderHandler);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return po;
    }

	/**
     * Queries order details from Orders table.
     * @param orderId orderId
     * @return Order object with order details
     */
	public Order getOrder (String orderId) {
		Order order = null; // default
		try {
        	order = dynamoDBMapper.load(Order.class, orderId);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderId=" + orderId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return order;
    }

	/**
     * Queries orders placed by a buyer.
     * @param buyerId UserId of the Buyer
     * @return List of Orders Placed
    */
	public List<Map<String,AttributeValue>> getOrdersPlaced (String buyerId) {

		QueryResult result = new QueryResult();
		try {

        	/*** Query orders ***/
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("BuyerId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(buyerId)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Orders")
       			.withIndexName("BuyerIdIdx")
       			.withScanIndexForward(false)
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"buyerId: " + buyerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries orders received by a blogger.
     * @param bloggerId UserId of the Blogger
     * @return List of Orders Received
    */
	public List<Map<String,AttributeValue>> getOrdersReceived (String bloggerId) {

		QueryResult result = new QueryResult();
		try {

        	/*** Query orders ***/
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("BloggerId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(bloggerId)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Orders")
       			.withIndexName("BloggerIdIdx")
       			.withScanIndexForward(false)
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries order progress.
     * @param orderId orderId
     * @return List of Order Progress details
    */
	public List<Map<String,AttributeValue>> getOrderProgress (String orderId) {

		QueryResult result = new QueryResult();
		try {

        	/*** Query order progress ***/
        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("OrderId", new Condition()
           		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(orderId)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("OrderProgress")
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderId: " + orderId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Creates a comment on the Order
     * @param orderId Order Id
     * @param commentBy Id of the commenter
     * @param commentText Comment Text
     **/
	public void orderPostComment (String orderId, String commentBy, String commentText) {

    	try {

    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("OrderId", new AttributeValue().withS(orderId));
        	item.put("StepDate", new AttributeValue().withN(System.currentTimeMillis()+""));
        	item.put("StepType", new AttributeValue().withS("COMMENT"));
        	item.put("CommentBy", new AttributeValue().withS(commentBy));
        	item.put("CommentText", new AttributeValue().withS(commentText));

        	PutItemRequest request = new PutItemRequest()
	            .withTableName("OrderProgress")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderId: " + orderId + " commentText: " + commentText);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Request buyer for a review
     * @param orderId Order Id
     * @param bloggerId Blogger Id
     **/
	public void orderReview (String orderId, String bloggerId) {

    	try {

    		long publishDate = System.currentTimeMillis();

    		// Order Progress
    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("OrderId", new AttributeValue().withS(orderId));
        	item.put("StepDate", new AttributeValue().withN(publishDate+""));
        	item.put("StepType", new AttributeValue().withS("REVIEW"));
        	item.put("BloggerId", new AttributeValue().withS(bloggerId));
        	PutItemRequest request = new PutItemRequest()
	            .withTableName("OrderProgress")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

        	// Orders
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("OrderId", new AttributeValue().withS(orderId));
            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("PublishDate",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(publishDate+""))
           			.withAction(AttributeAction.PUT)
           		);
            UpdateItemRequest requestUpdate = new UpdateItemRequest()
            	.withTableName("Orders")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(requestUpdate);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderId: " + orderId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Close the order
     * @param orderId Order Id
     * @param buyerId Buyer Id
     **/
	public void orderClose (String orderId, String buyerId) {

    	try {

    		long closeDate = System.currentTimeMillis();

    		// Order Progress
    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("OrderId", new AttributeValue().withS(orderId));
        	item.put("StepDate", new AttributeValue().withN(closeDate+""));
        	item.put("StepType", new AttributeValue().withS("CLOSE"));
        	item.put("BuyerId", new AttributeValue().withS(buyerId));
        	PutItemRequest request = new PutItemRequest()
	            .withTableName("OrderProgress")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

        	// Orders
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("OrderId", new AttributeValue().withS(orderId));
            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("CloseDate",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(closeDate+""))
           			.withAction(AttributeAction.PUT)
           		);
            UpdateItemRequest requestUpdate = new UpdateItemRequest()
            	.withTableName("Orders")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(requestUpdate);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderId: " + orderId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**
     * Cancel the order
     * @param orderId Order Id
     * @param buyerId Buyer Id
     **/
	public void orderCancel (String orderId, String buyerId) {

    	try {

    		long cancelDate = System.currentTimeMillis();

    		// Order Progress
    		HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("OrderId", new AttributeValue().withS(orderId));
        	item.put("StepDate", new AttributeValue().withN(cancelDate+""));
        	item.put("StepType", new AttributeValue().withS("CANCEL"));
        	item.put("BuyerId", new AttributeValue().withS(buyerId));
        	PutItemRequest request = new PutItemRequest()
	            .withTableName("OrderProgress")
	   			.withItem(item);
        	dynamoDBClient.putItem(request);

        	// Orders
            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            keyUpdate.put("OrderId", new AttributeValue().withS(orderId));
            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            updateItems.put("CancelDate",
           		new AttributeValueUpdate()
           			.withValue(new AttributeValue().withN(cancelDate+""))
           			.withAction(AttributeAction.PUT)
           		);
            UpdateItemRequest requestUpdate = new UpdateItemRequest()
            	.withTableName("Orders")
            	.withKey(keyUpdate)
            	.withAttributeUpdates(updateItems);
            dynamoDBClient.updateItem(requestUpdate);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"orderId: " + orderId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**************************************************************************
     **************  TOP ZONES, TOP POSTS & TOP BLOGGERS  *********************
     **************************************************************************/

	/**
     * Populates TopCharts table with TOP posts, TOP zones, TOP bloggers, and TOP pages
     * based on data from PostActivity table.
     **/
	public String generateTopCharts (long runPeriod) {

		String stepName = null;
		String retMessage = "FAILURE"; //default
   		long postCount = 0;
   		long zoneCount = 0;
   		long bloggerCount = 0;
   		long pageCount = 0;
    	try {

    		/*** Define throughput needed for this job ***/
    		long neededReadCapacityTopCharts = 3L;
    		long neededWriteCapacityTopCharts = 24L;
    		long neededReadCapacityPostActivity = 3L;
    		long neededWriteCapacityPostActivity = 12L;

    		/*** Query current throughput of TopCharts table ***/
    		DescribeTableRequest request = new DescribeTableRequest()
    			.withTableName("TopCharts");
    		TableDescription tableDescription = dynamoDBClient
    			.describeTable(request).getTable();
    		long currentReadCapacityTopCharts = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
    		long currentWriteCapacityTopCharts = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

          	/*** Increase throughput of TopCharts table ***/
    		if ( currentReadCapacityTopCharts < neededReadCapacityTopCharts ||
    			 currentWriteCapacityTopCharts < neededWriteCapacityTopCharts
    			) {
	    		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
	    			.withReadCapacityUnits(neededReadCapacityTopCharts)
	    		    .withWriteCapacityUnits(neededWriteCapacityTopCharts);
	    		UpdateTableRequest updateTableRequest = new UpdateTableRequest()
	    			.withTableName("TopCharts")
	    			.withProvisionedThroughput(provisionedThroughput);
	    		dynamoDBClient.updateTable(updateTableRequest);
	    		// wait for the capacity increase
	    		waitForTargetCapacity("TopCharts", neededReadCapacityTopCharts, neededWriteCapacityTopCharts);
    		}

    		// Define maps to hold data for zones, bloggers and pages
			HashMap<String, Long> topZonesMap = new HashMap<String, Long>();
			HashMap<String, String> zoneNameMap = new HashMap<String, String>();
			HashMap<String, Long> topBloggersMap = new HashMap<String, Long>();
			HashMap<String, Long> topPagesMap = new HashMap<String, Long>();

    		// Number of periods to read HI data
    		int topChartsProcessPeriodCount = Integer.parseInt(config.getProperty("topChartsProcessPeriodCount"));

    		/*** Generate TopCharts for Posts ***/
    		long postHIPeriod = runPeriod;
    		while ( postHIPeriod > runPeriod-topChartsProcessPeriodCount ) {

    			postHIPeriod--;
				long hiPrevious = 0;

    			/*** Query PostActivity table ***/
       		    stepName = "Query PostActivity";
               	Condition hashKeyConditionBH = new Condition()
              		.withComparisonOperator(ComparisonOperator.EQ.toString())
               		.withAttributeValueList(new AttributeValue().withN( postHIPeriod+"" ));

               	Map<String, Condition> keyConditionsBH = new HashMap<String, Condition>();
               	keyConditionsBH.put("Period", hashKeyConditionBH);

               	Map<String, AttributeValue> lastEvaluatedKeyBH = null;
               	do
               	{

	               	QueryRequest requestBH = new QueryRequest()
	           			.withTableName("PostActivity")
	           			.withKeyConditions(keyConditionsBH)
	           			.withExclusiveStartKey(lastEvaluatedKeyBH);

	               	System.out.println(stepName + " ; LastEvaluatedKey=" + lastEvaluatedKeyBH);

	               	QueryResult resultBH = dynamoDBClient.query(requestBH);

	               	for (Map<String, AttributeValue> itemBH : resultBH.getItems()) {

	    		    	String postId = itemBH.get("PostId").getS();
	    		       	long hi = Long.parseLong(itemBH.get("HI").getN());

		    			/*** Insert/ Update TopCharts table for PO-period ***/
		                HashMap<String, AttributeValue> keyUpdateBT = new HashMap<String, AttributeValue>();
		                keyUpdateBT.put("Name", new AttributeValue().withS( "PO-"+runPeriod ));
		                keyUpdateBT.put("Id", new AttributeValue().withS(postId));

		                Map<String, AttributeValueUpdate> updateItemsBT = new HashMap<String, AttributeValueUpdate>();
		                updateItemsBT.put("HI",
		               		new AttributeValueUpdate()
		               			.withValue(new AttributeValue().withN(hi+""))
		               			.withAction(AttributeAction.ADD)
		               		);

		                UpdateItemRequest requestBT = new UpdateItemRequest()
		                	.withTableName("TopCharts")
		                	.withKey(keyUpdateBT)
		                	.withAttributeUpdates(updateItemsBT);
		                dynamoDBClient.updateItem(requestBT);

		    			/*** Insert/ Update Map for ZoneId ***/
						try {
							hiPrevious = topZonesMap.get(itemBH.get("ZoneId").getS()).longValue();
						} catch (Exception e) {
							hiPrevious = 0;
						}
		                topZonesMap.put(itemBH.get("ZoneId").getS(), hi+hiPrevious);
		                zoneNameMap.put(itemBH.get("ZoneId").getS(), itemBH.get("ZoneName").getS());

		    			/*** Insert/ Update Map for BloggerId ***/
						try {
							hiPrevious = topBloggersMap.get(itemBH.get("BloggerId").getS()).longValue();
						} catch (Exception e) {
							hiPrevious = 0;
						}
		                topBloggersMap.put(itemBH.get("BloggerId").getS(), hi+hiPrevious);

		    			/*** Insert/ Update Map for PageId ***/
		                try {
			                if ( StringUtils.isNotBlank(itemBH.get("PageId").getS()) ) {
								try {
									hiPrevious = topPagesMap.get(itemBH.get("PageId").getS()).longValue();
								} catch (Exception e) {
									hiPrevious = 0;
								}
				                topPagesMap.put(itemBH.get("PageId").getS(), hi+hiPrevious);
			                }
		                } catch (Exception e) {}

		                // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
		                postCount++;
		   				if ( postCount % 8 == 0 ) {
		   					Thread.sleep( 2 * 1000 );
		   				}

	               	}

	               	lastEvaluatedKeyBH = resultBH.getLastEvaluatedKey();
               	} while (lastEvaluatedKeyBH != null);

    		}

	        logger.log(Level.INFO,"Generate TopCharts for Posts completed.");

    		// Insert Top Zones
       		for ( Map.Entry<String, Long> entry : topZonesMap.entrySet() ) {

    			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("Name", new AttributeValue().withS("Z-"+runPeriod));
				item.put("Id", new AttributeValue().withS(entry.getKey()));
				item.put("ZoneName", new AttributeValue().withS(zoneNameMap.get(entry.getKey())));
				item.put("HI", new AttributeValue().withN(entry.getValue()+""));
				PutItemRequest putItemRequest = new PutItemRequest()
					.withTableName("TopCharts")
					.withItem(item);
				dynamoDBClient.putItem(putItemRequest);

                // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
				zoneCount++;
   				if ( zoneCount % 8 == 0 ) {
   					Thread.sleep( 2 * 1000 );
   				}

    		}
			// Insert Sticky Zones into the TopCharts table
    		String[] stickyZonesAttr = getAttribute("A", "Sticky Zones").split(";");
    		for ( int i=0; i<stickyZonesAttr.length; i++ ) {
    			String[] stickyZonesValueSplit = stickyZonesAttr[i].split("=");

           		HashMap<String, AttributeValue> keyUpdateAT = new HashMap<String, AttributeValue>();
           		keyUpdateAT.put("Name", new AttributeValue().withS( "Z-"+runPeriod ));
           		keyUpdateAT.put("Id", new AttributeValue().withS(stickyZonesValueSplit[0]));

           		Map<String, AttributeValueUpdate> updateItemsAT = new HashMap<String, AttributeValueUpdate>();
           		updateItemsAT.put("HI",
	            	new AttributeValueUpdate()
	            		.withValue(new AttributeValue().withN(Integer.parseInt(stickyZonesValueSplit[1])+""))
	            		.withAction(AttributeAction.ADD)
	            );
           		updateItemsAT.put("ZoneName",
   	            	new AttributeValueUpdate()
   	            		.withValue(new AttributeValue().withS( getZoneName(stickyZonesValueSplit[0]) ))
   	            		.withAction(AttributeAction.PUT)
   	            );

	            UpdateItemRequest requestAT = new UpdateItemRequest()
	              	.withTableName("TopCharts")
	              	.withKey(keyUpdateAT)
	              	.withAttributeUpdates(updateItemsAT);
	            dynamoDBClient.updateItem(requestAT);

	            // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
				zoneCount++;
   				if ( zoneCount % 8 == 0 ) {
   					Thread.sleep( 2 * 1000 );
   				}

    		}

	        logger.log(Level.INFO,"Generate TopCharts for Zones completed.");

    		// Insert Top Bloggers
       		for ( Map.Entry<String, Long> entry : topBloggersMap.entrySet() ) {

    			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("Name", new AttributeValue().withS("B-"+runPeriod));
				item.put("Id", new AttributeValue().withS(entry.getKey()));
				item.put("HI", new AttributeValue().withN(entry.getValue()+""));
				PutItemRequest putItemRequest = new PutItemRequest()
					.withTableName("TopCharts")
					.withItem(item);
				dynamoDBClient.putItem(putItemRequest);

                // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
				bloggerCount++;
   				if ( bloggerCount % 8 == 0 ) {
   					Thread.sleep( 2 * 1000 );
   				}

    		}
			// Insert Sticky Bloggers into the TopCharts table
    		String[] stickyBloggersAttr = getAttribute("A", "Sticky Bloggers").split(";");
    		for ( int i=0; i<stickyBloggersAttr.length; i++ ) {
    			String[] stickyBloggersValueSplit = stickyBloggersAttr[i].split("=");

           		HashMap<String, AttributeValue> keyUpdateAT = new HashMap<String, AttributeValue>();
           		keyUpdateAT.put("Name", new AttributeValue().withS( "B-"+runPeriod ));
           		keyUpdateAT.put("Id", new AttributeValue().withS(stickyBloggersValueSplit[0]));

           		Map<String, AttributeValueUpdate> updateItemsAT = new HashMap<String, AttributeValueUpdate>();
           		updateItemsAT.put("HI",
	            	new AttributeValueUpdate()
	            		.withValue(new AttributeValue().withN(Integer.parseInt(stickyBloggersValueSplit[1])+""))
	            		.withAction(AttributeAction.ADD)
	            );

	            UpdateItemRequest requestAT = new UpdateItemRequest()
	              	.withTableName("TopCharts")
	              	.withKey(keyUpdateAT)
	              	.withAttributeUpdates(updateItemsAT);
	            dynamoDBClient.updateItem(requestAT);

	            // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
				bloggerCount++;
   				if ( bloggerCount % 8 == 0 ) {
   					Thread.sleep( 2 * 1000 );
   				}

    		}

	        logger.log(Level.INFO,"Generate TopCharts for Bloggers completed.");

    		// Insert Top Pages
       		for ( Map.Entry<String, Long> entry : topPagesMap.entrySet() ) {

    			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("Name", new AttributeValue().withS("PA-"+runPeriod));
				item.put("Id", new AttributeValue().withS(entry.getKey()));
				item.put("HI", new AttributeValue().withN(entry.getValue()+""));
				PutItemRequest putItemRequest = new PutItemRequest()
					.withTableName("TopCharts")
					.withItem(item);
				dynamoDBClient.putItem(putItemRequest);

                // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
				pageCount++;
   				if ( pageCount % 8 == 0 ) {
   					Thread.sleep( 2 * 1000 );
   				}

    		}
			// Insert Sticky Pages into the TopCharts table
    		String[] stickyPagesAttr = getAttribute("A", "Sticky Pages").split(";");
    		for ( int i=0; i<stickyPagesAttr.length; i++ ) {
    			String[] stickyPagesValueSplit = stickyPagesAttr[i].split("=");

           		HashMap<String, AttributeValue> keyUpdateAT = new HashMap<String, AttributeValue>();
           		keyUpdateAT.put("Name", new AttributeValue().withS( "PA-"+runPeriod ));
           		keyUpdateAT.put("Id", new AttributeValue().withS(stickyPagesValueSplit[0]));

           		Map<String, AttributeValueUpdate> updateItemsAT = new HashMap<String, AttributeValueUpdate>();
           		updateItemsAT.put("HI",
	            	new AttributeValueUpdate()
	            		.withValue(new AttributeValue().withN(Integer.parseInt(stickyPagesValueSplit[1])+""))
	            		.withAction(AttributeAction.ADD)
	            );

	            UpdateItemRequest requestAT = new UpdateItemRequest()
	              	.withTableName("TopCharts")
	              	.withKey(keyUpdateAT)
	              	.withAttributeUpdates(updateItemsAT);
	            dynamoDBClient.updateItem(requestAT);

	            // sleep for a moment to conserve DynamoDB write throughput on TopCharts table
				pageCount++;
   				if ( pageCount % 8 == 0 ) {
   					Thread.sleep( 2 * 1000 );
   				}

    		}

	        logger.log(Level.INFO,"Generate TopCharts for Pages completed.");

           	/*** Purge TopCharts data beyond the retention period ***/
       		purgeTopCharts( runPeriod-Long.parseLong(config.getProperty("topChartsRetentionPeriodCount")) );
       		purgeTopCharts( runPeriod-Long.parseLong(config.getProperty("topChartsRetentionPeriodCount"))-1 );
       		purgeTopCharts( runPeriod-Long.parseLong(config.getProperty("topChartsRetentionPeriodCount"))-2 );
       		purgeTopCharts( runPeriod-Long.parseLong(config.getProperty("topChartsRetentionPeriodCount"))-3 );
       		purgeTopCharts( runPeriod-Long.parseLong(config.getProperty("topChartsRetentionPeriodCount"))-4 );

          	/*** Reset throughput of TopCharts table to the original values ***/
        	// since we consumed more-than-normal throughput on the table, give it a minute to cool down
        	Thread.sleep( 60 * 1000 );
        	ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
    			.withReadCapacityUnits(common.resetReadCapacityTopCharts)
    		    .withWriteCapacityUnits(common.resetWriteCapacityTopCharts);
    		UpdateTableRequest updateTableRequest = new UpdateTableRequest()
    			.withTableName("TopCharts")
    			.withProvisionedThroughput(provisionedThroughput);
    		dynamoDBClient.updateTable(updateTableRequest);

    		/*** Query current throughput of PostActivity table ***/
    		request = new DescribeTableRequest()
    			.withTableName("PostActivity");
    		tableDescription = dynamoDBClient
    			.describeTable(request).getTable();
    		long currentReadCapacityPostActivity = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
    		long currentWriteCapacityPostActivity = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

          	/*** Increase throughput of PostActivity table ***/
    		if ( currentReadCapacityPostActivity < neededReadCapacityPostActivity ||
    			 currentWriteCapacityPostActivity < neededWriteCapacityPostActivity
    			) {
	    		provisionedThroughput = new ProvisionedThroughput()
	    			.withReadCapacityUnits(neededReadCapacityPostActivity)
	    		    .withWriteCapacityUnits(neededWriteCapacityPostActivity);
	    		updateTableRequest = new UpdateTableRequest()
	    			.withTableName("PostActivity")
	    			.withProvisionedThroughput(provisionedThroughput);
	    		dynamoDBClient.updateTable(updateTableRequest);
	    		// wait for the capacity increase
	    		waitForTargetCapacity("PostActivity", neededReadCapacityPostActivity, neededWriteCapacityPostActivity);
    		}

           	/*** Purge PostActivity data beyond the retention period ***/
       		purgePostActivity( runPeriod-Long.parseLong(config.getProperty("topChartsActivityRetentionPeriodCount")) );
       		purgePostActivity( runPeriod-Long.parseLong(config.getProperty("topChartsActivityRetentionPeriodCount"))-1 );
       		purgePostActivity( runPeriod-Long.parseLong(config.getProperty("topChartsActivityRetentionPeriodCount"))-2 );
       		purgePostActivity( runPeriod-Long.parseLong(config.getProperty("topChartsActivityRetentionPeriodCount"))-3 );
       		purgePostActivity( runPeriod-Long.parseLong(config.getProperty("topChartsActivityRetentionPeriodCount"))-4 );

          	/*** Reset throughput of PostActivity table to the original values ***/
        	// since we consumed more-than-normal throughput on the table, give it a minute to cool down
        	Thread.sleep( 60 * 1000 );
        	provisionedThroughput = new ProvisionedThroughput()
    			.withReadCapacityUnits(common.resetReadCapacityPostActivity)
    		    .withWriteCapacityUnits(common.resetWriteCapacityPostActivity);
    		updateTableRequest = new UpdateTableRequest()
    			.withTableName("PostActivity")
    			.withProvisionedThroughput(provisionedThroughput);
    		dynamoDBClient.updateTable(updateTableRequest);

			logger.log(Level.INFO,"HeatbudDynamoDBUtil.java - generateTopCharts: Processed " +
				postCount + " Post records and " +
				zoneCount + " Zone records and " +
				bloggerCount + " Blogger records and " +
				pageCount + " Page records."
			);

			// Consider success if we processed at least one record for each type
    		if ( postCount > 0 && zoneCount > 0 && bloggerCount > 0 && pageCount > 0 ) {
    			retMessage = "SUCCESS";
    		}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Run Period=" + runPeriod + " Step Name=" + stepName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

    	return retMessage;
	}

	/**
     * Populates RList table with TOP posts for guest user id.
     **/
	public String generateGuestRL () {

		String stepName = null;
		String retMessage = "FAILURE"; //default
		int insertCount = 0;
    	try {

    		/*** Define throughput needed for this job ***/
    		long neededReadCapacityRList = 3L;
    		long neededWriteCapacityRList = 10L;

    		/*** Query current throughput of RList table ***/
    		DescribeTableRequest request = new DescribeTableRequest().withTableName("RList");
    		TableDescription tableDescription = dynamoDBClient.describeTable(request).getTable();
    		long currentReadCapacityRL = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
    		long currentWriteCapacityRL = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

          	/*** Increase throughput of RList table ***/
    		if ( currentReadCapacityRL < neededReadCapacityRList ||
    			 currentWriteCapacityRL < neededWriteCapacityRList
    			) {
	    		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
	    			.withReadCapacityUnits(neededReadCapacityRList)
	    		    .withWriteCapacityUnits(neededWriteCapacityRList);
	    		UpdateTableRequest updateTableRequest = new UpdateTableRequest()
	    			.withTableName("RList")
	    			.withProvisionedThroughput(provisionedThroughput);
	    		dynamoDBClient.updateTable(updateTableRequest);
	    		// wait for the capacity increase
	    		waitForTargetCapacity("RList", neededReadCapacityRList, neededWriteCapacityRList);
    		}

    		// Wait for 3 minutes after increasing the capacity
        	Thread.sleep( 180 * 1000 );

			/*** Query Zones table ***/
   		    stepName = "Query Zones";
           	Condition hashKeyConditionZones = new Condition()
          		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS("M"));

           	Map<String, Condition> keyConditionsZones = new HashMap<String, Condition>();
           	keyConditionsZones.put("UserId", hashKeyConditionZones);

           	Map<String, AttributeValue> lastEvaluatedKeyZones = null;
           	do
           	{
	           	QueryRequest requestZones = new QueryRequest()
	       			.withTableName("Zones")
	       			.withKeyConditions(keyConditionsZones)
	       			.withExclusiveStartKey(lastEvaluatedKeyZones)
	       			.withAttributesToGet("ZoneId");

	           	QueryResult resultZones = dynamoDBClient.query(requestZones);

	           	for (Map<String, AttributeValue> itemZones : resultZones.getItems()) {

	           		String zoneId = itemZones.get("ZoneId").getS();

	           		/*** Query Ranking table for the input zone in the reverse order of PostHI ***/
	       		    stepName = "Query Ranking";
	           		Map<String, Condition> conditionsRanking = new HashMap<String, Condition>();
	           		conditionsRanking.put("ZoneId",
	           			new Condition()
							.withComparisonOperator(ComparisonOperator.EQ)
							.withAttributeValueList(new AttributeValue().withS(zoneId)));

	           		QueryRequest requestRanking = new QueryRequest()
	       				.withTableName("Ranking")
	       				.withIndexName("PostHIIdx")
	       				.withKeyConditions(conditionsRanking)
	       				.withScanIndexForward(false)
	       				.withAttributesToGet("PostId");

	           		List<Map<String,AttributeValue>> itemsRanking = dynamoDBClient.query(requestRanking).getItems();

	           		/*** Loop through PostIds and insert them into RL ***/
	           		for (int currentRO = 1; currentRO <= itemsRanking.size(); currentRO++) {

						/*** Read PostId from Ranking ***/
						String postId = itemsRanking.get(currentRO-1).get("PostId").getS();

						/*** Insert postId into RL ***/
		       		    stepName = "Insert RList";
			    		Map<String, AttributeValue> itemRL = new HashMap<String, AttributeValue>();
			            itemRL.put("UserId-ZoneId", new AttributeValue().withS("guest-"+zoneId));
			            itemRL.put("RO", new AttributeValue().withN( currentRO+"" ));
			            itemRL.put("PostId", new AttributeValue().withS(postId));

			            PutItemRequest putItemRequest = new PutItemRequest()
			    	    	.withTableName("RList")
			    	    	.withItem(itemRL);

			            dynamoDBClient.putItem(putItemRequest);
			            insertCount++;

		                // sleep for a second to conserve DynamoDB write throughput on RList table
		   				if ( insertCount % 8 == 0 ) {
		   					Thread.sleep( 1000 );
		   				}

	           		}

	           		/*** Delete extra posts from the RL ***/
	       		    stepName = "Delete RList";
                	// Prepare key for delete
                	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
                	key.put("UserId-ZoneId", new AttributeValue().withS("guest-"+zoneId));
    	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
    		            .withTableName("RList")
    		   			.withKey(key)
    		   			.withReturnValues("ALL_OLD");
    	        	// first RO to be deleted = (number of items in the ranking table) + 1
           			int deleteRO = itemsRanking.size()+1;
	           		boolean exitLoop = false;
	           		do {
    	            	key.put("RO", new AttributeValue().withN(deleteRO+""));
    		        	DeleteItemResult deleteItemResult = dynamoDBClient.deleteItem(deleteItemRequest);
    		        	try {
	    		        	Map<String,AttributeValue> deletedItem = deleteItemResult.getAttributes();
	    		        	if ( deletedItem.isEmpty() )
	    		        		exitLoop = true;
	    		        	else
	    		        		deleteRO++;
    		        	} catch (Exception e) {
    		        		exitLoop = true;
    		        	}
	           		} while (!exitLoop);

	           	}
           		lastEvaluatedKeyZones = resultZones.getLastEvaluatedKey();
           	} while (lastEvaluatedKeyZones != null);

          	/*** Reset throughput of RList table to the original values ***/
        	// since we consumed more-than-normal throughput on the table, give it a minute to cool down
        	Thread.sleep( 60 * 1000 );
        	ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
    			.withReadCapacityUnits(common.resetReadCapacityRList)
    		    .withWriteCapacityUnits(common.resetWriteCapacityRList);
    		UpdateTableRequest updateTableRequest = new UpdateTableRequest()
    			.withTableName("RList")
    			.withProvisionedThroughput(provisionedThroughput);
    		dynamoDBClient.updateTable(updateTableRequest);

	        // Consider success if we processed at least ten posts
    		if ( insertCount > 10 ) {
    			System.out.println("HeatbudDynamoDBUtil.java - generateGuestRL: Processed " + insertCount + " posts.");
    			retMessage = "SUCCESS";
    		}

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Step Name=" + stepName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

    	return retMessage;
	}

    /**
     * Purges old Top Charts data from TopCharts table
     * @param purgePeriod Period for which to delete records
     */
    public void purgeTopCharts (long purgePeriod) {

    	int i = 0;
		try {
			/*** DELETE TOP POSTS FOR THE PURGE PERIOD ***/
        	// Prepare key for delete
        	HashMap<String, AttributeValue> keyB = new HashMap<String, AttributeValue>();
        	keyB.put("Name", new AttributeValue().withS("PO-"+purgePeriod));

        	// Conditions for TopCharts table
			HashMap<String, Condition> conditionsB = new HashMap<String, Condition>();
			conditionsB.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("PO-"+purgePeriod)));

		    Map<String, AttributeValue> lastEvaluatedKeyB = null;
		    do {
			    // Query TopCharts table
			    QueryRequest requestB = new QueryRequest()
	                .withTableName("TopCharts")
	                .withKeyConditions(conditionsB)
	                .withExclusiveStartKey(lastEvaluatedKeyB);
		        QueryResult resultB = dynamoDBClient.query(requestB);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemB : resultB.getItems()) {

	            	// Add Id to the Key and Delete
	            	keyB.put("Id", itemB.get("Id"));
		        	DeleteItemRequest deleteItemRequestB = new DeleteItemRequest()
			            .withTableName("TopCharts")
			   			.withKey(keyB);
		        	dynamoDBClient.deleteItem(deleteItemRequestB);

		        	// sleep for a second to conserve DynamoDB write throughput on TopCharts table
		        	i++;
	   				if ( i % 10 == 0 ) {
	   					Thread.sleep( 1000 );
	   				}

	            }

	        	lastEvaluatedKeyB = resultB.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyB != null);

			/*** DELETE TOP BLOGGERS FOR THE PURGE PERIOD ***/
        	// Prepare key for delete
        	HashMap<String, AttributeValue> keyA = new HashMap<String, AttributeValue>();
        	keyA.put("Name", new AttributeValue().withS("B-"+purgePeriod));

        	// Conditions for TopCharts table
			HashMap<String, Condition> conditionsA = new HashMap<String, Condition>();
			conditionsA.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("B-"+purgePeriod)));

		    Map<String, AttributeValue> lastEvaluatedKeyA = null;
		    do {
			    // Query TopCharts table
			    QueryRequest requestA = new QueryRequest()
	                .withTableName("TopCharts")
	                .withKeyConditions(conditionsA)
	                .withExclusiveStartKey(lastEvaluatedKeyA);
		        QueryResult resultA = dynamoDBClient.query(requestA);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemA : resultA.getItems()) {

	            	// Add Id to the Key and Delete
	            	keyA.put("Id", itemA.get("Id"));
		        	DeleteItemRequest deleteItemRequestA = new DeleteItemRequest()
			            .withTableName("TopCharts")
			   			.withKey(keyA);
		        	dynamoDBClient.deleteItem(deleteItemRequestA);

		        	// sleep for a second to conserve DynamoDB write throughput on TopCharts table
		        	i++;
	   				if ( i % 10 == 0 ) {
	   					Thread.sleep( 1000 );
	   				}

	            }

	        	lastEvaluatedKeyA = resultA.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyA != null);

			/*** DELETE TOP PAGES FOR THE PURGE PERIOD ***/
        	// Prepare key for delete
        	HashMap<String, AttributeValue> keyP = new HashMap<String, AttributeValue>();
        	keyP.put("Name", new AttributeValue().withS("PA-"+purgePeriod));

        	// Conditions for TopCharts table
			HashMap<String, Condition> conditionsP = new HashMap<String, Condition>();
			conditionsP.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("PA-"+purgePeriod)));

		    Map<String, AttributeValue> lastEvaluatedKeyP = null;
		    do {
			    // Query TopCharts table
			    QueryRequest requestP = new QueryRequest()
	                .withTableName("TopCharts")
	                .withKeyConditions(conditionsP)
	                .withExclusiveStartKey(lastEvaluatedKeyP);
		        QueryResult resultP = dynamoDBClient.query(requestP);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemP : resultP.getItems()) {

	            	// Add Id to the Key and Delete
	            	keyP.put("Id", itemP.get("Id"));
		        	DeleteItemRequest deleteItemRequestP = new DeleteItemRequest()
			            .withTableName("TopCharts")
			   			.withKey(keyP);
		        	dynamoDBClient.deleteItem(deleteItemRequestP);

		        	// sleep for a second to conserve DynamoDB write throughput on TopCharts table
		        	i++;
	   				if ( i % 10 == 0 ) {
	   					Thread.sleep( 1000 );
	   				}

	            }

	        	lastEvaluatedKeyP = resultP.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyP != null);

			/*** DELETE TOP ZONES FOR THE PURGE PERIOD ***/
        	// Prepare key for delete
        	HashMap<String, AttributeValue> keyZ = new HashMap<String, AttributeValue>();
        	keyZ.put("Name", new AttributeValue().withS("Z-"+purgePeriod));

        	// Conditions for TopCharts table
			HashMap<String, Condition> conditionsZ = new HashMap<String, Condition>();
			conditionsZ.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("Z-"+purgePeriod)));

		    Map<String, AttributeValue> lastEvaluatedKeyZ = null;
		    do {
			    // Query TopCharts table
			    QueryRequest requestZ = new QueryRequest()
	                .withTableName("TopCharts")
	                .withKeyConditions(conditionsZ)
	                .withExclusiveStartKey(lastEvaluatedKeyZ);
		        QueryResult resultZ = dynamoDBClient.query(requestZ);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemZ : resultZ.getItems()) {

	            	// Add Id to the Key and Delete
	            	keyZ.put("Id", itemZ.get("Id"));
		        	DeleteItemRequest deleteItemRequestZ = new DeleteItemRequest()
			            .withTableName("TopCharts")
			   			.withKey(keyZ);
		        	dynamoDBClient.deleteItem(deleteItemRequestZ);

	            }

	        	lastEvaluatedKeyZ = resultZ.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyZ != null);

   			// Print success message
	        logger.log(Level.INFO,"purgeTopCharts successfully completed for Period=" + purgePeriod );

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"purgePeriod=" + purgePeriod);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Purges old data from PostActivity table
     * @param purgePeriod Period for which to delete records
     */
    public void purgePostActivity (long purgePeriod) {

		try {
        	// Prepare key for delete
        	HashMap<String, AttributeValue> keyB = new HashMap<String, AttributeValue>();
        	keyB.put("Period", new AttributeValue().withN(purgePeriod+""));

        	// Conditions for PostActivity table
			HashMap<String, Condition> conditionsB = new HashMap<String, Condition>();
			conditionsB.put("Period",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withN(purgePeriod+"")));

		    Map<String, AttributeValue> lastEvaluatedKeyB = null;
		    do {
			    // Query PostActivity table
			    QueryRequest requestB = new QueryRequest()
	                .withTableName("PostActivity")
	                .withKeyConditions(conditionsB)
	                .withExclusiveStartKey(lastEvaluatedKeyB);
		        QueryResult resultB = dynamoDBClient.query(requestB);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemB : resultB.getItems()) {

	            	// Add Id to the key and delete
	            	keyB.put("PostId", itemB.get("PostId"));
		        	DeleteItemRequest deleteItemRequestB = new DeleteItemRequest()
			            .withTableName("PostActivity")
			   			.withKey(keyB);
		        	dynamoDBClient.deleteItem(deleteItemRequestB);

	            }

	        	lastEvaluatedKeyB = resultB.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyB != null);

   			// Print success message
	        logger.log(Level.INFO,"purgePostActivity successfully completed for Period=" + purgePeriod );

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"purgePeriod=" + purgePeriod);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

	/**
     * Gets Trending HI for a given BloggerId from the TopCharts table.
     * @param bloggerId userId of the Blogger
     * @return HI Heat Index of the given blogger; 0 if not found
	**/
	public long getBloggerHITrending (String bloggerId) {
		long HI = 0;
		try {
			/*** Get the latest period for which job was run previously ***/
   			long generateTopChartsJobPeriod = Long.parseLong(getAttribute("T", "generateTopChartsJobPeriod"));

	    	HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("B-"+generateTopChartsJobPeriod)));
			conditions.put("Id",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(bloggerId)));

			QueryRequest request = new QueryRequest()
				.withTableName("TopCharts")
				.withKeyConditions(conditions)
	   			.withAttributesToGet("HI");

			try {
				HI = Long.parseLong(dynamoDBClient.query(request).getItems().get(0).get("HI").getN());
			} catch (Exception e) {
				// keep default value for HI
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId: " + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return HI;
	}

	/**
     * Gets Trending HI for a given PageId from the TopCharts table.
     * @param pageId userId of the Page
     * @return HI Heat Index of the given page; 0 if not found
	**/
	public long getPageHITrending (String pageId) {
		long HI = 0;
		try {
			/*** Get the latest period for which job was run previously ***/
   			long generateTopChartsJobPeriod = Long.parseLong(getAttribute("T", "generateTopChartsJobPeriod"));

	    	HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("PA-"+generateTopChartsJobPeriod)));
			conditions.put("Id",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(pageId)));

			QueryRequest request = new QueryRequest()
				.withTableName("TopCharts")
				.withKeyConditions(conditions)
	   			.withAttributesToGet("HI");

			try {
				HI = Long.parseLong(dynamoDBClient.query(request).getItems().get(0).get("HI").getN());
			} catch (Exception e) {
				// keep default value for HI
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId: " + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return HI;
	}

    /**************************************************************************
     ***********************  COMMENT MODULE  *********************************
     **************************************************************************/

	/**
     * Queries 6 previous or next comments.
     * First 5 comments will be displayed on the page -
     * 6th comment will be used to decide whether or not to display next or previous page links.
     * @param postId postId
     * @param lastEvaluatedCD starting Comment Date for the next or previous page
     * @param isForward false for Forward and true for Reverse (since we want recently created comments first)
     * @return List of Map objects with Comment information
    */
	public List<Map<String,AttributeValue>> getComments(String postId, String lastEvaluatedCD, Boolean isForward) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();

		try {
    		/*** Prepare starting record for the next or previous page ***/
            if ( StringUtils.isBlank(lastEvaluatedCD) ) {
            	lastEvaluatedKey = null;
            } else if ( StringUtils.equals(lastEvaluatedCD,"NULL") ) {
            	lastEvaluatedKey = null;
            } else {
	       		lastEvaluatedKey.put("PostId", new AttributeValue().withS(postId));
	       		lastEvaluatedKey.put("CommentDate", new AttributeValue().withN(lastEvaluatedCD));
            }

        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS(postId));

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("PostId", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Comments")
       			.withKeyConditions(keyConditions)
       			.withScanIndexForward(isForward)
       			.withLimit(6)
       			.withExclusiveStartKey(lastEvaluatedKey)
       			.withAttributesToGet("CommentDate", "CommentText","CommenterId");

       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Gets CommenterId for a given PostId and CommentDate from the Comments table.
     * @param postId postId
     * @param commentDate commentDate
     * @return UserId of the Commenter; Null if not found
	**/
	public String getCommenterId(String postId, String commentDate) {
		String commenterId = null;
		try {
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));
			conditions.put("CommentDate",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withN(commentDate)));

			QueryRequest request = new QueryRequest()
				.withTableName("Comments")
				.withKeyConditions(conditions)
	   			.withAttributesToGet("CommenterId");

			commenterId = dynamoDBClient.query(request).getItems().get(0).get("CommenterId").getS();

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " CommentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return commenterId;
	}

	/**
     * Gets Comment Text for a given PostId and CommentDate from the Comments table.
     * @param postId postId
     * @param commentDate commentDate
     * @return Comment Text; Null if not found
	**/
	public String getCommentText(String postId, String commentDate) {
		String commentText = null;
		try {
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));
			conditions.put("CommentDate",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withN(commentDate)));

			QueryRequest request = new QueryRequest()
				.withTableName("Comments")
				.withKeyConditions(conditions)
	   			.withAttributesToGet("CommentText");

			try {
				commentText = dynamoDBClient.query(request).getItems().get(0).get("CommentText").getS();
			} catch (Exception e) {
				// keep default value for commenterId
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " CommentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return commentText;
	}

	/**
     * Check if the user has already commented on the post.
     * @param postId postId
     * @param commenterId userId of the commenter
     * @return Y if commented before, N if not commented before
    */
	public String checkCommenterIdExists (String postId, String commenterId) {

		String retValue = "N";
		try {

			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));
			conditions.put("CommenterId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(commenterId)));

        	QueryRequest request = new QueryRequest()
       			.withTableName("Comments")
       			.withKeyConditions(conditions)
       			.withIndexName("CommenterIdIdx")
       			.withLimit(1)
       			.withAttributesToGet("CommenterId");

        	try {
	        	if ( dynamoDBClient.query(request).getCount() > 0 ) retValue = "Y";
			} catch (Exception e) {
				// keep default value for retValue
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " CommenterId: " + commenterId );
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return retValue;

	}

    /**
     * Reads a record from UnconfirmedComments table
     * @param unconfirmedCommentId unconfirmedCommentId
     * @return UnconfirmedComments Record
     */
	public Map<String,AttributeValue> getUnconfirmedComments (String unconfirmedCommentId) {

		GetItemResult result = null;
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("UnconfirmedCommentId", new AttributeValue().withS(unconfirmedCommentId));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("UnconfirmedComments")
			    .withKey(key);

			result = dynamoDBClient.getItem(getItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"unconfirmedCommentId=" + unconfirmedCommentId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return result.getItem();

	}

    /**
     * Inserts into UnconfirmedComments table
     * @param unconfirmedCommentId unconfirmedCommentId
     * @param zoneId zoneId
     * @param zoneName zoneName
     * @param postId postId
     * @param pageId pageId
     * @param publishFlag
     * @param parentCommentDate (0 if no parent)
     * @param origCommentText Text of the original comment, if reply
     * @param commentText Text of the comment
     * @param thankedFlag '1' for not-thanked '2' for thanked
     * @param commenterEmail Email Address
     * @param commenterId commenterId
     */
	public void putUnconfirmedComments (String unconfirmedCommentId, String zoneId, String zoneName,
			String postId, String pageId, String publishFlag,
    		long parentCommentDate, String origCommentText, String commentText, String thankedFlag,
    		String commenterEmail, String commenterId) {

		try {

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		    item.put("UnconfirmedCommentId", new AttributeValue().withS(unconfirmedCommentId));
		    item.put("ZoneId", new AttributeValue().withS(zoneId));
		    item.put("ZoneName", new AttributeValue().withS(zoneName));
		    item.put("PostId", new AttributeValue().withS(postId));
		    item.put("PageId", new AttributeValue().withS(pageId));
		    item.put("PublishFlag", new AttributeValue().withS(publishFlag));
		    item.put("ParentCommentDate", new AttributeValue().withN(parentCommentDate+""));
		    if ( StringUtils.isNotBlank(origCommentText) ) {
			    item.put("OrigCommentText", new AttributeValue().withS(origCommentText));
		    }
		    item.put("CommentText", new AttributeValue().withS(commentText));
		    item.put("ThankedFlag", new AttributeValue().withS(thankedFlag));
		    item.put("CommenterEmail", new AttributeValue().withS(commenterEmail));
		    item.put("CommenterId", new AttributeValue().withS(commenterId));
		    PutItemRequest putItemRequest = new PutItemRequest()
		    	.withTableName("UnconfirmedComments")
		    	.withItem(item);
			dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"unconfirmedCommentId=" + unconfirmedCommentId + " zoneId=" + zoneId + " zoneName=" + zoneName +
        		" postId=" + postId + " publishFlag=" + publishFlag + " parentCommentDate=" + parentCommentDate +
        		" origCommentText=" + origCommentText + " commentText=" + commentText + " thankedFlag=" + thankedFlag +
        		" commenterEmail=" + commenterEmail + " commenterId=" + commenterId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Delete from UnconfirmedComments table
     * @param unconfirmedCommentId unconfirmedCommentId
     */
	public void deleteUnconfirmedComments (String unconfirmedCommentId) {

		try {

        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("UnconfirmedCommentId", new AttributeValue().withS(unconfirmedCommentId));
        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("UnconfirmedComments")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"unconfirmedCommentId=" + unconfirmedCommentId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

    /**
     * Saves comment in Comments table
     * @param postId postId
     * @param parentCommentDate (0 if no parent)
     * @param commentText Text of the comment
     * @param commenterId userId of the commenter
     * @param thankedFlag '1' for not-thanked '2' for thanked
     * @param UTC Timestamp of the comment
     */
    public void saveComment(String postId, long parentCommentDate, String commentText, String commenterId, String thankedFlag, long UTC) {

    	try {
	    	/*** Derive commentDate as "1"+{(UTC+9999999999999 if parent) or (parentCommentDate+(9999999999999-UTC) if child)} ***/
    	    // CommentDate is the RANGE KEY, so we can't have two comments with the same commentDate.
			// We start with the UTC and decrement by a millisecond each time until we succeed.
    		String commentDate;
    		if ( parentCommentDate == 0 ) {
    			commentDate = thankedFlag+UTC+"9999999999999";
    		} else {
    			commentDate = thankedFlag+parentCommentDate+(new Long("9999999999999") - UTC);
    		}

			HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			Boolean succeeded = false;
			int i = 0;
			do {
				try {
		        	item.put("PostId", new AttributeValue().withS(postId));
		        	item.put("CommentDate", new AttributeValue().withN(commentDate));
		        	item.put("CommentText", new AttributeValue().withS(commentText));
		        	item.put("CommenterId", new AttributeValue().withS(commenterId));

		            // don't insert two comments at the same millisecond
		            Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
		            expected.put("CommentDate", new ExpectedAttributeValue()
		            	.withExists(false));

		        	PutItemRequest putItemRequest = new PutItemRequest()
			            .withTableName("Comments")
			   			.withItem(item)
			   			.withExpected(expected);

		        	dynamoDBClient.putItem(putItemRequest);
		        	succeeded = true;
				} catch (ConditionalCheckFailedException e) {
    	    		// another record exists in the same millisecond
					// let's subtract one millisecond to the failed transaction and try again
    	           	UTC--;
    	    		if ( parentCommentDate == 0 ) {
    	    			commentDate = thankedFlag+UTC+"9999999999999";
    	    		} else {
    	    			commentDate = thankedFlag+parentCommentDate+(new Long("9999999999999") - UTC);
    	    		}
		        	item.put("CommentDate", new AttributeValue().withN(commentDate));
    	           	i++;
    	    	}
			} while (!succeeded && i<50); // 50 attempts is the max I can take

		} catch (Exception e) {
	        logger.log(Level.SEVERE," postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

    }

	/**
     * Updates Comments table with CommentText
     * @param postId postId
     * @param commentDate commentDate
     * @param commentText commentText
     */
	public void updateComment(String postId, String commentDate, String commentText) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		    key.put("PostId", new AttributeValue().withS(postId));
		    key.put("CommentDate", new AttributeValue().withN(commentDate));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("CommentText",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS(commentText))
	                .withAction(AttributeAction.PUT)
	    		);

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Comments")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);
		    dynamoDBClient.updateItem(updateItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

    /**
     * Thank a given parent comment on the post. If the parent comment has replies,
     * they all will be thanked too. Replies can't be thanked separately.
     * @param postId postId
     * @param commentDate commentDate
     * @param thankFlag 2 for thank, 1 for unthank
     */
	public void thankComment(String postId, String commentDate, String thankFlag) {

		try {

			/*** Define variables ***/
			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));
			DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	        	.withTableName("Comments")
	        	.withKey(key);

			/*** Thank replies ***/
			// Parent Comment Date
			String pcd = StringUtils.substring(commentDate, 1, 14);

			// Conditions for Comments table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));

			// Last Evaluated Key to Start with
			Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
			lastEvaluatedKey.put("PostId", new AttributeValue().withS(postId));
			lastEvaluatedKey.put("CommentDate", new AttributeValue().withN(commentDate));

			boolean all_done = false; // all records with the input parent key have been processed
			do {
			    // Query Comments table
			   	QueryRequest request = new QueryRequest()
		               .withTableName("Comments")
		               .withKeyConditions(conditions)
		               .withExclusiveStartKey(lastEvaluatedKey)
		               .withScanIndexForward(false);
			   	QueryResult result = dynamoDBClient.query(request);
			   	List<Map<String, AttributeValue>> items = result.getItems();

			   	// Loop through each record
			   	boolean fetch_done = false; // all records from this fetch have been read
			   	int i = 0;
			   	do {

			   		// extract comment date
			   		Map<String, AttributeValue> item = null;
			   		String commentDate1 = null;
			   		try {
			   			item = items.get(i);
			   			commentDate1 = item.get("CommentDate").getN();
			   		} catch (Exception e) {
			   			fetch_done = true; // all records from this fetch have been read
			   		}
			   		// increment the counter
			   		i++;
			   		// thank if pcd matches with the input, or else exit
		    		if ( StringUtils.equals(StringUtils.substring(commentDate1, 1, 14), pcd) ) {
		    			// delete the record
			           	key.put("CommentDate", new AttributeValue().withN(commentDate1));
			           	dynamoDBClient.deleteItem(deleteItemRequest);
			           	// insert a new record with updated commentDate
			           	item.put("CommentDate", new AttributeValue().withN(thankFlag+StringUtils.substring(commentDate1,1)));
			           	PutItemRequest pir = new PutItemRequest()
			           		.withTableName("Comments")
			           		.withItem(item);
			           	dynamoDBClient.putItem(pir);
		    		} else {
		    			all_done = true; // all records with the input parent key have been processed
		    		}

			   	} while (!all_done && !fetch_done);

			   	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null && !all_done);

			/*** Thank the parent ***/
			// query the parent record
			HashMap<String, Condition> conditionsP = new HashMap<String, Condition>();
			conditionsP.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));
			conditionsP.put("CommentDate",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withN(commentDate)));

			QueryRequest requestP = new QueryRequest()
				.withTableName("Comments")
				.withKeyConditions(conditionsP);
			Map<String, AttributeValue> item = dynamoDBClient.query(requestP).getItems().get(0);

			// delete the parent record
           	key.put("CommentDate", new AttributeValue().withN(commentDate));
           	dynamoDBClient.deleteItem(deleteItemRequest);

           	// insert a new record with updated commentDate
           	item.put("CommentDate", new AttributeValue().withN(thankFlag+StringUtils.substring(commentDate,1)));
           	PutItemRequest pir = new PutItemRequest()
           		.withTableName("Comments")
           		.withItem(item);
           	dynamoDBClient.putItem(pir);

		} catch (Exception e) {
	        logger.log(Level.SEVERE," postId: " + postId + " commentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Delete a given parent comment or reply on the post; If the comment is a parent with replies,
     * they all will be deleted too.
     * @param postId postId
     * @param commentDate commentDate
     */
	public void deleteComment(String postId, String commentDate) {

		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("PostId", new AttributeValue().withS(postId));
			DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	        	.withTableName("Comments")
	        	.withKey(key);

			// if parent, delete the replies
			if ( StringUtils.equals(StringUtils.substring(commentDate, 14, 27), "9999999999999") ) {

				// Parent Comment Date
				String pcd = StringUtils.substring(commentDate, 1, 14);

				// Conditions for Comments table
				HashMap<String, Condition> conditions = new HashMap<String, Condition>();
				conditions.put("PostId",
					new Condition()
						.withComparisonOperator(ComparisonOperator.EQ)
						.withAttributeValueList(new AttributeValue().withS(postId)));

				// Last Evaluated Key to Start with
				Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
				lastEvaluatedKey.put("PostId", new AttributeValue().withS(postId));
				lastEvaluatedKey.put("CommentDate", new AttributeValue().withN(commentDate));

		    	boolean all_done = false; // all records with the input parent key have been processed
				do {
				    // Query Comments table
			    	QueryRequest request = new QueryRequest()
		                .withTableName("Comments")
		                .withKeyConditions(conditions)
		                .withExclusiveStartKey(lastEvaluatedKey)
		                .withScanIndexForward(false)
		                .withAttributesToGet("CommentDate");
			    	QueryResult result = dynamoDBClient.query(request);
			    	List<Map<String, AttributeValue>> items = result.getItems();

			        // Loop through each record
			    	boolean fetch_done = false; // all records from this fetch have been read
			    	int i = 0;
			    	do {
			    		// extract comment date
			    		String commentDate1 = null;
			    		try {
			    			commentDate1 = items.get(i).get("CommentDate").getN();
			    		} catch (Exception e) {
			    			fetch_done = true; // all records from this fetch have been read
			    		}
			    		// increment the counter
			    		i++;
			    		// delete if pcd matches with the input, or else exit
		    			if ( StringUtils.equals(StringUtils.substring(commentDate1, 1, 14), pcd) ) {
			            	key.put("CommentDate", new AttributeValue().withN(commentDate1));
			            	dynamoDBClient.deleteItem(deleteItemRequest);
		    			} else {
		    				all_done = true; // all records with the input parent key have been processed
		    			}
		    		} while (!all_done && !fetch_done);

		            lastEvaluatedKey = result.getLastEvaluatedKey();
			    } while (lastEvaluatedKey != null || !all_done);

			}

			// delete the input comment date
			key.put("CommentDate", new AttributeValue().withN(commentDate));
			dynamoDBClient.deleteItem(deleteItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE," postId: " + postId + " commentDate: " + commentDate);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

    /**
     * Delete all comments on a post
     * @param postId postId
     * @return number of comments deleted
     */
	public int deleteComments(String postId) {
		int commentCount = 0;
		try {
			// Conditions for Comments table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));

			Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
			    // Query Comments table
		    	QueryRequest request = new QueryRequest()
	                .withTableName("Comments")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("CommentDate");
		    	QueryResult result = dynamoDBClient.query(request);

            	// Prepare key for delete
		    	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            	key.put("PostId", new AttributeValue().withS(postId));
            	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
            		.withTableName("Comments")
            		.withKey(key);

		        // Loop through each CommentDate and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {
	            	key.put("CommentDate", new AttributeValue().withN(item.get("CommentDate").getN()));
	            	dynamoDBClient.deleteItem(deleteItemRequest);
	            	commentCount++;
	            }

	            lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE," postId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return commentCount;
    }

    /**
     * Insert into CommentFollowers table
     * @param postId postId
     * @param email Email Address of the follower
     * @return followerId
     */
    public String followComments (String postId, String email) {

    	String followerId = null;
		try {
			// Check if the user is already a follower
			followerId = getFollowerId(postId, email);
    		if ( StringUtils.equals(followerId,"NULL") ) {

    			// generate unique follower id
		        UUID followerUUID = UUID.randomUUID();
		        followerId = followerUUID.toString();

		        // create item
				HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	        	item.put("PostId", new AttributeValue().withS(postId));
	        	item.put("FollowerId", new AttributeValue().withS(followerId));
	        	item.put("Email", new AttributeValue().withS(email));

	        	// insert into CommentFollowers table
	        	PutItemRequest putItemRequestCF = new PutItemRequest()
		            .withTableName("CommentFollowers")
		   			.withItem(item);
	        	dynamoDBClient.putItem(putItemRequestCF);

    		}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return followerId;
    }

    /**
     * Deletes from CommentFollowers table
     * @param postId postId
     * @param FollowerId UUID of the follower
     * @return UNSUBSCRIBED or NOT A FOLLOWER indicating the status of the operation
     */
    public String unfollowComments (String postId, String followerId) {
    	String retStatus = "UNSUBSCRIBED"; // default
		try {

			/*** Delete from CommentFollowers ***/
        	HashMap<String, AttributeValue> keyCF = new HashMap<String, AttributeValue>();
        	keyCF.put("PostId", new AttributeValue().withS(postId));
        	keyCF.put("FollowerId", new AttributeValue().withS(followerId));

        	DeleteItemRequest deleteItemRequestCF = new DeleteItemRequest()
	            .withTableName("CommentFollowers")
	   			.withKey(keyCF)
	   			.withReturnValues("ALL_OLD");
        	DeleteItemResult resultCF = dynamoDBClient.deleteItem(deleteItemRequestCF);

        	try {
        		if ( resultCF.getAttributes().size() == 0 ) {
            		retStatus = "NOT A FOLLOWER";
            		return retStatus;
        		}
        	} catch (Exception e) {
        		retStatus = "NOT A FOLLOWER";
        		return retStatus;
        	}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " FollowerId: " + followerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return retStatus;
    }

    /**
     * Count of records from CommentFollowers table for a given email address
     * @param email Email address of the user
     * @return count of followed posts
     */
    public int getCFCount (String email) {

    	int countFC = 0;
		try {

			Condition hashKeyConditionFC = new Condition()
          		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(email));

           	Map<String, Condition> keyConditionsFC = new HashMap<String, Condition>();
           	keyConditionsFC.put("Email", hashKeyConditionFC);

           	Map<String, AttributeValue> lastEvaluatedKeyFC = null;
           	do
           	{

           		QueryRequest requestFC = new QueryRequest()
	       			.withTableName("CommentFollowers")
	       			.withIndexName("CFEmailIdx")
	       			.withKeyConditions(keyConditionsFC)
	       			.withExclusiveStartKey(lastEvaluatedKeyFC)
	       			.withAttributesToGet("PostId");

	           	QueryResult resultFC = dynamoDBClient.query(requestFC);
	           	countFC = countFC + resultFC.getCount();

	           	lastEvaluatedKeyFC = resultFC.getLastEvaluatedKey();
           	} while (lastEvaluatedKeyFC != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return countFC;
    }

    /**
     * Count of records from CommentFollowers table for a given postId
     * @param postId post Id
     * @return count of followers
     */
    public int getCommentFollowersCount(String postId) {

    	int countCF = 0;
		try {

			Condition hashKeyConditionCF = new Condition()
          		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(postId));

           	Map<String, Condition> keyConditionsCF = new HashMap<String, Condition>();
           	keyConditionsCF.put("PostId", hashKeyConditionCF);

           	Map<String, AttributeValue> lastEvaluatedKeyCF = null;
           	do
           	{
	           	QueryRequest requestCF = new QueryRequest()
	       			.withTableName("CommentFollowers")
	       			.withKeyConditions(keyConditionsCF)
	       			.withExclusiveStartKey(lastEvaluatedKeyCF)
	       			.withAttributesToGet("FollowerId");

	           	QueryResult resultCF = dynamoDBClient.query(requestCF);
	           	countCF = countCF + resultCF.getCount();

	           	lastEvaluatedKeyCF = resultCF.getLastEvaluatedKey();
           	} while (lastEvaluatedKeyCF != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
		return countCF;
    }

    /**
     * Deletes all records for a given Email from the CommentFollowers table
     * @param email Email address of the user
     */
    public void unfollowAllComments (String email) {

    	try {
			// Conditions for CommentFollowers table
           	HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("Email",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(email)));

			// Define variables
			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	DeleteItemRequest deleteItemRequestCF = new DeleteItemRequest()
	    		.withTableName("CommentFollowers")
	    		.withKey(key);

			Map<String, AttributeValue> lastEvaluatedKey = null;
        	do {
			    // Query CommentFollowers table
			    QueryRequest request = new QueryRequest()
			    	.withTableName("CommentFollowers")
			    	.withIndexName("CFEmailIdx")
			    	.withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("PostId","FollowerId");
			    QueryResult result = dynamoDBClient.query(request);

		        // Loop through each record and delete
			    int insertCount = 0;
	            for (Map<String, AttributeValue> item : result.getItems()) {

	            	key.put("PostId", item.get("PostId"));
	            	key.put("FollowerId", item.get("FollowerId"));
	            	dynamoDBClient.deleteItem(deleteItemRequestCF);
	            	insertCount++;
	                // sleep for a second to conserve DynamoDB write throughput on CommentFollowers table
	   				if ( insertCount % 6 == 0 ) {
						logger.log(Level.INFO, "Deleted " + insertCount + " rows from CommentFollowers for email=" + email + ".");
	   					Thread.sleep( 1000 );
	   				}

	            }

	            lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

	/**
     * Gets the FollowerId for a given postId and Email.
     * @param postId postId
     * @param email Email address of the user
     * @return FollowerId; null if not found
	**/
	public String getFollowerId (String postId, String email) {
		String followerId = "NULL"; // default
		try {
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("Email",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(email)));
			conditions.put("PostId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(postId)));

			QueryRequest request = new QueryRequest()
				.withTableName("CommentFollowers")
				.withIndexName("CFEmailIdx")
				.withKeyConditions(conditions)
				.withLimit(1)
	   			.withAttributesToGet("FollowerId");

			try {
				followerId = dynamoDBClient.query(request).getItems().get(0).get("FollowerId").getS();
			} catch (Exception e) {
				// keep default value for followerId
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return followerId;
	}

    /**
     * Updates Email Address in the CommentFollowers table
     * @param email email address of the user
     * @param newEmail New Email address of the user
     */
    public void updateCFEmail (String email, String newEmail) {

    	try {
			// Conditions for CommentFollowers table
           	HashMap<String, Condition> conditionsCF = new HashMap<String, Condition>();
			conditionsCF.put("Email",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(email)));

			Map<String, AttributeValue> lastEvaluatedKeyCF = null;
		    do
		    {
			    // Query CommentFollowers table
			    QueryRequest requestCF = new QueryRequest()
			    	.withTableName("CommentFollowers")
			    	.withIndexName("CFEmailIdx")
			    	.withKeyConditions(conditionsCF)
	                .withExclusiveStartKey(lastEvaluatedKeyCF)
	                .withAttributesToGet("PostId","FollowerId");
			    QueryResult resultCF = dynamoDBClient.query(requestCF);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemCF : resultCF.getItems()) {

	            	itemCF.put("Email", new AttributeValue().withS(newEmail));
	            	PutItemRequest putItemRequestCF = new PutItemRequest()
        	            .withTableName("CommentFollowers")
	        	   		.withItem(itemCF);
		        	dynamoDBClient.putItem(putItemRequestCF);

	            }

	            lastEvaluatedKeyCF = resultCF.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyCF != null);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Sends email to each comment-follower on the post
     * @param postId postId
     * @param commenterId UserId of the commenter
     * @param commenterName Pen Name of the commenter
     * @param commenterEmail Email address of the commenter
     * @param origCommentText Text of the original comment, if reply
     * @param commentText Text of the comment
     */
    public void emailCommentFollowersJob (String postId, String commenterId, String commenterName, String commenterEmail, String origCommentText, String commentText) {
		try {
	       	String postTitle = getPostTitle(postId);

	       	Condition hashKeyConditionCF = new Condition()
          		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(postId));

           	Map<String, Condition> keyConditionsCF = new HashMap<String, Condition>();
           	keyConditionsCF.put("PostId", hashKeyConditionCF);

           	Map<String, AttributeValue> lastEvaluatedKeyCF = null;
           	do
           	{

	           	QueryRequest requestCF = new QueryRequest()
	       			.withTableName("CommentFollowers")
	       			.withKeyConditions(keyConditionsCF)
	       			.withExclusiveStartKey(lastEvaluatedKeyCF)
	       			.withAttributesToGet("FollowerId","Email");

	           	QueryResult resultCF = dynamoDBClient.query(requestCF);

	           	for (Map<String, AttributeValue> itemCF : resultCF.getItems()) {

			       	String followerId = itemCF.get("FollowerId").getS();
			       	String followerEmail = itemCF.get("Email").getS();
			       	String followerFirstName = getFirstName(followerEmail);
			       	// don't send email if the follower is the commenter
			       	if ( !StringUtils.equals(commenterEmail, followerEmail) ) {
			       		ses.sendNewCommentEmail(followerId, followerEmail, followerFirstName, postId, postTitle, commenterId, commenterName, origCommentText, commentText);
			       	}

	           	}

	           	lastEvaluatedKeyCF = resultCF.getLastEvaluatedKey();
           	} while (lastEvaluatedKeyCF != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"PostId: " + postId + " CommenterName: "+ commenterName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

	/**************************************************************************
     *********************  TOP CHARTS MODULE  ********************************
     **************************************************************************/

	/**
     * Queries postsPerPage+1 number of previous or next top posts, zones, bloggers or pages for Top Charts page.
     * First postsPerPage records will be displayed on the page.
     * (postsPerPage)th record will be used to decide whether or not to display next or previous page links.
     * @param name Name of the Chart
     * @param lastEvaluatedId starting Id for the next or previous page
     * @param lastEvaluatedHI starting HI for the next or previous page
     * @param isForward false for Forward and true for Reverse
     * @return List of Map objects with Id's
    */
	public List<Map<String,AttributeValue>> getTopCharts(String name, String lastEvaluatedId, String lastEvaluatedHI, Boolean isForward) {

		Map<String,AttributeValue> lastEvaluatedKey = new HashMap<String,AttributeValue>();
		QueryResult result = new QueryResult();
    	int postsPerPage = Integer.parseInt(config.getProperty("postsPerPage"));

		try {
            if ( StringUtils.isBlank(lastEvaluatedHI) ) {
            	lastEvaluatedKey = null;
            } else {
	    		/*** Prepare starting record for the next or previous page ***/
	       		lastEvaluatedKey.put("Name", new AttributeValue().withS(name));
	       		lastEvaluatedKey.put("Id", new AttributeValue().withS(lastEvaluatedId));
	       		lastEvaluatedKey.put("HI", new AttributeValue().withN(lastEvaluatedHI));
            }

            Map<String, Condition> keyConditions = new HashMap<String, Condition>();
	        keyConditions.put("Name", new Condition()
	        	.withComparisonOperator(ComparisonOperator.EQ.toString())
	        	.withAttributeValueList(new AttributeValue().withS(name)));

	        QueryRequest queryRequest = new QueryRequest()
	       		.withTableName("TopCharts")
	       		.withIndexName("HIIdx")
	       		.withKeyConditions(keyConditions)
	       		.withScanIndexForward(isForward)
	       		.withLimit(postsPerPage+1)
	       		.withExclusiveStartKey(lastEvaluatedKey)
	       		.withAttributesToGet("Id", "HI", "ZoneName");
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"lastEvaluatedKey: " + lastEvaluatedKey);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Purges Just Published records from the Top Charts table.
     * @return Message indicating SUCCESS or FAILURE
     **/
	public String purgeJustPublished() {

		String stepName = null;
		String retMessage = "FAILURE"; //default
    	try {

       		/*** Derive purgeUTC ***/
   		    stepName = "Derive purgeUTC";
            long purgeUTC = (long) System.currentTimeMillis() - Long.parseLong(config.getProperty("justPublishedRetentionDays"))*24*3600*1000;

       		/*** Conditions for TopCharts table ***/
   		    stepName = "Conditions for TopCharts table";
	    	HashMap<String, Condition> conditions = new HashMap<String, Condition>();
	    	conditions.put("Name",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS("J")));
			conditions.put("HI",
				new Condition()
					.withComparisonOperator(ComparisonOperator.LT)
					.withAttributeValueList(new AttributeValue().withN(purgeUTC+"")));

			Map<String,AttributeValue> lastEvaluatedKey = null;
   		    do {
		    	// Query TopCharts table
   		        QueryRequest queryRequest = new QueryRequest()
   		       		.withTableName("TopCharts")
   		       		.withIndexName("HIIdx")
   		       		.withKeyConditions(conditions)
   		       		.withExclusiveStartKey(lastEvaluatedKey)
   		       		.withAttributesToGet("Name","Id");
   	       		QueryResult result = dynamoDBClient.query(queryRequest);

		        // Loop through each record
		        for (Map<String, AttributeValue> item : result.getItems()) {

		        	// Delete the item from TopCharts table
		   		    stepName = "Deleting Id=" + item.get("Id").getS();
		        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		        		.withTableName("TopCharts")
		        		.withKey(item);
		        	dynamoDBClient.deleteItem(deleteItemRequest);

		        }

   			    lastEvaluatedKey = result.getLastEvaluatedKey();
   		    } while (lastEvaluatedKey != null);

	        retMessage = "SUCCESS";

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Step Name: " + stepName );
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

    	return retMessage;

	}

	/**
     * Send a reminder email to the bloggers for unpublished posts
     * @return retMessage Success or Error message
     */
    public String remindUnpublished() {

    	String stepName = "none";
    	String retMessage = "SUCCESS";
    	String postId = null;

    	try {

    		/*** Define throughput needed for this job ***/
    		long neededReadCapacityPosts = 12L;
    		long neededWriteCapacityPosts = 2L;

    		/*** Query current throughput of Posts table ***/
    		DescribeTableRequest request = new DescribeTableRequest()
    			.withTableName("Posts");
    		TableDescription tableDescription = dynamoDBClient
    			.describeTable(request).getTable();
    		long currentReadCapacityPosts = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
    		long currentWriteCapacityPosts = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

          	/*** Increase throughput of Posts table ***/
    		if ( currentReadCapacityPosts < neededReadCapacityPosts ||
    			 currentWriteCapacityPosts < neededWriteCapacityPosts
    			) {
	    		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
	    			.withReadCapacityUnits(neededReadCapacityPosts)
	    		    .withWriteCapacityUnits(neededWriteCapacityPosts);
	    		UpdateTableRequest updateTableRequest = new UpdateTableRequest()
	    			.withTableName("Posts")
	    			.withProvisionedThroughput(provisionedThroughput);
	    		dynamoDBClient.updateTable(updateTableRequest);
	    		// wait for the capacity increase
	    		waitForTargetCapacity("Posts", neededReadCapacityPosts, neededWriteCapacityPosts);
    		}

    		// Get the current System Date
            long currentUTC = (long) System.currentTimeMillis();
        	// Get the latest UpdateDate for which email reminder was sent
   			long lastReminderUpdateDate = Long.parseLong(getAttribute("A", "lastReminderUpdateDate"));

   			// Set of Active Pages
       		Set<String> activePages = new HashSet<String>();
       		Map<String, AttributeValue> lastEvaluatedKey = null;
       		do
       		{
       			stepName = "Reading from PagePayments table";
        		ScanRequest scanRequest = new ScanRequest()
    				.withTableName("PagePayments")
    				.withExclusiveStartKey(lastEvaluatedKey)
    				.withAttributesToGet("PageId", "EndDate");
        		ScanResult result = dynamoDBClient.scan(scanRequest);

				// current date plus seven days of grace period
	    		long currentDate = System.currentTimeMillis() - 7*24*60*60*1000L;

	    		for (Map<String,AttributeValue> item : result.getItems()) {
					String pageId = item.get("PageId").getS();
					long endDate = Long.parseLong(item.get("EndDate").getN());
					if ( endDate == 1 || endDate > currentDate ) activePages.add(pageId);
	    		}

	        	// sleep for 10 seconds for every read
	        	Thread.sleep( 10 * 1000 );

	    		lastEvaluatedKey = result.getLastEvaluatedKey();
       		} while (lastEvaluatedKey != null);

	        /*** Gather posts which have been unpublished state for more than 5 days ***/
    		// unsortedEntityIds	= EntityId : Maximum update date 
    		// posts 				= EntityId : List of posts
    		// Sorting by update date helps us restart the job from the exact point of failure
       		Map<String, Long> unsortedEntityIds = new HashMap<String, Long>();
       		Map<String, List<String>> posts = new HashMap<String, List<String>>();
       		Map<String, String> inactiveBloggers = new HashMap<String, String>();
       		lastEvaluatedKey = null;
       		String pageId = null;
       		do
       		{
       			stepName = "Reading from Posts table";
        		ScanRequest scanRequest = new ScanRequest()
    				.withTableName("Posts")
    				.withExclusiveStartKey(lastEvaluatedKey)
    				.withAttributesToGet("PostId", "BloggerId", "PublishFlag", "UpdateDate", "PageId");
        		ScanResult result = dynamoDBClient.scan(scanRequest);

	    		for (Map<String,AttributeValue> item : result.getItems()) {

					// read data from item
					postId = item.get("PostId").getS();
					String bloggerId = item.get("BloggerId").getS();
					String publishFlag = item.get("PublishFlag").getS();
					Long updateDate = Long.parseLong(item.get("UpdateDate").getN());
					try { pageId = item.get("PageId").getS(); } catch (Exception e) { pageId = null; }

	    			if ( StringUtils.equals(publishFlag,"N")
	   					 && updateDate < currentUTC - 5*24*3600*1000
	   					 && updateDate > lastReminderUpdateDate
	   					 && !StringUtils.containsAny(postId, common.unwantedPosts)
	    				) {

	    				// check if the bloggerId already existed in the map
	    				Long prevUpdateDate = unsortedEntityIds.get(bloggerId);

	    				// insert into unsortedEntityIds if the key didn't exist or if the date is more current
	    				if ( prevUpdateDate == null || updateDate > -prevUpdateDate ) {
	        				unsortedEntityIds.put(bloggerId, -updateDate);
	    				}

	    				// add postId to the list
	    				List<String> postIds = new ArrayList<String>();
	    				if ( prevUpdateDate != null ) {
	    					postIds = posts.get(bloggerId);
	    				}
						postIds.add(postId);
	    				posts.put(bloggerId, postIds);

		    			if ( StringUtils.isNotBlank(pageId) && !activePages.contains(pageId) ) {
		    				inactiveBloggers.put(bloggerId, pageId);
		    			}

	    			}

	    		}

	        	// sleep for 10 seconds for every read
	        	Thread.sleep( 10 * 1000 );

	    		lastEvaluatedKey = result.getLastEvaluatedKey();
       		} while (lastEvaluatedKey != null);

	        /*** Send emails for all those bloggers in the unsortedEntityIds ***/
       		List<Entity> potentialCustomers = new ArrayList<Entity>();
	        for (Map.Entry<String, Long> sortedEntityId : sortByValuesDesc(unsortedEntityIds) ) {

   		        /*** Get EntityId and details from sortedEntityId ***/
   				String bloggerId = sortedEntityId.getKey();
   				Long updateDate = sortedEntityId.getValue();
   				String entityEmail = getEntityEmail(bloggerId);
   				String entityName = getEntityName(bloggerId);

   		        /*** This person is a potentialCustomer ***/
    			if ( inactiveBloggers.containsKey(bloggerId) ) {
	   				Entity potentialCustomer = new Entity();
	   				potentialCustomer.setEntityId(bloggerId);
	   				potentialCustomer.setEntityEmail(entityEmail);
	   				potentialCustomer.setEntityName(entityName);
	   				potentialCustomer.setPrimaryPageId(inactiveBloggers.get(bloggerId));
	   				potentialCustomers.add(potentialCustomer);
    			}

   		        /*** Get PostIds from posts ***/
				List<String> postIds = posts.get(bloggerId);

   				/*** Send email ***/
				String flag = getNotificationFlag(entityEmail, "remindDraftPost");
				if ( StringUtils.equals(flag, "Y") ) {
	       			stepName = "Sending email to " + entityEmail;
					ses.sendUnpublishedBusinessEmail(entityName, entityEmail, postIds);
				}

   				/*** Update attributes table ***/
       			setAttribute("A", "lastReminderUpdateDate", -updateDate+"");
	        }

	        /*** Email the list of potentialCustomers to sales team ***/
	        if ( !potentialCustomers.isEmpty() ) {
	        	ses.sendPotentialCustomersEmail(potentialCustomers);
	        }

          	/*** Reset throughput of Posts table to the original values ***/
        	// since we consumed more-than-normal throughput on the table, give it a minute to cool down
        	Thread.sleep( 60 * 1000 );
        	ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
    			.withReadCapacityUnits(common.resetReadCapacityPosts)
    		    .withWriteCapacityUnits(common.resetWriteCapacityPosts);
    		UpdateTableRequest updateTableRequest = new UpdateTableRequest()
    			.withTableName("Posts")
    			.withProvisionedThroughput(provisionedThroughput);
    		dynamoDBClient.updateTable(updateTableRequest);

    	} catch (Exception e) {
			retMessage = "stepName=" + stepName + " postId=" + postId;
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return retMessage;
    }

	/**
     * Send a reminder email to those who have started a page payment but not completed yet
     * @return retMessage Success or Error message
     */
    public String remindPagePayments() {

    	String stepName = "none";
    	String retMessage = "SUCCESS";
    	String paymentHandler = null;

    	try {

   			// Loop through PendingPagePayments
       		Map<String, AttributeValue> lastEvaluatedKey = null;
       		do
       		{
       			stepName = "Reading from PendingPagePayments table";
        		ScanRequest scanRequest = new ScanRequest()
    				.withTableName("PendingPagePayments")
    				.withExclusiveStartKey(lastEvaluatedKey);
        		ScanResult result = dynamoDBClient.scan(scanRequest);

        		// Status can be
        		// NONE 			- Payment has been initiated but user closed the browser window
        		// SUCCESS			- Payment has been successfully processed
        		// ERROR			- User has canceled the payment or some other error
        		// DELETED			- User has manually deleted the payment
        		// REMINDER-SENT	- Email has been sent to the user
        		String status = "UNKNOWN"; // we have not read the status yet
	    		for (Map<String,AttributeValue> item : result.getItems()) {

	       			stepName = "read current status";
	    			try {
						status = item.get("Status").getS();
					} catch (Exception e) {
						status = "UNKNOWN";
					}

	       			stepName = "proceed if NONE or ERROR";
					if ( StringUtils.equals(status, "NONE") || StringUtils.equals(status, "ERROR") ) {

		       			stepName = "read the record";
						paymentHandler = item.get("PaymentHandler").getS();
						long handlerDate = Long.parseLong(item.get("HandlerDate").getN());
						String pageId = item.get("PageId").getS();
						String productType = item.get("ProductType").getS();
						long amount = Long.parseLong(item.get("Amount").getN());
						String coupon = null;
						try { coupon = item.get("Coupon").getS(); } catch (Exception e) {}
						String createdBy = item.get("CreatedBy").getS();

		       			stepName = "update status to REMINDER-SENT";
		                HashMap<String, AttributeValue> keyUpdatePP = new HashMap<String, AttributeValue>();
		                keyUpdatePP.put("PaymentHandler", new AttributeValue().withS(paymentHandler));

		                Map<String, AttributeValueUpdate> updateItemsPP = new HashMap<String, AttributeValueUpdate>();
		                updateItemsPP.put("Status",
		               		new AttributeValueUpdate()
		               			.withValue(new AttributeValue().withS("REMINDER-SENT"))
		               			.withAction(AttributeAction.PUT)
		               		);

		                UpdateItemRequest requestPP = new UpdateItemRequest()
		                	.withTableName("PendingPagePayments")
		                	.withKey(keyUpdatePP)
		                	.withAttributeUpdates(updateItemsPP);
		                dynamoDBClient.updateItem(requestPP);

		       			stepName = "get entity details for createdBy";
		       			Entity createdByDetails = getEntity(createdBy);

		       			if ( !StringUtils.equals(createdByDetails.getEntityName(), "Deleted User") ) {
			       			stepName = "get entity details for pageId";
			       			Entity pageDetails = getEntity(pageId);

			       			stepName = "send email to " + createdByDetails.getEntityEmail();
							ses.sendPendingPagePaymentEmail(paymentHandler, handlerDate,
								createdByDetails.getEntityName(), createdByDetails.getEntityEmail(),
								pageId, pageDetails.getEntityName(),
								productType, amount, coupon);
		       			}

					}

	    		}

	        	// sleep for 10 seconds for every read
	        	Thread.sleep( 10 * 1000 );

	    		lastEvaluatedKey = result.getLastEvaluatedKey();
       		} while (lastEvaluatedKey != null);

    	} catch (Exception e) {
			retMessage = "stepName=" + stepName + " paymentHandler=" + paymentHandler;
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return retMessage;
    }

	/**
     * Purge unsuccessful cron jobs
     * @return retMessage Success or Error message
     */
    public String purgeUnsuccessfulCronjobs() {

    	String stepName = "none";
    	String retMessage = "SUCCESS";
    	String attrValue = null;
    	try {

    		// Evaluate current period
            long currentPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

            // Purge generateTopChartsJob
            for ( String jobName : new String[]{"generateTopChartsJob", "dailyAdminJob", "generateGuestRLJob"} ) {
	            stepName = "Running " + jobName;
	            attrValue = getAttribute("STARTING", jobName);
	            if ( StringUtils.isNotBlank(attrValue) ) {
	            	long runPeriod = Long.parseLong(StringUtils.substring(attrValue, 0, StringUtils.indexOf(attrValue, ":")));
	            	if ( runPeriod < currentPeriod-3 ) {
	            		logger.log(Level.SEVERE, "Cleaning up... " + jobName + " AttrValue=" + attrValue);
	                	HashMap<String, AttributeValue> keyA = new HashMap<String, AttributeValue>();
	                	DeleteItemRequest deleteItemRequestA = new DeleteItemRequest()
	        	            .withTableName("Attributes")
	        	   			.withKey(keyA);
	                	keyA.put("AttrName", new AttributeValue().withS("STARTING"+"-"+jobName));
	                	dynamoDBClient.deleteItem(deleteItemRequestA);
	            	}
	            }
            }

    	} catch (Exception e) {
			retMessage = "stepName=" + stepName;
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
   		return retMessage;
    }

	/**************************************************************************
     ***********************  DROP USER ACCOUNT  ******************************
     **************************************************************************/

    /**
     * Permanently Delete EVERYTHING related to the user.
     * @param username Email Address
     * @param userId userId
     */
	public void dropAccount(String username, String userId) {

		HashMap<String, AttributeValue> key;
		DeleteItemRequest deleteItemRequest;
		HashMap<String, Condition> conditions;
		Map<String, AttributeValue> lastEvaluatedKey;
		QueryRequest request;
		QueryResult result;

		String stepName = null;

		try {
        	/*** Delete from Notifications table ***/
			stepName = "Delete from Notifications";
        	deleteNotificationFlags(username);

		    /*** Reset data in Bloggers table ***/
			stepName = "Reset data in Bloggers";
		    key = new HashMap<String, AttributeValue>();
		    key.put("EntityId", new AttributeValue().withS(userId));

		    Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
		    updateItems.put("EntityName",
	    		new AttributeValueUpdate()
	    			.withValue(new AttributeValue().withS("Deleted User"))
	                .withAction(AttributeAction.PUT)
	    		);
		    updateItems.put("ProfilePhoto",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
	    		);
		    updateItems.put("ProfileBG",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
	    		);
		    updateItems.put("About",
			    new AttributeValueUpdate()
			    	.withValue(new AttributeValue().withS("This user has voluntarily left Heatbud."))
			    	.withAction(AttributeAction.PUT)
			    );
		    updateItems.put("Passion",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
		    	);
		    updateItems.put("Achievements",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
		    	);
		    updateItems.put("Announcements",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
	    		);
		    updateItems.put("Contact",
	    		new AttributeValueUpdate()
	                .withAction(AttributeAction.DELETE)
	    		);
		    updateItems.put("EnableEmail",
			    new AttributeValueUpdate()
			    	.withValue(new AttributeValue().withS("N"))
			    	.withAction(AttributeAction.PUT)
			    );

		    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
	            .withTableName("Entities")
	            .withKey(key)
	            .withAttributeUpdates(updateItems);

		    dynamoDBClient.updateItem(updateItemRequest);

		    /*** Delete Blogger TopCharts ***/
			stepName = "Delete Blogger TopCharts";
			deleteBloggerTopCharts(userId);

		    /*** Loop through PostsByEntity table for PublishFlag=Y ***/
			stepName = "Loop PostsByEntity for PublishFlag=Y";
	    	// Conditions for PostsByEntity table
			conditions = new HashMap<String, Condition>();
			conditions.put("EntityId-PublishFlag",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-Y")));

		    lastEvaluatedKey = null;
		    do {
		    	// Query PostsByEntity table
		    	request = new QueryRequest()
	                .withTableName("PostsByEntity")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey);
		        result = dynamoDBClient.query(request);

		        // Loop through each post
		        for (Map<String, AttributeValue> item : result.getItems()) {

		        	// Gather input variables
		        	String postId = item.get("PostId").getS();
		        	String zoneId = getPostZoneId(postId);
		        	String pageId = getPostPageId(postId);
		        	String publishFlag = getPostPublishFlag(postId);
		    		long updateDate = System.currentTimeMillis();
		    		// Call delete post async procedure
		        	deletePostAsync(userId, zoneId, pageId, postId, publishFlag);
		        	// Do deletes that are not covered above
					setPostDeleted(postId, updateDate);
					savePostsByEntity(userId, "D", updateDate, postId);
					updatePostSummary(postId,"");
					// Delete from S3
					s3.delete(config.getProperty("bucketNamePosts"), postId);
		        }

		        lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		    /*** Loop through PostsByEntity table for PublishFlag=N ***/
			stepName = "Loop PostsByEntity for PublishFlag=N";
	    	// Conditions for PostsByEntity table
			conditions = new HashMap<String, Condition>();
			conditions.put("EntityId-PublishFlag",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-N")));

		    lastEvaluatedKey = null;
		    do {
		    	// Query PostsByEntity table
		    	request = new QueryRequest()
	                .withTableName("PostsByEntity")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey);
		        result = dynamoDBClient.query(request);

		        // Loop through each post
		        for (Map<String, AttributeValue> item : result.getItems()) {

		        	// Gather input variables
		        	String postId = item.get("PostId").getS();
		        	String zoneId = getPostZoneId(postId);
		        	String pageId = getPostPageId(postId);
		        	String publishFlag = getPostPublishFlag(postId);
		    		long updateDate = System.currentTimeMillis();
		    		// Call delete post async procedure
		        	deletePostAsync(userId, zoneId, pageId, postId, publishFlag);
		        	// Do deletes that are not covered above
					setPostDeleted(postId, updateDate);
					savePostsByEntity(userId, "D", updateDate, postId);
					updatePostSummary(postId,"");
					// Delete from S3
					s3.delete(config.getProperty("bucketNamePosts"), postId);
		        }

		        lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		    /*** Loop through PostsByEntity table for PublishFlag=D ***/
			stepName = "Loop PostsByEntity for PublishFlag=D";
	    	// Conditions for PostsByEntity table
			conditions = new HashMap<String, Condition>();
			conditions.put("EntityId-PublishFlag",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId+"-D")));

		    lastEvaluatedKey = null;
		    do {
		    	// Query PostsByEntity table
		    	request = new QueryRequest()
	                .withTableName("PostsByEntity")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey);
		        result = dynamoDBClient.query(request);

		        // Loop through each post
		        for (Map<String, AttributeValue> item : result.getItems()) {

		        	// Gather input variables
		        	String postId = item.get("PostId").getS();
		        	// Do deletes that are not covered during Async delete
					updatePostSummary(postId,"");
					// Delete from S3
					s3.delete(config.getProperty("bucketNamePosts"), postId);
		        }

		        lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		    /*** Delete from CommentFollowers table ***/
		    // The PostId's that appear in this section are the ones that the user has been following.
		    // These posts were NOT written by the deleted user.
			stepName = "Delete from CommentFollowers";
			// Conditions for CommentFollowers table
			conditions = new HashMap<String, Condition>();
			conditions.put("Email",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(username)));

			// Define variables
			key = new HashMap<String, AttributeValue>();
        	DeleteItemRequest deleteItemRequestCF = new DeleteItemRequest()
	    		.withTableName("CommentFollowers")
	    		.withKey(key);

			lastEvaluatedKey = null;
        	do {
			    // Query CommentFollowers table
			    request = new QueryRequest()
	                .withTableName("CommentFollowers")
	                .withIndexName("CFEmailIdx")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("PostId","FollowerId");
		        result = dynamoDBClient.query(request);

		        // Loop through each PostId and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {

	            	// Put PostId into the key
	            	key.put("PostId", item.get("PostId"));
	            	key.put("FollowerId", item.get("FollowerId"));
	            	dynamoDBClient.deleteItem(deleteItemRequestCF);

	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

        	/*** Delete MyZones and RList data from Zones and RList tables ***/
			stepName = "Conditions for Zones";
			// Conditions for Zones table
			conditions = new HashMap<String, Condition>();
			conditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId)));

        	// Prepare key for delete from Zones table
        	key = new HashMap<String, AttributeValue>();
        	key.put("UserId", new AttributeValue().withS(userId));
        	deleteItemRequest = new DeleteItemRequest()
	            .withTableName("Zones")
	   			.withKey(key);

		    lastEvaluatedKey = null;
		    do {
			    // Query Zones table
			    request = new QueryRequest()
	                .withTableName("Zones")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("ZoneId");
		        result = dynamoDBClient.query(request);

	        	// Loop through each Zone and delete
	        	String zoneId;
	            for (Map<String, AttributeValue> item : result.getItems()) {

	            	// Read Zone
	            	zoneId = item.get("ZoneId").getS();

	            	// Delete from RList table
	            	deleteRL(userId, zoneId);

	            	// Delete from Zones table
	            	key.put("ZoneId", item.get("ZoneId"));
		        	dynamoDBClient.deleteItem(deleteItemRequest);

	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

        	/*** Delete from AdminZones table ***/
			stepName = "Conditions for AdminZones";
			conditions = new HashMap<String, Condition>();
			conditions.put("UserId",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(userId)));

        	// Prepare key for delete from AdminZones table
        	key = new HashMap<String, AttributeValue>();
        	key.put("UserId", new AttributeValue().withS(userId));
        	deleteItemRequest = new DeleteItemRequest()
	            .withTableName("AdminZones")
	   			.withKey(key);

		    lastEvaluatedKey = null;
		    do {
			    // Query AdminZones table
			    request = new QueryRequest()
	                .withTableName("AdminZones")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("ZoneId");
		        result = dynamoDBClient.query(request);

	        	// Loop through each Zone and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {

	        		// Remove from Admins of the Zones table
	                HashMap<String, AttributeValue> keyUpdateZ = new HashMap<String, AttributeValue>();
	                keyUpdateZ.put("UserId", new AttributeValue().withS("M"));
	                keyUpdateZ.put("ZoneId", item.get("ZoneId"));

	                Map<String, AttributeValueUpdate> updateItemsZ = new HashMap<String, AttributeValueUpdate>();
	                updateItemsZ.put("Admins",
	               		new AttributeValueUpdate()
	               			.withValue(new AttributeValue().withSS(userId))
	               			.withAction(AttributeAction.DELETE)
	               		);

	                UpdateItemRequest requestZ = new UpdateItemRequest()
	    	        	.withTableName("Zones")
	    	        	.withKey(keyUpdateZ)
	    	        	.withAttributeUpdates(updateItemsZ);
	                dynamoDBClient.updateItem(requestZ);

	            	// Delete from AdminZones table
	            	key.put("ZoneId", item.get("ZoneId"));
		        	dynamoDBClient.deleteItem(deleteItemRequest);

	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

        	/*** Delete from Attributes table for the lastVisitedZoneId and commentTrackerTime ***/
        	HashMap<String, AttributeValue> keyA = new HashMap<String, AttributeValue>();
        	DeleteItemRequest deleteItemRequestA = new DeleteItemRequest()
	            .withTableName("Attributes")
	   			.withKey(keyA);
        	keyA.put("AttrName", new AttributeValue().withS(userId+"-lastVisitedZoneId"));
        	dynamoDBClient.deleteItem(deleteItemRequestA);
        	keyA.put("AttrName", new AttributeValue().withS(userId+"-commentTrackerTime"));
        	dynamoDBClient.deleteItem(deleteItemRequestA);
        	keyA.put("AttrName", new AttributeValue().withS(userId+"-lastNewPostDate"));
        	dynamoDBClient.deleteItem(deleteItemRequestA);

        	/*** Send dropAccount Email ***/
        	if ( StringUtils.isNotBlank(getFirstName(username)) ) {
        		ses.sendDropAccountEmail(getFirstName(username), username);
        	} else {
    	        logger.log(Level.SEVERE,"Unable to send dropAccount email - username: " + username);
        	}

        	/*** Delete from Users table ***/
			stepName = "Delete from Users";
        	deleteUser(username);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Step Name: " + stepName + " Username: " + username);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**************************************************************************
     **********************  NOTIFICATION MODULE  *****************************
     **************************************************************************/

	/**
     * Queries notification types and flags for given email address.
     * @param email username
     * @return List of Map objects with Notification details
     */
	public List<Map<String,AttributeValue>> getNotificationFlags(String email) {

		QueryResult result = new QueryResult();
		try {

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("Email", new Condition()
    			.withComparisonOperator(ComparisonOperator.EQ.toString())
    			.withAttributeValueList(new AttributeValue().withS(email)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Notifications")
       			.withKeyConditions(keyConditions);
       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Queries notification flag for given email address and notification type.
     * @param email username
     * @param type notification type
     * @return Notification flag; Returns N if the user is not found
     */
	public String getNotificationFlag(String email, String type) {

		String flag = "N";
		try {

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("Email", new Condition()
    			.withComparisonOperator(ComparisonOperator.EQ.toString())
    			.withAttributeValueList(new AttributeValue().withS(email)));
        	keyConditions.put("Type", new Condition()
	    		.withComparisonOperator(ComparisonOperator.EQ.toString())
	    		.withAttributeValueList(new AttributeValue().withS(type)));

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("Notifications")
       			.withKeyConditions(keyConditions);

        	try {
        		flag = dynamoDBClient.query(queryRequest).getItems().get(0).get("Flag").getS();
        	} catch (Exception e) {
        		// keep the default
        	}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return flag;
	}

	/**
     * Saves notification flags into the database.
     * @param email username
     */
	public void saveNotificationFlags (String email, String followWhenPublished, String followWhenCommented, String notifyWhenThanked, String remindDraftPost, String weeklyNewsLetter) {

		try {

            HashMap<String, AttributeValue> keyUpdate = new HashMap<String, AttributeValue>();
            Map<String, AttributeValueUpdate> updateItems = new HashMap<String, AttributeValueUpdate>();
            UpdateItemRequest request;
            keyUpdate.put("Email", new AttributeValue().withS(email));

            if ( !StringUtils.equals(followWhenPublished, "S") ) {
            	keyUpdate.put("Type", new AttributeValue().withS("followWhenPublished"));
	            updateItems.put("Flag",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(followWhenPublished))
	           			.withAction(AttributeAction.PUT)
	           		);
	            request = new UpdateItemRequest()
	            	.withTableName("Notifications")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);
            }

            if ( !StringUtils.equals(followWhenCommented, "S") ) {
            	keyUpdate.put("Type", new AttributeValue().withS("followWhenCommented"));
	            updateItems.put("Flag",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(followWhenCommented))
	           			.withAction(AttributeAction.PUT)
	           		);
	            request = new UpdateItemRequest()
	            	.withTableName("Notifications")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);
            }

            if ( !StringUtils.equals(notifyWhenThanked, "S") ) {
            	keyUpdate.put("Type", new AttributeValue().withS("notifyWhenThanked"));
	            updateItems.put("Flag",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(notifyWhenThanked))
	           			.withAction(AttributeAction.PUT)
	           		);
	            request = new UpdateItemRequest()
	            	.withTableName("Notifications")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);
            }

            if ( !StringUtils.equals(remindDraftPost, "S") ) {
            	keyUpdate.put("Type", new AttributeValue().withS("remindDraftPost"));
	            updateItems.put("Flag",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(remindDraftPost))
	           			.withAction(AttributeAction.PUT)
	           		);
	            request = new UpdateItemRequest()
	            	.withTableName("Notifications")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);
            }

            if ( !StringUtils.equals(weeklyNewsLetter, "S") ) {
            	keyUpdate.put("Type", new AttributeValue().withS("weeklyNewsLetter"));
	            updateItems.put("Flag",
	           		new AttributeValueUpdate()
	           			.withValue(new AttributeValue().withS(weeklyNewsLetter))
	           			.withAction(AttributeAction.PUT)
	           		);
	            request = new UpdateItemRequest()
	            	.withTableName("Notifications")
	            	.withKey(keyUpdate)
	            	.withAttributeUpdates(updateItems);
	            dynamoDBClient.updateItem(request);
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

	/**
     * Creates notification flags for a new user.
     * @param email username
     */
	public void createNotificationFlags(String email) {

		try {

			// define the notification types
    		List<String> types = new ArrayList<String>();
    		types.add("followWhenPublished");
    		types.add("followWhenCommented");
    		types.add("notifyWhenThanked");
    		types.add("remindDraftPost");
    		types.add("weeklyNewsLetter");

    		// put one record for each notification type
			Map<String, AttributeValue> itemN = new HashMap<String, AttributeValue>();
			itemN.put("Email", new AttributeValue().withS(email));
			itemN.put("Flag", new AttributeValue().withS("Y"));
			for ( int i = 0; i < types.size(); i++ ) {
				itemN.put("Type", new AttributeValue().withS(types.get(i)));
    			PutItemRequest putItemRequest = new PutItemRequest()
    				.withTableName("Notifications")
    				.withItem(itemN);
    			dynamoDBClient.putItem(putItemRequest);
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
	}

    /**
     * Deletes all records from the Notifications table for a given email address
     * @param email email address
     */
    public void deleteNotificationFlags(String email) {

		try {
			// Conditions for Notifications table
			HashMap<String, Condition> conditions = new HashMap<String, Condition>();
			conditions.put("Email",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(email)));

		    Map<String, AttributeValue> lastEvaluatedKey = null;
		    do {
			    // Query Notifications table
			    QueryRequest request = new QueryRequest()
	                .withTableName("Notifications")
	                .withKeyConditions(conditions)
	                .withExclusiveStartKey(lastEvaluatedKey)
	                .withAttributesToGet("Type");
		        QueryResult result = dynamoDBClient.query(request);

            	// Prepare key for delete
            	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            	key.put("Email", new AttributeValue().withS(email));
	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
		            .withTableName("Notifications")
		   			.withKey(key);

		        // Loop through each type and delete
	            for (Map<String, AttributeValue> item : result.getItems()) {
	            	key.put("Type", new AttributeValue().withS(item.get("Type").getS()));
		        	dynamoDBClient.deleteItem(deleteItemRequest);
	            }

	        	lastEvaluatedKey = result.getLastEvaluatedKey();
		    } while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    /**
     * Updates Email Address in the Notifications table.
     * @param email email address of the user
     * @param newEmail New Email address of the user
     */
    public void updateNotificationsEmail (String email, String newEmail) {

    	try {
			// Conditions for Notifications table
           	HashMap<String, Condition> conditionsN = new HashMap<String, Condition>();
			conditionsN.put("Email",
				new Condition()
					.withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(email)));

			Map<String, AttributeValue> lastEvaluatedKeyN = null;
		    do
		    {
			    // Query Notifications table
			    QueryRequest requestN = new QueryRequest()
			    	.withTableName("Notifications")
			    	.withKeyConditions(conditionsN)
	                .withExclusiveStartKey(lastEvaluatedKeyN)
	                .withAttributesToGet("Type","Flag");
			    QueryResult resultN = dynamoDBClient.query(requestN);

		        // Loop through each record
	            for (Map<String, AttributeValue> itemN : resultN.getItems()) {

	            	itemN.put("Email", new AttributeValue().withS(newEmail));
	            	PutItemRequest putItemRequestN = new PutItemRequest()
        	            .withTableName("Notifications")
	        	   		.withItem(itemN);
		        	dynamoDBClient.putItem(putItemRequestN);

	            }

	            lastEvaluatedKeyN = resultN.getLastEvaluatedKey();
		    } while (lastEvaluatedKeyN != null);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Email: " + email);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
    }

	/**************************************************************************
     ***********************   TICKER MODULE   ********************************
     **************************************************************************/

	/**
     * Queries 25 latest tickers from TopCharts table.
     * @return List of Map objects with Tickers
    */
	public List<Map<String,AttributeValue>> getTickers() {

		QueryResult result = new QueryResult();
		try {
        	Condition hashKeyCondition = new Condition()
        		.withComparisonOperator(ComparisonOperator.EQ.toString())
        		.withAttributeValueList(new AttributeValue().withS("T"));

        	Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        	keyConditions.put("Name", hashKeyCondition);

        	QueryRequest queryRequest = new QueryRequest()
       			.withTableName("TopCharts")
       			.withKeyConditions(keyConditions)
       			.withIndexName("HIIdx")
       			.withScanIndexForward(false)
       			.withLimit(50)
       			.withAttributesToGet("HI", "Id");

       		result = dynamoDBClient.query(queryRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }
   		return result.getItems();
	}

	/**
     * Puts a record into Top Charts table for Ticker when a new user has signed up and confirmed their email.
     * @param userId
     */
	public void putSignupTicker (String userId) {

		try {

			// define variables
			long hi = System.currentTimeMillis();
			String id =
            	"<a href='https://www.heatbud.com/" + userId + "'>" + getEntityName(userId) + "</a>" +
               	" signed up.";

			// insert into top charts
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("T"));
            item.put("Id", new AttributeValue().withS(id));
            item.put("HI", new AttributeValue().withN(hi+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId=" + userId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Puts a record into Top Charts table for Ticker when a zone has been created.
     * @param userId
     * @param zoneId
     */
	public void putCreateZoneTicker (String userId, String zoneId) {

		try {

			// define variables
			long hi = System.currentTimeMillis();
			String id =
            	"<a href='https://www.heatbud.com/" + userId + "'>" + getEntityName(userId) + "</a>" +
               	" created the zone " +
               	"<a href='https://www.heatbud.com/zone/" + zoneId + "'>" + getZoneName(zoneId) + "</a>.";

			// insert into top charts
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("T"));
            item.put("Id", new AttributeValue().withS(id));
            item.put("HI", new AttributeValue().withN(hi+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId=" + userId + " zoneId=" + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Puts a record into Top Charts table for Ticker when a page has been created.
     * @param pageId
     */
	public void putCreatePageTicker (String userId, String bloggerName, String pageId, String pageName) {

		try {

			// define variables
			long hi = System.currentTimeMillis();
			String id =
            	"<a href='https://www.heatbud.com/" + userId + "'>" + bloggerName + "</a>" +
               	" created the page " +
               	"<a href='https://www.heatbud.com/" + pageId + "'>" + pageName + "</a>.";

			// insert into top charts
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("T"));
            item.put("Id", new AttributeValue().withS(id));
            item.put("HI", new AttributeValue().withN(hi+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId=" + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Puts a record into Top Charts table for Ticker when a post has been published.
     * @param bloggerId
     * @param postId
     * @param zoneId
     * @param pageId
     */
	public void putPublishTicker (String bloggerId, String postId, String zoneId, String pageId) {

		try {

			// define variables
			long hi = System.currentTimeMillis();
			String id =
            	"<a href='https://www.heatbud.com/" + bloggerId + "'>" + getEntityName(bloggerId) + "</a>" +
              	" published " +
               	"<a href='https://www.heatbud.com/post/" + postId + "'>" + getPostTitle(postId) + "</a>" +
              	" in the zone " +
               	"<a href='https://www.heatbud.com/zone/" + zoneId + "'>" + getZoneName(zoneId) + "</a>";
	  			if (StringUtils.isNotBlank(pageId)) {
		          	id = id + " for page " +
		           	"<a href='https://www.heatbud.com/" + pageId + "'>" + getEntityName(pageId) + "</a>.";
	  			} else {
	  				id = id + ".";
	  			}

			// insert into top charts
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("T"));
            item.put("Id", new AttributeValue().withS(id));
            item.put("HI", new AttributeValue().withN(hi+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId=" + bloggerId + " postId=" + postId + " zoneId=" + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Puts a record into Top Charts table for Ticker when a post has been voted up or down.
     * @param voterId
     * @param postId
     * @param newVote
     */
	public void putVoteTicker (String voterId, String postId, long newVote) {

		try {

			// define variables
			long hi = System.currentTimeMillis();
			String vote = null;
			if ( newVote == 1 ) {
				vote = "UP";
			} else {
				vote = "DOWN";
			}
			String id =
				"<a href='https://www.heatbud.com/" + voterId + "'>" + getEntityName(voterId) + "</a>" +
	            " voted " + vote + " on " +
	            "<a href='https://www.heatbud.com/post/" + postId + "'>" + getPostTitle(postId) + "</a>.";

			// insert into top charts
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("T"));
            item.put("Id", new AttributeValue().withS(id));
            item.put("HI", new AttributeValue().withN(hi+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"voterId=" + voterId + " postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Puts a record into Top Charts table for Ticker when a post has been commented.
     * @param commenterId
     * @param postId
     * @param newVote
     */
	public void putCommentTicker (String commenterId, String postId) {

		try {

			// define variables
			long hi = System.currentTimeMillis();
			String id =
            	"<a href='https://www.heatbud.com/" + commenterId + "'>" + getEntityName(commenterId) + "</a>" +
              	" commented on " +
               	"<a href='https://www.heatbud.com/post/" + postId + "'>" + getPostTitle(postId) + "</a>.";

			// insert into top charts
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Name", new AttributeValue().withS("T"));
            item.put("Id", new AttributeValue().withS(id));
            item.put("HI", new AttributeValue().withN(hi+""));
            PutItemRequest putItemRequest = new PutItemRequest()
            	.withTableName("TopCharts")
            	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"commenterId=" + commenterId + " postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Delete TopCharts where Name=T
     * @param id
     **/
	public void deleteTicker(String id) {

    	try {

        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("Name", new AttributeValue().withS("T"));
        	key.put("Id", new AttributeValue().withS(id));
        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("TopCharts")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"id: " + id);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

	/**************************************************************************
     *********************   NEWSLETTER MODULE   ******************************
     **************************************************************************/

	/**
     * Generates newsletter for a given Username.
     **/
	public String generateNewsLetter (String username) {

		// String topChartsPeriod = "";
		String featuredPostId = "";
		String stepName = null;
		String retMessage = "FAILURE"; // default
		int insertCount = 0;
    	try {

			/*** Query User Information ***/
    		User user = getUser(username);

			/*** Query Featured Post Information ***/
    		Post post = getPost(featuredPostId);

			/*** Query Zones table ***/
   		    stepName = "Query Zones";
           	Condition hashKeyConditionZones = new Condition()
          		.withComparisonOperator(ComparisonOperator.EQ.toString())
           		.withAttributeValueList(new AttributeValue().withS(user.getUserId()));

           	Map<String, Condition> keyConditionsZones = new HashMap<String, Condition>();
           	keyConditionsZones.put("UserId", hashKeyConditionZones);

           	Map<String, AttributeValue> lastEvaluatedKeyZones = null;
           	do
           	{
	           	QueryRequest requestZones = new QueryRequest()
	       			.withTableName("Zones")
	       			.withKeyConditions(keyConditionsZones)
	       			.withExclusiveStartKey(lastEvaluatedKeyZones)
	       			.withAttributesToGet("ZoneId");

	           	QueryResult resultZones = dynamoDBClient.query(requestZones);

	           	for (Map<String, AttributeValue> itemZones : resultZones.getItems()) {

	           		String zoneId = itemZones.get("ZoneId").getS();

	           		/*** Query Ranking table for the input zone in the reverse order of PostHI ***/
	       		    stepName = "Query Ranking";
	           		Map<String, Condition> conditionsRanking = new HashMap<String, Condition>();
	           		conditionsRanking.put("ZoneId",
	           			new Condition()
							.withComparisonOperator(ComparisonOperator.EQ)
							.withAttributeValueList(new AttributeValue().withS(zoneId)));

	           		QueryRequest requestRanking = new QueryRequest()
	       				.withTableName("Ranking")
	       				.withIndexName("PostHIIdx")
	       				.withKeyConditions(conditionsRanking)
	       				.withScanIndexForward(false)
	       				.withAttributesToGet("PostId");

	           		List<Map<String,AttributeValue>> itemsRanking = dynamoDBClient.query(requestRanking).getItems();

	           		/*** Loop through PostIds and insert them into RL ***/
	           		for (int currentRO = 1; currentRO <= itemsRanking.size(); currentRO++) {

						/*** Read PostId from Ranking ***/
						String postId = itemsRanking.get(currentRO-1).get("PostId").getS();

						/*** Insert postId into RL ***/
		       		    stepName = "Insert RList";
			    		Map<String, AttributeValue> itemRL = new HashMap<String, AttributeValue>();
			            itemRL.put("UserId-ZoneId", new AttributeValue().withS("guest-"+zoneId));
			            itemRL.put("RO", new AttributeValue().withN( currentRO+"" ));
			            itemRL.put("PostId", new AttributeValue().withS(postId));

			            PutItemRequest putItemRequest = new PutItemRequest()
			    	    	.withTableName("RList")
			    	    	.withItem(itemRL);

			            dynamoDBClient.putItem(putItemRequest);
			            insertCount++;

		                // sleep for a second to conserve DynamoDB write throughput on RList table
		   				if ( insertCount % 10 == 0 ) {
		   					Thread.sleep( 1000 );
		   				}

	           		}

	           		/*** Delete extra posts from the RL ***/
	       		    stepName = "Delete RList";
                	// Prepare key for delete
                	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
                	key.put("UserId-ZoneId", new AttributeValue().withS("guest-"+zoneId));
    	        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
    		            .withTableName("RList")
    		   			.withKey(key)
    		   			.withReturnValues("ALL_OLD");
    	        	// first RO to be deleted = (number of items in the ranking table) + 1
           			int deleteRO = itemsRanking.size()+1;
	           		boolean exitLoop = false;
	           		do {
    	            	key.put("RO", new AttributeValue().withN(deleteRO+""));
    		        	DeleteItemResult deleteItemResult = dynamoDBClient.deleteItem(deleteItemRequest);
    		        	try {
	    		        	Map<String,AttributeValue> deletedItem = deleteItemResult.getAttributes();
	    		        	if ( deletedItem.isEmpty() )
	    		        		exitLoop = true;
	    		        	else
	    		        		deleteRO++;
    		        	} catch (Exception e) {
    		        		exitLoop = true;
    		        	}
	           		} while (!exitLoop);

	           	}
           		lastEvaluatedKeyZones = resultZones.getLastEvaluatedKey();
           	} while (lastEvaluatedKeyZones != null);

           	/*** Validations ***/

           	/*** Email ***/
           	ses.sendNewsLetterEmail(username, user.getFirstName(), post.getPostId(), post.getPostHeadshot(), post.getBloggerName(), post.getPostSummary());

    	} catch (Exception e) {
	        logger.log(Level.SEVERE,"Step Name=" + stepName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
			retMessage = "FAILURE";
	    }

    	return retMessage;
	}

	/**************************************************************************
     ***********************   SEARCH MODULE   ********************************
     **************************************************************************/

	/**
     * Queries Search table.
     * @return List of Map objects with Search Results
    */
	public List<String> getSearchResults (String confineBloggerId, String type, String terms) {

		Map<String, AttributeValue> lastEvaluatedKey = null;
   		Map<String, Long> unsortedIds = new HashMap<String, Long>();
		List<String> sortedIds = new ArrayList<String>();

		try {

    		// convert to lower case
			terms = StringUtils.lowerCase(terms);
			// replace all non-alphanumeric characters (including spaces) with dashes
			terms = terms.replaceAll("[^a-z0-9]","-");
			// trim two or more subsequent dashes into one
			terms = terms.trim().replaceAll("-+", "-");
			// remove the first character if it's a dash
			terms = terms.startsWith("-") ? terms.substring(1) : terms;
			// remove the last character if it's a dash
			terms = terms.endsWith("-") ? terms.substring(0,terms.length()-1) : terms;

			// pick three longest words from the search terms
			List<String> termsLongest = new ArrayList<String>();
			try {
				List<String> termsList = new ArrayList<String>(Arrays.asList(terms.split("-")));
		        System.out.println("SEARCH: termsList=" + termsList);
				String maxS1 = common.getLongestString(termsList);
				if ( StringUtils.isNotBlank(maxS1)) {
					termsLongest.add(maxS1);
					termsList.remove(maxS1);
					String maxS2 = common.getLongestString(termsList);
					if ( StringUtils.isNotBlank(maxS2)) {
						termsLongest.add(maxS2);
						termsList.remove(maxS2);
						String maxS3 = common.getLongestString(termsList);
						if ( StringUtils.isNotBlank(maxS3)) {
							termsLongest.add(maxS3);
							termsList.remove(maxS3);
						}
					}
				}
			} catch (Exception e) {}
	        System.out.println("SEARCH: termsList=" + termsLongest);

			// search those three longest terms
			for ( String term : termsLongest ) {

		        Map<String, AttributeValue> expressionAttributeValues =  new HashMap<String, AttributeValue>();
		        String filterExpression = "";
		        expressionAttributeValues.put(":val1", new AttributeValue().withS(type));
		        filterExpression = filterExpression + "Typ = :val1";
		        expressionAttributeValues.put(":val2", new AttributeValue().withS(term));
		        filterExpression = filterExpression + " AND contains(Txt,:val2)";
		        if ( !StringUtils.equals(confineBloggerId, "Any") ) {
			        expressionAttributeValues.put(":val3", new AttributeValue().withS(confineBloggerId));
			        filterExpression = filterExpression + " AND begins_with(Txt,:val3)";
		        }

				do {
			        ScanRequest scanRequest = new ScanRequest()
			       		.withTableName("Search")
			       		.withExclusiveStartKey(lastEvaluatedKey)
			        	.withFilterExpression(filterExpression)
			        	.withExpressionAttributeValues(expressionAttributeValues)
			        	.withProjectionExpression("Id, Score");

			       	ScanResult result = dynamoDBClient.scan(scanRequest);
			       	for ( Map<String, AttributeValue> item : result.getItems() ) {
			       		Long currentScore = unsortedIds.get(item.get("Id").getS());
			       		if ( currentScore == null ) {
			       			unsortedIds.put(item.get("Id").getS(), Long.parseLong(item.get("Score").getN())*term.length());
			       		} else {
			       			unsortedIds.put(item.get("Id").getS(), currentScore + Long.parseLong(item.get("Score").getN())*term.length());
			       		}
			       	}

					lastEvaluatedKey = result.getLastEvaluatedKey();
				} while (lastEvaluatedKey != null);

			}

			for ( Map.Entry<String, Long> sortedId : sortByValuesDesc(unsortedIds) ) {
				sortedIds.add(sortedId.getKey());
			}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"type=" + type + " terms=" + terms);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        config.setProperty("lastSearchErrorPeriod", "" + (long) System.currentTimeMillis()/21600000);
        }
   		return sortedIds;
	}

	/**
     * Creates Search records for a Post.
     * @param userId
     */
	public void putPostSearch (String postId) {

		try {

    		// Get the current period
            long currentPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours
            
            // Get Post details
            Post post = getPost(postId);

			// insert bloggerId + postId + bloggerName with 20 as the multiplier
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item.put("Typ", new AttributeValue().withS("PO"));
			item.put("Txt", new AttributeValue().withS(post.getBloggerId() + " " + postId + " " + post.getBloggerName().toLowerCase()));
			item.put("Id", new AttributeValue().withS(postId));
			item.put("Score", new AttributeValue().withN(currentPeriod * 20 + ""));
			PutItemRequest putItemRequest = new PutItemRequest()
				.withTableName("Search")
				.withItem(item);
			dynamoDBClient.putItem(putItemRequest);

            // insert zoneName + postTitle with 10 as the multiplier
			item = new HashMap<String, AttributeValue>();
            item.put("Typ", new AttributeValue().withS("PO"));
            item.put("Txt", new AttributeValue().withS(post.getZoneName().toLowerCase() + " " + post.getPostTitle().toLowerCase()));
            item.put("Id", new AttributeValue().withS(postId));
            item.put("Score", new AttributeValue().withN(currentPeriod*10+""));
            putItemRequest = new PutItemRequest()
	        	.withTableName("Search")
	        	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

            // insert postSummary with 5 as the multiplier
            if ( StringUtils.isNotBlank(post.getPostSummary()) ) {
				item = new HashMap<String, AttributeValue>();
	            item.put("Typ", new AttributeValue().withS("PO"));
	            item.put("Txt", new AttributeValue().withS(post.getPostSummary().toLowerCase()));
	            item.put("Id", new AttributeValue().withS(postId));
	            item.put("Score", new AttributeValue().withN(currentPeriod*5+""));
	            putItemRequest = new PutItemRequest()
		        	.withTableName("Search")
		        	.withItem(item);
	            dynamoDBClient.putItem(putItemRequest);
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"postId=" + postId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Creates Search records for a Zone.
     * @param zoneId
     */
	public void putZoneSearch (String zoneId) {

		try {

            // insert zoneId + zoneName with 10 as the multiplier
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Typ", new AttributeValue().withS("Z"));
            item.put("Txt", new AttributeValue().withS(zoneId + " " + getZoneName(zoneId).toLowerCase()));
            item.put("Id", new AttributeValue().withS(zoneId));
            item.put("Score", new AttributeValue().withN("10"));
            PutItemRequest putItemRequest = new PutItemRequest()
	        	.withTableName("Search")
	        	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

            // insert zoneDesc with 5 as the multiplier
            String zoneDesc = null;
            try {
            	zoneDesc = StringUtils.trimToEmpty(getZoneDesc(zoneId).toLowerCase());
            	if ( zoneDesc.length()>1000 ) {
            		zoneDesc = zoneDesc.substring(0,999);
            	}
    			item = new HashMap<String, AttributeValue>();
                item.put("Typ", new AttributeValue().withS("Z"));
                item.put("Txt", new AttributeValue().withS(zoneDesc));
                item.put("Id", new AttributeValue().withS(zoneId));
                item.put("Score", new AttributeValue().withN("5"));
                putItemRequest = new PutItemRequest()
    	        	.withTableName("Search")
    	        	.withItem(item);
                dynamoDBClient.putItem(putItemRequest);
            } catch (Exception e) {
            	// do nothing
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"zoneId=" + zoneId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Creates Search records for a Page.
     * @param pageId
     */
	public void putPageSearch (String pageId, String pageName) {

		// Get the current period
        long currentPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

		try {

            // insert pageId + pageName with 10 as the multiplier
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Typ", new AttributeValue().withS("PA"));
            item.put("Txt", new AttributeValue().withS(pageId + " " + pageName.toLowerCase()));
            item.put("Id", new AttributeValue().withS(pageId));
            item.put("Score", new AttributeValue().withN(currentPeriod*10+""));
            PutItemRequest putItemRequest = new PutItemRequest()
	        	.withTableName("Search")
	        	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

            // insert about with 5 as the multiplier
            String about = null;
            try {
            	about = StringUtils.trimToEmpty(getEntityAbout(pageId).toLowerCase());
            	if ( about.length()>1000 ) {
            		about = about.substring(0,999);
            	}
    			item = new HashMap<String, AttributeValue>();
                item.put("Typ", new AttributeValue().withS("PA"));
                item.put("Txt", new AttributeValue().withS(about));
                item.put("Id", new AttributeValue().withS(pageId));
                item.put("Score", new AttributeValue().withN(currentPeriod*5+""));
                putItemRequest = new PutItemRequest()
    	        	.withTableName("Search")
    	        	.withItem(item);
                dynamoDBClient.putItem(putItemRequest);

            } catch (Exception e) {
            	// do nothing
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"pageId=" + pageId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**
     * Creates Search records for a Blogger.
     * @param bloggerId
     */
	public void putBloggerSearch (String bloggerId, String bloggerName) {

		try {

    		// Get the current period
            long currentPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

            // insert bloggerId + bloggerName with 10 as the multiplier
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Typ", new AttributeValue().withS("B"));
            item.put("Txt", new AttributeValue().withS(bloggerId + " " + bloggerName.toLowerCase()));
            item.put("Id", new AttributeValue().withS(bloggerId));
            item.put("Score", new AttributeValue().withN(currentPeriod*10+""));
            PutItemRequest putItemRequest = new PutItemRequest()
	        	.withTableName("Search")
	        	.withItem(item);
            dynamoDBClient.putItem(putItemRequest);

            // insert about with 5 as the multiplier
            String about = null;
            try {
            	about = StringUtils.trimToEmpty(getEntityAbout(bloggerId).toLowerCase());
            	if ( about.length()>1000 ) {
            		about = about.substring(0,999);
            	}
    			item = new HashMap<String, AttributeValue>();
                item.put("Typ", new AttributeValue().withS("B"));
                item.put("Txt", new AttributeValue().withS(about));
                item.put("Id", new AttributeValue().withS(bloggerId));
                item.put("Score", new AttributeValue().withN(currentPeriod*5+""));
                putItemRequest = new PutItemRequest()
    	        	.withTableName("Search")
    	        	.withItem(item);
                dynamoDBClient.putItem(putItemRequest);

            } catch (Exception e) {
            	// do nothing
            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"bloggerId=" + bloggerId);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	/**************************************************************************
     ***********************  COMMON FUNCTIONS  *******************************
     **************************************************************************/

	/**
     * Sorts Entries in a HashMap in the Descending order of Values.
     **/
	public <K,V extends Comparable<? super V>> List<Entry<K, V>> sortByValuesDesc (Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

		Collections.sort(sortedEntries,  new Comparator<Entry<K,V>>() {
			@Override
			public int compare(Entry<K,V> e1, Entry<K,V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

    /**
     * Gets value for the given attribute from the Attributes table.
     * @param userId userId
     * @param attrName Name of the attribute
     * @return Value of the attribute
     */
	public String getAttribute(String userId, String attrName) {
		String attrValue = null; // default
		try {

			HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("AttrName", new AttributeValue().withS(userId+"-"+attrName));

			GetItemRequest getItemRequest = new GetItemRequest()
			    .withTableName("Attributes")
			    .withKey(key);

			GetItemResult result = dynamoDBClient.getItem(getItemRequest);
			attrValue = result.getItem().get("AttrValue").getS();

		} catch (NullPointerException npe) {
	        return attrValue; // value doesn't exist, so return default value (null)

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"userId=" + userId + " attrName=" + attrName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
        return attrValue;
    }

    /**
     * Sets value for the given attribute in the Attributes table.
     * @param userId userId
     * @param attrName Name of the attribute
     * @param attrValue Value of the attribute
     */
	public void setAttribute(String userId, String attrName, String attrValue) {
		try {

			HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("AttrName", new AttributeValue().withS(userId+"-"+attrName));
        	item.put("AttrValue", new AttributeValue().withS(attrValue));

        	PutItemRequest putItemRequest = new PutItemRequest()
	            .withTableName("Attributes")
	   			.withItem(item);

        	dynamoDBClient.putItem(putItemRequest);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"UserId: " + userId + " AttrName: " + attrName + " AttrValue:" + attrValue);
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }
    }

    /**
     * Puts starting record for a cron job to ensure only one instance proceeds
     * @param jobName Name of the cron job
     * @param currentRunPeriod period for the current run
     * @param startedBy Server that is attempting to start the cron job
     * @return Success or Failure message
     */
	public String putStartingRecord (String jobName, long currentRunPeriod, String startedBy) {

		String retMessage = "SUCCESS";
		try {

			// item to insert
			HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        	item.put("AttrName", new AttributeValue().withS("STARTING-"+jobName));
        	item.put("AttrValue", new AttributeValue().withS(currentRunPeriod + ":" + startedBy));

			// condition to expect - AttrName shouldn't already exist in the database
	        Map<String, ExpectedAttributeValue> expected = new HashMap<String, ExpectedAttributeValue>();
	        expected.put("AttrName", new ExpectedAttributeValue().withExists(false));

        	PutItemRequest putItemRequest = new PutItemRequest()
	            .withTableName("Attributes")
	   			.withItem(item)
	   			.withExpected(expected);

        	// we fail if the hash key already exists
        	try {
        		dynamoDBClient.putItem(putItemRequest);
        	} catch (ConditionalCheckFailedException e) {
        		retMessage = "FAILURE";
        	}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"jobName: " + jobName + " startedBy: " + startedBy );
	        logger.log(Level.SEVERE,e.getMessage(),e);
	        retMessage = "FAILURE";
	    }

		return retMessage;

    }

    /**
     * Deletes starting record for a cron job
     * @param jobName Name of the cron job
     */
	public void deleteStartingRecord (String jobName) {

		try {
        	HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("AttrName", new AttributeValue().withS("STARTING-"+jobName));
        	DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
	            .withTableName("Attributes")
	   			.withKey(key);
        	dynamoDBClient.deleteItem(deleteItemRequest);
		} catch (Exception e) {
	        logger.log(Level.SEVERE,"jobName: " + jobName);
	        logger.log(Level.SEVERE,e.getMessage(),e);
        }

	}

	public boolean waitForTableActive(String tableName) {

		long startTime = System.currentTimeMillis();
		long endTime = startTime + (20 * 60 * 1000); // 20 minutes from startTime
		boolean tableCreated = false;
		while ( System.currentTimeMillis() < endTime && !tableCreated ) {
			try {
				DescribeTableRequest request = new DescribeTableRequest()
					.withTableName(tableName);
				TableDescription tableDescription = dynamoDBClient
					.describeTable(request).getTable();
				if ( tableDescription.getTableStatus().equals(TableStatus.ACTIVE.toString()) ) {
					tableCreated = true;
				} else {
					try { Thread.sleep(1000 * 10); } catch (Exception e) {}
				}
			} catch (Exception e1) {
				try { Thread.sleep(1000 * 10); } catch (Exception e) {} // in case of exception, assume table hasn't been created yet
			}
		}
		return tableCreated;
	}

	public boolean waitForTargetCapacity(String tableName, long targetReadCapacity, long targetWriteCapacity) {

		long startTime = System.currentTimeMillis();
		long endTime = startTime + (20 * 60 * 1000); // 20 minutes from startTime
		boolean targetReached = false;
		while ( System.currentTimeMillis() < endTime && !targetReached ) {
			try {
				DescribeTableRequest request = new DescribeTableRequest()
					.withTableName(tableName);
				TableDescription tableDescription = dynamoDBClient
					.describeTable(request).getTable();
	    		long currentReadCapacity = tableDescription.getProvisionedThroughput().getReadCapacityUnits();
	    		long currentWriteCapacity = tableDescription.getProvisionedThroughput().getWriteCapacityUnits();

	    		if ( currentReadCapacity == targetReadCapacity && currentWriteCapacity == targetWriteCapacity ) {
	    			targetReached = true;
				} else {
					try { Thread.sleep(1000 * 10); } catch (Exception e) {}
				}
			} catch (Exception e1) {
				try { Thread.sleep(1000 * 10); } catch (Exception e) {} // in case of exception, assume target has not reached yet
			}
		}
		return targetReached;
	}

	public boolean waitForTableDelete(String tableName) {

		boolean tableDeleted = false;
		try {
			long startTime = System.currentTimeMillis();
			long endTime = startTime + (20 * 60 * 1000); // 20 minutes from startTime
			while ( System.currentTimeMillis() < endTime && !tableDeleted ) {
				try {
					DescribeTableRequest request = new DescribeTableRequest()
						.withTableName(tableName);
					TableDescription tableDescription = dynamoDBClient
						.describeTable(request).getTable();
					if ( StringUtils.equals(tableDescription.getTableName(),tableName) ) {
						try { Thread.sleep(1000 * 10); } catch (Exception e) {}
					} else {
						tableDeleted = true;
					}
				} catch (Exception e1) {
					tableDeleted = true;
				}
			}
		} catch (Exception e) {
			tableDeleted = true; // in case of exception, assume table deleted
		}
		return tableDeleted;
	}

	/**************************************************************************
     ******************  INITIALIZATION FUNCTIONS  ****************************
     **************************************************************************/

    /**
     * Read from MarketPricing table and store data in common.marketPricingList array.
     * @return none
     */
	public void loadMarketPricing () {

		try {

			Map<String,AttributeValue> lastEvaluatedKey = null;
			do {
				ScanRequest scanRequest = new ScanRequest()
					.withTableName("MarketPricing")
					.withExclusiveStartKey(lastEvaluatedKey);
				ScanResult result = dynamoDBClient.scan(scanRequest);

				for (Map<String,AttributeValue> item : result.getItems()) {
					// marketpricing set
					MarketPricing mp = new MarketPricing();
					mp.setBloggerId(item.get("BloggerId").getS());
					mp.setPosition(Long.parseLong(item.get("Position").getN()));
					mp.setName(item.get("Name").getS());
					mp.setCountry(item.get("Country").getS());
					try { mp.setAbout(item.get("About").getS()); } catch (Exception e) {}
					try { mp.setTags(item.get("Tags").getS()); } catch (Exception e) {}
					mp.setPostType(item.get("PostType").getS());
					mp.setDeliveryDays(Long.parseLong(item.get("DeliveryDays").getN()));
					mp.setPrice(Long.parseLong(item.get("Price").getN()));
					common.marketPricingSet.add(mp);
					// marketpricing countries set
					common.marketPricingCountriesSet.add(item.get("Country").getS());
				}

				// sleep for 10 seconds for every read
				Thread.sleep( 10 * 1000 );
				lastEvaluatedKey = result.getLastEvaluatedKey();
			} while (lastEvaluatedKey != null);

		} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
	    }

	}

}
