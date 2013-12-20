var isSave;
var interval = 60000;

$( document ).ready( function() 
{
	$(":button").button();
	$(":submit").button();
	$("#saveButton").button("option", "icons", { primary: "ui-icon-disk" });
	$("#cancelButton").button("option", "icons", { primary: "ui-icon-cancel" });
	$("#deleteButton").button("option", "icons", { primary: "ui-icon-trash" });
	$("#insertButton").button("option", "icons", { primary: "ui-icon-plusthick" });
	$("#propertiesButton").button("option", "icons", { primary: "ui-icon-newwin" });
	$("#insertImagesButton").button("option", "icons", { primary: "ui-icon-newwin" });
	$("#insertImageButton").button("option", "icons", { primary: "ui-icon-plusthick" });
	
	$("#imageDialog").bind("dialogopen", function(event, ui) {
		$("#insertImagesButton").button("disable");
	})
	$("#imageDialog").bind("dialogclose", function(event, ui) {
		$("#insertImagesButton").button("enable");
	})
	
	$("#insertImagesButton").click(function() {
		$("#imageDialog").dialog({
			overlay:{background:'#000000', opacity:0.1},
			width:400,
			height:300,
			position: [$("body").width()- 50, 0],
		});
	});
	
	if( autoSave )
	{
		timeOut = window.setTimeout( "validateRegistrationFormTimeout( false );", interval );
	}
});
	
function openPropertiesSelector()
{	
	$("#propertiesButton").addClass("ui-state-active2");
	$('#selectionDialog' ).dialog(
		{
			title:i18n_properties,
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:500,
			height:460,
			position: [$("body").width()- 50, 0],
			close: function(ev, ui) { 
				$("#propertiesButton").removeClass("ui-state-active2"); 
			}
		});
}

function fixAttrOnClick()
{
	$("#insertButton").click(function() {
		insertElement( 'fixedAttr' );
	});	
	
	$("#fixAttrButton").addClass("ui-state-active2");
	$("#identifierTypeButton").removeClass("ui-state-active2");
	$("#attributesButton").removeClass("ui-state-active2");
	$("#programAttrButton").removeClass("ui-state-active2");
	hideById('attributeTab');
	hideById('identifierTypeTab');
	hideById('programAttrTab');
	showById('fixedAttrTab');
}

function identifierTypeOnClick()
{
	$("#insertButton").click(function() {
		insertElement( 'iden' )
	});
	
	$("#fixAttrButton").removeClass("ui-state-active2");
	$("#identifierTypeButton").addClass("ui-state-active2");
	$("#attributesButton").removeClass("ui-state-active2");
	$("#programAttrButton").removeClass("ui-state-active2");
	hideById('attributeTab');
	hideById('fixedAttrTab');
	hideById('programAttrTab');
	showById('identifierTypeTab');
}

function attributesOnClick()
{
	$("#insertButton").click(function() {
		insertElement( 'attr' );
	});	
	
	$("#fixAttrButton").removeClass("ui-state-active2");
	$("#identifierTypeButton").removeClass("ui-state-active2");
	$("#attributesButton").addClass("ui-state-active2");
	$("#programAttrButton").removeClass("ui-state-active2");
	hideById('identifierTypeTab');
	hideById('fixedAttrTab');
	hideById('programAttrTab');
	showById('attributeTab');
}

function programAttrOnClick()
{
	$("#insertButton").click(function() {
		insertElement( 'prg' );
	});	
	
	$("#fixAttrButton").removeClass("ui-state-active2");
	$("#identifierTypeButton").removeClass("ui-state-active2");
	$("#attributesButton").removeClass("ui-state-active2");
	$("#programAttrButton").addClass("ui-state-active2");
	hideById('attributeTab');
	hideById('identifierTypeTab');
	hideById('fixedAttrTab');
	showById('programAttrTab');
}

function getDefaultRequiredFields()
{
	var requiredFields = {};
	if( getFieldValue("disableRegistrationFields")!='true' )
	{
		requiredFields['fixedattributeid=registrationDate'] = i18n_registration_date;
		requiredFields['fixedattributeid=fullName'] = i18n_full_name;
		requiredFields['fixedattributeid=gender'] = i18n_gender;
		requiredFields['fixedattributeid=dobType'] = i18n_dob_type;
		requiredFields['fixedattributeid=birthDate'] = i18n_date_of_birth;
			
		jQuery('#identifiersSelector option').each(function() {
			var item = jQuery(this);
			if( item.attr('mandatory')=='true'){
				requiredFields['identifierid=' + item.val()] = item.text();
			}
		});

		jQuery('#attributesSelector option').each(function() {
			var item = jQuery(this);
			if( item.attr('mandatory')=='true'){
				requiredFields['attributeid=' + item.val()] = item.text();
			}
		});
		
		jQuery('#programAttrSelector option').each(function() {
			var item = jQuery(this);
			if( item.attr('mandatory')=='true'){
				requiredFields['programid=' + item.val()] = item.text();
			}
		});
		
		var html = jQuery("#designTextarea").ckeditorGet().getData();
		var input = jQuery( html ).find("input");
		if( input.length > 0 )
		{
			input.each( function(i, item){	
				var key = "";
				var inputKey = jQuery(item).attr('fixedattributeid');
				if( inputKey!=undefined)
				{
					key = 'fixedattributeid=' + inputKey
				}
				else if( jQuery(item).attr('identifierid')!=undefined ){
					inputKey = jQuery(item).attr('identifierid');
					key = 'identifierid=' + inputKey
				}
				else if( jQuery(item).attr('attributeid')!=undefined ){
					inputKey = jQuery(item).attr('attributeid');
					key = 'attributeid=' + inputKey
				}
				else if( jQuery(item).attr('programid')!=undefined ){
					inputKey = jQuery(item).attr('programid');
					key = 'programid=' + inputKey
				}
					
				for (var idx in requiredFields){
					if( key == idx)
					{
						delete requiredFields[idx];
					}
				}
			});
		}
	
	}
	return requiredFields;
}

