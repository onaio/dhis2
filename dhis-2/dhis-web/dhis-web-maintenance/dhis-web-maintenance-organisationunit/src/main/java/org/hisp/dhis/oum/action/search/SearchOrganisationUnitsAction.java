package org.hisp.dhis.oum.action.search;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.system.grid.ListGrid;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class SearchOrganisationUnitsAction
    implements Action
{
    private static final Log log = LogFactory.getLog( SearchOrganisationUnitsAction.class );
    
    private static final int ANY = 0;
    private static final String DEFAULT_TYPE = "html";
    
    // -------------------------------------------------------------------------
    // Depdencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input and output
    // -------------------------------------------------------------------------

    private boolean skipSearch;
    
    public void setSkipSearch( boolean skipSearch )
    {
        this.skipSearch = skipSearch;
    }

    private String name;
    
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    private Collection<Integer> groupId = new HashSet<Integer>();

    public Collection<Integer> getGroupId()
    {
        return groupId;
    }

    public void setGroupId( Collection<Integer> groupId )
    {
        this.groupId = groupId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitGroupSet> groupSets;

    public List<OrganisationUnitGroupSet> getGroupSets()
    {
        return groupSets;
    }
    
    private List<OrganisationUnit> organisationUnits;

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }
    
    private OrganisationUnit selectedOrganisationUnit;

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        return selectedOrganisationUnit;
    }
    
    boolean limited = false;

    public boolean isLimited()
    {
        return limited;
    }
    
    private String type;
    
    public void setType( String type )
    {
        this.type = type;
    }

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        type = StringUtils.trimToNull( type );
        
        // ---------------------------------------------------------------------
        // Get group sets
        // ---------------------------------------------------------------------

        groupSets = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getCompulsoryOrganisationUnitGroupSets() );
        
        Collections.sort( groupSets, IdentifiableObjectNameComparator.INSTANCE );
        
        // ---------------------------------------------------------------------
        // Assemble groups and get search result
        // ---------------------------------------------------------------------

        if ( !skipSearch )
        {
            name = StringUtils.trimToNull( name );
            
            selectedOrganisationUnit = selectionManager.getSelectedOrganisationUnit();

            log.debug( "Name: " + name + ", Orgunit: " + selectedOrganisationUnit + ", type: " + type );

            // -----------------------------------------------------------------
            // Set orgunit to null if root to avoid subquery and improve perf
            // -----------------------------------------------------------------

            selectedOrganisationUnit = selectedOrganisationUnit != null && selectedOrganisationUnit.getParent() == null ? null : selectedOrganisationUnit;
            
            Collection<OrganisationUnitGroup> groups = new HashSet<OrganisationUnitGroup>();
            
            for ( Integer id : groupId )
            {
                if ( id != ANY )
                {
                    OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( id );
                    groups.add( group );
                }
            }
            
            boolean limit = type == null; // Only limit for HTML view since browser is memory constrained
            
            organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsByNameAndGroups( name, groups, selectedOrganisationUnit, limit ) );
            
            limited = organisationUnits != null && organisationUnits.size() == OrganisationUnitService.MAX_LIMIT;
            
            Collections.sort( organisationUnits, IdentifiableObjectNameComparator.INSTANCE );
            
            if ( type != null && !type.equalsIgnoreCase( DEFAULT_TYPE ) )
            {
                grid = generateGrid();
            }            
        }
        
        return type != null ? type : SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Grid generateGrid()
    {
        final Grid orgUnitGrid = new ListGrid().setTitle( "Organisation unit search result" );

        orgUnitGrid.addHeader( new GridHeader( "Identifier", false, true ) );
        orgUnitGrid.addHeader( new GridHeader( "Code", false, true ) );
        orgUnitGrid.addHeader( new GridHeader( "Name", false, true ) );
        
        for ( OrganisationUnitGroupSet groupSet : groupSets )
        {
            orgUnitGrid.addHeader( new GridHeader( groupSet.getName(), false, true ) );
        }
        
        for ( OrganisationUnit unit : organisationUnits )
        {
            orgUnitGrid.addRow();

            orgUnitGrid.addValue( unit.getUid() );
            orgUnitGrid.addValue( unit.getCode() );
            orgUnitGrid.addValue( unit.getName() );
            
            for ( OrganisationUnitGroupSet groupSet : groupSets )
            {
                orgUnitGrid.addValue( unit.getGroupNameInGroupSet( groupSet ) );
            }
        }
        
        return orgUnitGrid;
    }
}
