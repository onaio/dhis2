
var currentPeriodOffset = 0;
var periodTypeFactory = new PeriodType();

//------------------------------------------------------------------------------
// Get and set methods
//------------------------------------------------------------------------------

function getDataSetReport()
{
    var dataSetReport = {
        ds: $( "#dataSetId" ).val(),
        periodType: $( "#periodType" ).val(),
        pe: $( "#periodId" ).val(),
        ou: selectionTreeSelection.getSelectedUid()[0],
        selectedUnitOnly: $( "#selectedUnitOnly" ).is( ":checked" ),
        offset: currentPeriodOffset
    };
    
    var groups = "";
    
    $( "[name='groupSet']" ).each( function( index, value ) {
    	var item = $( this ).val();
    	if ( item )
    	{
    		groups += item + ";";
    	}
    } );
    
    if ( groups )
    {
    	dataSetReport["groups"] = groups;
    }
    
    return dataSetReport;
}

function setDataSetReport( dataSetReport )
{
	$( "#dataSetId" ).val( dataSetReport.dataSet );
	$( "#periodType" ).val( dataSetReport.periodType );
	
	currentPeriodOffset = dataSetReport.offset;
	
	displayPeriods();
	$( "#periodId" ).val( dataSetReport.period );
	
	selectionTreeSelection.setMultipleSelectionAllowed( false );
	selectionTree.buildSelectionTree();
	
	$( "body" ).on( "oust.selected", function() 
	{
		$( "body" ).off( "oust.selected" );
		generateDataSetReport();
	} );
}

//------------------------------------------------------------------------------
// Period
//------------------------------------------------------------------------------

function displayPeriods()
{
    var periodType = $( "#periodType" ).val();
    var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.reverse( periods );
    periods = periodTypeFactory.filterFuturePeriodsExceptCurrent( periods );

    $( "#periodId" ).removeAttr( "disabled" );
    clearListById( "periodId" );

    for ( i in periods )
    {
        addOptionById( "periodId", periods[i].iso, periods[i].name );
    }
}

function displayNextPeriods()
{
    if ( currentPeriodOffset < 0 ) // Cannot display future periods
    {
        currentPeriodOffset++;
        displayPeriods();
    }
}

function displayPreviousPeriods()
{
    currentPeriodOffset--;
    displayPeriods();
}

//------------------------------------------------------------------------------
// Run report
//------------------------------------------------------------------------------

//TODO rewrite to use uid only

function drillDownDataSetReport( orgUnitId, orgUnitUid )
{
	selectionTree.clearSelectedOrganisationUnits();
	selectionTreeSelection.select( orgUnitId );
	
	var dataSetReport = getDataSetReport();
	dataSetReport["ou"] = orgUnitUid;
	displayDataSetReport( dataSetReport );
}

function generateDataSetReport()
{
	var dataSetReport = getDataSetReport();
	displayDataSetReport( dataSetReport );
}

function displayDataSetReport( dataSetReport )
{	
    if ( !dataSetReport.ds )
    {
        setHeaderMessage( i18n_select_data_set );
        return false;
    }
    if ( !dataSetReport.pe )
    {
        setHeaderMessage( i18n_select_period );
        return false;
    }
    if ( !selectionTreeSelection.isSelected() )
    {
        setHeaderMessage( i18n_select_organisation_unit );
        return false;
    }
    
    hideHeaderMessage();
    hideCriteria();
    hideContent();
    showLoader();
	
    delete dataSetReport.periodType;
    delete dataSetReport.offset;
    
    $.get( 'generateDataSetReport.action', dataSetReport, function( data ) {
    	$( '#content' ).html( data );
    	hideLoader();
    	showContent();
    	setTableStyles();
    } );
}

function exportDataSetReport( type )
{
	var dataSetReport = getDataSetReport();
	
	var url = "generateDataSetReport.action" + 
		"?ds=" + dataSetReport.ds +
	    "&pe=" + dataSetReport.pe +
	    "&selectedUnitOnly=" + dataSetReport.selectedUnitOnly +
	    "&ou=" + dataSetReport.ou +
	    "&type=" + type;
	    
	window.location.href = url;
}

function setUserInfo( username )
{
	$( "#userInfo" ).load( "../dhis-web-commons-ajax-html/getUser.action?username=" + username, function() {
		$( "#userInfo" ).dialog( {
	        modal : true,
	        width : 350,
	        height : 350,
	        title : "User"
	    } );
	} );	
}

function showCriteria()
{
	$( "#criteria" ).show( "fast" );
}

function hideCriteria()
{
	$( "#criteria" ).hide( "fast" );
}

function showContent()
{
	$( "#content" ).show( "fast" );
	$( ".downloadButton" ).show();
	$( "#interpretationArea" ).autogrow();
}

function hideContent()
{
	$( "#content" ).hide( "fast" );
	$( ".downloadButton" ).hide();
}

function showAdvancedOptions()
{
	$( "#advancedOptionsLink" ).hide();
	$( "#advancedOptions" ).show();
}

//------------------------------------------------------------------------------
// Share
//------------------------------------------------------------------------------

function viewShareForm() // Not in use
{
	$( "#shareForm" ).dialog( {
		modal : true,
		width : 550,
		resizable: false,
		title : i18n_share_your_interpretation
	} );
}

function shareInterpretation()
{
	var dataSetReport = getDataSetReport();
    var text = $( "#interpretationArea" ).val();
    
    if ( text.length && $.trim( text ).length )
    {
    	text = $.trim( text );
    	
	    var url = "../api/interpretations/dataSetReport/" + $( "#currentDataSetId" ).val() +
	    	"?pe=" + dataSetReport.pe +
	    	"&ou=" + dataSetReport.ou;
	    	    
	    $.ajax( url, {
	    	type: "POST",
	    	contentType: "text/html",
	    	data: text,
	    	success: function() {	    		
	    		$( "#interpretationArea" ).val( "" );
	    		setHeaderDelayMessage( i18n_interpretation_was_shared );
	    	}    	
	    } );
    }
}

//------------------------------------------------------------------------------
// Hooks in custom forms - must be present to avoid errors in forms
//------------------------------------------------------------------------------

function onValueSave( fn )
{
	// Do nothing
}

function onFormLoad( fn )
{
	// Do nothing
}
