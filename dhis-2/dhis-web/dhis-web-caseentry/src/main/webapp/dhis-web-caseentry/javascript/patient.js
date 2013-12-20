function organisationUnitSelected( orgUnits, orgUnitNames )
{	
	showById('selectDiv');
	showById('searchDiv');
	showById( "programLoader" );
	disable('programIdAddPatient');
	showById('mainLinkLbl');
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
	hideById('patientDashboard');
	enable('listPatientBtn');
	enable('addPatientBtn');
	enable('advancedSearchBtn');
	enable('searchObjectId');
	setInnerHTML('patientDashboard','');
	setInnerHTML('editPatientDiv','');
	
	setFieldValue("orgunitName", orgUnitNames[0]);
	
	clearListById('programIdAddPatient');
	jQuery.get("getAllPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddPatient').append( '<option value="">' + i18n_view_all + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
			hideById('programLoader');
			enable('programIdAddPatient');
		});
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// List && Search patients
// -----------------------------------------------------------------------------

function Patient()
{
	var patientId;
	var	fullName;
	
	this.advancedSearch = function(params)
	{
		$.ajax({
			url: 'searchRegistrationPatient.action',
			type:"POST",
			data: params,
			success: function( html ){
					setTableStyles();
					statusSearching = 1;
					setInnerHTML( 'listPatientDiv', html );
					showById('listPatientDiv');
					setFieldValue('listAll',false);
					showById('hideSearchCriteriaDiv');
					jQuery( "#loaderDiv" ).hide();
				}
			});
	};
	
	this.remove = function( confirm_delete_patient )
	{
		removeItem( this.patientId, this.fullName, confirm_delete_patient, 'removePatient.action' );
	};
	
	this.add = function( programId, related, params,isContinue)
	{
		$.ajax({
		  type: "POST",
		  url: 'addPatient.action',
		  data: params,
		  success: function(json) {
			if(json.response=='success')
			{
				var patientId = json.message.split('_')[0];
				var	dateOfIncident = jQuery('#patientForm [id=dateOfIncident]').val();
				var enrollmentDate = jQuery('#patientForm [id=enrollmentDate]').val();
				
				// Enroll patient into the program
				if( programId !='' && enrollmentDate != '')
				{
					jQuery.postJSON( "saveProgramEnrollment.action",
					{
						patientId: patientId,
						programId: programId,
						dateOfIncident: dateOfIncident,
						enrollmentDate: enrollmentDate
					}, 
					function( json ) 
					{    
						if(isContinue){
							jQuery("#patientForm :input").each( function(){
								if( $(this).attr('id') != "registrationDate" 
									&& $(this).attr('type') != 'button'
									&& $(this).attr('type') != 'submit' 
									&& $(this).attr('id') !='enrollmentDate' )
								{
									$(this).val("");
								}
							});
							$("#patientForm :input").attr("disabled", false);
							$("#patientForm").find("select").attr("disabled", false);
						}
						else{
							showPatientDashboardForm( patientId );
						}
					});
				}
				else if(isContinue){
						jQuery("#patientForm :input").each( function(){
							if( $(this).attr('id') != "registrationDate" 
								&& $(this).attr('type') != 'button'
								&& $(this).attr('type') != 'submit'  )
							{
								$(this).val("");
							}
						});
						$("#patientForm :input").attr("disabled", false);
						$("#patientForm").find("select").attr("disabled", false);
				}
				else
				{
					$("#patientForm :input").attr("disabled", false);
					$("#patientForm").find("select").attr("disabled", false);
					showPatientDashboardForm( patientId );
				}
			}
		  }
		 });
	}
}

Patient.listAll = function()
{
	jQuery('#loaderDiv').show();
	contentDiv = 'listPatientDiv';
	if( getFieldValue('programIdAddPatient')=='')
	{
		jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
				listAll:true
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listPatientDiv');
				jQuery('#loaderDiv').hide();
			});
	}
	else 
	{
		jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
				listAll:false,
				searchByUserOrgunits: false,
				searchBySelectedOrgunit: true,
				programId: getFieldValue('programIdAddPatient'),
				searchTexts: 'prg_' + getFieldValue('programIdAddPatient'),
				statusEnrollment: getFieldValue('statusEnrollment')
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listPatientDiv');
				jQuery('#loaderDiv').hide();
			});
	}
}

function listAllPatient()
{
	jQuery('#loaderDiv').show();
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('migrationPatientDiv');
	hideById('advanced-search');
	
	Patient.listAll();
}

function advancedSearch( params )
{
	var patient = new Patient();
	patient.advancedSearch( params );
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, fullName, i18n_confirm_delete_patient )
{
	var patient = new Patient();
	patient.patientId = patientId;
	patient.fullName = fullName;
	patient.remove( i18n_confirm_delete_patient );
}

// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function addPatient( programId, related, isContinue )
{		
	var patient = new Patient();
	var params = 'programId=' + programId + '&' + getParamsForDiv('patientForm');
	patient.add(programId,related,params, isContinue );
	registrationProgress = true;
    return false;
}

