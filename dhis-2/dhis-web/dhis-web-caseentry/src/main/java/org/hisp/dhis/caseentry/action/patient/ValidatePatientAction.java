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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.validation.ValidationCriteria;

import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Abyot Asalefew Gizaw
 */
public class ValidatePatientAction
    implements Action
{
    public static final String PATIENT_DUPLICATE = "duplicate";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    private PatientService patientService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService identifierTypeService;

    private ProgramService programService;

    private OrganisationUnitSelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String fullName;

    private String birthDate;

    private Integer age;

    private String gender;

    private Integer id;

    private boolean checkedDuplicate;

    private boolean underAge;

    private Integer representativeId;

    private Integer relationshipTypeId;

    private Integer programId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    private I18n i18n;

    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();

    private PatientIdentifier patientIdentifier;

    private Collection<Patient> patients;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( fullName != null )
        {
            fullName = fullName.trim();

            if ( !checkedDuplicate && birthDate != null && gender != null )
            {
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
                                patientAttributeValueMap.put( p.getId() + "_"
                                    + patientAttributeValue.getPatientAttribute().getId(),
                                    patientAttributeValue.getValue() );
                            }
                        }
                    }

                    if ( flagDuplicate )
                    {
                        return PATIENT_DUPLICATE;
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Check Under age information
        // ---------------------------------------------------------------------

        if ( underAge )
        {
            if ( representativeId == null )
            {
                message = i18n.getString( "please_choose_representative_for_this_under_age_patient" );
                return INPUT;
            }
            if ( relationshipTypeId == null )
            {
                message = i18n.getString( "please_choose_relationshipType_for_this_under_age_patient" );
                return INPUT;
            }
        }

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientIdentifierType> identifiers = identifierTypeService.getAllPatientIdentifierTypes();

        if ( identifiers != null && identifiers.size() > 0 )
        {
            String value = null;
            String idDuplicate = "";

            for ( PatientIdentifierType idType : identifiers )
            {
                if ( !underAge || (underAge && !idType.isRelated()) )
                {
                    value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + idType.getId() );

                    if ( StringUtils.isNotBlank( value ) )
                    {
                        boolean isDuplicate = false;

                        OrganisationUnit orgunit = (idType.getOrgunitScope()) ? selectionManager
                            .getSelectedOrganisationUnit() : null;
                        Program program = (idType.getProgramScope()) ? programService.getProgram( programId ) : null;

                        isDuplicate = patientIdentifierService.checkDuplicateIdentifier( idType, value, id,
                            orgunit, program, idType.getPeriodType() );

                        if ( isDuplicate )
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
        // Check Enrollment for adding patient single event with registration
        // ---------------------------------------------------------------------

        Patient p = new Patient();
        if ( gender != null )
        {
            p.setGender( gender );
        }

        if ( birthDate != null && !birthDate.isEmpty() )
        {
            birthDate = birthDate.trim();
            p.setBirthDate( format.parseDate( birthDate ) );

        }
        else if ( age != null )
        {
            p.setBirthDateFromAge( age, Patient.AGE_TYPE_YEAR );
        }

        if ( programId != null )
        {
            Program program = programService.getProgram( programId );
            ValidationCriteria criteria = program.isValid( p );

            if ( criteria != null )
            {
                message = i18n.getString( "patient_could_not_be_enrolled_due_to_following_enrollment_criteria" ) + ": "
                    + criteria.getDescription();
                return INPUT;
            }
        }

        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

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

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
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

    public void setRepresentativeId( Integer representativeId )
    {
        this.representativeId = representativeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }
}
