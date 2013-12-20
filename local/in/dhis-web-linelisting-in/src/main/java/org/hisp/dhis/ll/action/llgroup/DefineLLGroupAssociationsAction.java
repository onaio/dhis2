package org.hisp.dhis.ll.action.llgroup;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.util.HashSet;

import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DefineDataSetAssociationsAction.java 3648 2007-10-15 22:47:45Z larshelg $
 */
public class DefineLLGroupAssociationsAction
    implements Action
{
   

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }   
 
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    private int id;
    
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

 

    public String execute()
        throws Exception
    {
    	
        LineListGroup lineListGroup = lineListService.getLineListGroup( id );
        
        lineListGroup.updateOrganisationUnits( new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() ) );
        
        lineListService.updateLineListGroup( lineListGroup );
        
       
        /*
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits(); 
        
        Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();        
        
        getUnitsInTheTree( rootUnits, unitsInTheTree );          
	
        LineListGroup lineListGroup = lineListService.getLineListGroup( id );    	
    	
    	//Set<Source> assignedSources = lineListGroup.getSources();
    	Set<OrganisationUnit> assignedSources = lineListGroup.getSources();
    	
    	//assignedSources.removeAll( convert( unitsInTheTree ) );        
    	
    	assignedSources.removeAll(  unitsInTheTree );
    	Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
    	
    	//assignedSources.addAll( convert( selectedOrganisationUnits ) );
    	assignedSources.addAll( selectedOrganisationUnits  );
    	
    	lineListGroup.setSources( assignedSources );
    	
        lineListService.updateLineListGroup( lineListGroup );
        */
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
/*
    private Set<Source> convert( Collection<OrganisationUnit> organisationUnits )
    {
        Set<Source> sources = new HashSet<Source>();
        
        sources.addAll( organisationUnits );
        
        return sources;
    }   
*/    
    /*
    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
    	for( OrganisationUnit root : rootUnits )
        {
    		unitsInTheTree.add( root );
    		getUnitsInTheTree( root.getChildren(), unitsInTheTree );    		
        }
    }
    */
}
