package org.hisp.dhis.dxf2.events.person;

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

import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.Program;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public interface PersonService
{
    public void setFormat( I18nFormat format );

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    Persons getPersons();

    Person getPerson( Identifier identifier );

    Persons getPersons( OrganisationUnit organisationUnit );

    Persons getPersons( OrganisationUnit organisationUnit, String nameLike );

    Persons getPersons( Program program );

    Persons getPersons( OrganisationUnit organisationUnit, Program program );

    Persons getPersons( Collection<Patient> patients );

    Person getPerson( String uid );

    Person getPerson( Patient patient );

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    ImportSummaries savePersonXml( InputStream inputStream ) throws IOException;

    ImportSummaries savePersonJson( InputStream inputStream ) throws IOException;

    ImportSummary savePerson( Person person );

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    ImportSummary updatePersonXml( String id, InputStream inputStream ) throws IOException;

    ImportSummary updatePersonJson( String id, InputStream inputStream ) throws IOException;

    ImportSummary updatePerson( Person person );

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    void deletePerson( Person person );
}
