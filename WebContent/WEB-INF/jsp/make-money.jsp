<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="We have businesses looking for content. Signup and make money blogging."/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud | We have businesses looking for content. Signup and make money blogging.</title>
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

</head><body style="overflow-x:hidden">

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

	<div style="text-align:center; padding:88px 10px 20px 10px">
		<div style="float:left; text-align:left; width:30%; line-height:2em">
			<div class="helpHeader">Why Social Blogging?</div>
			<ul style="list-style-type:none">
				<li><a href="/do/help/main/what">What is Social Blogging? &gt;</a></li>
				<li><a href="/do/help/main/why-1">Why Heatbud? &gt;</a></li>
				<li><a href="/do/help/main/how">How Heatbud works &gt;</a></li>
			</ul>
			<div class="helpHeader">Getting started with Heatbud</div>
			<ul style="list-style-type:none">
				<li><a href="/do/help/main/pricing">Heatbud Pricing &gt;</a></li>
				<li><a href="/do/help/main/promotions">Heatbud Promotions &gt;</a></li>
				<li><a href="/do/help/main/bloggers">Guide for Bloggers &gt;</a></li>
				<li><a href="/do/help/main/business">Guide for Business users &gt;</a></li>
			</ul>
			<div class="helpHeader">Advanced Topics</div>
			<ul style="list-style-type:none">
				<li><a href="/do/help/read/topcharts">I want to read posts.</a></li>
				<li><a href="/do/help/account/eligibility">I want to sign up and collaborate.</a></li>
				<li><a href="/do/help/write/before">I want to create posts.</a></li>
				<li><a href="/do/help/popular/collaborate">I want to make my posts popular.</a></li>
				<li><a href="/do/help/main/features">Top Features of Heatbud &gt;</a></li>
			</ul>
			<div class="helpHeader">Contact</div>
			<ul style="list-style-type:none">
				<li>Still have questions? <a href="/do/contact">Contact Us</a>.</li>
			</ul>
		</div>
		<div style="float:left; text-align:left; max-width:50%; border:1px solid #e9eaed; padding:20px; margin-top:10px; font-size:1.1em">
			<div class="helpSub">Heatbud Pricing</div>
			<h1>For Bloggers</h1>
			<ul>
			<li style="margin-bottom:5px">Signup and create blog posts for FREE at Heatbud. You can reach your audience without creating your own website!
			<li style="margin-bottom:5px">If you want to earn money writing for businesses, go to your profile page and set your pricing.
			</ul>
			To get started, visit <a href="/do/help/main/bloggers">Guide for Bloggers</a>.<br/>
			<br/>
			<h1>For Businesses</h1>
			<div style="color:#737373"><a target="_blank" href="/do/help/main/what">Click here</a> to find out how Social Blogging helps grow your business. We offer three packages for your website.</div>
			<br/>
			<b>BASIC Package:</b>
			<i>For $29 a month OR $290 a year,</i>
			<ul>
			<li style="margin-bottom:5px"><i>If you don't have a website</i> -> Register a domain, redirect it to your Heatbud page and save money by not having to hire a web designer. <img alt="Have no website" style="max-width:95%; padding-top:10px; border:none" src="/resources/images/main-why-1-01.jpg"/></li>
			<li style="margin-bottom:5px"><i>If you have a website</i> -> Redirect your blog to your Heatbud page. <img alt="Already have website" style="max-width:95%; padding-top:10px; border:none" src="/resources/images/main-why-1-02.jpg"/></li>
			<li style="margin-bottom:5px"><b>Setup keywords</b> at Heatbud using our FREE SEO keyword tool exclusively available to Heatbud customers.</li>
			<li style="margin-bottom:5px"><b>Find freelancers</b> who can write SEO-rich blog posts for you. We will charge a service fee for each transaction.</li>
			<li style="margin-bottom:5px"><b>Track your progress</b> using the Google Analytics dashboard available in your Page Manager.</li>
			<li style="margin-bottom:5px">Compare 50k a year to hire a full time blogger vs $290 plus freelancer fees when using heatbud.</li>
			</ul>
			<b>PREMIUM Package:</b>
			<i>For $295 a month OR $2,950 a year, you get all of the above with none of the work.</i>
			<ul>
			<li style="margin-bottom:5px"><i>If you don't have a website</i> -> We will help you register a domain and redirect it to your Heatbud page. Save money by not having to hire a web designer.</li>
			<li style="margin-bottom:5px"><i>If you have a website</i> -> We will help you redirect your website's blog menu to your Heatbud page.</li>
			<li style="margin-bottom:5px">We will <b>Setup keywords</b> at Heatbud using our FREE SEO keyword tool exclusively available to Heatbud customers.</li>
			<li style="margin-bottom:5px">We will <b>Find and pay top bloggers</b> to write 2 SEO-rich blog posts of 300-500 words each every month for your business.
			<ul><li style="margin-bottom:5px">We will rotate the bloggers, so you get a diverse content.</ul></li>
			<li style="margin-bottom:5px">You can <b>Track your progress</b> anytime using the Google Analytics dashboard available in your Page Manager.</li>
			<li style="margin-bottom:5px">Compare 100k a year to hire a full time blogger and an account manager vs $2950 using heatbud.</li>
			</ul>
			<b>PREMIUM PLUS Package:</b>
			<i>For $495 a month OR $4,950 a year, you get all of the above in bigger and better sizes.</i>
			<ul>
			<li style="margin-bottom:5px"><i>If you don't have a website</i> -> This is an expensive option for small businesses. Please choose one from the above.</li>
			<li style="margin-bottom:5px"><i>If you have a website</i> -> We will help you redirect your website's blog menu to your Heatbud page.</li>
			<li style="margin-bottom:5px">We will <b>Setup keywords</b> at Heatbud using our FREE SEO keyword tool exclusively available to Heatbud customers.</li>
			<li style="margin-bottom:5px">We will <b>Find and pay top bloggers</b> to write 4 SEO-rich blog posts of 800-1000 words plus 2 images in each post every month for your business.
			<ul><li style="margin-bottom:5px">We will rotate the bloggers, so you get a diverse content.</ul></li>
			<li style="margin-bottom:5px">You can <b>Track your progress</b> anytime using the Google Analytics dashboard available in your Page Manager.</li>
			<li style="margin-bottom:5px">Compare 200k a year to hire multiple full time bloggers and an account manager vs $4950 using heatbud.</li>
			</ul>
			To get started, visit <a href="/do/help/main/businesses">Guide for Businesses</a>.<br/>
			<br/>
			<br/>
			Contact me for more info:<br/>
			<b>Gregory Schaeffer, Opportunity Specialist, gs@heatbud.com</b><br/>
			<br/><br/><br/>
		</div>
		<div style="float:left; width:20%"></div>
		<div style="clear:both"></div>
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