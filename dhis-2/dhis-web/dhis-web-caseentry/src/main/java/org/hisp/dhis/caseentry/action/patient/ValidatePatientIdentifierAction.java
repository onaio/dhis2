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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 */
public class ValidatePatientIdentifierAction
    implements Action
{
    public static final String PATIENT_DUPLICATE = "duplicate";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService identifierTypeService;

    private ProgramService programService;

    private OrganisationUnitSelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer programId;

    private Integer patientId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    private I18n i18n;

    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();

    private PatientIdentifier patientIdentifier;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientIdentifierType> identifiers = identifierTypeService.getAllPatientIdentifierTypes();

        if ( identifiers != null && identifiers.size() > 0 )
        {
            String value = null;
            String idDuplicate = "";

            for ( PatientIdentifierType idType : identifiers )
            {

                value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + idType.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    boolean isDuplicate = false;

                     OrganisationUnit orgunit = (idType.getOrgunitScope()) ? selectionManager
                            .getSelectedOrganisationUnit() : null;

                        Program program = (idType.getProgramScope()) ? programService.getProgram( programId ) : null;
                        isDuplicate = patientIdentifierService.checkDuplicateIdentifier( idType, value, patientId,  orgunit,
                            program, idType.getPeriodType() );
                 
                    if ( isDuplicate )
                    {
                        idDuplicate += idType.getName() + ", ";
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

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
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

}
