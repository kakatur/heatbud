
/************************************************/
/************ jquery ready functions ************/
/************************************************/
$(document).ready(function() {

	// AJAX call to query next page of Published Posts
	$("#getPublishedPostsNext").click(function() {
		$.getJSON("/action/get-profile-page-posts-next", {bloggerId : entityIdHidden.value, profilePagePostsKeyNextBI : publishedPostsKeyNextBIHidden.value, profilePagePostsKeyNextUD : publishedPostsKeyNextUDHidden.value, publishFlag : 'Y' }, function(JSONdata) {
			populateProfilePagePosts(JSONdata, 'Y');
		});
	});

	// AJAX call to query previous page of Published Posts
	$("#getPublishedPostsPrevious").click(function() {
		$.getJSON("/action/get-profile-page-posts-previous", {bloggerId : entityIdHidden.value, profilePagePostsKeyPrevBI : publishedPostsKeyPrevBIHidden.value, profilePagePostsKeyPrevUD : publishedPostsKeyPrevUDHidden.value, publishFlag : 'Y' }, function(JSONdata) {
			populateProfilePagePosts(JSONdata, 'Y');
		});
	});

	// AJAX call to enable/ disable email
	$("#enableEmail").click(function() {
		// prevent calling this method twice in case of doubleclick
		var b = enableEmail.innerHTML;
		if ( b == "Please wait..." ) {
			return false;
		} else {
			enableEmail.innerHTML = "Please wait...";
		}
		// get the flag
		var e;
		if ( b == "Disable email" ) {
			e = 'N';
		} else {
			e = 'Y';
		}
		// call controller method
		$.post(	"/user/entity-enable-email", { entityId : entityIdHidden.value, enableEmail : e } ).always(function (resp) {
			if ( resp == 'SUCCESS' ) {
				if ( b == "Disable email" ) {
					// reverse the text
					enableEmail.innerHTML = "Enable email";
					// disable div
					document.getElementById("sendEmailDiv").onclick = function () { return false; };
					document.getElementById("sendEmailDiv").style.color = '#727272';
					document.getElementById("sendEmailDiv").style.cursor = 'default';
				} else {
					// reverse the text
					enableEmail.innerHTML = "Disable email";
					// enable div
					document.getElementById("sendEmailDiv").onclick = function () { showSendEmailBox(); };
					document.getElementById("sendEmailDiv").style.color = 'white';
					document.getElementById("sendEmailDiv").style.cursor = 'pointer';
				}
			} else {
				alert(resp);
			}
		});
	});

	// AJAX call to query next page of Admin Zones
	$("#getAdminZonesNext").click(function() {
		$.getJSON("/action/get-admin-zones-next", {bloggerId : entityIdHidden.value, adminZonesKeyNextUI : adminZonesKeyNextUIHidden.value, adminZonesKeyNextZI : adminZonesKeyNextZIHidden.value}, function(JSONdata) {
			populateAdminZones(JSONdata);
		});
	});

	// AJAX call to query previous page of Admin Zones
	$("#getAdminZonesPrevious").click(function() {
		$.getJSON("/action/get-admin-zones-previous", {bloggerId : entityIdHidden.value, adminZonesKeyPrevUI : adminZonesKeyPrevUIHidden.value, adminZonesKeyPrevZI : adminZonesKeyPrevZIHidden.value}, function(JSONdata) {
			populateAdminZones(JSONdata);
		});
	});

});

/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Show image box
function showImageBox(imageType) {
	CKEditorFuncNumHidden.value = imageType;
	$('body, #imageBox, #modal-background').toggleClass('active');
	populateFolders();
	showContents('common');
}

// Update Color
function updateColor() {
	var f = document.getElementById("hexColor").value.trim();
	var isOk  = /(^#[0-9A-F]{6}$)|(^#[0-9A-F]{3}$)/i.test("#"+f);
	if ( !isOk ) {
		$("#profileColorRetMessage").text("Invalid color code.");
		return false;
	} else {
		$("#profileColorRetMessage").text("");
	}
	$('#selectedColor').css('background-color',"#"+f);
}

// Save Email
function saveEmail() {
	// prevent calling this method twice in case of doubleclick
	var b = emailRetMessage.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		emailRetMessage.innerHTML = "Please wait...";
	}
	// call controller method
	var f = document.getElementById("email").value.trim();
	if (f.length == 0) {
		$("#emailRetMessage").text("Please enter your Email.");
		return false;
	}
	$.post(	"/user/update-email-address", { entityId : entityIdHidden.value, emailAddress : encodeURIComponent(f) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			document.getElementById("emailA").href = "mailto:"+f;
			document.getElementById("emailA").innerHTML = f;
			$('body, #emailBox, #modal-background').toggleClass('active');
		} else {
			$("#emailRetMessage").text(resp);
		}
	});
}

