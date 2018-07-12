
/**********************************************/
/************ javascript functions ************/
/**********************************************/

// cancel payment
function cancelPayment () {
	var b = $("#page-payment-error").html();
	if ( b != 'Please wait...' ) {
		$("#page-payment-error").html('Please wait...');
		var ph = document.getElementById('paymentHandlerHidden').value.trim();
		$.ajax({
			type: "POST",
			url: "/action/page-payment-delete",
			data: { paymentHandler : ph },
			dataType: "text"
		}).always(function (resp) {
			if ( resp == "NULL" ) {
				window.location.href='/user/pages';
			} else {
				window.location.href='/user/page-payments/'+resp;
			}
		});
	}
}
