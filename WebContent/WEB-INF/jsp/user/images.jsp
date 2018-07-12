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
    <title>Heatbud - Images</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>
	<script src="/resources/js/heatbud-images-min.js?20180530"></script>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

</head><body style="position:relative">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- create folder box --%>
	<div id=createFolderBox class="modal-box" style="width:360px; height:200px; margin-left:-180px; margin-top:-100px">
		<div class="h1">Create an Album</div><br>
		<div>Hint: Create a separate album for each of your posts. Empty albums will be automatically deleted.</div><br>
		<input id=createFolderInput type=text style="width:300px"/><br><br>
		<input class="activeButton" onclick="createFolder()" type="button" value="Create">
		<input onclick="$('body, #createFolderBox, #modal-background').toggleClass('active');" type="button" value="Cancel"><br>
		<div id="createFolderMessage" style="color:red">&nbsp;</div>
	</div>

	<c:if test="${empty CKEditorFuncNum}">
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
	</c:if>

	<%-- Begin page content --%>
	<input id=CKEditorFuncNumHidden type=hidden value="${CKEditorFuncNum}">
	<input id=selectedImageHidden type=hidden value="NULL">
	<input id=userIdHidden type=hidden value="${userId}">

	<div style="padding:58px 10px 10px 10px"></div>

	<table style="width:70%; border-spacing:2px; margin-left:5%; margin-right:auto; margin-bottom:10px"><tr style="width:100%">

		<td style="width:30%; vertical-align:top; padding:0px">
			<%-- folders --%>
				<div>
					<div class="zoneHeader" style="padding-top:20px; padding-bottom:10px">MY ALBUMS</div>
					<ul id=folders style="list-style-type:none; padding-left:0px">
						<li class="zoneList" title="common">
							<div class="zoneName bgColor" style="width:156px" onclick="showContents('common')">common</div>
						</li>
						<c:forEach var="folder" items="${foldersList}">
							<li id="f${folder}" class="zoneList myZones" title="${folder}">
								<div class="zoneName" onclick="showContents('${folder}')">${folder}</div>
								<div style="float:left; width:10px">&nbsp;</div>
								<div style="float:left; width:12px" onclick="deleteFolder('${folder}')" title="Delete this album">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
							</li>
						</c:forEach>
					</ul>
					<ul style="list-style-type:none; padding-left:0px">
						<li class="zoneList" title="New Album">
							<div class="zoneName" onclick="showCreateFolder()">New Album</div>
						</li>
					</ul>
					<div style="clear:both"></div>
				</div>
		</td>
		<td style="width:70%; vertical-align:top; padding:0px">
			<%-- heading --%>
			<div style="margin-top:20px; color:#888888; font-size:12px">Hint: Create a separate album for each of your posts. Empty albums will be automatically deleted.</div>
			<div id="imagesMessageTop" style="color:red">&nbsp;</div>
			<div style="margin-top:10px"><span id=selectedFolder style="font-weight:bold; color: #333333; padding-left:6px">common</span></div>
			<%-- images --%>
			<div id=images style="min-height:200px; margin-left:10px">
				<c:if test="${empty imagesList}">
					<div style="margin-left:10px; margin-top:30px; font-size:12px">No images in the album.</div>
				</c:if>
				<c:forEach var="image" items="${imagesList}">
					<table id="${image.name}" onclick="selectImage('${image.name}')" ondblclick="selectAndChooseImage('${image.name}')" class="boxedElement" style="width:400px"><tr>
						<td>
							<img class="profileThumb" src="https://s3-us-west-2.amazonaws.com/heatbudimages/${userId}/thumbs/common/${image.name}">
						</td>
						<td style="vertical-align:top">
							<div style="margin-left:20px">
								<div style="font-weight:bold">${image.name}</div>
								<div style="white-space:pre-line; font-size:12px"><script>document.write(new Date(${image.date}).toLocaleString());</script></div>
							</div>
						</td>
					</tr></table>
				</c:forEach>
				<div style="clear:both"></div>
			</div>
			<%-- tool box --%>
			<div style="margin-top:10px">
				<c:if test="${not empty CKEditorFuncNum}">
					<div style="float:left">
						<input class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="chooseImage()" type="button" value="Choose">
					</div>
				</c:if>
				<div style="float:left; margin-left:10px">
					<input class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="deleteImage()" type="button" value="Delete">
				</div>
				<div id=uploadImageDiv style="float:left; margin-left:10px">
					<input id=uploadImageA class="activeButton" style="height:35px; padding-left:10px; padding-right:10px" onclick="uploadImageInput.click()" type="button" value="Upload">
				</div>
				<div style="clear:both"></div>
				<input id=uploadImageInput type=file style="visibility:collapse" onchange="uploadImage(this.files[0])">
			</div>
			<div id="imagesMessage" style="color:red">&nbsp;</div>
		</td>
	</tr></table>
	<%-- End page content --%>

	<script>
		var FL = new Array();
		<c:forEach items="${foldersList}" var="folder" varStatus="loopCounter">
			FL[<c:out value="${loopCounter.index}"/>] = "<c:out value="${folder}"/>";
		</c:forEach>
		FL[FL.length] = "common";
		var IL = new Array();
		<c:forEach items="${imagesList}" var="image" varStatus="loopCounter">
			IL[<c:out value="${loopCounter.index}"/>] = "<c:out value="${image.name}"/>";
		</c:forEach>
	</script>

	<%-- Begin footer --%>
	<c:if test="${empty CKEditorFuncNum}">
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
	<%-- End footer --%>

</body></html>
