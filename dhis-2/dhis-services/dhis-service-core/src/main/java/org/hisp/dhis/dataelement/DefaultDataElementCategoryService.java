package org.hisp.dhis.dataelement;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericDimensionalObjectStore;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 */
@Transactional
public class DefaultDataElementCategoryService
    implements DataElementCategoryService
{
    private static final Log log = LogFactory.getLog( DefaultDataElementCategoryService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericDimensionalObjectStore<DataElementCategory> dataElementCategoryStore;

    public void setDataElementCategoryStore( GenericDimensionalObjectStore<DataElementCategory> dataElementCategoryStore )
    {
        this.dataElementCategoryStore = dataElementCategoryStore;
    }

    private GenericDimensionalObjectStore<DataElementCategoryOption> dataElementCategoryOptionStore;

    public void setDataElementCategoryOptionStore(
        GenericDimensionalObjectStore<DataElementCategoryOption> dataElementCategoryOptionStore )
    {
        this.dataElementCategoryOptionStore = dataElementCategoryOptionStore;
    }

    private GenericIdentifiableObjectStore<DataElementCategoryCombo> dataElementCategoryComboStore;

    public void setDataElementCategoryComboStore(
        GenericIdentifiableObjectStore<DataElementCategoryCombo> dataElementCategoryComboStore )
    {
        this.dataElementCategoryComboStore = dataElementCategoryComboStore;
    }

    private GenericIdentifiableObjectStore<DataElementCategoryOptionCombo> dataElementCategoryOptionComboStore;

    public void setDataElementCategoryOptionComboStore(
        GenericIdentifiableObjectStore<DataElementCategoryOptionCombo> dataElementCategoryOptionComboStore )
    {
        this.dataElementCategoryOptionComboStore = dataElementCategoryOptionComboStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Category
    // -------------------------------------------------------------------------

    public int addDataElementCategory( DataElementCategory dataElementCategory )
    {
        return dataElementCategoryStore.save( dataElementCategory );
    }

    public void updateDataElementCategory( DataElementCategory dataElementCategory )
    {
        dataElementCategoryStore.update( dataElementCategory );
    }

    public void deleteDataElementCategory( DataElementCategory dataElementCategory )
    {
        dataElementCategoryStore.delete( dataElementCategory );
    }

    public Collection<DataElementCategory> getDataDimensionDataElementCategories()
    {
        Collection<DataElementCategory> categories = getAllDataElementCategories();
        
        FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory category )
            {
                return category != null && category.isDataDimension();
            }            
        } );
        
        return categories;
    }
    
    public Collection<DataElementCategory> getAllDataElementCategories()
    {
        return i18n( i18nService, dataElementCategoryStore.getAll() );
    }

    public DataElementCategory getDataElementCategory( int id )
    {
        return i18n( i18nService, dataElementCategoryStore.get( id ) );
    }

    public DataElementCategory getDataElementCategory( String uid )
    {
        return i18n( i18nService, dataElementCategoryStore.getByUid( uid ) );
    }

    public Collection<DataElementCategory> getDataElementCategories( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategory> categories = getAllDataElementCategories();

        return identifiers == null ? categories : FilterUtils.filter( categories, new Filter<DataElementCategory>()
        {
            public boolean retain( DataElementCategory object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }
    
    public Collection<DataElementCategory> getDataElementCategoriesByUid( Collection<String> uids )
    {
        return dataElementCategoryStore.getByUid( uids );
    }

    public DataElementCategory getDataElementCategoryByName( String name )
    {
        List<DataElementCategory> dataElementCategories = new ArrayList<DataElementCategory>(
            dataElementCategoryStore.getAllEqName( name ) );

        if ( dataElementCategories.isEmpty() )
        {
            return null;
        }

        return i18n( i18nService, dataElementCategories.get( 0 ) );
    }

    // -------------------------------------------------------------------------
    // CategoryOption
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        return dataElementCategoryOptionStore.save( dataElementCategoryOption );
    }

    public void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        dataElementCategoryOptionStore.update( dataElementCategoryOption );
    }

    public void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        dataElementCategoryOptionStore.delete( dataElementCategoryOption );
    }

    public DataElementCategoryOption getDataElementCategoryOption( int id )
    {
        return i18n( i18nService, dataElementCategoryOptionStore.get( id ) );
    }

    public DataElementCategoryOption getDataElementCategoryOption( String uid )
    {
        return i18n( i18nService, dataElementCategoryOptionStore.getByUid( uid ) );
    }

    public DataElementCategoryOption getDataElementCategoryOptionByName( String name )
    {
        return i18n( i18nService, dataElementCategoryOptionStore.getByName( name ) );
    }

    public DataElementCategoryOption getDataElementCategoryOptionByCode( String code )
    {
        return i18n( i18nService, dataElementCategoryOptionStore.getByCode( code ) );
    }

    public Collection<DataElementCategoryOption> getDataElementCategoryOptions( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOption> categoryOptions = getAllDataElementCategoryOptions();

        return identifiers == null ? categoryOptions : FilterUtils.filter( categoryOptions,
            new Filter<DataElementCategoryOption>()
            {
                public boolean retain( DataElementCategoryOption object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsByUid( Collection<String> uids )
    {
        return dataElementCategoryOptionStore.getByUid( uids );
    }

    public Collection<DataElementCategoryOption> getAllDataElementCategoryOptions()
    {
        return i18n( i18nService, dataElementCategoryOptionStore.getAll() );
    }

    // -------------------------------------------------------------------------
    // CategoryCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        return dataElementCategoryComboStore.save( dataElementCategoryCombo );
    }

    public void updateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        dataElementCategoryComboStore.save( dataElementCategoryCombo );
    }

    public void deleteDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo )
    {
        dataElementCategoryComboStore.delete( dataElementCategoryCombo );
    }

    public Collection<DataElementCategoryCombo> getAllDataElementCategoryCombos()
    {
        return i18n( i18nService, dataElementCategoryComboStore.getAll() );
    }

    public DataElementCategoryCombo getDataElementCategoryCombo( int id )
    {
        return i18n( i18nService, dataElementCategoryComboStore.get( id ) );
    }

    public DataElementCategoryCombo getDataElementCategoryCombo( String uid )
    {
        return i18n( i18nService, dataElementCategoryComboStore.getByUid( uid ) );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombos( final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryCombo> categoryCombos = getAllDataElementCategoryCombos();

        return identifiers == null ? categoryCombos : FilterUtils.filter( categoryCombos,
            new Filter<DataElementCategoryCombo>()
            {
                public boolean retain( DataElementCategoryCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public DataElementCategoryCombo getDataElementCategoryComboByName( String name )
    {
        return i18n( i18nService, dataElementCategoryComboStore.getByName( name ) );
    }

    // -------------------------------------------------------------------------
    // CategoryOptionCombo
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        return dataElementCategoryOptionComboStore.save( dataElementCategoryOptionCombo );
    }

    public void updateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        dataElementCategoryOptionComboStore.update( dataElementCategoryOptionCombo );
    }

    public void deleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        dataElementCategoryOptionComboStore.delete( dataElementCategoryOptionCombo );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( int id )
    {
        return dataElementCategoryOptionComboStore.get( id );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo( String uid )
    {
        return dataElementCategoryOptionComboStore.getByUid( uid );
    }

    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos(
        final Collection<Integer> identifiers )
    {
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = getAllDataElementCategoryOptionCombos();

        return identifiers == null ? categoryOptionCombos : FilterUtils.filter( categoryOptionCombos,
            new Filter<DataElementCategoryOptionCombo>()
            {
                public boolean retain( DataElementCategoryOptionCombo object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public Collection<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombosByUid( Collection<String> uids )
    {
        return dataElementCategoryOptionComboStore.getByUid( uids );
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        Collection<DataElementCategoryOption> categoryOptions )
    {
        for ( DataElementCategoryOptionCombo categoryOptionCombo : getAllDataElementCategoryOptionCombos() )
        {
            if ( CollectionUtils.isEqualCollection( categoryOptions, categoryOptionCombo.getCategoryOptions() ) )
            {
                return categoryOptionCombo;
            }
        }

        return null;
    }

    public DataElementCategoryOptionCombo getDataElementCategoryOptionCombo(
        DataElementCategoryOptionCombo categoryOptionCombo )
    {
        for ( DataElementCategoryOptionCombo dcoc : getAllDataElementCategoryOptionCombos() )
        {
            // -----------------------------------------------------------------
            // Hibernate puts proxies on associations and makes the native
            // equals methods unusable
            // -----------------------------------------------------------------

            if ( dcoc.equalsOnName( categoryOptionCombo ) )
            {
                return dcoc;
            }
        }

        return null;
    }

    public Collection<DataElementCategoryOptionCombo> getAllDataElementCategoryOptionCombos()
    {
        return dataElementCategoryOptionComboStore.getAll();
    }

    public void generateDefaultDimension()
    {
        // ---------------------------------------------------------------------
        // DataElementCategoryOption
        // ---------------------------------------------------------------------

        DataElementCategoryOption categoryOption = new DataElementCategoryOption(
            DataElementCategoryOption.DEFAULT_NAME );

        addDataElementCategoryOption( categoryOption );

        // ---------------------------------------------------------------------
        // DataElementCategory
        // ---------------------------------------------------------------------

        DataElementCategory category = new DataElementCategory( DataElementCategory.DEFAULT_NAME );
        category.addDataElementCategoryOption( categoryOption );
        addDataElementCategory( category );

        // ---------------------------------------------------------------------
        // DataElementCategoryCombo
        // ---------------------------------------------------------------------

        DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        categoryCombo.addDataElementCategory( category );
        addDataElementCategoryCombo( categoryCombo );

        // ---------------------------------------------------------------------
        // DataElementCategoryOptionCombo
        // ---------------------------------------------------------------------

        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( categoryCombo );
        categoryOptionCombo.addDataElementCategoryOption( categoryOption );

        addDataElementCategoryOptionCombo( categoryOptionCombo );

        Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();
        categoryOptionCombos.add( categoryOptionCombo );
        categoryCombo.setOptionCombos( categoryOptionCombos );

        updateDataElementCategoryCombo( categoryCombo );

        categoryOption.setCategoryOptionCombos( categoryOptionCombos );
        updateDataElementCategoryOption( categoryOption );
    }

    public DataElementCategoryOptionCombo getDefaultDataElementCategoryOptionCombo()
    {
        DataElementCategoryCombo categoryCombo = getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        return categoryCombo.getOptionCombos().iterator().next();
    }

    public Collection<DataElementOperand> populateOperands( Collection<DataElementOperand> operands )
    {
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo categoryOptionCombo = getDataElementCategoryOptionCombo( operand
                .getOptionComboId() );

            operand.updateProperties( dataElement, categoryOptionCombo );
        }

        return operands;
    }

    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements, boolean includeTotals )
    {
        Collection<DataElementOperand> operands = new ArrayList<DataElementOperand>();

        for ( DataElement dataElement : dataElements )
        {
            if ( !dataElement.getCategoryCombo().isDefault() && includeTotals )
            {
                DataElementOperand operand = new DataElementOperand();
                operand.updateProperties( dataElement );

                operands.add( operand );
            }

            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo()
                .getSortedOptionCombos() )
            {
                DataElementOperand operand = new DataElementOperand();
                operand.updateProperties( dataElement, categoryOptionCombo );

                operands.add( operand );
            }
        }

        return operands;
    }

    public Collection<DataElementOperand> getOperands( Collection<DataElement> dataElements )
    {
        return getOperands( dataElements, false );
    }

    public Collection<DataElementOperand> getOperandsLikeName( String name )
    {
        Collection<DataElement> dataElements = dataElementService.getDataElementsLikeName( name );

        return getOperands( dataElements );
    }

    public Collection<DataElementOperand> getFullOperands( Collection<DataElement> dataElements )
    {
        Collection<DataElementOperand> operands = new ArrayList<DataElementOperand>();

        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
            {
                DataElementOperand operand = new DataElementOperand( dataElement, categoryOptionCombo );
                operand.updateProperties( dataElement, categoryOptionCombo );

                operands.add( operand );
            }
        }

        return operands;
    }

    public void generateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        categoryCombo.generateOptionCombos();

        for ( DataElementCategoryOptionCombo optionCombo : categoryCombo.getOptionCombos() )
        {
            categoryCombo.getOptionCombos().add( optionCombo );
            addDataElementCategoryOptionCombo( optionCombo );
        }

        updateDataElementCategoryCombo( categoryCombo );
    }

    public void updateOptionCombos( DataElementCategory category )
    {
        for ( DataElementCategoryCombo categoryCombo : getAllDataElementCategoryCombos() )
        {
            if ( categoryCombo.getCategories().contains( category ) )
            {
                updateOptionCombos( categoryCombo );
            }
        }
    }

    public void updateOptionCombos( DataElementCategoryCombo categoryCombo )
    {
        List<DataElementCategoryOptionCombo> generatedOptionCombos = categoryCombo.generateOptionCombosList();
        Set<DataElementCategoryOptionCombo> persistedOptionCombos = categoryCombo.getOptionCombos();

        boolean modified = false;
        
        for ( DataElementCategoryOptionCombo optionCombo : generatedOptionCombos )
        {
            if ( !persistedOptionCombos.contains( optionCombo ) )
            {                
                categoryCombo.getOptionCombos().add( optionCombo );
                addDataElementCategoryOptionCombo( optionCombo );

                log.info( "Added missing category option combo: " + optionCombo + " for category combo: " + categoryCombo.getName() );
                modified = true;
            }
        }

        if ( modified )
        {        
            updateDataElementCategoryCombo( categoryCombo );
        }
    }
    
    public void updateAllOptionCombos()
    {
        Collection<DataElementCategoryCombo> categoryCombos = getAllDataElementCategoryCombos();
        
        for ( DataElementCategoryCombo categoryCombo : categoryCombos )
        {
            updateOptionCombos( categoryCombo );
        }
    }

    public Map<String, Integer> getDataElementCategoryOptionComboUidIdMap()
    {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for ( DataElementCategoryOptionCombo coc : getAllDataElementCategoryOptionCombos() )
        {
            map.put( coc.getUid(), coc.getId() );
        }

        return map;
    }

    public int getDataElementCategoryCount()
    {
        return dataElementCategoryStore.getCount();
    }

    public int getDataElementCategoryCountByName( String name )
    {
        return dataElementCategoryStore.getCountLikeName( name );
    }

    @Override
    public int getDataElementCategoryOptionCount()
    {
        return dataElementCategoryOptionStore.getCount();
    }

    @Override
    public int getDataElementCategoryOptionCountByName( String name )
    {
        return dataElementCategoryOptionStore.getCountLikeName( name );
    }

    @Override
    public int getDataElementCategoryOptionComboCount()
    {
        return dataElementCategoryOptionComboStore.getCount();
    }

    @Override
    public int getDataElementCategoryOptionComboCountByName( String name )
    {
        return dataElementCategoryOptionComboStore.getCountLikeName( name );
    }

    public Collection<DataElementCategory> getDataElementCategorysBetween( int first, int max )
    {
        return i18n( i18nService, dataElementCategoryStore.getAllOrderedName( first, max ) );
    }

    public Collection<DataElementCategory> getDataElementCategorysBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, dataElementCategoryStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    public int getDataElementCategoryComboCount()
    {
        return dataElementCategoryComboStore.getCount();
    }

    public int getDataElementCategoryComboCountByName( String name )
    {
        return dataElementCategoryComboStore.getCountLikeName( name );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetween( int first, int max )
    {
        return i18n( i18nService, dataElementCategoryComboStore.getAllOrderedName( first, max ) );
    }

    public Collection<DataElementCategoryCombo> getDataElementCategoryCombosBetweenByName( String name, int first,
        int max )
    {
        return i18n( i18nService, dataElementCategoryComboStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategorOptionsByConcept( Concept concept )
    {
        return dataElementCategoryOptionStore.getByConcept( concept );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategorysByConcept( Concept concept )
    {
        return i18n( i18nService, dataElementCategoryStore.getByConcept( concept ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoryBetween( int first, int max )
    {
        return i18n( i18nService, dataElementCategoryStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategory> getDataElementCategoryBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, dataElementCategoryStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetween( int first, int max )
    {
        return i18n( i18nService, dataElementCategoryOptionStore.getAllOrderedName( first, max ) );
    }

    @Override
    public Collection<DataElementCategoryOption> getDataElementCategoryOptionsBetweenByName( String name, int first,
        int max )
    {
        return i18n( i18nService, dataElementCategoryOptionStore.getAllLikeNameOrderedName( name, first, max ) );
    }
}
