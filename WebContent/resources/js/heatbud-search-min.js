function typeChanged(){if(document.getElementById("type").value=="post"){$("#terms").attr("placeholder","Search Post Title, Post Summary and Blogger Name.");$("#confineBloggerIdDiv").css({visibility:"visible"});$("#confineBloggerId").css({visibility:"visible"})}else{if(document.getElementById("type").value=="zone"){$("#terms").attr("placeholder","Search Zone Name and Description.");$("#confineBloggerIdDiv").css({visibility:"hidden"});confineBloggerId.innerHTML="Any";$("#confineBloggerId").css({visibility:"hidden"})}else{if(document.getElementById("type").value=="blogger"){$("#terms").attr("placeholder","Search Blogger Name and Blogger About.");$("#confineBloggerIdDiv").css({visibility:"hidden"});confineBloggerId.innerHTML="Any";$("#confineBloggerId").css({visibility:"hidden"})}else{$("#terms").attr("placeholder","Search Page Name and Page About.");$("#confineBloggerIdDiv").css({visibility:"hidden"});confineBloggerId.innerHTML="Any";$("#confineBloggerId").css({visibility:"hidden"})}}}}function submitSearch(){if(document.getElementById("terms").value.trim().length==0){$("#terms").attr("placeholder","Enter some words to search.")}else{window.location.href="/search/"+document.getElementById("confineBloggerId").innerHTML+"/"+document.getElementById("type").value+"/"+document.getElementById("terms").value.trim()}}function submitBloggerSearch(){var a=document.getElementById("boxTerms").value.trim();if(a==""){document.getElementById("boxSearchError").innerHTML="Please enter your search terms";$("#boxResults").html('<div style="margin-left:10px; margin-top:30px; font-size:12px">&nbsp;</div>')}else{$("#boxResults").html('<div style="margin-left:10px; margin-top:30px; font-size:12px">Please wait...</div>');document.getElementById("boxSearchError").innerHTML="&nbsp;";$.ajax({type:"POST",url:"/search/get-blogger-box",data:{terms:a},dataType:"json"}).always(function(b){populateBloggerBox(b)})}}function populateBloggerBox(a){$.each(a,function(d,f){switch(d){case"ERROR":document.getElementById("boxSearchError").innerHTML=f;break;case"bloggersList":var e=new Array(),b=-1;BL=new Array();for(var c=0;c<f.length;c++){e[++b]='<table id="'+f[c].entityId+'" onclick="selectBlogger(\''+f[c].entityId+"')\" ondblclick=\"selectBloggerAndClose('"+f[c].entityId+'\')" class="boxedElement" style="width:400px"><tr><td>';if(f[c].profilePhoto!=null&&f[c].profilePhoto!=undefined){if(f[c].profilePhoto.lastIndexOf("https://",0)===0){e[++b]='<img class="profileThumb" src="'+f[c].profilePhoto+'">'}else{e[++b]='<img class="profileThumb" src="/resources/images/def-blogger-photo.jpg">'}}else{e[++b]='<img class="profileThumb" src="/resources/images/def-blogger-photo.jpg">'}e[++b]='</td><td style="vertical-align:top"><div style="margin-left:20px">';e[++b]='<div style="font-weight:bold">'+f[c].entityName+"</div>";e[++b]='<div style="white-space:pre-line; font-size:12px">'+f[c].about+"</div>";e[++b]="</div></td></tr></table>";BL[c]=f[c].entityId}if(BL.length==0){$("#boxResults").html('<div style="margin-left:10px; margin-top:30px; font-size:12px">No Bloggers matching your search.</div>')}else{$("#boxResults").html(e.join(""));selectBlogger(BL[0])}break}})}function selectBloggerAndClose(a){confineBloggerId.innerHTML=a;$("#confineBloggerId").css({visibility:"visible"});document.getElementById("chooseBlogger").checked=true;$("body, #bloggerBox, #modal-background").toggleClass("active")}function selectBlogger(b){confineBloggerId.innerHTML=b;$("#confineBloggerId").css({visibility:"visible"});document.getElementById("chooseBlogger").checked=true;for(var a=0;a<BL.length;a++){if(BL[a]==b){document.getElementById(BL[a]).style.backgroundColor="rgb(208, 219, 225)"}else{document.getElementById(BL[a]).style.backgroundColor="white"}}};