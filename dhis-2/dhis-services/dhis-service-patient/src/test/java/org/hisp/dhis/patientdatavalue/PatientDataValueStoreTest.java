package org.hisp.dhis.patientdatavalue;

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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@SuppressWarnings( "unused" )
public class PatientDataValueStoreTest
    extends DhisSpringTest
{
    private PatientDataValueStore patientDataValueStore;
    
    private PatientService patientService;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private Patient patientA;
    private Patient patientB;
        
    private PatientDataValue valueA;
    private PatientDataValue valueB;
    private PatientDataValue valueC;    
    private PatientDataValue valueD;  
    
    private OrganisationUnit organisationUnit;
    
    @Override
    public void setUpTest()
    {
        patientDataValueStore = (PatientDataValueStore) getBean( PatientDataValueStore.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        patientService = (PatientService) getBean( PatientService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean ( OrganisationUnitService.ID );
        
        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        
        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );
        
        patientA = createPatient( 'A', organisationUnit );
        patientB = createPatient( 'B', organisationUnit );
        
        patientService.savePatient( patientA );
        patientService.savePatient( patientB );
                
        //TODO Add values
    }
    
    @Test
    public void addGet()
    {        
        //TODO Test get
    }
}
