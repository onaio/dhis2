package org.hisp.dhis.reports.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.ActionSupport;

public class GetOrgUnitsAction extends ActionSupport
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
    
    private String ouLevel;
    
    public String getOuLevel()
    {
        return ouLevel;
    }
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        /* OrganisationUnit */
        if(orgUnitId != null)
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );        
            ouLevel = ""+organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
            
            System.out.println(orgUnit.getId() + " ---- "+ orgUnit.getShortName());
        }

        return SUCCESS;
    }

}
