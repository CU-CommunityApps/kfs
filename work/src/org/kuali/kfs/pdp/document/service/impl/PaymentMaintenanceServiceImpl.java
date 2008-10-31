/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 12, 2004
 */
package org.kuali.kfs.pdp.document.service.impl;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.AchAccountNumber;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentChangeCode;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.document.service.PaymentMaintenanceService;
import org.kuali.kfs.pdp.service.EnvironmentService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.KualiCode;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class PaymentMaintenanceServiceImpl implements PaymentMaintenanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceServiceImpl.class);

    private PaymentGroupDao paymentGroupDao;
    private PaymentDetailDao paymentDetailDao;
    private PendingTransactionService glPendingTransactionService;
    private EnvironmentService environmentService;
    private MailService mailService;
    private ParameterService parameterService;
    private KualiCodeService kualiCodeService;
    private BankService bankService;
    private BusinessObjectService businessObjectService;
    private PaymentGroupService paymentGroupService;
    
    /**
     * This method changes status for a payment group.
     * @param paymentGroup the payment group
     * @param newPaymentStatus the new payment status
     * @param changeStatus the changed payment status
     * @param note a note for payment status change
     * @param user the user that changed the status
     */
    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, Person user) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);

        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        KualiCode cd = this.kualiCodeService.getByCode(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));
        
        this.businessObjectService.save(paymentGroupHistory);

        KualiCode code = this.kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);
        paymentGroup.setPaymentStatus((PaymentStatus) code);
        this.businessObjectService.save(paymentGroup);
        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * This method changes the state of a paymentGroup.
     * @param paymentGroup the payment group to change the state for
     * @param newPaymentStatus the new payment status
     * @param changeStatus the status that is changed
     * @param note the note entered by the user
     * @param user the user that changed the 
     * @param paymentGroupHistory
     */
    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, Person user, PaymentGroupHistory paymentGroupHistory) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);

        KualiCode cd = this.kualiCodeService.getByCode(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));
        
        this.businessObjectService.save(paymentGroupHistory);

        KualiCode code = this.kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);
        if (paymentGroup.getPaymentStatus() != ((PaymentStatus) code)) {
            paymentGroup.setPaymentStatus((PaymentStatus) code);
        }
        this.businessObjectService.save(paymentGroup);

        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelPendingPayment(java.lang.Integer, java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelPendingPayment() Enter method to cancel pending payment with group id = " + paymentGroupId);
        LOG.debug("cancelPendingPayment() payment detail id being cancelled = " + paymentDetailId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelPendingPayment() Pending payment not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT.equals(paymentStatus))) {
            LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; continue with cancel.");

            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.TAXHOLDERS_GROUP) || KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.SYSADMIN_GROUP)) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);

                    // set primary cancel indicator for EPIC to use
                    Map primaryKeys = new HashMap();
                    primaryKeys.put(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ID, paymentDetailId);
                    
                    PaymentDetail pd = (PaymentDetail) this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                    if (pd != null) {
                        pd.setPrimaryCancelledPayment(Boolean.TRUE);
                    }
                    this.businessObjectService.save(pd);
                    sendCancelEmail(paymentGroup, note, user);

                    LOG.debug("cancelPendingPayment() Pending payment cancelled and mail was sent; exit method.");
                }
                else {
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                    return false;
                }
            }
            else if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus) || PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.CANCEL_GROUP)) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);

                    // set primary cancel indicator for EPIC to use
                    Map primaryKeys = new HashMap();
                    primaryKeys.put(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ID, paymentDetailId);
                    
                    PaymentDetail pd = (PaymentDetail) this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                    if (pd != null) {
                        pd.setPrimaryCancelledPayment(Boolean.TRUE);
                        PaymentNoteText payNoteText = new PaymentNoteText();
                        payNoteText.setCustomerNoteLineNbr(new KualiInteger(pd.getNotes().size()+1));
                        payNoteText.setCustomerNoteText(note);
                        pd.addNote(payNoteText);
                    }

                    this.businessObjectService.save(pd);

                    LOG.debug("cancelPendingPayment() Pending payment cancelled; exit method.");
                }
                else {
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                    return false;
                }
            }
            else {
                LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; cannot cancel payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                return false;
            }
        }
        else {
            LOG.debug("cancelPendingPayment() Pending payment group has already been cancelled; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#holdPendingPayment(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean holdPendingPayment(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("holdPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("holdPendingPayment() Pending payment not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus))) {
            if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus)) {
                LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; continue with hold.");

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.HELD_CD, PdpConstants.PaymentChangeCodes.HOLD_CHNG_CD, note, user);

                LOG.debug("holdPendingPayment() Pending payment was put on hold; exit method.");
            }
            else {
                LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; cannot hold payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_HOLD);
                return false;
            }
        }
        else {
            LOG.debug("holdPendingPayment() Pending payment group has already been held; exit method.");
        }
        return true;

    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#removeHoldPendingPayment(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean removeHoldPendingPayment(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("removeHoldPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("removeHoldPendingPayment() Payment not found; throw exception.");

            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; continue with hold removal.");

            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.TAXHOLDERS_GROUP) || KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.SYSADMIN_GROUP)) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);
                    LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
                }
                else {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                    return false;
                }
            }
            else if (PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.HOLD_GROUP)) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);

                    LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
                }
                else {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                    return false;
                }
            }
            else {
                LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; cannot remove hold on payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                return false;
            }
        }
        else {
            LOG.debug("removeHoldPendingPayment() Pending payment group has already been un-held; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#changeImmediateFlag(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public void changeImmediateFlag(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("changeImmediateFlag() Enter method to hold pending payment with id = " + paymentGroupId);
        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);

        paymentGroupHistory.setOrigProcessImmediate(paymentGroup.getProcessImmediate());

        if (paymentGroup.getProcessImmediate().equals(Boolean.TRUE)) {
            paymentGroup.setProcessImmediate(Boolean.FALSE);
        }
        else {
            paymentGroup.setProcessImmediate(Boolean.TRUE);
        }

        changeStatus(paymentGroup, paymentGroup.getPaymentStatus().getCode(), PdpConstants.PaymentChangeCodes.CHANGE_IMMEDIATE_CHNG_CD, note, user, paymentGroupHistory);

        LOG.debug("changeImmediateFlag() exit method.");
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelDisbursement(java.lang.Integer, java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);

        if (paymentGroup == null) {
            LOG.debug("cancelDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (paymentGroup.getDisbursementDate() != null)) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup element : allDisbursementPaymentGroups) {

                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if ((element.getDisbursementType() != null) && (element.getDisbursementType().getCode().equals("CHCK"))) {
                        pgh.setPmtCancelExtractStat(new Boolean("False"));
                    }

                    changeStatus(element, PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT, PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT, note, user, pgh);

                    glPendingTransactionService.generateCancellationGeneralLedgerPendingEntry(element);
                }

                // set primary cancel indicator for EPIC to use
                Map primaryKeys = new HashMap();
                primaryKeys.put(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_ID, paymentDetailId);
                
                PaymentDetail pd = (PaymentDetail) this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                if (pd != null) {
                    pd.setPrimaryCancelledPayment(Boolean.TRUE);
                }

                this.businessObjectService.save(pd);

                LOG.debug("cancelDisbursement() Disbursement cancelled; exit method.");
            }
            else {
                LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL);
                return false;
            }
        }
        else {
            LOG.debug("cancelDisbursement() Disbursement has already been cancelled; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelReissueDisbursement(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean cancelReissueDisbursement(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelReissueDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelReissueDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (paymentGroup.getDisbursementDate() != null)) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup pg : allDisbursementPaymentGroups) {
                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if ((pg.getDisbursementType() != null) && (pg.getDisbursementType().getCode().equals("CHCK"))) {
                        pgh.setPmtCancelExtractStat(new Boolean("False"));
                    }

                    pgh.setOrigProcessImmediate(pg.getProcessImmediate());
                    pgh.setOrigPmtSpecHandling(pg.getPymtSpecialHandling());
                    pgh.setBank(pg.getBank());
                    pgh.setOrigPaymentDate(pg.getPaymentDate());
                    pgh.setOrigDisburseDate(pg.getDisbursementDate());
                    pgh.setOrigAchBankRouteNbr(pg.getAchBankRoutingNbr());
                    pgh.setOrigDisburseNbr(pg.getDisbursementNbr());
                    pgh.setOrigAdviceEmail(pg.getAdviceEmailAddress());
                    pgh.setDisbursementType(pg.getDisbursementType());
                    pgh.setProcess(pg.getProcess());

                    glPendingTransactionService.generateReissueGeneralLedgerPendingEntry(pg);

                    LOG.debug("cancelReissueDisbursement() Status is '" + paymentStatus + "; delete row from AchAccountNumber table.");

                    AchAccountNumber achAccountNumber = pg.getAchAccountNumber();

                    if (achAccountNumber != null) {
                        this.businessObjectService.delete(achAccountNumber);
                        pg.setAchAccountNumber(null);
                    }

                    // if bank functionality is not enabled or the group bank is inactive clear bank code
                    if (!bankService.isBankSpecificationEnabled() || !pg.getBank().isActive()) {
                        pg.setBank(null);
                    }

                    pg.setDisbursementDate(null);
                    pg.setAchBankRoutingNbr(null);
                    pg.setAchAccountType(null);
                    pg.setPhysCampusProcessCd(null);
                    pg.setDisbursementNbr((KualiInteger) null);
                    pg.setAdviceEmailAddress(null);
                    pg.setDisbursementType(null);
                    pg.setProcess(null);
                    pg.setProcessImmediate(false);

                    changeStatus(pg, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.CANCEL_REISSUE_DISBURSEMENT, note, user, pgh);
                }

                LOG.debug("cancelReissueDisbursement() Disbursement cancelled and reissued; exit method.");
            }
            else {
                LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL_AND_REISSUE);
                return false;
            }
        }
        else {
            LOG.debug("cancelReissueDisbursement() Disbursement already cancelled and reissued; exit method.");
        }
        return true;
    }

    /**
     * This method...
     * @param paymentGroup
     * @param note
     * @param user
     */
    public void sendCancelEmail(PaymentGroup paymentGroup, String note, Person user) {
        LOG.debug("sendCancelEmail() starting");

        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Cancelled Payment by Tax");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Cancelled Payment by Tax");
        }

        CustomerProfile cp = paymentGroup.getBatch().getCustomerProfile();
        String toAddresses = cp.getAdviceReturnEmailAddr();
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }
        // message.addToAddress(cp.getAdviceReturnEmailAddr());

        String ccAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.TAX_CANCEL_EMAIL_LIST);
        String ccAddressList[] = ccAddresses.split(",");

        if (ccAddressList.length > 0) {
            for (int i = 0; i < ccAddressList.length; i++) {
                if (ccAddressList[i] != null) {
                    message.addCcAddress(ccAddressList[i].trim());
                }
            }
        }

        String fromAddressList[] = { mailService.getBatchMailingList() };

        if (fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }

        StringBuffer body = new StringBuffer();

        //TODO: this if statement seems unnecessary
        if (paymentGroup.getPaymentDetails().size() > 1) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        }

        body.append(note + "\n\n");
        String taxEmail = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.TAX_GROUP_EMAIL_ADDRESS);

        if (StringUtils.isBlank(taxEmail)) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_2);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_3);
            body.append(MessageFormat.format(messageKey, new Object[] { taxEmail }) + " \n\n");
        }

        //TODO: unnecessary if statement?
        if (paymentGroup.getPaymentDetails().size() > 1) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        }

        for (PaymentDetail pd : paymentGroup.getPaymentDetails()) {

            String payeeMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYEE_NAME);
            String netPaymentAccountMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_NET_PAYMENT_AMOUNT);
            String sourceDocumentNumberMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_SOURCE_DOCUMENT_NUMBER);
            String invoiceNumberMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_INVOICE_NUMBER);
            String purchaseOrderNumberMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PURCHASE_ORDER_NUMBER);
            String paymentDetailIdMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYMENT_DETAIL_ID);

            body.append(MessageFormat.format(payeeMessageKey, new Object[] { paymentGroup.getPayeeName() }) + " \n");
            body.append(MessageFormat.format(netPaymentAccountMessageKey, new Object[] { pd.getNetPaymentAmount() }) + " \n");
            body.append(MessageFormat.format(sourceDocumentNumberMessageKey, new Object[] { pd.getCustPaymentDocNbr() }) + " \n");
            body.append(MessageFormat.format(invoiceNumberMessageKey, new Object[] { pd.getInvoiceNbr() }) + " \n");
            body.append(MessageFormat.format(purchaseOrderNumberMessageKey, new Object[] { pd.getPurchaseOrderNbr() }) + " \n");
            body.append(MessageFormat.format(paymentDetailIdMessageKey, new Object[] { pd.getId() }) + " \n");

        }

        //TODO: unnecessary if statement?
        if (paymentGroup.getPaymentDetails().size() > 1) {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_INFORMATION_HEADER);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");

        }
        else {
            String messageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_INFORMATION_HEADER);
            body.append(MessageFormat.format(messageKey, new Object[] { null }) + " \n\n");
        }

        String batchIdMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_BATCH_ID);
        String chartMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_CHART);
        String organizationMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_ORGANIZATION);
        String subUnitMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_SUB_UNIT);
        String creationDateMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_CREATION_DATE);
        String paymentCountMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYMENT_COUNT);
        String paymentTotalAmountMessageKey = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_PAYMENT_TOTAL_AMOUNT);

        body.append(MessageFormat.format(batchIdMessageKey, new Object[] { paymentGroup.getBatch().getId() }) + " \n");
        body.append(MessageFormat.format(chartMessageKey, new Object[] { cp.getChartCode() }) + " \n");
        body.append(MessageFormat.format(organizationMessageKey, new Object[] { cp.getOrgCode() }) + " \n");
        body.append(MessageFormat.format(subUnitMessageKey, new Object[] { cp.getSubUnitCode() }) + " \n");
        body.append(MessageFormat.format(creationDateMessageKey, new Object[] { paymentGroup.getBatch().getCustomerFileCreateTimestamp() }) + " \n");
        body.append(MessageFormat.format(paymentCountMessageKey, new Object[] { paymentGroup.getBatch().getPaymentCount() }) + " \n");
        body.append(MessageFormat.format(paymentTotalAmountMessageKey, new Object[] { paymentGroup.getBatch().getPaymentTotalAmount() }) + " \n");

        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * inject
     * 
     * @param dao
     */
    public void setPaymentGroupDao(PaymentGroupDao dao) {
        paymentGroupDao = dao;
    }

    /**
     * inject
     * 
     * @param dao
     */
    public void setPaymentDetailDao(PaymentDetailDao dao) {
        paymentDetailDao = dao;
    }
 
    /**
     * inject
     * 
     * @param service
     */
    public void setGlPendingTransactionService(PendingTransactionService service) {
        glPendingTransactionService = service;
    }

    /**
     * inject
     * 
     * @param service
     */
    public void setEnvironmentService(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    /**
     * inject
     * 
     * @param service
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public KualiCodeService getKualiCodeService() {
        return kualiCodeService;
    }

    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    /**
     * Sets the bankService attribute value.
     * 
     * @param bankService The bankService to set.
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }
    
    /**
     * Gets the business object service
     * 
     * @return
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Sets the payment group service
     * 
     * @param paymentGroupService
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }
}