// Save Phone
function savePhone() {
	// prevent calling this method twice in case of doubleclick
	var b = phoneRetMessage.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		phoneRetMessage.innerHTML = "Please wait...";
	}
	var f = document.getElementById("phone").value.trim();
	if (f.length == 0) {
		$("#phoneRetMessage").text("Please enter your Phone Number.");
		return false;
	}
	$.ajax({
		type: "POST",
		url: "/user/update-phone",
		data: { entityId : entityIdHidden.value, phone : encodeURIComponent(f) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			document.getElementById("phoneA").innerHTML = f;
			$('body, #phoneBox, #modal-background').toggleClass('active');
		} else {
			$("#phoneRetMessage").text(resp);
		}
	});
}

// Save Address
function saveAddress() {
	// prevent calling this method twice in case of doubleclick
	var b = addressRetMessage.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		addressRetMessage.innerHTML = "Please wait...";
	}
	var f = document.getElementById("address").value.trim();
	if (f.length == 0) {
		$("#addressRetMessage").text("Please enter your Address.");
		return false;
	}
	$.ajax({
		type: "POST",
		url: "/user/update-address",
		data: { entityId : entityIdHidden.value, address : encodeURIComponent(f) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			location.href = 'https://www.heatbud.com/'+entityIdHidden.value+'#announcementsDiv';
			location.reload();
		} else {
			$("#addressRetMessage").text(resp);
		}
	});
}

// Save Website
function saveWebsite() {
	// prevent calling this method twice in case of doubleclick
	var b = websiteRetMessage.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		websiteRetMessage.innerHTML = "Please wait...";
	}
	var f = document.getElementById("website").value.trim();
	if (f.length == 0) {
		$("#websiteRetMessage").text("Please enter your website.");
		return false;
	}
	$.ajax({
		type: "POST",
		url: "/user/update-website",
		data: { entityId : entityIdHidden.value, website : encodeURIComponent(f) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			document.getElementById("websiteA").href = f;
			document.getElementById("websiteA").innerHTML = f;
			document.getElementById("logoA").href = f;
			$('body, #websiteBox, #modal-background').toggleClass('active');
		} else {
			$("#websiteRetMessage").text(resp);
		}
	});
}

// Show Color Box for Profile Color or Theme Color
function showColorBox (colorType) {
	$('body, #colorBox, #modal-background').toggleClass('active');
	if ( colorType == "profile" ) {
		document.getElementById("saveColorButton").onclick = function () { saveProfileColor(); };
		document.getElementById("saveColorButton").value = 'Set Theme Color';
	} else {
		document.getElementById("saveColorButton").onclick = function () { saveContactColor(); };
		document.getElementById("saveColorButton").value = 'Set Contact Font Color';
	}
}

// Save Profile Color
function saveProfileColor() {
	var f = document.getElementById("hexColor").value.trim();
	$.ajax({
		type: "POST",
		url: "/user/update-profile-color",
		data: { entityId : entityIdHidden.value, profileColor : encodeURIComponent(f) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			document.body.style.backgroundColor = '#'+f;
			$('body, #colorBox, #modal-background').toggleClass('active');
		} else {
			$("#profileColorRetMessage").text(resp);
		}
	});
}

// Save Contact Color
function saveContactColor() {
	var f = document.getElementById("hexColor").value.trim();
	$.ajax({
		type: "POST",
		url: "/user/update-contact-color",
		data: { entityId : entityIdHidden.value, contactColor : encodeURIComponent(f) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			$("#emailA").css({'color': '#'+f});
			$("#phoneA").css({'color': '#'+f});
			$("#websiteA").css({'color': '#'+f});
			$('body, #colorBox, #modal-background').toggleClass('active');
		} else {
			$("#colorRetMessage").text(resp);
		}
	});
}

