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
package org.kuali.kfs.integration.purap;

import java.util.List;




public interface CapitalAssetSystem {

    public String getCapitalAssetSystemDescription();

    public void setCapitalAssetSystemDescription(String capitalAssetSystemDescription);

    public boolean isCapitalAssetNotReceivedCurrentFiscalYearIndicator();

    public void setCapitalAssetNotReceivedCurrentFiscalYearIndicator(boolean capitalAssetNotReceivedCurrentFiscalYearIndicator);

    public String getCapitalAssetTypeCode();

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode);

    public String getCapitalAssetManufacturerName();

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName);

    public String getCapitalAssetModelDescription();

    public void setCapitalAssetModelDescription(String capitalAssetModelDescription);

    public List<ItemCapitalAsset> getPurchasingItemCapitalAssets();

    public void setPurchasingItemCapitalAssets(List<ItemCapitalAsset> purchasingItemCapitalAssets);

    public List<CapitalAssetLocation> getPurchasingCapitalAssetLocations();

    public void setPurchasingCapitalAssetLocations(List<CapitalAssetLocation> purchasingCapitalAssetLocations);

    public Integer getCapitalAssetSystemIdentifier();

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier);

    public String getCapitalAssetNoteText();

    public void setCapitalAssetNoteText(String capitalAssetNoteText);

    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine();

    public void setNewPurchasingCapitalAssetLocationLine(CapitalAssetLocation newCapitalAssetLocationLine);

    public CapitalAssetLocation getNewPurchasingCapitalAssetLocationLine();

    public CapitalAssetLocation getAndResetNewPurchasingCapitalAssetLocationLine();

}