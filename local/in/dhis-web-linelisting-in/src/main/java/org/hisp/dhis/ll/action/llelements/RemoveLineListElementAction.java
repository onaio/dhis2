package org.hisp.dhis.ll.action.llelements;

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

import java.util.Collection;
import java.util.Iterator;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class RemoveLineListElementAction
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

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------
    private int lineListElementId;

    public void setLineListElementId( int lineListElementId )
    {
        this.lineListElementId = lineListElementId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        LineListElement element = lineListService.getLineListElement( lineListElementId );
        try
        {
            Boolean doNotDelete = false;
            Collection<LineListGroup> allLineListGroups = lineListService.getAllLineListGroups();
            Iterator<LineListGroup> groupIterator = allLineListGroups.iterator();
            //Iterator groupIterator = allLineListGroups.iterator();
            while ( groupIterator.hasNext() )
            {
                LineListGroup group = (LineListGroup) groupIterator.next();
                Collection<LineListElement> allElements = group.getLineListElements();
                if ( allElements.contains( element ) )
                {
                    doNotDelete = true;
                    break;
                }
            }
            if ( doNotDelete != false )
            {
                message = "\"" + element.getName() + "\" element cannot be deleted as it is used in groups";
                return ERROR;
            }
        }
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                //message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getClassName();
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();

                return ERROR;
            }
        }
        lineListService.deleteLineListElement( element );
        return SUCCESS;
    }
}
