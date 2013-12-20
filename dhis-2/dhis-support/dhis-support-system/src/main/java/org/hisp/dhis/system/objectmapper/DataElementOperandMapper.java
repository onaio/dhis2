package org.hisp.dhis.system.objectmapper;

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

import static org.hisp.dhis.dataelement.DataElementCategoryOptionCombo.DEFAULT_TOSTRING;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.amplecode.quick.mapper.RowMapper;
import org.hisp.dhis.dataelement.DataElementOperand;

/**
 * @author Lars Helge Overland
 */
public class DataElementOperandMapper
    implements RowMapper<DataElementOperand>, org.springframework.jdbc.core.RowMapper<DataElementOperand>
{
    private static final String SEPARATOR = " ";
    
    @Override
    public DataElementOperand mapRow( ResultSet resultSet )
        throws SQLException
    {
        String operandName = resultSet.getString( 2 );

        final String cocName = resultSet.getString( 4 );        
        
        if ( cocName != null && !cocName.equals( DEFAULT_TOSTRING ) )
        {
            operandName += SEPARATOR + cocName;
        }
                
        final DataElementOperand operand = new DataElementOperand(
            resultSet.getString( 1 ),
            resultSet.getString( 3 ),
            operandName );
        
        return operand;
    }

    @Override
    public DataElementOperand mapRow( ResultSet resultSet, int rowNum )
        throws SQLException
    {
        return mapRow( resultSet );
    }
}
