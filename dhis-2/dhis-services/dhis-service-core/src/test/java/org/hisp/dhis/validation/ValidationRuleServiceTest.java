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

import static org.hisp.dhis.expression.Expression.SEPARATOR;
import static org.hisp.dhis.expression.Operator.equal_to;
import static org.hisp.dhis.expression.Operator.greater_than;
import static org.hisp.dhis.expression.Operator.less_than;
import static org.hisp.dhis.expression.Operator.less_than_or_equal_to;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.system.util.MathUtils;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @author Jim Grace
 * @version $Id$
 */
public class ValidationRuleServiceTest
    extends DhisTest
{
    private DataElement dataElementA;

    private DataElement dataElementB;

    private DataElement dataElementC;

    private DataElement dataElementD;

    private DataElement dataElementE;

    private Set<DataElement> dataElementsA = new HashSet<DataElement>();

    private Set<DataElement> dataElementsB = new HashSet<DataElement>();

    private Set<DataElement> dataElementsC = new HashSet<DataElement>();

    private Set<DataElement> dataElementsD = new HashSet<DataElement>();

    private Set<DataElementCategoryOptionCombo> optionCombos;

    private DataElementCategoryCombo categoryCombo;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Expression expressionA;

    private Expression expressionB;

    private Expression expressionC;

    private Expression expressionD;

    private Expression expressionE;

    private Expression expressionF;

    private Expression expressionG;

    private DataSet dataSetWeekly;

    private DataSet dataSetMonthly;

    private DataSet dataSetYearly;

    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private Period periodE;

    private Period periodF;

    private Period periodG;

    private Period periodH;

    private Period periodI;

    private Period periodJ;

    private Period periodK;

    private Period periodL;

    private Period periodM;

    private Period periodN;

    private Period periodO;

    private Period periodW;

    private Period periodX;

    private Period periodY;

    private Period periodZ;

    private OrganisationUnit sourceA;

    private OrganisationUnit sourceB;

    private OrganisationUnit sourceC;

    private OrganisationUnit sourceD;

    private OrganisationUnit sourceE;

    private OrganisationUnit sourceF;

    private OrganisationUnit sourceG;

    private Set<OrganisationUnit> sourcesA = new HashSet<OrganisationUnit>();

    private ValidationRule validationRuleA;

    private ValidationRule validationRuleB;

    private ValidationRule validationRuleC;

    private ValidationRule validationRuleD;

    private ValidationRule monitoringRuleE;

    private ValidationRule monitoringRuleF;

    private ValidationRule monitoringRuleG;

    private ValidationRule monitoringRuleH;

    private ValidationRule monitoringRuleI;

    private ValidationRule monitoringRuleJ;

    private ValidationRule monitoringRuleK;

    private ValidationRule monitoringRuleL;

    private ValidationRuleGroup group;

    private PeriodType periodTypeWeekly;
    
    private PeriodType periodTypeMonthly;
    
    private PeriodType periodTypeYearly;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    private void joinDataSetToSource ( DataSet dataSet, OrganisationUnit source )
    {
    	source.getDataSets().add( dataSet );
    	dataSet.getSources().add( source );
    }
    
    @Override
    public void setUpTest()
        throws Exception
    {
        validationRuleService = (ValidationRuleService) getBean( ValidationRuleService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        periodTypeWeekly = new WeeklyPeriodType();
        periodTypeMonthly = new MonthlyPeriodType();
        periodTypeYearly = new YearlyPeriodType();

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        dataElementC = createDataElement( 'C' );
        dataElementD = createDataElement( 'D' );
        dataElementE = createDataElement( 'E' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );
        dataElementService.addDataElement( dataElementE );

        dataElementsA.add( dataElementA );
        dataElementsA.add( dataElementB );
        dataElementsB.add( dataElementC );
        dataElementsB.add( dataElementD );
        dataElementsC.add( dataElementB );
        dataElementsD.add( dataElementB );
        dataElementsD.add( dataElementE );

        categoryCombo = categoryService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        categoryOptionCombo = categoryCombo.getOptionCombos().iterator().next();

        String suffix = SEPARATOR + categoryOptionCombo.getUid();

        optionCombos = new HashSet<DataElementCategoryOptionCombo>();
        optionCombos.add( categoryOptionCombo );

        expressionA = new Expression( "#{" + dataElementA.getUid() + suffix + "} + #{" + dataElementB.getUid() + suffix + "}",
            "descriptionA", dataElementsA, optionCombos );
        expressionB = new Expression( "#{" + dataElementC.getUid() + suffix + "} - #{" + dataElementD.getUid() + suffix + "}",
            "descriptionB", dataElementsB , optionCombos);
        expressionC = new Expression( "#{" + dataElementB.getUid() + suffix + "} * 2", "descriptionC", dataElementsC, optionCombos );
        expressionD = new Expression( "#{" + dataElementB.getUid() + suffix + "}", "descriptionD", dataElementsC, optionCombos );
        expressionE = new Expression( "#{" + dataElementB.getUid() + suffix + "} * 1.5", "descriptionE", dataElementsC, optionCombos );
        expressionF = new Expression( "#{" + dataElementB.getUid() + suffix + "} / #{" + dataElementE.getUid() + suffix + "}",
        		"descriptionF", dataElementsD, optionCombos );
        expressionG = new Expression( "#{" + dataElementB.getUid() + suffix + "} * 1.5 / #{" + dataElementE.getUid() + suffix + "}",
        		"descriptionG", dataElementsD, optionCombos );

        expressionService.addExpression( expressionA );
        expressionService.addExpression( expressionB );
        expressionService.addExpression( expressionC );
        expressionService.addExpression( expressionD );
        expressionService.addExpression( expressionE );
        expressionService.addExpression( expressionF );
        expressionService.addExpression( expressionG );

        periodA = createPeriod( periodTypeMonthly, getDate( 2000, 3, 1 ), getDate( 2000, 3, 31 ) );
        periodB = createPeriod( periodTypeMonthly, getDate( 2000, 4, 1 ), getDate( 2000, 4, 30 ) );
        periodC = createPeriod( periodTypeMonthly, getDate( 2000, 5, 1 ), getDate( 2000, 5, 31 ) );
        periodD = createPeriod( periodTypeMonthly, getDate( 2000, 6, 1 ), getDate( 2000, 6, 30 ) );
        periodE = createPeriod( periodTypeMonthly, getDate( 2000, 7, 1 ), getDate( 2000, 7, 31 ) );
        
        periodF = createPeriod( periodTypeMonthly, getDate( 2001, 3, 1 ), getDate( 2001, 3, 31 ) );
        periodG = createPeriod( periodTypeMonthly, getDate( 2001, 4, 1 ), getDate( 2001, 4, 30 ) );
        periodH = createPeriod( periodTypeMonthly, getDate( 2001, 5, 1 ), getDate( 2001, 5, 31 ) );
        periodI = createPeriod( periodTypeMonthly, getDate( 2001, 6, 1 ), getDate( 2001, 6, 30 ) );
        periodJ = createPeriod( periodTypeMonthly, getDate( 2001, 7, 1 ), getDate( 2001, 7, 31 ) );
        
        periodK = createPeriod( periodTypeMonthly, getDate( 2002, 3, 1 ), getDate( 2002, 3, 31 ) );
        periodL = createPeriod( periodTypeMonthly, getDate( 2002, 4, 1 ), getDate( 2002, 4, 30 ) );
        periodM = createPeriod( periodTypeMonthly, getDate( 2002, 5, 1 ), getDate( 2002, 5, 31 ) );
        periodN = createPeriod( periodTypeMonthly, getDate( 2002, 6, 1 ), getDate( 2002, 6, 30 ) );
        periodO = createPeriod( periodTypeMonthly, getDate( 2002, 7, 1 ), getDate( 2002, 7, 31 ) );
        
        periodW = createPeriod( periodTypeWeekly, getDate( 2002, 4, 1 ), getDate( 2000, 4, 7 ) );
        
        periodX = createPeriod( periodTypeYearly, getDate( 2000, 1, 1 ), getDate( 2000, 12, 31 ) );
        periodY = createPeriod( periodTypeYearly, getDate( 2001, 1, 1 ), getDate( 2001, 12, 31 ) );
        periodZ = createPeriod( periodTypeYearly, getDate( 2002, 1, 1 ), getDate( 2002, 12, 31 ) );

        dataSetWeekly = createDataSet( 'W', periodTypeWeekly );
        dataSetMonthly = createDataSet( 'M', periodTypeMonthly );
        dataSetYearly = createDataSet( 'Y', periodTypeYearly );

        sourceA = createOrganisationUnit( 'A' );
        sourceB = createOrganisationUnit( 'B' );
        sourceC = createOrganisationUnit( 'C', sourceB );
        sourceD = createOrganisationUnit( 'D', sourceB );
        sourceE = createOrganisationUnit( 'E', sourceD );
        sourceF = createOrganisationUnit( 'F', sourceD );
        sourceG = createOrganisationUnit( 'G' );

        sourcesA.add( sourceA );
        sourcesA.add( sourceB );

        joinDataSetToSource( dataSetMonthly, sourceA );
        joinDataSetToSource( dataSetMonthly, sourceB );
        joinDataSetToSource( dataSetMonthly, sourceC );
        joinDataSetToSource( dataSetMonthly, sourceD );
        joinDataSetToSource( dataSetMonthly, sourceE );
        joinDataSetToSource( dataSetMonthly, sourceF );
        
        joinDataSetToSource( dataSetWeekly, sourceB );
        joinDataSetToSource( dataSetWeekly, sourceC );
        joinDataSetToSource( dataSetWeekly, sourceD );
        joinDataSetToSource( dataSetWeekly, sourceE );
        joinDataSetToSource( dataSetWeekly, sourceF );
        joinDataSetToSource( dataSetWeekly, sourceG );

        joinDataSetToSource( dataSetYearly, sourceB );
        joinDataSetToSource( dataSetYearly, sourceC );
        joinDataSetToSource( dataSetYearly, sourceD );
        joinDataSetToSource( dataSetYearly, sourceE );
        joinDataSetToSource( dataSetYearly, sourceF );

        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );
        organisationUnitService.addOrganisationUnit( sourceC );
        organisationUnitService.addOrganisationUnit( sourceD );
        organisationUnitService.addOrganisationUnit( sourceE );
        organisationUnitService.addOrganisationUnit( sourceF );
        organisationUnitService.addOrganisationUnit( sourceG );
        
        dataSetMonthly.getDataElements().add( dataElementA );
        dataSetMonthly.getDataElements().add( dataElementB );
        dataSetMonthly.getDataElements().add( dataElementC );
        dataSetMonthly.getDataElements().add( dataElementD );

        dataSetWeekly.getDataElements().add( dataElementE );

        dataSetYearly.getDataElements().add( dataElementE );

        dataElementA.getDataSets().add( dataSetMonthly );
        dataElementB.getDataSets().add( dataSetMonthly );
        dataElementC.getDataSets().add( dataSetMonthly );
        dataElementD.getDataSets().add( dataSetMonthly );
        
        dataElementE.getDataSets().add( dataSetWeekly );

        dataElementE.getDataSets().add( dataSetYearly );

        dataSetService.addDataSet( dataSetWeekly );
        dataSetService.addDataSet( dataSetMonthly );
        dataSetService.addDataSet( dataSetYearly );

        dataElementService.updateDataElement( dataElementA );
        dataElementService.updateDataElement( dataElementB );
        dataElementService.updateDataElement( dataElementC );
        dataElementService.updateDataElement( dataElementD );
        dataElementService.updateDataElement( dataElementE );

        validationRuleA = createValidationRule( 'A', equal_to, expressionA, expressionB, periodTypeMonthly );
        validationRuleB = createValidationRule( 'B', greater_than, expressionB, expressionC, periodTypeMonthly );
        validationRuleC = createValidationRule( 'C', less_than_or_equal_to, expressionB, expressionA, periodTypeMonthly );
        validationRuleD = createValidationRule( 'D', less_than, expressionA, expressionC, periodTypeMonthly );
        
        // Compare dataElementB with 1.5 times itself for one sequential previous period.
        monitoringRuleE = createMonitoringRule( 'E', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 1, 0, 0, 0 );

        // Compare dataElementB with 1.5 times itself for one annual previous period.
        monitoringRuleF = createMonitoringRule( 'F', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 0, 1, 0, 0 );
        
        // Compare dataElementB with 1.5 times itself for one sequential and two annual previous periods.
        monitoringRuleG = createMonitoringRule( 'G', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 1, 2, 0, 0 );
        
        // Compare dataElementB with 1.5 times itself for two sequential and two annual previous periods.
        monitoringRuleH = createMonitoringRule( 'H', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 0, 0 );
        
        // Compare dataElementB with 1.5 times itself for two sequential and two annual previous periods, discarding 2 high outliers.
        monitoringRuleI = createMonitoringRule( 'I', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 2, 0 );
        
        // Compare dataElementB with 1.5 times itself for two sequential and two annual previous periods, discarding 2 low outliers.
        monitoringRuleJ = createMonitoringRule( 'J', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 0, 2 );
        
        // Compare dataElementB with 1.5 times itself for two sequential and two annual previous periods, discarding 2 high & 2 low outliers.
        monitoringRuleK = createMonitoringRule( 'K', less_than_or_equal_to, expressionD, expressionE, periodTypeMonthly, 1, 2, 2, 2, 2 );
        
        // Compare dataElements B/E with 1.5 * B/E for one annual period, no outlier discarding.
        monitoringRuleL = createMonitoringRule( 'L', less_than_or_equal_to, expressionF, expressionG, periodTypeMonthly, 1, 0, 1, 0, 0 );

        group = createValidationRuleGroup( 'A' );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Local convenience routines
    // -------------------------------------------------------------------------

    /**
     * Returns a naturally ordered list of ValidationResults.
     * 
     * When comparing two collections, this assures that all the items
     * are in the same order for comparison. It also means that when there
     * are different values for the same period/rule/source, etc., the
     * results are more likely to be in the same order to make it easier
     * to see the difference.
     * 
     * By making this a List instead of, say a TreeSet, duplicate values
     * (if any should exist by mistake!) are preserved.
     * 
     * @param results collection of ValidationResult to order
     * @return ValidationResults in their natural order
     */
    private List<ValidationResult> orderedList( Collection<ValidationResult> results )
    {
    	List<ValidationResult> resultList = new ArrayList<ValidationResult>( results );
    	Collections.sort( resultList );
    	return resultList;
    }
    
    // -------------------------------------------------------------------------
    // Business logic tests
    // -------------------------------------------------------------------------

    @Test
    public void testValidateDateDateSources()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceB, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceB, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA ); // Invalid
        validationRuleService.saveValidationRule( validationRuleB ); // Invalid
        validationRuleService.saveValidationRule( validationRuleC ); // Valid
        validationRuleService.saveValidationRule( validationRuleD ); // Valid

        // Note: in this and subsequent tests we insert the validation results collection into a new HashSet. This
        // insures that if they are the same as the reference results, they will appear in the same order.
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2000, 2, 1 ), getDate( 2000, 6, 1 ), sourcesA, false, null );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceB, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceB, validationRuleA, 3.0, -1.0 ) );

        reference.add( new ValidationResult( periodA, sourceA, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodA, sourceB, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodB, sourceB, validationRuleB, -1.0, 4.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 8, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateDateDateSourcesGroup()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceB, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceB, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceB, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceB, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceB, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA ); // Invalid
        validationRuleService.saveValidationRule( validationRuleB ); // Invalid
        validationRuleService.saveValidationRule( validationRuleC ); // Valid
        validationRuleService.saveValidationRule( validationRuleD ); // Valid

        group.getMembers().add( validationRuleA );
        group.getMembers().add( validationRuleC );

        validationRuleService.addValidationRuleGroup( group );

        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2000, 2, 1 ), getDate( 2000, 6, 1 ), sourcesA, group, false, null );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceB, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceB, validationRuleA, 3.0, -1.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 4, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateDateDateSource()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, sourceA, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );
        validationRuleService.saveValidationRule( validationRuleC );
        validationRuleService.saveValidationRule( validationRuleD );

        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2000, 2, 1 ), getDate( 2000, 6, 1 ), sourceA );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceA, validationRuleB, -1.0, 4.0 ) );
        reference.add( new ValidationResult( periodB, sourceA, validationRuleB, -1.0, 4.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 4, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateDataSetPeriodSource()
    {
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, sourceA, "1", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "2", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, sourceA, "3", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, sourceA, "4", categoryOptionCombo ) );

        validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );
        validationRuleService.saveValidationRule( validationRuleC );
        validationRuleService.saveValidationRule( validationRuleD );

        Collection<ValidationResult> results = validationRuleService.validate(
        		dataSetMonthly, periodA, sourceA );

        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodA, sourceA, validationRuleA, 3.0, -1.0 ) );
        reference.add( new ValidationResult( periodA, sourceA, validationRuleB, -1.0, 4.0 ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 2, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoringSequential()
    {
    	// System.out.println("\ntestValidateMonitoring1Sequential");
    	// Note: for some monitoring tests, we enter more data than needed, to be sure the extra data *isn't* used.
    	
    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "30", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "35", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "40", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "45", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "50", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleE );
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodL, sourceA, monitoringRuleE, 200.0, 150.0 /* 1.5 * 100 */ ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleE, 400.0, 300.0 /* 1.5 * 200 */ ) );
        reference.add( new ValidationResult( periodN, sourceA, monitoringRuleE, 700.0, 600.0 /* 1.5 * 400 */ ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 3, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoringAnnual()
    {
    	// System.out.println("\ntestValidateMonitoring1Annual");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleF );
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodK, sourceA, monitoringRuleF, 100.0, 75.0 /* 1.5 * 50 */ ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleF, 400.0, 300.0 /* 1.5 * 200 */ ) );
        reference.add( new ValidationResult( periodO, sourceA, monitoringRuleF, 800.0, 600.0 /* 1.5 * 400 */ ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 3, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoring1Sequential2Annual()
    {
    	// System.out.println("\ntestValidateMonitoring1Sequential2Annual");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleG ); // 1 sequential and 2 annual periods
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodK, sourceA, monitoringRuleG, 100.0, 83.6 /* 1.5 * ( 11 + 12 + 50 + 150 ) / 4 */  ) );
        reference.add( new ValidationResult( periodL, sourceA, monitoringRuleG, 200.0, 114.9 /* 1.5 * ( 11 + 12 + 13 + 50 + 150 + 200 + 100 ) / 7 */  ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleG, 400.0, 254.8 /* 1.5 * ( 12 + 13 + 14 + 150 + 200 + 600 + 200 ) / 7 */  ) );
        reference.add( new ValidationResult( periodN, sourceA, monitoringRuleG, 700.0, 351.9 /* 1.5 * ( 13 + 14 + 15 + 200 + 600 + 400 + 400 ) / 7 */  ) );
        reference.add( new ValidationResult( periodO, sourceA, monitoringRuleG, 800.0, 518.7 /* 1.5 * ( 14 + 15 + 600 + 400 + 700 ) / 5 */  ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 5, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }
    
    @Test
    public void testValidateMonitoring2Sequential2Annual()
    {
    	// System.out.println("\ntestValidateMonitoring2Sequential2Annual");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleH ); // 2 sequential and 2 annual periods
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        // Not in results: reference.add( new ValidationResult( periodK, sourceA, monitoringRuleH, 100.0, 109.0 /* 1.5 * ( 11 + 12 + 13 + 50 + 150 + 200 ) / 6 */  ) );
        reference.add( new ValidationResult( periodL, sourceA, monitoringRuleH, 200.0, 191.7 /* 1.5 * ( 11 + 12 + 13 + 14 + 50 + 150 + 200 + 600 + 100 ) / 9 */  ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleH, 400.0, 220.6 /* 1.5 * ( 11 + 12 + 13 + 14 + 15 + 50 + 150 + 200 + 600 + 400 + 100 + 200 ) / 12 */  ) );
        reference.add( new ValidationResult( periodN, sourceA, monitoringRuleH, 700.0, 300.6 /* 1.5 * ( 12 + 13 + 14 + 15 + 150 + 200 + 600 + 400 + 200 + 400 ) / 10 */  ) );
        reference.add( new ValidationResult( periodO, sourceA, monitoringRuleH, 800.0, 439.1 /* 1.5 * ( 13 + 14 + 15 + 200 + 600 + 400 + 400 + 700 ) / 8 */  ) );
        
        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 4, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoring2HighOutliers()
    {
    	// System.out.println("\ntestValidateMonitoring2HighOutliers");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleI ); // discard 2 highest outliers
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodK, sourceA, monitoringRuleI, 100.0, 32.3 /* 1.5 * ( 11 + 12 + 13 + 50 ) / 4 */  ) );
        reference.add( new ValidationResult( periodL, sourceA, monitoringRuleI, 200.0, 75.0 /* 1.5 * ( 11 + 12 + 13 + 14 + 50 + 150 + 100 ) / 7 */  ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleI, 400.0, 114.8 /* 1.5 * ( 11 + 12 + 13 + 14 + 15 + 50 + 150 + 200 + 100 + 200 ) / 10 */  ) );
        reference.add( new ValidationResult( periodN, sourceA, monitoringRuleI, 700.0, 188.3 /* 1.5 * ( 12 + 13 + 14 + 15 + 150 + 200 + 200 + 400 ) / 8 */  ) );
        reference.add( new ValidationResult( periodO, sourceA, monitoringRuleI, 800.0, 260.5 /* 1.5 * ( 13 + 14 + 15 + 200 + 400 + 400 ) / 6 */  ) );
        
        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 5, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoring2LowOutliers()
    {
    	// System.out.println("\ntestValidateMonitoring2LowOutliers");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleJ ); // 2 sequential and 2 annual periods
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        // Not in results: reference.add( new ValidationResult( periodK, sourceA, monitoringRuleH, 100.0, 154.9 /* 1.5 * ( 13 + 50 + 150 + 200 ) / 4 */  ) );
        // Not in results: reference.add( new ValidationResult( periodL, sourceA, monitoringRuleJ, 200.0, 241.5 /* 1.5 * ( 13 + 14 + 50 + 150 + 200 + 600 + 100 ) / 7 */  ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleJ, 400.0, 261.3 /* 1.5 * ( 13 + 14 + 15 + 50 + 150 + 200 + 600 + 400 + 100 + 200 ) / 10 */  ) );
        reference.add( new ValidationResult( periodN, sourceA, monitoringRuleJ, 700.0, 371.1 /* 1.5 * ( 14 + 15 + 150 + 200 + 600 + 400 + 200 + 400 ) / 8 */  ) );
        reference.add( new ValidationResult( periodO, sourceA, monitoringRuleJ, 800.0, 578.8 /* 1.5 * ( 15 + 200 + 600 + 400 + 400 + 700 ) / 6 */  ) );
        
        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 3, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoring2High2LowOutliers()
    {
    	// System.out.println("\ntestValidateMonitoring2High2LowOutliers");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceA, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceA, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceA, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceA, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceA, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceA, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceA, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceA, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceA, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceA, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceA, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceA, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceA, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceA, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceA, "800", categoryOptionCombo ) ); // Jul 2002
    	
        validationRuleService.saveValidationRule( monitoringRuleK ); // discard 2 highest outliers
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceA );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodK, sourceA, monitoringRuleK, 100.0, 47.3 /* 1.5 * ( 13 + 50 ) / 2 */  ) );
        reference.add( new ValidationResult( periodL, sourceA, monitoringRuleK, 200.0, 98.1 /* 1.5 * ( 13 + 14 + 50 + 150 + 100 ) / 5 */  ) );
        reference.add( new ValidationResult( periodM, sourceA, monitoringRuleK, 400.0, 139.1 /* 1.5 * ( 13 + 14 + 15 + 50 + 150 + 200 + 100 + 200 ) / 8 */  ) );
        reference.add( new ValidationResult( periodN, sourceA, monitoringRuleK, 700.0, 244.8 /* 1.5 * ( 14 + 15 + 150 + 200 + 200 + 400 ) / 6 */  ) );
        reference.add( new ValidationResult( periodO, sourceA, monitoringRuleK, 800.0, 380.6 /* 1.5 * ( 15 + 200 + 400 + 400 ) / 4 */  ) );
        
        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 5, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    @Test
    public void testValidateMonitoringWithBaseline()
    {
    	// System.out.println("\ntestValidateMonitoringWithBaseline");

    	dataValueService.addDataValue( createDataValue( dataElementB, periodA, sourceB, "11", categoryOptionCombo ) ); // Mar 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, sourceB, "12", categoryOptionCombo ) ); // Apr 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, sourceB, "13", categoryOptionCombo ) ); // May 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodD, sourceB, "14", categoryOptionCombo ) ); // Jun 2000
        dataValueService.addDataValue( createDataValue( dataElementB, periodE, sourceB, "15", categoryOptionCombo ) ); // Jul 2000

        dataValueService.addDataValue( createDataValue( dataElementB, periodF, sourceB, "50", categoryOptionCombo ) ); // Mar 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodG, sourceB, "150", categoryOptionCombo ) ); // Apr 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodH, sourceB, "200", categoryOptionCombo ) ); // May 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodI, sourceB, "600", categoryOptionCombo ) ); // Jun 2001
        dataValueService.addDataValue( createDataValue( dataElementB, periodJ, sourceB, "400", categoryOptionCombo ) ); // Jul 2001

        dataValueService.addDataValue( createDataValue( dataElementB, periodK, sourceB, "100", categoryOptionCombo ) ); // Mar 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodL, sourceB, "200", categoryOptionCombo ) ); // Apr 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodM, sourceB, "400", categoryOptionCombo ) ); // May 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodN, sourceB, "700", categoryOptionCombo ) ); // Jun 2002
        dataValueService.addDataValue( createDataValue( dataElementB, periodO, sourceB, "800", categoryOptionCombo ) ); // Jul 2002
        
        // This weekly baseline data should be ignored because the period length is less than monthly:
        dataValueService.addDataValue( createDataValue( dataElementE, periodW, sourceB, "1000", categoryOptionCombo ) ); // Week: 1-7 Apr 2002

        dataValueService.addDataValue( createDataValue( dataElementE, periodX, sourceB, "40", categoryOptionCombo ) ); // Year: 2000
        dataValueService.addDataValue( createDataValue( dataElementE, periodY, sourceB, "50", categoryOptionCombo ) ); // Year: 2001
        dataValueService.addDataValue( createDataValue( dataElementE, periodZ, sourceB, "10", categoryOptionCombo ) ); // Year: 2002
        
        validationRuleService.saveValidationRule( monitoringRuleL );
        
        Collection<ValidationResult> results = validationRuleService.validate(
        		getDate( 2002, 1, 15 ), getDate( 2002, 8, 15 ), sourceB );
        
        Collection<ValidationResult> reference = new HashSet<ValidationResult>();

        reference.add( new ValidationResult( periodK, sourceB, monitoringRuleL, 10.0 /* 100 / 10 */, 1.5 /* 1.5 * 50 / 50 */ ) );
        reference.add( new ValidationResult( periodL, sourceB, monitoringRuleL, 20.0 /* 200 / 10 */, 4.5 /* 1.5 * 150 / 50 */ ) );
        reference.add( new ValidationResult( periodM, sourceB, monitoringRuleL, 40.0 /* 400 / 10 */, 6.0 /* 1.5 * 200 / 50 */ ) );
        reference.add( new ValidationResult( periodN, sourceB, monitoringRuleL, 70.0 /* 700 / 10 */, 18.0 /* 1.5 * 600 / 50 */ ) );
        reference.add( new ValidationResult( periodO, sourceB, monitoringRuleL, 80.0 /* 800 / 10 */, 12.0 /* 1.5 * 400 / 50 */ ) );

        for ( ValidationResult result : results )
        {
            assertFalse( MathUtils.expressionIsTrue( result.getLeftsideValue(), result.getValidationRule()
                .getOperator(), result.getRightsideValue() ) );
        }

        assertEquals( 5, results.size() );
        assertEquals( orderedList( reference ), orderedList( results ) );
    }

    // -------------------------------------------------------------------------
    // CURD functionality tests
    // -------------------------------------------------------------------------

    @Test
    public void testSaveValidationRule()
    {
    	// System.out.println("\ntestSaveValidationRule");
        int id = validationRuleService.saveValidationRule( validationRuleA );

        validationRuleA = validationRuleService.getValidationRule( id );

        assertEquals( "ValidationRuleA", validationRuleA.getName() );
        assertEquals( "DescriptionA", validationRuleA.getDescription() );
        assertEquals( ValidationRule.TYPE_ABSOLUTE, validationRuleA.getType() );
        assertEquals( equal_to, validationRuleA.getOperator() );
        assertNotNull( validationRuleA.getLeftSide().getExpression() );
        assertNotNull( validationRuleA.getRightSide().getExpression() );
        assertEquals( periodTypeMonthly, validationRuleA.getPeriodType() );
    }

    @Test
    public void testUpdateValidationRule()
    {
        int id = validationRuleService.saveValidationRule( validationRuleA );
        validationRuleA = validationRuleService.getValidationRuleByName( "ValidationRuleA" );

        assertEquals( "ValidationRuleA", validationRuleA.getName() );
        assertEquals( "DescriptionA", validationRuleA.getDescription() );
        assertEquals( ValidationRule.TYPE_ABSOLUTE, validationRuleA.getType() );
        assertEquals( equal_to, validationRuleA.getOperator() );

        validationRuleA.setId( id );
        validationRuleA.setName( "ValidationRuleB" );
        validationRuleA.setDescription( "DescriptionB" );
        validationRuleA.setType( ValidationRule.TYPE_STATISTICAL );
        validationRuleA.setOperator( greater_than );

        validationRuleService.updateValidationRule( validationRuleA );
        validationRuleA = validationRuleService.getValidationRule( id );

        assertEquals( "ValidationRuleB", validationRuleA.getName() );
        assertEquals( "DescriptionB", validationRuleA.getDescription() );
        assertEquals( ValidationRule.TYPE_STATISTICAL, validationRuleA.getType() );
        assertEquals( greater_than, validationRuleA.getOperator() );
    }

    @Test
    public void testDeleteValidationRule()
    {
        int idA = validationRuleService.saveValidationRule( validationRuleA );
        int idB = validationRuleService.saveValidationRule( validationRuleB );

        assertNotNull( validationRuleService.getValidationRule( idA ) );
        assertNotNull( validationRuleService.getValidationRule( idB ) );

        validationRuleA.clearExpressions();

        validationRuleService.deleteValidationRule( validationRuleA );

        assertNull( validationRuleService.getValidationRule( idA ) );
        assertNotNull( validationRuleService.getValidationRule( idB ) );

        validationRuleB.clearExpressions();

        validationRuleService.deleteValidationRule( validationRuleB );

        assertNull( validationRuleService.getValidationRule( idA ) );
        assertNull( validationRuleService.getValidationRule( idB ) );
    }

    @Test
    public void testGetAllValidationRules()
    {
        validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );

        Collection<ValidationRule> rules = validationRuleService.getAllValidationRules();

        assertTrue( rules.size() == 2 );
        assertTrue( rules.contains( validationRuleA ) );
        assertTrue( rules.contains( validationRuleB ) );
    }

    @Test
    public void testGetValidationRuleByName()
    {
        int id = validationRuleService.saveValidationRule( validationRuleA );
        validationRuleService.saveValidationRule( validationRuleB );

        ValidationRule rule = validationRuleService.getValidationRuleByName( "ValidationRuleA" );

        assertEquals( id, rule.getId() );
        assertEquals( "ValidationRuleA", rule.getName() );
    }

    // -------------------------------------------------------------------------
    // ValidationRuleGroup
    // -------------------------------------------------------------------------

    @Test
    public void testAddValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleService.addValidationRuleGroup( groupA );
        int idB = validationRuleService.addValidationRuleGroup( groupB );

        assertEquals( groupA, validationRuleService.getValidationRuleGroup( idA ) );
        assertEquals( groupB, validationRuleService.getValidationRuleGroup( idB ) );
    }

    @Test
    public void testUpdateValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleService.addValidationRuleGroup( groupA );
        int idB = validationRuleService.addValidationRuleGroup( groupB );

        assertEquals( groupA, validationRuleService.getValidationRuleGroup( idA ) );
        assertEquals( groupB, validationRuleService.getValidationRuleGroup( idB ) );

        ruleA.setName( "UpdatedValidationRuleA" );
        ruleB.setName( "UpdatedValidationRuleB" );

        validationRuleService.updateValidationRuleGroup( groupA );
        validationRuleService.updateValidationRuleGroup( groupB );

        assertEquals( groupA, validationRuleService.getValidationRuleGroup( idA ) );
        assertEquals( groupB, validationRuleService.getValidationRuleGroup( idB ) );
    }

    @Test
    public void testDeleteValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleService.addValidationRuleGroup( groupA );
        int idB = validationRuleService.addValidationRuleGroup( groupB );

        assertNotNull( validationRuleService.getValidationRuleGroup( idA ) );
        assertNotNull( validationRuleService.getValidationRuleGroup( idB ) );

        validationRuleService.deleteValidationRuleGroup( groupA );

        assertNull( validationRuleService.getValidationRuleGroup( idA ) );
        assertNotNull( validationRuleService.getValidationRuleGroup( idB ) );

        validationRuleService.deleteValidationRuleGroup( groupB );

        assertNull( validationRuleService.getValidationRuleGroup( idA ) );
        assertNull( validationRuleService.getValidationRuleGroup( idB ) );
    }

    @Test
    public void testGetAllValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        validationRuleService.addValidationRuleGroup( groupA );
        validationRuleService.addValidationRuleGroup( groupB );

        Collection<ValidationRuleGroup> groups = validationRuleService.getAllValidationRuleGroups();

        assertEquals( 2, groups.size() );
        assertTrue( groups.contains( groupA ) );
        assertTrue( groups.contains( groupB ) );
    }

    @Test
    public void testGetValidationRuleGroupByName()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodTypeMonthly );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodTypeMonthly );

        validationRuleService.saveValidationRule( ruleA );
        validationRuleService.saveValidationRule( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        validationRuleService.addValidationRuleGroup( groupA );
        validationRuleService.addValidationRuleGroup( groupB );

        ValidationRuleGroup groupByName = validationRuleService.getValidationRuleGroupByName( groupA.getName() );

        assertEquals( groupA, groupByName );
    }
}
