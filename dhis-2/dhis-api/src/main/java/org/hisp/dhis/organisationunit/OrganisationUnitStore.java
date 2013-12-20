package org.hisp.dhis.organisationunit;

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

import org.hisp.dhis.common.GenericNameableObjectStore;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Defines methods for persisting OrganisationUnits.
 *
 * @author Kristian Nordal
 * @version $Id: OrganisationUnitStore.java 5645 2008-09-04 10:01:02Z larshelg $
 */
public interface OrganisationUnitStore
    extends GenericNameableObjectStore<OrganisationUnit>
{
    String ID = OrganisationUnitStore.class.getName();

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    /**
     * Retrieves the object with the given uid.
     *
     * @param uuid the uid.
     * @return the object with the given uid.
     */
    OrganisationUnit getByUuid( String uuid );

    /**
     * Returns all OrganisationUnits by status.
     *
     * @param active Get active or inactive
     * @return a collection of all OrganisationUnits, or an empty collection if
     *         there are no OrganisationUnits.
     */
    Collection<OrganisationUnit> getAllOrganisationUnitsByStatus( boolean active );

    /**
     * Returns all OrganisationUnits by lastUpdated.
     *
     * @param lastUpdated OrganisationUnits from this date
     * @return a collection of all OrganisationUnits, or an empty collection if
     *         there are no OrganisationUnits.
     */
    Collection<OrganisationUnit> getAllOrganisationUnitsByLastUpdated( Date lastUpdated );

    /**
     * Returns all OrganisationUnits by status and lastUpdated.
     *
     * @param active      Get active or inactive
     * @param lastUpdated OrganisationUnits from this date
     * @return a collection of all OrganisationUnits, or an empty collection if
     *         there are no OrganisationUnits.
     */
    Collection<OrganisationUnit> getAllOrganisationUnitsByStatusLastUpdated( boolean active, Date lastUpdated );

    /**
     * Returns an OrganisationUnit with a given name. Case is ignored.
     *
     * @param name the name of the OrganisationUnit to return.
     * @return the OrganisationUnit with the given name, or null if not match.
     */
    OrganisationUnit getOrganisationUnitByNameIgnoreCase( String name );

    /**
     * Returns all root OrganisationUnits. A root OrganisationUnit is an
     * OrganisationUnit with no parent/has the parent set to null.
     *
     * @return a collection containing all root OrganisationUnits, or an empty
     *         collection if there are no OrganisationUnits.
     */
    Collection<OrganisationUnit> getRootOrganisationUnits();

    /**
     * Returns all OrganisationUnits which are not a member of any OrganisationUnitGroups.
     *
     * @return all OrganisationUnits which are not a member of any OrganisationUnitGroups.
     */
    Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups();

    /**
     * Returns all OrganisationUnit which names are like the given name, or which
     * code or uid are equal the given name, and are within the given groups.
     *
     * @param query  the query to match on name, code or uid.
     * @param groups the organisation unit groups.
     * @param limit  the limit of returned objects.
     * @return a collection of OrganisationUnits.
     */
    Collection<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String query, Collection<OrganisationUnitGroup> groups, boolean limit );

    Map<Integer, Set<Integer>> getOrganisationUnitDataSetAssocationMap();

    Set<Integer> getOrganisationUnitIdsWithoutData();


    /**
     * Retrieves the objects determined by the given first result and max result
     * which status is like the current status.
     *
     * @param status the name which result object names must be like.
     * @param first  the first result object to return.
     * @param max    the max number of result objects to return.
     * @return collection of objects.
     */
    Collection<OrganisationUnit> getBetweenByStatus( boolean status, int first, int max );

    /**
     * Retrieves the objects determined by the given first result and max result
     * which lastUpdated is larger or equal.
     *
     * @param lastUpdated the name which result object names must be like.
     * @param first       the first result object to return.
     * @param max         the max number of result objects to return.
     * @return collection of objects.
     */
    Collection<OrganisationUnit> getBetweenByLastUpdated( Date lastUpdated, int first, int max );


    /**
     * Retrieves the objects determined by the given first result and max result
     * which status is like the current status, and lastUpdated is larger or equal.
     *
     * @param status      the name which result object names must be like.
     * @param lastUpdated the name which result object names must be like.
     * @param first       the first result object to return.
     * @param max         the max number of result objects to return.
     * @return collection of objects.
     */
    Collection<OrganisationUnit> getBetweenByStatusLastUpdated( boolean status, Date lastUpdated, int first, int max );

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    /**
     * Get the OrganisationUnit hierarchy.
     *
     * @return a Collection with OrganisationUnitRelationship entries.
     */
    OrganisationUnitHierarchy getOrganisationUnitHierarchy();

    /**
     * Updates the parent id of the organisation unit with the given id.
     *
     * @param organisationUnitId the child organisation unit identifier.
     * @param parentId           the parent organisation unit identifier.
     */
    void updateOrganisationUnitParent( int organisationUnitId, int parentId );

}
