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
 * Holds Pending Page Payment information.
 */
@DynamoDBTable(tableName = "PendingPagePayments")
public class PendingPagePayment implements Serializable {

	private static final long serialVersionUID = 2L;

	private String paymentHandler;
	private long handlerDate;
	private String createdBy;
	private String pageId;
	private String productType;
	private long amount;
	private String coupon;
	private long endDate;
	private String status = "NONE";

	@DynamoDBHashKey(attributeName="PaymentHandler")
	public String getPaymentHandler() {
		return paymentHandler;
	}
	public void setPaymentHandler(String paymentHandler) {
		this.paymentHandler = paymentHandler;
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

	@DynamoDBAttribute(attributeName="ProductType")
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}

	@DynamoDBAttribute(attributeName="Amount")
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}

	@DynamoDBAttribute(attributeName="Coupon")
	public String getCoupon() {
		return coupon;
	}
	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	@DynamoDBAttribute(attributeName="EndDate")
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	@DynamoDBAttribute(attributeName="Status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
