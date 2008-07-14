package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.validation.impl.BarcodeInventoryErrorDocumentRule;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.KNSServiceLocator;


public class BarcodeInventoryErrorDocument extends FinancialSystemTransactionalDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocument.class);
    
    //private Long versionNumber;
	private String documentNumber;
	private String uploaderUniversalIdentifier;
    
    private List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetail;
    private FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();    
    
	/**
	 * Default constructor.
	 */
	public BarcodeInventoryErrorDocument() {
	    super();
	    this.setBarcodeInventoryErrorDetail(new TypedArrayList(BarcodeInventoryErrorDetail.class));	    
	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the uploaderUniversalIdentifier attribute.
	 * 
	 * @return Returns the uploaderUniversalIdentifier
	 * 
	 */
	public String getUploaderUniversalIdentifier() { 
		return uploaderUniversalIdentifier;
	}

	/**
	 * Sets the uploaderUniversalIdentifier attribute.
	 * 
	 * @param uploaderUniversalIdentifier The uploaderUniversalIdentifier to set.
	 * 
	 */
	public void setUploaderUniversalIdentifier(String uploaderUniversalIdentifier) {
		this.uploaderUniversalIdentifier = uploaderUniversalIdentifier;
	}

	/**
	 * Gets the documentHeader attribute.
	 * 
	 * @return Returns the documentHeader
	 * 
	 */
	public FinancialSystemDocumentHeader getDocumentHeader() { 
		return documentHeader;
	}

	/**
	 * Sets the documentHeader attribute.
	 * 
	 * @param documentHeader The documentHeader to set.
	 * @deprecated
	 */
	public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
		this.documentHeader = documentHeader;
	}

    public List<BarcodeInventoryErrorDetail> getBarcodeInventoryErrorDetail() {
        return barcodeInventoryErrorDetail;
    }

    public void setBarcodeInventoryErrorDetail(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) {
        this.barcodeInventoryErrorDetail = barcodeInventoryErrorDetails;
    }


//    public void setBarcodeInventoryErrorDetail(BarcodeInventoryErrorDetail detail) {
//        System.out.println("*** Index: "+detail.toString());
//    }

    
    public BarcodeInventoryErrorDetail getBarcodeInventoryErrorDetail(int index) {
        if (index >= barcodeInventoryErrorDetail.size()) {
            for (int i = barcodeInventoryErrorDetail.size(); i <= index; i++) {
                LOG.info("WWWWWWWWWWWWWWWWWW: barcodedocument - Adding element!!!!!!!");
                barcodeInventoryErrorDetail.add(new BarcodeInventoryErrorDetail());
            }
        }
        return barcodeInventoryErrorDetail.get(index);
    }  
    

    public void deleteBarcodeInventoryErrorDetail(int index) {
        barcodeInventoryErrorDetail.remove(index);
    }   
    
    /**
     * 
     * @see org.kuali.core.document.DocumentBase#validateBusinessRules(org.kuali.core.rule.event.KualiDocumentEvent)
     * 
     * Left empty in order to prevent rule validation when saving the document.
     */
    @Override    
    public void validateBusinessRules(KualiDocumentEvent event) {
    }
    

    public void setBarcodeInventoryErrorSelectedRows(boolean flag) {
        for (int i=0;i<this.getBarcodeInventoryErrorDetail().size();i++){
            this.getBarcodeInventoryErrorDetail().get(i).setRowSelected(flag);
        }        
    }

    /**
     * 
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedList = super.buildListOfDeletionAwareLists();

        managedList.add(this.getBarcodeInventoryErrorDetail());

        return managedList;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }
}
