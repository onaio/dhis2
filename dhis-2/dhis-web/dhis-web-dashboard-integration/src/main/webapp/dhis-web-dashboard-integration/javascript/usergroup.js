
// -----------------------------------------------------------------------------
// Usergroup functionality
// -----------------------------------------------------------------------------

function showUserGroupDetails( userGroupId )
{
    jQuery.post( 'getUserGroup.action', { userGroupId: userGroupId },
		function ( json ) {
			setInnerHTML( 'nameField', json.userGroup.name );
			setInnerHTML( 'noOfGroupField', json.userGroup.noOfUsers );

			showDetails();
	});
}

function removeUserGroup( userGroupId, userGroupName )
{
    removeItem( userGroupId, userGroupName, i18n_confirm_delete, 'removeUserGroup.action' );
}
