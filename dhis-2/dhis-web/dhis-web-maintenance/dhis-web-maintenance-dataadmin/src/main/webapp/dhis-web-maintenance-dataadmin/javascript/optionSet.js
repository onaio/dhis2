// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOptionSetDetails( optionSetId )
{
	jQuery.post( 'getOptionSet.action', { id:optionSetId },
		function( json ) {
			setInnerHTML( 'nameField', json.optionSet.name );
			setInnerHTML( 'optionCount', json.optionSet.optionCount );
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove category constant
// -----------------------------------------------------------------------------

function removeOptionSet ( optionSetId, optionSetName )
{
	removeItem( optionSetId, optionSetName, i18n_confirm_delete, 'removeOptionSet.action' );
}

// -----------------------------------------------------------------------------
// Add options constant
// -----------------------------------------------------------------------------

function addOption()
{
	var value = getFieldValue( 'option' );
	
	if ( value.length == 0 )
	{
		markInvalid( 'option', i18n_specify_option_name );
	}
	else if ( listContainsById( 'options', value, true ) )
	{
		markInvalid( 'option', i18n_option_name_already_exists );
	}
	else 
	{
		addOptionById( 'options', value, value );
	}
	
	setFieldValue('option', '');
	$("#option").focus();
}

function updateOption()
{
	var value = getFieldValue( 'option' );
	jQuery('#options option:selected').val(value);
	jQuery('#options option:selected').text(value);
	
	setFieldValue('option', '');
	$("#option").focus();
}
