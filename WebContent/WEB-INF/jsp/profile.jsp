<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<!-- common -->
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>Heatbud | ${entity.entityName}</title>
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

	<!-- for Open Graph (facebook) -->
	<meta property="og:type" content="profile"/>
	<meta property="og:title" content="${fn:escapeXml(entity.entityName)}"/>
	<meta property="og:description" content="${fn:escapeXml(entity.about)}"/>
	<meta property="og:url" content="https://www.heatbud.com/${entity.entityId}"/>
	<meta property="og:image" content="${entity.profilePhoto}"/>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>
	<meta property="fb:profile_id" content="heatbud"/>
	<meta property="profile:first_name" content="${fn:escapeXml(entity.entityName)}"/>
	<meta property="profile:username" content="${entity.entityId}"/>

	<!-- for Twitter -->
	<meta name="twitter:title" content="${fn:escapeXml(entity.entityName)}"/>
	<meta name="twitter:description" content="${fn:escapeXml(entity.about)}"/>
	<meta name="twitter:image" content="${entity.profilePhoto}"/>
	<meta name="twitter:card" content="summary_large_image"/>
	<meta name="twitter:site" content="@HeatbudLLC"/>

	<!-- for Google -->
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
	<c:if test="${empty entityTagsString}">
	    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
	 </c:if>
	<c:if test="${not empty entityTagsString}">
	    <meta name="keywords" content="${entityTagsString}"/>
	 </c:if>
	<meta name="application-name" content="Heatbud"/>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>
	<link rel="canonical" href="https://www.heatbud.com/${entity.entityId}"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-profile-min.js?20180530"></script>
	<script src="/resources/js/heatbud-images-min.js?20180530"></script>
	<script async src="/resources/js/heatbud-pagebox-min.js?20180530"></script>
	<script async src="//platform-api.sharethis.com/js/sharethis.js#property=5a9e07be57f7f1001382393f&product=inline-share-buttons"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head>

	<c:if test="${empty entity.profileColor}">
		<body style="position:relative; background-size:contain; background-repeat:repeat; background-color:#89BEE8">
	</c:if>
	<c:if test="${not empty entity.profileColor}">
		<body style="position:relative; background-size:contain; background-repeat:repeat; background-color:#${entity.profileColor}">
	</c:if>

	<%-- Begin pretty number --%>
	<script>
	function prettyNumber(n) {
		if ( n < 1000 ) {
			return n;
		} else if ( n >= 1000 && n < 1000000 ) {
			return Math.round(n*10/1000)/10+'k';
		} else {
			return Math.round(n*10/1000000)/10+'m';
		}
	}
	</script>
	<%-- End pretty number --%>

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- prompt signup box --%>
	<div id=promptSignupBox class="modal-box" style="width:360px; height:180px; margin-left:-180px; margin-top:-90px">
		<div class="h1">Please Login/ Signup to continue</div><br>
		<div>Registration is Free and Simple! And you get the ability to favorite zones, write posts, post comments, your own personalized My Reading List and more!</div><br>
		<input class="activeButton" onclick="window.location.href='/do/login';" type="button" value="Take me to the Login/ Signup page">
		<input onclick="$('body, #promptSignupBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
	</div>

	<%-- send email box --%>
	<div id="sendEmailBox" class="modal-box" style="width:700px; height:270px; margin-left:-350px; margin-top:-135px">
		<div class="h1">Email ${entity.entityName}:</div>
		<span style="color:#909090">Note: We save your IP address to report authorities in case of spamming.</span><br>
		<textarea id=personalMessage rows="4" cols="80" placeholder="Message"></textarea><br>
		<div id=sendEmailRetMessage style="color:red"></div>
		<div style="float:right; padding-right:20px; margin-top:5px">
			<input id=sendEmailButton class="activeButton" onclick="sendEmail()" type="button" value="Send Email">&nbsp;
			<input class="activeButton" onclick="$('body, #sendEmailBox, #modal-background').toggleClass('active');" type="button" value="Close">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- Page Box --%>
	<div id=pageBox class="modal-box" style="z-index:1100; padding:0px; width:480px; height:400px; margin-left:-240px; margin-top:-200px">
		<div class="modalHeader" style="width:470px">
			<span>Select Page</span>
			<span onclick="$('body, #pageBox, #modal-background').toggleClass('active');" style="padding-left:30px; padding-right:10px; color:white; font-size:18px; cursor:pointer">x</span>
		</div><br>
		<div style="overflow:auto; margin-top:60px; height:320px">
			<div style="margin-left:25px; margin-bottom:10px; color:#909090">Double click to Select</div>
			<div id=boxResults style="min-height:200px; margin-left:25px"></div>
		</div>
	</div>
	<script> var PL = new Array(); </script>

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
				<span>Page: </span>
				<span style="font-weight:bold; color:green" id="bloggerPricingPageId"></span>&nbsp;&nbsp;&nbsp;&nbsp;<input onclick="showPageBox('profile');" type="button" value="change page">
			</div>
			<div style="margin-top:5px">Post Type: <span style="font-weight:bold; color:green" id=bloggerPricingPostType></span></div>
			<div style="margin-top:5px">Price: <span style="font-weight:bold; color:green" id=bloggerPricingPrice></span></div>
			<div style="margin-top:5px">Delivery Days: <span style="font-weight:bold; color:green" id=bloggerPricingDeliveryDays></span></div>
		</div>
		<div id=orderBloggerPricingRetMessage style="color:red"></div>
		<div style="float:right; padding-right:20px; margin-top:5px">
			<input id=orderBloggerPricingButton class="activeButton" onclick="orderBloggerPricing()" type="button" value="Place Order">&nbsp;
			<input class="activeButton" onclick="$('body, #orderBloggerPricingBox, #modal-background').toggleClass('active');" type="button" value="Close">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- color box (used for theme color and contact font color --%>
	<div id="colorBox" class="modal-box" style="width:240px; height:220px; margin-left:-120px; margin-top:-110px">
		<div class="h1">Select Color</div>
		<div style="margin-top:12px">
			<span onclick="$('#hexColor').val('89C9FA'); $('#selectedColor').css('background-color','#89C9FA');" style="background-color:#89C9FA; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('ABB8C2'); $('#selectedColor').css('background-color','#ABB8C2');" style="background-color:#ABB8C2; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('F5ABB5'); $('#selectedColor').css('background-color','#F5ABB5');" style="background-color:#F5ABB5; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('FFCC4D'); $('#selectedColor').css('background-color','#FFCC4D');" style="background-color:#FFCC4D; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('D9EDF7'); $('#selectedColor').css('background-color','#D9EDF7');" style="background-color:#D9EDF7; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('F0F9FF'); $('#selectedColor').css('background-color','#F0F9FF');" style="background-color:#F0F9FF; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('90F9A2'); $('#selectedColor').css('background-color','#90F9A2');" style="background-color:#90F9A2; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
		</div>
		<div style="margin-top:8px">
			<span onclick="$('#hexColor').val('40C3BE'); $('#selectedColor').css('background-color','#40C3BE');" style="background-color:#40C3BE; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('279B61'); $('#selectedColor').css('background-color','#279B61');" style="background-color:#279B61; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('FFE7BA'); $('#selectedColor').css('background-color','#FFE7BA');" style="background-color:#FFE7BA; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('FF9966'); $('#selectedColor').css('background-color','#FF9966');" style="background-color:#FF9966; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('66FF66'); $('#selectedColor').css('background-color','#66FF66');" style="background-color:#66FF66; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('0066FF'); $('#selectedColor').css('background-color','#0066FF');" style="background-color:#0066FF; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<span onclick="$('#hexColor').val('DAC9DA'); $('#selectedColor').css('background-color','#DAC9DA');" style="background-color:#DAC9DA; box-radius:3px; cursor:pointer">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
		</div>
		<div style="margin-top:8px">
			<span>selected: </span>
		</div>
		<div style="margin-top:2px">
			<input id="hexColor" type="text" size="6" value="89C9FA" oninput="updateColor();"
				style="color: rgb(145, 145, 145); letter-spacing: 1px; padding: 2px 2px 2px 6px; width: 70px; border-radius: 2px; border: 1px solid #999">
			<span id="selectedColor" style="margin-top:2px; box-radius:3px; background-color:#89C9FA">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
		</div>
		<div style="margin-top:2px">
			<span>edit code to customize.</span>
		</div>
		<div id=colorRetMessage style="color:red"></div>
		<div style="float:right; padding-right:20px; margin-top:10px">
			<input id=saveColorButton class="activeButton" onclick="saveProfileColor()" type="button" value="Set Theme Color">&nbsp;
			<input class="activeButton" onclick="$('body, #colorBox, #modal-background').toggleClass('active');" type="button" value="Close">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- Email box --%>
	<div id="emailBox" class="modal-box" style="width:260px; height:150px; margin-left:-130px; margin-top:-75px">
		<div class="h1">Enter Email</div>
		<input style="margin-top:22px; margin-bottom:16px" id="email" type="text" size="40" placeholder="info@yourwebsite.com">
		<div id=emailRetMessage style="color:red"></div>
		<div style="float:right; padding-right:15px; margin-top:10px">
			<input id=saveEmailButton class="activeButton" onclick="saveEmail()" type="button" value="Save">&nbsp;
			<input class="activeButton" onclick="$('body, #emailBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- Phone number box --%>
	<div id="phoneBox" class="modal-box" style="width:260px; height:150px; margin-left:-130px; margin-top:-75px">
		<div class="h1">Enter Phone</div>
		<input style="margin-top:22px; margin-bottom:16px" id="phone" type="text" size="40" placeholder="123-456-7890">
		<div id=phoneRetMessage style="color:red"></div>
		<div style="float:right; padding-right:15px; margin-top:10px">
			<input id=savePhoneButton class="activeButton" onclick="savePhone()" type="button" value="Save">&nbsp;
			<input class="activeButton" onclick="$('body, #phoneBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- Address box --%>
	<div id="addressBox" class="modal-box" style="width:260px; height:150px; margin-left:-130px; margin-top:-75px">
		<div class="h1">Enter Street Address</div>
		<input style="margin-top:22px; margin-bottom:16px" id="address" type="text" size="40" placeholder="123 Some Street, Some City, SS 12345">
		<div id=addressRetMessage style="color:red"></div>
		<div style="float:right; padding-right:15px; margin-top:10px">
			<input id=saveAddressButton class="activeButton" onclick="saveAddress()" type="button" value="Save">&nbsp;
			<input class="activeButton" onclick="$('body, #addressBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- Website box --%>
	<div id="websiteBox" class="modal-box" style="width:260px; height:150px; margin-left:-130px; margin-top:-75px">
		<div class="h1">Enter Website</div>
		<input style="margin-top:22px; margin-bottom:16px" id="website" type="text" size="40" placeholder="https://www.website.com">
		<div id=websiteRetMessage style="color:red"></div>
		<div style="float:right; padding-right:15px; margin-top:10px">
			<input id=saveWebsiteButton class="activeButton" onclick="saveWebsite()" type="button" value="Save">&nbsp;
			<input class="activeButton" onclick="$('body, #websiteBox, #modal-background').toggleClass('active');" type="button" value="Cancel">
		</div>
		<div style="clear:both"></div>
	</div>

	<%-- create folder box --%>
	<div id=createFolderBox class="modal-box" style="z-index: 2000; width:360px; height:200px; margin-left:-180px; margin-top:-100px">
		<div class="h1">Create an Album</div><br>
		<div>Hint: Create a separate album for each of your posts. Empty albums will be automatically deleted.</div><br>
		<input id=createFolderInput type=text style="width:300px"/><br><br>
		<input class="activeButton" onclick="createFolder()" type="button" value="Create">
		<input onclick="$('body, #createFolderBox, #modal-background').toggleClass('active');" type="button" value="Cancel"><br>
		<div id="createFolderMessage" style="color:red">&nbsp;</div>
	</div>

	<%-- Image Box (for selecting Profile Photo, BG and Logo via My Images) --%>
	<input id=CKEditorFuncNumHidden type=hidden value="NONE">
	<input id=selectedImageHidden type=hidden value="NULL">
	<div id=imageBox class="modal-box" style="padding:0px; width:640px; height:480px; margin-left:-320px; margin-top:-240px">
		<div class="modalHeader" style="width:630px">
			<span>My Images</span>
			<span onclick="$('body, #imageBox, #modal-background').toggleClass('active');" style="padding-left:30px; padding-right:10px; color:white; font-size:18px; cursor:pointer">x</span>
		</div><br>
		<div style="overflow: auto; margin-top:30px; height:420px">
			<table style="width:100%; border-spacing:2px"><tr style="width:100%">
				<td style="width:30%; vertical-align:top; padding:0px">
					<%-- heading --%>
					<div class="zoneHeader" style="padding-left:6px; margin-top:20px">MY ALBUMS</div>
					<%-- folders --%>
					<div id=foldersDiv style="width:158px"></div>
				</td>
				<td style="width:70%; vertical-align:top; padding:0px">
					<%-- heading --%>
					<div style="margin-top:20px; margin-bottom:10px; color:#888888; font-size:12px">Hint: Create a separate album for each of your posts.</div>
					<div id="imagesMessageTop" style="color:red">&nbsp;</div>
					<div style="margin-top:10px"><span id=selectedFolder style="font-weight:bold; color: #333333">common</span></div>
					<%-- images --%>
					<div id=images style="min-height:200px; margin-right:10px"></div>
					<%-- tool box --%>
					<div style="margin-top:10px">
						<div style="float:left">
							<input class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="chooseImage()" type="button" value="Choose">
						</div>
						<div style="float:left; margin-left:10px">
							<input class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="deleteImage()" type="button" value="Delete">
						</div>
						<div id=uploadImageDiv style="float:left; margin-left:10px">
							<input id=uploadImageA class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="uploadImageInput.click()" type="button" value="Upload">
						</div>
						<div style="float:left; margin-left:10px; margin-top:12px">
							<a onclick="$('body, #imageBox, #modal-background').toggleClass('active');">Close</a>
						</div>
						<div style="clear:both"></div>
						<input id=uploadImageInput type=file style="visibility:collapse" onchange="uploadImage(this.files[0])">
					</div>
					<div id="imagesMessage" style="color:red">&nbsp;</div>
				</td>
			</tr></table>
		</div>
	</div>

	<%-- share elsewhere box --%>
	<div id=shareElsewhereBox class="modal-box" style="width:500px; height:240px; margin-left:-250px; margin-top:-120px">
		<div class="h1">Sharing is easy!</div><br>
		<div>Simply copy and paste the URL anywhere you like!</div><br>
		<div style="color:green">https://www.heatbud.com/${entity.entityId}</div><br>
		<div>Most social networking sites will be able to extract the Photo and About from Heatbud.</div><br>
		<input onclick="$('body, #shareElsewhereBox, #modal-background').toggleClass('active');" type="button" value="Close">
	</div>

	<%-- Begin header --%>
	<c:if test="${entity.entityType == 'P'}">
		<c:if test="${not empty entity.contactColor}">
			<c:set var="contactColor" value="#${entity.contactColor}"/>
		</c:if>
		<c:if test="${empty entity.contactColor}">
			<c:set var="contactColor" value="white"/>
		</c:if>
		<table style="width:100%"><tr style="width:100%">
			<td style="float:left; font-size:13px">
				<a id=logoA href="${entity.website}"><img id=logoImg alt="${fn:escapeXml(entity.entityName)}" style="height:60px; margin-left:10px; border:none" src="${entity.logo}"/></a>
			</td>
			<td style="float:right; font-size:13px; padding-top:20px; padding-right:20px">
				<a id=emailA style="font-family:Arial; font-size:1.2em; color:${contactColor}; padding-right:8px; padding-left:8px" href="mailto:${entity.entityEmail}">&#9993; ${entity.entityEmail}</a>
				<span id=phoneA style="font-family:Arial; font-size:1.2em; color:${contactColor}; padding-right:8px; padding-left:8px">&#9743; ${entity.phone}</span>
				<img src="/resources/images/home-black.png" alt="Visit Business Website" title="Visit Business Website" style="padding-left:4px; height:12px; border:none">
				<a id=websiteA style="font-family:Arial; font-size:1.2em; color:${contactColor}; padding-right:8px; padding-left:2px" href="${entity.website}">${entity.website}</a>
			</td>
		</tr></table>
	</c:if>
	<c:if test="${entity.entityType == 'B'}">
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
	</c:if>
	<div style="clear:both"></div>
	<%-- End header --%>

	<%-- Begin page content --%>
	<input id=userIdHidden type=hidden value="${userId}"/>
	<input id="entityTypeHidden" type=hidden value="${entity.entityType}"/>
	<input id="entityIdHidden" type=hidden value="${entity.entityId}"/>
	<input id="canEditHidden" type=hidden value="${canEdit}"/>

	<c:if test="${entity.entityType == 'B'}">
		<div style="padding-top:42px"></div>
	</c:if>

	<%-- Begin Profile --%>
	<table id="profileTable" style="width:98%; margin-left:10px">
		<tr style="width:100%">
			<td>
				<div style="position:absolute; left:30px; top:64px; z-index:1; color:white; font-family:'Segoe UI', Segoe, 'DejaVu Sans', Helvetica, Arial; font-size:20px; font-weight:bold">
					<c:if test="${entity.entityType == 'B'}">
						<span style="color:#aaaaaa; font-family:'offside, courier'">Blogger: </span>
						<span>${entity.entityName}</span>
					</c:if>
				</div>
				<div>
					<c:if test="${empty entity.profileBG}">
						<div id="profileBGDiv" style="min-width:510px; min-height:400px; border:1px solid #a1a1a1; border-radius:3px; background-size:cover;
							 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-bg.jpg');
							 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-bg.jpg');
							 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-bg.jpg');
							 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-bg.jpg');">
			    		</div>
					</c:if>
					<c:if test="${not empty entity.profileBG}">
						<div id="profileBGDiv" style="min-width:510px; min-height:400px; border:1px solid #a1a1a1; border-radius:3px; background-size:cover;
							 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profileBG}');
							 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profileBG}');
							 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profileBG}');
							 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profileBG}');">
			    		</div>
					</c:if>
					<div style="width:96%; margin-left:2%; margin-top:140px">
						<%-- Heat Index --%>
						<div style="float:right; font-size:12px; color:white; padding:6px; background-color:#f33; border:1px solid #a1a1a1; border-radius:5px">
							<div title="Blogger or Page Heat Index" style="min-width:50px; text-align:right; border-bottom:1px solid #e0e0e0">
								<img alt="Blogger or Page Heat Index" style="width:16px; height:20px; border:none" src="/resources/images/favicon.ico"/>
								<span style="font-size:20px"><script>document.write(prettyNumber(${entity.hi}));</script></span>
								<img alt="This week's change" title="This week's change" style="width:12px; height:12px; border:none; margin-left:10px" src="/resources/images/trending-up.png">
								<span title="This week's change" style="font-size:14px">${entity.hiTrending}</span>
							</div>
							<div><span style="font-size:14px">${entity.posts}</span> posts <span style="font-size:14px">${entity.votes}</span> votes <span style="font-size:14px">${entity.comments}</span> comments</div>
						</div>
						<%-- Country --%>
						<c:if test="${entity.entityType == 'B'}">
							<div style="float:right; margin-top:20px; margin-right:5px; background-color:dodgerblue; border-radius:5px; padding:8px 16px; color:white">${entity.country}</div>
						</c:if>
						<%-- Share controls --%>
						<div style="float:right; margin-top:20px; margin-right:5px" class="shareElsewhere" onclick="$('body, #shareElsewhereBox, #modal-background').toggleClass('active'); return false;">Share using URL</div>
						<div style="float:right; margin-top:20px; margin-right:5px; background-color:#95D03A; border-radius:5px; padding-right:5px" class="sharethis-inline-share-buttons"></div>
						<%-- Profile Photo --%>
						<div>
							<c:if test="${empty entity.profilePhoto}">
								<c:if test="${entity.entityType == 'B'}">
									<div id="profilePhotoDiv" style="width:310px; height:250px; margin-left:3px; margin-top:-180px; border:1px solid #a1a1a1; border-radius:3px; background-size:cover;
										 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');
										 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');
										 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');
										 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-blogger-photo.jpg');">
						    		</div>
						    	</c:if>
								<c:if test="${entity.entityType == 'P'}">
									<div id="profilePhotoDiv" style="width:310px; height:250px; margin-left:3px; margin-top:-180px; border:1px solid #a1a1a1; border-radius:3px; background-size:cover;
										 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');
										 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');
										 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');
										 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('/resources/images/def-page-photo.jpg');">
						    		</div>
						    	</c:if>
							</c:if>
							<c:if test="${not empty entity.profilePhoto}">
								<div id="profilePhotoDiv" style="width:310px; height:250px; margin-left:3px; margin-top:-180px; border:1px solid #a1a1a1; border-radius:3px; background-size:cover;
									 background-image:-moz-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');
									 background-image:-ms-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');
									 background-image:-webkit-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');
									 background-image:-o-linear-gradient(top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0) 20%),url('${entity.profilePhoto}');">
					    		</div>
							</c:if>
							<%-- About Me --%>
							<div style="margin-left:330px; margin-top:-150px">
								<div id=aboutDiv style="min-height:110px; font-family:Offside; font-size:22px; color:#434343">${entity.about}</div>
							</div>
						</div>
						<div style="clear:both"></div>
					</div>
				</div>
			</td>
		</tr>
	</table>
	<%-- End Profile --%>

	<%-- Profile Edit functions --%>
	<c:if test="${canEdit == 'Y'}">
		<div style="position:absolute; right:50px; top:80px; z-index:1; font-size:16px; color: #fafafa">
			<div class="disabledButton" id=cancelBio style="float:left; visibility:hidden">Cancel</div>
			<div class="activeButton" id=editBio style="float:left; margin-left:4px" onclick="editBio()">Edit Bio</div>
			<div class="activeButton" id=editProfilePhoto style="float:left; margin-left:4px" onclick="showImageBox('ProfilePhoto')">Photo</div>
			<div class="activeButton" id=editProfileBG style="float:left; margin-left:4px" onclick="showImageBox('ProfileBG')">Background</div>
			<c:if test="${entity.entityType == 'P'}">
				<div class="activeButton" id=editLogo style="float:left; margin-left:4px" onclick="showImageBox('Logo')">Logo</div>
				<div class="activeButton" id=editEmail style="float:left; margin-left:4px" onclick="$('body, #emailBox, #modal-background').toggleClass('active');">Email</div>
				<div class="activeButton" id=editPhone style="float:left; margin-left:4px" onclick="$('body, #phoneBox, #modal-background').toggleClass('active');">Phone</div>
				<div class="activeButton" id=editAddress style="float:left; margin-left:4px" onclick="$('body, #addressBox, #modal-background').toggleClass('active');">Address</div>
				<div class="activeButton" id=editWebsite style="float:left; margin-left:4px" onclick="$('body, #websiteBox, #modal-background').toggleClass('active');">Website</div>
			</c:if>
			<div class="activeButton" id=editProfileColor style="float:left; margin-left:4px" onclick="showColorBox('profile');">Theme Color</div>
			<c:if test="${entity.entityType == 'P'}">
				<div class="activeButton" id=editContactColor style="float:left; margin-left:4px" onclick="showColorBox('contact');">Contact Font Color</div>
			</c:if>
			<div style="clear:both"></div>
		</div>
		<div id=instructions style="visibility:hidden; position:absolute; right:200px; top:120px; z-index:1; font-size:17px; color:white">
			<div>Scroll down to fill all the sections. Scroll up to Save Bio.</div>
		</div>
	</c:if>

	<table id=publishedPostsHeader style="width:100%; border-spacing:2px; margin-top:80px"><tr style="width:100%">
	<td style="vertical-align:top; width:55%; padding-left:3%; padding-right:3%">
		<c:if test="${isActive == 'Y'}">
			<%-- Begin Published Posts --%>
			<div id=publishedPostsDiv style="padding-top:10px; float:right">
				<c:forEach var="post" items="${publishedPostsList}">
					<c:set var="votes" value="${post.upVotes-post.downVotes}"/>
					<div class="topChartsElement" style="max-width:520px; padding:6px">
						<div style="padding:6px 10px 0px 30px; font-family:'Text Me One'; font-size:18px; color:#8A8C8E">
							<span><b><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></b></span>
						</div>
						<div style="font-size:18px; font-weight:bold; padding:6px 10px 10px 30px">
							<a href="/post/${post.postId}">${post.postTitle}</a>
						</div>
						<div style="padding:0px 30px">
							<div onclick="location.href='/post/${post.postId}'" class="topChartsThumb grow" style="width:460px; height:240px; margin:0 auto; background-image:url(${post.postHeadshot})"></div>
						</div>
						<div style="padding:3px 10px 3px 30px">${post.postSummary}</div>
						<div style="font-size:12px; color:#909090; padding:3px 10px 6px 30px">
							<span>${post.views} views</span>
							<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
							<span>${votes} votes</span>
							<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
							<span>${post.comments} comments</span>
						</div>
					</div>
				</c:forEach>
			</div>
			<div id="publishedPostsNavigation" style="width:640px; padding-top:10px; float:right">
				<%-- previous page link will be hidden on the first page --%>
				<input type=hidden id=publishedPostsKeyPrevBIHidden value="NULL">
				<input type=hidden id=publishedPostsKeyPrevUDHidden value="NULL">
				<div id="getPublishedPostsPreviousDiv" style="width:45%; margin-top:10px; float:left; padding-left:50px; text-align:left; visibility:hidden">
					<a id="getPublishedPostsPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
				</div>
				<%-- next page link will be set to visible if the key is not NULL --%>
				<input type=hidden id=publishedPostsKeyNextBIHidden value="${publishedPostsKeyNextBI}">
				<input type=hidden id=publishedPostsKeyNextUDHidden value="${publishedPostsKeyNextUD}">
				<c:if test="${publishedPostsKeyNextBI != 'NULL'}">
					<div id="getPublishedPostsNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
						<a id="getPublishedPostsNext" class="nextPrevSmall" href="javascript:">MORE</a>
					</div>
				</c:if>
			</div>
			<div style="clear:both"></div>
			<%-- End Published Posts --%>
		</c:if>
		<c:if test="${isActive == 'N'}">
			<div style="font-size:20px; margin-top:40px; text-align:center">Posts are not visible because the page is not active.</div>
			<div style="font-size:20px; text-align:center">Please visit <a href="/user/page-payments/${entity.entityId}">Manage Page Payments</a> to make a payment.</div>
			<div style="font-size:20px; text-align:center">Visit <a href="/do/help/main/pricing">Heatbud Pricing</a> to learn more.</div>
		</c:if>
	</td>

	<td style="vertical-align:top; width:34%; padding-top:10px">
		<c:if test="${entity.entityType == 'B'}">
			<%-- My Passion --%>
			<div style="margin-left:3%">
				<div class="profileLabel h1" style="width:90%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">MY PASSION</span></div>
				<div id=passionDiv class="profileDiv" style="width:90%; padding-bottom:30px">${entity.passion}</div>
			</div>
			<%-- My Achievements --%>
			<div style="margin-top:10px; margin-left:3%">
				<div class="profileLabel h1" style="width:90%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">MY ACHIEVEMENTS</span></div>
				<div id=achievementsDiv class="profileDiv" style="width:90%; padding-bottom:30px">${entity.achievements}</div>
			</div>
		</c:if>
		<c:if test="${entity.entityType == 'P'}">
			<%-- Special Announcements --%>
			<div style="margin-top:10px; min-width:440px; margin-left:3%">
				<div class="profileLabel h1" style="width:90%">
					<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">SPECIAL ANNOUNCEMENTS</span>
				</div>
				<div id=announcementsDiv class="profileDiv" style="width:90%; padding-bottom:30px">${entity.announcements}</div>
			</div>
			<%-- Google Map --%>
			<c:if test="${not empty entity.address}">
				<div style="margin-top:10px; min-width:440px; margin-left:3%">
					<div class="profileLabel h1" style="width:90%">
						<span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">VISIT US!</span>
					</div>
					<div id=addressDiv class="profileDiv" style="width:90%; padding-bottom:30px">
						<iframe width="400" height="380" frameborder="0" style="border:0"
							src="https://www.google.com/maps/embed/v1/place?key=AIzaSyC0M5pbyIAE0Ij5ppItpRQRy_5ejik8OJQ
							&q=${fn:replace(fn:replace(entity.entityName,' ','+'),'&','and')},${fn:replace(entity.address,' ','+')}
							&attribution_source=Heatbud
							&attribution_web_url=https://www.heatbud.com/${entity.entityId}" allowfullscreen>
						</iframe>
					</div>
				</div>
			</c:if>
		</c:if>
		<c:if test="${entity.entityType == 'B'}">
			<%-- Contact --%>
			<div style="margin-top:10px; margin-left:3%">
				<div class="profileLabel h1" style="width:90%">
					<div style="float:left; border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">CONTACT</div>
					<%-- show send email button --%>
					<c:if test="${entity.enableEmail == 'Y'}">
						<div id="sendEmailDiv" class="email" style="color:white; float:right" onclick="showSendEmailBox();" title="Email ${entity.entityName}">
							<span style="font-size:20px"> @ </span>
							<span style="font-size:14px"> email </span>
						</div>
					</c:if>
					<%-- grey out send email button --%>
					<c:if test="${empty entity.enableEmail || entity.enableEmail == 'N'}">
						<div id="sendEmailDiv" class="email" style="color:#727272; float:right; cursor:default" onclick="return false;" title="Email ${entity.entityName}">
							<span style="font-size:20px"> @ </span>
							<span style="font-size:14px"> email </span>
						</div>
					</c:if>
					<div style="clear:both"></div>
					<c:if test="${canEdit == 'Y'}">
						<c:if test="${entity.enableEmail == 'Y'}">
							<div style="width:100%; text-align:right"><a id="enableEmail" style="font-size:12px; font-family:Calibri,Arial,sans-serif" href="javascript:" title="Let Heatbud users send email to you without seeing your email address.">Disable email</a></div>
						</c:if>
						<c:if test="${empty entity.enableEmail || entity.enableEmail == 'N'}">
							<div style="width:100%; text-align:right"><a id="enableEmail" style="font-size:12px; font-family:Calibri,Arial,sans-serif" href="javascript:" title="Let Heatbud users send email to you without seeing your email address.">Enable email</a></div>
						</c:if>
					</c:if>
				</div>
				<%-- display contact information entered by user --%>
				<div id=contactDiv class="profileDiv" style="width:90%; padding-bottom:30px">${entity.contact}</div>
			</div>
			<%-- My Skills --%>
			<div style="margin-top:10px; margin-left:3%">
				<div class="profileLabel h1" style="width:90%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">MY SKILLS</span></div>
				<div class="profileDiv" style="width:90%; white-space:normal">
					<script>
						var TL = new Array();
					</script>
					<div id="blogger-tags">
						<c:forEach items="${entityTagsList}" var="et" varStatus="loopCounter">
							<script>
								TL.push("${et.id}");
							</script>
							<div id="${et.id}" style="display:inline-block; border-radius:14px; border:1px solid #61cf81; padding:4px; margin-right:6px; font-family:Arial; font-size:16px; color:#365899">
								<span id="${et.id}-value">${et.tag}</span>&nbsp;&nbsp;
								<c:if test="${canEdit == 'Y'}">
									<a href="javascript:" onclick="deleteBloggerTag('${et.id}')" title="Delete this skill">&nbsp;x&nbsp;</a>
								</c:if>
							</div>
						</c:forEach>
					</div>
					<c:if test="${canEdit == 'Y'}">
					    <input type=hidden id="tag-count" value="${fn:length(entityTagsList)}">
						<div class="textDiv">
							<input id="tag-input" style="border-radius:14px; border:1px solid #61cf81" class="inputText"/>&nbsp;
							<button id="tag-button" onclick="addBloggerTag()" class="activeButton">add</button>
						</div>
						<div id="tag-error" style="color:red">&nbsp;</div>
						<div style="margin-top:6px; color:#909090">Put some skills so businesses can find you.</div>
				    </c:if>
				</div>
			</div>
			<%-- My Pricing --%>
			<div style="margin-top:10px; margin-left:3%">
			    <input type=hidden id="pricing-count" value="${fn:length(bloggerPricingList)}">
				<div class="profileLabel h1" style="width:90%"><span style="border-bottom:5px solid rgb(139, 197, 62); padding-bottom:4px">MY BLOG POST PRICING</span></div>
				<div class="profileDiv" style="width:90%; white-space:normal">
					<table id=pricingTable style="width:100%">
						<c:if test="${not empty bloggerPricingList}">
							<thead>
								<tr style="width:100%; text-align:left">
									<th style="width:53%">Post Type</th>
									<th style="width:25%">Delivery Time</th>
									<th style="width:20%">Price</th>
									<th style="width:5%">&nbsp;</th>
								</tr>
							</thead>
						</c:if>
						<tbody>
							<c:forEach items="${bloggerPricingList}" var="pricing" varStatus="loopCounter">
								<tr id="pricingTable-tr${pricing.position}" style="margin-top:4px; width:100%; text-align:left">
									<td style="width:53%"><span id="pricingTable-postType${pricing.position}">${pricing.postType}</span></td>
									<td style="width:25%"><span id="pricingTable-deliveryDays${pricing.position}">${pricing.deliveryDays}</span> days</td>
									<td style="width:20%">$<span id="pricingTable-price${pricing.position}">${pricing.price}</span></td>
									<c:if test="${userId == entity.entityId}">
										<td style="width:5%"><a href="javascript:" onclick="deleteBloggerPricing('${pricing.position}')" title="Delete this pricing">&nbsp;x&nbsp;</a></td>
									</c:if>
									<c:if test="${userId != entity.entityId}">
										<td style="width:5%"><a href="javascript:" class="activeButton" onclick="showOrderBloggerPricing('${pricing.position}')" title="Order this pricing">order</a></td>
									</c:if>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<c:if test="${canEdit == 'Y'}">
						<%-- new pricing detail --%>
						<div class="textDiv">
							<input id="pricing-input-postType" placeholder="500-600 words with 2 images" style="border-radius:14px; border:1px solid #61cf81; width:350px" class="inputText"/>&nbsp;
							<input id="pricing-input-deliveryDays" placeholder="5" style="margin-top:4px; border-radius:14px; border:1px solid #61cf81; width:40px" class="inputText"/> days&nbsp;
							<input id="pricing-input-price" placeholder="20" style="margin-top:4px; border-radius:14px; border:1px solid #61cf81; width:40px" class="inputText"/> USD&nbsp;
							<button id="pricing-button" onclick="addBloggerPricing()" class="activeButton">add</button>
						</div>
						<div id="pricing-error" style="color:red">&nbsp;</div>
						<%-- update email address --%>
						<input id=originalEmailAddress type=hidden value="NULL">
						<div>
							<div style="border-top:1px solid #b7b7b7; padding-top:6px; margin-top:10px; color:#909090">You will be paid by paypal at the below email address.</div>
							<span><input id="emailAddressInput" type=text style="width:150px; display:none"></span>
							<span id="emailAddressDiv">${entity.entityEmail}</span>
							<div style="display:inline-block" id="editEmailAddressDiv"><a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Update</a></div>
						</div>
						<div style="color:red" id="emailAddressRetMessage"></div>
						<div style="color:#909090">Visit <a target="_blank" href="/do/help/write/pricing">Blog Post Pricing</a> to learn more.</div>
					</c:if>
				</div>
			</div>
		</c:if>
		<%-- Begin Admin Zones --%>
		<c:if test="${entity.entityType == 'B'}">
			<div id="adminZonesHeader" class="profileLabel h1" style="width:87%; margin-top:10px; margin-left:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">ZONES I MANAGE</span></div>
			<div class="profileDiv" style="white-space:normal; width:87%; margin-left:3%">
				<c:if test="${empty adminZonesList}">
					<div style="text-align:center; color:#909090">No Zones</div>
				</c:if>
				<c:if test="${not empty adminZonesList}">
					<div id="adminZonesDiv">
						<table style="border-spacing:4px">
							<c:forEach var="zone" items="${adminZonesList}" varStatus="counter">
								<c:if test="${counter.index != 0}">
									<tr><td colspan=2><div style="border-top:1px solid #ddd">&nbsp;</div></td></tr>
								</c:if>
								<tr>
									<td style="vertical-align:top">
										<div style="padding-top:5px">
											<a href="/zone/${zone.zoneId}">
												<div class="topChartsThumb" style="width:140px; height:80px; background-image:url(${zone.zoneHeadshot})"></div>
											</a>
											<div>${zone.posts} <span style="font-size:11px; color:#909090">posts</span> ${zone.comments} <span style="font-size:11px; color:#909090">comments</span></div>
											<c:if test="${zone.zoneWho == 'E'}">
												<div style="font-size:11px; color:#909090">Anybody can post in this zone</div>
											</c:if>
											<c:if test="${zone.zoneWho == 'A'}">
												<div style="font-size:11px; color:#909090">Only admins can post in this zone</div>
											</c:if>
										</div>
									</td>
									<td style="vertical-align:top">
										<div style="font-weight:bold">
											<a href="/zone/${zone.zoneId}">${zone.zoneName}</a>
										</div>
										<div>${zone.zoneDesc}</div>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
					<div id="adminZonesNavigation">
						<%-- previous page link will be hidden on the first page --%>
						<input type=hidden id=adminZonesKeyPrevUIHidden value="NULL">
						<input type=hidden id=adminZonesKeyPrevZIHidden value="NULL">
						<div id="getAdminZonesPreviousDiv" style="width:45%; margin-top:10px; margin-left:15px; float:left; text-align:left; visibility:hidden">
							<a id="getAdminZonesPrevious" class="nextPrevSmall" href="javascript:">BACK</a>
						</div>
						<%-- next page link will be set to visible if the key is not NULL --%>
						<input type=hidden id=adminZonesKeyNextUIHidden value="${adminZonesKeyNextUI}">
						<input type=hidden id=adminZonesKeyNextZIHidden value="${adminZonesKeyNextZI}">
						<c:if test="${adminZonesKeyNextUI != 'NULL'}">
							<div id="getAdminZonesNextDiv" style="width:45%; margin-top:10px; float:right; text-align:right">
								<a id="getAdminZonesNext" class="nextPrevSmall" href="javascript:">MORE</a>
							</div>
						</c:if>
					</div>
					<div style="clear:both"></div>
				</c:if>
			</div>
		</c:if>
		<%-- End Admin Zones --%>
		</td>
	</tr></table>

	<div style="clear:both; margin-top:20px">&nbsp;</div>

	<%-- End page content --%>

	<%-- Begin footer --%>
	<c:if test="${entity.entityType == 'B'}">
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
	</c:if>
	<c:if test="${entity.entityType == 'P'}">
		<div class="footer" style="padding-bottom:50px">
			<div style="float: right; margin-right:40px">
				<span style="color:#C9C9C9">Blog page of </span>
				<span style="color:white">${entity.entityName}</span>
				<span style="color:#C9C9C9"> powered by</span>
				<span> <a href="/"><img alt="Heatbud logo" style="width:100px; border:none" src="/resources/images/heatbud-logo.png"/></a></span>
			</div>
		</div>
	</c:if>
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