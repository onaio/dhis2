function showDataElementCategoryComboDetails( categoryComboId )
{
    jQuery.post( 'getDataElementCategoryCombo.action', { id: categoryComboId },
		function ( json) {
			setInnerHTML( 'nameField', json.dataElementCategoryCombo.name );
			setInnerHTML( 'dataElementCategoryCountField', json.dataElementCategoryCombo.dataElementCategoryCount );

			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Delete Category
// -----------------------------------------------------------------------------

function removeDataElementCategoryCombo( categoryComboId, categoryComboName )
{
    removeItem( categoryComboId, categoryComboName, i18n_confirm_delete, 'removeDataElementCategoryCombo.action' );
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateSelectedCategories( form )
{
    var url = "validateDataElementCategoryCombo.action?";
    url += getParamString( "selectedList", "selectedCategories" );

    jQuery.postJSON( url, {}, function( json )
    {
        if ( json.response == 'success' )
        {
            form.submit();
        } else
            markInvalid( 'selectedCategories', json.message );
    } );

}
