package org.hisp.dhis.mapping.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.mapping.MapLayer;
import org.hisp.dhis.mapping.MapLayerStore;

import java.util.Collection;

/**
 * @author Jan Henrik Overland
 */
public class HibernateMapLayerStore
    extends HibernateIdentifiableObjectStore<MapLayer>
    implements MapLayerStore
{
    @SuppressWarnings( "unchecked" )
    public Collection<MapLayer> getMapLayersByType( String type )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MapLayer.class );

        criteria.add( Restrictions.eq( "type", type ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<MapLayer> getMapLayersByMapSourceType( String mapSourceType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MapLayer.class );

        criteria.add( Restrictions.eq( "mapSourceType", mapSourceType ) );

        return criteria.list();
    }

    public MapLayer getMapLayerByMapSource( String mapSource )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MapLayer.class );

        criteria.add( Restrictions.eq( "mapSource", mapSource ) );

        return (MapLayer) criteria.uniqueResult();
    }
}
