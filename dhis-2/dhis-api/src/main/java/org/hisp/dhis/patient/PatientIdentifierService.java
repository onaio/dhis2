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

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public interface PatientIdentifierService
{
    String ID = PatientIdentifierService.class.getName();

    int savePatientIdentifier( PatientIdentifier patientIdentifier );

    void deletePatientIdentifier( PatientIdentifier patientIdentifier );

    void updatePatientIdentifier( PatientIdentifier patientIdentifier );

    PatientIdentifier getPatientIdentifier( int id );

    PatientIdentifier getPatientIdentifier( Patient patient );

    Collection<PatientIdentifier> getAllPatientIdentifiers();

    Collection<PatientIdentifier> getPatientIdentifiersByType( PatientIdentifierType identifierType );

    Collection<PatientIdentifier> getPatientIdentifiersByIdentifier( String identifier );

    PatientIdentifier getPatientIdentifier( String identifier, Patient patient );

    PatientIdentifier getPatientIdentifier( PatientIdentifierType identifierType, Patient patient );

    Collection<PatientIdentifier> getPatientIdentifiers( Patient patient );

    PatientIdentifier get( PatientIdentifierType type, String identifier );

    Collection<PatientIdentifier> getAll( PatientIdentifierType type, String identifier );

    Patient getPatient( PatientIdentifierType identifierType, String value );

    Collection<Patient> getPatientsByIdentifier( String identifier, Integer min, Integer max );

    int countGetPatientsByIdentifier( String identifier );

    Collection<PatientIdentifier> getPatientIdentifiers( Collection<PatientIdentifierType> identifierTypes,
        Patient patient );

    boolean checkDuplicateIdentifier( PatientIdentifierType patientIdentifierType, String identifier,
        Integer patientId, OrganisationUnit organisationUnit, Program program, PeriodType periodType );
}
