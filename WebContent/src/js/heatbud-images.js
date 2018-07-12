
/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Show Create folder dialog
function showCreateFolder() {
	$('body, #createFolderBox, #modal-background').toggleClass('active');
}

// Close create folder dialog
function cancelCreateFolder() {
	$('body, #createFolderBox, #modal-background').toggleClass('active');
	createFolderMessage.innerHTML = "&nbsp;";
}

// Create folder
function createFolder() {
	var f = document.getElementById("createFolderInput").value.trim().toLowerCase();
	if (f == null || f == "") {
		createFolderMessage.innerHTML = "Please enter album name.";
		return false;
	}
	if ( f.length > 20 ) {
		createFolderMessage.innerHTML = "Album name cannot have more than twenty characters.";
		return false;
	}
	var letterNumber = /^[0-9a-zA-Z-]+$/;
	if ( !(f.match(letterNumber)) ) {
		createFolderMessage.innerHTML = "Only alphanumerics and dashes are allowed in the album name.";
		return false;
	}
	if ( FL.indexOf(f) != -1 ) {
		createFolderMessage.innerHTML = "Duplicate album name.";
		return false;
	}
	$("#folders").append(
		'<li id="f' + f + '" class="zoneList myZones" title="' + f + '">' +
			'<div class="zoneName" onclick="showContents(\'' + f + '\')">'  + f + '</div>' +
			'<div style="float:left; width:10px">&nbsp;</div>' +
			'<div style="float:left; width:12px" onclick="deleteFolder(\'' + f + '\')" title="Delete this album">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>' +
		'</li>'
	);
	FL[FL.length] = f;
	cancelCreateFolder();
}

// Populate Folders
function populateFolders() {
	$.ajax({
		type: "POST",
		url: "/user/get-folders",
		data: { },
		dataType: "json"
	}).always(function (JSONdata) {
		$.each( JSONdata, function(key,value) {
			switch (key) {
				case 'ERROR':
					alert(value);
					break;

				case 'foldersList':
					var r = new Array(), j = -1; // define
					FL = new Array(); // reset
				    r[++j] = '<ul id=folders style="list-style-type:none; padding-left:6px">';
				    r[++j] = '<li class="zoneList" title="common">';
				    r[++j] = '<div class="zoneName bgColor" onclick="showContents(\'common\')">common</div></li>';
					for (var i = 0; i < value.length; i++) {
						r[++j] = '<li id="f' + value[i] + '" class="zoneList myZones" title="' + value[i] + '">';
					    r[++j] = '<div class="zoneName" onclick="showContents(\'' + value[i] + '\')">' + value[i] + '</div>';
					    r[++j] = '<div style="float:left; width:10px">&nbsp;</div><div style="float:left; width:12px" onclick="deleteFolder(\'' + value[i] + '\')" title="Delete this album">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div></li>';
						FL[i] = value[i];
					}
				    r[++j] = '</ul><ul style="list-style-type:none; padding-left:6px">';
				    r[++j] = '<li class="zoneList" title="New Album">';
				    r[++j] = '<div class="zoneName" onclick="showCreateFolder()">New Album</div>';
				    r[++j] = '</li></ul><div style="clear:both"></div>';
					$('#foldersDiv').html(r.join(''));
					break;
			}
		});
	});
}

// AJAX call to upload image
function uploadImage(file) {

	// clear error messages
	document.getElementById('imagesMessage').innerHTML = "&nbsp;";
	document.getElementById('imagesMessageTop').innerHTML = "&nbsp;";

	// run validations
    if (!file || !file.type.match(/image.*/)) { document.getElementById('imagesMessage').innerHTML='Image type not supported.'; return; }
    if (file.size < 1024) { document.getElementById('imagesMessage').innerHTML='Image too small.'; return; }
    if (file.size > 5242880) { document.getElementById('imagesMessage').innerHTML='Please limit file size to 5MB.'; return; }
    uploadImageA.value = 'Please wait...';

    // read the file
    var reader = new FileReader();
    reader.readAsDataURL(file);

    // upload file to S3 via spring controller
    var formdata = new FormData();
   	formdata.append(selectedFolder.innerHTML, file);
   	$.ajax({
   		type: "POST",
   		url: "/user/upload-image",
   		data: formdata,
   		processData: false,
   		contentType: false
   	}).always(function(resp) {
   		if ( resp == "SUCCESS" ) {
   	   		showContents(selectedFolder.innerHTML);
   		} else {
   			document.getElementById('imagesMessage').innerHTML = resp;
   		}
	    uploadImageA.value = 'Upload';
   	    // clear file input value, otherwise onchange event doesn't trigger if user tries to upload the same file again
   	    $("#uploadImageInput").replaceWith($("#uploadImageInput").val('').clone(true));
   	});

}

// Select image
function selectImage(name) {
	selectedImageHidden.value = name;
	for (var i = 0; i < IL.length; i++) {
	    if ( IL[i] == name ) {
	    	document.getElementById(IL[i]).style.backgroundColor = "rgb(208, 219, 225)";
	    } else {
	    	document.getElementById(IL[i]).style.backgroundColor = "white";
	    }
	}
}

