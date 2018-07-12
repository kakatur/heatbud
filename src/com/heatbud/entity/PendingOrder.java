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
 * Holds Pending Order information.
 */
@DynamoDBTable(tableName = "PendingOrders")
public class PendingOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	private String orderHandler;
	private long handlerDate;
	private String createdBy;
	private String pageId;
	private String bloggerId;
	private String postType;
	private long deliveryDays;
	private long price;
	private String status = "PENDING";

	@DynamoDBHashKey(attributeName="OrderHandler")
	public String getOrderHandler() {
		return orderHandler;
	}
	public void setOrderHandler(String orderHandler) {
		this.orderHandler = orderHandler;
	}

	@DynamoDBAttribute(attributeName="HandlerDate")
	public long getHandlerDate() {
		return handlerDate;
	}
	public void setHandlerDate(long handlerDate) {
		this.handlerDate = handlerDate;
	}

	@DynamoDBAttribute(attributeName="CreatedBy")
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@DynamoDBAttribute(attributeName="PageId")
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	@DynamoDBAttribute(attributeName="BloggerId")
	public String getBloggerId() {
		return bloggerId;
	}
	public void setBloggerId(String bloggerId) {
		this.bloggerId = bloggerId;
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

	@DynamoDBAttribute(attributeName="Status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
