package org.hisp.dhis.patient.action.program;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdateProgramAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    private RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Integer version;

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion( Integer version )
    {
        this.version = version;
    }

    private String dateOfEnrollmentDescription;

    public void setDateOfEnrollmentDescription( String dateOfEnrollmentDescription )
    {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    private String dateOfIncidentDescription;

    public void setDateOfIncidentDescription( String dateOfIncidentDescription )
    {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    private Integer type;

    public void setType( Integer type )
    {
        this.type = type;
    }

    private Boolean displayProvidedOtherFacility;

    public void setDisplayProvidedOtherFacility( Boolean displayProvidedOtherFacility )
    {
        this.displayProvidedOtherFacility = displayProvidedOtherFacility;
    }

    private Boolean displayIncidentDate;

    public void setDisplayIncidentDate( Boolean displayIncidentDate )
    {
        this.displayIncidentDate = displayIncidentDate;
    }

    private List<String> selectedPropertyIds = new ArrayList<String>();

    public void setSelectedPropertyIds( List<String> selectedPropertyIds )
    {
        this.selectedPropertyIds = selectedPropertyIds;
    }

    private List<Boolean> personDisplayNames = new ArrayList<Boolean>();

    public void setPersonDisplayNames( List<Boolean> personDisplayNames )
    {
        this.personDisplayNames = personDisplayNames;
    }

    private Boolean generateBydEnrollmentDate;

    public void setGeneratedByEnrollmentDate( Boolean generateBydEnrollmentDate )
    {
        this.generateBydEnrollmentDate = generateBydEnrollmentDate;
    }

    private Boolean ignoreOverdueEvents;

    public void setIgnoreOverdueEvents( Boolean ignoreOverdueEvents )
    {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    private Boolean blockEntryForm;

    public void setBlockEntryForm( Boolean blockEntryForm )
    {
        this.blockEntryForm = blockEntryForm;
    }

    private Boolean onlyEnrollOnce = false;

    public void setOnlyEnrollOnce( Boolean onlyEnrollOnce )
    {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    private Boolean remindCompleted = false;

    public void setRemindCompleted( Boolean remindCompleted )
    {
        this.remindCompleted = remindCompleted;
    }

    private List<Integer> daysAllowedSendMessages = new ArrayList<Integer>();

    public void setDaysAllowedSendMessages( List<Integer> daysAllowedSendMessages )
    {
        this.daysAllowedSendMessages = daysAllowedSendMessages;
    }

    private List<String> templateMessages = new ArrayList<String>();

    public void setTemplateMessages( List<String> templateMessages )
    {
        this.templateMessages = templateMessages;
    }

    private List<String> datesToCompare = new ArrayList<String>();

    public void setDatesToCompare( List<String> datesToCompare )
    {
        this.datesToCompare = datesToCompare;
    }

    private List<Integer> sendTo = new ArrayList<Integer>();

    public void setSendTo( List<Integer> sendTo )
    {
        this.sendTo = sendTo;
    }

    private Boolean displayOnAllOrgunit;

    public void setDisplayOnAllOrgunit( Boolean displayOnAllOrgunit )
    {
        this.displayOnAllOrgunit = displayOnAllOrgunit;
    }

    private List<Integer> whenToSend = new ArrayList<Integer>();

    public void setWhenToSend( List<Integer> whenToSend )
    {
        this.whenToSend = whenToSend;
    }

    private List<Integer> messageType = new ArrayList<Integer>();

    public void setMessageType( List<Integer> messageType )
    {
        this.messageType = messageType;
    }

    private Boolean useBirthDateAsIncidentDate;

    public void setUseBirthDateAsIncidentDate( Boolean useBirthDateAsIncidentDate )
    {
        this.useBirthDateAsIncidentDate = useBirthDateAsIncidentDate;
    }

    private Boolean useBirthDateAsEnrollmentDate;

    public void setUseBirthDateAsEnrollmentDate( Boolean useBirthDateAsEnrollmentDate )
    {
        this.useBirthDateAsEnrollmentDate = useBirthDateAsEnrollmentDate;
    }

    private List<Integer> userGroup = new ArrayList<Integer>();

    public void setUserGroup( List<Integer> userGroup )
    {
        this.userGroup = userGroup;
    }

    private Boolean selectEnrollmentDatesInFuture;

    public void setSelectEnrollmentDatesInFuture( Boolean selectEnrollmentDatesInFuture )
    {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    private Boolean selectIncidentDatesInFuture;

    public void setSelectIncidentDatesInFuture( Boolean selectIncidentDatesInFuture )
    {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    private String relationshipText;

    public void setRelationshipText( String relationshipText )
    {
        this.relationshipText = relationshipText;
    }

    private Integer relationshipTypeId;

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    private Boolean relationshipFromA;

    public void setRelationshipFromA( Boolean relationshipFromA )
    {
        this.relationshipFromA = relationshipFromA;
    }

    private Integer relatedProgramId;

    public void setRelatedProgramId( Integer relatedProgramId )
    {
        this.relatedProgramId = relatedProgramId;
    }

    private Boolean dataEntryMethod;

    public void setDataEntryMethod( Boolean dataEntryMethod )
    {
        this.dataEntryMethod = dataEntryMethod;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        displayProvidedOtherFacility = (displayProvidedOtherFacility == null) ? false : displayProvidedOtherFacility;
        displayIncidentDate = (displayIncidentDate == null) ? false : displayIncidentDate;
        generateBydEnrollmentDate = (generateBydEnrollmentDate == null) ? false : generateBydEnrollmentDate;
        ignoreOverdueEvents = (ignoreOverdueEvents == null) ? false : ignoreOverdueEvents;
        blockEntryForm = (blockEntryForm == null) ? false : blockEntryForm;
        remindCompleted = (remindCompleted == null) ? false : remindCompleted;
        displayOnAllOrgunit = (displayOnAllOrgunit == null) ? false : displayOnAllOrgunit;
        useBirthDateAsIncidentDate = (useBirthDateAsIncidentDate == null) ? false : useBirthDateAsIncidentDate;
        useBirthDateAsEnrollmentDate = (useBirthDateAsEnrollmentDate == null) ? false : useBirthDateAsEnrollmentDate;
        selectEnrollmentDatesInFuture = (selectEnrollmentDatesInFuture == null) ? false : selectEnrollmentDatesInFuture;
        selectIncidentDatesInFuture = (selectIncidentDatesInFuture == null) ? false : selectIncidentDatesInFuture;
        dataEntryMethod = (dataEntryMethod == null) ? false : dataEntryMethod;

        Program program = programService.getProgram( id );
        program.setName( name );
        program.setDescription( description );
        program.setVersion( version );
        program.setDateOfEnrollmentDescription( dateOfEnrollmentDescription );
        program.setDateOfIncidentDescription( dateOfIncidentDescription );
        program.setType( type );
        program.setDisplayIncidentDate( displayIncidentDate );
        program.setOnlyEnrollOnce( onlyEnrollOnce );
        program.setDisplayOnAllOrgunit( displayOnAllOrgunit );
        program.setUseBirthDateAsIncidentDate( useBirthDateAsIncidentDate );
        program.setUseBirthDateAsEnrollmentDate( useBirthDateAsEnrollmentDate );
        program.setSelectEnrollmentDatesInFuture( selectEnrollmentDatesInFuture );
        program.setSelectIncidentDatesInFuture( selectIncidentDatesInFuture );
        program.setDataEntryMethod( dataEntryMethod );

        if ( type == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            program.setIgnoreOverdueEvents( ignoreOverdueEvents );
        }
        else
        {
            program.setIgnoreOverdueEvents( false );
        }

        if ( relatedProgramId != null )
        {
            Program relatedProgram = programService.getProgram( relatedProgramId );
            program.setRelatedProgram( relatedProgram );
        }

        if ( relationshipTypeId != null )
        {
            RelationshipType relationshipType = relationshipTypeService.getRelationshipType( relationshipTypeId );
            program.setRelationshipType( relationshipType );
        }
        program.setRelationshipFromA( relationshipFromA );
        program.setRelationshipText( relationshipText );

        List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
        int index = 0;
        for ( String selectedPropertyId : selectedPropertyIds )
        {
            String[] ids = selectedPropertyId.split( "_" );

            if ( ids[0].equals( Patient.PREFIX_IDENTIFIER_TYPE ) )
            {
                PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( Integer
                    .parseInt( ids[1] ) );

                identifierType.setPersonDisplayName( personDisplayNames.get( index ) );
                patientIdentifierTypeService.updatePatientIdentifierType( identifierType );

                identifierTypes.add( identifierType );
            }
            else if ( ids[0].equals( Patient.PREFIX_PATIENT_ATTRIBUTE ) )
            {
                PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer
                    .parseInt( ids[1] ) );
                patientAttributes.add( patientAttribute );
            }
            index++;
        }

        program.setPatientIdentifierTypes( identifierTypes );
        program.setPatientAttributes( patientAttributes );

        // Template messasges
        Set<PatientReminder> patientReminders = new HashSet<PatientReminder>();
        for ( int i = 0; i < daysAllowedSendMessages.size(); i++ )
        {
            PatientReminder reminder = new PatientReminder( "", daysAllowedSendMessages.get( i ),
                templateMessages.get( i ) );
            reminder.setDateToCompare( datesToCompare.get( i ) );
            reminder.setSendTo( sendTo.get( i ) );
            reminder.setWhenToSend( whenToSend.get( i ) );
            reminder.setMessageType( messageType.get( i ) );

            if ( reminder.getSendTo() == PatientReminder.SEND_TO_USER_GROUP )
            {
                UserGroup selectedUserGroup = userGroupService.getUserGroup( userGroup.get( i ) );
                reminder.setUserGroup( selectedUserGroup );
            }
            else
            {
                reminder.setUserGroup( null );
            }

            if ( relatedProgramId != null )
            {
                Program relatedProgram = programService.getProgram( relatedProgramId );
                program.setRelatedProgram( relatedProgram );
            }

            patientReminders.add( reminder );
        }
        program.setPatientReminders( patientReminders );

        programService.updateProgram( program );

        return SUCCESS;
    }
}
