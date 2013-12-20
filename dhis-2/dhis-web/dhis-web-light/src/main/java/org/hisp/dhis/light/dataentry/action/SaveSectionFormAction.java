package org.hisp.dhis.light.dataentry.action;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.struts2.StrutsStatics;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.light.utils.FormUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author mortenoh
 */
public class SaveSectionFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private FormUtils formUtils;

    public void setFormUtils( FormUtils formUtils )
    {
        this.formUtils = formUtils;
    }

    public FormUtils getFormUtils()
    {
        return formUtils;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    private String isoPeriod;

    public String getIsoPeriod()
    {
        return isoPeriod;
    }

    public void setIsoPeriod( String isoPeriod )
    {
        this.isoPeriod = isoPeriod;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public Integer getDataSetId()
    {
        return dataSetId;
    }

    private Integer sectionId;

    public void setSectionId( Integer sectionId )
    {
        this.sectionId = sectionId;
    }

    public Integer getSectionId()
    {
        return sectionId;
    }

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    private Map<String, String> dataValues = new HashMap<String, String>();

    public Map<String, String> getDataValues()
    {
        return dataValues;
    }

    private Map<String, DeflatedDataValue> validationViolations = new HashMap<String, DeflatedDataValue>();

    public Map<String, DeflatedDataValue> getValidationViolations()
    {
        return validationViolations;
    }

    private Map<String, String> typeViolations = new HashMap<String, String>();

    public Map<String, String> getTypeViolations()
    {
        return typeViolations;
    }

    private Boolean complete = false;

    public void setComplete( Boolean complete )
    {
        this.complete = complete;
    }

    public Boolean getComplete()
    {
        return complete;
    }

    private Boolean validated;

    public void setValidated( Boolean validated )
    {
        this.validated = validated;
    }

    public Boolean getValidated()
    {
        return validated;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        Validate.notNull( organisationUnitId );
        Validate.notNull( isoPeriod );
        Validate.notNull( dataSetId );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        Period period = periodService.getPeriod( isoPeriod );

        boolean needsValidation = false;

        dataSet = dataSetService.getDataSet( dataSetId );

        String storedBy = currentUserService.getCurrentUsername();

        if ( StringUtils.isBlank( storedBy ) )
        {
            storedBy = "[unknown]";
        }

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get( StrutsStatics.HTTP_REQUEST );
        Map<String, String> parameterMap = ContextUtils.getParameterMap( request );

        for ( String key : parameterMap.keySet() )
        {
            if ( key.startsWith( "DE" ) && key.indexOf( "OC" ) != -1 )
            {
                String[] splitKey = key.split( "OC" );
                Integer dataElementId = Integer.parseInt( splitKey[0].substring( 2 ) );
                Integer optionComboId = Integer.parseInt( splitKey[1] );
                String value = parameterMap.get( key );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = categoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                DataValue dataValue = dataValueService
                    .getDataValue( organisationUnit, dataElement, period, optionCombo );

                value = value.trim();
                Boolean valueIsEmpty = (value == null || value.length() == 0);

                // validate types
                Boolean correctType = true;
                String type = dataElement.getType();
                String numberType = dataElement.getNumberType();

                if ( !valueIsEmpty )
                {
                    if ( type.equals( DataElement.VALUE_TYPE_STRING ) )
                    {
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_BOOL ) )
                    {
                        if ( !valueIsEmpty && !FormUtils.isBoolean( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_boolean" ) );
                        }
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_DATE ) )
                    {
                        if ( !FormUtils.isDate( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_date" ) );
                        }
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_INT )
                        && numberType.equals( DataElement.VALUE_TYPE_NUMBER ) )
                    {
                        if ( !FormUtils.isNumber( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_number" ) );
                        }
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_INT )
                        && numberType.equals( DataElement.VALUE_TYPE_INT ) )
                    {
                        if ( !FormUtils.isInteger( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_integer" ) );
                        }
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_INT )
                        && numberType.equals( DataElement.VALUE_TYPE_POSITIVE_INT ) )
                    {
                        if ( !FormUtils.isPositiveInteger( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_positive_integer" ) );
                        }
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_INT )
                        && numberType.equals( DataElement.VALUE_TYPE_NEGATIVE_INT ) )
                    {
                        if ( !FormUtils.isNegativeInteger( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_negative_integer" ) );
                        }
                    }
                    else if ( type.equals( DataElement.VALUE_TYPE_INT)
                        && numberType.equals( DataElement.VALUE_TYPE_ZERO_OR_POSITIVE_INT ) )
                    {
                        if ( !FormUtils.isZeroOrPositiveInteger( value ) )
                        {
                            correctType = false;
                            typeViolations.put( key, "\"" + value + "\"" + " " + i18n.getString( "is_invalid_zero_or_positive_integer" ) );
                        }
                    }
                }

                // nothing entered
                if ( valueIsEmpty || !correctType )
                {
                    if ( dataValue != null )
                    {
                        dataValueService.deleteDataValue( dataValue );
                    }
                }

                if ( correctType && !valueIsEmpty )
                {
                    if ( dataValue == null )
                    {
                        needsValidation = true;

                        dataValue = new DataValue( dataElement, period, organisationUnit, value, storedBy, new Date(),
                            null, optionCombo );
                        dataValueService.addDataValue( dataValue );
                    }
                    else
                    {
                        if ( !dataValue.getValue().equals( value ) )
                        {
                            needsValidation = true;

                            dataValue.setValue( value );
                            dataValue.setTimestamp( new Date() );
                            dataValue.setStoredBy( storedBy );

                            dataValueService.updateDataValue( dataValue );
                        }
                    }
                }
            }
        }

        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet, period,
            organisationUnit );

        if ( registration == null && complete )
        {
            registration = new CompleteDataSetRegistration();
            registration.setDataSet( dataSet );
            registration.setPeriod( period );
            registration.setSource( organisationUnit );
            registration.setDate( new Date() );
            registration.setStoredBy( storedBy );

            registrationService.saveCompleteDataSetRegistration( registration );
        }
        else if ( registration != null && !complete )
        {
            registrationService.deleteCompleteDataSetRegistration( registration );
        }

        if ( typeViolations.size() > 0 )
        {
            needsValidation = true;
        }

        if ( sectionId != null )
        {
            for ( Section section : dataSet.getSections() )
            {
                if ( section.getId() == sectionId )
                {
                    name = section.getName();
                    dataElements = section.getDataElements();

                    break;
                }
            }
        }
        else
        {
            name = "Default";
            dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
            Collections.sort( dataElements, new IdentifiableObjectNameComparator() );
        }

        dataValues = formUtils.getDataValueMap( organisationUnit, dataSet, period );

        validationViolations = formUtils.getValidationViolations( organisationUnit, dataElements, period );

        if ( needsValidation && (!validationViolations.isEmpty() || !typeViolations.isEmpty()) )
        {
            return ERROR;
        }

        validated = true;

        return SUCCESS;
    }
}
