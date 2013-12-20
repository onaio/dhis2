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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataset.DataSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Kristian Nordal
 */
@JacksonXmlRootElement( localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0 )
public class OrganisationUnitGroup
    extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -1131637847640209166L;

    private String symbol;

    @Scanned
    private Set<OrganisationUnit> members = new HashSet<OrganisationUnit>();

    @Scanned
    private Set<DataSet> dataSets = new HashSet<DataSet>();

    private OrganisationUnitGroupSet groupSet;

    /**
     * Set of the dynamic attributes values that belong to this organisationUnit
     * group.
     */
    private Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OrganisationUnitGroup()
    {
    }

    public OrganisationUnitGroup( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnit( OrganisationUnit organisationUnit )
    {
        members.add( organisationUnit );
        organisationUnit.getGroups().add( this );
    }

    public void removeOrganisationUnit( OrganisationUnit organisationUnit )
    {
        members.remove( organisationUnit );
        organisationUnit.getGroups().remove( this );
    }

    public void removeAllOrganisationUnits()
    {
        for ( OrganisationUnit organisationUnit : members )
        {
            organisationUnit.getGroups().remove( this );
        }

        members.clear();
    }

    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( members ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }

        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
    }

    public void addDataSet( DataSet dataSet )
    {
        dataSets.add( dataSet );
        dataSet.getOrganisationUnitGroups().add( this );
    }

    public void removeDataSet( DataSet dataSet )
    {
        dataSets.remove( dataSet );
        dataSet.getOrganisationUnitGroups().remove( this );
    }
    
    public void removeAllDataSets()
    {
        for ( DataSet ds : dataSets )
        {
            ds.getOrganisationUnitGroups().remove( this );
        }
        
        dataSets.clear();
    }

    public void updateDataSets( Set<DataSet> updates )
    {
        for ( DataSet ds : new HashSet<DataSet>( dataSets ) )
        {
            if ( !updates.contains( ds ) )
            {
                removeDataSet( ds );
            }
        }

        for ( DataSet ds : updates )
        {
            addDataSet( ds );
        }
    }
    
    public boolean hasSymbol()
    {
        return symbol != null && !symbol.trim().isEmpty();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol( String symbol )
    {
        this.symbol = symbol;
    }

    @JsonProperty( value = "organisationUnits" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )
    public Set<OrganisationUnit> getMembers()
    {
        return members;
    }

    public void setMembers( Set<OrganisationUnit> members )
    {
        this.members = members;
    }

    @JsonProperty( value = "organisationUnitGroupSet" )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public OrganisationUnitGroupSet getGroupSet()
    {
        return groupSet;
    }

    public void setGroupSet( OrganisationUnitGroupSet groupSet )
    {
        this.groupSet = groupSet;
    }

    @JsonProperty( value = "attributes" )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "attributes", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "attribute", namespace = DxfNamespaces.DXF_2_0 )
    public Set<AttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<AttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    @JsonProperty( value = "dataSets" )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "dataSets", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "dataSet", namespace = DxfNamespaces.DXF_2_0 )
    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            OrganisationUnitGroup organisationUnitGroup = (OrganisationUnitGroup) other;

            groupSet = null;

            removeAllOrganisationUnits();

            for ( OrganisationUnit organisationUnit : organisationUnitGroup.getMembers() )
            {
                addOrganisationUnit( organisationUnit );
            }

            attributeValues.clear();
            attributeValues.addAll( organisationUnitGroup.getAttributeValues() );
        }
    }
}
