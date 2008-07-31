package org.kuali.kfs.module.ar.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class represents a customer invoice detail on the customer invoice document. This class extends SourceAccountingLine since
 * each customer invoice detail has associated accounting line information.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerInvoiceDetail extends SourceAccountingLine implements AppliedPayment {

    // private Integer invoiceItemNumber; using SourceAccountingLine.sequenceNumber
    private BigDecimal invoiceItemQuantity;
    private String invoiceItemUnitOfMeasureCode;
    private KualiDecimal invoiceItemUnitPrice;
    // private KualiDecimal invoiceItemTotalAmount; using SourceAccountingLine.amount for now
    private Date invoiceItemServiceDate;
    private String invoiceItemCode;
    private String invoiceItemDescription;
    private String accountsReceivableObjectCode;
    private String accountsReceivableSubObjectCode;
    private KualiDecimal invoiceItemTaxAmount;
    private boolean taxableIndicator;
    private boolean isDebit;
    private Integer invoiceItemDiscountLineNumber;
    
    private SubObjCd accountsReceivableSubObject;
    private ObjectCode accountsReceivableObject;
    
    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail;
    private CustomerInvoiceDetail discountCustomerInvoiceDetail;
    private Collection<InvoicePaidApplied> invoicePaidApplieds;

    /**
     * Default constructor.
     */
    public CustomerInvoiceDetail() {
        super();
    }
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }    

    public KualiDecimal getBalance() {
        return getAmount().subtract(getAppliedAmount());
    }
    
    public void setBalance(KualiDecimal balance){
        //do nothing
    }
    
    public KualiDecimal getAppliedAmount() {
        KualiDecimal total = new KualiDecimal(0);
        for(InvoicePaidApplied paidApplied : getInvoicePaidApplieds()) {
            total = total.add(paidApplied.getInvoiceItemAppliedAmount());
        }
        
        return total;
    }
    
    public void setAppliedAmount(KualiDecimal appliedAmount) {

    }

    public Collection<InvoicePaidApplied> getInvoicePaidApplieds() {
        // return SpringContext.getBean(CustomerInvoiceDetailService.class).getInvoicePaidAppliedsForInvoiceDetail(this);
        return invoicePaidApplieds;
    }

    public void setInvoicePaidApplieds(Collection<InvoicePaidApplied> invoicePaidApplieds) {
        this.invoicePaidApplieds = invoicePaidApplieds;
    }

    /**
     * Gets the accountsReceivableObjectCode attribute.
     * 
     * @return Returns the accountsReceivableObjectCode
     * 
     */
    public String getAccountsReceivableObjectCode() {
        return accountsReceivableObjectCode;
    }

    /**
     * Sets the accountsReceivableObjectCode attribute.
     * 
     * @param accountsReceivableObjectCode The accountsReceivableObjectCode to set.
     * 
     */
    public void setAccountsReceivableObjectCode(String accountsReceivableObjectCode) {
        this.accountsReceivableObjectCode = accountsReceivableObjectCode;
    }


    /**
     * Gets the accountsReceivableSubObjectCode attribute.
     * 
     * @return Returns the accountsReceivableSubObjectCode
     * 
     */
    public String getAccountsReceivableSubObjectCode() {
        return accountsReceivableSubObjectCode;
    }

    /**
     * Sets the accountsReceivableSubObjectCode attribute.
     * 
     * @param accountsReceivableSubObjectCode The accountsReceivableSubObjectCode to set.
     * 
     */
    public void setAccountsReceivableSubObjectCode(String accountsReceivableSubObjectCode) {
        this.accountsReceivableSubObjectCode = accountsReceivableSubObjectCode;
    }


    /**
     * Gets the invoiceItemQuantity attribute.
     * 
     * @return Returns the invoiceItemQuantity
     * 
     */
    public BigDecimal getInvoiceItemQuantity() {
        return invoiceItemQuantity;
    }

    /**
     * Sets the invoiceItemQuantity attribute.
     * 
     * @param invoiceItemQuantity The invoiceItemQuantity to set.
     * 
     */
    public void setInvoiceItemQuantity(BigDecimal invoiceItemQuantity) {
        this.invoiceItemQuantity = invoiceItemQuantity;
    }


    /**
     * Gets the invoiceItemUnitOfMeasureCode attribute.
     * 
     * @return Returns the invoiceItemUnitOfMeasureCode
     * 
     */
    public String getInvoiceItemUnitOfMeasureCode() {
        return invoiceItemUnitOfMeasureCode;
    }

    /**
     * Sets the invoiceItemUnitOfMeasureCode attribute.
     * 
     * @param invoiceItemUnitOfMeasureCode The invoiceItemUnitOfMeasureCode to set.
     * 
     */
    public void setInvoiceItemUnitOfMeasureCode(String invoiceItemUnitOfMeasureCode) {
        this.invoiceItemUnitOfMeasureCode = invoiceItemUnitOfMeasureCode;
    }


    /**
     * Gets the invoiceItemUnitPrice attribute.
     * 
     * @return Returns the invoiceItemUnitPrice
     * 
     */
    public KualiDecimal getInvoiceItemUnitPrice() {
        return invoiceItemUnitPrice;
    }

    /**
     * Sets the invoiceItemUnitPrice attribute.
     * 
     * @param invoiceItemUnitPrice The invoiceItemUnitPrice to set.
     * 
     */
    public void setInvoiceItemUnitPrice(KualiDecimal invoiceItemUnitPrice) {
        this.invoiceItemUnitPrice = invoiceItemUnitPrice;
    }


    /**
     * Gets the invoiceItemServiceDate attribute.
     * 
     * @return Returns the invoiceItemServiceDate
     * 
     */
    public Date getInvoiceItemServiceDate() {
        return invoiceItemServiceDate;
    }

    /**
     * Sets the invoiceItemServiceDate attribute.
     * 
     * @param invoiceItemServiceDate The invoiceItemServiceDate to set.
     * 
     */
    public void setInvoiceItemServiceDate(Date invoiceItemServiceDate) {
        this.invoiceItemServiceDate = invoiceItemServiceDate;
    }


    /**
     * Gets the invoiceItemCode attribute.
     * 
     * @return Returns the invoiceItemCode
     * 
     */
    public String getInvoiceItemCode() {
        return invoiceItemCode;
    }

    /**
     * Sets the invoiceItemCode attribute.
     * 
     * @param invoiceItemCode The invoiceItemCode to set.
     * 
     */
    public void setInvoiceItemCode(String invoiceItemCode) {
        this.invoiceItemCode = invoiceItemCode;
    }


    /**
     * Gets the invoiceItemDescription attribute.
     * 
     * @return Returns the invoiceItemDescription
     * 
     */
    public String getInvoiceItemDescription() {
        return invoiceItemDescription;
    }

    /**
     * Sets the invoiceItemDescription attribute.
     * 
     * @param invoiceItemDescription The invoiceItemDescription to set.
     * 
     */
    public void setInvoiceItemDescription(String invoiceItemDescription) {
        this.invoiceItemDescription = invoiceItemDescription;
    }
    
    /**
     * This method returns the invoice pre tax amount
     * @return
     */
    public KualiDecimal getInvoiceItemPreTaxAmount(){
        if( ObjectUtils.isNotNull(invoiceItemUnitPrice) && ObjectUtils.isNotNull(invoiceItemQuantity)){
            return invoiceItemUnitPrice.multiply(new KualiDecimal(invoiceItemQuantity));
        } else {
            return KualiDecimal.ZERO;
        }
        
    }

    /**
     * Gets the invoiceItemTaxAmount attribute.
     * TODO Use tax service to get invoice item tax amount
     * 
     * @return Returns the invoiceItemTaxAmount.
     */
    public KualiDecimal getInvoiceItemTaxAmount() {
        return KualiDecimal.ZERO;
    }

    /**
     * Sets the invoiceItemTaxAmount attribute value.
     * 
     * @param invoiceItemTaxAmount The invoiceItemTaxAmount to set.
     */
    public void setInvoiceItemTaxAmount(KualiDecimal invoiceItemTaxAmount) {
        this.invoiceItemTaxAmount = invoiceItemTaxAmount;
    }

    /**
     * Gets the invoiceItemDiscountLineNumber attribute. 
     * @return Returns the invoiceItemDiscountLineNumber.
     */
    public Integer getInvoiceItemDiscountLineNumber() {
        return invoiceItemDiscountLineNumber;
    }

    /**
     * Sets the invoiceItemDiscountLineNumber attribute value.
     * @param invoiceItemDiscountLineNumber The invoiceItemDiscountLineNumber to set.
     */
    public void setInvoiceItemDiscountLineNumber(Integer invoiceItemDiscountLineNumber) {
        this.invoiceItemDiscountLineNumber = invoiceItemDiscountLineNumber;
    }

    /**
     * Gets the accountsReceivableSubObject attribute.
     * 
     * @return Returns the accountsReceivableSubObject
     * 
     */
    public SubObjCd getAccountsReceivableSubObject() {
        return accountsReceivableSubObject;
    }

    /**
     * Sets the accountsReceivableSubObject attribute.
     * 
     * @param accountsReceivableSubObject The accountsReceivableSubObject to set.
     * @deprecated
     */
    public void setAccountsReceivableSubObject(SubObjCd accountsReceivableSubObject) {
        this.accountsReceivableSubObject = accountsReceivableSubObject;
    }

    /**
     * Gets the accountsReceivableObject attribute.
     * 
     * @return Returns the accountsReceivableObject
     * 
     */
    public ObjectCode getAccountsReceivableObject() {
        return accountsReceivableObject;
    }

    /**
     * Sets the accountsReceivableObject attribute.
     * 
     * @param accountsReceivableObject The accountsReceivableObject to set.
     * @deprecated
     */
    public void setAccountsReceivableObject(ObjectCode accountsReceivableObject) {
        this.accountsReceivableObject = accountsReceivableObject;
    }
    

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", getDocumentNumber());
        if (this.getSequenceNumber() != null) {
            m.put("invoiceItemNumber", this.getSequenceNumber().toString());
        }
        return m;
    }

    /**
     * Update line amount based on quantity and unit price
     */
    public void updateAmountBasedOnQuantityAndUnitPrice() {
            setAmount(getInvoiceItemPreTaxAmount());
    }


    public boolean isTaxableIndicator() {
        return taxableIndicator;
    }


    public void setTaxableIndicator(boolean taxableIndicator) {
        this.taxableIndicator = taxableIndicator;
    }


    public boolean isDebit() {
        return isDebit;
    }


    public void setDebit(boolean isDebit) {
        this.isDebit = isDebit;
    }
    
    /**
     * This method returns true if customer invoice detail has a corresponding discount line
     * @return
     */
    public boolean isDiscountLineParent(){
        return ObjectUtils.isNotNull(getInvoiceItemDiscountLineNumber() );
    }
    
    /**
     * This method should only be used to determine if detail is discount line in JSP. If you want to determine if
     * invoice detail is a detail line use CustomerInvoiceDocument.isDiscountLineBasedOnSequenceNumber() instead.  
     * 
     * @return
     */
    public boolean isDiscountLine() {
        return ObjectUtils.isNotNull(parentDiscountCustomerInvoiceDetail);
    }    
    
    /**
     * This method sets the amount to negative if it isn't already negative
     * @return
     */
    public void setInvoiceItemUnitPriceToNegative(){
        if( invoiceItemUnitPrice.isPositive() ){
            invoiceItemUnitPrice = invoiceItemUnitPrice.negated();
        }
    }

    public CustomerInvoiceDetail getParentDiscountCustomerInvoiceDetail() {
        return parentDiscountCustomerInvoiceDetail;
    }


    public void setParentDiscountCustomerInvoiceDetail(CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail) {
        this.parentDiscountCustomerInvoiceDetail = parentDiscountCustomerInvoiceDetail;
    }


    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail() {
        return discountCustomerInvoiceDetail;
    }


    public void setDiscountCustomerInvoiceDetail(CustomerInvoiceDetail discountCustomerInvoiceDetail) {
        this.discountCustomerInvoiceDetail = discountCustomerInvoiceDetail;
    }

    /**
     * This should only apply amounts if this invoice detail is a discount.  
     *
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getAmountToApply()
     */
    public KualiDecimal getAmountToApply() {
        return getAmount().negated();
    }
    
    /**
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getInvoiceItemNumber()
     */
    public Integer getInvoiceItemNumber() {
        return parentDiscountCustomerInvoiceDetail.getSequenceNumber();
    }

    /**
     * @see org.kuali.kfs.module.ar.businessobject.AppliedPayment#getInvoiceReferenceNumber()
     */
    public String getInvoiceReferenceNumber() {
      return customerInvoiceDocument.isInvoiceReversal() ? customerInvoiceDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() : customerInvoiceDocument.getDocumentNumber();
    }
}
