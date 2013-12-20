
// -----------------------------------------------------------------------------
// Export to PDF file
// -----------------------------------------------------------------------------

function exportPDF( type )
{	
	var params = "type=" + type;
	params += "&months=" + jQuery( '#months' ).val();

	exportPdfByType( type, params );
}

// -----------------------------------------------------------------------------
// Search users
// -----------------------------------------------------------------------------

function searchUserName()
{
	var key = getFieldValue( 'key' );
    
    if ( key != '' ) 
    {
		jQuery( '#userForm' ).load( 'searchUser.action', {key:key}, unLockScreen );
    	lockScreen();
    }
    else 
    {
    	jQuery( '#userForm' ).submit();
    }
}

function getInactiveUsers()
{
	var months = $( '#months' ).val();
	
	window.location.href = 'alluser.action?months=' + months;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUserDetails( userId )
{
    jQuery.post( 'getUser.action', { id: userId }, function ( json ) {
		setInnerHTML( 'usernameField', json.user.username );
		
		var fullName = json.user.firstName + ", " + json.user.surname;
		setInnerHTML( 'fullNameField', fullName );

		var email = json.user.email;
		setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

		var phoneNumber = json.user.phoneNumber;
		setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );
		
		var lastLogin = json.user.lastLogin;
		setInnerHTML( 'lastLoginField', lastLogin ? lastLogin : '[' + i18n_none + ']' );
		
		var created = json.user.created;
		setInnerHTML( 'createdField', created ? created : '[' + i18n_none + ']' );
		
		var disabled = json.user.disabled;
		setInnerHTML( 'disabledField', disabled ? i18n_yes : i18n_no );
		
		var organisationUnits = joinNameableObjects( json.user.organisationUnits );
		setInnerHTML( 'assignedOrgunitField', organisationUnits ? organisationUnits : '[' + i18n_none + ']' );
		
		var roles = joinNameableObjects( json.user.roles );
		setInnerHTML( 'roleField', roles ? roles : '[' + i18n_none + ']' );
		
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( userId, username )
{
	removeItem( userId, username, i18n_confirm_delete, "removeUser.action" );
}

function disableUser( userId, username )
{
	var confirmation = confirm( "Are you sure you want to disable this user?\n\n" + username );
	
	if ( confirmation )
	{
		$.post( "disableUser.action", 
			{
				username: username
			},
			function( json ) 
			{
				if ( json.response == "success" ) {
					$( "#disableImg" + userId ).attr( "src", "../images/add.png" );
					$( "#disableImg" + userId ).removeAttr( "onclick" ).off( "click" ).click( function() {
						enableUser( userId, username )
					} );
				}
			} );
	}
}

function enableUser( userId, username )
{
	var confirmation = confirm( "Are you sure you want to enable this user?\n\n" + username );
	
	if ( confirmation )
	{
		$.post( "disableUser.action", 
			{
				username: username,
				enable: true
			},
			function( json ) 
			{
				if ( json.response == "success" ) {
					$( "#disableImg" + userId ).attr( "src", "../images/disable.png" );
					$( "#disableImg" + userId ).removeAttr( "onclick" ).off( "click" ).click( function() {
						disableUser( userId, username )
					} );
				}
			} );
	}
}
