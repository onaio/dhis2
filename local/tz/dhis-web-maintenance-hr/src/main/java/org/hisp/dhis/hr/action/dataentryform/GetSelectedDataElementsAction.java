package org.hisp.dhis.hr.action.dataentryform;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;


import com.opensymphony.xwork2.Action;

/**
 * @author Yusuph Kassim Kulindwa
 * @version $Id$
 */
public class GetSelectedDataElementsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HrDataSetService dataSetService;

    public void setHrDataSetService( HrDataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    /*private AttributeService attributeService;
    
    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    */
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int dataSetId;

    public int getHrDataSetId()
    {
        return dataSetId;
    }

    public void setHrDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }  

   // private List<Attribute> attributeList;

   // public List<Attribute> getAttributeList()
    //{
    //    return attributeList;
   // }  

    private HrDataSet hrDataSet;

    public HrDataSet getHrDataSet()
    {
        return hrDataSet;
    }

    private List<Attribute> attributes;

    public List<Attribute> getAttributes()
    {
        return attributes;
    }
    

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {        
        hrDataSet = dataSetService.getHrDataSet( dataSetId );

        //attributeList = new ArrayList<Attribute>( dataSet.getAttribute() );
        attributes = new ArrayList<Attribute>( hrDataSet.getAttribute() );
        
        displayPropertyHandler.handle( attributes );

        return SUCCESS;
    }
}