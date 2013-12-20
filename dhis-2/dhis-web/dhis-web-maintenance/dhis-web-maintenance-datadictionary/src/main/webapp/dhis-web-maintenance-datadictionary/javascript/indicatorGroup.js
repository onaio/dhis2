// -----------------------------------------------------------------------------
// Show Indicator Group details
// -----------------------------------------------------------------------------

function showIndicatorGroupDetails( indicatorGroupId )
{
	jQuery.get( '../dhis-web-commons-ajax-json/getIndicatorGroup.action',
		{ id: indicatorGroupId }, function( json ) {
		setInnerHTML( 'nameField', json.indicatorGroup.name );
		setInnerHTML( 'memberCountField', json.indicatorGroup.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove indicator group
// -----------------------------------------------------------------------------

function removeIndicatorGroup( indicatorGroupId, indicatorGroupName )
{
    removeItem( indicatorGroupId, indicatorGroupName, i18n_confirm_delete, 'removeIndicatorGroup.action' );
}
