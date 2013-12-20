package org.hisp.dhis.validation;

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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.user.UserAuthorityGroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement( localName = "validationRuleGroup", namespace = DxfNamespaces.DXF_2_0)
public class ValidationRuleGroup
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 436511421834390504L;

    private String description;

    @Scanned
    private Set<ValidationRule> members = new HashSet<ValidationRule>();
    
    private Set<UserAuthorityGroup> userAuthorityGroupsToAlert = new HashSet<UserAuthorityGroup>();
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------     

    public ValidationRuleGroup()
    {
    }

    public ValidationRuleGroup( String name, String description, Set<ValidationRule> members )
    {
        this.name = name;
        this.description = description;
        this.members = members;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addValidationRule( ValidationRule validationRule )
    {
        members.add( validationRule );
        validationRule.getGroups().add( this );
    }

    public void removeValidationRule( ValidationRule validationRule )
    {
        members.remove( validationRule );
        validationRule.getGroups().remove( this );
    }

    public void removeAllValidationRules()
    {
        members.clear();
    }
    
    /**
     * Indicates whether this group has user roles to alert.
     */
    public boolean hasUserRolesToAlert()
    {
        return userAuthorityGroupsToAlert != null && !userAuthorityGroupsToAlert.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------     

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty( value = "validationRules" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "validationRules", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "validationRule", namespace = DxfNamespaces.DXF_2_0)
    public Set<ValidationRule> getMembers()
    {
        return members;
    }

    public void setMembers( Set<ValidationRule> members )
    {
        this.members = members;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class } )
    @JacksonXmlElementWrapper( localName = "userRolesToAlert", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "userRoleToAlert", namespace = DxfNamespaces.DXF_2_0)
    public Set<UserAuthorityGroup> getUserAuthorityGroupsToAlert()
    {
        return userAuthorityGroupsToAlert;
    }

    public void setUserAuthorityGroupsToAlert( Set<UserAuthorityGroup> userAuthorityGroupsToAlert )
    {
        this.userAuthorityGroupsToAlert = userAuthorityGroupsToAlert;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            ValidationRuleGroup validationRuleGroup = (ValidationRuleGroup) other;

            description = validationRuleGroup.getDescription() == null ? description : validationRuleGroup.getDescription();

            removeAllValidationRules();

            for ( ValidationRule validationRule : validationRuleGroup.getMembers() )
            {
                addValidationRule( validationRule );
            }
        }
    }
}
