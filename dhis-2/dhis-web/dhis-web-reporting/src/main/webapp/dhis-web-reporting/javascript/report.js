function addReport()
{
    if ( $( "#reportForm #name" ).val().trim().length == 0  )
   	{
    	setHeaderDelayMessage( i18n_specify_name );
   		return false;
   	}

    if ( $( "#reportForm #id" ).val().trim().length == 0 && !hasText( "upload" ) )
   	{
    	setHeaderDelayMessage( i18n_please_specify_file );
   		return false;
   	}

    $( "#reportForm" ).submit();
}

function removeReport( id )
{
    removeItem( id, "", i18n_confirm_remove_report, "removeReport.action" );
}

function setReportType()
{
	var type = $( "#type :selected" ).val();
	
	if ( "jasperReportTable" == type )
	{
		$( ".jasperJdbcDataSource" ).hide();
		$( ".htmlDataSource" ).hide();
		$( ".jasperReportTableDataSource" ).show();
	}
	else if ( "jasperJdbc" == type )
	{
		$( ".jasperReportTableDataSource" ).hide();
		$( ".htmlDataSource" ).hide();
		$( ".jasperJdbcDataSource" ).show();
	}
	else if ( "html" == type )
	{
		$( ".jasperReportTableDataSource" ).hide();
		$( ".jasperJdbcDataSource" ).hide();
		$( ".htmlDataSource" ).show();
	}
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showReportDetails( reportId )
{
	jQuery.get( 'getReport.action', { "id": reportId }, function( json )
	{
		setInnerHTML( 'nameField', json.report.name );

		var reportTableName = json.report.reportTableName;
		setInnerHTML( 'reportTableNameField', reportTableName ? reportTableName : '[' + i18n_none + ']' );

		var orgGroupSets = json.report.orgGroupSets;
		setInnerHTML( 'orgGroupSetsField', orgGroupSets == 'true' ? i18n_yes : i18n_no );

		showDetails();
	});
}