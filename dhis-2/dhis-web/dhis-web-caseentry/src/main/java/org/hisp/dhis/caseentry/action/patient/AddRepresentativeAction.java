package org.hisp.dhis.caseentry.action.patient;

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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.util.PatientIdentifierGenerator;

import com.opensymphony.xwork2.Action;

public class AddRepresentativeAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    private PatientService patientService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private OrganisationUnitSelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String fullName;

    private String birthDate;

    private char ageType;

    private Integer age;

    private Character dobType;

    private String gender;

    private String registrationDate;

    private Integer relationshipTypeId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Patient patient;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        patient = new Patient();

        // ---------------------------------------------------------------------
        // Set FullName
        // ---------------------------------------------------------------------

        fullName = fullName.trim();
        patient.setName( fullName );

        // ---------------------------------------------------------------------
        // Get Other information for patient
        // ---------------------------------------------------------------------

        patient.setGender( gender );
        patient.setIsDead( false );
        patient.setOrganisationUnit( organisationUnit );

        if ( dobType == Patient.DOB_TYPE_VERIFIED || dobType == Patient.DOB_TYPE_DECLARED )
        {
            birthDate = birthDate.trim();
            patient.setBirthDate( format.parseDate( birthDate ) );
        }
        else
        {
            patient.setBirthDateFromAge( age.intValue(), ageType );
        }

        patient.setDobType( dobType );

        patient.setRegistrationDate( format.parseDate( registrationDate ) );

        patientService.savePatient( patient );

        // --------------------------------------------------------------------------------
        // Generate system id with this format :
        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
        // PatientIdentifierType will be null
        // --------------------------------------------------------------------------------

        String identifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );

        PatientIdentifier systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        while ( systemGenerateIdentifier != null )
        {
            identifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );
            systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        }

        systemGenerateIdentifier = new PatientIdentifier();
        systemGenerateIdentifier.setIdentifier( identifier );
        systemGenerateIdentifier.setPatient( patient );

        patientIdentifierService.savePatientIdentifier( systemGenerateIdentifier );

        patientService.updatePatient( patient );

        // -----------------------------------------------------------------------------
        // Save Patient Identifiers
        // -----------------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        PatientIdentifier pIdentifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + identifierType.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    pIdentifier = new PatientIdentifier();
                    pIdentifier.setIdentifierType( identifierType );
                    pIdentifier.setPatient( patient );
                    pIdentifier.setIdentifier( value.trim() );
                    patientIdentifierService.savePatientIdentifier( pIdentifier );
                    patient.getIdentifiers().add( pIdentifier );
                }
            }
            patientService.updatePatient( patient );
        }

        return SUCCESS;

    }

    // -----------------------------------------------------------------------------
    // Getter/Setter
    // -----------------------------------------------------------------------------

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public Integer getRelationshipTypeId()
    {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }

    public void setAgeType( char ageType )
    {
        this.ageType = ageType;
    }
}
