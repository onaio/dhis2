function onValueTypeChange( e )
{
    var val = $( this ).find( ":selected" ).val();

    if ( val == "multiple_choice" )
    {
        $( "#memberValidator" ).addClass( "required" );
        $( "#multipleChoice" ).show();
    }
    else
    {
        $( "#memberValidator" ).removeClass( "required" );
        $( "#multipleChoice" ).hide();
    }
};
