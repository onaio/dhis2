package org.hisp.dhis.visualizer.action;

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

import java.awt.Color;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.svg.PDFTranscoder;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.StreamActionSupport;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */

public class ExportImageAction
    extends StreamActionSupport
{
    private static final Log log = LogFactory.getLog( ExportImageAction.class );

    private static final String TYPE_PNG = "png";

    private static final String TYPE_PDF = "pdf";
    
    // -------------------------------------------------------------------------
    // Output & input
    // -------------------------------------------------------------------------

    private String svg;

    public void setSvg( String svg )
    {
        this.svg = svg;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }
    
    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        if ( svg != null )
        {
            if ( type == null || TYPE_PNG.equals( type ) )
            {
                convertToPNG( new StringBuffer( svg ), out );
            }
            else if ( TYPE_PDF.equals( type ) )
            {
                convertToPDF( new StringBuffer( svg ), out );
            }
        }
        else
        {
            log.info( "svg = " + svg + ", type = " + type );

            return NONE;
        }

        return SUCCESS;
    }

    public void convertToPNG( StringBuffer buffer, OutputStream out )
        throws TranscoderException
    {
        PNGTranscoder t = new PNGTranscoder();

        t.addTranscodingHint( ImageTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE );

        TranscoderInput input = new TranscoderInput( new StringReader( buffer.toString() ) );

        TranscoderOutput output = new TranscoderOutput( out );

        t.transcode( input, output );
    }

    public void convertToPDF( StringBuffer buffer, OutputStream out )
        throws TranscoderException
    {
        PDFTranscoder t = new PDFTranscoder();

        TranscoderInput input = new TranscoderInput( new StringReader( buffer.toString() ) );

        TranscoderOutput output = new TranscoderOutput( out );

        t.transcode( input, output );
    }

    @Override
    protected String getContentType()
    {
        return type.equals( TYPE_PDF ) ? ContextUtils.CONTENT_TYPE_PDF : ContextUtils.CONTENT_TYPE_PNG;
    }

    @Override
    protected String getFilename()
    {
        String t = name != null ? CodecUtils.filenameEncode( name ) : "";

        return "dhis2_chart_" + t + "." + CodecUtils.filenameEncode( type );
    }

    @Override
    protected boolean disallowCache()
    {
        return true;
    }
}
