
/******************************************/
/************ global variables ************/
/******************************************/

var editor = '';
var html = '';
var config = {};
var autoSaveId = '';
var saveInProgress = false;

/************************************************/
/************ jquery ready functions ************/
/************************************************/

$(document).ready(function() {

	/*** On window close, verify if data has been saved ***/

	$(window).on('beforeunload', function() {
		if ( editor != '' ) {
			if ( postTitleHidden.value.trim() != postTitleInput.value.trim() ||
				 postSummaryHidden.value.trim() != postSummaryInput.value.trim() ||
				 postContentHidden.value.trim() != editor.getData().trim()
				) {
					return "There are unsaved changes. Are you sure you want to close?";
			}
		}
	});

	/*** My Zones ***/

	// AJAX call to query next page of My Zones
	$("#getMyZonesNext").click(function() {
		myZonesKeyAlternateRefreshZOHidden.value = myZonesKeyRefreshZOHidden.value;
		myZonesKeyAlternateRefreshZoneIdHidden.value = myZonesKeyRefreshZoneIdHidden.value;
		myZonesKeyRefreshZOHidden.value = myZonesKeyNextZOHidden.value;
		myZonesKeyRefreshZoneIdHidden.value = myZonesKeyNextZoneIdHidden.value;
		$.ajax({
			type: "POST",
			url: "/zone/get-myzones-next",
			data: { myZonesKeyNextZO : myZonesKeyNextZOHidden.value, myZonesKeyNextZoneId : myZonesKeyNextZoneIdHidden.value },
			dataType: "json"
		}).always(function (resp) { populateMyZones(resp); });
	});

	// AJAX call to query previous page of My Zones
	$("#getMyZonesPrev").click(function() {
		$.ajax({
			type: "POST",
			url: "/zone/get-myzones-prev",
			data: { myZonesKeyPrevZO : myZonesKeyPrevZOHidden.value, myZonesKeyPrevZoneId : myZonesKeyPrevZoneIdHidden.value },
			dataType: "json"
		}).always(function (resp) { populateMyZones(resp); });
	});

	/*** Top Zones ***/

	// Show create zone box
	$(".createZone").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		$('body, #createZoneBox, #modal-background').toggleClass('active');
	});

	// AJAX call to query next page of TopZones
	$("#getTopZonesNext").click(function() {
		topZonesKeyAlternateRefreshZOHidden.value = topZonesKeyRefreshZOHidden.value;
		topZonesKeyAlternateRefreshZoneIdHidden.value = topZonesKeyRefreshZoneIdHidden.value;
		topZonesKeyRefreshZOHidden.value = topZonesKeyNextZOHidden.value;
		topZonesKeyRefreshZoneIdHidden.value = topZonesKeyNextZoneIdHidden.value;
		$.ajax({
			type: "POST",
			url: "/zone/get-topzones-next",
			data: { topZonesKeyNextZO : topZonesKeyNextZOHidden.value, topZonesKeyNextZoneId : topZonesKeyNextZoneIdHidden.value },
			dataType: "json"
		}).always(function (resp) { populateTopZones(resp); });
	});

	// AJAX call to query previous page of TopZones
	$("#getTopZonesPrev").click(function() {
		$.ajax({
			type: "POST",
			url: "/zone/get-topzones-prev",
			data: { topZonesKeyPrevZO : topZonesKeyPrevZOHidden.value, topZonesKeyPrevZoneId : topZonesKeyPrevZoneIdHidden.value },
			dataType: "json"
		}).always(function (resp) { populateTopZones(resp); });
	});

	/*** Favorite Current Zone ***/

	$(".favoriteCurrentZone").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		zid = document.getElementById('zoneIdHidden').value;
		zname = document.getElementById('zoneNameHidden').value;
		favoriteZoneById(zid, zname);
	});

	/*** Go to my last read post ***/

	$(".lastReadPost").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		window.location.href='/zone/'+zoneIdHidden.value;
	});

	/*** Edit Zone ***/

	// AJAX call to edit zone description
	$("#editZoneDesc").on("click", "a", (function() {
		var d = $('#zoneDescDiv').html();
		$('#zoneDescDiv').html('<textarea cols="52" rows="6" id=zoneDescInput>'+d+'</textarea>');
		$('#editZoneDesc').html('<input class="activeButton" type=submit value="Save Description">');
	}));

	// AJAX call to save zone description
	$("#editZoneDesc").on("click", "input", (function() {
		$('#editZoneDesc').html('Please wait...');
		var d = $('#zoneDescInput').val();
		$('#zoneDescDiv').html(d);
	   	$.post(	"/zone/update-zone-desc",
	   			{ zoneId : zoneIdHidden.value, zoneDesc : encodeURIComponent(d) }
	   		).always(function () { $('#editZoneDesc').html('<a>Edit Description</a>'); });
	}));

	// Become Admin of zone (when there are no admins)
	$(".becomeAdmin").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		if (confirm("Are you sure you want to become admin of the zone \""+zoneNameHidden.value+"\"?")) {
			$.ajax({
				type: "POST",
				url: "/zone/become-admin",
				data: { zoneId : zoneIdHidden.value },
				dataType: "json"
			}).always(function (resp) { $('#adminMessage').html('Congratulations! You are now the admin of this Zone. Please reload the page.'); });
		}
	});

	// Request to be Admin of the zone (when the zone already has one or more admins)
	$(".requestAdmin").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		if (confirm("Are you sure you want to request to become one of the admins of the zone \""+zoneNameHidden.value+"\"?")) {
			$.ajax({
				type: "POST",
				url: "/zone/request-admin",
				data: { zoneId : zoneIdHidden.value, zoneName : zoneNameHidden.value },
				dataType: "json"
			}).always(function (resp) { $('#adminRequestsMessage').html('Current Admins have been notified of your request.'); });
		}
	});

	// AJAX call to save zone who
	$("#editZoneWho").on("click", "a", (function() {
		$('#zoneWhoMessage').html('Please wait...');
		// read zone who
		var zw = document.getElementById("zoneWhoInput").value.trim();
	   	$.post(	"/zone/update-zone-who",
	   			{ zoneId : zoneIdHidden.value, zoneWho : encodeURIComponent(zw) }
	   		).always(function () { $('#zoneWhoMessage').html('Saved.'); });
	}));

	/*** Write Post ***/

	$(".writePost").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		if ( editor != '' ) return;
		// check if zone home page or post page
		var pageType = document.getElementById("pageTypeHidden").value;
		if ( pageType == 'ZONE' ) {
			// zone divs
			$('#zoneNameDiv').html('');
			$('#zoneHeadshotEditControls').html('');
			$('#zoneAdmins').html('');
			$('#zoneAdminRequests').html('');
			$('#zoneDesc').html('');
			$('#zoneStatsDiv').html('');
			$('#zoneWhoDiv').html('');
			$('#zoneTopPostsDiv').html('');
			document.getElementById("CKEditorFuncNumHidden").value = 'PostHeadshot';
		} else {
			// post divs
			$("#postHIMain").css({'display': 'none'});
			$('#postComments').html('');
			$('#postComment').html('');
			// post title edit controls
			$('#pageNameDiv').html('');
			$('#bloggerNameDiv').html('');
			$('#postTitle').html('');
		}
		postIdHidden.value = 'NEW';
		publishFlagHidden.value = 'N';
		// post edit controls
		$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
			'&nbsp;<input id=publishPost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish" disabled>'+
			'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
			'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
		$("#postEditControls").css({'padding-top': '20px'});
		// post saved time stamp
		$("#postSavedTimeStamp").css({'display': 'block'});
		var a = '&nbsp;';
		if (primaryPageIdHidden.value == '') {
			a = '<div style="margin-top:30px">(2) Business Page: When businesses order blog posts from you, a list of pages appears here.</div>' +
			'<span id="pageIdSelected" style="color:green"> </span>';
			pageIdSelectedHidden.value=' ';
		} else {
			a = '<div style="margin-top:30px; font-family:Comic Sans MS,Courier; font-size:15px; font-style:italic; color:#939393;">(2) Business Page:</div>' +
			'<div style="font-family:Comic Sans MS,Courier; font-size:15px; font-style:italic; color:#939393;"><input id=pageIdRadio type=radio name=pageIdInput onchange="pageIdSelected.innerHTML=primaryPageIdHidden.value; pageIdSelectedHidden.value=primaryPageIdHidden.value;" checked> ' +
			'<label for="pageIdRadio"> <span id="pageIdSelected" style="color:green; font-weight:bold;"> ' + primaryPageIdHidden.value + ' </span></label>&nbsp;&nbsp;&nbsp;&nbsp;' +
			'<input class="activeButton" onclick="showPageBox(\'mrl\');" type="button" value="change page">&nbsp;&nbsp;' +
		    '<span>( You will not be able to publish the blog post if the business page is not active. )</span></div>' +
		    '<div style="font-family:Comic Sans MS,Courier; font-size:15px; font-style:italic; color:#939393;"><input id=noPageRadio type=radio name=pageIdInput onchange="pageIdSelected.innerHTML=\'  \'; pageIdSelectedHidden.value=\'  \';">' +
			'<label for="noPageRadio"> <span style="color:green"> For Pleasure</span></label>&nbsp;&nbsp;' +
			'<span style="font-size:15px; color:#939393;">( When writing for pleasure, you are not allowed to use backlinks. )</span></div>';
			pageIdSelectedHidden.value=primaryPageIdHidden.value;
		}
		$('#postTitleEditControls').html('<div style="margin-top:20px; font-family:Comic Sans MS,Courier; font-size:15px; font-style:italic; color:#939393;">(1) Zone: <span style="color:green; font-weight:bold;">' + zoneNameHidden.value + '</span>&nbsp;&nbsp;' +
			'<span style="font-size:15px; color:#939393;">( Your blog post will be deleted without notice if the theme of your post does not match with the Zone Name. ' +
			'To change zone, click Close and enter a zone that matches the theme of your blog post. )</span></div>' +
			a +
			'<div style="margin-top:30px; font-family:Comic Sans MS,Courier; font-size:15px; font-style:italic; color:#939393">(3) Title:</div>' +
			'<input id="postTitleInput" type=text spellcheck="true" style="width:350px">&nbsp;<input id=similarPosts class="disabledButton" type="button" value="Check if I already published a similar post"><br/>');
		$("#postTitleEditControls").css({'display': 'block'});
		$("#postTitleEditControls").on('change keydown paste input', '#postTitleInput', function() {
			if ( $("#postTitleInput").val().length > 3 ) {
				$("#similarPosts").css("color","white");
				$("#similarPosts").css("cursor","pointer");
				document.getElementById("similarPosts").onclick = function () { checkSimilarPosts(); };
			} else {
				$("#similarPosts").css("color","#727272");
				$("#similarPosts").css("cursor","default");
				document.getElementById("similarPosts").onclick = function () { return false; };
			}
		});
		// post headshot controls
		$("#postHeadshotEditControls").css({'display': 'block'});
		document.getElementById('headshotImgDiv').style.backgroundImage = 'url(/resources/images/def-post-image.png)';
		$("#headshotImgDiv").css({'width': '312px'});
		$("#headshotImgDiv").css({'height': '148px'});
		$("#headshotImgDiv").css({'max-width': ''});
		$("#headshotImgDiv").css({'min-height': ''});
		postHeadshotNewHidden.value = "/resources/images/def-post-image.png";
		// summary
		$('#postSummaryMain').html('<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
			'(5) Summary: <br/>' +
			'<input id="postSummaryInput" type=text spellcheck="true" style="width:700px"><br></p>');
		// content
		$('#postContent').html('<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
			'(6) Content:'+
			'<ul style="list-style-type:circle"><li>Insert images and slideshows.'+
			'<li>If you embed an image from external site, make sure the URL starts with https://.'+
			'<li>Spell Check happens as you type. Use "CTRL+Right Click" to view suggestions.</ul>'+
			'<a style="font-size:13px" target="_blank" href="/do/help/write/posteditor">More help?</a></p>');
		$("#postContent").css({'visibility': 'visible'});
		$("#postContent").css({'padding': '0px'});
		editor = CKEDITOR.appendTo('postContent', config, html);
		// save post every 2 minutes
		autoSaveId = setInterval('autoSavePost()',120000);
		postTitleHidden.value=postTitleInput.value;
		postSummaryHidden.value=postSummaryInput.value;
		postContentHidden.value=editor.getData();
	});

	/*** Edit Post ***/

	$("#editPost").click(function() {
		// check if editor is in disabled state
		if ( editor != '' ) return;
		// read published flag
		var p1 = publishFlagHidden.value;
		// *** draft or published posts ***
		if ( p1 == 'N' || p1 == 'Y' ) {
			// post edit controls and the title
			var a = $("#postTitle").text();
			// edit controls
			if ( p1 == 'N' ) {
				// draft
				$("#postEditControls").css({'margin-top': '30px'});
				$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
					'&nbsp;<input id=publishPost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish">'+
					'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
					'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
				// post title edit controls
				$('#postTitleEditControls').html('<p style="font-family:Helvetica; font-weight:bold">'+
					'Let\'s edit your post to perfection!</p>'+
					'<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
					'(1) Tighten your catchy title.<br>'+
					'<input id="postTitleInput" type=text spellcheck="true" style="width:350px" value=""><br></p>');
				$("#postTitleEditControls").css({'display': 'block'});
			} else if ( p1 == 'Y' ) {
				// published
				$("#postEditControls").css({'margin-top': '30px'});
				$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
					'&nbsp;<input id=publishPost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish" disabled>'+
					'&nbsp;<input id=deletePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete">'+
					'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
				// post title edit controls
				$('#postTitleEditControls').html('<p style="font-family:Helvetica; font-weight:bold">'+
					'Let\'s edit your post to perfection!</p>'+
					'<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
					'(1) Tighten your catchy title.<br>'+
					'<input id="postTitleInput" type=text spellcheck="true" style="width:350px" value=""><br></p>');
				$("#postTitleEditControls").css({'display': 'block'});
			}
			// post comments
			$('#pageNameDiv').html('');
			$('#bloggerNameDiv').html('');
			$('#postTitle').html('');
			$('#postComments').html('');
			$('#postComment').html('');
			postTitleInput.value = a;
			// post headshot edit controls
			$("#postHeadshotEditControls").css({'display': 'block'});
			$("#postHeadshotEditControls").html(
				'<span style="font-family:Comic Sans MS,Courier; font-size:16px; font-style:italic; color:#a0a0a0">' +
					'(2) Headshot Image: An aspect ratio of 2:1 is recommended for better display. Visit <a target="_blank" href="https://commons.wikimedia.org/wiki/Commons:Free_media_resources/Photography">Commons: Free media resources/Photography</a> to obtain free images. If using a tablet, you may upload images directly from the camera.<br/>' +
					'<input class="activeButton" onclick="showImageBox()" type="button" value="Upload/ Select Post Headshot">' +
				'</span>'
			);
			// summary
			var b = postSummaryHidden.value;
			$('#postSummaryMain').html('<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
				'(3) Remove those redundant words from your descriptive summary.'+
				'<input id="postSummaryInput" type=text spellcheck="true" style="width:550px" value=""><br><br></p>');
			postSummaryInput.value = b;
			// hi
			$("#postHIMain").css({'display': 'none'});
			// content
			html = document.getElementById('postContent').innerHTML;
			$('#postContent').html('<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
				'(4) Make your post content much more impressive!</p>');
			editor = CKEDITOR.appendTo('postContent', config, html);
		// *** deleted posts ***
		} else {
			// post edit controls
			$("#postEditControls").css({'margin-top': '30px'});
			$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
				'&nbsp;<input id=publishPost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish">'+
				'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
				'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
			// post title edit controls
			var a = $("#postTitle").text();
			$('#postTitleEditControls').html('<p style="font-family:Helvetica; font-weight:bold">'+
				'Let\'s resurrect your post!</p>'+
				'<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
				'(1) Tighten your catchy title.<br>'+
				'<input id="postTitleInput" type=text spellcheck="true" style="width:350px" value=""><br></p>');
			$("#postTitleEditControls").css({'display': 'block'});
			postTitleInput.value = a;
			// post headshot edit controls
			$("#postHeadshotEditControls").css({'display': 'block'});
			$("#postHeadshotEditControls").html(
				'<span style="font-family:Comic Sans MS,Courier; font-size:16px; font-style:italic; color:#a0a0a0">' +
					'(2) Headshot Image: An aspect ratio of 2:1 is recommended for better display. Visit <a target="_blank" href="https://commons.wikimedia.org/wiki/Commons:Free_media_resources/Photography">Commons: Free media resources/Photography</a> to obtain free images. If using a tablet, you may upload images directly from the camera.<br/>' +
					'<input class="activeButton" onclick="showImageBox()" type="button" value="Upload/ Select Post Headshot">' +
				'</span>'
			);
			// summary
			var b = postSummaryHidden.value;
			$('#postSummaryMain').html('<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
				'(3) Remove those redundant words from your descriptive summary.'+
				'<input id="postSummaryInput" type=text spellcheck="true" style="width:550px" value=""><br><br></p>');
			postSummaryInput.value = b;
			// hi
			$("#postHIMain").css({'display': 'none'});
			// content
			html = document.getElementById('postContent').innerHTML;
			$('#postContent').html('<p style="font-family:Comic Sans MS,Courier; font-style:italic; color:#a0a0a0">'+
				'(4) Make your post content much more impressive before publishing again!</p>');
			editor = CKEDITOR.appendTo('postContent', config, html);
		}
		// save post every 2 minutes
		autoSaveId = setInterval('autoSavePost()',120000);
		postTitleHidden.value=postTitleInput.value;
		postSummaryHidden.value=postSummaryInput.value;
		postContentHidden.value=editor.getData();
	});

	/*** Comments ***/

	// AJAX call to query next page of Comments
	$("#getCommentsNext").click(function() {
		commentsKeyAlternateRefreshHidden.value = commentsKeyRefreshHidden.value;
		commentsKeyRefreshHidden.value = commentsKeyNextHidden.value;
		$.ajax({
			type: "POST",
			url: "/action/get-comments-next",
			data: { postId : postIdHidden.value, commentsKeyNext : commentsKeyNextHidden.value },
			dataType: "json"
		}).always(function (resp) { populateComments(resp,0); });
	});

	// AJAX call to query previous page of Comments
	$("#getCommentsPrev").click(function() {
		$.ajax({
			type: "POST",
			url: "/action/get-comments-prev",
			data: { postId : postIdHidden.value, commentsKeyPrev : commentsKeyPrevHidden.value },
			dataType: "json"
		}).always(function (resp) { populateComments(resp,0); });
	});

	// Follow or Unfollow Comments
	$("#followCommentsA").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		// prevent calling this method twice in case of doubleclick
		var b = followCommentsA.innerHTML;
		if ( b == "Please wait..." ) {
			return false;
		} else {
			followCommentsA.innerHTML = "Please wait...";
		}
		// call either follow comments or unfollow comments and set the link to the vice versa
		var cfCount = 0;
		if ( b == "Follow?" ) {
			$.ajax({
				type: "POST",
				url: "/action/follow-comments",
				data: { postId : postIdHidden.value },
				dataType: "json"
			}).always(function (resp) {
				$.each( resp, function(key,value) {
					switch (key) {
						case 'ERROR':
							alert(value);
							break;
						case 'followerId':
							followerIdHidden.value = value;
							break;
						case 'cfCount':
							cfCount = value;
							break;
					}
				});
				followCommentsSpan.innerHTML = cfCount + ' blogger(s) are following this post, including yourself. ';
				followCommentsA.innerHTML = "Unfollow?";
			});
		} else {
			$.ajax({
				type: "POST",
				url: "/action/unfollow-comments",
				data: { postId : postIdHidden.value, followerId : followerIdHidden.value },
				dataType: "json"
			}).always(function (resp) {
				$.each( resp, function(key,value) {
					switch (key) {
						case 'ERROR':
							alert(value);
							break;
						case 'cfCount':
							cfCount = value;
							break;
					}
				});
				followCommentsSpan.innerHTML = cfCount + ' blogger(s) are following this post, but not you. ';
				followCommentsA.innerHTML = "Follow?";
			});
		}
	});

	/*** Request to be featured at www.facebook.com/heatbud ***/

	$("#requestFB").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		// prevent calling this method twice in case of double-click
		var b = requestFB.innerHTML;
		if ( b != "Request" ) {
			return false;
		}
		$.ajax({
			type: "POST",
			url: "/action/request-fb",
			data: { postId : postIdHidden.value },
			dataType: "json"
		}).always(function (resp) {
			$('#requestFBOuter').html('We have received your request to feature this post at www.facebook.com/heatbud.');
		});
	});

	// show onlyAdmins box
	$(".showOnlyAdminsBox").click(function() {
		$('body, #onlyAdminsBox, #modal-background').toggleClass('active');
		return false;
	});

});

