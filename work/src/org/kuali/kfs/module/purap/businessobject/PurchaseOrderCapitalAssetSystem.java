package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;



/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PurchaseOrderCapitalAssetSystem extends PurchasingCapitalAssetSystemBase {

	private String documentNumber;
	
	/**
	 * Default constructor.
	 */
	public PurchaseOrderCapitalAssetSystem() {
       super();
	}
	
	public PurchaseOrderCapitalAssetSystem(RequisitionCapitalAssetSystem reqSystem) {
	    super();
	    this.setCapitalAssetSystemDescription(reqSystem.getCapitalAssetSystemDescription());
	    this.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(reqSystem.isCapitalAssetNotReceivedCurrentFiscalYearIndicator());
	    this.setCapitalAssetTypeCode(reqSystem.getCapitalAssetTypeCode());
	    this.setCapitalAssetManufacturerName(reqSystem.getCapitalAssetManufacturerName());
	    this.setCapitalAssetModelDescription(reqSystem.getCapitalAssetModelDescription());
	    this.setCapitalAssetNoteText(reqSystem.getCapitalAssetNoteText());
	    populatePurchaseOrderItemCapitalAssets(reqSystem);
	    this.setCapitalAssetLocations(reqSystem.getCapitalAssetLocations());
	    this.setCapitalAssetCountAssetNumber(reqSystem.getCapitalAssetCountAssetNumber());
	}

	private void populatePurchaseOrderItemCapitalAssets(RequisitionCapitalAssetSystem reqSystem) {
	    for (ItemCapitalAsset reqAsset : reqSystem.getItemCapitalAssets()) {
	        PurchaseOrderItemCapitalAsset poAsset = new PurchaseOrderItemCapitalAsset(reqAsset.getCapitalAssetNumber());
	        this.getItemCapitalAssets().add(poAsset);
	    }
	}
	
	public String getDocumentNumber() {
        return documentNumber;
    }

    @Override
    public Class getCapitalAssetLocationClass() {
        return PurchaseOrderCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return PurchaseOrderItemCapitalAsset.class;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.documentNumber != null) {
            m.put("documentNumber", this.documentNumber.toString());
        }
        if (this.getCapitalAssetSystemIdentifier() != null) {
            m.put("capitalAssetSystemIdentifier", this.getCapitalAssetSystemIdentifier().toString());
        }
        return m;
    }
}
