package org.hisp.dhis.importexport.dxf.converter;

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
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.CompleteDataSetRegistrationImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CompleteDataSetRegistrationConverter
    extends CompleteDataSetRegistrationImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "completeDataSetRegistrations";
    public static final String ELEMENT_NAME = "registration";

    private static final String FIELD_DATASET = "dataSet";
    private static final String FIELD_PERIOD = "period";
    private static final String FIELD_SOURCE = "source";
    private static final String FIELD_DATE = "date";
        
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private CompleteDataSetRegistrationService registrationService;
    
    private DataSetService dataSetService;
    
    private OrganisationUnitService organisationUnitService;
    
    private PeriodService periodService;
    
    private Map<Object, Integer> dataSetMapping;    
    private Map<Object, Integer> periodMapping;    
    private Map<Object, Integer> sourceMapping;

    private PeriodType periodType;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public CompleteDataSetRegistrationConverter( CompleteDataSetRegistrationService completeDataSetRegistrationService,
        DataSetService dataSetService,
        OrganisationUnitService organisationUnitService,
        PeriodService periodService )
    {   
        this.registrationService = completeDataSetRegistrationService;
        this.dataSetService = dataSetService;
        this.organisationUnitService = organisationUnitService;
        this.periodService = periodService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the BatchHandler to use.
     * @param importObjectService the ImportObjectService to use.
     * @param params the ImportParams to use.
     * @param dataSetMapping the DataSet mapping to use.
     * @param periodMapping the Period mapping to use.
     * @param sourceMapping the Source mapping to use.
     */
    public CompleteDataSetRegistrationConverter( BatchHandler<CompleteDataSetRegistration> batchHandler,
        ImportObjectService importObjectService,
        ImportParams params,
        Map<Object, Integer> dataSetMapping,
        Map<Object, Integer> periodMapping,
        Map<Object, Integer> sourceMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.params = params;
        this.dataSetMapping = dataSetMapping;
        this.periodMapping = periodMapping;
        this.sourceMapping = sourceMapping;
        this.periodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        if ( params.isIncludeCompleteDataSetRegistrations() )
        {
            Collection<CompleteDataSetRegistration> registrations = null;
            
            Collection<DataSet> dataSets = dataSetService.getDataSets( params.getDataSets() );
            Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
            Collection<Period> periods = periodService.getPeriods( params.getPeriods() );
                    
            if ( dataSets.size() > 0 && units.size() > 0 && periods.size() > 0 )
            {
                writer.openElement( COLLECTION_NAME );
                
                registrations = registrationService.getCompleteDataSetRegistrations( dataSets, units, periods );
                
                for ( final CompleteDataSetRegistration registration : registrations )
                {
                    writer.writeElement( ELEMENT_NAME, EMPTY,
                        FIELD_DATASET, String.valueOf( registration.getDataSet().getId() ),
                        FIELD_PERIOD, String.valueOf( registration.getPeriod().getId() ),
                        FIELD_SOURCE, String.valueOf( registration.getSource().getId() ),
                        FIELD_DATE, DateUtils.getMediumDateString( registration.getDate() ) );
                }
                
                writer.closeElement();
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final CompleteDataSetRegistration registration = new CompleteDataSetRegistration();
            
            DataSet dataSet = new DataSet( "" );
            registration.setDataSet( dataSet );
            
            Period period = periodType.createPeriod();
            registration.setPeriod( period );
            
            OrganisationUnit source = new OrganisationUnit( "" );
            registration.setSource( source );
            
            registration.getDataSet().setId( dataSetMapping.get( Integer.parseInt( values.get( FIELD_DATASET ) ) ) );
            registration.getPeriod().setId( periodMapping.get( Integer.parseInt( values.get( FIELD_PERIOD ) ) ) );
            registration.getSource().setId( sourceMapping.get( Integer.parseInt( values.get( FIELD_SOURCE ) ) ) );
                        
            registration.setDate( DateUtils.getMediumDate( values.get( FIELD_DATE ) ) );
            
            importObject( registration, params );
        }
    }
}
