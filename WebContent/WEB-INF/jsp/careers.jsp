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
    <title>Heatbud - Careers</title>
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

	<div style="padding: 78px 100px 0px 100px">

	<br><br>
	<div style="width:100%">
		<div style="float:left; max-width:30%; margin-left:70px"><img style="border:none; max-width:100%" src="/resources/images/heatbud-careers.jpg"/></div>
		<div style="float:left; max-width:50%; margin-left:50px; font-size:18px">
			<b>Working for Heatbud is fun and rewarding!</b><br>
			<ul style="font-family:Text Me One; line-height:160%">
			<li>Best Work/Life balance ever. Guaranteed!</li>
			<li>Really flexible hours.</li>
			<li>Killer working environment.</li>
			<li>Party and play everyday.</li>
			<li>Work on cutting edge tools and technologies.</li>
			<li>Training and Conferences are encouraged.</li>
			</ul>
		</div>
		<div style="clear:both"></div>
	</div>

	<br>
	<div style="padding-left:70px; padding-right:70px">
	We develop software that helps people share their creativity and opinions with everybody across the world.  We have a small team that is lean and moves fast.<br>
	<br>
	We take time to learn and have fun, enjoying each other's company and priming the creative pump.  This is an opportunity to make a significant contribution to our flagship product and the organization as a whole.<br>
	<br>
	<b>We are currently looking for...</b>
	</div>

	<br>
	<div class="profileLabel h1"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">User Interface Designer</span></div>
	<div class="profileDiv" style="white-space:normal"><br>
	We're looking for a talented web and mobile designer with an excellent web and mobile portfolio (can be native and/or cross-platform), a willingness to take on large challenges, and a passion for collaboration. This position will help them define and implement a common interaction vocabulary. The ideal candidate will know how to focus their own efforts on high impact activities, while helping to guide others toward consistency with a broader vision.<br><br>
	You will be able to create effective and beautiful web and mobile designs.  You will know what needs to change when designing for a specific mobile OS versus designing a mobile web app.<br><br>
	Ideal candidate will be someone who enjoys working with development teams on small details as well as the big picture; who is both willing to explain design principals and is productive in their application.  Rapid prototyping is a requirement, with the ability to adjust fidelity as needed.  Development experience is a big plus.  The designer must be adept at making tradeoffs that balance technical and business requirements, and at providing final assets directly to engineering.<br><br>
	<b>Required Skills/Experience:</b>
	<ul>
	<li>Able to articulate design processes and design decision making considerations.</li>
	<li>Broad knowledge of design patterns for mobile UIs, both native OS and web applications.</li>
	<li>Experience in Agile environments.</li>
	<li>A portfolio of interaction designs for mobile platforms.</li>
	</ul>
	</div>

	<br>
	<div class="profileLabel h1"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Lead Software Engineer/ Architect</span></div>
	<div class="profileDiv" style="white-space:normal"><br>
	This Lead Software Engineer will play a Development Lead role for our Development team.<br><br>
	This individual will be responsible for the creation of quality web based software components that are used by millions of our customers across the world.  The Lead Software Engineer will work closely with the project manager, systems analysts, architects, and other developers to design, build, test, and deliver the features that make up our flagship product.<br><br>
	He/she will be expected to provide guidance and mentoring to the other developers on their projects.<br><br>
	<b>Required Skills/Experience:</b>
	<ul>
	<li>Be able to create intuitive web sites and services.</li>
	<li>Excellent Web and Mobile development skills.</li>
	<li>Experience with Spring Framework and Spring Security.</li>
	<li>Knowledge of HTML5, CSS and Javascript.</li>
	<li>Familiarity with at least one RDBMS and one NoSQL database.</li>
	<li>Having worked in Agile development environments.</li>
	</ul>
	</div>

	<br>
	<div style="padding-left:70px; padding-right:70px">
	<br>
	<b>Please email your resume to hr@heatbud.com.</b>
	</div>
	<br>
	<br>

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