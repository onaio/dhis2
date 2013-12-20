package org.hisp.dhis.program;

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

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.sms.outbound.OutboundSms;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface ProgramInstanceService
{
    String ID = ProgramInstanceService.class.getName();

    int addProgramInstance( ProgramInstance programInstance );

    void deleteProgramInstance( ProgramInstance programInstance );

    void updateProgramInstance( ProgramInstance programInstance );

    ProgramInstance getProgramInstance( int id );

    ProgramInstance getProgramInstance( String id );

    Collection<ProgramInstance> getAllProgramInstances();

    Collection<ProgramInstance> getProgramInstances( Integer status );

    Collection<ProgramInstance> getProgramInstances( Program program );

    Collection<ProgramInstance> getProgramInstances( Collection<Program> programs );

    Collection<ProgramInstance> getProgramInstances( Collection<Program> programs, OrganisationUnit organisationUnit );

    Collection<ProgramInstance> getProgramInstances( Collection<Program> programs, OrganisationUnit organisationUnit, int status );

    Collection<ProgramInstance> getProgramInstances( Program program, Integer status );

    Collection<ProgramInstance> getProgramInstances( Collection<Program> programs, Integer status );

    Collection<ProgramInstance> getProgramInstances( Patient patient );

    Collection<ProgramInstance> getProgramInstances( Patient patient, Integer status );

    Collection<ProgramInstance> getProgramInstances( Patient patient, Program program );

    Collection<ProgramInstance> getProgramInstances( Patient patient, Program program, Integer status );

    Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit );

    Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit, int min,
        int max );

    Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit,
        Date startDate, Date endDate );

    Collection<ProgramInstance> getProgramInstances( Program program, Collection<Integer> orgunitIds, Date startDate,
        Date endDate, int min, int max );

    int countProgramInstances( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate );

    List<Grid> getProgramInstanceReport( Patient patient, I18n i18n, I18nFormat format );

    Grid getProgramInstanceReport( ProgramInstance programInstance, I18n i18n, I18nFormat format );

    int countProgramInstancesByStatus( Integer status, Program program, Collection<Integer> orgunitIds, Date startDate,
        Date endDate );

    Collection<ProgramInstance> getProgramInstancesByStatus( Integer status, Program program,
        Collection<Integer> orgunitIds, Date startDate, Date endDate );

    void removeProgramEnrollment( ProgramInstance programInstance );

    Collection<SchedulingProgramObject> getScheduleMesssages();

    Collection<OutboundSms> sendMessages( ProgramInstance programInstance, int status, I18nFormat format );

    Collection<MessageConversation> sendMessageConversations( ProgramInstance programInstance,
        int sendWhenToC0mpletedEvent, I18nFormat format );

    ProgramInstance enrollPatient( Patient patient, Program program, Date enrollmentDate, Date dateOfIncident,
        OrganisationUnit orgunit, I18nFormat format );

    boolean canAutoCompleteProgramInstanceStatus( ProgramInstance programInstance );

    void completeProgramInstanceStatus( ProgramInstance programInstance, I18nFormat format );

    /**
     * Set status as skipped for overdue events; Remove scheduled events
     * <p/>
     * *
     */

    void cancelProgramInstanceStatus( ProgramInstance programInstance );
}
