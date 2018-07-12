/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

import java.io.Serializable;

/**
 * Object for receiving SESMessage through SQS/ SNS.
 */
public class SESMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private String notificationType;
	private String bounce;
	private String mail;

	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getBounce() {
		return bounce;
	}
	public void setBounce(String bounce) {
		this.bounce = bounce;
	}

	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

}
