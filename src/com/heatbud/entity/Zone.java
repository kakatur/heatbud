/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

import java.util.List;

/**
 * Contains data for Zone.
 */
public class Zone {

	private String userId;
	private String zoneId;
	private long zoneOrder;
	private String zoneName;
	private String zoneDesc;
	private String zoneWho;
	private String zoneHeadshot;
	private long currentRO;
	private long posts;
	private long votes;
	private long comments;
	private List<String> admins;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public long getZoneOrder() {
		return zoneOrder;
	}
	public void setZoneOrder(long zoneOrder) {
		this.zoneOrder = zoneOrder;
	}

	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getZoneDesc() {
		return zoneDesc;
	}
	public void setZoneDesc(String zoneDesc) {
		this.zoneDesc = zoneDesc;
	}

	public String getZoneWho() {
		return zoneWho;
	}
	public void setZoneWho(String zoneWho) {
		this.zoneWho = zoneWho;
	}

	public String getZoneHeadshot() {
		return zoneHeadshot;
	}
	public void setZoneHeadshot(String zoneHeadshot) {
		this.zoneHeadshot = zoneHeadshot;
	}

	public long getCurrentRO() {
		return currentRO;
	}
	public void setCurrentRO(long currentRO) {
		this.currentRO = currentRO;
	}

	public long getPosts() {
		return posts;
	}
	public void setPosts(long posts) {
		this.posts = posts;
	}

	public long getVotes() {
		return votes;
	}
	public void setVotes(long votes) {
		this.votes = votes;
	}

	public long getComments() {
		return comments;
	}
	public void setComments(long comments) {
		this.comments = comments;
	}

	public List<String> getAdmins() {
		return admins;
	}
	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}

}
