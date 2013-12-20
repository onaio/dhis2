// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showLocaleDetails( i18nLocaleId )
{		
	jQuery.getJSON( "getLocale.action", {
		id:i18nLocaleId
	}, function(json){
		setInnerHTML( 'nameField', json.i18nLocale.name );
		setInnerHTML( 'localeField', json.i18nLocale.locale );
		
		showDetails();
	});   
}

// -----------------------------------------------------------------------------
// Remove I18nLocale
// -----------------------------------------------------------------------------

function removeLocale( i18nLocaleId, name )
{
	removeItem( i18nLocaleId, name, i18n_confirm_delete, 'removeLocale.action' );
}

