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
package org.kuali.kfs.fp.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.util.Timer;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.fp.document.service.CashReceiptService;

/**
 * Abstract base class for all TransactionalDocumentAuthorizers, since there's this one bit of common code.
 */
public class CashReceiptDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(CashReceiptDocumentAuthorizer.class);

    /**
     * Overrides to use the parent's implementation, with the exception that if the cash drawer that is associated with this
     * document is closed, the CR doc cannot be approved at all.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        Timer t0 = new Timer("getDocumentActionFlags");
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        // if an approval is requested, check to make sure that the cash drawer is open
        // if it's not, then they should not be able to verify the CR document
        if (document.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && !document.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            CashReceiptDocument crd = (CashReceiptDocument) document;

            String unitName = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForCampusCode(crd.getCampusLocationCode());
            CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(unitName, true);
            if (cd == null) {
                throw new IllegalStateException("There is no cash drawer associated with cash receipt: " + crd.getDocumentNumber());
            }
            else if (cd.isClosed()) {
                flags.setCanBlanketApprove(false);
                flags.setCanApprove(false);
            }
        }

        flags.setCanErrorCorrect(false); // CR, DV, andd PCDO don't allow error correction

        t0.log();
        return flags;
    }

    /**
     * Overrides to always return false because there is never FO routing or FO approval for CR docs.
     * 
     * @see org.kuali.module.financial.document.FinancialDocumentAuthorizer#userOwnsAnyAccountingLine(org.kuali.core.bo.user.KualiUser,
     *      java.util.List)
     */
    @Override
    protected boolean userOwnsAnyAccountingLine(FinancialSystemUser user, List accountingLines) {
        return false;
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the CR doc.
     * 
     * @see org.kuali.core.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditableAccounts(TransactionalDocument document, UniversalUser user) {
        return new HashMap();
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the CR doc.
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#getEditableAccounts(java.util.List,
     *      org.kuali.module.chart.bo.ChartUser)
     */
    @Override
    public Map getEditableAccounts(List<AccountingLine> lines, UniversalUser user) {
        return new HashMap();
    }

    /**
     * CR docs cannot be initiated by users in a verification unit that the CR is associated with. Right now, since there is a
     * single verification unit, we only need this to check for exitence in that one.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        boolean authorized = false;
        String unitName = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(user);
        if (unitName != null) {
            authorized = !user.isMember(unitName);
        }
        if (!authorized) {
            // TODO: customize message indicating the required unitName using DocumentInitiationAuthorizationException
            throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "initiate", documentTypeName);
        }
    }
    
    /**
     * Method used by <code>{@link org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService}</code> to determine of the
     * <code>{@link CashReceiptDocument}</code> validates business rules for generating a cover page. <br/> <br/> Rule is the
     * <code>{@link Document}</code> must be ENROUTE.
     * 
     * @param document submitted cash receipt document
     * @return true if state is not cancelled, initiated, disapproved, saved, or exception
     */
    public boolean isCoverSheetPrintable(CashReceiptFamilyBase document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }
}
