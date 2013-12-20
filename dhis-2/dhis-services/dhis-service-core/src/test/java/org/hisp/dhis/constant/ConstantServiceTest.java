package org.hisp.dhis.constant;

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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ConstantServiceTest
    extends DhisSpringTest
{
    public void setUpTest()
    {
        constantService = (ConstantService) getBean( ConstantService.ID );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, Constant constant )
    {
        assertEquals( "Constant" + uniqueCharacter, constant.getName() );
    }

    // -------------------------------------------------------------------------
    // Constant
    // -------------------------------------------------------------------------

    @Test
    public void testAddConstant()
    {
        Constant constantA = createConstant( 'A', 1.23 );
        Constant constantB = createConstant( 'B', 21.3d );

        int idA = constantService.saveConstant( constantA );
        int idB = constantService.saveConstant( constantB );

        constantA = constantService.getConstant( idA );
        constantB = constantService.getConstant( idB );

        assertEquals( idA, constantA.getId() );
        assertEquals( 1.23, constantA.getValue(), DELTA );
        assertEq( 'A', constantA );

        assertEquals( idB, constantB.getId() );
        assertEquals( 21.3, constantB.getValue(), DELTA );
        assertEq( 'B', constantB );
    }

    @Test
    public void testUpdateConstant()
    {
        Constant constant = createConstant( 'A', 1.23 );

        int id = constantService.saveConstant( constant );
        constant = constantService.getConstant( id );

        assertEq( 'A', constant );

        constant.setName( "ConstantC" );

        constantService.updateConstant( constant );
        constant = constantService.getConstant( id );

        assertEquals( constant.getName(), "ConstantC" );
    }

    @Test
    public void testDeleteAndGetConstant()
    {
        Constant constantA = createConstant( 'A', 1.23 );
        Constant constantB = createConstant( 'B', 35.03 );
        Constant constantC = createConstant( 'C', 3.3d );

        int idA = constantService.saveConstant( constantA );
        int idB = constantService.saveConstant( constantB );
        int idC = constantService.saveConstant( constantC );

        assertNotNull( constantService.getConstant( idA ) );
        assertNotNull( constantService.getConstant( idB ) );
        assertNotNull( constantService.getConstant( idC ) );

        constantService.deleteConstant( constantB );

        assertNotNull( constantService.getConstant( idA ) );
        assertNull( constantService.getConstant( idB ) );
        assertNotNull( constantService.getConstant( idC ) );

    }

    @Test
    public void testGetConstantByName()
        throws Exception
    {
        Constant constantA = createConstant( 'A', 1.23 );
        Constant constantB = createConstant( 'B', 3.21 );

        int idA = constantService.saveConstant( constantA );
        int idB = constantService.saveConstant( constantB );

        assertEquals( constantService.getConstantByName( "ConstantA" ).getId(), idA );
        assertEquals( constantService.getConstantByName( "ConstantB" ).getId(), idB );
        assertNull( constantService.getConstantByName( "ConstantC" ) );
    }

    @Test
    public void testGetAllConstants()
    {
        Constant constantA = createConstant( 'A', 1.23 );
        Constant constantB = createConstant( 'B', 3.21 );
        Constant constantC = createConstant( 'C', 3.3d );
        Constant constantD = createConstant( 'D', 23.11d );

        constantService.saveConstant( constantA );
        constantService.saveConstant( constantB );
        constantService.saveConstant( constantD );

        Collection<Constant> constants = constantService.getAllConstants();

        assertEquals( 3, constants.size() );
        assertTrue( constants.contains( constantA ) );
        assertTrue( constants.contains( constantB ) );
        assertTrue( !constants.contains( constantC ) );
        assertTrue( constants.contains( constantD ) );
    }

    @Test
    public void testGetAllConstantNames()
    {
        Constant constantA = createConstant( 'A', 1.23d );
        Constant constantB = createConstant( 'B', 3.21 );
        Constant constantC = createConstant( 'C', 3.3d );
        Constant constantD = createConstant( 'D', 23.11 );

        constantService.saveConstant( constantA );
        constantService.saveConstant( constantB );
        constantService.saveConstant( constantC );
        constantService.saveConstant( constantD );

        assertEq( 'A', constantService.getConstantByName( "ConstantA" ) );
        assertEq( 'B', constantService.getConstantByName( "ConstantB" ) );
        assertEq( 'C', constantService.getConstantByName( "ConstantC" ) );
        assertEq( 'D', constantService.getConstantByName( "ConstantD" ) );
    }

    @Test
    public void testGetConstantCount()
    {
        Constant constantA = createConstant( 'A', 1.23d );
        Constant constantB = createConstant( 'B', 3.21 );
        Constant constantC = createConstant( 'C', 3.3d );

        constantService.saveConstant( constantA );
        constantService.saveConstant( constantB );
        constantService.saveConstant( constantC );

        assertEquals( 3, constantService.getConstantCount() );
    }

    @Test
    public void testGetConstantCountByName()
    {
        Constant constantA = createConstant( 'A', 1.23d );
        Constant constantB = createConstant( 'B', 3.21 );
        Constant constantC = createConstant( 'C', 3.3d );

        constantService.saveConstant( constantA );
        constantService.saveConstant( constantB );
        constantService.saveConstant( constantC );

        assertEquals( 1, constantService.getConstantCountByName( "ConstantA" ) );
        assertEquals( 1, constantService.getConstantCountByName( "ConstantB" ) );
        assertEquals( 1, constantService.getConstantCountByName( "ConstantC" ) );
    }

    @Test
    public void testGetConstantsBetween()
    {
        Constant constantA = createConstant( 'A', 1.23d );
        Constant constantB = createConstant( 'B', 3.21 );
        Constant constantC = createConstant( 'C', 3.3d );
        Constant constantD = createConstant( 'D', 23.11d );
        Constant constantE = createConstant( 'E', 2.1d );

        constantService.saveConstant( constantA );
        constantService.saveConstant( constantB );
        constantService.saveConstant( constantC );
        constantService.saveConstant( constantD );
        constantService.saveConstant( constantE );

        Collection<Constant> results = constantService.getConstantsBetween( 1, 3 );

        assertTrue( !results.contains( constantA ) );
        assertTrue( results.contains( constantB ) );
        assertTrue( results.contains( constantC ) );
        assertTrue( results.contains( constantD ) );
        assertTrue( !results.contains( constantE ) );
    }

    @Test
    public void testGetConstantsBetweenByName()
    {
        Constant constantA = createConstant( 'A', 1.23d );
        Constant constantB = createConstant( 'B', 3.21 );
        Constant constantC = createConstant( 'C', 3.3d );
        Constant constantD = createConstant( 'D', 23.11d );
        Constant constantE = createConstant( 'E', 2.1d );

        Collection<Constant> constantCollection = new ArrayList<Constant>();

        constantCollection.add( constantB );
        constantCollection.add( constantC );
        constantCollection.add( constantD );

        constantService.saveConstant( constantA );
        constantService.saveConstant( constantB );
        constantService.saveConstant( constantC );
        constantService.saveConstant( constantD );
        constantService.saveConstant( constantE );

        assertTrue( constantService.getConstantsBetween( 4, 1 ).contains( constantE ) );
        assertTrue( !constantService.getConstantsBetweenByName( "ConstantE", 4, 1 ).contains( constantE ) );
    }
}
