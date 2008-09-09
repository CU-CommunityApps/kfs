/*
 * Copyright 2008 The Kuali Foundation.
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

public class PdpKeyConstants {
    public static final String DISBURSEMENT_NUMBER_OUT_OF_RANGE_TOO_SMALL = "DisbursementNumberMaintenanceForm.endAssignedDisburseNbr.smaller";
    public static final String DISBURSEMENT_NUMBER_OUT_OF_RANGE_TOO_LARGE = "DisbursementNumberMaintenanceForm.lastAssignedDisburseNbr.outofrange";
    
    public static class BatchConstants{
        public static class ErrorMessages{
            public static final String ERROR_BATCH_CRITERIA_NONE_ENTERED ="batchSearchForm.batchcriteria.noneEntered";
            public static final String ERROR_BATCH_CRITERIA_NO_DATE = "batchSearchForm.batchcriteria.noDate";
            public static final String ERROR_BATCH_CRITERIA_SOURCE_MISSING = "batchSearchForm.batchcriteria.sourcemissing";
            public static final String ERROR_BATCH_ID_IS_NOT_NUMERIC = "error.batch.batchId.notNumeric";
            public static final String ERROR_NOTE_EMPTY = "paymentMaintenanceForm.changeText.empty";
            public static final String ERROR_NOTE_TOO_LONG = "paymentMaintenanceForm.changeText.over250";
            public static final String ERROR_PENDING_PAYMNET_GROUP_NOT_FOUND ="error.batch.pendingPaymentGroupsNotFound";
            public static final String ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_CANCEL = "error.batch.notAllPaymentGroupsOpenCannotCancel";
            public static final String ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_HOLD = "error.batch.notAllPaymentGroupsOpenCannotHold";
            public static final String ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_REMOVE_HOLD = "error.batch.notAllPaymentGroupsOpenCannotRemoveHold";
        }
        
        public static class Messages {
            public static final String BATCH_SUCCESSFULLY_CANCELED = "message.batch.successfullyCanceled";
            public static final String BATCH_SUCCESSFULLY_HOLD = "message.batch.successfullyCanceled";
            public static final String HOLD_SUCCESSFULLY_REMOVED_ON_BATCH = "message.batch.successfullyCanceled";
        }
        
        public static class LinkText{
            public static final String CANCEL_BATCH = "batchLookup.cancelBatch.text";
            public static final String HOLD_BATCH = "batchLookup.holdBatch.text";
            public static final String REMOVE_BATCH_HOLD = "batchLookup.removeBatchHold.text";
        }
        
        public static class Confirmation{
            public static final String CANCEL_BATCH_MESSAGE = "message.batch.cancel";
            public static final String CANCEL_BATCH_QUESTION = "CancelBatch";
            public static final String HOLD_BATCH_MESSAGE = "message.batch.hold";
            public static final String HOLD_BATCH_QUESTION = "HoldBatch";
            public static final String REMOVE_HOLD_BATCH_MESSAGE = "message.batch.removeHold";
            public static final String REMOVE_HOLD_BATCH_QUESTION = "RemoveHoldBatch";
            public static final Integer NOTE_TEXT_MAX_LENGTH = 250;
        }
    }
    
    public static class PaymentDetail{
        public static class ErrorMessages{
            public static final String ERROR_PAYMENT_DETAIL_CRITERIA_NOT_ENTERED = "paymentDetailLookup.criteria.noneEntered";
            public static final String ERROR_PAYMENT_DETAIL_DISBURSEMENT_NBR_INVALID = "paymentDetailLookup.disbursementNbr.invalid";
            public static final String ERROR_PAYMENT_DETAIL_PROCESS_ID_INVALID = "paymentDetailLookup.processId.invalid";
            public static final String ERROR_PAYMENT_DETAIL_PAYMENT_ID_INVALID = "paymentDetailLookup.paymentId.invalid";
            public static final String ERROR_PAYMENT_DETAIL_NET_AMOUNT_INVALID = "paymentDetailLookup.netPaymentAmount.invalid";
            public static final String ERROR_PAYMENT_DETAIL_PAYEE_ID_TYPE_CODE_NULL_WITH_PAYEE_ID = "paymentDetailLookup.payeeIdTypeCd.nullWithPayeeId";
            public static final String ERROR_PAYMENT_DETAIL_PAYEE_ID_NULL_WITH_PAYEE_ID_TYPE_CODE = "paymentDetailLookup.payeeId.nullWithPayeeIdTypeCd";
            public static final String ERROR_PAYMENT_DETAIL_CUST_DOC_NBR_LESS_THAN_2_CHARS  ="paymentDetailLookup.custPaymentDocNbr.lessThan2Chars";
            public static final String ERROR_PAYMENT_DETAIL_INVOICE_NBR_LESS_THAN_2_CHARS ="paymentDetailLookup.invoiceNbr.lessThan2Chars";
            public static final String ERROR_PAYMENT_DETAIL_REQUISITION_NBR_LESS_THAN_2_CHARS = "paymentDetailLookup.requisitionNbr.lessThan2Chars";
            public static final String ERROR_PAYMENT_DETAIL_PURCHASE_ORDER_NBR_LESS_THAN_2_CHARS = "paymentDetailLookup.purchaseOrderNbr.lessThan2Chars";
            public static final String ERROR_PAYMENT_DETAIL_PAYEE_NAME_LESS_THAN_2_CHARS = "paymentDetailLookup.payeeName.lessThan2Chars";
        }
    }
}
