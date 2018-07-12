package com.heatbud.util;

import java.net.InetAddress;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.heatbud.aws.HeatbudDynamoDBUtil;
import com.heatbud.entity.User;
import com.heatbud.util.HeatbudCommon;

public class HeatbudCronUtil {

	// Logger object
	private static final Logger logger = Logger.getLogger(HeatbudCronUtil.class.getName());
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
	// File Util
    @Autowired
	private HeatbudFileUtil file;

	/**
     * This cronjob runs once after the webserver starts.
     * Since fixedDelay expects a valid number, I'm giving it 10 years.
     */
    @Scheduled(fixedDelay=113529600000000L)
    public void runOnStartupJob () {

    	try {

	    	// Load MarketPricing data into the HashSet
	        dao.loadMarketPricing();
	        logger.log(Level.SEVERE,"Loaded " + common.marketPricingSet.size() + " records into marketPricingSet.");

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - runOnStartupJob");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}

    }

	/**
     * This cronjob reads messages from SQS queue processQueueSQS
     * and processes the data based on their process category.
     * Queue message body is JSONObject with the parameters to process.
     */
    @Scheduled(fixedDelay=39746)
    public void processQueueJob () {

    	JSONObject msgBody = new JSONObject();
    	long msgCount = 0;
    	boolean continueLoop = true;

    	try {

    		while (continueLoop) {

		    	ReceiveMessageRequest rmr
		    		= new ReceiveMessageRequest()
		    			.withQueueUrl( config.getProperty("processQueueSQS") )
		    			.withMaxNumberOfMessages(5);
		    	List<Message> msgs = sqs.receiveMessage(rmr).getMessages();

		    	if ( msgs.size() == 0 ) {

		    		continueLoop = false;

		    	} else {

			    	for ( Message msg : msgs ) {

			    		/*** Retrieve message body as a JSON object ***/
			    		msgBody = new JSONObject(msg.getBody());

			    		/*** Process the message ***/
						if ( StringUtils.equals(msgBody.getString("category"),"expandRL") ) {
							long maxRO = dao.expandRL(msgBody.getString("userId"), msgBody.getString("zoneId"), 10);
							if (maxRO != 0 ) {
								dao.putRLStats(msgBody.getString("userId"), msgBody.getString("zoneId"), 0, 0, maxRO);
							}

						} else if ( StringUtils.equals(msgBody.getString("category"),"viewPost") ) {
							// (1) Increment the statistic "Views" in Posts table
							//     This data is used for displaying unique view count
							//     This function also increases HI in Posts table
							dao.incrementPostStat(msgBody.getString("postId"), "Views", true, 1);
							// (2) Increment the statistic "HI" in Zones table
							//     This data is used for displaying zones stats
							dao.incrementZoneStat("M", msgBody.getString("zoneId"), "HI", false, 1);
							// (3) Increment the statistic "HI" in Entities table for blogger
							//     This data is used for displaying blogger stats
							dao.incrementEntityStat(msgBody.getString("bloggerId"), "HI", false, 1);
							// (4) Increment the statistic "HI" in Entities table for page
							//     This data is used for displaying page stats
							try {
								if ( StringUtils.isNotBlank(msgBody.getString("pageId")) ) {
									dao.incrementEntityStat(msgBody.getString("pageId").trim(), "HI", false, 1);
					  			}
							} catch (Exception e) {}
							// (5) Increment HI in TopCharts table for ZONE, AUTHOR and POST
							//     This data is used for All time TopCharts
							dao.incrementPostTopCharts(msgBody.getString("postId"), 1 );
							dao.incrementZoneTopCharts(msgBody.getString("zoneId"), 1 );
							dao.incrementBloggerTopCharts(msgBody.getString("bloggerId"), 1 );
							try {
					  			if ( StringUtils.isNotBlank(msgBody.getString("pageId")) ) {
					  				dao.incrementPageTopCharts(msgBody.getString("pageId").trim(), 1 );
					  			}
							} catch (Exception e) {}
							// (6) Increment HI in PostActivity table
							//     This data is used for Current TopCharts
			    			if ( !StringUtils.containsAny(msgBody.getString("postId"), common.unwantedPosts) ) {
								dao.incrementPostActivity(
									msgBody.getString("postId"),
									msgBody.getString("zoneId"),
									msgBody.getString("zoneName"),
									msgBody.getString("bloggerId"),
									msgBody.getString("pageId"),
									1
								);
			    			}
							// (7) Increment PostHI in Ranking table
							dao.incrementRanking(msgBody.getString("zoneId"), msgBody.getString("postId"), 1);
							// (8) Insert into PostViews table
							dao.putPostViews(msgBody.getString("postId"), msgBody.getString("ipAddress"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"publishPost") ) {
							// (1) Create record in Ranking table: Ranking data is used by expandRL procedure
							dao.initializeRanking(msgBody.getString("zoneId"), msgBody.getString("postId"));
							// (2) Add the post to RLs of users waiting for a post
							dao.addPostToRLs(msgBody.getString("zoneId"), msgBody.getString("postId"));
							// (3) Add the post to guest RL
							dao.putRL("guest", msgBody.getString("zoneId"), msgBody.getString("postId"));
							// (4) Insert into TopCharts (for Just Published) & Search tables
			    			if ( !StringUtils.containsAny(msgBody.getString("postId"), common.unwantedPosts) ) {
		    					dao.putJustPublished(msgBody.getString("postId"), msgBody.getLong("postUpdateDate"));
		    					dao.putPostSearch(msgBody.getString("postId"));
			    			}
							// (5) Increment the statistic "Posts" in Zones and Entities tables
							//     This data is used for displaying raw counts in UI
							dao.incrementZoneStat("ALL", msgBody.getString("zoneId"), "Posts", true, 1);
							dao.incrementEntityStat(msgBody.getString("bloggerId"), "Posts", false, 1); // false because bloggerHI won't be bumped up just for publishing a post
							try {
					  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
					  				dao.incrementEntityStat(msgBody.getString("pageId").trim(), "Posts", false, 1);  // false because bloggerHI won't be bumped up just for publishing a post
					  			}
							} catch (Exception e) {}
							// (6) Increment HI in TopCharts table
							//     This data is used for Alltime TopCharts
							dao.incrementZoneTopCharts(msgBody.getString("zoneId"), 10);
							// (7) Insert into TopCharts table (for Ticker)
							if (	!StringUtils.contains(msgBody.getString("postId"), "2017")
									&& !StringUtils.contains(msgBody.getString("postId"), "2022")
									&& !StringUtils.contains(msgBody.getString("postId"), "researchnest")
								) {
								String pageId = "";
								try {
									pageId = msgBody.getString("pageId").trim();
								} catch (Exception e) {}
		    					dao.putPublishTicker(msgBody.getString("bloggerId"), msgBody.getString("postId"), msgBody.getString("zoneId"), pageId);
							}

						} else if ( StringUtils.equals(msgBody.getString("category"),"deletePost") ) {
							String pageId = "";
							try {
								pageId = msgBody.getString("pageId").trim();
							} catch (Exception e) {}
							dao.deletePostAsync(msgBody.getString("bloggerId"), msgBody.getString("zoneId"), pageId, msgBody.getString("postId"), msgBody.getString("publishFlag"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"processVote") ) {
							// (1) Derive effective vote
							int newVote = msgBody.getInt("newVote");
							int currentVote = msgBody.getInt("currentVote");
							int effectiveVote = newVote - currentVote;
							// (2) Increment or Decrement PostHI in Ranking table
							//     This data is used for reading top posts for a given Zone while expanding RL
							dao.incrementRanking(msgBody.getString("zoneId"), msgBody.getString("postId"), effectiveVote * 2 );
							// (3) Increment or Decrement the statistic "Votes" in Entities, Zones and Posts tables
							//     This data is used for displaying raw counts in UI
							dao.incrementEntityStat(msgBody.getString("bloggerId"), "Votes", true, effectiveVote );
							try {
					  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
					  				dao.incrementEntityStat(msgBody.getString("pageId").trim(), "Votes", true, effectiveVote );
					  			}
							} catch (Exception e) {}
							dao.incrementZoneStat("M", msgBody.getString("zoneId"), "Votes", true, effectiveVote );
							if ( currentVote == 1 ) {
								dao.incrementPostStat(msgBody.getString("postId"), "UpVotes", true, -1); // reverse current up vote
							} else if ( currentVote == -1 ) {
								dao.incrementPostStat(msgBody.getString("postId"), "DownVotes", true, -1); // reverse current down vote
							}
							if ( newVote == 1 ) {
								dao.incrementPostStat(msgBody.getString("postId"), "UpVotes", true, 1); // vote up
							} else if ( newVote == -1 ) {
								dao.incrementPostStat(msgBody.getString("postId"), "DownVotes", true, 1); // vote down
							}
							// (4) Increment or Decrement HI in TopCharts table
							//     This data is used for Alltime TopCharts
							dao.incrementZoneTopCharts(msgBody.getString("zoneId"), effectiveVote * 2 );
							dao.incrementBloggerTopCharts(msgBody.getString("bloggerId"), effectiveVote * 2 );
							try {
					  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
					  				dao.incrementPageTopCharts(msgBody.getString("pageId").trim(), effectiveVote * 2 );
					  			}
							} catch (Exception e) {}
							dao.incrementPostTopCharts(msgBody.getString("postId"), effectiveVote * 2 );
	    					// (5) Insert into TopCharts table for Ticker
							//     Increment or Decrement HI in PostActivity table
							//     This data is used for Trending TopCharts
			    			if ( !StringUtils.containsAny(msgBody.getString("postId"), common.unwantedPosts) ) {
		    					dao.putVoteTicker(msgBody.getString("voterId"), msgBody.getString("postId"), newVote);
								dao.incrementPostActivity(
									msgBody.getString("postId"),
									msgBody.getString("zoneId"),
									msgBody.getString("zoneName"),
									msgBody.getString("bloggerId"),
									msgBody.getString("pageId"),
									effectiveVote * 2
								);
			    			}

						} else if ( StringUtils.equals(msgBody.getString("category"),"postComment") ) {
							// (1) Increment the statistic "Comments" in Zones, Entities and Posts tables
							//     This data is used for displaying raw counts in UI
							dao.incrementZoneStat("M", msgBody.getString("zoneId"), "Comments", true, 1 );
							dao.incrementEntityStat(msgBody.getString("bloggerId"), "Comments", true, 1 );
							try {
					  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
									dao.incrementEntityStat(msgBody.getString("pageId").trim(), "Comments", true, 1 );
					  			}
							} catch (Exception e) {}
							dao.incrementPostStat(msgBody.getString("postId"), "Comments", true, 1);
							// Don't increment HI if the same commenter posts another comment on the same post.
							if ( StringUtils.equals(msgBody.getString("checkCommenterIdExists"),"N") ) {
								// (2) Increment HI in TopCharts table for ZONE, BLOGGER and POST
								//     This data is used for Alltime TopCharts
								dao.incrementZoneTopCharts(msgBody.getString("zoneId"), 3 );
								dao.incrementBloggerTopCharts(msgBody.getString("bloggerId"), 3 );
								try {
						  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
										dao.incrementPageTopCharts(msgBody.getString("pageId").trim(), 3 );
						  			}
								} catch (Exception e) {}
								dao.incrementPostTopCharts(msgBody.getString("postId"), 3 );
								// (3) Increment PostHI by 3 in Ranking table
								dao.incrementRanking(msgBody.getString("zoneId"), msgBody.getString("postId"), 3);
							}
			    			if ( !StringUtils.containsAny(msgBody.getString("postId"), common.unwantedPosts) ) {
		    					// (4) Insert into TopCharts table for Ticker
		    					dao.putCommentTicker(msgBody.getString("commenterId"), msgBody.getString("postId"));
								// (5) Increment HI in PostActivity table
								//     This data is used for Current TopCharts
								dao.incrementPostActivity(
										msgBody.getString("postId"),
										msgBody.getString("zoneId"),
										msgBody.getString("zoneName"),
										msgBody.getString("bloggerId"),
										msgBody.getString("pageId"),
										3
									);
			    			}
							// (6) Email Comment to followers of the post
							dao.emailCommentFollowersJob(
								msgBody.getString("postId"), msgBody.getString("commenterId"),
								msgBody.getString("commenterName"), msgBody.getString("commenterEmail"),
								msgBody.getString("origCommentText"), msgBody.getString("commentText")
							);

						} else if ( StringUtils.equals(msgBody.getString("category"),"deleteComment") ) {
							// (1) Decrement the statistic "Comments" in Zones, Entities and Posts tables
							//     This data is used for displaying raw counts in UI
							dao.incrementZoneStat("M", msgBody.getString("zoneId"), "Comments", true, -1 );
							dao.incrementEntityStat(msgBody.getString("bloggerId"), "Comments", true, -1 );
							try {
					  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
					  				dao.incrementEntityStat(msgBody.getString("pageId").trim(), "Comments", true, -1 );
					  			}
							} catch (Exception e) {}
							dao.incrementPostStat(msgBody.getString("postId"), "Comments", true, -1);
							// Decrement HI when there are no more comments by the commenter on the post.
							if ( StringUtils.equals(msgBody.getString("checkCommenterIdExists"),"N") ) {
								// (2) Decrement HI in TopCharts table for ZONE, BLOGGER and POST
								//     This data is used for All time TopCharts
								dao.incrementZoneTopCharts(msgBody.getString("zoneId"), -3 );
								dao.incrementBloggerTopCharts(msgBody.getString("bloggerId"), -3 );
								try {
						  			if (StringUtils.isNotBlank(msgBody.getString("pageId"))) {
						  				dao.incrementPageTopCharts(msgBody.getString("pageId").trim(), -3 );
						  			}
								} catch (Exception e) {}
								dao.incrementPostTopCharts(msgBody.getString("postId"), -3 );
								// (3) Decrement PostHI by 3 in Ranking table
								dao.incrementRanking(msgBody.getString("zoneId"), msgBody.getString("postId"), -3);
							}

						} else if ( StringUtils.equals(msgBody.getString("category"),"dropAccount") ) {
							// Drop user Account
							dao.dropAccount(msgBody.getString("username"),msgBody.getString("userId"));
						}

			    		/*** Delete message from SQS ***/
						sqs.deleteMessage(
							new DeleteMessageRequest()
								.withQueueUrl( config.getProperty("processQueueSQS") )
								.withReceiptHandle(msg.getReceiptHandle())
						);

						/*** Print success message ***/
						if ( StringUtils.equals(msgBody.getString("category"),"expandRL") ) {
			    	        logger.log(Level.INFO,"expandRL: userId=" + msgBody.getString("userId") + " zoneId=" + msgBody.getString("zoneId"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"viewPost") ) {
			    	        logger.log(Level.INFO,"viewPost: bloggerId=" + msgBody.getString("bloggerId") + " zoneId=" + msgBody.getString("zoneId") + " postId=" + msgBody.getString("postId") + " ipAddress=" + msgBody.getString("ipAddress"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"publishPost") ) {
			    	        logger.log(Level.INFO,"publishPost: bloggerId=" + msgBody.getString("bloggerId") + " zoneId=" + msgBody.getString("zoneId") + " postId=" + msgBody.getString("postId"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"deletePost") ) {
			    	        logger.log(Level.INFO,"deletePost: bloggerId=" + msgBody.getString("bloggerId") + " zoneId=" + msgBody.getString("zoneId") + " postId=" + msgBody.getString("postId") + " publishFlag=" + msgBody.getString("publishFlag"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"processVote") ) {
			    	        logger.log(Level.INFO,"processVote: bloggerId=" + msgBody.getString("bloggerId") + " zoneId=" + msgBody.getString("zoneId") + " postId=" + msgBody.getString("postId"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"postComment") ) {
			    	        logger.log(Level.INFO,"postComment: bloggerId=" + msgBody.getString("bloggerId") + " zoneId=" + msgBody.getString("zoneId") + " postId=" + msgBody.getString("postId"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"deleteComment") ) {
			    	        logger.log(Level.INFO,"deleteComment: bloggerId=" + msgBody.getString("bloggerId") + " zoneId=" + msgBody.getString("zoneId") + " postId=" + msgBody.getString("postId"));

						} else if ( StringUtils.equals(msgBody.getString("category"),"dropAccount") ) {
			    	        logger.log(Level.INFO,"dropAccount: username=" + msgBody.getString("username") + " userId=" + msgBody.getString("userId"));

						}

						msgCount++;

			    	}
		    	}
    		}
	    	/*** Print stats message ***/
	        logger.log(Level.INFO,"heatbud-process-queue: Processed " + msgCount + " messages.");

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - processQueueJob");
	        logger.log(Level.SEVERE,"msg: " + msgBody.toString());
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

	/**
     * This cronjob reads messages from SQS queues sesBouncesQueueSQS and sesComplaintsQueueSQS
     * and deletes those email addresses from the system.
     * Queue message body is JSONObject with the parameters to process.
     */
	@Scheduled(cron="0 10 8,18 * * *")
    public void processSESQueuesJob () {

    	JSONObject snsMessage = new JSONObject();
    	long msgCount = 0;
    	boolean continueLoop = true;

    	try {

    		/*** BOUNCES QUEUE ***/
    		while (continueLoop) {

		    	ReceiveMessageRequest sqsMessagesRequest
		    		= new ReceiveMessageRequest()
		    			.withQueueUrl( config.getProperty("sesBouncesQueueSQS") )
		    			.withMaxNumberOfMessages(5);
		    	List<Message> sqsMessages = sqs.receiveMessage(sqsMessagesRequest).getMessages();

		    	if ( sqsMessages.size() == 0 ) {

		    		continueLoop = false;

		    	} else {

			    	for ( Message sqsMessage : sqsMessages ) {

			    		/*** Retrieve message body as an SNS Message ***/
			            snsMessage = new JSONObject(sqsMessage.getBody());
		    	        logger.log(Level.SEVERE,"Processing bounce msg: " + snsMessage.toString());

			    		/*** Process the message ***/
		    	        JSONObject sesMessage = new JSONObject(snsMessage.getString("Message"));
		    	        String bounceType = sesMessage.getJSONObject("bounce").getString("bounceType");
			    		if ( StringUtils.equals(bounceType, "Permanent") ) {

			    	        JSONArray bouncedRecipients = sesMessage.getJSONObject("bounce").getJSONArray("bouncedRecipients");
			    			for (int j = 0; j < bouncedRecipients.length(); j++) {

				    			String emailAddress = bouncedRecipients.getJSONObject(j).getString("emailAddress");
				    			String newEmailAddress = StringUtils.lowerCase(StringUtils.replace(emailAddress, "@", "-at-")+"@heatbud.com");

			    		    	/*** Delete Notifications flags ***/
			    				dao.deleteNotificationFlags(emailAddress);

			    		    	/*** Unfollow all comments ***/
				    			dao.unfollowAllComments(emailAddress);

			        			/*** Update Email Address in Users table ***/
			    				// email address is the hash key - create a new record and delete the old one
				    			User user = dao.getUser(emailAddress);
			    				user.setUsername(newEmailAddress);
			    				dao.saveUser(user);
			    				dao.deleteUser(emailAddress);

			    		    	/*** Update Entities table ***/
				    			dao.updateEntityEmail(user.getUserId(), newEmailAddress);

								// print
				    	        logger.log(Level.SEVERE,"Successfully processed bounce emailAddress: " + emailAddress);

			    			}

			    		} else {
			    	        logger.log(Level.SEVERE,"Skipped bounce message because bounceType: " + bounceType);
			    		}

			    		/*** Delete message from SQS ***/
						sqs.deleteMessage(
							new DeleteMessageRequest()
							.withQueueUrl( config.getProperty("sesBouncesQueueSQS") )
							.withReceiptHandle(sqsMessage.getReceiptHandle())
						);

						msgCount++;

			    	}
		    	}
    		}
	    	/*** Print stats message ***/
	        logger.log(Level.INFO,"heatbud-ses-bounces-queue: Processed " + msgCount + " messages.");

    		/*** COMPLAINTS QUEUE ***/
    		while (continueLoop) {

		    	ReceiveMessageRequest sqsMessagesRequest
		    		= new ReceiveMessageRequest()
		    			.withQueueUrl( config.getProperty("sesComplaintsQueueSQS") )
		    			.withMaxNumberOfMessages(5);
		    	List<Message> sqsMessages = sqs.receiveMessage(sqsMessagesRequest).getMessages();

		    	if ( sqsMessages.size() == 0 ) {

		    		continueLoop = false;

		    	} else {

			    	for ( Message sqsMessage : sqsMessages ) {

			    		/*** Retrieve message body as an SNS Message ***/
			            snsMessage = new JSONObject(sqsMessage.getBody());
		    	        logger.log(Level.SEVERE,"Processing complaint msg: " + snsMessage.toString());

			    		/*** Process the message ***/
		    	        JSONObject sesMessage = new JSONObject(snsMessage.getString("Message"));
		    	        JSONArray complainedRecipients = sesMessage.getJSONObject("complaint").getJSONArray("complainedRecipients");
		    	        for (int j = 0; j < complainedRecipients.length(); j++) {

			    			String emailAddress = complainedRecipients.getJSONObject(j).getString("emailAddress");

		    		    	/*** Delete Notifications flags ***/
		    				dao.deleteNotificationFlags(emailAddress);

		    		    	/*** Unfollow all comments ***/
			    			dao.unfollowAllComments(emailAddress);

			    			// For complaints, do not update Entities and Users tables

							// print
			    	        logger.log(Level.SEVERE,"Successfully processed complaint emailAddress: " + emailAddress);

		    			}

			    		/*** Delete message from SQS ***/
						sqs.deleteMessage(
							new DeleteMessageRequest()
							.withQueueUrl( config.getProperty("sesComplaintsQueueSQS") )
							.withReceiptHandle(sqsMessage.getReceiptHandle())
						);

						msgCount++;

			    	}
		    	}
    		}
	    	/*** Print stats message ***/
	        logger.log(Level.INFO,"heatbud-ses-complaints-queue: Processed " + msgCount + " messages.");

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - processSESQueuesJob");
	        logger.log(Level.SEVERE,"snsMessage: " + snsMessage.toString());
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

    
	/**
     * This cronjob reads messages from SQS queue newslettersQueueSQS and emails the newsletters.
     * Queue message body is JSONObject with the parameters to process.
     */
//	@Scheduled(fixedDelay=180000)
    public void newslettersQueueJob () {

    	JSONObject msgBody = new JSONObject();
    	long msgCount = 0;

    	try {

	    	ReceiveMessageRequest rmr
	    		= new ReceiveMessageRequest()
	    			.withQueueUrl( config.getProperty("newslettersQueueSQS") )
	    			.withMaxNumberOfMessages(1);
	    	List<Message> msgs = sqs.receiveMessage(rmr).getMessages();

	    	for ( Message msg : msgs ) {

	    		/*** Retrieve message body as a JSON object ***/
	    		msgBody = new JSONObject(msg.getBody());

				String retMessage = dao.generateNewsLetter(msgBody.getString("username"));

				/*** Delete message from SQS ***/
				if ( StringUtils.equals(retMessage, "SUCCESS") ) {
					sqs.deleteMessage(
						new DeleteMessageRequest()
							.withQueueUrl( config.getProperty("newslettersQueueSQS") )
							.withReceiptHandle(msg.getReceiptHandle())
					);
				}

    	        logger.log(Level.INFO,"newslettersQueueJob: username=" + msgBody.getString("username"));
				msgCount++;

    		}

    		/*** Print stats message ***/
	        logger.log(Level.INFO,"heatbud-newsletters-queue: Processed " + msgCount + " messages.");

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - newslettersQueueJob");
	        logger.log(Level.SEVERE,"msg: " + msgBody.toString());
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

	/**
     * This cronjob scans catalina.out and emails if any SERVERE lines found.
     * sec min hour date month day-of-the-week
     */
	@Scheduled(cron="0 5,15,25,35,45,55 * * * *")
    public void monitorTomcat () {

    	String retMessage = "FAILURE"; // default
    	try {

       		/*** Run monitorTomcat ***/
  	        logger.log(Level.INFO,"monitorTomcatJob started.");
          	retMessage = file.monitorTomcat();

          	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
          		logger.log(Level.INFO,"monitorTomcatJob completed.");
          	} else {
	   	        logger.log(Level.SEVERE,"monitorTomcatJob failed.");
	   	        logger.log(Level.SEVERE,retMessage);
          	}

       		/*** Run monitorLocalhost ***/
  	        logger.log(Level.INFO,"monitorLocalhost started.");
          	retMessage = file.monitorLocalhost();

          	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
          		logger.log(Level.INFO,"monitorLocalhost completed.");
          	} else {
	   	        logger.log(Level.SEVERE,"monitorLocalhost failed.");
	   	        logger.log(Level.SEVERE,retMessage);
          	}

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - monitorTomcatJob");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

	/**
     * This cronjob populates TopCharts table with TOP POSTS and TOP AUTHORS based on data from PostHI and BloggerHI tables.
     * It kicks off every hour but processes data only when the period changes.
     * sec min hour date month day-of-the-week
     */
	@Scheduled(cron="0 5 12 * * *")
    public void generateTopChartsJob() {

    	String retMessage = "FAILURE"; // default
    	try {

        	// Since this cron runs from multiple app servers,
        	// avoid locking issues by giving them a random sleep
        	Thread.sleep( new Random().nextInt(30)*1000 );

    		// Get the latest period for which job was run previously
   			long prevRunPeriod = Long.parseLong(dao.getAttribute("T", "generateTopChartsJobPeriod"));
    		// Get the current period
            long currentRunPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

            if ( currentRunPeriod > prevRunPeriod ) {

            	// Ensure that only one instance of this job will proceed beyond this step
            	retMessage = dao.putStartingRecord("generateTopChartsJob", currentRunPeriod, InetAddress.getLocalHost().getHostName());

               	if ( StringUtils.equals(retMessage, "SUCCESS") ) {

               		/*** Generate Top Charts ***/
	    	        logger.log(Level.INFO,"generateTopChartsJob started.");
	            	retMessage = dao.generateTopCharts(currentRunPeriod);

	            	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
	            		// Insert into Attributes
		       			dao.setAttribute("T", "generateTopChartsJobPeriod", currentRunPeriod+"");
		       			// Print success message
		    	        logger.log(Level.INFO,"generateTopChartsJob successfully completed for Period=" + currentRunPeriod);
	            	} else {
		       			// Print failure message
		    	        logger.log(Level.SEVERE,"generateTopChartsJob failed for Period=" + currentRunPeriod);
		    	        logger.log(Level.SEVERE,retMessage);
		    	        // when the job fails, sleep for 30 seconds before deleting the starting record
		    	        // otherwise, there is a chance that another server reruns the failed job
		            	Thread.sleep( 30*1000 );
	            	}

	            	// Delete Starting Record
	            	dao.deleteStartingRecord("generateTopChartsJob");

               	}

            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - generateTopChartsJob");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

	/**
     * This cronjob runs daily admin tasks.
     * Scheduled to run once a day.
     * sec min hour date month day-of-the-week
     */
	@Scheduled(cron="0 5 15 * * *")
    public void dailyAdminJob() {

    	String retMessage = "FAILURE"; // default
    	try {

        	// Since this cron runs from multiple app servers,
        	// avoid locking issues by giving them a random sleep
        	Thread.sleep( new Random().nextInt(30)*1000 );

        	// Get the latest period for which job was run previously
   			long prevRunPeriod = Long.parseLong(dao.getAttribute("A", "dailyAdminJobPeriod"));
    		// Get the current period
            long currentRunPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

            if ( currentRunPeriod > prevRunPeriod ) {

           		/*** Run purgeUnsuccessfulCronjobs task ***/
            	// Note: This task is done without placing a starting record into the attributes table
    	        logger.log(Level.INFO,"purgeUnsuccessfulCronjobs started.");
            	retMessage = dao.purgeUnsuccessfulCronjobs();

	            // Process SUCCESS or ERROR
	            if ( StringUtils.equals(retMessage, "SUCCESS") ) {
	            	logger.log(Level.INFO,"purgeUnsuccessfulCronjobs successfully completed for Period=" + currentRunPeriod);
	            } else {
	            	logger.log(Level.SEVERE,"purgeUnsuccessfulCronjobs failed for Period=" + currentRunPeriod);
	            	logger.log(Level.SEVERE,retMessage);
	            }

           		/*** Run other daily admin tasks ***/
            	// Ensure that only one instance of this job will proceed beyond this step
            	retMessage = dao.putStartingRecord("dailyAdminJob", currentRunPeriod, InetAddress.getLocalHost().getHostName());

               	if ( StringUtils.equals(retMessage, "SUCCESS") ) {

               		// Task 1: Send reminder emails to those bloggers with Unpublished posts
	    	        logger.log(Level.INFO,"remindUnpublished started.");
               		retMessage = dao.remindUnpublished();

               		// Task 2: Send reminder emails to those who have started a page payment but not completed yet
	            	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
		    	        logger.log(Level.INFO,"remindPagePayment started.");
	               		retMessage = dao.remindPagePayments();
	            	}

               		// Task 3: Purge older Just Published records from Top Charts table
	            	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
		    	        logger.log(Level.INFO,"purgeJustPublished started.");
	               		retMessage = dao.purgeJustPublished();
	            	}

	            	// Process SUCCESS or ERROR
	            	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
		            	// Insert into Attributes
		    	        logger.log(Level.INFO,"dailyAdminJob successfully completed for Period=" + currentRunPeriod);
		       			dao.setAttribute("A", "dailyAdminJobPeriod", currentRunPeriod+"");
	            	} else {
		    	        logger.log(Level.SEVERE,"dailyAdminJob failed for Period=" + currentRunPeriod);
		    	        logger.log(Level.SEVERE,retMessage);
		    	        // when the job fails, sleep for 30 seconds before deleting the starting record
		    	        // otherwise, there is a chance that another server reruns the failed job
		            	Thread.sleep( 30*1000 );
	            	}

	            	// Delete Starting Record
	            	dao.deleteStartingRecord("dailyAdminJob");

               	}

            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,"HeatbudCronUtil - dailyAdminJob");
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

	/**
     * This cronjob generates RLs for Guest users.
     * Scheduled to run once a day.
     * sec min hour date month day-of-the-week
     */
//    @Scheduled(cron="0 5 17 * * *")
    public void generateGuestRLJob () {

    	String retMessage = "FAILURE"; // default
    	try {

        	// Since this cron runs from multiple app servers,
        	// avoid locking issues by giving them a random sleep
        	Thread.sleep( new Random().nextInt(30)*1000 );

        	// Get the latest period for which job was run previously
   			long prevRunPeriod = Long.parseLong(dao.getAttribute("A", "generateGuestRLJobPeriod"));
    		// Get the current period
            long currentRunPeriod = (long) System.currentTimeMillis()/21600000; // 6 hours

            if ( currentRunPeriod > prevRunPeriod ) {

            	// Ensure that only one instance of this job will proceed beyond this step
            	retMessage = dao.putStartingRecord("generateGuestRLJob", currentRunPeriod, InetAddress.getLocalHost().getHostName());

               	if ( StringUtils.equals(retMessage, "SUCCESS") ) {

               		// Generate Guest RL
	    	        logger.log(Level.INFO,"generateGuestRLJob started.");
               		retMessage = dao.generateGuestRL();

	            	if ( StringUtils.equals(retMessage, "SUCCESS") ) {
		            	// Insert into Attributes
		       			dao.setAttribute("A", "generateGuestRLJobPeriod", currentRunPeriod+"");
		       			// Print success message
		    	        logger.log(Level.INFO,"generateGuestRLJob successfully completed for Period=" + currentRunPeriod);
	            	} else {
		       			// Print failure message
		    	        logger.log(Level.SEVERE,"generateGuestRLJob failed for Period=" + currentRunPeriod);
		    	        logger.log(Level.SEVERE,retMessage);
		    	        // when the job fails, sleep for 30 seconds before deleting the starting record
		    	        // otherwise, there is a chance that another server reruns the failed job
		            	Thread.sleep( 30*1000 );
	            	}

	            	// Delete Starting Record
	            	dao.deleteStartingRecord("generateGuestRLJob");

               	}

            }

		} catch (Exception e) {
	        logger.log(Level.SEVERE,e.getMessage(),e);
		}
    }

}
