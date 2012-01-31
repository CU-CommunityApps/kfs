/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBill;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsMilestone;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.KualiModuleService;

/**
 * This class tests the invoice correction process.
 */

@ConfigureContext(session = khuntley)
public class InvoiceCorrectionTest extends CGInvoiceDocumentSetupTest {

    /**
     * @see org.kuali.kfs.module.ar.document.CGInvoiceDocumentSetupTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGeneralCorrection() throws WorkflowException {
        document.correctContractsGrantsInvoiceDocument();
        assertFalse(document.getInvoiceGeneralDetail().isFinalBill());
        Iterator iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put("accountNumber", id.getAccountNumber());
            mapKey.put("chartOfAccountsCode", id.getChartOfAccountsCode());
            mapKey.put("proposalNumber", document.getProposalNumber());
            ContractsAndGrantsCGBAwardAccount awardAccount = (ContractsAndGrantsCGBAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAwardAccount.class, mapKey);
            assertFalse(awardAccount.isFinalBilled());
        }
    }

    public void testCorrectedMilestones() throws WorkflowException {
        if (document.getInvoiceGeneralDetail().getBillingFrequency().equals(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            List<InvoiceMilestone> milestones = document.getInvoiceMilestones();
            document.correctContractsGrantsInvoiceDocument();
            List<InvoiceMilestone> correctedMilestones = document.getInvoiceMilestones();
            Iterator iterator = milestones.iterator();
            Iterator correctedIterator = correctedMilestones.iterator();
            while (iterator.hasNext() || correctedIterator.hasNext()) {
                ContractsAndGrantsMilestone id = (ContractsAndGrantsMilestone) iterator.next();
                ContractsAndGrantsMilestone cid = (ContractsAndGrantsMilestone) correctedIterator.next();
                assertTrue(id.getMilestoneAmount().equals(cid.getMilestoneAmount().negated()));
                assertTrue(cid.getIsItBilled().equals("No"));
            }
        }
    }

    public void testCorrectedBills() throws WorkflowException {
        if (document.getInvoiceGeneralDetail().getBillingFrequency().equals(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            List<InvoiceBill> bills = document.getInvoiceBills();
            document.correctContractsGrantsInvoiceDocument();
            List<InvoiceBill> correctedBills = document.getInvoiceBills();
            Iterator iterator = bills.iterator();
            Iterator correctedIterator = correctedBills.iterator();
            while (iterator.hasNext() || correctedIterator.hasNext()) {
                ContractsAndGrantsBill id = (ContractsAndGrantsBill) iterator.next();
                ContractsAndGrantsBill cid = (ContractsAndGrantsBill) correctedIterator.next();
                assertTrue(id.getEstimatedAmount().equals(cid.getEstimatedAmount().negated()));
                assertTrue(cid.getIsItBilled().equals("No"));
            }
        }
    }

    public void testCorrectedInvoiceDetails() throws WorkflowException {
        List<InvoiceDetail> invoiceDetail = document.getInvoiceDetails();
        document.correctContractsGrantsInvoiceDocument();
        List<InvoiceDetail> correctedInvoiceDetail = document.getInvoiceDetails();
        Iterator iterator = invoiceDetail.iterator();
        Iterator correctedIterator = correctedInvoiceDetail.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceDetail id = (InvoiceDetail) iterator.next();
            InvoiceDetail cid = (InvoiceDetail) correctedIterator.next();
            assertTrue(id.getExpenditures().equals(cid.getExpenditures().negated()));
        }
    }

    public void testCorrectedInvoiceDetailAccounts() throws WorkflowException {
        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCode = document.getInvoiceDetailAccountObjectCodes();
        document.correctContractsGrantsInvoiceDocument();
        List<InvoiceDetailAccountObjectCode> correctedInvoiceDetailAccountObjectCode = document.getInvoiceDetailAccountObjectCodes();
        Iterator iterator = invoiceDetailAccountObjectCode.iterator();
        Iterator correctedIterator = correctedInvoiceDetailAccountObjectCode.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceDetailAccountObjectCode id = (InvoiceDetailAccountObjectCode) iterator.next();
            InvoiceDetailAccountObjectCode cid = (InvoiceDetailAccountObjectCode) correctedIterator.next();
            assertTrue(id.getCurrentExpenditures().equals(cid.getCurrentExpenditures().negated()));
        }
    }

    public void testCorrectedAccountDetails() throws WorkflowException {
        List<InvoiceAccountDetail> accountDetail = document.getAccountDetails();
        document.correctContractsGrantsInvoiceDocument();
        List<InvoiceAccountDetail> correctedAccountDetail = document.getAccountDetails();
        Iterator iterator = accountDetail.iterator();
        Iterator correctedIterator = correctedAccountDetail.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            InvoiceAccountDetail cid = (InvoiceAccountDetail) correctedIterator.next();
            assertTrue(id.getExpenditureAmount().equals(cid.getExpenditureAmount().negated()));
        }
    }

}
