package org.hisp.dhis.mapping;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MappingServiceTest
    extends DhisSpringTest
{
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

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

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
    }

    // -------------------------------------------------------------------------
    // MapLegend
    // -------------------------------------------------------------------------

    @Test
    public void testGetAddOrUpdateMapLegendByName()
    {
        MapLegend legend = createMapLegend( 'A', 0.1, 0.2 );

        mappingService.addOrUpdateMapLegend( legend.getName(), legend.getStartValue(), legend.getEndValue(),
            legend.getColor(), legend.getImage() );

        legend = mappingService.getMapLegendByName( legend.getName() );

        assertNotNull( legend );

        int id = legend.getId();

        mappingService.addOrUpdateMapLegend( legend.getName(), legend.getStartValue(), 0.3, "ColorB", "img.png" );

        assertEquals( "MapLegendA", mappingService.getMapLegend( id ).getName() );
        assertEquals( new Double( 0.1 ), mappingService.getMapLegend( id ).getStartValue() );
        assertEquals( new Double( 0.3 ), mappingService.getMapLegend( id ).getEndValue() );
        assertEquals( "ColorB", mappingService.getMapLegend( id ).getColor() );
        assertEquals( "img.png", mappingService.getMapLegend( id ).getImage() );
    }

    @Test
    public void testDeleteMapLegend()
    {
        MapLegend legend = createMapLegend( 'A', 0.1, 0.2 );

        mappingService.addOrUpdateMapLegend( legend.getName(), legend.getStartValue(), legend.getEndValue(),
            legend.getColor(), legend.getImage() );

        legend = mappingService.getMapLegendByName( legend.getName() );

        assertNotNull( legend );

        int id = legend.getId();

        mappingService.deleteMapLegend( legend );

        assertNull( mappingService.getMapLegend( id ) );
    }

    @Test
    public void testGetAllMapLegends()
    {
        MapLegend legend1 = createMapLegend( 'A', 0.1, 0.2 );
        MapLegend legend2 = createMapLegend( 'B', 0.3, 0.4 );
        MapLegend legend3 = createMapLegend( 'C', 0.5, 0.6 );

        mappingService.addOrUpdateMapLegend( legend1.getName(), legend1.getStartValue(), legend1.getEndValue(),
            legend1.getColor(), legend1.getImage() );
        mappingService.addOrUpdateMapLegend( legend3.getName(), legend3.getStartValue(), legend3.getEndValue(),
            legend3.getColor(), legend3.getImage() );

        legend1 = mappingService.getMapLegendByName( legend1.getName() );
        legend3 = mappingService.getMapLegendByName( legend3.getName() );

        assertNotNull( legend1 );
        assertNotNull( legend3 );

        int idA = legend1.getId();
        int idC = legend3.getId();

        assertEquals( legend1, mappingService.getMapLegend( idA ) );
        assertEquals( legend3, mappingService.getMapLegend( idC ) );
        assertTrue( !mappingService.getAllMapLegends().contains( legend2 ) );
    }

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetMapLegendSet()
    {
        MapLegendSet legendSet = createMapLegendSet( 'B' );

        int id = mappingService.addMapLegendSet( legendSet );

        assertNotNull( mappingService.getMapLegendSet( id ) );
    }

    @Test
    public void testGetUpdateMapLegendSetByName()
    {
        MapLegendSet legendSet = createMapLegendSet( 'C' );

        int id = mappingService.addMapLegendSet( legendSet );

        legendSet = mappingService.getMapLegendSet( id );

        assertNotNull( legendSet );

        legendSet.setName( "MapLegendSetB" );

        mappingService.updateMapLegendSet( legendSet );

        assertEquals( "MapLegendSetB", mappingService.getMapLegendSetByName( "MapLegendSetB" ).getName() );
    }

    @Test
    public void testGetAllMapLegendSets()
    {
        MapLegendSet legendSet1 = createMapLegendSet( 'B' );
        MapLegendSet legendSet2 = createMapLegendSet( 'C' );
        MapLegendSet legendSet3 = createMapLegendSet( 'D' );

        Collection<MapLegendSet> mapLegendSets = new HashSet<MapLegendSet>();

        mapLegendSets.add( mapLegendSet );
        mapLegendSets.add( legendSet1 );
        mapLegendSets.add( legendSet2 );
        mapLegendSets.add( legendSet3 );

        mappingService.addMapLegendSet( legendSet1 );
        mappingService.addMapLegendSet( legendSet2 );
        mappingService.addMapLegendSet( legendSet3 );

        assertTrue( mappingService.getAllMapLegendSets().containsAll( mapLegendSets ) );
    }

    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetMapLayer()
    {
        MapLayer mapLayer = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "A", 0.1,
            "B", 1 );

        int id = mappingService.addMapLayer( mapLayer );

        assertEquals( "MapLayerA", mappingService.getMapLayer( id ).getName() );
        assertEquals( MappingService.MAP_LAYER_TYPE_BASELAYER, mappingService.getMapLayer( id ).getType() );
        assertEquals( "A", mappingService.getMapLayer( id ).getFillColor() );
        assertEquals( "B", mappingService.getMapLayer( id ).getStrokeColor() );
        assertEquals( 0.1, mappingService.getMapLayer( id ).getFillOpacity(), DELTA );
        assertEquals( 1, mappingService.getMapLayer( id ).getStrokeWidth() );
    }

    @Test
    public void testGetUpdateDeleteMapLayerByName()
    {
        MapLayer mapLayer = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "A", 0.1,
            "B", 1 );

        int id = mappingService.addMapLayer( mapLayer );

        mapLayer = mappingService.getMapLayer( id );

        mapLayer.setName( "MapLayerB" );
        mapLayer.setFillOpacity( 0.05 );
        mapLayer.setStrokeWidth( 0 );

        mappingService.updateMapLayer( mapLayer );

        assertEquals( "MapLayerB", mappingService.getMapLayerByName( "MapLayerB" ).getName() );
        assertEquals( 0.05, mappingService.getMapLayerByName( "MapLayerB" ).getFillOpacity(), DELTA );
        assertEquals( 0, mappingService.getMapLayerByName( "MapLayerB" ).getStrokeWidth() );
    }

    @Test
    public void testGetAllMapLayers()
    {
        MapLayer mapLayer1 = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "A", 0.1,
            "B", 1 );
        MapLayer mapLayer2 = new MapLayer( "MapLayerB", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "C", 0.2,
            "D", 2 );
        MapLayer mapLayer3 = new MapLayer( "MapLayerC", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "E", 0.3,
            "F", 3 );
        MapLayer mapLayer4 = new MapLayer( "MapLayerD", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "G", 0.4,
            "H", 4 );

        int idA = mappingService.addMapLayer( mapLayer1 );
        int idB = mappingService.addMapLayer( mapLayer2 );
        int idC = mappingService.addMapLayer( mapLayer3 );

        assertEquals( mapLayer1, mappingService.getMapLayer( idA ) );
        assertEquals( mapLayer2, mappingService.getMapLayer( idB ) );
        assertEquals( mapLayer3, mappingService.getMapLayer( idC ) );
        assertTrue( !mappingService.getAllMapLayers().contains( mapLayer4 ) );

    }

    @Test
    public void testGetMapLayersByTypeOrMapSource()
    {
        List<MapLayer> baseLayers = new ArrayList<MapLayer>();
        List<MapLayer> overlayLayers = new ArrayList<MapLayer>();

        MapLayer mapLayer1 = new MapLayer( "MapLayerA", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "A", 0.1,
            "B", 1 );
        MapLayer mapLayer2 = new MapLayer( "MapLayerB", MappingService.MAP_LAYER_TYPE_OVERLAY, "", "", "", "C", 0.2,
            "D", 2 );
        MapLayer mapLayer3 = new MapLayer( "MapLayerC", MappingService.MAP_LAYER_TYPE_OVERLAY, "", "", "", "E", 0.3,
            "F", 3 );
        MapLayer mapLayer4 = new MapLayer( "MapLayerD", MappingService.MAP_LAYER_TYPE_BASELAYER, "", "", "", "G", 0.4,
            "H", 4 );

        baseLayers.add( mapLayer1 );
        baseLayers.add( mapLayer4 );

        overlayLayers.add( mapLayer2 );
        overlayLayers.add( mapLayer3 );

        mappingService.addMapLayer( mapLayer1 );
        mappingService.addMapLayer( mapLayer2 );
        mappingService.addMapLayer( mapLayer3 );
        mappingService.addMapLayer( mapLayer4 );

        assertEquals( baseLayers, mappingService.getMapLayersByType( MappingService.MAP_LAYER_TYPE_BASELAYER ) );
        assertEquals( overlayLayers, mappingService.getMapLayersByType( MappingService.MAP_LAYER_TYPE_OVERLAY ) );
    }
}
