package org.hisp.dhis.dataentryform;

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
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18n;

/**
 * @author Bharath Kumar
 */
public interface DataEntryFormService
{
    String ID = DataEntryFormService.class.getName();

    final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?/>)", Pattern.DOTALL );
    final Pattern IDENTIFIER_PATTERN = Pattern.compile( "id=\"(\\w+)-(\\w+)-val\"" );
    final Pattern DATAELEMENT_TOTAL_PATTERN = Pattern.compile( "dataelementid=\"(\\w+?)\"" );
    final Pattern INDICATOR_PATTERN = Pattern.compile( "indicatorid=\"(\\w+)\"" );
    final Pattern VALUE_TAG_PATTERN = Pattern.compile( "value=\"(.*?)\"", Pattern.DOTALL );
    final Pattern TITLE_TAG_PATTERN = Pattern.compile( "title=\"(.*?)\"", Pattern.DOTALL );
    
    // -------------------------------------------------------------------------
    // DataEntryForm
    // -------------------------------------------------------------------------

    /**
     * Adds a DataEntryForm.
     * 
     * @param dataEntryForm The DataEntryForm to add.
     * @return The generated unique identifier for this DataEntryForm.
     */
    int addDataEntryForm( DataEntryForm dataEntryForm );

    /**
     * Updates a DataEntryForm.
     * 
     * @param dataEntryForm The DataEntryForm to update.
     */
    void updateDataEntryForm( DataEntryForm dataEntryForm );

    /**
     * Deletes a DataEntryForm.
     * 
     * @param dataEntryForm The DataEntryForm to delete.
     */
    void deleteDataEntryForm( DataEntryForm dataEntryForm );

    /**
     * Get a DataEntryForm
     * 
     * @param id The unique identifier for the DataEntryForm to get.
     * @return The DataEntryForm with the given id or null if it does not exist.
     */
    DataEntryForm getDataEntryForm( int id );

    /**
     * Returns a DataEntryForm with the given name.
     * 
     * @param name The name.
     * @return A DataEntryForm with the given name.
     */
    DataEntryForm getDataEntryFormByName( String name );
    
    /**
     * Get all DataEntryForms.
     * 
     * @return A collection containing all DataEntryForms.
     */
    Collection<DataEntryForm> getAllDataEntryForms();    
    
    /**
     * Return the DataEntryForms with the given collection of identifiers. If the
     * given argument is null, all DataEntryForms are returned.
     * 
     * @param identifiers the collection of identifiers.
     * @return a collection of DataEntryForms.
     */
    Collection<DataEntryForm> getDataEntryForms( final Collection<Integer> identifiers );
    
    /**
     * Prepare DataEntryForm code for save by reversing the effects of
     * prepareDataEntryFormForEdit().
     * 
     * @return htmlCode the HTML code of the data entry form.
     */
    String prepareDataEntryFormForSave( String htmlCode );

    /**
     * Prepares the data entry form code by injecting the data element operand
     * name as value and title for each entry field.
     * 
     * @param htmlCode the HTML code of the data entry form.
     * @param i18n the i18n object.
     * @return HTML code for the data entry form.
     */
    String prepareDataEntryFormForEdit( String htmlCode, I18n i18n );
    
    /**
     * Prepares the data entry form for data entry by injecting required javascripts
     * and drop down lists.
     * 
     * @param htmlCode the HTML code of the data entry form. 
     * @param dataValues the data values which are registered for this form.
     * @param minMaxMap a map with data element operand identifier as key and
     *        corresponding min max data element as value.
     * @param disabled whether this form is disabled for entry.
     * @param i18n the i18n object.
     * @param dataSet the data set associated with this form.
     * @return HTML code for the form.
     */
    String prepareDataEntryFormForEntry( String htmlCode, I18n i18n, DataSet dataSet );
    
    Set<DataElement> getDataElementsInDataEntryForm( DataSet dataSet );
    
    Set<DataElementOperand> getOperandsInDataEntryForm( DataSet dataSet );
    
    Collection<DataEntryForm> listDisctinctDataEntryFormByProgramStageIds( List<Integer> programStageIds );
    
    Collection<DataEntryForm> listDisctinctDataEntryFormByDataSetIds( List<Integer> dataSetIds );
}
