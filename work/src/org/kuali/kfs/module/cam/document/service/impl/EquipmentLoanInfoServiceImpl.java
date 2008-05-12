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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.AssetHeaderService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;

/**
 * Implements EquipmentLoanInfoService
 */
public class EquipmentLoanInfoServiceImpl implements EquipmentLoanInfoService {

    private AssetHeaderService assetHeaderService;


    public AssetHeaderService getAssetHeaderService() {
        return assetHeaderService;
    }

    public void setAssetHeaderService(AssetHeaderService assetHeaderService) {
        this.assetHeaderService = assetHeaderService;
    }


    public void setEquipmentLoanInfo(Asset asset) {
        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            List<AssetHeader> assetHeaders = asset.getAssetHeaders();
            List<EquipmentLoanOrReturnDocument> sortableList = new ArrayList<EquipmentLoanOrReturnDocument>();

            for (AssetHeader assetHeader : assetHeaders) {
                EquipmentLoanOrReturnDocument equipmentLoanOrReturn = assetHeader.getEquipmentLoanOrReturnDocument();
                if (equipmentLoanOrReturn != null && assetHeaderService.isDocumentApproved(assetHeader)) {
                    sortableList.add(equipmentLoanOrReturn);
                }
            }
            Comparator<EquipmentLoanOrReturnDocument> comparator = new Comparator<EquipmentLoanOrReturnDocument>() {
                public int compare(EquipmentLoanOrReturnDocument o1, EquipmentLoanOrReturnDocument o2) {
                    // sort descending based on loan date
                    return o2.getLoanDate().compareTo(o1.getLoanDate());
                }
            };
            Collections.sort(sortableList, comparator);

            if (!sortableList.isEmpty()) {
                asset.setLoanOrReturnInfo(sortableList.get(0));
            }
        }
    }


}
