package org.hisp.dhis.ll.action.llValidation;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
import java.util.List;

import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.ll.action.lldataentry.SelectedStateManager;

import com.opensymphony.xwork2.Action;

public class ShowAddLineListValidationForm
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private final static int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

    private List<LineListGroup> lineListGroups;

    public List<LineListGroup> getLineListGroups() {
        return lineListGroups;
    }

    private Integer selectedLineListGroupId;

    public Integer getSelectedLineListGroupId() {
        return selectedLineListGroupId;
    }

    public void setSelectedLineListGroupId(Integer selectedLineListGroupId) {
        this.selectedLineListGroupId = selectedLineListGroupId;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        lineListGroups = new ArrayList<LineListGroup>( lineListService.getAllLineListGroups() );
        System.out.println("LineListGroupId = "+selectedLineListGroupId);
        System.out.println("lineListGroups.size() = "+lineListGroups.size());

        // ---------------------------------------------------------------------
        // Validate selected LineListGroup
        // ---------------------------------------------------------------------

        LineListGroup selectedLineListGroup;

        if ( selectedLineListGroupId != null )
        {
            selectedLineListGroup = lineListService.getLineListGroup( selectedLineListGroupId );
        }
        else
        {
            selectedLineListGroup = selectedStateManager.getSelectedLineListGroup();

        }

        if ( selectedLineListGroup != null && lineListGroups.contains( selectedLineListGroup ) )
        {
            selectedLineListGroupId = selectedLineListGroup.getId();
            selectedStateManager.setSelectedLineListGroup( selectedLineListGroup );
        }
        else
        {
            selectedLineListGroupId = null;
            selectedStateManager.clearSelectedLineListGroup();

            return SUCCESS;
        }

        System.out.println("LineListGroup = "+selectedLineListGroup);
        System.out.println("LineListGroupId = "+selectedLineListGroupId);
        return SUCCESS;
    }
}
