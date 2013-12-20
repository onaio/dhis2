package org.hisp.dhis.dataelement;

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

import static org.hisp.dhis.dataelement.DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME;

import java.util.Iterator;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.system.deletion.DeletionHandler;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return DataElement.class.getSimpleName();
    }

    @Override
    public void deleteDataElementCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        DataElementCategoryCombo default_ = categoryService
            .getDataElementCategoryComboByName( DEFAULT_CATEGORY_COMBO_NAME );

        for ( DataElement dataElement : dataElementService.getAllDataElements() )
        {
            if ( dataElement.getCategoryCombo().equals( categoryCombo ) )
            {
                dataElement.setCategoryCombo( default_ );

                dataElementService.updateDataElement( dataElement );
            }
        }
    }

    @Override
    public void deleteDataSet( DataSet dataSet )
    {
        Iterator<DataElement> iterator = dataSet.getDataElements().iterator();
        
        while ( iterator.hasNext() )
        {
            DataElement element = iterator.next();
            element.getDataSets().remove( dataSet );
            dataElementService.updateDataElement( element );            
        }
    }

    @Override
    public void deleteDataElementGroup( DataElementGroup group )
    {
        Iterator<DataElement> iterator = group.getMembers().iterator();
        
        while ( iterator.hasNext() )
        {
            DataElement element = iterator.next();
            element.getGroups().remove( group );
            dataElementService.updateDataElement( element );
        }
    }
    
    public String allowDeleteOptionSet( OptionSet optionSet )
    {
        String sql = "SELECT COUNT(*) " + "FROM dataelement " + "WHERE optionsetid=" + optionSet.getId();

        return jdbcTemplate.queryForObject( sql, Integer.class ) == 0 ? null : ERROR;
    }
}