// Edit Bio
function editBio() {
	// read entity type
	var entityType = document.getElementById("entityTypeHidden").value.trim();
	// read data
	var a = $('#aboutDiv').html();
	if ( entityType == 'B' ) {
		var p = $('#passionDiv').html();
		var ac = $('#achievementsDiv').html();
	}
	if ( entityType == 'P' ) {
		var an = $('#announcementsDiv').html();
	}
	var c = $('#contactDiv').html();
	// set input boxes in all fields
	$('#aboutDiv').html('<textarea cols="52" rows="6" id=aboutInput placeholder="Click Save Bio toward top of the page when done.">'+a+'</textarea>');
	if ( entityType == 'B' ) {
		$('#passionDiv').html('<textarea cols="102" id=passionInput placeholder="Click Save Bio toward top of the page when done.">'+p+'</textarea>');
		$('#achievementsDiv').html('<textarea cols="102" id=achievementsInput>'+ac+'</textarea>');
	}
	if ( entityType == 'P' ) {
		$('#announcementsDiv').html('<textarea cols="52" id=announcementsInput placeholder="Click Save Bio toward top of the page when done.">'+an+'</textarea>');
	}
	$('#contactDiv').html('<textarea cols="52" id=contactInput placeholder="Click Save Bio toward top of the page when done.">'+c+'</textarea>');
	// set edit controls
	$("#cancelBio").attr("class","activeButton");
	$("#cancelBio").attr("onclick","cancelBio()");
	$("#cancelBio").css("visibility","visible");
	$('#editBio').html('Save Bio');
	$("#editBio").attr("onclick","saveBio()");
	$("#instructions").css("visibility","visible");
	$("#editProfilePhoto").attr("class","disabledButton");
	$("#editProfileBG").attr("class","disabledButton");
	$("#editProfileColor").attr("class","disabledButton");
	if ( entityType == 'P' ) {
		$("#editLogo").attr("class","disabledButton");
		$("#editEmail").attr("class","disabledButton");
		$("#editPhone").attr("class","disabledButton");
		$("#editAddress").attr("class","disabledButton");
		$("#editWebsite").attr("class","disabledButton");
		$("#editContactColor").attr("class","disabledButton");
	}
}

// Cancel Bio
function cancelBio() {
	window.location.href='/'+entityIdHidden.value;
};

// Save Bio
function saveBio() {
	$('#editBio').html('Please wait...');
	// read entity type
	var entityType = document.getElementById("entityTypeHidden").value.trim();
	// read data
	var a=' ', p=' ', ac=' ', an=' ', c=' ';
	a = $('#aboutInput').val();
	if ( entityType == 'B' ) {
		p = $('#passionInput').val();
		ac = $('#achievementsInput').val();
	}
	if ( entityType == 'P' ) {
		an = $('#announcementsInput').val();
	}
	c = $('#contactInput').val();
	// place inputs into the html
	$('#aboutDiv').html(a);
	if ( entityType == 'B' ) {
		$('#passionDiv').html(p);
		$('#achievementsDiv').html(ac);
	}
	if ( entityType == 'P' ) {
		$('#announcementsDiv').html(an);
	}
	$('#contactDiv').html(c);
	// save data into the database
   	$.post(	"/user/update-bio",
   			{	entityId 		: entityIdHidden.value,
   				about			: encodeURIComponent(a),
   				passion			: encodeURIComponent(p),
   				achievements 	: encodeURIComponent(ac),
   				announcements	: encodeURIComponent(an),
   				contact			: encodeURIComponent(c)
   			}
   		).always(function () {
   			$('#editBio').html('Edit Bio');
   			$("#editBio").attr("onclick","editBio()");
   		}
   	);
	// reset edit controls
	$("#instructions").css("visibility","hidden");
	$("#cancelBio").css("visibility","hidden");
	$("#editProfilePhoto").attr("class","activeButton");
	$("#editProfileBG").attr("class","activeButton");
	$("#editProfileColor").attr("class","activeButton");
	if ( entityType == 'P' ) {
		$("#editLogo").attr("class","activeButton");
		$("#editEmail").attr("class","activeButton");
		$("#editPhone").attr("class","activeButton");
		$("#editAddress").attr("class","activeButton");
		$("#editWebsite").attr("class","activeButton");
		$("#editContactColor").attr("class","activeButton");
	}
};

