
/******************************************/
/************ global variables ************/
/******************************************/

var editor = '';
var html = '';
var config = {};

/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Get Post Content
function getPostContent() {
	if ( postIdInput.value.trim() == '' ) {
		s3EditorRetMessage.innerHTML = 'Please enter Post Id. For newsletter announcements, type nl-announcements';
	}
	$.ajax({
		type: "POST",
		url: "/admin/get-post-content",
		data: { postId: postIdInput.value },
		dataType: "json"
	}).always(function (resp) {
		$.each( resp, function(key,value) {
			switch (key) {
				case 'postTitle':
					postTitle.innerHTML = value;
					break;
				case 'postSummary':
					postSummary.innerHTML = value;
					break;
				case 'postHeadshot':
					if ( value.trim() == '' ) {
						$('#postHeadshotImg').css('display','none');
					} else {
						$('#postHeadshotImg').css('display','block');
						postHeadshotImg.src = value;
					}
					$('#postHeadshotEdit').css('display','none');
					break;
				case 'bloggerId':
					bloggerId.innerHTML = '<a target="_self" href="/' + value + '">' + value + "</a>";
					break;
				case 'postUpdateDate':
					postUpdateDate.innerHTML = new Date(value).toLocaleString();
					break;
				case 'postURL':
					$('#postContent').load(value);
					break;
				case 's3EditorRetMessage':
					s3EditorRetMessage.innerHTML = value;
					break;
			}
		});
		// make post area visible
		$('#postArea').css("display","block");
		// show get and edit buttons
		$('#s3EditorButtons').html(
			'<input class="activeButton" style="height:35px" onclick="getPostContent()" type="button" value="Get Content">' +
			'<input class="activeButton" style="height:35px" onclick="editContent()" type="button" value="Edit">'
		);
	});
}

// Edit Content
function editContent() {
	if ( editor != '' ) return;
	// title
	t = postTitle.innerHTML;
	$('#postTitle').html('<input id="postTitleInput" type=text style="width:350px" value="'+t+'"><br></p>');
	// summary
	s = postSummary.innerHTML;
	$('#postSummary').html('<input id="postSummaryInput" type=text style="width:350px" value="'+s+'"><br></p>');
	// headshot
	if ( postHeadshotImg.src.trim() != '' ) {
		$('#postHeadshotEdit').css('display','block');
	}
	// content
	html = postContent.innerHTML;
	$('#postContent').html('');
	editor = CKEDITOR.appendTo('postContent', config, html);
	// edit controls
	$('#s3EditorButtons').html(
		'<input class="activeButton" style="height:35px" onclick="saveContent()" type="button" value="Save">' +
		'<input class="activeButton" style="height:35px" onclick="closeEditor()" type="button" value="Close">'
	);
}

// Show headshot box
function showPostHeadshotBox() {
	$('body, #imageBox, #modal-background').toggleClass('active');
	populateFolders();
	showContents('common');
}

// Close CK editor and refresh post content
function closeEditor() {
	if ( editor != '' ) editor.destroy();
	editor = '';
	getPostContent();
}

// Save Post Content
function saveContent() {
	// prevent calling this method twice in case of doubleclick
	var b = s3EditorRetMessage.innerHTML;
	if ( b == "Processing..." ) {
		return false;
	} else {
		s3EditorRetMessage.innerHTML = "Processing...";
	}
	// run basic checks
	if ( editor == '' ) return;
	if (postTitleInput.value.length == 0) {
		s3EditorRetMessage.innerHTML = 'Title cannot be empty.';
		return false;
	}
	if (editor.getData().length == 0) {
		s3EditorRetMessage.innerHTML = 'Content cannot be empty.';
		return false;
	}
	// disable buttons while we save the data
	$('#s3EditorButtons').html(
		'<input class="disabledButton" style="height:35px" onclick="return false;" type="button" value="Save">' +
		'<input class="disabledButton" style="height:35px" onclick="return false;" type="button" value="Close">'
	);
    // upload data into S3 via spring controller
    var formdata = new FormData();
   	formdata.append("postId", postIdInput.value);
   	formdata.append("postTitle", postTitleInput.value);
   	formdata.append("postSummary", postSummaryInput.value);
   	formdata.append("postHeadshot", postHeadshotImg.src);
   	formdata.append("postContent", editor.getData());
   	$.ajax({
   		type: "POST",
   		url: "/admin/save-post-content",
   		data: formdata,
   		processData: false,
   		contentType: false
   	}).always(function(resp) {
   		if (resp == "SUCCESS") {
   			s3EditorRetMessage.innerHTML = 'Last saved : ' + new Date().toLocaleString();
   			$('#s3EditorButtons').html(
				'<input class="activeButton" style="height:35px" onclick="saveContent()" type="button" value="Save">' +
				'<input class="activeButton" style="height:35px" onclick="closeEditor()" type="button" value="Close">'
			);
   		} else {
   			s3EditorRetMessage.innerHTML = resp;
   		}
   	});
}
