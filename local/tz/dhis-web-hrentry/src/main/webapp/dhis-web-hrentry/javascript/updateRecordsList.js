//-----------------------------------------------------------------------------
//Remove Person
//-----------------------------------------------------------------------------

function removePerson( personId, name )
{
 var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
 
 if ( result )
 {
	
 	var request = new Request();
     request.setResponseTypeXML( 'message' );
     request.setCallbackSuccess( removePersonCompleted );     
     window.location.href = 'removePerson.action?id=' + personId;
 }
}

function removePersonCompleted( messageElement )
{
 var type = messageElement.getPerson( 'type' );
 var message = messageElement.firstChild.nodeValue;
 
 if ( type == 'success' )
 {
     window.location.href = 'updateRecordsList.action';
 }
 else if ( type = 'error' )
 {
     setFieldValue( 'warningField', message );
     
     showWarning();
 }
}

$(document).ready(function()
{
	// Use each() method to gain access to elements attribute
	$('#profile a[rel]:first').each(function()
	{
	   $(this).qtip(
	   {
		  content: {
			 text: '<img style="padding-left:45%" class="throbber" src="./image/throbber.gif" alt="Loading..." />' + "<h1>Please wait while loading data...</h1>",
			 url: $(this).attr('rel'),
			 title: {
		   		button: 'Close',
				text: 'Employee\'s - Profile'
				
			 }
		  },
		  position: {
		         target: $('div#mainPage'), // Position it via the document body...
		         corner: 'center' // ...at the center of the viewport
		      },
		  show: {
			 when: 'click', // Show it on click
			 solo: true // And hide all other tooltips
		  },
		  hide: false,
		  style: {
			 width: { max: 500 },
			 padding: '14px',
			 border: {
				width: 9,
				radius: 9,
				color: '#666666'
			 },
			 name: 'light'
		  },
		  api: {
			 beforeShow: function()
			 {
				// Fade in the modal "blanket" using the defined show speed
				$('#qtip-blanket').fadeIn(this.options.show.effect.length);
			 },
			 beforeHide: function()
			 {
				// Fade out the modal "blanket" using the defined hide speed
				$('#qtip-blanket').fadeOut(this.options.hide.effect.length);
			 }
		  }
	   }
	   );
	 });

   // Create the modal backdrop on document load so all modal tooltips can use it
   $('<div id="qtip-blanket">')
	  .css({
		 position: 'absolute',
		 top: $(document).scrollTop(), // Use document scrollTop so it's on-screen even if the window is scrolled
		 left: 0,
		 height: $(document).height(), // Span the full document height...
		 width: '100%', // ...and full width

		 opacity: 0.7, // Make it slightly transparent
		 backgroundColor: 'black',
		 zIndex: 5000  // Make sure the zIndex is below 6000 to keep it below tooltips!
	  })
	  .appendTo(document.body) // Append to the document body
	  .hide(); // Hide it initially
});
