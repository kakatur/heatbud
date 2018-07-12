
$(document).ready(function() {

	$("#loginbutton").click(function(e) {
		if (document.getElementById("j_username").value.trim().length == 0) {
			$("#signinerror").text("Please enter your Email Address.");
			return false;
		}
		if (document.getElementById("j_password").value.trim().length == 0) {
			$("#signinerror").text("Please enter your Password.");
			return false;
		}
	});

	$("#signupbutton").click(function(e) {
		if (document.getElementById("signupSource").value == 'facebook') {
			$("#signuperror").text("Please wait while we fetch your profile and cover images from facebook...");
		} else {
			$("#signuperror").text("Please wait...");
		}
		if (!document.getElementById("privacy").checked) {
			$("#signuperror").text("Do you agree to Heatbud's Terms?");
			return false;
		}
		if (document.getElementById("signupEmail").value.trim().length == 0) {
			$("#signuperror").text("Please enter your Email Address.");
			return false;
		}
		if (document.getElementById("email2").value.trim().length == 0) {
			$("#signuperror").text("Please re-enter email.");
			return false;
		}
		if (document.getElementById("email2").value != document.getElementById("signupEmail").value) {
			$("#signuperror").text("Emails do not match.");
			return false;
		}
		if (document.getElementById("signupFirstName").value.trim().length == 0) {
			$("#signuperror").text("Please enter your First Name.");
			return false;
		}
		if (document.getElementById("signupLastName").value.trim().length == 0) {
			$("#signuperror").text("Please enter your Last Name.");
			return false;
		}
		if (document.getElementById("password").value.trim().length < 6) {
			$("#signuperror").text("Password must have six or more characters.");
			return false;
		}
	});

});