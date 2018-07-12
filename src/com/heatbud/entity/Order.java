/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Holds Order information.
 */
@DynamoDBTable(tableName = "Orders")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private String orderId;
	private long orderDate;
	private String pageId;
	private String pageName;
	private String bloggerId;
	private String bloggerName;
	private String buyerId;
	private String buyerName;
	private String postType;
	private long deliveryDays;
	private long price;
	private long publishDate = 0;
	private long closeDate = 0;
	private long cancelDate = 0;
	private String stripeChargeId = "";

	@DynamoDBHashKey(attributeName="OrderId")
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@DynamoDBAttribute(attributeName="OrderDate")
	public long getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(long orderDate) {
		this.orderDate = orderDate;
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

	@DynamoDBAttribute(attributeName="BuyerId")
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	@DynamoDBAttribute(attributeName="BuyerName")
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	@DynamoDBAttribute(attributeName="PostType")
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}

	@DynamoDBAttribute(attributeName="DeliveryDays")
	public long getDeliveryDays() {
		return deliveryDays;
	}
	public void setDeliveryDays(long deliveryDays) {
		this.deliveryDays = deliveryDays;
	}

	@DynamoDBAttribute(attributeName="Price")
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}

	@DynamoDBAttribute(attributeName="PublishDate")
	public long getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(long publishDate) {
		this.publishDate = publishDate;
	}

	@DynamoDBAttribute(attributeName="CloseDate")
	public long getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(long closeDate) {
		this.closeDate = closeDate;
	}

	@DynamoDBAttribute(attributeName="CancelDate")
	public long getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(long cancelDate) {
		this.cancelDate = cancelDate;
	}

	@DynamoDBAttribute(attributeName="StripeChargeId")
	public String getStripeChargeId() {
		return stripeChargeId;
	}
	public void setStripeChargeId(String stripeChargeId) {
		this.stripeChargeId = stripeChargeId;
	}

}
