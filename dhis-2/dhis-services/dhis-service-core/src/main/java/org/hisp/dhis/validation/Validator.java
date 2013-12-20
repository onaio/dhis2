package org.hisp.dhis.validation;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.SystemUtils;

/**
 * Evaluates validation rules.
 * 
 * @author Jim Grace
 */
public class Validator
{
    /**
     * Evaluates validation rules for a collection of organisation units.
     * This method breaks the job down by organisation unit. It assigns the
     * evaluation for each organisation unit to a task that can be evaluated
     * independently in a multithreaded environment.
     * 
     * @param sources the organisation units in which to run the validation rules
     * @param periods the periods of data to check
     * @param rules the ValidationRules to evaluate
     * @param runType whether this is an INTERACTIVE or SCHEDULED run
     * @param lastScheduledRun date/time of the most recent successful
     *        scheduled monitoring run (needed only for scheduled runs)
     * @param constantService Constant Service reference
     * @param expressionService Expression Service reference
     * @param periodService Period Service reference
     * @param dataValueService Data Value Service reference
     * @return a collection of any validations that were found
     */
    public static Collection<ValidationResult> validate( Collection<OrganisationUnit> sources,
        Collection<Period> periods, Collection<ValidationRule> rules, Date lastScheduledRun,
        ConstantService constantService, ExpressionService expressionService, PeriodService periodService, DataValueService dataValueService )
    {
        ValidationRunContext context = ValidationRunContext.getNewValidationRunContext( sources, periods, rules,
            constantService.getConstantMap(), ValidationRunType.SCHEDULED, lastScheduledRun,
            expressionService, periodService, dataValueService );

        int threadPoolSize = getThreadPoolSize( context );
        ExecutorService executor = Executors.newFixedThreadPool( threadPoolSize );

        for ( OrganisationUnitExtended sourceX : context.getSourceXs() )
        {
            if ( sourceX.getToBeValidated() )
            {
                Runnable worker = new ValidatorThread( sourceX, context );
                executor.execute( worker );
            }
        }

        executor.shutdown();
        
        try
        {
            executor.awaitTermination( 23, TimeUnit.HOURS );
        }
        catch ( InterruptedException e )
        {
            executor.shutdownNow();
        }
        
        return context.getValidationResults();
    }
    
    /**
     * Determines how many threads we should use for testing validation rules.
     * 
     * @param context validation run context
     * @return number of threads we should use for testing validation rules
     */
    private static int getThreadPoolSize( ValidationRunContext context )
    {
        int threadPoolSize = SystemUtils.getCpuCores();
        
        if ( threadPoolSize > 2 )
        {
            threadPoolSize--;
        }
        
        if ( threadPoolSize > context.getCountOfSourcesToValidate() )
        {
            threadPoolSize = context.getCountOfSourcesToValidate();
        }

    	return threadPoolSize;
    }
}
