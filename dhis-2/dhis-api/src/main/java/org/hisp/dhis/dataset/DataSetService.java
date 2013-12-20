package org.hisp.dhis.dataset;

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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 * @version $Id: DataSetService.java 6255 2008-11-10 16:01:24Z larshelg $
 */
public interface DataSetService
{
    String ID = DataSetService.class.getName();

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    /**
     * Adds a DataSet.
     *
     * @param dataSet The DataSet to add.
     * @return The generated unique identifier for this DataSet.
     */
    int addDataSet( DataSet dataSet );

    /**
     * Updates a DataSet.
     *
     * @param dataSet The DataSet to update.
     */
    void updateDataSet( DataSet dataSet );

    /**
     * Deletes a DataSet.
     *
     * @param dataSet The DataSet to delete.
     */
    void deleteDataSet( DataSet dataSet );

    /**
     * Get a DataSet
     *
     * @param id The unique identifier for the DataSet to get.
     * @return The DataSet with the given id or null if it does not exist.
     */
    DataSet getDataSet( int id );

    /**
     * Get a DataSet
     *
     * @param id               The unique identifier for the DataSet to get.
     * @param i18nDataElements whether to i18n the data elements of this data set.
     * @param i18nIndicators   whether to i18n the indicators of this data set.
     * @param i18nOrgUnits     whether to i18n the org units of this data set.
     * @return The DataSet with the given id or null if it does not exist.
     */
    DataSet getDataSet( int id, boolean i18nDataElements, boolean i18nIndicators, boolean i18nOrgUnits );

    /**
     * Get a DataSet
     *
     * @param id               The unique identifier for the DataSet to get.
     * @param i18nDataElements whether to i18n the data elements of this data set.
     * @param i18nIndicators   whether to i18n the indicators of this data set.
     * @param i18nOrgUnits     whether to i18n the org units of this data set.
     * @param i18nSections     whether to i18n the sections of this data set.
     * @return The DataSet with the given id or null if it does not exist.
     */
    DataSet getDataSet( int id, boolean i18nDataElements, boolean i18nIndicators, boolean i18nOrgUnits, boolean i18nSections );

    /**
     * Returns the DataSet with the given UID.
     *
     * @param uid the UID.
     * @return the DataSet with the given UID, or null if no match.
     */
    DataSet getDataSet( String uid );

    /**
     * Returns the DataSet with the given UID. Bypasses the ACL system.
     *
     * @param uid the UID.
     * @return the DataSet with the given UID, or null if no match.
     */
    DataSet getDataSetNoAcl( String uid );

    /**
     * Returns a DataSets with the given name.
     *
     * @param name The name.
     * @return A DataSet with the given name.
     */
    List<DataSet> getDataSetByName( String name );

    /**
     * Returns the DataSet with the given short name.
     *
     * @param shortName The short name.
     * @return The DataSet with the given short name.
     */
    List<DataSet> getDataSetByShortName( String shortName );

    /**
     * Returns the DataSet with the given code.
     *
     * @param code The code.
     * @return The DataSet with the given code.
     */
    DataSet getDataSetByCode( String code );

    /**
     * Returns all DataSets associated with the specified sources.
     */
    Collection<DataSet> getDataSetsBySources( Collection<OrganisationUnit> sources );

    /**
     * Returns the number of Sources among the specified Sources associated with
     * the specified DataSet.
     */
    int getSourcesAssociatedWithDataSet( DataSet dataSet, Collection<OrganisationUnit> sources );

    /**
     * Get all DataSets.
     *
     * @return A collection containing all DataSets.
     */
    Collection<DataSet> getAllDataSets();

    /**
     * Gets all DataSets associated with the given PeriodType.
     *
     * @param periodType the PeriodType.
     * @return a collection of DataSets.
     */
    Collection<DataSet> getDataSetsByPeriodType( PeriodType periodType );

    /**
     * Get all DataSets with corresponding identifiers.
     *
     * @param identifiers the collection of identifiers.
     * @return a collection of indicators.
     */
    Collection<DataSet> getDataSets( Collection<Integer> identifiers );

    /**
     * Get list of available ie. unassigned datasets.
     *
     * @return A List containing all avialable DataSets.
     */
    List<DataSet> getAvailableDataSets();

    /**
     * Get list of assigned (ie. which had corresponding dataentryform)
     * datasets.
     *
     * @return A List containing assigned DataSets.
     */
    List<DataSet> getAssignedDataSets();

    /**
     * Get list of assigned (ie. which had corresponding dataentryform) datasets
     * for specific period type.
     *
     * @return A List containing assigned DataSets for specific period type.
     */
    List<DataSet> getAssignedDataSetsByPeriodType( PeriodType periodType );

