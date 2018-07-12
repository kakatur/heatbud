<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud - Notifications</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css?5" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-notifications-min.js?5"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head>
<body style="position: relative">

	<%-- Begin header --%>
	<table class="header"><tr style="width:100%">
		<td style="float:left">
			<a href="/"><img alt="Heatbud logo" style="width:140px; padding-top:2px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/></a>
		</td>
		<td style="float:right; font-size:13px; padding-top:14px; padding-bottom:6px">
			<div style="float:left; margin-right:8px"><a href="/top/posts-trending-now" class="mainSelection">TOP CHARTS</a></div>
			<div style="float:left; margin-right:8px"><a href="/post/singing-bowls-pashmina-the-softness-of-gold" class="mainSelection">BLOG POSTS</a></div>
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

	<div style="padding:48px 10px 10px 10px">
		<input id="originalFollowWhenPublished" type=hidden value="${followWhenPublished}">
		<input id="originalFollowWhenCommented" type=hidden value="${followWhenCommented}">
		<input id="originalNotifyWhenThanked" type=hidden value="${notifyWhenThanked}">
		<input id="originalRemindDraftPost" type=hidden value="${remindDraftPost}">
		<input id="originalWeeklyNewsLetter" type=hidden value="${weeklyNewsLetter}">
	</div>

	<%-- Begin page content --%>
	<table style="width:100%; border-spacing:2px">
		<tr style="width:100%">
			<td style="width:80%; vertical-align:top; padding:0px">

				<%-- Begin Notifications --%>
				<div class="h1" style="margin-left:30px; margin-top:40px; margin-bottom:30px; font-size:15px; font-weight:bold">
					<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom: 4px">NOTIFICATIONS</span>
				</div>
				<div style="margin-left:40px; line-height:25px">
					<div>
						<c:if test="${followWhenPublished == 'Y'}">
							<input id="followWhenPublished" type="checkbox" checked value="${followWhenPublished}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When I publish a post, make me a follower on the comments.</span>
						</c:if>
						<c:if test="${followWhenPublished == 'N'}">
							<input id="followWhenPublished" type="checkbox" value="${followWhenPublished}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When I publish a post, make me a follower on the comments.</span>
						</c:if>
					</div>
					<div>
						<c:if test="${followWhenCommented == 'Y'}">
							<input id="followWhenCommented" type="checkbox" checked value="${followWhenCommented}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When I comment on a post, make me a follower on the subsequent comments.</span>
						</c:if>
						<c:if test="${followWhenCommented == 'N'}">
							<input id="followWhenCommented" type="checkbox" value="${followWhenCommented}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When I comment on a post, make me a follower on the subsequent comments.</span>
						</c:if>
					</div>
					<div>
						<c:if test="${notifyWhenThanked == 'Y'}">
							<input id="notifyWhenThanked" type="checkbox" checked value="${notifyWhenThanked}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When a blogger thanks my comment, send me an email.</span>
						</c:if>
						<c:if test="${notifyWhenThanked == 'N'}">
							<input id="notifyWhenThanked" type="checkbox" value="${notifyWhenThanked}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When a blogger thanks my comment, send me an email.</span>
						</c:if>
					</div>
					<div>
						<c:if test="${remindDraftPost == 'Y'}">
							<input id="remindDraftPost" type="checkbox" checked value="${remindDraftPost}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When I create a draft post and don't publish it for five days, send me a reminder.</span>
						</c:if>
						<c:if test="${remindDraftPost == 'N'}">
							<input id="remindDraftPost" type="checkbox" value="${remindDraftPost}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>When I create a draft post and don't publish it for five days, send me a reminder.</span>
						</c:if>
					</div>
					<div>
						<c:if test="${weeklyNewsLetter == 'Y'}">
							<input id="weeklyNewsLetter" type="checkbox" checked value="${weeklyNewsLetter}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>Send me the Weekly News Letter with announcements and top posts.</span>
						</c:if>
						<c:if test="${weeklyNewsLetter == 'N'}">
							<input id="weeklyNewsLetter" type="checkbox" value="${weeklyNewsLetter}" onchange="if (this.checked) this.value='Y'; else this.value='N';"/>&nbsp;<span>Send me the Weekly News Letter with announcements and top posts.</span>
						</c:if>
					</div>
					<div>
						<input class="activeButton" style="height:25px; margin-top:10px" onclick="saveNotifications()" type="button" value="Save">
						<div style="margin-top:10px; color:red" id="saveNotifRetMessage"></div>
					</div>
				</div>
				<%-- End Notifications --%>

				<%-- Begin Follow Comments --%>
				<div class="h1" style="margin-left:30px; margin-top:40px; margin-bottom:30px; font-size:15px; font-weight:bold">
					<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom: 4px">FOLLOW COMMENTS</span>
				</div>
				<div style="margin-left:40px">
					<div style="float:left">You are following comments on <span id=followCommentsCount style="font-weight: bold">${countFC}</span> posts.</div>
					<div style="float:left; margin-left:20px"><a href="javascript:" onclick="unfollowAllComments()">Unfollow All?</a></div>
					<div style="clear: both">&nbsp;</div>
					<div style="margin-top:-10px; color:#4e9258">To stop following comments on a specific post, visit the Comments section in the Post page.</div>
					<div style="margin-top:10px; color:red" id="unfollowAllCommentsRetMessage"></div>
				</div>
				<%-- End Follow Comments --%>

			</td>

		</tr>
	</table>

	<div style="clear: both">&nbsp;</div>

	<div style="margin-top:50px; text-align:center; padding:20px">
		<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
		<!-- Heatbud-Horizontal -->
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

</body>
</html>