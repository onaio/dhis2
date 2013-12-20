package org.hisp.dhis.coldchain.reports.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class CCEMReportPageAction
    implements Action
{
    public static final String OWNERSHIP_GROUP_SET = "Ownership";// 2.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private List<OrganisationUnitGroup> orgUnitGroupList;

    public List<OrganisationUnitGroup> getOrgUnitGroupList()
    {
        return orgUnitGroupList;
    }

    private List<CCEMReport> reportList;

    public List<CCEMReport> getReportList()
    {
        return reportList;
    }

    public String facilityType = "";

    public void setFacilityType( String facilityType )
    {
        this.facilityType = facilityType;
    }
    
    public String getFacilityType()
    {
        return facilityType;
    }
    
    private List<OrganisationUnitGroup> orgUnitGroups;
    
    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        // System.out.println("fff  "+facilityType);
        orgUnitGroupList = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService
            .getAllOrganisationUnitGroups() );
        //CCEMReport ccem = new CCEMReport();
        // if(ccem.getCategoryType() == facilityType)
        {
            reportList = new ArrayList<CCEMReport>();

            getCCEMReportList();
        }
        
        Constant ownerShipGroupConstant = constantService.getConstantByName( OWNERSHIP_GROUP_SET );
        
        OrganisationUnitGroupSet organisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( (int) ownerShipGroupConstant.getValue() );
        
        if( organisationUnitGroupSet != null )
        {
            orgUnitGroups = new ArrayList<OrganisationUnitGroup>(organisationUnitGroupSet.getOrganisationUnitGroups());            
        }
        
        /*
        for( OrganisationUnitGroup organisationUnitGroup : orgUnitGroups )
        {
            System.out.println(" organisationUnitGroup   " + organisationUnitGroup.getName() );
        }
        */
        
        return SUCCESS;
    }

    public void getCCEMReportList()
    {
        String fileName = "ccemReportList.xml";
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "ccemreports" + File.separator + fileName;

        // JAXBContext context = JAXBContext.newInstance( CCEMReport.class );
        // Unmarshaller um = context.createUnmarshaller();
        // CCEMReport ccemReport = (CCEMReport) um.unmarshal( new FileReader(
        // path ) );

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName( "ccemReport" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    NodeList nodeList1 = reportElement.getElementsByTagName( "reportId" );
                    Element element1 = (Element) nodeList1.item( 0 );
                    NodeList textNodeList1 = element1.getChildNodes();
                    String reportId = ((Node) textNodeList1.item( 0 )).getNodeValue().trim();

                    NodeList nodeList2 = reportElement.getElementsByTagName( "reportName" );
                    Element element2 = (Element) nodeList2.item( 0 );
                    NodeList textNodeList2 = element2.getChildNodes();
                    String reportName = ((Node) textNodeList2.item( 0 )).getNodeValue().trim();

                    NodeList nodeList3 = reportElement.getElementsByTagName( "xmlTemplateName" );
                    Element element3 = (Element) nodeList3.item( 0 );
                    NodeList textNodeList3 = element3.getChildNodes();
                    String xmlTemplateName = ((Node) textNodeList3.item( 0 )).getNodeValue().trim();

                    NodeList nodeList4 = reportElement.getElementsByTagName( "outputType" );
                    Element element4 = (Element) nodeList4.item( 0 );
                    NodeList textNodeList4 = element4.getChildNodes();
                    String outputType = ((Node) textNodeList4.item( 0 )).getNodeValue().trim();

                    NodeList nodeList5 = reportElement.getElementsByTagName( "periodRequire" );
                    Element element5 = (Element) nodeList5.item( 0 );
                    NodeList textNodeList5 = element5.getChildNodes();
                    String periodRequire = ((Node) textNodeList5.item( 0 )).getNodeValue().trim();

                    NodeList nodeList6 = reportElement.getElementsByTagName( "categoryType" );
                    Element element6 = (Element) nodeList6.item( 0 );
                    NodeList textNodeList6 = element6.getChildNodes();
                    String categoryType = ((Node) textNodeList6.item( 0 )).getNodeValue().trim();

                    CCEMReport reportObj = new CCEMReport();

                    reportObj.setOutputType( outputType );
                    reportObj.setReportId( reportId );
                    reportObj.setReportName( reportName );
                    reportObj.setXmlTemplateName( xmlTemplateName );
                    reportObj.setPeriodRequire( periodRequire );
                    reportObj.setCategoryType( categoryType );
                    if ( reportObj.getCategoryType().equals( facilityType ) )
                    {
                        reportList.add( reportObj );
                    }

                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

    }
    // getReportList end

}
