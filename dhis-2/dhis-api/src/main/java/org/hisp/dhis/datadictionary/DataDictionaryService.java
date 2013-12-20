package org.hisp.dhis.datadictionary;

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

import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public interface DataDictionaryService
{
    String ID = DataDictionaryService.class.getName();

    // -------------------------------------------------------------------------
    // DataDictionary
    // -------------------------------------------------------------------------

    /**
     * Saves the DataDictionary.
     * 
     * @param dataDictionary the DataDictionary to save.
     * @return the generated identifier.
     */
    int saveDataDictionary( DataDictionary dataDictionary );
    
    /**
     * Retrieves the DataDictionary with the given identifier.
     * 
     * @param id the identifier of the DataDictionary.
     * @return the DataDictionary.
     */
    DataDictionary getDataDictionary( int id );

    /**
     * Retrieves the DataDictionaries with the given identifiers.
     * 
     * @param identifiers the identifiers.
     * @return a collection of DataDictionaries.
     */
    Collection<DataDictionary> getDataDictionaries( Collection<Integer> identifiers );
    
    /**
     * Retrieves the DataDictionary with the given name.
     * 
     * @param name the name of the DataDictionary.
     * @return the DataDictionary.
     */
    List<DataDictionary> getDataDictionaryByName( String name );
    
    /**
     * Deteles the DataDictionary.
     * 
     * @param dataDictionary the DataDictionary to delete.
     */
    void deleteDataDictionary( DataDictionary dataDictionary );
    
    /**
     * Retrieves all DataDictionaries.
     * 
     * @return a collection of DataDictionaries.
     */
    List<DataDictionary> getAllDataDictionaries();
    
    List<DataElement> getDataElementsByDictionaryId( int dictionaryId );
    
    List<DataDictionary> getDataDictionarysBetween( int first, int max );
    
    List<DataDictionary> getDataDictionarysBetweenByName( String name, int first, int max );
    
    int getDataDictionaryCount();
    
    int getDataDictionaryCountByName( String name );
}
