package org.hisp.dhis.patient;

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

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.Program;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */

public interface PatientService
{
    String ID = PatientService.class.getName();

    int savePatient( Patient patient );

    void deletePatient( Patient patient );

    void updatePatient( Patient patient );

    Patient getPatient( int id );

    Patient getPatient( String uid );

    Collection<Patient> getAllPatients();

    Collection<Patient> getPatients( String name, Date birthdate, String gender );

    /**
     * Search Patient base on birthDate
     *
     * @param birthDate
     * @return Patient List
     */
    Collection<Patient> getPatientsByBirthDate( Date birthDate );

    /**
     * Search Patient base on fullName
     *
     * @param name fullName
     * @return Patient List
     */
    Collection<Patient> getPatientsByNames( String name, Integer min, Integer max );

    /**
     * Search Patient base on full-name or identifier value
     *
     * @param searchText value
     * @return Patient List
     */
    Collection<Patient> getPatients( String searchText, Integer min, Integer max );

    /**
     * Search Patient for mobile base on identifier value
     *
     * @param searchText value
     * @param orgUnitId
     * @return Patient List
     */
    Collection<Patient> getPatientsForMobile( String searchText, int orgUnitId );

    /**
     * Search Patient base on organization unit with result limited
     *
     * @param organisationUnit organisationUnit
     * @return Patient List
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, Integer min, Integer max );

    /**
     * Search Patient base on organization unit with result limited
     *
     * @param organisationUnit organisationUnit
     * @return Patient List
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit );

    /**
     *
     * @param program
     * @return
     */
    Collection<Patient> getPatients( Program program );

    /**
     *
     * @param organisationUnit
     * @param program
     * @return
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, Program program );

    /**
     * Search Patient base on organization unit and sort the result by
     * PatientAttribute
     *
     * @param organisationUnit organisationUnit
     * @param patientAttribute
     * @param min
     * @param max
     * @return Patient List
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, PatientAttribute patientAttribute, Integer min,
        Integer max );

    /**
     * Search Patient base on organisationUnit and identifier value name
     *
     * @param organisationUnit
     * @param searchText       identifier value
     * @param min
     * @param max
     * @return
     */
    Collection<Patient> getPatientsLikeName( OrganisationUnit organisationUnit, String name, Integer min, Integer max );

    /**
     * Search Patient base on PatientIdentifierType or Attribute or Patient's
     * name
     *
     * @param identifierTypeId
     * @param attributeId
     * @param value
     * @return
     */
    Collection<Patient> getPatient( Integer identifierTypeId, Integer attributeId, String value );

    /**
     * Search Patient base on OrganisationUnit and Program with result limited
     * name
     *
     * @param organisationUnit
     * @param program
     * @param min
     * @param max
     * @return
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, Program program, Integer min, Integer max );

    /**
     * Sort the result by PatientAttribute
     *
     * @param patients
     * @param patientAttribute
     * @return Patient List
     */
    Collection<Patient> sortPatientsByAttribute( Collection<Patient> patients, PatientAttribute patientAttribute );

    Collection<Patient> getRepresentatives( Patient patient );

    /**
     * Search Patient base on identifier value and get number of result
     *
     * @param searchText
     * @return number of patients
     */
    int countGetPatients( String searchText );

    /**
     * Search Patient base on name and get number of
     * result
     *
     * @param name
     * @return number of patients
     */
    int countGetPatientsByName( String name );

    int createPatient( Patient patient, Integer representativeId, Integer relationshipTypeId,
        List<PatientAttributeValue> patientAttributeValues );

    void updatePatient( Patient patient, Integer representativeId, Integer relationshipTypeId,
        List<PatientAttributeValue> valuesForSave, List<PatientAttributeValue> valuesForUpdate,
        Collection<PatientAttributeValue> valuesForDelete );

    int countGetPatientsByOrgUnit( OrganisationUnit organisationUnit );

    int countGetPatientsByOrgUnitProgram( OrganisationUnit organisationUnit, Program program );

    Object getObjectValue( String property, String value, I18nFormat format );
    
    Collection<Patient> searchPatients( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Collection<PatientAttribute> patientAttributes, Collection<PatientIdentifierType> identifierTypes,
        Integer statusEnrollment, Integer min, Integer max );

    int countSearchPatients( List<String> searchKeys, Collection<OrganisationUnit> orgunit, Boolean followup, Integer statusEnrollment );

    Collection<String> getPatientPhoneNumbers( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max );

    List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max );

    Grid getScheduledEventsReport( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup, Integer statusEnrollment,
        Integer min, Integer max, I18n i18n );

    Collection<Patient> getPatientsByPhone( String phoneNumber, Integer min, Integer max );

    Collection<Patient> getPatientByFullname( String fullName, OrganisationUnit organisationUnit );

    Collection<Integer> getRegistrationOrgunitIds( Date startDate, Date endDate );

    Grid getTrackingEventsReport( Program program, List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment, I18n i18n );
}
