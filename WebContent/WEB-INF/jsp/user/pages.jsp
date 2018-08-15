<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud | Pages</title>
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
	<script src="/resources/js/heatbud-pages-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head>
<body style="position: relative">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- create page box --%>
	<div id=createPageBox class="modal-box" style="width:420px; height:440px; margin-left:-210px; margin-top:-240px; padding-left:35px">
		<div class="h1">Create Blog Page for your Business</div>
		<div style="color:#888888; margin-top:8px">https://www.heatbud.com/<span style="color:#5544ff; font-weight:bold">page-id</span></div><br/>
		<div><input id=createPageIdInput type=text placeholder=" Page ID (lowercase letters, numbers and hyphen only)" style="width:340px"/></div>
		<div style="margin-top:6px"><input id=createPageNameInput type=text placeholder=" Page Name" style="width:300px"/></div>
		<div style="margin-top:6px"><input id=createPagePhoneInput type=text placeholder=" Page Phone (example: 123-456-7890)" style="width:340px"/></div>
		<div style="margin-top:6px"><input id=createPageEmailInput type=text placeholder=" Page Email (example: info@yourwebsite.com)" style="width:340px"/></div>
		<div style="margin-top:6px"><textarea id=createPageAboutInput rows="3" cols="40" placeholder="About Us (describe your page in a sentence or two)"></textarea></div>
		<div id="createPageMessage" style="color:red">&nbsp;</div>
		<div style="color:#939393">You can customize the page later by adding your business logo, website address etc.</div>
		<div style="color:#939393; margin-top:8px">You will not be able to publish blog posts until you make a payment. Visit <a target="_blank" href="/do/help/main/pricing">Heatbud Pricing</a> for details.</div><br/>
		<input id="createPageButton" class="activeButton" style="padding:4px 20px 4px 20px" onclick="createPage()" type="button" value="Create">
		<input onclick="$('body, #createPageBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
	</div>

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
	<div style="padding:48px 10px 10px 10px"></div>
	<input id=userIdHidden type=hidden value="${userId}"/>

		<%-- Begin Blogger Pages --%>
			<div id="bloggerPagesHeader" class="profileLabel h1" style="width:87%; margin-top:10px; margin-left:4%"><span style="margin-left:36px; border-bottom:5px solid rgb(139, 197, 62); padding-bottom:4px">PAGE MANAGER</span></div>
			<div class="profileDiv" style="white-space:normal; width:87%; margin-left:4%">
				<c:if test="${empty bloggerPagesList}">
					<div style="text-align:center; color:#909090">No Pages</div>
				</c:if>
				<c:if test="${not empty bloggerPagesList}">
					<div id="bloggerPagesDiv" style="margin-top:20px">
						<table style="border-spacing:4px">
							<c:forEach var="page" items="${bloggerPagesList}" varStatus="counter">
								<c:if test="${counter.index != 0}">
									<tr><td colspan=2>
										<div style="margin-top:30px; border-top:1px solid #ddd">&nbsp;</div>
									</td></tr>
								</c:if>
								<tr>
									<td style="min-width:4%; vertical-align:top; padding-left:40px">
										<a href="/${page.pageId}">
											<c:if test="${empty page.profilePhoto}">
												<div class="topChartsThumb" style="width:140px; height:80px; background-image:url('/resources/images/def-page-photo.jpg')"></div>
											</c:if>
											<c:if test="${not empty page.profilePhoto}">
												<div class="topChartsThumb" style="width:140px; height:80px; background-image:url(${page.profilePhoto})"></div>
											</c:if>
										</a>
									</td>
									<td style="min-width:25%; vertical-align:top; padding-left:40px">
										<div><a style="font-weight:bold" href="/${page.pageId}">${page.pageName}</a></div>
										<c:if test="${page.adminFlag == 'Y'}">
											<div><span style="color:rgb(144, 144, 144)">My role: </span>Account Manager</div>
											<ul>
												<li><a href="/user/page-payments/${page.pageId}">Manage Page Payments</a>
												<li><a href="/user/page-keywords/${page.pageId}">Manage Page Keywords</a>
												<li><a href="/user/page-gad/${page.pageId}">View Google Analytics Dashboard</a>
												<c:if test="${fn:contains(page.productType,'BASIC')}">
													<li><a href="/user/marketplace/${page.pageId}">Find Bloggers</a>
												</c:if>
											</ul>
										</c:if>
										<c:if test="${page.adminFlag == 'N'}">
											<div><span style="color:rgb(144, 144, 144)">My role: </span>Blogger</div>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div id="bloggerPagesNavigation">
						<%-- previous page link will be hidden on the first page --%>
						<input type=hidden id=bloggerPagesPositionPrevHidden value="NULL">
						<div id="getBloggerPagesPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
							<a id="getBloggerPagesPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
						</div>
						<%-- next page link will be set to visible if the key is not NULL --%>
						<input type=hidden id=bloggerPagesPositionNextHidden value="${bloggerPosNext}">
						<c:if test="${bloggerPosNext != 'NULL'}">
							<div id="getBloggerPagesNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
								<a id="getBloggerPagesNext" class="nextPrevSmall" href="javascript:">MORE</a>
							</div>
						</c:if>
					</div>
					<div style="clear:both"></div>
				</c:if>
				<div style="margin-top:36px; margin-left:40px; margin-bottom:30px">
					<a style="font-size:15px; background-color:#FF3333; color:white; padding:2px 20px 4px 20px; border-radius:6px" class="createPage" href="javascript:" title="Create a New Page" target="_self">Create a New Page</a>
				</div>
			</div>
		<%-- End Blogger Pages --%>
	<%-- End page content --%>

	<div style="margin-top:60px"></div>

	<%-- Begin footer --%>
	<div class="footer">
		<div style="float:right; margin-right:40px">
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
