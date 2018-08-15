<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html><head>

	<!-- common -->
	<title>Heatbud | Social Blogging for Bloggers and Businesses</title>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
	<link rel="alternate" type="application/rss+xml" href="https://www.heatbud.com/do/rss" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />

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
	<meta property="og:type" content="website"/>
	<meta property="og:title" content="Heatbud | Social Blogging for Bloggers and Businesses"/>
    <meta property="og:description" content="Create Social Blog for your business starting $29 a month. OR, Add Social Blogging to your Business starting $29 a month!"/>
	<meta property="og:url" content="https://www.heatbud.com/top/posts-trending-now"/>
	<meta property="og:image" content="https://www.heatbud.com/resources/images/fb-share-picture.png"/>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>

	<!-- JS includes -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-login-min.js?20180530"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head><body style="position:relative">

	<%-- Begin facebook SDK --%>
	<div id="fb-root"></div>
	<script>
		// init the FB JS SDK
		window.fbAsyncInit = function() {
			FB.init({
			appId      : '1444142922465514',	// App ID from the app dashboard
			status     : true					// Check Facebook Login status
			});
		};

		// Load the SDK asynchronously
		(function(d, s, id){
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id)) {return;}
			js = d.createElement(s); js.id = id;
			js.src = "//connect.facebook.net/en_US/all.js";
			fjs.parentNode.insertBefore(js, fjs);
		}(document, 'script', 'facebook-jssdk'));
	</script>
	<%-- End facebook SDK --%>

	<%-- Begin Header --%>
	<div style="position: relative">
		<%-- banner --%>
		<div style="float:left; width:100%; height:100vh; filter:blur(1px) brightness(55%); background-size:cover; background-image:url('/resources/images/banner.jpg');"></div>
		<%-- logo & menu --%>
		<div style="position:absolute; width:100%; height:100vh">
			<div style="float:left; width:18%">
				<img alt="Heatbud logo" style="width:100%; margin-top:6px; margin-left:20px; border:none" src="/resources/images/heatbud-logo.png"/>
			</div>
			<div style="float:right; padding-right:10px">
				<div style="float:left; padding:30px 15px 20px 15px"><a href="/top/posts-trending-now#topChartsAnchor" class="mainSelection">TOP CHARTS</a></div>
				<div style="float:left; padding:30px 15px 20px 15px"><a href="/post/singing-bowls-singing-bowls-and-chakras" class="mainSelection">BLOG POSTS</a></div>
				<div style="float:left; padding:30px 15px 20px 15px"><a href="/do/search" class="mainSelection">SEARCH</a></div>
				<div style="float:left; padding:30px 15px 20px 15px"><a href="/do/help" class="mainSelection">HELP CENTER</a></div>
			</div>
		</div>
		<div style="clear:both"></div>
		<div style="position:absolute; width:100%; bottom:8vh; color:white">
			<%-- tag line --%>
			<div style="margin:0 auto; width:900px; font-size:3.0em">JOIN THE SOCIAL BLOGGING REVOLUTION!</div>
			<%-- inspire visitors to action --%>
			<div style="margin:0 auto; margin-top:10px; width:670px; font-family:'Fauna One', Calibri, Arial, Sans-serif">
				<div style="float:left; font-size:1.7em">
					<div>FOR BLOGGERS</div>
					<div style="font-size:0.65em"><a style="color:white" href="/do/help/main/pricing">&bull; Earn followers, and/or</a></div>
					<div style="font-size:0.65em"><a style="color:white" href="/do/help/main/pricing">&bull; Earn money</a></div>
				</div>
				<div style="float:left; margin-left:80px; font-size:1.7em">
					<div>FOR BUSINESSES</div>
					<div style="font-size:0.65em"><a style="color:white" href="/do/help/main/why-1">&bull; Build a new website for your business</a></div>
					<div style="font-size:0.65em"><a style="color:white" href="/do/help/main/why-1">&bull; Add Social Blogging to your current website</a></div>
				</div>
				<div style="clear:both"></div>
			</div>
			<%-- login --%>
			<div style="margin:0 auto; margin-top:20px; border:1px solid white; width:670px; padding:10px; font-family:'Fauna One', Calibri, Arial, Sans-serif">
				<form method="post" action="/do/login">
					<div class="textDiv">
						<input id="j_username" name="j_username" type="text"
							class="inputText" value="${username}" placeholder="Email Address" />
						<input id="j_password" type="password" style="margin-left:10px" class="inputText"
							name="j_password" value="" placeholder="Password" />
						<button id="loginbutton" class="activeButton" style="margin-left:10px; width:80px; height:35px">sign in</button>
					</div>
					<div class="labelDiv" style="margin-top: 2px">
						<input id="rememberMe" style="float:left; margin-left:30px; width:12px" type='checkbox' name='remember-me' checked/>
						<label for="rememberMe" style="float:left; display:table-cell; padding-top:2px; cursor:pointer; color:white; font-size:1.1em">&nbsp;Remember me</label>
						<a style="float:left; margin-left:40px; padding-top:2px; color:white" href="/do/forgot-password">Forgot password?</a>
						<a style="float:left; margin-left:40px; padding-top:2px; color:white" onclick="document.querySelector('#signupMain').scrollIntoView({ behavior: 'smooth' });">Create a new account</a>
					</div>
					<div style="clear: both"></div>
				</form>
			</div>
			<%-- error --%>
			<div style="margin:0 auto; width:500px; text-align:center; font-size:1.2em; color:yellow; margin-top:4px">
				<c:choose>
					<c:when test="${error == 'true'}">
						<div id="signinerror">Invalid username or password.</div>
					</c:when>
					<c:when test="${error == 'denied'}">
						<div id="signinerror">Please sign in to continue.</div>
					</c:when>
					<c:when test="${error == 'logout'}">
						<div id="signinerror">Logged out successfully.</div>
					</c:when>
					<c:when test="${not empty error}">
						<div id="signinerror">${error}</div>
					</c:when>
					<c:when test="${empty error}">
						<div id="signinerror">&nbsp;</div>
					</c:when>
				</c:choose>
			</div>
		</div>
	</div>
	<div style="clear:both"></div>
	<%-- End Header --%>

	<%-- Begin Signup --%>
	<table id="signupMain" style="padding-top:40px; width:100%; border-spacing:0px">
		<tr style="width:100%">
			<td style="width:42%; vertical-align:top">
				<div style="padding:5% 0% 50% 40%">
					<div class="h1" style="margin-bottom:10px">Signup As</div>
					<input id=blogger type="radio" name="signupType" onchange="$('#signup').css({'display': 'block'});$('#businessNote').css({'display': 'none'});">
					<label for="blogger" style="display:inline-block; cursor:pointer; color:#888888; font-size:1.2em">&nbsp;Blogger</label><br/>
					<input id=business type="radio" name="signupType" onchange="$('#signup').css({'display': 'block'});$('#businessNote').css({'display': 'block'});">
					<label for="business" style="display:inline-block; cursor:pointer; color:#888888; font-size:1.2em">&nbsp;Business</label><br/>
				</div>
			</td>
			<td style="width:42%; vertical-align:top">
				<div style="padding:5% 0% 5% 5%">
					<div id="signup" style="display:none">
						<form:form method="post" action="/do/signup" commandName="user">
							<div style="width:400px">
								<form:input id="signupSource" type="hidden" path="source"/>
								<form:input id="signupFbId" type="hidden" path="fbId"/>
								<form:input id="signupGender" type="hidden" path="gender"/>
								<form:input id="signupAbout" type="hidden" path="about"/>
								<form:input id="signupBirthday" type="hidden" path="birthday"/>
								<form:input id="signupContact" type="hidden" path="contact"/>
								<form:input id="signupTimeZone" type="hidden" path="timeZone"/>
								<form:input id="signupProfilePhoto" type="hidden" path="profilePhoto"/>
								<form:input id="signupProfileBG" type="hidden" path="profileBG"/>
								<div id="businessNote" style="padding:5px; display:none; margin-top:5px; border:1px solid #999; color:#858585"><b>Please DO NOT enter your business information here.</b><br/>This is the personal account of Business Owner or an Account Manager. You may create your Business page later from the Page Manager.</div>
								<div class="facebook" style="width:200px; border:1px solid rgb(136, 136, 136); padding:5px; cursor:pointer; margin-top:10px" onclick="signupFacebook(); return false;">
									<img alt="Signup using Facebook" style="margin-left:5px; height:20px; border:none" src="/resources/images/share-facebook.jpg"/>
									<span style="color:white; margin-left:5px">Signup using Facebook</span>
								</div>
								<div id="bloggerName">
									<div class="textDiv" style="float:left">
										<form:input id="signupFirstName" type="text" class="inputText" style="width:160px" path="firstName" placeholder="First Name" />
									</div>
									<div class="textDiv" style="float:left; margin-left:10px">
										<form:input id="signupLastName" type="text" class="inputText" style="width:160px" path="lastName" placeholder="Last Name" autocomplete='off' />
									</div>
									<div style="clear: both"></div>
								</div>
								<div class="textDiv" style="margin-top:5px">
									<form:input id="signupEmail" type="text" class="inputText" path="username" placeholder="Email (to send verification link)" />
								</div>
								<div class="textDiv">
									<input id="email2" type="text" class="inputText" name="email2" value="${user.username}" placeholder="Re-enter Email"/>
								</div>
								<div class="textDiv">
									<form:password class="inputText" style="float:left" path="password" placeholder="Password" autocomplete='off'/>
									<button id="signupbutton" class="activeButton" style="float:left; margin-left:10px; width:80px; height:35px">sign up</button>
								</div>
								<div style="clear: both"></div>
								<div class="labelDiv" style="margin-top:4px">
									<c:if test="${empty user.error}">
										<input id="privacy" style="margin-left:0px; padding-left:0px; width:12px" type='checkbox'/>
									</c:if>
									<c:if test="${not empty user.error}">
										<input id="privacy" style="margin-left:0px; padding-left:0px; width:12px" type='checkbox' checked/>
									</c:if>
									<label style="font-size:12px; padding-top:2px; cursor:pointer" for="privacy">
										<span>I'm signing up from </span><span id="country"></span> and I agree to Heatbud's <a target="_blank" href="/do/privacy">Terms</a>.
									</label>
								</div>
								<div id="signuperror" class="error">${user.error}</div>
							</div>
							<div style="clear: both"></div>
						</form:form>
					</div>
				</div>
			</td>
		</tr>
	</table>
	<%-- End Login and Signup --%>

    <script>
	function signupFacebook() {
		FB.login(function(response) {
			if (response.authResponse) {
				// connected - now fetch the data
				// reference: https://developers.facebook.com/docs/javascript/reference/FB.api
				// reference: https://developers.facebook.com/docs/graph-api/reference/user
				FB.api('/me', {fields: 'first_name, last_name, gender, email, hometown, birthday, website'}, function(response) {
					document.getElementById("signupFbId").value = response.id.trim();
					document.getElementById("signupFirstName").value = response.first_name.trim();
					document.getElementById("signupLastName").value = response.last_name.trim();
					document.getElementById("signupGender").value = response.gender.trim();
					try { document.getElementById("signupEmail").value = response.email.trim(); } catch (e) {}
					try { document.getElementById("email2").value = response.email.trim(); } catch (e) {}
					try { document.getElementById("signupAbout").value = response.hometown.trim(); } catch (e) {}
					try { document.getElementById("signupBirthday").value = response.birthday.trim(); } catch (e) {}
					try { document.getElementById("signupContact").value = response.email.trim(); } catch (e) {}
					try { document.getElementById("signupTimeZone").value = response.timezone.trim(); } catch (e) {}
				});
				// get profile photo and profile BG (read the first pictures from these albums)
				FB.api('/me/albums', {fields: 'id, name'}, function (response) {
				  for (album in response.data) {
				    if (response.data[album].name == "Profile Pictures") {
				      FB.api(response.data[album].id + "/photos", {fields: 'images'}, function(response) {
				    	  try { document.getElementById("signupProfilePhoto").value = response.data[0].images[0].source;  } catch (e) {}
				      });
				    }
				    if (response.data[album].name == "Cover Photos") {
				      FB.api(response.data[album].id + "/photos", {fields: 'images'}, function(response) {
				    	  try { document.getElementById("signupProfileBG").value = response.data[0].images[0].source;  } catch (e) {}
				      });
				    }
				  }
				  document.getElementById("signupSource").value = 'facebook';
				  document.getElementById("signuperror").innerHTML = 'We fetched your data from Facebook as available. Please fill-in the rest and click signup.';
				});
			} else {
				// cancelled
				signuperror.innerHTML = "Unable to get data from Facebook. Fill-in your details manually and click signup.";
			}
		}, {scope: 'email,public_profile,user_birthday'});
	}
    </script>

	<div>
		<c:if test="${ error == 'Thanks for signing up. Please sign in to continue.' || user.error == 'Please check your Inbox (also Junk folder) for Email Verification Request.' }">
			<!-- Google Code for Signup Conversion Page -->
			<script type="text/javascript">
				/* <![CDATA[ */
				var google_conversion_id = 987911956;
				var google_conversion_language = "en";
				var google_conversion_format = "2";
				var google_conversion_color = "ffffff";
				var google_conversion_label = "7NbKCMzmkgoQlK6J1wM";
				var google_remarketing_only = false;
				/* ]]> */
			</script>
			<script src="//www.googleadservices.com/pagead/conversion.js">
			</script>
			<noscript>
				<div style="display:inline;">
				<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/987911956/?label=7NbKCMzmkgoQlK6J1wM&amp;guid=ON&amp;script=0"/>
				</div>
			</noscript>
			<!-- Google analytics -->
			<script>
			  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
			  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
			  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
			  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
			  ga('create', 'UA-48436913-1', 'heatbud.com');
			  ga('send', 'pageview');
			</script>
		</c:if>
	</div>

	<script>
		window.history.replaceState("Heatbud", "Social Blogging Site", "/do/login");
		$.getJSON('https://api.ipdata.co', function(data) { $("#country").html(data.country_name); });
	</script>

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