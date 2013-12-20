package org.hisp.dhis.dd.action.indicator;

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

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hisp.dhis.user.UserSettingService.KEY_CURRENT_DATADICTIONARY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: GetIndicatorListAction.java 5573 2008-08-22 03:39:55Z
 *          ch_bharath1 $
 */
public class GetIndicatorListAction
    extends ActionPagingSupport<Indicator>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    private List<DataDictionary> dataDictionaries;

    public List<DataDictionary> getDataDictionaries()
    {
        return dataDictionaries;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer dataDictionaryId;

    public Integer getDataDictionaryId()
    {
        return dataDictionaryId;
    }

    public void setDataDictionaryId( Integer dataDictionaryId )
    {
        this.dataDictionaryId = dataDictionaryId;
    }

    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Action implemantation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( dataDictionaryId == null ) // None, get current data dictionary
        {
            dataDictionaryId = (Integer) userSettingService.getUserSetting( KEY_CURRENT_DATADICTIONARY );
        }
        else if ( dataDictionaryId == -1 ) // All, reset current data dictionary
        {
            userSettingService.saveUserSetting( KEY_CURRENT_DATADICTIONARY, null );
            
            dataDictionaryId = null;
        }
        else // Specified, set current data dictionary
        {
            userSettingService.saveUserSetting( KEY_CURRENT_DATADICTIONARY, dataDictionaryId );
        }
        
        dataDictionaries = new ArrayList<DataDictionary>( dataDictionaryService.getAllDataDictionaries() );

        Collections.sort( dataDictionaries, IdentifiableObjectNameComparator.INSTANCE );

        // -------------------------------------------------------------------------
        // Criteria
        // -------------------------------------------------------------------------

        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( indicatorService.getIndicatorCountByName( key ) );
            
            indicators = new ArrayList<Indicator>( indicatorService.getIndicatorsBetweenByName( key, paging.getStartPos(), paging.getPageSize() ) );
        }
        else if ( dataDictionaryId != null )
        {
            indicators = new ArrayList<Indicator>( dataDictionaryService.getDataDictionary( dataDictionaryId ).getIndicators() );
            
            this.paging = createPaging( indicators.size() );
            
            indicators = getBlockElement( indicators, paging.getStartPos(), paging.getPageSize() );
        }
        else
        {
            this.paging = createPaging( indicatorService.getIndicatorCount() );
            
            indicators = new ArrayList<Indicator>( indicatorService.getIndicatorsBetween( paging.getStartPos(), paging.getPageSize() ) );
        }
        
        Collections.sort( indicators, new IdentifiableObjectNameComparator() );

        return SUCCESS;
    }
}
