package org.hisp.dhis.mobile.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.mobile.SmsService;

public class SendSMSAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    SmsService smsService;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    boolean smsServiceStatus;

    public boolean getSmsServiceStatus()
    {
        smsServiceStatus = smsService.getServiceStatus();
        return smsServiceStatus;
    }

    String statAction;

    public void setStatAction( String statAction )
    {
        if ( statAction.equalsIgnoreCase( "Start" ) )
        {
            this.message = smsService.startService();
        } else
        {
            this.message = smsService.stopService();
        }
    }

    String recipient;

    public void setRecipient( String recipient )
    {
        this.recipient = recipient;
    }

    String msg;

    public void setMsg( String msg )
    {
        this.msg = msg;
    }

    String send;

    public void setSend( String send )
    {
        this.send = send;
    }

    String message = "";

    public String getMessage()
    {
        return message;
    }

    @Override
    public String execute()
        throws Exception
    {
        if ( this.send != null )
        {
            this.message = smsService.sendMessage( recipient, msg );
        }
        return SUCCESS;
    }
}
