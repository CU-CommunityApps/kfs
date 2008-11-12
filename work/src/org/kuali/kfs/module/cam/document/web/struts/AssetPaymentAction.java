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
package org.kuali.kfs.module.cam.document.web.struts;

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.validation.event.AssetPaymentAddAssetEvent;
import org.kuali.kfs.module.cam.document.web.struts.AssetPaymentForm;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.web.struts.ExpenseTransferDocumentFormBase;

//TODO delete this class once the KFSMI-1869 jira has been resolved see JIRA KULCAP-782
import org.kuali.kfs.module.cam.document.service.AssetSegmentedLookupResultsService;
//*************************************************************************************

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class AssetPaymentAction extends KualiAccountingDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentAction.class);
    private static AssetPaymentService assetPaymentService = SpringContext.getBean(AssetPaymentService.class);
    private static AssetSegmentedLookupResultsService assetSegmentedLookupResultsService = SpringContext.getBean(AssetSegmentedLookupResultsService.class);
    
    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    //TODO remove this method.
/*    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm apForm = (AssetPaymentForm) form;
        String command = ((AssetPaymentForm) form).getCommand();
        String docID = ((AssetPaymentForm) form).getDocId();
        String capitalAssetNumber = ((AssetPaymentForm) form).getCapitalAssetNumber();
        LOG.info("***AssetPaymentAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " - Command:" + command + " - DocId:" + docID + " - Capital Asset Number:" + capitalAssetNumber);
        
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        AssetPaymentDocument    assetPaymentDocument    = assetPaymentForm.getAssetPaymentDocument();        
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail();
        LOG.info("*** AssetPaymentAssetDetail:"+assetPaymentAssetDetails.toString());        
        
        return super.execute(mapping, form, request, response);
    }*/
    
    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        Collection<PersistableBusinessObject> rawValues = null;
        Map<String, Set<String>> segmentedSelection = new HashMap<String, Set<String>>();

        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, assetPaymentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = assetPaymentForm.getLookupResultsSequenceNumber();

            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                Set<String> selectedIds = assetSegmentedLookupResultsService.retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
                for (String selectedId : selectedIds) {
                    String selectedObjId = StringUtils.substringBefore(selectedId, ".");
                    String selectedMonthData = StringUtils.substringAfter(selectedId, ".");

                    if (!segmentedSelection.containsKey(selectedObjId)) {
                        segmentedSelection.put(selectedObjId, new HashSet<String>());
                    }
                    segmentedSelection.get(selectedObjId).add(selectedMonthData);
                }
                //Retrieving selected data from table.
                LOG.debug("Asking segmentation service for object ids " + segmentedSelection.keySet());
                rawValues = assetSegmentedLookupResultsService.retrieveSelectedResultBOs(lookupResultsSequenceNumber, segmentedSelection.keySet(), Asset.class, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            }

            List<AssetPaymentAssetDetail> assetPaymentAssetDetails = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail(); 
            if (rawValues != null) {
                for (PersistableBusinessObject bo : rawValues) {
                    Asset asset = (Asset)bo;
                    
                    boolean addIt=true;
                    for(AssetPaymentAssetDetail detail:assetPaymentAssetDetails){
                        if (detail.getCapitalAssetNumber().compareTo(asset.getCapitalAssetNumber()) == 0) {
                            addIt=false;
                            break;
                        }                        
                    }
                    
                    //If it doesn't already exist in the list add it.
                    if (addIt) {
                        AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
                        assetPaymentAssetDetail.setDocumentNumber(assetPaymentForm.getAssetPaymentDocument().getDocumentNumber());
                        assetPaymentAssetDetail.setCapitalAssetNumber(asset.getCapitalAssetNumber());                    
                        assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);                    
                        assetPaymentAssetDetail.setPreviousTotalCostAmount(assetPaymentAssetDetail.getAsset().getTotalCostAmount());                    
                        
                        assetPaymentAssetDetails.add(assetPaymentAssetDetail);
                    }
               }
               assetPaymentForm.getAssetPaymentDocument().setAssetPaymentAssetDetail(assetPaymentAssetDetails);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#uploadAccountingLines(boolean, org.apache.struts.action.ActionForm)
     */
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;        
        super.uploadAccountingLines(isSource, assetPaymentForm);
        List <AssetPaymentDetail> assetPaymentDetails =assetPaymentForm.getAssetPaymentDocument().getSourceAccountingLines(); 
        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails ) {
            assetPaymentService.extractPostedDatePeriod(assetPaymentDetail);
        }    
    }
    
    
    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override    
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        SourceAccountingLine line = assetPaymentForm.getNewSourceLine();
        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, assetPaymentForm.getDocument(), line, KFSPropertyConstants.SOURCE_ACCOUNTING_LINES));
        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(true, assetPaymentForm, line);

            // clear the used newTargetLine
            assetPaymentForm.setNewSourceLine(new AssetPaymentDetail());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    

    /**
     * 
     * Inserts a new asset into the document
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertAssetPaymentAssetDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        AssetPaymentDocument    assetPaymentDocument    = assetPaymentForm.getAssetPaymentDocument();
        
        List<AssetPaymentAssetDetail> assetPaymentDetails = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail();
        
        AssetPaymentAssetDetail newAssetPaymentAssetDetail = new AssetPaymentAssetDetail(); 
        String sCapitalAssetNumber = assetPaymentForm.getCapitalAssetNumber();
        
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;
        
        Long capitalAssetNumber=null;
        try {
            capitalAssetNumber = new Long(Long.parseLong(sCapitalAssetNumber));
        }
        catch (NumberFormatException e) {
            sCapitalAssetNumber = (sCapitalAssetNumber == null ? " " : sCapitalAssetNumber);             
            GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, sCapitalAssetNumber);            
            return mapping.findForward(KFSConstants.MAPPING_BASIC);            
        }        
        
        boolean rulePassed = true;

        newAssetPaymentAssetDetail.setDocumentNumber(assetPaymentDocument.getDocumentNumber());
        newAssetPaymentAssetDetail.setCapitalAssetNumber(capitalAssetNumber);
        newAssetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
                
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AssetPaymentAddAssetEvent(errorPath, assetPaymentForm.getDocument(), newAssetPaymentAssetDetail));
        if (rulePassed) {      
            //Storing the current asset cost.
            newAssetPaymentAssetDetail.setPreviousTotalCostAmount(newAssetPaymentAssetDetail.getAsset().getTotalCostAmount());
            
            assetPaymentForm.getAssetPaymentDocument().addAssetPaymentAssetDetail(newAssetPaymentAssetDetail);
            assetPaymentForm.setCapitalAssetNumber("");
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
     * Deletes an asset from the document
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteAssetPaymentAssetDetail (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        int deleteIndex = getLineToDelete(request);

        //Getting the asset number that is going to be deleted from document
        Long capitalAssetNumber = assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail().get(deleteIndex).getCapitalAssetNumber();
        
        //Deleting the asset from document
        assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail().remove(deleteIndex);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    
}