    /**
     * Searches through the data sets with the corresponding given identifiers.
     * If the given data element is a member of one of the data sets, that data
     * sets period type is returned. This implies that if the data element is a
     * member of more than one data set, which period type being returned is
     * undefined. If null is passed as the second argument, all data sets will
     * be searched.
     *
     * @param dataElement        the data element to find the period type for.
     * @param dataSetIdentifiers the data set identifiers to search through.
     * @return the period type of the given data element.
     */
    PeriodType getPeriodType( DataElement dataElement, Collection<Integer> dataSetIdentifiers );

    /**
     * Returns a distinct collection of data elements associated with the data
     * sets with the given corresponding data set identifiers.
     *
     * @param dataSetIdentifiers the data set identifiers.
     * @return a distinct collection of data elements.
     */
    Collection<DataElement> getDistinctDataElements( Collection<Integer> dataSetIdentifiers );

    /**
     * Returns a list of data sets with the given uids.
     *
     * @param uids the collection of uids.
     * @return a list of data sets.
     */
    List<DataSet> getDataSetsByUid( Collection<String> uids );

    /**
     * Returns a collection of data elements associated with the given
     * corresponding data set.
     *
     * @param dataSet the data set object.
     * @return a collection of data elements.
     */
    Collection<DataElement> getDataElements( DataSet dataSet );

    /**
     * Returns all DataSets that can be collected through mobile (one
     * organisation unit).
     */
    Collection<DataSet> getDataSetsForMobile( OrganisationUnit source );

    /**
     * Returns all DataSets that can be collected through mobile (all
     * organisation unit).
     */
    Collection<DataSet> getDataSetsForMobile();

    int getDataSetCountByName( String name );

    Collection<DataSet> getDataSetsBetweenByName( String name, int first, int max );

    int getDataSetCount();

    Collection<DataSet> getDataSetsBetween( int first, int max );

    // -------------------------------------------------------------------------
    // DataSet LockExceptions
    // -------------------------------------------------------------------------

    /**
     * Add new lock exception
     *
     * @param lockException LockException instance to add
     * @return Database ID of LockException
     */
    int addLockException( LockException lockException );

    /**
     * Update lock exception
     *
     * @param lockException LockException instance to update
     */
    void updateLockException( LockException lockException );

    /**
     * Delete lock exception
     *
     * @param lockException LockException instance to delete
     */
    void deleteLockException( LockException lockException );

    /**
     * Get LockException by ID
     *
     * @param id ID of LockException to get
     * @return LockException with given ID, or null if not found
     */
    LockException getLockException( int id );

    /**
     * Get number of LockExceptions in total
     *
     * @return Total count of LockExceptions
     */
    int getLockExceptionCount();

    /**
     * Returns all lock exceptions
     *
     * @return List of all the lock exceptions
     */
    Collection<LockException> getAllLockExceptions();

    /**
     * Get all LockExceptions within a specific range
     *
     * @param first Index to start at
     * @param max   Number of results wanted
     * @return Collection of LockExceptions withing the range specified
     */
    Collection<LockException> getLockExceptionsBetween( int first, int max );

    /**
     * Find all unique dataSet + period combinations
     * (mainly used for batch removal)
     *
     * @return A collection of all unique combinations (only dataSet and period is set)
     */
    public Collection<LockException> getLockExceptionCombinations();

    /**
     * Delete a dataSet + period combination, used for batch removal, e.g. when you
     * have a lock exception set on 100 OUs with the same dataSet + period combination.
     *
     * @param dataSet DataSet part of the combination
     * @param period  Period part of the combination
     */
    void deleteLockExceptionCombination( DataSet dataSet, Period period );

    /**
     * Checks whether the system is locked for data entry for the given input.
     *
     * @param dataSet          the data set
     * @param period           Period the period.s
     * @param organisationUnit the organisation unit.
     * @param now              the base date for deciding locked date, current date if null.
     * @return true or false indicating whether the system is locked or not.
     */
    boolean isLocked( DataSet dataSet, Period period, OrganisationUnit organisationUnit, Date now );

    /**
     * Checks whether the system is locked for data entry for the given input.
     *
     * @param dataElement      the data element.
     * @param period           the period.
     * @param organisationUnit the organisation unit.
     * @param now              the base date for deciding locked date, current date if null.
     * @return true or false indicating whether the system is locked or not.
     */
    boolean isLocked( DataElement dataElement, Period period, OrganisationUnit organisationUnit, Date now );
}
