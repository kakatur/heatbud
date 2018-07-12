<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
    <title>Heatbud | Social Blogging for Businesses</title>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
	<meta name=viewport content="width=device-width, initial-scale=1">

	<!-- for Open Graph (facebook) -->
	<meta property="og:type" content="website"/>
	<meta property="og:title" content="Heatbud | Social Blogging for Businesses"/>
    <meta property="og:description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
	<meta property="og:url" content="https://www.heatbud.com/do/help"/>
	<meta property="og:image" content="https://www.heatbud.com/resources/images/fb-share-picture.png"/>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>

	<!-- for Google -->
    <meta name="description" content="Heatbud helps you write sophisticated posts and share them with the world instantly."/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
	<meta name="application-name" content="Heatbud"/>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link rel="stylesheet" href="/resources/css/main-min.css?20180530" />

</head><body style="position:relative; background-color:#eff2f4">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- Begin menu modal box --%>
	<div id=menuBox class="modal-box" style="width:65%; top:0%; left:0%; background-color:#333333; color:white; font-size:1em; box-shadow:none">
		<h3 style="margin-bottom:0.7em">Top Bloggers</h3>
		<div onclick="window.location.href='/top/bloggers-trending-now'; return false;" style="padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">TRENDING NOW</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/bloggers-all-time'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">ALL TIME</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<h3 style="margin-bottom:0.7em">Top Pages</h3>
		<div onclick="window.location.href='/top/pages-trending-now'; return false;" style="padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">TRENDING NOW</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/pages-all-time'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">ALL TIME</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<h3 style="margin-bottom:0.7em">Top Posts</h3>
		<div onclick="window.location.href='/top/posts-trending-now'; return false;" style="padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">TRENDING NOW</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/posts-all-time'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">ALL TIME</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/posts-just-published'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">JUST PUBLISHED</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/do/help'; return false;" style="float:left; margin-top:1em; padding:0.5em 1em; text-align:center; border:1px solid #616161">Help Center</div>
		<div onclick="$('body, #menuBox, #modal-background').toggleClass('active');" style="float:right; margin-top:1em; padding:0.5em 1em; text-align:center; border:1px solid #616161">Close</div>
		<div style="clear:both"></div>
	</div>
	<%-- End menu modal box --%>

	<%-- Begin header --%>
	<div style="background-color:rgb(123,205,123)">
		<div style="text-align:center; padding-top:2%; padding-bottom:2%">
			<div onclick="$('body, #menuBox, #modal-background').toggleClass('active');"><img style="max-width:95%; border:none" src="/resources/images/heatbud-logo-mobile.png"/></div>
			<div style="font-family:Helvetica,Arial; padding:1% 2% 1% 2%; font-size:1.5em; font-weight:bold; color:white">Heatbud Help Center</div>
		</div>
	</div>
	<%-- End header --%>

	<%-- Begin page content --%>
	<div style="padding:1em 0.1em 1em 0.1em">
		<c:import url="/resources/help/mobile-navigation.html"/>
	</div>
	<%-- End page content --%>

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

	<!-- Begin Google analytics -->
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  ga('create', 'UA-48436913-1', 'heatbud.com');
	  ga('send', 'pageview');
	</script>
	<!-- End Google analytics -->

</body></html>