<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud - Orders</title>
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

	<table style="width:100%; border-spacing:2px; padding-top:50px"><tr style="width:100%">
	<td style="width:80%; padding:0px">

		<%-- Begin Orders Placed --%>
		<div class="profileLabel h1" style="width:87%; margin-top:30px; margin-left:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">ORDERS PLACED:</span></div>
		<c:if test="${empty ordersPlacedList}">
			<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%; text-align:center">You have not placed any orders yet.</div>
		</c:if>
		<c:if test="${not empty ordersPlacedList}">
			<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%">
				<table style="width:100%; border-spacing:4px">
					<thead>
						<tr style="width:100%; text-align:left">
							<th>Order Date</th>
							<th>Page Name</th>
							<th>Blogger Name</th>
							<th>Post Type</th>
							<th style="text-align:right">Delivery Time</th>
							<th style="text-align:right">Price</th>
							<th>Publish Date</th>
							<th>Close Date</th>
							<th>Cancel Date</th>
						</tr>
					</thead>
					<tbody id=ordersPlacedTBody>
						<c:forEach items="${ordersPlacedList}" var="order" varStatus="loopCounter">
							<tr style="margin-top:4px; width:100%; text-align:left">
								<td><a href="/user/order/${order.orderId}"><script>document.write(new Date(${order.orderDate}).toLocaleString());</script></a></td>
								<td><a href="/${order.pageId}">${fn:escapeXml(order.pageName)}</a></td>
								<td><a href="/${order.bloggerId}">${fn:escapeXml(order.bloggerName)}</a></td>
								<td>${order.postType}</td>
								<td style="text-align:right">${order.deliveryDays} days</td>
								<td style="text-align:right">$${order.price}</td>
								<c:if test="${order.publishDate == 0}">
									<td>&nbsp;</td>
								</c:if>
								<c:if test="${order.publishDate != 0}">
									<td><script>document.write(new Date(${order.publishDate}).toLocaleString());</script></td>
								</c:if>
								<c:if test="${order.closeDate == 0}">
									<td>&nbsp;</td>
								</c:if>
								<c:if test="${order.closeDate != 0}">
									<td><script>document.write(new Date(${order.closeDate}).toLocaleString());</script></td>
								</c:if>
								<c:if test="${order.cancelDate == 0}">
									<td>&nbsp;</td>
								</c:if>
								<c:if test="${order.cancelDate != 0}">
									<td><script>document.write(new Date(${order.cancelDate}).toLocaleString());</script></td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<div id="ordersPlacedNavigation">
					<%-- previous page link will be hidden on the first page --%>
					<input type=hidden id=ordersPlacedKeyPrevBIHidden value="NULL">
					<input type=hidden id=ordersPlacedKeyPrevODHidden value="NULL">
					<div id="getOrdersPlacedPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
						<a id="getOrdersPlacedPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
					</div>
					<%-- next page link will be set to visible if the key is not NULL --%>
					<input type=hidden id=ordersPlacedKeyNextBIHidden value="${ordersPlacedKeyNextBI}">
					<input type=hidden id=ordersPlacedKeyNextODHidden value="${OrdersPlacedKeyNextOD}">
					<c:if test="${ordersPlacedKeyNextBI != 'NULL'}">
						<div id="getOrdersPlacedNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
							<a id="getOrdersPlacedNext" class="nextPrevSmall" href="javascript:">MORE</a>
						</div>
					</c:if>
				</div>
			<div style="clear:both"></div>
			</div>
		</c:if>
		<%-- End Orders Placed --%>

		<%-- Begin Orders Received --%>
		<div class="profileLabel h1" style="width:87%; margin-top:30px; margin-left:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">ORDERS RECEIVED:</span></div>
		<c:if test="${empty ordersReceivedList}">
			<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%; text-align:center">You have not received any orders yet.</div>
		</c:if>
		<c:if test="${not empty ordersReceivedList}">
			<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%">
				<table style="width:100%; border-spacing:4px">
					<thead>
						<tr style="width:100%; text-align:left">
							<th>Order Date</th>
							<th>Page Name</th>
							<th>Buyer Name</th>
							<th>Post Type</th>
							<th>Delivery Time</th>
							<th style="text-align:right">Price</th>
							<th>Publish Date</th>
							<th>Close Date</th>
						</tr>
					</thead>
					<tbody id=ordersReceivedTBody>
						<c:forEach items="${ordersReceivedList}" var="order" varStatus="loopCounter">
							<tr style="margin-top:4px; width:100%; text-align:left">
								<td><a href="/user/order/${order.orderId}"><script>document.write(new Date(${order.orderDate}).toLocaleString());</script></a></td>
								<td><a href="/${order.pageId}">${fn:escapeXml(order.pageName)}</a></td>
								<td><a href="/${order.buyerId}">${fn:escapeXml(order.buyerName)}</a></td>
								<td>${order.postType}</td>
								<td>${order.deliveryDays} days</td>
								<td style="text-align:right">$${order.price}</td>
								<c:if test="${order.publishDate == 0}">
									<td>&nbsp;</td>
								</c:if>
								<c:if test="${order.publishDate != 0}">
									<td><script>document.write(new Date(${order.publishDate}).toLocaleString());</script></td>
								</c:if>
								<c:if test="${order.closeDate == 0}">
									<td>&nbsp;</td>
								</c:if>
								<c:if test="${order.closeDate != 0}">
									<td><script>document.write(new Date(${order.closeDate}).toLocaleString());</script></td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<div id="ordersReceivedNavigation">
					<%-- previous page link will be hidden on the first page --%>
					<input type=hidden id=ordersReceivedKeyPrevBIHidden value="NULL">
					<input type=hidden id=ordersReceivedKeyPrevODHidden value="NULL">
					<div id="getOrdersReceivedPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
						<a id="getOrdersReceivedPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
					</div>
					<%-- next page link will be set to visible if the key is not NULL --%>
					<input type=hidden id=ordersReceivedKeyNextBIHidden value="${ordersReceivedKeyNextBI}">
					<input type=hidden id=ordersReceivedKeyNextODHidden value="${OrdersReceivedKeyNextOD}">
					<c:if test="${ordersReceivedKeyNextBI != 'NULL'}">
						<div id="getOrdersReceivedNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
							<a id="getOrdersReceivedNext" class="nextPrevSmall" href="javascript:">MORE</a>
						</div>
					</c:if>
				</div>
			<div style="clear:both"></div>
			</div>
		</c:if>
		<%-- End Orders Received --%>

		<div style="clear: both; margin-top: 40px">&nbsp;</div>

	</td>
	</tr></table>
	<%-- End page content --%>

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