/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Enter a My zone and refresh the page using POST
function enterMyZone(zindex) {
	zid = document.getElementById("mzid"+zindex).value;
	enterZone(zid,'NO');
}

// Enter a Top zone and refresh the page using POST
function enterTopZone(zindex) {
	zid = document.getElementById("tzid"+zindex).value;
	enterZone(zid,'NO');
}

// Enter a zone and refresh the page using POST
function enterZone(zid,f) {

	var form = document.createElement("form");
	form.setAttribute("method", "POST");
	form.setAttribute("action", "/do/start");
	form.setAttribute("target", "_self");

	var zField = document.createElement("input");
	zField.setAttribute("name", "zoneId");
	zField.setAttribute("value", zid);
	form.appendChild(zField);

	var mField = document.createElement("input");
	mField.setAttribute("type", "hidden");
	mField.setAttribute("name", "myZonesKeyRefreshZO");
	mField.setAttribute("value", myZonesKeyRefreshZOHidden.value);
	form.appendChild(mField);

	var mField = document.createElement("input");
	mField.setAttribute("type", "hidden");
	mField.setAttribute("name", "myZonesKeyRefreshZoneId");
	mField.setAttribute("value", myZonesKeyRefreshZoneIdHidden.value);
	form.appendChild(mField);

	var mField = document.createElement("input");
	mField.setAttribute("name", "myZonesKeyAlternateRefreshZO");
	mField.setAttribute("value", myZonesKeyAlternateRefreshZOHidden.value);
	form.appendChild(mField);

	var mField = document.createElement("input");
	mField.setAttribute("name", "myZonesKeyAlternateRefreshZoneId");
	mField.setAttribute("value", myZonesKeyAlternateRefreshZoneIdHidden.value);
	form.appendChild(mField);

	var tField = document.createElement("input");
	tField.setAttribute("name", "topZonesKeyRefreshZO");
	tField.setAttribute("value", topZonesKeyRefreshZOHidden.value);
	form.appendChild(tField);

	var tField = document.createElement("input");
	tField.setAttribute("name", "topZonesKeyRefreshZoneId");
	tField.setAttribute("value", topZonesKeyRefreshZoneIdHidden.value);
	form.appendChild(tField);

	var tField = document.createElement("input");
	tField.setAttribute("name", "topZonesKeyAlternateRefreshZO");
	tField.setAttribute("value", topZonesKeyAlternateRefreshZOHidden.value);
	form.appendChild(tField);

	var tField = document.createElement("input");
	tField.setAttribute("name", "topZonesKeyAlternateRefreshZoneId");
	tField.setAttribute("value", topZonesKeyAlternateRefreshZoneIdHidden.value);
	form.appendChild(tField);

	if ( f == 'YES' ) {
		var tField = document.createElement("input");
		tField.setAttribute("name", "forceZoneHome");
		tField.setAttribute("value", "YES");
		form.appendChild(tField);
	}

	document.body.appendChild(form);
    form.submit();
}

