<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<html prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# article: http://ogp.me/ns/article#"><head>

	<!-- common -->
    <base target="_blank">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
	<c:if test="${pageType == 'POST'}">
	    <title>Heatbud | ${zoneName} - ${post.postTitle}</title>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
	    <title>Heatbud | ${zoneName}</title>
	</c:if>
	<link rel="alternate" type="application/rss+xml" href="https://www.heatbud.com/do/rss" />
	<meta name=viewport content="width=device-width, initial-scale=1">

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
	<meta property="og:type" content="article"/>
	<c:if test="${pageType == 'POST'}">
		<meta property="og:title" content="${fn:escapeXml(post.postTitle)}"/>
		<meta property="og:description" content="${fn:escapeXml(post.postSummary)}"/>
		<meta property="og:url" content="https://www.heatbud.com/post/${post.postId}"/>
		<meta property="og:image" content="${fn:replace(post.postHeadshot,'/thumbs/', '/social/')}"/>
		<c:if test="${not empty blogger.fbId}">
			<meta property="article:author" content="https://www.facebook.com/${blogger.fbId}"/>
		</c:if>
		<meta property="article:section" content="${fn:escapeXml(zoneName)}"/>
		<c:forTokens var="tag" items="${post.tags}" delims=",">
			<meta property="article:tag" content="${fn:trim(tag)}"/>
		</c:forTokens>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<meta property="og:title" content="${fn:escapeXml(zoneName)}"/>
		<meta property="og:description" content="${fn:escapeXml(zoneDesc)}"/>
		<meta property="og:url" content="https://www.heatbud.com/zone/${zoneId}"/>
		<meta property="og:image" content="${zoneHeadshot}"/>
		<meta property="article:section" content="${fn:escapeXml(zoneName)}"/>
	</c:if>
	<meta property="og:site_name" content="Heatbud"/>
	<meta property="fb:app_id" content="1444142922465514"/>
	<meta property="fb:profile_id" content="heatbud"/>
	<meta property="article:publisher" content="https://www.facebook.com/heatbud"/>

	<!-- for Twitter -->
	<c:if test="${pageType == 'POST'}">
		<meta name="twitter:title" content="${fn:escapeXml(post.postTitle)}"/>
		<meta name="twitter:description" content="${fn:escapeXml(post.postSummary)}"/>
		<meta name="twitter:image" content="${fn:replace(post.postHeadshot,'/thumbs/', '/social/')}"/>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<meta name="twitter:title" content="${fn:escapeXml(zoneName)}"/>
		<meta name="twitter:description" content="${fn:escapeXml(zoneDesc)}"/>
		<meta name="twitter:image" content="${zoneHeadshot}"/>
	</c:if>
	<meta name="twitter:card" content="summary_large_image"/>
	<meta name="twitter:site" content="@HeatbudLLC"/>

	<!-- for Google -->
	<c:if test="${pageType == 'POST'}">
	    <meta name="description" content="${fn:escapeXml(post.postSummary)}"/>
	    <meta name="keywords" content="${fn:escapeXml(post.tags)}"/>
		<link rel="canonical" href="https://www.heatbud.com/post/${post.postId}"/>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
	    <meta name="description" content="${fn:escapeXml(zoneDesc)}"/>
		<meta name="keywords" content="Social Blogging, Blogging, Business Website, Business, Website, Business Traffic, Traffic"/>
		<link rel="canonical" href="https://www.heatbud.com/zone/${zoneId}"/>
	</c:if>
	<link rel="publisher" href="https://plus.google.com/+Heatbud"/>
	<meta name="application-name" content="Heatbud"/>

	<!-- JS includes -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script src="/resources/js/heatbud-mrl-mobile-min.js?20180530"></script>
	<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
	<script async src="//platform-api.sharethis.com/js/sharethis.js#property=5a9e07be57f7f1001382393f&product=inline-share-buttons"></script>

    <!-- CSS includes -->
	<link type='text/css' rel='stylesheet' href="https://fonts.googleapis.com/css?family=Arvo%7CDroid+Sans+Mono%7CFauna+One%7CImprima%7CLato%7CMarvel%7COffside%7COpen+Sans%7COxygen+Mono%7CPermanent+Marker%7CRaleway%7CRoboto+Mono%7CScope+One%7CText+Me+One%7CUbuntu">
	<link type="text/css" href="/resources/css/main-min.css?20180530" media="screen" rel="stylesheet"/>

