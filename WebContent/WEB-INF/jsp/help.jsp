<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
    <title>Heatbud | Help Center</title>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
	<link rel="alternate" type="application/rss+xml" href="https://www.heatbud.com/do/rss" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />

	<!-- icons -->
	<link rel="shortcut icon" href="/resources/images/favicon.ico" type="image/x-icon"/>
    <link rel="apple-touch-icon" href="/resources/images/apple-touch-icon.png"/>
	<link rel="apple-touch-icon" sizes="152x152" href="/resources/images/apple-touch-icon-152x152.png"/>
	<link rel="apple-touch-icon" sizes="144x144" href="/resources/images/apple-touch-icon-144x144.png"/>
	<link rel="apple-touch-icon" sizes="120x120" href="/resources/images/apple-touch-icon-120x120.png"/>
	<link rel="apple-touch-icon" sizes="114x114" href="/resources/images/apple-touch-icon-114x114.png"/>
	<link rel="apple-touch-icon" sizes="76x76" href="/resources/images/apple-touch-icon-76x76.png"/>
	<link rel="apple-touch-icon" sizes="72x72" href="/resources/images/apple-touch-icon-72x72.png"/>
	<link rel="apple-touch-icon" sizes="57x57" href="/resources/images/apple-touch-icon-57x57.png"/>
    <link rel="apple-touch-icon-precomposed" href="/resources/images/apple-touch-icon-76x76.png">
    <link rel="icon" sizes="32x32" href="/resources/images/favicon.ico">
    <meta name="msapplication-TileColor" content="#d3ede7">
	<meta name="msapplication-TileImage" content="/resources/images/apple-touch-icon-114x114.png">

	<!-- for Open Graph (facebook) -->
	<meta property="og:type" content="website"/>
	<meta property="og:title" content="Heatbud | Help Center"/>
    <meta property="og:description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
	<meta property="og:url" content="https://www.heatbud.com/do/help/${navigation}/${content}"/>
	<meta property="og:image" content="https://www.heatbud.com/resources/images/bloggers-wanted.jpg"/>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>

	<!-- for Google -->
    <meta name="description" content="Heatbud helps you write sophisticated posts and share them with the world instantly."/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
	<meta name="application-name" content="Heatbud"/>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>

	<!-- JS includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head><body style="overflow-x:hidden">

	<%-- Begin header --%>
	<table class="header"><tr style="width:100%">
		<td style="float:left">
			<a href="/"><img alt="Heatbud logo" style="width:140px; padding-top:2px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/></a>
		</td>
		<td style="float:right; font-size:13px; padding-top:14px; padding-bottom:6px">
			<div style="float:left; margin-right:8px"><a href="/top/posts-trending-now" class="mainSelection">TOP CHARTS</a></div>
			<div style="float:left; margin-right:8px"><a href="/post/singing-bowls-singing-bowls-and-chakras" class="mainSelection">BLOG POSTS</a></div>
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

	<div style="text-align:center; padding:88px 10px 20px 10px">
		<div style="float:left; text-align:left; width:30%; line-height:2em">
			<c:import url="/resources/help/${navigation}-header.html"/>
			<c:if test="${navigation != 'main'}">
				<div style="margin-left:40px"><a href="/do/help">&lt; &nbsp;Back to Help Center</a></div>
			</c:if>
		</div>
		<div style="float:left; text-align:left; max-width:60%; border:1px solid #e9eaed; padding:20px; margin-top:10px; font-size:1.1em">
			<div style="background-color:#87bdd8; border-radius:4px; padding:16px; margin:0 auto">
				<div style="color:yellow; font-size:1.4em">Join the Social Blogging revolution!</div>
				<div style="width:38%; float:left; color:white; font-size:1.4em">
					<div>For Bloggers</div>
					<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/pricing">&bull; I want to earn followers</a></div>
					<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/pricing">&bull; I want to earn money</a></div>
				</div>
				<div style="width:60%; float:left; color:white; font-size:1.4em">
					<div>For Businesses</div>
					<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/why-1">&bull; I want to create a new website</a></div>
					<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/why-1">&bull; I want to add Social Blog to my existing website</a></div>
				</div>
				<div style="clear:both"></div>
			</div>
			<div style="margin-bottom:20px"></div>
			<c:import url="/resources/help/${navigation}-${content}.html"/>
		</div>
		<div style="float:left; width:20%"></div>
		<div style="clear:both"></div>
	</div>

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

</body>
</html>