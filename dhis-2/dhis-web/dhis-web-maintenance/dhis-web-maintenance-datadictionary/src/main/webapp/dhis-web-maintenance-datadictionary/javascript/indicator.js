function exportPDF( type )
{
	var params = "type=" + type;
	params += "&dataDictionaryId=" + jQuery( '#dataDictionaryList' ).val();
	
	exportPdfByType( type, params );
}

function indicatorTypeChanged()
{
    var type = byId( 'indicatorTypeId' ).options[byId( 'indicatorTypeId' ).selectedIndex].getAttribute( 'number' );
    byId( 'denominatorButton' ).disabled = eval( type );
    
    if ( eval( type ) )
    {
        setFieldValue( 'denominator', '1' );
    } 
    else
    {
        if ( getFieldValue( 'denominatorFormula' ) == undefined )
        {
            setFieldValue( 'denominator', '' );
        } 
        else
        {
            setFieldValue( 'denominator', getFieldValue( 'denominatorFormula' ) );
        }
    }
}

// -----------------------------------------------------------------------------
// Change indicator group and data dictionary
// -----------------------------------------------------------------------------

function criteriaChanged()
{
    var dataDictionaryId = getListValue( "dataDictionaryList" );

    var url = "indicator.action?&dataDictionaryId=" + dataDictionaryId;

    window.location.href = url;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showIndicatorDetails( indicatorId )
{
    jQuery.get( '../dhis-web-commons-ajax-json/getIndicator.action',
		{ id: indicatorId }, function( json ) {
		
		setInnerHTML( 'nameField', json.indicator.name );

		setInnerHTML( 'shortNameField', json.indicator.shortName );

		var description = json.indicator.description;
		setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );

		var annualized = json.indicator.annualized;
		setInnerHTML( 'annualizedField', annualized == "true" ? i18n_yes : i18n_no );

		setInnerHTML( 'indicatorTypeNameField', json.indicator.indicatorTypeName );

		var numeratorDescription = json.indicator.numeratorDescription;
		setInnerHTML( 'numeratorDescriptionField', numeratorDescription ? numeratorDescription : '[' + i18n_none + ']' );

		var denominatorDescription = json.indicator.denominatorDescription;
		setInnerHTML( 'denominatorDescriptionField', denominatorDescription ? denominatorDescription : '[' + i18n_none + ']' );

		var url = json.indicator.url;
		setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );

		var lastUpdated = json.indicator.lastUpdated;
		setInnerHTML( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + i18n_none + ']' );
		
		var dataSets = joinNameableObjects( json.indicator.dataSets );
		setInnerHTML( 'dataSetsField', dataSets ? dataSets : '[' + i18n_none + ']' );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove indicator
// -----------------------------------------------------------------------------

function removeIndicator( indicatorId, indicatorName )
{
    removeItem( indicatorId, indicatorName, i18n_confirm_delete, 'removeIndicator.action' );
}
