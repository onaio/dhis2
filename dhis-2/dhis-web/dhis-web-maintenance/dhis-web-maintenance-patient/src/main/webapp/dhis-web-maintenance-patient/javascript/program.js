// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramDetails( programId )
{
	jQuery.getJSON( "getProgram.action", {
		id:programId
	}, function(json){
		setInnerHTML( 'nameField', json.program.name );
		setInnerHTML( 'descriptionField', json.program.description );
		
		var type = i18n_multiple_events_with_registration;
		if( json.program.type == "2" )
			type = i18n_single_event_with_registration;
		else if( json.program.type == "3"  )
			type = i18n_single_event_without_registration;
		setInnerHTML( 'typeField', type ); 
		
		var displayIncidentDate = ( json.program.displayIncidentDate == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'displayIncidentDateField', displayIncidentDate );   	
		
		var ignoreOverdueEvents = ( json.program.ignoreOverdueEvents == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'ignoreOverdueEventsField', ignoreOverdueEvents );   	
		
		var onlyEnrollOnce = ( json.program.onlyEnrollOnce == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'onlyEnrollOnceField', onlyEnrollOnce );   	
		
		var displayOnAllOrgunit= ( json.program.displayOnAllOrgunit == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'displayOnAllOrgunitField', displayOnAllOrgunit );   	
		
		var useBirthDateAsIncidentDate = ( json.program.useBirthDateAsIncidentDate == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'useBirthDateAsIncidentDateField', useBirthDateAsIncidentDate );   	
		
		var useBirthDateAsEnrollmentDate = ( json.program.useBirthDateAsEnrollmentDate == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'useBirthDateAsEnrollmentDateField', useBirthDateAsEnrollmentDate );   	
		
		var selectEnrollmentDatesInFuture= ( json.program.selectEnrollmentDatesInFuture == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'selectEnrollmentDatesInFutureField', selectEnrollmentDatesInFuture );   	
		
		var selectIncidentDatesInFuture= ( json.program.selectIncidentDatesInFuture == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'selectIncidentDatesInFutureField', selectIncidentDatesInFuture );   	
		
		var dataEntryMethod= ( json.program.dataEntryMethod == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'dataEntryMethodField', dataEntryMethod );   	
		
		setInnerHTML( 'dateOfEnrollmentDescriptionField', json.program.dateOfEnrollmentDescription );   
		setInnerHTML( 'dateOfIncidentDescriptionField', json.program.dateOfIncidentDescription );   		
		setInnerHTML( 'programStageCountField',  json.program.programStageCount );
		setInnerHTML( 'noAttributesField', json.program.noAttributes );
		setInnerHTML( 'noIdentifierTypesField', json.program.noIdentifierTypes );
		
		showDetails();
	});   
}

// -----------------------------------------------------------------------------
// Remove Program
// -----------------------------------------------------------------------------

function removeProgram( programId, name )
{
	removeItem( programId, name, i18n_confirm_delete, 'removeProgram.action' );
}

function relationshipTypeOnchange()
{
	clearListById( 'relationshipSide' );
	var relationshipType = jQuery('#relationshipTypeId option:selected');
	if( relationshipType.val() != "")
	{
		var aIsToB = relationshipType.attr('aIsToB');
		var bIsToA = relationshipType.attr('bIsToA');
		
		var relationshipSide = jQuery("#relationshipFromA");
		relationshipSide.append( '<option value="false">' + aIsToB + '</option>' );
		relationshipSide.append( '<option value="true">' + bIsToA + '</option>' );
	}
}

