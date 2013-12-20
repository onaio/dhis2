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

import com.opensymphony.xwork2.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ValidateAddRelationshipPatientAction
    implements Action
{
    public static final String PATIENT_DUPLICATE = "duplicate";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    private I18nFormat format;

    private PatientService patientService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService identifierTypeService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String fullName;

    private Character dobType;

    private String birthDate;

    private char ageType;

    private Integer age;

    private String gender;

    private Integer id;

    private boolean checkedDuplicate;

    private boolean underAge;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    private I18n i18n;

    private Patient patient;

    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();

    private PatientIdentifier patientIdentifier;

    private Collection<Patient> patients;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        Date dateOfBirth;

        if ( selectionManager.getSelectedOrganisationUnit() == null )
        {
            message = i18n.getString( "please_select_a_registering_unit" );

            return INPUT;
        }

        if ( age == null && birthDate == null )
        {
            message = i18n.getString( "specfiy_birth_date_or_age" );

            return INPUT;
        }

        if ( dobType == 'V' || dobType == 'D' )
        {
            birthDate = birthDate.trim();

            dateOfBirth = format.parseDate( birthDate );

            if ( dateOfBirth == null || dateOfBirth.after( new Date() ) )
            {
                message = i18n.getString( "please_enter_a_valid_birth_date" );

                return INPUT;
            }
        }

        fullName = fullName.trim();

        if ( fullName.indexOf( ' ' ) == -1 )
        {
            message = i18n.getString( "please_enter_a_valid_full_name" );

            return INPUT;
        }

        // ---------------------------------------------------------------------
        // Check duplicated patient
        // ---------------------------------------------------------------------

        if ( !checkedDuplicate )
        {
            // Check duplication name, birthdate, gender
            patients = patientService.getPatients( fullName, format.parseDate( birthDate ), gender );

            if ( patients != null && patients.size() > 0 )
            {
                message = i18n.getString( "patient_duplicate" );
                boolean flagDuplicate = false;

                for ( Patient p : patients )
                {
                    if ( id == null || (id != null && p.getId() != id) )
                    {
                        flagDuplicate = true;
                        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
                            .getPatientAttributeValues( p );

                        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
                        {
                            patientAttributeValueMap
                                .put( p.getId() + "_" + patientAttributeValue.getPatientAttribute().getId(),
                                    patientAttributeValue.getValue() );
                        }
                    }
                }
                if ( flagDuplicate )
                    return PATIENT_DUPLICATE;
            }
        }

        // Check Identifiers duplicate

        Patient p = new Patient();
        if ( dobType == Patient.DOB_TYPE_VERIFIED || dobType == Patient.DOB_TYPE_DECLARED )
        {
            birthDate = birthDate.trim();
            p.setBirthDate( format.parseDate( birthDate ) );
        }
        else
        {
            p.setBirthDateFromAge( age, ageType );
        }

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientIdentifierType> identifiers = identifierTypeService.getAllPatientIdentifierTypes();

        if ( identifiers != null && identifiers.size() > 0 )
        {
            String value;
            String idDuplicate = "";

            for ( PatientIdentifierType idType : identifiers )
            {
                // If underAge is TRUE : Only check duplicate on
                // PatientIdentifierType which related is FALSE
                if ( !underAge || (underAge && !idType.isRelated()) )
                {
                    value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + idType.getId() );
                    if ( StringUtils.isNotBlank( value ) )
                    {
                        PatientIdentifier identifier = patientIdentifierService.get( idType, value );
                        if ( identifier != null
                            && (id == null || identifier.getPatient().getId() != id) )
                        {
                            idDuplicate += idType.getName() + ", ";
                        }
                    }
                }
            }

            if ( StringUtils.isNotBlank( idDuplicate ) )
            {
                idDuplicate = StringUtils.substringBeforeLast( idDuplicate, "," );
                message = i18n.getString( "identifier_duplicate" ) + ": " + idDuplicate;
                return INPUT;
            }
        }

        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }

    // ---------------------------------------------------------------------
    // Getter/Setter
    // ---------------------------------------------------------------------

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setCheckedDuplicate( boolean checkedDuplicate )
    {
        this.checkedDuplicate = checkedDuplicate;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }

    public void setAgeType( char ageType )
    {
        this.ageType = ageType;
    }

    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }
}
