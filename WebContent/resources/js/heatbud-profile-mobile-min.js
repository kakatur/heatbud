$(document).ready(function(){$("#getPublishedPostsNext").click(function(){$.getJSON("/action/get-profile-page-posts-next",{bloggerId:entityIdHidden.value,profilePagePostsKeyNextBI:publishedPostsKeyNextBIHidden.value,profilePagePostsKeyNextUD:publishedPostsKeyNextUDHidden.value,publishFlag:"Y"},function(a){populateProfilePagePosts(a)})});$("#getPublishedPostsPrevious").click(function(){$.getJSON("/action/get-profile-page-posts-previous",{bloggerId:entityIdHidden.value,profilePagePostsKeyPrevBI:publishedPostsKeyPrevBIHidden.value,profilePagePostsKeyPrevUD:publishedPostsKeyPrevUDHidden.value,publishFlag:"Y"},function(a){populateProfilePagePosts(a)})});$("#getAdminZonesNext").click(function(){$.getJSON("/action/get-admin-zones-next",{entityId:entityIdHidden.value,adminZonesKeyNextUI:publishedPostsKeyNextUIHidden.value,adminZonesKeyNextZI:publishedPostsKeyNextZIHidden.value},function(a){populateAdminZones(a)})});$("#getAdminZonesPrevious").click(function(){$.getJSON("/action/get-admin-zones-previous",{entityId:entityIdHidden.value,adminZonesKeyPrevUI:publishedPostsKeyPrevUIHidden.value,adminZonesKeyPrevZI:publishedPostsKeyPrevZIHidden.value},function(a){populateAdminZones(a)})})});function populateProfilePagePosts(a){$.each(a,function(d,f){switch(d){case"ERROR":$("#publishedPostsDiv").html(f);break;case"profilePagePostsList":var e=new Array(),b=-1;for(var c=0;c<f.length;c++){e[++b]='<div style="padding:6% 3% 3% 3%; background-color:white; border-bottom:1px solid rgb(52, 127, 125)">';e[++b]='<div style="font-size:1.3em; font-weight:bold">';e[++b]='<a href="/post/'+f[c].postId+'">'+f[c].postTitle+"</a></div>";e[++b]='<div style="text-align:center; margin-top:2%">';e[++b]='<a href="/post/'+f[c].postId+'"><img alt="'+f[c].postTitle+'" title="'+f[c].postTitle+'" style="width:90%" src="'+f[c].postHeadshot+'"></a></div>';e[++b]='<div style="font-size:1.2em; color:#909090; margin-top:2%">by <a href="/'+f[c].bloggerId+'">'+f[c].bloggerName+"</a></div>";e[++b]='<div style="font-size:1em; color:#909090; margin-top:2%">on '+new Date(f[c].updateDate).toLocaleString()+"</div>";e[++b]='<div style="font-size:1.2em; color:#909090; margin-top:2%">zone <a href="/zone/'+f[c].zoneId+'">'+f[c].zoneName.replace(/"/g,"&#034;")+"</a></div>";e[++b]='<div style="font-size:1.2em; margin-top:2%" >'+f[c].postSummary+"</div></div>"}$("#publishedPostsDiv").html(e.join(""));document.getElementById("publishedPostsHeader").scrollIntoView();break;case"profilePagePostsKeyPrevBI":publishedPostsKeyPrevBIHidden.value=f;if(f=="NULL"){$("#getPublishedPostsPreviousDiv").css("visibility","hidden")}else{$("#getPublishedPostsPreviousDiv").css("visibility","visible")}break;case"profilePagePostsKeyNextBI":publishedPostsKeyNextBIHidden.value=f;if(f=="NULL"){$("#getPublishedPostsNextDiv").css("visibility","hidden")}else{$("#getPublishedPostsNextDiv").css("visibility","visible")}break;case"profilePagePostsKeyPrevUD":publishedPostsKeyPrevUDHidden.value=f;break;case"profilePagePostsKeyNextUD":publishedPostsKeyNextUDHidden.value=f;break}})}function populateAdminZones(a){$.each(a,function(d,f){switch(d){case"ERROR":alert(f);break;case"adminZonesList":var e=new Array(),b=-1;e[++b]='<table style="border-spacing:8px">';for(var c=0;c<f.length;c++){var g=parseInt(f[c].upVotes)-parseInt(f[c].downVotes);e[++b]='<tr><td style="vertical-align:top">';e[++b]='<div style="font-size:200%; text-align:center">'+g+'</div><div style="font-size:11px; color:#909090">votes</div></td>';e[++b]='<td style="vertical-align:top">';e[++b]='<div style="font-size:200%; text-align:center">'+f[c].comments+'</div><div style="font-size:11px; color:#909090">comments</div></td>';e[++b]='<td style="vertical-align:top"><div style="margin-left:10px">';e[++b]='<div style="font-weight:bold"><a href="/post/'+f[c].postId+'">'+f[c].postTitle+"</a></div>";e[++b]='<div style="font-size:11px; color:#909090"><span><a href="/zone/'+f[c].zoneId+'">'+f[c].zoneName.replace(/"/g,"&#034;")+"</a>";e[++b]='</span><span style="font-weight:bold; color:rgb(144, 144, 144)">&nbsp;.&nbsp;</span>';e[++b]='<span style="color:#909090">'+new Date(f[c].updateDate).toLocaleString()+"</span></div>";e[++b]="<div>"+f[c].postSummary+"</div>";e[++b]="</div></td></tr>"}e[++b]="</table>";$("#adminZonesDiv").html(e.join(""));document.getElementById("adminZonesHeader").scrollIntoView();break;case"adminZonesKeyPrevUI":adminZonesKeyPrevUIHidden.value=f;if(f=="NULL"){$("#getAdminZonesPreviousDiv").css("visibility","hidden")}else{$("#getAdminZonesPreviousDiv").css("visibility","visible")}break;case"adminZonesKeyNextUI":adminZonesKeyNextUIHidden.value=f;if(f=="NULL"){$("#getAdminZonesNextDiv").css("visibility","hidden")}else{$("#getAdminZonesNextDiv").css("visibility","visible")}break;case"adminZonesKeyPrevZI":adminZonesKeyPrevZIHidden.value=f;break;case"adminZonesKeyNextZI":adminZonesKeyNextZIHidden.value=f;break}})}function getFBShares(a){var b=document.getElementById(a+"IdHidden").value;$.ajax({type:"GET",url:"https://graph.facebook.com/https://www.heatbud.com/"+b,data:{},dataType:"json"}).always(function(c){document.getElementById(a+"FbShares").innerHTML=" "+c.share.share_count+" "})};