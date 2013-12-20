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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
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
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdatePatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    private PatientService patientService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private OrganisationUnitSelectionManager selectionManager;

    private PatientAttributeOptionService patientAttributeOptionService;

    private UserService userService;

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private String fullName;

    private String birthDate;

    private boolean isDead;

    private String deathDate;

    private Integer age;

    private Boolean verified;

    private String gender;

    private String[] phoneNumber;

    private boolean underAge;

    private Integer representativeId;

    private Integer relationshipTypeId;

    private Integer healthWorker;

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
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        patient = patientService.getPatient( id );

        verified = (verified == null) ? false : verified;

        // ---------------------------------------------------------------------
        // Set FullName
        // ---------------------------------------------------------------------

        patient.setName( fullName );

        // ---------------------------------------------------------------------
        // Set Other information for patient
        // ---------------------------------------------------------------------

        String phone = "";
        if ( phoneNumber != null )
        {
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
        }
        
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
        else
        {
            patient.setHealthWorker( null );
        }

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

            patient.setDobType( dobType );
        }

        // -------------------------------------------------------------------------------------
        // Save PatientIdentifier
        // -------------------------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        PatientIdentifier identifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + identifierType.getId() );

                identifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );

                if ( StringUtils.isNotBlank( value ) )
                {
                    value = value.trim();

                    if ( identifier == null )
                    {
                        identifier = new PatientIdentifier();
                        identifier.setIdentifierType( identifierType );
                        identifier.setPatient( patient );
                        identifier.setIdentifier( value );
                        patient.getIdentifiers().add( identifier );
                    }
                    else
                    {
                        identifier.setIdentifier( value );
                        patient.getIdentifiers().add( identifier );
                    }
                }
                else if ( identifier != null )
                {
                    patient.getIdentifiers().remove( identifier );
                }
            }
        }

        // --------------------------------------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------------------------------

        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
        List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        Collection<PatientAttributeValue> valuesForDelete = null;

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( AddPatientAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );

                    if ( attributeValue == null )
                    {
                        attributeValue = new PatientAttributeValue();
                        attributeValue.setPatient( patient );
                        attributeValue.setPatientAttribute( attribute );
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                            attributeValue.setValue( value );
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        valuesForSave.add( attributeValue );
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        attributeValue.setValue( value.trim() );
                        valuesForUpdate.add( attributeValue );
                        valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        patientService.updatePatient( patient, representativeId, relationshipTypeId, valuesForSave, valuesForUpdate,
            valuesForDelete );

        return SUCCESS;
    }

    // -----------------------------------------------------------------------------
    // Getter/Setter
    // -----------------------------------------------------------------------------

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public void setHealthWorker( Integer healthWorker )
    {
        this.healthWorker = healthWorker;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setIsDead( boolean isDead )
    {
        this.isDead = isDead;
    }

    public void setDeathDate( String deathDate )
    {
        this.deathDate = deathDate;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public void setPhoneNumber( String[] phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
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

    public void setVerified( Boolean verified )
    {
        this.verified = verified;
    }

}
