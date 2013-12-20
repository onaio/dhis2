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
import java.util.Iterator;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Chau Thu Tran
 * 
 * @version ProgramStageDataElementDeletionHandler.java Oct 5, 2010 11:06:03 PM
 */
public class ProgramStageDataElementDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageDataElementService programStageDEService;

    public void setProgramStageDEService( ProgramStageDataElementService programStageDEService )
    {
        this.programStageDEService = programStageDEService;
    }

    private ProgramStageSectionService programStageSectionService;

    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return ProgramStageDataElement.class.getSimpleName();
    }

    @Override
    public void deleteProgram( Program program )
    {
        // ---------------------------------------------------------------------
        // Delete Program Stage data elements
        // ---------------------------------------------------------------------

        Set<ProgramStage> programStages = program.getProgramStages();

        for ( ProgramStage programStage : programStages )
        {
            Collection<ProgramStageDataElement> psDataElements = programStageDEService.get( programStage );

            if ( psDataElements != null && psDataElements.size() > 0 )
            {
                for ( ProgramStageDataElement psDataElement : psDataElements )
                {
                    programStageDEService.deleteProgramStageDataElement( psDataElement );
                }

            }
        }
    }

    @Override
    public void deleteProgramStage( ProgramStage programStage )
    {
        Collection<ProgramStageDataElement> psDataElements = programStage.getProgramStageDataElements();
        for ( ProgramStageDataElement psDataElement : psDataElements )
        {
            programStageDEService.deleteProgramStageDataElement( psDataElement );
        }
    }

    @Override
    public void deleteProgramStageSection( ProgramStageSection programStageSection )
    {
        programStageSection.getProgramStageDataElements().clear();
        programStageSectionService.updateProgramStageSection( programStageSection );
    }

    @Override
    public String allowDeleteDataElement( DataElement dataElement )
    {
        if ( dataElement != null && DataElement.DOMAIN_TYPE_PATIENT.equals( dataElement.getDomainType() ) )
        {
            // TODO use a query which will be more efficient

            Iterator<ProgramStageDataElement> iterator = programStageDEService.getAllProgramStageDataElements()
                .iterator();

            while ( iterator.hasNext() )
            {
                ProgramStageDataElement element = iterator.next();

                if ( element.getDataElement() != null && element.getDataElement().equals( dataElement ) )
                {
                    programStageDEService.deleteProgramStageDataElement( element );
                }
            }
        }

        return null;
    }
}