function programOnChange()
{
	var type = getFieldValue('type');
	
	// anonymous
	if(type == "3")
	{
		disable('onlyEnrollOnce');
		disable('dateOfEnrollmentDescription');
		disable("displayIncidentDate");
		disable("dateOfIncidentDescription");
		disable("generatedByEnrollmentDate");
		disable("availablePropertyIds");
		disable('ignoreOverdueEvents');
		hideById('selectedList');
		hideById('programMessageTB');
		
		jQuery("[name=displayed]").attr("disabled", true);
		jQuery("[name=displayed]").removeAttr("checked");
		
		jQuery("[name=nonAnonymous]").hide();
	}
	else{
		enable('onlyEnrollOnce');
		jQuery("[name=displayed]").prop("disabled", false);
		enable("availablePropertyIds");
		enable("generatedByEnrollmentDate");
		enable('dateOfEnrollmentDescription');
		enable("displayIncidentDate");
		enable('ignoreOverdueEvents');
		showById('programMessageTB');
		showById("selectedList");
		
		jQuery("[name=nonAnonymous]").show();
		if( type == 2 ){
			disable('ignoreOverdueEvents');
			disable('onlyEnrollOnce');
			disable('generatedByEnrollmentDate');
		}
		
		if(byId('displayIncidentDate').checked){
			enable("dateOfIncidentDescription");
		}
		else {
			disable("dateOfIncidentDescription");
		}
	}
}

// -----------------------------------------------------------------------------
// select identifiers / attributes
// -----------------------------------------------------------------------------

function selectProperties()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availablePropertyIds").children().each(function(i, item){
		if( item.selected ){
			html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectProperties( this )'><td onmousedown='select(event,this)'>" + item.text + "</td>";
			html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
			if( item.value.match("^attr_")=="attr_" )
			{
				html += " style='display:none' ";
			}
			html += "></td></tr>";
			selectedList.append( html );
			jQuery( item ).remove();
		}
	});
	
	if(getFieldValue('type') == "3")
	{
		jQuery("[name=displayed]").attr("disabled", true);
	}
}

function selectAllProperties()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availablePropertyIds").children().each(function(i, item){
		html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onmousedown='select(this)'>" + item.text + "</td>";
		html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
		if( item.value.match("^attr_")=="attr_" )
		{
			html += " style='display:none' ";
		}
		html += "'></td></tr>";
		selectedList.append( html );
		jQuery( item ).remove();
	});
}

function unSelectProperties()
{
	var availableList = jQuery("#availablePropertyIds");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{		
			availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
			item.remove();
		}
	});
}

function unSelectAllProperties()
{
	var availableList = jQuery("#availablePropertyIds");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
		item.remove();
	});
}

function select( event, element )
{
	if ( !getKeyCode( event ) )// Ctrl
	{
		jQuery("#selectedList .selected").removeClass( 'selected' );
	}
	
	element = jQuery( element ).parent();
	if( element.hasClass( 'selected') ) element.removeClass( 'selected' );
	else element.addClass( 'selected' );
}

function getKeyCode(e)
{
	var ctrlPressed=0;

	if (parseInt(navigator.appVersion)>3) {

		var evt = e ? e:window.event;

		if (document.layers && navigator.appName=="Netscape"
		&& parseInt(navigator.appVersion)==4) {
			// NETSCAPE 4 CODE
			var mString =(e.modifiers+32).toString(2).substring(3,6);
			ctrlPressed =(mString.charAt(1)=="1");
		}
		else {
			// NEWER BROWSERS [CROSS-PLATFORM]
			ctrlPressed=evt.ctrlKey;
		}
	}
	return ctrlPressed;
}

//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------

function moveUpPropertyList()
{
	var selectedList = jQuery("#selectedList");

	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{
			var prev = item.prev('#selectedList tr');
			if (prev.length == 1) 
			{ 
				prev.before(item);
			}
		}
	});
}

function moveDownPropertyList()
{
	var selectedList = jQuery("#selectedList");
	var items = new Array();
	jQuery("#selectedList").find("tr").each( function( i, item ){
		items.push(jQuery(item));
	});
	
	for( var i=items.length-1;i>=0;i--)
	{	
		var item = items[i];
		if( item.hasClass("selected") )
		{
			var next = item.next('#selectedList tr');
			if (next.length == 1) 
			{ 
				next.after(item);
			}
		}
	}
}

// --------------------------------------------------------------------
// Generate template message form
// --------------------------------------------------------------------

