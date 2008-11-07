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
package org.kuali.kfs.module.cab.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class overrides the base getActionUrls method
 */
public class GeneralLedgerEntryLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerEntryLookupableHelperServiceImpl.class);
    private BusinessObjectService businessObjectService;

    /*******************************************************************************************************************************
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        GeneralLedgerEntry entry = (GeneralLedgerEntry) bo;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        if (entry.isActive()) {
            anchorHtmlDataList.add(new AnchorHtmlData("../cabGlLine.do?methodToCall=process&" + CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER + "=" + entry.getGeneralLedgerAccountIdentifier(), "process", "process"));
        }
        else {
            List<GeneralLedgerEntryAsset> generalLedgerEntryAssets = entry.getGeneralLedgerEntryAssets();
            if (!generalLedgerEntryAssets.isEmpty()) {
                anchorHtmlDataList.add(new AnchorHtmlData("../cabGlLine.do?methodToCall=viewDoc&" + "documentNumber" + "=" + generalLedgerEntryAssets.get(0).getCapitalAssetManagementDocumentNumber(), "viewDoc", generalLedgerEntryAssets.get(0).getCapitalAssetManagementDocumentNumber()));
            }
            else {
                anchorHtmlDataList.add(new AnchorHtmlData("", "n/a"));
            }
        }
        return anchorHtmlDataList;
    }

    /**
     * This method will remove all PO related transactions from display on GL results
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty()) {
            return searchResults;
        }
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(GeneralLedgerEntry.class);
        Long matchingResultsCount = null;
        List<GeneralLedgerEntry> newList = new ArrayList<GeneralLedgerEntry>();
        for (BusinessObject businessObject : searchResults) {
            GeneralLedgerEntry entry = (GeneralLedgerEntry) businessObject;
            if (!entry.getFinancialDocumentTypeCode().equals(CabConstants.PREQ)) {
                if (!entry.getFinancialDocumentTypeCode().equals(CabConstants.CM)) {
                    newList.add(entry);
                }
                else if (entry.getFinancialDocumentTypeCode().equals(CabConstants.CM)) {
                    Map<String, String> cmKeys = new HashMap<String, String>();
                    cmKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, entry.getDocumentNumber());
                    // check if CAB PO document exsists, if not include
                    Collection<PurchasingAccountsPayableDocument> matchingCreditMemos = businessObjectService.findMatching(PurchasingAccountsPayableDocument.class, cmKeys);
                    if (matchingCreditMemos == null || matchingCreditMemos.isEmpty()) {
                        newList.add(entry);
                    }
                }
            }
        }
        matchingResultsCount = Long.valueOf(newList.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(newList, matchingResultsCount);
    }


    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
