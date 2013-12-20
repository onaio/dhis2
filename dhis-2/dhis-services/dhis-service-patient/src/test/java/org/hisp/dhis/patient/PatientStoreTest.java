package org.hisp.dhis.patient;

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
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class PatientStoreTest
    extends DhisSpringTest
{
    @Autowired
    private PatientStore patientStore;

    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProgramInstanceService programInstanceService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    private Patient patientA;
    private Patient patientB;
    private Patient patientC;
    private Patient patientD;
        
    private Program programA;
    private Program programB;
        
    private OrganisationUnit organisationUnit;
    
    private Date date = new Date();

    @Override
    public void setUpTest()
    {
        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );
        
        patientA = createPatient( 'A', organisationUnit );
        patientB = createPatient( 'B', organisationUnit );
        patientC = createPatient( 'A', null );
        patientD = createPatient( 'B', organisationUnit );
        
        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnit );
    }
    
    @Test
    public void addGet()
    {
        int idA = patientStore.save( patientA );
        int idB = patientStore.save( patientB );
        
        assertEquals( patientA.getName(), patientStore.get( idA ).getName() );
        assertEquals( patientB.getName(), patientStore.get( idB ).getName() );
    }

    @Test
    public void addGetbyOu()
    {
        int idA = patientStore.save( patientA );
        int idB = patientStore.save( patientB );
      
        assertEquals( patientA.getName(), patientStore.get( idA ).getName() );
        assertEquals( patientB.getName(), patientStore.get( idB ).getName() );
    }
    
    @Test
    public void delete()
    {
        int idA = patientStore.save( patientA );
        int idB = patientStore.save( patientB );
        
        assertNotNull( patientStore.get( idA ) );
        assertNotNull( patientStore.get( idB ) );

        patientStore.delete( patientA );
        
        assertNull( patientStore.get( idA ) );
        assertNotNull( patientStore.get( idB ) );

        patientStore.delete( patientB );
        
        assertNull( patientStore.get( idA ) );
        assertNull( patientStore.get( idB ) );        
    }
    
    @Test
    public void getAll()
    {
        patientStore.save( patientA );
        patientStore.save( patientB );
        
        assertTrue( equals( patientStore.getAll(), patientA, patientB ) );
    }
    
    @Test
    public void testGetByFullName()
    {
        patientStore.save( patientA );
        patientStore.save( patientB );
        patientStore.save( patientC );
        patientStore.save( patientD );
        
        Collection<Patient> patients = patientStore.getByFullName( "NameA", organisationUnit );
        
        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientA ) );
        
        patients = patientStore.getByFullName( "NameB", organisationUnit );
        
        assertEquals( 2, patients.size() );
        assertTrue( patients.contains( patientB ) );
        assertTrue( patients.contains( patientD ) );
    }
    
    @Test
    public void testGetByOrgUnitProgram()
    {
        programService.saveProgram( programA );
        programService.saveProgram( programB );
        
        patientStore.save( patientA );
        patientStore.save( patientB );
        patientStore.save( patientC );
        patientStore.save( patientD );
        
        programInstanceService.enrollPatient( patientA, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientC, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientD, programB, date, date, organisationUnit, null );
        
        Collection<Patient> patients = patientStore.getByOrgUnitProgram( organisationUnit, programA, 0, 100 );
        
        assertEquals( 2, patients.size() );
        assertTrue( patients.contains( patientA ) );
        assertTrue( patients.contains( patientB ) );

        patients = patientStore.getByOrgUnitProgram( organisationUnit, programB, 0, 100 );
        
        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientD ) );
    }

    @Test
    public void testGetByProgram()
    {
        programService.saveProgram( programA );
        programService.saveProgram( programB );
        
        patientStore.save( patientA );
        patientStore.save( patientB );
        patientStore.save( patientC );
        patientStore.save( patientD );
        
        programInstanceService.enrollPatient( patientA, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientC, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientD, programB, date, date, organisationUnit, null );
        
        Collection<Patient> patients = patientStore.getByProgram( programA, 0, 100 );
        
        assertEquals( 3, patients.size() );
        assertTrue( patients.contains( patientA ) );
        assertTrue( patients.contains( patientB ) );
        assertTrue( patients.contains( patientC ) );

        patients = patientStore.getByOrgUnitProgram( organisationUnit, programB, 0, 100 );
        
        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientD ) );
    }
}
