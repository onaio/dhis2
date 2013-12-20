package org.hisp.dhis.common;

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
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.common.view.ShortNameView;

/**
 * @author Bob Jolliffe
 */
@JacksonXmlRootElement(localName = "nameableObject", namespace = DxfNamespaces.DXF_2_0)
public class BaseNameableObject
    extends BaseIdentifiableObject
    implements NameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 714136796552146362L;

    /**
     * An short name representing this Object. Optional but unique.
     */
    protected String shortName;

    /**
     * Description of this Object.
     */
    protected String description;

    /**
     * The i18n variant of the short name. Should not be persisted.
     */
    protected transient String displayShortName;

    /**
     * The i18n variant of the description. Should not be persisted.
     */
    protected transient String displayDescription;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public BaseNameableObject()
    {
    }

    public BaseNameableObject( String uid, String code, String name )
    {
        this.uid = uid;
        this.code = code;
        this.name = name;
    }
    
    public BaseNameableObject( int id, String uid, String name, String shortName,
        String code, String description )
    {
        super( id, uid, name );
        this.shortName = shortName;
        this.code = code;
        this.description = description;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (getShortName() != null ? getShortName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }

    /**
     * Class check uses isAssignableFrom and get-methods to handle proxied objects.
     */
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
        
        if ( !getClass().isAssignableFrom( o.getClass() ) )
        {
            return false;
        }
        
        if ( !super.equals( o ) )
        {
            return false;
        }

        final BaseNameableObject other = (BaseNameableObject) o;

        if ( getShortName() != null ? !getShortName().equals( other.getShortName() ) : other.getShortName() != null )
        {
            return false;
        }
        
        if ( getDescription() != null ? !getDescription().equals( other.getDescription() ) : other.getDescription() != null )
        {
            return false;
        }

        return true;
    }

    @JsonProperty
    @JsonView({ ShortNameView.class, DetailedView.class, ExportView.class })
    @JacksonXmlProperty(isAttribute = true)
    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getDisplayShortName()
    {
        return displayShortName != null && !displayShortName.trim().isEmpty() ? displayShortName : shortName;
    }

    public void setDisplayShortName( String displayShortName )
    {
        this.displayShortName = displayShortName;
    }

    public String getDisplayDescription()
    {
        return displayDescription != null && !displayDescription.trim().isEmpty() ? displayDescription : description;
    }

    public void setDisplayDescription( String displayDescription )
    {
        this.displayDescription = displayDescription;
    }
    
    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            NameableObject nameableObject = (NameableObject) other;

            this.shortName = nameableObject.getShortName() == null ? this.shortName : nameableObject.getShortName();
            this.description = nameableObject.getDescription() == null ? this.description : nameableObject.getDescription();
        }
    }
}
