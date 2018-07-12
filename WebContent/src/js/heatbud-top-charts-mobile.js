
/************************************************/
/************ jquery ready functions ************/
/************************************************/

$(document).ready(function() {

	// AJAX call to query next page of Top Charts
	$("#getTopChartsNext").click(function() {
		getTopChartsNext.innerHTML = "WAIT...";
		$.getJSON("/action/get-top-charts-next", {
			generateTopChartsJobPeriod : generateTopChartsJobPeriodHidden.value,
			topChartsName : topChartsNameHidden.value,
			topChartsKeyNextId : topChartsKeyNextIdHidden.value,
			topChartsKeyNextHI : topChartsKeyNextHIHidden.value
		}, function(JSONdata) {
			populateTopCharts(JSONdata);
			getTopChartsNext.innerHTML = "MORE";
			scroll(0,0);
		});
	});

	// AJAX call to query previous page of Top Charts
	$("#getTopChartsPrevious").click(function() {
		getTopChartsPrevious.innerHTML = "WAIT...";
		$.getJSON("/action/get-top-charts-previous", {
			generateTopChartsJobPeriod : generateTopChartsJobPeriodHidden.value,
			topChartsName : topChartsNameHidden.value,
			topChartsKeyPrevId : topChartsKeyPrevIdHidden.value,
			topChartsKeyPrevHI : topChartsKeyPrevHIHidden.value
		}, function(JSONdata) {
			populateTopCharts(JSONdata);
			getTopChartsPrevious.innerHTML = "BACK";
			scroll(0,0);
		});
	});

});

/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Function to populate top charts
function populateTopCharts(JSONdata) {
	$.each( JSONdata, function(key,value) {
		switch (key) {
			case 'ERROR':
				alert(value);
				break;

			case 'topChartsList':
				var r = new Array(), j = -1;
				var n = topChartsNameHidden.value;
				if ( n.indexOf("post") > -1 ) {
					for (var i = 0; i < value.length; i++) {
						r[++j] = '<div style="margin-top:6%; padding:3%; background-color:white">';
					    r[++j] = '<div style="font-size:1.3em; font-weight:bold">';
					    r[++j] = '<a href="/post/' + value[i].postId + '">' + value[i].postTitle + '</a></div>';
					    r[++j] = '<div style="text-align:center; margin-top:2%">';
					    r[++j] = '<a href="/post/' + value[i].postId + '"><img alt="' + value[i].postTitle + '" title="' + value[i].postTitle + '" style="width:90%" src="' + value[i].postHeadshot + '"></a></div>';
					    r[++j] = '<div style="font-size:1.2em; color:#909090; margin-top:2%">by <a href="/' + value[i].bloggerId + '">' + value[i].bloggerName + '</a></div>';
					    r[++j] = '<div style="font-size:1em; color:#909090; margin-top:2%">on ' + new Date(value[i].updateDate).toLocaleString() + '</div>';
					    r[++j] = '<div style="font-size:1.2em; color:#909090; margin-top:2%">zone <a href="/zone/' + value[i].zoneId + '">' + value[i].zoneName.replace(/"/g,'&#034;') + '</a></div>';
					    r[++j] = '<div style="font-size:1.2em; margin-top:2%" >' + value[i].postSummary + '</div></div>';
					}
				} else {
					for (var i = 0; i < value.length; i++) {
						r[++j] = '<div style="margin-top:6%; padding:3%; background-color:white">';
						r[++j] = '<div style="font-size:1.3em; font-weight:bold">';
					    r[++j] = '<a href="/' + value[i].entityId + '">' + value[i].entityName + '</a>';
					    r[++j] = '</div><div style="text-align:center; margin-top:2%">';
						if ( value[i].profilePhoto != null && value[i].profilePhoto != undefined ) {
							if ( value[i].profilePhoto.lastIndexOf("https://", 0) === 0 ) {
								r[++j] = '<a href="/' + value[i].entityId + '"><img alt="' + value[i].entityName + '" style="width:90%" src="' + value[i].profilePhoto + '"></a>';
							} else {
								if ( n.indexOf("blogger") > -1 ) {
									r[++j] = '<a href="/' + value[i].entityId + '"><img alt="' + value[i].entityName + '" style="width:90%" src="/resources/images/def-blogger-photo.jpg"></a>';
								} else {
									r[++j] = '<a href="/' + value[i].entityId + '"><img alt="' + value[i].entityName + '" style="width:90%" src="/resources/images/def-page-photo.jpg"></a>';
								}
							}
						} else {
							if ( n.indexOf("blogger") > -1 ) {
								r[++j] = '<a href="/' + value[i].entityId + '"><img alt="' + value[i].entityName + '" style="width:90%" src="/resources/images/def-blogger-photo.jpg"></a>';
							} else {
								r[++j] = '<a href="/' + value[i].entityId + '"><img alt="' + value[i].entityName + '" style="width:90%" src="/resources/images/def-page-photo.jpg"></a>';
							}
						}
					    r[++j] = '</div><div style="font-size:1.2em; margin-top:2%">' + value[i].about + '</div>';
					    r[++j] = '<div style="font-size:1.2em; color:#909090; margin-top:2%">';
					    r[++j] = '<span>' + value[i].posts + ' posts</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>' + value[i].votes + ' votes</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>' + value[i].comments + ' comments</span>';
					    r[++j] = '</div></div>';
					}
				}
				$('#topChartsDiv').html(r.join(''));
				break;

			case 'topChartsKeyPrevId':
				topChartsKeyPrevIdHidden.value = value;
				if (value == 'NULL') {
					$('#getTopChartsPreviousDiv').css("visibility","hidden");
				} else {
					$('#getTopChartsPreviousDiv').css("visibility","visible");
				}
				break;

			case 'topChartsKeyPrevHI':
				topChartsKeyPrevHIHidden.value = value;
				break;

			case 'topChartsKeyNextId':
				topChartsKeyNextIdHidden.value = value;
				if (value == 'NULL') {
					$('#getTopChartsNextDiv').css("visibility","hidden");
				} else {
					$('#getTopChartsNextDiv').css("visibility","visible");
				}
				break;

			case 'topChartsKeyNextHI':
				topChartsKeyNextHIHidden.value = value;
				break;
		}
	});
}
