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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientreport.TabularReportColumn;
import org.hisp.dhis.sms.outbound.OutboundSms;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface ProgramStageInstanceService
{
    String ID = ProgramStageInstanceService.class.getName();

    int addProgramStageInstance( ProgramStageInstance programStageInstance );

    void deleteProgramStageInstance( ProgramStageInstance programStageInstance );

    void updateProgramStageInstance( ProgramStageInstance programStageInstance );

    ProgramStageInstance getProgramStageInstance( int id );

    ProgramStageInstance getProgramStageInstance( String uid );

    ProgramStageInstance getProgramStageInstance( ProgramInstance programInstance, ProgramStage programStage );

    Collection<ProgramStageInstance> getProgramStageInstances( ProgramInstance programInstance, ProgramStage programStage );

    Collection<ProgramStageInstance> getProgramStageInstances( ProgramStage programStage );

    Collection<ProgramStageInstance> getProgramStageInstances( ProgramStage programStage,
        OrganisationUnit organisationUnit );

    Collection<ProgramStageInstance> getProgramStageInstances( ProgramStage programStage,
        OrganisationUnit organisationUnit, Date start, Date end );

    Collection<ProgramStageInstance> getProgramStageInstances( Collection<ProgramInstance> programInstances );

    Collection<ProgramStageInstance> getProgramStageInstances( Collection<ProgramInstance> programInstances,
        boolean completed );

    Collection<ProgramStageInstance> getProgramStageInstances( Date dueDate );

    Collection<ProgramStageInstance> getProgramStageInstances( Date dueDate, Boolean completed );

    Collection<ProgramStageInstance> getProgramStageInstances( Date startDate, Date endDate );

    Collection<ProgramStageInstance> getProgramStageInstances( Date startDate, Date endDate, Boolean completed );

    Collection<ProgramStageInstance> getAllProgramStageInstances();

    Map<Integer, Integer> statusProgramStageInstances( Collection<ProgramStageInstance> programStageInstances );

    /**
     * Get all {@link ProgramStageInstance program stage instances} for unit,
     * optionally filtering by date or completed.
     *
     * @param unit      - the unit to get instances for.
     * @param after     - optional date the instance should be on or after.
     * @param before    - optional date the instance should be on or before.
     * @param completed - optional flag to only get completed (<code>true</code>
     *                  ) or uncompleted (<code>false</code>) instances.
     * @return
     */
    List<ProgramStageInstance> get( OrganisationUnit unit, Date after, Date before, Boolean completed );

    List<ProgramStageInstance> getProgramStageInstances( Patient patient, Boolean completed );

    Grid getTabularReport( Boolean anonynousEntryForm, ProgramStage programStage, List<TabularReportColumn> columns,
        Collection<Integer> organisationUnits, int level, Date startDate, Date endDate, boolean descOrder,
        Boolean completed, Boolean accessPrivateInfo, Boolean displayOrgunitCode, Integer min, Integer max, I18n i18n );

    int getTabularReportCount( Boolean anonynousEntryForm, ProgramStage programStage,
        List<TabularReportColumn> columns, Collection<Integer> organisationUnits, int level, Boolean completed,
        Date startDate, Date endDate );

    List<Grid> getProgramStageInstancesReport( ProgramInstance programInstance, I18nFormat format, I18n i18n );

    void removeEmptyEvents( ProgramStage programStage, OrganisationUnit organisationUnit );

    void updateProgramStageInstances( Collection<Integer> programStageInstances, OutboundSms outboundSms );

    Collection<SchedulingProgramObject> getSendMesssageEvents();

    Grid getStatisticalReport( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        I18n i18n, I18nFormat format );

    List<ProgramStageInstance> getStatisticalProgramStageDetailsReport( ProgramStage programStage,
        Collection<Integer> orgunitIds, Date startDate, Date endDate, int status, Integer max, Integer min );
    
    // -------------------------------------------------------------------------
    // Statistical
    // -------------------------------------------------------------------------

    Collection<ProgramStageInstance> getProgramStageInstances( Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, Boolean completed );

    int getOverDueEventCount( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate, Date endDate );

    int averageNumberCompletedProgramInstance( Program program, Collection<Integer> orgunitIds, Date startDate,
        Date endDate, Integer status );

    Collection<Integer> getOrganisationUnitIds( Date startDate, Date endDate );

    Grid getCompletenessProgramStageInstance( Collection<Integer> orgunits, Program program, String startDate,
        String endDate, I18n i18n );

    Collection<OutboundSms> sendMessages( ProgramStageInstance programStageInstance, int status, I18nFormat format );

    Collection<MessageConversation> sendMessageConversations( ProgramStageInstance programStageInstance, int status,
        I18nFormat format );

    void completeProgramStageInstance( ProgramStageInstance programStageInstance, I18nFormat format );
}
