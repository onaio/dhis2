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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.program.Program;

/**
 * @author Chau Thu Tran
 */
public class PatientRegistrationForm
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = -6000530171659755186L;

    public static String FIXED_ATTRIBUTE_FULLNAME = "fullName";

    public static String FIXED_ATTRIBUTE_GENDER = "gender";

    public static String FIXED_ATTRIBUTE_BIRTHDATE = "birthDate";

    public static String FIXED_ATTRIBUTE_AGE = "age";

    public static String FIXED_ATTRIBUTE_PHONE_NUMBER = "phoneNumber";

    public static String FIXED_ATTRIBUTE_DEATH_DATE = "deathDate";

    public static String FIXED_ATTRIBUTE_REGISTRATION_DATE = "registrationDate";

    public static String FIXED_ATTRIBUTE_IS_DEAD = "isDead";

    public static String FIXED_ATTRIBUTE_DOB_TYPE = "dobType";

    public static String FIXED_ATTRIBUTE_HEALTH_WORKER = "healthWorker";

    private Program program;

    private DataEntryForm dataEntryForm;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientRegistrationForm()
    {
    }

    public PatientRegistrationForm( Program program, DataEntryForm dataEntryForm )
    {
        this.program = program;
        this.dataEntryForm = dataEntryForm;
    }

    // TODO implement hashcode and equals

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }

}
