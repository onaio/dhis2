package org.hisp.dhis.common;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Map;
import java.util.UUID;

/**
 * @author bobj
 */
public class IdentityPopulator
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( IdentityPopulator.class );

    private static final String[] tables = { "chart", "constant", "concept", "attribute", "indicatortype", "indicatorgroupset", "indicator",
        "indicatorgroup", "datadictionary", "validationrulegroup", "validationrule", "dataset", "orgunitlevel", "document",
        "organisationunit", "orgunitgroup", "orgunitgroupset", "dataelementcategoryoption", "dataelementgroup", "sqlview",
        "dataelement", "dataelementgroupset", "dataelementcategory", "categorycombo", "categoryoptioncombo", "map", "mapview",
        "reporttable", "report", "messageconversation", "message", "userinfo", "usergroup", "userrole", "maplegend",
        "maplegendset", "maplayer", "section", "optionset", "program", "programinstance", "programstage", "programstageinstance",
        "patient", "patientidentifier", "relationshiptype"
    };

    private static final Map<String, String> TABLE_ID_MAP = DimensionalObjectUtils.asMap(
        "dataelementcategoryoption", "categoryoptionid",
        "dataelementcategory", "categoryid" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public void execute()
        throws Exception
    {
        for ( String table : tables )
        {
            try
            {
                log.debug( "Checking table: " + table );

                int count = 0;

                SqlRowSet resultSet = jdbcTemplate.queryForRowSet( "SELECT * from " + table + " WHERE uid IS NULL" );

                while ( resultSet.next() )
                {
                    ++count;
                    String idColumn = getIdColumn( table );
                    int id = resultSet.getInt( idColumn );
                    String sql = "update " + table + " set uid = '" + CodeGenerator.generateCode() + "' where " + idColumn + " = " + id;
                    jdbcTemplate.update( sql );
                }

                if ( count > 0 )
                {
                    log.info( count + " uids set on " + table );
                }

                count = 0;

                resultSet = jdbcTemplate.queryForRowSet( "SELECT * from " + table + " WHERE lastUpdated IS NULL" );

                String timestamp = DateUtils.getLongDateString();

                while ( resultSet.next() )
                {
                    ++count;
                    String idColumn = getIdColumn( table );
                    int id = resultSet.getInt( idColumn );
                    String sql = "update " + table + " set lastupdated = '" + timestamp + "' where " + idColumn + " = " + id;
                    jdbcTemplate.update( sql );
                }

                if ( count > 0 )
                {
                    log.info( count + " last updated set on " + table );
                }

                count = 0;

                resultSet = jdbcTemplate.queryForRowSet( "SELECT * from " + table + " WHERE created IS NULL" );

                while ( resultSet.next() )
                {
                    ++count;
                    String idColumn = getIdColumn( table );
                    int id = resultSet.getInt( idColumn );
                    String sql = "update " + table + " set created = '" + timestamp + "' where " + idColumn + " = " + id;
                    jdbcTemplate.update( sql );
                }

                if ( count > 0 )
                {
                    log.info( count + " timestamps set on " + table );
                }
            }
            catch ( Exception ex ) // Log and continue
            {
                log.error( "Problem updating: " + table + ", id column: " + getIdColumn( table ), ex );
            }
        }

        log.debug( "Identifiable properties updated" );

        createUidConstraints();

        log.debug( "Identifiable constraints updated" );

        createOrgUnitUuids();

        log.debug( "Organisation unit uuids updated" );
    }

    private String getIdColumn( String table )
    {
        return TABLE_ID_MAP.containsKey( table ) ? TABLE_ID_MAP.get( table ) : (table + "id");
    }

    private void createUidConstraints()
    {
        for ( String table : tables )
        {
            try
            {
                final String sql = "ALTER TABLE " + table + " ADD CONSTRAINT " + table + "_uid_key UNIQUE(uid)";
                jdbcTemplate.execute( sql );
            }
            catch ( Exception ex ) // Log and continue, will fail after first run
            {
                log.debug( "Could not create uid constraint on table " + table +
                    ", might already be created or column contains duplicates", ex );
            }
        }
    }

    private void createOrgUnitUuids()
    {
        try
        {
            SqlRowSet resultSet = jdbcTemplate.queryForRowSet( "SELECT * from organisationunit WHERE uuid IS NULL" );
            int count = 0;

            while ( resultSet.next() )
            {
                ++count;
                int id = resultSet.getInt( "organisationunitid" );
                String sql = "update organisationunit set uuid = '" + UUID.randomUUID().toString() + "' where organisationunitid = " + id;
                jdbcTemplate.update( sql );
            }

            if ( count > 0 )
            {
                log.info( count + " UUIDs updated on organisationunit" );
            }
        }
        catch ( Exception ex ) // Log and continue
        {
            log.error( "Problem updating organisationunit: ", ex );
        }
    }
}
