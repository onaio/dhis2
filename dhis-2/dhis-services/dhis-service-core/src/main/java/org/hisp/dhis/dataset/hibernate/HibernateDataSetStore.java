package org.hisp.dhis.dataset.hibernate;

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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ConversionUtils;

import java.util.Collection;

/**
 * @author Kristian Nordal
 */
public class HibernateDataSetStore
    extends HibernateIdentifiableObjectStore<DataSet>
    implements DataSetStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    @Override
    public int save( DataSet dataSet )
    {
        PeriodType periodType = periodService.reloadPeriodType( dataSet.getPeriodType() );

        dataSet.setPeriodType( periodType );

        return super.save( dataSet );
    }

    @Override
    public void update( DataSet dataSet )
    {
        PeriodType periodType = periodService.reloadPeriodType( dataSet.getPeriodType() );

        dataSet.setPeriodType( periodType );

        super.update( dataSet );
    }

    @SuppressWarnings("unchecked")
    public Collection<DataSet> getDataSetsByPeriodType( PeriodType periodType )
    {
        periodType = periodService.reloadPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class );
        criteria.add( Restrictions.eq( "periodType", periodType ) );

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public Collection<DataSet> getDataSetsBySources( Collection<OrganisationUnit> sources )
    {
        String hql = "select distinct d from DataSet d join d.sources s where s.id in (:ids)";

        return sessionFactory.getCurrentSession().createQuery( hql )
            .setParameterList( "ids", ConversionUtils.getIdentifiers( OrganisationUnit.class, sources ) ).list();
    }

    @SuppressWarnings("unchecked")
    public Collection<DataSet> getDataSetsForMobile( OrganisationUnit source )
    {
        String hql = "from DataSet d where :source in elements(d.sources) and d.mobile = true";
        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setEntity( "source", source );

        return query.list();
    }

    @SuppressWarnings("unchecked")
    public Collection<DataSet> getDataSetsForMobile()
    {
        String hql = "from DataSet d where d.mobile = true";
        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        return query.list();
    }
}
