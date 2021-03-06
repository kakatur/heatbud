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
	<title>Heatbud - Settings</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-settings-min.js?20180530"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head>
<body style="position: relative">

	<input id=userIdHidden type=hidden value="${userId}"/>

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

	<div style="padding:48px 10px 10px 10px">
		<input id="originalFirstName" type=hidden>
		<input id="originalLastName" type=hidden>
		<input id="originalEmailAddress" type=hidden>
		<input id="originalFbId" type=hidden>
		<input id="originalGoogleId" type=hidden>
	</div>

	<%-- Begin page content --%>
	<table style="width: 100%; border-spacing: 2px">
		<tr style="width: 100%">
			<td style="width: 80%; vertical-align: top; padding: 0px">

				<%-- Begin Account Settings --%>
				<div class="h1" style="margin-left:30px; margin-top:40px; margin-bottom:30px; font-size:15px; font-weight:bold">
					<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom: 4px">ACCOUNT SETTINGS</span>
				</div>
				<div style="margin-left: 40px">

					<div class="accountLabel">First Name:</div>
					<div id="firstNameDiv" style="float: left">${firstName}</div>
					<div style="float: left">
						<input id="firstNameInput" type=text
							style="width: 150px; display: none">
					</div>
					<div id="editFirstNameDiv" style="float: left">
						&nbsp;&nbsp;<a id="editFirstNameA" onclick="editFirstName()"
							href="javascript:">Edit</a>
					</div>
					<div style="clear: both"></div>
					<div style="margin-left: 160px; margin-top: 10px; color: red"
						id="firstNameRetMessage"></div>
					<br />

					<div class="accountLabel">Last Name:</div>
					<div id="lastNameDiv" style="float:left">${lastName}</div>
					<div style="float: left"> <input id="lastNameInput" type=text style="width:150px; display:none"> </div>
					<div id="editLastNameDiv" style="float:left">&nbsp;&nbsp;<a id="editLastNameA" onclick="editLastName()" href="javascript:">Edit</a></div>
					<div style="clear: both"></div>
					<div style="margin-left: 160px; margin-top: 10px; color: red" id="lastNameRetMessage"></div>
					<br>

					<div class="accountLabel">Email Address:</div>
					<div id="emailAddressDiv" style="float: left">${emailAddress}</div>
					<div style="float:left"><input id="emailAddressInput" type=text style="width:150px; display:none"></div>
					<div id="editEmailAddressDiv" style="float:left">&nbsp;&nbsp;<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Edit</a></div>
					<div style="clear: both"></div>
					<div style="margin-left:160px; margin-top:10px; color:red" id="emailAddressRetMessage"></div>
					<br>

					<div class="accountLabel" id=passwordLabel>Password:</div>
					<div id=passwordDiv style="float:left">*******</div>
					<div style="float:left"><input id="passwordInput" type=password style="width:150px; display:none"></div>
					<div id="editPasswordDiv" style="float:left">&nbsp;&nbsp;<a id="editPasswordA" onclick="editPassword()" href="javascript:">Edit</a></div>
					<div style="clear:both"></div>
					<div class="accountLabel" id="newPass1Label" style="display: none">New Password</div>
					<div style="float:left"><input id="newPass1Input" type=password style="width: 150px; display: none"></div>
					<div style="clear:both"></div>
					<div class="accountLabel" id="newPass2Label" style="display: none">Re-type New Password</div>
					<div style="float:left"><input id="newPass2Input" type=password style="width: 150px; display: none"></div>
					<div style="clear:both"></div>
					<div style="margin-left:160px; margin-top:10px; color:red" id="passwordRetMessage"></div>
					<br>
					<br>
				</div>
				<%-- End Account Settings --%>

				<%-- Begin Social Settings --%>
				<div
					style="margin-left: 30px; margin-bottom: 30px; font-size: 15px; font-weight: bold"
					class="h1">
					<span
						style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom: 4px">SOCIAL
						SETTINGS</span>
				</div>
				<div style="margin-left: 40px">
					<div style="margin-top: -10px; margin-bottom: 20px; color: #4e9258">
						We use these Id's to tell Google and Facebook about your
						authorship. To learn more, visit
						<a href="/do/help/popular/seo">Search Engine Optimization</a>.
					</div>

					<div class="accountLabel">Facebook Id:</div>
					<div style="float: left">https://www.facebook.com/</div>
					<div id="fbIdDiv" style="float: left">${fbId}</div>
					<div style="float: left">
						<input id="fbIdInput" type=text
							style="width: 150px; display: none">
					</div>
					<div id="editFbIdDiv" style="float: left">
						&nbsp;&nbsp;<a id="editFbIdA" onclick="editFbId()"
							href="javascript:">Edit</a>
					</div>
					<div style="clear: both"></div>
					<div style="margin-left: 160px; margin-top: 10px; color: red"
						id="fbIdRetMessage"></div>
					<br />

					<div class="accountLabel">Google+ Id:</div>
					<div style="float: left">https://plus.google.com/</div>
					<div id="googleIdDiv" style="float: left">${googleId}</div>
					<div style="float: left">
						<input id="googleIdInput" type=text
							style="width: 150px; display: none">
					</div>
					<div id="editGoogleIdDiv" style="float: left">
						&nbsp;&nbsp;<a id="editGoogleIdA" onclick="editGoogleId()"
							href="javascript:">Edit</a>
					</div>
					<div style="clear: both"></div>
					<div style="margin-left: 160px; margin-top: 10px; color: red"
						id="googleIdRetMessage"></div>
					<br />

				</div>
				<%-- End Social Settings --%>

			</td>

		</tr>
	</table>

	<div style="clear:both">&nbsp;</div>

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
