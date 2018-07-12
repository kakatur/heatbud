/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

/**
 * Holds S3 file information. This is different from HeatbudS3Object in that this stores general file details.
 */
public class S3File {

	private String name; // file name
	private long date; // last modified date

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}

}
