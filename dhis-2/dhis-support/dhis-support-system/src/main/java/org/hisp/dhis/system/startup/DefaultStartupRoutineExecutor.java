package org.hisp.dhis.system.startup;

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
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.hisp.dhis.datavalue.DataValue.TRUE;

/**
 * Default implementation of StartupRoutineExecutor. The execute method will
 * execute the added StartupRoutines ordered by their runlevels. Startup routines
 * can be ignored from the command line by appending the below.
 * 
 * <code>-Ddhis.skip.startup=true</code>
 * 
 * @author <a href="mailto:torgeilo@gmail.com">Torgeir Lorange Ostby</a>
 * @version $Id: DefaultStartupRoutineExecutor.java 5781 2008-10-01 12:12:48Z larshelg $
 */
public class DefaultStartupRoutineExecutor
    extends AbstractStartupRoutine
    implements StartupRoutineExecutor
{
    private static final Log LOG = LogFactory.getLog( DefaultStartupRoutineExecutor.class );

    private static final String SKIP_PROP = "dhis.skip.startup";
    
    private List<StartupRoutine> routines = new ArrayList<StartupRoutine>();

    // -------------------------------------------------------------------------
    // Add methods
    // -------------------------------------------------------------------------

    public void addStartupRoutine( StartupRoutine routine )
    {
        routines.add( routine );
    }

    public void addStartupRoutines( Collection<StartupRoutine> routines )
    {
        for ( StartupRoutine routine : routines )
        {
            addStartupRoutine( routine );
        }
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public void execute()
        throws Exception
    {
        execute( false );
    }
    
    public void executeForTesting()
        throws Exception
    {
        execute( true );
    }
    
    private void execute( boolean testing )
        throws Exception
    {
        if ( TRUE.equalsIgnoreCase( System.getProperty( SKIP_PROP ) ) )
        {
            LOG.info( "Skipping startup routines" );
            return;
        }
        
        Collections.sort( routines, new StartupRoutineComparator() );

        int total = routines.size();
        int index = 1;

        for ( StartupRoutine routine : routines )
        {
            if ( !( testing && routine.skipInTests() ) )
            {
                LOG.info( "Executing startup routine [" + index + " of " + total + ", runlevel " + routine.getRunlevel()
                    + "]: " + routine.getName() );

                routine.execute();
                
                ++index;
            }
        }

        LOG.info( "All startup routines done" );
    }
}
