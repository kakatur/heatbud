<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html>
<head>

<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!" />
<meta name="keywords" content="Social Networking, Social Media, blog, blogging, site, website, free blog, personal blog, create blog, new blog, collaboration, macroblogging, tips, ideas, top, free, best" />
<link rel="shortcut icon" href="/resources/images/favicon.ico" />
<title>Heatbud - Blogging without Boundaries</title>

<!-- JQuery includes -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

<!-- Heatbud includes -->
<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet" />
<script src="/resources/js/heatbud-convert-min.js?20180530"></script>

<!-- Google fonts includes -->
<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head>
</head><body style="position:relative">

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

	<div style="padding: 78px 100px 20px 100px">
		<div style="padding-left:80px; padding-right:80px">
			At Heatbud, bloggers create their accounts with their own personal information, and then they create "pages" for their businesses.<br/><br/>
			A blogger can have username and password. A business can't. A business is managed by one or more bloggers.<br/><br/>
			We split your business information into two sections. Please supply the missing information and click submit.<br/><br/>
		</div>
	</div>

	<table style="margin-bottom:10px; margin-left: 50px">
		<tr>
			<td style="width: 242px; vertical-align:top">
				<div>
					<div class="h1" style="margin-bottom:10px">Your information:</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<input id="bloggerUsername" type="text" class="inputText" placeholder="Your Personal Email Address">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<input id="bloggerFirstname" type="text" class="inputText" placeholder="Your First Name">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<input id="bloggerLastname" type="text" class="inputText" placeholder="Your Last Name">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							https://www.heatbud.com/<input id="bloggerURL" type="text" class="inputText" placeholder="Your Blog Site URL (every blogger at Heatbud will have their own personal URL)">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<textarea id="bloggerAbout" rows="4" cols="102" style="width:740px" placeholder="Describe Yourself in a Sentence or Two."></textarea>
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<textarea id="bloggerContact" rows="4" cols="102" style="width:740px" placeholder="Your website or any contact information (OPTIONAL)"></textarea>
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							https://www.facebook.com/<input id="bloggerFbId" type="text" class="inputText" placeholder="facebook Id">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							https://plus.google.com/<input id="bloggerGoogleId" type="text" class="inputText" placeholder="google Id">
						</div>
				</div>
			</td>
			<td style="width: 242px; vertical-align:top">
				<div>
					<div class="h1" style="margin-bottom:10px">Your Business information:</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<input id="pageUsername" type="text" class="inputText" value="{$blogger.entityEmail}" disabled>
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<input id="pageName" type="text" class="inputText" value="{$blogger.entityName}" placeholder="Your Business Name">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							https://www.heatbud.com/<input id="pageURL" type="text" class="inputText" value="{$blogger.entityId}" disabled>
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<textarea id="pageAbout" rows="4" cols="102" style="width:740px" placeholder="Describe Your Business in a Sentence or Two.">{$blogger.about}</textarea>
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							<textarea id="pageContact" rows="4" cols="102" style="width:740px" placeholder="Contact Information for your business (OPTIONAL).">{$blogger.contact}</textarea>
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							https://www.facebook.com/<input id="pageFbId" type="text" class="inputText" value="{$blogger.fbId}" placeholder="facebook Id">
						</div>
						<div class="textDiv" style="background-color:#F7F7F7">
							https://plus.google.com/<input id="pageGoogleId" type="text" class="inputText" value="{$blogger.googleId}" placeholder="google Id">
						</div>
				</div>
			</td>
		</tr>
	</table>

	<div style="padding: 20px">
		<div id="convertError" class="error">&nbsp;</div>
		<div class="activeButton" style="padding-left:300px" onclick="submit()">Submit</div>
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
