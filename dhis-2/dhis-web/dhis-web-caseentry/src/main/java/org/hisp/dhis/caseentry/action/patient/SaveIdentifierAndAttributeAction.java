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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $SaveIdentifierAndAttributeAction.java Mar 29, 2012 10:33:00 AM$
 */
public class SaveIdentifierAndAttributeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientIdentifierService patientIdentifierService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeOptionService patientAttributeOptionService;

    private ProgramService programService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    private Integer patientId;

    private Integer statusCode;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

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

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Patient patient = patientService.getPatient( patientId );
        Program program = programService.getProgram( programId );

        saveIdentifiers( patient, program );
        saveAttributeValues( patient, program );

        statusCode = 0;

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    public void saveIdentifiers( Patient patient, Program program )
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<PatientIdentifierType> identifierTypes = program.getPatientIdentifierTypes();

        PatientIdentifier identifier = null;

        if ( identifierTypes != null )
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
                    patientIdentifierService.deletePatientIdentifier( identifier );
                }
            }
        }
    }

    private void saveAttributeValues( Patient patient, Program program )
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            // patient.getAttributes().clear();

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
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
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
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        patientAttributeValueService.updatePatientAttributeValue( attributeValue );
                    }
                }
                else if ( attributeValue != null )
                {
                    patientAttributeValueService.deletePatientAttributeValue( attributeValue );
                }
            }
        }

        patientService.updatePatient( patient );
    }

}
