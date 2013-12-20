// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showConceptDetails( conceptId )
{
    jQuery.post( 'getConcept.action', { id : conceptId }, function( json ) {
		setInnerHTML( 'nameField', json.concept.name );
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove category concept
// -----------------------------------------------------------------------------

function removeConcept( conceptId, conceptName )
{
    removeItem( conceptId, conceptName, i18n_confirm_delete, 'removeConcept.action' );
}
