/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

/**
 * Contains data for Ticker.
 */
public class Ticker {

	private long tickerTime;
	private String tickerDesc;

	public long getTickerTime() {
		return tickerTime;
	}
	public void setTickerTime(long tickerTime) {
		this.tickerTime = tickerTime;
	}

	public String getTickerDesc() {
		return tickerDesc;
	}
	public void setTickerDesc(String tickerDesc) {
		this.tickerDesc = tickerDesc;
	}

}
