package org.hisp.dhis.dxf2.events.person;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.patient.Patient;

import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "dateOfBirth", namespace = DxfNamespaces.DXF_2_0 )
public class DateOfBirth
{
    private final Date date;

    private final DateOfBirthType type;

    private final Integer age;

    public DateOfBirth()
    {
        this.type = DateOfBirthType.APPROXIMATE;
        this.age = 0;
        this.date = Patient.getBirthFromAge( age, Patient.AGE_TYPE_YEAR );
    }

    public DateOfBirth( Date date )
    {
        this.type = DateOfBirthType.VERIFIED;
        this.age = Patient.getIntegerValueOfAge( date );
        this.date = date;
    }

    public DateOfBirth( Date date, DateOfBirthType type )
    {
        this.type = type;
        this.age = Patient.getIntegerValueOfAge( date );
        this.date = date;
    }

    public DateOfBirth( Integer age )
    {
        this.type = DateOfBirthType.APPROXIMATE;
        this.age = age;
        this.date = Patient.getBirthFromAge( age, Patient.AGE_TYPE_YEAR );
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public Date getDate()
    {
        return date;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DateOfBirthType getType()
    {
        return type;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public Integer getAge()
    {
        return age;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DateOfBirth that = (DateOfBirth) o;

        if ( age != null ? !age.equals( that.age ) : that.age != null ) return false;
        if ( date != null ? !date.equals( that.date ) : that.date != null ) return false;
        if ( type != that.type ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "DateOfBirth{" +
            "date=" + date +
            ", type=" + type +
            ", age=" + age +
            '}';
    }
}
