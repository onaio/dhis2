package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.datavalue.DataValue;

import static org.hisp.dhis.system.util.DateUtils.*;

/**
 * @author Lars Helge Overland
 * @version $Id: DataValueBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class DataValueBatchHandler
    extends AbstractBatchHandler<DataValue>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public DataValueBatchHandler( JdbcConfiguration config )
    {
        super( config, true, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "datavalue" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "dataelementid" );
        statementBuilder.setIdentifierColumn( "periodid" );
        statementBuilder.setIdentifierColumn( "sourceid" );
        statementBuilder.setIdentifierColumn( "categoryoptioncomboid" );
    }

    @Override
    protected void setIdentifierValues( DataValue value )
    {        
        statementBuilder.setIdentifierValue( value.getDataElement().getId() );
        statementBuilder.setIdentifierValue( value.getPeriod().getId() );
        statementBuilder.setIdentifierValue( value.getSource().getId() );
        statementBuilder.setIdentifierValue( value.getOptionCombo().getId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "dataelementid" );
        statementBuilder.setUniqueColumn( "periodid" );
        statementBuilder.setUniqueColumn( "sourceid" );
        statementBuilder.setUniqueColumn( "categoryoptioncomboid" );
    }
    
    protected void setUniqueValues( DataValue value )
    {        
        statementBuilder.setUniqueValue( value.getDataElement().getId() );
        statementBuilder.setUniqueValue( value.getPeriod().getId() );
        statementBuilder.setUniqueValue( value.getSource().getId() );
        statementBuilder.setUniqueValue( value.getOptionCombo().getId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "dataelementid" );
        statementBuilder.setColumn( "periodid" );
        statementBuilder.setColumn( "sourceid" );
        statementBuilder.setColumn( "value" );
        statementBuilder.setColumn( "storedby" );
        statementBuilder.setColumn( "lastupdated" );
        statementBuilder.setColumn( "comment" );
        statementBuilder.setColumn( "categoryoptioncomboid" );
        statementBuilder.setColumn( "followup" );
    }
    
    protected void setValues( DataValue value )
    {        
        statementBuilder.setValue( value.getDataElement().getId() );
        statementBuilder.setValue( value.getPeriod().getId() );
        statementBuilder.setValue( value.getSource().getId() );
        statementBuilder.setValue( value.getValue() );
        statementBuilder.setValue( value.getStoredBy() );
        statementBuilder.setValue( getLongDateString( value.getTimestamp() ) );
        statementBuilder.setValue( value.getComment() );
        statementBuilder.setValue( value.getOptionCombo().getId() );
        statementBuilder.setValue( value.isFollowup() );
    }
}
