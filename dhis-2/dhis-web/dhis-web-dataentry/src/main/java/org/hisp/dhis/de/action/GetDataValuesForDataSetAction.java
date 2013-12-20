package org.hisp.dhis.de.action;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
public class GetDataValuesForDataSetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private boolean multiOrganisationUnit;

    public void setMultiOrganisationUnit( boolean multiOrganisationUnit )
    {
        this.multiOrganisationUnit = multiOrganisationUnit;
    }

    public boolean isMultiOrganisationUnit()
    {
        return multiOrganisationUnit;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<DataValue> dataValues = new ArrayList<DataValue>();

    public Collection<DataValue> getDataValues()
    {
        return dataValues;
    }

    private Collection<MinMaxDataElement> minMaxDataElements = new ArrayList<MinMaxDataElement>();

    public Collection<MinMaxDataElement> getMinMaxDataElements()
    {
        return minMaxDataElements;
    }

    private boolean locked = false;

    public boolean isLocked()
    {
        return locked;
    }

    private boolean complete = false;

    public boolean isComplete()
    {
        return complete;
    }

    private Date date;

    public Date getDate()
    {
        return date;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        Set<OrganisationUnit> children = organisationUnit.getChildren();

        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        Period period = PeriodType.getPeriodFromIsoString( periodId );

        // TODO null-checks
        
        // ---------------------------------------------------------------------
        // Data values & Min-max data elements
        // ---------------------------------------------------------------------

        dataValues.addAll( dataValueService.getDataValues( organisationUnit, period, dataSet.getDataElements() ) );
        
        minMaxDataElements.addAll( minMaxDataElementService.getMinMaxDataElements( organisationUnit, dataSet
            .getDataElements() ) );

        if ( multiOrganisationUnit )
        {
            for ( OrganisationUnit ou : children )
            {
                // -------------------------------------------------------------
                // Make sure that the org unit have this data set 
                // -------------------------------------------------------------

                if ( ou.getDataSets().contains( dataSet ) )
                {
                    dataValues.addAll( dataValueService.getDataValues( ou, period, dataSet.getDataElements() ) );
                    minMaxDataElements.addAll( minMaxDataElementService.getMinMaxDataElements( ou, dataSet
                        .getDataElements() ) );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Data set completeness info
        // ---------------------------------------------------------------------

        if ( period != null )
        {
            if ( !multiOrganisationUnit )
            {
                CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet,
                    period, organisationUnit );

                if ( registration != null )
                {
                    complete = true;
                    date = registration.getDate();
                    storedBy = registration.getStoredBy();
                }

                locked = dataSetService.isLocked( dataSet, period, organisationUnit, null );
            }
            else
            {
                complete = true;

                // -------------------------------------------------------------
                // If multi-org and one of the children is locked, lock all
                // -------------------------------------------------------------

                for ( OrganisationUnit ou : children )
                {
                    if ( ou.getDataSets().contains( dataSet ) )
                    {
                        locked = dataSetService.isLocked( dataSet, period, organisationUnit, null );

                        if ( locked )
                        {
                            break;
                        }

                        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration(
                            dataSet, period, ou );

                        if ( complete && registration == null )
                        {
                            complete = false;
                        }
                    }
                }
            }
        }

        return SUCCESS;
    }
}