// Choose image
function chooseImage() {
	if (selectedImageHidden.value == "NULL") {
		document.getElementById('imagesMessage').innerHTML = "Please select an image.";
		return false;
	}
	if (CKEditorFuncNumHidden.value == "PostHeadshot") {
		// use thumbs folder for post headshot
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/thumbs/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		postHeadshotNewHidden.value = selectedURL;
		document.getElementById('headshotImgDiv').style.backgroundImage = 'url('+selectedURL+')';
		$('body, #imageBox, #modal-background').toggleClass('active');
	} else if (CKEditorFuncNumHidden.value == "S3PostHeadshot") {
		// use thumbs folder for S3 post headshot
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/thumbs/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		postHeadshotImg.src = selectedURL;
		$('body, #imageBox, #modal-background').toggleClass('active');
	} else if (CKEditorFuncNumHidden.value == "ZoneHeadshot") {
		// use social folder for zone headshot
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/social/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		saveZoneHeadshot(selectedURL);
		$('body, #imageBox, #modal-background').toggleClass('active');
	} else if (CKEditorFuncNumHidden.value == "ProfilePhoto") {
		// use thumbs folder for profile photo
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/thumbs/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		saveProfilePhoto(selectedURL);
		$('body, #imageBox, #modal-background').toggleClass('active');
	} else if (CKEditorFuncNumHidden.value == "ProfileBG") {
		// use images folder for profile BG
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/images/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		saveProfileBG(selectedURL);
		$('body, #imageBox, #modal-background').toggleClass('active');
	} else if (CKEditorFuncNumHidden.value == "Logo") {
		// use images folder for Logo
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/images/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		saveLogo(selectedURL);
		$('body, #imageBox, #modal-background').toggleClass('active');
	} else {
		// use images folder if embedded in post content
		var selectedURL = "https://s3-us-west-2.amazonaws.com/heatbudimages/"+userIdHidden.value+"/images/"+selectedFolder.innerHTML+"/"+selectedImageHidden.value;
		window.opener.CKEDITOR.tools.callFunction(CKEditorFuncNumHidden.value,selectedURL);
		window.close();
	}
}

// Select and choose image (on double click)
function selectAndChooseImage(name) {
	selectImage(name);
	chooseImage();
}

// Delete Image
function deleteImage() {
	if (selectedImageHidden.value == "NULL") {
		document.getElementById('imagesMessage').innerHTML = "Please select an image.";
		return false;
	}
	// clear error messages
	document.getElementById('imagesMessage').innerHTML = "&nbsp;";
	document.getElementById('imagesMessageTop').innerHTML = "&nbsp;";
	// get confirmation to delete
	if (confirm("Are you sure \""+selectedImageHidden.value+"\" is not being used in the posts or in the profile page?")) {
		$.ajax({
			type: "POST",
			url: "/user/delete-image",
			data: { key : selectedFolder.innerHTML+"/"+selectedImageHidden.value },
			dataType: "text"
		}).always(function (resp) {
			showContents(selectedFolder.innerHTML);
			document.getElementById('imagesMessage').innerHTML = resp;
		});
		var index = IL.indexOf(selectedImageHidden.value);
		IL.splice(index, 1);
		selectedImageHidden.value = "NULL";
	}
}

// Delete Folder
function deleteFolder(f) {
	// clear error messages
	document.getElementById('imagesMessage').innerHTML = "&nbsp;";
	document.getElementById('imagesMessageTop').innerHTML = "&nbsp;";
	if (confirm("Are you sure images from the album \""+f+"\" are not being used in the posts or in the profile page?")) {
		$.ajax({
			type: "POST",
			url: "/user/delete-folder",
			data: { key : f+"/" },
			dataType: "text"
		}).always(function (resp) {
			imagesMessageTop.innerHTML = resp;
			$("#f"+f).remove();
			var index = FL.indexOf(f);
			FL.splice(index, 1);
			if ( f == selectedFolder.innerHTML ) {
				showContents('common');
			}
		});
	}
}

// Show contents of a folder
function showContents(folder) {
	$('#images').html('<div style="margin-left:10px; margin-top:30px; font-size:12px">No images in the album.</div>');
	// clear error messages
	document.getElementById('imagesMessage').innerHTML = "&nbsp;";
	document.getElementById('imagesMessageTop').innerHTML = "&nbsp;";
	selectedFolder.innerHTML = folder;
	selectedImageHidden.value = "NULL";
	$.ajax({
		type: "POST",
		url: "/user/get-images",
		data: { folderName : selectedFolder.innerHTML },
		dataType: "json"
	}).always(function (resp) { populateImages(resp); });
}

// Populate Images
function populateImages(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'imagesList':
				var r = new Array(), j = -1; // define
				IL = new Array(); // reset
				for (var i = 0; i < value.length; i++) {
					r[++j] = '<table id="' + value[i].name + '" onclick="selectImage(\'' + value[i].name + '\')" ondblclick="selectAndChooseImage(\'' + value[i].name + '\')" class="boxedElement" style="width:400px"><tr>';
				    r[++j] = '<td><img class="profileThumb" src="https://s3-us-west-2.amazonaws.com/heatbudimages/' + userIdHidden.value + '/thumbs/' + selectedFolder.innerHTML + '/' + value[i].name + '">';
				    r[++j] = '</td><td style="vertical-align:top"><div style="margin-left:20px">';
				    r[++j] = '<div style="font-weight:bold">' + value[i].name + '</div>';
				    r[++j] = '<div style="white-space:pre-line; font-size:12px">' + new Date(value[i].date).toLocaleString() + '</div>';
				    r[++j] = '</div></td></tr></table>';
					IL[i] = value[i].name;
				}
				if ( IL.length != 0 ) {
					$('#images').html(r.join(''));
					selectImage(IL[0]);					
				}
				break;
		}
	});
}
