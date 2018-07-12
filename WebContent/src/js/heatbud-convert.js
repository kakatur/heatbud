
function submit() {
	if (document.getElementById("bloggerUsername").value.trim().length == 0) {
		$("#convertError").text("Please enter your Email Address.");
		return false;
	}
	if (document.getElementById("bloggerFirstname").value.trim().length == 0) {
		$("#convertError").text("Please enter your First Name.");
		return false;
	}
	if (document.getElementById("bloggerLastname").value.trim().length == 0) {
		$("#convertError").text("Please enter your Last Name.");
		return false;
	}
	if (document.getElementById("bloggerURL").value.trim().length == 0) {
		$("#convertError").text("Please enter your preferred Blog Site URL.");
		return false;
	}
	if(!/^[a-z0-9-]+$/i.test(document.getElementById("bloggerURL").value.trim())) {
		$("#convertError").text("Alpha numerics and hypen are the only characters allowed in the Blog Site URL.");
		return false;
	}
	if(!/^[a-z0-9]+[a-z0-9-]+[a-z0-9]+$/i.test(document.getElementById("bloggerURL").value.trim())) {
		$("#convertError").text("Blog Site URL can't begin or end with a hyphen.");
		return false;
	}
   	$.post(	"/user/convert-submit",
		{	bloggerUsername		: bloggerUsername.value,
  			bloggerFirstname	: bloggerFirstname.value,
  			bloggerLastname		: bloggerLastname.value,
   			bloggerURL			: bloggerURL.value,
   			bloggerAbout		: encodeURIComponent(bloggerAbout.value),
   			bloggerContact		: encodeURIComponent(bloggerContact.value),
   			bloggerFbId			: bloggerFbId.value,
   			bloggerGoogleId		: bloggerGoogleId.value,
   			pageName			: pageName.value,
   			pageAbout			: encodeURIComponent(pageAbout.value),
   			pageContact			: encodeURIComponent(pageContact.value),
   			pageFbId			: pageFbId.value,
   			pageGoogleId		: pageGoogleId.value
   		}).always(function () {
			// process success or error
			if ( resp == 'SUCCESS' ) {
				alert("You will now be logged out of Heatbud. Please login with your new personal email address and current password.");
				window.location.href='/<c:url value="/do/logout"/>';
			} else {
				convertError.innerHTML = resp;
			}
   		}
   	);
}
