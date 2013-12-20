function removeDocument( id )
{
    removeItem( id, "", i18n_confirm_remove_report, "removeDocument.action" );
}

function toggleExternal()
{
    var external = getListValue( "external" );

    if ( external == "true" )
    {
        document.getElementById( "fileDiv" ).style.display = "none";
        document.getElementById( "urlDiv" ).style.display = "block";
        $( '#url' ).attr( 'class', '{validate:{required:true}}' );
        $( '#upload' ).attr( 'class', '' );
    } else
    {
        document.getElementById( "fileDiv" ).style.display = "block";
        document.getElementById( "urlDiv" ).style.display = "none";
        if ( byId( 'id' ).value == "" )
        {
            $( '#upload' ).attr( 'class', '{validate:{required:true}}' );
            $( '#url' ).attr( 'class', '' );
        }
    }
}
