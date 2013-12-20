package org.hisp.dhis.visualizer.action;

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
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;

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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DimensionService dimensionService;

    public void setDimensionService( DimensionService dimensionService )
    {
        this.dimensionService = dimensionService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
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

    private List<Period> lastMonth;

    public List<Period> getLastMonth()
    {
        return lastMonth;
    }

    private List<Period> last12Months;

    public List<Period> getLast12Months()
    {
        return last12Months;
    }
    
    private List<Period> last3Months;

    public List<Period> getLast3Months()
    {
        return last3Months;
    }

    private List<Period> lastQuarter;

    public List<Period> getLastQuarter()
    {
        return lastQuarter;
    }

    private List<Period> last4Quarters;

    public List<Period> getLast4Quarters()
    {
        return last4Quarters;
    }

    private List<Period> lastSixMonth;

    public List<Period> getLastSixMonth()
    {
        return lastSixMonth;
    }

    private List<Period> last2SixMonths;

    public List<Period> getLast2SixMonths()
    {
        return last2SixMonths;
    }

    private List<Period> lastYear;

    public List<Period> getLastYear()
    {
        return lastYear;
    }

    private List<Period> thisYear;

    public List<Period> getThisYear()
    {
        return thisYear;
    }

    private List<Period> last5Years;

    public List<Period> getLast5Years()
    {
        return last5Years;
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

        RelativePeriods rp = new RelativePeriods();

        rp.clear().setReportingMonth( true );
        lastMonth = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLast12Months( true );
        last12Months = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLast3Months( true );
        last3Months = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setReportingQuarter( true );
        lastQuarter = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLast4Quarters( true );
        last4Quarters = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLastSixMonth( true );
        lastSixMonth = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLast2SixMonths( true );
        last2SixMonths = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLastYear( true );
        lastYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setThisYear( true );
        thisYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        rp.clear().setLast5Years( true );
        last5Years = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        dimensions = dimensionService.getAllDimensions();
        
        levels = organisationUnitService.getOrganisationUnitLevels();

        return SUCCESS;
    }

    private List<Period> setNames( List<Period> periods )
    {
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }

        return periods;
    }
}
