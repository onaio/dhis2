package org.hisp.dhis.period.hibernate;

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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriods;

/**
 * Implements the PeriodStore interface.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: HibernatePeriodStore.java 5983 2008-10-17 17:42:44Z larshelg $
 */
public class HibernatePeriodStore
    extends HibernateIdentifiableObjectStore<Period>
    implements PeriodStore
{
    // -------------------------------------------------------------------------
    // Period
    // -------------------------------------------------------------------------

    public int addPeriod( Period period )
    {
        period.setPeriodType( reloadPeriodType( period.getPeriodType() ) );

        return save( period );
    }

    public Period getPeriod( Date startDate, Date endDate, PeriodType periodType )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );
        criteria.add( Restrictions.eq( "periodType", reloadPeriodType( periodType ) ) );

        return (Period) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Period> getPeriodsBetweenDates( Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.ge( "startDate", startDate ) );
        criteria.add( Restrictions.le( "endDate", endDate ) );
        criteria.setCacheable( true );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Period> getPeriodsBetweenDates( PeriodType periodType, Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "periodType", reloadPeriodType( periodType ) ) );
        criteria.add( Restrictions.ge( "startDate", startDate ) );
        criteria.add( Restrictions.le( "endDate", endDate ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Period> getPeriodsBetweenOrSpanningDates( Date startDate, Date endDate )
    {
        String hql = "from Period p where ( p.startDate >= :startDate and p.endDate <= :endDate ) or ( p.startDate <= :startDate and p.endDate >= :endDate )";
        
        return getQuery( hql ).setDate( "startDate", startDate ).setDate( "endDate", endDate ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Period> getIntersectingPeriodsByPeriodType( PeriodType periodType, Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "periodType", reloadPeriodType( periodType ) ) );
        criteria.add( Restrictions.ge( "endDate", startDate ) );
        criteria.add( Restrictions.le( "startDate", endDate ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Period> getIntersectingPeriods( Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.ge( "endDate", startDate ) );
        criteria.add( Restrictions.le( "startDate", endDate ) );
        
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Period> getPeriodsByPeriodType( PeriodType periodType )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "periodType", reloadPeriodType( periodType ) ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Period> getPeriods( Period period, Collection<DataElement> dataElements,
        Collection<OrganisationUnit> sources )
    {
        Set<Period> periods = new HashSet<Period>();

        Session session = sessionFactory.getCurrentSession();

        Collection<Period> intersectingPeriods = getIntersectingPeriods( period.getStartDate(), period.getEndDate() );

        if ( intersectingPeriods != null && intersectingPeriods.size() > 0 )
        {
            Criteria criteria = session.createCriteria( DataValue.class );
            criteria.add( Restrictions.in( "dataElement", dataElements ) );
            criteria.add( Restrictions.in( "source", sources ) );
            criteria.add( Restrictions.in( "period", intersectingPeriods ) );

            Collection<DataValue> dataValues = criteria.list();

            for ( DataValue dataValue : dataValues )
            {
                periods.add( dataValue.getPeriod() );
            }
        }

        return periods;
    }

    public Period getPeriodFromDates( Date startDate, Date endDate, PeriodType periodType )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );
        criteria.add( Restrictions.eq( "periodType", periodType ) );

        return (Period) criteria.uniqueResult();
    }

    public Period reloadPeriod( Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        if ( session.contains( period ) )
        {
            return period; // Already in session, no reload needed
        }

        Period storedPeriod = getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
        
        return storedPeriod != null ? storedPeriod.copyTransientProperties( period ) : null;
    }

    public Period reloadForceAddPeriod( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    // -------------------------------------------------------------------------
    // PeriodType (do not use generic store which is linked to Period)
    // -------------------------------------------------------------------------

    public int addPeriodType( PeriodType periodType )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( periodType );
    }

    public void deletePeriodType( PeriodType periodType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( periodType );
    }

    public PeriodType getPeriodType( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (PeriodType) session.get( PeriodType.class, id );
    }

    public PeriodType getPeriodType( Class<? extends PeriodType> periodType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( periodType );

        return (PeriodType) criteria.setCacheable( true ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PeriodType> getAllPeriodTypes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( PeriodType.class ).setCacheable( true ).list();
    }

    public PeriodType reloadPeriodType( PeriodType periodType )
    {
        Session session = sessionFactory.getCurrentSession();

        if ( periodType == null || session.contains( periodType ) )
        {
            return periodType;
        }

        PeriodType reloadedPeriodType = getPeriodType( periodType.getClass() );

        if ( reloadedPeriodType == null )
        {
            throw new IllegalArgumentException( "The PeriodType referenced by the Period is not in database: "
                + periodType.getName() );
        }

        return reloadedPeriodType;
    }

    // -------------------------------------------------------------------------
    // RelativePeriods (do not use generic store which is linked to Period)
    // -------------------------------------------------------------------------

    public void deleteRelativePeriods( RelativePeriods relativePeriods )
    {
        sessionFactory.getCurrentSession().delete( relativePeriods );
    }
}