// Create zone (if f=true, continue with creating the zone even if a zone with similar name exists)
// Note: There is another createZone function in heatbud-top-charts.js that simply pops up the signup box
function createZone(f) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	// read & validate zone name
	var zn = document.getElementById("createZoneNameInput").value.trim();
	if (zn.length == 0) {
		alert("Zone name cannot be empty.");
		return false;
	}
	if (zn.length > 50) {
		alert("Zone name cannot contain more than 50 characters.");
		return false;
	}
	// read & validate zone description
	var zd = document.getElementById("createZoneDescInput").value.trim();
	if (zd.length == 0) {
		alert("Zone description cannot be empty.");
		return false;
	}
	// read zone who
	var zw = document.getElementById("createZoneWhoInput").value.trim();
	// save zone data in the database
	$.post( "/zone/create", { zoneName : zn, zoneDesc : zd, zoneWho : zw, force : f } ).always(function (resp) {
		// process success or error
		if ( resp.indexOf('EXISTS:') == 0 ) {
			createZoneMessage.innerHTML = resp.substring(8);
			createZoneButton.onclick = function () { createZone('true'); };
		} else if ( resp.indexOf('ERROR:') == 0 ) {
			alert( resp.substring(7) );
		} else {
			window.location.href='/zone/'+resp;
		}
	});
}

