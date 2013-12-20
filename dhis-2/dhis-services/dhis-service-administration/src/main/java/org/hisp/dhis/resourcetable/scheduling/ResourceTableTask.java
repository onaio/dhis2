package org.hisp.dhis.resourcetable.scheduling;

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

import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.resourcetable.ResourceTableService;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.sqlview.SqlViewService;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class ResourceTableTask
    implements Runnable
{
    @Autowired
    private ResourceTableService resourceTableService;
    
    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private MessageService messageService;

    private TaskId taskId;

    public void setTaskId( TaskId taskId )
    {
        this.taskId = taskId;
    }

    // -------------------------------------------------------------------------
    // Runnable implementation
    // -------------------------------------------------------------------------

    @Override
    public void run()
    {
        notifier.notify( taskId, "Generating resource tables" );
        
        try
        {
            generateAll();
            
            notifier.notify( taskId, NotificationLevel.INFO, "Resource tables generated", true );
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( taskId, NotificationLevel.ERROR, "Process failed: " + ex.getMessage(), true );
            
            messageService.sendFeedback( "Resource table process failed", "Resource table process failed, please check the logs.", null );
            
            throw ex;
        }
    }    

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void generateAll()
    {
        sqlViewService.dropAllSqlViewTables();
        resourceTableService.generateOrganisationUnitStructures();        
        resourceTableService.generateCategoryOptionComboNames();
        resourceTableService.generateDataElementGroupSetTable();
        resourceTableService.generateIndicatorGroupSetTable();
        resourceTableService.generateOrganisationUnitGroupSetTable();
        resourceTableService.generateCategoryTable();
        resourceTableService.generateDataElementTable();
        resourceTableService.generatePeriodTable();
        resourceTableService.generateDatePeriodTable();
        resourceTableService.generateDataElementCategoryOptionComboTable();
        sqlViewService.createAllViewTables();
    }
}
