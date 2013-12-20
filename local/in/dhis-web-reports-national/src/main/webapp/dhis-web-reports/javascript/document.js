
function saveDocument()
{
    var name = document.getElementById( "name" );
    
    //var url = "validateDocument.action?name=" + name;
    
    /* var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( saveDocumentReceived );
    //request.send( url );

    var requestString = "validateDocument.action";
    var params = "name=" + name;
    request.sendAsPost( params );
    request.send( requestString ); */
	
	$.post("validateDocument.action",
		{
			name : name
		},
		function (data)
		{
			saveDocumentReceived(data);
		},'xml');
}

function saveDocumentReceived( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == "input" )
    {
        setMessage( message );
        
        return false;
    }
    else if ( type == "success" )
    {
        document.getElementById( "documentForm" ).submit();
    }
}

function removeDocument( id )
{
    var dialog = window.confirm( i18n_confirm_remove_report );
    
    if ( dialog )
    {
    	/* //window.location.href = "removeDocument.action?id=" + id;
        var request = new Request();
        var requestString = "removeDocument.action";
        var params = "id=" + id ;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("removeDocument.action",
			{
				id : id
			},
			function (data)
			{
			},'xml');
		
    }
}

function addDocumentToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_to_dashboard );
    
    if ( dialog )
    {
    	/* var request = new Request(); 
        //request.send( "addDocumentToDashboard.action?id=" + id );
        var requestString = "addDocumentToDashboard.action";
        var params = "id=" + id ;
        request.sendAsPost( params );
        request.send( requestString ); */
		
		$.post("addDocumentToDashboard.action",
			{
				id : id
			},
			function (data)
			{
			},'xml');
    }
}

function toggleExternal()
{
    var external = getListValue( "external" );
    
    if ( external == "true" )
    {
        document.getElementById( "fileDiv" ).style.display = "none";
        document.getElementById( "urlDiv" ).style.display = "block";
    }
    else
    {
        document.getElementById( "fileDiv" ).style.display = "block";
        document.getElementById( "urlDiv" ).style.display = "none";
    }
}