// Delete a zone from favorites
function deleteMyZone(zindex) {
	zid = document.getElementById("mzid"+zindex).value;
	zname = document.getElementById("mzname"+zindex).value;
	// if the record being deleted is the only one in the page, the page will refresh as blank, so we need to shift one page backwards
	if ( myZonesCountHidden.value == 1 ) {
		myZonesKeyRefreshZOHidden.value = myZonesKeyAlternateRefreshZOHidden.value;
		myZonesKeyRefreshZoneIdHidden.value = myZonesKeyAlternateRefreshZoneIdHidden.value;
		myZonesKeyAlternateRefreshZOHidden.value = 'NULL';
		myZonesKeyAlternateRefreshZoneIdHidden.value = 'NULL';
	}
	if (confirm("Are you sure you want to delete the zone \""+zname+"\" from My Zones?")) {
		$.ajax({
			type: "POST",
			url: "/zone/delete-myzone",
			data: { zoneId : zid, myZonesKeyRefreshZO : myZonesKeyRefreshZOHidden.value, myZonesKeyRefreshZoneId : myZonesKeyRefreshZoneIdHidden.value },
			dataType: "json"
		}).always(function (resp) { populateMyZones(resp); });
	}
}

// Approve admin
function approveAdmin(adminId, adminName) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	if (confirm("Once approved, \""+adminName+"\" will have the same privileges as you in this zone. Proceed?")) {
		$.ajax({
			type: "POST",
			url: "/zone/approve-admin",
			data: { zoneId : zoneIdHidden.value, zoneName : zoneNameHidden.value, adminId : adminId },
			dataType: "json"
		}).always(function (resp) { $('#adminRequestsMessage').html(adminName+' has been added as an admin for this zone.'); });
	}
}

// Remove admin
function removeAdmin(adminId, adminName) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	if (confirm("Are you sure you want to remove \""+adminName+"\" from being admin of this zone?")) {
		$.ajax({
			type: "POST",
			url: "/zone/remove-admin",
			data: { zoneId : zoneIdHidden.value, zoneName : zoneNameHidden.value, adminId : adminId },
			dataType: "json"
		}).always(function (resp) { $('#adminMessage').html(adminName+' has been removed as admin for this zone. Please reload the page.'); });
	}
}

// Favorite a zone
function favoriteZone(zindex) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	zid = document.getElementById("tzid"+zindex).value;
	zname = document.getElementById("tzname"+zindex).value;
	favoriteZoneById(zid,zname);
}

// Favorite a zone by Id
function favoriteZoneById(zid,zname) {
	$.ajax({
		type: "POST",
		url: "/zone/favorite",
		data: { zoneId : zid, zoneName : zname },
		dataType: "json"
	}).always(function (resp) { populateMyZones(resp); });
}

// Populate My Zones
function populateMyZones(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'myZonesList':
				myZonesCountHidden.value = value.length;
				var r = new Array(), j = -1;
				r[++j] = '<ul style="list-style-type:none; padding-left:0px">';
				for (var i = 0; i < value.length; i++) {
					myZone = JSON.parse(value[i]);
					r[++j] = '<li class="zoneList myZones" title="';
				    r[++j] = myZone.zoneName.replace(/"/g,'&#034;');
				    r[++j] = '"><div class="zoneName" style="width:126px" onclick="enterMyZone(';
				    r[++j] = i;
				    r[++j] = ')">';
				    r[++j] = myZone.zoneName;
				    r[++j] = '</div><div style="float:left; width:10px; color:#909090; font-size:11px; font-weight:bold" title="Unread Post Count">';
				    if ( myZone.unreadCount < 100 ) {
				    	r[++j] = '<span style="background-color:#efefff; padding:0px 4px 0px 4px">'+myZone.unreadCount+'</span>';
				    } else {
				    	r[++j] = '<span style="background-color:#efefff; padding:0px 4px 0px 4px">99+</span>';
				    }
				    r[++j] = '</div><div style="float:left; width:12px" onclick="deleteMyZone(';
				    r[++j] = i;
				    r[++j] = ')" title="Remove from favorites">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>';
				    r[++j] = '<input type=hidden id="mzid' + i + '" value="' + myZone.zoneId + '">';
				    r[++j] = '<input type=hidden id="mzname' + i + '" value="' + myZone.zoneName.replace(/"/g,'&#034;') + '"></li>';
				}
			    r[++j] = '</ul><div style="clear:both"></div>';
				$('#myZonesList').html(r.join(''));
				document.getElementById('myZonesDiv').scrollIntoView();
				window.scrollBy(-10,-10);
				break;

			case 'myZonesKeyPrevZO':
				myZonesKeyPrevZOHidden.value = value;
				if (value == 'NULL')
					$('#getMyZonesPrevDiv').css("visibility","hidden");
				else
					$('#getMyZonesPrevDiv').css("visibility","visible");
				break;

			case 'myZonesKeyPrevZoneId':
				myZonesKeyPrevZoneIdHidden.value = value;
				break;

			case 'myZonesKeyNextZO':
				myZonesKeyNextZOHidden.value = value;
				if (value == 'NULL')
					$('#getMyZonesNextDiv').css("visibility","hidden");
				else
					$('#getMyZonesNextDiv').css("visibility","visible");
				break;

			case 'myZonesKeyNextZoneId':
				myZonesKeyNextZoneIdHidden.value = value;
				break;

			case 'myZonesKeyRefreshZO':
				myZonesKeyRefreshZOHidden.value = value;
				break;

			case 'myZonesKeyRefreshZoneId':
				myZonesKeyRefreshZoneIdHidden.value = value;
				break;

		}
	});
}

// Populate Top Zones
function populateTopZones(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'topZonesList':
				topZonesCountHidden.value = value.length;
				var r = new Array(), j = -1;
				r[++j] = '<ul style="list-style-type:none; padding-left:0px; margin-bottom:0px">';
				for (var i = 0; i < value.length; i++) {
					topZone = JSON.parse(value[i]);
					r[++j] = '<li class="zoneList topZones" title="';
				    r[++j] = topZone.zoneName.replace(/"/g,'&#034;');
				    r[++j] = '"><div class="zoneName" onclick="enterTopZone(';
				    r[++j] = i;
				    r[++j] = ')">';
				    r[++j] = topZone.zoneName;
				    r[++j] = '</div><div id="z' + i + '" class="favorite" style="float:right; width:15px; margin-top:2px;" onclick="favoriteZone(';
				    r[++j] = i;
				    r[++j] = '); this.style.backgroundColor=\'#FF9696\'; setTimeout(function() { z' + i + '.style.backgroundColor=\'#ffffff\'; }, 1000);" title="Add this to My Zones">&nbsp;&nbsp;&nbsp;</div>';
				    r[++j] = '<input type=hidden id="tzid' + i + '" value="' + topZone.zoneId + '">';
				    r[++j] = '<input type=hidden id="tzname' + i + '" value="' + topZone.zoneName.replace(/"/g,'&#034;') + '"></li>';
				}
			    r[++j] = '</ul><div style="clear:both"></div>';
				$('#topZonesList').html(r.join(''));
				document.getElementById('myZonesNavigation').scrollIntoView();
				window.scrollBy(-20,-20);
				break;

			case 'topZonesKeyPrevZO':
				topZonesKeyPrevZOHidden.value = value;
				if (value == 'NULL')
					$('#getTopZonesPrevDiv').css("visibility","hidden");
				else
					$('#getTopZonesPrevDiv').css("visibility","visible");
				break;

			case 'topZonesKeyPrevZoneId':
				topZonesKeyPrevZoneIdHidden.value = value;
				break;

			case 'topZonesKeyNextZO':
				topZonesKeyNextZOHidden.value = value;
				if (value == 'NULL')
					$('#getTopZonesNextDiv').css("visibility","hidden");
				else
					$('#getTopZonesNextDiv').css("visibility","visible");
				break;

			case 'topZonesKeyNextZoneId':
				topZonesKeyNextZoneIdHidden.value = value;
				break;

			case 'topZonesKeyRefreshZO':
				topZonesKeyRefreshZOHidden.value = value;
				break;

			case 'topZonesKeyRefreshZoneId':
				topZonesKeyRefreshZoneIdHidden.value = value;
				break;

		}
	});
}

