// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showConstantDetails( constantId )
{
	jQuery.post( 'getConstant.action', { id:constantId },
		function( json ) {
			setInnerHTML( 'nameField', json.constant.name );
			setInnerHTML( 'valueField', json.constant.value );
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove category constant
// -----------------------------------------------------------------------------

function removeConstant( constantId, constantName )
{
	removeItem( constantId, constantName, i18n_confirm_delete, 'removeConstant.action' );
}