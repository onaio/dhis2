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

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public interface MappingService
{
    final String ID = MappingService.class.getName();

    final String GEOJSON_DIR = "geojson";

    final String MAP_LEGEND_SYMBOLIZER_COLOR = "color";
    final String MAP_LEGEND_SYMBOLIZER_IMAGE = "image";

    final String KEY_MAP_DATE_TYPE = "dateType";

    final String MAP_DATE_TYPE_FIXED = "fixed";
    final String MAP_DATE_TYPE_START_END = "start-end";

    final String ORGANISATION_UNIT_SELECTION_TYPE_PARENT = "parent";
    final String ORGANISATION_UNIT_SELECTION_TYPE_LEVEL = "level";

    final String MAP_LAYER_TYPE_BASELAYER = "baselayer";
    final String MAP_LAYER_TYPE_OVERLAY = "overlay";
    
    // -------------------------------------------------------------------------
    // MapLegend
    // -------------------------------------------------------------------------

    void addOrUpdateMapLegend( String name, Double startValue, Double endValue, String color, String image );

    int addMapLegend( MapLegend mapLegend );
    
    void deleteMapLegend( MapLegend legend );

    MapLegend getMapLegend( int id );

    MapLegend getMapLegend( String uid );

    MapLegend getMapLegendByName( String name );

    Collection<MapLegend> getAllMapLegends();

    // -------------------------------------------------------------------------
    // MapLegendSet
    // -------------------------------------------------------------------------

    int addMapLegendSet( MapLegendSet legendSet );

    void updateMapLegendSet( MapLegendSet legendSet );

    void addOrUpdateMapLegendSet( String name, String type, String symbolizer, Set<MapLegend> mapLegends );

    void deleteMapLegendSet( MapLegendSet legendSet );

    MapLegendSet getMapLegendSet( int id );

    MapLegendSet getMapLegendSet( String uid );

    MapLegendSet getMapLegendSetByName( String name );

    Collection<MapLegendSet> getAllMapLegendSets();

    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    int addMap( Map map );
    
    void updateMap( Map map );
    
    Map getMap( int id );
    
    Map getMap( String uid );
    
    Map getMapNoAcl( String uid );
    
    void deleteMap( Map map );
        
    List<Map> getMapsBetweenLikeName( String name, int first, int max );
    
    List<Map> getAllMaps();
    
    // -------------------------------------------------------------------------
    // MapView
    // -------------------------------------------------------------------------

    int addMapView( MapView mapView );

    void updateMapView( MapView mapView );

    void deleteMapView( MapView view );

    MapView getMapView( int id );

    MapView getMapView( String uid );

    MapView getMapViewByName( String name );

    MapView getIndicatorLastYearMapView( String indicatorUid, String organisationUnitUid, int level );

    Collection<MapView> getAllMapViews();
    
    Collection<MapView> getMapViewsBetweenByName( String name, int first, int max );

    // -------------------------------------------------------------------------
    // MapLayer
    // -------------------------------------------------------------------------

    int addMapLayer( MapLayer mapLayer );

    void updateMapLayer( MapLayer mapLayer );

    void addOrUpdateMapLayer( String name, String type, String url, String layers, String time, String fillColor,
                              double fillOpacity, String strokeColor, int strokeWidth );

    void deleteMapLayer( MapLayer mapLayer );

    MapLayer getMapLayer( int id );

    MapLayer getMapLayer( String uid );

    MapLayer getMapLayerByName( String name );

    Collection<MapLayer> getMapLayersByType( String type );

    MapLayer getMapLayerByMapSource( String mapSource );

    Collection<MapLayer> getAllMapLayers();
}