// JS function to edit Zone Name
function editZoneName() {
	var a = document.getElementById('zoneNameHidden').value;
	zoneNameInput.style.display = "block";
	zoneNameInput.value = a;
	editZoneNameDiv.innerHTML = '<a id="saveZoneName" target="_self" onclick="saveZoneName()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelZoneName" target="_self" onclick="cancelZoneName()" href="javascript:">Cancel</a>';
	zoneNameRetMessage.innerHTML = "";
}

// JS function to cancel Zone Name
function cancelZoneName() {
	zoneNameInput.style.display = "none";
	editZoneNameDiv.innerHTML = '<a id="editZoneNameA" style="color:white" target="_self" onclick="editZoneName()" href="javascript:">Edit Zone Name</a>';
	zoneNameRetMessage.innerHTML = "";
}

// AJAX call to save Zone Name
function saveZoneName() {
	// prevent calling this method twice in case of doubleclick
	var b = zoneNameRetMessage.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		zoneNameRetMessage.innerHTML = "Please wait...";
	}
	// call controller method
	var a = zoneNameInput.value.trim();
	$.post(	"/zone/update-zone-name", { zoneId : zoneIdHidden.value, zoneName : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			zoneName.innerHTML = a;
			document.getElementById('zoneNameHidden').value = a;
			zoneNameInput.style.display = "none";
			editZoneNameDiv.innerHTML = '<a id="editZoneNameA" style="color:white" target="_self" onclick="editZoneName()" href="javascript:">Edit Zone Name</a>';
			zoneNameRetMessage.innerHTML = "Saved.";
		} else {
			zoneNameRetMessage.innerHTML = resp;
		}
	});
}

// AJAX call to save Zone Headshot
function saveZoneHeadshot(a) {
	$.post(	"/zone/update-zone-headshot",
		{ zoneId : zoneIdHidden.value, zoneHeadshot : encodeURIComponent(a) }
	).always(function (resp) {
		document.getElementById('headshotImgDiv').style.backgroundImage = 'url('+a+')';
	});
}

// Show image box
function showImageBox() {
	$('body, #imageBox, #modal-background').toggleClass('active');
	populateFolders();
	showContents('common');
}

// Save Post (publish = Y to Publish, N to Save)
function savePost(publish) {
	// prompt login/signup
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	// run basic checks
	if ( editor == '' ) return;
	if (postTitleInput.value.trim().length == 0) {
		alert('Post title cannot be empty.');
		return false;
	}
	if (postTitleInput.value.trim().length > 100) {
		alert('Post title cannot contain more than 100 characters.');
		return false;
	}
	if (postSummaryInput.value.trim().length == 0) {
		alert('Post summary cannot be empty.');
		return false;
	}
	if (postSummaryInput.value.trim().length > 300) {
		alert('Post summary cannot contain more than 300 characters.');
		return false;
	}
	if (editor.getData().trim().length == 0) {
		alert('Post content cannot be empty.');
		return false;
	}
	if ( publish == 'Y' ) {
		if (!confirm("Are you ready to publish?")) {
			return false;
		}
	}
	// exit if another save is in progress
	if ( saveInProgress ) {
		if ( publish == 'Y' ) {
			alert('Unable to publish. Please try again.');
		}
		return false;
	} else {
		saveInProgress = true;
	}
	// post saved timestamp
	$("#postSavedTimeStamp").css({'display': 'block'});
	$("#postSavedTimeStamp").html('<span style="color:#909090">Saving in progress... Please don\'t close the browser.</span>');
	// disable buttons while we save the post
	$('#postEditControls').html('<input id=savePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Saving..." disabled>'+
		'&nbsp;<input id=publishPost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish" disabled>'+
		'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
		'&nbsp;<input id=closeEditor class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close" disabled>');
	// check if headshot has changed
	var headshotChanged = 'N';
	if ( postHeadshotOrigHidden.value != postHeadshotNewHidden.value ) {
		headshotChanged = 'Y';
	}
	// put post data into formdata object
	var formdata = new FormData();
	formdata.append("postId", postIdHidden.value);
	formdata.append("postTitle", postTitleInput.value.trim());
	formdata.append("postHeadshot", postHeadshotNewHidden.value);
	formdata.append("headshotChanged", headshotChanged);
	formdata.append("postSummary", postSummaryInput.value.trim());
	formdata.append("zoneId", zoneIdHidden.value);
	formdata.append("pageId", document.getElementById("pageIdSelectedHidden").value);
	formdata.append("publishFlag", publish);
	formdata.append("priorPublishFlag", publishFlagHidden.value);
	formdata.append("postContent", editor.getData());
	// save post data into the database
	$.ajax({
		type: "POST",
		async: false,
		url: "/action/save",
		data: formdata,
		processData: false,
		contentType: false,
		dataType: "json"
	}).always(function (resp) {
		$.each( resp, function(key,value) {
			switch (key) {
				case 'ERROR':
					$('body, #promptErrorBox, #modal-background').toggleClass('active');
					$('#errorValue').html(value);
					$("#postSavedTimeStamp").html('<span style="color:#909090">This post could not be published.</span>');
			   		// enable buttons
					$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
						'&nbsp;<input id=publishPost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish">'+
						'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
						'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
					return false;
					break;

				case 'SUCCESS':
					// reset hidden values
					postTitleHidden.value = postTitleInput.value;
					postSummaryHidden.value = postSummaryInput.value;
					postContentHidden.value = editor.getData();
					postHeadshotOrigHidden.value = postHeadshotNewHidden.value;
					// update last saved timestamp
					$("#postSavedTimeStamp").html('<span style="color:#909090">Last saved : ' + new Date().toLocaleString() + '</span>');
					if ( publish == 'Y' ) {
						closeEditor();
					} else {
				   		// enable buttons
						if ( publishFlagHidden.value == 'Y' ) {
							$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
								'&nbsp;<input id=publishPost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish" disabled>'+
								'&nbsp;<input id=deletePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete">'+
								'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
						} else {
							$('#postEditControls').html('<input id=savePost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save">'+
								'&nbsp;<input id=publishPost class="activeButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish">'+
								'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
								'&nbsp;<input id=closeEditor class="activeButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close">');
						}
					}
					break;

				case 'postId':
					postIdHidden.value = value;
					break;

				case 'postHeadshot':
					// if the post headshot is generated by the program
					document.getElementById('headshotImgDiv').style.backgroundImage = 'url('+value+')';
					postHeadshotNewHidden.value = value;
					postHeadshotOrigHidden.value = value;
					break;

			}
		});
	});
	saveInProgress = false;
}

// Auto save post only when there are changes
function autoSavePost() {
	if ( postTitleInput.value.length != 0 && postSummaryInput.value.length != 0 && editor.getData().length != 0 ) {
		if ( postTitleHidden.value.trim() != postTitleInput.value.trim() ||
			 postSummaryHidden.value.trim() != postSummaryInput.value.trim() ||
			 postContentHidden.value.trim() != editor.getData().trim()
			) {
				savePost('N');
		}
	}
}

// Delete Post
function deletePost() {
	if (confirm("When you delete a post, Heat Index and Votes will reset to zero. Other users will not see your post content, but it is still available to you for editing and publishing again.\n\nDo you want to proceed?")) {
		// disable buttons while we process delete
		$('#postEditControls').html('<input id=savePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'N\')" type="button" value="Save" disabled>'+
			'&nbsp;<input id=publishPost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="savePost(\'Y\')" type="button" value="Publish" disabled>'+
			'&nbsp;<input id=deletePost class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="deletePost()" type="button" value="Delete" disabled>'+
			'&nbsp;<input id=closeEditor class="disabledButton" style="padding-left:20px; padding-right:20px" onclick="closeEditor()" type="button" value="Close" disabled>');
		// delete selected post from the database
		$.ajax({
			type: "POST",
			url: "/action/delete",
			data: { postId: postIdHidden.value, zoneId : zoneIdHidden.value, pageId : pageIdHidden.value },
			dataType: "json"
		}).always(function (resp) {
			// process the return value
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						alert(value);
						break;
				}
			});
			// close the editor
			closeEditor();
		});
	}
}

