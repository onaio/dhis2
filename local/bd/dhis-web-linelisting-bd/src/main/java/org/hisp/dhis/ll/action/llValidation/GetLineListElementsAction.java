package org.hisp.dhis.ll.action.llValidation;

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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetLineListElementsAction
    implements Action
{
    private final static int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
/*    
    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
*/
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String deOptionValue;

    public void setDeOptionValue( String deOptionValue )
    {
        this.deOptionValue = deOptionValue;
    }

    public String getDeOptionValue()
    {
        return deOptionValue;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private List<String> optionComboNames;

    public List<String> getOptionComboNames()
    {
        return optionComboNames;
    }

    private List<String> optionComboIds;
    
    public List<String> getOptionComboIds()
    {
        return optionComboIds;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        optionComboIds = new ArrayList<String>();
        optionComboNames = new ArrayList<String>();
        
        if ( id == null || id == ALL )
        {
            dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        }
        else
        {
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( id );

            if ( dataElementGroup != null )
            {
                dataElements = new ArrayList<DataElement>( dataElementGroup.getMembers() );
            }
            else
            {
                dataElements = new ArrayList<DataElement>();
            }
        }

        Collections.sort( dataElements, dataElementComparator );

        displayPropertyHandler.handle( dataElements );
        
        if ( deOptionValue != null )
        {
        	if( deOptionValue.equalsIgnoreCase( "optioncombo" ))
            {
                Iterator<DataElement> deIterator = dataElements.iterator();
                while(deIterator.hasNext())
                {
                    DataElement de = deIterator.next();
                    DataElementCategoryCombo dataElementCategoryCombo = de.getCategoryCombo();
                    List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                        dataElementCategoryCombo.getOptionCombos() );

                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                    while ( optionComboIterator.hasNext() )
                    {
                        DataElementCategoryOptionCombo decoc = optionComboIterator.next();
                        optionComboIds.add( de.getId()+":"+decoc.getId());
                        optionComboNames.add( de.getName()+":"+dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( decoc ).getName());
                        
                    }   
                }
            }        	
        }
        
                       
        return SUCCESS;
    }
}
