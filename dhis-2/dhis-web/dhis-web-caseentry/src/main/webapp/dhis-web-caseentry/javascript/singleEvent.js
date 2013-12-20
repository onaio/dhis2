
var _continue = false;

function orgunitSelected( orgUnits, orgUnitNames )
{	
	var width = jQuery('#programIdAddPatient').width();
	jQuery('#programIdAddPatient').width(width-30);
	showById( "programLoader" );
	disable('programIdAddPatient');
	hideById('addNewDiv');
	organisationUnitSelected( orgUnits, orgUnitNames );
	clearListById('programIdAddPatient');
	$.postJSON( 'singleEventPrograms.action', {}, function( json )
		{
			var count = 0;
			for ( i in json.programs ) {
				jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" programStageId="' + json.programs[i].programStageId + '" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
			}
			
			if(json.programs.length==0){
				jQuery( '#programIdAddPatient').prepend( '<option value="" >' + i18n_none_program + '</option>' );
			}
			else if(json.programs.length>1){
				jQuery( '#programIdAddPatient').prepend( '<option value="" selected>' + i18n_please_select + '</option>' );
			}
			
			enableBtn();
			hideById('programLoader');
			jQuery('#programIdAddPatient').width(width);
			enable('programIdAddPatient');
		});
}
selection.setListenerFunction( orgunitSelected );

function showAddPatientForm()
{
	hideById('dataEntryMenu');
	showById('eventActionMenu');
	showById('nextEventLink');
	hideById('contentDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	setInnerHTML('addNewDiv','');
	setInnerHTML('dataRecordingSelectDiv','');
	jQuery('#loaderDiv').show();
	jQuery('#addNewDiv').load('showEventWithRegistrationForm.action',
		{
			programId: getFieldValue('programIdAddPatient')
		}, function()
		{
			setInnerHTML('singleProgramName',jQuery('#programIdAddPatient option:selected').text());	unSave = true;
			showById('singleProgramName');
			showById('addNewDiv');
			jQuery('#loaderDiv').hide();
		});
}

function showUpdatePatientForm( patientId )
{
	hideById('dataEntryMenu');
	showById('eventActionMenu');
	hideById('nextEventLink');
	setInnerHTML('singleProgramName',jQuery('#programIdAddPatient option:selected').text());	
	showById('singleProgramName');
	setInnerHTML('addNewDiv','');
	unSave = false;
	showSelectedDataRecoding(patientId, getFieldValue('programIdAddPatient'));
}

function addEventForPatientForm( divname )
{
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
	
	jQuery("#" + divname + " [id=dobType]").change(function() {
		dobTypeOnChange( divname );
	});
}

function validateData()
{
	var params = "programId=" + getFieldValue('programIdAddPatient') + "&" + getParamsForDiv('patientForm');
	$("#patientForm :input").attr("disabled", true);
	$("#entryForm :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: params,
		success: function( data ){
			var type = jQuery(data).find('message').attr('type');
			var message = jQuery(data).find('message').text();
			
			if ( type == 'success' )
			{
				removeDisabledIdentifier( );
				addPatient();
			}
			else
			{
				$("#patientForm :input").attr("disabled", true);
				if ( type == 'error' )
				{
					showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
				}
				else if ( type == 'input' )
				{
					showWarningMessage( message );
				}
				else if( type == 'duplicate' )
				{
					showListPatientDuplicate(data, false);
				}
					
				$("#patientForm :input").attr("disabled", false);
			}
		}
    });	
}

function addPatient()
{
	$.ajax({
		type: "POST",
		url: 'addPatient.action',
		data: getParamsForDiv('patientForm'),
		success: function(json) {
			var patientId = json.message.split('_')[0];
			addData( getFieldValue('programIdAddPatient'), patientId );
		}
     });
}

function addData( programId, patientId )
{		
	var params = "programId=" + getFieldValue('programIdAddPatient');
		params += "&patientId=" + patientId;
		params += "&" + getParamsForDiv('entryForm');
		
	$.ajax({
		type: "POST",
		url: 'saveValues.action',
		data: params,
		success: function(json) {
			if( _continue==true )
			{
				$("#patientForm :input").attr("disabled", false);
				$("#entryForm :input").attr("disabled", false);
				jQuery('#patientForm :input').each(function()
				{
					var type=$( this ).attr('type');
					if(type=='checkbox'){
						this.checked = false;
					}
					if(type!='button'){
						$( this ).val('');
					}
					enable(this.id);
				});
				jQuery('#entryForm :input').each(function()
				{
					var type=$( this ).attr('type');
					if(type=='checkbox'){
						this.checked = false;
					}
					else if(type!='button'){
						$( this ).val('');
					}
				});
			}
			else
			{
				setInnerHTML('singleProgramName','');
				hideById('addNewDiv');
				if( getFieldValue('listAll')=='true'){
					listAllPatient();
				}
				else{
					showById('searchDiv');
					showById('contentDiv');
				}
			}
			showSuccessMessage( i18n_save_success );
		}
     });
    return false;
}

