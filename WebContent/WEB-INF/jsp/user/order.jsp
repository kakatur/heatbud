<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
    <base target="_blank">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud | Blog Post Order</title>
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
	<script src="/resources/js/heatbud-order-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head>
<body style="position:relative">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- Begin header - Note: This page has customized footer that includes target keyword --%>
	<table class="header"><tr style="width:100%">
		<td style="float:left">
			<a href="/" target="_self"><img alt="Heatbud logo" style="width:140px; padding-top:2px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/></a>
		</td>
		<td style="float:right; font-size:13px; padding-top:14px; padding-bottom:6px">
			<div style="float:left; margin-right:8px"><a target="_self" href="/top/posts-trending-now" class="mainSelection">TOP CHARTS</a></div>
			<div style="float:left; margin-right:8px"><a target="_self" href="/post/singing-bowls-pashmina-the-softness-of-gold" class="mainSelection">BLOG POSTS</a></div>
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

	<%-- Begin page content --%>
	<div style="padding:48px 10px 10px 10px"></div>
	<input id=userIdHidden type=hidden value="${userId}"/>
	<input id=orderIdHidden type=hidden value="${orderId}"/>
	<input id=bloggerIdHidden type=hidden value="${order.bloggerId}"/>
	<input id=buyerIdHidden type=hidden value="${order.buyerId}"/>
	<input id=priceHidden type=hidden value="${order.price}"/>
	<input id=stripeChargeIdHidden type=hidden value="${order.stripeChargeId}"/>

	<%-- Order header --%>
	<div style="margin-top:10px; padding-left:4%; margin-left:4%; font-family:Fauna One, Helvetica, Arial; font-size:22px; font-weight:bold; color:#303030">Order by <a href="/${order.buyerId}">${order.buyerName}</a> dated <script>document.write(new Date(${order.orderDate}).toLocaleString());</script></div>

	<%-- Order details --%>
	<div class="profileDiv" style="border-top:1px solid #d1d1d1; white-space:normal; width:80%; margin-top:20px; padding-left:4%; margin-left:4%">
		<div style="margin-top:8px" class="h1">Order Details:</div>
		<div>Buyer's Heatbud page: <a href="/${order.pageId}">${order.pageName}</a></div>
		<div>Blogger: <a href="/${order.bloggerId}">${order.bloggerName}</a></div>
		<div>Post Type: ${order.postType}</div>
		<div>Delivery Time: ${order.deliveryDays} days</div>
		<div>Price: ${order.price} USD</div>
	</div>
	<%-- Note to the blogger --%>
	<div class="profileDiv" style="border-top:1px solid #d1d1d1; white-space:normal; width:80%; margin-top:10px; padding-left:4%; margin-left:4%">
		<div style="margin-top:8px" class="h1">Blogger to do:</div>
		<div>To complete this order, Blogger must follow the instructions at <a href="/do/help/write/business">Writing for Business</a>.</div>
		<div style="margin-top:5px">Make sure to use some of the <a href="/user/page-keywords/${order.pageId}">SEO keywords</a> listed by the buyer in your blog post.</div>
	</div>
	<%-- Order Progress --%>
	<div id="orderProgress" class="profileDiv" style="border-top:1px solid #d1d1d1; white-space:normal; width:80%; margin-top:10px; padding-left:4%; margin-left:4%">
		<div style="margin-top:8px" class="h1">Order Progress:</div>
		<c:if test="${empty orderProgressList}">
			<div>No progress yet.</div>
		</c:if>
		<c:forEach var="op" items="${orderProgressList}" varStatus="counter">
			<c:if test="${op.stepType == 'COMMENT'}">
				<div style="margin-top:8px; font-weight:bold; color:#707070"><script>document.write(new Date(${op.stepDate}).toLocaleString());</script> - ${op.commentBy} commented.</div>
				<div style="padding:3px 10px 3px 6px"><pre style="white-space: pre-wrap;">${op.commentText}</pre></div>
			</c:if>
			<c:if test="${op.stepType == 'REVIEW'}">
				<div style="margin-top:8px; font-weight:bold; color:#707070"><script>document.write(new Date(${op.stepDate}).toLocaleString());</script> - ${op.bloggerId} published the blog post and requested buyer for a review.</div>
			</c:if>
			<c:if test="${op.stepType == 'CLOSE'}">
				<div style="margin-top:8px; font-weight:bold; color:#707070"><script>document.write(new Date(${op.stepDate}).toLocaleString());</script> - ${op.buyerId} closed the order.</div>
			</c:if>
			<c:if test="${op.stepType == 'CANCEL'}">
				<div style="margin-top:8px; font-weight:bold; color:#707070"><script>document.write(new Date(${op.stepDate}).toLocaleString());</script> - ${op.buyerId} canceled the order.</div>
			</c:if>
		</c:forEach>
	</div>
	<%-- Order Actions --%>
	<div class="profileDiv" style="border-top:1px solid #d1d1d1; white-space:normal; width:80%; margin-top:10px; padding-left:4%; margin-left:4%">
		<%-- Post comment --%>
		<c:if test="${order.closeDate == 0 && order.cancelDate == 0}">
			<div style="margin-top:8px" class="h1">Post a Comment:</div>
			<textarea id=textComment rows="4" cols="90" placeholder="Buyer or blogger may comment here to communicate about the order."></textarea>
			<div style="margin-top:8px">
				<input class="activeButton" type="button" onclick="postComment()" value="Post Comment">
			</div>
			<div id="commentsError" style="margin-top:8px; font-size:1.4em" class="error">&nbsp;</div>
		</c:if>
		<%-- Review order --%>
		<c:if test="${order.publishDate == 0 && order.closeDate == 0 && order.cancelDate == 0 }">
			<div style="margin-top:8px" class="h1">Blogger to do:</div>
			<div style="margin-top:8px">Please publish the blog post and then request buyer for a review here.</div>
			<c:if test="${order.bloggerId == userId}">
				<input style="margin-top:8px" class="activeButton" type="button" onclick="reviewOrder()" value="I have published the post. Please review.">
			</c:if>
			<br/>
		</c:if>
		<%-- Close order --%>
		<c:if test="${order.closeDate == 0 && order.cancelDate == 0 }">
			<div style="margin-top:8px" class="h1">Buyer to do:</div>
			<c:choose>
				<c:when test="${order.publishDate == 0 && isLate == 'YES' }">
					<div style="margin-top:8px">Blogger has not published the blog post within specified time. Click cancel for a refund.</div>
					<c:if test="${order.buyerId == userId}">
						<input style="margin-top:8px" class="activeButton" type="button" onclick="cancelOrder()" value="Cancel the order">
						<div style="margin-top:8px">In case Blogger has actually published the Blog Post,</div>
						<input style="margin-top:8px" class="activeButton" type="button" onclick="closeOrder()" value="I'm satisfied with the Blog Post. Close the order.">
					</c:if>
				</c:when>
				<c:when test="${order.publishDate != 0 }">
					<div style="margin-top:8px">Blogger says they have published the Blog Post. Please approve here.</div>
					<c:if test="${order.buyerId == userId}">
						<input style="margin-top:8px" class="activeButton" type="button" onclick="closeOrder()" value="I'm satisfied with the Blog Post. Close the order.">
						<input style="margin-top:8px" class="activeButton" type="button" onclick="cancelOrder()" value="Blogger didn't publish the Blog Post. Refund my money.">
					</c:if>
					<div style="margin-top:8px">Order will automatically close if no action has been taken by <script>document.write(new Date(${order.publishDate}+3*24*3600*1000).toLocaleString());</script>.</div>
				</c:when>
				<c:otherwise>
					<c:if test="${order.buyerId == userId}">
						<div style="margin-top:8px">In case Blogger has actually published the Blog Post,</div>
						<input style="margin-top:8px" class="activeButton" type="button" onclick="closeOrder()" value="I'm satisfied with the Blog Post. Close the order.">
						<div style="margin-top:8px">In case you want to cancel the order,</div>
						<input style="margin-top:8px" class="activeButton" type="button" onclick="cancelOrder()" value="I want to cancel the order. Please refund my money.">
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>
		<%-- Order has been closed or closed --%>
		<c:if test="${order.closeDate != 0}">
			<div style="margin-top:8px" class="h1">Order has been completed.</div>
		</c:if>
		<c:if test="${order.cancelDate != 0}">
			<div style="margin-top:8px" class="h1">Order has been canceled.</div>
		</c:if>
	</div>
	<div style="margin:20px 0px 30px 50px">
		<a style="font-size:15px" href="/user/orders" title="Orders" target="_self">&lt;&lt; Back to your Orders</a>
	</div>
	<%-- End page content --%>

	<div style="margin-top:60px"></div>

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