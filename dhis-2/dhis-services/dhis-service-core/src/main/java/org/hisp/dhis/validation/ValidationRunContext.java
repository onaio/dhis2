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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * Holds common values that are used during a validation run (either interactive
 * or scheduled.) These values don't change during the multi-threaded tasks
 * (except that results entries are added in a threadsafe way.)
 * 
 * Some of the values are precalculated collections, to save CPU time during the
 * run. All of these values are stored in this single "context" object to allow
 * a single object reference for each of the scheduled tasks. (This also reduces
 * the amount of memory needed to queue all the multi-threaded tasks.)
 * 
 * For some of these properties this is also important because they should be
 * copied from Hibernate lazy collections before the multithreaded part of the
 * run starts, otherwise the threads may not be able to access these values.
 * 
 * @author Jim Grace
 */
public class ValidationRunContext
{
    private static final Log log = LogFactory.getLog( ValidationRunContext.class );

    private Map<PeriodType, PeriodTypeExtended> periodTypeExtendedMap;

    private ValidationRunType runType;

    private Date lastScheduledRun;

    private Map<String, Double> constantMap;

    private Map<ValidationRule, ValidationRuleExtended> ruleXMap;

    private Collection<OrganisationUnitExtended> sourceXs;
    
    private int countOfSourcesToValidate;

    private Collection<ValidationResult> validationResults;

    private ExpressionService expressionService;

    private PeriodService periodService;

    private DataValueService dataValueService;

    private ValidationRunContext()
    {
    }

