
/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Subscribe to the newsletter
function subscribe() {
	if (document.getElementById("emailAddress").value.trim().length == 0) {
		$("#subscribeError").text("Enter your email address.");
	} else {
		// prevent calling this method twice in case of doubleclick
		var z = subscribeButton.innerHTML;
		if ( z == "Please wait..." ) {
			return false;
		} else {
			document.getElementById("subscribeButton").href = function () { return false; };
			subscribeButton.innerHTML = "Please wait...";
		}
		$.ajax({
			type: "POST",
			url: "/action/subscribe",
			data: { username : document.getElementById("emailAddress").value.trim() },
			dataType: "text"
		}).always(function (resp) {
   			if ( resp != "SUCCESS" ) {
   				$("#subscribeError").text(resp);
   			}
   			subscribeButton.innerHTML = "SUBSCRIBE";
			document.getElementById("subscribeButton").href = function () { javascript:subscribe(); };
	   	});
	}
}
