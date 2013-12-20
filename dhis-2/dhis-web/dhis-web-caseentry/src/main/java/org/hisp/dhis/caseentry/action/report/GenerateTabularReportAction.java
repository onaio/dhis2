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

package org.hisp.dhis.caseentry.action.report;

import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_DATA_ELEMENT;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_NUMBER_DATA_ELEMENT;
import static org.hisp.dhis.patientreport.PatientTabularReport.VALUE_TYPE_OPTION_SET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.SQLGrammarException;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patientreport.TabularReportColumn;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Chau Thu Tran
 * @version $GenerateTabularReportAction.java Feb 29, 2012 10:15:05 AM$
 */
public class GenerateTabularReportAction
    extends ActionPagingSupport<ProgramStageInstance>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private Boolean anonynousEntryForm;

    public void setAnonynousEntryForm( Boolean anonynousEntryForm )
    {
        this.anonynousEntryForm = anonynousEntryForm;
    }

    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    private Collection<Integer> orgunitIds = new HashSet<Integer>();

    public void setOrgunitIds( Collection<Integer> orgunitIds )
    {
        this.orgunitIds = orgunitIds;
    }

    private Integer programStageId;

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private List<String> values = new ArrayList<String>();

    public List<String> getValues()
    {
        return values;
    }

    private List<String> filterValues = new ArrayList<String>();

    public void setFilterValues( List<String> filterValues )
    {
        this.filterValues = filterValues;
    }

    private boolean orderByOrgunitAsc;

    public void setOrderByOrgunitAsc( boolean orderByOrgunitAsc )
    {
        this.orderByOrgunitAsc = orderByOrgunitAsc;
    }

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
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

	private Integer pageSize;
  
    public void setPageSize( Integer pageSize )
    {
         this.pageSize = pageSize;
    }
 
    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private int totalRecords;

    public int getTotalRecords()
    {
        return totalRecords;
    }

    private Integer total;
	
    public void setTotal( Integer total )
    {
        this.total = total;
    }

    public Integer getTotal()
    {
        return total;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private Boolean useCompletedEvents;

    public void setUseCompletedEvents( Boolean useCompletedEvents )
    {
        this.useCompletedEvents = useCompletedEvents;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String facilityLB; // All, children, current

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> valueTypes = new ArrayList<String>();

    public List<String> getValueTypes()
    {
        return valueTypes;
    }

    private Map<Integer, List<String>> mapSuggestedValues = new HashMap<Integer, List<String>>();

    public Map<Integer, List<String>> getMapSuggestedValues()
    {
        return mapSuggestedValues;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private Boolean displayOrgunitCode;

    public void setDisplayOrgunitCode( Boolean displayOrgunitCode )
    {
        this.displayOrgunitCode = displayOrgunitCode;
    }

    private Boolean useFormNameDataElement;

    public void setUseFormNameDataElement( Boolean useFormNameDataElement )
    {
        this.useFormNameDataElement = useFormNameDataElement;
    }

    private boolean accessPrivateInfo = false;

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( programStageId == null )
        {
            return INPUT;
        }

        // ---------------------------------------------------------------------
        // Get user orgunits
        // ---------------------------------------------------------------------

        if ( userOrganisationUnit || userOrganisationUnitChildren )
        {
            Collection<OrganisationUnit> userOrgunits = currentUserService.getCurrentUser().getOrganisationUnits();
            orgunitIds = new HashSet<Integer>();

            if ( userOrganisationUnit )
            {
                for ( OrganisationUnit userOrgunit : userOrgunits )
                {
                    orgunitIds.add( userOrgunit.getId() );
                }
            }

            if ( userOrganisationUnitChildren )
            {
                for ( OrganisationUnit userOrgunit : userOrgunits )
                {
                    if ( userOrgunit.hasChild() )
                    {
                        for ( OrganisationUnit childOrgunit : userOrgunit.getSortedChildren() )
                        {
                            orgunitIds.add( childOrgunit.getId() );
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Get orgunitIds
        // ---------------------------------------------------------------------

        Set<Integer> organisationUnits = new HashSet<Integer>();

        if ( facilityLB.equals( "selected" ) )
        {
            organisationUnits.addAll( orgunitIds );
        }
        else if ( facilityLB.equals( "childrenOnly" ) )
        {
            for ( Integer orgunitId : orgunitIds )
            {
                OrganisationUnit selectedOrgunit = organisationUnitService.getOrganisationUnit( orgunitId );
                organisationUnits.addAll( organisationUnitService.getOrganisationUnitHierarchy()
                    .getChildren( orgunitId ) );
                organisationUnits.remove( selectedOrgunit );
            }
        }
        else
        {
            for ( Integer orgunitId : orgunitIds )
            {
                organisationUnits.addAll( organisationUnitService.getOrganisationUnitHierarchy()
                    .getChildren( orgunitId ) );
            }
        }

        // ---------------------------------------------------------------------
        // Get program-stage, start-date, end-date
        // ---------------------------------------------------------------------

        if ( level == 0 )
        {
            level = organisationUnitService.getMaxOfOrganisationUnitLevels();
        }

        // ---------------------------------------------------------------------
        // Get program-stage, start-date, end-date
        // ---------------------------------------------------------------------

        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        Date startValue = format.parseDate( startDate );
        Date endValue = format.parseDate( endDate );
        List<TabularReportColumn> columns = getTableColumns();

        // ---------------------------------------------------------------------
        // Generate tabular report
        // ---------------------------------------------------------------------
        try
        {
            if ( type == null ) // Tabular report
            {
                totalRecords = programStageInstanceService.getTabularReportCount( anonynousEntryForm, programStage,
                    columns, organisationUnits, level, useCompletedEvents, startValue, endValue );

                total = getNumberOfPages( totalRecords );

                this.paging = createPaging( totalRecords );

                grid = programStageInstanceService.getTabularReport( anonynousEntryForm, programStage, columns,
                    organisationUnits, level, startValue, endValue, !orderByOrgunitAsc, useCompletedEvents,
                    accessPrivateInfo, displayOrgunitCode, paging.getStartPos(), paging.getPageSize(), i18n );
            }
            // Download as Excel
            else
            {
                grid = programStageInstanceService.getTabularReport( anonynousEntryForm, programStage, columns,
                    organisationUnits, level, startValue, endValue, !orderByOrgunitAsc, useCompletedEvents,
                    accessPrivateInfo, displayOrgunitCode, null, null, i18n );
            }
        }
        catch ( SQLGrammarException ex )
        {
            message = i18n.getString( "failed_to_get_events" );
        }

        return type == null ? SUCCESS : type;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private int getNumberOfPages( int totalRecord )
    {
        int size = ( pageSize != null ) ? pageSize : this.getDefaultPageSize();
        return (totalRecord % size == 0) ? (totalRecord / size) : (totalRecord / size + 1);
    }

    public int getStartPos()
    {
        if ( currentPage == null )
        {
            return paging.getStartPos();
        }
        int startPos = currentPage <= 0 ? 0 : (currentPage - 1) * paging.getPageSize();
        startPos = (startPos > total) ? total : startPos;
        return startPos;
    }

    private List<TabularReportColumn> getTableColumns()
    {
        List<TabularReportColumn> columns = new ArrayList<TabularReportColumn>();

        int index = 0;

        for ( String filterValue : filterValues )
        {
            String[] values = filterValue.split( "_" );

            if ( values != null && values.length >= 3 )
            {
                String prefix = values[0];

                TabularReportColumn column = new TabularReportColumn();
                column.setPrefix( prefix );
                column.setIdentifier( values[1] );
                column.setHidden( Boolean.parseBoolean( values[2] ) );

                column.setOperator( values.length > 3 ? TextUtils.lower( values[3] ) : TextUtils.EMPTY);
                column.setQuery( values.length > 4 ? TextUtils.lower( values[4] ) : TextUtils.EMPTY );
				
               if ( PREFIX_DATA_ELEMENT.equals( prefix ) )
                {
                    int objectId = Integer.parseInt( values[1] );
                    DataElement dataElement = dataElementService.getDataElement( objectId );
                    if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
                    {
                        column.setPrefix( PREFIX_NUMBER_DATA_ELEMENT );
                    }
                    dataElements.add( dataElement );

                    String valueType = dataElement.getOptionSet() != null ? VALUE_TYPE_OPTION_SET : dataElement
                        .getType();
                    valueTypes.add( valueType );
                    mapSuggestedValues.put( index, getSuggestedDataElementValues( dataElement ) );
                    if ( dataElement.getType().equals( DataElement.VALUE_TYPE_DATE ) )
                    {
                        column.setDateType( true );
                    }
                    
                    if ( useFormNameDataElement != null && useFormNameDataElement )
                    {
                        column.setName( dataElement.getFormNameFallback() );
                    }
                    else
                    {
                        column.setName( dataElement.getDisplayName() );  
                    }
                }

                columns.add( column );

                index++;
            }
        }

        return columns;
    }
    
    private List<String> getSuggestedDataElementValues( DataElement dataElement )
    {
        List<String> values = new ArrayList<String>();
        String valueType = dataElement.getType();

        if ( valueType.equals( DataElement.VALUE_TYPE_BOOL ) )
        {
            values.add( i18n.getString( "yes" ) );
            values.add( i18n.getString( "no" ) );
        }
        if ( valueType.equals( DataElement.VALUE_TYPE_TRUE_ONLY ) )
        {
            values.add( i18n.getString( "" ) );
            values.add( i18n.getString( "yes" ) );
        }

        return values;
    }
}
