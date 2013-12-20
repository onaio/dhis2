package org.hisp.dhis.caseaggregation;

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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Chau Thu Tran
 */
public interface CaseAggregationConditionManager
{
    List<Integer> executeSQL( String sql );

    /**
     * Aggregate data values from query builder formulas defined based on
     * datasets which have data elements defined in the formulas
     * 
     * @param caseAggregateSchedule
     * @param taskStrategy Specify how to get period list based on period type
     *        of each dataset. There are four options, include last month, last
     *        3 month, last 6 month and last 12 month
     */
    Future<?> aggregate( ConcurrentLinkedQueue<CaseAggregateSchedule> caseAggregateSchedule, String taskStrategy );

    /**
     * Return a data value table aggregated of a query builder formula
     * 
     * @param caseAggregationCondition The query builder expression
     * @param orgunitIds The ids of organisation unit where to aggregate data
     *        value
     * @param period The date range for aggregate data value
     * @param format
     * @param i18n
     */
    Grid getAggregateValue( CaseAggregationCondition caseAggregationCondition, Collection<Integer> orgunitIds,
        Period period, I18nFormat format, I18n i18n );

    /**
     * Insert data values into database directly
     * 
     * @param caseAggregationCondition The query builder expression
     * @param orgunitIds The ids of organisation unit where to aggregate data
     *        value
     * @param period The date range for aggregate data value
     */
    void insertAggregateValue( CaseAggregationCondition caseAggregationCondition, Collection<Integer> orgunitIds,
        Period period );

    /**
     * Return standard SQL from query builder formula
     * 
     * @param isInsert Insert aggregate result into database directly
     * @param caseExpression The query builder expression
     * @param operator There are six operators, includes Number of persons,
     *        Number of visits, Sum, Average, Minimum and Maximum of data
     *        element values.
     * @param aggregateDeId The id of aggregate data element
     * @param aggregateDeName The name of aggregate data element
     * @param optionComboId The id of category option combo
     * @param optionComboName The name of category option combo
     * @param deSumId The id of aggregate data element which used for aggregate
     *        data values for operator Sum, Average, Minimum and Maximum of data
     *        element values. This fill is null for other operators.
     * @param orgunitId The id of organisation unit where to aggregate data
     *        value
     * @param startDate Start date
     * @param endDate End date
     */
    String parseExpressionToSql( boolean isInsert, String caseExpression, String operator, Integer aggregateDeId,
        String aggregateDeName, Integer optionComboId, String optionComboName, Integer deSumId,
        Collection<Integer> orgunitIds, Period period );

    boolean hasOrgunitProgramStageCompleted( String expresstion );
    
    Grid getAggregateValueDetails( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period, I18nFormat format, I18n i18n );
    
    String parseExpressionDetailsToSql( String caseExpression, String operator, Integer orgunitId, Period period );
}
