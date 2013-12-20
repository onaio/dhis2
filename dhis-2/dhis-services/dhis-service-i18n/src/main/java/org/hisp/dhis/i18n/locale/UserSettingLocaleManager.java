package org.hisp.dhis.i18n.locale;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hisp.dhis.i18n.resourcebundle.ResourceBundleManager;
import org.hisp.dhis.i18n.resourcebundle.ResourceBundleManagerException;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: UserSettingLocaleManager.java 6335 2008-11-20 11:11:26Z
 *          larshelg $
 */
public class UserSettingLocaleManager
    implements LocaleManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }
    
    private ResourceBundleManager resourceBundleManager;

    public void setResourceBundleManager( ResourceBundleManager resourceBundleManager )
    {
        this.resourceBundleManager = resourceBundleManager;
    }

    // -------------------------------------------------------------------------
    // LocaleManager implementation
    // -------------------------------------------------------------------------

    public Locale getCurrentLocale()
    {
        Locale locale = getUserSelectedLocale();

        if ( locale != null )
        {
            return locale;
        }

        return DHIS_STANDARD_LOCALE;
    }

    public void setCurrentLocale( Locale locale )
    {
        userSettingService.saveUserSetting( UserSettingService.KEY_UI_LOCALE, locale );
    }

    public List<Locale> getLocalesOrderedByPriority()
    {
        List<Locale> locales = new ArrayList<Locale>();

        Locale userLocale = getUserSelectedLocale();

        if ( userLocale != null )
        {
            locales.add( userLocale );
        }

        locales.add( DHIS_STANDARD_LOCALE );

        return locales;
    }

    private Locale getUserSelectedLocale()
    {
        return (Locale) userSettingService.getUserSetting( UserSettingService.KEY_UI_LOCALE, null );
    }

    public Locale getFallbackLocale()
    {
        return DHIS_STANDARD_LOCALE;
    }
    
    public List<Locale> getAvailableLocales()
    {
        try
        {
            return resourceBundleManager.getAvailableLocales();
        }
        catch ( ResourceBundleManagerException ex )
        {
            throw new RuntimeException( ex );
        }
    }
}
