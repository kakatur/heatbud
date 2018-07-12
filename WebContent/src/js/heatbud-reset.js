
$(document).ready(function() {

	$("#resetbutton").click(function(e) {
		if (document.getElementById("username").value.trim().length == 0) {
			$("#error").text("Please enter your Email Address.");
			return false;
		}
		if (document.getElementById("salt").value.trim().length == 0) {
			$("#error").text("Please enter your Verification Code.");
			return false;
		}
		if (document.getElementById("password").value.trim().length < 6) {
			$("#error").text("Password must have six or more characters.");
			return false;
		}
		if (document.getElementById("password2").value.trim().length == 0) {
			$("#error").text("Please re-enter password.");
			return false;
		}
		if (document.getElementById("password2").value != document.getElementById("password").value) {
			$("#error").text("Passwords do not match.");
			return false;
		}
	});

});
