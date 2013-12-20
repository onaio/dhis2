function showValidationRuleDetails( validationId )
{
    jQuery.post( 'getValidationRule.action', { id: validationId }, function ( json ) {
		setText( 'nameField', json.validationRule.name );
		
		var description = json.validationRule.description;
		setText( 'descriptionField', description ? description : '[' + i18n_none + ']' );
		
		var importance = json.validationRule.importance;
		setText( 'importanceField', i18nalizeImportance( importance ) );
		
		var ruleType = json.validationRule.ruleType;
		setText( 'ruleTypeField', i18nalizeRuleType( ruleType ) );
		
		if ( ruleType == 'surveillance' ) 
		{
			var organisationUnitLevel = string( json.validationRule.organisationUnitLevel );
			setText( 'organisationUnitLevelField', organisationUnitLevel ? organisationUnitLevel : '[' + i18n_none + ']' );
			
			var sequentialSampleCount = string( json.validationRule.sequentialSampleCount );
			setText( 'sequentialSampleCountField', sequentialSampleCount ? sequentialSampleCount : '[' + i18n_none + ']' );
			
			var annualSampleCount = json.validationRule.annualSampleCount;
			setText( 'annualSampleCountField', annualSampleCount ? annualSampleCount : '[' + i18n_none + ']' );
			
			var highOutliers = string( json.validationRule.highOutliers );
			setText( 'highOutliersField', highOutliers ? highOutliers : '[' + i18n_none + ']' );
			
			var lowOutliers = string( json.validationRule.lowOutliers );
			setText( 'lowOutliersField', lowOutliers ? lowOutliers : '[' + i18n_none + ']' );

			document.getElementById('organisationUnitLevelP').style.display = '';
			document.getElementById('sequentialSampleCountP').style.display = '';
			document.getElementById('annualSampleCountP').style.display = '';
			document.getElementById('highOutliersP').style.display = '';
			document.getElementById('lowOutliersP').style.display = '';
		} 
		else
		{
			document.getElementById('organisationUnitLevelP').style.display = 'none';
			document.getElementById('sequentialSampleCountP').style.display = 'none';
			document.getElementById('annualSampleCountP').style.display = 'none';
			document.getElementById('highOutliersP').style.display = 'none';
			document.getElementById('lowOutliersP').style.display = 'none';
		}
		
		var leftSideDescription = json.validationRule.leftSideDescription;
		setText( 'leftSideDescriptionField', leftSideDescription ? leftSideDescription : '[' + i18n_none + ']' );
		
		var operator = json.validationRule.operator;
		setText( 'operatorField', i18nalizeOperator( operator ) );
		
		var rightSideDescription = json.validationRule.rightSideDescription;
		setText( 'rightSideDescriptionField', rightSideDescription ? rightSideDescription : '[' + i18n_none + ']' );

		showDetails();
	});
}

function i18nalizeImportance ( importance )
{
	if ( importance == "high" )
	{
		return i18n_high;
	}
	else if ( importance == "medium" )
	{
		return i18n_medium;
	}
	if ( importance == "low" )
	{
		return i18n_low;
	}
	
	return null;
}

function i18nalizeRuleType ( ruleType )
{
	if ( ruleType == "validation" )
	{
		return i18n_validation;
	}
	else if ( ruleType == "surveillance" )
	{
		return i18n_surveillance;
	}
	
	return null;
}

function i18nalizeOperator( operator )
{
    if ( operator == "equal_to" )
    {
        return i18n_equal_to;
    }
    else if ( operator == "not_equal_to" )
    {
        return i18n_not_equal_to;
    }
    else if ( operator == "greater_than" )
    {
        return i18n_greater_than;       
    }
    else if ( operator == "greater_than_or_equal_to" )
    {
        return i18n_greater_than_or_equal_to;
    }
    else if ( operator == "less_than" )
    {
        return i18n_less_than;
    }
    else if ( operator == "less_than_or_equal_to" )
    {
        return i18n_less_than_or_equal_to;
    }
    else if ( operator == "compulsory_pair" )
    {
        return i18n_compulsory_pair;
    }
    
    return null;
}

function removeValidationRule( ruleId, ruleName )
{
	removeItem( ruleId, ruleName, i18n_confirm_delete, 'removeValidationRule.action' );
}
