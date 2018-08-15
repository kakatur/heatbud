<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
    <meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud - S3 Object Editor</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

	<!-- CKEditor includes -->
	<script src="/resources/js/ckeditor/ckeditor.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-s3editor-min.js"></script>
	<script src="/resources/js/heatbud-images-min.js"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head><body style="position:relative">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- Post Headshot Box via My Images --%>
	<input id=userIdHidden type=hidden value="${userId}"/>
	<input id=CKEditorFuncNumHidden type=hidden value="S3PostHeadshot">
	<input id=selectedImageHidden type=hidden value="NULL">

	<div id=imageBox class="modal-box" style="padding:0px; width:640px; height:480px; margin-left:-320px; margin-top:-240px">
		<div class="modalHeader" style="width:630px">
			<span>My Images</span>
			<span onclick="$('body, #imageBox, #modal-background').toggleClass('active');" style="margin-left:30px; margin-right:10px; color:red; font-size:14px; cursor:pointer"><sup>x</sup></span>
		</div><br>
		<div style="overflow: auto; margin-top:30px; height:420px">
			<table style="width:100%; border-spacing:2px"><tr style="width:100%">
				<td style="width:30%; vertical-align:top; padding:0px">
					<%-- heading --%>
					<div class="zoneHeader">MY ALBUMS</div>
					<%-- folders --%>
					<div id=foldersDiv></div>
				</td>
				<td style="width:70%; vertical-align:top; padding:0px">
					<%-- heading --%>
					<div style="margin-top:10px; margin-bottom:10px; color:#888888; font-size:12px">Hint: Create a separate album for each of your Posts.</div>
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

	<div style="padding:48px 10px 10px 10px"></div>

	<div class="pageTitle" style="width:100%">
		<span style="border-bottom:5px solid rgb(139, 197, 62); padding-bottom:10px; margin-right:100px">S3 OBJECT EDITOR</span>
	</div>

	<div style="width:900px; margin-top:40px; margin-left:80px; color:white; background:#84BFBD; border-radius:5px; padding:20px">

		<form>
			<input id="postIdInput" class="inputText" placeholder="Post Id" type=text style="width:555px">
		</form>
		<div id="s3EditorButtons" style="margin-top:5px">
			<input class="activeButton" style="height:35px" onclick="getPostContent()" type="button" value="Get Content">
		</div>
		<div style="color:red" id="s3EditorRetMessage"></div>

	</div>

	<div id="postArea" style="display:none; padding:80px">
		<div class="labelDiv" style="border-top:1px solid #e9eaed; margin-top:10px">Title:</div>
		<div id="postTitle" style="margin-bottom:10px; font-family:'Fauna One', Helvetica, Arial; font-size:16px; font-weight:bold"></div>
		<div class="labelDiv" style="border-top:1px solid #e9eaed; margin-top:10px">Summary:</div>
		<div id="postSummary" style="margin-bottom:10px"></div>
		<div class="labelDiv" style="border-top:1px solid #e9eaed; margin-top:10px">Headshot:</div>
		<div>
			<img id=postHeadshotImg class="profileThumb" style="margin-bottom:10px" src="">
			<div id="postHeadshotEdit" style="display:none"><input class="activeButton" onclick="showPostHeadshotBox()" type="button" value="Edit Image"></div>
		</div>
		<div class="labelDiv" style="border-top:1px solid #e9eaed; margin-top:10px">Blogger (for post)/ Last updated by (for newsletter):</div>
		<div id="bloggerId" style="margin-bottom:10px"></div>
		<div class="labelDiv" style="border-top:1px solid #e9eaed; margin-top:10px">Last update date:</div>
		<div id="postUpdateDate" style="margin-bottom:10px"></div>
		<div class="labelDiv" style="border-top:1px solid #e9eaed; margin-top:10px">Content:</div>
		<div id="postContent" style="margin-bottom:10px"></div>
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

</body></html>
