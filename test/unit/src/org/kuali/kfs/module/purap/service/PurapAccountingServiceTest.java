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
package org.kuali.kfs.module.purap.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kuluser;
import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApSummaryItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.fixture.PurapAccountingServiceFixture;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = kuluser, shouldCommitTransactions=true)
public class PurapAccountingServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceTest.class);

    private PurapAccountingService purapAccountingService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(purapAccountingService == null) {
            purapAccountingService = SpringContext.getBean(PurapAccountingService.class);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        purapAccountingService = null;
        super.tearDown();
    }
        
    /*
     * Tests of generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz)
     */
    
    /**
     * Used by tests of generateAccountDistributionForProration and related methods to make comparisons between
     * the percentages given by the resulting distributed accounts and the percentages that we think should be
     * correct for those lines.
     * 
     * @param distributedAccounts       A List of the PurApAccountingLines that result from the distribution to be tested.
     * @param correctPercents           A List of percents we think should be correct, in BigDecimal format
     */
    private void comparePercentages(List<PurApAccountingLine> distributedAccounts, List<BigDecimal> correctPercents) {
        for(int i = 0; i < distributedAccounts.size(); i++) {
            PurApAccountingLine line = distributedAccounts.get(i);
            BigDecimal percent = line.getAccountLinePercent();
            assertTrue(percent.floatValue() == correctPercents.get(i).floatValue());
        }
    }
    
    public void testGenerateAccountDistributionForProration_OneAcctGood() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_ONE_ACCOUNT;
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProration(
                                                                                fixture.getSourceAccountingLineList(),
                                                                                fixture.getTotalAmount(),
                                                                                fixture.getPercentScale(),
                                                                                fixture.getAccountClass());
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("100"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    public void testGenerateAccountDistributionForProration_TwoAcctGood() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_TWO_ACCOUNTS;
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProration(
                                                                                fixture.getSourceAccountingLineList(),
                                                                                fixture.getTotalAmount(),
                                                                                fixture.getPercentScale(),
                                                                                fixture.getAccountClass());
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("50"));
        correctPercents.add(1,new BigDecimal("50"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    public void testGenerateAccountDistributionForProration_ThreeAccountGood() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_THIRDS;
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProration(
                                                                                fixture.getSourceAccountingLineList(),
                                                                                fixture.getTotalAmount(),
                                                                                fixture.getPercentScale(),
                                                                                fixture.getAccountClass());
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("33"));
        correctPercents.add(1,new BigDecimal("33"));
        correctPercents.add(2,new BigDecimal("34"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    /*
     * Tests of generateAccountDistributionForProrationWithZeroTotal(PurchasingAccountsPayableDocument purapdoc)
     */
    /*@ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateAccountDistributionForProration_OneAcctZeroTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_ONE_ACCOUNT_ZERO_TOTAL;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProrationWithZeroTotal(preq);
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("100"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateAccountDistributionForProration_TwoAcctZeroTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_TWO_ACCOUNTS_ZERO_TOTAL;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProrationWithZeroTotal(preq);
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("50"));
        correctPercents.add(1,new BigDecimal("50"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateAccountDistributionForProration_ThreeAccountZeroTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_THIRDS_ZERO_TOTAL;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProrationWithZeroTotal(preq);
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("33"));
        correctPercents.add(1,new BigDecimal("33"));
        correctPercents.add(2,new BigDecimal("34"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }*/
    
    /*
     * Tests of generateSummaryAccounts(PurchasingAccountsPayableDocument document)
     */
    
    /**
     * Used by tests of generateSummaryAccounts and related methods to marshall the comparisons of the accounts 
     * and items of the generated summary accounts.
     * 
     * @param fixture       The PurapAccountingServiceFixture
     * @param document      A PurchasingAccountsPayableDocument generated from the fixture.
     */
    private void makePerAccountComparisons(PurapAccountingServiceFixture fixture, PurchasingAccountsPayableDocument doc) {
        int itemCount = doc.getItems().size();
        List<SummaryAccount> accounts = purapAccountingService.generateSummaryAccounts(doc);
        
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        assertEquals(accounts.size(),originalSourceAccounts.size());
        for(int i = 0; i < originalSourceAccounts.size(); i++) {
            SummaryAccount account = accounts.get(i);
            SourceAccountingLine sourceAccount = account.getAccount();
            compareSourceAccounts(sourceAccount, originalSourceAccounts.get(i));
            List<PurApSummaryItem> summaryItems = account.getItems();
            assertTrue(summaryItems.size() <= itemCount);
        }
    }
    
    /**
     * Compares SourceAccounts to see whether those fields which are displayed in the SummaryAccount tab are
     * faithfully represented, with the exception of the Amount, which changes.
     * 
     * @param sourceAccount         The generated SourceAccountingLine
     * @param correctSourceAccount  The SourceAccountingLine which we think should contain correct values
     */
    private void compareSourceAccounts(SourceAccountingLine sourceAccount, SourceAccountingLine correctSourceAccount) {
        Map source = sourceAccount.getValuesMap();
        Map correct = correctSourceAccount.getValuesMap();
        assertEquals(source.get("chartOfAccountsCode"),correct.get("chartOfAccountsCode"));
        assertEquals(source.get("accountNumber"),correct.get("accountNumber"));
        assertEquals(source.get("subAccountNumber"),correct.get("subAccountNumber"));
        assertEquals(source.get("financialObjectCode"),correct.get("financialObjectCode"));
        assertEquals(source.get("financialSubObjectCode"),correct.get("financialSubObjectCode"));
        assertEquals(source.get("projectCode"),correct.get("projectCode"));
        assertEquals(source.get("organizationReferenceId"),correct.get("organizationReferenceId"));
        assertEquals(source.get("organizationDocumentNumber"),correct.get("organizationDocumentNumber"));
    }
    
    public void testGenerateSummaryAccounts_OneRequisitionAccountOneItemWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generateRequisitionDocument_OneItem();
        makePerAccountComparisons(fixture, doc);
    }
    
    public void testGenerateSummaryAccounts_OneRequisitionAccountTwoItemsWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generateRequisitionDocument_TwoItems();
        makePerAccountComparisons(fixture, doc);
    }
    
    /*public void testGenerateSummaryAccounts_TwoRequisitionAccountsTwoItems_OneEach_WithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_TWO_ACCOUNTS;
        PurchasingAccountsPayableDocument doc = fixture.generateRequisitionDocument_TwoItems();
        //TODO: Failing.  Needs work.
        makePerAccountComparisons(fixture, doc);
    }*/
    
    /*@ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateSummaryAccounts_OnePREQAccountOneItemWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generatePaymentRequestDocument_OneItem();
        makePerAccountComparisons(fixture, doc);
    }*/
    
    /*@ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateSummaryAccounts_OnePREQAccountTwoItemsWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generatePaymentRequestDocument_TwoItems();
        makePerAccountComparisons(fixture, doc);
    }*/
    
    /*
     * Tests of generateSummaryAccountsWithNoZeroTotals(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummaryAccountsWithNoZeroTotalsNoUseTax(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummary(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryWithNoZeroTotals(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryWithNoZeroTotalsNoUseTax(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items)
     */
    
    /*
     * Tests of generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes)
     */
    
    /*
     * Tests of generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes)
     */
    
    /*
     * Tests of generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes)
     */
    
    /*
     * Tests of updateAccountAmounts(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of updateItemAccountAmounts(PurApItem item)
     */
    
    /*
     * Tests of getAccountsFromItem(PurApItem item)
     */
    
    /*
     * Tests of deleteSummaryAccounts(Integer purapDocumentIdentifier)
     */
    
    /*
     * Tests of generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of convertMoneyToPercent(PaymentRequestDocument pr)
     */
    
    /*
     * Tests of generateUseTaxAccount(PurchasingAccountsPayableDocument document)
     */
}
