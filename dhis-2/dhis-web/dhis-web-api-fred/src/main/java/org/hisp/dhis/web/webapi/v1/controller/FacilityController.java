package org.hisp.dhis.web.webapi.v1.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hisp.dhis.api.controller.organisationunit.OrganisationUnitLevelController;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.webapi.v1.domain.Facilities;
import org.hisp.dhis.web.webapi.v1.domain.Facility;
import org.hisp.dhis.web.webapi.v1.domain.Identifier;
import org.hisp.dhis.web.webapi.v1.exception.DuplicateCodeException;
import org.hisp.dhis.web.webapi.v1.exception.DuplicateUidException;
import org.hisp.dhis.web.webapi.v1.exception.DuplicateUuidException;
import org.hisp.dhis.web.webapi.v1.exception.ETagVerificationException;
import org.hisp.dhis.web.webapi.v1.exception.FacilityNotFoundException;
import org.hisp.dhis.web.webapi.v1.exception.UuidFormatException;
import org.hisp.dhis.web.webapi.v1.utils.ContextUtils;
import org.hisp.dhis.web.webapi.v1.utils.MessageUtils;
import org.hisp.dhis.web.webapi.v1.utils.ValidationUtils;
import org.hisp.dhis.web.webapi.v1.validation.group.Create;
import org.hisp.dhis.web.webapi.v1.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller(value = "facility-controller-" + FredController.PREFIX)
@RequestMapping(FacilityController.RESOURCE_PATH)
@PreAuthorize("hasRole('M_dhis-web-api-fred') or hasRole('ALL')")
public class FacilityController
{
    public static final String RESOURCE_PATH = "/" + FredController.PREFIX + "/facilities";

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private Validator validator;

    @Autowired
    private ObjectMapper objectMapper;