</head><body style="position:relative" data-role="page">

	<%-- used by all modal windows --%>
	<div id="modal-background"></div>

	<%-- Begin menu modal box --%>
	<div id=menuBox class="modal-box" style="width:65%; top:0%; left:0%; background-color:#333333; color:white; font-size:1em; box-shadow:none">
		<h3 style="margin-bottom:0.7em">Top Bloggers</h3>
		<div onclick="window.location.href='/top/bloggers-trending-now'; return false;" style="padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">TRENDING NOW</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/bloggers-all-time'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">ALL TIME</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<h3 style="margin-bottom:0.7em">Top Pages</h3>
		<div onclick="window.location.href='/top/pages-trending-now'; return false;" style="padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">TRENDING NOW</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/pages-all-time'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">ALL TIME</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<h3 style="margin-bottom:0.7em">Top Posts</h3>
		<div onclick="window.location.href='/top/posts-trending-now'; return false;" style="padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">TRENDING NOW</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/posts-all-time'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">ALL TIME</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/top/posts-just-published'; return false;" style="padding-top:0.4em; padding-bottom:0.15em; border-bottom:1px solid #616161">
			<div style="float:left; padding-left:10px">JUST PUBLISHED</div>
			<div style="float:right; color:#919191">&gt;</div>
			<div style="clear:both"></div>
		</div>
		<div onclick="window.location.href='/do/help'; return false;" style="float:left; margin-top:1em; padding:0.5em 1em; text-align:center; border:1px solid #616161">Help Center</div>
		<div onclick="$('body, #menuBox, #modal-background').toggleClass('active');" style="float:right; margin-top:1em; padding:0.5em 1em; text-align:center; border:1px solid #616161">Close</div>
		<div style="clear:both"></div>
	</div>
	<%-- End menu modal box --%>

	<%-- Begin zone header --%>
	<div style="background-color:rgb(123,205,123); padding:2%">
		<div style="text-align:center">
			<div onclick="$('body, #menuBox, #modal-background').toggleClass('active');"><img style="max-width:95%; border:none" src="/resources/images/heatbud-logo-mobile.png"/></div>
		</div>
		<table style="width:100%; border-spacing:0">
			<tr style="width:100%">
				<td style="text-align:center; width:100%" colspan=2>
					<div style="font-size:1em; color:white">You are in the zone</div>
					<div id=zoneNameDiv itemprop="articleSection" style="font-family:'Permanent Marker', Helvetica, Arial; font-size:1.5em; color:white; font-weight:bold">${zoneName}</div>
				</td>
			</tr>
			<tr style="width:100%">
				<td style="text-align:center; width:100%" colspan=2>
					<c:if test="${pageType == 'POST'}">
						<a target="_self" style="font-size:1.3em; color:white; text-decoration:none" href="/zone/${zoneId}" title="Zone Home Page">
							<span>Visit zone home page</span>
						</a>
					</c:if>
				</td>
			</tr>
			<tr style="width:100%; margin-top:2%">
				<td style="text-align:left; width:47%">
					<c:if test="${pageType == 'POST'}">
						<c:if test="${prevPostId != 'NULL'}">
							<a target="_self" style="font-size:1.3em; color:white; text-decoration:none" href="/post/${prevPostId}" title="Previous Post">
								<span style="font-weight:bold">&#8249;</span>
								<span> prev post</span>
							</a>
						</c:if>
					</c:if>
				</td>
				<td style="text-align:right; width:47%">
					<c:if test="${pageType == 'POST'}">
						<c:if test="${nextPostId != 'NULL'}">
							<a target="_self" style="font-size:1.3em; color:white; text-decoration:none" href="/post/${nextPostId}" title="Next Post">
								<span>next post </span>
								<span style="font-weight:bold">&#8250;</span>
							</a>
						</c:if>
					</c:if>
					<c:if test="${pageType == 'ZONE'}">
						<c:if test="${prevPostId != 'NULL'}">
							<a target="_self" style="font-size:1.3em; color:white; text-decoration:none" href="/post/${prevPostId}" title="Start Reading">
								<span>start reading </span>
								<span style="font-weight:bold">&#8250;</span>
							</a>
						</c:if>
					</c:if>
				</td>
			</tr>
		</table>
	</div>
	<%-- End zone header --%>

	<%-- Begin Page --%>
	<div itemscope itemtype="http://schema.org/Article" style="padding:2%; background-color:#eff2f4">

		<%-- Error for the Zone or Post --%>
		<c:if test="${not empty ERROR}">
			<div style="margin-top:3%; min-width:65%">
				<div style="line-height:150%; padding:3%; margin-top:3%">
					<div class="h1">Error:</div>
					${ERROR}<br>
				</div>
			</div>
		</c:if>

		<%-- Begin if no ERROR --%>
		<c:if test="${empty ERROR}">

			<input id=zoneIdHidden type=hidden value="${zoneId}">
			<input id=userIdHidden type=hidden value="${userId}"/>
			<c:if test="${pageType == 'POST'}">
				<input id=postIdHidden type=hidden value="${post.postId}"/>
				<input id=bloggerIdHidden type=hidden value="${post.bloggerId}"/>
			</c:if>

			<div style="padding:3% 0% 3% 0%; margin-bottom:3%; text-align:center; background:#F3FAB6; border:1px solid #d1d1d1">
				<a style="font-family:'Fauna One', Helvetica, Arial; font-size:0.8em; color:#222222" target="_self" href=/do/login>Signup as a freelancer and earn money writing for businesses!</a>
			</div>

			<%-- post title --%>
			<div id="postTitleMain" style="margin-top:3%; padding:2%">
				<c:if test="${pageType == 'POST'}">
					<div itemprop="name" id="postTitle" style="font-family:'Fauna One', Helvetica, Arial; font-size:1.3em; font-weight:bold; color:rgb(31,79,130)">${fn:escapeXml(post.postTitle)}</div>
				</c:if>
			</div>

			<%-- post summary and HI --%>
			<div id="postSummaryMain" style="float:left; margin-top:2%; padding:2%">
				<c:if test="${pageType == 'POST'}">
					<input id="postSummaryHidden" type=hidden value="${fn:escapeXml(post.postSummary)}"/>
					<div style="font-size:1.2em">
						<a target="_self" href="/${post.bloggerId}">
							<span itemprop="author" itemscope itemtype="http://schema.org/Person">
								<span itemprop="name">${blogger.entityName}</span>
							</span>
						</a>
					</div>
					<c:if test="${not empty blogger.googleId}">
						<div style="padding:3%; margin-top:3%; border-radius:3%; background-color:#EE4141">
							<a style="color:white" href="https://plus.google.com/${blogger.googleId}?rel=author">Circle me on Google+</a>
						</div>
					</c:if>
					<c:if test="${not empty blogger.fbId}">
						<div style="padding:3%; margin-top:3%; border-radius:3%; background-color:#3B5998">
							<a style="color:white" href="https://www.facebook.com/${blogger.fbId}">Friend me on Facebook</a>
						</div>
					</c:if>
					<div id="postUpdateDate" style="color:#909090; margin-top:2%"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></div>
					<div style="margin-top:2%">${post.views} unique views</div>
				</c:if>
			</div>
			<div id="postHIMain" style="float:right; width:120px; margin-right:5px; font-family:Calibri,Helvetica; color:rgb(144, 144, 144)">
				<c:if test="${pageType == 'POST'}">
					<input id=currentVoteHidden type=hidden value="${currentVote}">
					<div title="Post Heat Index" style="min-width:100px; text-align:right; border-bottom:1px solid #e0e0e0">
						<span id=postHI style="font-size:20px">${post.hi}</span>
						<img alt="Heatbud Index" style="width:10px; height:14px; margin-left:1px; border:none" src="/resources/images/favicon.ico"/>
					</div>
					<div style="float:right">
						<table><tr>
						<td><span id="upVotes" style="margin-left:5px; font-size:13px">${post.upVotes}</span></td>
						<td style="vertical-align:bottom"><img id="voteUpImg" alt="Vote Up" title="Your current vote: UP" style="width:14px; height:18px; margin-left:-2px; border:none" src="/resources/images/vote-up.png" onclick="return false;"/></td>
						<td><span id="downVotes" style="margin-left:5px; font-size:13px">${post.downVotes}</span></td>
						<td style="vertical-align:bottom"><img id="voteDownImg" alt="Vote Down" title="Your current vote: DOWN" style="width:14px; height:18px; margin-left:-2px; border:none" src="/resources/images/vote-down.png" onclick="return false;"/></td>
						<td><span id="comments" title="Comments" style="margin-left:5px; font-size:13px">${post.comments}</span></td>
						<td style="vertical-align:bottom"><a href="#postComments" target="_self"><img id="commentsImg" alt="Comments" title="comments" style="width:8px; height:10px; margin-left:-1px; border:none" src="/resources/images/comment-post.gif"/></a></td>
						</tr></table>
					</div>
				</c:if>
			</div>
			<div style="clear:both"></div>

			<%-- headshot image : zone or post --%>
			<div style="text-align:center; margin-top:3%">
				<c:if test="${pageType == 'POST'}">
					<img itemprop="image" alt="${post.postTitle}" title="${post.postTitle}" style="width:90%" src="${fn:replace(post.postHeadshot,'/thumbs/', '/social/')}">
				</c:if>
				<c:if test="${pageType == 'ZONE'}">
					<img itemprop="image" alt="${zoneName}" title="${zoneName}" style="width:90%" src="${zoneHeadshot}"/>
				</c:if>
			</div>

			<c:if test="${pageType == 'ZONE'}">

				<%-- zone share controls --%>
				<div class="sharethis-inline-share-buttons"></div>

				<%-- zone admins --%>
				<div id=zoneAdmins style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Admins</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<c:forEach items="${admins}" var="blogger" varStatus="loopCounter">
							<div style="margin-left:5px; float:left">
								<div>
									<c:if test="${not empty blogger.profilePhoto}">
										<a target="_self" href="/${blogger.entityId}">
											<img alt="${blogger.entityId}" title="${blogger.entityId}" class="adminThumb" src="${blogger.profilePhoto}">
										</a>
									</c:if>
									<c:if test="${empty blogger.profilePhoto}">
										<a target="_self" href="/${blogger.entityId}">
											<img alt="${blogger.entityId}" title="${blogger.entityId}" class="adminThumb" src="/resources/images/def-blogger-photo.jpg">
										</a>
									</c:if>
								</div>
								<div style="font-size:18px; font-weight:bold">
									<a target="_self" href="/${blogger.entityId}">${blogger.entityName}</a>
								</div>
							</div>
						</c:forEach>
						<c:if test="${empty admins}">
							<div style="margin-left:5px; float:left">
								No Admins for this Zone.
							</div>
						</c:if>
						<div style="clear:both"></div>
					</div>
				</div>

				<%-- zone description --%>
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Description</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<div id=zoneDescDiv style="padding-bottom:10px">${zoneDesc}</div>
					</div>
				</div>

				<%-- zone stats --%>
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Statistics</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<div style="font-weight: bold">Zone Heat Index: ${zoneHI} <img style="width:10px; height:16px; border:none" src="/resources/images/favicon.ico"/></div>
						<div>Posts: ${posts}</div>
						<div>Comments: ${comments}</div>
					</div>
				</div>

				<%-- zone who --%>
				<div style="width:100%; padding:1%">
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Who can post in this zone?</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<c:if test="${zoneWho == 'E'}">
							<input type="radio" name="zoneWhoRadio" checked disabled>Any Heatbud User<br>
							<input type="radio" name="zoneWhoRadio" disabled>Only the Admins<br>
						</c:if>
						<c:if test="${zoneWho == 'A'}">
							<input type="radio" name="zoneWhoRadio" disabled>Any Heatbud User<br>
							<input type="radio" name="zoneWhoRadio" checked disabled>Only the Admins<br>
						</c:if>
					</div>
				</div>

				<%-- top three posts in the zone --%>
				<div id=zoneTopPostsDiv>
					<div class="profileLabel h1" style="width:94%; margin-top:3%"><span style="border-bottom: 5px solid rgb(139, 197, 62); padding-bottom:4px">Top posts in this Zone</span></div>
					<div class="profileDiv" style="white-space:normal; width:94%">
						<div>
							<table style="border-spacing:2%">
								<c:if test="${not empty prevPost}">
									<c:set var="votes" value="${prevPost.upVotes-prevPost.downVotes}"/>
									<tr>
										<td style="vertical-align:top">
											<div style="font-size:200%; text-align:center">${votes}</div>
											<div style="font-size:11px; color:#909090">votes</div>
										</td>
										<td style="vertical-align:top">
											<div style="font-size:200%; text-align:center">${prevPost.comments}</div>
											<div style="font-size:11px; color:#909090">comments</div>
										</td>
										<td style="vertical-align:top; width:100%">
											<div style="margin-left:10px">
												<div style="font-weight:bold">
													<a target="_self" href="/post/${prevPost.postId}">${prevPost.postTitle}</a>
												</div>
												<div style="font-size:12px; color:#909090; margin-top:1%">
													<span><a target="_self" href="/${prevPost.bloggerId}">${fn:escapeXml(prevPost.bloggerName)}</a></span>
													<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
													<span style="color:#909090"><script>document.write(new Date(${prevPost.updateDate}).toLocaleString());</script></span>
												</div>
												<div>${prevPost.postSummary}</div>
											</div>
										</td>
									</tr>
								</c:if>
								<c:if test="${not empty post}">
									<c:set var="votes" value="${post.upVotes-post.downVotes}"/>
									<tr>
										<td style="vertical-align:top">
											<div style="font-size:200%; text-align:center">${votes}</div>
											<div style="font-size:11px; color:#909090">votes</div>
										</td>
										<td style="vertical-align:top">
											<div style="font-size:200%; text-align:center">${post.comments}</div>
											<div style="font-size:11px; color:#909090">comments</div>
										</td>
										<td style="vertical-align:top; width:100%">
											<div style="margin-left:10px">
												<div style="font-weight:bold">
													<a target="_self" href="/post/${post.postId}">${post.postTitle}</a>
												</div>
												<div style="font-size:12px; color:#909090; margin-top:1%">
													<span><a target="_self" href="/${post.bloggerId}">${fn:escapeXml(post.bloggerName)}</a></span>
													<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
													<span style="color:#909090"><script>document.write(new Date(${post.updateDate}).toLocaleString());</script></span>
												</div>
												<div>${post.postSummary}</div>
											</div>
										</td>
									</tr>
								</c:if>
								<c:if test="${not empty nextPost}">
									<c:set var="votes" value="${nextPost.upVotes-nextPost.downVotes}"/>
									<tr>
										<td style="vertical-align:top">
											<div style="font-size:200%; text-align:center">${votes}</div>
											<div style="font-size:11px; color:#909090">votes</div>
										</td>
										<td style="vertical-align:top">
											<div style="font-size:200%; text-align:center">${nextPost.comments}</div>
											<div style="font-size:11px; color:#909090">comments</div>
										</td>
										<td style="vertical-align:top; width:100%">
											<div style="margin-left:10px">
												<div style="font-weight:bold">
													<a target="_self" href="/post/${nextPost.postId}">${nextPost.postTitle}</a>
												</div>
												<div style="font-size:12px; color:#909090; margin-top:1%">
													<span><a target="_self" href="/${nextPost.bloggerId}">${fn:escapeXml(nextPost.bloggerName)}</a></span>
													<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>
													<span style="color:#909090"><script>document.write(new Date(${nextPost.updateDate}).toLocaleString());</script></span>
												</div>
												<div>${nextPost.postSummary}</div>
											</div>
										</td>
									</tr>
								</c:if>
								<tr><td colspan="3">
									<c:if test="${empty prevPost}">
										<div>Be the first to post in this zone!</div>
									</c:if>
								</td></tr>
								<tr><td colspan="3">
									<c:if test="${not empty prevPost}">
										<div style="width:100%; text-align:right; font-size:16px"><a target="_self" href="/post/${prevPost.postId}">Start reading posts in this zone...</a></div>
									</c:if>
								</td></tr>
							</table>
						</div>
						<div style="clear:both"></div>
					</div>
				</div>

			</c:if>

			<c:if test="${pageType == 'POST'}">

				<%-- post share controls --%>
				<div class="sharethis-inline-share-buttons"></div>

				<%-- post content --%>
				<div id="postContent" style="margin-top:3%; padding:2%; background-color:white; max-width:100%">
					<c:choose>
						<c:when test="${post.publishFlag != 'D' }">
							<c:import url="https://heatbudposts.s3.amazonaws.com/${post.postId}"/>
						</c:when>
						<c:otherwise>
							<span style="color:rgb(144, 144, 144); font-size:1.2em"><b>This post has been deleted.</b></span>
						</c:otherwise>
					</c:choose>
				</div>

				<%-- Begin post comments --%>
				<div id="postComments" style="width:100%; background-color:white; padding:1%; margin-top:2%">
					<div class="h1">Comments:</div>
					<div id="commentsDiv">
						<c:if test="${empty commentsList}">
							<c:if test="${ post.publishFlag == 'Y' }"><div style="color:rgb(144, 144, 144); font-size:1.2em; margin-top:2%">No comments yet.</div></c:if>
							<c:if test="${ post.publishFlag == 'N' }"><div style="color:rgb(144, 144, 144); font-size:1.2em; margin-top:2%">No comments yet. This post has not yet been published.</div></c:if>
						</c:if>
						<c:if test="${not empty commentsList}">
							<c:forEach var="comment" items="${commentsList}">
								<%-- split commentDate into thanked, parentCommentDate and commentDate --%>
								<c:set var="thanked" value="${fn:substring(comment.commentDate,0,1)}"/>
								<c:set var="pcd" value="${fn:substring(comment.commentDate,1,14)}"/>
								<c:set var="cd" value="${fn:substring(comment.commentDate,14,27)}"/>
								<%-- set header and indent css styles for parent comments and sub-comments --%>
								<c:if test="${cd == '9999999999999'}">
									<c:set var="cd" value="${pcd}"/>
									<c:set var="commentHeaderCSS" value="margin-top:16px; background-color:#F5F5F5; border-bottom:1px solid #BBBBBB; padding:4px;"/>
									<c:set var="commentIndentCSS" value=""/>
								</c:if>
								<c:if test="${cd != pcd}">
									<c:set var="cd" value="${9999999999999-cd}"/>
									<c:set var="commentHeaderCSS" value=""/>
									<c:set var="commentIndentCSS" value="margin-left:15px;"/>
								</c:if>
								<%-- commenter name, date, delete icon, thank comments --%>
								<div style="font-size:1.2em; ${commentIndentCSS} ${commentHeaderCSS}">
									<span id="thankComment${comment.commentDate}Span">
										<c:if test="${thanked == '2' && cd == pcd }">
											<img alt="thanked by the blogger" style="max-height:15px; margin-right:10px; border:none" src="/resources/images/thanked.jpg"/>&nbsp;
										</c:if>
									</span>
									<span style="color:#909090"><script>document.write(new Date(${cd}).toLocaleString());</script></span><br/>
									<a target="_self" href="/${comment.commenterId}">${comment.commenterName}</a>
								</div>
								<%-- comment text --%>
								<div id="comment${comment.commentDate}Div" style="font-size:1.2em; ${commentIndentCSS} margin-top:4px; white-space:pre-line">${comment.commentText}</div>
							</c:forEach>
						</c:if>
					</div>
					<div id="commentsNavigation" style="margin-left:2%">
						<%-- previous page link will be hidden on the first page --%>
						<input id=commentsKeyPrevHidden type=hidden value="NULL">
						<div id="getCommentsPrevDiv" style="width:48%; margin-top:10px; float:left; text-align:left; visibility:hidden">
							<a target="_self" id="getCommentsPrev" style="font-size:1.5em; background-color:#FF3333; color:white; padding:2px 6px 2px 6px; border-radius:5px; text-decoration:none" href="javascript:">BACK</a>
						</div>
						<%-- next page link will be set to visible if the key is not NULL --%>
						<input id=commentsKeyNextHidden type=hidden value="${commentsKeyNext}">
						<c:if test="${commentsKeyNext != 'NULL'}">
							<div id="getCommentsNextDiv" style="width:48%; margin-top:10px; float:right; text-align:right">
								<a target="_self" id="getCommentsNext" style="font-size:1.5em; background-color:#FF3333; color:white; padding:2px 6px 2px 6px; border-radius:5px; text-decoration:none" href="javascript:">MORE</a>
							</div>
						</c:if>
					</div>
					<div style="clear:both"></div>
				</div>
				<%-- End Comments --%>

			</c:if>

		</c:if>
		<%-- End if no ERROR --%>

	</div>
	<%-- End Page --%>

	<%-- set header --%>
	<c:if test="${pageType == 'POST'}">
		<script>
			window.history.replaceState("Heatbud", "Heatbud | ${fn:escapeXml(zoneName)} - ${fn:escapeXml(post.postTitle)}", "/post/${post.postId}");
		</script>
	</c:if>
	<c:if test="${pageType == 'ZONE'}">
		<script>
			window.history.replaceState("Heatbud", "Heatbud | ${zoneName}", "/zone/${zoneId}");
		</script>
	</c:if>

	<%-- Begin Google Ads --%>
	<div style="margin-bottom:3%; font-size:2em">
		<!-- Heatbud-Responsive -->
		<ins class="adsbygoogle"
		     style="display:block"
		     data-ad-client="ca-pub-3344897177583439"
		     data-ad-slot="6645813308"
		     data-ad-format="auto"></ins>
		<script>
		(adsbygoogle = window.adsbygoogle || []).push({});
		</script>
	</div>
	<%-- End Google Ads --%>

	<div style="padding:3% 6% 3% 6%; margin-bottom:3%; text-align:center; background:#F3FAB6; border:1px solid #d1d1d1">
		<div style="font-family:'Fauna One', Helvetica, Arial; font-size:1.2em; color:#222222">Connect from PC or Tablet to Signup and Create Posts!</div>
	</div>

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