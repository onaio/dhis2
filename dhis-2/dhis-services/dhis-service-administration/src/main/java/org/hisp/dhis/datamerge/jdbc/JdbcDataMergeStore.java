package org.hisp.dhis.datamerge.jdbc;

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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datamerge.DataMergeStore;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class JdbcDataMergeStore
    implements DataMergeStore
{
    private static final Log log = LogFactory.getLog( JdbcDataMergeStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // DataMergeStore implementation
    // -------------------------------------------------------------------------

    public void eliminateDuplicateDataElement( DataElement destDataElement, DataElementCategoryOptionCombo destCategoryOptionCombo,
        DataElement sourceDataElemenet, DataElementCategoryOptionCombo sourceCategoryOptionCombo )
    {
        final int destDataElementId = destDataElement.getId();
        final int destCategoryOptionComboId = destCategoryOptionCombo.getId();
        final int sourceDataElementId = sourceDataElemenet.getId();
        final int sourceCategoryOptionComboId = sourceCategoryOptionCombo.getId();

        // Move from source to destination where destination does not exist
        
        String sql = statementBuilder.getMoveFromSourceToDestination( destDataElementId, destCategoryOptionComboId, sourceDataElementId, sourceCategoryOptionComboId );
        log.info( sql );        
        jdbcTemplate.execute( sql );
        
        // Update destination with source where source is last update
        
        sql = statementBuilder.getUpdateDestination( destDataElementId, destCategoryOptionComboId, sourceDataElementId, sourceCategoryOptionComboId );
        log.info( sql );        
        jdbcTemplate.execute( sql );

        // Delete remaining source data value audits
        
        sql = "DELETE FROM datavalue_audit WHERE dataelementid=" + sourceDataElementId + " AND categoryoptioncomboid=" + sourceCategoryOptionComboId + ";";

        log.info( sql );        
        jdbcTemplate.execute( sql );

        // Delete remaining source data values
        
        sql = "DELETE FROM datavalue WHERE dataelementid=" + sourceDataElementId + " AND categoryoptioncomboid=" + sourceCategoryOptionComboId + ";";

        log.info( sql );        
        jdbcTemplate.execute( sql );
    }
    
    public void mergeOrganisationUnits( OrganisationUnit dest, OrganisationUnit source )
    {
        final int destId = dest.getId();
        final int sourceId = source.getId();

        // Move from source to destination where destination does not exist
        
        String sql = statementBuilder.getMoveDataValueToDestination( sourceId, destId );
        log.info( sql );        
        jdbcTemplate.execute( sql );

        // Summarize destination and source where matching

        sql = statementBuilder.getSummarizeDestinationAndSourceWhereMatching( sourceId, destId );
        log.info( sql );
        jdbcTemplate.execute( sql );

        // TODO also deal with bool and string
            
        // Delete remaining source data value audits
        
        sql = "DELETE FROM datavalue_audit WHERE sourceid=" + sourceId + ";";

        log.info( sql );        
        jdbcTemplate.execute( sql );

        // Delete remaining source data values
            
        sql = "DELETE FROM datavalue WHERE sourceid=" + sourceId + ";";

        log.info( sql );        
        jdbcTemplate.execute( sql );

        // Delete complete data set registrations
        
        sql = "DELETE FROM completedatasetregistration WHERE sourceid=" + sourceId + ";";
        
        log.info( sql );        
        jdbcTemplate.execute( sql );

    }
}
