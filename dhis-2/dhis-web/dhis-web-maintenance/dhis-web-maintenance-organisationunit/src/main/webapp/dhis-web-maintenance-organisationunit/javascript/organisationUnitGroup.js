
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitGroupDetails( unitId )
{
    jQuery.post( 'getOrganisationUnitGroup.action', { id: unitId },
		function ( json ) {
			setInnerHTML( 'nameField', json.organisationUnitGroup.name );
			setInnerHTML( 'shortNameField', json.organisationUnitGroup.shortName );
			setInnerHTML( 'codeField', json.organisationUnitGroup.code );
			setInnerHTML( 'memberCountField', json.organisationUnitGroup.memberCount );
			
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove organisation unit group
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroup( unitGroupId, unitGroupName )
{
	removeItem( unitGroupId, unitGroupName, confirm_to_delete_org_unit_group, 'removeOrganisationUnitGroup.action' );
}

//-----------------------------------------------------------------------------
//Symbol
//-----------------------------------------------------------------------------

function openSymbolDialog()
{
	$( "#symbolDiv" ).dialog( {
		height: 294,
		width: 250,
		modal: true,
		resizable: false,
		title: "Select symbol"
	} );
}

function selectSymbol( img )
{
	$( "#symbol" ).val( img );
	$( "#symbolDiv" ).dialog( "close" );
	$( "#symbolImg" ).attr( "src", "../images/orgunitgroup/" + img ).show();
}