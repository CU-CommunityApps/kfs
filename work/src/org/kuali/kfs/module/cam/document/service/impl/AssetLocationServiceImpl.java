/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cams.service.impl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.bo.AssetType;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.AssetService;

public class AssetLocationServiceImpl implements AssetLocationService {

    private AssetService assetService;

    /**
     * @see org.kuali.module.cams.service.AssetLocationService#setOffCampusLocation(org.kuali.module.cams.bo.Asset)
     */
    public void setOffCampusLocation(Asset asset) {
        List<AssetLocation> assetLocations = asset.getAssetLocations();
        AssetLocation assetLocation = asset.getOffCampusLocation();

        for (AssetLocation location : assetLocations) {
            if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equalsIgnoreCase(location.getAssetLocationTypeCode())) {
                asset.setOffCampusLocation(location);
                assetLocation = location;
                break;
            }
        }

        if (assetLocation == null) {
            assetLocation = new AssetLocation();
            assetLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
            assetLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
        }
        asset.setOffCampusLocation(assetLocation);
    }

    /**
     * @see org.kuali.module.cams.service.AssetLocationService#updateOffCampusLocation(org.kuali.module.cams.bo.Asset)
     */
    public void updateOffCampusLocation(Asset asset) {
        List<AssetLocation> assetLocations = asset.getAssetLocations();
        AssetLocation offCampusLocation = asset.getOffCampusLocation();
        offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
        assetLocations.add(offCampusLocation);
    }

    /**
     * @see org.kuali.module.cams.service.AssetLocationService#validateLocation(java.lang.Object, org.kuali.module.cams.bo.Asset,
     *      java.util.Map)
     */
    public boolean validateLocation(Object currObject, Asset asset, Map<LocationField, String> fieldMap) {
        Map<LocationField, PropertyDescriptor> propertyDescriptorMap = new HashMap<LocationField, PropertyDescriptor>();
        prepareFieldDescriptorMap(currObject, fieldMap, propertyDescriptorMap);
        String campusCode = readPropertyValue(currObject, propertyDescriptorMap, LocationField.CAMPUS_CODE);
        String buildingCode = readPropertyValue(currObject, propertyDescriptorMap, LocationField.BUILDING_CODE);
        String roomNumber = readPropertyValue(currObject, propertyDescriptorMap, LocationField.ROOM_NUMBER);
        String subRoomNumber = readPropertyValue(currObject, propertyDescriptorMap, LocationField.SUB_ROOM_NUMBER);
        String streetAddress = readPropertyValue(currObject, propertyDescriptorMap, LocationField.STREET_ADDRESS);
        String cityName = readPropertyValue(currObject, propertyDescriptorMap, LocationField.CITY_NAME);
        String stateCode = readPropertyValue(currObject, propertyDescriptorMap, LocationField.STATE_CODE);
        String zipCode = readPropertyValue(currObject, propertyDescriptorMap, LocationField.ZIP_CODE);
        boolean onCampus = StringUtils.isNotBlank(buildingCode) || StringUtils.isNotBlank(roomNumber) || StringUtils.isNotBlank(subRoomNumber);
        boolean offCampus = StringUtils.isNotBlank(streetAddress) || StringUtils.isNotBlank(cityName) || StringUtils.isNotBlank(stateCode) || StringUtils.isNotBlank(zipCode);

        boolean valid = true;
        if (StringUtils.isBlank(campusCode)) {
            // campus code is always mandatory
            putError(fieldMap, LocationField.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_CAMPUS_CODE_REQUIRED);
            valid &= false;
        }
        if (getAssetService().isCapitalAsset(asset)) {
            valid &= validateCapitalAssetLocation(asset, fieldMap, campusCode, buildingCode, roomNumber, subRoomNumber, streetAddress, cityName, stateCode, zipCode, onCampus, offCampus);
        }
        else {
            valid &= validateNonCapitalAssetLocation(fieldMap, streetAddress, cityName, stateCode, zipCode, onCampus, offCampus);
        }
        return valid;
    }


    private boolean validateCapitalAssetLocation(Asset asset, Map<LocationField, String> fieldMap, String campusCode, String buildingCode, String roomNumber, String subRoomNumber, String streetAddress, String cityName, String stateCode, String zipCode, boolean onCampus, boolean offCampus) {
        AssetType assetType = asset.getCapitalAssetType();
        boolean valid = true;
        if (onCampus && offCampus) {
            putError(fieldMap, LocationField.LOCATION_TAB_KEY, CamsKeyConstants.AssetLocation.ERROR_CHOOSE_LOCATION_INFO);
            valid &= false;
        }
        else if (assetType.isRequiredBuildingIndicator() && offCampus) {
            // off campus information not allowed
            putError(fieldMap, LocationField.LOCATION_TAB_KEY, CamsKeyConstants.AssetLocation.ERROR_LOCATION_OFF_CAMPUS_NOT_PERMITTED, assetType.getCapitalAssetTypeDescription());
            valid &= false;
        }
        else if (!assetType.isMovingIndicator() && !assetType.isRequiredBuildingIndicator() && onCampus) {
            // land information cannot have on-campus
            putError(fieldMap, LocationField.LOCATION_TAB_KEY, CamsKeyConstants.AssetLocation.ERROR_LOCATION_ON_CAMPUS_NOT_PERMITTED, assetType.getCapitalAssetTypeDescription());
            valid &= false;
        }
        else if (onCampus) {
            valid = validateOnCampusLocation(fieldMap, assetType, campusCode, buildingCode, roomNumber, subRoomNumber, asset);
        }
        else if (offCampus) {
            valid = validateOffCampusLocation(fieldMap, streetAddress, cityName, stateCode, zipCode);
        }
        else if (assetType.isMovingIndicator() || assetType.isRequiredBuildingIndicator()) {
            putError(fieldMap, LocationField.LOCATION_TAB_KEY, CamsKeyConstants.AssetLocation.ERROR_LOCATION_INFO_REQUIRED);
            valid &= false;
        }
        return valid;
    }

    private boolean validateNonCapitalAssetLocation(Map<LocationField, String> fieldMap, String streetAddress, String cityName, String stateCode, String zipCode, boolean onCampus, boolean offCampus) {
        boolean valid = true;
        if (onCampus && offCampus) {
            putError(fieldMap, LocationField.LOCATION_TAB_KEY, CamsKeyConstants.AssetLocation.ERROR_CHOOSE_LOCATION_INFO);
            valid &= false;
        }
        else if (offCampus) {
            valid = validateOffCampusLocation(fieldMap, streetAddress, cityName, stateCode, zipCode);
        }
        return valid;
    }


    /**
     * Convenience method to append the path prefix
     */
    public TypedArrayList putError(Map<LocationField, String> fieldMap, LocationField field, String errorKey, String... errorParameters) {
        return GlobalVariables.getErrorMap().putError(fieldMap.get(field), errorKey, errorParameters);
    }

    private boolean validateOnCampusLocation(Map<LocationField, String> fieldMap, AssetType assetType, String campusCode, String buildingCode, String buildingRoomNumber, String subRoomNumber, Asset asset) {
        boolean valid = true;
        if (assetType.isMovingIndicator()) {
            if (StringUtils.isBlank(buildingCode)) {
                putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_CODE_REQUIRED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
            if (StringUtils.isBlank(buildingRoomNumber)) {
                putError(fieldMap, LocationField.ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_ROOM_NUMBER_REQUIRED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
        }
        if (assetType.isRequiredBuildingIndicator()) {
            if (StringUtils.isBlank(buildingCode)) {
                putError(fieldMap, LocationField.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_CODE_REQUIRED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
            if (StringUtils.isNotBlank(buildingRoomNumber)) {
                putError(fieldMap, LocationField.ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_BUILDING_ROOM_NUMBER_NOT_PERMITTED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
            if (StringUtils.isNotBlank(subRoomNumber)) {
                putError(fieldMap, LocationField.SUB_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_ONCAMPUS_SUB_ROOM_NUMBER_NOT_PERMITTED, assetType.getCapitalAssetTypeDescription());
                valid &= false;
            }
        }
        return valid;
    }

    private boolean validateOffCampusLocation(Map<LocationField, String> fieldMap, String streetAddress, String cityName, String stateCode, String zipCode) {
        boolean valid = true;
        // when off campus, make sure required fields are populated
        // TODO - Check if contact name is a mandatory information

        if (StringUtils.isBlank(streetAddress)) {
            putError(fieldMap, LocationField.STREET_ADDRESS, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_ADDRESS_REQUIRED);
            valid &= false;
        }
        if (StringUtils.isBlank(cityName)) {
            putError(fieldMap, LocationField.CITY_NAME, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_CITY_REQUIRED);
            valid &= false;
        }
        if (StringUtils.isBlank(stateCode)) {
            putError(fieldMap, LocationField.STATE_CODE, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_STATE_REQUIRED);
            valid &= false;
        }
        if (StringUtils.isBlank(zipCode)) {
            putError(fieldMap, LocationField.ZIP_CODE, CamsKeyConstants.AssetLocation.ERROR_OFFCAMPUS_ZIP_REQUIRED);
            valid &= false;
        }
        return valid;
    }

    private String readPropertyValue(Object currObject, Map<LocationField, PropertyDescriptor> propertyDescriptorMap, LocationField field) {
        String stringValue = null;
        try {
            stringValue = (String) propertyDescriptorMap.get(field).getReadMethod().invoke(currObject);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return stringValue;
    }

    private void prepareFieldDescriptorMap(Object currObject, Map<LocationField, String> fieldMap, Map<LocationField, PropertyDescriptor> propertyDescriptorMap) {
        try {
            for (LocationField property : fieldMap.keySet()) {
                PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(currObject, fieldMap.get(property));
                if (propertyDescriptor != null) {
                    propertyDescriptorMap.put(property, propertyDescriptor);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }


}
