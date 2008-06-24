package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.module.purap.document.ReceivingCorrectionDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingCorrectionItem extends ReceivingItemBase {

	private ReceivingCorrectionDocument receivingCorrectionDocument;
    
	/**
	 * Default constructor.
	 */
	public ReceivingCorrectionItem() {

	}

    public ReceivingCorrectionItem(ReceivingLineItem rli, ReceivingCorrectionDocument rcd){
        
        this.setDocumentNumber( rcd.getDocumentNumber() );        
        this.setItemTypeCode( rli.getItemTypeCode() );
        
        this.setItemLineNumber( rli.getItemLineNumber() );
        this.setItemCatalogNumber( rli.getItemCatalogNumber() );
        this.setItemDescription( rli.getItemDescription() );        
        this.setItemUnitOfMeasureCode( rli.getItemUnitOfMeasureCode() );
                
        this.setItemOriginalReceivedTotalQuantity( rli.getItemReceivedTotalQuantity() );
        this.setItemOriginalReturnedTotalQuantity( rli.getItemReturnedTotalQuantity() );
        this.setItemOriginalDamagedTotalQuantity( rli.getItemDamagedTotalQuantity() );

        this.setItemReceivedTotalQuantity(rli.getItemReceivedTotalQuantity());
        this.setItemReturnedTotalQuantity(rli.getItemReturnedTotalQuantity());
        this.setItemDamagedTotalQuantity(rli.getItemDamagedTotalQuantity());

        //not added
        this.setItemReasonAddedCode(null);
    }

	/**
     * Gets the receivingCorrectionDocument attribute. 
     * @return Returns the receivingCorrectionDocument.
     */
    public ReceivingCorrectionDocument getReceivingCorrectionDocument() {
        return receivingCorrectionDocument;
    }

    /**
     * Sets the receivingCorrectionDocument attribute value.
     * @param receivingCorrectionDocument The receivingCorrectionDocument to set.
     * @deprecated
     */
    public void setReceivingCorrectionDocument(ReceivingCorrectionDocument receivingCorrectionDocument) {
        this.receivingCorrectionDocument = receivingCorrectionDocument;
    }


}
