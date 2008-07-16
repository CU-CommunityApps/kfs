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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DocumentService;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Struts Action for Contract Manager Assignment document.
 */
public class ContractManagerAssignmentAction extends FinancialSystemTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractManagerAssignmentAction.class);

    /**
     * Do initialization for a new <code>ContractManagerAssignmentDocument</code>.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ContractManagerAssignmentDocument acmDocument = (ContractManagerAssignmentDocument) kualiDocumentFormBase.getDocument();
        acmDocument.getDocumentHeader().setDocumentDescription(PurapConstants.ASSIGN_CONTRACT_MANAGER_DEFAULT_DESC);
        acmDocument.populateDocumentWithRequisitions();
    }
    
    /**
     * Overrides the method in KualiDocumentActionBase to fetch a List of requisition documents for the
     * ContractManagerAssignmentDocument from documentService, because we need the workflowDocument to get the
     * createDate. If we don't fetch the requisition documents from the documentService, the workflowDocument
     * in the requisition's documentHeader would be null and would cause the transient flexDoc is null error.
     * That's the reason we need this override.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument (KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        ContractManagerAssignmentDocument document = (ContractManagerAssignmentDocument)kualiDocumentFormBase.getDocument();
        List<String>documentHeaderIds = new ArrayList();
        Map<String, ContractManagerAssignmentDetail>documentHeaderIdsAndDetails = new HashMap();
        
        //Compose a Map in which the keys are the document header ids of each requisition in this acm document and the values are the 
        //corresponding ContractManagerAssignmentDetail object.
        for (ContractManagerAssignmentDetail detail : (List<ContractManagerAssignmentDetail>)document.getContractManagerAssignmentDetails()) {
            documentHeaderIdsAndDetails.put(detail.getRequisition().getDocumentNumber(), detail);
        }
        //Add all of the document header ids (which are the keys of the documentHeaderIdsAndDetails  map) to the 
        //documentHeaderIds List.
        documentHeaderIds.addAll(documentHeaderIdsAndDetails.keySet());
        
        //Get a List of requisition documents from documentService so that we can have the workflowDocument as well
        List<RequisitionDocument> requisitionDocumentsFromDocService = new ArrayList();
        try {
            if ( documentHeaderIds.size() > 0 )
                requisitionDocumentsFromDocService = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(RequisitionDocument.class, documentHeaderIds);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught: " + we.getLocalizedMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
        
        //Set the documentHeader of the requisition of each of the ContractManagerAssignmentDetail to the documentHeader of
        //the requisitions resulted from the documentService, so that we'll have workflowDocument in the documentHeader.
        for (RequisitionDocument req : requisitionDocumentsFromDocService) {
            ContractManagerAssignmentDetail detail = (ContractManagerAssignmentDetail)documentHeaderIdsAndDetails.get(req.getDocumentNumber());
            detail.getRequisition().setDocumentHeader(req.getDocumentHeader());
        }
    }
}
