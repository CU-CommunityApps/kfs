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
package org.kuali.module.purap.service.impl;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.dao.RequisitionDao;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class RequisitionServiceImpl implements RequisitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private ObjectCodeService objectCodeService;
    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private PurapService purapService;
    private RequisitionDao requisitionDao;
    private UniversalUserService universalUserService;
    private UniversityDateService universityDateService;
    private VendorService vendorService;
    private KualiConfigurationService kualiConfigurationService;

    public void saveDocumentWithoutValidation(RequisitionDocument document) {
        try {
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage(); 
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }
    
    public RequisitionDocument getRequisitionById(Integer id) {
        String documentNumber = requisitionDao.getDocumentNumberForRequisitionId(id);
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                RequisitionDocument doc = (RequisitionDocument)documentService.getByDocumentHeaderId(documentNumber);
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting requisition document from document service";
                LOG.error("getRequisitionById() " + errorMessage,e);
                throw new RuntimeException(errorMessage,e);
            }
        }
        return null;
    }
    
    public boolean isAutomaticPurchaseOrderAllowed(RequisitionDocument requisition) {
        LOG.debug("isAutomaticPurchaseOrderAllowed() started");

        /*
         * The private checkAutomaticPurchaseOrderRules method contains rules to check if a requisition can become an APO (Automatic
         * Purchase Order). The method returns a string containing the reason why this method should return false. Save the reason
         * as a note on the requisition.
         */
        String note = checkAutomaticPurchaseOrderRules(requisition);
        if (StringUtils.isNotEmpty(note)) {
            note = PurapConstants.REQ_REASON_NOT_APO+note;
            try {
                Note apoNote = documentService.createNoteFromDocument(requisition,note);
                documentService.addNoteToDocument(requisition,apoNote);
            }
            catch (Exception e) {
                throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE+" "+e);
            }
            LOG.debug("isAutomaticPurchaseOrderAllowed() return false; " + note);
            return false;
        }

        LOG.debug("isAutomaticPurchaseOrderAllowed() You made it!  Your REQ can become an APO; return true.");
        return true;
    }
       
    private String checkAutomaticPurchaseOrderRules(RequisitionDocument requisition) {
        String requisitionSource = requisition.getRequisitionSourceCode();
        KualiDecimal reqTotal = requisition.getTotalDollarAmount();
        KualiDecimal apoLimit = purapService.getApoLimit(requisition.getVendorContractGeneratedIdentifier(),
          requisition.getChartOfAccountsCode(), requisition.getOrganizationCode());
        requisition.setOrganizationAutomaticPurchaseOrderLimit(apoLimit);
        
        LOG.debug("isAPO() reqId = " + requisition.getPurapDocumentIdentifier() + "; apoLimit = " + apoLimit + "; reqTotal = " + reqTotal);
        if (apoLimit == null) {
            return "APO limit is empty.";
        }
        else {
            if (reqTotal.compareTo(apoLimit) == 1) {
                return "Requisition total is greater than the APO limit.";
            }
        }

        if (reqTotal.compareTo(KFSConstants.ZERO) <= 0) {
            return "Requisition total is not greater than zero.";
        }

        for (Iterator iter = requisition.getItems().iterator(); iter.hasNext();) {
            RequisitionItem item = (RequisitionItem) iter.next();
            if (item.isItemRestrictedIndicator()) {
                return "Requisition contains an item that is marked as restricted.";
            }
        }//endfor items

        LOG.debug("isAPO() vendor #" + requisition.getVendorHeaderGeneratedIdentifier() + "-" + requisition.getVendorDetailAssignedIdentifier());
        if (requisition.getVendorHeaderGeneratedIdentifier() == null || requisition.getVendorDetailAssignedIdentifier() == null) {
            return "Vendor was not selected from the vendor database.";
        }
        else {
            VendorDetail vendorDetail = new VendorDetail();
            vendorDetail.setVendorHeaderGeneratedIdentifier(requisition.getVendorHeaderGeneratedIdentifier());
            vendorDetail.setVendorDetailAssignedIdentifier(requisition.getVendorDetailAssignedIdentifier());
            vendorDetail = (VendorDetail) businessObjectService.retrieve(vendorDetail);
            if (vendorDetail == null) {
                return "Error retrieving vendor from the database.";
            }

            requisition.setVendorRestrictedIndicator(vendorDetail.getVendorRestrictedIndicator());
            if (requisition.getVendorRestrictedIndicator() != null && requisition.getVendorRestrictedIndicator()) {
                return "Selected vendor is marked as restricted.";
            }

            if ((!PurapConstants.RequisitionSources.B2B.equals(requisitionSource)) && (requisition.getVendorContractGeneratedIdentifier() == null)) {
                UniversalUser initiator = null;
                try {
                    initiator = SpringContext.getBean(UniversalUserService.class).getUniversalUser(new AuthenticationUserId(requisition.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));
                }
                catch (UserNotFoundException e) {
                    throw new RuntimeException("Document Initiator not found " + e.getMessage());
                }
                VendorContract b2bContract = vendorService.getVendorB2BContract(vendorDetail, initiator.getCampusCode());
                if (b2bContract != null) {
                    return "Standard requisition with no contract selected but a B2B contract exists for the selected vendor.";
                }
            }
        }

        if (StringUtils.isNotEmpty(requisition.getRecurringPaymentTypeCode())) {
            return "Payment type is marked as recurring.";
        }

        if ((requisition.getPurchaseOrderTotalLimit() != null) && (KFSConstants.ZERO.compareTo(requisition.getPurchaseOrderTotalLimit()) != 0)) {
            LOG.debug("isAPO() po total limit is not null and not equal to zero; return false.");
            return "The 'PO not to exceed' amount has been entered.";
        }

        if (StringUtils.isNotEmpty(requisition.getAlternate1VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate2VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate3VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate4VendorName()) || 
                StringUtils.isNotEmpty(requisition.getAlternate5VendorName())) {
            LOG.debug("isAPO() alternate vendor name exists; return false.");
            return "Requisition contains alternate vendor names.";
        }

        return "";
    }
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }

    public void setObjectCodeService(ObjectCodeService objectService) {
        this.objectCodeService = objectService;    
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;    
    }
    
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setRequisitionDao(RequisitionDao requisitionDao) {
        this.requisitionDao = requisitionDao;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;    
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}