function generateTemplateMessageForm()
{
	var rowId = jQuery('.daysAllowedSendMessage').length + 1;
	var contend = '<tr name="tr' + rowId + '" class="listAlternateRow" >'
				+ 	'<td colspan="2">' + i18n_reminder + ' ' + rowId + '<a href="javascript:removeTemplateMessageForm('+ rowId +')"> ( '+ i18n_remove_reminder + ' )</a></td>'
				+ '</tr>'
				+ '<tr name="tr' + rowId + '">'
				+ 	'<td><label>' + i18n_send_when_to + '</label></td>'
				+ 	'<td>'
				+ 		'<select id="whenToSend' + rowId + '" name="whenToSend' + rowId + '" class="whenToSend" onchange="whenToSendOnChange(' + rowId + ')" >'
				+ 			'<option value="">' + i18n_days_scheduled + '</option>'
				+ 			'<option value="3">' + i18n_complete_program + '</option>'
				+ 			'<option value="1">' + i18n_program_enrollment + '</option>'
				+ 		'</select>'
				+	'</td>'
				+ '</tr>'
				+ '<tr name="tr' + rowId + '">'
				+ 	'<td><label>' + i18n_date_to_compare + '</label></td>'
				+ 	'<td>'
				+		'<select id="dateToCompare' + rowId + '" class="dateToCompare">'
				+			'<option value="dateofincident">' + i18n_incident_date + '</option>'
				+			'<option value="enrollmentdate">' + i18n_enrollment_date + '</option>'
				+ 		'</select>'
				+   '</td>'
				+ '</tr>'
				+ '<tr name="tr' + rowId + '">'
				+ 	'<td><label>' + i18n_send_message + '</label></td>'
				+ 	'<td>'
				+		'<input type="text" onchange="setRealDays(' + rowId + ')" style="width:100px;" realvalue="" id="daysAllowedSendMessage' + rowId + '" name="daysAllowedSendMessage' + rowId + '" class="daysAllowedSendMessage {validate:{required:true,number:true}}"/> '
				+ 		i18n_days
				+		' <select id="time' + rowId + '" name="time' + rowId + '" style="width:100px;" onchange="setRealDays(' + rowId + ')" >'
				+			'<option value="1">' + i18n_before + '</option>'
				+			'<option value="-1">' + i18n_after + '</option>'
				+		'</select> '
				+		i18n_scheduled_date
				+   ' </td>'
				+ '</tr>'
				+ '<tr name="tr' + rowId + '">'
				+ 	'<td><label>' + i18n_recipients + '</label></td>'
				+ 	'<td>'
				+ 		'<select id="sendTo' + rowId + '" name="sendTo' + rowId + '" class="sendTo" onchange="onchangeUserGroup('+ rowId +')">'
				+ 			'<option value="1">' + i18n_patient_sms_only + '</option>'
				+ 			'<option value="3">' + i18n_orgunit_phone_number_sms_only + '</option>'
				+ 			'<option value="2">' + i18n_health_worker_assigned_to_person + '</option>'
				+ 			'<option value="4">' + i18n_all_users_at_orgunit + '</option>'
				+ 			'<option value="5">' + i18n_user_group + '</option>'
				+ 		'</select>'
				+	'</td>'
				+ '/<tr>'
				+ '<tr name="tr' + rowId + '" id="tr' + rowId + '">'
				+ 	'<td><label>' + i18n_user_group + '</label></td>'
				+ 	'<td>'
				+	program_SMS_reminder_form
				+	'</td>'
				+ '/<tr>'
				+ '<tr name="tr' + rowId + '">'
				+ '	<td><label>' + i18n_message_type + '</label></td>'
				+ '	<td>'
				+ '		<select type="text" id="messageType' + rowId + '" name="messageType' + rowId + '" class="messageType {validate:{required:true,number:true}}" >'
				+ '			<option value="1">' + i18n_direct_sms + '</option>'
				+ '			<option value="2">' + i18n_message + '</option>'
				+ '			<option value="3">' + i18n_both + '</option>'
				+ '		</select>'
				+ '	</td>'
				+ '</tr>'
				+ '<tr name="tr' + rowId + '">'
				+	'<td>' + i18n_params + '</td>'
				+	'<td>'
				+		'<select multiple size="4" id="params' + rowId +'" name="params" ondblclick="insertParams(this.value, ' + rowId + ');">'
				+			'<option value="{patient-name}">' + i18n_patient_name + '</option>'
				+			'<option value="{program-name}">' + i18n_program_name + '</option>'
				+			'<option value="{incident-date}">' + i18n_incident_date + '</option>'
				+			'<option value="{days-since-incident-date}">' + i18n_days_since_incident_date + '</option>'
				+			'<option value="{enrollement-date}">' + i18n_enrollment_date + '</option>'
				+			'<option value="{days-since-enrollement-date}">' + i18n_days_since_enrollment_date + '</option>'
				+			'<option value="{orgunit-name}">' + i18n_orgunit_name + '</option>'
				+		'</select>'
				+	'</td>'
				+ '</tr>'
				+ '<tr name="tr' + rowId + '">'
				+	'<td><label>' + i18n_message + '</label></td>'
				+	'<td><textarea onkeyup="getMessageLength( ' + rowId + ');" id="templateMessage' + rowId + '" name="templateMessage' + rowId + '" style="width:320px" class="templateMessage {validate:{required:true}}"></textarea></td>'
				+ '</tr>'
				+ '<tr>'
				+	'<td></td>'
				+ 	'<td id="messageLengthTD' + rowId + '"></td>'
				+ '</tr>';

	jQuery('#programStageMessage').append( contend );
	showHideUserGroup();
}

