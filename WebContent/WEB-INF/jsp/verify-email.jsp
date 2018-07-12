<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html><head>

	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud - Verify Email</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css?20150722" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-verify-email-min.js?20150722"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head><body style="overflow-x:hidden; font-family:calibri,tahoma,verdana; font-size:16px">

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
			<div style="float:left"><a href="/do/login" class="mainSelection">LOGIN / SIGNUP</a></div>
		</td>
	</tr></table>
	<div style="clear:both"></div>
	<%-- End header --%>

	<div style="padding:88px 10px 250px 30px">

	<br><br>
	<form:form method="post" action="/do/verify-email" commandName="user">
		<div class="heading">Please enter verification code and your email address.</div>
		<br>
		<div class="labelDiv">Verification Code</div>
		<div class="textDiv"><form:input class="inputText" style="width:300px" path="salt"/></div>
		<div class="labelDiv">Email Address</div>
		<div class="textDiv"><form:input class="inputText" path="username"/></div>
		<div id="error" class="error">${user.error}</div>
		<button id="verifyButton" class="activeButton" style="margin-left:165px;">Verify Email</button>
	</form:form>

	</div>
	<br><br>

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
