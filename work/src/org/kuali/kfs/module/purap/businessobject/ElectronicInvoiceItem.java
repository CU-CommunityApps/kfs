/*
 * Created on Aug 25, 2004
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;


/**
 * @author delyea
 *
 */
public class ElectronicInvoiceItem {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceItem.class);
  
  private static BigDecimal zero = new BigDecimal(0.00);
  
  // this class is equiped to hold InvoiceDetailItem values as well as a few rudimentary
  // InvoiceDetailServiceItem values

  private String catalogNumber;
  private String invoiceLineNumber;
  private String quantity;
  private String unitOfMeasure;
  // UnitPrice is deprecated for InvoiceDetailServiceItem tags
  private String unitPrice; // has money xml node
  private String unitPriceCurrency;
  private String subTotalAmount; // has money xml node
  private String subTotalAmountCurrency;
  private String invoiceLineSpecialHandlingAmount; // has money xml node
  private String invoiceLineSpecialHandlingAmountCurrency;
  private String invoiceLineShippingAmount; // has money xml node
  private String invoiceLineShippingAmountCurrency;
  private String taxAmount; // has money xml node  (not all tax fields are stored as tax should never occur)
  private String taxAmountCurrency;
  private String taxDescription;
  // invoiceLineGrossAmount should = subtotalAmount + taxAmount + invoiceLineSpecialHandlingAmount + invoiceLineShippingAmount 
  private String invoiceLineGrossAmount; // subtotal + taxes + shipping + special handling
  private String invoiceLineGrossAmountCurrency;
  private String invoiceLineDiscountAmount; // has money xml node
  private String invoiceLineDiscountAmountCurrency;
  // invoiceLineNetAmount should = invoiceLineGrossAmount - invoiceLineDiscountAmount 
  private String invoiceLineNetAmount;  // has money xml node
  private String invoiceLineNetAmountCurrency;
  private String shippingDateString;
  private Date shippingDate;
  
  private List invoiceShippingContacts = new ArrayList();
  private List comments = new ArrayList();
  private List extrinsic = new ArrayList();
  
  // following fields describe PO information
  private String referenceLineNumber; // only match available for InvoiceDetailServiceItem values
  private String referenceSerialNumber; // attribute of <InvoiceDetailItemReference> deprecated to be <SerialNumber> inside it
  private List<String> referenceSerialNumbers = new ArrayList<String>(); // used only if above String field is null
  private String referenceItemIDSupplierPartID;
  private String referenceItemIDSupplierPartAuxID;
  private String referenceDescription;
  private String referenceManufacturerPartID;
  private String referenceManufacturerName;
  private String referenceCountryCode;
  private String referenceCountryName;
  
  private ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument;
  
  public ElectronicInvoiceItem() {
  }
  
  public Integer getReferenceLineNumberInteger() {
    if (this.referenceLineNumber != null) {
      return new Integer(Integer.parseInt(referenceLineNumber));
    }
    return null;
  }
  
  // TODO NEXT RELEASE: E-Invoicing - Shipping Description Text, where to get it?
  public String getInvoiceLineShippingDescription() {
    return "";
  }
  
  public BigDecimal getInvoiceLineQuantityBigDecimal() {
    if ( (this.quantity != null) && (!("".equalsIgnoreCase(this.quantity))) ) {
      return new BigDecimal(this.quantity);
    } else {
      return null;
    }
  }
  
  public BigDecimal getInvoiceLineUnitCostBigDecimal() {
    if (this.unitPrice != null) {
      return new BigDecimal(this.unitPrice);
    } else {
      return zero;
    }
  }
  
  public BigDecimal getInvoiceLineSubTotalAmountBigDecimal() {
    if (this.subTotalAmount != null) {
      return new BigDecimal(this.subTotalAmount);
    } else {
      return zero;
    }
  }

  public BigDecimal getInvoiceLineTaxAmountBigDecimal() {
    if (this.taxAmount != null) {
      return new BigDecimal(this.taxAmount);
    } else {
      return zero;
    }
  }

  public BigDecimal getInvoiceLineSpecialHandlingAmountBigDecimal() {
    if (this.invoiceLineSpecialHandlingAmount != null) {
      return new BigDecimal(this.invoiceLineSpecialHandlingAmount);
    } else {
      return zero;
    }
  }

  public BigDecimal getInvoiceLineShippingAmountBigDecimal() {
    if (this.invoiceLineShippingAmount != null) {
      return new BigDecimal(this.invoiceLineShippingAmount);
    } else {
      return zero;
    }
  }

  public BigDecimal getInvoiceLineGrossAmountBigDecimal() {
    if (this.invoiceLineGrossAmount != null) {
      return new BigDecimal(this.invoiceLineGrossAmount);
    } else {
      return zero;
    }
  }

  public BigDecimal getInvoiceLineDiscountAmountBigDecimal() {
    if (this.invoiceLineDiscountAmount != null) {
      return new BigDecimal(this.invoiceLineDiscountAmount);
    } else {
      return zero;
    }
  }

  public BigDecimal getInvoiceLineNetAmountBigDecimal() {
    if (this.invoiceLineNetAmount != null) {
      return new BigDecimal(this.invoiceLineNetAmount);
    } else {
      return zero;
    }
  }

  /**
   * @return Returns the shippingDateString.
   */
  public String getShippingDateString() {
    return shippingDateString;
  }
  /**
   * @param shippingDateString The shippingDateString to set.
   */
  public void setShippingDateString(String shippingDateString) {
    this.shippingDateString = shippingDateString;
    if ( (shippingDateString != null) && (!("".equals(shippingDateString))) ) {
      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT, Locale.US);
      try {
        this.shippingDate = sdf.parse(shippingDateString);
      } catch (ParseException e) {
        // setting shipping date to null to identify problem
        LOG.error("setShippingDateString() SimpleDateFormat parser error attempting to set invalid date string " + shippingDateString + " in ShippingDate field... setting date to null");
        this.shippingDate = null;
      }
    } else {
      this.shippingDate = null;
    }
  }
  
  /**
   * @return Returns the catalogNumber.
   */
  public String getCatalogNumber() {
    return catalogNumber;
  }
  /**
   * @param catalogNumber The catalogNumber to set.
   */
  public void setCatalogNumber(String catalogNumber) {
    this.catalogNumber = catalogNumber;
  }
  /**
   * @return Returns the comments.
   */
  public List getComments() {
    return comments;
  }
  /**
   * @param comments The comments to set.
   */
  public void setComments(List comments) {
    this.comments = comments;
  }
  /**
   * @return Returns the extrinsic.
   */
  public List getExtrinsic() {
    return extrinsic;
  }
  /**
   * @param extrinsic The extrinsic to set.
   */
  public void setExtrinsic(List extrinsic) {
    this.extrinsic = extrinsic;
  }
  /**
   * @return Returns the invoiceLineDiscountAmount.
   */
  public String getInvoiceLineDiscountAmount() {
    return invoiceLineDiscountAmount;
  }
  /**
   * @param invoiceLineDiscountAmount The invoiceLineDiscountAmount to set.
   */
  public void setInvoiceLineDiscountAmount(String invoiceLineDiscountAmount) {
    this.invoiceLineDiscountAmount = invoiceLineDiscountAmount;
  }
  /**
   * @return Returns the invoiceLineDiscountAmountCurrency.
   */
  public String getInvoiceLineDiscountAmountCurrency() {
    return invoiceLineDiscountAmountCurrency;
  }
  /**
   * @param invoiceLineDiscountAmountCurrency The invoiceLineDiscountAmountCurrency to set.
   */
  public void setInvoiceLineDiscountAmountCurrency(String invoiceLineDiscountAmountCurrency) {
    this.invoiceLineDiscountAmountCurrency = invoiceLineDiscountAmountCurrency;
  }
  /**
   * @return Returns the invoiceLineGrossAmount.
   */
  public String getInvoiceLineGrossAmount() {
    return invoiceLineGrossAmount;
  }
  /**
   * @param invoiceLineGrossAmount The invoiceLineGrossAmount to set.
   */
  public void setInvoiceLineGrossAmount(String invoiceLineGrossAmount) {
    this.invoiceLineGrossAmount = invoiceLineGrossAmount;
  }
  /**
   * @return Returns the invoiceLineGrossAmountCurrency.
   */
  public String getInvoiceLineGrossAmountCurrency() {
    return invoiceLineGrossAmountCurrency;
  }
  /**
   * @param invoiceLineGrossAmountCurrency The invoiceLineGrossAmountCurrency to set.
   */
  public void setInvoiceLineGrossAmountCurrency(String invoiceLineGrossAmountCurrency) {
    this.invoiceLineGrossAmountCurrency = invoiceLineGrossAmountCurrency;
  }
  /**
   * @return Returns the invoiceLineNetAmount.
   */
  public String getInvoiceLineNetAmount() {
    return invoiceLineNetAmount;
  }
  /**
   * @param invoiceLineNetAmount The invoiceLineNetAmount to set.
   */
  public void setInvoiceLineNetAmount(String invoiceLineNetAmount) {
    this.invoiceLineNetAmount = invoiceLineNetAmount;
  }
  /**
   * @return Returns the invoiceLineNetAmountCurrency.
   */
  public String getInvoiceLineNetAmountCurrency() {
    return invoiceLineNetAmountCurrency;
  }
  /**
   * @param invoiceLineNetAmountCurrency The invoiceLineNetAmountCurrency to set.
   */
  public void setInvoiceLineNetAmountCurrency(String invoiceLineNetAmountCurrency) {
    this.invoiceLineNetAmountCurrency = invoiceLineNetAmountCurrency;
  }
  /**
   * @return Returns the invoiceLineNumber.
   */
  public String getInvoiceLineNumber() {
    return invoiceLineNumber;
  }
  /**
   * @param invoiceLineNumber The invoiceLineNumber to set.
   */
  public void setInvoiceLineNumber(String invoiceLineNumber) {
    this.invoiceLineNumber = invoiceLineNumber;
  }
  /**
   * @return Returns the invoiceLineShippingAmount.
   */
  public String getInvoiceLineShippingAmount() {
    return invoiceLineShippingAmount;
  }
  /**
   * @param invoiceLineShippingAmount The invoiceLineShippingAmount to set.
   */
  public void setInvoiceLineShippingAmount(String invoiceLineShippingAmount) {
    this.invoiceLineShippingAmount = invoiceLineShippingAmount;
  }
  /**
   * @return Returns the invoiceLineShippingAmountCurrency.
   */
  public String getInvoiceLineShippingAmountCurrency() {
    return invoiceLineShippingAmountCurrency;
  }
  /**
   * @param invoiceLineShippingAmountCurrency The invoiceLineShippingAmountCurrency to set.
   */
  public void setInvoiceLineShippingAmountCurrency(String invoiceLineShippingAmountCurrency) {
    this.invoiceLineShippingAmountCurrency = invoiceLineShippingAmountCurrency;
  }
  /**
   * @return Returns the invoiceLineSpecialHandlingAmount.
   */
  public String getInvoiceLineSpecialHandlingAmount() {
    return invoiceLineSpecialHandlingAmount;
  }
  /**
   * @param invoiceLineSpecialHandlingAmount The invoiceLineSpecialHandlingAmount to set.
   */
  public void setInvoiceLineSpecialHandlingAmount(String invoiceLineSpecialHandlingAmount) {
    this.invoiceLineSpecialHandlingAmount = invoiceLineSpecialHandlingAmount;
  }
  /**
   * @return Returns the invoiceLineSpecialHandlingAmountCurrency.
   */
  public String getInvoiceLineSpecialHandlingAmountCurrency() {
    return invoiceLineSpecialHandlingAmountCurrency;
  }
  /**
   * @param invoiceLineSpecialHandlingAmountCurrency The invoiceLineSpecialHandlingAmountCurrency to set.
   */
  public void setInvoiceLineSpecialHandlingAmountCurrency(
      String invoiceLineSpecialHandlingAmountCurrency) {
    this.invoiceLineSpecialHandlingAmountCurrency = invoiceLineSpecialHandlingAmountCurrency;
  }
  /**
   * @return Returns the invoiceShippingContacts.
   */
  public List getInvoiceShippingContacts() {
    return invoiceShippingContacts;
  }
  /**
   * @param invoiceShippingContacts The invoiceShippingContacts to set.
   */
  public void setInvoiceShippingContacts(List invoiceShippingContacts) {
    this.invoiceShippingContacts = invoiceShippingContacts;
  }
  /**
   * @return Returns the quantity.
   */
  public String getQuantity() {
    return quantity;
  }
  /**
   * @param quantity The quantity to set.
   */
  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }
  /**
   * @return Returns the referenceCountryCode.
   */
  public String getReferenceCountryCode() {
    return referenceCountryCode;
  }
  /**
   * @param referenceCountryCode The referenceCountryCode to set.
   */
  public void setReferenceCountryCode(String referenceCountryCode) {
    this.referenceCountryCode = referenceCountryCode;
  }
  /**
   * @return Returns the referenceCountryName.
   */
  public String getReferenceCountryName() {
    return referenceCountryName;
  }
  /**
   * @param referenceCountryName The referenceCountryName to set.
   */
  public void setReferenceCountryName(String referenceCountryName) {
    this.referenceCountryName = referenceCountryName;
  }
  /**
   * @return Returns the referenceDescription.
   */
  public String getReferenceDescription() {
    return referenceDescription;
  }
  /**
   * @param referenceDescription The referenceDescription to set.
   */
  public void setReferenceDescription(String referenceDescription) {
    this.referenceDescription = referenceDescription;
  }
  /**
   * @return Returns the referenceItemIDSupplierPartAuxID.
   */
  public String getReferenceItemIDSupplierPartAuxID() {
    return referenceItemIDSupplierPartAuxID;
  }
  /**
   * @param referenceItemIDSupplierPartAuxID The referenceItemIDSupplierPartAuxID to set.
   */
  public void setReferenceItemIDSupplierPartAuxID(String referenceItemIDSupplierPartAuxID) {
    this.referenceItemIDSupplierPartAuxID = referenceItemIDSupplierPartAuxID;
  }
  /**
   * @return Returns the referenceItemIDSupplierPartID.
   */
  public String getReferenceItemIDSupplierPartID() {
    return referenceItemIDSupplierPartID;
  }
  /**
   * @param referenceItemIDSupplierPartID The referenceItemIDSupplierPartID to set.
   */
  public void setReferenceItemIDSupplierPartID(String referenceItemIDSupplierPartID) {
    this.referenceItemIDSupplierPartID = referenceItemIDSupplierPartID;
  }
  /**
   * @return Returns the referenceLineNumber.
   */
  public String getReferenceLineNumber() {
    return referenceLineNumber;
  }
  /**
   * @param referenceLineNumber The referenceLineNumber to set.
   */
  public void setReferenceLineNumber(String referenceLineNumber) {
    this.referenceLineNumber = referenceLineNumber;
  }
  /**
   * @return Returns the referenceManufacturerName.
   */
  public String getReferenceManufacturerName() {
    return referenceManufacturerName;
  }
  /**
   * @param referenceManufacturerName The referenceManufacturerName to set.
   */
  public void setReferenceManufacturerName(String referenceManufacturerName) {
    this.referenceManufacturerName = referenceManufacturerName;
  }
  /**
   * @return Returns the referenceManufacturerPartID.
   */
  public String getReferenceManufacturerPartID() {
    return referenceManufacturerPartID;
  }
  /**
   * @param referenceManufacturerPartID The referenceManufacturerPartID to set.
   */
  public void setReferenceManufacturerPartID(String referenceManufacturerPartID) {
    this.referenceManufacturerPartID = referenceManufacturerPartID;
  }
  /**
   * @return Returns the referenceSerialNumber.
   */
  public String getReferenceSerialNumber() {
    return referenceSerialNumber;
  }
  /**
   * @param referenceSerialNumber The referenceSerialNumber to set.
   */
  public void setReferenceSerialNumber(String referenceSerialNumber) {
    this.referenceSerialNumber = referenceSerialNumber;
  }
  /**
   * @return Returns the referenceSerialNumbers.
   */
  public List getReferenceSerialNumbers() {
    return referenceSerialNumbers;
  }
  /**
   * @param referenceSerialNumbers The referenceSerialNumbers to set.
   */
  public void setReferenceSerialNumbers(List referenceSerialNumbers) {
    this.referenceSerialNumbers = referenceSerialNumbers;
  }
  /**
   * @return Returns the shippingDate.
   */
  public Date getShippingDate() {
    return shippingDate;
  }
  /**
   * @param shippingDate The shippingDate to set.
   */
  public void setShippingDate(Date shippingDate) {
    this.shippingDate = shippingDate;
  }
  /**
   * @return Returns the subtotalAmount.
   */
  public String getSubTotalAmount() {
    return subTotalAmount;
  }
  /**
   * @param subtotalAmount The subtotalAmount to set.
   */
  public void setSubTotalAmount(String subTotalAmount) {
    this.subTotalAmount = subTotalAmount;
  }
  /**
   * @return Returns the subtotalAmountCurrency.
   */
  public String getSubTotalAmountCurrency() {
    return subTotalAmountCurrency;
  }
  /**
   * @param subtotalAmountCurrency The subtotalAmountCurrency to set.
   */
  public void setSubTotalAmountCurrency(String subTotalAmountCurrency) {
    this.subTotalAmountCurrency = subTotalAmountCurrency;
  }
  /**
   * @return Returns the taxAmount.
   */
  public String getTaxAmount() {
    return taxAmount;
  }
  /**
   * @param taxAmount The taxAmount to set.
   */
  public void setTaxAmount(String taxAmount) {
    this.taxAmount = taxAmount;
  }
  /**
   * @return Returns the taxAmountCurrency.
   */
  public String getTaxAmountCurrency() {
    return taxAmountCurrency;
  }
  /**
   * @param taxAmountCurrency The taxAmountCurrency to set.
   */
  public void setTaxAmountCurrency(String taxAmountCurrency) {
    this.taxAmountCurrency = taxAmountCurrency;
  }
  /**
   * @return Returns the taxDescription.
   */
  public String getTaxDescription() {
    return taxDescription;
  }
  /**
   * @param taxDescription The taxDescription to set.
   */
  public void setTaxDescription(String taxDescription) {
    this.taxDescription = taxDescription;
  }
  /**
   * @return Returns the unitOfMeasure.
   */
  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }
  /**
   * @param unitOfMeasure The unitOfMeasure to set.
   */
  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }
  /**
   * @return Returns the unitPrice.
   */
  public String getUnitPrice() {
    return unitPrice;
  }
  /**
   * @param unitPrice The unitPrice to set.
   */
  public void setUnitPrice(String unitPrice) {
    this.unitPrice = unitPrice;
  }
  /**
   * @return Returns the unitPriceCurrency.
   */
  public String getUnitPriceCurrency() {
    return unitPriceCurrency;
  }
  /**
   * @param unitPriceCurrency The unitPriceCurrency to set.
   */
  public void setUnitPriceCurrency(String unitPriceCurrency) {
    this.unitPriceCurrency = unitPriceCurrency;
  }

    public ElectronicInvoiceRejectDocument getElectronicInvoiceRejectDocument() {
        return electronicInvoiceRejectDocument;
    }
    
    public void setElectronicInvoiceRejectDocument(ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument) {
        this.electronicInvoiceRejectDocument = electronicInvoiceRejectDocument;
    }
    
    
    public void addReferenceSerialNumber(String referenceSerialNumber) {
        this.referenceSerialNumbers.add(referenceSerialNumber);
    }
    
    public String[] getReferenceSerialNumbersAsArray() {
        if (referenceSerialNumbers.size() > 0){
            String[] serialNumbers = new String[referenceSerialNumbers.size()];
            referenceSerialNumbers.toArray(serialNumbers);
            return serialNumbers;
        }
        return null;
    }

    public String toString(){
        
        ToStringBuilder toString = new ToStringBuilder(this);
        
        toString.append("invoiceLineNumber",getInvoiceLineNumber());
        toString.append("quantity",getQuantity());
        toString.append("catalogNumber",getCatalogNumber());
        toString.append("unitOfMeasure",getUnitOfMeasure());
        toString.append("unitPrice",getUnitPrice());
        toString.append("unitPriceCurrency",getUnitPriceCurrency());
        toString.append("subTotalAmount",getSubTotalAmount());
        toString.append("subTotalAmountCurrency",getSubTotalAmountCurrency());
        toString.append("invoiceLineSpecialHandlingAmount",getInvoiceLineSpecialHandlingAmount());
        toString.append("invoiceLineSpecialHandlingAmountCurrency",getInvoiceLineSpecialHandlingAmountCurrency());
        toString.append("invoiceLineShippingAmount",getInvoiceLineShippingAmount());
        toString.append("invoiceLineShippingAmountCurrency",getInvoiceLineShippingAmountCurrency());
        toString.append("taxAmount",getTaxAmount());
        toString.append("taxAmountCurrency",getTaxAmountCurrency());
        toString.append("taxDescription",getTaxDescription());
        toString.append("invoiceLineGrossAmount",getInvoiceLineGrossAmount());
        toString.append("invoiceLineGrossAmountCurrency",getInvoiceLineGrossAmountCurrency());
        toString.append("invoiceLineDiscountAmount",getInvoiceLineDiscountAmount());
        toString.append("invoiceLineDiscountAmountCurrency",getInvoiceLineDiscountAmountCurrency());
        toString.append("invoiceLineNetAmount",getInvoiceLineNetAmount());
        toString.append("invoiceLineNetAmountCurrency",getInvoiceLineNetAmountCurrency());
        toString.append("shippingDateString",getShippingDateString());
        
        toString.append("referenceLineNumber",getReferenceLineNumber());
        toString.append("referenceSerialNumber",getReferenceSerialNumber());
        toString.append("referenceSerialNumbersList",getReferenceSerialNumbers());
        toString.append("referenceItemIDSupplierPartID",getReferenceItemIDSupplierPartID());
        toString.append("referenceItemIDSupplierPartAuxID",getReferenceItemIDSupplierPartAuxID());
        toString.append("referenceDescription",getReferenceDescription());
        toString.append("referenceManufacturerPartID",getReferenceManufacturerPartID());
        toString.append("referenceManufacturerName",getReferenceManufacturerName());
        toString.append("referenceCountryCode",getReferenceCountryCode());
        toString.append("referenceCountryName",getReferenceCountryName());
        
        return toString.toString();
        
    }
  
}
