
$(document).ready(function() {

	$("#forgotbutton").click(function(e) {
		if (document.getElementById("username").value.trim().length == 0) {
			$("#errors").text("Please enter your Email Address.");
			return false;
		}
	});

});
