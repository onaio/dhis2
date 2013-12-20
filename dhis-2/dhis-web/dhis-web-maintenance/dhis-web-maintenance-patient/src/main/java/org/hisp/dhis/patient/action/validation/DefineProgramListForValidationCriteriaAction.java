package org.hisp.dhis.patient.action.validation;

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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.validation.ValidationCriteria;
import org.hisp.dhis.validation.ValidationCriteriaService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version DefineProgramListForValidationCriteriaAction.java May 17, 2010
 */
public class DefineProgramListForValidationCriteriaAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ValidationCriteriaService validationCriteriaService;

    private ProgramService programService;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------
    private Integer criteriaId;

    private Integer[] programIds;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setValidationCriteriaService( ValidationCriteriaService validationCriteriaService )
    {
        this.validationCriteriaService = validationCriteriaService;
    }

    public void setProgramIds( Integer[] programIds )
    {
        this.programIds = programIds;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setCriteriaId( Integer criteriaId )
    {
        this.criteriaId = criteriaId;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        ValidationCriteria validationCriteria = validationCriteriaService.getValidationCriteria( criteriaId );

        Set<Program> selectedProgram = new HashSet<Program>();

        if ( programIds != null )
        {
            for ( Integer id : programIds )
            {
                Program program = programService.getProgram( id );

                program.getPatientValidationCriteria().add( validationCriteria );

                selectedProgram.add( program );

                programService.updateProgram( program );

            }
        }

        Set<Program> removePrograms = new HashSet<Program>( programService.getAllPrograms() );
        removePrograms.removeAll( selectedProgram );

        for ( Program program : removePrograms )
        {
            program.getPatientValidationCriteria().remove( validationCriteria );

            programService.updateProgram( program );
        }

        return SUCCESS;
    }

}
