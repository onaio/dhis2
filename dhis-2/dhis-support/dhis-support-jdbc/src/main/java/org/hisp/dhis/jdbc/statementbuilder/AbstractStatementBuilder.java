package org.hisp.dhis.jdbc.statementbuilder;

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

import static org.hisp.dhis.system.util.DateUtils.getSqlDateString;

import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 */
public abstract class AbstractStatementBuilder
    implements StatementBuilder
{
    @Override
    public String encode( String value )
    {
        return encode( value, true );
    }
    
    @Override
    public String encode( String value, boolean quote )
    {
        if ( value != null )
        {
            value = value.endsWith( "\\" ) ? value.substring( 0, value.length() - 1 ) : value;
            value = value.replaceAll( QUOTE, QUOTE + QUOTE );
        }
        
        return quote ? ( QUOTE + value + QUOTE ) : value;
    }
    
    public String columnQuote( String column )
    {
        return column != null ? ( getColumnQuote() + column + getColumnQuote() ) : null;
    }

    @Override
    public String limitRecord( int offset, int limit )
    {
        return " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String getPeriodIdentifierStatement( Period period )
    {
        return
            "SELECT periodid FROM period WHERE periodtypeid=" + period.getPeriodType().getId() + " " + 
            "AND startdate='" + getSqlDateString( period.getStartDate() ) + "' " +
            "AND enddate='" + getSqlDateString( period.getEndDate() ) + "'";
    }

    @Override
    public String getCreateAggregatedDataValueTable( boolean temp )
    {
        return
            "CREATE TABLE aggregateddatavalue" + ( temp ? "_temp" : "" ) + " ( " +
            "dataelementid INTEGER, " +
            "categoryoptioncomboid INTEGER, " +
            "periodid INTEGER, " +
            "organisationunitid INTEGER, " +
            "periodtypeid INTEGER, " +
            "level INTEGER, " +
            "value " + getDoubleColumnType() + " );";
    }

    @Override
    public String getCreateAggregatedOrgUnitDataValueTable( boolean temp )
    {
        return
            "CREATE TABLE aggregatedorgunitdatavalue" + ( temp ? "_temp" : "" ) + " ( " +
            "dataelementid INTEGER, " +
            "categoryoptioncomboid INTEGER, " +
            "periodid INTEGER, " +
            "organisationunitid INTEGER, " +
            "organisationunitgroupid INTEGER, " +
            "periodtypeid INTEGER, " +
            "level INTEGER, " +
            "value " + getDoubleColumnType() + " );";
    }

    @Override
    public String getCreateAggregatedIndicatorTable( boolean temp )
    {
        return
            "CREATE TABLE aggregatedindicatorvalue" + ( temp ? "_temp" : "" ) + " ( " +
            "indicatorid INTEGER, " +
            "periodid INTEGER, " +
            "organisationunitid INTEGER, " +
            "periodtypeid INTEGER, " +
            "level INTEGER, " +
            "annualized VARCHAR( 10 ), " +
            "factor " + getDoubleColumnType() + ", " +
            "value " + getDoubleColumnType() + ", " +
            "numeratorvalue " + getDoubleColumnType() + ", " +
            "denominatorvalue " + getDoubleColumnType() + " );";
    }

    @Override
    public String getCreateAggregatedOrgUnitIndicatorTable( boolean temp )
    {
        return
            "CREATE TABLE aggregatedorgunitindicatorvalue" + ( temp ? "_temp" : "" ) + " ( " +
            "indicatorid INTEGER, " +
            "periodid INTEGER, " +
            "organisationunitid INTEGER, " +
            "organisationunitgroupid INTEGER, " +
            "periodtypeid INTEGER, " +
            "level INTEGER, " +
            "annualized VARCHAR( 10 ), " +
            "factor " + getDoubleColumnType() + ", " +
            "value " + getDoubleColumnType() + ", " +
            "numeratorvalue " + getDoubleColumnType() + ", " +
            "denominatorvalue " + getDoubleColumnType() + " );";
    }

    @Override
    public String getCreateDataSetCompletenessTable()
    {
        return
            "CREATE TABLE aggregateddatasetcompleteness ( " +
            "datasetid INTEGER, " +
            "periodid INTEGER, " +
            "periodname VARCHAR( 30 ), " +
            "organisationunitid INTEGER, " +
            "sources INTEGER, " +
            "registrations INTEGER, " +
            "registrationsOnTime INTEGER, " +
            "value " + getDoubleColumnType() + ", " +
            "valueOnTime " + getDoubleColumnType() + " );";
    }

    @Override
    public String getCreateOrgUnitDataSetCompletenessTable()
    {
        return
            "CREATE TABLE aggregatedorgunitdatasetcompleteness ( " +
            "datasetid INTEGER, " +
            "periodid INTEGER, " +
            "periodname VARCHAR( 30 ), " +
            "organisationunitid INTEGER, " +
            "organisationunitgroupid INTEGER, " +
            "sources INTEGER, " +
            "registrations INTEGER, " +
            "registrationsOnTime INTEGER, " +
            "value " + getDoubleColumnType() + ", " +
            "valueOnTime " + getDoubleColumnType() + " );";
    }
}
