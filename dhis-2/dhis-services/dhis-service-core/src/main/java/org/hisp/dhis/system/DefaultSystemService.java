package org.hisp.dhis.system;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Lars Helge Overland
 */
public class DefaultSystemService
    implements SystemService
{
    @Autowired
    private LocationManager locationManager;
    
    @Autowired
    private DatabaseInfoProvider databaseInfoProvider;
    
    @Autowired
    private ConfigurationService configurationService;

    // -------------------------------------------------------------------------
    // SystemService implementation
    // -------------------------------------------------------------------------

    @Override
    public SystemInfo getSystemInfo()
    {
        SystemInfo info = new SystemInfo();
        
        // ---------------------------------------------------------------------
        // Version
        // ---------------------------------------------------------------------

        ClassPathResource resource = new ClassPathResource( "build.properties" );
        
        if ( resource.isReadable() )
        {
            InputStream in = null;
            
            try
            {
                in = resource.getInputStream();
                
                Properties properties = new Properties();
        
                properties.load( in );
        
                info.setVersion( properties.getProperty( "build.version" ) );
                info.setRevision( properties.getProperty( "build.revision" ) );
        
                String buildTime = properties.getProperty( "build.time" );
    
                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    
                info.setBuildTime( dateFormat.parse( buildTime ) );
            }
            catch ( IOException ex )
            {
                // Do nothing
            }
            catch ( ParseException ex )
            {
                // Do nothing
            }
            finally
            {
                IOUtils.closeQuietly( in );
            }
        }
        
        // ---------------------------------------------------------------------
        // External directory
        // ---------------------------------------------------------------------

        info.setEnvironmentVariable( locationManager.getEnvironmentVariable() );
        
        try
        {
            File directory = locationManager.getExternalDirectory();
        
            info.setExternalDirectory( directory.getAbsolutePath() );
        }
        catch ( LocationManagerException ex )
        {
            info.setExternalDirectory( "Not set" );
        }
        
        // ---------------------------------------------------------------------
        // Database
        // ---------------------------------------------------------------------

        info.setDatabaseInfo( databaseInfoProvider.getDatabaseInfo() );

        // ---------------------------------------------------------------------
        // System env variables and properties
        // ---------------------------------------------------------------------

        try
        {
            info.setJavaOpts( System.getenv( "JAVA_OPTS" ) );
        }
        catch ( SecurityException ex )
        {
            info.setJavaOpts( "Unknown" );
        }
        
        Properties props = System.getProperties();
        
        info.setJavaIoTmpDir( props.getProperty( "java.io.tmpdir" ) );
        info.setJavaVersion( props.getProperty( "java.version" ) );
        info.setJavaVendor( props.getProperty( "java.vendor" ) );
        info.setOsName( props.getProperty( "os.name" ) );
        info.setOsArchitecture( props.getProperty( "os.arch" ) );
        info.setOsVersion( props.getProperty( "os.version" ) );
        
        info.setMemoryInfo( SystemUtils.getMemoryString() );        
        info.setCpuCores( SystemUtils.getCpuCores() );
        info.setServerDate( new Date() );
        
        Configuration config = configurationService.getConfiguration();
        
        info.setSystemId( config.getSystemId() );
        
        return info;
    }
}
