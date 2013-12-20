package org.hisp.dhis.analytics.table;

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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.hisp.dhis.analytics.AnalyticsTable;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class JdbcCompletenessTargetTableManager
    extends AbstractJdbcTableManager
{
    @Override
    @Transactional
    public List<AnalyticsTable> getTables( boolean last3YearsOnly )
    {
        List<AnalyticsTable> tables = new ArrayList<AnalyticsTable>();
        tables.add( new AnalyticsTable( getTableName(), getDimensionColumns( null ) ) );
        return tables;
    }
    
    public boolean validState()
    {
        return true;
    }    
    
    public String getTableName()
    {
        return "completenesstarget";
    }

    public void createTable( AnalyticsTable table )
    {
        final String tableName = table.getTempTableName();
        
        final String sqlDrop = "drop table " + tableName;
        
        executeSilently( sqlDrop );

        String sqlCreate = "create table " + tableName + " (";

        for ( String[] col : getDimensionColumns( table ) )
        {
            sqlCreate += col[0] + " " + col[1] + ",";
        }
        
        sqlCreate += "value double precision)";
        
        log.info( "Create SQL: " + sqlCreate );
        
        executeSilently( sqlCreate );
    }

    @Async
    public Future<?> populateTableAsync( ConcurrentLinkedQueue<AnalyticsTable> tables )
    {
        taskLoop : while ( true )
        {
            AnalyticsTable table = tables.poll();
                
            if ( table == null )
            {
                break taskLoop;
            }
            
            String sql = "insert into " + table.getTempTableName() + " (";
    
            for ( String[] col : getDimensionColumns( table ) )
            {
                sql += col[0] + ",";
            }
    
            sql += "value) select ";
    
            for ( String[] col : getDimensionColumns( table ) )
            {
                sql += col[2] + ",";
            }
                        
            sql +=
                "1 as value " +
                "from datasetsource dss " +
                "left join dataset ds on dss.datasetid=ds.datasetid " +
                "left join _orgunitstructure ous on dss.sourceid=ous.organisationunitid " +
                "left join _organisationunitgroupsetstructure ougs on dss.sourceid=ougs.organisationunitid";            
    
            log.info( "Populate SQL: "+ sql );
            
            jdbcTemplate.execute( sql );
        }
        
        return null;
    }

    public List<String[]> getDimensionColumns( AnalyticsTable table )
    {
        List<String[]> columns = new ArrayList<String[]>();

        Collection<OrganisationUnitGroupSet> orgUnitGroupSets = 
            organisationUnitGroupService.getAllOrganisationUnitGroupSets();
        
        Collection<OrganisationUnitLevel> levels =
            organisationUnitService.getOrganisationUnitLevels();
        
        for ( OrganisationUnitGroupSet groupSet : orgUnitGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "ougs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }
        
        for ( OrganisationUnitLevel level : levels )
        {
            String column = quote( PREFIX_ORGUNITLEVEL + level.getLevel() );
            String[] col = { column, "character(11)", "ous." + column };
            columns.add( col );
        }

        String[] ds = { "ds", "character(11) not null", "ds.uid" };
        
        columns.add( ds );
        
        return columns;
    }

    public Date getEarliestData()
    {
        return null; // Not relevant
    }

    public Date getLatestData()
    {
        return null; // Not relevant
    }

    @Async
    public Future<?> applyAggregationLevels( ConcurrentLinkedQueue<AnalyticsTable> tables, Collection<String> dataElements, int aggregationLevel )
    {
        return null; // Not relevant
    }
}
