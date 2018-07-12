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
@DynamoDBTable(tableName = "OrderProgress")
public class OrderProgress implements Serializable {

	private static final long serialVersionUID = 1L;

	private String orderId;
	private long stepDate;
	private String stepType;
	private String commentBy;
	private String commentText;
	private String bloggerId;
	private String buyerId;

	@DynamoDBHashKey(attributeName="OrderId")
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@DynamoDBAttribute(attributeName="StepDate")
	public long getStepDate() {
		return stepDate;
	}
	public void setStepDate(long stepDate) {
		this.stepDate = stepDate;
	}

	@DynamoDBAttribute(attributeName="StepType")
	public String getStepType() {
		return stepType;
	}
	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	@DynamoDBAttribute(attributeName="CommentBy")
	public String getCommentBy() {
		return commentBy;
	}
	public void setCommentBy(String commentBy) {
		this.commentBy = commentBy;
	}

	@DynamoDBAttribute(attributeName="CommentText")
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	@DynamoDBAttribute(attributeName="BloggerId")
	public String getBloggerId() {
		return bloggerId;
	}
	public void setBloggerId(String bloggerId) {
		this.bloggerId = bloggerId;
	}

	@DynamoDBAttribute(attributeName="BuyerId")
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

}
