/**
 * 
 */
package org.hisp.dhis.ccem.reports.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.reports.CCEMReport;
import org.hisp.dhis.coldchain.reports.CCEMReportDesign;
import org.hisp.dhis.coldchain.reports.CCEMReportManager;
import org.hisp.dhis.coldchain.reports.CCEMReportOutput;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version GenerateJRXMLReportAction.java Jun 26, 2012 12:12:17 PM
 */
public class GenerateJRXMLReportAction
    implements Action
{
    private static final String DEFAULT_TYPE = "html";

    protected JasperPrint jasperPrint;

    protected JasperReport jr;

    protected Map param = new HashMap();

    protected DynamicReport dr;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CCEMReportManager ccemReportManager;

    public void setCcemReportManager( CCEMReportManager ccemReportManager )
    {
        this.ccemReportManager = ccemReportManager;
    }

    private ConstantService constantService;
    
    public void setConstantService(ConstantService constantService) 
    {
		this.constantService = constantService;
	}

	private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private List<Integer> selOrgUnitList;

    public void setSelOrgUnitList( List<Integer> selOrgUnitList )
    {
        this.selOrgUnitList = selOrgUnitList;
    }

    private List<Integer> orgunitGroupList;

    public void setOrgunitGroupList( List<Integer> orgunitGroupList )
    {
        this.orgunitGroupList = orgunitGroupList;
    }

    private CCEMReport ccemReport;

    public CCEMReport getCcemReport()
    {
        return ccemReport;
    }

    private CCEMReportOutput ccemReportOutput;

    public CCEMReportOutput getCcemReportOutput()
    {
        return ccemReportOutput;
    }

    private String periodRadio;

    public void setPeriodRadio( String periodRadio )
    {
        this.periodRadio = periodRadio;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private String pe;

    public void setPe( String pe )
    {
        this.pe = pe;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------
  
    @Override
    public String execute()
        throws Exception,JRException 
    {
        Connection con = jdbcTemplate.getDataSource().getConnection();
        String fileName = null;
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "ireports" + File.separator;

        HashMap<String, Object> hash = new HashMap<String, Object>();

        String orgUnitIdsByComma = ccemReportManager.getOrgunitIdsByComma( selOrgUnitList, orgunitGroupList );
        ccemReport = ccemReportManager.getCCEMReportByReportId( reportList );
        Map<String, String> ccemSettingsMap = new HashMap<String, String>( ccemReportManager.getCCEMSettings() );
        List<CCEMReportDesign> reportDesignList = new ArrayList<CCEMReportDesign>( ccemReportManager.getCCEMReportDesign( ccemReport.getXmlTemplateName() ) );

        String oName = null;
        String oUnitGrpName = null;
        oUnitGrpName = organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( 0 ) ).getName()
            + "";
        for ( int i = 1; i <= orgunitGroupList.size() - 1; i++ )
        {
            oUnitGrpName += ","
                + organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) ).getName();
            System.out.println( "Group is: "
                + organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupList.get( i ) ) );
        }
        oName = organisationUnitService.getOrganisationUnit( selOrgUnitList.get( 0 ) ).getName() + "";
        for ( int j = 1; j <= selOrgUnitList.size() - 1; j++ )
        {
            oName += "," + organisationUnitService.getOrganisationUnit( selOrgUnitList.get( j ) ).getName();
            System.out.println( "Group is: " + organisationUnitService.getOrganisationUnit( selOrgUnitList.get( j ) ) );
        }
        hash.put( "orgunitGroup", oUnitGrpName );
        hash.put( "selOrgUnit", oName );
        hash.put( "orgUnitIdsByComma", orgUnitIdsByComma );
        HttpServletResponse response = ServletActionContext.getResponse();

        ccemReport = ccemReportManager.getCCEMReportByReportId( reportList );
        Date date = pe != null ? DateUtils.getMediumDate( pe ) : new Date();

        hash.put( "reportName", ccemReport.getReportName() );
        hash.put( "date", date );
        if ( ccemReport.getReportType().equals( CCEMReport.MODELTYPE_ATTRIBUTE_VALUE ) )
        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            hash.put( "equipmentTypeId", equipmentTypeId );
            hash.put( "modelTypeAttributeId", modelTypeAttributeId );
            fileName = "Refrigerators_freezer_models_by_agegroup.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, con );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.MODELTYPE_ATTRIBUTE_VALUE_AGE_GROUP.trim() ) )

        {
            CCEMReportDesign ccemReportDesign = reportDesignList.get( 0 );
            String ccemCellContent = ccemSettingsMap.get( ccemReportDesign.getContent() );
            Integer equipmentTypeId = Integer.parseInt( ccemCellContent.split( ":" )[0] );
            Integer modelTypeAttributeId = Integer.parseInt( ccemCellContent.split( ":" )[1] );
            hash.put( "equipmentTypeId", equipmentTypeId );
            hash.put( "modelTypeAttributeId", modelTypeAttributeId );
            int i = 0;
            Integer equipmentTypeAttributeId = 3;
            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                i++;
                if ( i == 1 )
                    continue;
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                if ( ccemCellContent1.split( ":" )[3].equalsIgnoreCase( "UNKNOWN" ) )
                {

                }
                else if ( ccemCellContent1.split( ":" )[4].equalsIgnoreCase( "MORE" ) )
                {
                    equipmentTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    modelTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    equipmentTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
                else
                {
                    equipmentTypeId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                    modelTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );
                    equipmentTypeAttributeId = Integer.parseInt( ccemCellContent1.split( ":" )[2] );
                }
            }
            Map<String, Integer> modelTypeAttributeValueMap1 = new HashMap<String, Integer>( ccemReportManager
                .getModelTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 0, 2 ) );

            Map<String, Integer> modelTypeAttributeValueMap2 = new HashMap<String, Integer>( ccemReportManager
                .getModelTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 3, 5 ) );

            Map<String, Integer> modelTypeAttributeValueMap3 = new HashMap<String, Integer>( ccemReportManager
                .getModelTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId,
                    equipmentTypeAttributeId, 6, 10 ) );

            Map<String, Integer> modelTypeAttributeValueMap4 = new HashMap<String, Integer>( ccemReportManager
                .getModelTypeAttributeValueByAge( orgUnitIdsByComma, equipmentTypeId, modelTypeAttributeId, 3, 11,
                    -1 ) );

            hash.put( "Value_0_2", modelTypeAttributeValueMap1 );
            hash.put( "Value_3_5", modelTypeAttributeValueMap2 );
            hash.put( "Value_6_10", modelTypeAttributeValueMap3 );
            hash.put( "Value_11_MORE", modelTypeAttributeValueMap4 );

            fileName = "MODELTYPE ATTRIBUTE VALUE AGE GROUP.jrxml";
            JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
            jasperPrint = JasperFillManager.fillReport( jasperReport, hash, con );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.ORGUNITGROUP_DATAVALUE ) )
        {            
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List tableData = new ArrayList();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List<String> content = new ArrayList<String>();

            FastReportBuilder frb = new FastReportBuilder();
            Integer periodId = 0;
            Date date1 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date1 );
            String periodStartDate = "";
            Map<String, Integer> subHeadingNumber = new HashMap<String, Integer>();
            if ( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
                periodStartDate = (calendar.get( Calendar.YEAR ) - 1) + "-01-01";
            }

            periodId = ccemReportManager.getPeriodId( periodStartDate, ccemReport.getPeriodRequire() );
            tableHeadings.add( "Facility Type" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "Total Facilities" );
            oneSubHeadingRow.add( " " );
            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";
            List<String> dataElementOptions = new ArrayList<String>();

            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;
                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                List<String> distinctDataElementValues = new ArrayList<String>( ccemReportManager
                    .getDistinctDataElementValue( dataElementId, optComboId, periodId ) );
                int number = 0;
                for ( int i = 0; i < distinctDataElementValues.size(); i++ )
                {
                    if ( i != 0 )
                    {
                        tableHeadings.add( " " );
                    }
                    oneSubHeadingRow.add( distinctDataElementValues.get( i ).split( ":" )[2] );
                    dataElementOptions.add( distinctDataElementValues.get( i ) );
                    number++;
                }
                subHeadingNumber.put( ccemReportDesign1.getDisplayheading(), number );
            }

            tableSubHeadings.add( oneSubHeadingRow );
            int count = 0;

            for ( int i = 0; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {
                }
                else
                {
                    if ( i == 0 || i == 1 )
                    {
                        frb.addColumn( tableHeadings.get( i ), tableHeadings.get( i ), String.class.getName(), 100,
                            true );
                        count++;

                    }
                    else
                    {
                    }
                }
            }
            for ( int j = 0; j <= tableSubHeadings.size() - 1; j++ )
            {
                for ( int k = 0; k <= tableSubHeadings.get( j ).size() - 1; k++ )
                {
                    if ( tableSubHeadings.get( j ).get( k ) == " " )
                    {
                    }
                    else
                    {
                        frb.addColumn( tableSubHeadings.get( j ).get( k ), tableSubHeadings.get( j ).get( k ),
                            String.class.getName(), 50, true );
                        content.add( tableSubHeadings.get( j ).get( k ) );
                        count++;
                    }
                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, count ).setUseFullPageWidth( true );

            int start = 2;
            for ( int i = 2; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {

                }
                else
                {
                    frb.setColspan( start, subHeadingNumber.get( tableHeadings.get( i ) ), tableHeadings.get( i ) );
                    start = start + subHeadingNumber.get( tableHeadings.get( i ) );
                }
            }
            frb.setTemplateFile( path + "ORGUNITGROUP_DATAVALUE.jrxml" );
            frb.setPrintBackgroundOnOddRows(true);
            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                Map numberOfData = new HashMap();
                List<Integer> orgUnitIds = ccemReportManager.getOrgunitIds( selOrgUnitList, orgUnitGroupId );
                if ( orgUnitIds == null || orgUnitIds.size() <= 0 )
                {

                }
                else
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );

                    String orgUnitIdsBycomma = getCommaDelimitedString( orgUnitIds );
                    numberOfData.put( "Facility Type", orgUnitGroup.getName() );
                    numberOfData.put( "Total Facilities", "" + orgUnitIds.size() );
                    Map<String, Integer> dataValueCountMap2 = new HashMap<String, Integer>( ccemReportManager
                        .getDataValueCountforDataElements( dataElementIdsByComma, optComboIdsByComma, periodId,
                            orgUnitIdsBycomma ) );
                    for ( int i = 0; i <= dataElementOptions.size() - 1; i++ )
                    {
                        Integer temp = dataValueCountMap2.get( dataElementOptions.get( i ) );
                        if ( temp == null )
                        {
                            numberOfData.put( content.get( i ), "0" );
                        }
                        else
                        {
                            numberOfData.put( content.get( i ), temp + "" );
                        }
                    }
                    tableData.add( numberOfData );
                }
            }

            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }

        else if ( ccemReport.getReportType().equals( CCEMReport.ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE ) )
        {            
            List<String> tableHeadings = new ArrayList<String>();
            List<List<String>> tableSubHeadings = new ArrayList<List<String>>();
            List<String> oneSubHeadingRow = new ArrayList<String>();
            List tableData = new ArrayList();
            List<String> content = new ArrayList<String>();
            Date date2 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            String periodStartDate = "";
            String periodEndDate = "";
            String periodIdsByComma = "";
            List<Period> periodList = null;
            PeriodType periodType = periodService.getPeriodTypeByName( ccemReport.getPeriodRequire() );
            Date sDate = null;
            Date eDate = null;
            Map<String, Integer> subHeadingNumber = new HashMap<String, Integer>();
            int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

            tableHeadings.add( "OrgUnit Hierarchy" );
            oneSubHeadingRow.add( " " );
            tableHeadings.add( "OrgUnit" );
            oneSubHeadingRow.add( " " );

            if ( periodRadio.equalsIgnoreCase( CCEMReport.CURRENT_YEAR ) )
            {
                periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";
                periodEndDate = calendar.get( Calendar.YEAR ) + "-12-31";
                sDate = format.parseDate( periodStartDate );
                eDate = format.parseDate( periodEndDate );
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_YEAR ) )
            {
                periodStartDate = (calendar.get( Calendar.YEAR ) - 1) + "-01-01";
                periodEndDate = (calendar.get( Calendar.YEAR ) - 1) + "-12-31";
                sDate = format.parseDate( periodStartDate );
                eDate = format.parseDate( periodEndDate );
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_6_MONTHS ) )
            {
                calendar.add( Calendar.MONTH, -1 );
                calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
                eDate = calendar.getTime();

                calendar.add( Calendar.MONTH, -5 );
                calendar.set( Calendar.DATE, 1 );
                sDate = calendar.getTime();
            }
            else if ( periodRadio.equalsIgnoreCase( CCEMReport.LAST_3_MONTHS ) )
            {
                calendar.add( Calendar.MONTH, -1 );
                calendar.set( Calendar.DATE, monthDays[calendar.get( Calendar.MONTH )] );
                eDate = calendar.getTime();

                calendar.add( Calendar.MONTH, -2 );
                calendar.set( Calendar.DATE, 1 );
                sDate = calendar.getTime();
            }

            periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
            periodIdsByComma = getCommaDelimitedString( periodIds );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );

            String dataElementIdsByComma = "-1";
            String optComboIdsByComma = "-1";

            for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
            {
                String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );
                Integer optComboId = Integer.parseInt( ccemCellContent1.split( ":" )[1] );

                dataElementIdsByComma += "," + dataElementId;
                optComboIdsByComma += "," + optComboId;

                tableHeadings.add( ccemReportDesign1.getDisplayheading() );
                int i = 0;
                for ( Period period : periodList )
                {
                    oneSubHeadingRow.add( simpleDateFormat.format( period.getStartDate() ) );
                    if ( i != 0 )
                        tableHeadings.add( " " );
                    i++;
                }
                subHeadingNumber.put( ccemReportDesign1.getDisplayheading(), i );
            }

            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();

            for ( Integer orgUnitGroupId : orgunitGroupList )
            {
                OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitGroupId );
                orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            Map<String, Integer> equipmentDataValueMap = new HashMap<String, Integer>( ccemReportManager
                .getFacilityWiseEquipmentRoutineData( orgUnitIdsByComma, periodIdsByComma, dataElementIdsByComma,
                    optComboIdsByComma ) );

            FastReportBuilder frb = new FastReportBuilder();
            tableSubHeadings.add( oneSubHeadingRow );

            int count = 0;

            for ( int i = 0; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {
                }
                else
                {
                    if ( i == 0 || i == 1 )
                    {
                        content.add( tableHeadings.get( i ) );
                        frb
                            .addColumn( tableHeadings.get( i ), tableHeadings.get( i ), String.class.getName(), 50,
                                true );
                        count++;

                    }
                    else
                    {
                    }
                }
            }
            for ( int j = 0; j <= tableSubHeadings.size() - 1; j++ )
            {
                int increment = 0;

                for ( int k = 0; k <= tableSubHeadings.get( j ).size() - 1; k++ )
                {

                    if ( tableSubHeadings.get( j ).get( k ) == " " )
                    {
                    }
                    else
                    {
                        if ( tableSubHeadings.get( j ).get( k ).contains( tableSubHeadings.get( j ).get( k ) ) )
                        {
                            increment++;
                        }

                        frb.addColumn( tableSubHeadings.get( j ).get( k ), tableSubHeadings.get( j ).get( k ) + " "
                            + increment, String.class.getName(), 50, true );
                        content.add( tableSubHeadings.get( j ).get( k ) + " " + increment );
                        count++;
                    }
                }
            }
            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, count ).setUseFullPageWidth( true );

            int start = 2;
            for ( int i = 2; i <= tableHeadings.size() - 1; i++ )
            {
                if ( tableHeadings.get( i ) == " " )
                {

                }
                else
                {
                    frb.setColspan( start, subHeadingNumber.get( tableHeadings.get( i ) ), tableHeadings.get( i ) );
                    start = start + subHeadingNumber.get( tableHeadings.get( i ) );
                }
            }

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();               
                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }

                numberOfData.put( content.get( 0 ), orgUnitBranch );
                numberOfData.put( content.get( 1 ), orgUnit.getName() );
                int i = 2;
                for ( CCEMReportDesign ccemReportDesign1 : reportDesignList )
                {
                    String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
                    Integer dataElementId = Integer.parseInt( ccemCellContent1.split( ":" )[0] );                    

                    for ( Period period : periodList )
                    {
                        Integer temp = equipmentDataValueMap.get( orgUnit.getId() + ":" + dataElementId + ":"
                            + period.getId() );
                        if ( temp == null )
                        {
                            numberOfData.put( content.get( i ), " " );
                        }
                        else
                        {
                            numberOfData.put( content.get( i ), temp + "" );
                        }
                        i++;
                    }

                }
                tableData.add( numberOfData );
            }

            frb.setTemplateFile( path + "ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE.jrxml" );
            frb.setPrintBackgroundOnOddRows(true);
            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );
        }
        else if ( ccemReport.getReportType().equals( CCEMReport.VACCINE_STORAGE_CAPACITY ) )
        {            
            List tableData = new ArrayList();
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();            
            String orgUnitGroupIdsByComma = "-1";           
            Integer periodId = 0;
            Date date2 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date2 );
            String periodStartDate = "";

            periodStartDate = calendar.get( Calendar.YEAR ) + "-01-01";

            periodId = ccemReportManager.getPeriodId( periodStartDate, "Yearly" );

            FastReportBuilder frb = new FastReportBuilder();
            frb.addColumn( "OrgUnit Hierarchy", "OrgUnit Hierarchy", String.class.getName(), 170, true );
            frb.addColumn( "OrgUnit", "OrgUnit", String.class.getName(), 80, true );
            frb.addColumn( "OrgUnit Code", "OrgUnit Code", String.class.getName(), 70, true );
            frb.addColumn( "OrgUnit Type", "OrgUnit Type", String.class.getName(), 80, true );
            frb.addColumn( "Actual", "Actual", String.class.getName(), 50, true );
            frb.addColumn( "Required", "Required", String.class.getName(), 50, true );
            frb.addColumn( "Difference", "Difference", String.class.getName(), 50, true );
            frb.addColumn( ">30%", ">30%-1", String.class.getName(), 50, true );
            frb.addColumn( "10-30%", "10-30%-1", String.class.getName(), 50, true );
            frb.addColumn( "+/- 10%", "+/- 10%-1", String.class.getName(), 50, true );
            frb.addColumn( "10-30%", "10-30%-2", String.class.getName(), 50, true );
            frb.addColumn( ">30%", ">30%-2", String.class.getName(), 50, true );
            frb.setColspan( 4, 3, "Net Storage" );
            frb.setColspan( 7, 2, "Surplus" );
            frb.setColspan( 9, 1, "Match" );
            frb.setColspan( 10, 2, "Shortage" );

            CCEMReportDesign ccemReportDesign1 = reportDesignList.get( 0 );
            String ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            if ( ccemCellContent1.equals( "ALL" ) )
            {
                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                    orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                }
            }
            else
            {
                String orgUnitGroupIds[] = ccemReportDesign1.getContent().split( "," );

                for ( Integer orgUnitGroupId : orgunitGroupList )
                {
                    int flag = 0;
                    for ( String ouGroupId : orgUnitGroupIds )
                    {
                        if ( Integer.parseInt( ouGroupId ) == orgUnitGroupId )
                        {
                            orgUnitGroupIdsByComma += "," + orgUnitGroupId;
                            flag = 1;
                            break;
                        }
                    }

                    if ( flag == 0 )
                        continue;

                    OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService
                        .getOrganisationUnitGroup( orgUnitGroupId );
                    orgUnitGroupMembers.addAll( orgUnitGroup.getMembers() );
                }
            }

            for ( Integer orgUnitId : selOrgUnitList )
            {
                orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            }

            orgUnitList.retainAll( orgUnitGroupMembers );
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                orgUnitList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            // Calculations for Actual Column
            ccemReportDesign1 = reportDesignList.get( 1 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );

            String[] partsOfCellContent = ccemCellContent1.split( "-" );
            Integer vscrActualEquipmentTypeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[0] );
            Integer vscrActualEquipmentTypeAttributeId = Integer.parseInt( partsOfCellContent[0].split( ":" )[1] );
            Double factor = Double.parseDouble( partsOfCellContent[0].split( ":" )[2] );
            

            Map<Integer, Double> equipmentSumByEquipmentTypeMap = new HashMap<Integer, Double>( ccemReportManager
                .getSumOfEquipmentDatabyEquipmentType( orgUnitIdsByComma, vscrActualEquipmentTypeId,
                    vscrActualEquipmentTypeAttributeId, factor ) );

            String[] partsOfVSRActualCellContent = partsOfCellContent[1].split( ":" );
            Integer vsrActualEquipmentTypeId = Integer.parseInt( partsOfVSRActualCellContent[0] );
            Integer vsrActualModelTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[1] );
            Integer vsrActualEquipmentTypeAttributeId = Integer.parseInt( partsOfVSRActualCellContent[2] );
            String vsrActualEquipmentValue = partsOfVSRActualCellContent[3];

            Map<Integer, Double> modelSumByEquipmentDataMap = new HashMap<Integer, Double>( ccemReportManager
                .getModelDataSumByEquipmentData( orgUnitIdsByComma, vsrActualEquipmentTypeId,
                    vsrActualModelTypeAttributeId, vsrActualEquipmentTypeAttributeId, vsrActualEquipmentValue ) );

            /*
            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            String[] modelDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqModelTypeId = Integer.parseInt( modelDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( modelDataParts[1] );
            String vsReqStorageTemp = modelDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( modelDataParts[3] );
            String vsReqNationalSupply = modelDataParts[4];
            String vsReqModelAttribIds = modelDataParts[5];

            Integer vsReqPackedVol = Integer.parseInt( vsReqModelAttribIds.split( "," )[0] );
            Integer vsReqDiluentVol = Integer.parseInt( vsReqModelAttribIds.split( "," )[1] );
            Integer vsReqDoses = Integer.parseInt( vsReqModelAttribIds.split( "," )[2] );
            Integer vsReqTargetPopCat = Integer.parseInt( vsReqModelAttribIds.split( "," )[3] );
            Integer vsReqUsage = Integer.parseInt( vsReqModelAttribIds.split( "," )[4] );
            Integer vsReqWastage = Integer.parseInt( vsReqModelAttribIds.split( "," )[5] );

            List<Integer> modelIdsForRequirement = new ArrayList<Integer>( ccemReportManager
                .getModelIdsForRequirement( vsReqModelTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply ) );

            Map<String, String> modelDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getModelDataForRequirement( vsReqModelTypeId, vsReqStorageTempId, vsReqStorageTemp,
                    vsReqNationalSupplyId, vsReqNationalSupply, vsReqModelAttribIds ) );

            Integer vsReqStaticDel = Integer.parseInt( partsOfCellContent[3].split( "," )[0] );
            Integer vsReqOutReachDel = Integer.parseInt( partsOfCellContent[3].split( "," )[1] );

            String modelOption_DataelementIds = vsReqStaticDel + "," + vsReqOutReachDel;

            String[] dataelementDataParts = partsOfCellContent[1].split( "," );
            Map<String, Integer> modelOption_DataelementMap = new HashMap<String, Integer>();

            for ( String de_modelOption : dataelementDataParts )
            {
                modelOption_DataelementMap.put( de_modelOption.split( ":" )[1], Integer.parseInt( de_modelOption
                    .split( ":" )[0] ) );
                modelOption_DataelementIds += "," + Integer.parseInt( de_modelOption.split( ":" )[0] );
            }

            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getDataElementDataForModelOptionsForRequirement( orgUnitIdsByComma, modelOption_DataelementIds,
                    periodId ) );

            String orgUnitGroupAttribIds = partsOfCellContent[2];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager
                .getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager
                .getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();
                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }
                numberOfData.put( "OrgUnit Hierarchy", orgUnitBranch );
                numberOfData.put( "OrgUnit", orgUnit.getName() );
                numberOfData.put( "OrgUnit Code", orgUnit.getCode() );
                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "OrgUnit Type", " " );
                }
                else
                {
                    numberOfData.put( "OrgUnit Type", orgUnitGroupName );
                }

                Double vsrActualValue = modelSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByEquipmentTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Actual", vaccineActualValue + "" );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                for ( Integer modelId : modelIdsForRequirement )
                {
                    Double vsReqUsageData = 0.0;
                    tempStr = modelDataForRequirement.get( modelId + ":" + vsReqUsage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqUsageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqUsageData = 0.0;
                        }
                    }

                    Double vsReqTargetPopData = 0.0;
                    String vsReqTargetPopCatData = modelDataForRequirement.get( modelId + ":" + vsReqTargetPopCat );
                    if ( vsReqTargetPopCatData != null )
                    {
                        Integer deId = modelOption_DataelementMap.get( vsReqTargetPopCatData );
                        tempStr = dataElementDataForRequirement.get( deId + ":" + periodId + ":" + orgUnit.getId() );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqTargetPopData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqTargetPopData = 0.0;
                            }
                        }
                    }

                    Double vsReqDosesData = 0.0;
                    tempStr = modelDataForRequirement.get( modelId + ":" + vsReqDoses );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqDosesData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqDosesData = 0.0;
                        }
                    }

                    Double vsReqPackedVolData = 0.0;
                    tempStr = modelDataForRequirement.get( modelId + ":" + vsReqPackedVol );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqPackedVolData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqPackedVolData = 0.0;
                        }
                    }

                    String tempStr1 = dataElementDataForRequirement.get( vsReqStaticDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    String tempStr2 = dataElementDataForRequirement.get( vsReqOutReachDel + ":" + periodId + ":"
                        + orgUnit.getId() );
                    if ( (tempStr1 != null && tempStr1.equalsIgnoreCase( "true" ))
                        || (tempStr2 != null && tempStr2.equalsIgnoreCase( "true" )) )
                    {
                        Double vsReqDiluentVolData = 0.0;
                        tempStr = modelDataForRequirement.get( modelId + ":" + vsReqDiluentVol );
                        if ( tempStr != null )
                        {
                            try
                            {
                                vsReqDiluentVolData = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                vsReqDiluentVolData = 0.0;
                            }
                        }

                        vsReqPackedVolData += vsReqDiluentVolData;
                    }

                    Double vsReqWastageData = 0.0;
                    tempStr = modelDataForRequirement.get( modelId + ":" + vsReqWastage );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqWastageData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqWastageData = 0.0;
                        }
                    }

                    Double vsReqSupplyIntervalData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqSupplyIntervalData = 0.0;
                        }
                    }

                    Double vsReqReserveStockData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqReserveStockData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqReserveStockData = 0.0;
                        }
                    }

                    // Formula for calculating Requirement for individual
                    // vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = ((vsReqUsageData * vsReqTargetPopData) / 100) * vsReqDosesData
                            * vsReqPackedVolData * (1 / (1 - (vsReqWastageData / 100)))
                            * (((vsReqSupplyIntervalData + vsReqReserveStockData) / 52) / 1000);
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }
                    
                    vaccineRequirement += individualVaccineRequirement;

                }
				*/

            
            // Calculations for Required Column
            ccemReportDesign1 = reportDesignList.get( 2 );
            ccemCellContent1 = ccemSettingsMap.get( ccemReportDesign1.getContent() );
            partsOfCellContent = ccemCellContent1.split( "--" );

            // PART 1 - 4:47:+4C:48:Yes
            String[] modelDataParts = partsOfCellContent[0].split( ":" );
            Integer vsReqModelTypeId = Integer.parseInt( modelDataParts[0] );
            Integer vsReqStorageTempId = Integer.parseInt( modelDataParts[1] );
            String vsReqStorageTemp = modelDataParts[2];
            Integer vsReqNationalSupplyId = Integer.parseInt( modelDataParts[3] );
            String vsReqNationalSupply = modelDataParts[4];
            
            List<Integer> modelIdsForRequirement = new ArrayList<Integer>( ccemReportManager.getModelIdsForRequirement( vsReqModelTypeId, vsReqStorageTempId, vsReqStorageTemp, vsReqNationalSupplyId, vsReqNationalSupply ) );

            // PART 2 - 5
            String dataelementIds = ""+ partsOfCellContent[1];
            Integer vsReqLiveBirthDeId = Integer.parseInt( dataelementIds.split( "," )[0] );
            Map<String, String> dataElementDataForRequirement = new HashMap<String, String>( ccemReportManager.getDataElementDataForModelOptionsForRequirement( orgUnitIdsByComma, dataelementIds, periodId ) );

            // PART 3 -  VaccineVolumePerChild
            String vvpcConstantName = ""+ partsOfCellContent[2];
            Constant constant = constantService.getConstantByName( vvpcConstantName );
            Double vsReqVaccineVolumePerChildData = constant.getValue();
            
            // PART 4 - 3,4
            String orgUnitGroupAttribIds = partsOfCellContent[3];
            Integer vsReqSupplyInterval = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[0] );
            Integer vsReqReserveStock = Integer.parseInt( orgUnitGroupAttribIds.split( "," )[1] );

            Map<String, String> orgUnitGroupAttribDataForRequirement = new HashMap<String, String>( ccemReportManager.getOrgUnitGroupAttribDataForRequirement( orgUnitGroupIdsByComma, orgUnitGroupAttribIds ) );

            Map<Integer, String> orgUnitGroupMap = new HashMap<Integer, String>( ccemReportManager.getOrgunitAndOrgUnitGroupMap( orgUnitGroupIdsByComma, orgUnitIdsByComma ) );

            for ( OrganisationUnit orgUnit : orgUnitList )
            {
                Map<String, String> numberOfData = new HashMap<String, String>();
                String orgUnitBranch = "";
                if ( orgUnit.getParent() != null )
                {
                    orgUnitBranch = getOrgunitBranch( orgUnit.getParent() );
                }
                else
                {
                    orgUnitBranch = " ";
                }
                numberOfData.put( "OrgUnit Hierarchy", orgUnitBranch );
                numberOfData.put( "OrgUnit", orgUnit.getName() );
                numberOfData.put( "OrgUnit Code", orgUnit.getCode() );
                String orgUnitGroupName = orgUnitGroupMap.get( orgUnit.getId() );
                if ( orgUnitGroupName == null )
                {
                    numberOfData.put( "OrgUnit Type", " " );
                }
                else
                {
                    numberOfData.put( "OrgUnit Type", orgUnitGroupName );
                }

                Double vsrActualValue = modelSumByEquipmentDataMap.get( orgUnit.getId() );
                if ( vsrActualValue == null )
                    vsrActualValue = 0.0;

                Double vscrActualValue = equipmentSumByEquipmentTypeMap.get( orgUnit.getId() );
                if ( vscrActualValue == null )
                    vscrActualValue = 0.0;

                Double vaccineActualValue = vsrActualValue + vscrActualValue;
                vaccineActualValue = Math.round( vaccineActualValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Actual", vaccineActualValue + "" );

                // Calculation for Requirement Column
                String tempStr = null;
                Double vaccineRequirement = 0.0;
                for ( Integer modelId : modelIdsForRequirement )
                {
                    Double vsReqLiveBirthData = 0.0;
                    tempStr = dataElementDataForRequirement.get( vsReqLiveBirthDeId + ":" + periodId + ":" + orgUnit.getId() );
                    if ( tempStr != null )
                    {
                        try
                        {
                        	vsReqLiveBirthData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                        	vsReqLiveBirthData = 0.0;
                        }
                    }

                    Double vsReqSupplyIntervalData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqSupplyInterval );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqSupplyIntervalData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqSupplyIntervalData = 0.0;
                        }
                    }

                    Double vsReqReserveStockData = 0.0;
                    tempStr = orgUnitGroupAttribDataForRequirement.get( orgUnit.getId() + ":" + vsReqReserveStock );
                    if ( tempStr != null )
                    {
                        try
                        {
                            vsReqReserveStockData = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            vsReqReserveStockData = 0.0;
                        }
                    }

                    // Formula for calculating Requirement for individual vaccine
                    Double individualVaccineRequirement = 0.0;
                    try
                    {
                        individualVaccineRequirement = vsReqLiveBirthData * vsReqVaccineVolumePerChildData * ((vsReqSupplyIntervalData + vsReqReserveStockData) / 52 );
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception while calculating individualVaccineRequirement" );
                        individualVaccineRequirement = 0.0;
                    }
                    
                    vaccineRequirement += individualVaccineRequirement;
                }
                
                vaccineRequirement = Math.round( vaccineRequirement * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Required", vaccineRequirement + "" );

                Double diffVaccineReq = vaccineActualValue - vaccineRequirement;
                diffVaccineReq = Math.round( diffVaccineReq * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                numberOfData.put( "Difference", "" + diffVaccineReq );

                Double diffPercentage = (diffVaccineReq / vaccineActualValue) * 100;
                if ( diffPercentage < -30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "1" );
                }
                else if ( diffPercentage >= -30.0 && diffPercentage < -10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "1" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= -10.0 && diffPercentage < 10.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "1" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else if ( diffPercentage >= 10.0 && diffPercentage < 30.0 )
                {
                    numberOfData.put( ">30%-1", "0" );
                    numberOfData.put( "10-30%-1", "1" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }
                else
                {
                    numberOfData.put( ">30%-1", "1" );
                    numberOfData.put( "10-30%-1", "0" );
                    numberOfData.put( "+/- 10%-1", "0" );
                    numberOfData.put( "10-30%-2", "0" );
                    numberOfData.put( ">30%-2", "0" );
                }

                tableData.add( numberOfData );
            }

            frb.setPrintColumnNames( true );
            frb.setHeaderHeight( 100 );
            frb.setColumnsPerPage( 1, 15 ).setUseFullPageWidth( true );
            frb.setPrintBackgroundOnOddRows(true);
            frb.setTemplateFile( path + "VACCINE_STORAGE_CAPACITY.jrxml" );
            
            JRDataSource ds = new JRMapCollectionDataSource( tableData );
            DynamicReport dynamicReport = frb.build();
            dynamicReport.getOptions().getDefaultHeaderStyle().setBorder( Border.THIN() );
            //Color.LIGHT_GRAY
            dynamicReport.getOptions().getOddRowBackgroundStyle().setBackgroundColor( Color.decode( "#F5F5F6" ) );
            dynamicReport.getOptions().getDefaultHeaderStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setBorder( Border.THIN() );
            dynamicReport.getOptions().getDefaultDetailStyle().setHorizontalAlign( HorizontalAlign.CENTER );
            dynamicReport.getOptions().getDefaultDetailStyle().setVerticalAlign( VerticalAlign.MIDDLE );
            jr = DynamicJasperHelper.generateJasperReport( dynamicReport, new ClassicLayoutManager(), hash );
            jasperPrint = JasperFillManager.fillReport( jr, hash, ds );

        }

        ServletOutputStream ouputStream = response.getOutputStream();
        JRExporter exporter = null;
        if ( "pdf".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/pdf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.pdf\"" );
            exporter = new JRPdfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "rtf".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/rtf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.rtf\"" );

            exporter = new JRRtfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "html".equalsIgnoreCase( type ) )
        {
            exporter = new JRHtmlExporter();
            exporter.setParameter( JRHtmlExporterParameter.OUTPUT_STREAM, false );
            exporter.setParameter( JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean( false ) );
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "xls".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/xls" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.xls\"" );

            exporter = new JRXlsExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        else if ( "csv".equalsIgnoreCase( type ) )
        {
            response.setContentType( "application/csv" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"file.csv\"" );

            exporter = new JRCsvExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
        }
        con.close();       
        try
        {
            exporter.exportReport();
        }
        catch ( JRException e )
        {
            throw new ServletException( e );
        }
        
        return SUCCESS;
    }

    private String getOrgunitBranch( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " -> " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }

}
