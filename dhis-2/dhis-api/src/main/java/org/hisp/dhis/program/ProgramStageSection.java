package org.hisp.dhis.program;

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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

/**
 * @author Chau Thu Tran
 * 
 * @version ProgramStageSection.java 11:07:27 AM Aug 22, 2012 $
 */
@JacksonXmlRootElement( localName = "programStageSection", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramStageSection
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 3141607927546197116L;

    private List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    private Integer sortOrder;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramStageSection()
    {

    }

    public ProgramStageSection( String name, List<ProgramStageDataElement> programStageDataElements )
    {
        this.name = name;
        this.programStageDataElements = programStageDataElements;
    }

    public ProgramStageSection( String name, List<ProgramStageDataElement> programStageDataElements, Integer sortOrder )
    {
        this.name = name;
        this.programStageDataElements = programStageDataElements;
        this.sortOrder = sortOrder;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JacksonXmlElementWrapper( localName = "programStageDataElements", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programStageDataElement", namespace = DxfNamespaces.DXF_2_0 )
    public List<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    public void setProgramStageDataElements( List<ProgramStageDataElement> programStageDataElements )
    {
        this.programStageDataElements = programStageDataElements;
    }
}
