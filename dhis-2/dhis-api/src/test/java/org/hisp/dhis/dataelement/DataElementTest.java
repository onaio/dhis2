package org.hisp.dhis.dataelement;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementTest
{
    @Test
    public void getPeriodType()
    {
        PeriodType periodType = new MonthlyPeriodType();
        
        DataElement element = new DataElement();
        
        DataSet dataSetA = new DataSet( "A", periodType );
        DataSet dataSetB = new DataSet( "B", periodType );
        DataSet dataSetC = new DataSet( "C", periodType );
        
        element.getDataSets().add( dataSetA );
        element.getDataSets().add( dataSetB );
        element.getDataSets().add( dataSetC );
        
        assertEquals( periodType, element.getPeriodType() );
    }

    @Test
    public void periodTypeIsValid()
    {
        DataElement element = new DataElement();
        
        DataSet dataSetA = new DataSet( "A", new MonthlyPeriodType() );
        DataSet dataSetB = new DataSet( "B", new MonthlyPeriodType() );
        DataSet dataSetC = new DataSet( "C", new QuarterlyPeriodType() );
        
        element.getDataSets().add( dataSetA );
        element.getDataSets().add( dataSetB );
        
        assertTrue( element.periodTypeIsValid() );

        element.getDataSets().add( dataSetC );
        
        assertFalse( element.periodTypeIsValid() );
    }
}
