<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud | Search</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-search-min.js?20180530"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

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

	<input id=userIdHidden type=hidden value="${userId}"/>

	<%-- Blogger Box --%>
	<div id=bloggerBox class="modal-box" style="padding:0px; width:480px; height:400px; margin-left:-240px; margin-top:-200px">
		<div class="modalHeader" style="width:470px">
			<span>Select Blogger</span>
			<span onclick="$('body, #bloggerBox, #modal-background').toggleClass('active');" style="padding-left:30px; padding-right:10px; color:white; font-size:18px; cursor:pointer">x</span>
		</div><br>
		<div style="overflow:auto; margin-top:60px; height:320px">
			<%-- heading --%>
			<c:if test="${empty boxTerms}">
				<input id="boxTerms" name="boxTerms" type=search style="margin-left:30px; border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:300px; border-radius:2px" placeholder="Search Blogger Id, Name or About">
			</c:if>
			<c:if test="${not empty boxTerms}">
				<input id="boxTerms" name="boxTerms" type=search style="margin-left:30px; border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:300px; border-radius:2px" placeholder="${boxTerms}">
			</c:if>
			<a id="boxSearch" style="font-size:15px; background-color:#FF3333; color:white; padding:2px 20px 4px 20px; border-radius:3px" href="javascript:submitBloggerSearch();">SEARCH</a>
			<div id="boxSearchError" class="error" style="margin-top:10px; margin-left:30px">&nbsp;</div>
			<%-- results --%>
			<div id=boxResults style="min-height:200px; margin-left:25px"></div>
		</div>
	</div>
	<script> var BL = new Array(); </script>

	<%-- Begin search criteria --%>
	<div style="padding-top:80px">
		<div style="width:750px; margin:0 auto">
			<form method="get" action="javascript:submitSearch();">
				<select id="type" name="type" onchange="typeChanged();" style="border: 2px solid #BDC7D8; padding:4px 10px 4px 10px">
					<c:choose>
						<c:when test="${type == 'post'}">
							<option value="post">Post</option><option value="zone">Zone</option><option value="page">Page</option><option value="blogger">Blogger</option>
						</c:when>
						<c:when test="${type == 'zone'}">
							<option value="zone">Zone</option><option value="post">Post</option><option value="page">Page</option><option value="blogger">Blogger</option>
						</c:when>
						<c:when test="${type == 'blogger'}">
							<option value="blogger">Blogger</option><option value="page">Page</option><option value="post">Post</option><option value="zone">Zone</option>
						</c:when>
						<c:otherwise>
							<option value="page">Page</option><option value="post">Post</option><option value="zone">Zone</option><option value="blogger">Blogger</option>
						</c:otherwise>
					</c:choose>
				</select>
				<c:if test="${empty terms}">
					<c:choose>
						<c:when test="${type == 'post'}">
							<input id="terms" name="terms" type=search style="border: 2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:400px; border-radius:2px" placeholder="Search Post Title, Post Summary and Blogger Name.">
						</c:when>
						<c:when test="${type == 'zone'}">
							<input id="terms" name="terms" type=search style="border: 2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:400px; border-radius:2px" placeholder="Search Zone Name and Description.">
						</c:when>
						<c:when test="${type == 'blogger'}">
							<input id="terms" name="terms" type=search style="border: 2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:400px; border-radius:2px" placeholder="Search Blogger Name and Blogger About.">
						</c:when>
						<c:otherwise>
							<input id="terms" name="terms" type=search style="border: 2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:400px; border-radius:2px" placeholder="Search Page Name and Page About.">
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test="${not empty terms}">
					<input id="terms" name="terms" type=search style="border: 2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:400px; border-radius:2px" value="${terms}">
				</c:if>
				<a id="search" style="font-size:15px; background-color:#FF3333; color:white; padding:2px 20px 4px 20px; border-radius:3px" href="javascript:submitSearch();">SEARCH</a>
				<c:if test="${type == 'post'}">
					<div id="confineBloggerIdDiv" style="margin-left:96px; margin-top:5px">
						<span>Show only posts published by </span><br/>
						<c:if test="${userId != 'NULL'}">
							<c:if test="${confineBloggerId == userId}">
								<input id=me type="radio" name="confineBloggerIdRadio" onchange="document.getElementById('confineBloggerId').innerHTML='${userId}'" checked><label for=me>Me</label><br>
							</c:if>
							<c:if test="${confineBloggerId != userId}">
								<input id=me type="radio" name="confineBloggerIdRadio" onchange="document.getElementById('confineBloggerId').innerHTML='${userId}'"><label for=me>Me</label><br>
							</c:if>
						</c:if>
						<c:if test="${confineBloggerId != userId && confineBloggerId != 'Any'}">
							<input id=chooseBlogger type="radio" name="confineBloggerIdRadio" onchange="$('body, #bloggerBox, #modal-background').toggleClass('active');" checked><span onclick="$('body, #bloggerBox, #modal-background').toggleClass('active');" style="padding:0px 10px 0px 0px; cursor:pointer">Choose Blogger</span><span id=confineBloggerId style="color:#888888">${confineBloggerId}</span><br/>
						</c:if>
						<c:if test="${confineBloggerId == userId || confineBloggerId == 'Any'}">
							<input id=chooseBlogger type="radio" name="confineBloggerIdRadio" onchange="$('body, #bloggerBox, #modal-background').toggleClass('active');"><span onclick="$('body, #bloggerBox, #modal-background').toggleClass('active');" style="padding:0px 10px 0px 0px; cursor:pointer">Choose Blogger</span><span id=confineBloggerId style="color:#888888; visibility:hidden">${confineBloggerId}</span><br/>
						</c:if>
						<c:if test="${confineBloggerId == 'Any'}">
							<input id=any type="radio" name="confineBloggerIdRadio" onchange="document.getElementById('confineBloggerId').innerHTML='Any'" checked><label for=any>Any Blogger</label><br>
						</c:if>
						<c:if test="${confineBloggerId != 'Any'}">
							<input id=any type="radio" name="confineBloggerIdRadio" onchange="document.getElementById('confineBloggerId').innerHTML='Any'"><label for=any>Any Blogger</label><br>
						</c:if>
					</div>
				</c:if>
				<c:if test="${type != 'post'}">
					<div id="confineBloggerIdDiv" style="margin-left:96px; margin-top:5px; visibility:hidden">
						<span>Show only posts published by </span><br/>
						<c:if test="${userId != 'NULL'}">
							<input id=me type="radio" name="confineBloggerIdRadio" onchange="document.getElementById('confineBloggerId').innerHTML='${userId}'"><label for=me>Me</label><br>
						</c:if>
						<input id=chooseBlogger type="radio" name="confineBloggerIdRadio" onchange="$('body, #bloggerBox, #modal-background').toggleClass('active');"><span onclick="$('body, #bloggerBox, #modal-background').toggleClass('active');" style="padding:0px 10px 0px 0px; cursor:pointer">Choose Blogger</span><span id=confineBloggerId style="color:#888888">${confineBloggerId}</span><br/>
						<input id=any type="radio" name="confineBloggerIdRadio" onchange="document.getElementById('confineBloggerId').innerHTML='Any'"><label for=any>Any Blogger</label><br>
					</div>
				</c:if>
			</form>
		</div>
		<%-- Search Results for Posts --%>
		<div style="width:800px; padding-top:30px; margin:0 auto">
			<c:if test="${type == 'post'}">
				<c:forEach var="post" items="${searchPostsList}">
					<c:set var="votes" value="${post.upVotes-post.downVotes}"/>
					<div class="topChartsElement" style="max-width:600px; padding:10px">
						<div style="width:100%; text-align:center; margin-bottom:5px">
							<a style="font-size:18px; font-weight:bold" href="/post/${post.postId}">${post.postTitle}</a>
						</div>
						<div style="width:100%">
							<div style="width:42%; padding:0px 15px 0px 30px; float:left">
								<div onclick="location.href='/post/${post.postId}'" class="topChartsThumb grow" style="width:280px; height:180px; margin:0 auto; background-image:url(${post.postHeadshot})"></div>
							</div>
							<div style="width:42%; padding:0px 15px 0px 30px; float:left">
								<div>${post.postSummary}</div>
								<div style="margin-top:5px">by <a href="/${post.bloggerId}">${post.bloggerName}</a></div>
								<div style="margin-top:5px">zone <a href="/zone/${post.zoneId}">${fn:escapeXml(post.zoneName)}</a></div>
								<div style="font-size:12px; color:#909090; margin-top:5px">
									<span>${post.views} views</span>
									<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
									<span>${votes} votes</span>
									<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
									<span>${post.comments} comments</span>
								</div>
							</div>
							<div style="clear:both"></div>
						</div>
					</div>
				</c:forEach>
				<c:if test="${empty searchPostsList && not empty terms}">
					<div style="font-size:16px; margin-left:40px; margin-top:60px">No posts matching your search.</div>
					<div style="margin-top:30px; margin-left:40px">
						<span>Explore top posts at </span>
						<a style="font-weight:bold" href="/top/posts-trending-now">Now Trending Posts</a>.<br/>
						<span>Or, all posts at <a style="font-weight:bold" href="/do/start">Browse Zones</a>.</span>
					</div>
				</c:if>
			</c:if>
		</div>
		<%-- Search Results for Zones --%>
		<div style="width:800px; padding-top:30px; margin:0 auto">
			<c:if test="${type == 'zone'}">
				<c:forEach var="zone" items="${searchZonesList}">
					<div class="topChartsElement" style="max-width:600px; padding:10px">
						<div style="width:100%; text-align:center; margin-bottom:5px">
							<a style="font-size:18px; font-weight:bold" href="/zone/${zone.zoneId}">${zone.zoneName}</a>
						</div>
						<div style="width:100%">
							<div style="width:42%; padding:0px 15px 0px 30px; float:left">
								<div onclick="location.href='/zone/${zone.zoneId}'" class="topChartsThumb grow" style="width:280px; height:180px; margin:0 auto; background-image:url(${zone.zoneHeadshot})"></div>
							</div>
							<div style="width:42%; padding:0px 15px 0px 30px; float:left">
								<div>${zone.zoneDesc}</div>
								<div style="font-size:12px; color:#909090; margin-top:5px">
									<span>${zone.posts} posts</span>
									<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
									<span>${zone.comments} comments</span>
								</div>
							</div>
							<div style="clear:both"></div>
						</div>
					</div>
				</c:forEach>
				<c:if test="${empty searchZonesList}">
					<div style="font-size:16px; margin-left:40px; margin-top:60px">No zones matching your search.</div>
					<div style="margin-top:30px; margin-left:40px">
						<span>You may select a zone from </span>
						<a style="font-weight:bold" href="/top/zones-trending-now">Now Trending Zones</a>.<br/>
						<span>Or, create your own at <a style="font-weight:bold" href="/do/start">Browse Zones</a>.</span>
					</div>
				</c:if>
			</c:if>
		</div>
		<%-- Search Results for entities (bloggers and pages) --%>
		<div style="width:800px; padding-top:30px; margin:0 auto">
			<c:if test="${type == 'blogger' || type == 'page'}">
				<c:forEach var="entity" items="${searchEntitiesList}">
					<div class="topChartsElement" style="max-width:600px; padding:10px">
						<div style="width:100%; text-align:center; margin-bottom:5px">
							<a style="font-size:18px; font-weight:bold" href="/${entity.entityId}">${entity.entityName}</a>
						</div>
						<div style="width:100%">
							<div style="width:42%; padding:0px 15px 0px 30px; float:left">
								<c:if test="${empty entity.profilePhoto}">
									<div onclick="location.href='/${entity.entityId}'" class="topChartsThumb grow" style="width:280px; height:180px; margin:0 auto; background-image:url('/resources/images/def-${type}-photo.jpg')"></div>
								</c:if>
								<c:if test="${not empty entity.profilePhoto}">
									<div onclick="location.href='/${entity.entityId}'" class="topChartsThumb grow" style="width:280px; height:180px; margin:0 auto; background-image:url('${entity.profilePhoto}')"></div>
								</c:if>
							</div>
							<div style="width:42%; padding:0px 15px 0px 30px; float:left">
								<div>${entity.about}</div>
								<div style="font-size:12px; color:#909090; margin-top:5px">
									<span>${entity.posts} posts</span>
									<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
									<span>${entity.votes} votes</span>
									<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
									<span>${entity.comments} comments</span>
								</div>
							</div>
							<div style="clear:both"></div>
						</div>
					</div>
				</c:forEach>
				<c:if test="${empty searchEntitiesList}">
					<c:if test="${type == 'blogger'}">
						<div style="font-size:16px; margin-left:40px; margin-top:60px">No bloggers matching your search.</div>
						<div style="margin-top:30px; margin-left:40px">
							<span>Find top bloggers at </span>
							<a style="font-weight:bold" href="/top/bloggers-trending-now">Now Trending Bloggers</a>.<br/>
						</div>
					</c:if>
					<c:if test="${type == 'page'}">
						<div style="font-size:16px; margin-left:40px; margin-top:60px">No pages matching your search.</div>
						<div style="margin-top:20px; margin-left:40px">
							<span>Explore top pages at </span>
							<a style="font-weight:bold" href="/top/pages-trending-now">Now Trending Pages</a>.<br/>
						</div>
					</c:if>
				</c:if>
			</c:if>
		</div>
	</div>
	<%-- End search criteria --%>

	<%-- Begin Google ads --%>
	<div style="width:800px; padding:80px 20px 20px 20px; margin:0 auto">
		<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
		<!-- Heatbud-Horizontal -->
		<ins class="adsbygoogle"
		     style="display:inline-block;width:728px;height:90px"
		     data-ad-client="ca-pub-3344897177583439"
		     data-ad-slot="5851386905">
		</ins>
		<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
	</div>
	<%-- End Google ads --%>

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
