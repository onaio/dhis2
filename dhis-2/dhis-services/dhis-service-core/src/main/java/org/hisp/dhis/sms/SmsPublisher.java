package org.hisp.dhis.sms;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsListener;
import org.springframework.beans.factory.annotation.Autowired;

public class SmsPublisher
{
    private static final Log log = LogFactory.getLog( SmsPublisher.class );

    private List<IncomingSmsListener> listeners;

    private MessageQueue messageQueue;

    private SMSConsumerThread thread;

    private SmsSender smsSender;

    public void start()
    {
        messageQueue.initialize();

        if ( thread == null )
        {
            thread = new SMSConsumerThread();
            thread.start();
        }
    }

    public void stop()
    {
        if ( thread != null )
        {
            thread.stopFetching();
        }
        
        thread = null;
    }

    private class SMSConsumerThread
        extends Thread
    {
        private boolean stop;

        public SMSConsumerThread()
        {
        }

        public void run()
        {
            while ( !stop )
            {
                try
                {
                    fetchAndParseSMS();
                }
                catch ( Exception ex )
                {
                    log.error( ex.getMessage() );
                }

                try
                {
                    Thread.sleep( 3000 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        }

        private void fetchAndParseSMS()
        {
            IncomingSms message = messageQueue.get();

            while ( message != null )
            {
                log.info( "Received SMS: " + message.getText() );
                try
                {
                    for ( IncomingSmsListener listener : listeners )
                    {
                        if ( listener.accept( message ) )
                        {
                            listener.receive( message );
                            messageQueue.remove( message );
                            return;
                        }
                    }
                    
                    smsSender.sendMessage( "No command found", message.getOriginator() );
                }
                catch ( Exception e )
                {
                    log.error( e );
                    smsSender.sendMessage( e.getMessage(), message.getOriginator() );
                }

                messageQueue.remove( message );
                message = messageQueue.get();
                log.info( "Queue Size: " + messageQueue.get() );
            }
        }

        public void stopFetching()
        {
            this.stop = true;
        }
    }

    public void setMessageQueue( MessageQueue messageQueue )
    {
        this.messageQueue = messageQueue;
    }

    public List<IncomingSmsListener> getListeners()
    {
        return listeners;
    }

    @Autowired
    public void setListeners( List<IncomingSmsListener> listeners )
    {
        this.listeners = listeners;
    }

    public SmsSender getSmsSender()
    {
        return smsSender;
    }

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }
}
