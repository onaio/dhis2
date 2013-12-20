package org.hisp.dhis.indicator;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class IndicatorGroupStoreTest
    extends DhisSpringTest
{
    private GenericIdentifiableObjectStore<IndicatorGroup> indicatorGroupStore;

    @Override
    public void setUpTest()
    {
        indicatorGroupStore = (GenericIdentifiableObjectStore<IndicatorGroup>) getBean( "org.hisp.dhis.indicator.IndicatorGroupStore" );
    }

    @Test
    public void testAddIndicatorGroup()
        throws Exception
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        int idA = indicatorGroupStore.save( groupA );
        int idB = indicatorGroupStore.save( groupB );

        groupA = indicatorGroupStore.get( idA );
        assertNotNull( groupA );
        assertEquals( idA, groupA.getId() );

        groupB = indicatorGroupStore.get( idB );
        assertNotNull( groupB );
        assertEquals( idB, groupB.getId() );
    }

    @Test
    public void testUpdateIndicatorGroup()
        throws Exception
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        int idA = indicatorGroupStore.save( groupA );
        groupA = indicatorGroupStore.get( idA );
        assertEquals( groupA.getName(), "IndicatorGroupA" );

        groupA.setName( "IndicatorGroupB" );
        indicatorGroupStore.update( groupA );
        groupA = indicatorGroupStore.get( idA );
        assertNotNull( groupA );
        assertEquals( groupA.getName(), "IndicatorGroupB" );
    }

    @Test
    public void testGetAndDeleteIndicatorGroup()
        throws Exception
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        int idA = indicatorGroupStore.save( groupA );
        int idB = indicatorGroupStore.save( groupB );

        assertNotNull( indicatorGroupStore.get( idA ) );
        assertNotNull( indicatorGroupStore.get( idB ) );

        indicatorGroupStore.delete( groupA );

        assertNull( indicatorGroupStore.get( idA ) );
        assertNotNull( indicatorGroupStore.get( idB ) );

        indicatorGroupStore.delete( groupB );

        assertNull( indicatorGroupStore.get( idA ) );
        assertNull( indicatorGroupStore.get( idB ) );
    }

    @Test
    public void testGetAllIndicatorGroups()
        throws Exception
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        indicatorGroupStore.save( groupA );
        indicatorGroupStore.save( groupB );

        Collection<IndicatorGroup> groups = indicatorGroupStore.getAll();

        assertEquals( groups.size(), 2 );
        assertTrue( groups.contains( groupA ) );
        assertTrue( groups.contains( groupB ) );
    }

    @Test
    public void testGetIndicatorGroupByName()
        throws Exception
    {
        IndicatorGroup groupA = new IndicatorGroup( "IndicatorGroupA" );
        IndicatorGroup groupB = new IndicatorGroup( "IndicatorGroupB" );

        int idA = indicatorGroupStore.save( groupA );
        int idB = indicatorGroupStore.save( groupB );

        assertNotNull( indicatorGroupStore.get( idA ) );
        assertNotNull( indicatorGroupStore.get( idB ) );

        groupA = indicatorGroupStore.getByName( "IndicatorGroupA" );
        assertNotNull( groupA );
        assertEquals( groupA.getId(), idA );

        IndicatorGroup groupC = indicatorGroupStore.getByName( "IndicatorGroupC" );
        assertNull( groupC );
    }
}