// Save Profile Photo
function saveProfilePhoto(a) {
	$.ajax({
		type: "POST",
		url: "/user/update-profile-photo",
		data: { entityId : entityIdHidden.value, profilePhoto : encodeURIComponent(a) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			document.getElementById('profilePhotoDiv').style.backgroundImage = 'url('+a+')';
		} else {
			alert (resp);
		}
	});
}

// Save Profile Background
function saveProfileBG(a) {
	$.ajax({
		type: "POST",
		url: "/user/update-profile-bg",
		data: { entityId : entityIdHidden.value, profileBG : encodeURIComponent(a) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			document.getElementById('profileBGDiv').style.backgroundImage = 'url('+a+')';
		} else {
			alert (resp);
		}
	});
}

// Save Logo
function saveLogo(a) {
	$.ajax({
		type: "POST",
		url: "/user/update-logo",
		data: { entityId : entityIdHidden.value, logo : encodeURIComponent(a) },
		dataType: "text"
	}).always(function (resp) {
		if ( resp == "SUCCESS" ) {
			document.getElementById('logoImg').src = a;
		} else {
			alert (resp);
		}
	});
}

// JS function to edit Email Address
function editEmailAddress() {
	var a = emailAddressDiv.innerHTML;
	originalEmailAddress.value = a;
	emailAddressDiv.innerHTML = "";
	emailAddressInput.style.display = "block";
	emailAddressInput.value = a;
	editEmailAddressDiv.innerHTML = '<a id="saveEmailAddress" onclick="saveEmailAddress()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;&nbsp;&nbsp;<a id="cancelEmailAddress" onclick="cancelEmailAddress()" href="javascript:">Cancel</a>';
}

// JS function to cancel Email Address
function cancelEmailAddress() {
	emailAddressDiv.innerHTML = originalEmailAddress.value;
	emailAddressInput.value = "";
	emailAddressInput.style.display = "none";
	editEmailAddressDiv.innerHTML = '<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Update</a>';
	emailAddressRetMessage.innerHTML = "";
}

// AJAX call to save Email Address
function saveEmailAddress() {
	// prevent calling this method twice in case of doubleclick
	var b = editEmailAddressDiv.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		editEmailAddressDiv.innerHTML = "Please wait...";
	}
	// call controller method
	var a = emailAddressInput.value.trim();
	$.post(	"/user/update-email-address", { entityId : entityIdHidden.value, emailAddress : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			emailAddressDiv.innerHTML = a;
			emailAddressInput.value = "";
			emailAddressInput.style.display = "none";
			editEmailAddressDiv.innerHTML = '<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Update</a>';
			emailAddressRetMessage.innerHTML = "";
		} else {
			editEmailAddressDiv.innerHTML = b;			
			emailAddressRetMessage.innerHTML = resp;
		}
	});
}

// Send email
function sendEmail() {
	// gray out send email button to prevent a double click
	sendEmailButton.onclick = function () { return false; };
	sendEmailButton.style.color = 'rgb(144, 144, 144)';
	// read input
	var p = personalMessage.value.trim();
	// validate message
	if (p.length == 0) {
		sendEmailRetMessage.innerHTML = 'Message cannot be blank.';
		// enable email button
		sendEmailButton.onclick = function () { sendEmail(); };
		sendEmailButton.style.color = 'white';
		return false;
	}
	// call controller method
	sendEmailRetMessage.innerHTML = 'Processing...';
	$.post(	"/action/entity-send-email",
			{ entityId : entityIdHidden.value, personalMessage : encodeURIComponent(p) }
		  ).always(function (resp) {
			  sendEmailRetMessage.innerHTML = resp;
	});
	// enable send email button
	sendEmailButton.onclick = function () { sendEmail(); };
	sendEmailButton.style.color = 'white';
}

// Show send email box
function showSendEmailBox() {
	// prompt signup if not registered
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	$('body, #sendEmailBox, #modal-background').toggleClass('active');
	sendEmailRetMessage.innerHTML = '';
}

