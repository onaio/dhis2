package org.hisp.dhis.validation.hibernate;

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
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleStore;

/**
 * @author Chau Thu Tran
 * @version HibernateValidationRuleStore.java May 19, 2010 1:48:44 PM
 */

public class HibernateValidationRuleStore
    extends HibernateIdentifiableObjectStore<ValidationRule>
    implements ValidationRuleStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public int save( ValidationRule validationRule )
    {
        PeriodType periodType = periodService.reloadPeriodType( validationRule.getPeriodType() );

        validationRule.setPeriodType( periodType );

        return super.save( validationRule );
    }
    
    @Override
    public void update( ValidationRule validationRule )
    {
        PeriodType periodType = periodService.reloadPeriodType( validationRule.getPeriodType() );

        validationRule.setPeriodType( periodType );

        super.save( validationRule );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidationRule> getValidationRulesByDataElements( Collection<DataElement> dataElements )
    {
        Set<ValidationRule> validationRules = new HashSet<ValidationRule>();
        
        Collection<Integer> ids = ConversionUtils.getIdentifiers( DataElement.class, dataElements );
        
        String hql = "select distinct v from ValidationRule v join v.leftSide ls join ls.dataElementsInExpression lsd where lsd.id in (:ids)";
        
        validationRules.addAll( sessionFactory.getCurrentSession().createQuery( hql ).setParameterList( "ids", ids ).list() );
        
        hql = "select distinct v from ValidationRule v join v.rightSide rs join rs.dataElementsInExpression rsd where rsd.id in (:ids)";

        validationRules.addAll( getQuery( hql ).setParameterList( "ids", ids ).list() );
        
        return validationRules;
    }
}
