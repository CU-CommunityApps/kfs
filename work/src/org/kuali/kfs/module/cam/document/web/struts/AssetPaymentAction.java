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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.PersistenceService;

public class AssetPaymentAction extends KualiAccountingDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentAction.class);

    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    //TODO remove this method.
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm apForm = (AssetPaymentForm) form;
        String command = ((AssetPaymentForm) form).getCommand();
        String docID = ((AssetPaymentForm) form).getDocId();
        String capitalAssetNumber = ((AssetPaymentForm) form).getCapitalAssetNumber();
        LOG.info("***AssetPaymentAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " - Command:" + command + " - DocId:" + docID + " - Capital Asset Number:" + capitalAssetNumber);
        return super.execute(mapping, form, request, response);
    }
    
    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     *
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) kualiDocumentFormBase;
        AssetPaymentDocument assetPaymentDocument = assetPaymentForm.getAssetPaymentDocument();

        String capitalAssetNumber = assetPaymentForm.getCapitalAssetNumber();

        //Retrieving the asset data object for the selected asset. 
        Asset asset = assetPaymentDocument.getAsset();
        
        asset = handleRequestFromLookup(capitalAssetNumber, assetPaymentForm, assetPaymentDocument, asset);
        asset = handleRequestFromLookup(capitalAssetNumber, assetPaymentDocument);

        //Populating the hidden fields in the assetPayment.jsp
        assetPaymentDocument.setCapitalAssetNumber(asset.getCapitalAssetNumber());        
        assetPaymentDocument.setPreviousTotalCostAmount(asset.getTotalCostAmount());

        //Adding the changes made in the document in the ActionForm.
        assetPaymentForm.setDocument(assetPaymentDocument);
    }*/

    /**
     * 
     * Retrieves the asset records for a selected asset number
     * @param capitalAssetNumber
     * @param assetPaymentForm
     * @param assetPaymentDocument
     * @param asset
     * @return Asset 
     */
//    private Asset handleRequestFromLookup(String capitalAssetNumber, AssetPaymentDocument assetPaymentDocument) {
//        Asset newAsset = new Asset();
//        HashMap<String, Object> keys = new HashMap<String, Object>();
//        keys.put(CAPITAL_ASSET_NUMBER, capitalAssetNumber.toString());
//
//        newAsset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, keys);
//        if (newAsset != null) {
//            assetPaymentDocument.setAsset(newAsset);
//        }
//        return newAsset;
//    }

    /**
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#uploadAccountingLines(boolean, org.apache.struts.action.ActionForm)
     */
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        
        super.uploadAccountingLines(isSource, assetPaymentForm);
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
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, assetPaymentForm.getDocument(), line));
        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(true, assetPaymentForm, line);

            // clear the used newTargetLine
            assetPaymentForm.setNewSourceLine(null);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    

    public ActionForward insertAssetPaymentAssetDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;

        //AssetPaymentAssetDetail assetPaymentAssetDetail = assetPaymentForm.getNewAssetPaymentAssetDetail();
        AssetPaymentDocument    assetPaymentDocument    = assetPaymentForm.getAssetPaymentDocument();
        AssetPaymentAssetDetail newAssetPaymentAssetDetail = new AssetPaymentAssetDetail(); 

        String capitalAssetNumber = assetPaymentForm.getCapitalAssetNumber();
        
        boolean rulePassed = true;

        // check any business rules
        //rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, assetPaymentForm.getDocument(), line));
        //TODO CREATE a RULE for this validation!!!!
        if (rulePassed) {    
            newAssetPaymentAssetDetail.setDocumentNumber(assetPaymentDocument.getDocumentNumber());
            newAssetPaymentAssetDetail.setCapitalAssetNumber(new Long(capitalAssetNumber));
            newAssetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
            newAssetPaymentAssetDetail.setPreviousTotalCostAmount(newAssetPaymentAssetDetail.getAsset().getTotalCostAmount());
            assetPaymentForm.getAssetPaymentDocument().addAssetPaymentAssetDetail(newAssetPaymentAssetDetail);
            
            // clearing 
            assetPaymentForm.setCapitalAssetNumber("");
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
                
    public ActionForward deleteAssetPaymentAssetDetail (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetPaymentForm assetPaymentForm = (AssetPaymentForm) form;
        //AssetPaymentAssetDetail assetPaymentAssetDetail = assetPaymentForm.getNewAssetPaymentAssetDetail();
        AssetPaymentDocument    assetPaymentDocument    = assetPaymentForm.getAssetPaymentDocument();

        int deleteIndex = getLineToDelete(request);

        assetPaymentForm.getAssetPaymentDocument().getAssetPaymentAssetDetail().remove(deleteIndex);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    
}