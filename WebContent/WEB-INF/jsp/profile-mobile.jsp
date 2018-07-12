<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud | ${entity.entityName}</title>
	<link rel="alternate" type="application/rss+xml" href="https://www.heatbud.com/do/rss" />
	<meta name=viewport content="width=device-width, initial-scale=1">

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
	<meta property="og:type" content="profile"/>
	<meta property="og:title" content="${fn:escapeXml(entity.entityName)}"/>
	<meta property="og:description" content="${fn:escapeXml(entity.about)}"/>
	<meta property="og:url" content="https://www.heatbud.com/${entity.entityId}"/>
	<meta property="og:image" content="${entity.profilePhoto}"/>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>
	<meta property="fb:profile_id" content="heatbud"/>
	<meta property="profile:first_name" content="${fn:escapeXml(entity.entityName)}"/>
	<meta property="profile:username" content="${entity.entityId}"/>

	<!-- for Twitter -->
	<meta name="twitter:title" content="${fn:escapeXml(entity.entityName)}"/>
	<meta name="twitter:description" content="${fn:escapeXml(entity.about)}"/>
	<meta name="twitter:image" content="${entity.profilePhoto}"/>
	<meta name="twitter:card" content="summary_large_image"/>
	<meta name="twitter:site" content="@HeatbudLLC"/>

	<!-- for Google -->
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
	<c:if test="${empty bloggerTagsString}">
	    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
	 </c:if>
	<c:if test="${not empty bloggerTagsString}">
	    <meta name="keywords" content="${bloggerTagsList}"/>
	 </c:if>
	<link rel="canonical" href="https://www.heatbud.com/${entity.entityId}"/>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>
	<meta name="application-name" content="Heatbud"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-profile-mobile-min.js?20180530"></script>
	<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
	<script async src="//platform-api.sharethis.com/js/sharethis.js#property=5a9e07be57f7f1001382393f&product=inline-share-buttons"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head>

	<c:if test="${empty entity.profileColor}">
		<body style="position:relative; background-size:contain; background-repeat:repeat; background-color:#89BEE8" data-role="page">
	</c:if>
	<c:if test="${not empty entity.profileColor}">
		<body style="position:relative; background-size:contain; background-repeat:repeat; background-color:#${entity.profileColor}" data-role="page">
	</c:if>

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

	<%-- Begin profile header --%>
	<div style="background-color:rgb(123,205,123); padding:2%">
		<div style="text-align:center">
			<div onclick="$('body, #menuBox, #modal-background').toggleClass('active');"><img style="max-width:95%; border:none" src="/resources/images/heatbud-logo-mobile.png"/></div>
		</div>
		<div style="text-align:center; width:100%">
			<div id=entityNameDiv style="font-family:'Permanent Marker', Helvetica, Arial; font-size:1.5em; color:white; font-weight:bold">${entity.entityName}</div>
		</div>
	</div>
	<%-- End profile header --%>

	<%-- Begin page content --%>
	<input id="entityIdHidden" type=hidden value="${entity.entityId}"/>

	<c:if test="${not empty entity.entityName}">

		<%-- profile photo --%>
		<div style="text-align:center; margin-top:3%">
			<c:if test="${empty entity.profilePhoto}">
				<c:if test="${entity.entityType == 'B'}">
					<div style="min-width:200px; min-height:300px; background-size:cover;
						 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');
						 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');
						 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');
						 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');">
		    		</div>
	    		</c:if>
				<c:if test="${entity.entityType == 'P'}">
					<div style="min-width:200px; min-height:300px; background-size:cover;
						 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');
						 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');
						 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');
						 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');">
		    		</div>
	    		</c:if>
			</c:if>
			<c:if test="${not empty entity.profilePhoto}">
				<div style="min-width:200px; min-height:300px; background-size:cover;
					 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');
					 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');
					 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');
					 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');">
	    		</div>
			</c:if>
		</div>

		<%-- entity share controls --%>
		<div class="sharethis-inline-share-buttons"></div>

		<%-- about --%>
		<div style="width:100%; padding:1%">
			<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">About</span></div>
			<div class="profileDiv" style="white-space:normal; width:94%">
				<div id=aboutDiv style="padding-bottom:10px">${entity.about}</div>
			</div>
		</div>

		<c:if test="${entity.entityType == 'B'}">
			<%-- my passion --%>
			<c:if test="${not empty fn:trim(entity.passion)}">
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">My Passion</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<div style="padding-bottom:10px">${entity.passion}</div>
					</div>
				</div>
			</c:if>
			<%-- my achievements --%>
			<c:if test="${not empty fn:trim(entity.achievements)}">
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">My Achievements</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<div style="padding-bottom:10px">${entity.achievements}</div>
					</div>
				</div>
			</c:if>
		</c:if>

		<c:if test="${entity.entityType == 'P'}">
			<%-- special announcements --%>
			<c:if test="${not empty fn:trim(entity.announcements)}">
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%">
						<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Special Announcements</span>
					</div>
					<div class="profileDiv" style="width:94%">
						<div style="padding-bottom:10px">${entity.announcements}</div>
					</div>
				</div>
			</c:if>
			<%-- Google Map --%>
			<c:if test="${not empty entity.address}">
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%">
						<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Visit Us!</span>
					</div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<iframe height="380" frameborder="0" style="border:0"
							src="https://www.google.com/maps/embed/v1/place?key=AIzaSyC0M5pbyIAE0Ij5ppItpRQRy_5ejik8OJQ
							&q=${fn:replace(fn:replace(entity.entityName,' ','+'),'&','and')},${fn:replace(entity.address,' ','+')}
							&attribution_source=Heatbud
							&attribution_web_url=https://www.heatbud.com/${entity.entityId}" allowfullscreen>
						</iframe>
					</div>
				</div>
			</c:if>
		</c:if>

		<%-- contact --%>
		<c:if test="${entity.entityType == 'B'}">
			<div style="width:100%; padding:1%">
				<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Contact</span></div>
				<div class="profileDiv" style="white-space:normal; width:94%">
					<div style="padding-bottom:10px">${entity.contact}</div>
				</div>
			</div>
		</c:if>

		<%-- entity stats --%>
		<div style="width:100%; padding:1%">
			<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Statistics</span></div>
			<div class="profileDiv" style="white-space:normal; width:94%">
				<div>Posts: ${entity.posts}</div>
				<div>Votes: ${entity.votes}</div>
				<div>Comments: ${entity.comments}</div>
				<div style="font-weight: bold">Heat Index: <script>document.write(prettyNumber(${entity.hi}));</script> <img style="width:10px; height:16px; border:none" src="/resources/images/favicon.ico"/></div>
			</div>
		</div>

		<%-- Begin Admin Zones --%>
		<c:if test="${entity.entityType == 'B'}">
			<div id="adminZonesHeader" class="profileLabel h1" style="width:94%; margin-top:3%">
				<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Zones I Manage</span>
			</div>
			<c:if test="${empty adminZonesList}">
				<div class="profileDiv" style="white-space:normal; width:94%; text-align:center">No Zones</div>
			</c:if>
			<c:if test="${not empty adminZonesList}">
				<div id="adminZonesDiv">
					<c:forEach var="zone" items="${adminZonesList}" varStatus="counter">
						<div style="padding:4%; background-color:white">
							<div style="font-size:1.3em; font-weight:bold">
								<a href="/zone/${zone.zoneId}">${fn:escapeXml(zone.zoneName)}</a>
							</div>
							<div style="text-align:center; margin-top:2%">
								<a href="/zone/${zone.zoneId}"><img alt="${fn:escapeXml(zone.zoneName)}" title="${fn:escapeXml(zone.zoneName)}" style="width:90%" src="${zone.zoneHeadshot}"></a>
							</div>
							<div>${zone.posts} <span style="font-size:11px; color:#909090">posts</span> ${zone.comments} <span style="font-size:11px; color:#909090">comments</span></div>
							<div style="font-size:1.2em; margin-top:2%">${zone.zoneDesc}</div>
						</div>
					</c:forEach>
				</div>
				<div id="adminZonesNavigation">
					<%-- previous page link will be hidden on the first page --%>
					<input type=hidden id=adminZonesKeyPrevUIHidden value="NULL">
					<input type=hidden id=adminZonesKeyPrevZIHidden value="NULL">
					<div id="getAdminZonesPreviousDiv" style="width:45%; margin-top:10px; float:left; text-align:left; visibility:hidden">
						<a id="getAdminZonesPrevious" style="font-size:1.5em; background-color:#FF3333; color:white; padding: 2px 6px 2px 6px; border-radius: 5px; text-decoration:none" href="javascript:">BACK</a>
					</div>
					<%-- next page link will be set to visible if the key is not NULL --%>
					<input type=hidden id=adminZonesKeyNextUIHidden value="${adminZonesKeyNextUI}">
					<input type=hidden id=adminZonesKeyNextZIHidden value="${adminZonesKeyNextZI}">
					<c:if test="${adminZonesKeyNextUI != 'NULL'}">
						<div id="getAdminZonesNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
							<a id="getAdminZonesNext" style="font-size:1.5em; background-color:#FF3333; color:white; padding: 2px 6px 2px 6px; border-radius: 5px; text-decoration:none" href="javascript:">MORE</a>
						</div>
					</c:if>
				</div>
				<div style="clear:both"></div>
			</c:if>
		</c:if>
		<%-- End Admin Zones --%>

		<%-- Begin Published Posts --%>
		<div id="publishedPostsHeader" class="profileLabel h1" style="width:94%; margin-top:3%">Blog Posts</div>
		<c:if test="${empty publishedPostsList}">
			<div class="profileDiv" style="white-space:normal; width:94%; text-align:center">No Posts</div>
		</c:if>
		<c:if test="${not empty publishedPostsList}">
			<div id="publishedPostsDiv">
				<c:forEach var="post" items="${publishedPostsList}" varStatus="counter">
					<div style="padding:6% 3% 3% 3%; background-color:white; border-bottom:1px solid rgb(52, 127, 125)">
						<div style="font-size:1.3em; font-weight:bold">
							<a href="/post/${post.postId}">${post.postTitle}</a>
						</div>
						<div style="text-align:center; margin-top:2%">
							<a href="/post/${post.postId}"><img alt="${post.postTitle}" title="${post.postTitle}" style="width:90%" src="${post.postHeadshot}"></a>
						</div>
						<div style="font-size:1em; color:#909090; margin-top:2%">on <script>document.write(new Date(${post.updateDate}).toLocaleString());</script></div>
						<div style="font-size:1.2em; color:#909090; margin-top:2%">zone <a href="/zone/${post.zoneId}">${fn:escapeXml(post.zoneName)}</a></div>
						<div style="font-size:1.2em; margin-top:2%">${post.postSummary}</div>
					</div>
				</c:forEach>
			</div>
			<div id="publishedPostsNavigation">
			<%-- previous page link will be hidden on the first page --%>
				<input type=hidden id=publishedPostsKeyPrevBIHidden value="NULL"/>
				<input type=hidden id=publishedPostsKeyPrevUDHidden value="NULL"/>
				<div id="getPublishedPostsPreviousDiv" style="width:45%; margin-top:10px; float:left; text-align:left; visibility:hidden">
					<a id="getPublishedPostsPrevious" style="font-size:1.5em; background-color:#FF3333; color:white; padding: 2px 6px 2px 6px; border-radius: 5px; text-decoration:none" href="javascript:">BACK</a>
				</div>
				<%-- next page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=publishedPostsKeyNextBIHidden value="${publishedPostsKeyNextBI}"/>
				<input type=hidden id=publishedPostsKeyNextUDHidden value="${publishedPostsKeyNextUD}"/>
				<c:if test="${publishedPostsKeyNextBI != 'NULL'}">
					<div id="getPublishedPostsNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
						<a id="getPublishedPostsNext" style="font-size:1.5em; background-color:#FF3333; color:white; padding: 2px 6px 2px 6px; border-radius: 5px; text-decoration:none" href="javascript:">MORE</a>
					</div>
				</c:if>
			</div>
		<div style="clear:both"></div>
		</c:if>
		<%-- End Published Posts --%>

	</c:if>
	<%-- End page content --%>

	<%-- Begin Google Ads --%>
	<c:if test="${entity.entityType == 'B'}">
		<div style="margin-top:6%; font-size:2em">
			<!-- Heatbud-Responsive -->
			<ins class="adsbygoogle"
			     style="display:block"
			     data-ad-client="ca-pub-3344897177583439"
			     data-ad-slot="6645813308"
			     data-ad-format="auto">
			</ins>
			<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
		</div>
	</c:if>
	<%-- End Google Ads --%>

	<div style="padding:3% 6% 3% 6%; margin-top:6%; margin-bottom:6%; text-align:center; background:#F3FAB6; border:1px solid #d1d1d1">
		<div style="font-family:'Fauna One', Helvetica, Arial; font-size:1.2em; color:#222222">Connect from PC or Tablet to Signup and Publish Posts!</div>
	</div>

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
