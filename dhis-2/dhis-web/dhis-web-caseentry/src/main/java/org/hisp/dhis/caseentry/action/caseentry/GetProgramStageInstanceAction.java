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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class GetProgramStageInstanceAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private ProgramStageInstance programStageInstance;

    public ProgramStageInstance getProgramStageInstance()
    {
        return programStageInstance;
    }

    private String latitude;

    public String getLatitude()
    {
        return latitude;
    }

    private String longitude;

    public String getLongitude()
    {
        return longitude;
    }

    private String currentUsername;

    public String getCurrentUsername()
    {
        return currentUsername;
    }

    private Map<String, PatientDataValue> dataValueMap = new HashMap<String, PatientDataValue>();

    public Map<String, PatientDataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        if ( programStageInstanceId == null )
        {
            return INPUT;
        }

        programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );

        populateCurrentUsername();
        populateCoordinates();
        populateDataValueMap();

        return SUCCESS;
    }

    private void populateCurrentUsername()
    {
        currentUsername = currentUserService.getCurrentUsername();
    }

    private void populateCoordinates()
    {
        if ( programStageInstance != null && programStageInstance.getProgramStage() != null
            && programStageInstance.getProgramStage().getCaptureCoordinates() )
        {
            longitude = ValidationUtils.getLongitude( programStageInstance.getCoordinates() );
            latitude = ValidationUtils.getLatitude( programStageInstance.getCoordinates() );
        }
    }

    private void populateDataValueMap()
    {
        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programStageInstance );

        dataValueMap = new HashMap<String, PatientDataValue>( patientDataValues.size() );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            dataValueMap.put( patientDataValue.getDataElement().getUid(), patientDataValue );
        }
    }
}

