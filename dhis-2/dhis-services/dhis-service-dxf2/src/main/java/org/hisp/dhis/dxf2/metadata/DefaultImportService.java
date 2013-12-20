package org.hisp.dhis.dxf2.metadata;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public class DefaultImportService
    implements ImportService
{
    private static final Log log = LogFactory.getLog( DefaultImportService.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired( required = false )
    private Set<Importer> importerClasses = new HashSet<Importer>();

    @Autowired
    private ObjectBridge objectBridge;

    @Autowired
    private HibernateCacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private Notifier notifier;

    //-------------------------------------------------------------------------------------------------------
    // ImportService Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    public ImportSummary importMetaData( String userUid, MetaData metaData, TaskId taskId )
    {
        return importMetaData( userUid, metaData, ImportOptions.getDefaultImportOptions(), taskId );
    }

    @Override
    public ImportSummary importMetaData( String userUid, MetaData metaData )
    {
        return importMetaData( userUid, metaData, ImportOptions.getDefaultImportOptions() );
    }

    @Override
    public ImportSummary importMetaData( String userUid, MetaData metaData, ImportOptions importOptions )
    {
        return importMetaData( userUid, metaData, importOptions, null );
    }

    @Override
    public ImportSummary importMetaData( String userUid, MetaData metaData, ImportOptions importOptions, TaskId taskId )
    {
        User user = userService.getUser( userUid );
        String username = user != null ? user.getUsername() : null;

        log.info( "User '" + username + "' started import at " + new Date() );

        notifier.clear( taskId ).notify( taskId, "Importing meta-data" );

        ImportSummary importSummary = new ImportSummary();

        objectBridge.init();

        if ( importOptions.isDryRun() )
        {
            objectBridge.setWriteEnabled( false );
        }

        for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getImportMap().entrySet() )
        {
            Object value = ReflectionUtils.invokeGetterMethod( entry.getValue(), metaData );

            if ( value != null )
            {
                if ( Collection.class.isAssignableFrom( value.getClass() ) )
                {
                    List<?> objects = new ArrayList<Object>( (Collection<?>) value );

                    if ( !objects.isEmpty() )
                    {
                        String message = "Importing " + objects.size() + " " + StringUtils.capitalize( entry.getValue() );

                        if ( taskId != null )
                        {
                            notifier.notify( taskId, message );
                        }
                        else
                        {
                            log.info( message );
                        }

                        ImportTypeSummary importTypeSummary = doImport( user, objects, importOptions );

                        if ( importTypeSummary != null )
                        {
                            importSummary.getImportTypeSummaries().add( importTypeSummary );
                            importSummary.incrementImportCount( importTypeSummary.getImportCount() );
                        }
                    }
                }
                else
                {
                    log.warn( "Getter for '" + entry.getValue() + "' did not return a collection." );
                }
            }
            else
            {
                log.warn( "Can not find getter for '" + entry.getValue() + "'." );
            }
        }

        if ( importOptions.isDryRun() )
        {
            sessionFactory.getCurrentSession().clear();
        }

        cacheManager.clearCache();
        objectBridge.destroy();

        if ( taskId != null )
        {
            notifier.notify( taskId, NotificationLevel.INFO, "Import done", true ).
                addTaskSummary( taskId, importSummary );
        }
        else
        {
            log.info( "Import done." );
        }

        return importSummary;
    }

    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    private <T> Importer<T> findImporterClass( List<?> clazzes )
    {
        notNull( clazzes );

        if ( !clazzes.isEmpty() && clazzes.get( 0 ) != null )
        {
            return findImporterClass( clazzes.get( 0 ).getClass() );
        }

        return null;
    }

    private <T> Importer<T> findImporterClass( Class<?> clazz )
    {
        if ( clazz != null )
        {
            for ( Importer<T> i : importerClasses )
            {
                if ( i.canHandle( clazz ) )
                {
                    return i;
                }
            }
        }

        return null;
    }

    private <T> ImportTypeSummary doImport( User user, List<T> objects, ImportOptions importOptions )
    {
        if ( !objects.isEmpty() && objects.get( 0 ) != null )
        {
            Importer<T> importer = findImporterClass( objects );

            if ( importer != null )
            {
                return importer.importObjects( user, objects, importOptions );
            }
            else
            {
                log.warn( "Importer for object of type " + objects.get( 0 ).getClass().getSimpleName() + " not found." );
            }
        }

        return null;
    }
}
