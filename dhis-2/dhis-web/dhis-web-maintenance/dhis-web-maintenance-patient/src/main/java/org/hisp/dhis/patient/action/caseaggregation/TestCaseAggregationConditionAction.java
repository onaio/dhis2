package org.hisp.dhis.patient.action.caseaggregation;

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
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.caseaggregation.CaseAggregationConditionManager;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class TestCaseAggregationConditionAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private CaseAggregationConditionManager caseAggregationConditionManager;

    public void setCaseAggregationConditionManager( CaseAggregationConditionManager caseAggregationConditionManager )
    {
        this.caseAggregationConditionManager = caseAggregationConditionManager;
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    private String condition;

    public void setCondition( String condition )
    {
        this.condition = condition;
    }

    private String operator;

    public void setOperator( String operator )
    {
        this.operator = operator;
    }

    private Integer deSumId;

    public void setDeSumId( Integer deSumId )
    {
        this.deSumId = deSumId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds.add( 0 );

        MonthlyPeriodType periodType = new MonthlyPeriodType();
        Period period = new Period();
        period.setStartDate( new Date() );
        period.setEndDate( new Date() );
        period.setPeriodType( periodType );

        String sql = caseAggregationConditionManager.parseExpressionToSql( false, condition, operator, 0,
            "dataelementname", 0, "optioncomboid", deSumId, orgunitIds, period );

        List<Integer> ids = caseAggregationConditionManager.executeSQL( sql );
        
        return (ids == null) ? INPUT : SUCCESS;
    }
}