function showHideUserGroup()
{
	jQuery(".sendTo").each( function( i, item ){
		var numb = i+1;
		if( item.value == 5){
			showById( 'tr'+numb );
		}
		else
			hideById ( 'tr'+numb );
	});
}

function removeTemplateMessageForm( rowId )
{
	jQuery("[name=tr" + rowId + "]").remove();
}

function insertParams( paramValue, rowId )
{
	var templateMessage = paramValue;
	insertTextCommon('templateMessage' + rowId, templateMessage);
	getMessageLength(rowId );
}

function whenToSendOnChange(index)
{
	var whenToSend = getFieldValue('whenToSend' + index );
	if(whenToSend==""){
		enable('dateToCompare' + index );
		enable('daysAllowedSendMessage' + index );
		enable('time' + index );
	}
	else{
		disable('dateToCompare' + index );
		disable('daysAllowedSendMessage' + index );
		disable('time' + index );
	}
}

function getMessageLength(rowId)
{
	var message = getFieldValue( 'templateMessage' + rowId );
	var length = 0;
	var idx = message.indexOf('{');
	while( idx >=0 ){
		length += message.substr(0,idx).length;
		var end = message.indexOf('}');
		if(end>=0){
			message = message.substr(end + 1, message.length);
			idx = message.indexOf('{');
		}
	}
	length += message.length;
	setInnerHTML('messageLengthTD' + rowId, length + " " + i18n_characters_without_params);
	if( length>=160 )
	{
		jQuery('#templateMessage' + rowId ).attr('maxlength', 160);
	}
	else
	{
		jQuery('#templateMessage' + rowId ).removeAttr('maxlength');
	}
}

function setRealDays(rowId)
{
	var daysAllowedSendMessage = jQuery("#daysAllowedSendMessage" + rowId);
	var time = jQuery("#time" + rowId + " option:selected ").val();
	daysAllowedSendMessage.attr("realvalue", time * eval(daysAllowedSendMessage).val());
	var aasdf= 0;
}

function onchangeUserGroup( id )
{
	var value = document.getElementById( 'sendTo' + id ).value;
	hideById( 'tr'+id );
	
	if( value=="1" || value=="3" ){
		setFieldValue('messageType' + id , '1');
		disable('messageType' + id );
	}
	else{
		if ( value == "5") {
			showById( 'tr' + id );
		}
		enable ('messageType' + id );
	}
}
