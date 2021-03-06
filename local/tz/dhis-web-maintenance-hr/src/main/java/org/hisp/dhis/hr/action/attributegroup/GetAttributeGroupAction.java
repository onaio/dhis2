package org.hisp.dhis.hr.action.attributegroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class GetAttributeGroupAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeGroupService attributeGroupService;

    public void setAttributeGroupService( AttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }
    
    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private AttributeGroup attributeGroups;

    public AttributeGroup getAttributeGroup()
    {
        return attributeGroups;
    } 
    
    private Collection<Attribute> attributes = new ArrayList<Attribute>();

    public Collection<Attribute> getAttribute()
    {
        return attributes;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        attributeGroups = attributeGroupService.getAttributeGroup( id );
        
        attributes = attributeService.getAllAttribute();

        return SUCCESS;
    }

}