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
package org.kuali.module.cams.service;

import org.kuali.core.document.MaintenanceLock;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.bo.Asset;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetService {
    boolean isAssetMovable(Asset asset);

    boolean isCapitalAsset(Asset asset);

    boolean isAssetRetired(Asset asset);

    boolean isInServiceDateChanged(Asset oldAsset, Asset newAsset);

    /**
     * The Asset Type Code is allowed to be changed only: (1)If the tag number has not been assigned or (2)The asset is tagged, and
     * the asset created in the current fiscal year
     * 
     * @return
     */
    boolean isAssetTaggedInPriorFiscalYear(Asset asset);

    /**
     * The Tag Number check excludes value of "N" and retired assets.
     * 
     * @return
     */
    boolean isTagNumberCheckExclude(Asset asset);

    /**
     * Test if any of the off campus location field is entered.
     * 
     * @param asset
     * @return
     */
    boolean isOffCampusLocationEntered(Asset asset);

    /**
     * Test if financialObjectSubTypeCode is changed.
     * 
     * @param oldAsset
     * @param newAsset
     * @return
     */
    boolean isFinancialObjectSubTypeCodeChanged(Asset oldAsset, Asset newAsset);

    /**
     * Test if assetTypeCode is changed.
     * 
     * @param oldAsset
     * @param newAsset
     * @return
     */
    boolean isAssetTypeCodeChanged(Asset oldAsset, Asset newAsset);
    
    /** 
     * Test if Depreciable Life Limit is "0"
     * This method...
     * @param asset
     * @return
     */
    boolean isAssetDepreciableLifeLimitZero(Asset asset);
    
    /**
     * 
     * Test two capitalAssetNumber equal.
     * @param capitalAssetNumber1
     * @param capitalAssetNumber2
     * @return
     */
    boolean isCapitalAssetNumberDuplicate(Long capitalAssetNumber1, Long capitalAssetNumber2);
    
    /**
     * Creates the particular locking representation for this transactional asset document.
     * 
     * @param documentNumber
     * @param capitalAssetNumber
     * @return locking representation
     */
    public MaintenanceLock generateAssetLock(String documentNumber, Long capitalAssetNumber);

    /**
     * Helper method that will check if an asset is locked. If it is we fail error check and
     * call another helper message to add appropriate error message to global ErrorMap.
     * 
     * @param documentNumber
     * @param capitalAssetNumber
     * @return boolean True if no lock, false otherwise.
     */
    public boolean isAssetLocked(String documentNumber, Long capitalAssetNumber);
    
    /**
     * This method calls the service codes to calculate the summary fields for each asset
     * 
     * @param asset
     */
    void setAssetNonPersistentFields(Asset asset);
}