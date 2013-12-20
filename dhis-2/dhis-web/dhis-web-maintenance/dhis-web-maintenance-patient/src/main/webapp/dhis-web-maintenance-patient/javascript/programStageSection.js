
function programStageSectionList( programStageId )
{
	window.location.href = "programStage.action?id=" + programId;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showSectionDetails( sectionId )
{
	jQuery.getJSON( 'getProgramStageSection.action', { id: sectionId }, function ( json ) {
		setInnerHTML( 'nameField', json.programStageSection.name );	
		setInnerHTML( 'dataElementCountField', json.programStageSection.dataElementCount ); 
		showDetails();
	});
}

function removeSection( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeProgramStageSection.action' );
}
