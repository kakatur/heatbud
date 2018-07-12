
/************************************************/
/************ jquery ready functions ************/
/************************************************/
$(document).ready(function() {

	// AJAX call to query next page of Draft Posts
	$("#getDraftPostsNext").click(function() {
		$.getJSON("/action/get-profile-page-posts-next", {bloggerId : bloggerIdHidden.value, profilePagePostsKeyNextBI : draftPostsKeyNextBIHidden.value, profilePagePostsKeyNextUD : draftPostsKeyNextUDHidden.value, publishFlag : 'N' }, function(JSONdata) {
			populateUnpublishedPagePosts(JSONdata, 'N');
		});
	});

	// AJAX call to query previous page of Draft Posts
	$("#getDraftPostsPrevious").click(function() {
		$.getJSON("/action/get-profile-page-posts-previous", {bloggerId : bloggerIdHidden.value, profilePagePostsKeyPrevBI : draftPostsKeyPrevBIHidden.value, profilePagePostsKeyPrevUD : draftPostsKeyPrevUDHidden.value, publishFlag : 'N' }, function(JSONdata) {
			populateUnpublishedPagePosts(JSONdata, 'N');
		});
	});

	// AJAX call to query next page of Deleted Posts
	$("#getDeletedPostsNext").click(function() {
		$.getJSON("/action/get-profile-page-posts-next", {bloggerId : bloggerIdHidden.value, profilePagePostsKeyNextBI : deletedPostsKeyNextBIHidden.value, profilePagePostsKeyNextUD : deletedPostsKeyNextUDHidden.value, publishFlag : 'D' }, function(JSONdata) {
			populateUnpublishedPagePosts(JSONdata, 'D');
		});
	});

	// AJAX call to query previous page of Deleted Posts
	$("#getDeletedPostsPrevious").click(function() {
		$.getJSON("/action/get-profile-page-posts-previous", {bloggerId : bloggerIdHidden.value, profilePagePostsKeyPrevBI : deletedPostsKeyPrevBIHidden.value, profilePagePostsKeyPrevUD : deletedPostsKeyPrevUDHidden.value, publishFlag : 'D' }, function(JSONdata) {
			populateUnpublishedPagePosts(JSONdata, 'D');
		});
	});

});

/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Function to populate posts
function populateUnpublishedPagePosts(JSONdata, publishFlag) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'profilePagePostsList':
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
				if (publishFlag == 'N') {
					$('#draftPostsDiv').html(r.join(''));
				} else {
					$('#deletedPostsDiv').html(r.join(''));
				}
				if (publishFlag == 'N') {
					document.getElementById('draftPostsHeader').scrollIntoView();
				} else {
					document.getElementById('deletedPostsHeader').scrollIntoView();
				}
				break;

			case 'profilePagePostsKeyPrevBI':
				if (publishFlag == 'N') {
					draftPostsKeyPrevBIHidden.value = value;
					if (value == 'NULL') {
						$('#getDraftPostsPreviousDiv').css("visibility","hidden");
					} else {
						$('#getDraftPostsPreviousDiv').css("visibility","visible");
					}
				} else {
					deletedPostsKeyPrevBIHidden.value = value;
					if (value == 'NULL') {
						$('#getDeletedPostsPreviousDiv').css("visibility","hidden");
					} else {
						$('#getDeletedPostsPreviousDiv').css("visibility","visible");
					}
				}
				break;

			case 'profilePagePostsKeyNextBI':
				if (publishFlag == 'N') {
					draftPostsKeyNextBIHidden.value = value;
					if (value == 'NULL') {
						$('#getDraftPostsNextDiv').css("visibility","hidden");
					} else {
						$('#getDraftPostsNextDiv').css("visibility","visible");
					}
				} else {
					deletedPostsKeyNextBIHidden.value = value;
					if (value == 'NULL') {
						$('#getDeletedPostsNextDiv').css("visibility","hidden");
					} else {
						$('#getDeletedPostsNextDiv').css("visibility","visible");
					}
				}
				break;

			case 'profilePagePostsKeyPrevUD':
				if (publishFlag == 'N') {
					draftPostsKeyPrevUDHidden.value = value;
				} else {
					deletedPostsKeyPrevUDHidden.value = value;
				}
				break;

			case 'profilePagePostsKeyNextUD':
				if (publishFlag == 'N') {
					draftPostsKeyNextUDHidden.value = value;
				} else {
					deletedPostsKeyNextUDHidden.value = value;
				}
				break;
			}
	});
}
