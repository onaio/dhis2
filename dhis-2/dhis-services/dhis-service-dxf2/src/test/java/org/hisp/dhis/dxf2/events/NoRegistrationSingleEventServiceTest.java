package org.hisp.dhis.dxf2.events;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.events.event.DataValue;
import org.hisp.dhis.dxf2.events.event.Event;
import org.hisp.dhis.dxf2.events.event.EventService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class NoRegistrationSingleEventServiceTest
    extends DhisTest
{
    @Autowired
    private EventService eventService;

    @Autowired
    private ProgramStageDataElementService programStageDataElementService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private IdentifiableObjectManager manager;

    private OrganisationUnit organisationUnitA;
    private DataElement dataElementA;
    private Program programA;
    private ProgramStage programStageA;

    @Override
    protected void setUpTest() throws Exception
    {
        organisationUnitA = createOrganisationUnit( 'A' );
        manager.save( organisationUnitA );

        dataElementA = createDataElement( 'A' );
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        manager.save( dataElementA );

        programStageA = createProgramStage( 'A', 0 );
        manager.save( programStageA );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );
        programA.setType( Program.SINGLE_EVENT_WITHOUT_REGISTRATION );
        manager.save( programA );

        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setDataElement( dataElementA );
        programStageDataElement.setProgramStage( programStageA );
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );

        programStageA.getProgramStageDataElements().add( programStageDataElement );
        programStageA.setProgram( programA );
        programA.getProgramStages().add( programStageA );

        manager.update( programStageA );
        manager.update( programA );

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setProgram( programA );
        programInstance.setDateOfIncident( new Date() );
        programInstance.setEnrollmentDate( new Date() );

        programInstanceService.addProgramInstance( programInstance );
        programA.getProgramInstances().add( programInstance );
        manager.update( programA );

        createSuperuserAndInjectSecurityContext( 'A' );

        // mocked format
        I18nFormat mockFormat = mock( I18nFormat.class );
        when( mockFormat.parseDate( anyString() ) ).thenReturn( new Date() );
        eventService.setFormat( mockFormat );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Test
    public void testGetPersonsByProgramOrgUnit()
    {
        for ( int i = 0; i < 10; i++ )
        {
            Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

            ImportSummary importSummary = eventService.saveEvent( event );
            assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
            assertNotNull( importSummary.getReference() );
        }

        assertEquals( 10, eventService.getEvents( programA, organisationUnitA ).getEvents().size() );
    }

    @Test
    public void testGetPersonsByProgramStageOrgUnit()
    {
        for ( int i = 0; i < 10; i++ )
        {
            Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

            ImportSummary importSummary = eventService.saveEvent( event );
            assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
            assertNotNull( importSummary.getReference() );
        }

        assertEquals( 10, eventService.getEvents( programStageA, organisationUnitA ).getEvents().size() );
    }

    @Test
    public void testGetPersonsByProgramProgramStageOrgUnit()
    {
        for ( int i = 0; i < 10; i++ )
        {
            Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

            ImportSummary importSummary = eventService.saveEvent( event );
            assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
            assertNotNull( importSummary.getReference() );
        }

        assertEquals( 10, eventService.getEvents( programA, programStageA, organisationUnitA ).getEvents().size() );
    }

    @Test
    public void testGetPersonsByProgramStageInstance()
    {
        Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

        ImportSummary importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
        assertNotNull( importSummary.getReference() );

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( importSummary.getReference() );

        assertNotNull( programStageInstance );
        assertNotNull( eventService.getEvent( programStageInstance ) );
    }

    @Test
    public void testGetEventByUid()
    {
        Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

        ImportSummary importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
        assertNotNull( importSummary.getReference() );

        assertNotNull( eventService.getEvent( importSummary.getReference() ) );
    }

    @Test
    public void testSaveEvent()
    {
        Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );
        ImportSummary importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
        assertEquals( 0, importSummary.getConflicts().size() );
        assertNotNull( importSummary.getReference() );

        event = eventService.getEvent( importSummary.getReference() );
        assertNotNull( event );
        assertEquals( "10", event.getDataValues().get( 0 ).getValue() );
    }

    @Test
    public void testUpdateEvent()
    {
        Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

        ImportSummary importSummary = eventService.saveEvent( event );

        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
        assertNotNull( importSummary.getReference() );
        assertEquals( "10", event.getDataValues().get( 0 ).getValue() );

        event = eventService.getEvent( importSummary.getReference() );
        event.getDataValues().get( 0 ).setValue( "254" );
        eventService.updateEvent( event );

        event = eventService.getEvent( importSummary.getReference() );
        assertEquals( "254", event.getDataValues().get( 0 ).getValue() );
    }

    @Test
    public void testDeleteEvent()
    {
        Event event = createEvent( programA.getUid(), organisationUnitA.getUid() );

        ImportSummary importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
        assertNotNull( importSummary.getReference() );

        event = eventService.getEvent( importSummary.getReference() );
        assertNotNull( event );
        eventService.deleteEvent( event );
        event = eventService.getEvent( importSummary.getReference() );
        assertNull( event );
    }

    private Event createEvent( String program, String orgUnit )
    {
        Event event = new Event();
        event.setProgram( program );
        event.setOrgUnit( orgUnit );

        event.getDataValues().add( new DataValue( dataElementA.getUid(), "10" ) );

        return event;
    }
}
