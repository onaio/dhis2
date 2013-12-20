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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Serializable configuration object for Sms.
 */
@XmlRootElement( name = "smsConfiguration" )
public class SmsConfiguration
    implements Serializable
{
    private static final long serialVersionUID = 7460688383539123303L;

    private boolean enabled = false;

    private String longNumber;

    private Integer pollingInterval;

    private List<SmsGatewayConfig> gateways;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SmsConfiguration()
    {
        this.gateways = new ArrayList<SmsGatewayConfig>();
    }

    public SmsConfiguration( boolean enabled )
    {
        this.enabled = enabled;
        this.gateways = new ArrayList<SmsGatewayConfig>();
    }

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public String getLongNumber()
    {
        return longNumber;
    }

    public void setLongNumber( String longNumber )
    {
        this.longNumber = longNumber;
    }

    @XmlElementWrapper( name = "gateways" )
    @XmlElements( { @XmlElement( name = "bulksms", type = BulkSmsGatewayConfig.class ),
        @XmlElement( name = "clickatell", type = ClickatellGatewayConfig.class ),
        @XmlElement( name = "http", type = GenericHttpGatewayConfig.class ),
        @XmlElement( name = "modem", type = ModemGatewayConfig.class ) } )
    public List<SmsGatewayConfig> getGateways()
    {
        return gateways;
    }

    public void setGateways( List<SmsGatewayConfig> gateways )
    {
        this.gateways = gateways;
    }

    public Integer getPollingInterval()
    {
        return pollingInterval;
    }

    public void setPollingInterval( Integer pollingInterval )
    {
        this.pollingInterval = pollingInterval;
    }
    
    public SmsGatewayConfig getDefaultGateway()
    {
        for ( SmsGatewayConfig gw : gateways )
        {   
            if ( gw.isDefault() )
            {
                return gw;
            }
        }
        return null;
    }
}
