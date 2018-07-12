
/************************************************/
/************ jquery ready functions ************/
/************************************************/
$(document).ready(function() {

	// AJAX call to query next page of Published Posts
	$("#getPublishedPostsNext").click(function() {
		$.getJSON(
			"/action/get-profile-page-posts-next",
			{bloggerId : entityIdHidden.value, profilePagePostsKeyNextBI : publishedPostsKeyNextBIHidden.value, profilePagePostsKeyNextUD : publishedPostsKeyNextUDHidden.value, publishFlag : 'Y' },
			function(JSONdata) { populateProfilePagePosts(JSONdata); }
		);
	});

	// AJAX call to query previous page of Published Posts
	$("#getPublishedPostsPrevious").click(function() {
		$.getJSON(
			"/action/get-profile-page-posts-previous",
			{bloggerId : entityIdHidden.value, profilePagePostsKeyPrevBI : publishedPostsKeyPrevBIHidden.value, profilePagePostsKeyPrevUD : publishedPostsKeyPrevUDHidden.value, publishFlag : 'Y' },
			function(JSONdata) { populateProfilePagePosts(JSONdata); }
		);
	});

	// AJAX call to query next page of Admin Zones
	$("#getAdminZonesNext").click(function() {
		$.getJSON(
			"/action/get-admin-zones-next",
			{entityId : entityIdHidden.value, adminZonesKeyNextUI : publishedPostsKeyNextUIHidden.value, adminZonesKeyNextZI : publishedPostsKeyNextZIHidden.value},
			function(JSONdata) { populateAdminZones(JSONdata); }
		);
	});

	// AJAX call to query previous page of Admin Zones
	$("#getAdminZonesPrevious").click(function() {
		$.getJSON(
			"/action/get-admin-zones-previous",
			{entityId : entityIdHidden.value, adminZonesKeyPrevUI : publishedPostsKeyPrevUIHidden.value, adminZonesKeyPrevZI : publishedPostsKeyPrevZIHidden.value},
			function(JSONdata) { populateAdminZones(JSONdata); } );
	});

});

/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Function to populate profile page posts
function populateProfilePagePosts(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				$('#publishedPostsDiv').html(value);
				break;

			case 'profilePagePostsList':
				var r = new Array(), j = -1;
				for (var i = 0; i < value.length; i++) {
				    r[++j] = '<div style="padding:6% 3% 3% 3%; background-color:white; border-bottom:1px solid rgb(52, 127, 125)">';
				    r[++j] = '<div style="font-size:1.3em; font-weight:bold">';
				    r[++j] = '<a href="/post/' + value[i].postId + '">' + value[i].postTitle + '</a></div>';
				    r[++j] = '<div style="text-align:center; margin-top:2%">';
				    r[++j] = '<a href="/post/' + value[i].postId + '"><img alt="' + value[i].postTitle + '" title="' + value[i].postTitle + '" style="width:90%" src="' + value[i].postHeadshot + '"></a></div>';
				    r[++j] = '<div style="font-size:1.2em; color:#909090; margin-top:2%">by <a href="/' + value[i].bloggerId + '">' + value[i].bloggerName + '</a></div>';
				    r[++j] = '<div style="font-size:1em; color:#909090; margin-top:2%">on ' + new Date(value[i].updateDate).toLocaleString() + '</div>';
				    r[++j] = '<div style="font-size:1.2em; color:#909090; margin-top:2%">zone <a href="/zone/' + value[i].zoneId + '">' + value[i].zoneName.replace(/"/g,'&#034;') + '</a></div>';
				    r[++j] = '<div style="font-size:1.2em; margin-top:2%" >' + value[i].postSummary + '</div></div>';
				}
				$('#publishedPostsDiv').html(r.join(''));
				document.getElementById('publishedPostsHeader').scrollIntoView();
				break;

			case 'profilePagePostsKeyPrevBI':
				publishedPostsKeyPrevBIHidden.value = value;
				if (value == 'NULL') {
					$('#getPublishedPostsPreviousDiv').css("visibility","hidden");
				} else {
					$('#getPublishedPostsPreviousDiv').css("visibility","visible");
				}
				break;

			case 'profilePagePostsKeyNextBI':
				publishedPostsKeyNextBIHidden.value = value;
				if (value == 'NULL') {
					$('#getPublishedPostsNextDiv').css("visibility","hidden");
				} else {
					$('#getPublishedPostsNextDiv').css("visibility","visible");
				}
				break;

			case 'profilePagePostsKeyPrevUD':
				publishedPostsKeyPrevUDHidden.value = value;
				break;

			case 'profilePagePostsKeyNextUD':
				publishedPostsKeyNextUDHidden.value = value;
				break;
			}
	});
}

// Function to populate admin zones
function populateAdminZones(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'adminZonesList':
				var r = new Array(), j = -1;
				r[++j] = '<table style="border-spacing:8px">';
				for (var i = 0; i < value.length; i++) {
					var votes = parseInt(value[i].upVotes)-parseInt(value[i].downVotes);
					r[++j] = '<tr><td style="vertical-align:top">';
				    r[++j] = '<div style="font-size:200%; text-align:center">'+votes+'</div><div style="font-size:11px; color:#909090">votes</div></td>';
					r[++j] = '<td style="vertical-align:top">';
				    r[++j] = '<div style="font-size:200%; text-align:center">'+value[i].comments+'</div><div style="font-size:11px; color:#909090">comments</div></td>';
					r[++j] = '<td style="vertical-align:top"><div style="margin-left:10px">';
				    r[++j] = '<div style="font-weight:bold"><a href="/post/' + value[i].postId + '">' + value[i].postTitle + '</a></div>';
				    r[++j] = '<div style="font-size:11px; color:#909090"><span><a href="/zone/' + value[i].zoneId + '">' + value[i].zoneName.replace(/"/g,'&#034;') + '</a>';
				    r[++j] = '</span><span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
				    r[++j] = '<span style="color:#909090">' + new Date(value[i].updateDate).toLocaleString() +'</span></div>';
				    r[++j] = '<div>' + value[i].postSummary + '</div>';
				    r[++j] = '</div></td></tr>';
				}
				r[++j] = '</table>';
				$('#adminZonesDiv').html(r.join(''));
				document.getElementById('adminZonesHeader').scrollIntoView();
				break;

			case 'adminZonesKeyPrevUI':
				adminZonesKeyPrevUIHidden.value = value;
				if (value == 'NULL') {
					$('#getAdminZonesPreviousDiv').css("visibility","hidden");
				} else {
					$('#getAdminZonesPreviousDiv').css("visibility","visible");
				}
				break;

			case 'adminZonesKeyNextUI':
				adminZonesKeyNextUIHidden.value = value;
				if (value == 'NULL') {
					$('#getAdminZonesNextDiv').css("visibility","hidden");
				} else {
					$('#getAdminZonesNextDiv').css("visibility","visible");
				}
				break;

			case 'adminZonesKeyPrevZI':
				adminZonesKeyPrevZIHidden.value = value;
				break;

			case 'adminZonesKeyNextZI':
				adminZonesKeyNextZIHidden.value = value;
				break;
			}
	});
}

// Get FB shares
function getFBShares(f) {
	var id = document.getElementById(f+"IdHidden").value;
	$.ajax({
		type: "GET",
		url: "https://graph.facebook.com/https://www.heatbud.com/"+id,
		data: { },
		dataType: "json"
	}).always(function (resp) {
		document.getElementById(f+"FbShares").innerHTML = " "+resp.share.share_count+" ";
	});
}
