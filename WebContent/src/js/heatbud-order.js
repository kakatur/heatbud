
/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Post Comment
function postComment() {
	// validate comment text
	if (document.getElementById("textComment").value.trim().length == 0) {
		$("#commentsError").text("Please enter your comment.");
		return false;
	}
	var b = $("#commentsError").html();
	if ( b != 'Please wait...' ) {
		$("#commentsError").html('Please wait...');
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/order-post-comment",
			data: { orderId : document.getElementById("orderIdHidden").value.trim(),
					bloggerId : document.getElementById("bloggerIdHidden").value.trim(),
					buyerId : document.getElementById("buyerIdHidden").value.trim(),
					commentText : document.getElementById("textComment").value.trim()
				  },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#commentsError").text(value);
						break;

					case 'SUCCESS':
						$("#commentsError").text(value);
						$("#orderProgress").append(
							'<div style="margin-top:8px; font-weight:bold; color:#707070">just now - ' + userIdHidden.value + ' commented.</div>'+
							'<div style="padding:3px 10px 3px 6px"><pre style="white-space:pre-wrap;">' + document.getElementById("textComment").value.trim() + '</pre></div>'
						);
						$("#textComment").val("");
						break;
				}
			});
		});
	}
}

// Review order
function reviewOrder() {
	var b = $("#commentsError").html();
	if ( b != 'Please wait...' ) {
		$("#commentsError").html('Please wait...');
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/order-review",
			data: { orderId : document.getElementById("orderIdHidden").value.trim(),
					buyerId : document.getElementById("buyerIdHidden").value.trim()
				  },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#commentsError").text(value);
						break;

					case 'SUCCESS':
						$("#commentsError").text(value);
						break;
				}
			});
		});
	}
}

// Close order
function closeOrder() {
	var b = $("#commentsError").html();
	if ( b != 'Please wait...' ) {
		$("#commentsError").html('Please wait...');
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/order-close",
			data: { orderId : document.getElementById("orderIdHidden").value,
					bloggerId : document.getElementById("bloggerIdHidden").value,
					price : document.getElementById("priceHidden").value
				  },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#commentsError").text(value);
						break;

					case 'SUCCESS':
						window.location.href='/user/orders';
						break;
				}
			});
		});
	}
}

// Cancel order
function cancelOrder() {
	var b = $("#commentsError").html();
	if ( b != 'Please wait...' ) {
		$("#commentsError").html('Please wait...');
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/order-cancel",
			data: { orderId : document.getElementById("orderIdHidden").value,
					bloggerId : document.getElementById("bloggerIdHidden").value,
					buyerId : document.getElementById("buyerIdHidden").value,
					price : document.getElementById("priceHidden").value,
					stripeChargeId : document.getElementById("stripeChargeIdHidden").value
				  },
			dataType: "json"
		}).always(function (resp) {
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#commentsError").text(value);
						break;

					case 'SUCCESS':
						window.location.href='/user/orders';
						break;
				}
			});
		});
	}
}
