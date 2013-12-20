// -----------------------------------------------------------------------------
// Show Indicator Group Set details
// -----------------------------------------------------------------------------

function showIndicatorGroupSetDetails( id )
{
	jQuery.post( '../dhis-web-commons-ajax-json/getIndicatorGroupSet.action',
		{ id: id }, function( json ) {
		setInnerHTML( 'nameField', json.indicatorGroupSet.name );
		setInnerHTML( 'memberCountField', json.indicatorGroupSet.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Delete Indicator Group Set
// -----------------------------------------------------------------------------

function deleteIndicatorGroupSet( groupSetId, groupSetName )
{
    removeItem( groupSetId, groupSetName, i18n_confirm_delete, "deleteIndicatorGroupSet.action" );
}
