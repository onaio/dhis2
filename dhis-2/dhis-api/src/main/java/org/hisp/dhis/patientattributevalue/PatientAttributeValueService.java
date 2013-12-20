package org.hisp.dhis.patientattributevalue;

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
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface PatientAttributeValueService
{
    String ID = PatientAttributeValueService.class.getName();

    void savePatientAttributeValue( PatientAttributeValue patientAttributeValue );

    void updatePatientAttributeValue( PatientAttributeValue patientAttributeValue );

    void deletePatientAttributeValue( PatientAttributeValue patientAttributeValue );

    int deletePatientAttributeValue( Patient patient );

    int deletePatientAttributeValue( PatientAttribute patientAttribute );

    PatientAttributeValue getPatientAttributeValue( Patient patient, PatientAttribute patientAttribute );

    Collection<PatientAttributeValue> getPatientAttributeValues( Patient patient );

    Collection<PatientAttributeValue> getPatientAttributeValues( PatientAttribute patientAttribute );

    Collection<PatientAttributeValue> getPatientAttributeValues( Collection<Patient> patients );

    Collection<PatientAttributeValue> getAllPatientAttributeValues();

    Map<Integer, Collection<PatientAttributeValue>> getPatientAttributeValueMapForPatients( Collection<Patient> patients );
    
    Map<Integer, PatientAttributeValue> getPatientAttributeValueMapForPatients( Collection<Patient> patients, PatientAttribute patientAttribute );
    
    Collection<PatientAttributeValue> searchPatientAttributeValue( PatientAttribute patientAttribute, String searchText );   

    void copyPatientAttributeValues( Patient source, Patient destination );
    
    int countByPatientAttributeoption( PatientAttributeOption attributeOption ); 
    
    Collection<Patient> getPatient( PatientAttribute attribute, String value );
    
    void updatePatientAttributeValues( PatientAttributeOption patientAttributeOption);
    
}
