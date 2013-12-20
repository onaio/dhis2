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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.mock.MockI18n;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Bharath
 * @version $Id$
 */
public class DataEntryFormServiceTest
    extends DhisSpringTest
{
    private PeriodType periodType;
    
    private DataElement dataElement;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;
    
    private I18n i18n;
    
    private String dataElementUid;
    
    private String categoryOptionComboUid;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataSetService = (DataSetService) getBean( DataSetService.ID );

        dataEntryFormService = (DataEntryFormService) getBean( DataEntryFormService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        periodType = new MonthlyPeriodType();
        
        dataElement = createDataElement( 'A' );
        
        dataElementService.addDataElement( dataElement );
        
        categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        dataElementUid = dataElement.getUid();
        categoryOptionComboUid = categoryOptionCombo.getUid();
        
        i18n = new MockI18n();
    }

    // -------------------------------------------------------------------------
    // DataEntryForm
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataEntryForm()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A");

        int dataEntryFormAid = dataEntryFormService.addDataEntryForm( dataEntryFormA );

        dataEntryFormA = dataEntryFormService.getDataEntryForm( dataEntryFormAid );

        assertEquals( dataEntryFormAid, dataEntryFormA.getId() );
        assertEquals( "DataEntryForm-A", dataEntryFormA.getName() );
    }

    @Test
    public void testUpdateDataEntryForm()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertEquals( "DataEntryForm-A", dataEntryForm.getName() );

        dataEntryForm.setName( "DataEntryForm-X" );

        dataEntryFormService.updateDataEntryForm( dataEntryForm );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertEquals( dataEntryForm.getName(), "DataEntryForm-X" );
    }

    @Test
    public void testDeleteAndGetDataEntryForm()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertNotNull( dataEntryFormService.getDataEntryForm( id ) );

        dataEntryFormService.deleteDataEntryForm( dataEntryFormService.getDataEntryForm( id ) );

        assertNull( dataEntryFormService.getDataEntryForm( id ) );
    }

    @Test
    public void testGetDataEntryFormByName()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertEquals( dataEntryFormService.getDataEntryFormByName( "DataEntryForm-A" ), dataEntryForm );
        assertNull( dataEntryFormService.getDataEntryFormByName( "DataEntryForm-X" ) );
    }

    @Test
    public void testGetAllDataEntryForms()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );
        DataSet dataSetB = new DataSet( "DataSet-B", periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        
        dataEntryFormService.addDataEntryForm( dataEntryFormA );
        dataEntryFormService.addDataEntryForm( dataEntryFormB );

        Collection<DataEntryForm> dataEntryForms = dataEntryFormService.getAllDataEntryForms();

        assertEquals( dataEntryForms.size(), 2 );
        assertTrue( dataEntryForms.contains( dataEntryFormA ) );
        assertTrue( dataEntryForms.contains( dataEntryFormB ) );
    }

    @Test
    public void testGetAvailableDataSets()
    {
        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );
        DataSet dataSetB = new DataSet( "DataSet-B", periodType );
        DataSet dataSetC = new DataSet( "DataSet-C", periodType );

        dataSetA.setDataEntryForm( dataEntryFormA );
        dataSetB.setDataEntryForm( dataEntryFormB );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );
        
        List<DataSet> dataSets = dataSetService.getAvailableDataSets();

        assertEquals( dataSets.size(), 1 );
    }

    @Test
    public void testGetAssignedDataSets()
    {
        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );
        DataSet dataSetB = new DataSet( "DataSet-B", periodType );
        DataSet dataSetC = new DataSet( "DataSet-C", periodType );

        dataSetA.setDataEntryForm( dataEntryFormA );
        dataSetB.setDataEntryForm( dataEntryFormB );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );
        
        List<DataSet> dataSets = dataSetService.getAssignedDataSets();

        assertEquals( dataSets.size(), 2 );
    }

    @Test
    public void testGetOperands()
    {
        String html = "<table><tr><td><input id=\"abc-def-val\" style=\"width:4em;text-align:center\" /></td></tr></table>";
        DataEntryForm dataEntryForm = new DataEntryForm( "FormA", html );
        DataSet dataSet = createDataSet( 'A', new MonthlyPeriodType() );
        dataSet.setDataEntryForm( dataEntryForm );
        
        Set<DataElementOperand> operands = dataEntryFormService.getOperandsInDataEntryForm( dataSet );
        
        DataElementOperand operand = new DataElementOperand( "abc", "def" );
        
        assertEquals( 1, operands.size() );
        assertTrue( operands.contains( operand ) );
    }
    
    @Test
    public void testPrepareForSave()
    {
        String html = "<table><tr><td><input id=\"1434-11-val\" style=\"width:4em;text-align:center\" title=\"[ 1434 - Expected Births - 11 - (default) - int ]\" value=\"[ Expected Births - (default) ]\" /></td></tr></table>";
        String expected = "<table><tr><td><input id=\"1434-11-val\" style=\"width:4em;text-align:center\" title=\"\" value=\"\" /></td></tr></table>";
        String actual = dataEntryFormService.prepareDataEntryFormForSave( html );
        
        assertEquals( expected, actual );
    }
    
    @Test
    public void testPrepareForEdit()
    {        
        String html = "<table><tr><td><input id=\"" + dataElementUid + "-" + categoryOptionComboUid + "-val\" style=\"width:4em;text-align:center\" title=\"\" value=\"\" /></td></tr></table>";
        String title = "" + dataElementUid + " - " + dataElement.getName() + " - " + categoryOptionComboUid + " - " + categoryOptionCombo.getName() + " - " + dataElement.getType();
        String value = "[ " + dataElement.getName() + " " + categoryOptionCombo.getName() + " ]";
        String expected = "<table><tr><td><input id=\"" + dataElementUid + "-" + categoryOptionComboUid + "-val\" style=\"width:4em;text-align:center\" title=\"" + title + "\" value=\"" + value + "\" /></td></tr></table>";
        String actual = dataEntryFormService.prepareDataEntryFormForEdit( html, i18n );

        assertEquals( expected.length(), actual.length() );
    }
}
