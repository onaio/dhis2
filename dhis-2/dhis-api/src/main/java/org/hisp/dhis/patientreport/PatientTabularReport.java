package org.hisp.dhis.patientreport;

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
import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Chau Thu Tran
 * @version $PatientTabularReport.java May 7, 2012 12:41:41 PM$
 */

@JacksonXmlRootElement( localName = "patientTabularReport", namespace = DxfNamespaces.DXF_2_0 )
public class PatientTabularReport
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = -2880334669266185058L;

    public static String CASE_BASED_REPORT = "caseBasedReport";

    public static String AGGREGATE_REPORT = "aggregateReport";

    public static String PREFIX_DATA_ELEMENT = "de";

    public static String VALUE_TYPE_OPTION_SET = "optionSet";

    public static String PREFIX_NUMBER_DATA_ELEMENT = "numberDe";

    private Program program;

    private ProgramStage programStage;

    private Date startDate;

    private Date endDate;

    private List<String> dimension = new ArrayList<String>();

    private List<String> filter = new ArrayList<String>();

    private String ouMode;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientTabularReport()
    {
    }

    public PatientTabularReport( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "dimension", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "dimension", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getDimension()
    {
        return dimension;
    }

    public void setDimension( List<String> dimension )
    {
        this.dimension = dimension;
    }

    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "filter", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "filter", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getFilter()
    {
        return filter;
    }

    public void setFilter( List<String> filter )
    {
        this.filter = filter;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "programStages", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programStage", namespace = DxfNamespaces.DXF_2_0 )
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getOuMode()
    {
        return ouMode;
    }

    public void setOuMode( String ouMode )
    {
        this.ouMode = ouMode;
    }

}