function validateProgramFields()
{
	var requiredFields = {};
	jQuery('#programAttrSelector option').each(function() {
		var item = jQuery(this);
		if( item.attr('mandatory')=='true'){
			requiredFields['programid=' + item.val()] = item.text();
		}
	});
	
	var html = jQuery("#designTextarea").ckeditorGet().getData();
	var input = jQuery( html ).find("input");
	if( input.length > 0 )
	{
		input.each( function(i, item){	
			var key = "";
			var inputKey = jQuery(item).attr('fixedattributeid');
			if( jQuery(item).attr('programid')!=undefined ){
				inputKey = jQuery(item).attr('programid');
				key = 'programid=' + inputKey
			}
			
			for (var idx in requiredFields){
				if( key == idx){
					delete requiredFields[idx];
				}
			}
		});
	}
	
	var violate = "";
	if( Object.keys(requiredFields).length > 0 )
	{
		violate = '<h3>' + i18n_please_insert_all_required_fields + '<h3>';
		for (var idx in requiredFields){
			violate += " - " + requiredFields[idx] + '<br>';
		}
		jQuery('#validateDiv').html(violate).dialog({
			title:i18n_required_fields_valivation,
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:500,
			height:300
		});
		return false;
	}
	
	return true;
}

function validateFormOnclick()
{
	var requiredFields = getRequiredFields();
	var violate = "";
	if( Object.keys(requiredFields).length > 0 )
	{
		violate = '<h3>' + i18n_please_insert_all_required_fields + '<h3>';
		for (var idx in requiredFields){
			violate += " - " + requiredFields[idx] + '<br>';
		}
	}
	else
	{
		violate = '<h3>' + i18n_validate_success + '<h3>';
	}
	
	jQuery('#validateDiv').html(violate).dialog({
		title:i18n_required_fields_valivation,
		maximize:true, 
		closable:true,
		modal:false,
		overlay:{background:'#000000', opacity:0.1},
		width:500,
		height:300
	});
}

function validateForm( checkViolate )
{
	requiredFields = getRequiredFields();
	
	if( Object.keys(requiredFields).length > 0 )
	{
		if ( byId('autoSave').checked )
		{
			setHeaderMessage( i18n_save_unsuccess_please_insert_all_required_fields );
		}
		else
		{
			var violate = '<h3>' + i18n_please_insert_all_required_fields + '<h3>';
			for (var idx in requiredFields){
				violate += " - " + requiredFields[idx] + '<br>';
			}
			
			setInnerHTML('validateDiv', violate);
			jQuery('#validateDiv').dialog({
				title:i18n_required_fields_valivation,
				maximize:true, 
				closable:true,
				modal:false,
				overlay:{background:'#000000', opacity:0.1},
				width:500,
				height:300
			});
			
		}
		return false;
	}
	else
	{
		return true;
	}
}

function checkExisted( id )
{	
	var result = false;
	var html = jQuery("#designTextarea").ckeditorGet().getData();
	var input = jQuery( html ).find("input");

	input.each( function(i, item){		
		var key = "";
		var inputKey = jQuery(item).attr('fixedattributeid');
		if( inputKey!=undefined)
		{
			key = 'fixedattributeid="' + inputKey + '"';
		}
		else if( jQuery(item).attr('identifierid')!=undefined ){
			inputKey = jQuery(item).attr('identifierid');
			key = 'identifierid="' + inputKey + '"';
		}
		else if( jQuery(item).attr('attributeid')!=undefined ){
			inputKey = jQuery(item).attr('attributeid');
			key = 'attributeid="' + inputKey + '"';
		}
		else if( jQuery(item).attr('programid')!=undefined ){
			inputKey = jQuery(item).attr('programid');
			key = 'programid="' + inputKey + '"';
		}
		
		if( id == key ) result = true;		
		
	});

	return result;
}

