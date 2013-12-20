package org.hisp.dhis.option;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class OptionServiceTest
    extends DhisSpringTest
{
    private OptionService optionService;

    private List<String> options = new ArrayList<String>();

    private OptionSet optionSetA = new OptionSet( "OptionSetA" );

    private OptionSet optionSetB = new OptionSet( "OptionSetB" );

    private OptionSet optionSetC = new OptionSet( "OptionSetC" );

    @Override
    public void setUpTest()
    {
        optionService = (OptionService) getBean( OptionService.ID );

        options.add( "OptA1" );
        options.add( "OptA2" );
        options.add( "OptB1" );
        options.add( "OptB2" );

        optionSetA.setOptions( options );
        optionSetB.setOptions( options );
    }

    @Test
    public void testSaveGet()
    {
        int idA = optionService.saveOptionSet( optionSetA );
        int idB = optionService.saveOptionSet( optionSetB );
        int idC = optionService.saveOptionSet( optionSetC );

        OptionSet actualA = optionService.getOptionSet( idA );
        OptionSet actualB = optionService.getOptionSet( idB );
        OptionSet actualC = optionService.getOptionSet( idC );

        assertEquals( optionSetA, actualA );
        assertEquals( optionSetB, actualB );
        assertEquals( optionSetC, actualC );

        assertEquals( 4, optionSetA.getOptions().size() );
        assertEquals( 4, optionSetB.getOptions().size() );
        assertEquals( 0, optionSetC.getOptions().size() );

        assertTrue( optionSetA.getOptions().contains( "OptA1" ) );
        assertTrue( optionSetA.getOptions().contains( "OptA2" ) );
        assertTrue( optionSetA.getOptions().contains( "OptB1" ) );
        assertTrue( optionSetA.getOptions().contains( "OptB2" ) );
    }

    @Test
    public void testCodec()
    {
        String decoded = "Malaria Severe Under 5";
        String encoded = "[Malaria_Severe_Under_5]";

        assertEquals( encoded, OptionSet.optionEncode( decoded ) );
        assertEquals( decoded, OptionSet.optionDecode( encoded ) );
    }

    @Test
    public void testGetList()
    {
        int idA = optionService.saveOptionSet( optionSetA );

        List<String> options = optionService.getOptions( idA, "OptA", 10 );
        
        assertEquals( 2, options.size() );

        options = optionService.getOptions( idA, "OptA1", 10 );

        assertEquals( 1, options.size() );

        options = optionService.getOptions( idA, "OptA1", null );

        assertEquals( 1, options.size() );

        options = optionService.getOptions( idA, "Opt", null );

        assertEquals( 4, options.size() );

        options = optionService.getOptions( idA, "Opt", 3 );

        assertEquals( 3, options.size() );
    }
}
