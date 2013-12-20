
var currentPage = 0;
var pageLock = false;

$( document ).ready( function() {
	$( document ).scroll( function() {
		isNextPage();
	} );
	
	$( "#interpretationFeed" ).load( "getInterpretations.action", function() {
		$( ".commentArea" ).autogrow();
	} );
} );

function expandComments( id )
{
	$( "#comments" + id ).children().show();
	$( "#commentHeader" + id ).hide();
}

function isNextPage()
{
	var fromTop = $( document ).scrollTop();
	var docHeight = $( document ).height();
	var windowHeight = $( window ).height();
	var threshold = parseInt( 350 );
	var remaining = parseInt( docHeight - ( fromTop + windowHeight ) );
	
	if ( remaining < threshold )
	{
		loadNextPage();
	}
}

function loadNextPage()
{
	if ( pageLock == true )
	{
		return false;
	}
	
	pageLock = true;
	currentPage++;
	
	$.get( "getInterpretations.action", { page: currentPage }, function( data ) {
		$( "#interpretationFeed" ).append( data );
		
		if ( !isDefined ( data ) || $.trim( data ).length == 0 )
		{
			$( document ).off( "scroll" );
		}
		
		pageLock = false;
	} );
}

function postComment( uid )
{	
	var text = $( "#commentArea" + uid ).val();
	
	$( "#commentArea" + uid ).val( "" );
	
	var url = "../api/interpretations/" + uid + "/comment";
	
	var created = getCurrentDate();
	
	if ( text.length && $.trim( text ).length )
	{
		$.ajax( url, {
			type: "POST",
			contentType: "text/html",
			data: $.trim( text ),
			success: function() {			
				var template = 
					"<div><div class=\"interpretationName\">" +
					"<a class=\"bold userLink\" href=\"profile.action?id=${userUid}\">${userName}</a>&nbsp;" +
					"<span class=\"grey\">${created}<\/span><\/div><\/div>" +
					"<div class=\"interpretationText\">${text}<\/div>";
				
				$.tmpl( template, { 
					"userId": currentUser.id,
					"userUid": currentUser.uid,
					"userName": currentUser.name, 
					created: created, 
					text: text } ).appendTo( "#comments" + uid );
			}		
		} );
	}
}
