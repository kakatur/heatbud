
/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Apply Coupon
function applyCoupon() {
	$("#page-payment-error").html("");
	var coupon = document.getElementById("page-payment-coupon").value.trim();
	if ( coupon == "NC19" && document.getElementById("basic-monthly").checked ) {
		$("#page-payment-amount").html("19");
		$("#page-payment-error").html("Coupon has been successfully applied.");
	} else if ( coupon == "NC190" && document.getElementById("basic-yearly").checked ) {
		$("#page-payment-amount").html("190");
		$("#page-payment-error").html("Coupon has been successfully applied.");
	} else if ( coupon == "NC99" && document.getElementById("premium-monthly").checked ) {
		$("#page-payment-amount").html("99");
		$("#page-payment-error").html("Coupon has been successfully applied.");
	} else if ( coupon == "NC199" && document.getElementById("premium-monthly").checked ) {
		$("#page-payment-amount").html("199");
		$("#page-payment-error").html("Coupon has been successfully applied.");
	} else {
		$("#page-payment-error").html("This coupon is not applicable for the product selected.");
	}
}

// Make a one-time payment
function makeOneTime() {
	var b = $("#page-payment-error").html();
	if ( b != 'Please wait...' ) {
		$("#page-payment-error").html('Please wait...');
		var pid = document.getElementById('pageIdHidden').value.trim();
		var p = $('input[type=radio][name=product-type]:checked').attr('id');
		var a = document.getElementById('page-payment-amount').innerHTML;
		var c = document.getElementById("page-payment-coupon").value.trim();
		$.ajax({
			type: "POST",
			url: "/action/make-one-time-page-payment",
			data: { pageId : pid, productType : p, amount : a, coupon : c},
			dataType: "json"
		}).always(function (resp) {
			$("#page-payment-error").html('');
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#page-payment-error").html(value);
						break;

					case 'paymentHandler':
						window.location.href='/user/page-payment/'+value;
						break;
				}
			});
		});
	}
}

// Setup recurring payments
function setupRecurring() {
	var b = $("#page-payment-error").html();
	if ( b != 'Please wait...' ) {
		$("#page-payment-error").html('Please wait...');
		var pid = document.getElementById('pageIdHidden').value.trim();
		var p = $('input[type=radio][name=product-type]:checked').attr('id');
		var a = document.getElementById('page-payment-amount').innerHTML;
		var c = document.getElementById("page-payment-coupon").value.trim();
		$.ajax({
			type: "POST",
			url: "/action/setup-recurring-page-payments",
			data: { pageId : pid, productType : p, amount : a, coupon : c},
			dataType: "json"
		}).always(function (resp) {
			$("#page-payment-error").html('');
			$.each( resp, function(key,value) {
				switch (key) {
					case 'ERROR':
						$("#page-payment-error").html(value);
						break;

					case 'paymentHandler':
						window.location.href='/user/page-payment/'+value;
						break;
				}
			});
		});
	}
}

