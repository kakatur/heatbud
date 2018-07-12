
// Show Page Box
function showPageBox(page) {
	if ( userIdHidden.value == 'NULL' ) {
		$('body, #promptSignupBox, #modal-background').toggleClass('active');
		return false;
	}
	$('body, #pageBox, #modal-background').toggleClass('active');
	$.ajax({
		type: "POST",
		url: "/user/get-page-box",
		data: { bloggerId : userIdHidden.value },
		dataType: "json"
	}).always(function (JSONdata) {
		$.each( JSONdata, function(key,value) {
			switch (key) {
				case 'ERROR':
					alert(value);
					break;

				case 'pagesList':
					var r = new Array(), j = -1; // define
					PL = new Array(); // reset
					for (var i = 0; i < value.length; i++) {
						r[++j] = '<table id="' + value[i].pageId + '" onclick="selectPage(\'' + value[i].pageId + '\',\'' + page + '\')" ondblclick="selectPageAndClose(\'' + value[i].pageId + '\',\'' + page + '\')" style="width:400px"><tr><td>';
					    r[++j] = '</td><td style="vertical-align:top"><div style="margin-left:20px">';
					    r[++j] = '<div style="font-weight:bold; cursor:pointer">' + value[i].pageName + '</div>';
					    r[++j] = '</div></td></tr></table>';
						PL[i] = value[i].pageId;
					}
					if ( PL.length == 0 ) {
						$('#boxResults').html('<div style="margin-left:10px; margin-top:30px; font-size:12px">No Pages.</div>');
					} else {
						$('#boxResults').html(r.join(''));
						selectPage(PL[0], page);
					}
					break;
			}
		});
	});
}

// Select Page and Close (on double click)
function selectPageAndClose (pageId, page) {
	if ( page == 'mrl' ) {
		pageIdSelected.innerHTML = ' ' + pageId + ' ';
		document.getElementById("pageIdRadio").checked = true;
	} else {
		bloggerPricingPageId.innerHTML = ' ' + pageId + ' ';
	}
	$('body, #pageBox, #modal-background').toggleClass('active');
}

// Select Page (on single click)
function selectPage (pageId, page) {
	if ( page == 'mrl' ) {
		pageIdSelected.innerHTML = ' ' + pageId + ' ';
		document.getElementById("pageIdRadio").checked = true;
	} else {
		bloggerPricingPageId.innerHTML = ' ' + pageId + ' ';
	}
	for (var i = 0; i < PL.length; i++) {
	    if ( PL[i] == pageId ) {
	    	document.getElementById(PL[i]).style.backgroundColor = "rgb(208, 219, 225)";
	    } else {
	    	document.getElementById(PL[i]).style.backgroundColor = "white";
	    }
	}
}
