
//------------------------------------------------------------------------------
// Get dataelements by dataset
//------------------------------------------------------------------------------

function getDataElementsByDataset()
{
	var dataSets = document.getElementById( 'dataSets' );
	var dataSetId = dataSets.options[ dataSets.selectedIndex ].value;
	setFieldValue('aggregationDataElementId','');
	setFieldValue('aggregationDataElementInput','');
	
	if( dataSetId == "" ){
		disable( 'dataElementsButton' );
		setFieldValue( 'aggregationDataElementInput','');
		return;
	}
	autoCompletedField();
}

function autoCompletedField()
{
	$( "#dataElementsButton" ).unbind('click');
	enable( 'dataElementsButton' );
	
	var input = jQuery( "#aggregationDataElementInput" )
		.autocomplete({
			delay: 0,
			minLength: 0,
			source: function( request, response ){
				$.ajax({
					url: "getDataElementsByDataset.action?id=" + getFieldValue('dataSets') + "&query=" + input.val(),
					dataType: "json",
					success: function(data) {
						response($.map(data.dataElements, function(item) {
							return {
								label: item.name,
								id: item.id
							};
						}));
					}
				});
			},
			select: function( event, ui ) {
				input.val(ui.item.value);
				setFieldValue('aggregationDataElementId',ui.item.id);
				input.autocomplete( "close" );
			},
			change: function( event, ui ) {
				if ( !ui.item ) {
					var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
						valid = false;
					select.children( "option" ).each(function() {
						if ( $( this ).text().match( matcher ) ) {
							this.selected = valid = true;
							return false;
						}
					});
					if ( !valid ) {
						// remove invalid value, as it didn't match anything
						$( this ).val( "" );
						select.val( "" );
						input.data( "autocomplete" ).term = "";
						return false;
					}
				}
			}
		}).addClass( "ui-widget" );

	input.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.label + "</a>" )
			.appendTo( ul );
	};
	
	var wrapper = this.wrapper = $( "<span style='width:200px'>" )
			.addClass( "ui-combobox" )
			.insertAfter( input );
	
	var button = $( "#dataElementsButton" )
		.attr( "tabIndex", -1 )
		.attr( "title", i18n_show_all_items )
		.appendTo( wrapper )
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.click(function() {
			// close if already visible
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return;
			}
			// work around a bug (likely same cause as #5265)
			$( this ).blur();
			// pass empty string as value to search for, displaying all results
			input.autocomplete( "search", "" );
			input.focus();
		});
}

//------------------------------------------------------------------------------
// Get Program Stages
//------------------------------------------------------------------------------

function getParams()
{
	clearListById( 'programStageId' );
  	clearListById( 'dataElements' );
	clearListById('caseProperty');
	var programId = getFieldValue( 'programId' );
	if( programId == ''){
		var caseProperty = jQuery( '#caseProperty' );
		caseProperty.append( '<option suggested="" title="' + i18n_total_of_patient_registration + '" value="[PT:count]">'+ i18n_total_of_patient_registration +'</option>' );
		caseProperty.append( '<option suggested="F, M" title="' + i18n_gender + '" value="[CP:gender]">'+ i18n_gender +'</option>' );
		caseProperty.append( '<option suggested="" title="' + i18n_dob_type + '" value="[CP:dobType]">'+ i18n_dob_type +'</option>' );
		caseProperty.append( '<option suggested="" title="' + i18n_age_days + '" value="[CP:age]">'+ i18n_age_days +'</option>' );
		
		disable('programProperty');
		disable('programStageProperty');
	}
	
	if(jQuery('#programId option:selected').attr('programType')==3){
		jQuery("[name=multiProgram]").remove();
		if( jQuery("[value=times]").attr('checked')!=undefined
			&& jQuery("[value=times]").attr('checked')!='true'  )
		{
			jQuery("[value=times]").attr('checked',true);
		}
	}
	
	jQuery.getJSON( 'getParamsByProgram.action',{ programId:programId }
		,function( json ) 
		{
			enable('programProperty');
			var programstage = jQuery('#programStageId');
			
			for ( i in json.programStages ) 
			{ 
				var id = json.programStages[i].id;
				var formularId = "[PS:" + id + "]";
				var name = json.programStages[i].name;

				programstage.append( "<option value='" + id + "' title='" + name + "'>" + name + "</option>" );
			}
			
			if( json.programStages.length > 1 )
			{
				programstage.prepend( "<option value='' title='" + i18n_all + "'>" + i18n_all + "</option>" );
			}
			byId('programStageId').options[0].selected = true;
			getPatientDataElements();
			
			clearListById( 'caseProperty' );
			var type = jQuery('#programId option:selected').attr('programType');
			if( type!='3')
			{
				var caseProperty = jQuery( '#caseProperty' );
				for ( i in json.fixedAttributes )
				{
					var id = json.fixedAttributes[i].id;
					var name = json.fixedAttributes[i].name;
					
					caseProperty.append( "<option value='" + id + "' title='" + name + "' suggested='" + json.fixedAttributes[i].suggested + "'>" + name + "</option>" );
				}
				
				for ( i in json.patientAttributes )
				{ 
					var id = json.patientAttributes[i].id;
					var name = json.patientAttributes[i].name;
					var suggested = json.patientAttributes[i].suggested;
					
					caseProperty.append( "<option value='" + id + "' title='" + name + "' suggested='" + suggested + "'>" + name + "</option>" );	
				}
			}
		});
}

