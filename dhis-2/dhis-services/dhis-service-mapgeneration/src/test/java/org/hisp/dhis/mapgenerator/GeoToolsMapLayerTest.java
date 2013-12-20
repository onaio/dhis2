package org.hisp.dhis.mapgenerator;

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

import java.awt.Color;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapgeneration.InternalMapLayer;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Kenneth Solbø Andersen <kennetsa@ifi.uio.no>
 */
public class GeoToolsMapLayerTest
    extends DhisSpringTest
{
    private InternalMapLayer internalMapLayer;

    private MappingService mappingService;

    private OrganisationUnit organisationUnit;

    private OrganisationUnitLevel organisationUnitLevel;

    private IndicatorGroup indicatorGroup;

    private IndicatorType indicatorType;

    private Indicator indicator;

    private DataElement dataElement;

    private DataElementGroup dataElementGroup;

    private PeriodType periodType;

    private Period period;

    private MapLegendSet mapLegendSet;

    @Override
    public void setUpTest()
    {
        mappingService = (MappingService) getBean( MappingService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitLevel = new OrganisationUnitLevel( 1, "Level" );

        organisationUnitService.addOrganisationUnit( organisationUnit );
        organisationUnitService.addOrganisationUnitLevel( organisationUnitLevel );

        indicatorGroup = createIndicatorGroup( 'A' );
        indicatorService.addIndicatorGroup( indicatorGroup );

        indicatorType = createIndicatorType( 'A' );
        indicatorService.addIndicatorType( indicatorType );

        indicator = createIndicator( 'A', indicatorType );
        indicatorService.addIndicator( indicator );

        dataElement = createDataElement( 'A' );
        dataElementService.addDataElement( dataElement );

        dataElementGroup = createDataElementGroup( 'A' );
        dataElementGroup.getMembers().add( dataElement );
        dataElementService.addDataElementGroup( dataElementGroup );

        periodType = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );
        period = createPeriod( periodType, getDate( 2000, 1, 1 ), getDate( 2000, 2, 1 ) );
        periodService.addPeriod( period );

        mapLegendSet = createMapLegendSet( 'A' );
        mappingService.addMapLegendSet( mapLegendSet );

        internalMapLayer = new InternalMapLayer();
        internalMapLayer.setRadiusLow( 15 );
        internalMapLayer.setRadiusHigh( 35 );
        internalMapLayer.setColorLow( Color.YELLOW );
        internalMapLayer.setColorHigh( Color.RED );
        internalMapLayer.setOpacity( 0.5f );
    }

    @Test
    @Ignore
    public void testBuildGeometryForOrganisationUnit()
    {
        //TODO
    }

    @Test
    @Ignore
    public void testGetAllMapObjects()
    {
        //TODO
    }

    @Ignore
    @Test
    public void testSetGetRadiusHigh()
    {
        internalMapLayer.setRadiusHigh( 45 );
        assertEquals( 45.8F, internalMapLayer.getRadiusHigh(), 0.00001F );
        internalMapLayer.setRadiusHigh( 82 );
        assertEquals( 82.023984F, internalMapLayer.getRadiusHigh(), 0.00001F );
    }

    @Ignore
    @Test
    public void testSetGetRadiusLow()
    {
        internalMapLayer.setRadiusLow( 45 );
        assertEquals( 45.8F, internalMapLayer.getRadiusLow(), 0.00001F );
        internalMapLayer.setRadiusLow( 82 );
        assertEquals( 82.023984F, internalMapLayer.getRadiusLow(), 0.00001F );
    }

    @Ignore
    @Test
    public void testSetGetColorHigh()
    {
        internalMapLayer.setColorHigh( Color.YELLOW );
        assertEquals( Color.YELLOW, internalMapLayer.getColorHigh() );
        internalMapLayer.setColorHigh( Color.BLUE );
        assertEquals( Color.BLUE, internalMapLayer.getColorHigh() );
    }

    @Ignore
    @Test
    public void testSetGetColorLow()
    {
        internalMapLayer.setColorLow( Color.YELLOW );
        assertEquals( Color.YELLOW, internalMapLayer.getColorLow() );
        internalMapLayer.setColorLow( Color.BLUE );
        assertEquals( Color.BLUE, internalMapLayer.getColorLow() );
    }

    @Ignore
    @Test
    public void testSetGetOpacity()
    {
        internalMapLayer.setOpacity( 34.8F );
        assertEquals( 34.8F, internalMapLayer.getOpacity(), 0.00001 );
        internalMapLayer.setOpacity( 14.5F );
        assertEquals( 14.5F, internalMapLayer.getOpacity(), 0.00001 );
    }

    @Ignore
    @Test
    public void testGetIntervalSet()
    {
        //TODO
    }
}
