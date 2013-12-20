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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.user.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement( localName = "map", namespace = DxfNamespaces.DXF_2_0 )
public class Map
    extends BaseIdentifiableObject
{
    private Double longitude;

    private Double latitude;

    private Integer zoom;

    @Scanned
    private List<MapView> mapViews = new ArrayList<MapView>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Map()
    {
    }
    
    public Map( String name, User user, Double longitude, Double latitude, Integer zoom )
    {
        this.name = name;
        this.user = user;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zoom = zoom;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude( Double longitude )
    {
        this.longitude = longitude;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude( Double latitude )
    {
        this.latitude = latitude;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getZoom()
    {
        return zoom;
    }

    public void setZoom( Integer zoom )
    {
        this.zoom = zoom;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlElementWrapper( localName = "mapViews", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "mapView", namespace = DxfNamespaces.DXF_2_0 )
    public List<MapView> getMapViews()
    {
        return mapViews;
    }

    public void setMapViews( List<MapView> mapViews )
    {
        this.mapViews = mapViews;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );
        
        if ( other.getClass().isInstance( this ) )
        {
            Map map = (Map) other;
            
            user = map.getUser() == null ? user : map.getUser();
            longitude = map.getLongitude() == null ? longitude : map.getLongitude();
            latitude = map.getLatitude() == null ? latitude : map.getLatitude();
            zoom = map.getZoom() == null ? zoom : map.getZoom();
            
            mapViews.clear();
            mapViews.addAll( map.getMapViews() );
        }            
    }
}
