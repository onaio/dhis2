
function addNewForm( programId ){
	window.location.href='viewPatientRegistrationForm.action?programId=' + programId;
}

function updateNewForm( registrationFormId, programId ){
	window.location.href='viewPatientRegistrationForm.action?programId=' + programId + '&id=' + registrationFormId;
}

function removeRegistrationForm( programId, programName )
{
	var result = window.confirm( i18n_confirm_delete + "\n\n" + programName );
    
    if ( result )
    {
		jQuery.postJSON("delRegistrationFormAction.action", {id:programId}
		, function(json) {
			hideById('active_' + programId);
			showById('define_' + programId);
			hideById('update_' + programId);
			showById('add_' + programId);
		});
	}
}
