/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

/**
 * Holds Marketplace Pricing information.
 */
public class MarketPricing {

	private String bloggerId;
	private long position;
	private String name;
	private String country = " ";
	private String about;
	private String tags = " ";
	private String postType;
	private long deliveryDays;
	private long price;

	public String getBloggerId() {
		return bloggerId;
	}
	public void setBloggerId(String bloggerId) {
		this.bloggerId = bloggerId;
	}

	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}

	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}

	public long getDeliveryDays() {
		return deliveryDays;
	}
	public void setDeliveryDays(long deliveryDays) {
		this.deliveryDays = deliveryDays;
	}

	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}

	public boolean equals(Object obj) {
       if (obj instanceof MarketPricing) {
    	   MarketPricing mp = (MarketPricing) obj;
    	   return (mp.bloggerId.equals(this.bloggerId) && mp.position == this.position);
        } else {
        	return false;
        }
	}

	public int hashCode(){
		int hashcode = (int) position * 20;
		hashcode += bloggerId.hashCode();
		return hashcode;
    }

}
