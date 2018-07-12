
/************************************************/
/************ jquery ready functions ************/
/************************************************/
$(document).ready(function() {

	// Show create page box
	$(".createPage").click(function() {
		if ( userIdHidden.value == 'NULL' ) {
			$('body, #promptSignupBox, #modal-background').toggleClass('active');
			return false;
		}
		$('body, #createPageBox, #modal-background').toggleClass('active');
	});

});

/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Create page
function createPage() {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	// read & validate page id
	var pi = document.getElementById("createPageIdInput").value.trim();
	if (pi.length == 0) {
		createPageMessage.innerHTML = "Page ID cannot be empty.";
		return false;
	}
	if (pi.length > 30) {
		createPageMessage.innerHTML = "Page ID cannot contain more than 30 characters.";
		return false;
	}
	// read & validate page name
	var pn = document.getElementById("createPageNameInput").value.trim();
	if (pn.length == 0) {
		createPageMessage.innerHTML = "Page Name cannot be empty.";
		return false;
	}
	if (pn.length > 80) {
		createPageMessage.innerHTML = "Page Name cannot contain more than 80 characters.";
		return false;
	}
	// read & validate page email
	var pe = document.getElementById("createPageEmailInput").value.trim();
	if (pe.length == 0) {
		createPageMessage.innerHTML = "Page Email cannot be empty.";
		return false;
	}
	// read & validate page phone
	var pp = document.getElementById("createPagePhoneInput").value.trim();
	// read & validate About section of the Page
	var pa = document.getElementById("createPageAboutInput").value.trim();
	if (pa.length == 0) {
		createPageMessage.innerHTML = "About section cannot be empty.";
		return false;
	}
	// save page data in the database
	$.ajax({
		type: "POST",
		url: "/action/create-page",
		data: { pageId : pi, pageName : pn, pageEmail : pe, pagePhone: pp, about : pa },
		dataType: "json"
	}).always(function (resp) {
		if ( resp.error != "None" ) {
			createPageMessage.innerHTML = resp.error;
			document.getElementById("createPageIdInput").value = resp.pageId;
		} else {
			window.location.href='https://www.heatbud.com/user/pages';
		}
	});
}
