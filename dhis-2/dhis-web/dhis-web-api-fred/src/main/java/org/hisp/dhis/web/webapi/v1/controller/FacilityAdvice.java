package org.hisp.dhis.web.webapi.v1.controller;

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

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.web.webapi.v1.exception.DuplicateCodeException;
import org.hisp.dhis.web.webapi.v1.exception.DuplicateUidException;
import org.hisp.dhis.web.webapi.v1.exception.DuplicateUuidException;
import org.hisp.dhis.web.webapi.v1.exception.ETagVerificationException;
import org.hisp.dhis.web.webapi.v1.exception.FacilityNotFoundException;
import org.hisp.dhis.web.webapi.v1.exception.UuidFormatException;
import org.hisp.dhis.web.webapi.v1.utils.MessageUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@ControllerAdvice
public class FacilityAdvice
{
    //--------------------------------------------------------------------------
    // EXCEPTION HANDLERS
    //--------------------------------------------------------------------------

    @ExceptionHandler( { HttpClientErrorException.class, HttpServerErrorException.class } )
    public ResponseEntity<String> statusCodeExceptionHandler( HttpStatusCodeException ex ) throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        return new ResponseEntity<String>( MessageUtils.jsonMessage( ex.getStatusText(),
            ex.getMessage() ), headers, ex.getStatusCode() );
    }

    @ExceptionHandler( { DeleteNotAllowedException.class, HierarchyViolationException.class } )
    public ResponseEntity<String> handleForbidden( Exception ex ) throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        return new ResponseEntity<String>( MessageUtils.jsonMessage( HttpStatus.FORBIDDEN.toString(),
            ex.getMessage() ), headers, HttpStatus.FORBIDDEN );
    }

    @ExceptionHandler( { ETagVerificationException.class, UuidFormatException.class } )
    public ResponseEntity<String> handlePreconditionFailed( Exception ex ) throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        return new ResponseEntity<String>( MessageUtils.jsonMessage( HttpStatus.PRECONDITION_FAILED.toString(),
            ex.getMessage() ), headers, HttpStatus.PRECONDITION_FAILED );
    }

    @ExceptionHandler( { FacilityNotFoundException.class } )
    public ResponseEntity<String> handleNotFound( Exception ex ) throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        return new ResponseEntity<String>( MessageUtils.jsonMessage( HttpStatus.NOT_FOUND.toString(),
            ex.getMessage() ), headers, HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler( { DuplicateCodeException.class, DuplicateUidException.class, DuplicateUuidException.class } )
    public ResponseEntity<String> handleConflict( Exception ex ) throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        return new ResponseEntity<String>( MessageUtils.jsonMessage( HttpStatus.CONFLICT.toString(),
            ex.getMessage() ), headers, HttpStatus.CONFLICT );
    }
}
