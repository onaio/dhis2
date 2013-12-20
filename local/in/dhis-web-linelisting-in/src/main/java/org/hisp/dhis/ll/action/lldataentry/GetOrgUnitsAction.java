package org.hisp.dhis.ll.action.lldataentry;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetOrgUnitsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private OrganisationUnit orgUnit;

    public OrganisationUnit getOrgUnit()
    {
        return orgUnit;
    }

    private Integer orgUnitLevel;

    public Integer getOrgUnitLevel()
    {
        return orgUnitLevel;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        /* OrganisationUnit */
        if ( orgUnitId != null )
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }

        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );

        return SUCCESS;
    }

}
