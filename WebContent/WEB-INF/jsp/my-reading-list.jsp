<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html lang="en" prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# article: http://ogp.me/ns/article#"><head>

	<!-- common -->
    <base target="_blank">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
	<c:if test="${pageType == 'ZONE'}">
	    <title>Heatbud | ${zoneName}</title>
	</c:if>
	<c:if test="${pageType == 'POST'}">
	    <title>Heatbud | ${zoneName} - ${post.postTitle}</title>
	</c:if>
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
	<meta property="og:type" content="article"/>
	<c:if test="${pageType == 'POST'}">
		<meta property="og:title" content="${fn:escapeXml(post.postTitle)}"/>
		<meta property="og:description" content="${fn:escapeXml(post.postSummary)}"/>
		<meta property="og:url" content="https://www.heatbud.com/post/${post.postId}"/>
		<meta property="og:image" content="${fn:replace(post.postHeadshot,'/thumbs/', '/social/')}"/>
		<c:if test="${not empty blogger.fbId}">
			<meta property="article:author" content="https://www.facebook.com/${blogger.fbId}"/>
		</c:if>
		<meta property="article:section" content="${fn:escapeXml(zoneName)}"/>
		<c:forTokens var="tag" items="${post.tags}" delims=",">
			<meta property="article:tag" content="${fn:trim(tag)}"/>
		</c:forTokens>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<meta property="og:title" content="${fn:escapeXml(zoneName)}"/>
		<meta property="og:description" content="${fn:escapeXml(zoneDesc)}"/>
		<meta property="og:url" content="https://www.heatbud.com/zone/${zoneId}"/>
		<meta property="og:image" content="${zoneHeadshot}"/>
		<meta property="article:section" content="${fn:escapeXml(zoneName)}"/>
	</c:if>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>
	<meta property="fb:profile_id" content="heatbud"/>
	<meta property="article:publisher" content="https://www.facebook.com/heatbud"/>

	<!-- for Twitter -->
	<c:if test="${pageType == 'POST'}">
		<meta name="twitter:title" content="${fn:escapeXml(post.postTitle)}"/>
		<meta name="twitter:description" content="${fn:escapeXml(post.postSummary)}"/>
		<meta name="twitter:image" content="${fn:replace(post.postHeadshot,'/thumbs/', '/social/')}"/>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<meta name="twitter:title" content="${fn:escapeXml(zoneName)}"/>
		<meta name="twitter:description" content="${fn:escapeXml(zoneDesc)}"/>
		<meta name="twitter:image" content="${zoneHeadshot}"/>
	</c:if>
	<meta name="twitter:card" content="summary_large_image"/>
	<meta name="twitter:site" content="@HeatbudLLC"/>

	<!-- for Google -->
	<c:if test="${pageType == 'POST'}">
	    <meta name="description" content="${fn:escapeXml(post.postSummary)}"/>
	    <meta name="keywords" content="${fn:escapeXml(post.tags)}"/>
		<link rel="canonical" href="https://www.heatbud.com/post/${post.postId}"/>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
	    <meta name="description" content="${fn:escapeXml(zoneDesc)}"/>
		<meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
		<link rel="canonical" href="https://www.heatbud.com/zone/${zoneId}"/>
	</c:if>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>
	<meta name="application-name" content="Heatbud"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-images-min.js?20180530"></script>
	<script src="/resources/js/heatbud-mrl-min.js?20180530"></script>
	<script async src="/resources/js/heatbud-pagebox-min.js?20180530"></script>
	<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
	<script async src="//platform-api.sharethis.com/js/sharethis.js#property=5a9e07be57f7f1001382393f&product=inline-share-buttons"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head><body style="position:relative">

	<%-- common hidden fields --%>
	<input id=zoneIdHidden type=hidden value="${zoneId}">
	<input id=zoneNameHidden type=hidden value="${zoneName}">
	<input id=pageTypeHidden type=hidden value="${pageType}">
	<input id=userIdHidden type=hidden value="${userId}"/>
	<input id=postIdHidden type=hidden value="${post.postId}"/>
	<input id=publishFlagHidden type=hidden value="${post.publishFlag}"/>
	<%-- for editing the post --%>
	<input id=postTitleHidden type=hidden value=""/>
	<input id=postContentHidden type=hidden value=""/>
	<input id=postSummaryHidden type=hidden value="${fn:escapeXml(post.postSummary)}"/>
	<input id=postTagsHidden type=hidden value="${fn:escapeXml(post.tags)}"/>
	<input id=postHeadshotOrigHidden type=hidden value="${post.postHeadshot}"/>
	<input id=postHeadshotNewHidden type=hidden value="${post.postHeadshot}"/>
	<input id=primaryPageIdHidden type=hidden value="${primaryPageId}"/>
	<input id=pageIdHidden type=hidden value="${post.pageId}"/>
	<input id=pageIdSelectedHidden type=hidden value="${post.pageId}"/>
	<c:if test="${pageType == 'POST'}">
		<%-- needed only for the post page --%>
		<input id=bloggerIdHidden type=hidden value="${post.bloggerId}"/>
		<input id=upVotesHidden type=hidden value="${post.upVotes}">
		<input id=downVotesHidden type=hidden value="${post.downVotes}">
		<input id=commentsHidden type=hidden value="${post.comments}">
		<input id=postHIHidden type=hidden value="${post.hi}">
		<input id=currentVoteHidden type=hidden value="${currentVote}">
		<input id=followerIdHidden type=hidden value="${followerId}">
		<input id=checkCommenterIdExistsHidden type=hidden value="${checkCommenterIdExists}">
		<%-- variables to keep comments list intact when a comment is deleted --%>
		<input id=commentsCountHidden type=hidden value="${fn:length(commentsList)}">
		<input id=commentsKeyRefreshHidden type=hidden value="NULL">
		<input id=commentsKeyAlternateRefreshHidden type=hidden value="NULL">
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

	<%-- prompt signup box --%>
	<div id=promptSignupBox class="modal-box" style="width:360px; height:180px; margin-left:-180px; margin-top:-90px">
		<div class="h1">Please Login/ Signup to continue</div><br>
		<div>Registration is Free and Simple! And you get the ability to favorite zones, create blog posts, write comments, your own personalized My Reading List and more!</div><br>
		<input class="activeButton" onclick="window.location.href='/do/login';" type="button" value="Take me to the Login/ Signup page">
		<input onclick="$('body, #promptSignupBox, #modal-background').toggleClass('active');" type="button" value="Close">
	</div>

	<%-- prompt error box --%>
	<div id=promptErrorBox class="modal-box" style="width:360px; height:180px; margin-left:-180px; margin-top:-90px">
		<div class="h1">Error</div><br/>
		<div id="errorValue"></div><br/>
		<input onclick="$('body, #promptErrorBox, #modal-background').toggleClass('active');" type="button" value="Close">
	</div>

	<%-- create zone box --%>
	<div id=createZoneBox class="modal-box" style="width:360px; height:400px; margin-left:-180px; margin-top:-200px">
		<div class="h1">Create a Zone</div><br>
		<div>Your post gets better attention in an existing zone that already has some posts. But if you don't find an appropriate zone for your post, it's time to create one!</div><br>
		<input id=createZoneNameInput type=text placeholder="Zone Name (use proper case)" style="width:300px"/><br><br>
		<textarea id=createZoneDescInput rows="4" cols="40" placeholder="Describe your zone in a sentence or two."></textarea><br>
		Who can post in this zone?<br>
		<input type="radio" name="createZoneWhoRadio" onchange="document.getElementById('createZoneWhoInput').value='E'" checked>Any Heatbud User<br>
		<input type="radio" name="createZoneWhoRadio" onchange="document.getElementById('createZoneWhoInput').value='A'">Only Me and the Admins that I approve<br>
		<input type="hidden" id="createZoneWhoInput" value="E">
		<div id="createZoneMessage" style="color:red">&nbsp;</div>
		<input id="createZoneButton" class="activeButton" onclick="createZone('false')" type="button" value="Create">
		<input onclick="$('body, #createZoneBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
	</div>

	<%-- share elsewhere box --%>
	<div id=shareElsewhereBox class="modal-box" style="width:500px; height:300px; margin-left:-250px; margin-top:-150px">
		<div class="h1">Sharing is easy!</div><br>
		<div>Simply copy and paste the URL anywhere you like!</div><br>
		<c:if test="${pageType == 'POST'}">
			<div style="color:green">https://www.heatbud.com/post/${post.postId}</div><br>
		</c:if>
		<c:if test="${pageType == 'ZONE'}">
			<div style="color:green">https://www.heatbud.com/zone/${zoneId}</div><br>
		</c:if>
		<div>Most social networking sites will be able to extract the Headshot, Title and Summary from Heatbud.</div><br>
		<div>Note: Reproduction of blog post content is not allowed. Please see <a target="_blank" href="/do/privacy">Heatbud Privacy &amp; Terms</a>.</div><br>
		<input onclick="$('body, #shareElsewhereBox, #modal-background').toggleClass('active');" type="button" value="Close">
	</div>

	<%-- Page Box --%>
	<div id=pageBox class="modal-box" style="padding:0px; width:480px; height:400px; margin-left:-240px; margin-top:-200px">
		<div class="modalHeader" style="width:470px">
			<span>Select Page</span>
			<span onclick="$('body, #pageBox, #modal-background').toggleClass('active');" style="padding-left:30px; padding-right:10px; color:white; font-size:18px; cursor:pointer">x</span>
		</div><br>
		<div style="overflow:auto; margin-top:60px; height:320px">
			<div style="margin-left:25px; margin-bottom:10px; color:#909090">Double click to Select</div>
			<div id=boxResults style="min-height:200px; margin-left:25px"></div>
		</div>
	</div>
	<script> var PL = new Array(); </script>

	<%-- Image Box (for selecting Post Headshot and Zone Headshot via My Images) --%>
	<c:if test="${pageType == 'POST'}">
		<input id=CKEditorFuncNumHidden type=hidden value="PostHeadshot">
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<input id=CKEditorFuncNumHidden type=hidden value="ZoneHeadshot">
	</c:if>
	<input id=selectedImageHidden type=hidden value="NULL">

	<div id=imageBox class="modal-box" style="padding:0px; width:640px; height:480px; margin-left:-320px; margin-top:-240px">
		<div class="modalHeader" style="width:630px">
			<span>My Images</span>
			<span onclick="$('body, #imageBox, #modal-background').toggleClass('active');" style="margin-left:30px; margin-right:10px; color:red; font-size:18px; cursor:pointer">x</span>
		</div><br>
		<div style="overflow: auto; margin-top:30px; height:420px">
			<table style="width:100%; border-spacing:2px"><tr style="width:100%">
				<td style="width:30%; vertical-align:top; padding:0px">
					<%-- heading --%>
					<div class="zoneHeader" style="margin-top:15px">MY ALBUMS</div>
					<%-- folders --%>
					<div id=foldersDiv style="padding-right:10px"></div>
				</td>
				<td style="width:70%; vertical-align:top; padding:0px">
					<%-- heading --%>
					<div style="margin-top:15px; color:#888888; font-size:12px">Hint: Create a separate album for each of your posts.</div>
					<div id="imagesMessageTop" style="color:red">&nbsp;</div>
					<div style="margin-top:10px"><span id=selectedFolder style="font-weight:bold; color: #333333">common</span></div>
					<%-- images --%>
					<div id=images style="min-height:200px; margin-right:10px"></div>
					<%-- tool box --%>
					<div style="margin-top:10px">
						<div style="float:left">
							<input class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="chooseImage()" type="button" value="Select">
						</div>
						<div style="float:left; margin-left:10px; margin-top:12px">
							<a onclick="deleteImage()">Delete</a>
						</div>
						<div id=uploadImageDiv style="float:left; margin-left:10px; margin-top:12px">
							<a id=uploadImageA onclick="uploadImageInput.click()">Upload</a>
						</div>
						<div style="float:left; margin-left:10px; margin-top:12px">
							<a onclick="$('body, #imageBox, #modal-background').toggleClass('active');">Close</a>
						</div>
						<div style="clear:both"></div>
						<input id=uploadImageInput type=file style="visibility:collapse" onchange="uploadImage(this.files[0])">
					</div>
					<div id="imagesMessage" style="color:red">&nbsp;</div>
				</td>
			</tr></table>
		</div>
	</div>

	<c:if test="${pageType == 'POST'}">
		<%-- prompt save box --%>
		<div id=promptSaveBox class="modal-box" style="width:300px; height:120px; margin-left:-150px; margin-top:-60px">
			<div class="h1">Save changes?</div><br>
			<div>There are unsaved changes. Save now?</div><br>
			<input class="activeButton" onclick="savePost('N'); $('body, #promptSaveBox, #modal-background').toggleClass('active');" type="button" value="Save">
			<input onclick="refreshMRL();" type="button" value="Close without saving">
		</div>
	</c:if>

	<%-- create folder box --%>
	<div id=createFolderBox class="modal-box" style="width:360px; height:200px; margin-left:-180px; margin-top:-100px">
		<div class="h1">Create an Album</div><br>
		<div>Hint: Create a separate album for each of your post. Empty albums will be automatically deleted.</div><br>
		<input id=createFolderInput type=text style="width:300px"/><br><br>
		<input class="activeButton" onclick="createFolder()" type="button" value="Create">
		<input onclick="$('body, #createFolderBox, #modal-background').toggleClass('active');" type="button" value="Cancel"><br>
		<div id="createFolderMessage" style="color:red">&nbsp;</div>
	</div>

	<%-- onlyAdmins box --%>
	<div id=onlyAdminsBox class="modal-box" style="width:320px; height:60px; margin-left:-160px; margin-top:-30px">
		<div>Only the zone admins can post in this zone.</div><br>
		<input onclick="$('body, #onlyAdminsBox, #modal-background').toggleClass('active');" type="button" value="Close">
	</div>

	<%-- whatIsHI box --%>
	<div id=whatIsHIBox class="modal-box" style="width:200px; height:180px; margin-left:-100px; margin-top:-90px">
		<div class="h1">Heat Index</div><br>
		<div>= number of unique views</div>
		<div>+ number of up votes * 2</div>
		<div>- number of down votes * 2</div>
		<div>+ number of comments * 3</div><br>
		<input onclick="$('body, #whatIsHIBox, #modal-background').toggleClass('active');" type="button" value="Close">
	</div>

	<%-- Begin header - Note: This page has customized header that includes target keyword --%>
	<table class="header"><tr style="width:100%">
		<td style="float:left">
			<a href="/" target="_self"><img alt="Heatbud logo" style="width:140px; padding-top:2px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/></a>
		</td>
		<td style="float:right; font-size:13px; padding-top:14px; padding-bottom:6px">
			<div style="float:left; margin-right:8px"><a target="_self" href="/top/posts-trending-now" class="mainSelection">TOP CHARTS</a></div>
			<div style="float:left; margin-right:8px"><a target="_self" href="/post/singing-bowls-singing-bowls-and-chakras" class="mainSelection">BLOG POSTS</a></div>
			<div style="float:left; margin-right:8px"><a target="_self" href="/do/search" class="mainSelection">SEARCH</a></div>
			<div style="float:left; margin-right:8px"><a target="_self" href="/do/help" class="mainSelection">HELP CENTER</a></div>
			<sec:authorize access="!isAuthenticated()">
				<div style="float:left"><a target="_self" href="/do/login" class="mainSelection">LOGIN / SIGNUP</a></div>
			</sec:authorize>
			<sec:authorize access="isAuthenticated()">
				<div style="float:left; font-size:16px">
					<ul id="nav" style="margin-top:0px; margin-bottom:0px">
						<li>
							<span style="color:#ffffff; letter-spacing:1.5px"><sec:authentication property="principal.firstName"/> <sec:authentication property="principal.lastName"/> <img src="/resources/images/menu_header.png" style="padding-left:5px; height:15px"></span>
							<ul>
								<li><a target="_self" href="/<sec:authentication property="principal.userId"/>" style="margin-top:10px; padding-top:10px">Profile</a></li>
								<li><a target="_self" href="/user/settings" style="padding-top:10px">Settings</a></li>
								<li><a target="_self" href="/user/notifications" style="padding-top:10px">Notifications</a></li>
								<li><a target="_self" href="/user/pages" style="padding-top:10px">Page Manager</a></li>
								<li><a target="_self" href="/user/orders" style="padding-top:10px">Orders</a></li>
								<li><a target="_self" href="/user/images" style="padding-top:10px">Images</a></li>
								<li><a target="_self" href="/user/posts" style="padding-top:10px">Unpublished Posts</a></li>
								<li><a target="_self" href="<c:url value="/do/logout"/>" style="padding-top:10px">Logout</a></li>
								<li><a target="_self" href="/user/drop" style="padding-top:10px; padding-bottom:30px">Drop Account</a></li>
							</ul>
						</li>
					</ul>
				</div>
			</sec:authorize>
		</td>
	</tr></table>
	<div style="clear:both"></div>
	<%-- End header --%>

	<table itemscope itemtype="http://schema.org/Article" style="padding-top:40px; border-spacing:0px; width:100%">

		<tr style="width:100%; border-spacing:0px">

		<%-- Begin zones --%>
		<td style="width:250px; vertical-align:top; padding:32px 10px 0px 40px; background-color:#f7f7f7; border-right:1px solid #d7d7d7">

			<div id=zoneName itemprop="articleSection" style="font-family:Helvetica, Arial; font-size:20px; font-weight:bold">
				<span>${zoneName}</span>
				<c:if test="${pageType == 'POST'}">
					<a class="visitZoneHomePage" target="_self" href="/zone-home/${zoneId}">
						<img src="/resources/images/home.png" alt="Visit zone home page" title="Visit zone home page" style="padding-left:4px; height:12px; border:none">
					</a>
				</c:if>
			</div>
			<c:if test="${isAdmin == 'true'}">
				<div style="width:100%; text-align:center">
					<div><input id="zoneNameInput" type=text style="width:450px; display:none"></div>
					<div id="editZoneNameDiv"><a id="editZoneNameA" style="color:white" target="_self" onclick="editZoneName()" href="javascript:">Edit Zone Name</a></div>
					<div style="margin-top: 10px; color: red" id="zoneNameRetMessage"></div>
				</div>
			</c:if>
			<c:if test="${zoneWho == 'E'}">
				<div style="font-size:13px; color:#909090">Open Zone</div>
			</c:if>
			<c:if test="${zoneWho == 'A'}">
				<div style="font-size:13px; color:#909090">Closed Zone</div>
			</c:if>
			<div class="favoriteCurrentZone" style="display:inline-block; height:17px; line-height:15px; padding:0px 10px; color:#03a87c; border:1px solid #03a87c; border-radius:4px; font-size:13px; cursor:pointer">Favorite</div>
			<c:if test="${pageType == 'POST'}">
				<c:if test="${prevPostId != 'NULL'}">
					<div style="margin-top:6px"><a target="_self" style="color:#1f4f82; text-decoration:none" href="/post/${prevPostId}">&lt; Previous Post</a></div>
				</c:if>
				<c:if test="${prevPostId == 'NULL'}">
					<div style="margin-top:6px; color:#1f4f82; text-decoration:line-through;">&lt; Previous Post</div>
				</c:if>
			</c:if>
			<c:if test="${pageType == 'ZONE'}">
				<c:if test="${prevPostId != 'NULL'}">
					<div style="margin-top:6px"><a target="_self" style="color:#1f4f82; text-decoration:none" href="/post/${prevPostId}">Read Posts</a></div>
				</c:if>
				<c:if test="${prevPostId == 'NULL'}">
					<div style="margin-top:6px; color:#1f4f82; text-decoration:line-through;">Read Posts</div>
				</c:if>
			</c:if>
			<c:if test="${userId != 'NULL'}">
				<div class="lastReadPost" style="margin-top:6px; color:#1f4f82; cursor:pointer">&#9633; My last read post</div>
			</c:if>
			<c:if test="${pageType == 'POST'}">
				<c:if test="${nextPostId != 'NULL'}">
					<div style="margin-top:6px"><a target="_self" style="color:#1f4f82; text-decoration:none" href="/post/${nextPostId}">&gt; Next Post</a></div>
				</c:if>
				<c:if test="${nextPostId == 'NULL'}">
					<div style="margin-top:6px; color:#1f4f82; text-decoration:line-through;">&gt; Next Post</div>
				</c:if>
			</c:if>
			<c:if test="${zoneWho == 'A' && isAdmin == 'false'}">
				<div class="showOnlyAdminsBox" style="color:#1f4f82; margin-top:6px; text-decoration:line-through; cursor:pointer">Create a Post</div>
			</c:if>
			<c:if test="${zoneWho == 'E' || (zoneWho == 'A' && isAdmin == 'true')}">
				<div class="writePost" style="color:#1f4f82; margin-top:6px; cursor:pointer">Create a Post</div>
			</c:if>
			<div class="createZone" style="color:#1f4f82; margin-top:6px; cursor:pointer">Create a Zone</div>

			<sec:authorize access="isAuthenticated()">
				<div id="myZonesDiv">&nbsp;</div>
			</sec:authorize>
			<div class="zoneHeader" style="margin-top:30px">MY ZONES</div>
			<div id="myZonesList" style="min-height:20px">
				<c:if test="${empty myZonesList}">
					<c:if test="${userId == 'NULL'}">
						<div style="margin-top:20px; font-size:12px; color:#575757">Login to favorite zones.</div>
					</c:if>
					<c:if test="${userId != 'NULL'}">
						<div style="margin-top:20px; font-size:12px">Hover over TOP ZONES section below to favorite.</div>
					</c:if>
				</c:if>
				<ul style="list-style-type:none; padding-left:0px">
					<c:forEach var="myZone" items="${myZonesList}" varStatus="counter">
						<li class="zoneList myZones" title="${fn:escapeXml(myZone.zoneName)}">
							<div class="zoneName" style="width:136px" onclick="enterMyZone(${counter.index})">${fn:escapeXml(myZone.zoneName)}</div>
							<c:if test="${myZone.unreadCount < 100}">
								<div style="float:left; width:10px; color:#909090; font-size:11px; font-weight:bold" title="Unread Post Count"><span style="background-color:#efefff; padding:0px 4px 0px 4px">${myZone.unreadCount}</span></div>
							</c:if>
							<c:if test="${myZone.unreadCount >= 100}">
								<div style="float:left; width:10px; color:#909090; font-size:11px; font-weight:bold" title="Unread Post Count"><span style="background-color:#efefff; padding:0px 4px 0px 4px">99+</span></div>
							</c:if>
							<div style="float:left; width:12px" onclick="deleteMyZone(${counter.index})" title="Remove from favorites">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
							<input type=hidden id="mzid${counter.index}" value="${myZone.zoneId}">
							<input type=hidden id="mzname${counter.index}" value="${fn:escapeXml(myZone.zoneName)}">
						</li>
					</c:forEach>
				</ul>
				<div style="clear:both"></div>
			</div>
			<div id="myZonesNavigation" style="margin-right:10px">
				<%-- previous page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=myZonesKeyPrevZOHidden value="${myZonesKeyPrevZO}">
				<input type=hidden id=myZonesKeyPrevZoneIdHidden value="${myZonesKeyPrevZoneId}">
				<c:if test="${myZonesKeyPrevZO == 'NULL'}">
					<div id="getMyZonesPrevDiv" style="width:48%; margin-top:10px; float:left; text-align:left; visibility:hidden">
						<a target="_self" id="getMyZonesPrev" class="nextPrevSmall" style="font-size:12px" href="javascript:">&lt; BACK</a>
					</div>
				</c:if>
				<c:if test="${myZonesKeyPrevZO != 'NULL'}">
					<div id="getMyZonesPrevDiv" style="width:48%; margin-top:10px; float:left; text-align:left">
						<a target="_self" id="getMyZonesPrev" class="nextPrevSmall" style="font-size:12px" href="javascript:">&lt; BACK</a>
					</div>
				</c:if>
				<%-- next page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=myZonesKeyNextZOHidden value="${myZonesKeyNextZO}">
				<input type=hidden id=myZonesKeyNextZoneIdHidden value="${myZonesKeyNextZoneId}">
				<c:if test="${myZonesKeyNextZO == 'NULL'}">
					<div id="getMyZonesNextDiv" style="width:48%; margin-top:10px; margin-bottom:20px; float:right; text-align:right; visibility:hidden">
						<a target="_self" id="getMyZonesNext" class="nextPrevSmall" style="font-size:12px" href="javascript:">MORE &gt;</a>
					</div>
				</c:if>
				<c:if test="${myZonesKeyNextZO != 'NULL'}">
					<div id="getMyZonesNextDiv" style="width:48%; margin-top:10px; margin-bottom:20px; float:right; text-align:right">
						<a target="_self" id="getMyZonesNext" class="nextPrevSmall" style="font-size:12px" href="javascript:">MORE &gt;</a>
					</div>
				</c:if>
				<%-- fields to keep my zones list intact when entire page is refreshed or when a my zone is deleted --%>
				<input type=hidden id=myZonesCountHidden value="${fn:length(myZonesList)}">
				<input type=hidden id=myZonesKeyRefreshZOHidden value="${myZonesKeyRefreshZO}">
				<input type=hidden id=myZonesKeyRefreshZoneIdHidden value="${myZonesKeyRefreshZoneId}">
				<input type=hidden id=myZonesKeyAlternateRefreshZOHidden value="${myZonesKeyAlternateRefreshZO}">
				<input type=hidden id=myZonesKeyAlternateRefreshZoneIdHidden value="${myZonesKeyAlternateRefreshZoneId}">
			</div>
			<div style="clear:both"></div>

			<div class="zoneHeader">TOP ZONES<a class="createZone" href="javascript:" title="Create a New Zone" target="_self"><span style="color:red; font-size:18px; margin-left:20px">+</span></a></div>

			<div id="topZonesList" style="height:600px">
				<ul style="list-style-type:none; padding-left:0px; margin-bottom:0px">
					<c:forEach var="topZone" items="${topZonesList}" varStatus="counter">
						<li class="zoneList topZones" title="${fn:escapeXml(topZone.zoneName)}">
							<div class="zoneName" onclick="enterTopZone(${counter.index})">${fn:escapeXml(topZone.zoneName)}</div>
							<div id="z${counter.index}" class="favorite" style="float:right; width:15px; margin-top:2px"
								onclick="favoriteZone(${counter.index}); this.style.backgroundColor='#ff9696'; setTimeout(function() { z${counter.index}.style.backgroundColor='#ffffff'; }, 2000);"
								title="Add this to My Zones">&nbsp;&nbsp;&nbsp;
							</div>
							<input type=hidden id="tzid${counter.index}" value="${topZone.zoneId}">
							<input type=hidden id="tzname${counter.index}" value="${topZone.zoneName}">
						</li>
					</c:forEach>
				</ul>
				<div style="clear:both"></div>
			</div>
			<div id="topZonesNavigation" style="margin-right:10px; margin-bottom:40px">
				<%-- previous page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=topZonesKeyPrevZOHidden value="${topZonesKeyPrevZO}">
				<input type=hidden id=topZonesKeyPrevZoneIdHidden value="${topZonesKeyPrevZoneId}">
				<c:if test="${topZonesKeyPrevZO == 'NULL'}">
					<div id="getTopZonesPrevDiv" style="width:48%; margin-top:10px; float:left; text-align:left; visibility:hidden">
						<a target="_self" id="getTopZonesPrev" class="nextPrevSmall" style="font-size:12px" href="javascript:">&lt; BACK</a>
					</div>
				</c:if>
				<c:if test="${topZonesKeyPrevZO != 'NULL'}">
					<div id="getTopZonesPrevDiv" style="width:48%; margin-top:10px; float:left; text-align:left">
						<a target="_self" id="getTopZonesPrev" class="nextPrevSmall" style="font-size:12px" href="javascript:">&lt; BACK</a>
					</div>
				</c:if>
				<%-- next page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=topZonesKeyNextZOHidden value="${topZonesKeyNextZO}">
				<input type=hidden id=topZonesKeyNextZoneIdHidden value="${topZonesKeyNextZoneId}">
				<c:if test="${topZonesKeyNextZO == 'NULL'}">
					<div id="getTopZonesNextDiv" style="width:48%; margin-top:10px; float:right; text-align:right; visibility:hidden">
						<a target="_self" id="getTopZonesNext" class="nextPrevSmall" style="font-size:12px" href="javascript:">MORE &gt;</a>
					</div>
				</c:if>
				<c:if test="${topZonesKeyNextZO != 'NULL'}">
					<div id="getTopZonesNextDiv" style="width:48%; margin-top:10px; float:right; text-align:right">
						<a target="_self" id="getTopZonesNext" class="nextPrevSmall" style="font-size:12px" href="javascript:">MORE &gt;</a>
					</div>
				</c:if>
				<%-- fields to keep top zones list intact when entire page is refreshed or when a top zone is deleted --%>
				<input type=hidden id=topZonesCountHidden value="${topZonesCount}">
				<input type=hidden id=topZonesKeyRefreshZOHidden value="${topZonesKeyRefreshZO}">
				<input type=hidden id=topZonesKeyRefreshZoneIdHidden value="${topZonesKeyRefreshZoneId}">
				<input type=hidden id=topZonesKeyAlternateRefreshZOHidden value="${topZonesKeyAlternateRefreshZO}">
				<input type=hidden id=topZonesKeyAlternateRefreshZoneIdHidden value="${topZonesKeyAlternateRefreshZoneId}">
			</div>
			<div style="clear:both"></div>

			<c:if test="${empty fn:trim(post.pageId)}">
				<div style="padding:20px; margin-top:100px">
					<!-- Heatbud-Vertical -->
					<ins class="adsbygoogle"
					     style="display:inline-block;width:160px;height:600px"
					     data-ad-client="ca-pub-3344897177583439"
					     data-ad-slot="2200563309"></ins>
					<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
				</div>
			</c:if>
		</td>
		<%-- End zones --%>

		<td id=mainContent style="vertical-align:top; width:100%; padding:0px 0px 0px 60px">

			<c:if test="${empty fn:trim(post.pageId)}">
				<div style="width:90%; background-color:#87bdd8; border-radius:4px; padding:16px; margin-top:20px">
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
			</c:if>

			<table style="width:100%; border-spacing: 0px">

				<%-- headshot image : zone or post --%>
				<tr><td style="padding:0px">

					<%-- post editing controls --%>
					<div id="postEditControls">
						<c:if test="${pageType == 'POST'}">
							<c:if test="${post.bloggerId == userId}">
								<div style="padding-top:8px; margin-top:30px">
									<span id="editPost" style="color:blue; font-size:15px; cursor:pointer">Edit</span> this post.
								</div>
								<c:if test="${empty post.requestFB && post.publishFlag == 'Y'}">
									<div id="requestFBOuter" style="padding-top:4px">
										<span id="requestFB" style="color:blue; font-size:15px; cursor:pointer">Request</span> to feature this post at <a href="https://www.facebook.com/heatbud">www.facebook.com/heatbud</a>.<br/>
									</div>
								</c:if>
								<c:if test="${post.requestFB == 'Y'}">
									<div style="padding-top:4px; color:rgb(144, 144, 144); font-size:13px">We have received your request to feature this post at www.facebook.com/heatbud.</div>
								</c:if>
								<c:if test="${post.requestFB == 'A'}">
									<div style="padding-top:4px; color:rgb(144, 144, 144); font-size:13px">Congratulations! Your post has been featured at www.facebook.com/heatbud.</div>
								</c:if>
								<c:if test="${post.requestFB == 'R'}">
									<div style="padding-top:4px; color:rgb(144, 144, 144); font-size:13px">Your request to feature this post at www.facebook.com/heatbud has been rejected.</div>
								</c:if>
								<c:if test="${post.publishFlag == 'D'}">
									<div id="draftNote" style="padding-top:4px; color:rgb(144, 144, 144); font-size:12px">*** This Post has been deleted. Only Blogger can see the Content. ***</div>
								</c:if>
							</c:if>
							<c:if test="${post.publishFlag == 'N'}">
								<div id="draftNote" style="padding-top:8px; padding-bottom:8px; color:rgb(144, 144, 144); font-size:12px">*** This post has not yet been published. However, anyone with the URL can still view the content and post comments. ***</div>
							</c:if>
						</c:if>
					</div>
					<div id="postSavedTimeStamp" style="padding-top:8px; display:none"></div>
					<div id="postTitleEditControls" style="padding-top:8px; display:none"></div>

					<%-- post headshot edit controls --%>
					<div id=postHeadshotEditControls style="text-align:left; padding-top:30px; display:none">
						<span style="font-family:Comic Sans MS,Courier; font-size:15px; font-style:italic; color:#939393">
							(4) Headshot Image: An aspect ratio of 2:1 is recommended for better display. Visit <a target="_blank" href="https://commons.wikimedia.org/wiki/Commons:Free_media_resources/Photography">Commons: Free media resources/Photography</a> to obtain free images. If using a tablet, you may upload images directly from the camera.<br/>
							<input class="activeButton" onclick="showImageBox()" type="button" value="Upload/ Select Post Headshot">
						</span>
					</div>

					<%-- post title and headshot image --%>
					<c:if test="${pageType == 'ZONE'}">
						<div id="zoneNameDiv" style="margin-top:30px; margin-left:10px; font-size:16px">
							<div itemprop="name" style="font-family:'Fauna One', Helvetica, Arial; margin-bottom:10px; font-size:24px; font-weight:bold; color:#303030">${fn:escapeXml(zoneName)}</div>
							<div style="margin-top:12px; vertical-align:middle">
								<span class="sharethis-inline-share-buttons" style="display:inline; vertical-align:top"></span>
								<span class="shareElsewhere" style="margin-left:6px" onclick="$('body, #shareElsewhereBox, #modal-background').toggleClass('active'); return false;">Share Zone by URL</span>
							</div>
						</div>
						<div id=headshotImgDiv style="margin-top:20px; margin-left:10px; width:768px; min-height:256px; border:1px solid #BDC7D8; background-size: cover;
							 background-image:url('${zoneHeadshot}');">
			    		</div>
					</c:if>
					<c:if test="${pageType == 'POST'}">
						<div style="margin-top:30px"></div>
						<div id="pageNameDiv">
							<c:if test="${not empty fn:trim(post.pageId)}">
								<a target="_self" href="/${post.pageId}">${post.pageName}</a>
								<span class="favoriteCurrentZone" style="height:17px; line-height:15px; padding:0px 10px; color:#03a87c; border:1px solid #03a87c; border-radius:4px; font-size:13px; cursor:pointer">Follow</span>
							</c:if>
						</div>
						<div itemprop="name" id="postTitle" style="margin-top:10px; margin-bottom:6px; font-family:'Fauna One', Helvetica, Arial; font-size:24px; font-weight:bold; color:#303030">${fn:escapeXml(post.postTitle)}</div>
						<div id="bloggerNameDiv" style="font-size:16px">
							<div style="margin-top:10px">
								<span style="color:#909090">by </span>
								<a target="_self" href="/${post.bloggerId}">
									<span itemprop="author" itemscope itemtype="http://schema.org/Person">
										<span id="bloggerName" itemprop="name">${post.bloggerName}</span>
									</span>
								</a>
								<span style="font-size:14px; color:#909090"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></span>
							</div>
							<div style="margin-top:12px; vertical-align:middle">
								<span class="sharethis-inline-share-buttons" style="display:inline; vertical-align:top"></span>
								<span class="shareElsewhere" style="margin-left:6px" onclick="$('body, #shareElsewhereBox, #modal-background').toggleClass('active'); return false;">Share Blog Post by URL</span>
								<c:if test="${not empty blogger.fbId}">
									<span class="friendFacebook" style="margin-left:6px">
										<a style="color:white" href="https://www.facebook.com/${blogger.fbId}">Friend Blogger on Facebook</a>
									</span>
								</c:if>
								<c:if test="${not empty blogger.googleId}">
									<span class="circleGoogle" style="margin-left:10px">
										<a style="color:white" href="https://plus.google.com/${blogger.googleId}?rel=author">Circle Blogger on Google+</a>
									</span>
								</c:if>
								<span class="friendFacebook" style="margin-left:6px">
									<a style="color:white" href="https://www.facebook.com/heatbud">Like Heatbud on Facebook</a>
								</span>
							</div>
						</div>
						<div id=headshotImgDiv style="margin-top:20px; width:768px; min-height:256px; border:1px solid #BDC7D8; background-size: cover;
							 background-image:url('${fn:replace(post.postHeadshot,'/thumbs/', '/social/')}');">
			    		</div>
					</c:if>
				</td></tr>

				<tr><td>

					<%-- post summary --%>
					<div id="postSummaryMain" style="float:left; margin-top:10px">
						<c:if test="${pageType == 'POST'}">
							<div style="font-size:11px; color:#909090; padding:6px 0px 0px 0px">
								<span><script>document.write(prettyNumber(${post.views}));</script></span>
								<span> UNIQUE VIEWS</span>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;&nbsp;+&nbsp;&nbsp;</span>
								<span id="upVotes"><script>document.write(prettyNumber(${post.upVotes}));</script></span>
								<span style="margin-right:4px"> UP VOTES</span>
								<c:choose>
									<c:when test="${ post.publishFlag != 'Y' }">
										<img id="voteUpImg" alt="Vote Up" style="width:16px; height:18px; border:none" src="/resources/images/vote-up.png" onclick="return false;"/>
									</c:when>
									<c:when test="${currentVote != 1 }">
										<img id="voteUpImg" alt="Vote Up" title="Vote Up" style="width:16px; height:18px; border:none; cursor:pointer" src="/resources/images/vote-up.png" onclick="voteUpPost()"/>
									</c:when>
									<c:otherwise>
										<img id="voteUpImg" alt="Vote Up" title="Your current vote: UP" style="width:16px; height:18px; border:none" src="/resources/images/vote-up.png" onclick="return false;"/>
									</c:otherwise>
								</c:choose>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;&nbsp;-&nbsp;&nbsp;</span>
								<span id="downVotes"><script>document.write(prettyNumber(${post.downVotes}));</script></span>
								<span style="margin-right:4px"> DOWN VOTES</span>
								<c:choose>
									<c:when test="${ post.publishFlag != 'Y' }">
										<img id="voteDownImg" alt="Vote Down" style="width:16px; height:18px; border:none" src="/resources/images/vote-down.png" onclick="return false;"/>
									</c:when>
									<c:when test="${currentVote != -1 }">
										<img id="voteDownImg" alt="Vote Down" title="Vote Down" style="width:16px; height:18px; border:none; cursor:pointer" src="/resources/images/vote-down.png" onclick="voteDownPost()"/>
									</c:when>
									<c:otherwise>
										<img id="voteDownImg" alt="Vote Down" title="Your current vote: DOWN" style="width:16px; height:18px; border:none" src="/resources/images/vote-down.png" onclick="return false;"/>
									</c:otherwise>
								</c:choose>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;&nbsp;+&nbsp;&nbsp;</span>
								<span id="comments"><script>document.write(prettyNumber(${post.comments}));</script></span>
								<span style="margin-right:4px"> COMMENTS </span>
								<a href="#postComments" target="_self"><img id="commentsImg" alt="Comments" title="Navigate to Comments section" style="width:16px; height:16px; border:none" src="/resources/images/comment-post.gif"/></a>
								<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;&nbsp;=&nbsp;&nbsp;</span>
								<span id=postHI style="font-size:20px"><script>document.write(prettyNumber(${post.hi}));</script></span>
								<span style="font-size:13px"> HEAT INDEX</span>
								<span style="cursor:pointer" onclick="$('body, #whatIsHIBox, #modal-background').toggleClass('active');"><img style="height:16px" alt="What is Heat Index?" src="/resources/images/whatis.png"></span>
							</div>
						</c:if>
					</div>
				</td></tr>

				<tr style="vertical-align:top"><td style="width:100%">

					<c:if test="${pageType == 'ZONE'}">

						<%-- zone headshot edit controls --%>
						<c:if test="${isAdmin == 'true'}">
							<div id=zoneHeadshotEditControls style="width:100%">
								<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Zone Headshot</span></div>
								<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
									<div>
										<input class="activeButton" onclick="showImageBox()" type="button" value="Upload/ Update Zone Headshot">
										<div style="font-family:Comic Sans MS,Courier; font-size:16px; font-style:italic; color:#a0a0a0; margin-top:5px">
											An aspect ratio of 2:1 is recommended for better display. Visit <a target="_blank" href="https://commons.wikimedia.org/wiki/Commons:Free_media_resources/Photography">Commons: Free media resources/Photography</a> to obtain free images. If you are editing from a tablet, click the button below to grab images from your camera.
										</div>
									</div>
								</div>
							</div>
							<div style="clear:both"></div>
						</c:if>

						<%-- zone admins --%>
						<div id=zoneAdmins style="width:100%">
							<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Admins</span></div>
							<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
								<c:forEach items="${admins}" var="ablogger" varStatus="loopCounter">
									<div style="margin-left:5px; float:left">
										<div>
											<c:if test="${not empty ablogger.profilePhoto}">
												<a target="_self" href="/${ablogger.entityId}">
													<img alt="${ablogger.entityId}" title="${ablogger.entityId}" class="adminThumb" src="${ablogger.profilePhoto}">
												</a>
											</c:if>
											<c:if test="${empty ablogger.profilePhoto}">
												<a target="_self" href="/${ablogger.entityId}">
													<img alt="${ablogger.entityId}" title="${ablogger.entityId}" class="adminThumb" src="/resources/images/def-blogger-photo.jpg">
												</a>
											</c:if>
										</div>
										<div style="font-size:18px; font-weight:bold">
											<a target="_self" href="/${ablogger.entityId}">${ablogger.entityName}</a>
										</div>
										<c:if test="${isAdmin == 'true'}">
											<div style="font-size:13px">
												<a onclick="removeAdmin('${ablogger.entityId}','${fn:escapeXml(ablogger.entityName)}')" target="_self" href="javascript:">Remove as Admin?</a>
											</div>
										</c:if>
									</div>
								</c:forEach>
								<c:if test="${empty admins}">
									<div style="margin-left:5px; float:left">No Admins for this Zone.</div>
								</c:if>
								<div style="clear:both"></div>
							</div>
							<div id="adminMessage" style="margin-left:10px; color:red">&nbsp;</div>
						</div>

						<%-- zone admin requests --%>
						<div id=zoneAdminRequests style="width:100%">
							<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Admin Requests</span></div>
							<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
								<c:if test="${isAdmin == 'true'}">
									<c:forEach items="${adminRequests}" var="rblogger" varStatus="loopCounter">
										<div style="margin-left:5px; float:left">
											<div>
												<c:if test="${not empty rblogger.profilePhoto}">
													<a href="/${rblogger.entityId}">
														<img alt="${rblogger.entityId}" title="${rblogger.entityId}" class="adminThumb" src="${rblogger.profilePhoto}">
													</a>
												</c:if>
												<c:if test="${empty rblogger.profilePhoto}">
													<a href="/${rblogger.entityId}">
														<img alt="${rblogger.entityId}" title="${rblogger.entityId}" class="adminThumb" src="/resources/images/def-blogger-photo.jpg">
													</a>
												</c:if>
											</div>
											<div style="font-size:13px">
												<a onclick="approveAdmin('${rblogger.entityId}','${fn:escapeXml(rblogger.entityName)}')" target="_self" href="javascript:">Approve ${rblogger.entityName}?</a>
											</div>
										</div>
									</c:forEach>
									<c:if test="${empty adminRequests}">
										<div style="margin-left:5px; float:left">
											No Admin Requests for this Zone.
										</div>
									</c:if>
								</c:if>
								<c:if test="${isAdmin != 'true' && isRequestedAdmin != 'true'}">
									<div style="margin-left:5px; float:left">
										<c:if test="${empty admins}">
											<span class="becomeAdmin" style="letter-spacing:1px; cursor:pointer; color:#2A5D84">Become Admin?</span>
										</c:if>
										<c:if test="${not empty admins}">
											<span class="requestAdmin" style="letter-spacing:1px; cursor:pointer; color:#2A5D84">Request to be Admin?</span>
										</c:if>
									</div>
								</c:if>
								<c:if test="${isAdmin != 'true' && isRequestedAdmin == 'true'}">
									<div style="margin-left:5px; float:left">
										<span style="letter-spacing:1px">Your Admin Request is waiting for approval.</span>
									</div>
								</c:if>
								<div style="clear:both"></div>
							</div>
							<div id="adminRequestsMessage" style="margin-left:10px; color:red">&nbsp;</div>
						</div>

						<%-- zone description --%>
						<div id=zoneDesc style="width:100%">
							<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Description</span></div>
							<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
								<div id=zoneDescDiv style="padding-bottom:10px">${zoneDesc}</div>
								<c:if test="${isAdmin == 'true'}">
									<div id=editZoneDesc style="width:100%; margin-top:3px; margin-bottom:3px; text-align:right"><a>Edit Description</a></div>
								</c:if>
							</div>
						</div>
						<div style="clear:both"></div>
		
						<%-- zone stats --%>
						<div id=zoneStatsDiv style="width:100%">
							<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Statistics</span></div>
							<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
								<div style="font-weight: bold">Zone Heat Index: ${zoneHI} <img style="width:10px; height:16px; border:none" src="/resources/images/favicon.ico"/></div>
								<div>Posts: ${posts}</div>
								<div>Comments: ${comments}</div>
							</div>
						</div>
						<div style="clear:both"></div>

						<%-- zone who --%>
						<div id=zoneWhoDiv style="width:100%">
							<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Who can post in this zone?</span></div>
							<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
								<c:if test="${isAdmin != 'true'}">
									<c:if test="${zoneWho == 'E'}">
										<input type="radio" name="zoneWhoRadio" checked disabled>Any Heatbud User<br>
										<input type="radio" name="zoneWhoRadio" disabled>Only the Admins<br>
									</c:if>
									<c:if test="${zoneWho == 'A'}">
										<input type="radio" name="zoneWhoRadio" disabled>Any Heatbud User<br>
										<input type="radio" name="zoneWhoRadio" checked disabled>Only the Admins<br>
									</c:if>
								</c:if>
								<c:if test="${isAdmin == 'true'}">
									<c:if test="${zoneWho == 'E'}">
										<input type="radio" name="zoneWhoRadio" onchange="document.getElementById('zoneWhoInput').value='E'" checked>Any Heatbud User<br>
										<input type="radio" name="zoneWhoRadio" onchange="document.getElementById('zoneWhoInput').value='A'">Only the Admins<br>
										<input type="hidden" id="zoneWhoInput" value="E">
									</c:if>
									<c:if test="${zoneWho == 'A'}">
										<input type="radio" name="zoneWhoRadio" onchange="document.getElementById('zoneWhoInput').value='E'">Any Heatbud User<br>
										<input type="radio" name="zoneWhoRadio" onchange="document.getElementById('zoneWhoInput').value='A'" checked>Only the Admins<br>
										<input type="hidden" id="zoneWhoInput" value="A">
									</c:if>
									<div id=editZoneWho style="width:100%; margin-top:3px; margin-bottom:3px; text-align:right"><a>Save changes</a></div>
									<div id="zoneWhoMessage" style="color:red">&nbsp;</div>
								</c:if>
							</div>
						</div>
						<div style="clear:both"></div>

						<%-- top three posts in the zone --%>
						<div id=zoneTopPostsDiv>
							<div class="profileLabel h1" style="width:94%; margin-top:20px; margin-left:10px"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Top posts in this Zone</span></div>
							<div class="profileDiv" style="white-space:normal; width:94%; margin-left:10px">
								<div>
									<table style="border-spacing:8px">
										<c:if test="${not empty prevPost}">
											<c:set var="votes" value="${prevPost.upVotes-prevPost.downVotes}"/>
											<tr>
												<td style="vertical-align:top">
													<div style="font-size:200%; text-align:center">${votes}</div>
													<div style="font-size:11px; color:#909090">votes</div>
												</td>
												<td style="vertical-align:top">
													<div style="font-size:200%; text-align:center">${prevPost.comments}</div>
													<div style="font-size:11px; color:#909090">comments</div>
												</td>
												<td style="vertical-align:top; width:100%">
													<div style="margin-left:10px">
														<div style="font-weight:bold">
															<a target="_self" href="/post/${prevPost.postId}">${prevPost.postTitle}</a>
														</div>
														<div style="font-size:12px; color:#909090">
															<span><a target="_self" href="/${prevPost.bloggerId}">${fn:escapeXml(prevPost.bloggerName)}</a></span>
															<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
															<span style="color:#909090"><script>document.write(new Date(${prevPost.updateDate}).toLocaleString());</script></span>
														</div>
														<div>${prevPost.postSummary}</div>
													</div>
												</td>
											</tr>
										</c:if>
										<c:if test="${not empty post}">
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
												<td style="vertical-align:top; width:100%">
													<div style="margin-left:10px">
														<div style="font-weight:bold">
															<a target="_self" href="/post/${post.postId}">${post.postTitle}</a>
														</div>
														<div style="font-size:12px; color:#909090">
															<span><a target="_self" href="/${post.bloggerId}">${fn:escapeXml(post.bloggerName)}</a></span>
															<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
															<span style="color:#909090"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></span>
														</div>
														<div>${post.postSummary}</div>
													</div>
												</td>
											</tr>
										</c:if>
										<c:if test="${not empty nextPost}">
											<c:set var="votes" value="${nextPost.upVotes-nextPost.downVotes}"/>
											<tr>
												<td style="vertical-align:top">
													<div style="font-size:200%; text-align:center">${votes}</div>
													<div style="font-size:11px; color:#909090">votes</div>
												</td>
												<td style="vertical-align:top">
													<div style="font-size:200%; text-align:center">${nextPost.comments}</div>
													<div style="font-size:11px; color:#909090">comments</div>
												</td>
												<td style="vertical-align:top; width:100%">
													<div style="margin-left:10px">
														<div style="font-weight:bold">
															<a target="_self" href="/post/${nextPost.postId}">${nextPost.postTitle}</a>
														</div>
														<div style="font-size:12px; color:#909090">
															<span><a target="_self" href="/${nextPost.bloggerId}">${fn:escapeXml(nextPost.bloggerName)}</a></span>
															<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
															<span style="color:#909090"><script>document.write(new Date(${nextPost.updateDate}).toLocaleString());</script></span>
														</div>
														<div>${nextPost.postSummary}</div>
													</div>
												</td>
											</tr>
										</c:if>
										<tr><td colspan="3">
											<c:if test="${empty prevPost}">
												<div>Be the first to post in this zone!</div>
											</c:if>
										</td></tr>
										<tr><td colspan="3">
											<c:if test="${not empty prevPost}">
												<div style="width:100%; text-align:right; font-size:16px"><a target="_self" href="/post/${prevPost.postId}">Start reading posts in this zone...</a></div>
											</c:if>
										</td></tr>
									</table>
								</div>
								<div style="clear:both"></div>
							</div>
						</div>
					</c:if>

					<%-- Begin post heading & content --%>
					<div style="width:800px; margin-top:8px">

						<%-- post content --%>
						<c:if test="${pageType == 'ZONE'}">
							<div id="postContent" style="visibility:hidden; min-width:700px; margin-top:8px"></div>
						</c:if>
						<c:if test="${pageType == 'POST'}">
							<c:if test="${userId == 'kakatur'}">
								<input type=text/>
								<div id=purgePost class="nextPrevSmall" style="cursor:pointer" onclick="purgePost(); return false;">PURGE</div>
							</c:if>
							<div id="postContent" style="margin-top:10px">
								<c:choose>
									<c:when test="${post.bloggerId == userId || post.publishFlag != 'D' }">
										<c:import url="https://heatbudposts.s3.amazonaws.com/${post.postId}"/>
									</c:when>
									<c:otherwise>
										<span style="color:rgb(144, 144, 144); font-size:12px"><b>This post has been deleted.</b></span>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</div>
					<%-- End post heading & content --%>

					<c:if test="${pageType == 'POST'}">
						<%-- Begin post comments --%>
						<div id="postComments" style="width:800px; margin-top:40px">

							<%-- Display Comments --%>
							<div>
								<div>
									<div style="float:left" class="h1">Comments:</div>
									<div style="float:right; margin-bottom:15px; padding-right:5px">
										<%-- Users can follow comments only on a published post. Users can unfollow comments any time. --%>
										<c:if test="${followerId == 'NULL'}">
											<c:if test="${ post.publishFlag == 'Y' }">
												<span id=followCommentsSpan style="font-size:12px; color:#909090">${cfCount} blogger(s) are following this post, but not you. </span><a target="_self" id=followCommentsA href="javascript:" style="font-size:10px">Follow?</a>
											</c:if>
											<c:if test="${ post.publishFlag != 'Y' }">
												<span id=followCommentsSpan style="font-size:12px; color:#909090">&nbsp;</span><a target="_self" id=followCommentsA href="javascript:" style="font-size:10px">&nbsp;</a>
											</c:if>
										</c:if>
										<c:if test="${followerId != 'NULL'}">
											<span id=followCommentsSpan style="font-size:12px; color:#909090">${cfCount} blogger(s) are following this post, including yourself. </span><a target="_self" id=followCommentsA href="javascript:" style="font-size:10px">Unfollow?</a>
										</c:if>
									</div>
								</div>
								<div style="clear:both"></div>
								<div style="margin-top:5px; color:red" id="commentsRetMessage"></div>
								<div style="margin-right:10px">
									<div id="commentsDiv">
										<c:if test="${empty commentsList}">
											<c:if test="${ post.publishFlag == 'Y' || post.publishFlag == 'N' }">
												<div style="color:rgb(144, 144, 144); margin-top:30px">No comments yet.</div>
											</c:if>
										</c:if>
										<c:if test="${not empty commentsList}">
											<c:forEach var="comment" items="${commentsList}">
												<%-- split commentDate into thanked, parentCommentDate and commentDate --%>
												<c:set var="thanked" value="${fn:substring(comment.commentDate,0,1)}"/>
												<c:set var="pcd" value="${fn:substring(comment.commentDate,1,14)}"/>
												<c:set var="cd" value="${fn:substring(comment.commentDate,14,27)}"/>
												<%-- set header and indent css styles for parent comments and sub-comments --%>
												<c:if test="${cd == '9999999999999'}">
													<c:set var="cd" value="${pcd}"/>
													<c:set var="commentHeaderCSS" value="margin-top:16px; background-color:#F5F5F5; border-bottom:1px solid #BBBBBB; padding:4px;"/>
													<c:set var="commentIndentCSS" value=""/>
												</c:if>
												<c:if test="${cd != pcd}">
													<c:set var="cd" value="${9999999999999-cd}"/>
													<c:set var="commentHeaderCSS" value=""/>
													<c:set var="commentIndentCSS" value="margin-left:15px;"/>
												</c:if>
												<%-- commenter name, date, delete icon, thank comments --%>
												<div style="font-size:12px; ${commentIndentCSS} ${commentHeaderCSS}">
													<span id="thankComment${comment.commentDate}Span">
														<c:if test="${thanked == '2' && cd == pcd }">
															<img alt="thanked by the blogger" style="max-height:15px; margin-right:10px; border:none" src="/resources/images/thanked.jpg"/>&nbsp;
														</c:if>
													</span>
													<span style="color:#909090"><script>document.write(new Date(${cd}).toLocaleString());</script></span>
													<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>
													<a target="_self" href="/${comment.commenterId}">${comment.commenterName}</a>
												</div>
												<%-- comment text --%>
												<input id="originalComment${comment.commentDate}" type=hidden>
												<div id="comment${comment.commentDate}Div" style="${commentIndentCSS} margin-top:4px; white-space:pre-line">${comment.commentText}</div>
												<textarea id="comment${comment.commentDate}Input" rows="4" cols="95" style="display:none"></textarea>
												<%-- reply, thank, edit, delete or report --%>
												<div style="${commentIndentCSS} font-size:12px; margin-top:8px; margin-bottom:6px">
													<c:if test="${cd == pcd}">
														<a target="_self" href="javascript:" onclick="showCommentBox('${comment.commentDate}','${pcd}','${thanked}')" title="Reply to this thread">Reply to this thread</a>
														<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>
														<c:if test="${userId == post.bloggerId}">
															<c:if test="${thanked == '1'}">
																<a id="thankComment${comment.commentDate}A" target="_self" href="javascript:" onclick="thankComment('${comment.commentDate}','2','${comment.commenterId}')">Thank</a>
															</c:if>
															<c:if test="${thanked == '2'}">
																<a id="thankComment${comment.commentDate}A" target="_self" href="javascript:" onclick="thankComment('${comment.commentDate}','1','${comment.commenterId}')">Unthank</a>
															</c:if>
															<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>
														</c:if>
													</c:if>
													<c:if test="${userId == comment.commenterId}">
														<span id="editComment${comment.commentDate}Div"><a id="editComment${comment.commentDate}A" target="_self" href="javascript:" onclick="editComment('${comment.commentDate}')" title="Edit this comment">Edit</a></span>
														<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>
													</c:if>
													<c:if test="${userId == comment.commenterId || userId == post.bloggerId}">
														<a target="_self" href="javascript:" onclick="deleteComment('${comment.commentDate}')" title="Delete this comment">Delete</a>
														<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>
													</c:if>
													<a id="reportComment${comment.commentDate}A" target="_self" href="javascript:" onclick="reportComment('${comment.commentDate}')" title="Report this comment as spam">Report</a>
													<c:if test="${cd == pcd}">
														<div id="commentBox${pcd}" style="margin-top:5px">&nbsp;</div>
													</c:if>
												</div>
											</c:forEach>
										</c:if>
									</div>
									<div id="commentsNavigation" style="margin-left:4px; margin-top:5px">
										<%-- previous page link will be hidden on the first page --%>
										<input type=hidden id=commentsKeyPrevHidden value="NULL">
										<div id="getCommentsPrevDiv" style="width:48%; margin-top:10px; float:left; text-align:left; visibility:hidden">
											<a target="_self" id="getCommentsPrev" class="nextPrevSmall" href="javascript:">&lt; BACK</a>
										</div>
										<%-- next page link will be set to visible if the key is not NULL --%>
										<input type=hidden id=commentsKeyNextHidden value="${commentsKeyNext}">
										<c:if test="${commentsKeyNext != 'NULL'}">
											<div id="getCommentsNextDiv" style="width:48%; margin-top:10px; float:right; text-align:right">
												<a target="_self" id="getCommentsNext" class="nextPrevSmall" href="javascript:">MORE &gt;</a>
											</div>
										</c:if>
									</div>
								</div>
								<div style="clear:both">&nbsp;</div>
							</div>

							<%-- Post Comment --%>
							<div>
								<div id="postComment">
									<div style="margin-top:8px" class="h1">Post a Comment:</div>
									<div style="float:left">
										<textarea id=textComment0 rows="4" cols="95" style="width:740px" placeholder="Comments must be relevant to the post content. No Ads or promotions. Heatbud takes spamming seriously."></textarea><br>
										<div style="float:right; padding-right:6px; margin-top:3px">
											<c:if test="${ userId == 'NULL' }">
												<input id=email0 name="email" type="text" style="border: 2px solid #BDC7D8; color:rgb(145, 145, 145); letter-spacing:1px; padding:4px; width:310px; border-radius:2px" placeholder="Email Address (you will be asked to verify)">
												<input id=passwd0 type="password" style="border: 2px solid #BDC7D8; color:rgb(145, 145, 145); letter-spacing:1px; padding:4px; width:310px; border-radius:2px" placeholder="Password (optional)">
											</c:if>
											<input id=postComment0 class="activeButton" type="button" onclick="postComment(0,0,1)" value="Post Comment">
										</div>
										<div id="commentsError0" class="error">&nbsp;</div>
									</div>
									<div style="clear:both"></div>
								</div>
							</div>

						</div>
						<%-- End post comments --%>

						<%-- Begin Related Posts --%>
						<div style="margin-top:20px" class="h1">Related Posts:</div>
						<div id="relatedPostsDiv">&nbsp;</div>
						<%-- End Related Posts --%>

					</c:if>

					<c:if test="${empty fn:trim(post.pageId)}">
						<div style="padding:20px">
							<!-- Heatbud-Horizontal -->
							<ins class="adsbygoogle"
							     style="display:inline-block;width:728px;height:90px"
							     data-ad-client="ca-pub-3344897177583439"
							     data-ad-slot="5851386905">
							</ins>
							<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
						</div>
					</c:if>

				</td>
				</tr>
			</table>

		</td>
		</tr>
	</table>

	<div style="clear:both; margin-top:20px">&nbsp;</div>

	<%-- Begin footer - Note: This page has customized footer that includes target keyword --%>
	<div class="footer">
		<div style="float: right; margin-right: 40px">
			<a target="_self" href="/top/posts-trending-now">Home</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_self" href="/do/help">Help Center</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_self" href="/do/privacy">Privacy &amp; Terms</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_self" href="/do/partnerships">Partnerships</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_self" href="/do/careers">Careers</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_self" href="/do/contact">Contact Us</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_self" href="/do/newsletters">Newsletters</a>
		</div>
	</div>
	<%-- End footer --%>

	<%-- Page Title and other async functions --%>
	<c:if test="${pageType == 'POST'}">
		<script>
			window.history.replaceState("Heatbud", "Heatbud | ${fn:escapeXml(zoneName)} - ${fn:escapeXml(post.postTitle)}", "/post/${post.postId}");
			getRelatedPosts('${post.rpcPeriod}');
		</script>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<script>
			window.history.replaceState("Heatbud", "Heatbud | ${zoneName}", "/zone/${zoneId}");
		</script>
	</c:if>

	<!-- CKEditor includes -->
	<script src="/resources/js/ckeditor/ckeditor.js"></script>

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