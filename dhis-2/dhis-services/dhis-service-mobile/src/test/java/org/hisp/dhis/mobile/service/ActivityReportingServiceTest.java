package org.hisp.dhis.mobile.service;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Patient;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.PatientIdentifier;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ActivityReportingServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ActivityReportingService activityReportingService;

    @Test
    public void testSavePatient()
        throws NotAllowedException
    {
        Patient patientA = this.createLWUITPatient( 'A' );
        Patient patientB = this.createLWUITPatient( 'B' );

        int patientAId = activityReportingService.savePatient( patientA, 779, "" );
        int patientBId = activityReportingService.savePatient( patientB, 779, "" );

        patientA = activityReportingService.findPatient( patientAId );
        assertEquals( patientAId, patientA.getId() );
        assertEquals( "FirstnameA", patientA.getName() );

        patientB = activityReportingService.findPatient( patientBId );
        assertEquals( patientBId, patientB.getId() );
        assertEquals( "FirstnameB", patientB.getName() );
    }

    private Patient createLWUITPatient( char uniqueCharacter )
    {
        Patient patient = new Patient();
        patient.setAge( 1 );
        patient.setBirthDate( "25-09-1990" );
        patient.setName( "Firstname" + uniqueCharacter );
        patient.setGender( "male" );
        patient.setOrganisationUnitName( "OrgUnitName" );
        patient.setPhoneNumber( "095678943" );
        patient.setRegistrationDate( new Date() );
        patient.setIdentifiers( new ArrayList<PatientIdentifier>() );
        patient.setPatientAttValues( new ArrayList<PatientAttribute>() );
        return patient;
    }
}
