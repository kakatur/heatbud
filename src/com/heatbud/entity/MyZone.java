/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

/**
 * Contains data for MyZone.
 */
public class MyZone {

	private String zoneId;
	private String zoneName;
	private long unreadCount;

	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public long getUnreadCount() {
		return unreadCount;
	}
	public void setUnreadCount(long unreadCount) {
		this.unreadCount = unreadCount;
	}

}
