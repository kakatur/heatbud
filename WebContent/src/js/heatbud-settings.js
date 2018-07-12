
/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// JS function to edit First Name
function editFirstName() {
	var a = firstNameDiv.innerHTML;
	originalFirstName.value = a;
	firstNameDiv.innerHTML = "";
	firstNameInput.style.display = "block";
	firstNameInput.value = a;
	editFirstNameDiv.innerHTML = '&nbsp;&nbsp;<a id="saveFirstName" onclick="saveFirstName()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelFirstName" onclick="cancelFirstName()" href="javascript:">Cancel</a>';
}

// JS function to cancel First Name
function cancelFirstName() {
	firstNameDiv.innerHTML = originalFirstName.value;
	firstNameInput.value = "";
	firstNameInput.style.display = "none";
	editFirstNameDiv.innerHTML = '&nbsp;&nbsp;<a id="editFirstNameA" onclick="editFirstName()" href="javascript:">Edit</a>';
	firstNameRetMessage.innerHTML = "";
}

// AJAX call to save First Name
function saveFirstName() {
	// prevent calling this method twice in case of doubleclick
	var b = editFirstNameDiv.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		editFirstNameDiv.innerHTML = "Please wait...";
	}
	// call controller method
	var a = firstNameInput.value.trim();
	$.post(	"/user/update-first-name", { firstName : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			firstNameDiv.innerHTML = a;
			firstNameInput.value = "";
			firstNameInput.style.display = "none";
			editFirstNameDiv.innerHTML = '&nbsp;&nbsp;<a id="editFirstNameA" onclick="editFirstName()" href="javascript:">Edit</a>';
			firstNameRetMessage.innerHTML = "";
		} else {
			editFirstNameDiv.innerHTML = b;
			firstNameRetMessage.innerHTML = resp;
		}
	});
}

// JS function to edit Last Name
function editLastName() {
	var a = lastNameDiv.innerHTML;
	originalLastName.value = a;
	lastNameDiv.innerHTML = "";
	lastNameInput.style.display = "block";
	lastNameInput.value = a;
	editLastNameDiv.innerHTML = '&nbsp;&nbsp;<a id="saveLastName" onclick="saveLastName()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelLastName" onclick="cancelLastName()" href="javascript:">Cancel</a>';
}

// JS function to cancel Last Name
function cancelLastName() {
	lastNameDiv.innerHTML = originalLastName.value;
	lastNameInput.value = "";
	lastNameInput.style.display = "none";
	editLastNameDiv.innerHTML = '&nbsp;&nbsp;<a id="editLastNameA" onclick="editLastName()" href="javascript:">Edit</a>';
	lastNameRetMessage.innerHTML = "";
}

// AJAX call to save Last Name
function saveLastName() {
	// prevent calling this method twice in case of doubleclick
	var b = editLastNameDiv.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		editLastNameDiv.innerHTML = "Please wait...";
	}
	// call controller method
	var a = lastNameInput.value.trim();
	$.post(	"/user/update-last-name", { lastName : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			lastNameDiv.innerHTML = a;
			lastNameInput.value = "";
			lastNameInput.style.display = "none";
			editLastNameDiv.innerHTML = '&nbsp;&nbsp;<a id="editLastNameA" onclick="editLastName()" href="javascript:">Edit</a>';
			lastNameRetMessage.innerHTML = "";
		} else {
			editLastNameDiv.innerHTML = b;
			lastNameRetMessage.innerHTML = resp;
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
	editEmailAddressDiv.innerHTML = '&nbsp;&nbsp;<a id="saveEmailAddress" onclick="saveEmailAddress()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelEmailAddress" onclick="cancelEmailAddress()" href="javascript:">Cancel</a>';
}

// JS function to cancel Email Address
function cancelEmailAddress() {
	emailAddressDiv.innerHTML = originalEmailAddress.value;
	emailAddressInput.value = "";
	emailAddressInput.style.display = "none";
	editEmailAddressDiv.innerHTML = '&nbsp;&nbsp;<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Edit</a>';
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
	$.post(	"/user/update-email-address", { emailAddress : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			emailAddressDiv.innerHTML = a;
			emailAddressInput.value = "";
			emailAddressInput.style.display = "none";
			editEmailAddressDiv.innerHTML = '&nbsp;&nbsp;<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Edit</a>';
			emailAddressRetMessage.innerHTML = "";
		} else {
			editEmailAddressDiv.innerHTML = b;			
			emailAddressRetMessage.innerHTML = resp;
		}
	});
}

// JS function to edit Password
function editPassword() {
	passwordLabel.innerHTML = "Current Password";
	passwordDiv.innerHTML = "";
	passwordInput.style.display = "block";
	editPasswordDiv.innerHTML = '&nbsp;&nbsp;<a id="savePassword" onclick="savePassword()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelPassword" onclick="cancelPassword()" href="javascript:">Cancel</a>';
	newPass1Label.innerHTML = 'New Password';
	newPass1Label.style.display = "block";
	newPass1Input.style.display = "block";
	newPass2Label.style.display = "block";
	newPass2Input.style.display = "block";
}

// JS function to cancel Password
function cancelPassword() {
	passwordLabel.innerHTML = "Password";
	passwordDiv.innerHTML = "******";
	passwordInput.value = "";
	passwordInput.style.display = "none";
	editPasswordDiv.innerHTML = '&nbsp;&nbsp;<a id="editPasswordA" onclick="editPassword()" href="javascript:">Edit</a>';
	newPass1Label.style.display = "none";
	newPass1Input.value = "";
	newPass1Input.style.display = "none";
	newPass2Label.style.display = "none";
	newPass2Input.value = "";
	newPass2Input.style.display = "none";
	passwordRetMessage.innerHTML = '&nbsp;';
}

