<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html lang="en" prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb#"><head>

	<!-- common -->
	<title>Heatbud | Social Blogging for Bloggers and Businesses</title>
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
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-top-charts-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head><body style="position:relative; background-color:rgb(248, 250, 252)">

	<%-- Begin pretty number --%>
	<script>
	function prettyNumber(n) {
		if ( n < 1000 ) {
			return n;
		} else if ( n >= 1000 && n < 1000000 ) {
			return Math.round(n*10/1000)/10+'k';
		} else {
			return Math.round(n*10/1000000)/10+'m';
		}
	}
	</script>
	<%-- End pretty number --%>

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
	<input id=topChartsNameHidden type=hidden value="${topChartsName}">
	<input id=generateTopChartsJobPeriodHidden type=hidden value="${generateTopChartsJobPeriod}">

	<table style="border-spacing:2px; width:96%; padding-top:60px; margin-left:4%">

	<tr><td colspan="2">
		<%-- Social Blogging revolution --%>
		<div style="width:70%; background-color:#87bdd8; border-radius:4px; padding:10px 30px; margin:0 auto">
			<div style="color:yellow; font-size:1.8em">Join the Social Blogging revolution!</div>
			<div style="width:38%; float:left; color:white; font-size:1.6em">
				<div>For Bloggers</div>
				<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/pricing">&bull; I want to earn followers</a></div>
				<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/pricing">&bull; I want to earn money</a></div>
			</div>
			<div style="width:60%; float:left; color:white; font-size:1.6em">
				<div>For Businesses</div>
				<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/why-1">&bull; I want to create a new website</a></div>
				<div style="font-size:0.7em"><a style="color:white" href="/do/help/main/why-1">&bull; I want to add Social Blog to my existing website</a></div>
			</div>
			<div style="clear:both"></div>
		</div>
	</td></tr>

	<tr><td style="width:65%; vertical-align:top">

		<%-- Page Title --%>
		<div style="float:right">
			<ul id="nav" style="z-index:3">
				<li>
					<div style="font-family:Calibri, Arial, Sans-serif; font-weight:bold; font-size:1.5em; background-color:#FD8A33; color:white; letter-spacing:3px; padding:3px 13px 7px 20px; border-radius:5px">
						<span>${pageTitle}</span>
						<span><img alt="top charts menu" src="/resources/images/menu.png" style="padding-left:8px; height:24px"></span>
					</div>
					<ul style="width:160px">
						<li style="padding-top:6px; padding-left:6px; font-weight:bold; color:#333333; margin-top:15px">TRENDING NOW</li>
						<li><div onclick="switchChart('posts-trending-now')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">POSTS</div></li>
						<li><div onclick="switchChart('zones-trending-now')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">ZONES</div></li>
						<li><div onclick="switchChart('bloggers-trending-now')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">BLOGGERS</div></li>
						<li><div onclick="switchChart('pages-trending-now')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">PAGES</div></li>
						<li style="padding-top:6px; padding-left:6px; font-weight:bold; color:#333333; margin-top:6px">ALL TIME</li>
						<li><div onclick="switchChart('posts-all-time')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">POSTS</div></li>
						<li><div onclick="switchChart('bloggers-all-time')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">BLOGGERS</div></li>
						<li><div onclick="switchChart('pages-all-time')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; cursor:pointer">PAGES</div></li>
						<li style="padding-top:6px; padding-left:6px; font-weight:bold; color:#333333; margin-top:6px">JUST PUBLISHED</li>
						<li><div onclick="switchChart('posts-just-published')" style="color:#0E8D9E; padding-top:6px; padding-left:12px; margin-bottom:40px; cursor:pointer">POSTS</div></li>
					</ul>
				</li>
			</ul>
		</div>
		<div style="clear:both"></div>
	</td><td style="width:28%; vertical-align:top"></td></tr>

	<tr>
		<td style="width:65%; vertical-align:top; padding-right:11px">

			<div id="topChartsDiv">

				<%-- Top Charts for Posts --%>
				<c:if test="${fn:contains(topChartsName,'post')}">
					<c:forEach var="post" items="${topChartsList}" varStatus="loopStatus">
						<div class="topChartsElement" style="float:right">
							<div style="font-size:18px; font-weight:bold; padding:10px 20px">
								<a href="/post/${post.postId}">${post.postTitle}</a>
							</div>
							<div onclick="location.href='/post/${post.postId}'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(${post.postHeadshot})"></div>
							<div style="font-size:15px; padding:10px 20px">
								<span style="font-size:13px; color:rgb(144, 144, 144)"> By </span>
								<span><a href="/${post.bloggerId}">${post.bloggerName}</a></span>
								<span style="font-size:13px; color:rgb(144, 144, 144)"> Zone </span>
								<span><a href="/zone/${post.zoneId}">${fn:escapeXml(post.zoneName)}</a></span>
							</div>
							<c:if test="${fn:contains(topChartsName,'just')}">
								<div style="padding:3px 20px; color:#8A8C8E"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></div>
							</c:if>
							<div style="padding:3px 20px">${post.postSummary}</div>
							<div style="padding:5px 20px">
								<img alt="Overall Heat Index" title="Overall Heat Index" style="width:14px; height:18px; border:none" src="/resources/images/favicon.ico"/>
								<span title="Overall Heat Index" style="color:#7A7C7E; font-size:17px"><script>document.write(prettyNumber(${post.hi}));</script></span>
								<c:if test="${fn:contains(topChartsName,'trending')}">
									<c:if test="${post.hiTrending >= 0}">
										<span><img alt="This week's change" title="This week's change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-up.png"></span>
										<span title="This week's change" style="color:#8A8C8E; font-size:10px">${post.hiTrending}</span>
									</c:if>
									<c:if test="${post.hiTrending < 0}">
										<span><img alt="This week's change" title="This week's change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-down.png"></span>
										<span title="This week's change" style="color:#8A8C8E; font-size:10px">${-post.hiTrending}</span>
									</c:if>
								</c:if>
							</div>
						</div>
						<c:if test="${loopStatus.index % 2 == 1}">
							<div style="clear:both"></div>
						</c:if>
					</c:forEach>
				</c:if>

				<%-- Top Charts for Zones --%>
				<c:if test="${fn:contains(topChartsName,'zone')}">
					<c:forEach var="zone" items="${topChartsList}" varStatus="loopStatus">
						<div class="topChartsElement" style="float:right">
							<div style="font-size:18px; font-weight:bold; padding:10px 20px">
								<a href="/zone/${zone.zoneId}">${zone.zoneName}</a>
							</div>
							<div onclick="location.href='/zone/${zone.zoneId}'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(${zone.zoneHeadshot})"></div>
							<div style="padding:3px 20px">${zone.zoneDesc}</div>
							<div style="font-size:12px; color:#909090; padding:5px 20px">
								<span>${zone.posts} posts</span>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
								<span>${zone.comments} comments</span>
							</div>
						</div>
						<c:if test="${loopStatus.index % 2 == 1}">
							<div style="clear:both"></div>
						</c:if>
					</c:forEach>
				</c:if>

				<%-- Top Charts for entities (bloggers and pages) --%>
				<c:if test="${fn:contains(topChartsName,'blogger') || fn:contains(topChartsName,'page')}">
					<c:forEach var="entity" items="${topChartsList}"  varStatus="loopStatus">
						<div class="topChartsElement" style="float:right">
							<div style="font-size:18px; padding:10px 20px; font-weight:bold">
								<a href="/${entity.entityId}">${entity.entityName}</a>
							</div>
							<c:if test="${empty entity.profilePhoto}">
								<c:if test="${fn:contains(topChartsName,'blogger')}">
									<div onclick="location.href='/${entity.entityId}'" class="topChartsThumb grow" style="margin:0 auto; background-image:url('/resources/images/def-blogger-photo.jpg')"></div>
								</c:if>
								<c:if test="${fn:contains(topChartsName,'page')}">
									<div onclick="location.href='/${entity.entityId}'" class="topChartsThumb grow" style="margin:0 auto; background-image:url('/resources/images/def-page-photo.jpg')"></div>
								</c:if>
							</c:if>
							<c:if test="${not empty entity.profilePhoto}">
								<div onclick="location.href='/${entity.entityId}'" class="topChartsThumb grow" style="margin:0 auto; background-image:url('${entity.profilePhoto}')"></div>
							</c:if>
							<div style="padding:3px 20px; white-space:pre-line">${entity.about}</div>
							<div style="padding:3px 20px">
								<img alt="Overall Heat Index" title="Overall Heat Index" style="width:14px; height:18px; border:none" src="/resources/images/favicon.ico"/>
								<span title="Overall Heat Index" style="color:#7A7C7E; font-size:17px"><script>document.write(prettyNumber(${entity.hi}));</script></span>
								<c:if test="${fn:contains(topChartsName,'trending')}">
									<c:if test="${entity.hiTrending >= 0}">
										<span><img alt="This week's change" title="This week's change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-up.png"></span>
										<span title="This week's change" style="color:#8A8C8E; font-size:10px">${entity.hiTrending}</span>
									</c:if>
									<c:if test="${entity.hiTrending < 0}">
										<span><img alt="This week's change" title="This week's change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-down.png"></span>
										<span title="This week's change" style="color:#8A8C8E; font-size:10px">${-entity.hiTrending}</span>
									</c:if>
								</c:if>
							</div>
							<div style="font-size:12px; color:#909090; padding:5px 20px">
								<span>${entity.posts} posts</span>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
								<span>${entity.votes} votes</span>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
								<span>${entity.comments} comments</span>
							</div>
						</div>
						<c:if test="${loopStatus.index % 2 == 1}">
							<div style="clear:both"></div>
						</c:if>
					</c:forEach>
				</c:if>
			</div>

			<%-- Top Charts navigation --%>
			<div id="topChartsNavigation">
				<%-- previous page link will be hidden on the first page --%>
				<input type=hidden id=topChartsKeyPrevIdHidden value="NULL">
				<input type=hidden id=topChartsKeyPrevHIHidden value="NULL">
				<div id="getTopChartsPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
					<a id="getTopChartsPrevious" class="topNextPrev" href="javascript:">BACK</a>
				</div>
				<%-- next page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=topChartsKeyNextIdHidden value="${topChartsKeyNextId}">
				<input type=hidden id=topChartsKeyNextHIHidden value="${topChartsKeyNextHI}">
				<c:if test="${topChartsKeyNextId != 'NULL'}">
					<div id="getTopChartsNextDiv" style="width:45%; margin-top:10px; margin-right:56px; float:right; text-align:right">
						<a id="getTopChartsNext" class="topNextPrev" href="javascript:">MORE</a>
					</div>
				</c:if>
			</div>
			<div style="clear:both"></div>

		</td>
		<td style="width:28%; vertical-align:top">
			<%-- Facebook --%>
			<div class="friendFacebook" style="float:right">
				<a style="color:white" target="_blank" href="https://www.facebook.com/heatbud">Like us on Facebook</a>
			</div>
			<div style="clear:both"></div>
			<%-- Ticker --%>
			<div style="margin-top:10px; margin-bottom:5px; color:#797979; font-size:15px">Recent activity</div>
			<div style="border:1px solid #d1d1d1; padding:20px 10px 30px 20px; background-color:white">
				<c:forEach var="ticker" items="${tickersList}" varStatus="counter">
					<div style="margin-bottom:25px">
						<span style="color:#8A8C8E">&#9898;</span>
						<span style="color:#8A8C8E"><script>document.write(new Date(${ticker.tickerTime}).toLocaleString());</script></span><br/>
						<span>${ticker.tickerDesc}</span>
					</div>
				</c:forEach>
			</div>
		</td>

	</tr></table>

	<%-- Google Ads Horizontal --%>
	<div style="width:100%; text-align:center; margin-top:30px; margin-bottom:40px">
		<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
		<ins class="adsbygoogle"
		     style="display:inline-block;width:728px;height:90px"
		     data-ad-client="ca-pub-3344897177583439"
		     data-ad-slot="5851386905">
		</ins>
		<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
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

	<!-- Google analytics -->
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	  ga('create', 'UA-48436913-1', 'heatbud.com');
	  ga('send', 'pageview');
	</script>

</body></html>