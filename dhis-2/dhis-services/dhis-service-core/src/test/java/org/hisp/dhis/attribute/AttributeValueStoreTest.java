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

import org.junit.Test;
import static org.junit.Assert.*;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericStore;

public class AttributeValueStoreTest
    extends DhisSpringTest
{
    private GenericStore<AttributeValue> attributeValueStore;

    private AttributeValue attributeValue1;

    private AttributeValue attributeValue2;

    @Override
    @SuppressWarnings("unchecked")
    protected void setUpTest()
    {
        AttributeStore attributeStore = (AttributeStore) getBean( "org.hisp.dhis.attribute.AttributeStore" );
        attributeValueStore = (GenericStore<AttributeValue>) getBean( "org.hisp.dhis.attribute.AttributeValueStore" );

        Attribute attribute1 = new Attribute();
        attribute1.setName( "attribute_simple" );
        attribute1.setValueType( "string" );

        attributeStore.save( attribute1 );

        attributeValue1 = new AttributeValue( "value 1" );
        attributeValue1.setAttribute( attribute1 );

        attributeValue2 = new AttributeValue( "value 2" );
        attributeValue2.setAttribute( attribute1 );

        attributeValueStore.save( attributeValue1 );
        attributeValueStore.save( attributeValue2 );
    }

    @Test
    public void testGetAttribute()
    {
        AttributeValue av = attributeValueStore.get( attributeValue1.getId() );

        assertNotNull( av );
        assertNotNull( av.getAttribute() );
    }

    @Test
    public void testGetValue()
    {
        AttributeValue av = attributeValueStore.get( attributeValue1.getId() );

        assertNotNull( av );
        assertEquals( "value 1", av.getValue() );
    }
}
