package org.hisp.dhis.patient.action.programstage;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @modified Tran Thanh Tri
 * @version $Id$
 */
public class GetProgramStageAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private UserGroupService userGroupService;
    
    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    private Collection<ProgramStageDataElement> programStageDataElements;

    public Collection<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }
    
    private List<UserGroup> userGroups;

    public List<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    public void setUserGroups( List<UserGroup> userGroups )
    {
        this.userGroups = userGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        programStage = programStageService.getProgramStage( id );

        programStageDataElements = programStage.getProgramStageDataElements();
        
        userGroups = new ArrayList<UserGroup>( userGroupService.getAllUserGroups() );

        return SUCCESS;
    }
}
