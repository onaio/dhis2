package org.hisp.dhis.sms.outbound;

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

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.SmsPublisher;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.ClickatellGatewayConfig;
import org.hisp.dhis.sms.config.GateWayFactory;
import org.hisp.dhis.sms.config.GenericHttpGatewayConfig;
import org.hisp.dhis.sms.config.SMPPGatewayConfig;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.Message.MessageEncodings;
import org.smslib.Service.ServiceStatus;

public class DefaultOutboundSmsTransportService
    implements OutboundSmsTransportService
{
    private static final Log log = LogFactory.getLog( DefaultOutboundSmsTransportService.class );

    private final String BULK_GATEWAY = "bulk_gw";

    private final String CLICKATELL_GATEWAY = "clickatell_gw";

    private final String HTTP_GATEWAY = "generic_http_gw";

    private final String MODEM_GATEWAY = "modem_gw";

    private final String SMPP_GATEWAY = "smpp_gw";

    public static Map<String, String> gatewayMap = new HashMap<String, String>();

    private GateWayFactory gatewayFactory = new GateWayFactory();

    private SmsConfiguration config;

    private String message = "success";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IInboundMessageNotification smppInboundMessageNotification;

    private OutboundSmsService outboundSmsService;

    private SmsPublisher smsPublisher;

    @Override
    public Map<String, String> getGatewayMap()
    {
        if ( gatewayMap == null || gatewayMap.isEmpty() )
        {
            reloadConfig();
        }
        return gatewayMap;
    }

    @Override
    public void stopService()
    {
        message = "success";

        try
        {
            getService().stopService();
            smsPublisher.stop();
        }
        catch ( SMSLibException e )
        {
            message = "Unable to stop smsLib service " + e.getCause().getMessage();
            log.warn( "Unable to stop smsLib service", e );
        }
        catch ( IOException e )
        {
            message = "Unable to stop smsLib service" + e.getCause().getMessage();
            log.warn( "Unable to stop smsLib service", e );
        }
        catch ( InterruptedException e )
        {
            message = "Unable to stop smsLib service" + e.getCause().getMessage();
            log.warn( "Unable to stop smsLib service", e );
        }

    }

    @Override
    public void startService()
    {
        message = "success";

        if ( config != null && config.isEnabled() && (config.getGateways() != null && !config.getGateways().isEmpty()) )
        {
            try
            {
                getService().startService();
                if ( gatewayMap.containsKey( SMPP_GATEWAY ) )
                {
                    getService().setInboundMessageNotification( smppInboundMessageNotification );
                }

                try
                {
                    smsPublisher.start();
                }
                catch ( Exception e1 )
                {
                    message = "Unable to start smsConsumer service " + e1.getMessage();
                    log.warn( "Unable to start smsConsumer service ", e1 );
                }

            }
            catch ( SMSLibException e )
            {
                message = "Unable to start smsLib service " + e.getMessage();
                log.warn( "Unable to start smsLib service", e );
            }
            catch ( IOException e )
            {
                message = "Unable to start smsLib service" + e.getMessage();
                log.warn( "Unable to start smsLib service", e );
            }
            catch ( InterruptedException e )
            {
                message = "Unable to start smsLib service" + e.getMessage();
                log.warn( "Unable to start smsLib service", e );
            }
        }
        else
        {
            message = "sms_unable_or_there_is_no_gateway_service_not_started";
            log.debug( "Sms not enabled or there is no any gateway, won't start service" );
        }

    }

    @Override
    public void reloadConfig()
        throws SmsServiceException
    {
        Service service = Service.getInstance();

        service.setOutboundMessageNotification( new OutboundNotification() );

        service.getGateways().clear();

        AGateway gateway = null;

        message = "success";

        if ( config == null )
        {
            message = "unable_to_load_configure";
        }
        else if ( config.getGateways() == null || config.getGateways().isEmpty() )
        {
            message = "unable_load_configuration_cause_of_there_is_no_gateway";
        }
        else
        {
            for ( SmsGatewayConfig gatewayConfig : config.getGateways() )
            {
                try
                {
                    gateway = gatewayFactory.create( gatewayConfig );

                    service.addGateway( gateway );

                    if ( gatewayConfig instanceof BulkSmsGatewayConfig )
                    {
                        gatewayMap.put( BULK_GATEWAY, gateway.getGatewayId() );
                    }
                    else if ( gatewayConfig instanceof ClickatellGatewayConfig )
                    {
                        gatewayMap.put( CLICKATELL_GATEWAY, gateway.getGatewayId() );
                    }
                    else if ( gatewayConfig instanceof GenericHttpGatewayConfig )
                    {
                        gatewayMap.put( HTTP_GATEWAY, gateway.getGatewayId() );
                    }
                    else if ( gatewayConfig instanceof SMPPGatewayConfig )
                    {
                        gatewayMap.put( SMPP_GATEWAY, gateway.getGatewayId() );
                        // Service.getInstance().setInboundMessageNotification(
                        // new InboundNotification() );
                    }
                    else
                    {
                        gatewayMap.put( MODEM_GATEWAY, gateway.getGatewayId() );
                    }

                    log.debug( "Added gateway " + gatewayConfig.getName() );
                }
                catch ( GatewayException e )
                {
                    log.warn( "Unable to load gateway " + gatewayConfig.getName(), e );
                    message = "Unable to load gateway " + gatewayConfig.getName() + e.getCause().getMessage();
                }
            }
        }

    }

    @Override
    public String getServiceStatus()
    {
        ServiceStatus serviceStatus = getService().getServiceStatus();

        if ( serviceStatus == ServiceStatus.STARTED )
        {
            return "service_started";
        }
        else if ( serviceStatus == ServiceStatus.STARTING )
        {
            return "service_starting";
        }
        else if ( serviceStatus == ServiceStatus.STOPPED )
        {
            return "service_stopped";
        }
        else
        {
            return "service_stopping";
        }
    }

    @Override
    public String getMessageStatus()
    {
        return message;
    }

    @Override
    public String getDefaultGateway()
    {
        if ( config == null )
        {
            return null;
        }

        SmsGatewayConfig gatewayConfig = config.getDefaultGateway();

        if ( gatewayConfig == null )
        {
            return null;
        }

        if ( getGatewayMap() == null )
        {
            return null;
        }

        String gatewayId;

        if ( gatewayConfig instanceof BulkSmsGatewayConfig )
        {
            gatewayId = gatewayMap.get( BULK_GATEWAY );
        }
        else if ( gatewayConfig instanceof ClickatellGatewayConfig )
        {
            gatewayId = gatewayMap.get( CLICKATELL_GATEWAY );
        }
        else if ( gatewayConfig instanceof GenericHttpGatewayConfig )
        {
            gatewayId = gatewayMap.get( HTTP_GATEWAY );
        }
        else if ( gatewayConfig instanceof SMPPGatewayConfig )
        {
            gatewayId = gatewayMap.get( SMPP_GATEWAY );
        }
        else
        {
            gatewayId = gatewayMap.get( MODEM_GATEWAY );
        }

        return gatewayId;
    }

    @Override
    public boolean isEnabled()
    {
        return config != null && config.isEnabled();
    }

    @Override
    public String initialize( SmsConfiguration smsConfiguration )
        throws SmsServiceException
    {
        log.debug( "Initializing SmsLib" );

        this.config = smsConfiguration;

        ServiceStatus status = getService().getServiceStatus();

        if ( status == ServiceStatus.STARTED || status == ServiceStatus.STARTING )
        {
            log.debug( "Stopping SmsLib" );
            stopService();

            if ( message != null && !message.equals( "success" ) )
            {
                return message;
            }
        }

        log.debug( "Loading configuration" );
        reloadConfig();

        if ( message != null && !message.equals( "success" ) )
        {
            return message;
        }

        log.debug( "Starting SmsLib" );
        startService();

        if ( message != null && !message.equals( "success" ) )
        {
            return message;
        }

        return message;
    }

    @Override
    public String sendMessage( OutboundSms sms, String gatewayId )
        throws SmsServiceException
    {
        message = getServiceStatus();

        if ( message != null && (message.equals( "service_stopped" ) || message.equals( "service_stopping" )) )
        {
            return message = "service_stopped_cannot_send_sms";
        }

        String recipient = null;

        Set<String> recipients = sms.getRecipients();

        if ( recipients.size() == 0 )
        {
            log.warn( "Trying to send sms without recipients: " + sms );

            return message = "no_recipient";
        }
        else if ( recipients.size() == 1 )
        {
            recipient = recipients.iterator().next();
        }
        else
        {
            recipient = createTmpGroup( recipients );
        }

        OutboundMessage outboundMessage = new OutboundMessage( recipient, sms.getMessage() );

        // Check if text contain any specific unicode character
        for ( char each : sms.getMessage().toCharArray() )
        {
            if ( !Character.UnicodeBlock.of( each ).equals( UnicodeBlock.BASIC_LATIN ) )
            {
                outboundMessage.setEncoding( MessageEncodings.ENCUCS2 );
                break;
            }
        }

        outboundMessage.setStatusReport( true );

        String longNumber = config.getLongNumber();

        if ( longNumber != null && !longNumber.isEmpty() )
        {
            outboundMessage.setFrom( longNumber );
        }

        boolean sent = false;

        try
        {
            log.info( "Sending message " + sms );

            if ( gatewayId == null || gatewayId.isEmpty() )
            {
                sent = getService().sendMessage( outboundMessage );
            }
            else
            {
                sent = getService().sendMessage( outboundMessage, gatewayId );
            }
        }
        catch ( SMSLibException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            message = "Unable to send message: " + sms + " " + e.getCause().getMessage();
        }
        catch ( IOException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            message = "Unable to send message: " + sms + " " + e.getCause().getMessage();
        }
        catch ( InterruptedException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            message = "Unable to send message: " + sms + " " + e.getCause().getMessage();
        }
        catch ( Exception e )
        {
            log.warn( "Unable to send message: " + sms, e );
            message = "Unable to send message: " + sms + " " + e.getCause().getMessage();
        }
        finally
        {
            if ( recipients.size() > 1 )
            {
                // Make sure we delete "tmp" group
                removeGroup( recipient );
            }
            sms.setStatus( OutboundSmsStatus.ERROR );
        }

        if ( sent )
        {
            message = "success";
            sms.setStatus( OutboundSmsStatus.SENT );
        }
        else
        {
            log.warn( "Message not sent" );
            message = "message_not_sent";
            sms.setStatus( OutboundSmsStatus.ERROR );
        }

        if ( sms.getId() == 0 )
        {
            outboundSmsService.saveOutboundSms( sms );
        }
        else
        {
            outboundSmsService.updateOutboundSms( sms );
        }

        return message;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Service getService()
    {
        return Service.getInstance();
    }

    private class OutboundNotification
        implements IOutboundMessageNotification
    {
        @Override
        public void process( AGateway gateway, OutboundMessage msg )
        {
            log.debug( "Sent message through gateway " + gateway.getGatewayId() + ": " + msg );
        }
    }

    private String createTmpGroup( Set<String> recipients )
    {
        String groupName = Thread.currentThread().getName();

        getService().createGroup( groupName );

        for ( String recepient : recipients )
        {
            getService().addToGroup( groupName, recepient );
        }

        return groupName;
    }

    private void removeGroup( String groupName )
    {
        getService().removeGroup( groupName );
    }

    public IInboundMessageNotification getSmppInboundMessageNotification()
    {
        return smppInboundMessageNotification;
    }

    public void setSmppInboundMessageNotification( IInboundMessageNotification smppInboundMessageNotification )
    {
        this.smppInboundMessageNotification = smppInboundMessageNotification;
    }

    public OutboundSmsService getOutboundSmsService()
    {
        return outboundSmsService;
    }

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    public SmsPublisher getSmsPublisher()
    {
        return smsPublisher;
    }

    public void setSmsPublisher( SmsPublisher smsPublisher )
    {
        this.smsPublisher = smsPublisher;
    }
}
