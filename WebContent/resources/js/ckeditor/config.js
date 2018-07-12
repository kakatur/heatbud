/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 * If you update this file, keep a copy in the Documentation folder.
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For the complete reference:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config

	// height
	config.height = 600;

	// Extra Plugins
	config.extraPlugins = 'codemirror';

	// The toolbar arrangement, customized into three rows.
	config.toolbar = [
   	   ['Templates'],
   	   ['Source', '-', 'Preview'],
 	   ['Link', 'Unlink', 'Anchor'],
	   ['Table', 'HorizontalRule', 'SpecialChar', 'Smiley', 'PageBreak'],
	   ['Image', 'oembed', 'Slideshow'],
	   ['About'],
  		'/',
	   ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord'],
	   ['Undo', 'Redo', '-', 'Find', 'Replace', '-', 'RemoveFormat'],
	   ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', 'Blockquote'],
  		'/',
	   ['Bold', 'Italic', 'Underline', 'Strike', '-', 'Subscript', 'Superscript'],
	   ['Styles', 'Format'],
	   ['Font', 'FontSize'],
	   ['TextColor', 'BGColor']
	];

	// Set the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Fonts
	config.font_names =
		'Open Sans/Open Sans, Helvetica, sans-serif;' +
		'Roboto Mono/Roboto Mono, Lucida Grande, sans-serif;' +
		'Scope One/Scope One, Lucida Grande, sans-serif;' +
		'Droid Sans Mono/Droid Sans Mono, Helvetica, sans-serif;' +
		'Ubuntu/Ubuntu, serif;' +
		'Oxygen Mono/Oxygen Mono, Courier, monospace;' +
		'Arvo/Arvo, Courier, monospace;' +
		'Text Me One/Text Me one, Courier, monospace;' +
		'Fauna One/Fauna One, Courier, monospace;' +
		'Offside/Offside, Helvetica, sans-serif;' +
		'Permanent Marker/Permanent Marker, Helvetica, sans-serif;' +
		'Imprima/Imprima, Lucida Grande, sans-serif;' +
		'Lato/Lato, Lucida Grande, sans-serif;' +
		'Marvel/Marvel, Courier, monospace;' +
		'Raleway/Raleway, Courier, monospace;' +
		'Arial/Arial, Helvetica, sans-serif;' +
		'Comic Sans MS/Comic Sans MS, cursive;' +
		'Courier New/Courier New, Courier, monospace;' +
		'Georgia/Georgia, serif;' +
		'Lucida Sans Unicode/Lucida Sans Unicode, Lucida Grande, sans-serif;' +
		'Tahoma/Tahoma, Geneva, sans-serif;' +
		'Times New Roman/Times New Roman, Times, serif;' +
		'Trebuchet MS/Trebuchet MS, Helvetica, sans-serif;' +
		'Verdana/Verdana, Geneva, sans-serif';
	config.fontSize_sizes = '14/14px;16/16px;18/18px;20/20px;24/24px;28/28px;32/32px;48/48px;60/60px;72/72px;';
	
	// Default font
	config.font_defaultLabel = 'Fauna One';
	config.fontSize_defaultLabel = '16';

	// Don't convert UTF and special characters into HTML code
	config.entities = false;
	config.entities_greek = false;
	config.entities_latin = false;
	config.htmlEncodeOutput = false;

	// My Images
	config.filebrowserImageBrowseUrl = '/user/images';
	config.filebrowserWindowWidth = 640;
	config.filebrowserWindowHeight = 640;

	// Enable browser spell checker
	config.disableNativeSpellChecker = false;

};

// make link and dialog plugins simpler
CKEDITOR.on( 'dialogDefinition', function( ev ) {

	// Take the dialog name and its definition from the event data.
	var dialogName = ev.data.name;
	var dialogDefinition = ev.data.definition;

	// Check if the definition is from the dialog we're
	// interested in (the 'link' dialog).
	if ( dialogName == 'link' || dialogName == 'image' ) {

		// Remove the 'Target' and 'Advanced' tabs from the 'Link' dialog.
		dialogDefinition.removeContents( 'target' );
		dialogDefinition.removeContents( 'advanced' );
		 
		// Get a reference to the 'Link Info' tab.
		var infoTab = dialogDefinition.getContents( 'info' );
		 
		// Remove unnecessary widgets from the 'Link Info' tab.         
		infoTab.remove( 'linkType');
		infoTab.remove( 'protocol');
	}
});
