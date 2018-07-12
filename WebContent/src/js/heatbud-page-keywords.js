
/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Add Tag to the Page
function addPageTag() {
	var b = $("#tag-button").html();
	if ( b != 'Please wait...' ) {
		$("#tag-button").html('Please wait...');
		$("#tag-error").text(" ");
		// validate the input tag
		var tag = document.getElementById("tag-input").value.trim();
		if (tag.length == 0) {
			$("#tag-error").text("Please enter a keyword.");
			$("#tag-button").html('add');
			return false;
		}
		if(!/^[a-zA-Z0-9\s]*$/.test(tag)) {
			$("#tag-error").text("Alphabets, numbers and the space are the only characters allowed in a keyword.");
			$("#tag-button").html('add');
			return false;
		}
		if (tag.length > 50) {
			$("#tag-error").text("A keyword cannot have more than 50 characters.");
			$("#tag-button").html('add');
			return false;
		}
		// check if tag already exists
		var id = tag.toLowerCase().replace(/\s/g,'');
		if ( TL.includes(id) ) {
			$("#tag-error").text("Keyword already exists.");
			$("#tag-button").html('add');
			return false;
		}
		// validate tag count
		var tagCount = document.getElementById("tag-count").value;
		if ( tagCount >= 20 ) {
			$("#tag-error").text("A business page can have a maximum of 20 keywords.");
			$("#tag-button").html('add');
			return false;
		}
		// increment tag count and add tag to the list
		tagCount++;
		document.getElementById("tag-count").value=tagCount;
		TL.push(id);
		// insert into the database
		$.ajax({
			type: "POST",
			url: "/action/add-entity-tag",
			data: { entityId : pageIdHidden.value, tag : tag },
			dataType: "json"
		}).always(function (resp) {
			// display new tag on the page
			if ( tagCount == 1 ) {
				$("#page-tags").html(
					'<div id="' + id + '" style="display:inline-block; border-radius:14px; border:1px solid #61cf81; padding:4px 10px 4px 10px; margin-top:6px; margin-right:6px; font-family:Arial; font-size:16px; color:#365899">'+
					'<span id="' + id + '-value">' + tag + '</span>&nbsp;&nbsp;&nbsp;'+
					'<a href="javascript:" onclick="deletePageTag(\'' + id + '\')" title="Delete this tag">&nbsp;x&nbsp;</a>'+
					'</div>'
				);
			} else {
				$("#page-tags").append(
					'<div id="' + id + '" style="display:inline-block; border-radius:14px; border:1px solid #61cf81; padding:4px 10px 4px 10px; margin-top:6px; margin-right:6px; font-family:Arial; font-size:16px; color:#365899">'+
					'<span id="' + id + '-value">' + tag + '</span>&nbsp;&nbsp;&nbsp;'+
					'<a href="javascript:" onclick="deletePageTag(\'' + id + '\')" title="Delete this tag">&nbsp;x&nbsp;</a>'+
					'</div>'
				);
			}
			document.getElementById("tag-input").value="";
			$("#tag-button").html('add');
		});
	}
}

// Delete Tag from Page
function deletePageTag(id) {
	var b = $("#tag-error").html();
	if ( b != 'Please wait...' ) {
		$("#tag-error").html('Please wait...');
		// read tag
		var tag = document.getElementById(id+"-value").textContent.trim();
		// delete from the database
		$.ajax({
			type: "POST",
			url: "/action/delete-entity-tag",
			data: { entityId : pageIdHidden.value, tag : tag },
			dataType: "json"
		}).always(function (resp) {
			// decrement tag count
			var tagCount = document.getElementById("tag-count").value;
			tagCount--;
			document.getElementById("tag-count").value=tagCount;
			// delete tag from the list
			TL.splice( TL.indexOf(id), 1 );
			$("#"+id).text(" ");
			// reset error message
			$("#tag-error").text(" ");
		});
	}
}
