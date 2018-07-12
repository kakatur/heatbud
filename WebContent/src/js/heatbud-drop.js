
/************************************************/
/************ jquery ready functions ************/
/************************************************/

$(document).ready(function() {

	// AJAX call to drop account
	$("#dropAccount").click(function() {
		// prevent calling this method twice in case of doubleclick
		var b = dropAccount.innerHTML;
		if ( b == "Please wait..." ) {
			return false;
		} else {
			dropAccount.innerHTML = "Please wait...";
		}
		// call controller method
		if (confirm("Are you sure you want to drop your account? This action is irriversible.")) {
			$.post(	"/user/drop" ).always(function (resp) {
				// process success or error
				if ( resp == 'SUCCESS' ) {
					alert("We received the request to drop your account and we will process it shortly. You will now be logged out of Heatbud.");
					window.location.href='/<c:url value="/do/logout"/>';
				} else {
					dropAccount.innerHTML = b;
					dropAccountRetMessage.innerHTML = resp;
				}
			});
		} else {
			dropAccount.innerHTML = b;
		}
	});

});
