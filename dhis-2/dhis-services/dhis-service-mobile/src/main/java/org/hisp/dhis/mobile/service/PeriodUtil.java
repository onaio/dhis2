package org.hisp.dhis.mobile.service;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

public class PeriodUtil
{
    public static Period getPeriod( String periodName, PeriodType periodType )
        throws IllegalArgumentException
    {

        if ( periodType instanceof DailyPeriodType )
        {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat( pattern );
            Date date;
            try
            {
                date = formatter.parse( periodName );
            }
            catch ( ParseException e )
            {
                throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName()
                    + " and name " + periodName, e );
            }
            return periodType.createPeriod( date );

        }

        if ( periodType instanceof WeeklyPeriodType )
        {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat( pattern );
            Date date;
            try
            {
                date = formatter.parse( periodName );
            }
            catch ( ParseException e )
            {
                throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName()
                    + " and name " + periodName, e );
            }
            return periodType.createPeriod( date );

            // int dashIndex = periodName.indexOf( '-' );
            //
            // if ( dashIndex < 0 )
            // {
            // return null;
            // }
            //
            // int week = Integer.parseInt( periodName.substring( 0, dashIndex )
            // );
            // int year = Integer.parseInt( periodName.substring( dashIndex + 1,
            // periodName.length() ) );
            //
            // return periodType.createPeriod(year + "W" + week);
        }

        if ( periodType instanceof MonthlyPeriodType )
        {
            int dashIndex = periodName.indexOf( '-' );

            if ( dashIndex < 0 )
            {
                return null;
            }

            int month = Integer.parseInt( periodName.substring( 0, dashIndex ) );
            int year = Integer.parseInt( periodName.substring( dashIndex + 1, periodName.length() ) );

            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, year );
            cal.set( Calendar.MONTH, month );

            return periodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof YearlyPeriodType )
        {
            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, Integer.parseInt( periodName ) );

            return periodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof QuarterlyPeriodType )
        {
            Calendar cal = Calendar.getInstance();

            int month = 0;
            if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jan" ) )
            {
                month = 1;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Apr" ) )
            {
                month = 4;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jul" ) )
            {
                month = 6;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Oct" ) )
            {
                month = 10;
            }

            int year = Integer.parseInt( periodName.substring( periodName.lastIndexOf( " " ) + 1 ) );

            cal.set( Calendar.MONTH, month );
            cal.set( Calendar.YEAR, year );

            if ( month != 0 )
            {
                return periodType.createPeriod( cal.getTime() );
            }

        }

        throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName() + " and name "
            + periodName );
    }

    public static String dateToString( Date date )
    {
        DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        return dateFormat.format( date );
    }

    public static Date stringToDate( String dateString )
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date date = null;
        try
        {
            date = dateFormat.parse( dateString );
        }
        catch ( Exception e )
        {
            return null;
        }
        
        return date;
    }

    public static String convertDateFormat( String standardDate )
    {
        try
        {
            String[] tokens = standardDate.split( "-" );
            return tokens[2] + "-" + tokens[1] + "-" + tokens[0];
        }
        catch ( Exception e )
        {
            return standardDate;
        }
    }

}
