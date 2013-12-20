package org.hisp.dhis.dxf2.events.event;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdList;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultEventStore implements EventStore
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getAll( Program program, OrganisationUnit organisationUnit )
    {
        return getAll( Arrays.asList( program ), new ArrayList<ProgramStage>(), Arrays.asList( organisationUnit ), null, null );
    }

    @Override
    public List<Event> getAll( Program program, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        return getAll( Arrays.asList( program ), new ArrayList<ProgramStage>(), Arrays.asList( organisationUnit ), startDate, endDate );
    }

    @Override
    public List<Event> getAll( ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        return getAll( new ArrayList<Program>(), Arrays.asList( programStage ), Arrays.asList( organisationUnit ), null, null );
    }

    @Override
    public List<Event> getAll( ProgramStage programStage, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        return getAll( new ArrayList<Program>(), Arrays.asList( programStage ), Arrays.asList( organisationUnit ), startDate, endDate );
    }

    @Override
    public List<Event> getAll( Program program, ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        return getAll( Arrays.asList( program ), Arrays.asList( programStage ), Arrays.asList( organisationUnit ), null, null );
    }

    @Override
    public List<Event> getAll( Program program, ProgramStage programStage, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        return getAll( Arrays.asList( program ), Arrays.asList( programStage ), Arrays.asList( organisationUnit ), startDate, endDate );
    }

    @Override
    public List<Event> getAll( Program program, List<ProgramStage> programStages, OrganisationUnit organisationUnit )
    {
        return getAll( Arrays.asList( program ), programStages, Arrays.asList( organisationUnit ), null, null );
    }

    @Override
    public List<Event> getAll( Program program, List<ProgramStage> programStages, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        return getAll( Arrays.asList( program ), programStages, Arrays.asList( organisationUnit ), startDate, endDate );
    }

    @Override
    public List<Event> getAll( List<Program> programs, List<ProgramStage> programStages, List<OrganisationUnit> organisationUnits, Date startDate, Date endDate )
    {
        List<Event> events = new ArrayList<Event>();
        String sql = buildSql( getIdList( programs ), getIdList( programStages ), getIdList( organisationUnits ),
            startDate, endDate );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        Event event = new Event();
        event.setEvent( "not_valid" );

        while ( rowSet.next() )
        {
            if ( !event.getEvent().equals( rowSet.getString( "psi_uid" ) ) )
            {
                event = new Event();

                event.setEvent( rowSet.getString( "psi_uid" ) );
                event.setStatus( EventStatus.fromInt( rowSet.getInt( "psi_status" ) ) );
                event.setProgram( rowSet.getString( "p_uid" ) );
                event.setProgramStage( rowSet.getString( "ps_uid" ) );
                event.setStoredBy( rowSet.getString( "psi_completeduser" ) );
                event.setOrgUnit( rowSet.getString( "ou_uid" ) );
                event.setEventDate( rowSet.getString( "psi_executiondate" ) );

                events.add( event );
            }

            DataValue dataValue = new DataValue();
            dataValue.setValue( rowSet.getString( "pdv_value" ) );
            dataValue.setProvidedElsewhere( rowSet.getBoolean( "pdv_providedelsewhere" ) );
            dataValue.setDataElement( rowSet.getString( "de_uid" ) );
            dataValue.setStoredBy( rowSet.getString( "pdv_storedby" ) );

            event.getDataValues().add( dataValue );
        }

        return events;
    }

    private String buildSql( List<Integer> programIds, List<Integer> programStageIds, List<Integer> orgUnitIds, Date startDate, Date endDate )
    {
        String sql = "select p.uid as p_uid, ps.uid as ps_uid, psi.uid as psi_uid, psi.status as psi_status, ou.uid as ou_uid, psi.executiondate as psi_executiondate," +
            " psi.completeduser as psi_completeduser," +
            " pdv.value as pdv_value, pdv.storedby as pdv_storedby, pdv.providedelsewhere as pdv_providedelsewhere, de.uid as de_uid" +
            " from program p" +
            " left join programstage ps on ps.programid=p.programid" +
            " left join programstageinstance psi on ps.programstageid=psi.programstageid" +
            " left join organisationunit ou on (psi.organisationunitid=ou.organisationunitid)" +
            " left join patientdatavalue pdv on psi.programstageinstanceid=pdv.programstageinstanceid" +
            " left join dataelement de on pdv.dataelementid=de.dataelementid ";

        boolean startedWhere = false;

        if ( !programIds.isEmpty() )
        {
            if ( startedWhere )
            {
                sql += " and p.programid in (" + TextUtils.getCommaDelimitedString( programIds ) + ") ";
            }
            else
            {
                sql += " where p.programid in (" + TextUtils.getCommaDelimitedString( programIds ) + ") ";
                startedWhere = true;
            }
        }

        if ( !programStageIds.isEmpty() )
        {
            if ( startedWhere )
            {
                sql += " and ps.programstageid in (" + TextUtils.getCommaDelimitedString( programStageIds ) + ") ";
            }
            else
            {
                sql += " where ps.programstageid in (" + TextUtils.getCommaDelimitedString( programStageIds ) + ") ";
                startedWhere = true;
            }
        }

        if ( !orgUnitIds.isEmpty() )
        {
            if ( startedWhere )
            {
                sql += " and ou.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgUnitIds ) + ") ";
            }
            else
            {
                sql += " where ou.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgUnitIds ) + ") ";
                startedWhere = true;
            }
        }

        if ( startDate != null )
        {
            sql += " and psi.executiondate >= '" + getMediumDateString( startDate ) + "' ";
        }

        if ( endDate != null )
        {
            sql += " and psi.executiondate <= '" + getMediumDateString( endDate ) + "' ";
        }
				
        sql += " order by psi_uid;";

        return sql;
    }
}
