package org.hisp.dhis.pivot.action;

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

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public class InitializeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DimensionService dimensionService;

    public void setDimensionService( DimensionService dimensionService )
    {
        this.dimensionService = dimensionService;
    }
    
    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private String callback;
    
    public void setCallback( String callback )
    {
        this.callback = callback;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    public String getCallback()
    {
        return callback;
    }

    private String contextPath;

    public String getContextPath()
    {
        return contextPath;
    }

    private Collection<OrganisationUnit> rootNodes;

    public Collection<OrganisationUnit> getRootNodes()
    {
        return rootNodes;
    }
    
    private Collection<DimensionalObject> dimensions;

    public Collection<DimensionalObject> getDimensions()
    {
        return dimensions;
    }

    private Collection<MapLegendSet> legendSets;

    public Collection<MapLegendSet> getLegendSets()
    {
        return legendSets;
    }
    
    private Collection<OrganisationUnitLevel> levels;

    public Collection<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        contextPath = ContextUtils.getContextPath( ServletActionContext.getRequest() );
        
        rootNodes = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsAtLevel( 1 ) );

        if ( rootNodes.size() < 1 )
        {
            rootNodes.add( new OrganisationUnit() );
        }
        
        dimensions = dimensionService.getAllDimensions();
        
        legendSets = mappingService.getAllMapLegendSets();
        
        levels = organisationUnitService.getOrganisationUnitLevels();

        return SUCCESS;
    }
}
