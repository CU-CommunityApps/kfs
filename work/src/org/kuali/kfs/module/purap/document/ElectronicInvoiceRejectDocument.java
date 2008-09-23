/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.document;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceContact;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoicePostalAddress;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.document.SessionDocument;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author delyea
 */
public class ElectronicInvoiceRejectDocument extends FinancialSystemTransactionalDocumentBase implements SessionDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceRejectDocument.class);
    private static BigDecimal zero = new BigDecimal(0);

    // NOT NULL FIELDS
    private Integer purapDocumentIdentifier;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private Integer invoiceLoadSummaryIdentifier;
    private Date invoiceProcessDate;
    private Boolean invoiceFileHeaderTypeIndicator = Boolean.FALSE;
    private Boolean invoiceFileInformationOnlyIndicator = Boolean.FALSE;
    private Boolean invoiceFileTaxInLineIndicator = Boolean.FALSE;
    private Boolean invoiceFileSpecialHandlingInLineIndicator = Boolean.FALSE;
    private Boolean invoiceFileShippingInLineIndicator = Boolean.FALSE;
    private Boolean invoiceFileDiscountInLineIndicator = Boolean.FALSE;

    private String invoiceFileName;
    private String vendorDunsNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String invoiceFileDate;
    private String invoiceFileNumber;
    private String invoiceFilePurposeIdentifier;
    private String invoiceFileOperationIdentifier;
    private String invoiceFileDeploymentModeValue;
    private String invoiceOrderReferenceOrderIdentifier;
    private String invoiceOrderReferenceDocumentReferencePayloadIdentifier;
    private String invoiceOrderReferenceDocumentReferenceText;
    private String invoiceOrderMasterAgreementReferenceIdentifier;
    private String invoiceOrderMasterAgreementReferenceDate;
    private String invoiceOrderMasterAgreementInformationIdentifier;
    private String invoiceOrderMasterAgreementInformationDate;
    private String invoiceOrderPurchaseOrderIdentifier;
    private String invoiceOrderPurchaseOrderDate;
    private String invoiceOrderSupplierOrderInformationIdentifier;
    private String invoiceShipDate;
    private String invoiceShipToAddressName;
    private String invoiceShipToAddressType;
    private String invoiceShipToAddressLine1;
    private String invoiceShipToAddressLine2;
    private String invoiceShipToAddressLine3;
    private String invoiceShipToAddressCityName;
    private String invoiceShipToAddressStateCode;
    private String invoiceShipToAddressPostalCode;
    private String invoiceShipToAddressCountryCode;
    private String invoiceShipToAddressCountryName;
    private String invoiceBillToAddressName;
    private String invoiceBillToAddressType;
    private String invoiceBillToAddressLine1;
    private String invoiceBillToAddressLine2;
    private String invoiceBillToAddressLine3;
    private String invoiceBillToAddressCityName;
    private String invoiceBillToAddressStateCode;
    private String invoiceBillToAddressPostalCode;
    private String invoiceBillToAddressCountryCode;
    private String invoiceBillToAddressCountryName;
    private String invoiceRemitToAddressName;
    private String invoiceRemitToAddressType;
    private String invoiceRemitToAddressLine1;
    private String invoiceRemitToAddressLine2;
    private String invoiceRemitToAddressLine3;
    private String invoiceRemitToAddressCityName;
    private String invoiceRemitToAddressStateCode;
    private String invoiceRemitToAddressPostalCode;
    private String invoiceRemitToAddressCountryCode;
    private String invoiceRemitToAddressCountryName;

    private String invoiceCustomerNumber;
    private String invoicePurchaseOrderNumber;
    private Integer purchaseOrderIdentifier; // epic
    private String purchaseOrderDeliveryCampusCode; // epic

    private String invoiceItemSubTotalCurrencyCode;
    private String invoiceItemSpecialHandlingCurrencyCode;
    private String invoiceItemSpecialHandlingDescription;
    private String invoiceItemShippingCurrencyCode;
    private String invoiceItemShippingDescription;
    private String invoiceItemTaxCurrencyCode;
    private String invoiceItemTaxDescription;
    private String invoiceItemGrossCurrencyCode;
    private String invoiceItemDiscountCurrencyCode;
    private String invoiceItemNetCurrencyCode;

    private BigDecimal invoiceItemSubTotalAmount;
    private BigDecimal invoiceItemSpecialHandlingAmount;
    private BigDecimal invoiceItemShippingAmount;
    private BigDecimal invoiceItemTaxAmount;
    private BigDecimal invoiceItemGrossAmount;
    private BigDecimal invoiceItemDiscountAmount;
    private BigDecimal invoiceItemNetAmount;

    private boolean invoiceNumberAcceptIndicator = false;
    private boolean invoiceResearchIndicator = false;
    
    private Date accountsPayableApprovalDate;
    
    private transient PurApRelatedViews relatedViews;
    private PurchaseOrderDocument currentPurchaseOrderDocument;

    private VendorDetail vendorDetail;
    private ElectronicInvoiceLoadSummary invoiceLoadSummary;
    private List<ElectronicInvoiceRejectItem> invoiceRejectItems = new ArrayList<ElectronicInvoiceRejectItem>();
    private List<ElectronicInvoiceRejectReason> invoiceRejectReasons = new ArrayList<ElectronicInvoiceRejectReason>();

    /**
   * 
   */
    public ElectronicInvoiceRejectDocument() {
        super();
    }

    public void setFileLevelData(ElectronicInvoice ei) {
        this.invoiceProcessDate = new Timestamp((new Date()).getTime());
        this.invoiceFileHeaderTypeIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator());
        this.invoiceFileInformationOnlyIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isInformationOnly());
        this.invoiceFileTaxInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isTaxInLine());
        this.invoiceFileSpecialHandlingInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isSpecialHandlingInLine());
        this.invoiceFileShippingInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isShippingInLine());
        this.invoiceFileDiscountInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isDiscountInLine());

        this.invoiceFileName = ei.getFileName();
        this.vendorDunsNumber = ei.getDunsNumber();
        this.vendorHeaderGeneratedIdentifier = ei.getVendorHeaderID();
        this.vendorDetailAssignedIdentifier = ei.getVendorDetailID();
        this.invoiceFileDate = ei.getInvoiceDateDisplayText();
        this.invoiceFileNumber = ei.getInvoiceDetailRequestHeader().getInvoiceId();
        this.invoiceFilePurposeIdentifier = ei.getInvoiceDetailRequestHeader().getPurpose();
        this.invoiceFileOperationIdentifier = ei.getInvoiceDetailRequestHeader().getOperation();
        this.invoiceFileDeploymentModeValue = ei.getDeploymentMode();
        this.invoiceCustomerNumber = ei.getCustomerNumber();

        for (ElectronicInvoiceRejectReason eirr : ei.getFileRejectReasons()) {
            ElectronicInvoiceRejectReason newReason = new ElectronicInvoiceRejectReason();
            ObjectUtil.buildObject(newReason, eirr);
            this.invoiceRejectReasons.add(newReason);
        }
    }

    public void setInvoiceOrderLevelData(ElectronicInvoice ei, ElectronicInvoiceOrder eio) {
        this.invoiceOrderReferenceOrderIdentifier = eio.getOrderReferenceOrderID();
        this.invoiceOrderReferenceDocumentReferencePayloadIdentifier = eio.getOrderReferenceDocumentRefPayloadID();
        this.invoiceOrderReferenceDocumentReferenceText = eio.getOrderReferenceDocumentRef();
        this.invoiceOrderMasterAgreementReferenceIdentifier = eio.getMasterAgreementReferenceID();
        this.invoiceOrderMasterAgreementReferenceDate = ei.getMasterAgreementReferenceDateDisplayText(eio);
        this.invoiceOrderMasterAgreementInformationIdentifier = eio.getMasterAgreementIDInfoID();
        this.invoiceOrderMasterAgreementInformationDate = ei.getMasterAgreementIDInfoDateDisplayText(eio);
        this.invoiceOrderPurchaseOrderIdentifier = eio.getOrderIDInfoID();
        this.invoiceOrderPurchaseOrderDate = ei.getOrderIDInfoDateDisplayText(eio);
        this.invoiceOrderSupplierOrderInformationIdentifier = eio.getSupplierOrderInfoID();
        this.invoiceShipDate = ei.getShippingDateDisplayText(eio);

        ElectronicInvoiceContact shipToContact = ei.getCxmlContact(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_ROLE_ID);
        ElectronicInvoicePostalAddress shipToAddress = this.getCxmlPostalAddressByAddressName(shipToContact, ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_NAME);
        // ElectronicInvoicePostalAddress shipToAddress =
        // ei.getCxmlPostalAddress(eio,ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_ROLE_ID,
        // ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_NAME);
        if (shipToAddress != null) {
            this.invoiceShipToAddressName = shipToContact.getName();
            this.invoiceShipToAddressType = shipToAddress.getType();
            this.invoiceShipToAddressLine1 = shipToAddress.getLine1();
            this.invoiceShipToAddressLine2 = shipToAddress.getLine2();
            this.invoiceShipToAddressLine3 = shipToAddress.getLine3();
            this.invoiceShipToAddressCityName = shipToAddress.getCityName();
            this.invoiceShipToAddressStateCode = shipToAddress.getStateCode();
            this.invoiceShipToAddressPostalCode = shipToAddress.getPostalCode();
            this.invoiceShipToAddressCountryCode = shipToAddress.getCountryCode();
            this.invoiceShipToAddressCountryName = shipToAddress.getCountryName();
        }

        ElectronicInvoiceContact billToContact = ei.getCxmlContact(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_ROLE_ID);
        ElectronicInvoicePostalAddress billToAddress = this.getCxmlPostalAddressByAddressName(billToContact, ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_NAME);
        // ElectronicInvoicePostalAddress billToAddress =
        // ei.getCxmlPostalAddress(eio,ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_ROLE_ID,
        // ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_NAME);
        if (billToAddress != null) {
            this.invoiceBillToAddressName = billToContact.getName();
            this.invoiceBillToAddressType = billToAddress.getType();
            this.invoiceBillToAddressLine1 = billToAddress.getLine1();
            this.invoiceBillToAddressLine2 = billToAddress.getLine2();
            this.invoiceBillToAddressLine3 = billToAddress.getLine3();
            this.invoiceBillToAddressCityName = billToAddress.getCityName();
            this.invoiceBillToAddressStateCode = billToAddress.getStateCode();
            this.invoiceBillToAddressPostalCode = billToAddress.getPostalCode();
            this.invoiceBillToAddressCountryCode = billToAddress.getCountryCode();
            this.invoiceBillToAddressCountryName = billToAddress.getCountryName();
        }

        ElectronicInvoiceContact remitToContact = ei.getCxmlContact(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_ROLE_ID);
        ElectronicInvoicePostalAddress remitToAddress = this.getCxmlPostalAddressByAddressName(remitToContact, ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_NAME);
        // ElectronicInvoicePostalAddress remitToAddress =
        // ei.getCxmlPostalAddress(eio,ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_ROLE_ID,
        // ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_NAME);
        if (remitToAddress != null) {
            this.invoiceRemitToAddressName = remitToContact.getName();
            this.invoiceRemitToAddressType = remitToAddress.getType();
            this.invoiceRemitToAddressLine1 = remitToAddress.getLine1();
            this.invoiceRemitToAddressLine2 = remitToAddress.getLine2();
            this.invoiceRemitToAddressLine3 = remitToAddress.getLine3();
            this.invoiceRemitToAddressCityName = remitToAddress.getCityName();
            this.invoiceRemitToAddressStateCode = remitToAddress.getStateCode();
            this.invoiceRemitToAddressPostalCode = remitToAddress.getPostalCode();
            this.invoiceRemitToAddressCountryCode = remitToAddress.getCountryCode();
            this.invoiceRemitToAddressCountryName = remitToAddress.getCountryName();
        }
        this.invoicePurchaseOrderNumber = eio.getInvoicePurchaseOrderID();
        this.purchaseOrderIdentifier = eio.getPurchaseOrderID();
        this.purchaseOrderDeliveryCampusCode = eio.getPurchaseOrderCampusCode();

        try {
            this.invoiceItemSubTotalAmount = ei.getInvoiceSubTotalAmount(eio);
            this.invoiceItemSubTotalCurrencyCode = ei.getInvoiceSubTotalCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemSubTotalAmount = null;
            this.invoiceItemSubTotalCurrencyCode = "INVALID Amount";
        }
        try {
            this.invoiceItemSpecialHandlingAmount = ei.getInvoiceSpecialHandlingAmount(eio);
            this.invoiceItemSpecialHandlingCurrencyCode = ei.getInvoiceSpecialHandlingCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemSpecialHandlingAmount = null;
            this.invoiceItemSpecialHandlingCurrencyCode = "INVALID AMOUNT";
        }
        this.invoiceItemSpecialHandlingDescription = ei.getInvoiceSpecialHandlingDescription(eio);
        try {
            this.invoiceItemShippingAmount = ei.getInvoiceShippingAmount(eio);
            this.invoiceItemShippingCurrencyCode = ei.getInvoiceShippingCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemShippingAmount = null;
            this.invoiceItemShippingCurrencyCode = "INVALID AMOUNT";
        }
        this.invoiceItemShippingDescription = ei.getInvoiceShippingDescription(eio);
        try {
            this.invoiceItemTaxAmount = ei.getInvoiceTaxAmount(eio);
            this.invoiceItemTaxCurrencyCode = ei.getInvoiceTaxCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemTaxAmount = null;
            this.invoiceItemTaxCurrencyCode = "INVALID AMOUNT";
        }
        this.invoiceItemTaxDescription = ei.getInvoiceTaxDescription(eio);
        try {
            this.invoiceItemGrossAmount = ei.getInvoiceGrossAmount(eio);
            this.invoiceItemGrossCurrencyCode = ei.getInvoiceGrossCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemGrossAmount = null;
            this.invoiceItemGrossCurrencyCode = "INVALID AMOUNT";
        }
        try {
            this.invoiceItemDiscountAmount = ei.getInvoiceDiscountAmount(eio);
            this.invoiceItemDiscountCurrencyCode = ei.getInvoiceDiscountCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemDiscountAmount = null;
            this.invoiceItemDiscountCurrencyCode = "INVALID AMOUNT";
        }
        try {
            this.invoiceItemNetAmount = ei.getInvoiceNetAmount(eio);
            this.invoiceItemNetCurrencyCode = ei.getInvoiceNetCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceItemNetAmount = null;
            this.invoiceItemNetCurrencyCode = "INVALID AMOUNT";
        }

        for (ElectronicInvoiceItem eii : eio.getInvoiceItems()) {
            ElectronicInvoiceRejectItem eiri = new ElectronicInvoiceRejectItem(this, eii);
            this.invoiceRejectItems.add(eiri);
        }

        for (ElectronicInvoiceRejectReason eirr : eio.getOrderRejectReasons()) {
            eirr.setElectronicInvoiceRejectDocument(this);
            this.invoiceRejectReasons.add(eirr);
        }
    }

    private ElectronicInvoicePostalAddress getCxmlPostalAddressByAddressName(ElectronicInvoiceContact contact, String addressName) {
        if (contact != null) {
            for (ElectronicInvoicePostalAddress cpa : contact.getPostalAddresses()) {
                if (addressName == null) {
                    return cpa;
                }
                else {
                    if (addressName.equalsIgnoreCase(cpa.getName())) {
                        return cpa;
                    }
                }
            }
        }
        return null;
    }

    public KualiDecimal getTotalAmount() {
        KualiDecimal returnValue = new KualiDecimal(zero);
        try {
            for (ElectronicInvoiceRejectItem eiri : this.invoiceRejectItems) {
                KualiDecimal toAddAmount = new KualiDecimal(eiri.getInvoiceItemSubTotalAmount());
                LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                returnValue = returnValue.add(toAddAmount);
            }
            LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());
            return returnValue;
        }
        catch (NumberFormatException n) {
            // do nothing this is already rejected
            LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
            return new KualiDecimal(zero);
        }
    }

    public void setTotalAmount(KualiDecimal amount) {
        // ignore this
    }

    public KualiDecimal getGrandTotalAmount() {
        KualiDecimal returnValue = new KualiDecimal(zero);
        try {
            for (ElectronicInvoiceRejectItem eiri : this.invoiceRejectItems) {
                KualiDecimal toAddAmount = new KualiDecimal(eiri.getInvoiceItemNetAmount());
                LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                returnValue = returnValue.add(toAddAmount);
            }
            LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());

            if (this.getInvoiceItemSpecialHandlingAmount() != null && zero.compareTo(this.getInvoiceItemSpecialHandlingAmount()) != 0) {
                returnValue = returnValue.add(new KualiDecimal(this.getInvoiceItemSpecialHandlingAmount()));
            }
            if (this.getInvoiceItemShippingAmount() != null && zero.compareTo(this.getInvoiceItemShippingAmount()) != 0) {
                returnValue = returnValue.add(new KualiDecimal(this.getInvoiceItemShippingAmount()));
            }
            // if (this.getInvoiceItemTaxAmount() != null && zero.compareTo(this.getInvoiceItemTaxAmount()) != 0) {
            // returnValue = returnValue.add(new KualiDecimal(this.getInvoiceItemTaxAmount()));
            // }
            if (this.getInvoiceItemDiscountAmount() != null && zero.compareTo(this.getInvoiceItemDiscountAmount()) != 0) {
                returnValue = returnValue.subtract(new KualiDecimal(this.getInvoiceItemDiscountAmount()));
            }
            LOG.debug("getGrandTotalAmount() returning amount " + returnValue.doubleValue());
            return returnValue;
        }
        catch (NumberFormatException n) {
            // do nothing this is already rejected
            LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
            return new KualiDecimal(zero);
        }
    }

    public void setGrandTotalAmount(KualiDecimal amount) {
        // ignore this
    }

    /**
     * @return Returns the invoiceLoadSummaryIdentifier.
     */
    public Integer getInvoiceLoadSummaryIdentifier() {
        return invoiceLoadSummaryIdentifier;
    }

    /**
     * @param invoiceLoadSummaryIdentifier The invoiceLoadSummaryIdentifier to set.
     */
    public void setInvoiceLoadSummaryIdentifier(Integer loadSummaryId) {
        this.invoiceLoadSummaryIdentifier = loadSummaryId;
    }

    public PurchaseOrderDocument getCurrentPurchaseOrderDocument() {
        if (currentPurchaseOrderDocument == null) {
            currentPurchaseOrderDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(this.getPurchaseOrderIdentifier());
        }
        return currentPurchaseOrderDocument;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * @return Returns the invoiceLoadSummary.
     */
    public ElectronicInvoiceLoadSummary getInvoiceLoadSummary() {
        return invoiceLoadSummary;
    }

    /**
     * @param invoiceLoadSummary The invoiceLoadSummary to set.
     */
    public void setInvoiceLoadSummary(ElectronicInvoiceLoadSummary electronicInvoiceLoadSummary) {
        this.invoiceLoadSummary = electronicInvoiceLoadSummary;
        this.invoiceLoadSummaryIdentifier = electronicInvoiceLoadSummary.getInvoiceLoadSummaryIdentifier();
    }

    /**
     * @return Returns the invoiceBillToAddressCityName.
     */
    public String getInvoiceBillToAddressCityName() {
        return invoiceBillToAddressCityName;
    }

    /**
     * @param invoiceBillToAddressCityName The invoiceBillToAddressCityName to set.
     */
    public void setInvoiceBillToAddressCityName(String billToAddressCityName) {
        this.invoiceBillToAddressCityName = billToAddressCityName;
    }

    /**
     * @return Returns the invoiceBillToAddressCountryCode.
     */
    public String getInvoiceBillToAddressCountryCode() {
        return invoiceBillToAddressCountryCode;
    }

    /**
     * @param invoiceBillToAddressCountryCode The invoiceBillToAddressCountryCode to set.
     */
    public void setInvoiceBillToAddressCountryCode(String billToAddressCountryCode) {
        this.invoiceBillToAddressCountryCode = billToAddressCountryCode;
    }

    /**
     * @return Returns the invoiceBillToAddressCountryName.
     */
    public String getInvoiceBillToAddressCountryName() {
        return invoiceBillToAddressCountryName;
    }

    /**
     * @param invoiceBillToAddressCountryName The invoiceBillToAddressCountryName to set.
     */
    public void setInvoiceBillToAddressCountryName(String billToAddressCountryName) {
        this.invoiceBillToAddressCountryName = billToAddressCountryName;
    }

    /**
     * @return Returns the invoiceBillToAddressLine1.
     */
    public String getInvoiceBillToAddressLine1() {
        return invoiceBillToAddressLine1;
    }

    /**
     * @param invoiceBillToAddressLine1 The invoiceBillToAddressLine1 to set.
     */
    public void setInvoiceBillToAddressLine1(String billToAddressLine1) {
        this.invoiceBillToAddressLine1 = billToAddressLine1;
    }

    /**
     * @return Returns the invoiceBillToAddressLine2.
     */
    public String getInvoiceBillToAddressLine2() {
        return invoiceBillToAddressLine2;
    }

    /**
     * @param invoiceBillToAddressLine2 The invoiceBillToAddressLine2 to set.
     */
    public void setInvoiceBillToAddressLine2(String billToAddressLine2) {
        this.invoiceBillToAddressLine2 = billToAddressLine2;
    }

    /**
     * @return Returns the invoiceBillToAddressLine3.
     */
    public String getInvoiceBillToAddressLine3() {
        return invoiceBillToAddressLine3;
    }

    /**
     * @param invoiceBillToAddressLine3 The invoiceBillToAddressLine3 to set.
     */
    public void setInvoiceBillToAddressLine3(String billToAddressLine3) {
        this.invoiceBillToAddressLine3 = billToAddressLine3;
    }

    /**
     * @return Returns the invoiceBillToAddressName.
     */
    public String getInvoiceBillToAddressName() {
        return invoiceBillToAddressName;
    }

    /**
     * @param invoiceBillToAddressName The invoiceBillToAddressName to set.
     */
    public void setInvoiceBillToAddressName(String billToAddressName) {
        this.invoiceBillToAddressName = billToAddressName;
    }

    /**
     * @return Returns the invoiceBillToAddressPostalCode.
     */
    public String getInvoiceBillToAddressPostalCode() {
        return invoiceBillToAddressPostalCode;
    }

    /**
     * @param invoiceBillToAddressPostalCode The invoiceBillToAddressPostalCode to set.
     */
    public void setInvoiceBillToAddressPostalCode(String billToAddressPostalCode) {
        this.invoiceBillToAddressPostalCode = billToAddressPostalCode;
    }

    /**
     * @return Returns the invoiceBillToAddressStateCode.
     */
    public String getInvoiceBillToAddressStateCode() {
        return invoiceBillToAddressStateCode;
    }

    /**
     * @param invoiceBillToAddressStateCode The invoiceBillToAddressStateCode to set.
     */
    public void setInvoiceBillToAddressStateCode(String billToAddressStateCode) {
        this.invoiceBillToAddressStateCode = billToAddressStateCode;
    }

    /**
     * @return Returns the invoiceBillToAddressType.
     */
    public String getInvoiceBillToAddressType() {
        return invoiceBillToAddressType;
    }

    /**
     * @param invoiceBillToAddressType The invoiceBillToAddressType to set.
     */
    public void setInvoiceBillToAddressType(String billToAddressType) {
        this.invoiceBillToAddressType = billToAddressType;
    }

    /**
     * @return Returns the invoiceFileDeploymentModeValue.
     */
    public String getInvoiceFileDeploymentModeValue() {
        return invoiceFileDeploymentModeValue;
    }

    /**
     * @param invoiceFileDeploymentModeValue The invoiceFileDeploymentModeValue to set.
     */
    public void setInvoiceFileDeploymentModeValue(String deploymentMode) {
        this.invoiceFileDeploymentModeValue = deploymentMode;
    }

    /**
     * @return Returns the invoiceRejectItems.
     */
    public List<ElectronicInvoiceRejectItem> getInvoiceRejectItems() {
        return invoiceRejectItems;
    }

    /**
     * @param invoiceRejectItems The invoiceRejectItems to set.
     */
    public void setInvoiceRejectItems(List<ElectronicInvoiceRejectItem> electronicInvoiceRejectItems) {
        this.invoiceRejectItems = electronicInvoiceRejectItems;
    }

    public ElectronicInvoiceRejectItem getInvoiceRejectItem(int index) {
        while (getInvoiceRejectItems().size() <= index) {
            getInvoiceRejectItems().add(new ElectronicInvoiceRejectItem());
        }
        return (ElectronicInvoiceRejectItem) getInvoiceRejectItems().get(index);
    }

    /**
     * @return Returns the invoiceRejectReasons.
     */
    public List<ElectronicInvoiceRejectReason> getInvoiceRejectReasons() {
        return invoiceRejectReasons;
    }

    /**
     * @param invoiceRejectReasons The invoiceRejectReasons to set.
     */
    public void setInvoiceRejectReasons(List<ElectronicInvoiceRejectReason> electronicInvoiceRejectReasons) {
        this.invoiceRejectReasons = electronicInvoiceRejectReasons;
    }

    public ElectronicInvoiceRejectReason getInvoiceRejectReason(int index) {
        while (getInvoiceRejectReasons().size() <= index) {
            getInvoiceRejectReasons().add(new ElectronicInvoiceRejectReason());
        }
        return (ElectronicInvoiceRejectReason) getInvoiceRejectReasons().get(index);
    }

    /**
     * @return Returns the purchaseOrderDeliveryCampusCode.
     */
    public String getPurchaseOrderDeliveryCampusCode() {
        return purchaseOrderDeliveryCampusCode;
    }

    /**
     * @param purchaseOrderDeliveryCampusCode The purchaseOrderDeliveryCampusCode to set.
     */
    public void setPurchaseOrderDeliveryCampusCode(String epicPODeliveryCampusCode) {
        this.purchaseOrderDeliveryCampusCode = epicPODeliveryCampusCode;
    }

    /**
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    /**
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer epicPurchaseOrderId) {
        this.purchaseOrderIdentifier = epicPurchaseOrderId;
    }

    /**
     * @return Returns the invoiceFileDiscountInLineIndicator.
     */
    public Boolean getInvoiceFileDiscountInLineIndicator() {
        return invoiceFileDiscountInLineIndicator;
    }

    public Boolean isInvoiceFileDiscountInLineIndicator() {
        return invoiceFileDiscountInLineIndicator;
    }

    /**
     * @param invoiceFileDiscountInLineIndicator The invoiceFileDiscountInLineIndicator to set.
     */
    public void setInvoiceFileDiscountInLineIndicator(Boolean fileDiscountInLineIndicator) {
        this.invoiceFileDiscountInLineIndicator = fileDiscountInLineIndicator;
    }

    /**
     * @return Returns the invoiceFileHeaderTypeIndicator.
     */
    public Boolean getInvoiceFileHeaderTypeIndicator() {
        return invoiceFileHeaderTypeIndicator;
    }

    public Boolean isInvoiceFileHeaderTypeIndicator() {
        return invoiceFileHeaderTypeIndicator;
    }

    /**
     * @param invoiceFileHeaderTypeIndicator The invoiceFileHeaderTypeIndicator to set.
     */
    public void setInvoiceFileHeaderTypeIndicator(Boolean fileHeaderTypeIndicator) {
        this.invoiceFileHeaderTypeIndicator = fileHeaderTypeIndicator;
    }

    /**
     * @return Returns the invoiceFileInformationOnlyIndicator.
     */
    public Boolean getInvoiceFileInformationOnlyIndicator() {
        return invoiceFileInformationOnlyIndicator;
    }

    public Boolean isInvoiceFileInformationOnlyIndicator() {
        return invoiceFileInformationOnlyIndicator;
    }

    /**
     * @param invoiceFileInformationOnlyIndicator The invoiceFileInformationOnlyIndicator to set.
     */
    public void setInvoiceFileInformationOnlyIndicator(Boolean fileInformationOnlyIndicator) {
        this.invoiceFileInformationOnlyIndicator = fileInformationOnlyIndicator;
    }

    /**
     * @return Returns the invoiceFileOperationIdentifier.
     */
    public String getInvoiceFileOperationIdentifier() {
        return invoiceFileOperationIdentifier;
    }

    /**
     * @param invoiceFileOperationIdentifier The invoiceFileOperationIdentifier to set.
     */
    public void setInvoiceFileOperationIdentifier(String fileOperationId) {
        this.invoiceFileOperationIdentifier = fileOperationId;
    }

    /**
     * @return Returns the invoiceFilePurposeIdentifier.
     */
    public String getInvoiceFilePurposeIdentifier() {
        return invoiceFilePurposeIdentifier;
    }

    /**
     * @param invoiceFilePurposeIdentifier The invoiceFilePurposeIdentifier to set.
     */
    public void setInvoiceFilePurposeIdentifier(String filePurposeId) {
        this.invoiceFilePurposeIdentifier = filePurposeId;
    }

    /**
     * @return Returns the invoiceFileShippingInLineIndicator.
     */
    public Boolean getInvoiceFileShippingInLineIndicator() {
        return invoiceFileShippingInLineIndicator;
    }

    public Boolean isInvoiceFileShippingInLineIndicator() {
        return invoiceFileShippingInLineIndicator;
    }

    /**
     * @param invoiceFileShippingInLineIndicator The invoiceFileShippingInLineIndicator to set.
     */
    public void setInvoiceFileShippingInLineIndicator(Boolean fileShippingInLineIndicator) {
        this.invoiceFileShippingInLineIndicator = fileShippingInLineIndicator;
    }

    /**
     * @return Returns the invoiceFileSpecialHandlingInLineIndicator.
     */
    public Boolean getInvoiceFileSpecialHandlingInLineIndicator() {
        return invoiceFileSpecialHandlingInLineIndicator;
    }

    public Boolean isInvoiceFileSpecialHandlingInLineIndicator() {
        return invoiceFileSpecialHandlingInLineIndicator;
    }

    /**
     * @param invoiceFileSpecialHandlingInLineIndicator The invoiceFileSpecialHandlingInLineIndicator to set.
     */
    public void setInvoiceFileSpecialHandlingInLineIndicator(Boolean fileSpecHandlingInLineIndicator) {
        this.invoiceFileSpecialHandlingInLineIndicator = fileSpecHandlingInLineIndicator;
    }

    /**
     * @return Returns the invoiceFileTaxInLineIndicator.
     */
    public Boolean getInvoiceFileTaxInLineIndicator() {
        return invoiceFileTaxInLineIndicator;
    }

    public Boolean isInvoiceFileTaxInLineIndicator() {
        return invoiceFileTaxInLineIndicator;
    }

    /**
     * @param invoiceFileTaxInLineIndicator The invoiceFileTaxInLineIndicator to set.
     */
    public void setInvoiceFileTaxInLineIndicator(Boolean fileTaxInLineIndicator) {
        this.invoiceFileTaxInLineIndicator = fileTaxInLineIndicator;
    }

    /**
     * @return Returns the purapDocumentIdentifier.
     */
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    /**
     * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
     */
    public void setPurapDocumentIdentifier(Integer id) {
        this.purapDocumentIdentifier = id;
    }

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    /**
     * @return Returns the invoiceCustomerNumber.
     */
    public String getInvoiceCustomerNumber() {
        return invoiceCustomerNumber;
    }

    /**
     * @param invoiceCustomerNumber The invoiceCustomerNumber to set.
     */
    public void setInvoiceCustomerNumber(String invoiceCustomerNumber) {
        this.invoiceCustomerNumber = invoiceCustomerNumber;
    }

    /**
     * @return Returns the invoiceFileDate.
     */
    public String getInvoiceFileDate() {
        return invoiceFileDate;
    }

    /**
     * @param invoiceFileDate The invoiceFileDate to set.
     */
    public void setInvoiceFileDate(String invoiceDate) {
        this.invoiceFileDate = invoiceDate;
    }

    /**
     * @return Returns the invoiceItemDiscountAmount.
     */
    public BigDecimal getInvoiceItemDiscountAmount() {
        if (this.isInvoiceFileDiscountInLineIndicator()) {
            BigDecimal returnValue = zero;
            try {
                for (ElectronicInvoiceRejectItem eiri : this.invoiceRejectItems) {
                    BigDecimal toAddAmount = eiri.getInvoiceItemDiscountAmount();
                    LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                    returnValue = returnValue.add(toAddAmount);
                }
                LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());
                return returnValue;
            }
            catch (NumberFormatException n) {
                // do nothing this is already rejected
                LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
                return zero;
            }
        }
        else {
            return invoiceItemDiscountAmount;
        }
    }

    /**
     * @param invoiceItemDiscountAmount The invoiceItemDiscountAmount to set.
     */
    public void setInvoiceItemDiscountAmount(BigDecimal invoiceDiscountAmount) {
        this.invoiceItemDiscountAmount = invoiceDiscountAmount;
    }

    /**
     * @return Returns the invoiceItemDiscountCurrencyCode.
     */
    public String getInvoiceItemDiscountCurrencyCode() {
        return invoiceItemDiscountCurrencyCode;
    }

    /**
     * @param invoiceItemDiscountCurrencyCode The invoiceItemDiscountCurrencyCode to set.
     */
    public void setInvoiceItemDiscountCurrencyCode(String invoiceDiscountAmountCurrency) {
        this.invoiceItemDiscountCurrencyCode = invoiceDiscountAmountCurrency;
    }

    /**
     * @return Returns the invoiceFileName.
     */
    public String getInvoiceFileName() {
        return invoiceFileName;
    }

    /**
     * @param invoiceFileName The invoiceFileName to set.
     */
    public void setInvoiceFileName(String invoiceFileName) {
        this.invoiceFileName = invoiceFileName;
    }

    /**
     * @return Returns the invoiceItemGrossAmount.
     */
    public BigDecimal getInvoiceItemGrossAmount() {
        return invoiceItemGrossAmount;
    }

    /**
     * @param invoiceItemGrossAmount The invoiceItemGrossAmount to set.
     */
    public void setInvoiceItemGrossAmount(BigDecimal invoiceGrossAmount) {
        this.invoiceItemGrossAmount = invoiceGrossAmount;
    }

    /**
     * @return Returns the invoiceItemGrossCurrencyCode.
     */
    public String getInvoiceItemGrossCurrencyCode() {
        return invoiceItemGrossCurrencyCode;
    }

    /**
     * @param invoiceItemGrossCurrencyCode The invoiceItemGrossCurrencyCode to set.
     */
    public void setInvoiceItemGrossCurrencyCode(String invoiceGrossAmountCurrency) {
        this.invoiceItemGrossCurrencyCode = invoiceGrossAmountCurrency;
    }

    /**
     * @return Returns the invoiceItemNetAmount.
     */
    public BigDecimal getInvoiceItemNetAmount() {
        return invoiceItemNetAmount;
    }

    /**
     * @param invoiceItemNetAmount The invoiceItemNetAmount to set.
     */
    public void setInvoiceItemNetAmount(BigDecimal invoiceNetAmount) {
        this.invoiceItemNetAmount = invoiceNetAmount;
    }

    public boolean isInvoiceNumberAcceptIndicator() {
        return invoiceNumberAcceptIndicator;
    }

    public void setInvoiceNumberAcceptIndicator(boolean invoiceNumberAcceptIndicator) {
        this.invoiceNumberAcceptIndicator = invoiceNumberAcceptIndicator;
    }

    public boolean isInvoiceResearchIndicator() {
        return invoiceResearchIndicator;
    }

    public boolean getInvoiceResearchIndicator() {
        return invoiceResearchIndicator;
    }

    public void setInvoiceResearchIndicator(boolean invoiceResearchIndicator) {
        this.invoiceResearchIndicator = invoiceResearchIndicator;
    }

    public Date getAccountsPayableApprovalDate() {
        return accountsPayableApprovalDate;
    }

    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
    }

    /**
     * @return Returns the invoiceItemNetCurrencyCode.
     */
    public String getInvoiceItemNetCurrencyCode() {
        return invoiceItemNetCurrencyCode;
    }

    /**
     * @param invoiceItemNetCurrencyCode The invoiceItemNetCurrencyCode to set.
     */
    public void setInvoiceItemNetCurrencyCode(String invoiceNetAmountCurrency) {
        this.invoiceItemNetCurrencyCode = invoiceNetAmountCurrency;
    }

    /**
     * @return Returns the invoiceFileNumber.
     */
    public String getInvoiceFileNumber() {
        return invoiceFileNumber;
    }

    /**
     * @param invoiceFileNumber The invoiceFileNumber to set.
     */
    public void setInvoiceFileNumber(String invoiceNumber) {
        this.invoiceFileNumber = invoiceNumber;
    }

    /**
     * @return Returns the invoiceOrderPurchaseOrderDate.
     */
    public String getInvoiceOrderPurchaseOrderDate() {
        return invoiceOrderPurchaseOrderDate;
    }

    /**
     * @param invoiceOrderPurchaseOrderDate The invoiceOrderPurchaseOrderDate to set.
     */
    public void setInvoiceOrderPurchaseOrderDate(String invoiceOrderDate) {
        this.invoiceOrderPurchaseOrderDate = invoiceOrderDate;
    }

    /**
     * @return Returns the invoiceOrderPurchaseOrderIdentifier.
     */
    public String getInvoiceOrderPurchaseOrderIdentifier() {
        return invoiceOrderPurchaseOrderIdentifier;
    }

    /**
     * @param invoiceOrderPurchaseOrderIdentifier The invoiceOrderPurchaseOrderIdentifier to set.
     */
    public void setInvoiceOrderPurchaseOrderIdentifier(String invoiceOrderId) {
        this.invoiceOrderPurchaseOrderIdentifier = invoiceOrderId;
    }

    /**
     * @return Returns the invoiceProcessDate.
     */
    public Date getInvoiceProcessDate() {
        return invoiceProcessDate;
    }

    /**
     * @param invoiceProcessDate The invoiceProcessDate to set.
     */
    public void setInvoiceProcessDate(Date invoiceProcessTimestamp) {
        this.invoiceProcessDate = invoiceProcessTimestamp;
    }

    /**
     * @return Returns the invoicePurchaseOrderNumber.
     */
    public String getInvoicePurchaseOrderNumber() {
        return invoicePurchaseOrderNumber;
    }

    /**
     * @param invoicePurchaseOrderNumber The invoicePurchaseOrderNumber to set.
     */
    public void setInvoicePurchaseOrderNumber(String invoicePurchaseOrderId) {
        this.invoicePurchaseOrderNumber = invoicePurchaseOrderId;
    }

    /**
     * @return Returns the invoiceShipDate.
     */
    public String getInvoiceShipDate() {
        return invoiceShipDate;
    }

    /**
     * @param invoiceShipDate The invoiceShipDate to set.
     */
    public void setInvoiceShipDate(String invoiceShipDate) {
        this.invoiceShipDate = invoiceShipDate;
    }

    /**
     * @return Returns the invoiceItemShippingAmount.
     */
    public BigDecimal getInvoiceItemShippingAmount() {
        if (this.isInvoiceFileShippingInLineIndicator()) {
            BigDecimal returnValue = zero;
            try {
                for (ElectronicInvoiceRejectItem eiri : this.invoiceRejectItems) {
                    BigDecimal toAddAmount = eiri.getInvoiceItemShippingAmount();
                    LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                    returnValue = returnValue.add(toAddAmount);
                }
                LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());
                return returnValue;
            }
            catch (NumberFormatException n) {
                // do nothing this is already rejected
                LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
                return zero;
            }
        }
        else {
            return invoiceItemShippingAmount;
        }
    }

    /**
     * @param invoiceItemShippingAmount The invoiceItemShippingAmount to set.
     */
    public void setInvoiceItemShippingAmount(BigDecimal invoiceShippingAmount) {
        this.invoiceItemShippingAmount = invoiceShippingAmount;
    }

    /**
     * @return Returns the invoiceItemShippingCurrencyCode.
     */
    public String getInvoiceItemShippingCurrencyCode() {
        return invoiceItemShippingCurrencyCode;
    }

    /**
     * @param invoiceItemShippingCurrencyCode The invoiceItemShippingCurrencyCode to set.
     */
    public void setInvoiceItemShippingCurrencyCode(String invoiceShippingAmountCurrency) {
        this.invoiceItemShippingCurrencyCode = invoiceShippingAmountCurrency;
    }

    /**
     * @return Returns the invoiceItemShippingDescription.
     */
    public String getInvoiceItemShippingDescription() {
        return invoiceItemShippingDescription;
    }

    /**
     * @param invoiceItemShippingDescription The invoiceItemShippingDescription to set.
     */
    public void setInvoiceItemShippingDescription(String invoiceShippingDescription) {
        this.invoiceItemShippingDescription = invoiceShippingDescription;
    }

    /**
     * @return Returns the invoiceItemSpecialHandlingAmount.
     */
    public BigDecimal getInvoiceItemSpecialHandlingAmount() {
        if (this.isInvoiceFileSpecialHandlingInLineIndicator()) {
            BigDecimal returnValue = zero;
            try {
                for (ElectronicInvoiceRejectItem eiri : this.invoiceRejectItems) {
                    BigDecimal toAddAmount = eiri.getInvoiceItemSpecialHandlingAmount();
                    LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                    returnValue = returnValue.add(toAddAmount);
                }
                LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());
                return returnValue;
            }
            catch (NumberFormatException n) {
                // do nothing this is already rejected
                LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
                return zero;
            }
        }
        else {
            return invoiceItemSpecialHandlingAmount;
        }
    }

    /**
     * @param invoiceItemSpecialHandlingAmount The invoiceItemSpecialHandlingAmount to set.
     */
    public void setInvoiceItemSpecialHandlingAmount(BigDecimal invoiceSpecialHandlingAmount) {
        this.invoiceItemSpecialHandlingAmount = invoiceSpecialHandlingAmount;
    }

    /**
     * @return Returns the invoiceItemSpecialHandlingCurrencyCode.
     */
    public String getInvoiceItemSpecialHandlingCurrencyCode() {
        return invoiceItemSpecialHandlingCurrencyCode;
    }

    /**
     * @param invoiceItemSpecialHandlingCurrencyCode The invoiceItemSpecialHandlingCurrencyCode to set.
     */
    public void setInvoiceItemSpecialHandlingCurrencyCode(String invoiceSpecialHandlingAmountCurrency) {
        this.invoiceItemSpecialHandlingCurrencyCode = invoiceSpecialHandlingAmountCurrency;
    }

    /**
     * @return the invoiceItemSpecialHandlingDescription
     */
    public String getInvoiceItemSpecialHandlingDescription() {
        return invoiceItemSpecialHandlingDescription;
    }

    /**
     * @param invoiceItemSpecialHandlingDescription the invoiceItemSpecialHandlingDescription to set
     */
    public void setInvoiceItemSpecialHandlingDescription(String invoiceSpecialHandlingDescription) {
        this.invoiceItemSpecialHandlingDescription = invoiceSpecialHandlingDescription;
    }

    /**
     * @return Returns the invoiceItemSubTotalAmount.
     */
    public BigDecimal getInvoiceItemSubTotalAmount() {

        return invoiceItemSubTotalAmount;
    }

    /**
     * @param invoiceItemSubTotalAmount The invoiceItemSubTotalAmount to set.
     */
    public void setInvoiceItemSubTotalAmount(BigDecimal invoiceSubTotalAmount) {
        this.invoiceItemSubTotalAmount = invoiceSubTotalAmount;
    }

    /**
     * @return Returns the invoiceItemSubTotalCurrencyCode.
     */
    public String getInvoiceItemSubTotalCurrencyCode() {
        return invoiceItemSubTotalCurrencyCode;
    }

    /**
     * @param invoiceItemSubTotalCurrencyCode The invoiceItemSubTotalCurrencyCode to set.
     */
    public void setInvoiceItemSubTotalCurrencyCode(String invoiceSubTotalAmountCurrency) {
        this.invoiceItemSubTotalCurrencyCode = invoiceSubTotalAmountCurrency;
    }

    /**
     * @return Returns the invoiceItemTaxAmount.
     */
    public BigDecimal getInvoiceItemTaxAmount() {
        BigDecimal returnValue = zero;
        try {
            for (ElectronicInvoiceRejectItem eiri : this.invoiceRejectItems) {
                BigDecimal toAddAmount = eiri.getInvoiceItemTaxAmount();
                LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                returnValue = returnValue.add(toAddAmount);
            }
            LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());
            return returnValue;
        }
        catch (NumberFormatException n) {
            // do nothing this is already rejected
            LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
            return zero;
        }
        // return invoiceItemTaxAmount;
    }

    /**
     * @param invoiceItemTaxAmount The invoiceItemTaxAmount to set.
     */
    public void setInvoiceItemTaxAmount(BigDecimal invoiceTaxAmount) {
        this.invoiceItemTaxAmount = invoiceTaxAmount;
    }

    /**
     * @return Returns the invoiceItemTaxCurrencyCode.
     */
    public String getInvoiceItemTaxCurrencyCode() {
        return invoiceItemTaxCurrencyCode;
    }

    /**
     * @param invoiceItemTaxCurrencyCode The invoiceItemTaxCurrencyCode to set.
     */
    public void setInvoiceItemTaxCurrencyCode(String invoiceTaxAmountCurrency) {
        this.invoiceItemTaxCurrencyCode = invoiceTaxAmountCurrency;
    }

    /**
     * @return Returns the invoiceItemTaxDescription.
     */
    public String getInvoiceItemTaxDescription() {
        return invoiceItemTaxDescription;
    }

    /**
     * @param invoiceItemTaxDescription The invoiceItemTaxDescription to set.
     */
    public void setInvoiceItemTaxDescription(String invoiceTaxDescription) {
        this.invoiceItemTaxDescription = invoiceTaxDescription;
    }

    /**
     * @return Returns the invoiceOrderMasterAgreementInformationDate.
     */
    public String getInvoiceOrderMasterAgreementInformationDate() {
        return invoiceOrderMasterAgreementInformationDate;
    }

    /**
     * @param invoiceOrderMasterAgreementInformationDate The invoiceOrderMasterAgreementInformationDate to set.
     */
    public void setInvoiceOrderMasterAgreementInformationDate(String masterAgreementInfoDate) {
        this.invoiceOrderMasterAgreementInformationDate = masterAgreementInfoDate;
    }

    /**
     * @return Returns the invoiceOrderMasterAgreementInformationIdentifier.
     */
    public String getInvoiceOrderMasterAgreementInformationIdentifier() {
        return invoiceOrderMasterAgreementInformationIdentifier;
    }

    /**
     * @param invoiceOrderMasterAgreementInformationIdentifier The invoiceOrderMasterAgreementInformationIdentifier to set.
     */
    public void setInvoiceOrderMasterAgreementInformationIdentifier(String masterAgreementInfoId) {
        this.invoiceOrderMasterAgreementInformationIdentifier = masterAgreementInfoId;
    }

    /**
     * @return Returns the invoiceOrderMasterAgreementReferenceDate.
     */
    public String getInvoiceOrderMasterAgreementReferenceDate() {
        return invoiceOrderMasterAgreementReferenceDate;
    }

    /**
     * @param invoiceOrderMasterAgreementReferenceDate The invoiceOrderMasterAgreementReferenceDate to set.
     */
    public void setInvoiceOrderMasterAgreementReferenceDate(String masterAgreementReferenceDate) {
        this.invoiceOrderMasterAgreementReferenceDate = masterAgreementReferenceDate;
    }

    /**
     * @return Returns the invoiceOrderMasterAgreementReferenceIdentifier.
     */
    public String getInvoiceOrderMasterAgreementReferenceIdentifier() {
        return invoiceOrderMasterAgreementReferenceIdentifier;
    }

    /**
     * @param invoiceOrderMasterAgreementReferenceIdentifier The invoiceOrderMasterAgreementReferenceIdentifier to set.
     */
    public void setInvoiceOrderMasterAgreementReferenceIdentifier(String masterAgreementReferenceId) {
        this.invoiceOrderMasterAgreementReferenceIdentifier = masterAgreementReferenceId;
    }

    /**
     * @return Returns the invoiceOrderReferenceDocumentReferencePayloadIdentifier.
     */
    public String getInvoiceOrderReferenceDocumentReferencePayloadIdentifier() {
        return invoiceOrderReferenceDocumentReferencePayloadIdentifier;
    }

    /**
     * @param invoiceOrderReferenceDocumentReferencePayloadIdentifier The invoiceOrderReferenceDocumentReferencePayloadIdentifier to
     *        set.
     */
    public void setInvoiceOrderReferenceDocumentReferencePayloadIdentifier(String referenceDocumentRefPayloadId) {
        this.invoiceOrderReferenceDocumentReferencePayloadIdentifier = referenceDocumentRefPayloadId;
    }

    /**
     * @return Returns the invoiceOrderReferenceDocumentReferenceText.
     */
    public String getInvoiceOrderReferenceDocumentReferenceText() {
        return invoiceOrderReferenceDocumentReferenceText;
    }

    /**
     * @param invoiceOrderReferenceDocumentReferenceText The invoiceOrderReferenceDocumentReferenceText to set.
     */
    public void setInvoiceOrderReferenceDocumentReferenceText(String referenceDocumentRefText) {
        this.invoiceOrderReferenceDocumentReferenceText = referenceDocumentRefText;
    }

    /**
     * @return Returns the invoiceOrderReferenceOrderIdentifier.
     */
    public String getInvoiceOrderReferenceOrderIdentifier() {
        return invoiceOrderReferenceOrderIdentifier;
    }

    /**
     * @param invoiceOrderReferenceOrderIdentifier The invoiceOrderReferenceOrderIdentifier to set.
     */
    public void setInvoiceOrderReferenceOrderIdentifier(String referenceOrderId) {
        this.invoiceOrderReferenceOrderIdentifier = referenceOrderId;
    }

    /**
     * @return Returns the invoiceRemitToAddressCityName.
     */
    public String getInvoiceRemitToAddressCityName() {
        return invoiceRemitToAddressCityName;
    }

    /**
     * @param invoiceRemitToAddressCityName The invoiceRemitToAddressCityName to set.
     */
    public void setInvoiceRemitToAddressCityName(String remitToAddressCityName) {
        this.invoiceRemitToAddressCityName = remitToAddressCityName;
    }

    /**
     * @return Returns the invoiceRemitToAddressCountryCode.
     */
    public String getInvoiceRemitToAddressCountryCode() {
        return invoiceRemitToAddressCountryCode;
    }

    /**
     * @param invoiceRemitToAddressCountryCode The invoiceRemitToAddressCountryCode to set.
     */
    public void setInvoiceRemitToAddressCountryCode(String remitToAddressCountryCode) {
        this.invoiceRemitToAddressCountryCode = remitToAddressCountryCode;
    }

    /**
     * @return Returns the invoiceRemitToAddressCountryName.
     */
    public String getInvoiceRemitToAddressCountryName() {
        return invoiceRemitToAddressCountryName;
    }

    /**
     * @param invoiceRemitToAddressCountryName The invoiceRemitToAddressCountryName to set.
     */
    public void setInvoiceRemitToAddressCountryName(String remitToAddressCountryName) {
        this.invoiceRemitToAddressCountryName = remitToAddressCountryName;
    }

    /**
     * @return Returns the invoiceRemitToAddressLine1.
     */
    public String getInvoiceRemitToAddressLine1() {
        return invoiceRemitToAddressLine1;
    }

    /**
     * @param invoiceRemitToAddressLine1 The invoiceRemitToAddressLine1 to set.
     */
    public void setInvoiceRemitToAddressLine1(String remitToAddressLine1) {
        this.invoiceRemitToAddressLine1 = remitToAddressLine1;
    }

    /**
     * @return Returns the invoiceRemitToAddressLine2.
     */
    public String getInvoiceRemitToAddressLine2() {
        return invoiceRemitToAddressLine2;
    }

    /**
     * @param invoiceRemitToAddressLine2 The invoiceRemitToAddressLine2 to set.
     */
    public void setInvoiceRemitToAddressLine2(String remitToAddressLine2) {
        this.invoiceRemitToAddressLine2 = remitToAddressLine2;
    }

    /**
     * @return Returns the invoiceRemitToAddressLine3.
     */
    public String getInvoiceRemitToAddressLine3() {
        return invoiceRemitToAddressLine3;
    }

    /**
     * @param invoiceRemitToAddressLine3 The invoiceRemitToAddressLine3 to set.
     */
    public void setInvoiceRemitToAddressLine3(String remitToAddressLine3) {
        this.invoiceRemitToAddressLine3 = remitToAddressLine3;
    }

    /**
     * @return Returns the invoiceRemitToAddressName.
     */
    public String getInvoiceRemitToAddressName() {
        return invoiceRemitToAddressName;
    }

    /**
     * @param invoiceRemitToAddressName The invoiceRemitToAddressName to set.
     */
    public void setInvoiceRemitToAddressName(String remitToAddressName) {
        this.invoiceRemitToAddressName = remitToAddressName;
    }

    /**
     * @return Returns the invoiceRemitToAddressPostalCode.
     */
    public String getInvoiceRemitToAddressPostalCode() {
        return invoiceRemitToAddressPostalCode;
    }

    /**
     * @param invoiceRemitToAddressPostalCode The invoiceRemitToAddressPostalCode to set.
     */
    public void setInvoiceRemitToAddressPostalCode(String remitToAddressPostalCode) {
        this.invoiceRemitToAddressPostalCode = remitToAddressPostalCode;
    }

    /**
     * @return Returns the invoiceRemitToAddressStateCode.
     */
    public String getInvoiceRemitToAddressStateCode() {
        return invoiceRemitToAddressStateCode;
    }

    /**
     * @param invoiceRemitToAddressStateCode The invoiceRemitToAddressStateCode to set.
     */
    public void setInvoiceRemitToAddressStateCode(String remitToAddressStateCode) {
        this.invoiceRemitToAddressStateCode = remitToAddressStateCode;
    }

    /**
     * @return Returns the invoiceRemitToAddressType.
     */
    public String getInvoiceRemitToAddressType() {
        return invoiceRemitToAddressType;
    }

    /**
     * @param invoiceRemitToAddressType The invoiceRemitToAddressType to set.
     */
    public void setInvoiceRemitToAddressType(String remitToAddressType) {
        this.invoiceRemitToAddressType = remitToAddressType;
    }

    /**
     * @return Returns the invoiceShipToAddressCityName.
     */
    public String getInvoiceShipToAddressCityName() {
        return invoiceShipToAddressCityName;
    }

    /**
     * @param invoiceShipToAddressCityName The invoiceShipToAddressCityName to set.
     */
    public void setInvoiceShipToAddressCityName(String shipToAddressCityName) {
        this.invoiceShipToAddressCityName = shipToAddressCityName;
    }

    /**
     * @return Returns the invoiceShipToAddressCountryCode.
     */
    public String getInvoiceShipToAddressCountryCode() {
        return invoiceShipToAddressCountryCode;
    }

    /**
     * @param invoiceShipToAddressCountryCode The invoiceShipToAddressCountryCode to set.
     */
    public void setInvoiceShipToAddressCountryCode(String shipToAddressCountryCode) {
        this.invoiceShipToAddressCountryCode = shipToAddressCountryCode;
    }

    /**
     * @return Returns the invoiceShipToAddressCountryName.
     */
    public String getInvoiceShipToAddressCountryName() {
        return invoiceShipToAddressCountryName;
    }

    /**
     * @param invoiceShipToAddressCountryName The invoiceShipToAddressCountryName to set.
     */
    public void setInvoiceShipToAddressCountryName(String shipToAddressCountryName) {
        this.invoiceShipToAddressCountryName = shipToAddressCountryName;
    }

    /**
     * @return Returns the invoiceShipToAddressLine1.
     */
    public String getInvoiceShipToAddressLine1() {
        return invoiceShipToAddressLine1;
    }

    /**
     * @param invoiceShipToAddressLine1 The invoiceShipToAddressLine1 to set.
     */
    public void setInvoiceShipToAddressLine1(String shipToAddressLine1) {
        this.invoiceShipToAddressLine1 = shipToAddressLine1;
    }

    /**
     * @return Returns the invoiceShipToAddressLine2.
     */
    public String getInvoiceShipToAddressLine2() {
        return invoiceShipToAddressLine2;
    }

    /**
     * @param invoiceShipToAddressLine2 The invoiceShipToAddressLine2 to set.
     */
    public void setInvoiceShipToAddressLine2(String shipToAddressLine2) {
        this.invoiceShipToAddressLine2 = shipToAddressLine2;
    }

    /**
     * @return Returns the invoiceShipToAddressLine3.
     */
    public String getInvoiceShipToAddressLine3() {
        return invoiceShipToAddressLine3;
    }

    /**
     * @param invoiceShipToAddressLine3 The invoiceShipToAddressLine3 to set.
     */
    public void setInvoiceShipToAddressLine3(String shipToAddressLine3) {
        this.invoiceShipToAddressLine3 = shipToAddressLine3;
    }

    /**
     * @return Returns the invoiceShipToAddressName.
     */
    public String getInvoiceShipToAddressName() {
        return invoiceShipToAddressName;
    }

    /**
     * @param invoiceShipToAddressName The invoiceShipToAddressName to set.
     */
    public void setInvoiceShipToAddressName(String shipToAddressName) {
        this.invoiceShipToAddressName = shipToAddressName;
    }

    /**
     * @return Returns the invoiceShipToAddressPostalCode.
     */
    public String getInvoiceShipToAddressPostalCode() {
        return invoiceShipToAddressPostalCode;
    }

    /**
     * @param invoiceShipToAddressPostalCode The invoiceShipToAddressPostalCode to set.
     */
    public void setInvoiceShipToAddressPostalCode(String shipToAddressPostalCode) {
        this.invoiceShipToAddressPostalCode = shipToAddressPostalCode;
    }

    /**
     * @return Returns the invoiceShipToAddressStateCode.
     */
    public String getInvoiceShipToAddressStateCode() {
        return invoiceShipToAddressStateCode;
    }

    /**
     * @param invoiceShipToAddressStateCode The invoiceShipToAddressStateCode to set.
     */
    public void setInvoiceShipToAddressStateCode(String shipToAddressStateCode) {
        this.invoiceShipToAddressStateCode = shipToAddressStateCode;
    }

    /**
     * @return Returns the invoiceShipToAddressType.
     */
    public String getInvoiceShipToAddressType() {
        return invoiceShipToAddressType;
    }

    /**
     * @param invoiceShipToAddressType The invoiceShipToAddressType to set.
     */
    public void setInvoiceShipToAddressType(String shipToAddressType) {
        this.invoiceShipToAddressType = shipToAddressType;
    }

    /**
     * @return Returns the invoiceOrderSupplierOrderInformationIdentifier.
     */
    public String getInvoiceOrderSupplierOrderInformationIdentifier() {
        return invoiceOrderSupplierOrderInformationIdentifier;
    }

    /**
     * @param invoiceOrderSupplierOrderInformationIdentifier The invoiceOrderSupplierOrderInformationIdentifier to set.
     */
    public void setInvoiceOrderSupplierOrderInformationIdentifier(String supplierOrderInfoId) {
        this.invoiceOrderSupplierOrderInformationIdentifier = supplierOrderInfoId;
    }

    /**
     * @return Returns the vendorDetailAssignedIdentifier.
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailID) {
        this.vendorDetailAssignedIdentifier = vendorDetailID;
    }

    /**
     * @return Returns the vendorDunsNumber.
     */
    public String getVendorDunsNumber() {
        return vendorDunsNumber;
    }

    /**
     * @param vendorDunsNumber The vendorDunsNumber to set.
     */
    public void setVendorDunsNumber(String vendorDunsNumber) {
        this.vendorDunsNumber = vendorDunsNumber;
    }

    /**
     * @return Returns the vendorHeaderGeneratedIdentifier.
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderID) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderID;
    }

    public void addRejectItem(ElectronicInvoiceRejectItem item) {
        // int itemLinePosition = getItemLinePosition();
        // if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <=
        // itemLinePosition)) {
        // itemLinePosition = item.getItemLineNumber().intValue() - 1;
        // }

        item.setPurapDocumentIdentifier(this.purapDocumentIdentifier);
        item.setElectronicInvoiceRejectDocument(this);

        invoiceRejectItems.add(item);
        // renumberItems(itemLinePosition);
    }

    public void addRejectReason(ElectronicInvoiceRejectReason reason) {
        // int itemLinePosition = getItemLinePosition();
        // if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <=
        // itemLinePosition)) {
        // itemLinePosition = item.getItemLineNumber().intValue() - 1;
        // }

        reason.setPurapDocumentIdentifier(this.purapDocumentIdentifier);
        reason.setElectronicInvoiceRejectDocument(this);

        invoiceRejectReasons.add(reason);
        // renumberItems(itemLinePosition);
    }

    public PurApRelatedViews getRelatedViews() {
        if (relatedViews == null) {
            relatedViews = new PurApRelatedViews(this.documentNumber, this.accountsPayablePurchasingDocumentLinkIdentifier);
        }
        return relatedViews;
    }

    public void setRelatedViews(PurApRelatedViews relatedViews) {
        this.relatedViews = relatedViews;
    }
    
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

}
/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers, Cornell University, Trustees of
 * Indiana University, Michigan State University Board of Trustees, Trustees of San Joaquin Delta College, University of Hawai'i,
 * The Arizona Board of Regents on behalf of the University of Arizona, and the rsmart group. Licensed under the Educational
 * Community License Version 1.0 (the "License"); By obtaining, using and/or copying this Original Work, you agree that you have
 * read, understand, and will comply with the terms and conditions of the Educational Community License. You may obtain a copy of
 * the License at: http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
