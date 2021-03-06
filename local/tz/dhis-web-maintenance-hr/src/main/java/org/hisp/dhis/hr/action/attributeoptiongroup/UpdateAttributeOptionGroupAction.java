package org.hisp.dhis.hr.action.attributeoptiongroup;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.AttributeOptionsService;

import com.opensymphony.xwork2.Action;

public class UpdateAttributeOptionGroupAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeOptionGroupService attributeOptionGroupService;

    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
        this.attributeOptionGroupService = attributeOptionGroupService;
    }

    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    private String nameField;

    private String[] selectedAttributes;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    public void setSelectedAttributes( String[] selectedAttributes )
    {
        this.selectedAttributes = selectedAttributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        AttributeOptionGroup attributeOptionGroup = attributeOptionGroupService.getAttributeOptionGroup( id );

        attributeOptionGroup.setName( nameField );

        Set<AttributeOptions> attributesOptions = new HashSet<AttributeOptions>();
        for ( String attributeOptionsId : selectedAttributes )
        {
            AttributeOptions attributeOptions = attributeOptionsService.getAttributeOptions( Integer
                .parseInt( attributeOptionsId ) );
            attributesOptions.add( attributeOptions );
        }
        attributeOptionGroup.setMembers( attributesOptions );

        attributeOptionGroupService.updateAttributeOptionGroup( attributeOptionGroup );

        return SUCCESS;
    }
}