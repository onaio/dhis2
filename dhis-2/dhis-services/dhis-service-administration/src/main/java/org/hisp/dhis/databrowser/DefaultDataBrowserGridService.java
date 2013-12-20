package org.hisp.dhis.databrowser;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DefaultDataBrowserGridService
    implements DataBrowserGridService
{
    private static final String STARTDATE = "1900-01-01";

    private static final String ENDDATE = "3000-01-01";

    private static final String SPACE = " ";

    private static final String DASH = " - ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataBrowserGridStore dataBrowserGridStore;

    public void setDataBrowserGridStore( DataBrowserGridStore dataBrowserGridStore )
    {
        this.dataBrowserGridStore = dataBrowserGridStore;
    }

    // -------------------------------------------------------------------------
    // DataBrowserGridService implementation
    //
    // Basic
    // -------------------------------------------------------------------------

    public Grid getDataSetsInPeriod( String startDate, String endDate, PeriodType periodType, I18nFormat format,
        boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        return dataBrowserGridStore.getDataSetsBetweenPeriods( betweenPeriodIds, periodType, isZeroAdded );
    }

    public Grid getDataElementGroupsInPeriod( String startDate, String endDate, PeriodType periodType,
        I18nFormat format, boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        return dataBrowserGridStore.getDataElementGroupsBetweenPeriods( betweenPeriodIds, isZeroAdded );
    }

    public Grid getOrgUnitGroupsInPeriod( String startDate, String endDate, PeriodType periodType, I18nFormat format,
        boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        return dataBrowserGridStore.getOrgUnitGroupsBetweenPeriods( betweenPeriodIds, isZeroAdded );
    }

    // -------------------------------------------------------------------------
    // Advance
    // -------------------------------------------------------------------------

    public Grid getOrgUnitsInPeriod( Integer orgUnitParent, String startDate, String endDate, PeriodType periodType,
        Integer maxLevel, I18nFormat format, boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        Grid grid = new ListGrid();
        List<Integer> metaIds = new ArrayList<Integer>();

        dataBrowserGridStore.setStructureForOrgUnit( grid, orgUnitParent, metaIds );

        dataBrowserGridStore.setCountOrgUnitsBetweenPeriods( grid, orgUnitParent, betweenPeriodIds, maxLevel, metaIds,
            isZeroAdded );

        return grid;
    }

    public Grid getCountDataElementsForDataSetInPeriod( Integer dataSetId, String startDate, String endDate,
        PeriodType periodType, I18nFormat format, boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        Grid grid = new ListGrid();
        List<Integer> metaIds = new ArrayList<Integer>();

        dataBrowserGridStore.setDataElementStructureForDataSet( grid, dataSetId, metaIds );

        dataBrowserGridStore.setCountDataElementsForDataSetBetweenPeriods( grid, dataSetId, periodType,
            betweenPeriodIds, metaIds, isZeroAdded );

        return grid;
    }

    public Grid getCountDataElementsForDataElementGroupInPeriod( Integer dataElementGroupId, String startDate,
        String endDate, PeriodType periodType, I18nFormat format, boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        Grid grid = new ListGrid();
        List<Integer> metaIds = new ArrayList<Integer>();

        dataBrowserGridStore.setDataElementStructureForDataElementGroup( grid, dataElementGroupId, metaIds );

        dataBrowserGridStore.setCountDataElementsForDataElementGroupBetweenPeriods( grid, dataElementGroupId,
            betweenPeriodIds, metaIds, isZeroAdded );

        return grid;
    }

    public Grid getCountDataElementGroupsForOrgUnitGroupInPeriod( Integer orgUnitGroupId, String startDate,
        String endDate, PeriodType periodType, I18nFormat format, boolean isZeroAdded )
    {
        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        Grid grid = new ListGrid();
        List<Integer> metaIds = new ArrayList<Integer>();

        dataBrowserGridStore.setDataElementGroupStructureForOrgUnitGroup( grid, orgUnitGroupId, metaIds );

        dataBrowserGridStore.setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( grid, orgUnitGroupId,
            betweenPeriodIds, metaIds, isZeroAdded );

        return grid;
    }

    // -------------------------------------------------------------------------
    // Advance - Raw data
    // -------------------------------------------------------------------------

    public Grid getRawDataElementsForOrgUnitInPeriod( Integer orgUnitId, String startDate, String endDate,
        PeriodType periodType, I18nFormat format, boolean isZeroAdded )
    {
        Grid grid = new ListGrid();
        List<Integer> metaIds = new ArrayList<Integer>();

        List<Integer> betweenPeriodIds = getAllPeriodIdsBetweenDatesOnPeriodType( startDate, endDate, periodType,
            format );

        dataBrowserGridStore.setDataElementStructureForOrgUnit( grid, orgUnitId, metaIds );

        dataBrowserGridStore.setRawDataElementsForOrgUnitBetweenPeriods( grid, orgUnitId, betweenPeriodIds, metaIds,
            isZeroAdded );

        return grid;
    }

    // -------------------------------------------------------------------------
    // Others
    // -------------------------------------------------------------------------

    public String convertDate( PeriodType periodType, String dateString, I18n i18n, I18nFormat format )
    {
        if ( !DateUtils.dateIsValid( dateString ) )
        {
            return i18n.getString( dateString );
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat( Period.DEFAULT_DATE_FORMAT );

        try
        {
            Date date = dateFormat.parse( dateString );
            CalendarPeriodType calendarPeriodType = (CalendarPeriodType) periodType;

            return format.formatPeriod( calendarPeriodType.createPeriod( date ) );
        }
        catch ( ParseException pe )
        {
            throw new RuntimeException( "Date string could not be parsed: " + dateString );
        }
    }

    public String getFromToDateFormat( PeriodType periodType, String fromDate, String toDate, I18nFormat format )
    {
        String stringFormatDate = "";
        List<Period> periods = new ArrayList<Period>( this.getPeriodsList( periodType, fromDate, toDate ) );

        for ( Period period : periods )
        {
            String sTemp = format.formatPeriod( period );

            if ( stringFormatDate.isEmpty() )
            {
                stringFormatDate = SPACE + sTemp;
            }
            else
            {
                if ( !stringFormatDate.contains( sTemp ) )
                {
                    stringFormatDate += DASH + sTemp;
                }
            }
        }

        return stringFormatDate;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Helper-method that finds all PeriodIds between a given period. Uses
     * functionality already in the DHIS. Returns a list with all id's that was
     * found.
     * 
     * @param startDate
     * @param endDate
     * @param periodType
     * @return List<Integer>
     */
    private List<Integer> getAllPeriodIdsBetweenDatesOnPeriodType( String startDate, String endDate,
        PeriodType periodType, I18nFormat i18nFormat )
    {
        if ( startDate == null || startDate.length() == 0 )
        {
            startDate = STARTDATE;
        }
        if ( endDate == null || endDate.length() == 0 )
        {
            endDate = ENDDATE;
        }

        Date date1 = i18nFormat.parseDate( startDate );
        Date date2 = i18nFormat.parseDate( endDate );

        Collection<Period> pp = periodService.getPeriodsBetweenDates( periodType, date1, date2 );

        Iterator<Period> it = pp.iterator();
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();

        while ( it.hasNext() )
        {
            betweenPeriodIds.add( it.next().getId() );
        }

        if ( betweenPeriodIds.size() <= 0 )
        {
            betweenPeriodIds.add( -1 );
        }

        return betweenPeriodIds;
    }

    /**
     * This is a helper method for checking if the fromDate is later than the
     * toDate. This is necessary in case a user sends the dates with HTTP GET.
     * 
     * @param fromDate
     * @param toDate
     * @return List of Periods
     */
    private List<Period> getPeriodsList( PeriodType periodType, String fromDate, String toDate )
    {
        String formatString = Period.DEFAULT_DATE_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat( formatString );

        Date date1 = new Date();
        Date date2 = new Date();

        try
        {
            date1 = sdf.parse( fromDate );
            date2 = sdf.parse( toDate );

            List<Period> periods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, date1,
                date2 ) );

            if ( periods.isEmpty() )
            {
                CalendarPeriodType calendarPeriodType = (CalendarPeriodType) periodType;

                periods.add( calendarPeriodType.createPeriod( date1 ) );
                periods.add( calendarPeriodType.createPeriod( date2 ) );
            }

            Collections.sort( periods, new AscendingPeriodComparator() );

            return periods;
        }
        catch ( ParseException e )
        {
            return null; // The user hasn't specified any dates
        }
    }
}
