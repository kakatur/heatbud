$(document).ready(function(){$("#dropAccount").click(function(){var a=dropAccount.innerHTML;if(a=="Please wait..."){return false}else{dropAccount.innerHTML="Please wait..."}if(confirm("Are you sure you want to drop your account? This action is irriversible.")){$.post("/user/drop").always(function(b){if(b=="SUCCESS"){alert("We received the request to drop your account and we will process it shortly. You will now be logged out of Heatbud.");window.location.href='/<c:url value="/do/logout"/>'}else{dropAccount.innerHTML=a;dropAccountRetMessage.innerHTML=b}})}else{dropAccount.innerHTML=a}})});