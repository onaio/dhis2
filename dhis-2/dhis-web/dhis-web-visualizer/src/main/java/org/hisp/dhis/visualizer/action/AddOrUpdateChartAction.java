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

import static org.hisp.dhis.system.util.DateUtils.setNames;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 */
public class AddOrUpdateChartAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private I18nManager i18nManager;

    public void setI18nManager( I18nManager i18nManager )
    {
        this.i18nManager = i18nManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String uid;

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String series;

    public void setSeries( String series )
    {
        this.series = series;
    }

    private String category;

    public void setCategory( String category )
    {
        this.category = category;
    }

    private List<String> indicatorIds;

    public void setIndicatorIds( List<String> indicatorIds )
    {
        this.indicatorIds = indicatorIds;
    }

    private List<String> dataElementIds;

    public void setDataElementIds( List<String> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private List<String> dataSetIds;

    public void setDataSetIds( List<String> dataSetIds )
    {
        this.dataSetIds = dataSetIds;
    }

    private boolean reportingMonth;

    public void setReportingMonth( boolean reportingMonth )
    {
        this.reportingMonth = reportingMonth;
    }

    private boolean last12Months;

    public void setLast12Months( boolean last12Months )
    {
        this.last12Months = last12Months;
    }

    private boolean last3Months;

    public void setLast3Months( boolean last3Months )
    {
        this.last3Months = last3Months;
    }

    private boolean reportingQuarter;

    public void setReportingQuarter( boolean reportingQuarter )
    {
        this.reportingQuarter = reportingQuarter;
    }

    private boolean last4Quarters;

    public void setLast4Quarters( boolean last4Quarters )
    {
        this.last4Quarters = last4Quarters;
    }

    private boolean lastSixMonth;

    public void setLastSixMonth( boolean lastSixMonth )
    {
        this.lastSixMonth = lastSixMonth;
    }

    private boolean last2SixMonths;

    public void setLast2SixMonths( boolean last2SixMonths )
    {
        this.last2SixMonths = last2SixMonths;
    }

    private boolean thisYear;

    public void setThisYear( boolean thisYear )
    {
        this.thisYear = thisYear;
    }

    private boolean lastYear;

    public void setLastYear( boolean lastYear )
    {
        this.lastYear = lastYear;
    }

    private boolean last5Years;

    public void setLast5Years( boolean last5Years )
    {
        this.last5Years = last5Years;
    }

    private boolean rewind;

    public void setRewind( boolean rewind )
    {
        this.rewind = rewind;
    }

    private List<String> periodIds;

    public void setPeriodIds( List<String> periodIds )
    {
        this.periodIds = periodIds;
    }

    private List<String> organisationUnitIds;

    public void setOrganisationUnitIds( List<String> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }

    private Boolean system;

    public void setSystem( Boolean system )
    {
        this.system = system;
    }

    private Boolean trendLine;

    public void setTrendLine( Boolean trendLine )
    {
        this.trendLine = trendLine;
    }

    private Boolean hideSubtitle;

    public void setHideSubtitle( Boolean hideSubtitle )
    {
        this.hideSubtitle = hideSubtitle;
    }

    private Boolean hideLegend;

    public void setHideLegend( Boolean hideLegend )
    {
        this.hideLegend = hideLegend;
    }

    private Boolean userOrganisationUnit;

    public void setUserOrganisationUnit( Boolean userOrganisationUnit )
    {
        this.userOrganisationUnit = userOrganisationUnit;
    }

    private Boolean userOrganisationUnitChildren;

    public void setUserOrganisationUnitChildren( Boolean userOrganisationUnitChildren )
    {
        this.userOrganisationUnitChildren = userOrganisationUnitChildren;
    }

    private Boolean showData;

    public void setShowData( Boolean showData )
    {
        this.showData = showData;
    }

    private String domainAxisLabel;

    public void setDomainAxisLabel( String domainAxisLabel )
    {
        this.domainAxisLabel = domainAxisLabel;
    }

    private String rangeAxisLabel;

    public void setRangeAxisLabel( String rangeAxisLabel )
    {
        this.rangeAxisLabel = rangeAxisLabel;
    }

    private Double targetLineValue;

    public void setTargetLineValue( Double targetLineValue )
    {
        this.targetLineValue = targetLineValue;
    }

    private String targetLineLabel;

    public void setTargetLineLabel( String targetLineLabel )
    {
        this.targetLineLabel = targetLineLabel;
    }

    private Double baseLineValue;

    public void setBaseLineValue( Double baseLineValue )
    {
        this.baseLineValue = baseLineValue;
    }

    private String baseLineLabel;

    public void setBaseLineLabel( String baseLineLabel )
    {
        this.baseLineLabel = baseLineLabel;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String chartId;

    public String getChartId()
    {
        return chartId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Chart chart;

        if ( uid != null )
        {
            chart = chartService.getChart( uid );
        }
        else
        {
            chart = new Chart();

            chart.setName( name );
        }

        chart.setType( type );
        chart.setSeries( series );
        chart.setCategory( category );
        // chart.setFilter( filter );

        chart.getIndicators().clear();

        if ( indicatorIds != null )
        {
            for ( String id : indicatorIds )
            {
                chart.getIndicators().add( indicatorService.getIndicator( id ) );
            }
        }

        chart.getDataElements().clear();

        if ( dataElementIds != null )
        {
            for ( String id : dataElementIds )
            {
                chart.getDataElements().add( dataElementService.getDataElement( id ) );
            }
        }

        chart.getDataSets().clear();

        if ( dataSetIds != null )
        {
            for ( String id : dataSetIds )
            {
                chart.getDataSets().add( dataSetService.getDataSet( id ) );
            }
        }
        
        RelativePeriods rp = new RelativePeriods();

        if ( reportingMonth || last12Months || last3Months || reportingQuarter || last4Quarters || lastSixMonth || last2SixMonths || thisYear
            || lastYear || last5Years )
        {
            rp.setReportingMonth( reportingMonth );
            rp.setLast12Months( last12Months );
            rp.setLast3Months( last3Months );
            rp.setReportingQuarter( reportingQuarter );
            rp.setLast4Quarters( last4Quarters );
            rp.setLastSixMonth( lastSixMonth );
            rp.setLast2SixMonths( last2SixMonths );
            rp.setThisYear( thisYear );
            rp.setLastYear( lastYear );
            rp.setLast5Years( last5Years );
        }

        chart.setRelatives( rp );

        chart.setRewindRelativePeriods( rewind );

        chart.getPeriods().clear();

        if ( periodIds != null )
        {
            List<Period> periods = new ArrayList<Period>();

            for ( String id : periodIds )
            {
                periods.add( PeriodType.getPeriodFromIsoString( id ) );
            }

            chart.getPeriods().addAll( periodService.reloadPeriods( setNames( periods, i18nManager.getI18nFormat() ) ) );
        }

        chart.getOrganisationUnits().clear();

        if ( organisationUnitIds != null )
        {
            for ( String id : organisationUnitIds )
            {
                chart.getOrganisationUnits().add( organisationUnitService.getOrganisationUnit( id ) );
            }
        }

        chart.setUser( system == null ? currentUserService.getCurrentUser() : null );

        chart.setRegression( trendLine );

        chart.setHideSubtitle( hideSubtitle );

        chart.setHideLegend( hideLegend );

        chart.setUserOrganisationUnit( userOrganisationUnit );

        chart.setUserOrganisationUnitChildren( userOrganisationUnitChildren );

        chart.setShowData( showData );

        chart.setDomainAxisLabel( domainAxisLabel );

        chart.setRangeAxisLabel( rangeAxisLabel );

        chart.setTargetLineValue( targetLineValue );

        chart.setTargetLineLabel( targetLineLabel );

        chart.setBaseLineValue( baseLineValue );

        chart.setBaseLineLabel( baseLineLabel );

        if ( uid == null )
        {
            chartService.addChart( chart );
        }
        else
        {
            chartService.updateChart( chart );
        }

        chartId = chart.getUid();

        return SUCCESS;
    }
}
