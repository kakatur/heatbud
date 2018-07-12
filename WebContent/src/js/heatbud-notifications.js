
/************************************************/
/************* javascript functions *************/
/************************************************/

// AJAX call to unfollow comments on all posts
function unfollowAllComments() {
	// prevent calling this method twice in case of doubleclick
	var z = unfollowAllCommentsRetMessage.innerHTML;
	if ( z == "Please wait..." ) {
		return false;
	} else {
		unfollowAllCommentsRetMessage.innerHTML = "Please wait...";
	}
	// call controller method
	if (confirm("Are you sure you want to Unfollow Comments on All Posts? This action is irriversible.")) {
		$.post(	"/user/unfollow-all-comments" ).always(function (resp) {
			// process success or error
			if ( resp == 'SUCCESS' ) {
				followCommentsCount.innerHTML = '0';
				unfollowAllCommentsRetMessage.innerHTML = "You have been unsubscribed from the comments on All Posts.";
			} else {
				unfollowAllCommentsRetMessage.innerHTML = resp;
			}
		});
	} else {
		unfollowAllCommentsRetMessage.innerHTML = "";
	}
}

// AJAX call to save notification flags
function saveNotifications() {
	// prevent calling this method twice in case of doubleclick
	var z = saveNotifRetMessage.innerHTML;
	if ( z == "Saving..." ) {
		return false;
	} else {
		saveNotifRetMessage.innerHTML = "Saving...";
	}
	// read notification preferences - the default value of S indicates no change, so the db function will skip it from saving
	var followWhenPublished = 'S';
	if ( document.getElementById("followWhenPublished").value != document.getElementById("originalFollowWhenPublished").value )
		followWhenPublished = document.getElementById("followWhenPublished").value;
	var followWhenCommented = 'S';
	if ( document.getElementById("followWhenCommented").value != document.getElementById("originalFollowWhenCommented").value )
		followWhenCommented = document.getElementById("followWhenCommented").value;
	var notifyWhenThanked = 'S';
	if ( document.getElementById("notifyWhenThanked").value != document.getElementById("originalNotifyWhenThanked").value )
		notifyWhenThanked = document.getElementById("notifyWhenThanked").value;
	var remindDraftPost = 'S';
	if ( document.getElementById("remindDraftPost").value != document.getElementById("originalRemindDraftPost").value )
		remindDraftPost = document.getElementById("remindDraftPost").value;
	var weeklyNewsLetter = 'S';
	if ( document.getElementById("weeklyNewsLetter").value != document.getElementById("originalWeeklyNewsLetter").value )
		weeklyNewsLetter = document.getElementById("weeklyNewsLetter").value;
	// check if at least one preference has been changed
	if ( followWhenPublished == 'S' && followWhenCommented == 'S' && notifyWhenThanked == 'S' && remindDraftPost == 'S' && weeklyNewsLetter == 'S' ) {
		saveNotifRetMessage.innerHTML = "No changes to save.";
		return false;
	}
	// call controller method
	$.post(	"/user/save-notification-flags",
		{ followWhenPublished : followWhenPublished, followWhenCommented : followWhenCommented, notifyWhenThanked : notifyWhenThanked, remindDraftPost : remindDraftPost, weeklyNewsLetter : weeklyNewsLetter }
	).always(function (resp) {
		// process success or error
		if ( resp == 'SUCCESS' ) {
			originalFollowWhenPublished.value = document.getElementById("followWhenPublished").value;
			originalFollowWhenCommented.value = document.getElementById("followWhenCommented").value;
			originalNotifyWhenThanked.value = document.getElementById("notifyWhenThanked").value;
			originalRemindDraftPost.value = document.getElementById("remindDraftPost").value;
			originalWeeklyNewsLetter.value = document.getElementById("weeklyNewsLetter").value;
			saveNotifRetMessage.innerHTML = 'Your preferences have been successfully saved.';
		} else {
			saveNotifRetMessage.innerHTML = resp;
		}
	});
}
