/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

/**
 * Contains data for Ranking.
 */
public class Ranking {

	private String zoneId;
	private String postId;
	private long postHi;

	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}

	public long getPostHi() {
		return postHi;
	}
	public void setPostHi(long postHi) {
		this.postHi = postHi;
	}

}
