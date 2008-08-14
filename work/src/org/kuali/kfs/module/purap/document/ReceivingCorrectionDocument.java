package org.kuali.kfs.module.purap.document;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.Carrier;
import org.kuali.kfs.module.purap.businessobject.DeliveryRequiredDateReason;
import org.kuali.kfs.module.purap.businessobject.ReceivingCorrectionItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingLineItem;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.businessobject.Country;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingCorrectionDocument extends ReceivingDocumentBase {

    private String receivingLineDocumentNumber;
    //Collections
    private List<ReceivingCorrectionItem> items;
    
    private ReceivingLineDocument receivingLineDocument;
    
    /**
     * Default constructor.
     */
    public ReceivingCorrectionDocument() {
        super();
        items = new TypedArrayList(getItemClass());
    }

    public void populateReceivingCorrectionFromReceivingLine(ReceivingLineDocument rlDoc){
        
        //populate receiving line document from purchase order
        this.setPurchaseOrderIdentifier( rlDoc.getPurchaseOrderIdentifier() );
        this.getDocumentHeader().setDocumentDescription( rlDoc.getDocumentHeader().getDocumentDescription());
        this.getDocumentHeader().setOrganizationDocumentNumber( rlDoc.getDocumentHeader().getOrganizationDocumentNumber() );
        this.setAccountsPayablePurchasingDocumentLinkIdentifier( rlDoc.getAccountsPayablePurchasingDocumentLinkIdentifier() );        
        this.setReceivingLineDocumentNumber(rlDoc.getDocumentNumber());
        
        //copy receiving line items
        for (ReceivingLineItem rli : (List<ReceivingLineItem>) rlDoc.getItems()) {
            this.getItems().add(new ReceivingCorrectionItem(rli, this));            
        }
        
    }
    
    @Override
    public void handleRouteStatusChange() {
        if(this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(ReceivingService.class).completeReceivingCorrectionDocument(this);
        }
        super.handleRouteStatusChange();
    }
    
    /**
     * Gets the receivingLineDocumentNumber attribute.
     * 
     * @return Returns the receivingLineDocumentNumber
     * 
     */
    public String getReceivingLineDocumentNumber() { 
        return receivingLineDocumentNumber;
    }
    
    /**
     * Sets the receivingLineDocumentNumber attribute.
     * 
     * @param receivingLineDocumentNumber The receivingLineDocumentNumber to set.
     * 
     */
    public void setReceivingLineDocumentNumber(String receivingLineDocumentNumber) {
        this.receivingLineDocumentNumber = receivingLineDocumentNumber;
    }

    /**
     * Gets the receivingLineDocument attribute. 
     * @return Returns the receivingLineDocument.
     */
    public ReceivingLineDocument getReceivingLineDocument() {
        if(receivingLineDocument == null){
            this.refreshReferenceObject("receivingLineDocument");
        }
        
        return receivingLineDocument;
    }

    /**
     * Sets the receivingLineDocument attribute value.
     * @param receivingLineDocument The receivingLineDocument to set.
     * @deprecated
     */
    public void setReceivingLineDocument(ReceivingLineDocument receivingLineDocument) {
        this.receivingLineDocument = receivingLineDocument;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public Class getItemClass() {
        return ReceivingCorrectionItem.class;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public ReceivingItem getItem(int pos) {
        return (ReceivingItem) items.get(pos);
    }

    public void addItem(ReceivingItem item) {
        getItems().add(item);
    }

    public void deleteItem(int lineNum) {
        if (getItems().remove(lineNum) == null) {
            // throw error here
        }
    }

    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {        
        return null;
    }

    @Override
    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return getReceivingLineDocument().getAlternateVendorDetailAssignedIdentifier();
    }

    @Override
    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return getReceivingLineDocument().getAlternateVendorHeaderGeneratedIdentifier();
    }

    @Override
    public String getAlternateVendorName() {
        return getReceivingLineDocument().getAlternateVendorName();
    }

    @Override
    public String getAlternateVendorNumber() {
        return getReceivingLineDocument().getAlternateVendorNumber();
    }

    @Override
    public Carrier getCarrier() {
        return getReceivingLineDocument().getCarrier();
    }

    @Override
    public String getCarrierCode() {
        return getReceivingLineDocument().getCarrierCode();
    }

    @Override
    public String getDeliveryBuildingCode() {
        return getReceivingLineDocument().getDeliveryBuildingCode();
    }

    @Override
    public String getDeliveryBuildingLine1Address() {
        return getReceivingLineDocument().getDeliveryBuildingLine1Address();
    }

    @Override
    public String getDeliveryBuildingLine2Address() {
        return getReceivingLineDocument().getDeliveryBuildingLine2Address();
    }

    @Override
    public String getDeliveryBuildingName() {
        return getReceivingLineDocument().getDeliveryBuildingName();
    }

    @Override
    public String getDeliveryBuildingRoomNumber() {
        return getReceivingLineDocument().getDeliveryBuildingRoomNumber();
    }

    @Override
    public Campus getDeliveryCampus() {
        return getReceivingLineDocument().getDeliveryCampus();
    }

    @Override
    public String getDeliveryCampusCode() {
        return getReceivingLineDocument().getDeliveryCampusCode();
    }

    @Override
    public String getDeliveryCityName() {
        return getReceivingLineDocument().getDeliveryCityName();
    }

    @Override
    public String getDeliveryCountryCode() {
        return getReceivingLineDocument().getDeliveryCountryCode();
    }

    @Override
    public String getDeliveryInstructionText() {
        return getReceivingLineDocument().getDeliveryInstructionText();
    }

    @Override
    public String getDeliveryPostalCode() {
        return getReceivingLineDocument().getDeliveryPostalCode();
    }

    @Override
    public Date getDeliveryRequiredDate() {
        return getReceivingLineDocument().getDeliveryRequiredDate();
    }

    @Override
    public DeliveryRequiredDateReason getDeliveryRequiredDateReason() {
        return getReceivingLineDocument().getDeliveryRequiredDateReason();
    }

    @Override
    public String getDeliveryRequiredDateReasonCode() {
        return getReceivingLineDocument().getDeliveryRequiredDateReasonCode();
    }

    @Override
    public String getDeliveryStateCode() {
        return getReceivingLineDocument().getDeliveryStateCode();
    }

    @Override
    public String getDeliveryToEmailAddress() {
        return getReceivingLineDocument().getDeliveryToEmailAddress();
    }

    @Override
    public String getDeliveryToName() {
        return getReceivingLineDocument().getDeliveryToName();
    }

    @Override
    public String getDeliveryToPhoneNumber() {
        return getReceivingLineDocument().getDeliveryToPhoneNumber();
    }

    @Override
    public String getShipmentBillOfLadingNumber() {
        return getReceivingLineDocument().getShipmentBillOfLadingNumber();
    }

    @Override
    public String getShipmentPackingSlipNumber() {
        return getReceivingLineDocument().getShipmentPackingSlipNumber();
    }

    @Override
    public Date getShipmentReceivedDate() {
        return getReceivingLineDocument().getShipmentReceivedDate();
    }

    @Override
    public String getShipmentReferenceNumber() {
        return getReceivingLineDocument().getShipmentReferenceNumber();
    }

    @Override
    public Integer getVendorAddressGeneratedIdentifier() {
        return getReceivingLineDocument().getVendorAddressGeneratedIdentifier();
    }

    @Override
    public String getVendorCityName() {
        return getReceivingLineDocument().getVendorCityName();
    }

    @Override
    public Country getVendorCountry() {
        return getReceivingLineDocument().getVendorCountry();
    }

    @Override
    public String getVendorCountryCode() {
        return getReceivingLineDocument().getVendorCountryCode();
    }

    @Override
    public VendorDetail getVendorDetail() {
        return getReceivingLineDocument().getVendorDetail();
    }

    @Override
    public Integer getVendorDetailAssignedIdentifier() {
        return getReceivingLineDocument().getVendorDetailAssignedIdentifier();
    }

    @Override
    public Integer getVendorHeaderGeneratedIdentifier() {
        return getReceivingLineDocument().getVendorHeaderGeneratedIdentifier();
    }

    @Override
    public String getVendorLine1Address() {
        return getReceivingLineDocument().getVendorLine1Address();
    }

    @Override
    public String getVendorLine2Address() {
        return getReceivingLineDocument().getVendorLine2Address();
    }

    @Override
    public String getVendorName() {
        return getReceivingLineDocument().getVendorName();
    }

    @Override
    public String getVendorNumber() {
        return getReceivingLineDocument().getVendorNumber();
    }

    @Override
    public String getVendorPostalCode() {
        return getReceivingLineDocument().getVendorPostalCode();
    }

    @Override
    public String getVendorStateCode() {
        return getReceivingLineDocument().getVendorStateCode();
    }

}
