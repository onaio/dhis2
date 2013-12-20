package org.hisp.dhis.dataentryform;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/**
 * Upgrades the format of the input field identifiers from the legacy
 * "value[12].value:value[34].value" to the new "12-34-val"
 */
public class DataEntryFormUpgrader
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( DataEntryFormUpgrader.class );

    private final static String ID_EXPRESSION = "id=\"value\\[(\\d+)\\]\\.value:value\\[(\\d+)\\]\\.value\"";

    private final static Pattern ID_PATTERN = Pattern.compile( ID_EXPRESSION );

    private final Pattern SELECT_PATTERN = Pattern.compile( "(<select.*?)[/]?</select>", Pattern.DOTALL );

    private final Pattern ID_PROGRAM_ENTRY_TEXTBOX = Pattern
        .compile( "id=\"value\\[(\\d+)\\].value:value\\[(\\d+)\\].value:value\\[(\\d+)\\].value\"" );

    private final Pattern ID_PROGRAM_ENTRY_OPTION = Pattern
        .compile( "id=\"value\\[(\\d+)\\].(combo|boolean){1}:value\\[(\\d+)\\].(combo|boolean){1}\"" );

    private final Pattern ID_PROGRAM_ENTRY_DATE = Pattern
        .compile( "id=\"value\\[(\\d+)\\].date:value\\[(\\d+)\\].date\"" );
    
    private final Pattern IDENTIFIER_PATTERN_TEXTBOX = Pattern.compile( "id=\"(\\d+)-(\\d+)-(\\d+)-val\"" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    // -------------------------------------------------------------------------
    // Implementation method
    // -------------------------------------------------------------------------

    @Transactional
    @Override
    public void execute()
    {
        Collection<DataEntryForm> dataEntryForms = dataEntryFormService.getAllDataEntryForms();

        int i = 0;

        for ( DataEntryForm programDataEntryForm : dataEntryForms )
        {
            String customForm = upgradeDataEntryForm( programDataEntryForm.getHtmlCode() );

            customForm = upgradeProgramDataEntryFormForTextBox( customForm );

            customForm = upgradeProgramDataEntryFormForDate( customForm );

            customForm = upgradeProgramDataEntryFormForOption( customForm );
            
            customForm = upgradeProgramDataEntryForm( customForm );

            programDataEntryForm.setHtmlCode( customForm );

            dataEntryFormService.updateDataEntryForm( programDataEntryForm );

            i++;
        }

        log.info( "Upgraded custom case entry form identifiers: " + i );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String upgradeDataEntryForm( String htmlCode )
    {
        Matcher matcher = ID_PATTERN.matcher( htmlCode );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-val\"";

            matcher.appendReplacement( out, upgradedId );
        }

        matcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }

    private String upgradeProgramDataEntryFormForTextBox( String htmlCode )
    {
        Matcher matcher = ID_PROGRAM_ENTRY_TEXTBOX.matcher( htmlCode );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-" + matcher.group( 3 )
                + "-val\"";

            matcher.appendReplacement( out, upgradedId );
        }

        matcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }

    private String upgradeProgramDataEntryFormForOption( String htmlCode )
    {
        StringBuffer out = new StringBuffer();
        Matcher inputMatcher = SELECT_PATTERN.matcher( htmlCode );

        while ( inputMatcher.find() )
        {
            String inputHtml = inputMatcher.group();

            Matcher matcher = ID_PROGRAM_ENTRY_OPTION.matcher( inputHtml );

            if ( matcher.find() )
            {
                String upgradedId = matcher.group( 1 ) + "-" + matcher.group( 3 ) + "-val";

                inputHtml = "<input name=\"entryselect\" id=\"" + upgradedId + "\" >";
            }

            inputMatcher.appendReplacement( out, inputHtml );
        }

        inputMatcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }

    private String upgradeProgramDataEntryFormForDate( String htmlCode )
    {
        Matcher matcher = ID_PROGRAM_ENTRY_DATE.matcher( htmlCode );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-val\" ";

            matcher.appendReplacement( out, upgradedId );
        }

        matcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }
    
    private String upgradeProgramDataEntryForm( String htmlCode )
    {
        Matcher matcher = IDENTIFIER_PATTERN_TEXTBOX.matcher( htmlCode );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-val\"";

            matcher.appendReplacement( out, upgradedId );
        }

        matcher.appendTail( out );
        
        return out.toString();
    }
}