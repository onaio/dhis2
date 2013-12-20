package org.hisp.dhis.attribute;

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

import java.util.Set;

/**
 * @author mortenoh
 */
public interface AttributeService
{
    String ID = AttributeService.class.getName();

    // -------------------------------------------------------------------------
    // Attribute
    // -------------------------------------------------------------------------

    void addAttribute( Attribute attribute );

    void updateAttribute( Attribute attribute );

    void deleteAttribute( Attribute attribute );

    Attribute getAttribute( int id );

    Attribute getAttribute( String uid );

    Attribute getAttributeByName( String name );

    Set<Attribute> getAllAttributes();

    Set<Attribute> getDataElementAttributes();

    Set<Attribute> getDataElementGroupAttributes();

    Set<Attribute> getIndicatorAttributes();

    Set<Attribute> getIndicatorGroupAttributes();

    Set<Attribute> getOrganisationUnitAttributes();

    Set<Attribute> getOrganisationUnitGroupAttributes();

    Set<Attribute> getUserAttributes();

    Set<Attribute> getUserGroupAttributes();

    int getAttributeCount();

    int getAttributeCountByName( String name );

    Set<Attribute> getAttributesBetween( int first, int max );

    Set<Attribute> getAttributesBetweenByName( String name, int first, int max );

    // -------------------------------------------------------------------------
    // AttributeValue
    // -------------------------------------------------------------------------

    void addAttributeValue( AttributeValue attributeValue );

    void updateAttributeValue( AttributeValue attributeValue );

    void deleteAttributeValue( AttributeValue attributeValue );

    AttributeValue getAttributeValue( int id );

    Set<AttributeValue> getAllAttributeValues();

    int getAttributeValueCount();
}