// Function to populate posts
function populateProfilePagePosts(JSONdata, publishFlag) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'profilePagePostsList':
				var r = new Array(), j = -1;
				for (var i = 0; i < value.length; i++) {
					var votes = parseInt(value[i].upVotes)-parseInt(value[i].downVotes);
					r[++j] = '<div class="topChartsElement" style="max-width:520px; padding:6px">';
						r[++j] = '<div style="padding:6px 10px 0px 30px; font-family:\'Text Me One\'; font-size:18px; color:#8A8C8E">';
					    	r[++j] = '<span><b>' + new Date(value[i].updateDate).toLocaleString() +'</b></span>';
						r[++j] = '</div>';
					    r[++j] = '<div style="font-size:18px; font-weight:bold; padding:6px 10px 10px 30px"><a href="/post/' + value[i].postId + '">' + value[i].postTitle + '</a></div>';
						r[++j] = '<div style="padding:0px 30px">';
							r[++j] = '<div onclick="location.href=\'/post/' + value[i].postId + '\'" class="topChartsThumb grow" style="width:460px; height:240px; margin:0 auto; background-image:url(' + value[i].postHeadshot + ')"></div>';
						r[++j] = '</div>';
					    r[++j] = '<div style="padding:3px 10px 3px 30px">' + value[i].postSummary + '</div>';
					    r[++j] = '<div style="font-size:12px; color:#909090; padding:3px 10px 6px 30px">';
					    r[++j] = '<span>'+value[i].views+' views</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>'+votes+' votes</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>'+value[i].comments+' comments</span>';
				    r[++j] = '</div></div>';
				}
				$('#publishedPostsDiv').html(r.join(''));
				document.getElementById('publishedPostsHeader').scrollIntoView();
				window.scrollBy(0,-40);
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
					r[++j] = '<tr><td style="vertical-align:top">';
				    r[++j] = '<div style="font-size:200%; text-align:center">'+value[i].posts+'</div><div style="font-size:11px; color:#909090">posts</div></td>';
					r[++j] = '<td style="vertical-align:top">';
				    r[++j] = '<div style="font-size:200%; text-align:center">'+value[i].comments+'</div><div style="font-size:11px; color:#909090">comments</div></td>';
					r[++j] = '<td style="vertical-align:top"><div style="margin-left:10px">';
				    r[++j] = '<div style="font-weight:bold"><a href="/zone/' + value[i].zoneId + '">' + value[i].zoneName.replace(/"/g,'&#034;') + '</a></div>';
				    if ( value[i].zoneWho == 'E' ) {
					    r[++j] = '<div style="color:#909090">Anybody can post in this zone</div>';
				    } else {
					    r[++j] = '<div style="color:#909090">Only admins can post in this zone</div>';
				    }
				    r[++j] = '<div>' + value[i].zoneDesc + '</div>';
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

// Add Tag to the Blogger
function addBloggerTag() {
	var b = $("#tag-button").html();
	if ( b != 'Please wait...' ) {
		$("#tag-button").html('Please wait...');
		$("#tag-error").text(" ");
		// validate the input tag
		var tag = document.getElementById("tag-input").value.trim();
		if (tag.length == 0) {
			$("#tag-error").text("Please enter a skill.");
			$("#tag-button").html('add');
			return false;
		}
		if(!/^[a-zA-Z0-9\s]*$/.test(tag)) {
			$("#tag-error").text("Alphabets, numbers and the space are the only characters allowed in a skill.");
			$("#tag-button").html('add');
			return false;
		}
		if (tag.length > 50) {
			$("#tag-error").text("A skill cannot have more than 50 characters.");
			$("#tag-button").html('add');
			return false;
		}
		// check if tag already exists
		var id = tag.toLowerCase().replace(/\s/g,'');
		if ( TL.includes(id) ) {
			$("#tag-error").text("Skill already exists.");
			$("#tag-button").html('add');
			return false;
		}
		// validate tag count
		var tagCount = document.getElementById("tag-count").value;
		if ( tagCount >= 30 ) {
			$("#tag-error").text("You can add a maximum of 30 skills.");
			$("#tag-button").html('add');
			return false;
		}
		// increment tag count and add tag to the list
		tagCount++;
		document.getElementById("tag-count").value=tagCount;
		TL.push(id);
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/add-entity-tag",
			data: { entityId : entityIdHidden.value, tag : tag },
			dataType: "json"
		}).always(function (resp) {
			// display new tag on the page
			if ( tagCount == 1 ) {
				$("#blogger-tags").html(
					'<div id="' + id + '" style="display:inline-block; border-radius:14px; border:1px solid #61cf81; padding:4px 10px 4px 10px; margin-top:6px; margin-right:6px; font-family:Arial; font-size:16px; color:#365899">'+
					'<span id="' + id + '-value">' + tag + '</span>&nbsp;&nbsp;&nbsp;'+
					'<a href="javascript:" onclick="deleteBloggerTag(\'' + id + '\')" title="Delete this skill">&nbsp;x&nbsp;</a>'+
					'</div>'
				);
			} else {
				$("#blogger-tags").append(
					'<div id="' + id + '" style="display:inline-block; border-radius:14px; border:1px solid #61cf81; padding:4px 10px 4px 10px; margin-top:6px; margin-right:6px; font-family:Arial; font-size:16px; color:#365899">'+
					'<span id="' + id + '-value">' + tag + '</span>&nbsp;&nbsp;&nbsp;'+
					'<a href="javascript:" onclick="deleteBloggerTag(\'' + id + '\')" title="Delete this skill">&nbsp;x&nbsp;</a>'+
					'</div>'
				);
			}
			document.getElementById("tag-input").value="";
			$("#tag-button").html('add');
		});
	}
}

