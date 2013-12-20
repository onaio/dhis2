package org.hisp.dhis.dataanalysis;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.MathUtils;

/**
 * @author Lars Helge Overland
 */
public class StdDevOutlierAnalysisService
    implements DataAnalysisService
{
    private static final Log log = LogFactory.getLog( StdDevOutlierAnalysisService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataAnalysisStore dataAnalysisStore;

    public void setDataAnalysisStore( DataAnalysisStore dataAnalysisStore )
    {
        this.dataAnalysisStore = dataAnalysisStore;
    }

    // -------------------------------------------------------------------------
    // DataAnalysisService implementation
    // -------------------------------------------------------------------------

    public final Collection<DeflatedDataValue> analyse( Collection<OrganisationUnit> organisationUnits,
        Collection<DataElement> dataElements, Collection<Period> periods, Double stdDevFactor )
    {
        Set<Integer> units = new HashSet<Integer>( ConversionUtils.getIdentifiers( OrganisationUnit.class, organisationUnits ) );

        log.info( "Starting std dev analysis, no of org units: " + organisationUnits.size() + ", factor: " + stdDevFactor );
        
        Collection<DeflatedDataValue> outlierCollection = new ArrayList<DeflatedDataValue>();

        loop : for ( DataElement dataElement : dataElements )
        {
            // TODO filter periods with data element period type
            
            if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
            {
                Collection<DataElementCategoryOptionCombo> categoryOptionCombos = dataElement.getCategoryCombo()
                    .getOptionCombos();

                for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryOptionCombos )
                {
                    Map<Integer, Double> standardDeviations = dataAnalysisStore.getStandardDeviation( dataElement, categoryOptionCombo, units );
                    
                    Map<Integer, Double> averages = dataAnalysisStore.getAverage( dataElement, categoryOptionCombo, standardDeviations.keySet() );
                    
                    Map<Integer, Integer> lowBoundMap = new HashMap<Integer, Integer>();
                    Map<Integer, Integer> highBoundMap = new HashMap<Integer, Integer>();
                    
                    for ( Integer unit : averages.keySet() )
                    {
                        Double stdDev = standardDeviations.get( unit );
                        Double avg = averages.get( unit );
                        
                        if ( stdDev != null && stdDevFactor != null && avg != null )
                        {
                            lowBoundMap.put( unit, (int) MathUtils.getLowBound( stdDev, stdDevFactor, avg ) );
                            highBoundMap.put( unit, (int) MathUtils.getHighBound( stdDev, stdDevFactor, avg ) );                            
                        }
                    }

                    outlierCollection.addAll( dataAnalysisStore.getDeflatedDataValues( dataElement, categoryOptionCombo, periods,
                        lowBoundMap, highBoundMap ) );
                    
                    if ( outlierCollection.size() > MAX_OUTLIERS )
                    {
                        break loop;
                    }
                }
            }
        }

        return outlierCollection;
    }
}