function showListPatientDuplicate( rootElement, validate )
{
	var message = jQuery(rootElement).find('message').text();
	var patients = jQuery(rootElement).find('patient');
	
	var sPatient = "";
	jQuery( patients ).each( function( i, patient )
        {
			sPatient += "<hr style='margin:5px 0px;'><table>";
			sPatient += "<tr><td class='bold'>" + i18n_patient_system_id + "</td><td>" + jQuery(patient).find('systemIdentifier').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_full_name + "</td><td>" + jQuery(patient).find('fullName').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_gender + "</td><td>" + jQuery(patient).find('gender').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_date_of_birth + "</td><td>" + jQuery(patient).find('dateOfBirth').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_age + "</td><td>" + jQuery(patient).find('age').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_phone_number + "</td><td>" + jQuery(patient).find('phoneNumber').text() + "</td></tr>";
        	
			var identifiers = jQuery(patient).find('identifier');
        	if( identifiers.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2' class='bold'>" + i18n_patient_identifiers + "</td></tr>";

        		jQuery( identifiers ).each( function( i, identifier )
				{
        			sPatient +="<tr class='identifierRow'>"
        				+"<td class='bold'>" + jQuery(identifier).find('name').text() + "</td>"
        				+"<td>" + jQuery(identifier).find('value').text() + "</td>	"	
        				+"</tr>";
        		});
        	}
			
        	var attributes = jQuery(patient).find('attribute');
        	if( attributes.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2' class='bold'>" + i18n_patient_attributes + "</td></tr>";

        		jQuery( attributes ).each( function( i, attribute )
				{
        			sPatient +="<tr class='attributeRow'>"
        				+"<td class='bold'>" + jQuery(attribute).find('name').text() + "</td>"
        				+"<td>" + jQuery(attribute).find('value').text() + "</td>	"	
        				+"</tr>";
        		});
        	}
        	sPatient += "<tr><td colspan='2'><input type='button' id='"+ jQuery(patient).find('id').first().text() + "' value='" + i18n_show_data_entry + "' onclick='showSelectedDataRecoding(" + jQuery(patient).find('id').first().text() + ");showEntryFormDiv(); '/></td></tr>";
        	sPatient += "</table>";
		});
		
		var result = i18n_duplicate_warning;
		if( !validate )
		{
			result += "<input type='button' value='" + i18n_create_new_patient + "' onClick='removeDisabledIdentifier( );addPatient();'/>";
			result += "<br><hr style='margin:5px 0px;'>";
		}
		
		result += "<br>" + sPatient;
		jQuery('#resultSearchDiv' ).html( result );
		jQuery('#resultSearchDiv' ).dialog({
			title: i18n_duplicated_patient_list,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 400
		});
}

function showEntryFormDiv()
{
	hideById('singleEventForm');
	jQuery("#resultSearchDiv").dialog("close");
}

function removeDisabledIdentifier()
{
	jQuery("input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}

function backEventList()
{
	showById('dataEntryMenu');
	hideById('eventActionMenu');
	hideById('singleProgramName');
	showSearchForm();
	if( getFieldValue('listAll')=='true'){
		listAllPatient();
	}
	hideById('backBtnFromEntry');
}

// --------------------------------------------------------
// Check an available person allowed to enroll a program
// --------------------------------------------------------

function validateAllowEnrollment( patientId, programId  )
{	
	jQuery.getJSON( "validatePatientProgramEnrollment.action",
		{
			patientId: patientId,
			programId: programId
		}, 
		function( json ) 
		{    
			jQuery('#loaderDiv').hide();
			hideById('message');
			var type = json.response;
			if ( type == 'success' ){
				showSelectedDataRecoding(patientId, programId );
			}
			else if ( type == 'input' ){
				showWarningMessage( json.message );
			}
		});
}

function completedAndAddNewEvent()
{
	_continue=true;
	jQuery("#singleEventForm").submit();
}