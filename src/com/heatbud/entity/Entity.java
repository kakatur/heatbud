/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Holds Entity information. An Entity is either a Blogger or a Page.
 */
@DynamoDBTable(tableName = "Entities")
public class Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String entityId;
	private String entityType;
	private String entityName;
	private String entityEmail;
	private String website;
	private String about = " ";
	private String passion;
	private String achievements;
	private String announcements;
	private String contact;
	private String timeZone;
	private String logo;
	private String phone;
	private String address;
	private String country = "";
	private String state;
	private String city;
	private String contactColor;
	private String profileColor;
	private String profilePhoto;
	private String profileBG;
	private String enableEmail;
	private String fbId;
	private String googleId;
	private String primaryPageId;
	private String productType;
	private long endDate = 0;
	private long createDate;
	private long posts;
	private long votes;
	private long comments;
	private long hi;
	private long hiTrending;	// not stored in database

	@DynamoDBHashKey(attributeName="EntityId")
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@DynamoDBAttribute(attributeName="EntityType")
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	@DynamoDBAttribute(attributeName="EntityName")
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	@DynamoDBAttribute(attributeName="EntityEmail")
	public String getEntityEmail() {
		return entityEmail;
	}
	public void setEntityEmail(String entityEmail) {
		this.entityEmail = entityEmail;
	}

	@DynamoDBAttribute(attributeName="Website")
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}

	@DynamoDBAttribute(attributeName="About")
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}

	@DynamoDBAttribute(attributeName="Passion")
	public String getPassion() {
		return passion;
	}
	public void setPassion(String passion) {
		this.passion = passion;
	}

	@DynamoDBAttribute(attributeName="Achievements")
	public String getAchievements() {
		return achievements;
	}
	public void setAchievements(String achievements) {
		this.achievements = achievements;
	}

	@DynamoDBAttribute(attributeName="Announcements")
	public String getAnnouncements() {
		return announcements;
	}
	public void setAnnouncements(String announcements) {
		this.announcements = announcements;
	}

	@DynamoDBAttribute(attributeName="Contact")
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}

	@DynamoDBAttribute(attributeName="TimeZone")
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@DynamoDBAttribute(attributeName="ProfilePhoto")
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	@DynamoDBAttribute(attributeName="Logo")
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}

	@DynamoDBAttribute(attributeName="Phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@DynamoDBAttribute(attributeName="Address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@DynamoDBAttribute(attributeName="Country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	@DynamoDBAttribute(attributeName="State")
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	@DynamoDBAttribute(attributeName="City")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	@DynamoDBAttribute(attributeName="ContactColor")
	public String getContactColor() {
		return contactColor;
	}
	public void setContactColor(String contactColor) {
		this.contactColor = contactColor;
	}

	@DynamoDBAttribute(attributeName="ProfileColor")
	public String getProfileColor() {
		return profileColor;
	}
	public void setProfileColor(String profileColor) {
		this.profileColor = profileColor;
	}

	@DynamoDBAttribute(attributeName="ProfileBG")
	public String getProfileBG() {
		return profileBG;
	}
	public void setProfileBG(String profileBG) {
		this.profileBG = profileBG;
	}

	@DynamoDBAttribute(attributeName="EnableEmail")
	public String getEnableEmail() {
		return enableEmail;
	}
	public void setEnableEmail(String enableEmail) {
		this.enableEmail = enableEmail;
	}

	@DynamoDBAttribute(attributeName="FbId")
	public String getFbId() {
		return fbId;
	}
	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	@DynamoDBAttribute(attributeName="GoogleId")
	public String getGoogleId() {
		return googleId;
	}
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	@DynamoDBAttribute(attributeName="PrimaryPageId")
	public String getPrimaryPageId() {
		return primaryPageId;
	}
	public void setPrimaryPageId(String primaryPageId) {
		this.primaryPageId = primaryPageId;
	}

	@DynamoDBAttribute(attributeName="ProductType")
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}

	@DynamoDBAttribute(attributeName="EndDate")
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	@DynamoDBAttribute(attributeName="CreateDate")
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	@DynamoDBAttribute(attributeName="Posts")
	public long getPosts() {
		return posts;
	}
	public void setPosts(long posts) {
		this.posts = posts;
	}

	@DynamoDBAttribute(attributeName="Votes")
	public long getVotes() {
		return votes;
	}
	public void setVotes(long votes) {
		this.votes = votes;
	}

	@DynamoDBAttribute(attributeName="Comments")
	public long getComments() {
		return comments;
	}
	public void setComments(long comments) {
		this.comments = comments;
	}

	@DynamoDBAttribute(attributeName="HI")
	public long getHi() {
		return hi;
	}
	public void setHi(long hi) {
		this.hi = hi;
	}

	@DynamoDBIgnore
	public long getHiTrending() {
		return hiTrending;
	}
	public void setHiTrending(long hiTrending) {
		this.hiTrending = hiTrending;
	}

}
