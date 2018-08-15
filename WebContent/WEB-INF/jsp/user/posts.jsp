<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud - Unpublished Posts</title>
	<link rel="alternate" type="application/rss+xml" href="https://www.heatbud.com/do/rss" />

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

	<!-- for Google -->
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>
	<meta name="application-name" content="Heatbud"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-posts-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head>
<body style="position: relative">

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

	<%-- Begin page content --%>
	<input id=userIdHidden type=hidden value="${userId}"/>
	<input id="bloggerIdHidden" type=hidden value="${blogger.entityId}"/>

	<c:if test="${not empty blogger.entityName}">
		<table id="profile" style="width:100%; border-spacing:2px; padding-top:50px"><tr style="width:100%">
		<td style="width:80%; vertical-align:top; padding:0px">

		<%-- Begin Draft Posts --%>
		<c:if test="${blogger.entityId == userId}">
			<div id="draftPostsHeader" class="profileLabel h1" style="width:87%; margin-top:30px; margin-left:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">MY DRAFT POSTS</span></div>
			<c:if test="${empty draftPostsList}">
				<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%; text-align:center">No Draft Posts.</div>
			</c:if>
			<c:if test="${not empty draftPostsList}">
				<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%">
					<div id="draftPostsDiv">
						<table style="border-spacing:8px">
							<c:forEach var="post" items="${draftPostsList}" varStatus="counter">
								<c:set var="votes" value="${post.upVotes-post.downVotes}"/>
								<tr>
									<td style="vertical-align:top">
										<div style="font-size:200%; text-align:center">${votes}</div>
										<div style="font-size:11px; color:#909090">votes</div>
									</td>
									<td style="vertical-align:top">
										<div style="font-size:200%; text-align:center">${post.comments}</div>
										<div style="font-size:11px; color:#909090">comments</div>
									</td>
									<td style="vertical-align:top">
										<div style="margin-left:10px">
											<div style="font-weight:bold">
												<a href="/post/${post.postId}">${post.postTitle}</a>
											</div>
											<div style="font-size:11px; color:#909090">
												<span><a href="/zone/${post.zoneId}">${fn:escapeXml(post.zoneName)}</a></span>
												<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
												<span style="color:#909090"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></span>
											</div>
											<div>${post.postSummary}</div>
										</div>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div id="draftPostsNavigation">
						<%-- previous page link will be hidden on the first page --%>
						<input type=hidden id=draftPostsKeyPrevBIHidden value="NULL">
						<input type=hidden id=draftPostsKeyPrevUDHidden value="NULL">
						<div id="getDraftPostsPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
							<a id="getDraftPostsPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
						</div>
						<%-- next page link will be set to visible if the key is not NULL --%>
						<input type=hidden id=draftPostsKeyNextBIHidden value="${draftPostsKeyNextBI}">
						<input type=hidden id=draftPostsKeyNextUDHidden value="${draftPostsKeyNextUD}">
						<c:if test="${draftPostsKeyNextBI != 'NULL'}">
							<div id="getDraftPostsNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
								<a id="getDraftPostsNext" class="nextPrevSmall" href="javascript:">MORE</a>
							</div>
						</c:if>
					</div>
				<div style="clear:both"></div>
				</div>
			</c:if>
		</c:if>
		<%-- End Draft Posts --%>

		<%-- Begin Deleted Posts --%>
		<c:if test="${blogger.entityId == userId}">
			<div id="deletedPostsHeader" class="profileLabel h1" style="width:87%; margin-top:20px; margin-left:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">MY DELETED POSTS</span></div>
			<c:if test="${empty deletedPostsList}">
				<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%; text-align:center">No Deleted Posts.</div>
			</c:if>
			<c:if test="${not empty deletedPostsList}">
				<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%">
					<div id="deletedPostsDiv">
						<table style="border-spacing:8px">
							<c:forEach var="post" items="${deletedPostsList}" varStatus="counter">
								<c:set var="votes" value="${post.upVotes-post.downVotes}"/>
								<tr>
									<td style="vertical-align:top">
										<div style="font-size:200%; text-align:center">${votes}</div>
										<div style="font-size:11px; color:#909090">votes</div>
									</td>
									<td style="vertical-align:top">
										<div style="font-size:200%; text-align:center">${post.comments}</div>
										<div style="font-size:11px; color:#909090">comments</div>
									</td>
									<td style="vertical-align:top">
										<div style="margin-left:10px">
											<div style="font-weight:bold">
												<a href="/post/${post.postId}">${post.postTitle}</a>
											</div>
											<div style="font-size:11px; color:#909090">
												<span><a href="/zone/${post.zoneId}">${fn:escapeXml(post.zoneName)}</a></span>
												<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
												<span style="color:#909090"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></span>
											</div>
											<div>${post.postSummary}</div>
										</div>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div id="deletedPostsNavigation">
						<%-- previous page link will be hidden on the first page --%>
						<input type=hidden id=deletedPostsKeyPrevBIHidden value="NULL">
						<input type=hidden id=deletedPostsKeyPrevUDHidden value="NULL">
						<div id="getDeletedPostsPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
							<a id="getDeletedPostsPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
						</div>
						<%-- next page link will be set to visible if the key is not NULL --%>
						<input type=hidden id=deletedPostsKeyNextBIHidden value="${deletedPostsKeyNextBI}">
						<input type=hidden id=deletedPostsKeyNextUDHidden value="${deletedPostsKeyNextUD}">
						<c:if test="${deletedPostsKeyNextBI != 'NULL'}">
							<div id="getDeletedPostsNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
								<a id="getDeletedPostsNext" class="nextPrevSmall" href="javascript:">MORE</a>
							</div>
						</c:if>
					</div>
				<div style="clear:both"></div>
				</div>
			</c:if>
		</c:if>
		<%-- End Deleted Posts --%>

		<div style="clear: both; margin-top: 40px">&nbsp;</div>

		</td>
	
		</tr></table>
	</c:if>
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
