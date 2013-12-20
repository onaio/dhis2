package org.hisp.dhis.sms.hibernate;

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
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.sms.parse.ParserType;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandStore;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class HibernateSMSCommandStore
    implements SMSCommandStore
{
    protected SessionFactory sessionFactory;

    @Required
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<SMSCommand> getSMSCommands()
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( SMSCommand.class );
        criteria.addOrder( Order.asc( "name" ) );
        return criteria.list();
    }

    @Transactional
    public int save( SMSCommand cmd )
    {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate( cmd );
        return 0;
    }

    @Transactional
    public void save( Set<SMSCode> codes )
    {
        Session session = sessionFactory.getCurrentSession();
        
        for ( SMSCode x : codes )
        {
            session.saveOrUpdate( x );
        }
    }

    public SMSCommand getSMSCommand( int id )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( SMSCommand.class );
        criteria.add( Restrictions.eq( "id", id ) );

        if ( criteria.list() != null && criteria.list().size() > 0 )
        {
            return (SMSCommand) criteria.list().get( 0 );
        }

        return null;
    }

    @Transactional
    public void delete( SMSCommand cmd )
    {
        Session session = sessionFactory.getCurrentSession();
        
        for ( SMSCode x : cmd.getCodes() )
        {
            session.delete( x );
        }
        
        session.delete( cmd );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<SMSCommand> getJ2MESMSCommands()
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( SMSCommand.class );
        criteria.add( Restrictions.eq( "parserType", ParserType.J2ME_PARSER ) );
        return criteria.list();
    }

    @Override
    public SMSCommand getSMSCommand( String commandName, ParserType parserType )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( SMSCommand.class );
        criteria.add( Restrictions.eq( "parserType", parserType ) );
        criteria.add( Restrictions.ilike( "name", "%"+commandName+"%") );
        
        if ( criteria.list() != null && criteria.list().size() > 0 )
        {
            return (SMSCommand) criteria.list().get( 0 );
        }

        return null;
    }
}
