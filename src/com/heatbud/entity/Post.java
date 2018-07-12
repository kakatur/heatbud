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
 * Holds metadata information for a post. Actual post content is stored in S3.
 */
@DynamoDBTable(tableName = "Posts")
public class Post implements Serializable {

	private static final long serialVersionUID = 1L;

	private String postId;
	private String postTitle;
	private String tags;
	private String postHeadshot;
	private String postSummary;
	private String zoneId;
	private String zoneName;
	private String pageId = " ";
	private String pageName = " ";
	private long updateDate; // Milliseconds since Epoch
	private String bloggerId;
	private String bloggerName;
	private String publishFlag;
	private String requestFB;
	private Integer upVotes;
	private Integer downVotes;
	private Integer comments;
	private long views;
	private long hi;
	private long rpcPeriod = 0;
	private long hiTrending;	// not stored in database

	@DynamoDBHashKey(attributeName="PostId")
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}

	@DynamoDBAttribute(attributeName="PostTitle")
	public String getPostTitle() {
		return postTitle;
	}
	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	@DynamoDBAttribute(attributeName="Tags")
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}

	@DynamoDBAttribute(attributeName="PostHeadshot")
	public String getPostHeadshot() {
		return postHeadshot;
	}
	public void setPostHeadshot(String postHeadshot) {
		this.postHeadshot = postHeadshot;
	}

	@DynamoDBAttribute(attributeName="PostSummary")
	public String getPostSummary() {
		return postSummary;
	}
	public void setPostSummary(String postSummary) {
		this.postSummary = postSummary;
	}

	@DynamoDBAttribute(attributeName="ZoneId")
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	@DynamoDBAttribute(attributeName="ZoneName")
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	@DynamoDBAttribute(attributeName="PageId")
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	@DynamoDBAttribute(attributeName="PageName")
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	@DynamoDBAttribute(attributeName="UpdateDate")
	public long getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}

	@DynamoDBAttribute(attributeName="BloggerId")
	public String getBloggerId() {
		return bloggerId;
	}
	public void setBloggerId(String bloggerId) {
		this.bloggerId = bloggerId;
	}

	@DynamoDBAttribute(attributeName="BloggerName")
	public String getBloggerName() {
		return bloggerName;
	}
	public void setBloggerName(String bloggerName) {
		this.bloggerName = bloggerName;
	}

	@DynamoDBAttribute(attributeName="PublishFlag")
	public String getPublishFlag() {
		return publishFlag;
	}
	public void setPublishFlag(String publishFlag) {
		this.publishFlag = publishFlag;
	}

	@DynamoDBAttribute(attributeName="RequestFB")
	public String getRequestFB() {
		return requestFB;
	}
	public void setRequestFB(String requestFB) {
		this.requestFB = requestFB;
	}

	@DynamoDBAttribute(attributeName="UpVotes")
	public Integer getUpVotes() {
		return upVotes;
	}
	public void setUpVotes(Integer upVotes) {
		this.upVotes = upVotes;
	}

	@DynamoDBAttribute(attributeName="DownVotes")
	public Integer getDownVotes() {
		return downVotes;
	}
	public void setDownVotes(Integer downVotes) {
		this.downVotes = downVotes;
	}

	@DynamoDBAttribute(attributeName="Comments")
	public Integer getComments() {
		return comments;
	}
	public void setComments(Integer comments) {
		this.comments = comments;
	}

	@DynamoDBAttribute(attributeName="Views")
	public long getViews() {
		return views;
	}
	public void setViews(long views) {
		this.views = views;
	}

	@DynamoDBAttribute(attributeName="HI")
	public long getHi() {
		return hi;
	}
	public void setHi(long hi) {
		this.hi = hi;
	}

	@DynamoDBAttribute(attributeName="RPCPeriod")
	public long getRpcPeriod() {
		return rpcPeriod;
	}
	public void setRpcPeriod(long rpcPeriod) {
		this.rpcPeriod = rpcPeriod;
	}

	@DynamoDBIgnore
	public long getHiTrending() {
		return hiTrending;
	}
	public void setHiTrending(long hiTrending) {
		this.hiTrending = hiTrending;
	}
}
