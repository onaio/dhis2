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
import org.hisp.dhis.common.adapter.JacksonPeriodTypeDeserializer;
import org.hisp.dhis.common.adapter.JacksonPeriodTypeSerializer;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.period.PeriodType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Kristian Nordal
 */
@JacksonXmlRootElement( localName = "validationRule", namespace = DxfNamespaces.DXF_2_0)
public class ValidationRule
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -9058559806538024350L;

    public static final String IMPORTANCE_HIGH = "high";
    public static final String IMPORTANCE_MEDIUM = "medium";
    public static final String IMPORTANCE_LOW = "low";

    public static final String RULE_TYPE_VALIDATION = "validation";
    public static final String RULE_TYPE_SURVEILLANCE = "surveillance";
    
    public static final String TYPE_STATISTICAL = "statistical";
    public static final String TYPE_ABSOLUTE = "absolute";

    /**
     * A description of the ValidationRule.
     */
    private String description;

    /**
     * The user-assigned importance of this rule (e.g. high, medium or low).
     */
    private String importance;
    
    /**
     * Whether this is a VALIDATION or MONITORING type rule.
     */
    private String ruleType;

    /**
     * Whether this is a STATISTICAL or ABSOLUTE rule (only ABSOLUTE rules are currently implemented!)
     */
    private String type;

    /**
     * The comparison operator to compare left and right expressions in the rule.
     */
    private Operator operator;

    /**
     * The left-side expression to be compared against the right side.
     */
    private Expression leftSide;

    /**
     * The right-side expression to be compared against the left side.
     */
    private Expression rightSide;

    /**
     * The set of ValidationRuleGroups to which this ValidationRule belongs.
     */
    private Set<ValidationRuleGroup> groups = new HashSet<ValidationRuleGroup>();

    /**
     * The organisation unit level at which this rule is evaluated (Monitoring-type rules only).
     */
    private Integer organisationUnitLevel;

    /**
     * The type of period in which this rule is evaluated.
     */
    private PeriodType periodType;
    
    /**
     * The number of sequential right-side periods from which to collect samples
     * to average (Monitoring-type rules only). Sequential periods are those
     * immediately preceding (or immediately following in previous years) the selected period.
     */
    private Integer sequentialSampleCount; // Number of sequential right-side samples to average.

    /**
     * The number of annual right-side periods from which to collect samples
     * to average (Monitoring-type rules only). Annual periods are from previous
     * years. Samples collected from previous years can also include sequential
     * periods adjacent to the equivalent period in previous years.
     */
    private Integer annualSampleCount; // Number of (previous) annual right-side samples to average.

    /**
     * The number of high values sampled from previous periods that are discarded before averaging.
     */
    private Integer highOutliers;
    
    /**
     * The number of low values sampled from previous periods that are discarded before averaging.
     */
    private Integer lowOutliers;

    // -------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------- 

    public ValidationRule()
    {
    }

    public ValidationRule( String name, String description, String type,
        Operator operator, Expression leftSide, Expression rightSide )
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------- 

    /**
     * Clears the left-side and right-side expressions. This can be useful, for example,
     * before changing the validation rule period type, because the data elements
     * allowed in the expressions depend on the period type.
     */
    public void clearExpressions()
    {
        this.leftSide = null;
        this.rightSide = null;
    }

    /**
     * Joins a validation rule group.
     * 
     * @param validationRuleGroup the group to join.
     */
    public void addValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        groups.add( validationRuleGroup );
        validationRuleGroup.getMembers().add( this );
    }

    /**
     * Leaves a validation rule group.
     * 
     * @param validationRuleGroup the group to leave.
     */
    public void removeValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        groups.remove( validationRuleGroup );
        validationRuleGroup.getMembers().remove( this );
    }

    /**
     * Gets the validation rule description, but returns the validation rule name
     * if there is no description.
     * 
     * @return the description (or name).
     */
    public String getDescriptionNameFallback()
    {
        return description != null && !description.trim().isEmpty() ? description : name;
    }

    /**
     * Gets the data elements to evaluate for the current period. For validation-type
     * rules this means all data elements. For monitoring-type rules this means just
     * the left side elements.
     * 
     * @return the data elements to evaluate for the current period.
     */
    public Set<DataElement> getCurrentDataElements()
    {
    	Set<DataElement> currentDataElements = leftSide.getDataElementsInExpression();
    	
    	if ( RULE_TYPE_VALIDATION.equals( ruleType ) )
    	{
    	    currentDataElements = new HashSet<DataElement>( currentDataElements );
    	    currentDataElements.addAll( rightSide.getDataElementsInExpression() );
    	}
    	
    	return currentDataElements;
    }

    /**
     * Gets the data elements to compare against for past periods. For validation-type
     * rules this returns null. For monitoring-type rules this is just the
     * right side elements.
     * 
     * @return the data elements to evaluate for past periods.
     */
    public Set<DataElement> getPastDataElements()
    {
        return RULE_TYPE_VALIDATION.equals( ruleType ) ? null : rightSide.getDataElementsInExpression();
    }
    
    /**
     * Indicates whether this validation rule has user roles to alert.
     */
    public boolean hasUserRolesToAlert()
    {
        for ( ValidationRuleGroup group : groups )
        {
            if ( group.hasUserRolesToAlert() )
            {
                return true;
            }
        }
        
        return false;
    }

    // -------------------------------------------------------------------------
    // Set and get methods
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

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getImportance()
    {
        return importance != null && !importance.isEmpty() ? importance : IMPORTANCE_MEDIUM;
    }

    public void setImportance( String importance )
    {
        this.importance = importance;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Integer getOrganisationUnitLevel()
    {
        return organisationUnitLevel;
    }

    public void setOrganisationUnitLevel( Integer organisationUnitLevel )
    {
        this.organisationUnitLevel = organisationUnitLevel;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getRuleType()
    {
        return ruleType != null && !ruleType.isEmpty() ? ruleType : RULE_TYPE_VALIDATION;
    }

    public void setRuleType( String ruleType )
    {
        this.ruleType = ruleType;
    }

    @JsonProperty
    @JsonSerialize( using = JacksonPeriodTypeSerializer.class )
    @JsonDeserialize( using = JacksonPeriodTypeDeserializer.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Integer getSequentialSampleCount()
    {
        return sequentialSampleCount;
    }

    public void setSequentialSampleCount( Integer sequentialSampleCount )
    {
        this.sequentialSampleCount = sequentialSampleCount;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Integer getAnnualSampleCount()
    {
        return annualSampleCount;
    }

    public void setAnnualSampleCount( Integer annualSampleCount )
    {
        this.annualSampleCount = annualSampleCount;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Integer getHighOutliers()
    {
        return highOutliers;
    }

    public void setHighOutliers( Integer highOutliers )
    {
        this.highOutliers = highOutliers;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Integer getLowOutliers()
    {
        return lowOutliers;
    }

    public void setLowOutliers( Integer lowOutliers )
    {
        this.lowOutliers = lowOutliers;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Operator getOperator()
    {
        return operator;
    }

    public void setOperator( Operator operator )
    {
        this.operator = operator;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Expression getLeftSide()
    {
        return leftSide;
    }

    public void setLeftSide( Expression leftSide )
    {
        this.leftSide = leftSide;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Expression getRightSide()
    {
        return rightSide;
    }

    public void setRightSide( Expression rightSide )
    {
        this.rightSide = rightSide;
    }

    @JsonProperty( value = "validationRuleGroups" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class} )
    @JacksonXmlElementWrapper( localName = "validationRuleGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "validationRuleGroup", namespace = DxfNamespaces.DXF_2_0)
    public Set<ValidationRuleGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<ValidationRuleGroup> groups )
    {
        this.groups = groups;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            ValidationRule validationRule = (ValidationRule) other;

            description = validationRule.getDescription() == null ? description : validationRule.getDescription();
            type = validationRule.getType() == null ? type : validationRule.getType();
            operator = validationRule.getOperator() == null ? operator : validationRule.getOperator();
            periodType = validationRule.getPeriodType() == null ? periodType : validationRule.getPeriodType();

            if ( leftSide != null && validationRule.getLeftSide() != null )
            {
                leftSide.mergeWith( validationRule.getLeftSide() );
            }
            
            if ( rightSide != null && validationRule.getRightSide() != null )
            {
                rightSide.mergeWith( validationRule.getRightSide() );
            }

            groups.clear();
        }
    }
}