// Purge Post
function purgePost() {
	$.ajax({
		type: "POST",
		url: "/action/purge",
		data: { postId: postIdHidden.value },
		dataType: "json"
	}).always(function (resp) {
		// process the return value
		$.each( resp, function(key,value) {
			switch (key) {
				case 'MESSAGE':
					$('#purgePost').html(value);
					break;
			}
		});
	});
}

// Close CK editor
function closeEditor() {
	clearInterval(autoSaveId);
	if ( postTitleInput.value.length != 0 && postSummaryInput.value.length != 0 && editor.getData().length != 0 ) {
		if ( postTitleHidden.value.trim() != postTitleInput.value.trim() ||
			 postSummaryHidden.value.trim() != postSummaryInput.value.trim() ||
			 postContentHidden.value.trim() != editor.getData().trim()
			) {
				$('body, #promptSaveBox, #modal-background').toggleClass('active');
				return false;
		}
	}
	refreshMRL();
}

// Refresh my-reading-list page
function refreshMRL() {
	if ( editor != '' ) editor.destroy();
	editor = '';
	var b = postIdHidden.value;
	var z = zoneIdHidden.value;
	if ( b != 'PROCESSING' && b != 'NEW' ) {
		window.location.href='/post/'+b;
	} else {
		enterZone(z,'NO');
	}
}

// Vote Down Post
function voteDownPost() {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	// Disable UP and DOWN vote images while we process
	voteDownImg.onclick = function () { return false; };
	voteUpImg.onclick = function () { return false; };
	if ( parseInt(currentVoteHidden.value) == -1 ) {
		alert('You cannot DOWN VOTE this post, because your current vote is already DOWN.');
	}
	// Register DOWN vote in the database
	$.ajax({
		type: "POST",
		url: "/action/vote",
		data: { postId : postIdHidden.value, zoneId : zoneIdHidden.value, zoneName : zoneNameHidden.value, pageId : pageIdHidden.value, newVote : -1 },
		dataType: "json"
	}).always(function (resp) {
		$.each( resp, function(key,value) {
			switch (key) {
				case 'ERROR':
					alert(value);
					break;
			}
		});
	});
	// Increment "downVotes" statistic for post
	downVotesHidden.value = parseInt(downVotesHidden.value) + 1;
	downVotes.innerHTML = downVotesHidden.value;
	// Decrement post HI
	postHIHidden.value = parseInt(postHIHidden.value) - 2;
	postHI.innerHTML = postHIHidden.value;
	// If previous vote was UP
	if ( parseInt(currentVoteHidden.value) == 1 ) {
		// Decrement "upVotes" statistic for post
		upVotesHidden.value = parseInt(upVotesHidden.value) - 1;
		upVotes.innerHTML = upVotesHidden.value;
		// Decrement post HI (once again)
		postHIHidden.value = parseInt(postHIHidden.value) - 2;
		postHI.innerHTML = postHIHidden.value;
	}
	// Set current vote to DOWN
	currentVoteHidden.value = '-1';
	voteDownImg.style.cursor = "default";
	voteDownImg.title = 'Your current vote: DOWN';
	// Enable UP vote image
	voteUpImg.style.cursor = "pointer";
	voteUpImg.onclick = function () { voteUpPost(); };
	voteUpImg.title = 'Vote Up';
}

// Vote Up Post
function voteUpPost() {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	// Disable UP and DOWN vote images while we process
	voteUpImg.onclick = function () { return false; };
	voteDownImg.onclick = function () { return false; };
	if ( parseInt(currentVoteHidden.value) == 1 ) {
		alert('You cannot UP VOTE this post, because your current vote is already UP.');
	}
	// Register UP vote in the database
	$.ajax({
		type: "POST",
		url: "/action/vote",
		data: { postId : postIdHidden.value, zoneId : zoneIdHidden.value, zoneName : zoneNameHidden.value, pageId : pageIdHidden.value, newVote : 1 },
		dataType: "json"
	}).always(function (resp) {
		$.each( resp, function(key,value) {
			switch (key) {
				case 'ERROR':
					alert(value);
					break;
			}
		});
	});
	// Increment "upVotes" statistic for post
	upVotesHidden.value = parseInt(upVotesHidden.value) + 1;
	upVotes.innerHTML = upVotesHidden.value;
	// Increment post HI
	postHIHidden.value = parseInt(postHIHidden.value) + 2;
	postHI.innerHTML = postHIHidden.value;
	// If previous vote was DOWN
	if ( parseInt(currentVoteHidden.value) == -1 ) {
		// Decrement "downVotes" statistic for post
		downVotesHidden.value = parseInt(downVotesHidden.value) - 1;
		downVotes.innerHTML = downVotesHidden.value;
		// Increment post HI (once again)
		postHIHidden.value = parseInt(postHIHidden.value) + 2;
		postHI.innerHTML = postHIHidden.value;
	}
	// Set current vote to UP
	currentVoteHidden.value = '1';
	voteUpImg.style.cursor = "default";
	voteUpImg.title = 'Your current vote: UP';
	// Enable DOWN vote image
	voteDownImg.style.cursor = "pointer";
	voteDownImg.onclick = function () { voteDownPost(); };
	voteDownImg.title = 'Vote Down';
}

// Show email post box
function showEmailPostBox() {
	$('body, #emailPostBox, #modal-background').toggleClass('active');
	emailPostRetMessage.innerHTML = '';
}

// Hide email post box
function closeEmailPostBox() {
	$('body, #emailPostBox, #modal-background').toggleClass('active');
}

// Send email
function sendEmailPost() {
	// disable email button
	emailPostButton.onclick = function () { return false; };
	emailPostButton.style.color = 'rgb(144, 144, 144)';
	// read input
	var b = postIdHidden.value;
	var p = personalMessage.value.trim();
	var r = recipients.value.trim();
	// validate recipients
	if (r.length == 0) {
		emailPostRetMessage.innerHTML = 'Please enter one or more email addresses.';
		// enable email button
		emailPostButton.onclick = function () { sendEmailPost(); };
		emailPostButton.style.color = 'white';
		return false;
	}
	// read "from email" and "from name" for guest users
	var fe=" ", fn=" ";
	if ( userIdHidden.value == 'NULL' ) {
		fe = fromEmail.value.trim();
		fn = fromName.value.trim();
		if (fe.length == 0) {
			emailPostRetMessage.innerHTML = 'Please enter your email address.';
			// enable email button
			emailPostButton.onclick = function () { sendEmailPost(); };
			emailPostButton.style.color = 'white';
			return false;
		}
		if (fn.length == 0) {
			emailPostRetMessage.innerHTML = 'Please enter your name.';
			// enable email button
			emailPostButton.onclick = function () { sendEmailPost(); };
			emailPostButton.style.color = 'white';
			return false;
		}
	}
	// call controller method
	emailPostRetMessage.innerHTML = 'Processing...';
	$.post(	"/action/email",
   		{ postId : b, fromEmail : fe, fromName : fn, recipients : encodeURIComponent(r), personalMessage : encodeURIComponent(p) }
	).always(function (resp) { emailPostRetMessage.innerHTML = resp; });
	// enable email button
	emailPostButton.onclick = function () { sendEmailPost(); };
	emailPostButton.style.color = 'white';
}