function getProgramStages()
{
	var programId = getFieldValue( 'orgunitProgramId' );
	if(programId=='') return;
	
	clearListById( 'orgunitProgramStageId' );
	
	jQuery.getJSON( 'getProgramStages.action',{ id:programId }
		,function( json ) 
		{
			enable('programProperty');
			var programstage = jQuery('#orgunitProgramStageId');
			
			for ( i in json.programStages ) 
			{ 
				var id = json.programStages[i].id;
				var formularId = "[PSIC:" + id + "]";
				var name = json.programStages[i].name;

				programstage.append( "<option value='" + formularId + "' title='" + name + "'>" + name + "</option>" );
			}
		});
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage
//------------------------------------------------------------------------------

function getPatientDataElements()
{
	clearListById( 'dataElements' );
	clearListById( 'dataElementBackups' );
	clearListById( 'deSumId' );
	var programStageId = getFieldValue('programStageId');
	
	jQuery.getJSON( 'getPatientDataElements.action',
		{ 
			programId:getFieldValue( 'programId' ),
			programStageId:programStageId
		}
		,function( json )
		{
			if( programStageId!='' ){
				enable('programStageProperty');
			}
			else{
				disable('programStageProperty');
			}
			
			var dataElements = jQuery('#dataElements');
			var dataElementBackups = jQuery('#dataElementBackups');
			clearListById( 'dataElements' );
			clearListById( 'dataElementBackups' );
			var deSumId = jQuery('#deSumId');
			deSumId.append( "<option value='' >" + i18n_please_select + "</option>" );
			for ( i in json.dataElements )
			{ 
				dataElements.append( "<option value='" + json.dataElements[i].id + "' title='" + json.dataElements[i].name + "' dename='" + json.dataElements[i].name + "' decode='" + json.dataElements[i].code + "' suggested='" + json.dataElements[i].optionset + "' valuetype='" + json.dataElements[i].type + "'>" + json.dataElements[i].name + "</option>" );
				dataElementBackups.append( "<option value='" + json.dataElements[i].id + "' title='" + json.dataElements[i].name + "' dename='" + json.dataElements[i].name + "' decode='" + json.dataElements[i].code + "' suggested='" + json.dataElements[i].optionset + "' valuetype='" + json.dataElements[i].type + "'>" + json.dataElements[i].name + "</option>" );
				if( json.dataElements[i].type=='int')
				{
					deSumId.append( "<option value='" + json.dataElements[i].id + "' title='" + json.dataElements[i].name + "' suggested='" + json.dataElements[i].optionset + "' valuetype='" + json.dataElements[i].type + "'>" + json.dataElements[i].name + "</option>" );
				}
			}
			
		});
}

//-----------------------------------------------------------------
// Insert items into Condition
//-----------------------------------------------------------------

function insertDataElement( element )
{
	var progamId = getFieldValue('programId');
	var programStageId = getFieldValue('programStageId');
	programStageId = ( programStageId == "" ) ? "*" : programStageId;
	var dataElementId = element.options[element.selectedIndex].value;
	
	insertTextCommon( 'aggregationCondition', "[DE:" + progamId + "." + programStageId + "." + dataElementId + "]" );
	getConditionDescription();
}

function insertInfo( element, isProgramStageProperty )
{
	var id = "";
	if( isProgramStageProperty )
	{
		id = getFieldValue('programStageId');
	}
	else
	{
		id = getFieldValue('programId');
	}
	
	value = element.options[element.selectedIndex].value.replace( '*', id );
	insertTextCommon('aggregationCondition', value );
	getConditionDescription();
}

function insertOperator( value )
{
	insertTextCommon('aggregationCondition', ' ' + value + ' ' );
	getConditionDescription();
}

function insertBoolValue( value )
{
	insertTextCommon("aggregationCondition", " ='" + value + "' " );
	getConditionDescription();
}

// -----------------------------------------------------------------------------
// Remove Case Aggregation Condition
// -----------------------------------------------------------------------------

function removeCaseAggregation( caseAggregationId, caseAggregationName )
{
	removeItem( caseAggregationId, caseAggregationName, i18n_confirm_delete, 'removeCaseAggregation.action' );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showCaseAggregationDetails( caseAggregationId )
{
    jQuery.getJSON( 'getCaseAggregation.action', { id:caseAggregationId }, function ( json )
	{
		setInnerHTML( 'nameField', json.caseAggregation.name );	
		setInnerHTML( 'operatorField', json.caseAggregation.operator );
		setInnerHTML( 'aggregationDataElementField', json.caseAggregation.aggregationDataElement );
		setInnerHTML( 'optionComboField', json.caseAggregation.optionCombo );	
		setInnerHTML( 'aggregationExpressionField', json.caseAggregation.aggregationExpression );
		setInnerHTML( 'deSumField', json.caseAggregation.deSum );
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function getConditionDescription()
{
	$.postUTF8( 'getCaseAggregationDescription.action', 
		{ 
			condition:getFieldValue('aggregationCondition') 
		},function (data)
		{
			byId('aggregationDescription').innerHTML = data;
		},'html');
}

// -----------------------------------------------------------------------------
// Test condition
// -----------------------------------------------------------------------------

function testCaseAggregationCondition()
{
	var operator = jQuery('[name=operator]:checked').val();
	$.postUTF8( 'testCaseAggregationCondition.action', 
		{ 
			condition: getFieldValue('aggregationCondition'),
			deSumId: getFieldValue('deSumId'),
			operator: operator
		},function (json)
		{
			var type = json.response;
			
			if ( type == "input" )
			{
				showWarningMessage( i18n_run_fail );
			}
			else
			{
				showSuccessMessage( i18n_run_success );
			}
		});
}

function getSuggestedValues( sourceId, targetId )
{
	clearListById( targetId );
	
	var suggestedValues = jQuery('select[id=' + sourceId + '] option:selected').attr('suggested');	
	if( suggestedValues )
	{
		var arrValues = new Array();
		arrValues = suggestedValues.replace(/[//[]+/g,'').replace(/]/g, '').split(', ');

		var suggestedValueSelector = byId( targetId );
		for( var i=0; i< arrValues.length; i++ )
		{
			var option = document.createElement("option");
			var value = jQuery.trim( arrValues[i] );
			option.value = "'" + value + "'";
			option.text = value;
			option.title = value;

			suggestedValueSelector.add(option, null); 
		}
	}
}

function insertSingleValue( elementId )
{
	var element = byId( elementId );
	insertTextCommon('aggregationCondition', "=" + element.options[element.selectedIndex].value );
	getConditionDescription();
}

function insertMultiValues( elementId )
{
	var list = jQuery('select[id=' + elementId + '] option:selected')
	if( list.length == 0 )
	{
		return;
	}
	if( list.length > 1 )
	{
		var selectedValues = "";
		list.each(function(){
			selectedValues += jQuery(this).val() + ", ";
		});
		selectedValues = " IN @ " + selectedValues.substring( 0, selectedValues.length - 2) + " #";
		
		insertTextCommon('aggregationCondition', selectedValues );
		getConditionDescription();
	}
	else
	{
		insertSingleValue( elementId );
	}
}

function getCaseAggConditionByDataset()
{
	$.get( 'getCaseAggConditionByDataset.action',
		{
			dataSetId: getFieldValue( 'dataSetId' )
		}
		, function( html ) 
		{
			setTableStyles();
			setInnerHTML('list', html );
		} );
}

function showAddCaseAggregationForm()
{
	window.location.href='showAddCaseAggregationForm.action?dataSetId=' + getFieldValue( 'dataSetId' );
}

function operatorOnchange(operator)
{
	if( operator=='sum' || operator=='avg' 
		|| operator=='min' || operator=='max' ){
		enable('deSumId');
	}
	else{
		disable('deSumId');
	}
}

function filterDataElement( event, value, fieldName, backupFieldsName )
{
	// Remove all options in data element fields
	var field = jQuery('#' + fieldName + " option " ).remove();
	var valueType = getFieldValue('deValueType');
			
	jQuery('#' + backupFieldsName + " option ").each( function(){
		var option = jQuery(this);
		if (valueType=='' || valueType == option.attr('valueType') )
		{
			if(value.length == 0 )
			{
				jQuery('#' + fieldName ).append( "<option value='" + option.attr('value') + "' title='" + option.text() + "' suggested='" + option.attr('optionset') + "' valueType='" + option.attr('valueType') + "'>" + option.text() + "</option>" );				
			}
			else if (option.text().toLowerCase().indexOf( value.toLowerCase() ) != -1 )
			{
				jQuery('#' + fieldName ).append( "<option value='" + option.attr('value') + "' title='" + option.text() + "' suggested='" + option.attr('optionset') + "' valueType='" + option.attr('valueType') + "'>" + option.text() + "</option>" );				
			}
		}
	});
		    
}

function sortByOnChange( sortBy )
{
	if( sortBy == 1)
	{
		jQuery('#dataElements').each(function() {

			// Keep track of the selected option.
			var selectedValue = $(this).val();

			// sort it out
			$(this).html($("option", $(this)).sort(function(a, b) { 
				return $(a).attr('dename') == $(b).attr('dename') ? 0 : $(a).attr('dename') < $(b).attr('dename') ? -1 : 1 
			}));

			// Select one option.
			$(this).val(selectedValue);

		});
	}
	else
	{
		jQuery('#dataElements').each(function() {

			// Keep track of the selected option.
			var selectedValue = $(this).val();

			// sort it out
			$(this).html($("option", $(this)).sort(function(a, b) { 
				return $(a).attr('decode') == $(b).attr('decode') ? 0 : $(a).attr('decode') < $(b).attr('decode') ? -1 : 1 
			}));

			// Select one option.
			$(this).val(selectedValue);

		});
	} 
}

function displayNameOnChange( displayName )
{
	// display - name
	if(displayName=='1'){
		jQuery('#dataElements option').each(function(){
			var item = jQuery(this);
			item[0].text = item.attr('dename');
			item[0].title = item[0].text;
		});
		jQuery('#dataElementBackups option').each(function(){
			var item = jQuery(this);
			item[0].text = item.attr('dename');
		});
	}
	// display - code
	else if(displayName=='2'){
		jQuery('#dataElements option').each(function(){
			var item = jQuery(this);
			item[0].text = item.attr('decode');
			item[0].title = item[0].text;
		});
		jQuery('#dataElementBackups option').each(function(){
			var item = jQuery(this);
			item[0].text = item.attr('decode');
		});
	}
	// display - code and name
	else{
		jQuery('#dataElements option').each(function(){
			var item = jQuery(this);
			item[0].text = "(" + item.attr('decode') + ") " + item.attr('dename');
			item[0].title = item[0].text;
		});
		jQuery('#dataElementBackups option').each(function(){
			var item = jQuery(this);
			item[0].text = "(" + item.attr('decode') + ") " + item.attr('dename');
		});
	}
}

function cancelOnClick()
{
	var dataSetId = getFieldValue("dataSets"); 
	window.location.href='caseAggregation.action?dataSetId=' + dataSetId;
}
