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
    <title>Heatbud - Privacy &#38; Terms</title>
	<link type="text/css" href="/resources/css/main-min.css" media="screen" rel="stylesheet"/>
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head><body style="overflow-x:hidden">

	<%-- Begin header --%>
	<table class="header"><tr style="width:100%">
		<td style="float:left">
			<a href="/"><img alt="Heatbud logo" style="width:140px; padding-top:2px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/></a>
		</td>
		<td style="float:right; font-size:13px; padding-top:14px; padding-bottom:6px">
			<div style="float:left; margin-right:8px"><a href="/top/posts-trending-now" class="mainSelection">TOP CHARTS</a></div>
			<div style="float:left; margin-right:8px"><a href="/post/singing-bowls-singing-bowls-a-do-it-yourself-method-to-wellness" class="mainSelection">BLOG POSTS</a></div>
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

	<div style="padding: 78px 100px 0px 100px">

	<br>
	<div style="padding-left:60px; padding-right:60px">
	By using our services, you're accepting to our Privacy and Terms as stated below.  This page is updated periodically, so please check back often.<br>
	</div>

	<br>
	<div class="profileLabel h1" style="text-align:left"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Personal Information</span></div>
	<div class="profileDiv">
	Heatbud is a podium to express your ideas, thoughts and opinions to the entire world.<br>
	<b>Please use discretion when sharing personal information in your posts. Never share sensitive and private information such as social security number, driver's license, credit card numbers etc.</b><br>
	While most of what you type on Heatbud becomes public instantly, we do recognize that the email address and password that we collect during the process of registration need to be protected. We handle them sensitively.<br>
	We will use your email address only for important communication from us and our partners, while giving you the ability to restrict such email communication, as required by law.<br>
	However, we may still send some critical communication to you, as deemed necessary by us to continue business relationships with you, and as warranted by law.<br>
	</div>

	<br>
	<div class="profileLabel h1" style="text-align:left"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Data Encryption</span></div>
	<div class="profileDiv">
	Your data is encrypted using a High-grade 256 bit encryption before being transmitted over the internet.<br>
	Your password is encrypted before storing, using a one-way key algorithm that provides no means of decrypting. As such, our employees and our customer service can never see your password as a plain text.<br>
	</div>

	<br>
	<div class="profileLabel h1" style="text-align:left"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Cookies</span></div>
	<div class="profileDiv">
	We use cookies to store data on your computer. You must take steps to protect this information from hackers and viruses, as you do with your other critical files on your computer.<br>
	When you login from public computers, you must uncheck the box "Remember me on this computer" on the Heatbud login page. This will tell us not to store cookies on the computer.<br>
	</div>

	<br>
	<div class="profileLabel h1" style="text-align:left"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Content</span></div>
	<div class="profileDiv">
	You are the sole owner of the content that you submit.  You're responsible to make sure that copyright of the content is duly honored.<br>
	We request that you avoid using objectionable content in your posts.  We're not the authority to decide which content is objectionable, and hence we don't edit, modify or censor your posts.  But we reserve the right to delete your blog(s) or delete your account completely, if we choose to.<br>
	We don't accept blog posts on adult or illegal topics such as casino, tobacco, escorts, guns, alcohol, drugs etc.<br/>
	<span style="font-weight:bold">Brands and Trademarks:</span> Heatbud prohibits use of content that impersonates any brand and/or uses any trademark in an attempt to steal personal information or perform similar unlawful activities. Furthermore, unauthorized display and/or misappropriation of copyrighted material and trademarks with an intent to profit is a violation of their intellectual property rights and as such those activities are strictly prohibited on Heatbud.<br>
	Under no circumstances do we take the ownership of your content.  We may, however, use your content (giving due credit to you) for promotional purposes.<br>
	<span style="text-decoration:underline"><i>Heatbud protects the intellectual property of the bloggers to the extent allowed by the law.</i></span> Readers may share Heatbud posts in their "original" form giving due credit to the blogger. However, the blog content should NOT be extracted, copied or used elsewhere without obtaining permission from the blogger.<br>
	</div>

	<br>
	<div class="profileLabel h1" style="text-align:left"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">No Warranties</span></div>
	<div class="profileDiv">
	This software comes with no warranties.<br>
	While we do our best to keep your information safe and secure, we can't rule out the possibility of your information being hacked or lost, due to the things beyond our control.  Examples are bugs in the software, and issues with the infrastructure.  In the unlikely event that it happens, we will do our best to recover and protect your information.<br>
	</div>

	<br>
	<div style="padding-left:80px; padding-right:80px">
	We're always looking for your valuable suggestions. Please do <a href="/do/contact">Contact Us</a> any time.
	</div>
	</div>
	<br><br>

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