package org.hisp.dhis.api.controller;

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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.analytics.scheduling.AnalyticsTableTask;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.resourcetable.scheduling.ResourceTableTask;
import org.hisp.dhis.scheduling.DataMartTask;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.validation.scheduling.MonitoringTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = ResourceTableController.RESOURCE_PATH )
public class ResourceTableController
{
    public static final String RESOURCE_PATH = "/resourceTables";
    
    @Resource(name="analyticsAllTask")
    private AnalyticsTableTask analyticsTableTask;
    
    @Resource(name="dataMartLast12MonthsTask")
    private DataMartTask dataMartTask;
    
    @Autowired
    private ResourceTableTask resourceTableTask;
    
    @Autowired
    private MonitoringTask monitoringTask;
    
    @Autowired
    private Scheduler scheduler;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    //TODO make tasks prototypes to avoid potential concurrency issues?
    
    @RequestMapping( value = "/analytics", method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATA_MART_ADMIN')" )
    public void analytics( HttpServletResponse response )
    {
        analyticsTableTask.setTaskId( new TaskId( TaskCategory.DATAMART, currentUserService.getCurrentUser() ) );
        
        scheduler.executeTask( analyticsTableTask );
        
        ContextUtils.okResponse( response, "Initiated analytics table update" );
    }

    @RequestMapping( value = "/dataMart", method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATA_MART_ADMIN')" )
    public void dataMart( HttpServletResponse response )
    {
        dataMartTask.setTaskId( new TaskId( TaskCategory.DATAMART, currentUserService.getCurrentUser() ) );
        
        scheduler.executeTask( dataMartTask );
        
        ContextUtils.okResponse( response, "Initiated data mart update" );
    }
    
    @RequestMapping( method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    public void resourceTables( HttpServletResponse response )
    {
        resourceTableTask.setTaskId( new TaskId( TaskCategory.RESOURCETABLE_UPDATE, currentUserService.getCurrentUser() ) );
        
        scheduler.executeTask( resourceTableTask );
        
        ContextUtils.okResponse( response, "Initiated resource table update" );
    }    

    @RequestMapping( value = "/monitoring", method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    public void monitoring( HttpServletResponse response )
    {
        monitoringTask.setTaskId( new TaskId( TaskCategory.MONITORING, currentUserService.getCurrentUser() ) );
        
        scheduler.executeTask( monitoringTask );
        
        ContextUtils.okResponse( response, "Initiated data monitoring" );
    }   
}
