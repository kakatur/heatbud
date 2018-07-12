
$(document).ready(function() {

	$("#verifyButton").click(function(e) {
		if (document.getElementById("username").value.trim().length == 0) {
			$("#errors").text("Please enter your Email Address.");
			return false;
		}
		if (document.getElementById("salt").value.trim().length == 0) {
			$("#errors").text("Please enter verification code.");
			return false;
		}
	});

});
