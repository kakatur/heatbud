
/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Submit market place search form
function submitMPSearch() {

	// read input and run validations
	// pageId
	var pageId = document.getElementById("bloggerPricingPageId").innerHTML.trim();
	// keyword
	var keyword = document.getElementById("keyword").value.trim();
	if (keyword.length > 60) {
		$("#mp-error").text("Keyword cannot have more than 60 characters.");
		return false;
	}
	// country
	var country = document.getElementById("country").value;
	// tag
	var tag = document.getElementById("tag").value.trim();
	if (tag.length > 60) {
		$("#mp-error").text("Skill cannot have more than 60 characters.");
		return false;
	}
	// ddFrom
	var ddFrom = document.getElementById("ddFrom").value.trim();
	if (ddFrom.length > 0) {
		if ( !(ddFrom % 1 === 0) ){
			$("#mp-error").text("Delivery Days must be a number.");
			return false;
		}
	}
	// ddTo
	var ddTo = document.getElementById("ddTo").value.trim();
	if (ddTo.length > 0) {
		if ( !(ddTo % 1 === 0) ){
			$("#mp-error").text("Delivery Days must be a number.");
			return false;
		}
	}
	// priceFrom
	var priceFrom = document.getElementById("priceFrom").value.trim();
	if (priceFrom.length > 0) {
		if ( !(priceFrom % 1 === 0) ){
			$("#mp-error").text("Price must be a number.");
			return false;
		}
	}
	// priceTo
	var priceTo = document.getElementById("priceTo").value.trim();
	if (priceTo.length > 0) {
		if ( !(priceTo % 1 === 0) ){
			$("#mp-error").text("Price must be a number.");
			return false;
		}
	}

	// submit form
	var form = document.createElement("form");
	form.setAttribute("method", "POST");
	form.setAttribute("action", "/user/marketplace");
	form.setAttribute("target", "_self");

	var f = document.createElement("input");
	f.setAttribute("name", "pageId");
	f.setAttribute("value", pageId);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "keyword");
	f.setAttribute("value", keyword);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "country");
	f.setAttribute("value", country);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "tag");
	f.setAttribute("value", tag);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "ddFrom");
	f.setAttribute("value", ddFrom);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "ddTo");
	f.setAttribute("value", ddTo);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "priceFrom");
	f.setAttribute("value", priceFrom);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "priceTo");
	f.setAttribute("value", priceTo);
	form.appendChild(f);

	var f = document.createElement("input");
	f.setAttribute("name", "sortCriteria");
	f.setAttribute("value", document.getElementById("sortCriteria").value);
	form.appendChild(f);

	document.body.appendChild(form);
    form.submit();
}

// Show Order Blogger Pricing
function showOrderMPPricing(position) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	$('body, #orderBloggerPricingBox, #modal-background').toggleClass('active');
	// populate blogger pricing box
	$("#bloggerPricingBloggerId").html(MP[position]['bloggerId']);
	$("#bloggerPricingPostType").html(MP[position]['postType']);
	$("#bloggerPricingDeliveryDays").html(MP[position]['deliveryDays']);
	$("#bloggerPricingPrice").html(MP[position]['price']);
}

// Order Blogger Pricing
function orderBloggerPricing() {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	var b = $("#orderBloggerPricingRetMessage").html();
	if ( b != 'Please wait...' ) {
		$("#orderBloggerPricingRetMessage").html('Please wait...');
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/order-blogger-pricing",
			data: { pageId : document.getElementById("bloggerPricingPageId").innerHTML.trim(),
					bloggerId : document.getElementById("bloggerPricingBloggerId").innerHTML.trim(),
					postType : document.getElementById("bloggerPricingPostType").innerHTML.trim(),
					deliveryDays : document.getElementById("bloggerPricingDeliveryDays").innerHTML.trim(),
					price : document.getElementById("bloggerPricingPrice").innerHTML.trim()
				  },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#orderBloggerPricingRetMessage").html(value);
						break;

					case 'SUCCESS':
						$("#orderBloggerPricingRetMessage").html(value);
						break;

					case 'orderHandler':
						window.location.href='/user/order-payment/'+value;
						break;
				}
			});
		});
	}
}
