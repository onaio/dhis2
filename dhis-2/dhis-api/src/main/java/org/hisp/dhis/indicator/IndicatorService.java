package org.hisp.dhis.indicator;

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

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public interface IndicatorService
{
    String ID = IndicatorService.class.getName();

    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    int addIndicator( Indicator indicator );

    void updateIndicator( Indicator indicator );

    void deleteIndicator( Indicator indicator );

    Indicator getIndicator( int id );

    Indicator getIndicator( String uid );

    Collection<Indicator> getAllIndicators();

    Collection<Indicator> getIndicators( Collection<Integer> identifiers );

    List<Indicator> getIndicatorsByUid( Collection<String> uids );

    List<Indicator> getIndicatorByName( String name );

    List<Indicator> getIndicatorByShortName( String shortName );

    Indicator getIndicatorByCode( String code );

    Collection<Indicator> getIndicatorsWithGroupSets();

    Collection<Indicator> getIndicatorsWithoutGroups();

    Collection<Indicator> getIndicatorsWithDataSets();

    int getIndicatorCountByName( String name );

    Collection<Indicator> getIndicatorsLikeName( String name );

    Collection<Indicator> getIndicatorsBetweenByName( String name, int first, int max );

    int getIndicatorCount();

    Collection<Indicator> getIndicatorsBetween( int first, int max );

    // -------------------------------------------------------------------------
    // IndicatorType
    // -------------------------------------------------------------------------

    int addIndicatorType( IndicatorType indicatorType );

    void updateIndicatorType( IndicatorType indicatorType );

    void deleteIndicatorType( IndicatorType indicatorType );

    IndicatorType getIndicatorType( int id );

    IndicatorType getIndicatorType( String uid );

    Collection<IndicatorType> getIndicatorTypes( Collection<Integer> identifiers );

    Collection<IndicatorType> getAllIndicatorTypes();

    IndicatorType getIndicatorTypeByName( String name );

    Collection<IndicatorType> getIndicatorTypesBetween( int first, int max );

    Collection<IndicatorType> getIndicatorTypesBetweenByName( String name, int first, int max );

    int getIndicatorTypeCount();

    int getIndicatorTypeCountByName( String name );

    // -------------------------------------------------------------------------
    // IndicatorGroup
    // -------------------------------------------------------------------------

    int addIndicatorGroup( IndicatorGroup indicatorGroup );

    void updateIndicatorGroup( IndicatorGroup indicatorGroup );

    void deleteIndicatorGroup( IndicatorGroup indicatorGroup );

    IndicatorGroup getIndicatorGroup( int id );

    IndicatorGroup getIndicatorGroup( int id, boolean i18nIndicators );

    Collection<IndicatorGroup> getIndicatorGroups( Collection<Integer> identifiers );

    IndicatorGroup getIndicatorGroup( String uid );

    Collection<IndicatorGroup> getAllIndicatorGroups();

    List<IndicatorGroup> getIndicatorGroupByName( String name );

    Collection<IndicatorGroup> getGroupsContainingIndicator( Indicator indicator );

    Collection<IndicatorGroup> getIndicatorGroupsBetween( int first, int max );

    Collection<IndicatorGroup> getIndicatorGroupsBetweenByName( String name, int first, int max );

    int getIndicatorGroupCount();

    int getIndicatorGroupCountByName( String name );

    // -------------------------------------------------------------------------
    // IndicatorGroupSet
    // -------------------------------------------------------------------------

    int addIndicatorGroupSet( IndicatorGroupSet groupSet );

    void updateIndicatorGroupSet( IndicatorGroupSet groupSet );

    void deleteIndicatorGroupSet( IndicatorGroupSet groupSet );

    IndicatorGroupSet getIndicatorGroupSet( int id );

    IndicatorGroupSet getIndicatorGroupSet( int id, boolean i18nGroups );

    IndicatorGroupSet getIndicatorGroupSet( String uid );

    List<IndicatorGroupSet> getIndicatorGroupSetByName( String name );

    Collection<IndicatorGroupSet> getCompulsoryIndicatorGroupSets();

    Collection<IndicatorGroupSet> getCompulsoryIndicatorGroupSetsWithMembers();

    Collection<IndicatorGroupSet> getCompulsoryIndicatorGroupSetsNotAssignedTo( Indicator indicator );

    Collection<IndicatorGroupSet> getAllIndicatorGroupSets();

    Collection<IndicatorGroupSet> getIndicatorGroupSets( Collection<Integer> identifiers );

    Collection<IndicatorGroupSet> getIndicatorGroupSetsBetween( int first, int max );

    Collection<IndicatorGroupSet> getIndicatorGroupSetsBetweenByName( String name, int first, int max );

    int getIndicatorGroupSetCount();

    int getIndicatorGroupSetCountByName( String name );
}