// Delete Tag from Blogger
function deleteBloggerTag(id) {
	var b = $("#tag-error").html();
	if ( b != 'Please wait...' ) {
		$("#tag-error").html('Please wait...');
		// read tag
		var tag = document.getElementById(id+"-value").textContent.trim();
		// delete from the database
		$.ajax({
			type: "POST",
			url: "/action/delete-entity-tag",
			data: { entityId : entityIdHidden.value, tag : tag },
			dataType: "json"
		}).always(function (resp) {
			// decrement tag count
			var tagCount = document.getElementById("tag-count").value;
			tagCount--;
			document.getElementById("tag-count").value=tagCount;
			// delete tag from the list
			TL.splice( TL.indexOf(id), 1 );
			$("#"+id).text(" ");
			// reset error message
			$("#tag-error").text(" ");
		});
	}
}

// Add Pricing details to Blogger
function addBloggerPricing() {
	var b = $("#pricing-button").html();
	if ( b != 'Please wait...' ) {
		$("#pricing-button").html('Please wait...');
		$("#pricing-error").text(" ");
		// validate input post type
		var postType = document.getElementById("pricing-input-postType").value.trim();
		if (postType.length == 0) {
			$("#pricing-error").text("Please enter post type.");
			$("#pricing-button").html('add');
			return false;
		}
		if (postType.length > 60) {
			$("#pricing-error").text("Post type cannot have more than 60 characters.");
			$("#pricing-button").html('add');
			return false;
		}
		// validate input delivery days
		var deliveryDays = document.getElementById("pricing-input-deliveryDays").value.trim();
		if (deliveryDays.length == 0) {
			$("#pricing-error").text("Please enter delivery days.");
			$("#pricing-button").html('add');
			return false;
		}
		if ( !(deliveryDays % 1 === 0) ){
			$("#pricing-error").text("Invalid delivery days.");
			$("#pricing-button").html('add');
			return false;
		}
		if (deliveryDays < 1) {
			$("#pricing-error").text("Delivery days cannot be less than 1.");
			$("#pricing-button").html('add');
			return false;
		}
		if (deliveryDays > 30) {
			$("#pricing-error").text("Delivery days cannot exceed 30 days.");
			$("#pricing-button").html('add');
			return false;
		}
		// validate input price
		var price = document.getElementById("pricing-input-price").value.trim();
		if (price.length == 0) {
			$("#pricing-error").text("Please enter price.");
			$("#pricing-button").html('add');
			return false;
		}
		if ( !(price % 1 === 0) ) {
			$("#pricing-error").text("Invalid price.");
			$("#pricing-button").html('add');
			return false;
		}
		if (price < 5) {
			$("#pricing-error").text("Price cannnot be less than $5.");
			$("#pricing-button").html('add');
			return false;
		}
		if (price > 10000) {
			$("#pricing-error").text("Price cannnot exceed $10,000.");
			$("#pricing-button").html('add');
			return false;
		}
		// validate pricing count
		var pricingCount = document.getElementById("pricing-count").value;
		if ( pricingCount >= 20 ) {
			$("#pricing-error").text("You can create a maximum of 20 pricing types.");
			$("#pricing-button").html('add');
			return false;
		}
		// increment pricing count and add pricing to the list
		pricingCount++;
		document.getElementById("pricing-count").value=pricingCount;
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/add-blogger-pricing",
			data: { bloggerId : entityIdHidden.value, postType : postType, deliveryDays : deliveryDays, price : price },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#pricing-error").text(value);
						$("#pricing-button").html('add');
						break;

					case 'position':
						// display new pricing on the page
						$("#pricingTable > tbody").append(
							'<tr id="pricingTable-tr' + value + '" style="width:100%; text-align:left">' +
								'<td style="width:65%">' + postType + '</td>' +
								'<td style="width:15%">' + deliveryDays + ' days</td>' +
								'<td style="width:15%">$' + price + '</td>' +
								'<td style="width:5%"><a href="javascript:" onclick="deleteBloggerPricing(\'' + value + '\')" title="Delete this pricing">&nbsp;x&nbsp;</a></td>' +
							'</tr>'
						);
						document.getElementById("pricing-input-postType").value="";
						document.getElementById("pricing-input-deliveryDays").value="";
						document.getElementById("pricing-input-price").value="";
						$("#pricing-button").html('add');
						break;
				}
			});
		});
	}
}

