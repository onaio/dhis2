package org.hisp.dhis.datavalue;

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

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import org.hisp.dhis.common.ImportableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Kristian Nordal
 * @version $Id: DataValue.java 4638 2008-02-25 10:06:47Z larshelg $
 */
public class DataValue
    implements Serializable, ImportableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6269303850789110610L;

    private static final Pattern ZERO_PATTERN = Pattern.compile( "^0(\\.0*)?$" );
    
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /**
     * Part of the DataValue's composite ID
     */
    private DataElement dataElement;

    /**
     * Part of the DataValue's composite ID
     */
    private Period period;

    /**
     * Part of the DataValue's composite ID
     */
    private OrganisationUnit source;

    /**
     * Part of the DataValue's composite ID
     */
    private DataElementCategoryOptionCombo optionCombo;

    private String value;

    private String storedBy;

    private Date timestamp;

    private String comment;

    private Boolean followup;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataValue()
    {
    }

    public DataValue( DataElement dataElement, Period period, OrganisationUnit source )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
    }

    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, DataElementCategoryOptionCombo optionCombo )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.optionCombo = optionCombo;
    }

    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, String value )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
    }

    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, String value, DataElementCategoryOptionCombo optionCombo )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.optionCombo = optionCombo;
    }

    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, String value, String storedBy,
                      Date timestamp, String comment )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public DataValue( DataElement dataElement, Period period, OrganisationUnit source, String value, String storedBy,
                      Date timestamp, String comment, DataElementCategoryOptionCombo optionCombo )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.source = source;
        this.value = value;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
        this.comment = comment;
        this.optionCombo = optionCombo;
    }

    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------

    public String getMeasure()
    {
        return value;
    }

    public String getName()
    {
        throw new UnsupportedOperationException();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isZero()
    {
        return dataElement != null && dataElement.getType().equals( DataElement.VALUE_TYPE_INT )
            && value != null && ZERO_PATTERN.matcher( value ).find();
    }

    public boolean isNullValue()
    {
        return value == null && comment == null;
    }

    public boolean isFollowup()
    {
        return followup != null && followup;
    }

    public boolean hasComment()
    {
        return comment != null && !comment.isEmpty();
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DataValue) )
        {
            return false;
        }

        final DataValue other = (DataValue) o;

        return dataElement.equals( other.getDataElement() ) && optionCombo.equals( other.getOptionCombo() )
            && period.equals( other.getPeriod() ) && source.equals( other.getSource() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + optionCombo.hashCode();
        result = result * prime + period.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + source.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public OrganisationUnit getSource()
    {
        return source;
    }

    public void setSource( OrganisationUnit source )
    {
        this.source = source;
    }

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public Boolean getFollowup()
    {
        return followup;
    }

    public void setFollowup( Boolean followup )
    {
        this.followup = followup;
    }
}
