package org.hisp.dhis.dataentryform.hibernate;

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

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.program.ProgramStage;

/**
 * @author Bharath Kumar
 * @version $Id$
 */
public class HibernateDataEntryFormStore
    implements DataEntryFormStore
{
    // ------------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // ------------------------------------------------------------------------
    // DataEntryFormStore implementation
    // ------------------------------------------------------------------------

    public int addDataEntryForm( DataEntryForm dataEntryForm )
    {
        if ( dataEntryForm != null )
        {
            dataEntryForm.setFormat( DataEntryForm.CURRENT_FORMAT );
        }
        
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( dataEntryForm );
    }

    public void updateDataEntryForm( DataEntryForm dataEntryForm )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dataEntryForm );
    }

    public void deleteDataEntryForm( DataEntryForm dataEntryForm )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataEntryForm );
    }

    public DataEntryForm getDataEntryForm( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataEntryForm) session.get( DataEntryForm.class, id );
    }

    public DataEntryForm getDataEntryFormByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryForm.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (DataEntryForm) criteria.uniqueResult();
    }

    public DataEntryForm getDataEntryFormByDataSet( DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryForm.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return (DataEntryForm) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryForm> getAllDataEntryForms()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryForm.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryForm> listDisctinctDataEntryFormByProgramStageIds( List<Integer> programStageIds )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramStage.class );
        criteria.add( Restrictions.in( "id", programStageIds ) ).add( Restrictions.isNotNull( "dataEntryForm" ) );
        criteria.setProjection( Projections.groupProperty( "dataEntryForm" ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryForm> listDisctinctDataEntryFormByDataSetIds( List<Integer> dataSetIds )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class ).add(
            Restrictions.in( "dataEntryForm.id", dataSetIds ) ).setProjection(
            Projections.distinct( Projections.property( "dataEntryForm" ) ) );

        return criteria.list();
    }
}
