package org.hisp.dhis.dataanalyser.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;

import com.opensymphony.xwork2.Action;

/**
 * @author Seid Hussein
 * @version $Id$
 */
public class GetDataElementsGroupAndDataSetAction
    implements Action
{
    private final static int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    public List<DataElementGroup> getApplicableDataElementGroups( Integer dataSetId )
    {

        List<DataElementGroup> applicableDataElementGroups = new ArrayList<DataElementGroup>();

        if ( id == null || id == ALL )
        {
            applicableDataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        }

        else
        {

            List<DataElement> dataSetMembers = new ArrayList<DataElement>();

            dataSetMembers.addAll( dataSetService.getDataSet( 0 ).getDataElements() );

            List<DataElementGroup> allDataElementGroups = new ArrayList<DataElementGroup>( dataElementService
                .getAllDataElementGroups() );

            Iterator degIterator = allDataElementGroups.iterator();

            while ( degIterator.hasNext() )
            {
                DataElementGroup deg = (DataElementGroup) degIterator.next();

                List<DataElement> checkDataElement = new ArrayList<DataElement>( deg.getMembers() );

                checkDataElement.retainAll( dataSetMembers );

                if ( checkDataElement != null && checkDataElement.size() > 0 )
                {
                    applicableDataElementGroups.add( deg );
                    
                    //System.out.println("DEG : \t" + deg.getName());
                }

            }
        }

        Collections.sort( applicableDataElementGroups, new IdentifiableObjectNameComparator() );
        
        return applicableDataElementGroups;
    }

}
