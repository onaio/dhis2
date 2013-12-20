package org.hisp.dhis.mobile.service;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.PatientMobileSettingService;
import org.hisp.dhis.api.mobile.model.Activity;
import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.api.mobile.model.ActivityValue;
import org.hisp.dhis.api.mobile.model.Beneficiary;
import org.hisp.dhis.api.mobile.model.DataValue;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.Task;
import org.hisp.dhis.api.mobile.model.LWUITmodel.LostEvent;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Notification;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Section;
import org.hisp.dhis.api.mobile.model.comparator.ActivityComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.message.Message;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.util.PatientIdentifierGenerator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class ActivityReportingServiceImpl
    implements ActivityReportingService
{
    private static final String PROGRAM_STAGE_UPLOADED = "program_stage_uploaded";

    private static final String PROGRAM_STAGE_SECTION_UPLOADED = "program_stage_section_uploaded";

    private static final String SINGLE_EVENT_UPLOADED = "single_event_uploaded";

    private ActivityComparator activityComparator = new ActivityComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    private PatientService patientService;

    private PatientAttributeValueService patientAttValueService;

    private PatientAttributeService patientAttService;

    private PatientDataValueService dataValueService;

    private PatientMobileSettingService patientMobileSettingService;

    private PatientIdentifierService patientIdentifierService;

    private ProgramStageSectionService programStageSectionService;

    private ProgramInstanceService programInstanceService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    private DataElementService dataElementService;

    private PatientDataValueService patientDataValueService;

    private ProgramService programService;

    private ProgramStageService programStageService;

    private org.hisp.dhis.mobile.service.ModelMapping modelMapping;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    public PatientAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private CurrentUserService currentUserService;

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private MessageService messageService;
    
    @Required
    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    private SmsSender smsSender;

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }
    
    public PatientIdentifierTypeService getPatientIdentifierTypeService()
    {
        return patientIdentifierTypeService;
    }

    private Collection<PatientIdentifier> patientIdentifiers;

    public Collection<PatientIdentifier> getPatientIdentifiers()
    {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers( Collection<PatientIdentifier> patientIdentifiers )
    {
        this.patientIdentifiers = patientIdentifiers;
    }

    private Collection<PatientIdentifierType> patientIdentifierTypes;

    public Collection<PatientIdentifierType> getPatientIdentifierTypes()
    {
        return patientIdentifierTypes;
    }

    public void setPatientIdentifierTypes( Collection<PatientIdentifierType> patientIdentifierTypes )
    {
        this.patientIdentifierTypes = patientIdentifierTypes;
    }

    private Collection<org.hisp.dhis.patient.PatientAttribute> patientAttributes;

    public Collection<org.hisp.dhis.patient.PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public void setPatientAttributes( Collection<org.hisp.dhis.patient.PatientAttribute> patientAttributes )
    {
        this.patientAttributes = patientAttributes;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    @Required
    public void setProgramStageInstanceService(
        org.hisp.dhis.program.ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    @Required
    public void setPatientAttValueService( PatientAttributeValueService patientAttValueService )
    {
        this.patientAttValueService = patientAttValueService;
    }

    @Required
    public void setPatientAttService( PatientAttributeService patientAttService )
    {
        this.patientAttService = patientAttService;
    }

    @Required
    public void setDataValueService( org.hisp.dhis.patientdatavalue.PatientDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

    @Required
    public void setModelMapping( org.hisp.dhis.mobile.service.ModelMapping modelMapping )
    {
        this.modelMapping = modelMapping;
    }

    public PatientMobileSetting getSetting()
    {
        return setting;
    }

    public void setSetting( PatientMobileSetting setting )
    {
        this.setting = setting;
    }

    public org.hisp.dhis.patient.PatientAttribute getGroupByAttribute()
    {
        return groupByAttribute;
    }

    public void setGroupByAttribute( org.hisp.dhis.patient.PatientAttribute groupByAttribute )
    {
        this.groupByAttribute = groupByAttribute;
    }

    @Required
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    @Required
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    @Required
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    @Required
    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Required
    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    @Required
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    @Required
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    @Required
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------

    private PatientMobileSetting setting;

    private org.hisp.dhis.patient.PatientAttribute groupByAttribute;

    @Override
    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString )
    {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, 30 );

        long upperBound = cal.getTime().getTime();

        cal.add( Calendar.DATE, -60 );
        long lowerBound = cal.getTime().getTime();

        List<Activity> items = new ArrayList<Activity>();
        Collection<Patient> patients = patientService.getPatients( unit, 0, Integer.MAX_VALUE );

        for ( Patient patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                if ( programStageInstance.getDueDate().getTime() >= lowerBound
                    && programStageInstance.getDueDate().getTime() <= upperBound )
                {
                    items.add( getActivity( programStageInstance, false ) );
                }
            }
        }

        this.setGroupByAttribute( patientAttService.getPatientAttributeByGroupBy( true ) );

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );

        return new ActivityPlan( items );
    }

    @Override
    public ActivityPlan getAllActivityPlan( OrganisationUnit unit, String localeString )
    {

        List<Activity> items = new ArrayList<Activity>();
        Collection<Patient> patients = patientService.getPatients( unit, 0, Integer.MAX_VALUE );

        for ( Patient patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                items.add( getActivity( programStageInstance, false ) );
            }
        }

        this.setGroupByAttribute( patientAttService.getPatientAttributeByGroupBy( true ) );

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );
        return new ActivityPlan( items );
    }

    @Override
    public ActivityPlan getActivitiesByIdentifier( String keyword )
        throws NotAllowedException
    {

        long time = PeriodType.createCalendarInstance().getTime().getTime();

        Calendar expiredDate = Calendar.getInstance();

        List<Activity> items = new ArrayList<Activity>();

        Collection<Patient> patients = patientIdentifierService.getPatientsByIdentifier( keyword, 0,
            patientIdentifierService.countGetPatientsByIdentifier( keyword ) );

        // Make sure user input full beneficiary identifier number

        if ( patients.size() > 1 )
        {
            throw NotAllowedException.NEED_MORE_SPECIFIC;
        }
        else if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }
        else
        {
            Iterator<Patient> iterator = patients.iterator();

            while ( iterator.hasNext() )
            {
                Patient patient = iterator.next();

                List<ProgramStageInstance> programStageInstances = programStageInstanceService
                    .getProgramStageInstances( patient, false );

                for ( int i = 0; i < programStageInstances.size(); i++ )
                {
                    ProgramStageInstance programStageInstance = programStageInstances.get( i );

                    // expiredDate.setTime( DateUtils.getDateAfterAddition(
                    // programStageInstance.getDueDate(), 0 ) );
                    expiredDate.setTime( DateUtils.getDateAfterAddition( programStageInstance.getDueDate(), 30 ) );

                    if ( programStageInstance.getDueDate().getTime() <= time && expiredDate.getTimeInMillis() > time )
                    {
                        items.add( getActivity( programStageInstance,
                            programStageInstance.getDueDate().getTime() < time ) );
                    }
                }
            }

            return new ActivityPlan( items );
        }

    }

    // -------------------------------------------------------------------------
    // DataValueService
    // -------------------------------------------------------------------------

    @Override
    public void saveActivityReport( OrganisationUnit unit, ActivityValue activityValue, Integer programStageSectionId )
        throws NotAllowedException
    {

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( activityValue
            .getProgramInstanceId() );
        if ( programStageInstance == null )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        programStageInstance.getProgramStage();
        Collection<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>();

        ProgramStageSection programStageSection = programStageSectionService
            .getProgramStageSection( programStageSectionId );

        if ( programStageSectionId != 0 )
        {
            for ( ProgramStageDataElement de : programStageSection.getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }
        else
        {
            for ( ProgramStageDataElement de : programStageInstance.getProgramStage().getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }

        programStageInstance.getProgramStage().getProgramStageDataElements();
        Collection<Integer> dataElementIds = new ArrayList<Integer>( activityValue.getDataValues().size() );

        for ( DataValue dv : activityValue.getDataValues() )
        {
            dataElementIds.add( dv.getId() );
        }

        if ( dataElements.size() != dataElementIds.size() )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            if ( !dataElementIds.contains( dataElement.getId() ) )
            {
                throw NotAllowedException.INVALID_PROGRAM_STAGE;
            }
            dataElementMap.put( dataElement.getId(), dataElement );
        }

        // Set ProgramStageInstance to completed
        if ( programStageSectionId == 0 )
        {
            programStageInstance.setCompleted( true );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
        }

        // Everything is fine, hence save
        saveDataValues( activityValue, programStageInstance, dataElementMap );

    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatient( String keyword, int orgUnitId )
        throws NotAllowedException
    {
        if ( isNumber( keyword ) == false )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
            
            List<Patient> patients = (List<Patient>) this.patientService.getPatientByFullname( keyword, organisationUnit );

            if ( patients.size() > 1 )
            {
                String patientsInfo = new String();

                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

                for ( Patient each : patients )
                {
                    patientsInfo += each.getId() + "/" + each.getName() + "/" + dateFormat.format( each.getBirthDate() )
                        + "$";
                }

                throw new NotAllowedException( patientsInfo );
            }
            else if ( patients.size() == 0 )
            {
                throw NotAllowedException.NO_BENEFICIARY_FOUND;
            }
            else
            {
                org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patients.get( 0 ) );

                return patientMobile;
            }
        }
        else
        {
            Patient patient = patientService.getPatient( Integer.parseInt( keyword ) );

            org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );

            return patientMobile;
        }

    }

    @Override
    public String saveProgramStage( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage,
        int patientId, int orgUnitId )
        throws NotAllowedException
    {
        if ( mobileProgramStage.isSingleEvent() )
        {
            Patient patient = patientService.getPatient( patientId );
            ProgramStageInstance prStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );
            ProgramStage programStage = programStageService.getProgramStage( prStageInstance.getProgramStage().getId() );
            // ProgramStage programStage = programStageService.getProgramStage(
            // mobileProgramStage.getId() );
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

            // ---------------------------------------------------------------------
            // Add a new program-instance
            // ---------------------------------------------------------------------
            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( new Date() );
            programInstance.setDateOfIncident( new Date() );
            programInstance.setProgram( programStage.getProgram() );
            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstance.setPatient( patient );

            programInstanceService.addProgramInstance( programInstance );

            // ---------------------------------------------------------------------
            // Add a new program-stage-instance
            // ---------------------------------------------------------------------

            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( new Date() );
            programStageInstance.setExecutionDate( new Date() );
            programStageInstance.setOrganisationUnit( organisationUnit );
            programStageInstance.setCompleted( true );
            programStageInstanceService.addProgramStageInstance( programStageInstance );

            // ---------------------------------------------------------------------
            // Save value
            // ---------------------------------------------------------------------

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );

                String value = dataElements.get( i ).getValue();

                if ( dataElement.getType().equalsIgnoreCase( "date" ) && !value.trim().equals( "" ) )
                {
                    value = PeriodUtil.convertDateFormat( value );
                }

                PatientDataValue patientDataValue = new PatientDataValue();
                patientDataValue.setDataElement( dataElement );

                patientDataValue.setValue( value );
                patientDataValue.setProgramStageInstance( programStageInstance );
                patientDataValue.setTimestamp( new Date() );
                patientDataValueService.savePatientDataValue( patientDataValue );

            }

            return SINGLE_EVENT_UPLOADED;

        }
        else
        {
            ProgramStageInstance programStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );

            /*
             * //Begin Changes ProgramStage programStage =
             * programStageService.getProgramStage( mobileProgramStage.getId()
             * ); Patient patient = patientService.getPatient( patientId );
             * Program program = programStage.getProgram();
             * 
             * Collection<ProgramInstance> programInstances =
             * programInstanceService.getProgramInstances( patient, program );
             * ProgramStageInstance programStageInstance = null; for (
             * ProgramInstance each : programInstances ) { if(
             * each.getStatus()==ProgramInstance.STATUS_ACTIVE ) {
             * programStageInstance =
             * programStageInstanceService.getProgramStageInstance( each,
             * programStage ); break; } }
             * 
             * //End Changes
             */
            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );
                String value = dataElements.get( i ).getValue();

                if ( dataElement.getType().equalsIgnoreCase( "date" ) && !value.trim().equals( "" ) )
                {
                    value = PeriodUtil.convertDateFormat( value );
                }

                PatientDataValue previousPatientDataValue = patientDataValueService.getPatientDataValue(
                    programStageInstance, dataElement );

                if ( previousPatientDataValue == null )
                {
                    PatientDataValue patientDataValue = new PatientDataValue( programStageInstance, dataElement,
                        new Date(), value );
                    patientDataValueService.savePatientDataValue( patientDataValue );
                }
                else
                {
                    previousPatientDataValue.setValue( value );
                    previousPatientDataValue.setTimestamp( new Date() );
                    previousPatientDataValue.setProvidedElsewhere( false );
                    patientDataValueService.updatePatientDataValue( previousPatientDataValue );
                }

            }

            if ( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) != null )
            {
                programStageInstance.setExecutionDate( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) );
            }
            else
            {
                programStageInstance.setExecutionDate( new Date() );
            }

            if ( programStageInstance.getProgramStage().getProgramStageDataElements().size() > dataElements.size() )
            {
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
                return PROGRAM_STAGE_SECTION_UPLOADED;
            }
            else
            {
                programStageInstance.setCompleted( mobileProgramStage.isCompleted() );

                // check if any compulsory value is null
                for ( int i = 0; i < dataElements.size(); i++ )
                {
                    if ( dataElements.get( i ).isCompulsory() == true )
                    {
                        if ( dataElements.get( i ).getValue() == null )
                        {
                            programStageInstance.setCompleted( false );
                            // break;
                            throw NotAllowedException.INVALID_PROGRAM_STAGE;
                        }
                    }
                }
                programStageInstanceService.updateProgramStageInstance( programStageInstance );

                // check if all belonged program stage are completed
                if ( isAllProgramStageFinished( programStageInstance ) == true )
                {
                    ProgramInstance programInstance = programStageInstance.getProgramInstance();
                    programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
                    programInstanceService.updateProgramInstance( programInstance );
                }
                return PROGRAM_STAGE_UPLOADED;
            }
        }
    }

    private boolean isAllProgramStageFinished( ProgramStageInstance programStageInstance )
    {
        ProgramInstance programInstance = programStageInstance.getProgramInstance();
        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();
        if ( programStageInstances != null )
        {
            Iterator<ProgramStageInstance> iterator = programStageInstances.iterator();

            while ( iterator.hasNext() )
            {
                ProgramStageInstance each = iterator.next();
                if ( !each.isCompleted() )
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient enrollProgram( String enrollInfo )
        throws NotAllowedException
    {
        String[] enrollProgramInfo = enrollInfo.split( "-" );
        int patientId = Integer.parseInt( enrollProgramInfo[0] );
        int programId = Integer.parseInt( enrollProgramInfo[1] );

        Patient patient = patientService.getPatient( patientId );
        Program program = programService.getProgram( programId );

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setEnrollmentDate( new Date() );
        programInstance.setDateOfIncident( new Date() );
        programInstance.setProgram( program );
        programInstance.setPatient( patient );
        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
        programInstanceService.addProgramInstance( programInstance );
        for ( ProgramStage programStage : program.getProgramStages() )
        {
            if ( programStage.getAutoGenerateEvent() )
            {
                ProgramStageInstance programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                Date dateCreatedEvent = new Date();
                if ( programStage.getGeneratedByEnrollmentDate() )
                {
                    // dateCreatedEvent = sdf.parseDateTime( enrollmentDate
                    // ).toDate();
                }
                Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                programStageInstance.setDueDate( dueDate );

                if ( program.isSingleEvent() )
                {
                    programStageInstance.setExecutionDate( dueDate );
                }

                programStageInstanceService.addProgramStageInstance( programStageInstance );

            }

        }
        return getPatientModel( patient );
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private Activity getActivity( ProgramStageInstance instance, boolean late )
    {

        Activity activity = new Activity();
        Patient patient = instance.getProgramInstance().getPatient();

        activity.setBeneficiary( getBeneficiaryModel( patient ) );
        activity.setDueDate( instance.getDueDate() );
        activity.setTask( getTask( instance ) );
        activity.setLate( late );
        activity.setExpireDate( DateUtils.getDateAfterAddition( instance.getDueDate(), 30 ) );

        return activity;
    }

    private Task getTask( ProgramStageInstance instance )
    {
        if ( instance == null )
            return null;

        Task task = new Task();
        task.setCompleted( instance.isCompleted() );
        task.setId( instance.getId() );
        task.setProgramStageId( instance.getProgramStage().getId() );
        task.setProgramId( instance.getProgramInstance().getProgram().getId() );
        return task;
    }

    private Beneficiary getBeneficiaryModel( Patient patient )
    {
        Beneficiary beneficiary = new Beneficiary();
        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();
        List<org.hisp.dhis.patient.PatientAttribute> atts;

        beneficiary.setId( patient.getId() );
        beneficiary.setName( patient.getName() );

        Period period = new Period( new DateTime( patient.getBirthDate() ), new DateTime() );
        beneficiary.setAge( period.getYears() );

        this.setSetting( getSettings() );

        if ( setting != null )
        {
            if ( setting.getGender() )
            {
                beneficiary.setGender( patient.getGender() );
            }
            if ( setting.getDobtype() )
            {
                beneficiary.setDobType( patient.getDobType() );
            }
            if ( setting.getBirthdate() )
            {
                beneficiary.setBirthDate( patient.getBirthDate() );
            }
            if ( setting.getRegistrationdate() )
            {
                beneficiary.setRegistrationDate( patient.getRegistrationDate() );
            }

            atts = setting.getPatientAttributes();
            for ( org.hisp.dhis.patient.PatientAttribute each : atts )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, each );
                if ( value != null )
                {
                    patientAtts.add( new PatientAttribute( each.getName(), value.getValue(), each.getValueType(),
                        new ArrayList<String>() ) );
                }
            }

        }

        // Set attribute which is used to group beneficiary on mobile (only if
        // there is attribute which is set to be group factor)
        PatientAttribute beneficiaryAttribute = null;

        if ( groupByAttribute != null )
        {
            beneficiaryAttribute = new PatientAttribute();
            beneficiaryAttribute.setName( groupByAttribute.getName() );
            PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, groupByAttribute );
            beneficiaryAttribute.setValue( value == null ? "Unknown" : value.getValue() );
            beneficiary.setGroupAttribute( beneficiaryAttribute );
        }

        // Set all identifier
        Set<PatientIdentifier> patientIdentifiers = patient.getIdentifiers();
        List<org.hisp.dhis.api.mobile.model.PatientIdentifier> identifiers = new ArrayList<org.hisp.dhis.api.mobile.model.PatientIdentifier>();
        if ( patientIdentifiers.size() > 0 )
        {

            for ( PatientIdentifier id : patientIdentifiers )
            {

                String idTypeName = "DHIS2 ID";

                // MIGHT BE NULL because of strange design..
                PatientIdentifierType identifierType = id.getIdentifierType();

                if ( identifierType != null )
                {
                    idTypeName = identifierType.getName();
                }

                identifiers
                    .add( new org.hisp.dhis.api.mobile.model.PatientIdentifier( idTypeName, id.getIdentifier() ) );
            }

            beneficiary.setIdentifiers( identifiers );
        }

        beneficiary.setPatientAttValues( patientAtts );
        return beneficiary;
    }

    // get patient model for LWUIT
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Patient getPatientModel( Patient patient )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientModel = new org.hisp.dhis.api.mobile.model.LWUITmodel.Patient();

        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> mobileProgramList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> mobileCompletedProgramList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();

        List<org.hisp.dhis.patient.PatientAttribute> atts;

        patientModel.setId( patient.getId() );

        if ( patient.getName() != null )
        {
            patientModel.setName( patient.getName() );
        }
        Period period = new Period( new DateTime( patient.getBirthDate() ), new DateTime() );
        patientModel.setAge( period.getYears() );
        /*
         * DateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
         * patientModel.setAge( dateFormat.format( patient.getBirthDate() ) );
         */
        if ( patient.getOrganisationUnit() != null )
        {
            patientModel.setOrganisationUnitName( patient.getOrganisationUnit().getName() );
        }
        if ( patient.getPhoneNumber() != null )
        {
            patientModel.setPhoneNumber( patient.getPhoneNumber() );
        }

        this.setSetting( getSettings() );

        if ( setting != null )
        {
            if ( setting.getGender() )
            {
                patientModel.setGender( patient.getGender() );
            }
            if ( setting.getDobtype() )
            {
                patientModel.setDobType( patient.getDobType() );
            }
            if ( setting.getBirthdate() && patient.getBirthDate() != null )
            {
                DateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
                patientModel.setBirthDate( dateFormat.format( patient.getBirthDate() ) );
            }
            if ( setting.getRegistrationdate() )
            {
                patientModel.setRegistrationDate( patient.getRegistrationDate() );
            }

            atts = setting.getPatientAttributes();
            for ( org.hisp.dhis.patient.PatientAttribute each : atts )
            {
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, each );
                if ( value != null )
                {
                    patientAtts.add( new PatientAttribute( each.getName(), value.getValue(), each.getValueType(),
                        new ArrayList<String>() ) );
                }
            }
        }

        // Set all identifier
        Set<PatientIdentifier> patientIdentifiers = patient.getIdentifiers();
        List<org.hisp.dhis.api.mobile.model.PatientIdentifier> identifiers = new ArrayList<org.hisp.dhis.api.mobile.model.PatientIdentifier>();
        if ( patientIdentifiers.size() > 0 )
        {

            for ( PatientIdentifier id : patientIdentifiers )
            {

                String idTypeName = "DHIS2 ID";

                // MIGHT BE NULL because of strange design..
                PatientIdentifierType identifierType = id.getIdentifierType();

                if ( identifierType != null )
                {
                    idTypeName = identifierType.getName();
                }

                identifiers
                    .add( new org.hisp.dhis.api.mobile.model.PatientIdentifier( idTypeName, id.getIdentifier() ) );
            }
        }
        patientModel.setIdentifiers( identifiers );

        patientModel.setPatientAttValues( patientAtts );

        // Set current programs
        List<ProgramInstance> listOfProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_ACTIVE ) );

        if ( listOfProgramInstance.size() > 0 )
        {
            for ( ProgramInstance each : listOfProgramInstance )
            {
                mobileProgramList.add( getMobileProgram( each ) );
            }
        }
        patientModel.setPrograms( mobileProgramList );

        // Set completed programs
        List<ProgramInstance> listOfCompletedProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_COMPLETED ) );

        if ( listOfCompletedProgramInstance.size() > 0 )
        {
            for ( ProgramInstance each : listOfCompletedProgramInstance )
            {
                mobileCompletedProgramList.add( getMobileProgram( each ) );
            }
        }
        patientModel.setCompletedPrograms( mobileCompletedProgramList );

        /*
         * List<Integer> mobileProgramIDList = new ArrayList<Integer>(); for (
         * Program eachProgram : patient.getPrograms()) {
         * mobileProgramIDList.add( eachProgram.getId() ); }
         * patientModel.setProgramsID( mobileProgramIDList );
         * 
         * // Set patient Data value for off-line storage function Map<Integer,
         * String> patientDataValues = new HashMap<Integer, String>(); for (
         * ProgramInstance eachProgramInstance :
         * programInstanceService.getProgramInstances( patient,
         * ProgramInstance.STATUS_ACTIVE ) ) { for ( ProgramStageInstance
         * eachProgramStageInstance :
         * eachProgramInstance.getProgramStageInstances() ) { for (
         * PatientDataValue each : patientDataValueService.getPatientDataValues(
         * eachProgramStageInstance ) ) { if( each.getValue() != null &&
         * !each.getValue().isEmpty()) { Integer dataElementID =
         * each.getDataElement().getId(); String value = each.getValue();
         * patientDataValues.put( dataElementID, value ); } } } }
         * patientModel.setPatientDataValues( patientDataValues );
         */

        // Set Relationship
        List<Relationship> relationships = new ArrayList<Relationship>(
            relationshipService.getRelationshipsForPatient( patient ) );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship> relationshipList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();

        for ( Relationship eachRelationship : relationships )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship relationshipMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
            relationshipMobile.setId( eachRelationship.getId() );
            if ( eachRelationship.getPatientA().getId() == patient.getId() )
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getaIsToB() );
                relationshipMobile.setPersonBName( eachRelationship.getPatientB().getName() );
                relationshipMobile.setPersonBId( eachRelationship.getPatientB().getId() );
            }
            else
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getbIsToA() );
                relationshipMobile.setPersonBName( eachRelationship.getPatientA().getName() );
                relationshipMobile.setPersonBId( eachRelationship.getPatientA().getId() );
            }
            relationshipList.add( relationshipMobile );
        }
        patientModel.setRelationships( relationshipList );

        // Set available enrollment programs
        List<Program> enrollmentProgramList = new ArrayList<Program>();
        enrollmentProgramList = generateEnrollmentProgramList( patient );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Program> enrollmentProgramListMobileList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Program>();

        for ( Program enrollmentProgram : enrollmentProgramList )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Program enrollmentProgramMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();
            enrollmentProgramMobile.setId( enrollmentProgram.getId() );
            enrollmentProgramMobile.setName( enrollmentProgram.getName() );
            enrollmentProgramMobile.setStatus( ProgramInstance.STATUS_ACTIVE );
            enrollmentProgramMobile.setVersion( enrollmentProgram.getVersion() );
            enrollmentProgramMobile.setProgramStages( null );
            enrollmentProgramListMobileList.add( enrollmentProgramMobile );
        }
        patientModel.setEnrollmentPrograms( enrollmentProgramListMobileList );

        // Set available enrollment relationships
        List<RelationshipType> enrollmentRelationshipList = new ArrayList<RelationshipType>(
            relationshipTypeService.getAllRelationshipTypes() );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship> enrollmentRelationshipMobileList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();
        for ( RelationshipType enrollmentRelationship : enrollmentRelationshipList )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship enrollmentRelationshipMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
            enrollmentRelationshipMobile.setId( enrollmentRelationship.getId() );
            enrollmentRelationshipMobile.setName( enrollmentRelationship.getName() );
            enrollmentRelationshipMobile.setaIsToB( enrollmentRelationship.getaIsToB() );
            enrollmentRelationshipMobile.setbIsToA( enrollmentRelationship.getbIsToA() );
            enrollmentRelationshipMobileList.add( enrollmentRelationshipMobile );
        }
        patientModel.setEnrollmentRelationships( enrollmentRelationshipMobileList );
        return patientModel;
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.Program getMobileProgram( ProgramInstance programInstance )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.Program mobileProgram = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        mobileProgram.setVersion( programInstance.getProgram().getVersion() );
        mobileProgram.setId( programInstance.getId() );
        mobileProgram.setName( programInstance.getProgram().getName() );
        mobileProgram.setStatus( programInstance.getStatus() );
        mobileProgram.setProgramStages( getMobileProgramStages( programInstance ) );
        return mobileProgram;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> getMobileProgramStages(
        ProgramInstance programInstance )
    {
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

        /*
         * for ( ProgramStage eachProgramStage :
         * programInstance.getProgram().getProgramStages() )
         */
        for ( ProgramStageInstance eachProgramStageInstance : programInstance.getProgramStageInstances() )
        {
            // only for Mujhu database, because there is null program stage
            // instance. This condition should be removed in the future
            if ( eachProgramStageInstance != null )
            {
                ProgramStage programStage = eachProgramStageInstance.getProgramStage();

                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();
                List<org.hisp.dhis.api.mobile.model.LWUITmodel.Section> mobileSections = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Section>();
                mobileProgramStage.setId( eachProgramStageInstance.getId() );
                /* mobileProgramStage.setName( eachProgramStage.getName() ); */
                mobileProgramStage.setName( programStage.getName() );

                // get report date
                if ( eachProgramStageInstance.getExecutionDate() != null )
                {
                    mobileProgramStage.setReportDate( PeriodUtil.dateToString( eachProgramStageInstance
                        .getExecutionDate() ) );
                }
                else
                {
                    mobileProgramStage.setReportDate( "" );
                }

                if ( eachProgramStageInstance.getProgramStage().getReportDateDescription() == null )
                {
                    mobileProgramStage.setReportDateDescription( "Report Date" );
                }
                else
                {
                    mobileProgramStage.setReportDateDescription( eachProgramStageInstance.getProgramStage()
                        .getReportDateDescription() );
                }

                // is repeatable
                mobileProgramStage.setRepeatable( programStage.getIrregular() );

                // is completed
                /*
                 * mobileProgramStage.setCompleted(
                 * checkIfProgramStageCompleted( patient,
                 * programInstance.getProgram(), programStage ) );
                 */
                mobileProgramStage.setCompleted( eachProgramStageInstance.isCompleted() );

                // is single event
                mobileProgramStage.setSingleEvent( programInstance.getProgram().isSingleEvent() );

                // Set all data elements
                mobileProgramStage.setDataElements( getDataElementsForMobile( programStage, eachProgramStageInstance ) );

                // Set all program sections
                if ( programStage.getProgramStageSections().size() > 0 )
                {
                    for ( ProgramStageSection eachSection : programStage.getProgramStageSections() )
                    {
                        org.hisp.dhis.api.mobile.model.LWUITmodel.Section mobileSection = new org.hisp.dhis.api.mobile.model.LWUITmodel.Section();
                        mobileSection.setId( eachSection.getId() );
                        mobileSection.setName( eachSection.getName() );

                        // Set all data elements' id, then we could have full
                        // from
                        // data element list of program stage
                        List<Integer> dataElementIds = new ArrayList<Integer>();
                        for ( ProgramStageDataElement eachPogramStageDataElement : eachSection
                            .getProgramStageDataElements() )
                        {
                            dataElementIds.add( eachPogramStageDataElement.getDataElement().getId() );
                        }
                        mobileSection.setDataElementIds( dataElementIds );
                        mobileSections.add( mobileSection );
                    }
                }
                mobileProgramStage.setSections( mobileSections );

                mobileProgramStages.add( mobileProgramStage );
            }
        }
        return mobileProgramStages;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> getDataElementsForMobile(
        ProgramStage programStage, ProgramStageInstance programStageInstance )
    {
        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
            programStage.getProgramStageDataElements() );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();
        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
            mobileDataElement.setId( programStageDataElement.getDataElement().getId() );

            String dataElementName;

            if ( programStageDataElement.getDataElement().getFormName() != null
                && !programStageDataElement.getDataElement().getFormName().trim().equals( "" ) )
            {
                dataElementName = programStageDataElement.getDataElement().getFormName();
            }
            else
            {
                dataElementName = programStageDataElement.getDataElement().getName();
            }

            mobileDataElement.setName( dataElementName );
            mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

            // problem
            mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

            mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

            // Value
            PatientDataValue patientDataValue = dataValueService.getPatientDataValue( programStageInstance,
                programStageDataElement.getDataElement() );
            if ( patientDataValue != null )
            {
                // Convert to standard date format before send to client
                if ( programStageDataElement.getDataElement().getType().equalsIgnoreCase( "date" )
                    && !patientDataValue.equals( "" ) )
                {
                    mobileDataElement.setValue( PeriodUtil.convertDateFormat( patientDataValue.getValue() ) );
                }
                else
                {
                    mobileDataElement.setValue( patientDataValue.getValue() );
                }
            }
            else
            {
                mobileDataElement.setValue( null );
            }

            // Option set
            if ( programStageDataElement.getDataElement().getOptionSet() != null )
            {
                mobileDataElement.setOptionSet( modelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setOptionSet( null );
            }

            // Category Option Combo
            if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
            {
                mobileDataElement.setCategoryOptionCombos( modelMapping
                    .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setCategoryOptionCombos( null );
            }
            mobileDataElements.add( mobileDataElement );
        }
        return mobileDataElements;
    }

    private PatientMobileSetting getSettings()
    {
        PatientMobileSetting setting = null;

        Collection<PatientMobileSetting> currentSetting = patientMobileSettingService.getCurrentSetting();
        if ( currentSetting != null && !currentSetting.isEmpty() )
            setting = currentSetting.iterator().next();
        return setting;
    }

    private List<Program> generateEnrollmentProgramList( Patient patient )
    {
        List<Program> programs = new ArrayList<Program>();

        // for ( Program program : programService.getPrograms(
        // orgUnitService.getOrganisationUnit( orgId ) ) )
        for ( Program program : programService.getPrograms( patient.getOrganisationUnit() ) )
        {
            if ( (program.isSingleEvent() && program.isRegistration()) || !program.isSingleEvent() )
            {
                // wrong here
                if ( programInstanceService.getProgramInstances( patient, program ).size() == 0 )
                {
                    programs.add( program );
                }
            }
        }
        return programs;
    }

    private boolean isNumber( String value )
    {
        try
        {
            Double.parseDouble( value );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient addRelationship(
        org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship enrollmentRelationship, int orgUnitId )
        throws NotAllowedException
    {
        Patient patientB;
        if ( enrollmentRelationship.getPersonBId() != 0 )
        {
            patientB = patientService.getPatient( enrollmentRelationship.getPersonBId() );
        }
        else
        {

            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
            
            String fullName = enrollmentRelationship.getPersonBName();
            List<Patient> patients = (List<Patient>) this.patientService.getPatientByFullname( fullName, organisationUnit );

            // remove the own searcher
            patients = removeIfDuplicated( patients, enrollmentRelationship.getPersonAId() );

            if ( patients.size() > 1 )
            {
                String patientsInfo = new String();

                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

                for ( Patient each : patients )
                {
                    patientsInfo += each.getId() + "/" + each.getName() + "/" + dateFormat.format( each.getBirthDate() )
                        + "$";
                }

                throw new NotAllowedException( patientsInfo );
            }
            else if ( patients.size() == 0 )
            {
                throw NotAllowedException.NO_BENEFICIARY_FOUND;
            }
            else
            {
                patientB = patients.get( 0 );
            }
        }
        Patient patientA = patientService.getPatient( enrollmentRelationship.getPersonAId() );
        RelationshipType relationshipType = relationshipTypeService
            .getRelationshipType( enrollmentRelationship.getId() );

        Relationship relationship = new Relationship();
        relationship.setRelationshipType( relationshipType );
        if ( enrollmentRelationship.getChosenRelationship().equals( relationshipType.getaIsToB() ) )
        {
            relationship.setPatientA( patientA );
            relationship.setPatientB( patientB );
        }
        else
        {
            relationship.setPatientA( patientB );
            relationship.setPatientB( patientA );
        }
        relationshipService.saveRelationship( relationship );
        // return getPatientModel( orgUnitId, patientA );
        return getPatientModel( patientA );
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program getAllProgramByOrgUnit( int orgUnitId, String programType )
        throws NotAllowedException
    {
        String programsInfo = "";

        int programTypeInt = Integer.valueOf( programType );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        List<Program> tempPrograms = null;

        if ( programTypeInt == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            tempPrograms = new ArrayList<Program>(
                programService.getProgramsByCurrentUser( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );
        }
        else if ( programTypeInt == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            tempPrograms = new ArrayList<Program>(
                programService.getProgramsByCurrentUser( Program.MULTIPLE_EVENTS_WITH_REGISTRATION ) );
        }

        List<Program> programs = new ArrayList<Program>();

        for ( Program program : tempPrograms )
        {
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                programs.add( program );
            }
        }

        if ( programs.size() != 0 )
        {
            if ( programs.size() == 1 )
            {
                Program program = programs.get( 0 );

                return getMobileProgramWithoutData( program );
            }
            else
            {
                for ( Program program : programs )
                {
                    if ( program.getOrganisationUnits().contains( organisationUnit ) )
                    {
                        programsInfo += program.getId() + "/" + program.getName() + "$";
                    }
                }
                throw new NotAllowedException( programsInfo );
            }
        }
        else
        {
            throw NotAllowedException.NO_PROGRAM_FOUND;
        }
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program findProgram( String programInfo )
        throws NotAllowedException
    {
        if ( isNumber( programInfo ) == false )
        {
            return null;
        }
        else
        {
            Program program = programService.getProgram( Integer.parseInt( programInfo ) );
            if ( program.isSingleEvent() )
            {
                return getMobileProgramWithoutData( program );
            }
            else
            {
                return null;
            }
        }
    }

    // If the return program is anonymous, the client side will show the entry
    // form as normal
    // If the return program is not anonymous, it is still OK because in client
    // side, we only need name and id
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Program getMobileProgramWithoutData( Program program )
    {
        Comparator<ProgramStageDataElement> OrderBySortOrder = new Comparator<ProgramStageDataElement>()
        {
            public int compare( ProgramStageDataElement i1, ProgramStageDataElement i2 )
            {
                return i1.getSortOrder().compareTo( i2.getSortOrder() );
            }
        };

        org.hisp.dhis.api.mobile.model.LWUITmodel.Program anonymousProgramMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        anonymousProgramMobile.setId( program.getId() );

        anonymousProgramMobile.setName( program.getName() );

        //if ( program.getType() == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            anonymousProgramMobile.setVersion( program.getVersion() );

            anonymousProgramMobile.setStatus( ProgramInstance.STATUS_ACTIVE );

            ProgramStage programStage = program.getProgramStages().iterator().next();

            List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
                programStage.getProgramStageDataElements() );
            Collections.sort( programStageDataElements, OrderBySortOrder );

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileProgramStageDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();

            mobileProgramStage.setId( programStage.getId() );
            mobileProgramStage.setName( programStage.getName() );
            mobileProgramStage.setCompleted( false );
            mobileProgramStage.setRepeatable( false );
            mobileProgramStage.setSingleEvent( true );
            mobileProgramStage.setSections( new ArrayList<Section>() );

            // get report date
            mobileProgramStage.setReportDate( PeriodUtil.dateToString( new Date() ) );

            if ( programStage.getReportDateDescription() == null )
            {
                mobileProgramStage.setReportDateDescription( "Report Date" );
            }
            else
            {
                mobileProgramStage.setReportDateDescription( programStage.getReportDateDescription() );
            }

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
                mobileDataElement.setId( programStageDataElement.getDataElement().getId() );
                mobileDataElement.setName( programStageDataElement.getDataElement().getName() );
                mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

                // problem
                mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

                mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

                mobileDataElement.setValue( "" );

                if ( programStageDataElement.getDataElement().getOptionSet() != null )
                {
                    mobileDataElement
                        .setOptionSet( modelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
                }
                else
                {
                    mobileDataElement.setOptionSet( null );
                }
                if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
                {
                    mobileDataElement.setCategoryOptionCombos( modelMapping
                        .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
                }
                else
                {
                    mobileDataElement.setCategoryOptionCombos( null );
                }
                mobileProgramStageDataElements.add( mobileDataElement );
            }
            mobileProgramStage.setDataElements( mobileProgramStageDataElements );
            mobileProgramStages.add( mobileProgramStage );
            anonymousProgramMobile.setProgramStages( mobileProgramStages );
        }

        return anonymousProgramMobile;
    }

    private List<Patient> removeIfDuplicated( List<Patient> patients, int patientId )
    {
        List<Patient> result = new ArrayList<Patient>( patients );
        for ( int i = 0; i < patients.size(); i++ )
        {
            if ( patients.get( i ).getId() == patientId )
            {
                result.remove( i );
            }
        }
        return result;
    }

    private void saveDataValues( ActivityValue activityValue, ProgramStageInstance programStageInstance,
        Map<Integer, DataElement> dataElementMap )
    {
        org.hisp.dhis.dataelement.DataElement dataElement;
        String value;

        for ( DataValue dv : activityValue.getDataValues() )
        {
            value = dv.getValue();

            if ( value != null && value.trim().length() == 0 )
            {
                value = null;
            }

            if ( value != null )
            {
                value = value.trim();
            }

            dataElement = dataElementMap.get( dv.getId() );
            PatientDataValue dataValue = dataValueService.getPatientDataValue( programStageInstance, dataElement );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    if ( programStageInstance.getExecutionDate() == null )
                    {
                        programStageInstance.setExecutionDate( new Date() );
                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }

                    dataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );

                    dataValueService.savePatientDataValue( dataValue );
                }
            }
            else
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    programStageInstance.setExecutionDate( new Date() );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );

                dataValueService.updatePatientDataValue( dataValue );
            }
        }
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        Collection<Program> programs = programService.getAllPrograms();
        for ( Program program : programs )
        {
            patientIdentifierTypes.removeAll( program.getPatientIdentifierTypes() );
        }
        return patientIdentifierTypes;
    }

    public Collection<org.hisp.dhis.patient.PatientAttribute> getPatientAtts( String programId )
    {
        if ( programId != null && !programId.trim().equals( "" ) )
        {
            Program program = programService.getProgram( Integer.parseInt( programId ) );
            patientAttributes = program.getPatientAttributes();
        }
        else
        {
            patientAttributes = patientAttributeService.getAllPatientAttributes();
        }

        return patientAttributes;
    }

    public Collection<PatientIdentifierType> getIdentifiers( String programId )
    {
        if ( programId != null && !programId.trim().equals( "" ) )
        {
            Program program = programService.getProgram( Integer.parseInt( programId ) );
            patientIdentifierTypes = program.getPatientIdentifierTypes();
        }
        else
        {
            patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        }

        return patientIdentifierTypes;

    }

    public Collection<PatientAttribute> getAttsForMobile()
    {
        Collection<PatientAttribute> list = new HashSet<PatientAttribute>();

        for ( org.hisp.dhis.patient.PatientAttribute patientAtt : getPatientAtts( null ) )
        {
            list.add( new PatientAttribute( patientAtt.getName(), null, patientAtt.getValueType(),
                new ArrayList<String>() ) );
        }

        return list;

    }

    @Override
    public Collection<org.hisp.dhis.api.mobile.model.PatientIdentifier> getIdentifiersForMobile( String programId )
    {
        Collection<org.hisp.dhis.api.mobile.model.PatientIdentifier> list = new HashSet<org.hisp.dhis.api.mobile.model.PatientIdentifier>();
        for ( PatientIdentifierType identifierType : getIdentifiers( programId ) )
        {
            String id = "";
            String idt = identifierType.getName();
            if ( identifierType.isMandatory() == true )
            {
                idt += " (*)";
            }
            list.add( new org.hisp.dhis.api.mobile.model.PatientIdentifier( idt, id ) );
        }
        return list;
    }

    @Override
    public Collection<PatientAttribute> getPatientAttributesForMobile( String programId )
    {
        Collection<PatientAttribute> list = new HashSet<PatientAttribute>();
        for ( org.hisp.dhis.patient.PatientAttribute pa : getPatientAtts( programId ) )
        {
            PatientAttribute patientAttribute = new PatientAttribute();
            String name = pa.getName();

            patientAttribute.setName( name );
            patientAttribute.setType( pa.getValueType() );
            patientAttribute.setValue( "" );
            List<String> optionList = new ArrayList<String>();
            if ( pa.getAttributeOptions() != null )
            {
                for ( PatientAttributeOption option : pa.getAttributeOptions() )
                {
                    optionList.add( option.getName() );
                }
            }

            patientAttribute.setPredefinedValues( optionList );
            list.add( patientAttribute );
        }
        return list;
    }

    @Required
    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Required
    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findLatestPatient()
        throws NotAllowedException
    {
        Patient patient = patientService.getPatient( this.patientId );

        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );
        return patientMobile;
    }

    @Override
    public Integer savePatient( org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patient, int orgUnitId,
        String programIdText )
        throws NotAllowedException
    {
        org.hisp.dhis.patient.Patient patientWeb = new org.hisp.dhis.patient.Patient();

        patientWeb.setName( patient.getName() );
        patientWeb.setGender( patient.getGender() );
        patientWeb.setDobType( patient.getDobType() );
        patientWeb.setPhoneNumber( patient.getPhoneNumber() );
        patientWeb.setBirthDate( PeriodUtil.stringToDate( patient.getBirthDate() ) );
        patientWeb.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );
        patientWeb.setRegistrationDate( new Date() );

        Set<org.hisp.dhis.patient.PatientIdentifier> patientIdentifierSet = new HashSet<org.hisp.dhis.patient.PatientIdentifier>();
        Set<org.hisp.dhis.patient.PatientAttribute> patientAttributeSet = new HashSet<org.hisp.dhis.patient.PatientAttribute>();
        List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();

        Collection<org.hisp.dhis.api.mobile.model.PatientIdentifier> identifiers = patient.getIdentifiers();

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> patientAttributesMobile = patient
            .getPatientAttValues();

        if ( identifierTypes.size() > 0 )
        {
            for ( org.hisp.dhis.api.mobile.model.PatientIdentifier identifier : identifiers )
            {
                PatientIdentifierType patientIdentifierType = patientIdentifierTypeService
                    .getPatientIdentifierType( identifier.getIdentifierType() );

                org.hisp.dhis.patient.PatientIdentifier patientIdentifier = new org.hisp.dhis.patient.PatientIdentifier();

                patientIdentifier.setIdentifierType( patientIdentifierType );
                patientIdentifier.setPatient( patientWeb );
                patientIdentifier.setIdentifier( identifier.getIdentifier() );
                patientIdentifierSet.add( patientIdentifier );
            }
        }
        // --------------------------------------------------------------------------------
        // Generate system id with this format :
        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
        // PatientIdentifierType will be null
        // --------------------------------------------------------------------------------
        if ( identifierTypes.size() == 0 )
        {
            String identifier = PatientIdentifierGenerator.getNewIdentifier(
                PeriodUtil.stringToDate( patient.getBirthDate() ), patient.getGender() );

            org.hisp.dhis.patient.PatientIdentifier systemGenerateIdentifier = new org.hisp.dhis.patient.PatientIdentifier();
            systemGenerateIdentifier.setIdentifier( identifier );
            systemGenerateIdentifier.setIdentifierType( null );
            systemGenerateIdentifier.setPatient( patientWeb );
            patientIdentifierSet.add( systemGenerateIdentifier );
        }

        if ( patientAttributesMobile != null )
        {
            for ( org.hisp.dhis.api.mobile.model.PatientAttribute paAtt : patientAttributesMobile )
            {

                org.hisp.dhis.patient.PatientAttribute patientAttribute = patientAttributeService
                    .getPatientAttributeByName( paAtt.getName() );

                patientAttributeSet.add( patientAttribute );

                PatientAttributeValue patientAttributeValue = new PatientAttributeValue();

                patientAttributeValue.setPatient( patientWeb );
                patientAttributeValue.setPatientAttribute( patientAttribute );
                patientAttributeValue.setValue( paAtt.getValue() );
                patientAttributeValues.add( patientAttributeValue );

            }
        }

        patientWeb.setIdentifiers( patientIdentifierSet );

        patientId = patientService.createPatient( patientWeb, null, null, patientAttributeValues );

        try
        {
            int programId = Integer.parseInt( programIdText );
            this.enrollProgram( patientId + "-" + programId );
        }
        catch ( Exception e )
        {
            return patientId;
        }

        return patientId;

    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatient( int patientId )
        throws NotAllowedException
    {
        Patient patient = patientService.getPatient( patientId );
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );
        return patientMobile;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatientInAdvanced( String keyword, int orgUnitId,
        int programId )
        throws NotAllowedException
    {
        Collection<Patient> patients = new HashSet<Patient>( patientService.getPatientsForMobile( keyword, orgUnitId ) );
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        if ( programId != 0 )
        {
            Program program = programService.getProgram( programId );
            List<Patient> tempPatients = (List<Patient>) patientService.getPatients( program );
            patients.retainAll( tempPatients );
        }

        if ( programId != 0 && orgUnitId != 0 )
        {
            boolean isProgramBelongToOrgUnit = false;
            for ( Program program : programService.getPrograms( orgUnit ) )
            {
                if ( program.getId() == programId )
                {
                    isProgramBelongToOrgUnit = true;
                    break;
                }
            }
            if ( isProgramBelongToOrgUnit == false )
            {
                throw NotAllowedException.NO_PROGRAM_BELONG_ORGUNIT;
            }
        }

        if ( patients.size() > 1 )
        {
            String patientsInfo = new String();

            DateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );

            int i = 1;
            String name = "";
            String DOB = "";
            for ( Patient each : patients )
            {
                if ( i > 10 )
                {
                    break;
                }

                if ( each.getName() != null )
                {
                    name = each.getName();
                }
                else
                {
                    name = "unknown";
                }

                if ( each.getBirthDate() != null )
                {
                    DOB = dateFormat.format( each.getBirthDate() );
                }
                else
                {
                    DOB = "unknown";
                }
                patientsInfo += each.getId() + "/" + name + ", DOB: " + DOB + "$";
                i++;
            }

            throw new NotAllowedException( patientsInfo );
        }
        else if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }
        else
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Patient();
            for ( Patient each : patients )
            {
                patientMobile = getPatientModel( each );
                break;
            }
            return patientMobile;
        }
    }

    @Override
    public String findLostToFollowUp( int orgUnitId, String programId )
        throws NotAllowedException
    {
        String eventsInfo = "";
        Boolean followUp = false;
        DateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );

        List<String> searchTextList = new ArrayList<String>();
        Collection<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();

        Calendar toCalendar = new GregorianCalendar();
        toCalendar.add( Calendar.DATE, -1 );
        toCalendar.add( Calendar.YEAR, 100 );
        Date toDate = toCalendar.getTime();

        Calendar fromCalendar = new GregorianCalendar();
        fromCalendar.add( Calendar.DATE, -1 );
        fromCalendar.add( Calendar.YEAR, -100 );

        Date fromDate = fromCalendar.getTime();

        String searchText = Patient.PREFIX_PROGRAM_EVENT_BY_STATUS + "_" + programId + "_"
            + formatter.format( fromDate ) + "_" + formatter.format( toDate ) + "_" + orgUnitId + "_" + true + "_"
            + ProgramStageInstance.LATE_VISIT_STATUS;

        searchTextList.add( searchText );
        orgUnitList.add( organisationUnitService.getOrganisationUnit( orgUnitId ) );
        List<Integer> stageInstanceIds = patientService.getProgramStageInstances( searchTextList, orgUnitList,
            followUp, ProgramInstance.STATUS_ACTIVE, null, null );

        if ( stageInstanceIds.size() == 0 )
        {
            throw NotAllowedException.NO_EVENT_FOUND;
        }
        else if ( stageInstanceIds.size() > 0 )
        {
            for ( Integer stageInstanceId : stageInstanceIds )
            {
                ProgramStageInstance programStageInstance = programStageInstanceService
                    .getProgramStageInstance( stageInstanceId );
                Patient patient = programStageInstance.getProgramInstance().getPatient();
                eventsInfo += programStageInstance.getId() + "/" + patient.getName() + ", "
                    + programStageInstance.getProgramStage().getName() + "("
                    + formatter.format( programStageInstance.getDueDate() ) + ")" + "$";
            }

            throw new NotAllowedException( eventsInfo );
        }
        else
        {
            return "";
        }
    }

    @SuppressWarnings( "finally" )
    @Override
    public Notification handleLostToFollowUp( LostEvent lostEvent )
        throws NotAllowedException
    {
        Notification notification = new Notification();
        try
        {
            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( lostEvent.getId() );
            programStageInstance.setDueDate( PeriodUtil.stringToDate( lostEvent.getDueDate() ) );
            programStageInstance.setStatus( lostEvent.getStatus() );
    
            if ( lostEvent.getComment() != null )
            {
                List<MessageConversation> conversationList = new ArrayList<MessageConversation>();
        
                MessageConversation conversation = new MessageConversation( lostEvent.getName(), currentUserService.getCurrentUser() );
        
                conversation.addMessage( new Message( lostEvent.getComment(), null, currentUserService.getCurrentUser() ) );
                
                conversation.setRead( true );
                
                conversationList.add( conversation );
        
                programStageInstance.setMessageConversations( conversationList );
                
                messageService.saveMessageConversation( conversation );
            }
    
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
            
            //send SMS
            if ( programStageInstance.getProgramInstance().getPatient().getPhoneNumber() != null && lostEvent.getSMS() != null )
            {
                User user = new User();
                user.setPhoneNumber( programStageInstance.getProgramInstance().getPatient().getPhoneNumber() );
                List<User> recipientsList = new ArrayList<User>();
                recipientsList.add( user );
                
                smsSender.sendMessage( lostEvent.getName(), lostEvent.getSMS(), currentUserService.getCurrentUser(), recipientsList, false );
            }
            
            notification.setMessage( "Success" );
        }
        catch (Exception e) {
            e.printStackTrace();
            notification.setMessage( "Fail" );
        }
        finally
        {
            return notification;
        }
    }
}
