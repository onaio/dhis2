package org.hisp.dhis.api.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "orgUnit" )
public class MobileOrgUnitLinks
    implements DataStreamSerializable
{
    private String clientVersion;

    private int id;

    private String name;

    private String downloadAllUrl;

    private String updateActivityPlanUrl;

    private String uploadFacilityReportUrl;

    private String uploadActivityReportUrl;

    private String updateDataSetUrl;

    private String changeUpdateDataSetLangUrl;

    private String searchUrl;

    public static double currentVersion = 2.11;

    private String updateNewVersionUrl;

    private String updateContactUrl;

    private String findPatientUrl;

    private String uploadProgramStageUrl;

    private String enrollProgramUrl;

    private String registerPersonUrl;

    private String addRelationshipUrl;

    private String downloadAnonymousProgramUrl;

    private String findProgramUrl;

    private String getVariesInfoUrl;

    private String findLatestPersonUrl;
    
    private String findPatientInAdvancedUrl;
    
    private String findLostToFollowUpUrl;
    
    private String handleLostToFollowUpUrl;

    @XmlAttribute
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @XmlAttribute
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDownloadAllUrl()
    {
        return downloadAllUrl;
    }

    public void setDownloadAllUrl( String downloadAllUrl )
    {
        this.downloadAllUrl = downloadAllUrl;
    }

    public String getUploadFacilityReportUrl()
    {
        return uploadFacilityReportUrl;
    }

    public void setUploadFacilityReportUrl( String uploadFacilityReportUrl )
    {
        this.uploadFacilityReportUrl = uploadFacilityReportUrl;
    }

    public String getUploadActivityReportUrl()
    {
        return uploadActivityReportUrl;
    }

    public void setUploadActivityReportUrl( String uploadActivityReportUrl )
    {
        this.uploadActivityReportUrl = uploadActivityReportUrl;
    }

    public String getUpdateDataSetUrl()
    {
        return updateDataSetUrl;
    }

    public void setUpdateDataSetUrl( String updateDataSetUrl )
    {
        this.updateDataSetUrl = updateDataSetUrl;
    }

    public String getChangeUpdateDataSetLangUrl()
    {
        return changeUpdateDataSetLangUrl;
    }

    public void setChangeUpdateDataSetLangUrl( String changeUpdateDataSetLangUrl )
    {
        this.changeUpdateDataSetLangUrl = changeUpdateDataSetLangUrl;
    }

    public String getSearchUrl()
    {
        return searchUrl;
    }

    public void setSearchUrl( String searchUrl )
    {
        this.searchUrl = searchUrl;
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }

    public String getUpdateActivityPlanUrl()
    {
        return updateActivityPlanUrl;
    }

    public void setUpdateActivityPlanUrl( String updateActivityPlanUrl )
    {
        this.updateActivityPlanUrl = updateActivityPlanUrl;
    }

    public String getUpdateNewVersionUrl()
    {
        return updateNewVersionUrl;
    }

    public void setUpdateNewVersionUrl( String updateNewVersionUrl )
    {
        this.updateNewVersionUrl = updateNewVersionUrl;
    }

    public String getUpdateContactUrl()
    {
        return updateContactUrl;
    }

    public void setUpdateContactUrl( String updateContactUrl )
    {
        this.updateContactUrl = updateContactUrl;
    }

    public String getFindPatientUrl()
    {
        return findPatientUrl;
    }

    public void setFindPatientUrl( String findPatientUrl )
    {
        this.findPatientUrl = findPatientUrl;
    }

    public String getUploadProgramStageUrl()
    {
        return uploadProgramStageUrl;
    }

    public void setUploadProgramStageUrl( String uploadProgramStageUrl )
    {
        this.uploadProgramStageUrl = uploadProgramStageUrl;
    }

    public String getEnrollProgramUrl()
    {
        return enrollProgramUrl;
    }

    public void setEnrollProgramUrl( String enrollProgramUrl )
    {
        this.enrollProgramUrl = enrollProgramUrl;
    }

    public String getRegisterPersonUrl()
    {
        return registerPersonUrl;
    }

    public void setRegisterPersonUrl( String registerPersonUrl )
    {
        this.registerPersonUrl = registerPersonUrl;
    }

    public String getGetVariesInfoUrl()
    {
        return getVariesInfoUrl;
    }

    public void setGetVariesInfoUrl( String getVariesInfoUrl )
    {
        this.getVariesInfoUrl = getVariesInfoUrl;
    }

    public String getAddRelationshipUrl()
    {
        return addRelationshipUrl;
    }

    public void setAddRelationshipUrl( String addRelationshipUrl )
    {
        this.addRelationshipUrl = addRelationshipUrl;
    }

    public String getDownloadAnonymousProgramUrl()
    {
        return downloadAnonymousProgramUrl;
    }

    public void setDownloadAnonymousProgramUrl( String downloadAnonymousProgramUrl )
    {
        this.downloadAnonymousProgramUrl = downloadAnonymousProgramUrl;
    }

    public String getFindProgramUrl()
    {
        return findProgramUrl;
    }

    public void setFindProgramUrl( String findProgramUrl )
    {
        this.findProgramUrl = findProgramUrl;
    }

    public String getFindLatestPersonUrl()
    {
        return findLatestPersonUrl;
    }

    public void setFindLatestPersonUrl( String findLatestPersonUrl )
    {
        this.findLatestPersonUrl = findLatestPersonUrl;
    }

    public String getFindPatientInAdvancedUrl()
    {
        return findPatientInAdvancedUrl;
    }

    public void setFindPatientInAdvancedUrl( String findPatientInAdvancedUrl )
    {
        this.findPatientInAdvancedUrl = findPatientInAdvancedUrl;
    }

    public String getFindLostToFollowUpUrl()
    {
        return findLostToFollowUpUrl;
    }

    public void setFindLostToFollowUpUrl( String findLostToFollowUpUrl )
    {
        this.findLostToFollowUpUrl = findLostToFollowUpUrl;
    }

    public String getHandleLostToFollowUpUrl()
    {
        return handleLostToFollowUpUrl;
    }

    public void setHandleLostToFollowUpUrl( String handleLostToFollowUpUrl )
    {
        this.handleLostToFollowUpUrl = handleLostToFollowUpUrl;
    }

    public void serialize( DataOutputStream dataOutputStream )

        throws IOException
    {
        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( this.name );
        dataOutputStream.writeUTF( this.downloadAllUrl );
        dataOutputStream.writeUTF( this.updateActivityPlanUrl );
        dataOutputStream.writeUTF( this.uploadFacilityReportUrl );
        dataOutputStream.writeUTF( this.uploadActivityReportUrl );
        dataOutputStream.writeUTF( this.updateDataSetUrl );
        dataOutputStream.writeUTF( this.changeUpdateDataSetLangUrl );
        dataOutputStream.writeUTF( this.searchUrl );
        dataOutputStream.writeUTF( this.updateNewVersionUrl );
        dataOutputStream.writeUTF( this.updateContactUrl );
        dataOutputStream.writeUTF( this.findPatientUrl );
        dataOutputStream.writeUTF( this.registerPersonUrl );
        dataOutputStream.writeUTF( this.uploadProgramStageUrl );
        dataOutputStream.writeUTF( this.enrollProgramUrl );
        dataOutputStream.writeUTF( this.getVariesInfoUrl );
        dataOutputStream.writeUTF( this.addRelationshipUrl );
        dataOutputStream.writeUTF( this.downloadAnonymousProgramUrl );
        dataOutputStream.writeUTF( this.findProgramUrl );
        dataOutputStream.writeUTF( this.findLatestPersonUrl );
        dataOutputStream.writeUTF( this.findPatientInAdvancedUrl );
        dataOutputStream.writeUTF( this.findLostToFollowUpUrl );
        dataOutputStream.writeUTF( this.handleLostToFollowUpUrl );
    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.id = dataInputStream.readInt();
        this.name = dataInputStream.readUTF();
        this.downloadAllUrl = dataInputStream.readUTF();
        this.updateActivityPlanUrl = dataInputStream.readUTF();
        this.uploadFacilityReportUrl = dataInputStream.readUTF();
        this.uploadActivityReportUrl = dataInputStream.readUTF();
        this.updateDataSetUrl = dataInputStream.readUTF();
        this.changeUpdateDataSetLangUrl = dataInputStream.readUTF();
        this.searchUrl = dataInputStream.readUTF();
        this.updateNewVersionUrl = dataInputStream.readUTF();
        this.updateContactUrl = dataInputStream.readUTF();
        this.findPatientUrl = dataInputStream.readUTF();
        this.registerPersonUrl = dataInputStream.readUTF();
        this.uploadProgramStageUrl = dataInputStream.readUTF();
        this.enrollProgramUrl = dataInputStream.readUTF();
        this.getVariesInfoUrl = dataInputStream.readUTF();
        this.addRelationshipUrl = dataInputStream.readUTF();
        this.downloadAnonymousProgramUrl = dataInputStream.readUTF();
        this.findProgramUrl = dataInputStream.readUTF();
        this.findLatestPersonUrl = dataInputStream.readUTF();
        this.findPatientInAdvancedUrl = dataInputStream.readUTF();
        this.findLostToFollowUpUrl = dataInputStream.readUTF();
        this.handleLostToFollowUpUrl = dataInputStream.readUTF();
    }

    @Override
    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( this.name );
        dataOutputStream.writeUTF( this.downloadAllUrl );
        dataOutputStream.writeUTF( this.updateActivityPlanUrl );
        dataOutputStream.writeUTF( this.uploadFacilityReportUrl );
        dataOutputStream.writeUTF( this.uploadActivityReportUrl );
        dataOutputStream.writeUTF( this.updateDataSetUrl );
        dataOutputStream.writeUTF( this.changeUpdateDataSetLangUrl );
        dataOutputStream.writeUTF( this.searchUrl );
    }

    @Override
    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( this.name );
        dataOutputStream.writeUTF( this.downloadAllUrl );
        dataOutputStream.writeUTF( this.updateActivityPlanUrl );
        dataOutputStream.writeUTF( this.uploadFacilityReportUrl );
        dataOutputStream.writeUTF( this.uploadActivityReportUrl );
        dataOutputStream.writeUTF( this.updateDataSetUrl );
        dataOutputStream.writeUTF( this.changeUpdateDataSetLangUrl );
        dataOutputStream.writeUTF( this.searchUrl );
        dataOutputStream.writeUTF( this.updateNewVersionUrl );
        // dataOutputStream.writeUTF( this.updateContactUrl );
    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( this.name );
        dataOutputStream.writeUTF( this.downloadAllUrl );
        dataOutputStream.writeUTF( this.updateActivityPlanUrl );
        dataOutputStream.writeUTF( this.uploadFacilityReportUrl );
        dataOutputStream.writeUTF( this.uploadActivityReportUrl );
        dataOutputStream.writeUTF( this.updateDataSetUrl );
        dataOutputStream.writeUTF( this.changeUpdateDataSetLangUrl );
        dataOutputStream.writeUTF( this.searchUrl );
        dataOutputStream.writeUTF( this.updateNewVersionUrl );
        dataOutputStream.writeUTF( this.updateContactUrl );
        dataOutputStream.writeUTF( this.findPatientUrl );
        dataOutputStream.writeUTF( this.registerPersonUrl );
        dataOutputStream.writeUTF( this.uploadProgramStageUrl );
        dataOutputStream.writeUTF( this.enrollProgramUrl );
        dataOutputStream.writeUTF( this.getVariesInfoUrl );
        dataOutputStream.writeUTF( this.addRelationshipUrl );
        dataOutputStream.writeUTF( this.downloadAnonymousProgramUrl );
        dataOutputStream.writeUTF( this.findProgramUrl );
        dataOutputStream.writeUTF( this.findLatestPersonUrl );
        dataOutputStream.writeUTF( this.findPatientInAdvancedUrl );
        dataOutputStream.writeUTF( this.findLostToFollowUpUrl );
        dataOutputStream.writeUTF( this.handleLostToFollowUpUrl );
    }
}
