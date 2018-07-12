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
    <title>Heatbud | MarketPlace</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-marketplace-min.js?20180530"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head><body style="position:relative">

	<script>var PL = new Array(); var MP = new Array();</script>

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- prompt signup box --%>
	<div id=promptSignupBox class="modal-box" style="width:360px; height:180px; margin-left:-180px; margin-top:-90px">
		<div class="h1">Please Login/ Signup to continue</div><br>
		<div>Registration is Free and Simple! And you get the ability to favorite zones, write posts, post comments, your own personalized My Reading List and more!</div><br>
		<input class="activeButton" onclick="window.location.href='/do/login';" type="button" value="Take me to the Login/ Signup page">
		<input onclick="$('body, #promptSignupBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
	</div>

	<%-- order blogger pricing box --%>
	<div id="orderBloggerPricingBox" class="modal-box" style="width:360px; height:270px; margin-left:-180px; margin-top:-135px">
		<div class="h1">Blog Post Order:</div>
		<div style="margin-top:16px">
			<div>
				<span>Hi </span>
				<span id=bloggerPricingBloggerId></span>
				<span> - Please write a blog post with the following details.</span>
			</div>
			<div style="margin-top:20px">
				<span>Page Id: </span>
				<span style="font-weight:bold; color:green" id="bloggerPricingPageId">${pageId}</span>
			</div>
			<div style="margin-top:5px">Post Type: <span style="font-weight:bold; color:green" id=bloggerPricingPostType></span></div>
			<div style="margin-top:5px">Price: $<span style="font-weight:bold; color:green" id=bloggerPricingPrice></span></div>
			<div style="margin-top:5px">Delivery Time: <span style="font-weight:bold; color:green" id=bloggerPricingDeliveryDays></span> days</div>
		</div>
		<div id=orderBloggerPricingRetMessage style="color:red"></div>
		<div style="float:right; padding-right:20px; margin-top:5px">
			<input id=orderBloggerPricingButton class="activeButton" onclick="orderBloggerPricing()" type="button" value="Place Order">&nbsp;
			<input class="activeButton" onclick="$('body, #orderBloggerPricingBox, #modal-background').toggleClass('active');" type="button" value="Close">
		</div>
		<div style="clear:both"></div>
	</div>

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

	<div style="padding-top:80px"></div>
	<input id=userIdHidden type=hidden value="${userId}"/>

	<%-- Begin search criteria --%>
	<div style="margin-left:14%; margin-right:auto">
		<div>
			<div style="display:inline-block; min-width:200px; margin-right:6px; text-align:right">Blogger : </div>
			<c:if test="${not empty keyword}">
				<input id="keyword" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:450px; border-radius:2px" placeholder="Enter a keyword to search on Blogger Id, Name or Description" value="${keyword}">
			</c:if>
			<c:if test="${empty keyword}">
				<input id="keyword" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:450px; border-radius:2px" placeholder="Enter a keyword to search on Blogger Id, Name or Description">
			</c:if>
		</div>
		<div style="margin-top:4px">
			<div style="display:inline-block; min-width:200px; margin-right:6px; text-align:right">Blogger Country : </div>
			<select id="country" name="country" style="border:2px solid #BDC7D8; padding:4px; border-radius:2px; width:450px">
				<c:if test="${not empty country}">
					<option value="${country}">${country}</option>
				</c:if>
				<option value=""></option>
				<c:forEach var="country1" items="${marketPricingCountriesSet}" varStatus="counter">
					<option value="${country1}">${country1}</option>
				</c:forEach>
			</select>
		</div>
		<div style="margin-top:4px">
			<div style="display:inline-block; min-width:200px; margin-right:6px; text-align:right">Skill : </div>
			<c:if test="${not empty tag}">
				<input id="tag" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:450px; border-radius:2px" placeholder="Enter a skill, like Finance" value="${tag}">
			</c:if>
			<c:if test="${empty tag}">
				<input id="tag" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:450px; border-radius:2px" placeholder="Enter a skill, like Finance">
			</c:if>
		</div>
		<div style="margin-top:4px">
			<div style="display:inline-block; min-width:200px; margin-right:6px; text-align:right">Delivery days : </div>
			<c:if test="${not empty ddFrom}">
				<input id="ddFrom" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:100px; border-radius:2px" placeholder="Lower limit" value="${ddFrom}">
			</c:if>
			<c:if test="${empty ddFrom}">
				<input id="ddFrom" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:100px; border-radius:2px" placeholder="Lower limit">
			</c:if>
			<c:if test="${not empty ddTo}">
				<input id="ddTo" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; margin-left:10px; width:100px; border-radius:2px" placeholder="Upper limit" value="${ddTo}">
			</c:if>
			<c:if test="${empty ddTo}">
				<input id="ddTo" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; margin-left:10px; width:100px; border-radius:2px" placeholder="Upper limit">
			</c:if>
		</div>
		<div style="margin-top:4px">
			<div style="display:inline-block; min-width:200px; margin-right:6px; text-align:right">Price in USD : </div>
			<c:if test="${not empty priceFrom}">
				<input id="priceFrom" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:100px; border-radius:2px" placeholder="Lower limit" value="${priceFrom}">
			</c:if>
			<c:if test="${empty priceFrom}">
				<input id="priceFrom" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; width:100px; border-radius:2px" placeholder="Lower limit">
			</c:if>
			<c:if test="${not empty priceTo}">
				<input id="priceTo" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; margin-left:10px; width:100px; border-radius:2px" placeholder="Upper limit" value="${priceTo}">
			</c:if>
			<c:if test="${empty priceTo}">
				<input id="priceTo" style="border:2px solid #BDC7D8; color:#333333; letter-spacing:1px; padding:4px; margin-left:10px; width:100px; border-radius:2px" placeholder="Upper limit">
			</c:if>
		</div>
		<div style="margin-top:4px">
			<div style="display:inline-block; min-width:200px; margin-right:6px; text-align:right">Sort the results by : </div>
			<select id="sortCriteria" name="sortCriteria" style="border: 2px solid #BDC7D8; padding:4px 10px 4px 10px">
				<c:if test="${empty sortCriteria || sortCriteria == 'price'}">
					<option value="price">Price</option><option value="days">Delivery Days</option><option value="name">Blogger Name</option>
				</c:if>
				<c:if test="${sortCriteria == 'days'}">
					<option value="days">Delivery Days</option><option value="name">Blogger Name</option><option value="price">Price</option>
				</c:if>
				<c:if test="${sortCriteria == 'name'}">
					<option value="name">Blogger Name</option><option value="price">Price</option><option value="days">Delivery Days</option>
				</c:if>
			</select>
			<div style="display:inline-block; cursor:pointer; margin-left:20px; font-size:15px; background-color:#FF3333; color:white; padding:2px 20px 4px 20px; border-radius:3px" onclick="submitMPSearch();">FIND FREELANCERS</div>
		</div>
		<c:if test="${not empty ERROR}">
			<div id="mp-error" style="margin-left:208px; margin-top:10px; color:red">${ERROR}</div>
		</c:if>
		<c:if test="${empty ERROR}">
			<div id="mp-error" style="margin-left:208px; margin-top:10px; color:red">&nbsp;</div>
		</c:if>
	</div>
	<%-- End search criteria --%>

	<%-- Begin search results --%>
	<div style="margin-top:30px; margin-left:12%; margin-right:auto">
		<c:if test="${empty marketPricingList}">
			<c:if test="${empty ERROR}">
				<div style="margin-left:300px">No results.</div>
			</c:if>
		</c:if>
		<c:if test="${not empty marketPricingList}">
			<div class="profileDiv" style="width:90%; border-top:1px solid #d1d1d1; white-space:normal">
				<script>var MP = new Array();</script>
				<table style="width:100%; border-spacing:4px">
					<thead>
						<tr style="width:100%; text-align:left">
							<th style="width:20%; padding:4px">Blogger Name</th>
							<th style="width:30%; padding:4px">Skills</th>
							<th style="width:20%; padding:4px">Post Type</th>
							<th style="width:10%; padding:4px; text-align:right">Delivery Time</th>
							<th style="width:10%; padding:4px; text-align:right">Price</th>
							<th style="width:10%; padding:4px">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${marketPricingList}" var="mp" varStatus="loopCounter">
						<tr style="width:100%; text-align:left">
							<td style="padding:4px"><a target="_blank" href="/${mp.bloggerId}">${fn:escapeXml(mp.name)} (${mp.country})</a></td>
							<td style="padding:4px">${mp.tags}</td>
							<td style="padding:4px">${mp.postType}</td>
							<td style="padding:4px; text-align:right">${mp.deliveryDays} days</td>
							<td style="padding:4px; text-align:right">$${mp.price}</td>
							<td style="padding:4px"><a href="javascript:" class="activeButton" onclick="showOrderMPPricing(${loopCounter.index})" title="Order this pricing">order</a></td>
						</tr>
						<script>
							MP[${loopCounter.index}] = {'bloggerId' : '${mp.bloggerId}',
														'postType' : '${fn:escapeXml(mp.postType)}',
														'deliveryDays' : '${mp.deliveryDays}',
														'price' : '${mp.price}'
							};
						</script>
					</c:forEach>
				</tbody>
				</table>
			</div>
			<script>
				window.history.replaceState("Heatbud", "Heatbud | Marketplace", "/user/marketplace/${pageId}");
			</script>
		</c:if>
	</div>
	<%-- End search results --%>

	<%-- Begin Google ads --%>
	<div style="margin-top:80px; width:100%; text-align:center; padding:20px">
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
	<div class="footer" style="margin-top:150px">
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
