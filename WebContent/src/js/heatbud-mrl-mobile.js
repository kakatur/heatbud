
/************************************************/
/************ jquery ready functions ************/
/************************************************/

$(document).ready(function() {

	// AJAX call to query next page of Comments
	$("#getCommentsNext").click(function() {
		$.ajax({
			type: "POST",
			url: "/action/get-comments-next",
			data: { postId : postIdHidden.value, commentsKeyNext : commentsKeyNextHidden.value },
			dataType: "json"
		}).always(function (resp) { populateComments(resp); });
	});

	// AJAX call to query previous page of Comments
	$("#getCommentsPrev").click(function() {
		$.ajax({
			type: "POST",
			url: "/action/get-comments-prev",
			data: { postId : postIdHidden.value, commentsKeyPrev : commentsKeyPrevHidden.value },
			dataType: "json"
		}).always(function (resp) { populateComments(resp); });
	});

});

/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Populate comments
function populateComments(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'commentsList':
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
					r[++j] = '<div style="font-size:1.2em; ' + commentIndentCSS + commentHeaderCSS + '">';
					r[++j] = '<span id="thankComment' + value[i].commentDate + 'Span">';
					if ( thanked == '2' && cd == pcd ) {
						r[++j] = '<img alt="thanked by the blogger" style="max-height:15px; margin-right:10px; border:none" src="/resources/images/thanked.jpg"/>&nbsp;';
					}
					r[++j] = '</span>';
					r[++j] = '<span style="color:#909090">' + new Date(cd).toLocaleString() + '</span><br/>';
					r[++j] = '<a target="_self" href="/' + value[i].commenterId + '">' + value[i].commenterName + '</a>';
					r[++j] = '</div>';
					// comment text
					r[++j] = '<div id="comment' + value[i].commentDate + 'Div" style="font-size:1.2em; ' + commentIndentCSS + 'margin-top:4px; white-space:pre-line">' + value[i].commentText + '</div>';
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

		}
	});
}

// Get FB shares
function getFBShares(f) {
	var id = document.getElementById(f+"IdHidden").value;
	$.ajax({
		type: "GET",
		url: "https://graph.facebook.com/https://www.heatbud.com/"+f+"/"+id,
		data: { },
		dataType: "json"
	}).always(function (resp) {
		document.getElementById(f+"FbShares").innerHTML = " " + resp.share.share_count + " ";
	});
}
