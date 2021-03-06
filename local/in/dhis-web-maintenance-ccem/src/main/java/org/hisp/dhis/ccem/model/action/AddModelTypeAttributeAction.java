package org.hisp.dhis.ccem.model.action;

import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionService;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class AddModelTypeAttributeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }

    private ModelTypeAttributeOptionService modelTypeAttributeOptionService;
    
    public void setModelTypeAttributeOptionService( ModelTypeAttributeOptionService modelTypeAttributeOptionService )
    {
        this.modelTypeAttributeOptionService = modelTypeAttributeOptionService;
    }
    
    private OptionService optionService;
    
    public void setOptionService(OptionService optionService) 
    {
		this.optionService = optionService;
	}

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }
    
    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }
    
    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }
    
    private boolean mandatory;

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }
    
    private Integer noChars;

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }
    
    private List<String> attrOptions;

    public void setAttrOptions( List<String> attrOptions )
    {
        this.attrOptions = attrOptions;
    }
    
    private boolean display = false;
    
    public void setDisplay( boolean display )
    {
        this.display = display;
    }

    private Integer optionSetId;
    
    public void setOptionSetId(Integer optionSetId) 
    {
		this.optionSetId = optionSetId;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
	public String execute() throws Exception
    {
        ModelTypeAttribute modelTypeAttribute = new ModelTypeAttribute();
        
        modelTypeAttribute.setName( name );
        modelTypeAttribute.setDescription( description );
        modelTypeAttribute.setValueType( valueType );
        modelTypeAttribute.setMandatory( mandatory );
        modelTypeAttribute.setNoChars( noChars );
        modelTypeAttribute.setDisplay( display );
        
        if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( valueType ) )
        {
        	if( optionSetId != -1 )
        	{
        		OptionSet optionSet = optionService.getOptionSet( optionSetId );
        		modelTypeAttribute.setOptionSet( optionSet );
        	}
        }
        
        modelTypeAttributeService.addModelTypeAttribute( modelTypeAttribute );
        
        /*
        if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( valueType ) )
        {
            ModelTypeAttributeOption option = null;
            for ( String optionName : attrOptions )
            {
                option = new ModelTypeAttributeOption();
                option.setName( optionName );
                option.setModelTypeAttribute( modelTypeAttribute );
                
                modelTypeAttributeOptionService.addModelTypeAttributeOption( option );
            }
        }
        */
        
        return SUCCESS;
    }
    
}
