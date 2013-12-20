package org.hisp.dhis.sms.config;

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

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.sms.config.SmsConfigurable;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Manages the {@link SmsConfiguration} for the DHIS instance.
 * <p>
 * The manager looks up all beans implementing {@link SmsConfigurable} in the
 * context, initializing them on startup and on any sms configuration changes.
 * 
 */
public class DefaultSmsConfigurationManager
    implements SmsConfigurationManager
{
    private static final Log log = LogFactory.getLog( DefaultSmsConfigurationManager.class );

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired( required = false )
    private List<SmsConfigurable> smsConfigurables;

    @PostConstruct
    public String initializeSmsConfigurables()
    {
        if ( smsConfigurables == null )
        {
            return null;
        }

        SmsConfiguration smsConfiguration = getSmsConfiguration();

        if ( smsConfiguration == null )
        {
            return null;
        }

        String message = null;

        for ( SmsConfigurable smsConfigurable : smsConfigurables )
        {
            try
            {
                log.debug( "Initialized " + smsConfigurable );

                message = smsConfigurable.initialize( smsConfiguration );

                if ( message != null && !message.equals( "success" ) )
                {
                    return message;
                }
            }
            catch ( Throwable t )
            {
                log.warn( "Unable to initialize service " + smsConfigurable.getClass().getSimpleName()
                    + " with configuration " + smsConfiguration, t );
                return "Unable to initialize service " + smsConfigurable.getClass().getSimpleName()
                    + " with configuration " + smsConfiguration + t.getMessage();
            }
        }

        return message;
    }

    @Override
    public SmsConfiguration getSmsConfiguration()
    {
        return (SmsConfiguration) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_SMS_CONFIG );
    }

    @Override
    public void updateSmsConfiguration( SmsConfiguration config )
    {
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_SMS_CONFIG, config );

        initializeSmsConfigurables();
    }

    @Override
    public SmsGatewayConfig checkInstanceOfGateway( Class<?> clazz )
    {
        if ( getSmsConfiguration() == null )
        {
            SmsConfiguration smsConfig = new SmsConfiguration( true );
            updateSmsConfiguration( smsConfig );
        }
        
        for ( SmsGatewayConfig gateway : getSmsConfiguration().getGateways() )
        {
            if ( gateway.getClass().equals( clazz ) )
            {
                return gateway;
            }
        }
        
        return null;
    }
}
