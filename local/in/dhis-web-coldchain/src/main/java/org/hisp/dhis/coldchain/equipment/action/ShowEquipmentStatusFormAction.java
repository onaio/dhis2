package org.hisp.dhis.coldchain.equipment.action;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }

    public Integer getEquipmentId()
    {
        return equipmentId;
    }
    
    private Equipment equipment;
    
    public Equipment getEquipment()
    {
        return equipment;
    }

    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        if ( equipmentId != null )
        {
            equipment = equipmentService.getEquipment( equipmentId );
        }
        
        //equipment.getOrganisationUnit().getName();
        //equipment.getModel().getName();
        
        return SUCCESS;
    }
}
