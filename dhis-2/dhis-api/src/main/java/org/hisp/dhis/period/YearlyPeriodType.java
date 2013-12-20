package org.hisp.dhis.period;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * PeriodType for yearly Periods. A valid yearly Period has startDate set to
 * January 1st and endDate set to the last day of the same year.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: YearlyPeriodType.java 2971 2007-03-03 18:54:56Z torgeilo $
 */
public class YearlyPeriodType
    extends CalendarPeriodType
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 3893035414025085437L;

    private static final String ISO_FORMAT = "yyyy";

    /**
     * The name of the YearlyPeriodType, which is "Yearly".
     */
    public static final String NAME = "Yearly";

    public static final int FREQUENCY_ORDER = 365;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod()
    {
        return createPeriod( createCalendarInstance() );
    }

    @Override
    public Period createPeriod( Date date )
    {
        return createPeriod( createCalendarInstance( date ) );
    }

    @Override
    public Period createPeriod( Calendar cal )
    {
        cal.set( Calendar.DAY_OF_YEAR, 1 );        
        Date startDate = cal.getTime();
        cal.set( Calendar.DAY_OF_YEAR, cal.getActualMaximum( Calendar.DAY_OF_YEAR ) );

        return new Period( this, startDate, cal.getTime() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.YEAR, 1 );
        return createPeriod( cal );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.YEAR, -1 );
        return createPeriod( cal );
    }

    /**
     * Generates yearly periods for the last 5, current and next 5 years.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.add( Calendar.YEAR, -5 );
        cal.set( Calendar.DAY_OF_YEAR, 1 );

        ArrayList<Period> periods = new ArrayList<Period>();

        for ( int i = 0; i < 11; ++i )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.YEAR, 1 );
        }

        return periods;
    }

    /**
     * Generates the last 5 years where the last one is the year which the given 
     * date is inside.
     */
    @Override
    public List<Period> generateRollingPeriods( Date date )
    {
        return generateLast5Years( date );
    }

    /**
     * Generates the last 5 years where the last one is the year which the given 
     * date is inside.
     */
    @Override
    public List<Period> generateLast5Years( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.add( Calendar.YEAR, -4 );
        cal.set( Calendar.DAY_OF_YEAR, 1 );

        ArrayList<Period> periods = new ArrayList<Period>();

        for ( int i = 0; i < 5; ++i )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.YEAR, 1 );
        }

        return periods;
    }

    @Override
    public String getIsoDate( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int year = cal.get( Calendar.YEAR );
        return String.valueOf( year );
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        Calendar cal = createCalendarInstance();
        cal.set( Calendar.YEAR, Integer.parseInt( isoDate ) );
        return createPeriod( cal );
    }

    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }
    
    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        date = date != null ? date : new Date();        
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        Calendar cal = createCalendarInstance( date );        
        cal.add( Calendar.YEAR, (rewindedPeriods * -1) );

        return cal.getTime();
    }
}
