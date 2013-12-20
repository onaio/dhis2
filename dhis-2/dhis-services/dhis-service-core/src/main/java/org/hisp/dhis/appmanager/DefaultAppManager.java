package org.hisp.dhis.appmanager;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;

import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.datavalue.DefaultDataValueService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Saptarshi Purkayastha
 */
public class DefaultAppManager
    implements AppManager
{
    private static final Log log = LogFactory.getLog( DefaultDataValueService.class );

    /**
     * In-memory singleton list holding state for apps.
     */
    private List<App> apps = new ArrayList<App>();

    @PostConstruct
    private void init()
    {
        reloadAppsInternal();
        
        log.info( "Detecting apps: " + apps );
    }
    
    @Autowired
    private SystemSettingManager appSettingManager;

    // -------------------------------------------------------------------------
    // AppManagerService implementation
    // -------------------------------------------------------------------------

    @Override
    public String getAppFolderPath()
    {
        return StringUtils.trimToNull( (String) appSettingManager.getSystemSetting( KEY_APP_FOLDER_PATH ) );
    }

    @Override
    public String getAppStoreUrl()
    {
        return StringUtils.trimToNull( (String) appSettingManager.getSystemSetting( KEY_APP_STORE_URL, DEFAULT_APP_STORE_URL ) );
    }

    @Override
    public List<App> getInstalledApps()
    {
        String baseUrl = getAppBaseUrl();
        
        for ( App app : apps )
        {
            app.setBaseUrl( baseUrl );
        }
        
        return apps;
    }
    
    @Override
    public void installApp( File file, String fileName, String rootPath )
        throws IOException
    {
        ZipFile zip = new ZipFile( file );
        ZipEntry entry = zip.getEntry( "manifest.webapp" );

        InputStream inputStream = zip.getInputStream( entry );
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        App app = mapper.readValue( inputStream, App.class );

        // Delete if app is already installed
        
        if ( getInstalledApps().contains( app ) )
        {
            String folderPath = getAppFolderPath() + File.separator + app.getFolderName();
            FileUtils.forceDelete( new File( folderPath ) );
        }

        String dest = getAppFolderPath() + File.separator + fileName.substring( 0, fileName.lastIndexOf( '.' ) );
        Unzip unzip = new Unzip();
        unzip.setSrc( file );
        unzip.setDest( new File( dest ) );
        unzip.execute();

        // Updating dhis server location
        
        File updateManifest = new File( dest + File.separator + "manifest.webapp" );
        App installedApp = mapper.readValue( updateManifest, App.class );

        if ( installedApp.getActivities().getDhis().getHref().equals( "*" ) )
        {
            installedApp.getActivities().getDhis().setHref( rootPath );
            mapper.writeValue( updateManifest, installedApp );
        }

        zip.close();
                
        reloadAppsInternal(); // Reload app state
    }

    @Override
    public boolean deleteApp( String name )
    {
        for ( App app : getInstalledApps() )
        {
            if ( app.getName().equals( name ) )
            {
                try
                {
                    String folderPath = getAppFolderPath() + File.separator + app.getFolderName();                
                    FileUtils.forceDelete( new File( folderPath ) );

                    return true;
                }
                catch ( IOException ex )
                {
                    log.error( "Could not delete app: " + name, ex );
                    return false;
                }
                finally
                {
                    reloadAppsInternal(); // Reload app state
                }
            }
        }

        return false;
    }

    @Override
    public void setAppFolderPath( String appFolderPath )
    {
        if ( !appFolderPath.isEmpty() )
        {
            try
            {
                File folder = new File( appFolderPath );
                if ( !folder.exists() )
                {
                    FileUtils.forceMkdir( folder );
                }
            }
            catch ( IOException ex )
            {
                log.error( ex.getLocalizedMessage(), ex );
            }
        }

        appSettingManager.saveSystemSetting( KEY_APP_FOLDER_PATH, appFolderPath );
    }

    @Override
    public void setAppStoreUrl( String appStoreUrl )
    {
        appSettingManager.saveSystemSetting( KEY_APP_STORE_URL, appStoreUrl );
    }

    @Override
    public String getAppBaseUrl()
    {
        return StringUtils.trimToNull( (String) appSettingManager.getSystemSetting( KEY_APP_BASE_URL ) );
    }

    @Override
    public void setAppBaseUrl( String appBaseUrl )
    {
        appSettingManager.saveSystemSetting( KEY_APP_BASE_URL, appBaseUrl );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Sets the list of apps with detected apps from the file system.
     */
    private void reloadAppsInternal()
    {
        List<App> appList = new ArrayList<App>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

        if ( null != getAppFolderPath() )
        {
            File appFolderPath = new File( getAppFolderPath() );
            if ( appFolderPath.isDirectory() )
            {
                File[] listFiles = appFolderPath.listFiles();
                for ( File folder : listFiles )
                {
                    if ( folder.isDirectory() )
                    {
                        File appManifest = new File( folder, "manifest.webapp" );
                        if ( appManifest.exists() )
                        {
                            try
                            {
                                App app = mapper.readValue( appManifest, App.class );
                                app.setFolderName( folder.getName() );
                                appList.add( app );
                            }
                            catch ( IOException ex )
                            {
                                log.error( ex.getLocalizedMessage(), ex );
                            }
                        }
                    }
                }
            }
        }

        this.apps = appList;
    }
}
