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
import java.util.List;

import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.kfs.module.purap.businessobject.Status;
import org.kuali.rice.kns.bo.Country;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.util.KualiDecimal;


/**
 * Interface for Purchasing-Accounts Payable Documents.
 */
public interface PurchasingAccountsPayableDocument extends AccountingDocument, PurapItemOperations {

    /**
     * Returns true if posting year on document is set to use NEXT fiscal year. If set to anything besides NEXT, then return false.
     * 
     * @return boolean
     */
    public boolean isPostingYearNext();

    /**
     * Returns true if posting year on document is set to use PRIOR fiscal year. If set to anything besides PRIOR, then return false.
     * 
     * @return boolean
     */
    public boolean isPostingYearPrior();
    
    /**
     * If posting year on document is set to use NEXT fiscal year, then return NEXT. If set to anything besides NEXT, then return
     * CURRENT fiscal year.  This is assuming that the system does not allow the user to set a posting year beyond NEXT. 
     * 
     * @return Integer
     */
    public Integer getPostingYearNextOrCurrent();

    /**
     * Returns the Item Class.
     * 
     * @return the Item Class.
     */
    public Class getItemClass();

    /**
     * Returns the source of this Purchasing Accounts Payable Document if exists.
     * 
     * @return the source of this document if exists, else null.
     */
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible();

    /**
     * Returns the label of the source of this Purchasing Accounts Payable Document if exists.
     * 
     * @return the label of the document source if exists, else null.
     */
    public String getPurApSourceDocumentLabelIfPossible();

    /**
     * Returns true if this document is stopped in the specified route node.
     * 
     * @param nodeDetails the node details of the specified node.
     * @return true if this document is stopped in the specified route node.
     */
    public boolean isDocumentStoppedInRouteNode(NodeDetails nodeDetails);

    /**
     * Adds the specified item to this document.
     * 
     * @param item the specified item to add.
     */
    public void addItem(PurApItem item);

    /**
     * Deletes the specified item from this document.
     * 
     * @param item the specified item to delete.
     */
    public void deleteItem(int lineNum);

    /**
     * Renumbers the item starting from the specified index.
     * 
     * @param start the index of the starting item to be renumbered.
     */
    public void renumberItems(int start);

    /**
     * Swaps the specified two items based on their item line numbers (which are one higher than the item positions in the list).
     * 
     * @param position1 the position of the first item
     * @param position2 the position of the second item
     */
    public void itemSwap(int position1, int position2);

    /**
     * Determines the item line position if the user did not specify the line number on an above the line items before clicking on
     * the add button. It subtracts the number of the below the line items on the list with the total item list size.
     * 
     * @return the item line position of the last (highest) line number of above the line items.
     */
    public int getItemLinePosition();

    /**
     * Gets the item at the specified index.
     * 
     * @param pos the specified index.
     * @return the item at the specified index.
     */
    public PurApItem getItem(int pos);

    /**
     * Gets all below the line item types.
     * 
     * @return Returns a list of below the line item types.
     */
    public String[] getBelowTheLineTypes();

    /**
     * Computes the total dollar amount of all items.
     * 
     * @return the total dollar amount of all items.
     */
    public KualiDecimal getTotalDollarAmount();

    /**
     * Sets the total dollar amount to the specified amount.
     * 
     * @param the specified total amount.
     */
    public void setTotalDollarAmount(KualiDecimal totalDollarAmount);

    /**
     * Computes the total dollar amount with the specified item types excluded.
     * 
     * @param excludedTypes the types of items to be excluded.
     * @return the total dollar amount with the specified item types excluded.
     */
    public KualiDecimal getTotalDollarAmountAllItems(String[] excludedTypes);

    /**
     * Computes the pre tax total dollar amount of all items.
     * 
     * @return the pre tax total dollar amount of all items.
     */
    public KualiDecimal getTotalPreTaxDollarAmount();

    /**
     * Sets the pre tax total dollar amount to the specified amount.
     * 
     * @param the specified total amount.
     */
    public void setTotalPreTaxDollarAmount(KualiDecimal totalDollarAmount);

    /**
     * Computes the pre tax total dollar amount with the specified item types excluded.
     * 
     * @param excludedTypes the types of items to be excluded.
     * @return the pre tax total dollar amount with the specified item types excluded.
     */
    public KualiDecimal getTotalPreTaxDollarAmountAllItems(String[] excludedTypes);

    public KualiDecimal getTotalTaxAmount();

    public void setTotalTaxAmount(KualiDecimal amount);

    public KualiDecimal getTotalTaxAmountAllItems(String[] excludedTypes);

    /**
     * Sets vendor address fields based on a given VendorAddress.
     * 
     * @param vendorAddress
     */
    public void templateVendorAddress(VendorAddress vendorAddress);

    public Country getVendorCountry();

    public Status getStatus();

    public VendorDetail getVendorDetail();

    public List<PurApItem> getItems();

    public void setItems(List items);

    public String getVendorNumber();

    public void setVendorNumber(String vendorNumber);

    public Integer getVendorHeaderGeneratedIdentifier();

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier);

    public Integer getVendorDetailAssignedIdentifier();

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier);

    public String getVendorCustomerNumber();

    public void setVendorCustomerNumber(String vendorCustomerNumber);

    public Integer getPurapDocumentIdentifier();

    public void setPurapDocumentIdentifier(Integer identifier);

    public String getStatusCode();

    public void setStatusCode(String statusCode);

    public String getVendorCityName();

    public void setVendorCityName(String vendorCityName);

    public String getVendorCountryCode();

    public void setVendorCountryCode(String vendorCountryCode);

    public String getVendorLine1Address();

    public void setVendorLine1Address(String vendorLine1Address);

    public String getVendorLine2Address();

    public void setVendorLine2Address(String vendorLine2Address);

    public String getVendorName();

    public void setVendorName(String vendorName);

    public String getVendorPostalCode();

    public void setVendorPostalCode(String vendorPostalCode);

    public String getVendorStateCode();

    public void setVendorStateCode(String vendorStateCode);
    
    public String getVendorAddressInternationalProvinceName();
    
    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName);

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier();

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier);

    public Integer getVendorAddressGeneratedIdentifier();

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier);
    
    public boolean isUseTaxIndicator();
    
    public void setUseTaxIndicator(boolean useTaxIndicator);

    public void fixItemReferences();
    
    public Date getTransactionTaxDate();
    
    public PurApItem getTradeInItem();
    
    public KualiDecimal getTotalDollarAmountForTradeIn();
    
    public List<PurApItem> getTradeInItems();
    
    /**
     * Determines whether the inquiry links should be rendered
     * for Object Code and Sub Object Code.
     * 
     * @return
     */
    public boolean isInquiryRendered();
    
}
