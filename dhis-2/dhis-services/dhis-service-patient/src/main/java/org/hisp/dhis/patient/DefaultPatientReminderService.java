package org.hisp.dhis.patient;

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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.User;

/**
 * @author Chau Thu Tran
 * 
 * @version $ DefaultPatientReminderService.java Aug 7, 2013 9:54:59 AM $
 */
public class DefaultPatientReminderService
    implements PatientReminderService
{

    @Override
    public String getMessageFromTemplate( PatientReminder patientReminder, ProgramInstance programInstance,
        I18nFormat format )
    {
        Patient patient = programInstance.getPatient();
        String templateMessage = patientReminder.getTemplateMessage();

        String patientName = patient.getName();
        String organisationunitName = patient.getOrganisationUnit().getName();
        String programName = programInstance.getProgram().getName();
        String daysSinceEnrollementDate = DateUtils.daysBetween( new Date(), programInstance.getEnrollmentDate() ) + "";
        String daysSinceIncidentDate = DateUtils.daysBetween( new Date(), programInstance.getDateOfIncident() ) + "";
        String incidentDate = format.formatDate( programInstance.getDateOfIncident() );
        String erollmentDate = format.formatDate( programInstance.getEnrollmentDate() );

        if ( patientName != null )
        {
            templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
        }

        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
        templateMessage = templateMessage
            .replace( PatientReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_INCIDENT_DATE, incidentDate );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_ENROLLMENT_DATE, erollmentDate );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_ENROLLMENT_DATE,
            daysSinceEnrollementDate );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_INCIDENT_DATE,
            daysSinceIncidentDate );

        return templateMessage;
    }

    @Override
    public String getMessageFromTemplate( PatientReminder patientReminder, ProgramStageInstance programStageInstance,
        I18nFormat format )
    {
        Patient patient = programStageInstance.getProgramInstance().getPatient();
        String templateMessage = patientReminder.getTemplateMessage();

        String patientName = patient.getName();
        String organisationunitName = patient.getOrganisationUnit().getName();
        String programName = programStageInstance.getProgramInstance().getProgram().getName();
        String programStageName = programStageInstance.getProgramStage().getName();
        String daysSinceDueDate = DateUtils.daysBetween( new Date(), programStageInstance.getDueDate() ) + "";
        String dueDate = format.formatDate( programStageInstance.getDueDate() );

        if ( patientName != null )
        {
            templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
        }
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME,
            programStageName );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_DUE_DATE, dueDate );
        templateMessage = templateMessage
            .replace( PatientReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
        templateMessage = templateMessage.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE,
            daysSinceDueDate );

        return templateMessage;
    }

    @Override
    public Set<String> getPhonenumbers( PatientReminder patientReminder, Patient patient )
    {
        Set<String> phoneNumbers = new HashSet<String>();
        switch ( patientReminder.getSendTo() )
        {
        case PatientReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED:
            Collection<User> users = patient.getOrganisationUnit().getUsers();
            for ( User user : users )
            {
                if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                {
                    phoneNumbers.add( user.getPhoneNumber() );
                }
            }
            break;
        case PatientReminder.SEND_TO_HEALTH_WORKER:
            if ( patient.getHealthWorker() != null && patient.getHealthWorker().getPhoneNumber() != null )
            {
                phoneNumbers.add( patient.getHealthWorker().getPhoneNumber() );
            }
            break;
        case PatientReminder.SEND_TO_ORGUGNIT_REGISTERED:
            if ( patient.getOrganisationUnit().getPhoneNumber() != null
                && !patient.getOrganisationUnit().getPhoneNumber().isEmpty() )
            {
                phoneNumbers.add( patient.getOrganisationUnit().getPhoneNumber() );
            }
            break;
        case PatientReminder.SEND_TO_USER_GROUP:
            for ( User user : patientReminder.getUserGroup().getMembers() )
            {
                if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                {
                    phoneNumbers.add( user.getPhoneNumber() );
                }
            }
            break;
        default:
            if ( patient.getPhoneNumber() != null && !patient.getPhoneNumber().isEmpty() )
            {
                if ( patient.getPhoneNumber().contains( ";" ) )
                {
                    String token[] = patient.getPhoneNumber().split( ";" );
                    for ( String phoneNumber : token )
                    {
                        phoneNumbers.add( phoneNumber );
                    }
                }
                else
                {
                    phoneNumbers.add( patient.getPhoneNumber() );
                }
            }
            break;
        }
        return phoneNumbers;
    }

    public Set<User> getUsers( PatientReminder patientReminder, Patient patient )
    {
        Set<User> users = new HashSet<User>();

        switch ( patientReminder.getSendTo() )
        {
        case PatientReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED:
            users.addAll( patient.getOrganisationUnit().getUsers() );
            break;
        case PatientReminder.SEND_TO_HEALTH_WORKER:
            if ( patient.getHealthWorker() != null && patient.getHealthWorker().getPhoneNumber() != null )
            {
                users.add( patient.getHealthWorker() );
            }
            break;
        case PatientReminder.SEND_TO_USER_GROUP:
            if ( patientReminder.getUserGroup().getMembers().size() > 0 )
            {
                users.addAll( patientReminder.getUserGroup().getMembers() );
            }
            break;
        default:
            break;
        }
        return users;
    }
}
