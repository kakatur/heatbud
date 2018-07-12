<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html prefix="og: http://ogp.me/ns#"><head>

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
	<meta property="og:url" content="https://www.heatbud.com/top/posts-trending-now"/>
	<meta property="og:image" content="https://www.heatbud.com/resources/images/fb-share-picture.png"/>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>

	<!-- for Google -->
    <meta name="description" content="Heatbud helps you write sophisticated posts and share them with the world instantly."/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
	<meta name="application-name" content="Heatbud"/>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>
	<link rel="canonical" href="https://www.heatbud.com/top/posts-trending-now"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-top-charts-mobile-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link rel="stylesheet" href="/resources/css/main-min.css?20180530" />

</head><body style="position:relative; background-color:#eff2f4" data-role="page">

	<input id=topChartsNameHidden type=hidden value="${topChartsName}">
	<input id=generateTopChartsJobPeriodHidden type=hidden value="${generateTopChartsJobPeriod}">

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
			<div style="font-family:Helvetica,Arial; padding:1% 2% 1% 2%; font-size:1.5em; font-weight:bold; color:white">Welcome to the Social Blogging experience!</div>
		</div>
	</div>
	<%-- End header --%>

	<%-- Begin Top Charts for Posts --%>
	<c:if test="${fn:contains(topChartsName,'post')}">
		<div style="width:94%; margin-top:6%; text-align:right">
			<h1>${pageTitle}</h1>
		</div>
		<div id="topChartsDiv" style="padding:3%">
			<c:forEach var="post" items="${topChartsList}" varStatus="counter">
				<div style="margin-top:6%; padding:3%; background-color:white">
					<div style="font-size:1.3em; font-weight:bold">
						<a href="/post/${post.postId}">${post.postTitle}</a>
					</div>
					<div style="text-align:center; margin-top:2%">
						<a href="/post/${post.postId}"><img alt="${post.postTitle}" title="${post.postTitle}" style="width:90%" src="${post.postHeadshot}"></a>
					</div>
					<div style="font-size:1.2em; color:#909090; margin-top:2%">by <a href="/${post.bloggerId}">${post.bloggerName}</a></div>
					<div style="font-size:1em; color:#909090; margin-top:2%">on <script>document.write(new Date(${post.updateDate}).toLocaleString());</script></div>
					<div style="font-size:1.2em; color:#909090; margin-top:2%">zone <a href="/zone/${post.zoneId}">${fn:escapeXml(post.zoneName)}</a></div>
					<div style="font-size:1.2em; margin-top:2%">${post.postSummary}</div>
				</div>
			</c:forEach>
		</div>
	</c:if>
	<%-- End Top Charts for Posts --%>

	<%-- Begin Top Charts for Entities (Bloggers and Pages) --%>
	<c:if test="${fn:contains(topChartsName,'blogger') || fn:contains(topChartsName,'page')}">
		<div style="width:94%; margin-top:6%; text-align:right">
			<h1>${pageTitle}</h1>
		</div>
		<div id="topChartsDiv" style="padding:3%">
			<c:forEach var="entity" items="${topChartsList}" varStatus="counter">
				<div style="margin-top:6%; padding:3%; background-color:white">
					<div style="font-size:1.3em; font-weight:bold">
						<a href="/${entity.entityId}">${entity.entityName}</a>
					</div>
					<div style="text-align:center; margin-top:2%">
						<c:if test="${empty entity.profilePhoto}">
							<a href="/${entity.entityId}">
								<c:if test="${fn:contains(topChartsName,'blogger')}">
									<img alt="${entity.entityId}" title="${entity.entityId}" style="width:90%" src="/resources/images/def-blogger-photo.jpg">
								</c:if>
								<c:if test="${fn:contains(topChartsName,'page')}">
									<img alt="${entity.entityId}" title="${entity.entityId}" style="width:90%" src="/resources/images/def-page-photo.jpg">
								</c:if>
							</a>
						</c:if>
						<c:if test="${not empty entity.profilePhoto}">
							<a href="/${entity.entityId}">
								<img alt="${entity.entityId}" title="${entity.entityId}" style="width:90%" src="${entity.profilePhoto}">
							</a>
						</c:if>
					</div>
					<div style="font-size:1.2em; margin-top:2%">${entity.about}</div>
					<div style="font-size:1.2em; color:#909090; margin-top:2%">
						<span>${entity.posts} posts</span>
						<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
						<span>${entity.votes} votes</span>
						<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
						<span>${entity.comments} comments</span>
					</div>
				</div>
			</c:forEach>
		</div>
	</c:if>
	<%-- End Top Charts for Entities --%>

	<%-- Begin Top Charts Navigation --%>
	<div id="topChartsNavigation" style="margin:3% 3% 6% 3%; padding:3%; background-color:white">
		<%-- previous page link will be hidden on the first page --%>
		<input type=hidden id=topChartsKeyPrevIdHidden value="NULL">
		<input type=hidden id=topChartsKeyPrevHIHidden value="NULL">
		<div id="getTopChartsPreviousDiv" style="width:45%; margin-top:10px; float:left; text-align:left; visibility:hidden">
			<a id="getTopChartsPrevious" style="font-size:1.5em; background-color:#FF3333; color:white; padding: 2px 6px 2px 6px; border-radius: 5px; text-decoration:none" href="javascript:">BACK</a>
		</div>
		<%-- next page link will be set to visible if the key is not NULL --%>
		<input type=hidden id=topChartsKeyNextIdHidden value="${topChartsKeyNextId}">
		<input type=hidden id=topChartsKeyNextHIHidden value="${topChartsKeyNextHI}">
		<c:if test="${topChartsKeyNextId != 'NULL'}">
			<div id="getTopChartsNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
				<a id="getTopChartsNext" style="font-size:1.5em; background-color:#FF3333; color:white; padding: 2px 6px 2px 6px; border-radius: 5px; text-decoration:none" href="javascript:">MORE</a>
			</div>
		</c:if>
		<div style="clear:both"></div>
	</div>
	<%-- End Top Charts Navigation --%>

	<%-- Begin Google Ads --%>
	<div>
		<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
		<!-- Heatbud-Responsive -->
		<ins class="adsbygoogle"
		     style="display:block"
		     data-ad-client="ca-pub-3344897177583439"
		     data-ad-slot="6645813308"
		     data-ad-format="auto"></ins>
		<script>
		(adsbygoogle = window.adsbygoogle || []).push({});
		</script>
	</div>
	<%-- End Google Ads --%>

	<div style="padding:3% 6% 3% 6%; margin-bottom:3%; text-align:center; background:#F3FAB6; border:1px solid #d1d1d1">
		<div style="font-family:'Fauna One', Helvetica, Arial; font-size:1.2em; color:#222222">Connect from PC or Tablet to Signup and Publish Posts!</div>
	</div>

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