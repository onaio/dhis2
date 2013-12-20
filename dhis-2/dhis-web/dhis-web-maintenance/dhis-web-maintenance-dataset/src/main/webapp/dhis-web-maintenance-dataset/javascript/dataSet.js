// -----------------------------------------------------------------------------
// DataSet details form
// -----------------------------------------------------------------------------

function showDataSetDetails( dataSetId )
{
    jQuery.get( '../dhis-web-commons-ajax-json/getDataSet.action', {
        id : dataSetId
    }, function( json )
    {
        setInnerHTML( 'nameField', json.dataSet.name );
        setInnerHTML( 'descriptionField', json.dataSet.description );
        setInnerHTML( 'frequencyField', json.dataSet.frequency );
        setInnerHTML( 'dataElementCountField', json.dataSet.dataElementCount );
        setInnerHTML( 'dataEntryFormField', json.dataSet.dataentryform );

        showDetails();
    } );
}

// -----------------------------------------------------------------------------
// Delete DataSet
// -----------------------------------------------------------------------------

var tmpDataSetId;

var tmpSource;

function removeDataSet( dataSetId, dataSetName )
{
    removeItem( dataSetId, dataSetName, i18n_confirm_delete, 'delDataSet.action' );
}

// ----------------------------------------------------------------------
// DataEntryForm
// ----------------------------------------------------------------------

function viewDataEntryForm( dataSetId )
{
    window.location.href = 'viewDataEntryForm.action?dataSetId=' + dataSetId;
}
