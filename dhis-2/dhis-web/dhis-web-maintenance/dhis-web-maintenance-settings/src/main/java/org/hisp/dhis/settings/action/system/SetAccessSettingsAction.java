package org.hisp.dhis.settings.action.system;

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

import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

import static org.hisp.dhis.setting.SystemSettingManager.KEY_ACCOUNT_RECOVERY;

/**
 * @author Lars Helge Overland
 */
public class SetAccessSettingsAction
    implements Action
{
    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private SystemSettingManager systemSettingManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer selfRegistrationRole;

    public void setSelfRegistrationRole( Integer selfRegistrationRole )
    {
        this.selfRegistrationRole = selfRegistrationRole;
    }

    private Integer selfRegistrationOrgUnit;

    public void setSelfRegistrationOrgUnit( Integer selfRegistrationOrgUnit )
    {
        this.selfRegistrationOrgUnit = selfRegistrationOrgUnit;
    }

    private Boolean accountRecovery;

    public void setAccountRecovery( Boolean accountRecovery )
    {
        this.accountRecovery = accountRecovery;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        UserAuthorityGroup group = null;
        OrganisationUnit unit = null;
        
        if ( selfRegistrationRole != null )
        {
            group = userService.getUserAuthorityGroup( selfRegistrationRole );
        }
        
        if ( selfRegistrationOrgUnit != null )
        {
            unit = organisationUnitService.getOrganisationUnit( selfRegistrationOrgUnit );
        }
        
        Configuration config = configurationService.getConfiguration();
        config.setSelfRegistrationRole( group );
        config.setSelfRegistrationOrgUnit( unit );
        configurationService.setConfiguration( config );

        systemSettingManager.saveSystemSetting( KEY_ACCOUNT_RECOVERY, accountRecovery );
        
        message = i18n.getString( "settings_updated" );

        return SUCCESS;
    }
}
