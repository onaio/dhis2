package org.hisp.dhis.datavalue;

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

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

/**
 * Defines the functionality for persisting DataValues.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: DataValueStore.java 5715 2008-09-17 14:05:28Z larshelg $
 */
public interface DataValueStore
{
    String ID = DataValueStore.class.getName();

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    /**
     * Adds a DataValue.
     * 
     * @param dataValue the DataValue to add.
     */
    void addDataValue( DataValue dataValue );

    /**
     * Updates a DataValue.
     * 
     * @param dataValue the DataValue to update.
     */
    void updateDataValue( DataValue dataValue );

    /**
     * Deletes a DataValue.
     * 
     * @param dataValue the DataValue to delete.
     */
    void deleteDataValue( DataValue dataValue );
    
    /**
     * Deletes all DataValues registered for the given Source.
     * 
     * @param source the Source for which the DataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesBySource( OrganisationUnit source );
    
    /**
     * Deletes all DataValues registered for the given DataElement.
     * 
     * @param dataElement the DataElement for which the DataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesByDataElement( DataElement dataElement );
    
    /**
     * Returns a DataValue.
     * 
     * @param source the Source of the DataValue.
     * @param dataElement the DataElement of the DataValue.
     * @param period the Period of the DataValue.
     * @return the DataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    DataValue getDataValue( OrganisationUnit source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo );

    /**
     * Returns a non-persisted DataValue.
     */
    DataValue getDataValue( int dataElementId, int categoryOptionComboId, int periodId, int sourceId );
    
    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all DataValues.
     * 
     * @return a collection of all DataValues.
     */
    Collection<DataValue> getAllDataValues();
    
    /**
     * Returns all DataValues for a given Source and Period.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @return a collection of all DataValues which match the given Source and
     *         Period, or an empty collection if no values match.
     */
    Collection<DataValue> getDataValues( OrganisationUnit source, Period period );
    
    /**
     * Returns all DataValues for a given Source and DataElement.
     * 
     * @param source the Source of the DataValues.
     * @param dataElement the DataElement of the DataValues.
     * @return a collection of all DataValues which match the given Source and
     *         DataElement, or an empty collection if no values match.
     */
    Collection<DataValue> getDataValues( OrganisationUnit source, DataElement dataElement );

    /**
     * Returns all DataValues for a given collection of Sources and a
     * DataElement.
     * 
     * @param sources the Sources of the DataValues.
     * @param dataElement the DataElement of the DataValues.
     * @return a collection of all DataValues which match any of the given
     *         Sources and the DataElement, or an empty collection if no values
     *         match.
     */
    Collection<DataValue> getDataValues( Collection<OrganisationUnit> sources, DataElement dataElement );

    /**
     * Returns all DataValues for a given Source, Period, and collection of
     * DataElements.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @param dataElements the DataElements of the DataValues.
     * @return a collection of all DataValues which match the given Source,
     *         Period, and any of the DataElements, or an empty collection if no
     *         values match.
     */
    Collection<DataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements );
    
    /**
     * Returns all DataValues for a given Source, Period, collection of
     * DataElements and collection of optioncombos.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @param dataElements the DataElements of the DataValues.
     * @return a collection of all DataValues which match the given Source,
     *         Period, and any of the DataElements, or an empty collection if no
     *         values match.
     */
    Collection<DataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos );
    
    /**
     * Returns all DataValues for a given DataElement, Period, and collection of 
     * Sources.
     * 
     * @param dataElement the DataElements of the DataValues.
     * @param period the Period of the DataValues.
     * @param sources the Sources of the DataValues.
     * @return a collection of all DataValues which match the given DataElement,
     *         Period, and Sources.
     */
    Collection<DataValue> getDataValues( DataElement dataElement, Period period, Collection<OrganisationUnit> sources );
    
    /**
     * Returns all DataValues for a given DataElement, collection of Periods, and 
     * collection of Sources.
     * 
     * @param dataElement the DataElements of the DataValues.
     * @param periods the Periods of the DataValues.
     * @param sources the Sources of the DataValues.
     * @return a collection of all DataValues which match the given DataElement,
     *         Periods, and Sources.
     */
    Collection<DataValue> getDataValues( DataElement dataElement, Collection<Period> periods, 
        Collection<OrganisationUnit> sources );
    
    /**
     * Returns all DataValues for a given DataElement, DataElementCategoryOptionCombo,
     * collection of Periods, and collection of Sources.
     * 
     * @param dataElement the DataElements of the DataValues.
     * @param optionCombo the DataElementCategoryOptionCombo of the DataValues.
     * @param periods the Periods of the DataValues.
     * @param sources the Sources of the DataValues.
     * @return a collection of all DataValues which match the given DataElement,
     *         Periods, and Sources.
     */
    Collection<DataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, 
        Collection<Period> periods, Collection<OrganisationUnit> sources );
    
    /**
     * Returns all DataValues for a given collection of DataElementCategoryOptionCombos.
     * 
     * @param optionCombos the DataElementCategoryOptionCombos of the DataValue.
     * @return a collection of all DataValues which match the given collection of
     *         DataElementCategoryOptionCombos.
     */
    Collection<DataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos );
    
    /**
     * Returns all DataValues for a given collection of DataElements.
     * 
     * @param dataElement the DataElements of the DataValue.
     * @return a collection of all DataValues which mach the given collection of DataElements.
     */
    Collection<DataValue> getDataValues( DataElement dataElement );  
    
    /**
     * Returns Latest DataValues for a given DataElement, PeriodType and OrganisationUnit
     * 
     * @param dataElement the DataElements of the DataValue.
     * @param periodType the Period Type of period of the DataValue
     * @param organisationUnit the Organisation Unit of the DataValue
     * @return a Latest DataValue 
     */   
    
    DataValue getLatestDataValues( DataElement dataElement, PeriodType periodType, OrganisationUnit organisationUnit );
    
    /**
     * Gets the number of DataValues persisted since the given data.
     * 
     * @param date the date.
     * @return the number of DataValues.
     */
    int getDataValueCount( Date date );
    
    /**
     * Returns a map of values indexed by DataElementOperand.
     * 
     * @param dataElements collection of DataElements to fetch for
     * @param period period for which to fetch the values
     * @param unit OrganisationUnit for which to fetch the values
     * @param lastUpdatedMap optional map in which to return the lastUpdated date for each value
     * @return
     */
    Map<DataElementOperand, Double> getDataValueMap( Collection<DataElement> dataElements, Period period, OrganisationUnit source );

    /**
     * Returns a map of values indexed by DataElementOperand.
     * 
     * In the (unlikely) event that the same dataElement/optionCombo is found in
     * more than one period for the same organisationUnit and date, the value
     * is returned from the period with the shortest duration.
     * 
     * @param dataElements collection of DataElements to fetch for
     * @param date date which must be present in the period
     * @param unit OrganisationUnit for which to fetch the values
     * @param periodTypes allowable period types in which to find the data
     * @param lastUpdatedMap map in which to return the lastUpdated date for each value
     * @return
     */
    Map<DataElementOperand, Double> getDataValueMap( Collection<DataElement> dataElements, Date date, OrganisationUnit source,
    		Collection<PeriodType> periodTypes, Map<DataElementOperand, Date> lastUpdatedMap );
    
    /**
     * Gets a Collection of DeflatedDataValues.
     * 
     * @param dataElementId the DataElement identifier.
     * @param periodId the Period identifier.
     * @param sourceIds the Collection of Source identifiers.
     */
    Collection<DeflatedDataValue> getDeflatedDataValues( int dataElementId, int periodId, Collection<Integer> sourceIds );
}
