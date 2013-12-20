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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.user.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Abyot Asalefew Gizaw
 */
@JacksonXmlRootElement(localName = "person", namespace = DxfNamespaces.DXF_2_0)
public class Patient
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 884114994005945275L;

    public static final String MALE = "M";
    public static final String FEMALE = "F";
    public static final String TRANSGENDER = "T";

    public static final char DOB_TYPE_VERIFIED = 'V';
    public static final char DOB_TYPE_DECLARED = 'D';
    public static final char DOB_TYPE_APPROXIMATED = 'A';

    public static final char AGE_TYPE_YEAR = 'Y';
    public static final char AGE_TYPE_MONTH = 'M';
    public static final char AGE_TYPE_DAY = 'D';

    public static String PREFIX_IDENTIFIER_TYPE = "iden";
    public static String PREFIX_FIXED_ATTRIBUTE = "fixedAttr";
    public static String PREFIX_PATIENT_ATTRIBUTE = "attr";
    public static String PREFIX_PROGRAM = "prg";
    public static String PREFIX_PROGRAM_INSTANCE = "pi";
    public static String PREFIX_PROGRAM_EVENT_BY_STATUS = "stat";
    public static String PREFIX_PROGRAM_STAGE = "prgst";

    public static String FIXED_ATTR_BIRTH_DATE = "birthDate";
    public static String FIXED_ATTR_AGE = "age";
    public static String FIXED_ATTR_INTEGER_AGE = "integerValueOfAge";
    public static String FIXED_ATTR_REGISTRATION_DATE = "registrationDate";

    public static String FIXED_ATTR_FULL_NAME = "fullName";

    private String gender;

    private Date birthDate;

    private String phoneNumber;

    private Date deathDate;

    private Date registrationDate;

    private boolean isDead = false;

    private Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();
    
    private Set<ProgramInstance> programInstances = new HashSet<ProgramInstance>();
    
    private OrganisationUnit organisationUnit;
    
    private Patient representative;

    private boolean underAge;

    private Character dobType;

    private User healthWorker;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Patient()
    {
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getFullName()
    {
        return name;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( Date birthDate )
    {
        this.birthDate = birthDate;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Date getDeathDate()
    {
        return deathDate;
    }

    public void setDeathDate( Date deathDate )
    {
        this.deathDate = deathDate;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Boolean getIsDead()
    {
        return isDead;
    }

    public void setIsDead( Boolean isDead )
    {
        this.isDead = isDead;
    }

    public Set<PatientIdentifier> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers( Set<PatientIdentifier> identifiers )
    {
        this.identifiers = identifiers;
    }
    
    public Set<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    public void setProgramInstances( Set<ProgramInstance> programInstances )
    {
        this.programInstances = programInstances;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public User getHealthWorker()
    {
        return healthWorker;
    }

    public void setHealthWorker( User healthWorker )
    {
        this.healthWorker = healthWorker;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Patient getRepresentative()
    {
        return representative;
    }

    public void setRepresentative( Patient representative )
    {
        this.representative = representative;
    }

    // -------------------------------------------------------------------------
    // Convenience method
    // -------------------------------------------------------------------------

    public String getAge()
    {
        if ( birthDate == null )
        {
            return "0";
        }

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime( birthDate );

        Calendar todayCalendar = Calendar.getInstance();

        int age = todayCalendar.get( Calendar.YEAR ) - birthCalendar.get( Calendar.YEAR );

        if ( todayCalendar.get( Calendar.MONTH ) < birthCalendar.get( Calendar.MONTH ) )
        {
            age--;
        }
        else if ( todayCalendar.get( Calendar.MONTH ) == birthCalendar.get( Calendar.MONTH )
            && todayCalendar.get( Calendar.DAY_OF_MONTH ) < birthCalendar.get( Calendar.DAY_OF_MONTH ) )
        {
            age--;
        }

        if ( age < 1 )
        {
            return "< 1 yr";
        }

        return age + " yr";
    }

    public int getIntegerValueOfAge()
    {
        return getIntegerValueOfAge( birthDate );
    }

    public static int getIntegerValueOfAge( Date birthDate )
    {
        if ( birthDate == null )
        {
            return 0;
        }

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime( birthDate );

        Calendar todayCalendar = Calendar.getInstance();

        int age = todayCalendar.get( Calendar.YEAR ) - birthCalendar.get( Calendar.YEAR );

        if ( todayCalendar.get( Calendar.MONTH ) < birthCalendar.get( Calendar.MONTH ) )
        {
            age--;
        }
        else if ( todayCalendar.get( Calendar.MONTH ) == birthCalendar.get( Calendar.MONTH )
            && todayCalendar.get( Calendar.DAY_OF_MONTH ) < birthCalendar.get( Calendar.DAY_OF_MONTH ) )
        {
            age--;
        }

        return age;
    }

    public static Date getBirthFromAge( int age, char ageType )
    {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.clear( Calendar.MILLISECOND );
        todayCalendar.clear( Calendar.SECOND );
        todayCalendar.clear( Calendar.MINUTE );
        todayCalendar.set( Calendar.HOUR_OF_DAY, 0 );

        // Assumed relative to the 1st of January
        // todayCalendar.set( Calendar.DATE, 1 );
        // todayCalendar.set( Calendar.MONTH, Calendar.JANUARY );

        if ( ageType == AGE_TYPE_YEAR )
        {
            todayCalendar.add( Calendar.YEAR, -1 * age );
        }
        else if ( ageType == AGE_TYPE_MONTH )
        {
            todayCalendar.add( Calendar.MONTH, -1 * age );
        }
        else if ( ageType == AGE_TYPE_DAY )
        {
            todayCalendar.add( Calendar.DATE, -1 * age );
        }

        return todayCalendar.getTime();
    }

    public void setBirthDateFromAge( int age, char ageType )
    {
        Date fromAge = getBirthFromAge( age, ageType );
        setBirthDate( fromAge );
    }

    public char getAgeType()
    {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.clear( Calendar.MILLISECOND );
        todayCalendar.clear( Calendar.SECOND );
        todayCalendar.clear( Calendar.MINUTE );
        todayCalendar.set( Calendar.HOUR_OF_DAY, 0 );

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime( birthDate );

        int age = todayCalendar.get( Calendar.YEAR ) - birthCalendar.get( Calendar.YEAR );

        if ( age > 0 )
        {
            return AGE_TYPE_YEAR;
        }

        age = todayCalendar.get( Calendar.MONTH ) - birthCalendar.get( Calendar.MONTH );
        if ( age > 0 )
        {
            return AGE_TYPE_MONTH;
        }

        return AGE_TYPE_DAY;
    }

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public boolean isUnderAge()
    {
        return underAge;
    }

    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }

    public String getTextGender()
    {
        return gender.equalsIgnoreCase( MALE ) ? "male" : "female";
    }

    public Character getDobType()
    {
        return dobType;
    }

    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }

    public String getTextDoBType()
    {
        switch ( dobType )
        {
            case DOB_TYPE_VERIFIED:
                return "Verified";
            case DOB_TYPE_DECLARED:
                return "Declared";
            default:
                return "Approxiated";
        }
    }
}
