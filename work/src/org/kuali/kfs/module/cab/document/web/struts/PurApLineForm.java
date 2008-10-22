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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class PurApLineForm extends KualiForm {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    private Integer purchaseOrderIdentifier;
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;

    private List<PurchasingAccountsPayableDocument> purApDocs;
    private int actionPurApDocIndex;
    private int actionItemAssetIndex;

    private KualiDecimal mergeQty;
    private String mergeDesc;

    private boolean activeItemExist;

    private Integer requisitionIdentifier;
    
    private String PurchaseOrderInquiryUrl;

    public PurApLineForm() {
        this.purApDocs = new TypedArrayList(PurchasingAccountsPayableDocument.class);
        this.activeItemExist = false;
    }


    /**
     * Gets the activeItemExist attribute.
     * 
     * @return Returns the activeItemExist.
     */
    public boolean isActiveItemExist() {
        return activeItemExist;
    }


    /**
     * Sets the activeItemExist attribute value.
     * 
     * @param activeItemExist The activeItemExist to set.
     */
    public void setActiveItemExist(boolean activeItemExist) {
        this.activeItemExist = activeItemExist;
    }


    /**
     * Gets the requisitionIdentifier attribute.
     * 
     * @return Returns the requisitionIdentifier.
     */
    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }


    /**
     * Sets the requisitionIdentifier attribute value.
     * 
     * @param requisitionIdentifier The requisitionIdentifier to set.
     */
    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    /**
     * Gets the mergeQty attribute.
     * 
     * @return Returns the mergeQty.
     */
    public KualiDecimal getMergeQty() {
        return mergeQty;
    }


    /**
     * Sets the mergeQty attribute value.
     * 
     * @param mergeQty The mergeQty to set.
     */
    public void setMergeQty(KualiDecimal mergeQty) {
        this.mergeQty = mergeQty;
    }


    /**
     * Gets the mergeDesc attribute.
     * 
     * @return Returns the mergeDesc.
     */
    public String getMergeDesc() {
        return mergeDesc;
    }


    /**
     * Sets the mergeDesc attribute value.
     * 
     * @param mergeDesc The mergeDesc to set.
     */
    public void setMergeDesc(String mergeDesc) {
        this.mergeDesc = mergeDesc;
    }


    /**
     * Gets the purApContactEmailAddress attribute.
     * 
     * @return Returns the purApContactEmailAddress.
     */
    public String getPurApContactEmailAddress() {
        return purApContactEmailAddress;
    }


    /**
     * Sets the purApContactEmailAddress attribute value.
     * 
     * @param purApContactEmailAddress The purApContactEmailAddress to set.
     */
    public void setPurApContactEmailAddress(String purApContactEmailAddress) {
        this.purApContactEmailAddress = purApContactEmailAddress;
    }


    /**
     * Gets the purApContactPhoneNumber attribute.
     * 
     * @return Returns the purApContactPhoneNumber.
     */
    public String getPurApContactPhoneNumber() {
        return purApContactPhoneNumber;
    }


    /**
     * Sets the purApContactPhoneNumber attribute value.
     * 
     * @param purApContactPhoneNumber The purApContactPhoneNumber to set.
     */
    public void setPurApContactPhoneNumber(String purApContactPhoneNumber) {
        this.purApContactPhoneNumber = purApContactPhoneNumber;
    }


    /**
     * Gets the actionPurApDocIndex attribute.
     * 
     * @return Returns the actionPurApDocIndex.
     */
    public int getActionPurApDocIndex() {
        return actionPurApDocIndex;
    }


    /**
     * Sets the actionPurApDocIndex attribute value.
     * 
     * @param actionPurApDocIndex The actionPurApDocIndex to set.
     */
    public void setActionPurApDocIndex(int actionPurApDocIndex) {
        this.actionPurApDocIndex = actionPurApDocIndex;
    }


    /**
     * Gets the actionItemAssetIndex attribute.
     * 
     * @return Returns the actionItemAssetIndex.
     */
    public int getActionItemAssetIndex() {
        return actionItemAssetIndex;
    }


    /**
     * Sets the actionItemAssetIndex attribute value.
     * 
     * @param actionItemAssetIndex The actionItemAssetIndex to set.
     */
    public void setActionItemAssetIndex(int actionItemAssetIndex) {
        this.actionItemAssetIndex = actionItemAssetIndex;
    }


    /**
     * Gets the purchaseOrderIdentifier attribute. 
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }


    /**
     * Sets the purchaseOrderIdentifier attribute value.
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }


    /**
     * Gets the purApDocs attribute.
     * 
     * @return Returns the purApDocs.
     */
    public List<PurchasingAccountsPayableDocument> getPurApDocs() {
        return purApDocs;
    }


    /**
     * Sets the purApDocs attribute value.
     * 
     * @param purApDocs The purApDocs to set.
     */
    public void setPurApDocs(List<PurchasingAccountsPayableDocument> purApDocs) {
        this.purApDocs = purApDocs;
    }

    public String getPurchaseOrderInquiryUrl() {
        return PurchaseOrderInquiryUrl;
    }
    
    /**
     * Sets the purchaseOrderInquiryUrl attribute value.
     * @param purchaseOrderInquiryUrl The purchaseOrderInquiryUrl to set.
     */
    public void setPurchaseOrderInquiryUrl(String purchaseOrderInquiryUrl) {
        PurchaseOrderInquiryUrl = purchaseOrderInquiryUrl;
    }


    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            // populate collection index
            String purApDocIndex = StringUtils.substringBetween(parameterName, CabConstants.DOT_DOC, ".");
            if (StringUtils.isNotBlank(purApDocIndex)) {
                this.setActionPurApDocIndex(Integer.parseInt(purApDocIndex));
            }
            String itemAssetIndex = StringUtils.substringBetween(parameterName, CabConstants.DOT_LINE, ".");
            if (StringUtils.isNotBlank(itemAssetIndex)) {
                this.setActionItemAssetIndex(Integer.parseInt(itemAssetIndex));
            }
        }
        
        if (this.purchaseOrderIdentifier != null) {
            this.setPurchaseOrderInquiryUrl(SpringContext.getBean(PurchasingAccountsPayableModuleService.class).getPurchaseOrderInquiryUrl(purchaseOrderIdentifier));
        }
    }
}
