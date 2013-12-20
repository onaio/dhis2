package org.hisp.dhis.organisationunit;

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

import java.util.Iterator;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.system.deletion.DeletionHandler;
import org.hisp.dhis.user.User;

/**
 * @author Lars Helge Overland
 */
public class OrganisationUnitDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return OrganisationUnit.class.getSimpleName();
    }

    @Override
    public void deleteDataSet( DataSet dataSet )
    {
        Iterator<OrganisationUnit> iterator = dataSet.getSources().iterator();
        
        while ( iterator.hasNext() )
        {
            OrganisationUnit unit = iterator.next();
            
            unit.getDataSets().remove( unit );
            
            organisationUnitService.updateOrganisationUnit( unit );
        }        
    }

    @Override
    public void deleteUser( User user )
    {
        Iterator<OrganisationUnit> iterator = user.getOrganisationUnits().iterator();
        
        while ( iterator.hasNext() )
        {
            OrganisationUnit unit = iterator.next();
            
            unit.getUsers().remove( user );
            
            organisationUnitService.updateOrganisationUnit( unit );
        }
    }

    @Override
    public void deleteOrganisationUnitGroup( OrganisationUnitGroup group )
    {
        Iterator<OrganisationUnit> iterator = group.getMembers().iterator();
        
        while ( iterator.hasNext() )
        {
            OrganisationUnit unit = iterator.next();
            
            unit.getGroups().remove( unit );
            
            organisationUnitService.updateOrganisationUnit( unit );
        }            
    }
}
