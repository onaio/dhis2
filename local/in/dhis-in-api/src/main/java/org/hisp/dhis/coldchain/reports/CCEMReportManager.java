package org.hisp.dhis.coldchain.reports;

import java.util.List;
import java.util.Map;

public interface CCEMReportManager
{
    String getOrgunitIdsByComma( List<Integer> selOrgUnitList, List<Integer> orgunitGroupList );
    
    Map<String,String> getCCEMSettings();
    
    List<CCEMReportDesign> getCCEMReportDesign( String designXMLFile );
    
    CCEMReport getCCEMReportByReportId( String selReportId );
    
    Map<String, Integer> getModelTypeAttributeValue( String orgUnitIdsByComma, Integer equipmentTypeId, Integer modelTypeAttributeId );
    
    Map<String, Integer> getModelTypeAttributeValueByAge( String orgUnitIdsByComma, Integer equipmentTypeId, Integer modelTypeAttributeId, Integer yearInvTypeAttId, Integer ageStart, Integer ageEnd );
    
    List<String> getDistinctDataElementValue( Integer dataelementID, Integer optComboId, Integer periodId );
    
    List<Integer> getOrgunitIds( List<Integer> selOrgUnitList, Integer orgUnitGroupId );
    
    Map<String, Integer> getDataValueCountforDataElements( String dataElementIdsByComma, String optComboIdsByComma, Integer periodId, String orgUnitIdsBycomma );
    
    Integer getPeriodId( String startDate, String periodType );
    
    Map<String, Integer> getFacilityWiseEquipmentRoutineData( String orgUnitIdsByComma, String periodIdsByComma, String dataElementIdsByComma, String optComboIdsByComma );
    
    Map<Integer, Double> getModelDataSumByEquipmentData( String orgUnitIdsByComma, Integer equipmentTypeId, Integer modelTypeAttributeId, Integer equipmentTypeAttributeId, String equipmentValue );
    
    Map<Integer, Double> getSumOfEquipmentDatabyEquipmentType( String orgUnitIdsByComma, Integer equipmentTypeId, Integer equipmentTypeAttributeId, Double factor );
    
    Map<String, String> getOrgUnitGroupAttribDataForRequirement( String orgUnitGroupIdsByComma, String orgUnitGroupAttribIds );
    
    Map<String, String> getDataElementDataForModelOptionsForRequirement( String orgUnitIdsByComma, String modelOption_DataelementIds, Integer periodId );
    
    Map<String, String> getModelDataForRequirement( Integer vsReqModelTypeId, Integer vsReqStorageTempId, String vsReqStorageTemp, Integer vsReqNationalSupplyId, String vsReqNationalSupply, String vsReqModelAttribIds );
    
    List<Integer> getModelIdsForRequirement( Integer vsReqModelTypeId, Integer vsReqStorageTempId, String vsReqStorageTemp, Integer vsReqNationalSupplyId, String vsReqNationalSupply );
    
    Map<Integer, String> getOrgunitAndOrgUnitGroupMap( String orgUnitGroupIdsByComma, String orgUnitIdsByComma );
    
    String getMinMaxAvgValues(String orgunitid, String periodid , Integer dataElementid, Integer optionCombo);
    
    Integer getGrandTotalValue(String orgunitid, String periodid , Integer dataElementid);
    
    Map<String,Integer> getModelAttributevalueId( String orgUnitIdsByComma, Integer equipmentTypeId, Integer modelTypeAttributeId );
    
    List<String> getModelName(Integer equipmentTypeId, Integer modelTypeAttributeId , String orgUnitIds);
    
    String getEquipmentValue(String modelTypeAttributeValue,Integer modelid, String euipmentValue, String orgUnitIdsByComma, Integer equipmentTypeId);
    
    Map<String,Integer> getModelNameAndCount(Integer modelTypeAttributeId , Integer equipmentTypeId, String equipmentValue, String orgUnitIdsByComma);
    
    Integer getDataValue( String dataelementId, String dataValue, String orgUnitByIds ,String periodId);
    
    Integer getCountByOrgUnitGroup( Integer rootOrgUnitId, Integer orgUnitGroupId );
    
    Map<String,Integer> getDataValueAndCount(String dataelementId, String orgUnitByIds,String periodId);
    
    List<String> getEquipmentValueAndData( Integer modelTypeAttributeId, String orgUnitIdsByComma,
        Integer equipmentTypeId );
    
    List<String> equipmentModelies( String orgUnitIdsByComma, Integer equipmentTypeId );
    
    Map<String, String> equipmentModelyValues( String orgUnitIdsByComma, Integer equipmentTypeId,
        Integer equipmentTypeAttributeId );
    
    Map<String, String> equipmentOrgUnit( String orgUnitIdsByComma, Integer equipmentTypeId );
    
    Map<String, String> getEquipmentNameWithOrgUnit( Integer equipmentTypeId, Integer modelTypeAttributeId,
        String orgUnitIds );
    
    Integer getTotalFacilitiesWithOrgUnit( String orgUnitIdsById );
    
    Map<String, String> getTotalColdRoomValue( Integer equipmenttypeid, String orgUnitIdByComma,
        String equipmentTypeAttributeId, String equipmentValue );
    
    Map<String, Integer> getModelNameAndCountForColdBox( Integer modelTypeAttributeId,
        Integer equipmentTypeId, String workingStatus, String orgUnitIdsByComma );
    
    String getModelNameAndCountForQuantityOfColdbox( Integer equipmentTypeId, String modelValue,
        String orgUnitIdsByComma );
    
    Map<String, Double> getSumOfEquipmentAndModelValue( Integer equipmentTypeId,
        Integer equipmentTypeAttributeId, Integer modelTypeAttributeId, String orgUnitIdsByComma );
    
    Map<String, Map<String,Integer>> getModelName_EquipmentUtilization_Count( Integer equipmentTypeId, Integer modelTypeAttributeId, Integer equipmentTypeAttributeId, String orgUnitIdsByComma );
    
    Map<String, Integer> getModelName_Count( Integer equipmentTypeId, Integer modelTypeAttributeId, String orgUnitIds );
	
	List<String> getDataValueFacility( Integer dataElementId, String dataValue, String orgUnitIdByComma, String periodIds );
	
    Integer getModelAttributeValueCount( Integer equipmentTypeId, Integer modelTypeAttributeId, String catogDataValue, String orgUnitIds );
    
    Map<String, Integer> getEquipmentValue_Count( Integer equipmentTypeId, Integer equipmentTypeAttributeId, String orgUnitIds );
    
    Integer getEquipmentCount( Integer equipmentTypeId, String orgUnitIds );
    
    Map<String, Map<String,Integer>> getEquipmentType_ElectricityAvailability_Count( Integer equipmentTypeId, Integer modelTypeAttributeId, Integer dataElementId, String periodId, String orgUnitIdsByComma );
}