// Populate comments
function populateComments(JSONdata, pcd) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				$("#commentsError"+pcd).text(value);
				break;

			case 'SUCCESS':
				$("#commentsError"+pcd).text(value);
				break;

			case 'commentsList':
				commentsCountHidden.value = value.length;
				var r = new Array(), j = -1;
				for (var i = 0; i < value.length; i++) {
					// split commentDate into thanked, parentCommentDate and commentDate
					thanked = parseInt(value[i].commentDate.substring(0,1));
					pcd = parseInt(value[i].commentDate.substring(1,14));
					cd = parseInt(value[i].commentDate.substring(14,27));
					// set header and indent css styles for parent comments and sub-comments
					if ( cd == 9999999999999 ) {
						cd = pcd;
						commentHeaderCSS = 'margin-top:16px; background-color:#F5F5F5; border-bottom:1px solid #BBBBBB; padding:4px;';
						commentIndentCSS = '';
					} else {
						cd = 9999999999999 - cd;
						commentHeaderCSS = '';
						commentIndentCSS = 'margin-left:15px;';
					}
					// commenter name, date, delete icon, thank comments
					r[++j] = '<div style="font-size:12px; ' + commentIndentCSS + commentHeaderCSS + '">';
					r[++j] = '<span id="thankComment' + value[i].commentDate + 'Span">';
					if ( thanked == '2' && cd == pcd ) {
						r[++j] = '<img alt="thanked by the blogger" style="max-height:15px; margin-right:10px; border:none" src="/resources/images/thanked.jpg"/>&nbsp;';
					}
					r[++j] = '</span>';
					r[++j] = '<span style="color:#909090">' + new Date(cd).toLocaleString() + '</span>';
					r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>';
					r[++j] = '<a target="_self" href="/' + value[i].commenterId + '">' + value[i].commenterName + '</a>';
					r[++j] = '</div>';
					// comment text
					r[++j] = '<input id="originalComment' + value[i].commentDate + '" type=hidden>';
					r[++j] = '<div id="comment' + value[i].commentDate + 'Div" style="' + commentIndentCSS + 'margin-top:4px; white-space:pre-line">' + value[i].commentText + '</div>';
					r[++j] = '<textarea id="comment' + value[i].commentDate + 'Input" rows="4" cols="95" style="display:none"></textarea>';
					// reply, thank, edit, delete or report
					r[++j] = '<div style="' + commentIndentCSS + 'font-size:12px; margin-top:8px; margin-bottom:6px">';
					if ( cd == pcd ) {
						r[++j] = '<a target="_self" href="javascript:" onclick="showCommentBox(\'' + value[i].commentDate + '\',\'' + pcd + '\',\'' + thanked + '\')" title="Reply to this thread">Reply to this thread</a>';
						r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>';
						if ( userIdHidden.value == bloggerIdHidden.value ) {
							if ( thanked == '1' ) {
								r[++j] = '<a id="thankComment' + value[i].commentDate + 'A" target="_self" href="javascript:" onclick="thankComment(\'' + value[i].commentDate + '\',\'2\',\'' + value[i].commenterId + '\')">Thank</a>';
							} else {
								r[++j] = '<a id="thankComment' + value[i].commentDate + 'A" target="_self" href="javascript:" onclick="thankComment(\'' + value[i].commentDate + '\',\'1\',\'' + value[i].commenterId + '\')">Unthank</a>';
							}
							r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>';
						}
					}
					if ( userIdHidden.value == value[i].commenterId ) {
						r[++j] = '<span id="editComment' + value[i].commentDate + 'Div">';
						r[++j] = '<a id="editComment' + value[i].commentDate + 'A" target="_self" href="javascript:" onclick="editComment(\'' + value[i].commentDate + '\')" title="Edit this comment">Edit</a></span>';
						r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>';
					}
					if ( userIdHidden.value == value[i].commenterId || userIdHidden.value == bloggerIdHidden.value ) {
						r[++j] = '<a target="_self" href="javascript:" onclick="deleteComment(\'' + value[i].commentDate + '\')" title="Delete this comment">Delete</a>';
						r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp; . &nbsp;</span>';
					}
					r[++j] = '<a id="reportComment' + value[i].commentDate + 'A" target="_self" href="javascript:" onclick="reportComment(\'' + value[i].commentDate + '\')" title="Report this comment as spam">Report</a>';
					if ( cd == pcd ) {
						r[++j] = '<div id="commentBox' + pcd + '" style="margin-top:5px">&nbsp;</div>';
					}
					r[++j] = '</div>';
				}
				$('#commentsDiv').html(r.join(''));
				document.getElementById('postComments').scrollIntoView();
				break;

			case 'commentsKeyPrev':
				commentsKeyPrevHidden.value = value;
				if (value == 'NULL') {
					$('#getCommentsPrevDiv').css("visibility","hidden");
				} else {
					$('#getCommentsPrevDiv').css("visibility","visible");
				}
				break;

			case 'commentsKeyNext':
				commentsKeyNextHidden.value = value;
				if (value == 'NULL') {
					$('#getCommentsNextDiv').css("visibility","hidden");
				} else {
					$('#getCommentsNextDiv').css("visibility","visible");
				}
				break;

			case 'commentsKeyRefresh':
				commentsKeyRefreshHidden.value = value;
				break;

		}
	});
}

// edit comment
function editComment(cd) {
	var a = document.getElementById("comment"+cd+"Div").innerHTML;
	document.getElementById("originalComment"+cd).value = a;
	document.getElementById("comment"+cd+"Div").innerHTML = "";
	document.getElementById("comment"+cd+"Input").style.display = "block";
	document.getElementById("comment"+cd+"Input").value = a;
	document.getElementById("editComment"+cd+"Div").innerHTML = '&nbsp;&nbsp;<a target="_self" id="saveComment' + cd + '" onclick="saveComment(\'' + cd + '\')" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;.&nbsp;&nbsp;<a target="_self" id="cancelComment' + cd + '" onclick="cancelComment(\'' + cd + '\')" href="javascript:">Cancel</a>';
}

// cancel edit comment
function cancelComment(cd) {
	document.getElementById("comment"+cd+"Div").innerHTML = document.getElementById("originalComment"+cd).value;
	document.getElementById("comment"+cd+"Input").value = "";
	document.getElementById("comment"+cd+"Input").style.display = "none";
	document.getElementById("editComment"+cd+"Div").innerHTML = '&nbsp;&nbsp;<a target="_self" id="editComment' + cd + 'A" onclick="editComment(\'' + cd + '\')" href="javascript:">Edit</a>';
	commentsRetMessage.innerHTML = "";
}

// save edited comment
function saveComment(cd) {
	// prevent calling this method twice in case of doubleclick
	var b = document.getElementById("editComment"+cd+"Div").innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		document.getElementById("editComment"+cd+"Div").innerHTML = "Please wait...";
	}
	// call controller method
	var a = document.getElementById("comment"+cd+"Input").value.trim();
	$.post(	"/action/update-comment", { postId : postIdHidden.value, commentDate : cd, commentText : a } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			document.getElementById("comment"+cd+"Div").innerHTML = a;
			document.getElementById("comment"+cd+"Input").value = "";
			document.getElementById("comment"+cd+"Input").style.display = "none";
			document.getElementById("editComment"+cd+"Div").innerHTML = '<a target="_self" id="editComment' + cd + 'A" onclick="editComment(\'' + cd + '\')" href="javascript:">Edit</a>';
			commentsRetMessage.innerHTML = "";
		} else {
			document.getElementById("editComment"+cd+"Div").innerHTML = b;
			commentsRetMessage.innerHTML = resp;
		}
	});
}

// report comment
function reportComment(cd) {
	// show prompt signup box if the user has not logged-in
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	// prevent calling this method twice in case of doubleclick
	var b = document.getElementById("reportComment"+cd+"A").innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		document.getElementById("reportComment"+cd+"A").innerHTML = "Please wait...";
	}
	// call controller method
	var a = document.getElementById("comment"+cd+"Div").innerHTML;
	$.post(	"/action/report-comment", { postId : postIdHidden.value, commentDate : cd, commentText : encodeURIComponent(a) } ).always(function (resp) {
		document.getElementById("reportComment"+cd+"A").innerHTML = b;
		commentsRetMessage.innerHTML = resp;
	});
}

// thank comment
function thankComment(cd,tf,ci) {
	// prevent calling this method twice in case of doubleclick
	var b = document.getElementById("thankComment"+cd+"A").innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		document.getElementById("thankComment"+cd+"A").innerHTML = "Please wait...";
	}
	// call controller method
	$.post(	"/action/thank-comment",
			{ postId : postIdHidden.value, postTitle : postTitle.innerHTML,
			bloggerId : bloggerIdHidden.value, bloggerName : bloggerName.innerHTML,
			commentDate : cd, thankFlag : tf, commenterId : ci }
	).always(function (resp) {
		if ( b == "Thank" ) {
			document.getElementById("thankComment"+cd+"A").innerHTML = "Unthank";
			document.getElementById("thankComment"+cd+"A").onclick = function () { thankComment(cd,'1',ci); };
			document.getElementById("thankComment"+cd+"Span").innerHTML = '<img alt="thanked by the blogger" style="max-height:12px; margin-right:10px; border:none" src="/resources/images/thanked.jpg"/>&nbsp;';
		} else {
			document.getElementById("thankComment"+cd+"A").innerHTML = "Thank";
			document.getElementById("thankComment"+cd+"A").onclick = function () { thankComment(cd,'2',ci); };
			document.getElementById("thankComment"+cd+"Span").innerHTML = '';
		}
	});
}

