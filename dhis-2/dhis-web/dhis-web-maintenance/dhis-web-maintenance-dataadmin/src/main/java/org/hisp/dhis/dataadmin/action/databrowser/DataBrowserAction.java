package org.hisp.dhis.dataadmin.action.databrowser;

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

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.util.SessionUtils;

/**
 * @author espenjac, joakibj, briane, eivinhb
 * @version $Id DataBrowerAction.java Apr 06, 2010 ddhieu
 */
public class DataBrowserAction
    extends ActionSupport
{
    private boolean isZeroAdded = false;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        isZeroAdded = (showZeroCheckBox != null) && showZeroCheckBox.equals( TRUE );

        // Check if the second selected date is later than the first selected
        // date
        if ( nullIfEmpty( fromDate ) == null && nullIfEmpty( toDate ) == null )
        {
            if ( DateUtils.checkDates( fromDate, toDate ) )
            {
                return ERROR;
            }
        }

        // If set, change the current selected unit
        if ( selectedUnitChanger != null )
        {
            selectionTreeManager.setSelectedOrganisationUnit( organisationUnitService.getOrganisationUnit( Integer
                .parseInt( selectedUnitChanger ) ) );
        }

        selectedUnit = selectionTreeManager.getReloadedSelectedOrganisationUnit();

        // Checks if the selected unit is a leaf node of tree then
        // We must add parent as the same parameter value
        if ( parent == null && mode.equals( "OU" ) && selectedUnit != null && selectedUnit.getChildren().size() == 0 )
        {
            parent = selectedUnit.getId() + EMPTY;
        }

        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );

        if ( mode.equals( "DS" ) )
        {
            if ( parent != null )
            {
                // Show DataElement for a given DataSet
                Integer parentInt = Integer.parseInt( parent );

                grid = dataBrowserGridService.getCountDataElementsForDataSetInPeriod( parentInt, fromDate, toDate,
                    periodType, format, isZeroAdded );
            }
            else
            {
                // Get all DataSets
                grid = dataBrowserGridService.getDataSetsInPeriod( fromDate, toDate, periodType, format, isZeroAdded );
            }

            this.setSummary( true );
        }
        else if ( mode.equals( "DEG" ) )
        {
            // Get all DataElementGroup objects
            if ( parent != null )
            {
                // Show DataElement
                Integer parentInt = Integer.parseInt( parent );

                grid = dataBrowserGridService.getCountDataElementsForDataElementGroupInPeriod( parentInt, fromDate,
                    toDate, periodType, format, isZeroAdded );
            }
            else
            {
                grid = dataBrowserGridService.getDataElementGroupsInPeriod( fromDate, toDate, periodType, format,
                    isZeroAdded );
            }

            this.setSummary( true );
        }
        else if ( mode.equals( "OUG" ) )
        {
            if ( parent != null )
            {
                // Show DataElementGroups
                Integer parentInt = Integer.parseInt( parent );
                grid = dataBrowserGridService.getCountDataElementGroupsForOrgUnitGroupInPeriod( parentInt, fromDate,
                    toDate, periodType, format, isZeroAdded );
            }
            else
            {
                grid = dataBrowserGridService.getOrgUnitGroupsInPeriod( fromDate, toDate, periodType, format,
                    isZeroAdded );
            }

            this.setSummary( true );
        }
        else if ( mode.equals( "OU" ) )
        {
            selectedUnit = selectionTreeManager.getSelectedOrganisationUnit();

            if ( (drillDownCheckBox != null) && drillDownCheckBox.equals( TRUE ) )
            {
                parent = String.valueOf( selectedUnit.getId() );
            }

            // This one is used for itself
            if ( parent != null )
            {
                Integer parentInt = Integer.parseInt( parent );

                // Show DataElement values entered for this specified unit only
                grid = dataBrowserGridService.getRawDataElementsForOrgUnitInPeriod( parentInt, fromDate, toDate,
                    periodType, format, isZeroAdded );

                this.setSummary( false );
            }
            else if ( selectedUnit != null )
            {
                // Show the summary values for the immediate and descendant
                // units of the specified unit
                grid = dataBrowserGridService.getOrgUnitsInPeriod( selectedUnit.getId(), fromDate, toDate, periodType,
                    null, format, isZeroAdded );

                this.setSummary( true );
            }
            else
            {
                return ERROR;
            }
        }
        else
        {
            return ERROR;
        }

        // Set title to grid
        setGridTitle();

        // Convert column date names
        convertColumnNames( grid );

        // Do paging
        doPaging();

        // Set DataBrowserTable variable for PDF export
        SessionUtils.setSessionVar( KEY_DATABROWSERGRID, grid );

        return SUCCESS;
    }
}