// Delete Pricing Details from Blogger
function deleteBloggerPricing(position) {
	var b = $("#pricing-error").html();
	if ( b != 'Please wait...' ) {
		$("#pricing-error").html('Please wait...');
		// delete from the database
		$.ajax({
			type: "POST",
			url: "/action/delete-blogger-pricing",
			data: { bloggerId : entityIdHidden.value, position : position },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#pricing-error").text(value);
						break;

					case 'SUCCESS':
						// decrement pricing count
						var pricingCount = document.getElementById("pricing-count").value;
						pricingCount--;
						document.getElementById("pricing-count").value=pricingCount;
						$("#pricingTable-tr"+position+" > td").text("");
						$("#pricing-error").text(" ");
						break;
				}
			});
		});
	}
}

// Show Order Blogger Pricing
function showOrderBloggerPricing(position) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	$('body, #orderBloggerPricingBox, #modal-background').toggleClass('active');
	// populate primary page id
	$.ajax({
		type: "POST",
		url: "/action/get-primary-page-id",
		data: { bloggerId : userIdHidden.value },
		dataType: "json"
	}).always(function (resp) {
		$.each( resp, function(key,value) {
			switch (key) {
				case 'ERROR':
					$("#orderBloggerPricingRetMessage").text(value);
					orderBloggerPricingButton.onclick = function () { return false; };
					$('#orderBloggerPricingButton').css("color","#727272");
					$('#orderBloggerPricingButton').css("cursor","default");
					break;

				case 'SUCCESS':
					$("#orderBloggerPricingRetMessage").html('');
					$("#bloggerPricingPageId").text(value);
					$("#bloggerPricingBloggerId").text(document.getElementById("entityIdHidden").value.trim());
					$("#bloggerPricingPostType").text(document.getElementById("pricingTable-postType"+position).innerHTML.trim());
					$("#bloggerPricingDeliveryDays").text(document.getElementById("pricingTable-deliveryDays"+position).innerHTML.trim());
					$("#bloggerPricingPrice").text(document.getElementById("pricingTable-price"+position).innerHTML.trim());
					break;
			}
		});
	});
}

// Order Blogger Pricing
function orderBloggerPricing() {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	var b = $("#orderBloggerPricingRetMessage").html();
	if ( b != 'Please wait...' ) {
		$("#orderBloggerPricingRetMessage").html('Please wait...');
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/order-blogger-pricing",
			data: { pageId : document.getElementById("bloggerPricingPageId").innerHTML.trim(),
					bloggerId : document.getElementById("entityIdHidden").value.trim(),
					postType : document.getElementById("bloggerPricingPostType").innerHTML.trim(),
					deliveryDays : document.getElementById("bloggerPricingDeliveryDays").innerHTML.trim(),
					price : document.getElementById("bloggerPricingPrice").innerHTML.trim()
				  },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#orderBloggerPricingRetMessage").html(value);
						break;

					case 'SUCCESS':
						$("#orderBloggerPricingRetMessage").html(value);
						break;

					case 'orderHandler':
						window.location.href='/user/order-payment/'+value;
						break;
				}
			});
		});
	}
}
