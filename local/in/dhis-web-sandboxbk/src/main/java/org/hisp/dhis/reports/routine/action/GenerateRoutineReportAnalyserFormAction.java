package org.hisp.dhis.reports.routine.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.api.Report;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;

public class GenerateRoutineReportAnalyserFormAction
    extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private List<Period> periodList;
    
    public List<Period> getPeriodList() 
    {
		return periodList;
	}
    
    private List<Report> reportList;

    public List<Report> getReportList()
    {
        return reportList;
    }

    String periodType;
    String orgUnitLevel;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
        throws Exception
    {
		periodType = "Yearly";
		orgUnitLevel = "1";
		simpleDateFormat = new SimpleDateFormat( "yyyy" );
		reportList = new ArrayList<Report>();
		
		
        /* Period Info */
    	PeriodType yearlyPeriodType = PeriodType.getPeriodTypeByName("Yearly");
    	periodList = new ArrayList<Period>(periodService.getPeriodsByPeriodType(yearlyPeriodType));
    	
    	getSelectedReportList();
    	
        return SUCCESS;
    }
	
    public void getSelectedReportList()
    {
        String fileName = "routineReportsList.xml";
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "USER_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + "dhis" + File.separator + "ra_national" + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        String reportId = "";
        String reportName = "";
        String reportType = "";
        String reportLevel = "";
        String reportModel = "";
        String reportFileName = "";
        int count = 0;

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

            NodeList listOfReports = doc.getElementsByTagName( "report" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;
                    reportId = reportElement.getAttribute( "id" );

                    NodeList reportNameList = reportElement.getElementsByTagName( "name" );
                    Element reportNameElement = (Element) reportNameList.item( 0 );
                    NodeList textreportNameList = reportNameElement.getChildNodes();
                    reportName = ((Node) textreportNameList.item( 0 )).getNodeValue().trim();

                    NodeList reportTypeList = reportElement.getElementsByTagName( "type" );
                    Element reportTypeElement = (Element) reportTypeList.item( 0 );
                    NodeList textreportTypeList = reportTypeElement.getChildNodes();
                    reportType = ((Node) textreportTypeList.item( 0 )).getNodeValue().trim();

                    NodeList reportModelList = reportElement.getElementsByTagName( "model" );
                    Element reportModelElement = (Element) reportModelList.item( 0 );
                    NodeList textreportModelList = reportModelElement.getChildNodes();
                    reportModel = ((Node) textreportModelList.item( 0 )).getNodeValue().trim();

                    NodeList reportFileNameList = reportElement.getElementsByTagName( "filename" );
                    Element reportFileNameElement = (Element) reportFileNameList.item( 0 );
                    NodeList textreportFileNameList = reportFileNameElement.getChildNodes();
                    reportFileName = ((Node) textreportFileNameList.item( 0 )).getNodeValue().trim();

                    NodeList reportLevelList = reportElement.getElementsByTagName( "level" );
                    Element reportLevelElement = (Element) reportLevelList.item( 0 );
                    NodeList textreportLevelList = reportLevelElement.getChildNodes();
                    reportLevel = ((Node) textreportLevelList.item( 0 )).getNodeValue().trim();

                    if ( reportType.equals( periodType ) && reportLevel.equals( orgUnitLevel ) )
                    {
                        Report reportObj = new Report(reportId, reportName, reportType, reportModel, reportFileName, reportLevel);
                        reportList.add( count, reportObj );
                        /*                         
                         reportIds.add( count, reportId );
                        reportNames.add( count, reportName );
                        reportModels.add( count, reportModel );
                        */
                        count++;
                        System.out.println( reportName + " : " + reportId );
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

    }// getReportList end

}
