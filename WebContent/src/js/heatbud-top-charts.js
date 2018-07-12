
/************************************************/
/************ jquery ready functions ************/
/************************************************/

$(document).ready(function() {

	// Scroll to the charts
    $(".scroll").click(function(event) {
        event.preventDefault();
        $('html,body').animate( { scrollTop:$(this.hash).offset().top } , 1000);
	});

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
			document.getElementById('topChartsDiv').scrollIntoView();
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
			document.getElementById('topChartsDiv').scrollIntoView();
		});
	});

});

/**********************************************/
/************ js/ jquery functions ************/
/**********************************************/

// Submit search form
function submitSearch() {
	if ( document.getElementById("terms").value.trim().length > 0 ) {
		window.location.href='/search/post/'+document.getElementById("terms").value.trim();
	}
}

// Switch the Chart
function switchChart(cname) {
	window.location.href='/top/'+cname;
}

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
						r[++j] = '<div class="topChartsElement" style="float:left">';
						r[++j] = '<div style="font-size:18px; font-weight:bold; padding:10px 20px">';
					    r[++j] = '<a href="/post/' + value[i].postId + '">' + value[i].postTitle + '</a></div>';
						r[++j] = '<div onclick="location.href=\'/post/' + value[i].postId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'' + value[i].postHeadshot + '\')"></div>';
						r[++j] = '<div style="font-size:15px; padding:10px 20px">';
						r[++j] = '<span style="font-size:13px; color:rgb(144, 144, 144)"> By </span>';
						r[++j] = '<span><a href="/' + value[i].bloggerId + '">' + value[i].bloggerName + '</a></span>';
						r[++j] = '<span style="font-size:13px; color:rgb(144, 144, 144)"> Zone </span>';
						r[++j] = '<span><a href="/zone/' + value[i].zoneId + '">' + value[i].zoneName.replace(/"/g,'&#034;') + '</a></span></div>';
						if ( n.indexOf("just") > -1 ) {
						    r[++j] = '<div style="padding:3px 20px; color:#8A8C8E">' + new Date(value[i].updateDate).toLocaleString() + '</div>';
						}
					    r[++j] = '<div style="padding:3px 20px">' + value[i].postSummary + '"</div>';
						r[++j] = '<div style="padding:5px 20px"><img alt="Overall Heat Index" title="Overall Heat Index" style="width:14px; height:18px; border:none" src="/resources/images/favicon.ico"/>';
						r[++j] = '<span title="Overall Heat Index" style="color:#7A7C7E; font-size:17px">' + prettyNumber(value[i].hi) + '</span>';
						if ( n.indexOf("trending") > -1 ) {
							if ( parseInt(value[i].hiTrending) >= 0 ) {
							    r[++j] = '<span><img alt="This week\'s change" title="This week\'s change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-up.png"></span>';
							    r[++j] = '<span title="This week\'s change" style="color:#8A8C8E; font-size:10px">' + value[i].hiTrending + '</span>';
							} else {
							    r[++j] = '<span><img title="This week\'s change" alt="This week\'s change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-down.png"></span>';
							    r[++j] = '<span title="This week\'s change" style="color:#8A8C8E; font-size:10px">' + -(value[i].hiTrending) + '</span>';
							}
						}
					    r[++j] = '</div></div>';
					    if ( i % 2 == 1 ) {
					    	r[++j] = '<div style="clear:both"></div>';
					    }
					}
				} else if ( n.indexOf("zone") > -1 ) {
					for (var i = 0; i < value.length; i++) {
						r[++j] = '<div class="topChartsElement" style="float:left">';
						r[++j] = '<div style="font-size:18px; font-weight:bold; padding:10px 20px">';
					    r[++j] = '<a href="/zone/' + value[i].zoneId + '">' + value[i].zoneName + '</a></div>';
						r[++j] = '<div onclick="location.href=\'/zone/' + value[i].zoneId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'' + value[i].zoneHeadshot + '\')"></div>';
					    r[++j] = '<div style="padding:3px 20px">' + value[i].zoneDesc + '</div>';
					    r[++j] = '<div style="font-size:12px; color:#909090; padding:5px 20px">';
					    r[++j] = '<span>' + value[i].posts + ' posts</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>' + value[i].comments + ' comments</span>';
					    r[++j] = '</div></div>';
					    if ( i % 2 == 1 ) {
					    	r[++j] = '<div style="clear:both"></div>';
					    }
					}
				} else {
					for (var i = 0; i < value.length; i++) {
						r[++j] = '<div class="topChartsElement" style="float:left">';
					    r[++j] = '<div style="font-size:18px; padding:10px 20px; font-weight:bold">';
					    r[++j] = '<a href="/' + value[i].entityId + '">' + value[i].entityName + '</a>';
						r[++j] = '</div>';
						if ( value[i].profilePhoto != null && value[i].profilePhoto != undefined ) {
							if ( value[i].profilePhoto.lastIndexOf("https://", 0) === 0 ) {
								r[++j] = '<div onclick="location.href=\'' + value[i].entityId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'' + value[i].profilePhoto + '\')"></div>';
							} else {
								if ( n.indexOf("blogger") > -1 ) {
									r[++j] = '<div onclick="location.href=\'' + value[i].entityId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'/resources/images/def-blogger-photo.jpg\')"></div>';
								} else {
									r[++j] = '<div onclick="location.href=\'' + value[i].entityId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'/resources/images/def-page-photo.jpg\')"></div>';
								}
							}
						} else {
							if ( n.indexOf("blogger") > -1 ) {
								r[++j] = '<div onclick="location.href=\'' + value[i].entityId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'/resources/images/def-blogger-photo.jpg\')"></div>';
							} else {
								r[++j] = '<div onclick="location.href=\'' + value[i].entityId + '\'" class="topChartsThumb grow" style="margin:0 auto; background-image:url(\'/resources/images/def-page-photo.jpg\')"></div>';
							}
						}
						r[++j] = '<div style="padding:3px 20px; white-space:pre-line">' + value[i].about + '</div><div style="padding:3px 20px">';
						r[++j] = '<img alt="Overall Heat Index" title="Overall Heat Index" style="width:14px; height:18px; border:none" src="/resources/images/favicon.ico"/>';
						r[++j] = '<span title="Overall Heat Index" style="color:#7A7C7E; font-size:17px">' + prettyNumber(value[i].hi) + '</span>';
						if ( n.indexOf("trending") > -1 ) {
							if ( parseInt(value[i].hiTrending) >= 0 ) {
							    r[++j] = '<span><img alt="This week\'s change" title="This week\'s change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-up.png"></span>';
							    r[++j] = '<span title="This week\'s change" style="color:#8A8C8E; font-size:10px">' + value[i].hiTrending + '</span>';
							} else {
							    r[++j] = '<span><img alt="This week\'s change" title="This week\'s change" style="width:8px; height:8px; border:none; margin-left:10px" src="/resources/images/trending-down.png"></span>';
							    r[++j] = '<span title="This week\'s change" style="color:#8A8C8E; font-size:10px">' + -(value[i].hiTrending) + '</span>';
							}
						}
					    r[++j] = '</div><div style="font-size:12px; color:#909090; padding:5px 20px">';
					    r[++j] = '<span>' + value[i].posts + ' posts</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>' + value[i].votes + ' votes</span>';
					    r[++j] = '<span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';
					    r[++j] = '<span>' + value[i].comments + ' comments</span>';
					    r[++j] = '</div></div>';
					    if ( i % 2 == 1 ) {
					    	r[++j] = '<div style="clear:both"></div>';
					    }
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
