package org.hisp.dhis.caseentry.action.caseentry;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveExecutionDateAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( SaveExecutionDateAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private String executionDate;

    public void setExecutionDate( String executionDate )
    {
        this.executionDate = executionDate;
    }

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Date dateValue = format.parseDate( executionDate );

        OrganisationUnit organisationUnit = organisationUnitId == null ? selectedStateManager
            .getSelectedOrganisationUnit() : organisationUnitService.getOrganisationUnit( organisationUnitId );

        Patient patient = patientId == null ? selectedStateManager.getSelectedPatient() : patientService
            .getPatient( patientId );

        if ( dateValue != null )
        {
            ProgramStageInstance programStageInstance = programStageInstanceService
                .getProgramStageInstance( programStageInstanceId );

            // If the program-stage-instance of the patient not exists,
            // create a program-instance and program-stage-instance for
            // single-event program
            if ( programStageInstance == null )
            {
                Program program = programService.getProgram( programId );
                ProgramStage programStage = null;

                if ( program.getProgramStages() != null )
                {
                    programStage = program.getProgramStages().iterator().next();
                }

                int type = program.getType();
                ProgramInstance programInstance = null;

                if ( type == Program.SINGLE_EVENT_WITH_REGISTRATION )
                {
                    // Add a new program-instance
                    programInstance = new ProgramInstance();
                    programInstance.setEnrollmentDate( dateValue );
                    programInstance.setDateOfIncident( dateValue );
                    programInstance.setProgram( program );
                    programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

                    programInstance.setPatient( patient );

                    programInstanceService.addProgramInstance( programInstance );
                }
                else if ( type == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
                {
                    Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( program );
                    if ( programInstances == null || programInstances.size() == 0 )
                    {
                        // Add a new program-instance
                        programInstance = new ProgramInstance();
                        programInstance.setEnrollmentDate( dateValue );
                        programInstance.setDateOfIncident( dateValue );
                        programInstance.setProgram( program );
                        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
                        programInstanceService.addProgramInstance( programInstance );
                    }
                    else
                    {
                        programInstance = programInstanceService.getProgramInstances( program ).iterator().next();
                    }
                }

                // Add a new program-stage-instance
                programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                programStageInstance.setDueDate( dateValue );
                programStageInstance.setExecutionDate( dateValue );
                programStageInstance.setOrganisationUnit( organisationUnit );

                programStageInstanceService.addProgramStageInstance( programStageInstance );
                selectedStateManager.setSelectedProgramInstance( programInstance );
                selectedStateManager.setSelectedProgramStageInstance( programStageInstance );
            }
            else
            {
                programStageInstance.setExecutionDate( dateValue );
                programStageInstance.setOrganisationUnit( organisationUnit );

                if ( programStageInstance.getProgramInstance().getProgram().isSingleEvent() )
                {
                    programStageInstance.setDueDate( dateValue );
                }

                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }

            LOG.debug( "Updating Execution Date, value added/changed" );

            message = programStageInstance.getId() + "";

            return SUCCESS;
        }

        return INPUT;
    }
}
