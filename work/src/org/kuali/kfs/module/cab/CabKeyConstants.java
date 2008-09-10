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
package org.kuali.kfs.module.cab;


/**
 * Holds error key constants.
 */
public class CabKeyConstants {
    public static final String ERROR_SPLIT_QTY_INVALID = "error.split.qty.invalid";
    public static final String ERROR_SPLIT_QTY_REQUIRED = "error.split.qty.required";
    public static final String ERROR_PO_ID_INVALID = "error.po.id.invalid";
    public static final String ERROR_PO_ID_EMPTY = "error.po.id.empty";
    public static final String MESSAGE_NO_ACTIVE_PURAP_DOC = "message.no.active.purap.doc";
    public static final String MESSAGE_CAB_CHANGES_SAVED_SUCCESS = "message.cab.changes.saved.success";

    public static final String ERROR_ITEM_CAPITAL_AND_EXPENSE = "errors.item.capitalAsset.capital.and.expense";
    public static final String WARNING_ABOVE_THRESHOLD_SUGESTS_CAPITAL_ASSET_LEVEL = "warnings.item.capitalAsset.threshold.objectCodeLevel";
    public static final String ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE = "errors.item.capitalAsset.tranType.objectCodeSubtype";
    public static final String ERROR_ITEM_WRONG_TRAN_TYPE = "errors.item.capitalAsset.wrong.tranType";
    public static final String ERROR_ITEM_NO_TRAN_TYPE = "errors.item.capitalAsset.no.tranType";
    public static final String ERROR_MERGE_QTY_EMPTY = "error.merge.qty.empty";
    public static final String ERROR_MERGE_DESCRIPTION_EMPTY = "error.merge.description.empty";
    public static final String ERROR_ALLOCATE_NO_LINE_SELECTED = "error.allocate.no.line.selected";
    public static final String ERROR_ALLOCATE_NO_TARGET_ACCOUNT = "error.allocate.no.target.account";
    public static final String ERROR_ADDITIONAL_CHARGES_EXIST = "error.additional.charges.exist";
    public static final String ERROR_MERGE_LINE_SELECTED = "error.merge.line.selected";
    public static final String ERROR_ADDL_CHARGE_PENDING = "error.addl.charge.pending";
    public static final String ERROR_TRADE_IN_PENDING = "error.trade.in.pending";
    public static final String QUESTION_TRADE_IN_INDICATOR_EXISTING = "question.trade.in.indicator.existing";
    
    public static class CapitalAssetManagementAsset {
        public static final String ERROR_ASSET_NEW_OR_UPDATE_ONLY = "error.asset.new.or.update.only";
        public static final String ERROR_ACTIVE_CAPITAL_ASSET_REQUIRED = "error.active.capital.asset.required";
    }
}
