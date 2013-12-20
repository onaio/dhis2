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
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.util.PatientIdentifierGenerator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

public class AddRelationshipPatientAction
    implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";

    public static final String PREFIX_IDENTIFIER = "iden";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    private PatientService patientService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private SelectedStateManager selectedStateManager;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientAttributeOptionService patientAttributeOptionService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    private SystemSettingManager systemSettingManager;

    private UserService userService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String fullName;

    private String birthDate;

    private Integer age;

    private Boolean verified;

    private String gender;

    private String[] phoneNumber;

    private String registrationDate;

    private boolean underAge;

    private Integer healthWorker;

    private boolean isDead;

    private String deathDate;

    private Integer relationshipId;

    private Integer relationshipTypeId;

    private boolean relationshipFromA;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        patient = new Patient();

        // ---------------------------------------------------------------------
        // Set FullName
        // ---------------------------------------------------------------------

        if ( fullName != null )
        {
            fullName = fullName.trim();
            patient.setName( fullName );
        }

        // ---------------------------------------------------------------------
        // Set Other information for patient
        // ---------------------------------------------------------------------

        String phone = "";

        for ( String _phoneNumber : phoneNumber )
        {
            _phoneNumber = (_phoneNumber != null && _phoneNumber.isEmpty() && _phoneNumber.trim().equals(
                systemSettingManager.getSystemSetting( SystemSettingManager.KEY_PHONE_NUMBER_AREA_CODE ) )) ? null
                : _phoneNumber;
            if ( _phoneNumber != null )
            {
                phone += _phoneNumber + ";";
            }
        }

        phone = (phone.isEmpty()) ? null : phone.substring( 0, phone.length() - 1 );

        patient.setPhoneNumber( phone );
        patient.setGender( gender );
        patient.setIsDead( false );
        patient.setUnderAge( underAge );
        patient.setOrganisationUnit( organisationUnit );
        patient.setIsDead( isDead );
        if ( deathDate != null )
        {
            deathDate = deathDate.trim();
            patient.setDeathDate( format.parseDate( deathDate ) );
        }

        if ( healthWorker != null )
        {
            patient.setHealthWorker( userService.getUser( healthWorker ) );
        }

        Date _birthDate = new Date();
        if ( birthDate != null || age != null )
        {
            verified = (verified == null) ? false : verified;

            Character dobType = (verified) ? Patient.DOB_TYPE_VERIFIED : Patient.DOB_TYPE_DECLARED;

            if ( !verified && age != null )
            {
                dobType = 'A';
            }

            if ( dobType == Patient.DOB_TYPE_VERIFIED || dobType == Patient.DOB_TYPE_DECLARED )
            {
                birthDate = birthDate.trim();
                patient.setBirthDate( format.parseDate( birthDate ) );
            }
            else
            {
                patient.setBirthDateFromAge( age.intValue(), Patient.AGE_TYPE_YEAR );
            }

            _birthDate = patient.getBirthDate();
            patient.setDobType( dobType );
        }

        // -----------------------------------------------------------------------------
        // Registration Date
        // -----------------------------------------------------------------------------

        if ( registrationDate == null )
        {
            patient.setRegistrationDate( new Date() );
        }
        else
        {
            patient.setRegistrationDate( format.parseDate( registrationDate ) );
        }

        // ---------------------------------------------------------------------
        // Generate system id with this format :
        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
        // PatientIdentifierType will be null
        // ---------------------------------------------------------------------

        String identifier = PatientIdentifierGenerator.getNewIdentifier( _birthDate, patient.getGender() );

        PatientIdentifier systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        while ( systemGenerateIdentifier != null )
        {
            identifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );
            systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        }

        systemGenerateIdentifier = new PatientIdentifier();
        systemGenerateIdentifier.setIdentifier( identifier );
        systemGenerateIdentifier.setPatient( patient );

        patient.getIdentifiers().add( systemGenerateIdentifier );

        selectedStateManager.clearListAll();
        selectedStateManager.clearSearchingAttributeId();
        selectedStateManager.clearSortingAttributeId();
        selectedStateManager.setSearchText( systemGenerateIdentifier.getIdentifier() );

        // ---------------------------------------------------------------------
        // Save Patient Identifiers
        // ---------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        PatientIdentifier pIdentifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                value = request.getParameter( PREFIX_IDENTIFIER + identifierType.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    pIdentifier = new PatientIdentifier();
                    pIdentifier.setIdentifierType( identifierType );
                    pIdentifier.setPatient( patient );
                    pIdentifier.setIdentifier( value.trim() );
                    patient.getIdentifiers().add( pIdentifier );
                }
            }
        }

        patientService.savePatient( patient );

        // Create relationship

        if ( relationshipId != null && relationshipTypeId != null )
        {
            Patient relationship = patientService.getPatient( relationshipId );
            if ( relationship != null )
            {
                if ( underAge )
                    patient.setRepresentative( relationship );

                Relationship rel = new Relationship();
                if ( relationshipFromA )
                {
                    rel.setPatientA( relationship );
                    rel.setPatientB( patient );
                }
                else
                {
                    rel.setPatientA( patient );
                    rel.setPatientB( relationship );
                }
                if ( relationshipTypeId != null )
                {
                    RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                    if ( relType != null )
                    {
                        rel.setRelationshipType( relType );
                        relationshipService.saveRelationship( rel );
                    }
                }
            }
        }

        // -----------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------

        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    attributeValue = new PatientAttributeValue();
                    attributeValue.setPatient( patient );
                    attributeValue.setPatientAttribute( attribute );

                    if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        PatientAttributeOption option = patientAttributeOptionService
                            .get( NumberUtils.toInt( value, 0 ) );
                        if ( option != null )
                        {
                            attributeValue.setPatientAttributeOption( option );
                            attributeValue.setValue( option.getName() );
                        }
                        else
                        {
                            // Someone deleted this option ...
                        }
                    }
                    else
                    {
                        attributeValue.setValue( value.trim() );
                    }
                    patientAttributeValueService.savePatientAttributeValue( attributeValue );
                }
            }
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

    public void setRelationshipFromA( boolean relationshipFromA )
    {
        this.relationshipFromA = relationshipFromA;
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

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }

    public void setRelationshipId( Integer relationshipId )
    {
        this.relationshipId = relationshipId;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public void setPhoneNumber( String[] phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }
}