function showAddPatientForm( programId, patientId, relatedProgramId )
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('migrationPatientDiv');
	setInnerHTML('addRelationshipDiv','');
	setInnerHTML('patientDashboard','');
	
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showAddPatientForm.action',
		{
			programId: programId,
			patientId: patientId,
			relatedProgramId: relatedProgramId
		}, function()
		{
			showById('editPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	
}

function validateAddPatient( programId, related, isContinue )
{	
	var params = "programId=" + programId + "&" + getParamsForDiv('patientForm');
	$("#patientForm :input").attr("disabled", true);
	$("#patientForm").find("select").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: params,
		success: function(data){
			addValidationCompleted( programId, related, data,isContinue);
		}
	});	
}

function addValidationCompleted( programId, related, data, isContinue )
{
    var type = jQuery(data).find('message').attr('type');
	var message = jQuery(data).find('message').text();
	
	if ( type == 'success' )
	{
		removeDisabledIdentifier( );
		addPatient( programId, related, isContinue );
	}
	else
	{
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
		$("#patientForm").find("select").attr("disabled", false);
	}
}

function addRelationship()
{
	jQuery('#loaderDiv').show();
	var params = getParamsForDiv('addRelationshipDiv');
		params += "&relationshipFromA=" + jQuery('#patientForm option:selected').attr("relationshipFromA");
	$.ajax({
		type: "POST",
		url: 'addRelationshipPatient.action',
		data: params,
		success: function( json ) {
			hideById('addRelationshipDiv');
			showById('selectDiv');
			showById('searchDiv');
			showById('listPatientDiv');
			jQuery('#loaderDiv').hide();

			if( getFieldValue( 'isShowPatientList' ) == 'false' ){
				showRelationshipList( getFieldValue('relationshipId') );
			}
			else{
				loadPatientList();
			}
		}});
    return false;
}


// ----------------------------------------------------------------
// Click Back to main form
// ----------------------------------------------------------------

function onClickBackBtn()
{
	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	showById('listPatientDiv');
	
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
	setInnerHTML('patientDashboard','');
	loadPatientList();
}

function loadPatientList()
{
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	hideById('migrationPatientDiv');
	setInnerHTML('patientDashboard','');
	setInnerHTML('editPatientDiv','');

	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	if(statusSearching==2)
	{
		return;
	}
	else if( statusSearching == 0)
	{
		Patient.listAll();
	}
	else if( statusSearching == 1 )
	{
		validateAdvancedSearch();
	}
	else if( statusSearching == 3 )
	{
		showById('listPatientDiv');
	}
}

//------------------------------------------------------------------------------
// Load data entry form
//------------------------------------------------------------------------------

function loadDataEntry( programStageInstanceId )
{
	setInnerHTML('dataEntryFormDiv', '');
	showById('dataEntryFormDiv');
	showById('executionDateTB');
	setFieldValue( 'dueDate', '' );
	setFieldValue( 'executionDate', '' );
	disable('validationBtn');
	disableCompletedButton(true);
	disable('uncompleteBtn');
	setFieldValue( 'programStageInstanceId', programStageInstanceId );
			
	$('#executionDate').unbind("change");
	$('#executionDate').change(function() {
		saveExecutionDate( getFieldValue('programId'), programStageInstanceId, byId('executionDate') );
	});
	
	jQuery(".stage-object-selected").removeClass('stage-object-selected');
	var selectedProgramStageInstance = jQuery( '#' + prefixId + programStageInstanceId );
	selectedProgramStageInstance.addClass('stage-object-selected');
	setFieldValue( 'programStageId', selectedProgramStageInstance.attr('psid') );
	
	showLoader();	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: programStageInstanceId
		},function( )
		{
			var editDataEntryForm = getFieldValue('editDataEntryForm');
			if(editDataEntryForm=='true')
			{
				var executionDate = jQuery('#executionDate').val();
				var completed = jQuery('#entryFormContainer input[id=completed]').val();
				var irregular = jQuery('#entryFormContainer input[id=irregular]').val();
				var reportDateDes = jQuery("#ps_" + programStageInstanceId).attr("reportDateDes");
				setInnerHTML('reportDateDescriptionField',reportDateDes);
				enable('validationBtn');
				if( executionDate == '' )
				{
					disable('validationBtn');
				}
				else if( executionDate != ''){
					if ( completed == 'false' ){
						disableCompletedButton(false);
					}
					else if( completed == 'true' ){
						disableCompletedButton(true);
					}
				}
				
				$(window).scrollTop(200);
			}
			else
			{
				blockEntryForm();
				disable('executionDate');
				hideById('inputCriteriaDiv');
			}
			
			resize();
			hideLoader();
			hideById('contentDiv');
			
			if(registrationProgress)
			{
				var reportDateToUse = selectedProgramStageInstance.attr('reportDateToUse');
				if(reportDateToUse != "undefined" && reportDateToUse!='' && $('#executionDate').val() == '' ){
					$('#executionDate').val(reportDateToUse);
					$('#executionDate').change();
				}
			}
			registrationProgress = false;
		
		} );
	
}