// Show Comment Box
function showCommentBox(commentDate, pcd, thanked) {
	var a = '<div style="float:left">' +
		'<textarea id=textComment' + pcd + ' rows="4" cols="95" style="width:740px" placeholder="Comments must be relevant to the post content. No Ads or promotions. Heatbud takes spamming seriously."></textarea><br>' +
		'</div><div style="float:right; padding-right:6px; margin-top:3px">';
	if ( userIdHidden.value == 'NULL' ) {
		a = a +
			'<input id=email' + pcd + ' name="email" type="text" style="border: 2px solid #BDC7D8; color:rgb(145, 145, 145); letter-spacing:1px; padding:4px; width:310px; border-radius:2px" placeholder="Email (won\'t be published)">' +
			'<input id=passwd' + pcd + ' type="password" style="border: 2px solid #BDC7D8; color:rgb(145, 145, 145); letter-spacing:1px; padding:4px; width:310px; border-radius:2px" placeholder="Password (if registered user)">';
	}
	a = a +
		'<input id=postComment' + pcd + ' class="activeButton" type="button" onclick="postComment(\'' + commentDate + '\',\'' + pcd + '\',\'' + thanked + '\')" value="Post Comment"> &nbsp;' +
		'<input id=hideComment' + pcd + ' class="activeButton" type="button" onclick="hideCommentBox(\'' + pcd + '\')" value="Cancel">' +
		'</div><div style="clear:both"></div><div id="commentsError' + pcd + '" class="error">&nbsp;</div>';
	$('#commentBox'+pcd).html(a);
}

// Hide Comment Box
function hideCommentBox(pcd) {
	$('#commentBox'+pcd).html('&nbsp;');
}

// Post Comment
function postComment(commentDate, pcd, thanked) {
	// validate comment text
	if (document.getElementById("textComment"+pcd).value.trim().length == 0) {
		$("#commentsError"+pcd).text("Please enter your comment.");
		return false;
	}
	// validate email and passwd
	var email = 'NULL';
	var passwd = 'NULL';
	if ( userIdHidden.value == 'NULL' ) {
		email = document.getElementById("email"+pcd).value.trim();
		passwd = document.getElementById("passwd"+pcd).value.trim();
		if ( email.length == 0 ) {
			$("#commentsError"+pcd).text("Please enter your Email Address.");
			return false;
		}
	}
	// prevent calling this method twice in case of doubleclick
	var b = document.getElementById("postComment"+pcd).value;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		document.getElementById("postComment"+pcd).value = "Please wait...";
		document.getElementById("postComment"+pcd).onclick = function () { return false; };
	}
	// Increment comment count in post Heading
	commentsHidden.value = parseInt(commentsHidden.value) + 1;
	comments.innerHTML = commentsHidden.value;
	// read original comment text, if reply
	var origCommentText = "";
	if ( pcd != 0 ) {
		origCommentText = document.getElementById("comment"+commentDate+"Div").innerHTML.trim();
	}
	// Save Comment in the database and refresh Comments table with the first page of comments
	$.ajax({
		type: "POST",
		url: "/action/post-comment",
		data: { postId : postIdHidden.value,
				postTitle : postTitle.innerHTML,
				publishFlag : publishFlagHidden.value,
				zoneId: zoneIdHidden.value,
				zoneName: zoneNameHidden.value,
				pageId: pageIdHidden.value,
				parentCommentDate : pcd,
				origCommentText : origCommentText,
				commentText : document.getElementById("textComment"+pcd).value.trim(),
				thankedFlag : thanked,
				email : email,
				passwd : passwd
			  },
		dataType: "json"
	}).always(function (resp) {
		if ( resp.hasOwnProperty('SUCCESS') ) {
			// Increment HI if not a former commenter
			if ( checkCommenterIdExistsHidden.value == 'N' ) {
				postHIHidden.value = parseInt(postHIHidden.value) + 3;
				postHI.innerHTML = postHIHidden.value;
				checkCommenterIdExistsHidden.value = 'Y';
			}
			if ( pcd == 0 || userIdHidden.value == 'NULL' ) {
				// Flush text area
				document.getElementById("textComment"+pcd).value = '';
			}
			if ( userIdHidden.value == 'NULL' ) {
				$('#commentBox'+pcd).html('<div id="commentsError' + pcd + '" class="error">&nbsp;</div>');
			}
		} else {
			// Enable Post Comment button
			document.getElementById("postComment"+pcd).value = b;
			document.getElementById("postComment"+pcd).onclick = function () { postComment(commentDate, pcd,thanked); };
		}
		// Populate Comments
		populateComments(resp,pcd);
	});
}

// Delete Comment
function deleteComment(cd) {
	// if the record being deleted is the only one in the page, the page will refresh as blank, so we need to shift one page backwards
	if ( commentsCountHidden.value == 1 ) {
		commentsKeyRefreshHidden.value = commentsKeyAlternateRefreshHidden.value;
		commentsKeyAlternateRefreshHidden.value = 'NULL';
	}
	// delete Comment in the database and refresh Comments table
	if (confirm("Are you sure you want to delete this comment?")) {
		var a = document.getElementById("comment"+cd+"Div").innerHTML;
		$.ajax({
			type: "POST",
			url: "/action/delete-comment",
			data: { zoneId : zoneIdHidden.value,
					postId : postIdHidden.value,
					pageId : pageIdHidden.value,
					commentDate : cd,
					commentText : encodeURIComponent(a),
					commentsKeyRefresh : commentsKeyRefreshHidden.value },
			dataType: "json"
		}).always(function (resp) { populateComments(resp,0); });
	}
}

// Check similar posts when writing post
function checkSimilarPosts() {
	window.open('/search/'+document.getElementById("userIdHidden").value+'/post/'+document.getElementById("postTitleInput").value.trim(), '_blank');
}

// Get Related Posts
function getRelatedPosts(rpcPeriod) {
	var b = postIdHidden.value;
	if ( b != 'PROCESSING' && b != 'NEW' ) {
		$.ajax({
			type: "POST",
			url: "/action/get-related-posts",
			data: { postId : b, postTitle : postTitle.innerHTML, rpcPeriod : rpcPeriod },
			dataType: "json"
		}).always(function (resp) { populateRelatedPosts(resp); });
	}
}

// Populate Related Posts
function populateRelatedPosts(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				$("#relatedPostsDiv").text(value);
				break;

			case 'relatedPostsList':
				var r = new Array(), j = -1;
				for (var i = 0; i < value.length; i++) {
					r[++j] = '<div class="grow" onclick="location.href=\'/post/' + value[i].postId + '\';" style="cursor:pointer; border:1px solid #DDD7D8; padding:4px; margin-right:15px; margin-top:20px; width:244px; height:310px; background-color:white; float:left">';
						r[++j] = '<div style="font-size:16px; font-weight:bold; color:#0074D9; padding:3px 8px">';
							var t = value[i].postTitle + " ";
							if ( t.length < 46 ) {
								r[++j] = t;
							} else {
								r[++j] = t.substr(0,45) + '...';
							}
						r[++j] = '</div>';
						r[++j] = '<div class="topChartsThumb" style="width:232px; height:150px; display:block; margin:0 auto; background-image:url(' + value[i].postHeadshot + ')"></div>';
						var s = value[i].postSummary + " ";
						if ( s.length < 101 ) {
							r[++j] = '<div style="padding:3px 8px; color:#575757">' + s + '</div>';
						} else {
							r[++j] = '<div style="padding:3px 8px; color:#575757">' + s.substr(0,100) + '...</div>';
						}
					r[++j] = '</div>';
				}
				r[++j] = '<div style="clear:both"></div>';
				$('#relatedPostsDiv').html(r.join(''));
				break;

		}
	});
}
