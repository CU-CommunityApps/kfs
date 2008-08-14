/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.options.PayeeTypeValuesFinder;
import org.kuali.kfs.fp.businessobject.options.PaymentReasonValuesFinder;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class is used to represent a disbursement voucher payee detail.
 */
public class DisbursementVoucherPayeeDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String disbVchrPaymentReasonCode;

    // Payee ID Number will correspond to the UniversalUser.personUniversalIdentifier or 
    // VendorHeader.vendorHeaderGeneratedIdentifier and VendorDetail.vendorDetailAssignedIdentifier,
    // depending on the type of payee that was selected (ie. Employee or Vendor)
    private String disbVchrPayeeIdNumber;
    
    private String disbVchrPayeePersonName;

    private String disbVchrPayeeLine1Addr;
    private String disbVchrPayeeLine2Addr;
    private String disbVchrPayeeCityName;
    private String disbVchrPayeeStateCode;
    private String disbVchrPayeeZipCode;
    private String disbVchrPayeeCountryCode;

    private String disbVchrSpecialHandlingPersonName;
    private String disbVchrSpecialHandlingLine1Addr;
    private String disbVchrSpecialHandlingLine2Addr;
    private String disbVchrSpecialHandlingCityName;
    private String disbVchrSpecialHandlingStateCode;
    private String disbVchrSpecialHandlingZipCode;
    private String disbVchrSpecialHandlingCountryCode;

    private Boolean dvPayeeSubjectPaymentCode;
    private Boolean disbVchrAlienPaymentCode;
    private Boolean disbVchrPayeeEmployeeCode;
    private Boolean disbVchrEmployeePaidOutsidePayrollCode;
    private String disbursementVoucherPayeeTypeCode;

    private PaymentReasonCode disbVchrPaymentReason;

    // The following vendor-associated attributes are for convenience only and are not mapped to OJB or the DB.
    private String disbVchrVendorHeaderIdNumber;
    private String disbVchrVendorDetailAssignedIdNumber;
    private String disbVchrVendorAddressIdNumber;
    private boolean hasMultipleVendorAddresses = false;
    
    // The following universal user-associated attributes are for convenience only and are not mapped to OJB or the the DB.
    private String disbVchrEmployeeIdNumber;
    
    
    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherPayeeDetail() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the disbVchrPaymentReasonCode attribute.
     * 
     * @return Returns the disbVchrPaymentReasonCode
     */
    public String getDisbVchrPaymentReasonCode() {
        return disbVchrPaymentReasonCode;
    }


    /**
     * Sets the disbVchrPaymentReasonCode attribute.
     * 
     * @param disbVchrPaymentReasonCode The disbVchrPaymentReasonCode to set.
     */
    public void setDisbVchrPaymentReasonCode(String disbVchrPaymentReasonCode) {
        this.disbVchrPaymentReasonCode = disbVchrPaymentReasonCode;
    }

     /**
      * Gets the disbVchrPayeeIdNumber attribute.
      * 
      * @return Returns the disbVchrVendorIdNumber
      */
     public String getDisbVchrPayeeIdNumber() {
         return disbVchrPayeeIdNumber;
     }
    
    
     /**
      * Sets the disbVchrPayeeIdNumber attribute.
      * 
      * @param disbVchrPayeeIdNumber The disbVchrPayeeIdNumber to set.
      */
     public void setDisbVchrPayeeIdNumber(String disbVchrPayeeIdNumber) {
         this.disbVchrPayeeIdNumber = disbVchrPayeeIdNumber;
     }
    
     /**
      * 
      * This method...
      * @return
      */
     public String getDisbVchrVendorHeaderIdNumber() {
         if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
             if(StringUtils.isBlank(disbVchrVendorHeaderIdNumber)) {
                 int dashIndex = disbVchrPayeeIdNumber.indexOf('-');
                 disbVchrVendorHeaderIdNumber = disbVchrPayeeIdNumber.substring(0, dashIndex);
             }
         }
         else { // Return null if payee is not a vendor
             return null;
         }
         return disbVchrVendorHeaderIdNumber;
     }
    
     /**
      * 
      * This method...
      * @param disbVchrVendorheaderIdNumber
      */
     public void setDisbVchrVendorHeaderIdNumber(String disbVchrVendorHeaderIdNumber) {
         // This field should only be set if the payee type is "V", otherwise, ignore any calls
         if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
             this.disbVchrVendorHeaderIdNumber = disbVchrVendorHeaderIdNumber;
         }
     }
    
     /**
      * Gets the disbVchrVendorIdNumber attribute as an Integer.
      * 
      * @return Returns the disbVchrVendorIdNumber in Integer form.  This is the format used on the VendorDetail.
      */
     public Integer getDisbVchrVendorHeaderIdNumberAsInteger() {
         if(getDisbVchrVendorHeaderIdNumber()!=null) try {
             return new Integer(getDisbVchrVendorHeaderIdNumber());            
         } catch(NumberFormatException nfe) {
             nfe.printStackTrace();
         }
         return null;
     }
    
    
     /**
      * 
      * This method...
      * @return
      */
     public String getDisbVchrVendorDetailAssignedIdNumber() {
         if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
             if(StringUtils.isBlank(disbVchrVendorDetailAssignedIdNumber)) {
                 int dashIndex = disbVchrPayeeIdNumber.indexOf('-');
                 disbVchrVendorDetailAssignedIdNumber = disbVchrPayeeIdNumber.substring(dashIndex+1);
             }
         }
         else { // Return null if payee is not a vendor
             return null;
         }
         return disbVchrVendorDetailAssignedIdNumber;
     }
    
     /**
      * 
      * This method...
      * @param disbVchrVendorDetailAssignedIdNumber
      */
     public void setDisbVchrVendorDetailAssignedIdNumber(String disbVchrVendorDetailAssignedIdNumber) {
         // This field should only be set if the payee type is "V", otherwise, ignore any calls
         if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
             this.disbVchrVendorDetailAssignedIdNumber = disbVchrVendorDetailAssignedIdNumber;
         }
     }
    
     /**
      * 
      * This method...
      * @return
      */
     public Integer getDisbVchrVendorDetailAssignedIdNumberAsInteger() {
         if(getDisbVchrVendorDetailAssignedIdNumber()!=null) try {
             return new Integer(getDisbVchrVendorDetailAssignedIdNumber());
         } catch(NumberFormatException nfe) {
             nfe.printStackTrace();
         }
         return null;
     }
    
     /**
      * 
      * This method should only be called for retrieving the id associated with the payee if the payee type is equal to "E".
      * Otherwise, this method will return null.
      * 
      * @return The id of the universal user set as the payee on the DV, if the payee type code indicates the payee is an employee.
      * Otherwise, this method will return null.
      */
     public String getDisbVchrEmployeeIdNumber() {
         if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
             if(StringUtils.isBlank(disbVchrEmployeeIdNumber)) {
                 disbVchrEmployeeIdNumber = disbVchrPayeeIdNumber;
             }
         }
         else { // Return null if payee is not a employee
             return null;
         }
         return disbVchrEmployeeIdNumber;
     }
    
     /**
      * 
      * This method...
      * @param disbVchrUniversalUserIdNumber
      */
     public void setDisbVchrEmployeeIdNumber(String disbVchrEmployeeIdNumber) {
         // This field should only be set if the payee type is "E", otherwise, ignore any calls
         if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
             this.disbVchrEmployeeIdNumber = disbVchrEmployeeIdNumber;
         }
     }
    
    /**
     * Gets the disbVchrPayeePersonName attribute.
     * 
     * @return Returns the disbVchrPayeePersonName
     */
    public String getDisbVchrPayeePersonName() {
        return disbVchrPayeePersonName;
    }

    /**
     * Sets the disbVchrPayeePersonName attribute.
     * 
     * @param disbVchrPayeePersonName The disbVchrPayeePersonName to set.
     */
    public void setDisbVchrPayeePersonName(String disbVchrPayeePersonName) {
        this.disbVchrPayeePersonName = disbVchrPayeePersonName;
    }

    /**
     * Gets the disbVchrPayeeLine1Addr attribute.
     * 
     * @return Returns the disbVchrPayeeLine1Addr
     */
    public String getDisbVchrPayeeLine1Addr() {
        return disbVchrPayeeLine1Addr;
    }

    /**
     * Sets the disbVchrPayeeLine1Addr attribute.
     * 
     * @param disbVchrPayeeLine1Addr The disbVchrPayeeLine1Addr to set.
     */
    public void setDisbVchrPayeeLine1Addr(String disbVchrPayeeLine1Addr) {
        this.disbVchrPayeeLine1Addr = disbVchrPayeeLine1Addr;
    }

    /**
     * Gets the disbVchrPayeeLine2Addr attribute.
     * 
     * @return Returns the disbVchrPayeeLine2Addr
     */
    public String getDisbVchrPayeeLine2Addr() {
        return disbVchrPayeeLine2Addr;
    }

    /**
     * Sets the disbVchrPayeeLine2Addr attribute.
     * 
     * @param disbVchrPayeeLine2Addr The disbVchrPayeeLine2Addr to set.
     */
    public void setDisbVchrPayeeLine2Addr(String disbVchrPayeeLine2Addr) {
        this.disbVchrPayeeLine2Addr = disbVchrPayeeLine2Addr;
    }

    /**
     * Gets the disbVchrPayeeCityName attribute.
     * 
     * @return Returns the disbVchrPayeeCityName
     */
    public String getDisbVchrPayeeCityName() {
        return disbVchrPayeeCityName;
    }


    /**
     * Sets the disbVchrPayeeCityName attribute.
     * 
     * @param disbVchrPayeeCityName The disbVchrPayeeCityName to set.
     */
    public void setDisbVchrPayeeCityName(String disbVchrPayeeCityName) {
        this.disbVchrPayeeCityName = disbVchrPayeeCityName;
    }

    /**
     * Gets the disbVchrPayeeStateCode attribute.
     * 
     * @return Returns the disbVchrPayeeStateCode
     */
    public String getDisbVchrPayeeStateCode() {
        return disbVchrPayeeStateCode;
    }


    /**
     * Sets the disbVchrPayeeStateCode attribute.
     * 
     * @param disbVchrPayeeStateCode The disbVchrPayeeStateCode to set.
     */
    public void setDisbVchrPayeeStateCode(String disbVchrPayeeStateCode) {
        this.disbVchrPayeeStateCode = disbVchrPayeeStateCode;
    }

    /**
     * Gets the disbVchrPayeeZipCode attribute.
     * 
     * @return Returns the disbVchrPayeeZipCode
     */
    public String getDisbVchrPayeeZipCode() {
        return disbVchrPayeeZipCode;
    }


    /**
     * Sets the disbVchrPayeeZipCode attribute.
     * 
     * @param disbVchrPayeeZipCode The disbVchrPayeeZipCode to set.
     */
    public void setDisbVchrPayeeZipCode(String disbVchrPayeeZipCode) {
        this.disbVchrPayeeZipCode = disbVchrPayeeZipCode;
    }

    /**
     * Gets the disbVchrPayeeCountryCode attribute.
     * 
     * @return Returns the disbVchrPayeeCountryCode
     */
    public String getDisbVchrPayeeCountryCode() {
        return disbVchrPayeeCountryCode;
    }


    /**
     * Sets the disbVchrPayeeCountryCode attribute.
     * 
     * @param disbVchrPayeeCountryCode The disbVchrPayeeCountryCode to set.
     */
    public void setDisbVchrPayeeCountryCode(String disbVchrPayeeCountryCode) {
        this.disbVchrPayeeCountryCode = disbVchrPayeeCountryCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingPersonName attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingPersonName
     */
    public String getDisbVchrSpecialHandlingPersonName() {
        return disbVchrSpecialHandlingPersonName;
    }


    /**
     * Sets the disbVchrSpecialHandlingPersonName attribute.
     * 
     * @param disbVchrSpecialHandlingPersonName The disbVchrSpecialHandlingPersonName to set.
     */
    public void setDisbVchrSpecialHandlingPersonName(String disbVchrSpecialHandlingPersonName) {
        this.disbVchrSpecialHandlingPersonName = disbVchrSpecialHandlingPersonName;
    }

    /**
     * Gets the disbVchrSpecialHandlingLine1Addr attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingLine1Addr
     */
    public String getDisbVchrSpecialHandlingLine1Addr() {
        return disbVchrSpecialHandlingLine1Addr;
    }


    /**
     * Sets the disbVchrSpecialHandlingLine1Addr attribute.
     * 
     * @param disbVchrSpecialHandlingLine1Addr The disbVchrSpecialHandlingLine1Addr to set.
     */
    public void setDisbVchrSpecialHandlingLine1Addr(String disbVchrSpecialHandlingLine1Addr) {
        this.disbVchrSpecialHandlingLine1Addr = disbVchrSpecialHandlingLine1Addr;
    }

    /**
     * Gets the disbVchrSpecialHandlingLine2Addr attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingLine2Addr
     */
    public String getDisbVchrSpecialHandlingLine2Addr() {
        return disbVchrSpecialHandlingLine2Addr;
    }


    /**
     * Sets the disbVchrSpecialHandlingLine2Addr attribute.
     * 
     * @param disbVchrSpecialHandlingLine2Addr The disbVchrSpecialHandlingLine2Addr to set.
     */
    public void setDisbVchrSpecialHandlingLine2Addr(String disbVchrSpecialHandlingLine2Addr) {
        this.disbVchrSpecialHandlingLine2Addr = disbVchrSpecialHandlingLine2Addr;
    }

    /**
     * Gets the disbVchrSpecialHandlingCityName attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingCityName
     */
    public String getDisbVchrSpecialHandlingCityName() {
        return disbVchrSpecialHandlingCityName;
    }


    /**
     * Sets the disbVchrSpecialHandlingCityName attribute.
     * 
     * @param disbVchrSpecialHandlingCityName The disbVchrSpecialHandlingCityName to set.
     */
    public void setDisbVchrSpecialHandlingCityName(String disbVchrSpecialHandlingCityName) {
        this.disbVchrSpecialHandlingCityName = disbVchrSpecialHandlingCityName;
    }

    /**
     * Gets the disbVchrSpecialHandlingStateCode attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingStateCode
     */
    public String getDisbVchrSpecialHandlingStateCode() {
        return disbVchrSpecialHandlingStateCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingStateCode attribute.
     * 
     * @param disbVchrSpecialHandlingStateCode The disbVchrSpecialHandlingStateCode to set.
     */
    public void setDisbVchrSpecialHandlingStateCode(String disbVchrSpecialHandlingStateCode) {
        this.disbVchrSpecialHandlingStateCode = disbVchrSpecialHandlingStateCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingZipCode attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingZipCode
     */
    public String getDisbVchrSpecialHandlingZipCode() {
        return disbVchrSpecialHandlingZipCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingZipCode attribute.
     * 
     * @param disbVchrSpecialHandlingZipCode The disbVchrSpecialHandlingZipCode to set.
     */
    public void setDisbVchrSpecialHandlingZipCode(String disbVchrSpecialHandlingZipCode) {
        this.disbVchrSpecialHandlingZipCode = disbVchrSpecialHandlingZipCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingCountryCode attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingCountryCode
     */
    public String getDisbVchrSpecialHandlingCountryCode() {
        return disbVchrSpecialHandlingCountryCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingCountryCode attribute.
     * 
     * @param disbVchrSpecialHandlingCountryCode The disbVchrSpecialHandlingCountryCode to set.
     */
    public void setDisbVchrSpecialHandlingCountryCode(String disbVchrSpecialHandlingCountryCode) {
        this.disbVchrSpecialHandlingCountryCode = disbVchrSpecialHandlingCountryCode;
    }

    /**
     * Gets the disbVchrPayeeEmployeeCode attribute.
     * 
     * @return Returns true if the vendor associated with this DV is an employee of the institution.
     */
    public boolean isDisbVchrPayeeEmployeeCode() {
        if(ObjectUtils.isNull(disbVchrPayeeEmployeeCode)) {
            if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
                disbVchrPayeeEmployeeCode = true;
            }
            else if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
                try {
                    disbVchrPayeeEmployeeCode = SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(getDisbVchrVendorHeaderIdNumberAsInteger());
                    this.setDisbVchrEmployeePaidOutsidePayrollCode(disbVchrPayeeEmployeeCode);
                } catch(Exception ex) {
                    disbVchrPayeeEmployeeCode = false;
                    ex.printStackTrace();
                }
            }
        }
        return disbVchrPayeeEmployeeCode;
    }


    /**
     * Sets the disbVchrPayeeEmployeeCode attribute.
     * 
     * @param disbVchrPayeeEmployeeCode The disbVchrPayeeEmployeeCode to set.
     */
    public void setDisbVchrPayeeEmployeeCode(boolean disbVchrPayeeEmployeeCode) {
        this.disbVchrPayeeEmployeeCode = disbVchrPayeeEmployeeCode;
    }

    /**
     * Gets the disbVchrAlienPaymentCode attribute.
     * 
     * @return Returns the disbVchrAlienPaymentCode
     */
    public boolean isDisbVchrAlienPaymentCode() {
        if(ObjectUtils.isNull(disbVchrAlienPaymentCode)) {
            if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
                disbVchrAlienPaymentCode = false; // TODO how do you figure out if an employee is an alien???
            }
            else if(StringUtils.equals(disbursementVoucherPayeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
                try {
                    disbVchrAlienPaymentCode = SpringContext.getBean(VendorService.class).isVendorForeign(getDisbVchrVendorHeaderIdNumberAsInteger());
                } catch(Exception ex) {
                    disbVchrAlienPaymentCode = false;
                    ex.printStackTrace();
                }
            }
        }
        return disbVchrAlienPaymentCode;
    }


    /**
     * Sets the disbVchrAlienPaymentCode attribute.
     * 
     * @param disbVchrAlienPaymentCode The disbVchrAlienPaymentCode to set.
     */
    public void setDisbVchrAlienPaymentCode(boolean disbVchrAlienPaymentCode) {
        this.disbVchrAlienPaymentCode = disbVchrAlienPaymentCode;
    }

    /**
     * Gets the dvPayeeSubjectPayment attribute.
     * 
     * @return Returns the dvPayeeSubjectPayment
     */
    public boolean isDvPayeeSubjectPaymentCode() {
        if(ObjectUtils.isNull(dvPayeeSubjectPaymentCode)) {
            dvPayeeSubjectPaymentCode = SpringContext.getBean(VendorService.class).isSubjectPaymentVendor(getDisbVchrVendorHeaderIdNumberAsInteger());
        }
        return dvPayeeSubjectPaymentCode;
    }

    /**
     * Sets the dvPayeeSubjectPayment attribute.
     * 
     * @param dvPayeeSubjectPayment The dvPayeeSubjectPayment to set.
     */
    public void setDvPayeeSubjectPaymentCode(boolean dvPayeeSubjectPaymentCode) {
        this.dvPayeeSubjectPaymentCode = dvPayeeSubjectPaymentCode;
    }

    /**
     * Gets the disbVchrEmployeePaidOutsidePayrollCode attribute. 
     * @return Returns the disbVchrEmployeePaidOutsidePayrollCode.
     */
    public boolean isDisbVchrEmployeePaidOutsidePayrollCode() {
        return disbVchrEmployeePaidOutsidePayrollCode;
    }

    /**
     * Gets the disbVchrEmployeePaidOutsidePayrollCode attribute. 
     * @return Returns the disbVchrEmployeePaidOutsidePayrollCode.
     */
    public boolean getDisbVchrEmployeePaidOutsidePayrollCode() {
        return disbVchrEmployeePaidOutsidePayrollCode;
    }

    /**
     * Sets the disbVchrEmployeePaidOutsidePayrollCode attribute value.
     * @param disbVchrEmployeePaidOutsidePayrollCode The disbVchrEmployeePaidOutsidePayrollCode to set.
     */
    public void setDisbVchrEmployeePaidOutsidePayrollCode(boolean disbVchrEmployeePaidOutsidePayrollCode) {
        this.disbVchrEmployeePaidOutsidePayrollCode = disbVchrEmployeePaidOutsidePayrollCode;
    }
    
    /**
     * Gets the disbVchrPaymentReason attribute.
     * 
     * @return Returns the disbVchrPaymentReason
     */
    public PaymentReasonCode getDisbVchrPaymentReason() {
        return disbVchrPaymentReason;
    }


    /**
     * Sets the disbVchrPaymentReason attribute.
     * 
     * @param disbVchrPaymentReason The disbVchrPaymentReason to set.
     * @deprecated
     */
    public void setDisbVchrPaymentReason(PaymentReasonCode disbVchrPaymentReason) {
        this.disbVchrPaymentReason = disbVchrPaymentReason;
    }

    /**
     * @return Returns the disbursementVoucherPayeeTypeCode.
     */
    public String getDisbursementVoucherPayeeTypeCode() {
        return disbursementVoucherPayeeTypeCode;
    }

    /**
     * @param disbursementVoucherPayeeTypeCode The disbursementVoucherPayeeTypeCode to set.
     */
    public void setDisbursementVoucherPayeeTypeCode(String disbursementVoucherPayeeTypeCode) {
        this.disbursementVoucherPayeeTypeCode = disbursementVoucherPayeeTypeCode;
    }

    /**
     * @return Returns the payee type name
     */
    public String getDisbursementVoucherPayeeTypeName() {
        return new PayeeTypeValuesFinder().getKeyLabel(disbursementVoucherPayeeTypeCode);
    }

    /**
     * 
     * This method is a dummy method defined for OJB.
     * @param name
     */
    public void setDisbursementVoucherPayeeTypeName(String name) {
    }

    /**
     * Returns the name associated with the payment reason name
     * 
     * @return
     */
    public String getDisbVchrPaymentReasonName() {
        return new PaymentReasonValuesFinder().getKeyLabel(disbVchrPaymentReasonCode);
    }

    /**
     * This method is a dummy method defined for OJB.
     * @param name
     */
    public void setDisbVchrPaymentReasonName(String name) {
    }

    /**
     * Gets the disbVchrVendorAddressIdNumber attribute. 
     * @return Returns the disbVchrVendorAddressIdNumber.
     */
    public String getDisbVchrVendorAddressIdNumber() {
        return disbVchrVendorAddressIdNumber;
    }

    /**
     * Gets the disbVchrVendorAddressIdNumber attribute. 
     * @return Returns the disbVchrVendorAddressIdNumber.
     */
    public Integer getDisbVchrVendorAddressIdNumberAsInteger() {
        if(getDisbVchrVendorAddressIdNumber()!=null) try {
            return new Integer(getDisbVchrVendorAddressIdNumber());
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the disbVchrVendorAddressIdNumber attribute value.
     * @param disbVchrVendorAddressIdNumber The disbVchrVendorAddressIdNumber to set.
     */
    public void setDisbVchrVendorAddressIdNumber(String disbVchrVendorAddressIdNumber) {
        this.disbVchrVendorAddressIdNumber = disbVchrVendorAddressIdNumber;
    }

    /**
     * Gets the hasMultipleVendorAddresses attribute. 
     * @return Returns the hasMultipleVendorAddresses.
     */
    public Boolean getHasMultipleVendorAddresses() {
        return hasMultipleVendorAddresses;
    }

    /**
     * Sets the hasMultipleVendorAddresses attribute value.
     * @param hasMultipleVendorAddresses The hasMultipleVendorAddresses to set.
     */
    public void setHasMultipleVendorAddresses(boolean hasMultipleVendorAddresses) {
        this.hasMultipleVendorAddresses = hasMultipleVendorAddresses;
    }

    /**
     * Checks the payee type code for vendor type
     * 
     * @return
     */
    public boolean isVendor() {
        return DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR.equals(disbursementVoucherPayeeTypeCode);
    }

    /**
     * Checks the payee type code for employee type
     * 
     * @return
     */
    public boolean isEmployee() {
        // If is vendor, then check vendor employee flag
        if(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR.equals(disbursementVoucherPayeeTypeCode)) {
            return SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(getDisbVchrVendorHeaderIdNumberAsInteger());
        }
        
        // Otherwise, just check payee type code equal to "E"
        return DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE.equals(disbursementVoucherPayeeTypeCode);
     }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * 
     * This method...
     * @param compareDetail
     * @return
     */
    public boolean hasSameAddress(DisbursementVoucherPayeeDetail compareDetail) {
        boolean isEqual = true;
        
        isEqual &= ObjectUtils.nullSafeEquals(this.disbVchrPayeeLine1Addr, compareDetail.disbVchrPayeeLine1Addr);
        isEqual &= ObjectUtils.nullSafeEquals(this.disbVchrPayeeLine2Addr, compareDetail.disbVchrPayeeLine2Addr);
        isEqual &= ObjectUtils.nullSafeEquals(this.disbVchrPayeeCityName, compareDetail.disbVchrPayeeCityName);
        isEqual &= ObjectUtils.nullSafeEquals(this.disbVchrPayeeStateCode, compareDetail.disbVchrPayeeStateCode);
        isEqual &= ObjectUtils.nullSafeEquals(this.disbVchrPayeeZipCode, compareDetail.disbVchrPayeeZipCode);
        isEqual &= ObjectUtils.nullSafeEquals(this.disbVchrPayeeCountryCode, compareDetail.disbVchrPayeeCountryCode);
        
        return isEqual;
    }
    
    /**
     * 
     * This method creates a string representation of the address assigned to this payee.
     * @return
     */
    public String getAddressAsString() {
        StringBuffer address = new StringBuffer();
        
        address.append(this.disbVchrPayeeLine1Addr).append(", ");
        address.append(this.disbVchrPayeeLine2Addr).append(", ");
        address.append(this.disbVchrPayeeCityName).append(", ");
        address.append(this.disbVchrPayeeStateCode).append(" ");
        address.append(this.disbVchrPayeeZipCode).append(", ");
        address.append(this.disbVchrPayeeCountryCode);
        
        return address.toString();
    }

}
