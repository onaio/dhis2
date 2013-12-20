
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showLineListElementDetails( lineListElementId )
{
	/*
    var request = new Request();
    request.setResponseTypeXML( 'getLineListElements' );
    request.setCallbackSuccess( lineListElementReceived );
    request.send( 'getLineListElement.action?lineListElementId=' + lineListElementId );
    */
	$.post("getLineListElement.action",
			{
				lineListElementId : lineListElementId
			},
			function (data)
			{
				lineListElementReceived(data);
			},'xml');
}

function lineListElementReceived( lineListElementElement )
{
  //setFieldValue( 'idField', getElementValue( lineListElementElement, 'id' ) );
  setInnerHTML( 'idField', getElementValue( lineListElementElement, 'id' ) );

  //setFieldValue( 'nameField', getElementValue( lineListElementElement, 'name' ) );
  setInnerHTML( 'idField', getElementValue( lineListElementElement, 'name' ) );
  
 // setFieldValue( 'shortNameField', getElementValue( lineListElementElement, 'shortName' ) );
  setInnerHTML( 'idField', getElementValue( lineListElementElement, 'shortName' ) );
  
  //setFieldValue( 'dataTypeField', getElementValue( lineListElementElement, 'dataType' ) );
  setInnerHTML( 'idField', getElementValue( lineListElementElement, 'dataType' ) );
  
  //setFieldValue( 'presentationTypeField', getElementValue( lineListElementElement, 'presentationType' ) );
  setInnerHTML( 'idField', getElementValue( lineListElementElement, 'presentationType' ) );
  
  //setFieldValue( 'memberCountField', getElementValue( lineListElementElement, 'memberCount' ) );
  setInnerHTML( 'idField', getElementValue( lineListElementElement, 'memberCount' ) );
  
  showDetails();
}

// -----------------------------------------------------------------------------
// Delete line list element
// -----------------------------------------------------------------------------

function removeLineListElement( lineListElementId, lineListElementName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\nLine List Element Name: ' + lineListElementName + '\n\nLine List Element Id: ' +lineListElementId);
    
    if ( result )
    {
        /*
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeLineListElementCompleted );
        request.send( 'removeLineListElement.action?lineListElementId=' + lineListElementId);
        */
    	$.post("removeLineListElement.action",
    			{
    				lineListElementId : lineListElementId
    			},
    			function (data)
    			{
    				removeLineListElementCompleted(data);
    			},'xml');
    }
}

function removeLineListElementCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
   // alert(type);
    if ( type == 'success' )
    {
       
    	window.location.href = 'lineListElements.action';
    }
    else if ( type == 'error' )
    {
        setFieldValue( 'warningField', message );

        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Validate add line list element
// -----------------------------------------------------------------------------

function validateAddLineListElement()
{
  /*
	var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addLineListElementValidationCompleted ); 
  
  var requestString = 'validateLineListElement.action?name=' + document.getElementById( 'name' ).value +
                      '&shortName=' + document.getElementById( 'shortName' ).value +
                      '&dataType=' + document.getElementById( 'dataType' ).value +
                      '&presentationType=' + document.getElementById( 'presentationType' ).value;

  request.send( requestString );
  */
  
	$.post("validateLineListElement.action",
			{
				name : document.getElementById( 'name' ).value,
				shortName : getFieldValue( 'shortName' ),
				dataType : document.getElementById( 'dataType' ).value,
				presentationType : document.getElementById( 'presentationType' ).value
			},
			function (data)
			{
				addLineListElementValidationCompleted(data);
			},'xml');
  

  return false;
}

function addLineListElementValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      var availableList = document.getElementById( 'availableList' );
      availableList.selectedIndex = -1;
        
	  selectAllById( 'selectedList' );
        
      document.getElementById('addLineListElementForm').submit();
  }

  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

// -----------------------------------------------------------------------------
// Validate update Line List Element
// -----------------------------------------------------------------------------

function validateUpdateLineListElement()
{
  /*
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( editLineListElementValidationCompleted );

  request.send( 'validateLineListElement.action?lineListElementId=' + getFieldValue ( 'lineListElementId' ) +
  					  '&name=' + getFieldValue( 'name' ) +
                      '&shortName=' + getFieldValue( 'shortName' ) +
                      '&dataType=' + getFieldValue( 'dataType' ) +
                      '&presentationType=' + getFieldValue( 'presentationType' ) );
  */
	$.post("validateLineListElement.action",
			{
				id : getFieldValue ( 'lineListElementId' ),
				name : getFieldValue( 'name' ),
				shortName : getFieldValue( 'shortName' ),
				dataType : getFieldValue( 'dataType' ),
				presentationType : getFieldValue( 'presentationType' )
			},
			function (data)
			{
				editLineListElementValidationCompleted(data);
			},'xml');

  return false;
}

function editLineListElementValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      var availableList = document.getElementById( 'availableList' );
      availableList.selectedIndex = -1;

      selectAllById( 'selectedList' );
      document.getElementById('editLineListElementForm').submit();
  }
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initListsLineListElement()
{
    var list = document.getElementById( 'selectedList' );
    var id;

    for ( id in lineListElementOptions )
    {
        list.add( new Option( lineListElementOptions[id], id ), null );
    }

    list = document.getElementById( 'availableList' );

    for ( id in availableLineListOptions )
    {
        list.add( new Option( availableLineListOptions[id], id ), null );
    }
}

function filterLineListElementOptions()
{
    var filter = document.getElementById( 'lineListElementOptionsFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in lineListElementOptions )
    {
        var value = lineListElementOptions[id];
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableLineListOptions()
{
    var filter = document.getElementById( 'availableLineListOptionsFilter' ).value;
    var list = document.getElementById( 'availableList' );
    list.options.length = 0;
    
    for ( var id in availableLineListOptions)
    {
        var value = availableLineListOptions[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addLineListElementOptions()
{
    var list = document.getElementById( 'availableList' );
    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        lineListElementOptions[id] = availableLineListOptions[id];
        
        delete availableLineListOptions[id];
    }
    
    filterLineListElementOptions();
    filterAvailableLineListOptions();
}

function removeLineListElementOptions()
{
    var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableLineListOptions[id] = lineListElementOptions[id];
        
        delete lineListElementOptions[id];
    }
    
    filterLineListElementOptions();
    filterAvailableLineListOptions();
}

function moveUp( elementId )
{

	var withInList = document.getElementById( elementId );

	var index = withInList.selectedIndex;

	if ( index == -1 ) { return; }

	if( index - 1 < 0 ) { return; }//window.alert( 'Item cant be moved up');

	var option = new Option( withInList.options[index].text, withInList.options[index].value);
	var temp = new Option( withInList.options[index-1].text, withInList.options[index-1].value);

	withInList.options[index-1] = option;
	withInList.options[index-1].selected = true;
	withInList.options[index] = temp;

}

function moveDown( elementId )
{
	var withInList = document.getElementById( elementId );

	var index = withInList.selectedIndex;

	if ( index == -1 ) { return; }

	if( index + 1 == withInList.options.length ) { return; }//window.alert( 'Item cant be moved down');

	var option = new Option( withInList.options[index].text, withInList.options[index].value);
	var temp = new Option( withInList.options[index+1].text, withInList.options[index+1].value);

	withInList.options[index+1] = option;
	withInList.options[index+1].selected = true;
	withInList.options[index] = temp;

}