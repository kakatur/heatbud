$(document).ready(function(){$("#getCommentsNext").click(function(){$.ajax({type:"POST",url:"/action/get-comments-next",data:{postId:postIdHidden.value,commentsKeyNext:commentsKeyNextHidden.value},dataType:"json"}).always(function(a){populateComments(a)})});$("#getCommentsPrev").click(function(){$.ajax({type:"POST",url:"/action/get-comments-prev",data:{postId:postIdHidden.value,commentsKeyPrev:commentsKeyPrevHidden.value},dataType:"json"}).always(function(a){populateComments(a)})})});function populateComments(a){$.each(a,function(d,f){switch(d){case"ERROR":alert(f);break;case"commentsList":var e=new Array(),b=-1;for(var c=0;c<f.length;c++){thanked=parseInt(f[c].commentDate.substring(0,1));pcd=parseInt(f[c].commentDate.substring(1,14));cd=parseInt(f[c].commentDate.substring(14,27));if(cd==9999999999999){cd=pcd;commentHeaderCSS="margin-top:16px; background-color:#F5F5F5; border-bottom:1px solid #BBBBBB; padding:4px;";commentIndentCSS=""}else{cd=9999999999999-cd;commentHeaderCSS="";commentIndentCSS="margin-left:15px;"}e[++b]='<div style="font-size:1.2em; '+commentIndentCSS+commentHeaderCSS+'">';e[++b]='<span id="thankComment'+f[c].commentDate+'Span">';if(thanked=="2"&&cd==pcd){e[++b]='<img alt="thanked by the blogger" style="max-height:15px; margin-right:10px; border:none" src="/resources/images/thanked.jpg"/>&nbsp;'}e[++b]="</span>";e[++b]='<span style="color:#909090">'+new Date(cd).toLocaleString()+"</span><br/>";e[++b]='<a target="_self" href="/'+f[c].commenterId+'">'+f[c].commenterName+"</a>";e[++b]="</div>";e[++b]='<div id="comment'+f[c].commentDate+'Div" style="font-size:1.2em; '+commentIndentCSS+'margin-top:4px; white-space:pre-line">'+f[c].commentText+"</div>"}$("#commentsDiv").html(e.join(""));document.getElementById("postComments").scrollIntoView();break;case"commentsKeyPrev":commentsKeyPrevHidden.value=f;if(f=="NULL"){$("#getCommentsPrevDiv").css("visibility","hidden")}else{$("#getCommentsPrevDiv").css("visibility","visible")}break;case"commentsKeyNext":commentsKeyNextHidden.value=f;if(f=="NULL"){$("#getCommentsNextDiv").css("visibility","hidden")}else{$("#getCommentsNextDiv").css("visibility","visible")}break}})}function getFBShares(a){var b=document.getElementById(a+"IdHidden").value;$.ajax({type:"GET",url:"https://graph.facebook.com/https://www.heatbud.com/"+a+"/"+b,data:{},dataType:"json"}).always(function(c){document.getElementById(a+"FbShares").innerHTML=" "+c.share.share_count+" "})};