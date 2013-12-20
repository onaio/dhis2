package org.hisp.dhis.api.controller.organisationunit;

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

import org.hisp.dhis.api.controller.AbstractCrudController;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.utils.WebUtils;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitByLevelComparator;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = OrganisationUnitController.RESOURCE_PATH)
public class OrganisationUnitController
    extends AbstractCrudController<OrganisationUnit>
{
    public static final String RESOURCE_PATH = "/organisationUnits";

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CurrentUserService currentUserService;

    @Override
    protected List<OrganisationUnit> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<OrganisationUnit> entityList;

        Date lastUpdated = options.getLastUpdated();

        boolean levelSorted = options.getOptions().containsKey( "levelSorted" ) && Boolean.parseBoolean( options.getOptions().get( "levelSorted" ) );

        Integer level = null;

        Integer maxLevel = null;

        if ( options.getOptions().containsKey( "level" ) )
        {
            level = Integer.parseInt( options.getOptions().get( "level" ) );
        }

        if ( options.getOptions().containsKey( "maxLevel" ) )
        {
            maxLevel = Integer.parseInt( options.getOptions().get( "maxLevel" ) );

            if ( organisationUnitService.getOrganisationUnitLevelByLevel( maxLevel ) == null )
            {
                maxLevel = null;
            }

            if ( level == null )
            {
                level = 1;
            }
        }

        if ( "true".equals( options.getOptions().get( "userOnly" ) ) )
        {
            entityList = new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser().getOrganisationUnits() );
        }
        else if ( lastUpdated != null )
        {
            entityList = new ArrayList<OrganisationUnit>( manager.getByLastUpdatedSorted( getEntityClass(), lastUpdated ) );

            if ( levelSorted )
            {
                Collections.sort( entityList, OrganisationUnitByLevelComparator.INSTANCE );
            }
        }
        else if ( maxLevel != null || level != null )
        {
            entityList = new ArrayList<OrganisationUnit>();

            if ( maxLevel == null )
            {
                entityList.addAll( organisationUnitService.getOrganisationUnitsAtLevel( level ) );
            }
            else
            {
                entityList.addAll( organisationUnitService.getOrganisationUnitsAtLevel( level ) );

                while ( !level.equals( maxLevel ) )
                {
                    entityList.addAll( organisationUnitService.getOrganisationUnitsAtLevel( ++level ) );
                }
            }
        }
        else if ( levelSorted )
        {
            entityList = new ArrayList<OrganisationUnit>( manager.getAll( getEntityClass() ) );
            Collections.sort( entityList, OrganisationUnitByLevelComparator.INSTANCE );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<OrganisationUnit>( manager.getBetween( getEntityClass(), pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<OrganisationUnit>( manager.getAllSorted( getEntityClass() ) );
        }

        return entityList;
    }

    @Override
    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public String getObject( @PathVariable("uid") String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        OrganisationUnit entity = getEntity( uid );

        if ( entity == null )
        {
            throw new NotFoundException( uid );
        }

        if ( options.getOptions().containsKey( "level" ) )
        {
            int level = -1;

            try
            {
                level = Integer.parseInt( options.getOptions().get( "level" ) );
            }
            catch ( Exception e )
            {
                level = entity.getOrganisationUnitLevel();
            }

            if ( level < 1 || level > organisationUnitService.getNumberOfOrganisationalLevels() )
            {
                level = entity.getOrganisationUnitLevel();
            }

            if ( level == entity.getOrganisationUnitLevel() )
            {
                model.addAttribute( "model", entity );
            }
            else if ( level < entity.getOrganisationUnitLevel() )
            {
                while ( level < entity.getOrganisationUnitLevel() )
                {
                    entity = entity.getParent();
                }

                model.addAttribute( "model", entity );
            }
            else
            {
                List<OrganisationUnit> entities = new ArrayList<OrganisationUnit>(
                    organisationUnitService.getOrganisationUnitsAtLevel( level, entity ) );

                MetaData metaData = new MetaData();
                metaData.setOrganisationUnits( entities );

                model.addAttribute( "model", metaData );
            }
        }
        if ( options.getOptions().containsKey( "includeDescendants" ) && Boolean.parseBoolean( options.getOptions().get( "includeDescendants" ) ) )
        {
            List<OrganisationUnit> entities = new ArrayList<OrganisationUnit>(
                organisationUnitService.getOrganisationUnitsWithChildren( uid ) );

            MetaData metaData = new MetaData();
            metaData.setOrganisationUnits( entities );

            model.addAttribute( "model", metaData );
        }
        if ( options.getOptions().containsKey( "includeChildren" ) && Boolean.parseBoolean( options.getOptions().get( "includeChildren" ) ) )
        {
            List<OrganisationUnit> entities = new ArrayList<OrganisationUnit>();
            entities.add( entity );
            entities.addAll( entity.getChildren() );

            MetaData metaData = new MetaData();
            metaData.setOrganisationUnits( entities );

            model.addAttribute( "model", metaData );
        }
        else
        {
            model.addAttribute( "model", entity );
        }

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( entity );
        }

        postProcessEntity( entity );
        postProcessEntity( entity, options, parameters );

        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
}
