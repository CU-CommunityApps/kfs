/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemState;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemType;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.FundingSource;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderTransmissionMethod;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.businessobject.ReceivingAddress;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.businessobject.RequisitionSource;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.ReceivingAddressService;
import org.kuali.kfs.module.purap.util.ItemParser;
import org.kuali.kfs.module.purap.util.ItemParserBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Base class for Purchasing Documents.
 */
public abstract class PurchasingDocumentBase extends PurchasingAccountsPayableDocumentBase implements PurchasingDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingDocumentBase.class);

    // SHARED FIELDS BETWEEN REQUISITION AND PURCHASE ORDER
    private String fundingSourceCode;
    private String requisitionSourceCode;
    private String purchaseOrderTransmissionMethodCode;
    private String purchaseOrderCostSourceCode;
    private String deliveryRequiredDateReasonCode;
    private String recurringPaymentTypeCode;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String deliveryCampusCode;
    private KualiDecimal purchaseOrderTotalLimit;
    private Boolean vendorRestrictedIndicator;
    private String vendorPhoneNumber;
    private String vendorFaxNumber;
    private Integer vendorContractGeneratedIdentifier;
    private String vendorNoteText;
    private String requestorPersonName;
    private String requestorPersonEmailAddress;
    private String requestorPersonPhoneNumber;
    private String nonInstitutionFundOrgChartOfAccountsCode;
    private String nonInstitutionFundOrganizationCode;
    private String nonInstitutionFundChartOfAccountsCode;
    private String nonInstitutionFundAccountNumber;
    private String deliveryBuildingCode;
    private String deliveryBuildingName;
    private String deliveryBuildingRoomNumber;
    private String deliveryBuildingLine1Address;
    private String deliveryBuildingLine2Address;
    private String deliveryCityName;
    private String deliveryStateCode;
    private String deliveryPostalCode;
    private String deliveryCountryCode;
    private String deliveryToName;
    private String deliveryToEmailAddress;
    private String deliveryToPhoneNumber;
    private Date deliveryRequiredDate;
    private String deliveryInstructionText;
    private Date purchaseOrderBeginDate;
    private Date purchaseOrderEndDate;
    private String institutionContactName;
    private String institutionContactPhoneNumber;
    private String institutionContactEmailAddress;
    private String billingName;
    private String billingLine1Address;
    private String billingLine2Address;
    private String billingCityName;
    private String billingStateCode;
    private String billingPostalCode;
    private String billingCountryCode;
    private String billingPhoneNumber;
    private String receivingName;
    private String receivingLine1Address;
    private String receivingLine2Address;
    private String receivingCityName;
    private String receivingStateCode;
    private String receivingPostalCode;
    private String receivingCountryCode;
    private boolean addressToVendorIndicator; // if true, use receiving address
    private String externalOrganizationB2bSupplierIdentifier;
    private boolean purchaseOrderAutomaticIndicator;
    private String vendorPaymentTermsCode;
    private String vendorShippingTitleCode;
    private String vendorShippingPaymentTermsCode;
    private String capitalAssetSystemTypeCode;
    private String capitalAssetSystemStateCode;
    

    // NOT PERSISTED IN DB
    private String vendorContractName;
    private String supplierDiversityLabel;
    private String vendorContactsLabel;
    private boolean deliveryBuildingOther;

    // REFERENCE OBJECTS
    private FundingSource fundingSource;
    private RequisitionSource requisitionSource;
    private PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod;
    private PurchaseOrderCostSource purchaseOrderCostSource;
    private DeliveryRequiredDateReason deliveryRequiredDateReason;
    private RecurringPaymentType recurringPaymentType;
    private Org organization;
    private Chart chartOfAccounts;
    private Campus deliveryCampus;
    private Chart nonInstitutionFundOrgChartOfAccounts;
    private Org nonInstitutionFundOrganization;
    private Account nonInstitutionFundAccount;
    private Chart nonInstitutionFundChartOfAccounts;
    private VendorContract vendorContract;
    private CapitalAssetSystemType capitalAssetSystemType;
    private CapitalAssetSystemState capitalAssetSystemState;
    private List<CapitalAssetSystem> purchasingCapitalAssetSystems;
    private List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems;
    
    private boolean receivingDocumentRequiredIndicator;
    private boolean paymentRequestPositiveApprovalIndicator;

    private List<CommodityCode> commodityCodesForRouting;
    
    /**
     * Default Constructor.
     */
    public PurchasingDocumentBase() {
        super();
        
        purchasingCapitalAssetItems = new TypedArrayList(getPurchasingCapitalAssetItemClass());
        purchasingCapitalAssetSystems = new TypedArrayList(getPurchasingCapitalAssetSystemClass());
    }

    public abstract PurchasingDocumentSpecificService getDocumentSpecificService();

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#templateVendorDetail(org.kuali.kfs.vnd.businessobject.VendorDetail)
     */
    public void templateVendorDetail(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }

        this.setVendorDetail(vendorDetail);
        this.setVendorName(vendorDetail.getVendorName());
        this.setVendorShippingTitleCode(vendorDetail.getVendorShippingTitleCode());
        this.setVendorPaymentTermsCode(vendorDetail.getVendorPaymentTermsCode());
        this.setVendorShippingPaymentTermsCode(vendorDetail.getVendorShippingPaymentTermsCode());
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#templateVendorContract(org.kuali.kfs.vnd.businessobject.VendorContract)
     */
    public void templateVendorContract(VendorContract vendorContract, VendorDetail vendorDetail) {
        if (vendorContract == null) {
            return;
        }
        this.setVendorContract(vendorContract);
        this.setVendorContractName(vendorContract.getVendorContractName());
        vendorDetail.setVendorShippingTitleCode(vendorContract.getVendorShippingTitleCode());
        vendorDetail.refreshReferenceObject("vendorShippingTitle");
        vendorDetail.setVendorPaymentTermsCode(vendorContract.getVendorPaymentTermsCode());
        vendorDetail.refreshReferenceObject("vendorPaymentTerms");
        vendorDetail.setVendorShippingPaymentTermsCode(vendorContract.getVendorShippingPaymentTermsCode());
        vendorDetail.refreshReferenceObject("vendorShippingPaymentTerms");
        this.setVendorDetail(vendorDetail);
        this.setVendorShippingTitleCode(vendorContract.getVendorShippingTitleCode());
        this.setVendorPaymentTermsCode(vendorContract.getVendorPaymentTermsCode());
        this.setVendorShippingPaymentTermsCode(vendorContract.getVendorShippingPaymentTermsCode());
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#templateVendorAddress(org.kuali.kfs.vnd.businessobject.VendorAddress)
     */
    public void templateVendorAddress(VendorAddress vendorAddress) {
        super.templateVendorAddress(vendorAddress);
        if (vendorAddress != null) {
            this.setVendorFaxNumber(vendorAddress.getVendorFaxNumber());
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocumentBase#templateBillingAddress(org.kuali.kfs.module.purap.businessobject.BillingAddress).
     */
    public void templateBillingAddress(BillingAddress billingAddress) {
        if (ObjectUtils.isNotNull(billingAddress)) {
            this.setBillingName(billingAddress.getBillingName());
            this.setBillingLine1Address(billingAddress.getBillingLine1Address());
            this.setBillingLine2Address(billingAddress.getBillingLine2Address());
            this.setBillingCityName(billingAddress.getBillingCityName());
            this.setBillingStateCode(billingAddress.getBillingStateCode());
            this.setBillingPostalCode(billingAddress.getBillingPostalCode());
            this.setBillingCountryCode(billingAddress.getBillingCountryCode());
            this.setBillingPhoneNumber(billingAddress.getBillingPhoneNumber());
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocumentBase#templateReceivingAddress(org.kuali.kfs.module.purap.businessobject.ReceivingAddress).
     */
    public void templateReceivingAddress(ReceivingAddress receivingAddress) {
        if (receivingAddress != null) {
            this.setReceivingName(receivingAddress.getReceivingName());
            this.setReceivingLine1Address(receivingAddress.getReceivingLine1Address());
            this.setReceivingLine2Address(receivingAddress.getReceivingLine2Address());
            this.setReceivingCityName(receivingAddress.getReceivingCityName());
            this.setReceivingStateCode(receivingAddress.getReceivingStateCode());
            this.setReceivingPostalCode(receivingAddress.getReceivingPostalCode());
            this.setReceivingCountryCode(receivingAddress.getReceivingCountryCode());            
            this.setAddressToVendorIndicator(receivingAddress.isUseReceivingIndicator());
        }
        else {
            this.setReceivingName(null);
            this.setReceivingLine1Address(null);
            this.setReceivingLine2Address(null);
            this.setReceivingCityName(null);
            this.setReceivingStateCode(null);
            this.setReceivingPostalCode(null);
            this.setReceivingCountryCode(null);            
            this.setAddressToVendorIndicator(false);            
        }
    }

    /**
     * Loads the default receiving address from database corresponding to the chart/org of this document.
     */
    public void loadReceivingAddress() {
        /*
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, this.getOrganizationCode());
        criteria.put(PurapPropertyConstants.RCVNG_ADDR_DFLT_IND, true);        
        criteria.put(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);        
        List<ReceivingAddress> addresses = (List)SpringContext.getBean(BusinessObjectService.class).findMatching(ReceivingAddress.class, criteria);
        if (addresses != null && addresses.size()>0 ) 
            this.templateReceivingAddress(addresses.get(0));        
        else // if no address is found, fill with null
            this.templateReceivingAddress(null);
        */
        String chartCode = getChartOfAccountsCode();
        String orgCode = getOrganizationCode();
        ReceivingAddress address = SpringContext.getBean(ReceivingAddressService.class).findUniqueDefaultByChartOrg(chartCode, orgCode);        
        // if default address for chart/org not found, look for chart default 
        if (address == null && orgCode != null) {
            address = SpringContext.getBean(ReceivingAddressService.class).findUniqueDefaultByChartOrg(chartCode, null);
        }
        this.templateReceivingAddress(address);
    }
    
    /**
     * Iterates through the purchasingCapitalAssetItems of the document and returns the purchasingCapitalAssetItem with the item id equal to the number given, or null if a
     * match is not found.
     * 
     * @param itemIdentifier item id to match on.
     * @return the PurchasingCapitalAssetItem if a match is found, else null.
     */
    public PurchasingCapitalAssetItem getPurchasingCapitalAssetItemByItemIdentifier(int itemIdentifier) {
        for (Iterator iter = purchasingCapitalAssetItems.iterator(); iter.hasNext();) {
            PurchasingCapitalAssetItem camsItem = (PurchasingCapitalAssetItem) iter.next();
            if (camsItem.getItemIdentifier().intValue() == itemIdentifier) {
                return camsItem;
            }
        }
        return null;
    }


    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#addItem(org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    @Override
    public void addItem(PurApItem item) {
        item.refreshReferenceObject(PurapPropertyConstants.COMMODITY_CODE);
        super.addItem(item);
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        commodityCodesForRouting = new ArrayList<CommodityCode>();
        for (PurchasingItemBase item : (List<PurchasingItemBase>)this.getItems()) {
            if (item.getCommodityCode() != null) {
                commodityCodesForRouting.add(item.getCommodityCode());
            }
        }
        super.populateDocumentForRouting();
    }

    // GETTERS AND SETTERS

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument.getItemParser().
     */
    public ItemParser getItemParser() {
        return new ItemParserBase();
    }
    
    public String getBillingCityName() {
        return billingCityName;
    }

    public void setBillingCityName(String billingCityName) {
        this.billingCityName = billingCityName;
    }

    public String getBillingCountryCode() {
        return billingCountryCode;
    }

    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    public String getBillingLine1Address() {
        return billingLine1Address;
    }

    public void setBillingLine1Address(String billingLine1Address) {
        this.billingLine1Address = billingLine1Address;
    }

    public String getBillingLine2Address() {
        return billingLine2Address;
    }

    public void setBillingLine2Address(String billingLine2Address) {
        this.billingLine2Address = billingLine2Address;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getBillingPhoneNumber() {
        return billingPhoneNumber;
    }

    public void setBillingPhoneNumber(String billingPhoneNumber) {
        this.billingPhoneNumber = billingPhoneNumber;
    }

    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    public String getBillingStateCode() {
        return billingStateCode;
    }

    public void setBillingStateCode(String billingStateCode) {
        this.billingStateCode = billingStateCode;
    }

    public String getReceivingCityName() {
        return receivingCityName;
    }

    public void setReceivingCityName(String receivingCityName) {
        this.receivingCityName = receivingCityName;
    }

    public String getReceivingCountryCode() {
        return receivingCountryCode;
    }

    public void setReceivingCountryCode(String receivingCountryCode) {
        this.receivingCountryCode = receivingCountryCode;
    }

    public String getReceivingLine1Address() {
        return receivingLine1Address;
    }

    public void setReceivingLine1Address(String receivingLine1Address) {
        this.receivingLine1Address = receivingLine1Address;
    }

    public String getReceivingLine2Address() {
        return receivingLine2Address;
    }

    public void setReceivingLine2Address(String receivingLine2Address) {
        this.receivingLine2Address = receivingLine2Address;
    }

    public String getReceivingName() {
        return receivingName;
    }

    public void setReceivingName(String receivingName) {
        this.receivingName = receivingName;
    }

    public String getReceivingPostalCode() {
        return receivingPostalCode;
    }

    public void setReceivingPostalCode(String receivingPostalCode) {
        this.receivingPostalCode = receivingPostalCode;
    }

    public String getReceivingStateCode() {
        return receivingStateCode;
    }

    public void setReceivingStateCode(String receivingStateCode) {
        this.receivingStateCode = receivingStateCode;
    }

    public boolean getAddressToVendorIndicator() {
        return addressToVendorIndicator;
    }

    public void setAddressToVendorIndicator(boolean addressToVendor) {
        this.addressToVendorIndicator = addressToVendor;
    }
    
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getDeliveryBuildingCode() {
        return deliveryBuildingCode;
    }

    public void setDeliveryBuildingCode(String deliveryBuildingCode) {
        this.deliveryBuildingCode = deliveryBuildingCode;
    }

    public String getDeliveryBuildingLine1Address() {
        return deliveryBuildingLine1Address;
    }

    public void setDeliveryBuildingLine1Address(String deliveryBuildingLine1Address) {
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
    }

    public String getDeliveryBuildingLine2Address() {
        return deliveryBuildingLine2Address;
    }

    public void setDeliveryBuildingLine2Address(String deliveryBuildingLine2Address) {
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
    }

    public String getDeliveryBuildingName() {
        return deliveryBuildingName;
    }

    public void setDeliveryBuildingName(String deliveryBuildingName) {
        this.deliveryBuildingName = deliveryBuildingName;
    }

    public boolean isDeliveryBuildingOther() {
        return deliveryBuildingOther;
    }

    public void setDeliveryBuildingOther(boolean deliveryBuildingOther) {
        this.deliveryBuildingOther = deliveryBuildingOther;
    }

    public String getDeliveryBuildingRoomNumber() {
        return deliveryBuildingRoomNumber;
    }

    public void setDeliveryBuildingRoomNumber(String deliveryBuildingRoomNumber) {
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
    }

    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    public String getDeliveryCityName() {
        return deliveryCityName;
    }

    public void setDeliveryCityName(String deliveryCityName) {
        this.deliveryCityName = deliveryCityName;
    }

    public String getDeliveryCountryCode() {
        return deliveryCountryCode;
    }

    public void setDeliveryCountryCode(String deliveryCountryCode) {
        this.deliveryCountryCode = deliveryCountryCode;
    }

    public String getDeliveryInstructionText() {
        return deliveryInstructionText;
    }

    public void setDeliveryInstructionText(String deliveryInstructionText) {
        this.deliveryInstructionText = deliveryInstructionText;
    }

    public String getDeliveryPostalCode() {
        return deliveryPostalCode;
    }

    public void setDeliveryPostalCode(String deliveryPostalCode) {
        this.deliveryPostalCode = deliveryPostalCode;
    }

    public Date getDeliveryRequiredDate() {
        return deliveryRequiredDate;
    }

    public void setDeliveryRequiredDate(Date deliveryRequiredDate) {
        this.deliveryRequiredDate = deliveryRequiredDate;
    }

    public String getDeliveryRequiredDateReasonCode() {
        return deliveryRequiredDateReasonCode;
    }

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    public String getDeliveryStateCode() {
        return deliveryStateCode;
    }

    public void setDeliveryStateCode(String deliveryStateCode) {
        this.deliveryStateCode = deliveryStateCode;
    }

    public String getDeliveryToEmailAddress() {
        return deliveryToEmailAddress;
    }

    public void setDeliveryToEmailAddress(String deliveryToEmailAddress) {
        this.deliveryToEmailAddress = deliveryToEmailAddress;
    }

    public String getDeliveryToName() {
        return deliveryToName;
    }

    public void setDeliveryToName(String deliveryToName) {
        this.deliveryToName = deliveryToName;
    }

    public String getDeliveryToPhoneNumber() {
        return deliveryToPhoneNumber;
    }

    public void setDeliveryToPhoneNumber(String deliveryToPhoneNumber) {
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
    }

    public String getExternalOrganizationB2bSupplierIdentifier() {
        return externalOrganizationB2bSupplierIdentifier;
    }

    public void setExternalOrganizationB2bSupplierIdentifier(String externalOrganizationB2bSupplierIdentifier) {
        this.externalOrganizationB2bSupplierIdentifier = externalOrganizationB2bSupplierIdentifier;
    }

    public String getFundingSourceCode() {
        return fundingSourceCode;
    }

    public void setFundingSourceCode(String fundingSourceCode) {
        this.fundingSourceCode = fundingSourceCode;
    }

    public String getInstitutionContactEmailAddress() {
        return institutionContactEmailAddress;
    }

    public void setInstitutionContactEmailAddress(String institutionContactEmailAddress) {
        this.institutionContactEmailAddress = institutionContactEmailAddress;
    }

    public String getInstitutionContactName() {
        return institutionContactName;
    }

    public void setInstitutionContactName(String institutionContactName) {
        this.institutionContactName = institutionContactName;
    }

    public String getInstitutionContactPhoneNumber() {
        return institutionContactPhoneNumber;
    }

    public void setInstitutionContactPhoneNumber(String institutionContactPhoneNumber) {
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
    }

    public String getNonInstitutionFundAccountNumber() {
        return nonInstitutionFundAccountNumber;
    }

    public void setNonInstitutionFundAccountNumber(String nonInstitutionFundAccountNumber) {
        this.nonInstitutionFundAccountNumber = nonInstitutionFundAccountNumber;
    }

    public String getNonInstitutionFundChartOfAccountsCode() {
        return nonInstitutionFundChartOfAccountsCode;
    }

    public void setNonInstitutionFundChartOfAccountsCode(String nonInstitutionFundChartOfAccountsCode) {
        this.nonInstitutionFundChartOfAccountsCode = nonInstitutionFundChartOfAccountsCode;
    }

    public String getNonInstitutionFundOrganizationCode() {
        return nonInstitutionFundOrganizationCode;
    }

    public void setNonInstitutionFundOrganizationCode(String nonInstitutionFundOrganizationCode) {
        this.nonInstitutionFundOrganizationCode = nonInstitutionFundOrganizationCode;
    }

    public String getNonInstitutionFundOrgChartOfAccountsCode() {
        return nonInstitutionFundOrgChartOfAccountsCode;
    }

    public void setNonInstitutionFundOrgChartOfAccountsCode(String nonInstitutionFundOrgChartOfAccountsCode) {
        this.nonInstitutionFundOrgChartOfAccountsCode = nonInstitutionFundOrgChartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public boolean getPurchaseOrderAutomaticIndicator() {
        return purchaseOrderAutomaticIndicator;
    }

    public void setPurchaseOrderAutomaticIndicator(boolean purchaseOrderAutomaticIndicator) {
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
    }

    public Date getPurchaseOrderBeginDate() {
        return purchaseOrderBeginDate;
    }

    public void setPurchaseOrderBeginDate(Date purchaseOrderBeginDate) {
        this.purchaseOrderBeginDate = purchaseOrderBeginDate;
    }

    public String getPurchaseOrderCostSourceCode() {
        return purchaseOrderCostSourceCode;
    }

    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
    }

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    public KualiDecimal getPurchaseOrderTotalLimit() {
        return purchaseOrderTotalLimit;
    }

    public void setPurchaseOrderTotalLimit(KualiDecimal purchaseOrderTotalLimit) {
        this.purchaseOrderTotalLimit = purchaseOrderTotalLimit;
    }

    public String getPurchaseOrderTransmissionMethodCode() {
        return purchaseOrderTransmissionMethodCode;
    }

    public void setPurchaseOrderTransmissionMethodCode(String purchaseOrderTransmissionMethodCode) {
        this.purchaseOrderTransmissionMethodCode = purchaseOrderTransmissionMethodCode;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    public String getRequestorPersonEmailAddress() {
        return requestorPersonEmailAddress;
    }

    public void setRequestorPersonEmailAddress(String requestorPersonEmailAddress) {
        this.requestorPersonEmailAddress = requestorPersonEmailAddress;
    }

    public String getRequestorPersonName() {
        return requestorPersonName;
    }

    public void setRequestorPersonName(String requestorPersonName) {
        this.requestorPersonName = requestorPersonName;
    }

    public String getRequestorPersonPhoneNumber() {
        return requestorPersonPhoneNumber;
    }

    public void setRequestorPersonPhoneNumber(String requestorPersonPhoneNumber) {
        this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
    }

    public String getRequisitionSourceCode() {
        return requisitionSourceCode;
    }

    public void setRequisitionSourceCode(String requisitionSourceCode) {
        this.requisitionSourceCode = requisitionSourceCode;
    }

    public String getVendorContactsLabel() {
        return vendorContactsLabel;
    }

    public void setVendorContactsLabel(String vendorContactsLabel) {
        this.vendorContactsLabel = vendorContactsLabel;
    }

    public VendorContract getVendorContract() {
        return vendorContract;
    }

    public void setVendorContract(VendorContract vendorContract) {
        this.vendorContract = vendorContract;
    }

    public Integer getVendorContractGeneratedIdentifier() {
        return vendorContractGeneratedIdentifier;
    }

    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
    }

    public String getVendorContractName() {
        return vendorContractName;
    }

    public void setVendorContractName(String vendorContractName) {
        this.vendorContractName = vendorContractName;
    }

    public String getVendorFaxNumber() {
        return vendorFaxNumber;
    }

    public void setVendorFaxNumber(String vendorFaxNumber) {
        this.vendorFaxNumber = vendorFaxNumber;
    }

    public String getVendorNoteText() {
        return vendorNoteText;
    }

    public void setVendorNoteText(String vendorNoteText) {
        this.vendorNoteText = vendorNoteText;
    }

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorPhoneNumber() {
        return vendorPhoneNumber;
    }

    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    public Boolean getVendorRestrictedIndicator() {
        return vendorRestrictedIndicator;
    }

    public void setVendorRestrictedIndicator(Boolean vendorRestrictedIndicator) {
        this.vendorRestrictedIndicator = vendorRestrictedIndicator;
    }

    public String getVendorShippingPaymentTermsCode() {
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public String getVendorShippingTitleCode() {
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public Campus getDeliveryCampus() {
        return deliveryCampus;
    }

    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return deliveryRequiredDateReason;
    }

    public FundingSource getFundingSource() {
        return fundingSource;
    }

    public Account getNonInstitutionFundAccount() {
        return nonInstitutionFundAccount;
    }

    public Chart getNonInstitutionFundChartOfAccounts() {
        return nonInstitutionFundChartOfAccounts;
    }

    public Org getNonInstitutionFundOrganization() {
        return nonInstitutionFundOrganization;
    }

    public Chart getNonInstitutionFundOrgChartOfAccounts() {
        return nonInstitutionFundOrgChartOfAccounts;
    }

    public Org getOrganization() {
        return organization;
    }

    public PurchaseOrderTransmissionMethod getPurchaseOrderTransmissionMethod() {
        return purchaseOrderTransmissionMethod;
    }

    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }

    public RequisitionSource getRequisitionSource() {
        return requisitionSource;
    }

    public String getSupplierDiversityLabel() {
        return supplierDiversityLabel;
    }

    public PurchaseOrderCostSource getPurchaseOrderCostSource() {
        if (ObjectUtils.isNull(purchaseOrderCostSource))
            refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_COST_SOURCE);
        return purchaseOrderCostSource;
    }

    /**
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setDeliveryCampus(Campus deliveryCampus) {
        this.deliveryCampus = deliveryCampus;
    }

    /**
     * @deprecated
     */
    public void setDeliveryRequiredDateReason(DeliveryRequiredDateReason deliveryRequiredDateReason) {
        this.deliveryRequiredDateReason = deliveryRequiredDateReason;
    }

    /**
     * @deprecated
     */
    public void setFundingSource(FundingSource fundingSource) {
        this.fundingSource = fundingSource;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundAccount(Account nonInstitutionFundAccount) {
        this.nonInstitutionFundAccount = nonInstitutionFundAccount;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundChartOfAccounts(Chart nonInstitutionFundChartOfAccounts) {
        this.nonInstitutionFundChartOfAccounts = nonInstitutionFundChartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrganization(Org nonInstitutionFundOrganization) {
        this.nonInstitutionFundOrganization = nonInstitutionFundOrganization;
    }

    /**
     * @deprecated
     */
    public void setNonInstitutionFundOrgChartOfAccounts(Chart nonInstitutionFundOrgChartOfAccounts) {
        this.nonInstitutionFundOrgChartOfAccounts = nonInstitutionFundOrgChartOfAccounts;
    }

    /**
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * @deprecated
     */
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource) {
        this.purchaseOrderCostSource = purchaseOrderCostSource;
    }

    /**
     * @deprecated
     */
    public void setPurchaseOrderTransmissionMethod(PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod) {
        this.purchaseOrderTransmissionMethod = purchaseOrderTransmissionMethod;
    }

    /**
     * @deprecated
     */
    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    /**
     * @deprecated
     */
    public void setRequisitionSource(RequisitionSource requisitionSource) {
        this.requisitionSource = requisitionSource;
    }

    /**
     * Gets the receivingDocumentRequiredIndicator attribute. 
     * @return Returns the receivingDocumentRequiredIndicator.
     */
    public boolean isReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }
    
    /**
     * Sets the receivingDocumentRequiredIndicator attribute value.
     * @param receivingDocumentRequiredIndicator The receivingDocumentRequiredIndicator to set.
     */
    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator) {
        this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
    }

    public boolean isPaymentRequestPositiveApprovalIndicator() {
        return paymentRequestPositiveApprovalIndicator;
    }

    public void setPaymentRequestPositiveApprovalIndicator(boolean paymentRequestPositiveApprovalIndicator) {
        this.paymentRequestPositiveApprovalIndicator = paymentRequestPositiveApprovalIndicator;
    }

    public List<CommodityCode> getCommodityCodesForRouting() {
        return commodityCodesForRouting;
    }

    public void setCommodityCodesForRouting(List<CommodityCode> commodityCodesForRouting) {
        this.commodityCodesForRouting = commodityCodesForRouting;
    }

    public String getCapitalAssetSystemTypeCode() {
        return capitalAssetSystemTypeCode;
    }

    public void setCapitalAssetSystemTypeCode(String capitalAssetSystemTypeCode) {
        this.capitalAssetSystemTypeCode = capitalAssetSystemTypeCode;
    }

    public String getCapitalAssetSystemStateCode() {
        return capitalAssetSystemStateCode;
    }

    public void setCapitalAssetSystemStateCode(String capitalAssetSystemStateCode) {
        this.capitalAssetSystemStateCode = capitalAssetSystemStateCode;
    }

    public CapitalAssetSystemType getCapitalAssetSystemType() {
        if(ObjectUtils.isNull(capitalAssetSystemType)){
            this.refreshReferenceObject("capitalAssetSystemType");
        }
        return capitalAssetSystemType;
    }

    public void setCapitalAssetSystemType(CapitalAssetSystemType capitalAssetSystemType) {
        this.capitalAssetSystemType = capitalAssetSystemType;
    }

    public CapitalAssetSystemState getCapitalAssetSystemState() {
        if(ObjectUtils.isNull(capitalAssetSystemState)){
            this.refreshReferenceObject("capitalAssetSystemState");
        }
        return capitalAssetSystemState;
    }

    public void setCapitalAssetSystemState(CapitalAssetSystemState capitalAssetSystemState) {
        this.capitalAssetSystemState = capitalAssetSystemState;
    }

    public List<CapitalAssetSystem> getPurchasingCapitalAssetSystems() {
        return purchasingCapitalAssetSystems;
    }

    public void setPurchasingCapitalAssetSystems(List<CapitalAssetSystem> purchasingCapitalAssetSystems) {
        this.purchasingCapitalAssetSystems = purchasingCapitalAssetSystems;
    }

    public List<PurchasingCapitalAssetItem> getPurchasingCapitalAssetItems() {
        return purchasingCapitalAssetItems;
    }

    public void setPurchasingCapitalAssetItems(List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems) {
        this.purchasingCapitalAssetItems = purchasingCapitalAssetItems;
    }

    public abstract Class getPurchasingCapitalAssetItemClass();

    public abstract Class getPurchasingCapitalAssetSystemClass();
    
    public PurchasingItem getPurchasingItem(Integer itemIdentifier){
        
        if(ObjectUtils.isNull(itemIdentifier)) return null;
        
        PurchasingItem item = null;
                
        for(PurchasingItem pi: (List<PurchasingItem>)this.getItems()){
            if(itemIdentifier.equals(pi.getItemIdentifier())){
                item = pi;
                break;
            }
        }
        
        return item;
    }
    
    public PurchasingCapitalAssetItem getPurchasingCapitalAssetItem(Integer itemIdentifier){

        if(ObjectUtils.isNull(itemIdentifier)) return null;
        
        PurchasingCapitalAssetItem item = null;
        
        for(PurchasingCapitalAssetItem pcai: this.getPurchasingCapitalAssetItems()){
            if(itemIdentifier.equals(pcai.getItemIdentifier())){
                item = pcai;
                break;
            }
        }
        
        return item;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        if (allowDeleteAwareCollection) {
            managedLists.add(this.getPurchasingCapitalAssetItems());
            List<ItemCapitalAsset> assetLists = new ArrayList<ItemCapitalAsset>();
            if (StringUtils.equals(this.getCapitalAssetSystemTypeCode(),PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                for (PurchasingCapitalAssetItem capitalAssetItem : this.getPurchasingCapitalAssetItems()) {
                    //We only need to add the itemCapitalAssets to assetLists if the system is not null, otherwise 
                    //just let the assetLists be empty ArrayList.
                    if (capitalAssetItem.getPurchasingCapitalAssetSystem() != null) {
                        assetLists.addAll(capitalAssetItem.getPurchasingCapitalAssetSystem().getItemCapitalAssets());
                    }
                }
            }
            else {
                for (CapitalAssetSystem system : this.getPurchasingCapitalAssetSystems()) {
                    assetLists.addAll(system.getItemCapitalAssets());
                }
            }
            managedLists.add(assetLists);
        }
        return managedLists;
    }
    
    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to remove the
     * purchasingCapitalAssetSystem when the system type is either ONE or MULT.
     * 
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        if (StringUtils.isNotBlank(this.getCapitalAssetSystemTypeCode())) {
            if (this.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM) || this.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.MULTIPLE)) {
                //If the system state is ONE or MULT, we have to remove all the systems on the items because it's not applicable.
                for (PurchasingCapitalAssetItem camsItem : this.getPurchasingCapitalAssetItems()) {
                    camsItem.setPurchasingCapitalAssetSystem(null);
                }
            }
        }
    }

}