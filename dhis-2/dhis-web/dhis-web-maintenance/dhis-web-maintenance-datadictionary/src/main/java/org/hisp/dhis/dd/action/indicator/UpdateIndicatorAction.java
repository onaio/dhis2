package org.hisp.dhis.dd.action.indicator;

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

import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.system.util.AttributeUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 */
public class UpdateIndicatorAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private boolean annualized;

    public void setAnnualized( boolean annualized )
    {
        this.annualized = annualized;
    }

    private Integer indicatorTypeId;

    public void setIndicatorTypeId( Integer indicatorTypeId )
    {
        this.indicatorTypeId = indicatorTypeId;
    }

    private Integer selectedLegendSetId;

    public void setSelectedLegendSetId( Integer selectedLegendSetId )
    {
        this.selectedLegendSetId = selectedLegendSetId;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private String numerator;

    public void setNumerator( String numerator )
    {
        this.numerator = numerator;
    }

    private String numeratorDescription;

    public void setNumeratorDescription( String numeratorDescription )
    {
        this.numeratorDescription = numeratorDescription;
    }

    private String denominator;

    public void setDenominator( String denominator )
    {
        this.denominator = denominator;
    }

    private String denominatorDescription;

    public void setDenominatorDescription( String denominatorDescription )
    {
        this.denominatorDescription = denominatorDescription;
    }

    private List<String> indicatorGroupSets = new ArrayList<String>();

    public void setIndicatorGroupSets( List<String> indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    private List<String> indicatorGroups = new ArrayList<String>();

    public void setIndicatorGroups( List<String> indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    private List<String> jsonAttributeValues;

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Indicator indicator = indicatorService.getIndicator( id );

        IndicatorType indicatorType = indicatorService.getIndicatorType( indicatorTypeId );

        MapLegendSet legendSet = mappingService.getMapLegendSet( selectedLegendSetId );
        
        if ( code != null && code.trim().length() == 0 )
        {
            code = null;
        }

        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        indicator.setName( name );
        indicator.setShortName( shortName );
        indicator.setCode( code );
        indicator.setDescription( description );
        indicator.setAnnualized( annualized );
        indicator.setIndicatorType( indicatorType );
        indicator.setLegendSet( legendSet );
        indicator.setUrl( url );
        indicator.setNumerator( numerator );
        indicator.setNumeratorDescription( numeratorDescription );
        indicator.setDenominator( denominator );
        indicator.setDenominatorDescription( denominatorDescription );

        for ( int i = 0; i < indicatorGroupSets.size(); i++ )
        {
            IndicatorGroupSet groupSet = indicatorService.getIndicatorGroupSet( Integer.parseInt( indicatorGroupSets
                .get( i ) ) );

            IndicatorGroup oldGroup = groupSet.getGroup( indicator );
            IndicatorGroup newGroup = indicatorService.getIndicatorGroup( Integer.parseInt( indicatorGroups.get( i ) ) );

            if ( oldGroup != null && oldGroup.getMembers().remove( indicator ) )
            {
                oldGroup.removeIndicator( indicator );
                indicatorService.updateIndicatorGroup( oldGroup );
            }

            if ( newGroup != null && newGroup.getMembers().add( indicator ) )
            {
                newGroup.addIndicator( indicator );
                indicatorService.updateIndicatorGroup( newGroup );
            }
        }

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( indicator.getAttributeValues(), jsonAttributeValues,
                attributeService );
        }

        indicatorService.updateIndicator( indicator );

        return SUCCESS;
    }
}
