package org.hisp.dhis.report;

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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.reporttable.ReportParams;
import org.hisp.dhis.reporttable.ReportTable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement( localName = "report", namespace = DxfNamespaces.DXF_2_0 )
public class Report
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 7880117720157807526L;

    public static final String TEMPLATE_DIR = "templates";
    
    public static final String TYPE_JASPER_REPORT_TABLE = "jasperReportTable";
    public static final String TYPE_JASPER_JDBC = "jasperJdbc";
    public static final String TYPE_HTML = "html";

    private String type;
    
    private String designContent;

    private ReportTable reportTable;

    private RelativePeriods relatives;

    private ReportParams reportParams;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Report()
    {
    }

    public Report( String name, String type, String designContent, ReportTable reportTable )
    {
        this.name = name;
        this.designContent = designContent;
        this.reportTable = reportTable;
    }
    
    public Report( String name, String type, String designContent, RelativePeriods relatives, ReportParams reportParams )
    {
        this.name = name;
        this.designContent = designContent;
        this.relatives = relatives;
        this.reportParams = reportParams;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isTypeReportTable()
    {
        return type != null && TYPE_JASPER_REPORT_TABLE.equals( type );
    }
    
    public boolean isTypeJdbc()
    {
        return type != null && TYPE_JASPER_JDBC.equals( type );
    }
    
    public boolean isTypeHtml()
    {
        return type != null && TYPE_HTML.equals( type );
    }
        
    public boolean hasReportTable()
    {
        return reportTable != null;
    }

    /**
     * Indicates whether this report has relative periods.
     */
    public boolean hasRelativePeriods()
    {
        return relatives != null && !relatives.isEmpty();
    }
    
    /**
     * Indicates whether this report has report parameters set.
     */
    public boolean hasReportParams()
    {
        return reportParams != null && reportParams.isSet();
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @Override
    public boolean haveUniqueNames()
    {
        return false;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDesignContent()
    {
        return designContent;
    }

    public void setDesignContent( String designContent )
    {
        this.designContent = designContent;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ReportTable getReportTable()
    {
        return reportTable;
    }

    public void setReportTable( ReportTable reportTable )
    {
        this.reportTable = reportTable;
    }

    @JsonProperty( value = "relativePeriods" )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public RelativePeriods getRelatives()
    {
        return relatives;
    }

    public void setRelatives( RelativePeriods relatives )
    {
        this.relatives = relatives;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ReportParams getReportParams()
    {
        return reportParams;
    }

    public void setReportParams( ReportParams reportParams )
    {
        this.reportParams = reportParams;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            Report report = (Report) other;

            designContent = report.getDesignContent() == null ? designContent : report.getDesignContent();
            reportTable = report.getReportTable() == null ? reportTable : report.getReportTable();
        }
    }
}
