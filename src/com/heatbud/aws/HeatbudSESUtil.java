/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.aws;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.heatbud.entity.Entity;
import com.heatbud.util.Configuration;
import com.heatbud.util.HeatbudCommon;

/**
 * This class sends emails using Simple Email Service (SES).
 */
public class HeatbudSESUtil {

	// Logger object
	private Logger logger = Logger.getLogger(HeatbudSESUtil.class.getName());
	// Heatbud properties
	private static Configuration config = Configuration.getInstance();
	// AWS credentials
	private static AWSCredentials creds = new BasicAWSCredentials(config.getProperty("accessKey"), config.getProperty("secretKey"));
	// common functions
	@Autowired
	HeatbudCommon common;
	// DynamoDB client
    @Autowired
	private HeatbudDynamoDBUtil dao;

	/*
	 * The SES Client is thread safe so we only ever need one static instance.
	 * While you can have multiple instances it is better to only have one because it's
	 * a relatively heavy weight class.
	 */
    private static AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
       	.withEndpointConfiguration(new EndpointConfiguration("https://email.us-west-2.amazonaws.com", "us-west-2"))
       	.withCredentials(new AWSStaticCredentialsProvider(creds))
       	.build();

	public void sendVerificationEmail (String firstName, String username, String salt, String ipAddress) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Heatbud Email Verification Request";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + firstName + "!<br>" +
				"<br>" +
				"Please click on the following link and confirm that you are the owner of this email address.<br>" +
				"<br></div>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/verify-email/" + salt + "/" + username + "\">Verify Email</a>" +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"If the above link is not clickable, paste this URL into your browser : https://www.heatbud.com/verify-email/" + salt + "/" + username + "<br>" +
				"<br>" +
				"Your account will not be created until you confirm your email address. If you have any questions, please contact us at https://www.heatbud.com/do/contact.<br>"+
				"<br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"PS: Our computer claims that your signup request has been originated from " + ipAddress +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send verification email: Email Address=" + username + " Verification Code=" + salt);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendWelcomeEmail (String firstName, String username, String userId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Welcome to Heatbud!";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + firstName + "!<br>" +
				"<br>" +
				"Thanks for signing up!<br>" +
				"<br>" +
				"Visit your " + "<a href=\"https://www.heatbud.com/" + userId + "\">Profile Page</a>" +
				" to set your skills and pricing. Build your portfolio by creating some blog posts that speak to your expertise. See the guide below:<br/>" +
				"<br/>" +
				"<a href=\"https://www.heatbud.com/do/help/main/bloggers\">Quick guide to creating your first post</a><br/>" +
				"<br/>" +
				"Please note that we don't accept blog posts on adult topics such as casino, tobacco, escorts, guns etc.<br/>" +
				"<br/>" +
				"We appreciate your feedback. Please " +
				"<a href=\"https://www.heatbud.com/do/contact\">Contact Us</a>" +
				" with your feature requests and suggestions for improvement.<br>" +
				"<br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("info@heatbud.com", "kk@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send welcome email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendUnpublishedBusinessEmail (String firstName, String username, List<String> postIds) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Gregory Schaeffer <gs@heatbud.com>";
			String SUBJECT="Heatbud users are waiting to read your post(s)";
			String BODY=
				"<div style=\"font-size:1.1em; background-color:rgb(248, 250, 252); padding:1%; color:#333333\">" +
				"<table style=\"background-color:rgb(123,205,123); border-spacing:0px; width:100%; padding:1%\"><tr style=\"width:100%\">" +
					"<td style=\"width:50%\"><a href=\"https://www.heatbud.com\"><img alt=\"Heatbud logo\" style=\"width:140px; border:none\" src=\"https://www.heatbud.com/resources/images/heatbud-logo.png\"/></a></td>" +
					"<td style=\"width:50%; text-align:right\"><div style=\"font-family:'Permanent Marker', Helvetica, Arial; font-size:1.2em; color:white; font-weight:bold\">Social Blogging for Businesses</div></td>" +
				"</tr></table><br/>" +
				"Hi " + firstName + "!<br/>" +
				"<br/>" +
				"My name is Gregory Schaeffer and I'm the opportunity specialist here at Heatbud. I see that the following post(s) have been unpublished for more than five days.<br/>" +
				"<br/>";
			for (int i = 0; i < postIds.size(); i++) {
				BODY= BODY + "<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postIds.get(i) + "\">" + dao.getPostTitle(postIds.get(i)) + "</a><br>";
			}
			BODY= BODY +
				"<br/>" +
				"Not sure if you checked out some of our new services, we now offer outsourcing services for businesses that just don't have the time to sit down and write the blog posts, which is really important for your digital footprint.<br/>" +
				"<br/>" +
				"<div>" +
					"<div style=\"background-color:rgb(17,85,204); padding:2%; color:white\">PREMIUM Package: $295 a month OR $2,950 a year</div>" +
					"<div style=\"background-color:rgb(204,221,255); padding:1%; color:black\"><ul>" +
						"<li>If you don't have a website -> We will help you register a domain and redirect it to your Heatbud page. Save money by not having to hire a web designer.</li>" +
						"<li>If you have a website -> We will help you redirect your website's blog menu to your Heatbud page.</li>" +
						"<li>We will Setup keywords at Heatbud using our FREE SEO keyword tool exclusively available to Heatbud customers.</li>" +
						"<li>We will Find and pay top bloggers to write 2 SEO-rich blog posts of 300-500 words each every month for your business.</li>" +
						"<li>You can Track your progress anytime using the Google Analytics dashboard available in your Page Manager.</li>" +
						"<li>Compare 100k a year to hire a full time blogger and an account manager vs $2950 using heatbud.</li>" +
					"</ul></div>" +
				"</div>" +
				"<div style=\"margin-top:15px\">" +
					"<div style=\"background-color:rgb(17,85,204); padding:2%; color:white\">PREMIUM PLUS Package: $495 a month OR $4,950 a year</div>" +
					"<div style=\"background-color:rgb(204,221,255); padding:1%; color:black\"><ul>" +
						"<li>If you have a website -> We will help you redirect your website's blog menu to your Heatbud page.</li>" +
						"<li>We will Setup keywords at Heatbud using our FREE SEO keyword tool exclusively available to Heatbud customers.</li>" +
						"<li>We will Find and pay top bloggers to write 4 SEO-rich blog posts of 800-1000 words plus 2 images in each post every month for your business.</li>" +
						"<li>You can Track your progress anytime using the Google Analytics dashboard available in your Page Manager.</li>" +
						"<li>Compare 200k a year to hire multiple full time bloggers and an account manager vs $4950 using heatbud.</li>" +
					"</ul></div>" +
				"</div>" +
				"<br/>" +
				"I can help you get your business thrive in social blogging. To get started, simply reply to this email.<br/>" +
				"<br/>" +
				"Sincerely,<br/>Gregory Schaeffer<br/>Opportunity Specialist<br/>gs@heatbud.com<br/>" +
				"<br/>" +
				"<div style=\"font-size:0.8em\">You will receive at most one reminder per blog post when unpublished in five days. " +
				"Don't want reminders on unpublished posts? Visit the <a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/user/notifications\">Notifications</a> page on Heatbud.</div>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("info@heatbud.com", "kk@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send unpublishedPosts email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendUnpublishedPersonalEmail (String firstName, String username, List<String> postIds) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Heatbud users are waiting to read your post(s)";
			String BODY=
				"<div style=\"font-size:1.1em; background-color:rgb(248, 250, 252); padding:1%; color:#333333\">" +
				"<table style=\"background-color:rgb(123,205,123); border-spacing:0px; width:100%; padding:1%\"><tr style=\"width:100%\">" +
					"<td style=\"width:50%\"><a href=\"https://www.heatbud.com\"><img alt=\"Heatbud logo\" style=\"width:140px; border:none\" src=\"https://www.heatbud.com/resources/images/heatbud-logo.png\"/></a></td>" +
					"<td style=\"width:50%; text-align:right\"><div style=\"font-family:'Permanent Marker', Helvetica, Arial; font-size:1.2em; color:white; font-weight:bold\">Social Blogging for Everyone</div></td>" +
				"</tr></table><br/>" +
				"Hi " + firstName + "!<br/>" +
				"<br/>" +
				"The following blog post(s) have been unpublished for more than five days.<br/>" +
				"<br/>";
			for (int i = 0; i < postIds.size(); i++) {
				BODY= BODY + "<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postIds.get(i) + "\">" + dao.getPostTitle(postIds.get(i)) + "</a><br>";
			}
			BODY= BODY +
				"<br/>" +
				"Publishing a blog post is as easy as clicking a button and it appears in the Just Published Posts section right away!<br/>" +
				"<br/>" +
				"If you have questions, feel free to " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/do/contact\">Contact Us</a> anytime.<br>" +
				"<br/>" +
				"Sincerely,<br/>Heatbud LLC<br/>" +
				"<br/>" +
				"<div style=\"font-size:0.8em\">You will receive at most one reminder per blog post when unpublished in five days. " +
				"Don't want reminders on unpublished posts? Visit the <a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/user/notifications\">Notifications</a> page on Heatbud.</div>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("info@heatbud.com", "kk@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send unpublishedPosts email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendPendingPagePaymentEmail (String paymentHandler, long handlerDate,
		String firstName, String username,
		String pageId, String pageName,
		String productType, long amount, String coupon) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your pending payment for " + pageName;
			// Convert HandlerDate to America/Los_Angeles
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(handlerDate);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
			String hanlderDateString = sdf.format(cal.getTime());
			String BODY =
				"<div style=\"font-size:1.1em; margin-top:3%; border:1px solid #f7f7f7; background-color:#f8fafc; text-align:center; color:#333333\"><div style=\"width:600px; margin:0 auto; text-align:left;\">" +
				"<table style=\"background-color:rgb(123,205,123); border-radius:5px; border-spacing:0px; width:100%; padding:1%\"><tr style=\"width:100%\">" +
					"<td style=\"width:50%\"><a href=\"https://www.heatbud.com\"><img alt=\"Heatbud logo\" style=\"width:140px; border:none\" src=\"https://www.heatbud.com/resources/images/heatbud-logo.png\"/></a></td>" +
					"<td style=\"width:50%; text-align:right\"><div style=\"font-family:'Permanent Marker', Helvetica, Arial; font-size:1.1em; color:white; font-weight:bold\">Social Blogging for Businesses</div></td>" +
				"</tr></table><br/>" +
				"<br/>" +
				"Hi " + firstName + "!<br/>" +
				"<br/>" +
				"You have initiated a payment for " +
				"<a href=\"https://www.heatbud.com/" + pageId + "\">" + pageName + "</a>" +
				" at " + hanlderDateString + " PDT that has not been completed yet.<br/>" +
				"<br/>" +
				"<b>Payment details:</b><br/>" +
				"Product Type: " + productType + "<br/>" +
				"Amount: " + amount + " USD<br/>";
			if ( StringUtils.isBlank(coupon) ) {
				BODY = BODY + "<br/>Looking for a coupon code? Check out <a href=\"https://www.heatbud.com/do/help/main/promotions\">Heatbud Promotions</a> page.<br/>";
			} else {
				BODY = BODY + "Coupon Code: " + coupon + "<br/>";
			}
			BODY = BODY + "<br/>" +
				"<div style=\"text-align:center\">" +
					"<div style=\"display:inline-block; background-color:rgb(0,206,209); padding:2% 8%; border-radius:5px;\">" +
						"<a style=\"color:white; text-decoration:none;\" href=\"https://www.heatbud.com/user/page-payment/" + paymentHandler + "\">Continue to Payment</a>" +
					"</div>" +
				"</div>" +
				"<br/>" +
				"If you have already made the payment, please ignore this email.<br/>" +
				"<br/>" +
				"Sincerely,<br/>Heatbud LLC<br/>" +
				"<br/>" +
				"<div style=\"font-size:0.9em\">You will not receive any more reminders on this pending payment." +
				"<br/></div></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send PendingPagePayment email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendPotentialCustomersEmail (List<Entity> potentialCustomers) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Today's list of potential customers";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello Sales team!<br>" +
				"<br>" +
				"This is the list of Heatbud users who have recently created draft blog posts for unpaid pages.<br>" +
				"<br></div><table style=\"border-spacing:4px\">" +
				"<thead><tr><th>Blogger Name</th><th>Blogger Email</th><th>Blogger Country</th><th>Blogger URL</th><th>Business Page URL</th></tr></thead><tbody>";
			for (int i = 0; i < potentialCustomers.size(); i++) {
				BODY = BODY + "<tr><td>" + potentialCustomers.get(i).getEntityName() + "</td><td>"
					+ potentialCustomers.get(i).getEntityEmail() + "</td><td>"
					+ potentialCustomers.get(i).getCountry() + "</td><td>"
					+ "https://www.heatbud.com/" + potentialCustomers.get(i).getEntityId() + "</td><td>"
					+ "https://www.heatbud.com/" + potentialCustomers.get(i).getPrimaryPageId() + "</td></tr>";
			}
			BODY = BODY + "</tbody></table>The first URL is Blogger's profile page and the second URL is Business page.<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses("kk@heatbud.com","gs@heatbud.com").withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send potential customers email.");
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendDropAccountEmail (String firstName, String username) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Sorry to see you go!";
			String BODY=
				"Hi " + firstName + "!\n" +
				"\n" +
				"We're sorry you're leaving Heatbud.\n" +
				"\n" +
				"If you decide to come back in future, please note that our doors are always open for you.\n" +
				"\n" +
				"Please let us know what went wrong and we will do our best to improve our services.\n" +
				"\n" +
				"https://www.heatbud.com/do/contact\n" +
				"\n" +
				"We will send you a $10 Starbucks Card if your feedback is received in the next 24 hours.\n" +
				"\n" +
				"Sincerely,\n" +
				"Heatbud LLC\n";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withText(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send welcome email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendMassEmail (String username) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Heatbud Login Issues";
			String BODY=
				"Dear Heatbud user,\n" +
				"\n" +
				"We've released a new version of the software last night and since then we've seen sporadic login issues on heatbud.com.\n" +
				"\n" +
				"We're happy to report that these issues have been resolved and you may now post and collaborate on Heatbud.\n" +
				"\n" +
				"If you've got any questions, please contact us at\n" +
				"\n" +
				"https://www.heatbud.com/do/contact\n" +
				"\n" +
				"Sincerely,\n" +
				"Heatbud LLC\n";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username);

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withText(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send mass email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendAdminRequestEmail (String adminEmail, String adminName, String zoneId, String zoneName, String requesterId, String requesterName) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Admin Request for " + zoneName;
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + adminName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + requesterId + "\">" + requesterName + "</a>" +
				" has requested to become one of the admins for the following zone.<br>" +
				"<br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/zone-home/" + zoneId + "\">" + zoneName + "</a><br>" +
				"<br>" +
				"You may visit the zone and approve the request." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(adminEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send admin request email: Email Address=" + adminEmail + " ZoneId=" + zoneId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendAdminApprovedEmail (String adminEmail, String adminName, String zoneId, String zoneName, String approverId, String approverName) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your Admin Request has been approved for " + zoneName;
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + adminName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + approverId + "\">" + approverName + "</a>" +
				" has approved your admin request for the following zone.<br>" +
				"<br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/zone-home/" + zoneId + "\">" + zoneName + "</a><br>" +
				"<br>" +
				"Congratulations!" +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(adminEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send admin approved email: Email Address=" + adminEmail + " ZoneId=" + zoneId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendAdminRemovedEmail (String adminEmail, String adminName, String zoneId, String zoneName, String removerId, String removerName) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="You have been removed as Admin for " + zoneName;
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + adminName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + removerId + "\">" + removerName + "</a>" +
				" has removed you from being the admin of the following zone.<br>" +
				"<br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/zone-home/" + zoneId + "\">" + zoneName + "</a><br>" +
				"<br>" +
				"You may visit the zone and request to become admin once again." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(adminEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send admin removed email: Email Address=" + adminEmail + " ZoneId=" + zoneId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendContactUsEmail (String contactName, String contactEmail, String contactSubject, String contactMessage, String contactIP) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT=contactSubject;
			String BODY=
				"A Heatbud customer has contacted us from " + contactIP + ".\n" +
				"\n" +
				"Name: " + contactName + "\n" +
				"\n" +
				"Email: " + contactEmail + "\n" +
				"\n" +
				"Message:" + "\n" +
				"\n" +
				contactMessage + "\n";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses("info@heatbud.com", "kk@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withText(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send Contact Us email: Name=" + contactName + " Email Address=" + contactEmail);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

	public void sendTomcatEmail (String errorString, String fileName) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Errors found in " + fileName;
			String BODY= "\n" + errorString + "\n";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withText(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send " + fileName + " email.");
			logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

	public void sendPostDeletedEmail (String bloggerEmail, String bloggerName, String zoneName, String postId, String postTitle, String postSummary, String postContent, String addlMessage) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT=postTitle + " : has been deleted from Heatbud.";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + bloggerName + "!<br>" +
				"<br>" +
				"We wanted to let you know that your post has been deleted from Heatbud.<br>" +
				"<br>";
			if ( StringUtils.equals(addlMessage, "addlMessage") ) {
				BODY = BODY +
					"Possible reasons for deletion:<br>" +
					"1. Objectionable content.<br>" +
					"2. Duplicate content from another post on Heatbud.<br>" +
					"3. Aimed at developing backlinks without providing any useful information to the reader.<br>" +
					"4. Posted in a wrong zone.<br>";
			} else {
				BODY = BODY +
					"Reason for deletion: " + addlMessage + "<br>";
			}
			BODY = BODY +
				"<br>" +
				"Please visit our Help Center at https://www.heatbud.com/do/help before posting your next post." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"For your information, here are your post details:<br>" +
				"<br>" +
				"ZONE NAME:<br>" +
				zoneName +
				"<br><br>" +
				"POST TITLE:<br>" +
				postTitle +
				"<br><br>" +
				"SUMMARY:<br>" +
				postSummary +
				"<br><br>" +
				"CONTENT:<br>" +
				postContent +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(bloggerEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send PostDeleted email: Email Address=" + bloggerEmail + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendPostRequestFBEmail (String postId, String postTitle, String ipAddress) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT=postTitle + " : requested to be featured on the Facebook page";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello Customer Service!<br>" +
				"<br>" +
				"We have received a request to feature the following post on our Facebook page.<br>" +
				"<br></div>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postTitle + "</a><br/>" +
				"<br>" +
				"Approve or reject this request on the admin page.<br>" +
				"<br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"PS: Our computer claims that this request has been originated from " + ipAddress +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send Post RequestFB email: PostId=" + postId + " IP Address=" + ipAddress);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

	public void sendApproveFBEmail (String bloggerEmail, String bloggerName, String postId, String postTitle, String addlMessage) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT=postTitle + " : has been featured in the Heatbud page on Facebook";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + bloggerName + "!<br>" +
				"<br>" +
				"Congratulations! We wanted to let you know that " +
				"your post has been featured in our Facebook page.<br>" +
				"<br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.facebook.com/heatbud\">https://www.facebook.com/heatbud</a><br>" +
				"<br>" +
				"You may visit our Facebook page and share the post with your friends or followers.<br/>";
			if ( !StringUtils.equals(addlMessage, "addlMessage") ) {
				BODY = BODY + "<p>" + addlMessage + "</p><br/>";
			}
			BODY = BODY +
				"<br/>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(bloggerEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send aproveFB email: Email Address=" + bloggerEmail + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendRejectFBEmail (String bloggerEmail, String bloggerName, String postId, String postTitle, String addlMessage) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT=postTitle + " : has not been featured in our page on Facebook";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + bloggerName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " +
				"your post has not been featured in our Facebook page.<br>" +
				"<br>" +
				"Possible reasons for rejection:<br>" +
				"1. Your post lacks originality.<br>" +
				"2. Your post is focussed on a marketing material.<br>" +
				"3. Objectionable content.<br>";
			if ( !StringUtils.equals(addlMessage, "addlMessage") ) {
				BODY = BODY + "<br>" + addlMessage;
			}
			BODY = BODY +
				"<br>" +
				"Our decision is final and sorry for the inconvinience." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(bloggerEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send rejectFB email: Email Address=" + bloggerEmail + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendForgotPasswordEmail (String firstName, String username, String salt, String ipAddress) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Heatbud Reset Password";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + firstName + "!<br>" +
				"<br>" +
				"We're missing you at Heatbud!  Please click on the link below to reset your password.<br>" +
				"<br></div>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/reset-password/" + salt + "/" + username + "\">Reset Password</a>" +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"If you didn't request to have your password reset, let us know immediately at info@heatbud.com.  Rest assured, your account information is safe with us.<br>"+
				"<br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"PS: Our computer claims that your forgot password request has been originated from " + ipAddress +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send Forgot Password email: Last Name=" + firstName + " Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendNewCommentEmail (String followerId, String followerEmail, String followerFirstName, String postId, String postTitle, String commenterId, String commenterName, String origCommentText, String commentText) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="New comment on " + postTitle;
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + followerFirstName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + commenterId + "\">" + commenterName + "</a>" +
				" has posted a comment on the post that you've been following on Heatbud.<br>" +
				"<br></div>" +
				"<div style=\"font-size:1.3em;font-style:italic;color:#333333\">" +
				"\"<pre>" + commentText + "</pre>\"<br>" +
				"</div>";
			if ( StringUtils.isNotBlank(origCommentText) ) {
				BODY = BODY +
					"<div style=\"font-size:1.1em;color:#333333\">in response to the comment<br></div>" +
					"<div style=\"font-size:1.3em;font-style:italic;color:#333333\"><br>" +
					"<pre>\"" + origCommentText + "\"</pre><br>" +
					"</div>";
			}
			BODY = BODY +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"To reply, visit the post " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postTitle + "</a>. " +
				"If you're the author of this post, you will be allowed to thank the commenter or delete the comment." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"To stop receiving notifications on this post, " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/unfollow-comments/" + postId + "/" + followerId + "\">click here</a>.<br>" +
				"<br>" +
				"To stop receiving notifications on all posts, visit <a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/user/notifications\">Notifications</a> page on Heatbud, or<br>" +
				"Write to : Heatbud LLC, Attention: Customer Service, PO Box 731, Bothell, WA 98041-0731" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(followerEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send new comment email: Email Address=" + followerEmail + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendConfirmCommentEmail (String firstName, String username, String salt, String ipAddress, String unconfirmedCommentId, String postId, String postTitle, String commentText) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your comment is waiting for your confirmation";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + firstName + "!<br>" +
				"<br>" +
				"Since you are not a registered user at Heatbud, we need you to click on the link below and confirm the comment before we can publish it.<br>" +
				"</div><br/><div style=\"font-size:1.1em;color:#666666\">" +
				"POST TITLE: " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postTitle + "</a>" +
				"<br>COMMENT: " + commentText +
				"<br><br></div>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/confirm-comment/" + unconfirmedCommentId + "\">Confirm Comment</a>" +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"<br>" +
				"Registered users need not confirm their comments every time. You may create a Heatbud account simply by choosing a password.<br>" +
				"<br></div>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/reset-password/" + salt + "/" + username + "\">Choose Password</a>" +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"<br>" +
				"If you have any questions, please contact us at https://www.heatbud.com/do/contact.<br>" +
				"<br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"PS: Our computer claims that this comment has been posted from " + ipAddress +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(username).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send verification email: Email Address=" + username + " Verification Code=" + salt);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendCommentThankedEmail (String commenterEmail, String commenterFirstName, String postId, String postTitle, String bloggerId, String bloggerName) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your comment on " + postTitle + " has been thanked";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + commenterFirstName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + bloggerId + "\">" + bloggerName + "</a>" +
				" has thanked your comment on the following post.<br>" +
				"<br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postTitle + "</a><br>" +
				"<br>" +
				"Now you can see a cool thanks image next to your comment." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"Don't want to receive email when your comment has been thanked? Visit <a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/user/notifications\">Notifications</a> page on Heatbud.<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(commenterEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send comment thanked email: Email Address=" + commenterEmail + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendCommentReportedEmail (String commenterEmail, String commenterFirstName, String postId, String postTitle, String commentText) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your comment on " + postTitle + " has been flagged for removal";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + commenterFirstName + "!<br>" +
				"<br>" +
				"We wanted to let you know that your comment on the following post has been flagged as inappropriate and scheduled for deletion.<br>" +
				"<br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postTitle + "</a><br>" +
				"<br>" +
				"Comment Text :<br></div>" +
				"<div style=\"font-size:1.3em;font-style:italic;color:#333333\">" +
				"\"<pre>" + commentText + "</pre>\"<br>" +
				"</div>" +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"Comments \"must\" be relevant to the post content. If you have something to express to the Heatbud community, why not write your own post? Just follow these simple " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/do/help/main/bloggers\">guidelines</a>.<br>" +
				"<br>" +
				"If you have questions, feel free to " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/do/contact\">contact us</a> anytime.<br>" +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(commenterEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send comment removed email: Email Address=" + commenterEmail + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendReportCommentEmail (String postId, String commentDate, String userId, String commentText) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Spam comment has been reported";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Heatbud user " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + userId + "\">" + userId + "</a>" +
				" has reported a spam comment.<br>" +
				"<br>" +
				"PostId: " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postId + "</a><br>" +
				"<br>" +
				"Comment Date: " + commentDate + "<br>" +
				"<br>" +
				"Comment Text: <br>" +
				"<div style=\"font-size:1.3em;font-style:italic;color:#333333\">" +
				"\"<pre>" + commentText + "</pre>\"<br>" +
				"</div>" +
				"<br></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send Report Comment email: postId=" + postId + " Comment Date=" + commentDate);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}

	}

	public void sendEmail (String fromId, String fromEmail, String fromName,
		String toEmail, String toName, String personalMessage, String ipAddress) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM = fromName + " via Heatbud <info@heatbud.com>";
			String SUBJECT="Hello from " + fromId;
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hi " + toName + "!<br>" +
				"<br>" +
				"We wanted to let you know that " + fromName + " has sent you a personal message on Heatbud.<br>" +
				"<br></div>" +
				"<div style=\"font-size:1.3em;font-style:italic;color:#333333\">" +
				"\"<pre>" + personalMessage + "</pre>\"<br>" +
				"</div>" +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"If you wish to respond, you can reply to this email or visit " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + fromId + "\">" + fromName + "</a> on Heatbud." +
				" If you don't want others to contact you, click \"Disable Email\" on your " +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/user/profile\">Profile</a> page on Heatbud." +
				"<br><br>" +
				"Sincerely,<br>" +
				"Heatbud LLC<br>" +
				"<br>" +
				"Note from Heatbud LLC:<br>" +
				"This message has been sent from " + ipAddress + ".<br>" +
				"If you think this message is spam, please forward this email to info@heatbud.com, or<br>" +
				"report to us at https://www.heatbud.com/do/contact, or<br>" +
				"write to us at Heatbud LLC, Attention: Customer Service, PO Box 731, Bothell, WA 98041-0731";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(toEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest()
	        	.withSource(FROM)
	        	.withReplyToAddresses(fromEmail)
	        	.withDestination(destination)
	        	.withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send email me: Email Address=" + toEmail + " FromId=" + fromId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendCSEmail (String emailAddress, String emailSubject, String personalMessage) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT=emailSubject;
			String BODY=
				"<div style=\"font-size:1.3em;font-style:italic;color:#333333\">" +
				"<pre>" + personalMessage + "</pre><br>" +
				"</div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(emailAddress).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send CS email: Email Address=" + emailAddress);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public String sendPostEmail (String fromUserId, String fromEmail, String fromName, String toEmailAddress, String postId, String postTitle, String personalMessage, String ipAddress) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM = fromName + " via Heatbud <info@heatbud.com>";
			String SUBJECT = "Post: " + postTitle;
			String BODY =
				"<div style=\"font-size:1.1em;color:#333333\">Hello!<br>" +
				"<br>" +
				"I thought you might be interested in reading the following post on Heatbud.<br><br>" +
				"<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/post/" + postId + "\">" + postTitle + "</a>" +
				"</div>";
			if ( StringUtils.isNotBlank(personalMessage) ) {
				BODY = BODY +
					"<div style=\"font-size:1.3em;font-style:italic;color:#333333\"><br>" +
					"<pre>\"" + personalMessage + "\"</pre><br>" +
					"</div>";
			}
			BODY = BODY +
				"<div style=\"font-size:1.1em;color:#333333\"><br>" +
				"Let me know if you liked it.<br><br>Sincerely,<br>";
			if ( StringUtils.isBlank(fromUserId) ) {
				BODY = BODY + fromName;
			} else {
				BODY = BODY + "<a style=\"font-size:1.1em\" href=\"https://www.heatbud.com/" + fromUserId + "\">" + fromName + "</a>";
			}
			BODY = BODY + "<br><br>" +
				"</div><div style=\"font-size:1em;color:#333333\">Note from Heatbud LLC:<br>" +
				"This message has been sent from " + ipAddress + ". If you think this is a spam, please forward " +
				"this email to info@heatbud.com, or report to us at https://www.heatbud.com/do/contact." +
				"<br></div>";

			// Convert email addresses to a list
			List<String> toEmailAddressList = Arrays.asList(toEmailAddress.split("\\s*,\\s*"));

			// Validate Email Addresses
			for (int i = 0; i < toEmailAddressList.size(); i++) {
				String retMessage = common.validateEmailAddress(toEmailAddressList.get(i));
				if ( retMessage != "SUCCESS" ) {
					return "One of the recipient's email address is not valid. " + " - " + toEmailAddressList.get(i);
				}
			}

	        // Construct an object to contain the recipient addresses (we convert comma separated emailAddress into a List).
	        Destination destination = new Destination().withToAddresses(toEmailAddressList).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message).withReplyToAddresses(fromEmail);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send post email: From UserId=" + fromUserId + " To Email Address=" + toEmailAddress + " PostId=" + postId);
			logger.log(Level.SEVERE,e.getMessage(),e);
	        return "Problem sending email.";
		}
        return "Email has been succefully sent.";
	}

	public void sendBuyerOrderEmail (String buyerEmail, String buyerName, String orderId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="You placed a blog post order";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + buyerName + "!<br/>" +
				"<br/>" +
				"Thanks for placing the blog post order at Heatbud.<br/>" +
				"<br/>" +
				"You can track your order at https://www.heatbud.com/user/order/" + orderId + ".<br/>" +
				"<br/>" +
				"Sincerely,<br/>" +
				"Heatbud LLC<br/>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(buyerEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send buyer email: Email Address=" + buyerEmail + " OrderId=" + orderId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendBloggerOrderEmail (String bloggerEmail, String buyerId, String bloggerName, String orderId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="You received a new blog post order!";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + bloggerName + "!<br/>" +
				"<br/>" +
				buyerId + " has placed a blog post order for you at Heatbud!<br/>" +
				"<br/>" +
				"You can track your order at https://www.heatbud.com/user/order/" + orderId + ".<br/>" +
				"<br/>" +
				"Please make sure to publish the blog post within the timeframe and request buyer for a review right away. You may put comments in the order page to communicate with the buyer.<br/>" +
				"<br/>" +
				"Sincerely,<br/>" +
				"Heatbud LLC<br/>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(bloggerEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send blogger email: Email Address=" + bloggerEmail + " OrderId=" + orderId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendOrderCommentEmail (String toEmail, String toUser, String commentBy, String commentText, String orderId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Comment on your blog post order";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + toUser + "!<br/>" +
				"<br/>" +
				commentBy + " has placed a comment on your blog post order at Heatbud!" +
				"<div style=\"font-size:1.3em;font-style:italic;color:#333333\"><br/>" +
				"<pre>\"" + commentText + "\"</pre><br>" +
				"</div>" +
				"<br/>" +
				"You may respond or track your order at https://www.heatbud.com/user/order/" + orderId + ".<br/>" +
				"<br/>" +
				"Sincerely,<br/>" +
				"Heatbud LLC<br/>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(toEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send order comment email: Email Address=" + toEmail + " OrderId=" + orderId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendOrderReviewEmail (String toEmail, String toUser, String bloggerId, String orderId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your blog post has been published";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + toUser + ",<br/>" +
				"<br/>" +
				"Good news! " + bloggerId + " has published the blog post and requsted a review from you.<br/>" +
				"<br/>" +
				"You may verify the blog post on your Heatbud page and close the order at https://www.heatbud.com/user/order/" + orderId + ".<br/>" +
				"<br/>" +
				"Order will automatically close if you don't take any action in three days.<br/>" +
				"<br/>" +
				"Sincerely,<br/>" +
				"Heatbud LLC<br/>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(toEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send order review email: Email Address=" + toEmail + " OrderId=" + orderId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendOrderCloseEmail (String toEmail, String toUser, String buyerId, String orderId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your Blog Post has been accepted.";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + toUser + ",<br/>" +
				"<br/>" +
				"Good news! " + buyerId + " has accepted your blog post and closed the below order.<br/>" +
				"<br/>" +
				"https://www.heatbud.com/user/order/" + orderId + "<br/>" +
				"<br/>" +
				"You will receive payment via paypal in a separate email.<br/>" +
				"<br/>" +
				"Sincerely,<br/>" +
				"Heatbud LLC<br/>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(toEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send order close email: Email Address=" + toEmail + " OrderId=" + orderId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendOrderCancelEmail (String toEmail, String toUser, String fromId, String orderId) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="Your Blog Post Order has been canceled.";
			String BODY=
				"<div style=\"font-size:1.1em;color:#333333\">Hello " + toUser + ",<br/>" +
				"<br/>" +
				"We wanted to let you know that " + fromId + " has canceled your blog post order.<br/>" +
				"<br/>" +
				"https://www.heatbud.com/user/order/" + orderId + "<br/>" +
				"<br/>" +
				"Please contact " + fromId + " if you have any questions.<br/>" +
				"<br/>" +
				"https://www.heatbud.com/" + fromId + "<br/>" +
				"<br/>" +
				"Sincerely,<br/>" +
				"Heatbud LLC<br/>" +
				"<br/></div>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses(toEmail).withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send order cancel email: Email Address=" + toEmail + " OrderId=" + orderId);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

	public void sendNewsLetterEmail (String username, String firstName, String featuredPostId, String postHeadshot, String postBloggerName, String postSummary) {

		try {
			// Define FROM, SUBJECT and BODY of the message
			String FROM="Heatbud <info@heatbud.com>";
			String SUBJECT="What's hot at Heatbud this week?";
			String BODY=
				"<table width=\"100%\" style=\"background-color:rgb(248, 250, 252); color:#333333\"><tr><td>" +
					"<div style=\"max-width:600px; margin-top:0; margin-bottom:0; margin-right:auto; margin-left:auto; padding-left:20px; padding-right:20px\">" +
						"<table style=\"border-spacing:0; font-family:sans-serif; width:100%; max-width:600px\">" +
						"<tr><td style=\"background-color:#61cf81; width:100%; font-size:16px; line-height:22px; font-weight:bold; font-family:Arial,sans-serif; padding-top:20px; padding-bottom:20px\">" +
							"<img alt=\"Heatbud logo\" style=\"width:140px; padding-top:2px; margin-left:20px; border:none\" src=\"https://www.heatbud.com/resources/images/heatbud-logo.png\">" +
							"<span>Heatbud Newsletter for the week of Feb 6th, 2017</span>" +
						"</td></tr>" +
						"<tr><td style=\"width:100%; font-size:12px; font-family:Arial,sans-serif; padding-top:20px; padding-bottom:20px; border-bottom:1px solid #61cf81\">" +
							"Dear " + firstName + ",<br/>" +
							"<br/>" +
							"Heatbud is the first social blogging website that welcomes blog posts from individuals as well as businesses. Find a zone for your niche and start blogging instantly. Or, create a new zone.<br/>" +
						"</td></tr>" +
						"<tr><td style=\"width:100%; font-size:12px; font-family:Arial,sans-serif; padding-top:20px; padding-bottom:20px; border-bottom:1px solid #61cf81\">" +
							"<span style=\"font-size:16px; font-weight:bold\">Featured Blog Post</span><br/>" +
							"<br/>" +
							"Heatbud is the first social blogging website that welcomes blog posts from individuals as well as businesses. Find a zone for your niche and start blogging instantly. Or, create a new zone.<br/>" +
						"</td></tr>" +
						"<br></div>" +
						"<div style=\"font-size:1.1em;color:#333333\"><br>" +
						"<br>" +
						"If you have any questions, please contact us at https://www.heatbud.com/do/contact.<br>"+
						"<br>" +
						"Sincerely,<br>" +
						"Heatbud LLC<br>" +
						"<br>" +
					"</div>" +
				"</td></tr></table>";

	        // Construct an object to contain the recipient address.
	        Destination destination = new Destination().withToAddresses("kakatur@gmail.com").withBccAddresses("production@heatbud.com");

	        // Create a message with the specified subject and body.
	        Content subject = new Content().withData(SUBJECT);
	        Body body = new Body().withHtml(new Content().withData(BODY));
	        Message message = new Message().withSubject(subject).withBody(body);

	        // Assemble and send the email.
	        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
	        sesClient.sendEmail(request);

		} catch (Exception e) {
			logger.log(Level.SEVERE,"Unable to send newsletter email: Email Address=" + username);
			logger.log(Level.SEVERE,e.getMessage(),e);
		}
	}

}
