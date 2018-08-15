<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html><head>

    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="description" content="Heatbud helps people share their interesting ideas, opinions, stories and funny stuff with the world."/>
    <meta name="keywords" content="macroblogging, blog, blogging, collaboration, travel, sports, news, music, movies, jokes">
    <link rel="shortcut icon" href="/resources/images/favicon.ico"/>
    <title>Heatbud - Admin Page</title>

	<!-- JQuery includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

	<!-- CKEditor includes -->
	<script src="/resources/js/ckeditor/ckeditor.js"></script>

    <!-- Heatbud includes -->
	<link type="text/css" href="/resources/css/main-min.css" media="screen" rel="stylesheet"/>

    <!-- Google fonts includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">

	<script>
	function selectFunction() {
		var f = functionOptions.selectedIndex-1;
		var s = fl[f].split("|");
		document.getElementById("desc").innerHTML = s[1];
		document.getElementById("input1").value = s[2];
		document.getElementById("input2").value = s[3];
		document.getElementById("input3").value = s[4];
		document.getElementById("input4").value = s[5];
		document.getElementById("input5").value = s[6];
	}
	function runFunction() {
		var f = functionOptions.value;
		errors.innerHTML = f + ' (' + input1.value + ')' + ' running...';
		$.ajax({
			type: "POST",
			url: "/admin/runFunction",
			data: { functionName: f, value1 : input1.value, value2 : input2.value, value3 : input3.value, value4 : input4.value, value5 : input5.value },
			dataType: "json"
		}).always(function (resp) {
			errors.innerHTML = resp.retMessage;
		});
	}
	</script>

</head><body style="position:relative">
<div id="main">

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

	<div style="padding:58px 10px 10px 10px">
 		<h1>Run a java function...</h1>
		<div class="labelDiv">
			<span>Function</span>
			<select id="functionOptions" onchange="selectFunction()">
			    <option>&nbsp;</option>
			</select>
			<input id="runFunction" onclick="runFunction();" type="button" value="Run">
		</div>
		<div class="labelDiv">
			<span>Description: </span>
			<span id="desc"></span>
		</div>
		<div class="labelDiv">
			<span>Input 1&nbsp;&nbsp;&nbsp;</span>
			<input id="input1" type="text" style="width:450px" value="">
		</div>
		<div class="labelDiv">
			<span>Input 2&nbsp;&nbsp;&nbsp;</span>
			<input id="input2" type="text" style="width:400px" value="">
		</div>
		<div class="labelDiv">
			<span>Input 3&nbsp;&nbsp;&nbsp;</span>
			<input id="input3" type="text" style="width:700px" value="">
		</div>
		<div class="labelDiv">
			<span>Input 4&nbsp;&nbsp;&nbsp;</span>
			<input id="input4" type="text" style="width:400px" value="">
		</div>
		<div class="labelDiv">
			<span style="vertical-align:top">Input 5&nbsp;&nbsp;&nbsp;</span>
			<textarea id="input5" rows="10" cols="100"></textarea>
		</div>
		<div class="labelDiv">Return Message:</div>
		<div id="errors" class="errors"></div>
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

	<script>
		var fl = ${functionList};
		var x = document.getElementById("functionOptions");
		for(var i = 0; i < fl.length; i++) {
			var option = document.createElement("option");
			var s = fl[i].split("|");
			option.text = s[0];
			x.add(option);
		}
	</script>
</div>
</body></html>