function insertElement( type )
{
	var id = '';
	var value = '';
	
	if( type == 'fixedAttr' ){
		var element = jQuery('#fixedAttrSelector option:selected');
		if( element.length == 0 ) return;		
		id = 'fixedattributeid="' + element.attr('value') + '"';
		value = element.text();
	}
	else if( type == 'iden' ){
		var element = jQuery('#identifiersSelector option:selected');
		if( element.length == 0 ) return;
		
		id = 'identifierid="' + element.attr('value') + '"';
		value = element.text();
	}
	else if( type == 'attr' ){
		var element = jQuery('#attributesSelector option:selected');
		if( element.length == 0 ) return;
		
		id = 'attributeid="' + element.attr('value') + '"';
		value = element.text();
	}
	else if( type == 'prg' ){
		var element = jQuery('#programAttrSelector option:selected');
		if( element.length == 0 ) return;
		
		id = 'programid="' + element.attr('value') + '"';
		value = element.text();
	}
	
	var htmlCode = "<input " + id + " value=\"[" + value + "]\" title=\"" + value + "\" ";
	
	var suggestedValue = getFieldValue('genderSelector');
	if( jQuery('#genderSelector').is(":visible") )
	{
		htmlCode += " suggested='" + suggestedValue + "' ";
	}
	suggestedValue = getFieldValue('dobTypeSelector');
	if( jQuery('#dobTypeSelector').is(":visible") )
	{
		htmlCode += " suggested='" + suggestedValue + "' ";
	}
	suggestedValue = getFieldValue('suggestedField');
	if( jQuery('#suggestedField').is(":visible") )
	{
		htmlCode += " suggested='" + suggestedValue + "' ";
	}
	
	var isHidden = jQuery('#hiddenField').attr('checked');
	if(isHidden)
	{
		htmlCode += " class='hidden' ";
	}
	htmlCode += " >";
	
	if( checkExisted( id ) ){		
		setMessage( "<span class='bold'>" + i18n_property_is_inserted + "</span>" );
		return;
	}else{
		var oEditor = jQuery("#designTextarea").ckeditorGet();
		oEditor.insertHtml( htmlCode );
		setMessage("");
	}

}

function deleteRegistrationForm( id, name )
{
	var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
	if ( result )
	{
		window.location.href = 'delRegistrationEntryFormAction.action?id=' + id;
	}
}

function insertImage() {
	var image = $("#imageDialog :selected").val();
	var html = "<img src=\"" + image + "\" title=\"" + $("#imageDialog :selected").text() + "\">";
	var oEditor = $("#designTextarea").ckeditorGet();
	oEditor.insertHtml( html );
}

// -------------------------------------------------------
// Auto-save data entry form
// -------------------------------------------------------

function setAutoSaveRegistrationSetting(_autoSave)
{
	jQuery.postJSON("setAutoSavePatientRegistrationSetting.action", {autoSave:_autoSave}, function(json) {
		autoSave = _autoSave;
		if (_autoSave) {
			window.setTimeout( "validateRegistrationFormTimeout( false );", 6000 );
		}
		else{
			window.clearTimeout(timeOut);
		}
	});
}

function validateRegistrationFormTimeout()
{
	validateDataEntryForm();
	timeOut = window.setTimeout( "validateRegistrationFormTimeout();", interval );
}

function validateDataEntryForm(form)
{
	var name = getFieldValue('name');
	if( name =='' || name.length<4 || name.length > 150 )
	{
		setHeaderDelayMessage( i18n_enter_a_name );
		return false;
	}
	else if(validateProgramFields())
	{
		$.postUTF8( 'validateDataEntryForm.action',
		{
			name: getFieldValue('name'),
			dataEntryFormId: getFieldValue('dataEntryFormId')
		}, 
		function( json )
		{
			if ( json.response == 'success' )
			{
				if( form != undefined)
				{
					form.submit();
				}
				else
				{
					autoSavePatientRegistrationForm();
				}
			}
			else if ( json.response = 'error' )
			{
				setHeaderDelayMessage( json.message );
			}
		} );
	}
}

function autoSavePatientRegistrationForm()
{
	$.postUTF8( 'autoSavePatientRegistrationForm.action',
	{
		name: getFieldValue('name'),
		designTextarea: jQuery("#designTextarea").ckeditorGet().getData(),
		programId: getFieldValue('programId'),
		id: getFieldValue('id')
	},
	function( json ) 
	{
		setFieldValue('dataEntryFormId', json.message);
		showById('deleteButton');
		setHeaderDelayMessage( i18n_save_success ); 
	} );
}

function deleteRegistrationFormFromView()
{
	var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
	if ( result )
	{
		window.location.href = 'delRegistrationEntryFormAction.action?id=' + getFieldValue('id');
	}
}

function suggestionSelectorToggle()
{
	hideById('genderSelector');
	hideById('dobTypeSelector');
	showById('suggestedField');
	if( getFieldValue('fixedAttrSelector')=='gender' ){
		hideById('suggestedField');
		showById('genderSelector');
	}
	else if(getFieldValue('fixedAttrSelector')=='dobType'){
		hideById('suggestedField');
		showById('dobTypeSelector');
	}
}

