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
package org.kuali.kfs.pdp;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.authorization.AuthorizationConstants.EditMode;

/**
 * Contains general PDP constants.
 */
public class PdpConstants {
    public static final String PDP_FILE_UPLOAD_FILE_PREFIX = "pdp_payment_file_";
    public static final String PAYMENT_FILE_TYPE_INDENTIFIER = "paymentInputFileType";
    public static final String PAYMENT_LOAD_CREATE_DATE_SEPARATOR = "T";
    public static final String PAYMENT_LOAD_CREATE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String PDP_FDOC_TYPE_CODE = "PDP";
    public static final String PDP_FDOC_ORIGIN_CODE = "01";
    public static final String ACH_ACCOUNT_IDENTIFIER_SEQUENCE_NAME = "PDP_ACH_ACCT_GNRTD_ID_SEQ";
    public static final String PDP_EXTRACT_JOB_NAME = "pdpExtractChecksJob";
    public static final String ACH_TRANSACTION_TYPE_DEFAULT = "22";

    public static final int CHECK_NUMBER_PLACEHOLDER_VALUE = -1;

    public static class PayeeIdTypeCodes {
        public static String SSN = "S";
        public static String EMPLOYEE_ID = "E";
        public static String FEIN = "F";
        public static String VENDOR_ID = "V";
        public static String OTHER = "X";
    }

    public static class Groups {
        // Security Workgroup names
        public static String CANCEL_GROUP = "PD_CANCEL_USERS";
        public static String HOLD_GROUP = "PD_HOLD_USERS";
        public static String LIMITEDVIEW_GROUP = "PD_LIMITED_VIEW";
        public static String PROCESS_GROUP = "PD_PROCESS_PAYMENTS";
        public static String RANGES_GROUP = "PD_RANGE_USERS";
        public static String SUBMIT_GROUP = "PD_SUBMIT_PAYMENT_FILE_USERS";
        public static String SYSADMIN_GROUP = "PD_SUPER_USERS";
        public static String TAXHOLDERS_GROUP = "PD_TAX_HOLD_USERS";
        public static String VIEWALL_GROUP = "PD_VIEW_ALL";
        public static String VIEWID_GROUP = "PD_VIEW_ID";
        public static String VIEWBANK_GROUP = "PD_VIEW_BANK";
        public static String VIEWIDPARTIALBANK_GROUP = "PD_VIEW_ID_AND PARTIAL_BANK";
    }

    public static class DisbursementTypeCodes {
        public static String CHECK = "CHCK";
        public static String ACH = "ACH";
    }

    public static class PaymentChangeCodes {
        public static final String CANCEL_DISBURSEMENT = "CD";
        public static final String CANCEL_REISSUE_DISBURSEMENT = "CRD";
        public static final String CANCEL_BATCH_CHNG_CD = "CB";
        public static final String HOLD_BATCH_CHNG_CD = "HB";
        public static final String REMOVE_HOLD_BATCH_CHNG_CD = "RHB";
        public static final String CANCEL_PAYMENT_CHNG_CD = "CP";
        public static final String HOLD_CHNG_CD = "HP";
        public static final String CHANGE_IMMEDIATE_CHNG_CD = "IMP";
        public static final String REMOVE_HOLD_CHNG_CD = "RHP";
        public static final String BANK_CHNG_CD = "BC";
    }

    public static class PaymentStatusCodes {
        public static String FORMAT = "FORM";
        public static String OPEN = KFSConstants.PdpConstants.PAYMENT_OPEN_STATUS_CODE;
        public static String CANCEL_DISBURSEMENT = "CDIS";
        public static String CANCEL_PAYMENT = "CPAY";
        public static String EXTRACTED = "EXTR";
        public static String PENDING_ACH = "PACH";
        public static final String HELD_TAX_ALL = "HTXA";
        public static final String HELD_TAX_ALL_FOR_SEARCH = "HTX*";
        public static final String HELD_CD = "HELD";
        public static final String HELD_TAX_EMPLOYEE_CD = "HTXE";
        public static final String HELD_TAX_NRA_CD = "HTXN";
        public static final String HELD_TAX_NRA_EMPL_CD = "HTXB";
    }
    
    public static class PurapParameterConstants {
        public static final String PURAP_PDP_EPIC_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
        public static final String PURAP_PDP_EPIC_SBUNT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
        public static final String PURAP_PDP_USER_ID = "PRE_DISBURSEMENT_EXTRACT_USER";
    }
    
    public static class Actions{
        public static final String BATCH_SEARCH_DETAIL_ACTION = "batchsearchDetail.do";
        public static final String PAYMENT_DETAIL_ACTION = "pdp/paymentdetailrice.do";
        public static final String FORMAT_PROCESS_ACTION = "pdp/formatrice.do";
    }
    
    public static class ActionMethods{
        public static final String CONFIRM_CANCEL_ACTION = "confirmAndCancel";
        public static final String CONFIRM_REMOVE_HOLD_ACTION = "confirmAndRemoveHold";
        public static final String CONFIRM_HOLD_ACTION = "confirmAndHold";
        public static final String CONFIRM_REMOVE_IMMEDIATE_PRINT_ACTION = "confirmAndRemoveImmediate";
        public static final String CONFIRM_SET_IMMEDIATE_PRINT_ACTION = "confirmAndSetImmediate";
        public static final String CONFIRM_DISBURSEMENT_CANCEL_ACTION = "confirmAndCancelDisbursement";
        public static final String CONFIRM_REISSUE_CANCEL_ACTION = "confirmAndReIssueCancel";
        public static final String CLEAR_FORMAT_PROCESS_ACTION = "clearUnfinishedFormat";
    }
    
    public static class AccountChangeCodes {
        public static final String INVALID_ACCOUNT = "ACCT";
        public static final String INVALID_SUB_ACCOUNT = "SA";
        public static final String INVALID_OBJECT = "OBJ";
        public static final String INVALID_SUB_OBJECT = "SO";
        public static final String INVALID_PROJECT = "PROJ";
    }
    
    public static class PaymentTypes {
        public static final String ALL = "all";
        public static final String DISBURSEMENTS_WITH_ATTACHMENTS = "pymtAttachment";
        public static final String DISBURSEMENTS_NO_ATTACHMENTS = "pymtAttachmentFalse";
        public static final String DISBURSEMENTS_WITH_SPECIAL_HANDLING = "pymtSpecialHandling";
        public static final String DISBURSEMENTS_NO_SPECIAL_HANDLING = "pymtSpecialHandlingFalse";
        public static final String PROCESS_IMMEDIATE = "immediate";
    }
    
    public static final String MAPPING_SELECTION = "selection";
    public static final String MAPPING_CONTINUE = "continue";
    public static final String MAPPING_FINISHED = "finished";
    
    public static class PDPEditMode extends EditMode {
        public static final String ENTRY = "entry";
    }

}