    public String toString()
    {
        return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE )
            .append( "\n PeriodTypeExtendedMap", (Arrays.toString( periodTypeExtendedMap.entrySet().toArray() )) )
            .append( "\n runType", runType )
            .append( "\n lastScheduledRun", lastScheduledRun )
            .append( "\n constantMap", "[" + constantMap.size() + "]" )
            .append( "\n ruleXMap", "[" + ruleXMap.size() + "]" )
            .append( "\n sourceXs", Arrays.toString( sourceXs.toArray() ) )
            .append( "\n validationResults", Arrays.toString( validationResults.toArray() ) ).toString();
    }

    /**
     * Creates and fills a new context object for a validation run.
     * 
     * @param sources organisation units for validation
     * @param periods periods for validation
     * @param rules validation rules for validation
     * @param runType whether this is an INTERACTIVE or SCHEDULED run
     * @param lastScheduledRun (for SCHEDULED runs) date/time of previous run
     * @return context object for this run
     */
    public static ValidationRunContext getNewValidationRunContext( Collection<OrganisationUnit> sources,
        Collection<Period> periods, Collection<ValidationRule> rules, Map<String, Double> constantMap,
        ValidationRunType runType, Date lastScheduledRun, ExpressionService expressionService, PeriodService periodService,
        DataValueService dataValueService )
    {
        ValidationRunContext context = new ValidationRunContext();
        context.runType = runType;
        context.lastScheduledRun = lastScheduledRun;
        context.validationResults = new ConcurrentLinkedQueue<ValidationResult>(); // thread-safe
        context.periodTypeExtendedMap = new HashMap<PeriodType, PeriodTypeExtended>();
        context.ruleXMap = new HashMap<ValidationRule, ValidationRuleExtended>();
        context.sourceXs = new HashSet<OrganisationUnitExtended>();
        context.constantMap = constantMap;
        context.expressionService = expressionService;
        context.periodService = periodService;
        context.dataValueService = dataValueService;
        context.initialize( sources, periods, rules );
        return context;
    }

    /**
     * Initializes context values based on sources, periods and rules
     * 
     * @param sources organisation units to evaluate for rules
     * @param periods periods for validation
     * @param rules validation rules for validation
     */
    private void initialize( Collection<OrganisationUnit> sources, Collection<Period> periods,
        Collection<ValidationRule> rules )
    {
        addPeriodsToContext( periods );
        
        boolean surveillanceRulesPresent = addRulesToContext ( rules );
        
        removeAnyUnneededPeriodTypes();
        
        addSourcesToContext( sources, true );
        
        countOfSourcesToValidate = sources.size();
        
        if ( surveillanceRulesPresent )
        {
        	Set<OrganisationUnit> otherDescendants = getAllOtherDescendants( sources );
        	addSourcesToContext( otherDescendants, false );
        }
    }

    /**
     * Adds Periods to the context, grouped by period type.
     * 
     * @param periods Periods to group and add
     */
    private void addPeriodsToContext ( Collection<Period> periods )
    {
	    for ( Period period : periods )
	    {
	        PeriodTypeExtended periodTypeX = getOrCreatePeriodTypeExtended( period.getPeriodType() );
	        periodTypeX.getPeriods().add( period );
	    }
    }

    /**
     * Adds validation rules to the context.
     * 
     * @param rules validation rules to add
     * @return true if there were some surveillance-type rules, false otherwise.
     */
    private boolean addRulesToContext ( Collection<ValidationRule> rules )
    {
    	boolean surveillanceRulesPresent = false;
    	
        for ( ValidationRule rule : rules )
        {
            if ( ValidationRule.RULE_TYPE_SURVEILLANCE.equals( rule.getRuleType() ) )
            {
                if ( rule.getOrganisationUnitLevel() == null )
                {
                    log.error( "surveillance-type validationRule '" + (rule.getName() == null ? "" : rule.getName())
                        + "' has no organisationUnitLevel." );
                    continue; // Ignore rule, avoid null reference later.
                }
                
                surveillanceRulesPresent = true;
            }

            // Find the period type extended for this rule
            PeriodTypeExtended periodTypeX = getOrCreatePeriodTypeExtended( rule.getPeriodType() );
            periodTypeX.getRules().add( rule ); // Add to the period type ext.

            if ( rule.getCurrentDataElements() != null )
            {
                // Add this rule's data elements to the period extended.
                periodTypeX.getDataElements().addAll( rule.getCurrentDataElements() );
            }
            // Add the allowed period types for rule's current data elements:
            periodTypeX.getAllowedPeriodTypes().addAll(
                getAllowedPeriodTypesForDataElements( rule.getCurrentDataElements(), rule.getPeriodType() ) );

            // Add the ValidationRuleExtended
            Collection<PeriodType> allowedPastPeriodTypes = getAllowedPeriodTypesForDataElements(
                rule.getPastDataElements(), rule.getPeriodType() );
            ValidationRuleExtended ruleX = new ValidationRuleExtended( rule, allowedPastPeriodTypes );
            ruleXMap.put( rule, ruleX );
        }
        return surveillanceRulesPresent;
    }

    /**
     * Removes any period types that don't have rules assigned to them.
     */
    private void removeAnyUnneededPeriodTypes()
    {
        // Start by making a defensive copy so we can delete while iterating.
        Set<PeriodTypeExtended> periodTypeXs = new HashSet<PeriodTypeExtended>( periodTypeExtendedMap.values() );
        for ( PeriodTypeExtended periodTypeX : periodTypeXs )
        {
            if ( periodTypeX.getRules().isEmpty() )
            {
                periodTypeExtendedMap.remove( periodTypeX.getPeriodType() );
            }
        }
    }

    /**
     * Finds all organisation unit descendants that are not in a given
     * collection of organisation units. This is needed for surveillance-type
     * rules, because the data values for the rules may need to be aggregated
     * from the organisation unit's descendants.
     * 
     * The descendants will likely be there anyway for a run including
     * surveillance-type rules, because an interactive run containing
     * surveillance-type rules should select an entire subtree, and a
     * scheduled monitoring run will contain all organisation units. But check
     * just to be sure, and find any that may be missing. This makes sure
     * that some of the tests will work, and may be required for some
     * future features to work.
     * 
     * @param sources organisation units whose descendants to check
     * @return all other descendants who need to be added who were not
     * in the original list
     */
    private Set<OrganisationUnit> getAllOtherDescendants( Collection<OrganisationUnit> sources )
    {
    	Set<OrganisationUnit> allOtherDescendants = new HashSet<OrganisationUnit>();
        for ( OrganisationUnit source : sources )
        {
            getOtherDescendantsRecursive( source, sources, allOtherDescendants );
        }
        return allOtherDescendants;
    }

    /**
     * If the children of this organisation unit are not in the collection, then
     * add them and all their descendants if needed.
     * 
     * @param source organisation unit whose children to check
     * @param sources organisation units in the initial list
     * @param allOtherDescendants list of organisation unit descendants we
     * need to add
     */
    private void getOtherDescendantsRecursive( OrganisationUnit source, Collection<OrganisationUnit> sources,
    		Set<OrganisationUnit> allOtherDescendants )
    {
        for ( OrganisationUnit child : source.getChildren() )
        {
            if ( !sources.contains( child ) && !allOtherDescendants.contains( child ) )
            {
            	allOtherDescendants.add( child );
                getOtherDescendantsRecursive( child, sources, allOtherDescendants );
            }
        }
    }

    /**
     * Adds a collection of organisation units to the validation run context.
     * 
     * @param sources organisation units to add
     * @param ruleCheckThisSource true if these organisation units should be
     * evaluated with validation rules, false if not. (This is false when
     * adding descendants of organisation units for the purpose of getting
     * aggregated expression values from descendants, but these organisation
     * units are not in the main list to be evaluated.)
     */
    private void addSourcesToContext ( Collection<OrganisationUnit> sources, boolean ruleCheckThisSource )
    {
        // Get the information we need for each source.
        for ( OrganisationUnit source : sources )
        {
            OrganisationUnitExtended sourceX = new OrganisationUnitExtended( source, ruleCheckThisSource );
            sourceXs.add( sourceX );

            Map<PeriodType, Set<DataElement>> sourceElementsMap = source.getDataElementsInDataSetsByPeriodType();
            for ( PeriodTypeExtended periodTypeX : periodTypeExtendedMap.values() )
            {
                periodTypeX.getSourceDataElements().put( source, new HashSet<DataElement>() );
                for ( PeriodType allowedType : periodTypeX.getAllowedPeriodTypes() )
                {
                    Collection<DataElement> sourceDataElements = sourceElementsMap.get( allowedType );
                    if ( sourceDataElements != null )
                    {
                        periodTypeX.getSourceDataElements().get( source ).addAll( sourceDataElements );
                    }
                }
            }
        }
    }
    
    /**
     * Gets the PeriodTypeExtended from the context object. If not found,
     * creates a new PeriodTypeExtended object, puts it into the context object,
     * and returns it.
     * 
     * @param context validation run context
     * @param periodType period type to search for
     * @return period type extended from the context object
     */
    private PeriodTypeExtended getOrCreatePeriodTypeExtended( PeriodType periodType )
    {
        PeriodTypeExtended periodTypeX = periodTypeExtendedMap.get( periodType );
        if ( periodTypeX == null )
        {
            periodTypeX = new PeriodTypeExtended( periodType );
            periodTypeExtendedMap.put( periodType, periodTypeX );
        }
        return periodTypeX;
    }

    /**
     * Finds all period types that may contain given data elements, whose period
     * type interval is at least as long as the given period type.
     * 
     * @param dataElements data elements to look for
     * @param periodType the minimum-length period type
     * @return all period types that are allowed for these data elements
     */
    private static Collection<PeriodType> getAllowedPeriodTypesForDataElements( Collection<DataElement> dataElements,
        PeriodType periodType )
    {
        Collection<PeriodType> allowedPeriodTypes = new HashSet<PeriodType>();
        if ( dataElements != null )
        {
            for ( DataElement dataElement : dataElements )
            {
                for ( DataSet dataSet : dataElement.getDataSets() )
                {
                    if ( dataSet.getPeriodType().getFrequencyOrder() >= periodType.getFrequencyOrder() )
                    {
                        allowedPeriodTypes.add( dataSet.getPeriodType() );
                    }
                }
            }
        }
        return allowedPeriodTypes;
    }

    // -------------------------------------------------------------------------
    // Set and get methods
    // -------------------------------------------------------------------------

    public Map<PeriodType, PeriodTypeExtended> getPeriodTypeExtendedMap()
    {
        return periodTypeExtendedMap;
    }

    public ValidationRunType getRunType()
    {
        return runType;
    }

    public Date getLastScheduledRun()
    {
        return lastScheduledRun;
    }

    public Map<String, Double> getConstantMap()
    {
        return constantMap;
    }

    public Map<ValidationRule, ValidationRuleExtended> getRuleXMap()
    {
        return ruleXMap;
    }

    public Collection<OrganisationUnitExtended> getSourceXs()
    {
        return sourceXs;
    }

    public int getCountOfSourcesToValidate()
    {
        return countOfSourcesToValidate;
    }

    public Collection<ValidationResult> getValidationResults()
    {
        return validationResults;
    }

    public ExpressionService getExpressionService()
    {
        return expressionService;
    }

    public PeriodService getPeriodService()
    {
        return periodService;
    }

    public DataValueService getDataValueService()
    {
        return dataValueService;
    }
}
