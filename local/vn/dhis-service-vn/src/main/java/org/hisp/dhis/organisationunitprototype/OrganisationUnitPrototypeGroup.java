package org.hisp.dhis.organisationunitprototype;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
@JacksonXmlRootElement( localName = "orgUnitPrototypeGroup", namespace = DxfNamespaces.DXF_2_0 )
public class OrganisationUnitPrototypeGroup
    extends BaseIdentifiableObject
{
    @Scanned
    private Set<OrganisationUnitPrototype> members = new HashSet<OrganisationUnitPrototype>();

    public OrganisationUnitPrototypeGroup()
    {
    }

    public OrganisationUnitPrototypeGroup( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnitPrototype( OrganisationUnitPrototype orgUnitPrototype )
    {
        members.add( orgUnitPrototype );
        orgUnitPrototype.getGroups().add( this );
    }

    public void removeOrganisationUnitPrototype( OrganisationUnitPrototype orgUnitPrototype )
    {
        members.remove( orgUnitPrototype );
        orgUnitPrototype.getGroups().remove( this );
    }

    public void removeAllOrganisationUnitPrototypes()
    {
        for ( OrganisationUnitPrototype orgUnitPrototype : members )
        {
            orgUnitPrototype.getGroups().remove( this );
        }

        members.clear();
    }

    public void updateOrganisationUnitPrototypes( Set<OrganisationUnitPrototype> updates )
    {
        for ( OrganisationUnitPrototype unitPrototype : new HashSet<OrganisationUnitPrototype>( members ) )
        {
            if ( !updates.contains( unitPrototype ) )
            {
                removeOrganisationUnitPrototype( unitPrototype );
            }
        }

        for ( OrganisationUnitPrototype unitPrototype : updates )
        {
            addOrganisationUnitPrototype( unitPrototype );
        }
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof OrganisationUnitPrototypeGroup) )
        {
            return false;
        }

        final OrganisationUnitPrototypeGroup other = (OrganisationUnitPrototypeGroup) o;

        return name.equals( other.getName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty( value = "orgUnitPrototypes" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "orgUnitPrototypes", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "orgUnitPrototype", namespace = DxfNamespaces.DXF_2_0 )
    public Set<OrganisationUnitPrototype> getMembers()
    {
        return members;
    }

    public void setMembers( Set<OrganisationUnitPrototype> members )
    {
        this.members = members;
    }
}
