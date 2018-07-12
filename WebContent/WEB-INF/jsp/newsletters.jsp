<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
    <meta name="keywords" content="Social Networking, Social Media, blog, blogging, site, website, free blog, personal blog, create blog, new blog, collaboration, macroblogging, tips, ideas, top, free, best"/>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud - Newsletters</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-newsletters-min.js?20180530"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head><body style="overflow-x:hidden">

	<%-- Begin header --%>
	<table class="header"><tr style="width:100%">
		<td style="float:left">
			<a href="/"><img alt="Heatbud logo" style="width:140px; padding-top:2px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/></a>
		</td>
		<td style="float:right; font-size:13px; padding-top:14px; padding-bottom:6px">
			<div style="float:left; margin-right:8px"><a href="/top/posts-trending-now" class="mainSelection">TOP CHARTS</a></div>
			<div style="float:left; margin-right:8px"><a href="/post/singing-bowls-singing-bowls-a-do-it-yourself-method-to-wellness" class="mainSelection">BLOG POSTS</a></div>
			<div style="float:left; margin-right:8px"><a href="/do/search" class="mainSelection">SEARCH</a></div>
			<div style="float:left; margin-right:8px"><a href="/do/help" class="mainSelection">HELP CENTER</a></div>
			<sec:authorize access="!isAuthenticated()">
				<div style="float:left"><a href="/do/login" class="mainSelection">LOGIN / SIGNUP</a></div>
			</sec:authorize>
			<sec:authorize access="isAuthenticated()">
				<div style="float:left; font-size:16px">
					<ul id="nav" style="margin-top:0px; margin-bottom:0px">
						<li>
							<span style="color:#ffffff; letter-spacing:1.5px"><sec:authentication property="principal.firstName"/> <sec:authentication property="principal.lastName"/> <img src="/resources/images/menu_header.png" style="padding-left:5px; height:15px"></span>
							<ul>
								<li><a href="/<sec:authentication property="principal.userId"/>" style="margin-top:10px; padding-top:10px">Profile</a></li>
								<li><a href="/user/settings" style="padding-top:10px">Settings</a></li>
								<li><a href="/user/notifications" style="padding-top:10px">Notifications</a></li>
								<li><a href="/user/pages" style="padding-top:10px">Page Manager</a></li>
								<li><a href="/user/orders" style="padding-top:10px">Orders</a></li>
								<li><a href="/user/images" style="padding-top:10px">Images</a></li>
								<li><a href="/user/posts" style="padding-top:10px">Unpublished Posts</a></li>
								<li><a href="<c:url value="/do/logout"/>" style="padding-top:10px">Logout</a></li>
								<li><a href="/user/drop" style="padding-top:10px; padding-bottom:30px">Drop Account</a></li>
							</ul>
						</li>
					</ul>
				</div>
			</sec:authorize>
		</td>
	</tr></table>
	<div style="clear:both"></div>
	<%-- End header --%>

	<%-- Begin page content --%>
	<table style="width:100%; border-spacing:2px; padding-top:78px"><tr style="width:100%">

	<td style="width:80%; vertical-align:top; padding:0px">

		<%-- Begin RSS Feeds --%>
		<div style="margin-left:30px; margin-top:40px; margin-bottom:50px; font-size:15px; font-weight:bold" class="h1"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">RSS FEEDS</span></div>
		<div style="margin-left:40px; margin-right:60px">

			<div>RSS = Subscription in your hands</div><br/>
			<div>RSS feeds can be subscribed or unsubscribed from your Web Browser, Outlook or your favorite Email Client. No information will be shared with us.</div><br/>
			<div><a target="_blank" href="/do/rss">Click here</a> to start receiving our RSS feeds.</div>

		</div>
		<%-- End RSS Feeds --%>

		<%-- Begin Newsletter Subscribe --%>
		<div style="margin-left:30px; margin-top:40px; margin-bottom:50px; font-size:15px; font-weight:bold" class="h1"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">NEWSLETTER SUBSCRIPTIONS</span></div>
		<div style="margin-left:40px">

			<div>If you have a Heatbud account, <a href="/user/notifications">click here</a> to subscribe/ unsubscribe to the newsletter.</div><br/>
			<div>If you don't have an account, enter your email address below to subscribe to the newsletter.</div><br/>
			<div>
				<input id="emailAddress" type=text style="float:left; border: 2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:400px; border-radius:2px" placeholder="Enter your email address">
				<a id="subscribeButton" style="font-size:15px; background-color:#FF3333; color:white; padding:2px 20px 4px 20px; border-radius:3px" href="javascript:subscribe();">SUBSCRIBE</a>
			</div>
			<div id="subscribeError" class="error" style="margin-left:82px">&nbsp;</div>
			<div>You can easily unsubscribe from the newsletter by clicking the link toward bottom of the emails.</div><br/>

		</div>
		<%-- End Newsletter Subscribe --%>

	</td>

	<td style="width:165px; vertical-align:top; padding:0px">
		<div id="ads" style="margin-left:11px">
			<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
			<ins class="adsbygoogle"
			     style="display:inline-block;width:160px;height:600px"
			     data-ad-client="ca-pub-3344897177583439"
			     data-ad-slot="2200563309">
			</ins>
			<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
		</div>
	</td>

	</tr></table>

	<%-- Begin footer --%>
	<div class="footer">
		<div style="float: right; margin-right: 40px">
			<a href="/top/posts-trending-now">Home</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="/do/help">Help Center</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="/do/privacy">Privacy &amp; Terms</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="/do/partnerships">Partnerships</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="/do/careers">Careers</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="/do/contact">Contact Us</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="/do/newsletters">Newsletters</a>
		</div>
	</div>
	<%-- End footer --%>

</body></html>