var editor="";var html="";var config={};function getPostContent(){if(postIdInput.value.trim()==""){s3EditorRetMessage.innerHTML="Please enter Post Id. For newsletter announcements, type nl-announcements"}$.ajax({type:"POST",url:"/admin/get-post-content",data:{postId:postIdInput.value},dataType:"json"}).always(function(a){$.each(a,function(b,c){switch(b){case"postTitle":postTitle.innerHTML=c;break;case"postSummary":postSummary.innerHTML=c;break;case"postHeadshot":if(c.trim()==""){$("#postHeadshotImg").css("display","none")}else{$("#postHeadshotImg").css("display","block");postHeadshotImg.src=c}$("#postHeadshotEdit").css("display","none");break;case"bloggerId":bloggerId.innerHTML='<a target="_self" href="/'+c+'">'+c+"</a>";break;case"postUpdateDate":postUpdateDate.innerHTML=new Date(c).toLocaleString();break;case"postURL":$("#postContent").load(c);break;case"s3EditorRetMessage":s3EditorRetMessage.innerHTML=c;break}});$("#postArea").css("display","block");$("#s3EditorButtons").html('<input class="activeButton" style="height:35px" onclick="getPostContent()" type="button" value="Get Content"><input class="activeButton" style="height:35px" onclick="editContent()" type="button" value="Edit">')})}function editContent(){if(editor!=""){return}t=postTitle.innerHTML;$("#postTitle").html('<input id="postTitleInput" type=text style="width:350px" value="'+t+'"><br></p>');s=postSummary.innerHTML;$("#postSummary").html('<input id="postSummaryInput" type=text style="width:350px" value="'+s+'"><br></p>');if(postHeadshotImg.src.trim()!=""){$("#postHeadshotEdit").css("display","block")}html=postContent.innerHTML;$("#postContent").html("");editor=CKEDITOR.appendTo("postContent",config,html);$("#s3EditorButtons").html('<input class="activeButton" style="height:35px" onclick="saveContent()" type="button" value="Save"><input class="activeButton" style="height:35px" onclick="closeEditor()" type="button" value="Close">')}function showPostHeadshotBox(){$("body, #imageBox, #modal-background").toggleClass("active");populateFolders();showContents("common")}function closeEditor(){if(editor!=""){editor.destroy()}editor="";getPostContent()}function saveContent(){var c=s3EditorRetMessage.innerHTML;if(c=="Processing..."){return false}else{s3EditorRetMessage.innerHTML="Processing..."}if(editor==""){return}if(postTitleInput.value.length==0){s3EditorRetMessage.innerHTML="Title cannot be empty.";return false}if(editor.getData().length==0){s3EditorRetMessage.innerHTML="Content cannot be empty.";return false}$("#s3EditorButtons").html('<input class="disabledButton" style="height:35px" onclick="return false;" type="button" value="Save"><input class="disabledButton" style="height:35px" onclick="return false;" type="button" value="Close">');var a=new FormData();a.append("postId",postIdInput.value);a.append("postTitle",postTitleInput.value);a.append("postSummary",postSummaryInput.value);a.append("postHeadshot",postHeadshotImg.src);a.append("postContent",editor.getData());$.ajax({type:"POST",url:"/admin/save-post-content",data:a,processData:false,contentType:false}).always(function(b){if(b=="SUCCESS"){s3EditorRetMessage.innerHTML="Last saved : "+new Date().toLocaleString();$("#s3EditorButtons").html('<input class="activeButton" style="height:35px" onclick="saveContent()" type="button" value="Save"><input class="activeButton" style="height:35px" onclick="closeEditor()" type="button" value="Close">')}else{s3EditorRetMessage.innerHTML=b}})};