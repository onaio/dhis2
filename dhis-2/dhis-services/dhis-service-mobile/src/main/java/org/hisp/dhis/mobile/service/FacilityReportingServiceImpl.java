package org.hisp.dhis.mobile.service;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.mobile.FacilityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.model.Contact;
import org.hisp.dhis.api.mobile.model.DataElement;
import org.hisp.dhis.api.mobile.model.DataSet;
import org.hisp.dhis.api.mobile.model.DataSetList;
import org.hisp.dhis.api.mobile.model.DataSetValue;
import org.hisp.dhis.api.mobile.model.DataValue;
import org.hisp.dhis.api.mobile.model.Section;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.comparator.DataElementSortOrderComparator;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Required;

public class FacilityReportingServiceImpl
    implements FacilityReportingService
{
    private static Log log = LogFactory.getLog( FacilityReportingServiceImpl.class );

    private static boolean DEBUG = log.isDebugEnabled();

    private DataElementSortOrderComparator dataElementComparator = new DataElementSortOrderComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    private org.hisp.dhis.dataelement.DataElementCategoryService categoryService;

    private org.hisp.dhis.datavalue.DataValueService dataValueService;

    private org.hisp.dhis.dataset.DataSetService dataSetService;

    private org.hisp.dhis.i18n.I18nService i18nService;

    private org.hisp.dhis.mobile.service.ModelMapping modelMapping;

    private CompleteDataSetRegistrationService registrationService;

    private CurrentUserService currentUserService;

    private OrganisationUnitService oUnitService;

    private ProgramService programService;

    public ProgramService getProgramService()
    {
        return programService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Service methods
    // -------------------------------------------------------------------------

    public List<DataSet> getMobileDataSetsForUnit( OrganisationUnit unit, String localeString )
    {

        List<DataSet> datasets = new ArrayList<DataSet>();
        Locale locale = LocaleUtil.getLocale( localeString );

        if ( DEBUG )
            log.debug( "Getting data sets for unit " + unit.getName() );

        for ( org.hisp.dhis.dataset.DataSet dataSet : dataSetService.getDataSetsForMobile( unit ) )
        {
            PeriodType periodType = dataSet.getPeriodType();
            if ( periodType instanceof DailyPeriodType || periodType instanceof WeeklyPeriodType
                || periodType instanceof MonthlyPeriodType || periodType instanceof YearlyPeriodType
                || periodType instanceof QuarterlyPeriodType )
            {
                if ( DEBUG )
                    log.debug( "Found data set " + dataSet.getName() );

                datasets.add( getDataSetForLocale( dataSet.getId(), locale ) );
            }
            else
            {
                log.warn( "Dataset '" + dataSet.getName()
                    + "' set to be reported from mobile, but not of a supported period type: " + periodType.getName() );
            }
        }

        return datasets;
    }

    public DataSetList getUpdatedDataSet( DataSetList dataSetList, OrganisationUnit unit, String locale )
    {
        if ( DEBUG )
            log.debug( "Checking updated datasets for org unit " + unit.getName() );
        DataSetList updatedDataSetList = new DataSetList();
        List<DataSet> dataSets = this.getMobileDataSetsForUnit( unit, locale );
        List<DataSet> currentDataSets = dataSetList.getCurrentDataSets();

        // check added dataset
        for ( DataSet dataSet : dataSets )
        {
            if ( !currentDataSets.contains( dataSet ) )
            {
                if ( updatedDataSetList.getAddedDataSets() == null )
                    updatedDataSetList.setAddedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getAddedDataSets().add( dataSet );
                currentDataSets.add( dataSet );
            }
        }

        // check deleted dataset
        for ( DataSet dataSet : currentDataSets )
        {
            if ( !dataSets.contains( dataSet ) )
            {
                if ( updatedDataSetList.getDeletedDataSets() == null )
                    updatedDataSetList.setDeletedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getDeletedDataSets().add( new DataSet( dataSet ) );
            }
        }
        if ( updatedDataSetList.getDeletedDataSets() != null )
        {
            for ( DataSet dataSet : updatedDataSetList.getDeletedDataSets() )
            {
                currentDataSets.remove( dataSet );
            }
        }

        // check modified dataset
        Collections.sort( dataSets );
        Collections.sort( currentDataSets );

        for ( int i = 0; i < dataSets.size(); i++ )
        {
            if ( dataSets.get( i ).getVersion() != currentDataSets.get( i ).getVersion() )
            {
                if ( updatedDataSetList.getModifiedDataSets() == null )
                    updatedDataSetList.setModifiedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getModifiedDataSets().add( dataSets.get( i ) );
            }
        }

        if ( DEBUG )
            log.debug( "Returning updated datasets for org unit " + unit.getName() );

        return updatedDataSetList;
    }

    public DataSetList getDataSetsForLocale( OrganisationUnit unit, String locale )
    {
        DataSetList dataSetList = new DataSetList();
        List<DataSet> dataSets = getMobileDataSetsForUnit( unit, locale );
        dataSetList.setModifiedDataSets( dataSets );
        return dataSetList;
    }

    public DataSet getDataSet( int id )
    {
        return getDataSetForLocale( id, null );
    }

    public DataSet getDataSetForLocale( int dataSetId, Locale locale )
    {
        org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetId );

        if ( dataSet == null )
        {
            return null;
        }

        dataSet = i18n( i18nService, locale, dataSet );

        DataSet ds = new DataSet();

        ds.setId( dataSet.getId() );

        String name = StringUtils.defaultIfEmpty( dataSet.getName(), dataSet.getShortName() );

        ds.setName( name );
        ds.setVersion( 1 );
        
        Integer version = dataSet.getVersion();
        
        if ( version != null )
        {
            ds.setVersion( version );
        }

        ds.setPeriodType( dataSet.getPeriodType().getName() );

        List<Section> sectionList = new ArrayList<Section>();
        ds.setSections( sectionList );

        Set<org.hisp.dhis.dataset.Section> sections = dataSet.getSections();

        if ( sections == null || sections.size() == 0 )
        {
            List<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>(
                dataSet.getDataElements() );

            Collections.sort( dataElements, dataElementComparator );

            // Fake section to store data elements
            
            Section section = new Section();
            section.setId( 0 );
            section.setName( "" );
            section.setDataElements( getDataElements( locale, dataElements ) );
            sectionList.add( section );
        }
        else
        {
            for ( org.hisp.dhis.dataset.Section sec : sections )
            {
                Section section = new Section();
                section.setId( sec.getId() );
                section.setName( sec.getName() );

                List<org.hisp.dhis.dataelement.DataElement> des = new ArrayList<org.hisp.dhis.dataelement.DataElement>( sec.getDataElements() );
                
                // Remove grey fields in order to not display them on mobile
                
                List<DataElement> dataElementList = getDataElements( locale, des );

                List<DataElement> dataElementListFinal = new ArrayList<DataElement>( dataElementList );

                int tempI = 0;
                
                for ( int i = 0; i < dataElementList.size(); i++ )
                {
                    if ( isGreyField( sec, dataElementList.get( i ).getId() ) )
                    {
                        dataElementListFinal.remove( i - tempI );
                        tempI++;
                    }
                }
                
                section.setDataElements( dataElementListFinal );
                sectionList.add( section );
            }
        }

        return ds;
    }

    private List<DataElement> getDataElements( Locale locale, List<org.hisp.dhis.dataelement.DataElement> dataElements )
    {
        List<DataElement> dataElementList = new ArrayList<DataElement>();

        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            dataElement = i18n( i18nService, locale, dataElement );

            DataElement de = modelMapping.getDataElement( dataElement );

            // For facility Reporting, no data elements are mandatory
            
            de.setCompulsory( false );

            dataElementList.add( de );
        }
        
        return dataElementList;
    }

    @Override
    public void saveDataSetValues( OrganisationUnit unit, DataSetValue dataSetValue )
        throws NotAllowedException
    {
        org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetValue.getId() );

        if ( !dataSetAssociatedWithOrgUnit( unit, dataSet ) )
        {
            log.info( "Failed to save data value set for: " + unit.getName() + ", " + dataSet.getName()
                + " - Org unit and data set not associated." );
            throw NotAllowedException.INVALID_DATASET_ASSOCIATION;
        }

        Period period = getPeriod( dataSetValue.getPeriodName(), dataSet.getPeriodType() );

        if ( period == null )
        {
            log.info( "Failed to save data value set for: " + unit.getName() + ", " + dataSet.getName()
                + " - Period not found." );
            throw NotAllowedException.INVALID_PERIOD;
        }

        log.info( "Recieved data value set for: " + unit.getName() + ", " + dataSet.getName() + ", "
            + period.getIsoDate() );

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = getDataElementIdMapping( dataSet );

        for ( DataValue dataValue : dataSetValue.getDataValues() )
        {
            org.hisp.dhis.dataelement.DataElement dataElement = dataElementMap.get( dataValue.getId() );

            if ( dataElement == null )
            {
                log.info( "Data value submitted for data element " + dataValue.getId() + ", that is not in data set '"
                    + dataSet.getName() + "'" );
                continue;
            }

            if ( StringUtils.isEmpty( dataValue.getValue() ) )
            {
                log.debug( "Empty data value for data element " + dataValue.getId() + " not saved" );
                continue;
            }

            saveValue( unit, period, dataElement, dataValue );

        }

        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet, period, unit );

        if ( registration != null )
        {
            registrationService.deleteCompleteDataSetRegistration( registration );
        }

        registration = new CompleteDataSetRegistration();

        registration.setDataSet( dataSet );
        registration.setPeriod( period );
        registration.setSource( unit );
        registration.setDate( new Date() );
        registration.setStoredBy( currentUserService.getCurrentUser().getUsername() );
        registrationService.saveCompleteDataSetRegistration( registration );

        log.info( "Saved and registered data value set as complete: " + unit.getName() + ", " + dataSet.getName()
            + ", " + period.getIsoDate() );
    }

    private Map<Integer, org.hisp.dhis.dataelement.DataElement> getDataElementIdMapping(
        org.hisp.dhis.dataset.DataSet dataSet )
    {
        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();

        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataSet.getDataElements() )
        {
            dataElementMap.put( dataElement.getId(), dataElement );
        }
        
        return dataElementMap;
    }

    private boolean dataSetAssociatedWithOrgUnit( OrganisationUnit unit, org.hisp.dhis.dataset.DataSet dataSet )
    {
        return unit.getDataSets().contains( dataSet );
    }

    private void saveValue( OrganisationUnit unit, Period period, org.hisp.dhis.dataelement.DataElement dataElement, DataValue dv )
    {
        String value = dv.getValue().trim();

        DataElementCategoryOptionCombo cateOptCombo = categoryService.getDataElementCategoryOptionCombo( dv
            .getCategoryOptComboID() );

        org.hisp.dhis.datavalue.DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period,
            cateOptCombo );

        if ( dataValue == null )
        {
            dataValue = new org.hisp.dhis.datavalue.DataValue( dataElement, period, unit, value, "", new Date(), "",
                cateOptCombo );
            dataValueService.addDataValue( dataValue );
        }
        else
        {
            dataValue.setValue( value );
            dataValue.setTimestamp( new Date() );
            dataValueService.updateDataValue( dataValue );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    public Period getPeriod( String periodName, PeriodType periodType )
    {
        Period period = PeriodUtil.getPeriod( periodName, periodType );

        if ( period == null )
        {
            return null;
        }

        Period persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(), periodType );

        if ( persistedPeriod == null )
        {
            periodService.addPeriod( period );
            persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(), periodType );
        }

        return persistedPeriod;
    }

    private boolean isGreyField( org.hisp.dhis.dataset.Section section, int id )
    {
        for ( DataElementOperand operand : section.getGreyedFields() )
        {
            if ( id == operand.getDataElement().getId() )
            {
                return true;
            }
        }
        
        return false;
    }

    // -------------------------------------------------------------------------
    // Dependency setters
    // -------------------------------------------------------------------------

    @Required
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    @Required
    public void setCategoryService( org.hisp.dhis.dataelement.DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    @Required
    public void setDataValueService( org.hisp.dhis.datavalue.DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setDataSetService( org.hisp.dhis.dataset.DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    @Required
    public void setI18nService( org.hisp.dhis.i18n.I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    @Required
    public void setModelMapping( org.hisp.dhis.mobile.service.ModelMapping modelMapping )
    {
        this.modelMapping = modelMapping;
    }

    @Required
    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setoUnitService( OrganisationUnitService oUnitService )
    {
        this.oUnitService = oUnitService;
    }

    @Override
    public Contact updateContactForMobile()
    {        Contact contact = new Contact();

        List<String> listOfContacts = new ArrayList<String>();

        List<OrganisationUnit> listOfOrgUnit = (List<OrganisationUnit>) oUnitService.getAllOrganisationUnits();

        for ( OrganisationUnit each : listOfOrgUnit )
        {
            String contactDetail = each.getName() + "/" + each.getPhoneNumber();
            listOfContacts.add( contactDetail );
        }

        contact.setListOfContacts( listOfContacts );

        return contact;
    }
}
