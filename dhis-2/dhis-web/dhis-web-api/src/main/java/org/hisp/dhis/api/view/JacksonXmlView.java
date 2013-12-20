package org.hisp.dhis.api.view;

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


import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonXmlView
    extends AbstractView
{
    private static String CONTENT_TYPE_APPLICATION_XML = "application/xml";

    private static String CONTENT_TYPE_APPLICATION_XML_GZIP = "application/xml+gzip";

    private boolean withCompression = false;

    @Autowired
    private ContextUtils contextUtils;

    public JacksonXmlView()
    {
        setContentType( CONTENT_TYPE_APPLICATION_XML );
    }

    public JacksonXmlView( boolean withCompression )
    {
        this.withCompression = withCompression;

        if ( !withCompression )
        {
            setContentType( CONTENT_TYPE_APPLICATION_XML );
        }
        else
        {
            setContentType( CONTENT_TYPE_APPLICATION_XML_GZIP );
        }
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        Object object = model.get( "model" );
        Class<?> viewClass = JacksonUtils.getViewClass( model.get( "viewClass" ) );
        response.setContentType( getContentType() + "; charset=UTF-8" );
        response.setStatus( HttpServletResponse.SC_OK );

        OutputStream outputStream;

        if ( !withCompression )
        {
            outputStream = response.getOutputStream();
        }
        else
        {
            response.setContentType( CONTENT_TYPE_APPLICATION_XML_GZIP );
            response.addHeader( ContextUtils.HEADER_CONTENT_TRANSFER_ENCODING, "binary" );
            outputStream = new GZIPOutputStream( response.getOutputStream() );
        }

        if ( viewClass != null )
        {
            JacksonUtils.toXmlWithView( outputStream, object, viewClass );
        }
        else
        {
            JacksonUtils.toXml( outputStream, object );
        }
    }
}
