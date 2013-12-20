isAjax = true;

function validateAddUpdateSqlView( mode )
{
	var name = $("#name").val(); 
	var sqlquery = $("#sqlquery").val(); 

	$.getJSON(
		"validateAddUpdateSqlView.action",
		{
			"name": name,
			"sqlquery": sqlquery,
			"mode": mode
		},
		function( json )
		{
			if ( json.response == "success" )
			{	
				if ( mode == "add" )
				{
					byId("addSqlViewForm").submit();
					return;
				}
				byId("updateSqlViewForm").submit();
			}
			else if ( json.response == "input" )
			{
				setMessage( json.message );
			}
		}
	);
}
 
function removeSqlViewObject( viewId, viewName )
{
	removeItem( viewId, viewName, i18n_confirm_delete, 'removeSqlViewObject.action' );
}

function showSqlViewDetails( viewId )
{
    jQuery.postJSON( 'getSqlView.action', { id: viewId }, function ( json ) {
	
		setInnerHTML( 'nameField', json.sqlView.name );
		
		var description = json.sqlView.description;
		setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );
		setInnerHTML( 'sqlQueryField', json.sqlView.sqlquery );
			
		showDetails();
	});
}

/**
 * Execute query to create a new view table
 * 
 * @param viewId the item identifier.
 */
function runSqlViewQuery( viewId )
{
	$.getJSON(
		"executeSqlViewQuery.action", { "id": viewId },
		function( json ) {
			if ( json.response == "success" ) {
				setHeaderDelayMessage( json.message );
			} else {
				setMessage( json.message );
			}
		}
	);
}

// -----------------------------------------------------------------------
// View data from the specified view table
// -----------------------------------------------------------------------

function showDataSqlViewForm( viewId )
{
	$.getJSON(
		"checkViewTableExistence.action",
		{
			"id": viewId
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				window.location.href = "exportSqlView.action?id=" + viewId;
			}
			else if ( json.response == "error" )
			{
				setHeaderDelayMessage( json.message );
			}
		}
	);
}

function exportSqlView( id, type )
{
	var url = "exportSqlView.action?id=" + id + "&type=" + type;

    window.location.href = url;
}