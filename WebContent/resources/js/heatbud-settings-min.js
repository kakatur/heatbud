function editFirstName(){var b=firstNameDiv.innerHTML;originalFirstName.value=b;firstNameDiv.innerHTML="";firstNameInput.style.display="block";firstNameInput.value=b;editFirstNameDiv.innerHTML='&nbsp;&nbsp;<a id="saveFirstName" onclick="saveFirstName()" href="javascript:">Save</a>&nbsp;&nbsp;<a id="cancelFirstName" onclick="cancelFirstName()" href="javascript:">Cancel</a>'}function cancelFirstName(){firstNameDiv.innerHTML=originalFirstName.value;firstNameInput.value="";firstNameInput.style.display="none";editFirstNameDiv.innerHTML='&nbsp;&nbsp;<a id="editFirstNameA" onclick="editFirstName()" href="javascript:">Edit</a>';firstNameRetMessage.innerHTML=""}function saveFirstName(){var c=editFirstNameDiv.innerHTML;if(c=="Please wait..."){return false}else{editFirstNameDiv.innerHTML="Please wait..."}var d=firstNameInput.value.trim();$.post("/user/update-first-name",{firstName:encodeURIComponent(d)}).always(function(a){if(a=="SUCCESS"){firstNameDiv.innerHTML=d;firstNameInput.value="";firstNameInput.style.display="none";editFirstNameDiv.innerHTML='&nbsp;&nbsp;<a id="editFirstNameA" onclick="editFirstName()" href="javascript:">Edit</a>';firstNameRetMessage.innerHTML=""}else{editFirstNameDiv.innerHTML=c;firstNameRetMessage.innerHTML=a}})}function editLastName(){var b=lastNameDiv.innerHTML;originalLastName.value=b;lastNameDiv.innerHTML="";lastNameInput.style.display="block";lastNameInput.value=b;editLastNameDiv.innerHTML='&nbsp;&nbsp;<a id="saveLastName" onclick="saveLastName()" href="javascript:">Save</a>&nbsp;&nbsp;<a id="cancelLastName" onclick="cancelLastName()" href="javascript:">Cancel</a>'}function cancelLastName(){lastNameDiv.innerHTML=originalLastName.value;lastNameInput.value="";lastNameInput.style.display="none";editLastNameDiv.innerHTML='&nbsp;&nbsp;<a id="editLastNameA" onclick="editLastName()" href="javascript:">Edit</a>';lastNameRetMessage.innerHTML=""}function saveLastName(){var c=editLastNameDiv.innerHTML;if(c=="Please wait..."){return false}else{editLastNameDiv.innerHTML="Please wait..."}var d=lastNameInput.value.trim();$.post("/user/update-last-name",{lastName:encodeURIComponent(d)}).always(function(a){if(a=="SUCCESS"){lastNameDiv.innerHTML=d;lastNameInput.value="";lastNameInput.style.display="none";editLastNameDiv.innerHTML='&nbsp;&nbsp;<a id="editLastNameA" onclick="editLastName()" href="javascript:">Edit</a>';lastNameRetMessage.innerHTML=""}else{editLastNameDiv.innerHTML=c;lastNameRetMessage.innerHTML=a}})}function editEmailAddress(){var b=emailAddressDiv.innerHTML;originalEmailAddress.value=b;emailAddressDiv.innerHTML="";emailAddressInput.style.display="block";emailAddressInput.value=b;editEmailAddressDiv.innerHTML='&nbsp;&nbsp;<a id="saveEmailAddress" onclick="saveEmailAddress()" href="javascript:">Save</a>&nbsp;&nbsp;<a id="cancelEmailAddress" onclick="cancelEmailAddress()" href="javascript:">Cancel</a>'}function cancelEmailAddress(){emailAddressDiv.innerHTML=originalEmailAddress.value;emailAddressInput.value="";emailAddressInput.style.display="none";editEmailAddressDiv.innerHTML='&nbsp;&nbsp;<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Edit</a>';emailAddressRetMessage.innerHTML=""}function saveEmailAddress(){var c=editEmailAddressDiv.innerHTML;if(c=="Please wait..."){return false}else{editEmailAddressDiv.innerHTML="Please wait..."}var d=emailAddressInput.value.trim();$.post("/user/update-email-address",{emailAddress:encodeURIComponent(d)}).always(function(a){if(a=="SUCCESS"){emailAddressDiv.innerHTML=d;emailAddressInput.value="";emailAddressInput.style.display="none";editEmailAddressDiv.innerHTML='&nbsp;&nbsp;<a id="editEmailAddressA" onclick="editEmailAddress()" href="javascript:">Edit</a>';emailAddressRetMessage.innerHTML=""}else{editEmailAddressDiv.innerHTML=c;emailAddressRetMessage.innerHTML=a}})}function editPassword(){passwordLabel.innerHTML="Current Password";passwordDiv.innerHTML="";passwordInput.style.display="block";editPasswordDiv.innerHTML='&nbsp;&nbsp;<a id="savePassword" onclick="savePassword()" href="javascript:">Save</a>&nbsp;&nbsp;<a id="cancelPassword" onclick="cancelPassword()" href="javascript:">Cancel</a>';newPass1Label.innerHTML="New Password";newPass1Label.style.display="block";newPass1Input.style.display="block";newPass2Label.style.display="block";newPass2Input.style.display="block"}function cancelPassword(){passwordLabel.innerHTML="Password";passwordDiv.innerHTML="******";passwordInput.value="";passwordInput.style.display="none";editPasswordDiv.innerHTML='&nbsp;&nbsp;<a id="editPasswordA" onclick="editPassword()" href="javascript:">Edit</a>';newPass1Label.style.display="none";newPass1Input.value="";newPass1Input.style.display="none";newPass2Label.style.display="none";newPass2Input.value="";newPass2Input.style.display="none";passwordRetMessage.innerHTML="&nbsp;"}function savePassword(){var f=passwordInput.value.trim();var e=newPass1Input.value.trim();var d=newPass2Input.value.trim();if(f.length==0){passwordRetMessage.innerHTML="Please enter current password.";return false}if(e.length<6){passwordRetMessage.innerHTML="New Password must have six or more characters.";return false}if(d.length==0){passwordRetMessage.innerHTML="Please re-enter password.";return false}if(d.length!=e.length){passwordRetMessage.innerHTML="Passwords do not match.";return false}var a=editPasswordDiv.innerHTML;if(a=="Please wait..."){return false}else{editPasswordDiv.innerHTML="Please wait..."}$.post("/user/change-password",{currentPassword:f,newPassword:e}).always(function(b){if(b=="SUCCESS"){passwordLabel.innerHTML="Password";passwordDiv.innerHTML="******";passwordInput.value="";passwordInput.style.display="none";editPasswordDiv.innerHTML='&nbsp;&nbsp;<a id="editPasswordA" onclick="editPassword()" href="javascript:">Edit</a>';newPass1Label.innerHTML='<span style="color:red; font-weight:400">Password has been successfully changed.</span>';newPass1Input.value="";newPass1Input.style.display="none";newPass2Label.style.display="none";newPass2Input.value="";newPass2Input.style.display="none";passwordRetMessage.innerHTML="&nbsp;"}else{editPasswordDiv.innerHTML=a;passwordRetMessage.innerHTML=b}})}function editFbId(){var b=fbIdDiv.innerHTML;originalFbId.value=b;fbIdDiv.innerHTML="";fbIdInput.style.display="block";fbIdInput.value=b;editFbIdDiv.innerHTML='&nbsp;&nbsp;<a id="saveFbId" onclick="saveFbId()" href="javascript:">Save</a>&nbsp;&nbsp;<a id="cancelFbId" onclick="cancelFbId()" href="javascript:">Cancel</a>'}function cancelFbId(){fbIdDiv.innerHTML=originalFbId.value;fbIdInput.value="";fbIdInput.style.display="none";editFbIdDiv.innerHTML='&nbsp;&nbsp;<a id="editFbIdA" onclick="editFbId()" href="javascript:">Edit</a>';fbIdRetMessage.innerHTML=""}function saveFbId(){var c=editFbIdDiv.innerHTML;if(c=="Please wait..."){return false}else{editFbIdDiv.innerHTML="Please wait..."}var d=fbIdInput.value.trim();$.post("/user/update-fb-id",{fbId:encodeURIComponent(d)}).always(function(a){if(a=="SUCCESS"){fbIdDiv.innerHTML=d;fbIdInput.value="";fbIdInput.style.display="none";editFbIdDiv.innerHTML='&nbsp;&nbsp;<a id="editFbIdA" onclick="editFbId()" href="javascript:">Edit</a>';fbIdRetMessage.innerHTML=""}else{editFbIdDiv.innerHTML=c;fbIdRetMessage.innerHTML=a}})}function editGoogleId(){var b=googleIdDiv.innerHTML;originalGoogleId.value=b;googleIdDiv.innerHTML="";googleIdInput.style.display="block";googleIdInput.value=b;editGoogleIdDiv.innerHTML='&nbsp;&nbsp;<a id="saveGoogleId" onclick="saveGoogleId()" href="javascript:">Save</a>&nbsp;&nbsp;<a id="cancelGoogleId" onclick="cancelGoogleId()" href="javascript:">Cancel</a>'}function cancelGoogleId(){googleIdDiv.innerHTML=originalGoogleId.value;googleIdInput.value="";googleIdInput.style.display="none";editGoogleIdDiv.innerHTML='&nbsp;&nbsp;<a id="editGoogleIdA" onclick="editGoogleId()" href="javascript:">Edit</a>';googleIdRetMessage.innerHTML=""}function saveGoogleId(){var c=editGoogleIdDiv.innerHTML;if(c=="Please wait..."){return false}else{editGoogleIdDiv.innerHTML="Please wait..."}var d=googleIdInput.value.trim();$.post("/user/update-google-id",{googleId:encodeURIComponent(d)}).always(function(a){if(a=="SUCCESS"){googleIdDiv.innerHTML=d;googleIdInput.value="";googleIdInput.style.display="none";editGoogleIdDiv.innerHTML='&nbsp;&nbsp;<a id="editGoogleIdA" onclick="editGoogleId()" href="javascript:">Edit</a>';googleIdRetMessage.innerHTML=""}else{editGoogleIdDiv.innerHTML=c;googleIdRetMessage.innerHTML=a}})};