// AJAX call to save new Password
function savePassword() {
	// validate input
	var c = passwordInput.value.trim();
	var n1 = newPass1Input.value.trim();
	var n2 = newPass2Input.value.trim();
	if ( c.length == 0 ) {
		passwordRetMessage.innerHTML = 'Please enter current password.';
		return false;
	}
	if ( n1.length < 6 ) {
		passwordRetMessage.innerHTML = 'New Password must have six or more characters.';
		return false;
	}
	if ( n2.length == 0 ) {
		passwordRetMessage.innerHTML = 'Please re-enter password.';
		return false;
	}
	if ( n2.length != n1.length ) {
		passwordRetMessage.innerHTML = 'Passwords do not match.';
		return false;
	}
	// prevent calling this method twice in case of doubleclick
	var b = editPasswordDiv.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		editPasswordDiv.innerHTML = "Please wait...";
	}
	// call controller method
	$.post(	"/user/change-password", { currentPassword : c, newPassword : n1 } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			passwordLabel.innerHTML = 'Password';
			passwordDiv.innerHTML = "******";
			passwordInput.value = "";
			passwordInput.style.display = "none";
			editPasswordDiv.innerHTML = '&nbsp;&nbsp;<a id="editPasswordA" onclick="editPassword()" href="javascript:">Edit</a>';
			newPass1Label.innerHTML = '<span style="color:red; font-weight:400">Password has been successfully changed.</span>';
			newPass1Input.value = "";
			newPass1Input.style.display = "none";
			newPass2Label.style.display = "none";
			newPass2Input.value = "";
			newPass2Input.style.display = "none";
			passwordRetMessage.innerHTML = '&nbsp;';
		} else {
			editPasswordDiv.innerHTML = b;
			passwordRetMessage.innerHTML = resp;
		}
	});
}

// JS function to edit Facebook Id
function editFbId() {
	var a = fbIdDiv.innerHTML;
	originalFbId.value = a;
	fbIdDiv.innerHTML = "";
	fbIdInput.style.display = "block";
	fbIdInput.value = a;
	editFbIdDiv.innerHTML = '&nbsp;&nbsp;<a id="saveFbId" onclick="saveFbId()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelFbId" onclick="cancelFbId()" href="javascript:">Cancel</a>';
}

// JS function to cancel Facebook Id
function cancelFbId() {
	fbIdDiv.innerHTML = originalFbId.value;
	fbIdInput.value = "";
	fbIdInput.style.display = "none";
	editFbIdDiv.innerHTML = '&nbsp;&nbsp;<a id="editFbIdA" onclick="editFbId()" href="javascript:">Edit</a>';
	fbIdRetMessage.innerHTML = "";
}

// AJAX call to save Facebook Id
function saveFbId() {
	// prevent calling this method twice in case of doubleclick
	var b = editFbIdDiv.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		editFbIdDiv.innerHTML = "Please wait...";
	}
	// call controller method
	var a = fbIdInput.value.trim();
	$.post(	"/user/update-fb-id", { fbId : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			fbIdDiv.innerHTML = a;
			fbIdInput.value = "";
			fbIdInput.style.display = "none";
			editFbIdDiv.innerHTML = '&nbsp;&nbsp;<a id="editFbIdA" onclick="editFbId()" href="javascript:">Edit</a>';
			fbIdRetMessage.innerHTML = "";
		} else {
			editFbIdDiv.innerHTML = b;
			fbIdRetMessage.innerHTML = resp;
		}
	});
}

// JS function to edit Google Id
function editGoogleId() {
	var a = googleIdDiv.innerHTML;
	originalGoogleId.value = a;
	googleIdDiv.innerHTML = "";
	googleIdInput.style.display = "block";
	googleIdInput.value = a;
	editGoogleIdDiv.innerHTML = '&nbsp;&nbsp;<a id="saveGoogleId" onclick="saveGoogleId()" href="javascript:">Save</a>' +
		'&nbsp;&nbsp;<a id="cancelGoogleId" onclick="cancelGoogleId()" href="javascript:">Cancel</a>';
}

// JS function to cancel Google Id
function cancelGoogleId() {
	googleIdDiv.innerHTML = originalGoogleId.value;
	googleIdInput.value = "";
	googleIdInput.style.display = "none";
	editGoogleIdDiv.innerHTML = '&nbsp;&nbsp;<a id="editGoogleIdA" onclick="editGoogleId()" href="javascript:">Edit</a>';
	googleIdRetMessage.innerHTML = "";
}

// AJAX call to save Google Id
function saveGoogleId() {
	// prevent calling this method twice in case of doubleclick
	var b = editGoogleIdDiv.innerHTML;
	if ( b == "Please wait..." ) {
		return false;
	} else {
		editGoogleIdDiv.innerHTML = "Please wait...";
	}
	// call controller method
	var a = googleIdInput.value.trim();
	$.post(	"/user/update-google-id", { googleId : encodeURIComponent(a) } ).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			googleIdDiv.innerHTML = a;
			googleIdInput.value = "";
			googleIdInput.style.display = "none";
			editGoogleIdDiv.innerHTML = '&nbsp;&nbsp;<a id="editGoogleIdA" onclick="editGoogleId()" href="javascript:">Edit</a>';
			googleIdRetMessage.innerHTML = "";
		} else {
			editGoogleIdDiv.innerHTML = b;
			googleIdRetMessage.innerHTML = resp;
		}
	});
}
