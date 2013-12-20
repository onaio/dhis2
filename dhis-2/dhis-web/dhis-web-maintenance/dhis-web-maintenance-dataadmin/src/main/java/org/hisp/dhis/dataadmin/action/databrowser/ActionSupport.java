package org.hisp.dhis.dataadmin.action.databrowser;

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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.databrowser.DataBrowserGridService;
import org.hisp.dhis.databrowser.MetaValue;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public abstract class ActionSupport
    extends ActionPagingSupport<Grid>
{
    protected static final String KEY_DATABROWSERGRID = "dataBrowserGridResults";

    protected static final String TRUE = "on";

    protected static final String EMPTY = "";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    protected DataBrowserGridService dataBrowserGridService;

    public void setDataBrowserGridService( DataBrowserGridService dataBrowserGridService )
    {
        this.dataBrowserGridService = dataBrowserGridService;
    }

    protected DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    protected DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    protected OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    protected PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    protected SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // I18n & I18nFormat
    // -------------------------------------------------------------------------

    protected I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    protected I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    protected Grid grid;

    protected String mode;

    protected String toDate;

    protected String fromDate;

    protected String fromToDate;

    protected String periodTypeId;

    protected String parent;

    protected String tmpParent;

    protected String orgunitid;

    protected String selectedUnitChanger;

    protected String dataElementName;

    protected String drillDownCheckBox;

    protected String showZeroCheckBox;

    protected OrganisationUnit selectedUnit;

    protected boolean isSummary;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    public void setToDate( String toDate )
    {
        this.toDate = toDate;
    }

    public void setFromDate( String fromDate )
    {
        this.fromDate = fromDate;
    }

    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }

    public void setParent( String parent )
    {
        this.parent = parent;
    }

    public void setSelectedUnitChanger( String selectedUnitChanger )
    {
        this.selectedUnitChanger = selectedUnitChanger.trim();
    }

    public void setOrgunitid( String orgunitid )
    {
        this.orgunitid = orgunitid;
    }

    public void setDrillDownCheckBox( String drillDownCheckBox )
    {
        this.drillDownCheckBox = drillDownCheckBox;
    }

    public void setShowZeroCheckBox( String showZeroCheckBox )
    {
        this.showZeroCheckBox = showZeroCheckBox;
    }

    public void setSummary( boolean isSummary )
    {
        this.isSummary = isSummary;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public boolean isSummary()
    {
        return isSummary;
    }

    public Grid getGrid()
    {
        return grid;
    }

    public Collection<DataElementGroup> getDataElementGroups()
    {
        return dataElementService.getAllDataElementGroups();
    }

    public String getMode()
    {
        return mode;
    }

    public String getToDate()
    {
        return toDate;
    }

    public String getFromDate()
    {
        return fromDate;
    }

    public String getFromToDate()
    {
        return fromToDate;
    }

    public String getPeriodTypeId()
    {
        return periodTypeId;
    }

    public String getParent()
    {
        return parent;
    }

    public String getOrgunitid()
    {
        return orgunitid;
    }

    public String getTmpParent()
    {
        return tmpParent;
    }

    public String getDataElementName()
    {
        return dataElementName;
    }

    public String getShowZeroCheckBox()
    {
        return showZeroCheckBox;
    }

    public String getParentName()
    {
        if ( mode.equals( "OU" ) )
        {
            return selectedUnit.getName();
        }

        if ( parent == null )
        {
            return EMPTY;
        }

        if ( mode.equals( "DS" ) )
        {
            return dataSetService.getDataSet( Integer.parseInt( parent ) ).getName();
        }

        if ( mode.equals( "OUG" ) )
        {
            return organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( parent ) ).getName();
        }

        if ( mode.equals( "DEG" ) )
        {
            return dataElementService.getDataElementGroup( Integer.parseInt( parent ) ).getName();
        }

        return EMPTY;
    }

    public String getCurrentParentsParent()
    {
        try
        {
            return selectedUnit.getParent().getName();
        }
        catch ( Exception e )
        {
            return EMPTY;
        }
    }

    public List<OrganisationUnit> getCurrentChildren()
    {
        Set<OrganisationUnit> tmp = selectedUnit.getChildren();
        List<OrganisationUnit> list = new ArrayList<OrganisationUnit>();

        for ( OrganisationUnit o : tmp )
        {
            if ( o.getChildren().size() > 0 )
            {
                list.add( o );
            }
        }
        Collections.sort( list, IdentifiableObjectNameComparator.INSTANCE );

        return list;
    }

    public List<OrganisationUnit> getBreadCrumbOrgUnit()
    {
        List<OrganisationUnit> myList = new ArrayList<OrganisationUnit>();

        boolean loop = true;
        OrganisationUnit currentOrgUnit = selectedUnit;

        while ( loop )
        {
            myList.add( currentOrgUnit );

            if ( currentOrgUnit.getParent() == null )
            {
                loop = false;
            }
            else
            {
                currentOrgUnit = currentOrgUnit.getParent();
            }
        }
        Collections.reverse( myList );

        return myList;
    }

    public List<Object> getMetaValues()
    {
        return grid.getColumn( 0 );
    }

    public Map<Integer, List<Object>> getMetaValueMaps()
    {
        Map<Integer, List<Object>> maps = new Hashtable<Integer, List<Object>>();

        for ( List<Object> row : grid.getRows() )
        {
            if ( !row.isEmpty() && row.size() > 1 )
            {
                maps.put( ((MetaValue) row.get( 0 )).getId(), row.subList( 1, row.size() ) );
            }
        }

        return maps;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * This is a helper method for populating a list of converted column names
     * 
     * @param DataBrowserTable
     */
    protected void convertColumnNames( Grid grid )
    {
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );

        for ( GridHeader col : grid.getVisibleHeaders() )
        {
            col.setName( dataBrowserGridService.convertDate( periodType, col.getName(), i18n, format ) );
        }
    }

    protected void setGridTitle()
    {
        grid.setTitle( i18n.getString( mappingMode( mode ) )
            + (mode.equals( "OU" ) == true ? " - " + getParentName() : EMPTY) );
        grid.setSubtitle( i18n.getString( "from_date" ) + ": " + fromDate + " " + i18n.getString( "to_date" ) + ": "
            + toDate + ", " + i18n.getString( "period_type" ) + ": " + i18n.getString( periodTypeId ) );
    }

    protected void doPaging()
    {
        this.paging = this.createPaging( grid.getHeight() );

        grid.limitGrid( paging.getStartPos(), paging.getEndPos() );
    }

    private String mappingMode( String mode )
    {
        if ( mode.equals( "DS" ) )
        {
            return "data_sets";
        }
        else if ( mode.equals( "DEG" ) )
        {
            return "data_element_groups";
        }
        else if ( mode.equals( "OU" ) )
        {
            return "organisation_units";
        }
        else if ( mode.equals( "OUG" ) )
        {
            return "organisation_unit_groups";
        }

        return mode;
    }
}
