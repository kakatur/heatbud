function submitMPSearch(){var d=document.getElementById("keyword").value.trim();if(d.indexOf(" ")>=0){$("#mp-error").text("Keyword cannot contain white spaces.");return false}if(d.length>60){$("#mp-error").text("Keyword cannot have more than 60 characters.");return false}var a=document.getElementById("tag").value.trim();if(a.indexOf(" ")>=0){$("#mp-error").text("Skill cannot contain white spaces.");return false}if(a.length>60){$("#mp-error").text("Skill cannot have more than 60 characters.");return false}var e=document.getElementById("ddFrom").value.trim();if(e.length>0){if(!(e%1===0)){$("#mp-error").text("Delivery Days must be a number.");return false}}var g=document.getElementById("ddTo").value.trim();if(g.length>0){if(!(g%1===0)){$("#mp-error").text("Delivery Days must be a number.");return false}}var b=document.getElementById("priceFrom").value.trim();if(b.length>0){if(!(b%1===0)){$("#mp-error").text("Price must be a number.");return false}}var c=document.getElementById("priceTo").value.trim();if(c.length>0){if(!(c%1===0)){$("#mp-error").text("Price must be a number.");return false}}var h=document.createElement("form");h.setAttribute("method","POST");h.setAttribute("action","/do/market");h.setAttribute("target","_self");var i=document.createElement("input");i.setAttribute("name","keyword");i.setAttribute("value",d);h.appendChild(i);var i=document.createElement("input");i.setAttribute("name","tag");i.setAttribute("value",a);h.appendChild(i);var i=document.createElement("input");i.setAttribute("name","ddFrom");i.setAttribute("value",e);h.appendChild(i);var i=document.createElement("input");i.setAttribute("name","ddTo");i.setAttribute("value",g);h.appendChild(i);var i=document.createElement("input");i.setAttribute("name","priceFrom");i.setAttribute("value",b);h.appendChild(i);var i=document.createElement("input");i.setAttribute("name","priceTo");i.setAttribute("value",c);h.appendChild(i);var i=document.createElement("input");i.setAttribute("name","sortCriteria");i.setAttribute("value",document.getElementById("sortCriteria").value);h.appendChild(i);document.body.appendChild(h);h.submit()}function showOrderMPPricing(a){if(userIdHidden.value=="NULL"){$("body, #promptSignupBox, #modal-background").toggleClass("active");return false}$("body, #orderBloggerPricingBox, #modal-background").toggleClass("active");$.ajax({type:"POST",url:"/action/get-primary-page-id",data:{bloggerId:userIdHidden.value},dataType:"json"}).always(function(b){$.each(b,function(c,d){switch(c){case"ERROR":$("#orderBloggerPricingRetMessage").text(d);orderBloggerPricingButton.onclick=function(){return false};$("#orderBloggerPricingButton").css("color","#727272");$("#orderBloggerPricingButton").css("cursor","default");break;case"SUCCESS":$("#orderBloggerPricingRetMessage").html("");$("#bloggerPricingPageId").html(d);$("#bloggerPricingBloggerId").html(MP[a]["bloggerId"]);$("#bloggerPricingPostType").html(MP[a]["postType"]);$("#bloggerPricingDeliveryDays").html(MP[a]["deliveryDays"]);$("#bloggerPricingPrice").html(MP[a]["price"]);break}})})}function orderBloggerPricing(){if(userIdHidden.value=="NULL"){$("body, #promptSignupBox, #modal-background").toggleClass("active");return false}var a=$("#orderBloggerPricingRetMessage").html();if(a!="Please wait..."){$("#orderBloggerPricingRetMessage").html("Please wait...");$.ajax({type:"POST",url:"/action/order-blogger-pricing",data:{pageId:document.getElementById("bloggerPricingPageId").innerHTML.trim(),bloggerId:document.getElementById("bloggerPricingBloggerId").innerHTML.trim(),postType:document.getElementById("bloggerPricingPostType").innerHTML.trim(),deliveryDays:document.getElementById("bloggerPricingDeliveryDays").innerHTML.trim(),price:document.getElementById("bloggerPricingPrice").innerHTML.trim()},dataType:"json"}).always(function(b){$.each(b,function(c,d){switch(c){case"ERROR":$("#orderBloggerPricingRetMessage").html(d);break;case"SUCCESS":$("#orderBloggerPricingRetMessage").html(d);break;case"orderHandler":window.location.href="/user/order-payment/"+d;break}})})}};