    @InitBinder
    protected void initBinder( WebDataBinder binder )
    {
        binder.registerCustomEditor( Date.class, new PropertyEditorSupport()
        {
            private SimpleDateFormat[] simpleDateFormats = new SimpleDateFormat[]{
                new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ),
                new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ),
                new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm" ),
                new SimpleDateFormat( "yyyy-MM-dd'T'HH" ),
                new SimpleDateFormat( "yyyy-MM-dd" ),
                new SimpleDateFormat( "yyyy-MM" ),
                new SimpleDateFormat( "yyyy" )
            };

            @Override
            public void setAsText( String value ) throws IllegalArgumentException
            {
                for ( SimpleDateFormat simpleDateFormat : simpleDateFormats )
                {
                    try
                    {
                        setValue( simpleDateFormat.parse( value ) );
                        return;
                    }
                    catch ( ParseException ignored )
                    {
                    }
                }

                setValue( null );
            }
        } );
    }

    //--------------------------------------------------------------------------
    // GET HTML
    //--------------------------------------------------------------------------

    private Facility filterFacility( Facility facility, boolean allProperties, String fields )
    {
        // if allProperties=false is added, filter away the properties block, and don't care about fields
        if ( !allProperties )
        {
            facility.setProperties( null );

            return facility;
        }

        if ( fields == null )
        {
            return facility;
        }

        List<String> strings = Arrays.asList( fields.split( "," ) );

        // simple field filtering
        if ( !strings.contains( "id" ) )
        {
            facility.setUuid( null );
        }

        if ( !strings.contains( "name" ) )
        {
            facility.setName( null );
        }

        if ( !strings.contains( "active" ) )
        {
            facility.setActive( null );
        }

        if ( !strings.contains( "createdAt" ) )
        {
            facility.setCreatedAt( null );
        }

        if ( !strings.contains( "updatedAt" ) )
        {
            facility.setUpdatedAt( null );
        }

        if ( !strings.contains( "coordinates" ) )
        {
            facility.setCoordinates( null );
        }

        if ( !strings.contains( "href" ) )
        {
            facility.setHref( null );
        }

        if ( !strings.contains( "identifiers" ) )
        {
            facility.setIdentifiers( null );
        }

        if ( fields.indexOf( ':' ) >= 0 )
        {
            Map<String, Object> properties = facility.getProperties();
            facility.setProperties( new HashMap<String, Object>() );

            for ( String s : strings )
            {
                if ( s.contains( ":" ) )
                {
                    String[] split = s.split( ":" );

                    if ( split.length > 1 )
                    {
                        if ( properties.containsKey( split[1] ) )
                        {
                            facility.getProperties().put( split[1], properties.get( split[1] ) );
                        }
                    }
                }
            }
        }
        else if ( !strings.contains( "properties" ) )
        {
            facility.setProperties( null );
        }

        return facility;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String readFacilities( Model model, @RequestParam(required = false) Boolean active,
        @RequestParam(value = "updatedSince", required = false) Date lastUpdated,
        @RequestParam(value = "allProperties", required = false, defaultValue = "true") Boolean allProperties,
        @RequestParam(value = "fields", required = false) String fields,
        @RequestParam(value = "limit", required = false, defaultValue = "25") String limit,
        @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
        HttpServletRequest request )
    {
        Facilities facilities = new Facilities();
        List<OrganisationUnit> allOrganisationUnits;

        Integer limitValue = 25;

        if ( limit.equalsIgnoreCase( "off" ) )
        {
            limitValue = null;
        }
        else
        {
            try
            {
                limitValue = Integer.parseInt( limit );
            }
            catch ( NumberFormatException ignored )
            {
            }
        }

        if ( active == null && lastUpdated == null )
        {
            if ( limitValue != null )
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsBetween( offset, limitValue ) );
            }
            else
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
            }
        }
        else if ( active == null )
        {
            if ( limitValue != null )
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.
                    getOrganisationUnitsBetweenByLastUpdated( lastUpdated, offset, limitValue ) );
            }
            else
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnitsByLastUpdated( lastUpdated ) );
            }
        }
        else if ( lastUpdated == null )
        {
            if ( limitValue != null )
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsBetweenByStatus( active, offset, limitValue ) );
            }
            else
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnitsByStatus( active ) );
            }
        }
        else
        {
            if ( limitValue != null )
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.
                    getOrganisationUnitsBetweenByStatusLastUpdated( active, lastUpdated, offset, limitValue ) );
            }
            else
            {
                allOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnitsByStatusLastUpdated( active, lastUpdated ) );
            }
        }

        facilities.getMeta().put( "limit", limitValue );
        facilities.getMeta().put( "offset", offset );
        facilities.getMeta().put( "total", organisationUnitService.getNumberOfOrganisationUnits() );

        Collections.sort( allOrganisationUnits, IdentifiableObjectNameComparator.INSTANCE );
        List<OrganisationUnitLevel> organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();

        for ( OrganisationUnit organisationUnit : allOrganisationUnits )
        {
            Facility facility = conversionService.convert( organisationUnit, Facility.class );
            filterFacility( facility, allProperties, fields );
            addHierarchyPropertyToFacility( organisationUnitLevels, facility );

            facilities.getFacilities().add( facility );

            if ( request.getRequestURL().toString().endsWith( ".json" ) )
            {
                facility.setHref( facility.getHref() + ".json" );
            }
        }

        setAccessRights( model );

        model.addAttribute( "esc", StringEscapeUtils.class );
        model.addAttribute( "entity", facilities );
        ContextUtils.populateContextPath( model, request );
        model.addAttribute( "baseUrl", linkTo( FredController.class ).toString() );
        model.addAttribute( "pageName", "facilities" );
        model.addAttribute( "page", FredController.PREFIX + "/facilities.vm" );

        if ( offset == 0 )
        {
            model.addAttribute( "prevDisabled", true );
        }

        if ( (offset + (limitValue == null ? 0 : limitValue) >= organisationUnitService.getNumberOfOrganisationUnits()) )
        {
            model.addAttribute( "nextDisabled", true );
        }

        return FredController.PREFIX + "/layout";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String readFacility( Model model, @PathVariable String id,
        @RequestParam(value = "allProperties", required = false, defaultValue = "true") Boolean allProperties,
        @RequestParam(value = "fields", required = false) String fields,
        HttpServletRequest request ) throws FacilityNotFoundException
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            throw new FacilityNotFoundException();
        }

        List<OrganisationUnitLevel> organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();

        Facility facility = conversionService.convert( organisationUnit, Facility.class );
        filterFacility( facility, allProperties, fields );
        addHierarchyPropertyToFacility( organisationUnitLevels, facility );

        if ( request.getRequestURL().toString().endsWith( ".json" ) )
        {
            facility.setHref( facility.getHref() + ".json" );
        }

        setAccessRights( model );

        model.addAttribute( "esc", StringEscapeUtils.class );
        model.addAttribute( "entity", facility );

        List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Collections.sort( dataSets, IdentifiableObjectNameComparator.INSTANCE );
        model.addAttribute( "dataSets", dataSets );

        ContextUtils.populateContextPath( model, request );

        model.addAttribute( "baseUrl", linkTo( FredController.class ).toString() );
        model.addAttribute( "pageName", "facility" );
        model.addAttribute( "page", FredController.PREFIX + "/facility.vm" );

        return FredController.PREFIX + "/layout";
    }

    private void setAccessRights( Model model )
    {
        // TODO fix this, a proper mock currentuserservice should be implemented
        if ( currentUserService != null && currentUserService.getCurrentUser() != null )
        {
            Set<String> authorities = currentUserService.getCurrentUser().getUserCredentials().getAllAuthorities();

            model.addAttribute( "canCreate", authorities.contains( "F_FRED_CREATE" ) || currentUserService.currentUserIsSuper() );
            model.addAttribute( "canRead", authorities.contains( "M-dhis-web-api-fred" ) || currentUserService.currentUserIsSuper() );
            model.addAttribute( "canUpdate", authorities.contains( "F_FRED_UPDATE" ) || currentUserService.currentUserIsSuper() );
            model.addAttribute( "canDelete", authorities.contains( "F_FRED_DELETE" ) || currentUserService.currentUserIsSuper() );
        }
        else
        {
            model.addAttribute( "canCreate", false );
            model.addAttribute( "canRead", false );
            model.addAttribute( "canUpdate", false );
            model.addAttribute( "canDelete", false );
        }
    }

    private void addHierarchyPropertyToFacility( List<OrganisationUnitLevel> organisationUnitLevels, Facility facility )
    {
        if ( facility.getProperties() == null )
        {
            return;
        }

        // TODO this probably belongs in "meta": {}
        List<Map<String, Object>> hierarchy = new ArrayList<Map<String, Object>>();
        facility.getProperties().put( "hierarchy", hierarchy );

        for ( OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels )
        {
            Map<String, Object> level = new HashMap<String, Object>();

            level.put( "id", organisationUnitLevel.getUid() );

            // temporary fix since ControllerLinkBuilder can't handle cross-servlet controllers
            level.put( "href", linkTo( OrganisationUnitLevelController.class ).slash( organisationUnitLevel.getUid() ).toString()
                .replace( "/api-fred", "/api" ) );

            level.put( "name", organisationUnitLevel.getName() );
            level.put( "level", organisationUnitLevel.getLevel() );

            hierarchy.add( level );
        }
    }

    //--------------------------------------------------------------------------
    // POST JSON
    //--------------------------------------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('F_FRED_CREATE') or hasRole('ALL')")
    public ResponseEntity<String> createFacility( @RequestBody Facility facility ) throws Exception
    {
        if ( facility.getUuid() == null )
        {
            facility.setUuid( UUID.randomUUID().toString() );
        }
        else
        {
            String uuid = facility.getUuid();

            try
            {
                UUID.fromString( uuid );
            }
            catch ( IllegalArgumentException ignored )
            {
                throw new UuidFormatException();
            }
        }

        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, Default.class, Create.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        if ( constraintViolations.isEmpty() )
        {
            OrganisationUnit organisationUnit = conversionService.convert( facility, OrganisationUnit.class );

            if ( organisationUnitService.getOrganisationUnit( organisationUnit.getUuid() ) != null )
            {
                throw new DuplicateUuidException();
            }
            if ( organisationUnitService.getOrganisationUnit( organisationUnit.getUid() ) != null )
            {
                throw new DuplicateUidException();
            }
            else if ( organisationUnit.getCode() != null && organisationUnitService.getOrganisationUnitByCode( organisationUnit.getCode() ) != null )
            {
                throw new DuplicateCodeException();
            }

            organisationUnitService.addOrganisationUnit( organisationUnit );

            for ( DataSet dataSet : organisationUnit.getDataSets() )
            {
                dataSet.addOrganisationUnit( organisationUnit );
                dataSetService.updateDataSet( dataSet );
            }

            headers.setLocation( linkTo( FacilityController.class ).slash( organisationUnit.getUid() ).toUri() );

            facility = conversionService.convert( organisationUnit, Facility.class );
            List<OrganisationUnitLevel> organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();
            addHierarchyPropertyToFacility( organisationUnitLevels, facility );
            json = objectMapper.writeValueAsString( facility );

            return new ResponseEntity<String>( json, headers, HttpStatus.CREATED );
        }
        else
        {
            return new ResponseEntity<String>( json, headers, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }

    //--------------------------------------------------------------------------
    // PUT JSON
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('F_FRED_UPDATE') or hasRole('ALL')")
    public ResponseEntity<String> updateFacility( @PathVariable String id, @RequestBody Facility facility, HttpServletRequest request ) throws Exception
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.APPLICATION_JSON_VALUE );

        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            throw new FacilityNotFoundException();
        }

        // getId == null is not legal, but will be catched by bean validation
        if ( facility.getUuid() != null )
        {
            String uuid = facility.getUuid();

            try
            {
                UUID.fromString( uuid );
            }
            catch ( IllegalArgumentException ignored )
            {
                throw new UuidFormatException();
            }
        }

        Set<ConstraintViolation<Facility>> constraintViolations = validator.validate( facility, Default.class, Update.class );

        String json = ValidationUtils.constraintViolationsToJson( constraintViolations );

        if ( constraintViolations.isEmpty() )
        {
            OrganisationUnit organisationUnitUpdate = conversionService.convert( facility, OrganisationUnit.class );
            checkIdentifier( facility, organisationUnit.getUid() );

            if ( request.getHeader( "If-Match" ) != null )
            {
                Facility old_facility = conversionService.convert( organisationUnit, Facility.class );
                List<OrganisationUnitLevel> organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();
                addHierarchyPropertyToFacility( organisationUnitLevels, old_facility );
                String body = objectMapper.writeValueAsString( old_facility );

                String ETag = generateETagHeaderValue( body.getBytes() );

                if ( !ETag.equals( request.getHeader( "If-Match" ) ) )
                {
                    throw new ETagVerificationException();
                }
            }

            if ( organisationUnitUpdate.getCode() != null )
            {
                OrganisationUnit ouByCode = organisationUnitService.getOrganisationUnitByCode( organisationUnitUpdate.getCode() );

                if ( ouByCode != null && !organisationUnit.getUid().equals( ouByCode.getUid() ) )
                {
                    throw new DuplicateCodeException();
                }
            }

            organisationUnit.setName( organisationUnitUpdate.getName() );
            organisationUnit.setShortName( organisationUnitUpdate.getShortName() );
            organisationUnit.setCode( organisationUnitUpdate.getCode() );
            organisationUnit.setFeatureType( organisationUnitUpdate.getFeatureType() );
            organisationUnit.setCoordinates( organisationUnitUpdate.getCoordinates() );
            organisationUnit.setParent( organisationUnitUpdate.getParent() );
            organisationUnit.setActive( organisationUnitUpdate.isActive() );

            organisationUnit.removeAllDataSets();
            organisationUnitService.updateOrganisationUnit( organisationUnit );

            for ( DataSet dataSet : organisationUnitUpdate.getDataSets() )
            {
                dataSet.addOrganisationUnit( organisationUnit );
                dataSetService.updateDataSet( dataSet );
            }

            facility = conversionService.convert( organisationUnit, Facility.class );
            List<OrganisationUnitLevel> organisationUnitLevels = organisationUnitService.getOrganisationUnitLevels();
            addHierarchyPropertyToFacility( organisationUnitLevels, facility );
            json = objectMapper.writeValueAsString( facility );

            return new ResponseEntity<String>( json, headers, HttpStatus.OK );
        }
        else
        {
            return new ResponseEntity<String>( json, headers, HttpStatus.UNPROCESSABLE_ENTITY );
        }
    }

    //--------------------------------------------------------------------------
    // DELETE JSON
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('F_FRED_DELETE') or hasRole('ALL')")
    public ResponseEntity<String> deleteFacility( @PathVariable String id ) throws HierarchyViolationException, IOException, FacilityNotFoundException
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            throw new FacilityNotFoundException();
        }

        organisationUnitService.deleteOrganisationUnit( organisationUnit );

        return new ResponseEntity<String>( MessageUtils.jsonMessage( HttpStatus.OK.toString(),
            "{}" ), HttpStatus.OK );
    }

    //--------------------------------------------------------------------------
    // UTILS
    //--------------------------------------------------------------------------

    private OrganisationUnit getOrganisationUnit( String id )
    {
        OrganisationUnit organisationUnit;

        if ( id.length() == 11 )
        {
            organisationUnit = organisationUnitService.getOrganisationUnit( id );
        }
        else
        {
            organisationUnit = organisationUnitService.getOrganisationUnitByUuid( id );
        }

        return organisationUnit;
    }

    private String generateETagHeaderValue( byte[] bytes )
    {
        StringBuilder builder = new StringBuilder( "\"0" );
        DigestUtils.appendMd5DigestAsHex( bytes, builder );
        builder.append( '"' );
        return builder.toString();
    }

    private void checkIdentifier( Facility facility, String id )
    {
        Identifier identifier = new Identifier();

        identifier.setAgency( Identifier.DHIS2_AGENCY );
        identifier.setContext( Identifier.DHIS2_UID_CONTEXT );
        identifier.setId( id );

        if ( facility.getIdentifiers().isEmpty() )
        {
            facility.getIdentifiers().add( identifier );
        }
        else
        {
            boolean found = false;

            for ( Identifier i : facility.getIdentifiers() )
            {
                if ( i.getAgency().equals( Identifier.DHIS2_AGENCY ) && i.getContext().equals( Identifier.DHIS2_UID_CONTEXT ) )
                {
                    i.setId( id );
                    found = true;
                }
            }

            if ( !found )
            {
                facility.getIdentifiers().add( identifier );
            }
        }
    }
}
