package org.hisp.dhis.api.controller;

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

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViewService;
import org.hisp.dhis.system.grid.GridUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = SqlViewController.RESOURCE_PATH )
public class SqlViewController
    extends AbstractCrudController<SqlView>
{
    public static final String RESOURCE_PATH = "/sqlViews";
    
    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( value = "/{uid}/data", method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_JSON )
    public String getViewJson( @PathVariable( "uid" ) String uid, 
        @RequestParam(required=false) Set<String> criteria, Model model, HttpServletResponse response )
    {
        SqlView sqlView = sqlViewService.getSqlViewByUid( uid );
        
        Grid grid = sqlViewService.getSqlViewGrid( sqlView, SqlView.getCriteria( criteria ) );

        model.addAttribute( "model", grid );
        model.addAttribute( "viewClass", "detailed" );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING );
        
        return grid != null ? "sqlView" : null;
    }

    @RequestMapping( value = "/{uid}/data.xml", method = RequestMethod.GET )
    public void getViewXml( @PathVariable( "uid" ) String uid, 
        @RequestParam(required=false) Set<String> criteria, HttpServletResponse response ) throws Exception
    {
        SqlView sqlView = sqlViewService.getSqlViewByUid( uid );
        
        Grid grid = sqlViewService.getSqlViewGrid( sqlView, SqlView.getCriteria( criteria ) );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.RESPECT_SYSTEM_SETTING );
        
        GridUtils.toXml( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.csv", method = RequestMethod.GET )
    public void getViewCsv( @PathVariable( "uid" ) String uid, 
        @RequestParam(required=false) Set<String> criteria, HttpServletResponse response ) throws Exception
    {
        SqlView sqlView = sqlViewService.getSqlViewByUid( uid );
        
        Grid grid = sqlViewService.getSqlViewGrid( sqlView, SqlView.getCriteria( criteria ) );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.RESPECT_SYSTEM_SETTING, "sqlview.csv", true );
        
        GridUtils.toCsv( grid, response.getOutputStream() );
    }
    
    @RequestMapping( value = "/{uid}/data.xls", method = RequestMethod.GET )
    public void getViewXls( @PathVariable( "uid" ) String uid, 
        @RequestParam(required=false) Set<String> criteria, HttpServletResponse response ) throws Exception
    {
        SqlView sqlView = sqlViewService.getSqlViewByUid( uid );
        
        Grid grid = sqlViewService.getSqlViewGrid( sqlView, SqlView.getCriteria( criteria ) );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.RESPECT_SYSTEM_SETTING, "sqlview.xls", true );
        
        GridUtils.toXls( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.html", method = RequestMethod.GET )
    public void getViewHtml( @PathVariable( "uid" ) String uid, 
        @RequestParam(required=false) Set<String> criteria, HttpServletResponse response ) throws Exception
    {
        SqlView sqlView = sqlViewService.getSqlViewByUid( uid );
        
        Grid grid = sqlViewService.getSqlViewGrid( sqlView, SqlView.getCriteria( criteria ) );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_HTML, CacheStrategy.RESPECT_SYSTEM_SETTING );
        
        GridUtils.toHtml( grid, response.getWriter() );
    }
    
    @RequestMapping( value = "/{uid}/execute", method = RequestMethod.POST )
    public void executeView( @PathVariable( "uid" ) String uid, HttpServletResponse response )
    {
        SqlView sqlView = sqlViewService.getSqlViewByUid( uid );
        
        if ( sqlView == null )
        {
            ContextUtils.notFoundResponse( response, "SQL view not found" );
            return;
        }
        
        String result = sqlViewService.createViewTable( sqlView );
        
        if ( result != null )
        {
            ContextUtils.conflictResponse( response, result );
        }
        else
        {
            String location = RESOURCE_PATH + "/" + sqlView.getUid();
            
            ContextUtils.createdResponse( response, "SQL view created", location );
        }
    }
}
