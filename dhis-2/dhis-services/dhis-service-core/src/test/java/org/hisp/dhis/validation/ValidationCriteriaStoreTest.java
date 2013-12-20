package org.hisp.dhis.validation;

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
import java.util.Date;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.patient.Patient;
import org.junit.Test;

/**
 * @author Chau Thu Tran
 * @version ValidationCriteriaStoreTest.java May 25, 201011:38:25 AM
 */
@SuppressWarnings( "unchecked" )
public class ValidationCriteriaStoreTest
    extends DhisSpringTest
{
    private GenericIdentifiableObjectStore<ValidationCriteria> validationCriteriaStore;

    private String propertyA;

    private String propertyB;

    private Object valueA;

    private Object valueB;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        validationCriteriaStore = (GenericIdentifiableObjectStore<ValidationCriteria>) getBean( "org.hisp.dhis.validation.ValidationCriteriaStore" );

        propertyA = Patient.class.getDeclaredField( "birthDate" ).getName();

        propertyB = Patient.class.getDeclaredField( "dobType" ).getName();

        valueA = new Date();

        valueB = (char) 'A';
    }

    // -------------------------------------------------------------------------
    // Test
    // -------------------------------------------------------------------------

    @Test
    public void testSaveValidationCriteria()
    {
        ValidationCriteria validationCriteria = createValidationCriteria( 'A', propertyA,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueA );

        int id = validationCriteriaStore.save( validationCriteria );
        
        validationCriteria = validationCriteriaStore.get( id );

        assertEquals( validationCriteria.getName(), "ValidationCriteriaA" );
        assertEquals( validationCriteria.getDescription(), "DescriptionA" );
        assertEquals( validationCriteria.getProperty(), propertyA );
        assertEquals( validationCriteria.getOperator(), ValidationCriteria.OPERATOR_EQUAL_TO );
        assertEquals( validationCriteria.getValue(), valueA );
    }

    @Test
    public void testUpdateValidationCriteria()
    {
        ValidationCriteria validationCriteria = createValidationCriteria( 'A', propertyA,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueA );

        int id = validationCriteriaStore.save( validationCriteria );
        validationCriteria = validationCriteriaStore.get( id );
        
        assertEquals( validationCriteria.getName(), "ValidationCriteriaA" );
        assertEquals( validationCriteria.getDescription(), "DescriptionA" );
        assertEquals( validationCriteria.getProperty(), propertyA );
        assertEquals( validationCriteria.getOperator(), ValidationCriteria.OPERATOR_EQUAL_TO );
        assertEquals( validationCriteria.getValue(), valueA );

        validationCriteria.setName( "ValidationCriteriaB" );
        validationCriteria.setDescription( "DescriptionB" );
        validationCriteria.setProperty( propertyB );
        validationCriteria.setOperator( ValidationCriteria.OPERATOR_GREATER_THAN );
        validationCriteria.setValue( valueB );

        validationCriteriaStore.update( validationCriteria );
        validationCriteria = validationCriteriaStore.get( id );

        assertEquals( validationCriteria.getName(), "ValidationCriteriaB" );
        assertEquals( validationCriteria.getDescription(), "DescriptionB" );
        assertEquals( validationCriteria.getProperty(), propertyB );
        assertEquals( validationCriteria.getOperator(), ValidationCriteria.OPERATOR_GREATER_THAN );
        assertEquals( validationCriteria.getValue(), valueB );
    }
    
    @Test
    public void testDeleteValidationCriteria()
    {
        ValidationCriteria validationCriteriaA = createValidationCriteria( 'A', propertyA,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueA );
        ValidationCriteria validationCriteriaB = createValidationCriteria( 'B', propertyB,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueB );

        int idA = validationCriteriaStore.save( validationCriteriaA );
        int idB = validationCriteriaStore.save( validationCriteriaB );

        assertNotNull( validationCriteriaStore.get( idA ) );
        assertNotNull( validationCriteriaStore.get( idB ) );

        validationCriteriaStore.delete( validationCriteriaStore.get( idA ) );

        assertNull( validationCriteriaStore.get( idA ) );
        assertNotNull( validationCriteriaStore.get( idB ) );

        validationCriteriaStore.delete( validationCriteriaStore.get( idB ) );

        assertNull( validationCriteriaStore.get( idA ) );
        assertNull( validationCriteriaStore.get( idB ) );
    }

    @Test
    public void testGetValidationCriteriaByName()
    {
        ValidationCriteria validationCriteriaA = createValidationCriteria( 'A', propertyA,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueA );
        ValidationCriteria validationCriteriaB = createValidationCriteria( 'B', propertyB,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueB );

        int id = validationCriteriaStore.save( validationCriteriaA );
        validationCriteriaStore.save( validationCriteriaB );

        ValidationCriteria validationCriteria = validationCriteriaStore.getByName( "ValidationCriteriaA" );

        assertEquals( validationCriteria.getId(), id );
        assertEquals( validationCriteria.getName(), "ValidationCriteriaA" );
    }

    @Test
    public void testGetAllValidationRules()
    {
        ValidationCriteria validationCriteriaA = createValidationCriteria( 'A', propertyA,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueA );
        ValidationCriteria validationCriteriaB = createValidationCriteria( 'B', propertyB,
            ValidationCriteria.OPERATOR_EQUAL_TO, valueB );

        validationCriteriaStore.save( validationCriteriaA );
        validationCriteriaStore.save( validationCriteriaB );

        Collection<ValidationCriteria> validationCriteria = validationCriteriaStore.getAll();

        assertTrue( validationCriteria.size() == 2 );
        assertTrue( validationCriteria.contains( validationCriteriaA ) );
        assertTrue( validationCriteria.contains( validationCriteriaB ) );
    }
}
