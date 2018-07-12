
/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// When Search Type changes
function typeChanged() {
	if (document.getElementById("type").value == 'post') {
		$("#terms").attr("placeholder", "Search Post Title, Post Summary and Blogger Name.");
		$("#confineBloggerIdDiv").css({'visibility': 'visible'});
		$("#confineBloggerId").css({'visibility': 'visible'});
	} else if (document.getElementById("type").value == 'zone') {
		$("#terms").attr("placeholder", "Search Zone Name and Description.");
		$("#confineBloggerIdDiv").css({'visibility': 'hidden'});
		confineBloggerId.innerHTML = "Any";
		$("#confineBloggerId").css({'visibility': 'hidden'});
	} else if (document.getElementById("type").value == 'blogger') {
		$("#terms").attr("placeholder", "Search Blogger Name and Blogger About.");
		$("#confineBloggerIdDiv").css({'visibility': 'hidden'});
		confineBloggerId.innerHTML = "Any";
		$("#confineBloggerId").css({'visibility': 'hidden'});
	} else {
		$("#terms").attr("placeholder", "Search Page Name and Page About.");
		$("#confineBloggerIdDiv").css({'visibility': 'hidden'});
		confineBloggerId.innerHTML = "Any";
		$("#confineBloggerId").css({'visibility': 'hidden'});
	}
}

// Submit search form
function submitSearch() {
	if (document.getElementById("terms").value.trim().length == 0) {
		$("#terms").attr("placeholder", "Enter some words to search.");
	} else {
		window.location.href='/search/'+document.getElementById("confineBloggerId").innerHTML+'/'+document.getElementById("type").value+'/'+document.getElementById("terms").value.trim();
	}
}

// Fetch search results for Blogger Box
function submitBloggerSearch () {
	var t = document.getElementById("boxTerms").value.trim();
	if ( t == '' ) {
		document.getElementById('boxSearchError').innerHTML = "Please enter your search terms";
		$('#boxResults').html('<div style="margin-left:10px; margin-top:30px; font-size:12px">&nbsp;</div>');
	} else {
		$('#boxResults').html('<div style="margin-left:10px; margin-top:30px; font-size:12px">Please wait...</div>');
		document.getElementById('boxSearchError').innerHTML = "&nbsp;";
		$.ajax({
			type: "POST",
			url: "/search/get-blogger-box",
			data: { terms : t },
			dataType: "json"
		}).always(function (resp) { populateBloggerBox(resp); });
	}
}

// Populate Blogger Box
function populateBloggerBox (JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				document.getElementById('boxSearchError').innerHTML = value;
				break;

			case 'bloggersList':
				var r = new Array(), j = -1; // define
				BL = new Array(); // reset
				for (var i = 0; i < value.length; i++) {
					r[++j] = '<table id="' + value[i].entityId + '" onclick="selectBlogger(\'' + value[i].entityId + '\')" ondblclick="selectBloggerAndClose(\'' + value[i].entityId + '\')" class="boxedElement" style="width:400px"><tr><td>';
					if ( value[i].profilePhoto != null && value[i].profilePhoto != undefined ) {
						if ( value[i].profilePhoto.lastIndexOf("https://", 0) === 0 ) {
							r[++j] = '<img class="profileThumb" src="' + value[i].profilePhoto + '">';
						} else {
							r[++j] = '<img class="profileThumb" src="/resources/images/def-blogger-photo.jpg">';
						}
					} else {
						r[++j] = '<img class="profileThumb" src="/resources/images/def-blogger-photo.jpg">';
					}
				    r[++j] = '</td><td style="vertical-align:top"><div style="margin-left:20px">';
				    r[++j] = '<div style="font-weight:bold">' + value[i].entityName + '</div>';
				    r[++j] = '<div style="white-space:pre-line; font-size:12px">' + value[i].about + '</div>';
				    r[++j] = '</div></td></tr></table>';
					BL[i] = value[i].entityId;
				}
				if ( BL.length == 0 ) {
					$('#boxResults').html('<div style="margin-left:10px; margin-top:30px; font-size:12px">No Bloggers matching your search.</div>');
				} else {
					$('#boxResults').html(r.join(''));
					selectBlogger(BL[0]);
				}
				break;
		}
	});
}

// Select Blogger and Close (on double click)
function selectBloggerAndClose (bloggerId) {
	confineBloggerId.innerHTML = bloggerId;
	$("#confineBloggerId").css({'visibility': 'visible'});
    document.getElementById("chooseBlogger").checked = true;
    $('body, #bloggerBox, #modal-background').toggleClass('active');
}

// Select Blogger (on single click)
function selectBlogger (bloggerId) {
	confineBloggerId.innerHTML = bloggerId;
	$("#confineBloggerId").css({'visibility': 'visible'});
    document.getElementById("chooseBlogger").checked = true;
	for (var i = 0; i < BL.length; i++) {
	    if ( BL[i] == bloggerId ) {
	    	document.getElementById(BL[i]).style.backgroundColor = "rgb(208, 219, 225)";
	    } else {
	    	document.getElementById(BL[i]).style.backgroundColor = "white";
	    }
	}
}
