package org.hisp.dhis.patient.scheduling;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_SEND_MESSAGE_SCHEDULED_TASKS;
import static org.hisp.dhis.system.scheduling.Scheduler.STATUS_NOT_STARTED;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.scheduling.Scheduler;

/**
 * @author Chau Thu Tran
 *
 * @version DefaultProgramSchedulingManager.java 12:51:02 PM Sep 10, 2012 $
 */
public class DefaultProgramSchedulingManager implements ProgramSchedulingManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private Scheduler scheduler;

    public void setScheduler( Scheduler scheduler )
    {
        this.scheduler = scheduler;
    }

    private Map<String, Runnable> tasks = new HashMap<String, Runnable>();

    public void setTasks( Map<String, Runnable> tasks )
    {
        this.tasks = tasks;
    }

    // -------------------------------------------------------------------------
    // SchedulingManager implementation
    // -------------------------------------------------------------------------

    public void scheduleTasks()
    {
        Map<String, String> keyCronMap = getScheduledTasks();
        
        for ( String key : keyCronMap.keySet() )
        {
            String cron = keyCronMap.get( key );
            Runnable task = tasks.get( key );
            
            if ( cron != null && task != null )
            {
                scheduler.scheduleTask( key, task, cron );
            }
        }
    }
    
    public void scheduleTasks( Map<String, String> keyCronMap )
    {
        systemSettingManager.saveSystemSetting( KEY_SEND_MESSAGE_SCHEDULED_TASKS, new HashMap<String, String>( keyCronMap ) );
        
        scheduleTasks();
    }
    
    public void stopTasks()
    {
        systemSettingManager.saveSystemSetting( KEY_SEND_MESSAGE_SCHEDULED_TASKS, null );
        
        scheduler.stopAllTasks();
    }
    
    public void executeTasks()
    {
        Map<String, String> keyCronMap = getScheduledTasks();
        
        for ( String key : keyCronMap.keySet() )
        {
            Runnable task = tasks.get( key );
            
            if ( task != null )
            {
                scheduler.executeTask( task );
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, String> getScheduledTasks()
    {
        return (Map<String, String>) systemSettingManager.getSystemSetting( KEY_SEND_MESSAGE_SCHEDULED_TASKS, new HashMap<String, String>() );
    }
    
    public String getTaskStatus()
    {
        Map<String, String> keyCronMap = getScheduledTasks();
                
        if ( keyCronMap.size() == 0 )
        {
            return STATUS_NOT_STARTED;
        }
        
        return scheduler.getTaskStatus( keyCronMap.keySet().iterator().next() );
    }
}

