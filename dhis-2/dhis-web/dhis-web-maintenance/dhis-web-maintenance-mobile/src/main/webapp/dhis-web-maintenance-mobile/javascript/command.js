currentType = '';
function changeParserType( value )
{
	hideAll();
    if ( value == 'KEY_VALUE_PARSER' || value == 'J2ME_PARSER') {
        showById( "dataSetParser" );
    } else if ( value == 'ALERT_PARSER' || value == 'UNREGISTERED_PARSER' ) {
    	showById( "alertParser" );
    }
	currentType = value;
}

function hideAll() 
{
	 hideById( "dataSetParser" ); 
	 hideById( "alertParser" );
}
