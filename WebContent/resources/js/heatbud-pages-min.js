$(document).ready(function(){$(".createPage").click(function(){if(userIdHidden.value=="NULL"){$("body, #promptSignupBox, #modal-background").toggleClass("active");return false}$("body, #createPageBox, #modal-background").toggleClass("active")})});function createPage(){if(userIdHidden.value=="NULL"){$("body, #promptSignupBox, #modal-background").toggleClass("active");return false}var e=document.getElementById("createPageIdInput").value.trim();if(e.length==0){createPageMessage.innerHTML="Page ID cannot be empty.";return false}if(e.length>30){createPageMessage.innerHTML="Page ID cannot contain more than 30 characters.";return false}var c=document.getElementById("createPageNameInput").value.trim();if(c.length==0){createPageMessage.innerHTML="Page Name cannot be empty.";return false}if(c.length>80){createPageMessage.innerHTML="Page Name cannot contain more than 80 characters.";return false}var b=document.getElementById("createPageEmailInput").value.trim();if(b.length==0){createPageMessage.innerHTML="Page Email cannot be empty.";return false}var a=document.getElementById("createPagePhoneInput").value.trim();var d=document.getElementById("createPageAboutInput").value.trim();if(d.length==0){createPageMessage.innerHTML="About section cannot be empty.";return false}$.ajax({type:"POST",url:"/action/create-page",data:{pageId:e,pageName:c,pageEmail:b,pagePhone:a,about:d},dataType:"json"}).always(function(f){if(f.error!="None"){createPageMessage.innerHTML=f.error;document.getElementById("createPageIdInput").value=f.pageId}else{window.location.href="https://www.heatbud.com/user/pages"}})};