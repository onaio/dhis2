// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeCriteria( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeValidationCriteria.action' );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showValidationCriteriaDetails( criteriaId )
{
	jQuery.getJSON( 'getValidationCriteria.action', { id: criteriaId }, function ( json ) {
		setInnerHTML( 'nameField', json.validationCriteria.name );
		setInnerHTML( 'descriptionField', json.validationCriteria.description );
		
		var property = json.validationCriteria.property;
		var operator = json.validationCriteria.operator;
		var	value = json.validationCriteria.value;
		
		// get operator
		if(operator == 0 ){
			operator = '=';
		}else if(operator == -1 ){
			operator = '<';
		}else {
			operator = '>';
		}
		
		setInnerHTML('criteriaField', property + " " + operator + " " + value );
		showDetails();
	});
}

// ----------------------------------------------------------------------------------------
// Show div to Add or Update Validation-Criteria
// ----------------------------------------------------------------------------------------
function showDivValue(){
	
	var propertyName = byId('property').value;
	hideDiv();
	if(propertyName != '')
	{
		hideById('emptyCriteria');
		
		var div = byId(propertyName + 'Div');
		div.style.display = 'block';
		if( propertyName == 'gender' || 
			propertyName == 'dobType' ){
				
			byId('operator').selectedIndex = 1;
			disable('operator');
		}
		else{
			enable('operator');
		}
	 }
}

function hideDiv()
{
	hideById('genderDiv');
	hideById('integerValueOfAgeDiv');
	hideById('birthDateDiv');
	hideById('dobTypeDiv');	
	showById('emptyCriteria');
}

function fillValue( value ){
	byId('value').value = value;
}