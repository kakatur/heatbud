<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud | Page Payments</title>
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
	<script src="/resources/js/heatbud-page-payments-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head>
<body style="position: relative">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

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

	<%-- Begin page content --%>
	<div style="padding:48px 10px 10px 10px"></div>
	<input id=userIdHidden type=hidden value="${userId}"/>
	<input id=pageIdHidden type=hidden value="${pageId}"/>

	<div class="profileLabel h1" style="width:87%; margin-top:10px; margin-left:4%">
		<span style="margin-left:26px; border-bottom:5px solid rgb(139, 197, 62); padding-bottom:4px">PAYMENTS FOR ${entity.entityName}</span>
	</div>
	<div class="profileDiv" style="white-space:normal; width:87%; margin-left:4%">
		<table style="width:95%">
			<tr style="width:100%">
				<td>
					<c:if test="${not empty pagePaymentsList}">
						<div style="margin:10px 20px 0px 20px">
							<div style="color:#707070">Payment history (page becomes inactive after a 7-day grace period):</div>
							<table>
								<tr style="text-align:left"><th>Start Date</th><th>Product Type</th><th>Amount</th><th>Coupon Used</th><th>End Date</th></tr>
								<c:forEach var="pp" items="${pagePaymentsList}" varStatus="counter">
									<tr style="background-color:#e8e8e8">
										<td style="padding:3px 10px 3px 6px"><script>document.write(new Date(${pp.startDate}).toLocaleString());</script></td>
										<td style="padding:3px 10px 3px 6px">${pp.productType}</td>
										<td style="padding:3px 10px 3px 6px">${pp.amount} USD</td>
										<td style="padding:3px 10px 3px 6px">${pp.coupon}</td>
										<td style="padding:3px 10px 3px 6px">
											<c:if test="${pp.endDate == 1}">
												Never
											</c:if>
											<c:if test="${pp.endDate > 1}">
												<script>document.write(new Date(${pp.endDate}).toLocaleString());</script>
											</c:if>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</c:if>
					<c:if test="${entity.endDate == 0}">
						<div style="display:inline-block; padding:10px 20px; margin:30px 0px 10px 20px; color:#707070; background-color:#FF3333; color:white; border-radius:3px">Page is not Active.</div>
					</c:if>
					<c:if test="${entity.endDate == 1}">
						<div style="display:inline-block; padding:10px 20px; margin:30px 0px 10px 20px; color:#707070; background-color:#FF3333; color:white; border-radius:3px">Page has been setup with recurring payments.</div>
					</c:if>
					<c:if test="${entity.endDate > 1}">
						<div style="display:inline-block; padding:10px 20px; margin:30px 0px 10px 20px; color:#707070; background-color:#FF3333; color:white; border-radius:3px">Page expires <script>document.write(new Date(${entity.endDate}).toLocaleString());</script></div>
					</c:if>
				</td>
				<td style="min-width:40%">
					<c:if test="${entity.endDate != 1}">
						<div style="margin-top:10px">
							<div style="color:#707070">
								<span>Make a payment</span>
								<c:if test="${entity.endDate > 1 && entity.endDate <= currentUTC}">
									<span> for the period starting from <br/><script>document.write(new Date(${currentUTC}).toLocaleString());</script></span>
								</c:if>
								<c:if test="${entity.endDate > currentUTC}">
									<span> for the period starting from <br/><script>document.write(new Date(${entity.endDate}).toLocaleString());</script></span>
								</c:if>
								<span> :</span>
							</div>
							<br/>
							<div>
								<input checked type="radio" id="basic-monthly" name="product-type"
									onchange="document.getElementById('page-payment-amount').innerHTML=29;"/>&nbsp;
								<label for="basic-monthly">Business BASIC monthly</label>
							</div>
							<div>
								<input type="radio" id="basic-yearly" name="product-type"
									onchange="document.getElementById('page-payment-amount').innerHTML=290;"/>&nbsp;
								<label for="basic-yearly">Business BASIC yearly</label>
							</div>
							<div>
								<input type="radio" id="premium-monthly" name="product-type"
									onchange="document.getElementById('page-payment-amount').innerHTML=295;"/>&nbsp;
								<label for="premium-monthly">Business PREMIUM monthly</label>
							</div>
							<div>
								<input type="radio" id="premium-yearly" name="product-type"
									onchange="document.getElementById('page-payment-amount').innerHTML=2950;"/>&nbsp;
								<label for="premium-yearly">Business PREMIUM yearly</label>
							</div>
							<div>
								<input type="radio" id="premium-plus-monthly" name="product-type"
									onchange="document.getElementById('page-payment-amount').innerHTML=495;"/>&nbsp;
								<label for="premium-plus-monthly">Business PREMIUM PLUS monthly</label>
							</div>
							<div>
								<input type="radio" id="premium-plus-yearly" name="product-type"
									onchange="document.getElementById('page-payment-amount').innerHTML=4950;"/>&nbsp;
								<label for="premium-plus-yearly">Business PREMIUM PLUS yearly</label>
							</div>
							<div style="margin-top:6px">
								<input id="page-payment-coupon" style="width:150px; border-radius:14px; border:1px solid #61cf81" placeholder="Coupon Code" class="inputText"/>&nbsp;
								<button onclick="applyCoupon()" style="padding:5px 10px;">apply</button>
							</div>
							<div style="margin-top:10px">
								<div id="page-payment-error" style="color:red; margin-bottom:6px">Email info@heatbud.com for a coupon code.</div>
							</div>
						</div>
					    <div style="margin-top:10px; font-size:1.2em; font-weight:bold;">Amount to pay: $<span id="page-payment-amount">29</span></div>
						<div style="margin-top:5px;"><button onclick="setupRecurring()" class="activeButton" style="padding:5px 20px;">Setup recurring payments</button></div>
						<div style="margin-top:5px;"><button onclick="makeOneTime()" class="activeButton" style="padding:5px 20px;">Make a one-time payment</button></div>
						<div style="margin-top:10px; color:#707070">Visit <a href="/do/help/main/pricing">Heatbud Pricing</a> to learn more.</div>
					</c:if>
				</td>
			</tr>
		</table>
		<div style="margin:20px 0px 30px 20px;">
			<a style="font-size:15px" href="/user/pages" title="Page Manager" target="_self">&lt;&lt; Back to Page Manager</a>
		</div>
	</div>
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