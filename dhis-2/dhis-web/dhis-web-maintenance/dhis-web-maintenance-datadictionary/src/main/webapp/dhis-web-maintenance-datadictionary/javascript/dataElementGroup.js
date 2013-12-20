function beforeSubmit()
{
    memberValidator = jQuery( "#memberValidator" );
    memberValidator.children().remove();

    jQuery.each( jQuery( "#groupMembers" ).children(), function( i, item )
    {
        item.selected = 'selected';
        memberValidator.append( '<option value="' + item.value + '" selected="selected">' + item.value + '</option>' );
    } );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementGroupDetails( dataElementGroupId )
{
    jQuery.post( '../dhis-web-commons-ajax-json/getDataElementGroup.action',
		{ id: dataElementGroupId }, function ( json ) {		
	
		setInnerHTML( 'nameField', json.dataElementGroup.name );
		setInnerHTML( 'shortNameField', json.dataElementGroup.shortName );
		setInnerHTML( 'codeField', json.dataElementGroup.code );
		setInnerHTML( 'memberCountField', json.dataElementGroup.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove data element group
// -----------------------------------------------------------------------------

function removeDataElementGroup( dataElementGroupId, dataElementGroupName )
{
    removeItem( dataElementGroupId, dataElementGroupName, i18n_confirm_delete, "removeDataElementGroup.action" );
}
