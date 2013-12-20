package org.hisp.dhis.patient.action.patientidentifiertype;

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

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Viet
 * @version $Id$
 */
public class RemovePatientIdentifierTypeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PatientIdentifierTypeService patientIdentifierTypeService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private String message;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            if ( id == null )
            {
                message = i18n.getString( "can_not_find_patient_identifier_type_with_id_null" );
                return ERROR;
            }

            PatientIdentifierType patientIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( id );

            if ( patientIdentifierType != null )
            {
                patientIdentifierTypeService.deletePatientIdentifierType( patientIdentifierType );
                message = i18n.getString( "delete_successfull" ) + patientIdentifierType.getName();
                return SUCCESS;
            }
            else
            {
                message = i18n.getString( "can_not_find_patient_identifier_type_with_id" ) + id.toString();
                return ERROR;
            }
        }
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();

                return ERROR;
            }
        }

        return ERROR;
    }

}
