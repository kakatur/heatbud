/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.heatbud.aws.HeatbudDynamoDBUtil;
import com.heatbud.entity.Post;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;

/**
 * This View is implemented as a Bean for RSS feeds.
 */
public class HeatbudRssView extends AbstractRssFeedView {

	// DynamoDB client
    @Autowired
	private HeatbudDynamoDBUtil dao;

	@Override
	protected void buildFeedMetadata (Map<String, Object> model, Channel channel, HttpServletRequest request) {

		channel.setEncoding("UTF-8");
		channel.setTitle("Heatbud - Free Social Blogging site");
		channel.setLink("https://www.heatbud.com");
		channel.setDescription("Ever seen Social Blogging in action? Experience yourself today.");
		channel.setWebMaster("info@heatbud.com");

		// image
		Image image = new Image();
		image.setUrl("https://www.heatbud.com/resources/images/heatbud.png");
		image.setTitle("Heatbud - Free Social Blogging site");
		image.setDescription("Heatbud - Free Social Blogging site");
		image.setLink("https://www.heatbud.com");
		channel.setImage(image);

		// published date
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(dao.getAttribute("T", "generateTopChartsJobPeriod"))*21600000);
		channel.setPubDate(calendar.getTime());

		super.buildFeedMetadata(model, channel, request);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		@SuppressWarnings("unchecked")
		List<Post> feedContent = (List<Post>) model.get("feedContent");
		List<Item> items = new ArrayList<Item>(feedContent.size());
		response.setCharacterEncoding("UTF-8");

		for(Post post : feedContent) {

			Item item = new Item();

			// post title
			item.setTitle(post.getPostTitle());

			// post url
			item.setLink("https://www.heatbud.com/post/"+post.getPostId());

			// post permalink (same as link in our case)
			Guid guid = new Guid();
			guid.setValue("https://www.heatbud.com/post/"+post.getPostId());
			guid.setPermaLink(true);
			item.setGuid(guid);

			// description (post summary)
			Description description = new Description();
			description.setValue(post.getPostSummary());
			item.setDescription(description);

			// published date
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(post.getUpdateDate());
			item.setPubDate(calendar.getTime());

			items.add(item);
		}

 		return items;
	}

}
