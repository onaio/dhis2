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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramStageService
    implements ProgramStageService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageStore programStageStore;

    public void setProgramStageStore( ProgramStageStore programStageStore )
    {
        this.programStageStore = programStageStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // ProgramStage implementation
    // -------------------------------------------------------------------------

    public int saveProgramStage( ProgramStage programStage )
    {
        return programStageStore.save( programStage );
    }

    public void deleteProgramStage( ProgramStage programStage )
    {
        programStageStore.delete( programStage );
    }

    public ProgramStage getProgramStage( int id )
    {
        return i18n( i18nService, programStageStore.get( id ) );
    }

    public ProgramStage getProgramStage( String uid )
    {
        return i18n( i18nService, programStageStore.getByUid( uid ) );
    }

    public List<ProgramStage> getProgramStageByName( String name )
    {
        return programStageStore.getAllEqName( name );
    }

    public ProgramStage getProgramStageByName( String name, Program program )
    {
        return i18n( i18nService, programStageStore.getByNameAndProgram( name, program ) );
    }

    public void updateProgramStage( ProgramStage programStage )
    {
        programStageStore.update( programStage );
    }

    public Collection<ProgramStage> getAllProgramStages()
    {
        return i18n( i18nService, programStageStore.getAll() );
    }

}
