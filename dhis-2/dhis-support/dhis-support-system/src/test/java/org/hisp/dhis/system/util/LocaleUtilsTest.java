package org.hisp.dhis.system.util;

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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.translation.Translation;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Lars Helge Overland
 */
public class LocaleUtilsTest
{
    @Test
    public void testGetTranslationsHighestSpecifity()
    {
        Locale l1 = new Locale( "en", "UK", "en" );
        Locale l2 = new Locale( "en", "UK" );
        Locale l3 = new Locale( "en" );
        
        Translation t1 = new Translation( DataElement.class.getSimpleName(), 1, l1.toString(), "name", "Name" );
        Translation t2 = new Translation( DataElement.class.getSimpleName(), 1, l2.toString(), "name", "Name" );
        Translation t3 = new Translation( DataElement.class.getSimpleName(), 1, l3.toString(), "name", "Name" );

        Translation t4 = new Translation( DataElement.class.getSimpleName(), 1, l1.toString(), "shortName", "Short name" );
        Translation t5 = new Translation( DataElement.class.getSimpleName(), 1, l2.toString(), "shortName", "Short name" );
        
        Translation t6 = new Translation( DataElement.class.getSimpleName(), 1, l2.toString(), "code", "Code" );
        
        List<Translation> list = Arrays.asList( t1, t2, t3, t4, t5, t6 );
        
        List<Translation> translations = LocaleUtils.getTranslationsHighestSpecifity( list );
        
        assertEquals( 3, translations.size() );
        assertTrue( translations.contains( t1 ) );
        assertTrue( translations.contains( t4 ) );
        assertTrue( translations.contains( t6 ) );
    }

    @Test
    public void testGetLocaleFallbacks()
    {
        Locale l1 = new Locale( "en", "UK", "en" );
        Locale l2 = new Locale( "en", "UK" );
        Locale l3 = new Locale( "en" );
        
        List<String> locales = LocaleUtils.getLocaleFallbacks( l1 );
        
        assertEquals( 3, locales.size() );
        assertTrue( locales.contains( "en_UK_en" ) );
        assertTrue( locales.contains( "en_UK" ) );
        assertTrue( locales.contains( "en_UK" ) );
        
        assertEquals( 2, LocaleUtils.getLocaleFallbacks( l2 ).size() );
        assertEquals( 1, LocaleUtils.getLocaleFallbacks( l3 ).size() );        
    }
}
