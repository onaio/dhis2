package org.hisp.dhis.den.api;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface LLDataValueService
{

    String ID = LLDataValueService.class.getName();

    // -------------------------------------------------------------------------
    // Basic Line listing DataValue
    // -------------------------------------------------------------------------

    /**
     * Adds a DataValue. If both the value and the comment properties of the
     * specified DataValue object are null, then the object should not be
     * persisted.
     * 
     * @param dataValue the DataValue to add.
     */
    void addDataValue( LLDataValue dataValue );

    /**
     * Updates a DataValue. If both the value and the comment properties of the
     * specified DataValue object are null, then the object should be deleted
     * from the underlying storage.
     * 
     * @param dataValue the DataValue to update.
     */
    void updateDataValue( LLDataValue dataValue );

    /**
     * Deletes a DataValue.
     * 
     * @param dataValue the DataValue to delete.
     */
    void deleteDataValue( LLDataValue dataValue );

    /**
     * Deletes all DataValues connected to a Source.
     * 
     * @param source the Source for which the DataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesBySource( OrganisationUnit source );

    /**
     * Deletes all DataValues registered for the given DataElemt.
     * 
     * @param dataElement the DataElement for which the DataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesByDataElement( DataElement dataElement );

    LLDataValue getDataValue( OrganisationUnit source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo, int recordNo);
    
    /**
     * Returns a List of DataValues.
     * 
     * @param source the Source of the DataValue.
     * @param dataElement the DataElement of the DataValue.
     * @param period the Period of the DataValue.
     * @return the DataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement, Period period );
    
    /**
     * Returns a List of DataValues.
     * 
     * @param source the Source of the DataValue.
     * @param dataElement the DataElement of the DataValue.
     * @param period the Period of the DataValue.
     * @return the DataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement, Period period, DataElementCategoryOptionCombo optionCombo );

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all DataValues.
     * 
     * @return a collection of all DataValues.
     */
    Collection<LLDataValue> getAllDataValues();
    
    /**
     * Returns all DataValues for a given Source and Period.
     * 
     * @param source the Source of the DataValues.
     * @param period the Period of the DataValues.
     * @return a collection of all DataValues which match the given Source and
     *         Period, or an empty collection if no values match.
     */
    Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period );

    /**
     * Returns all DataValues for a given Source and DataElement.
     * 
     * @param source the Source of the DataValues.
     * @param dataElement the DataElement of the DataValues.
     * @return a collection of all DataValues which match the given Source and
     *         DataElement, or an empty collection if no values match.
     */
    Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement );

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
    Collection<LLDataValue> getDataValues( Collection<OrganisationUnit> sources, DataElement dataElement );
    
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
    Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos );

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
    Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements );
    
    /**
     * Returns all DataValues for a given DataElement, collection of Periods, and 
     * collection of Sources
     * @param dataElement the dataElements of the DataValues.
     * @param periods the periods of the DataValues.
     * @param sources the Sources of the DataValues.
     * @return a collection of all DataValues which match the given DataElement,
     *         Periods, and Sources.
     */
    Collection<LLDataValue> getDataValues( DataElement dataElement, Collection<Period> periods, 
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
    Collection<LLDataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, 
        Collection<Period> periods, Collection<OrganisationUnit> sources );
    
    /**
     * Returns all DataValues for a given collection of DataElements, collection of Periods, and
     * collection of Sources, limited by a given start indexs and number of elements to return.
     * 
     * @param dataElements the DataElements of the DataValue.
     * @param periods the Periods of the DataValue.
     * @param sources the Sources of the DataValues.
     * @param firstResult the zero-based index of the first DataValue in the collection to return.
     * @param maxResults the maximum number of DataValues to return. 0 means no restrictions.
     * @return a collection of all DataValues which match the given collection of DataElements,
     *         Periods, and Sources, limited by the firstResult and maxResults property.
     */
    Collection<LLDataValue> getDataValues( Collection<DataElement> dataElements, Collection<Period> periods, 
        Collection<OrganisationUnit> sources, int firstResult, int maxResults );    
    
    /**
     * Returns all DataValues for a given collection of DataElementCategoryOptionCombos.
     * 
     * @param optionCombos the DataElementCategoryOptionCombos of the DataValue.
     * @return a collection of all DataValues which match the given collection of
     *         DataElementCategoryOptionCombos.
     */
    Collection<LLDataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos );
    
    /**
     * Returns all DataValues for a given collection of DataElements.
     * 
     * @param dataElement the DataElements of the DataValue.
     * @return a collection of all DataValues which mach the given collection of DataElements.
     */
    Collection<LLDataValue> getDataValues( DataElement dataElement );
    
    int getMaxRecordNo();
    
    Map<String,String> processLineListBirths( OrganisationUnit organisationUnit, Period period );
    
    Map<String,String> processLineListDeaths( OrganisationUnit organisationUnit, Period periodL );
    
    Map<String,String> processLineListMaternalDeaths( OrganisationUnit organisationUnit, Period periodL );
    
    List<String> getLLImportFiles();
    
    List<LLImportParameters> getLLImportParameters( String fileName );
    
    //void saveLLdataValue( String query );
    
    void removeLLRecord( int recordNo );
}
