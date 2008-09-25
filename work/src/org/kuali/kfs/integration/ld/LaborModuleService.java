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
package org.kuali.kfs.integration.ld;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This interface is exposing the service methods that may be used by the modules outside of labor
 */
public interface LaborModuleService {

    /**
     * find the employees who were paid based on a set of specified pay type within the given report periods. Here, a pay type can
     * be determined by earn code and pay group.
     * 
     * @param payPeriods the given pay periods
     * @param balanceTypes the specified balance type codes
     * @param earnCodePayGroupMap the combination of earn codes and pay groups, where pay group is the key and earn code set is the
     *        value
     * @return the employees who were paid based on a set of specified pay type within the given report periods
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap);

    /**
     * determine whether the given employee was paid based on a set of specified pay type within the given report periods. Here, a
     * pay type can be determined by earn code and pay group.
     * 
     * @param emplid the given employee id
     * @param payPeriods the given pay periods
     * @param balanceTypes the specified balance type codes
     * @param earnCodePayGroupMap the combination of earn codes and pay groups, where pay group is the key and earn code set is the
     *        value
     * @return true if the given employee was paid based on a set of specified pay type within the given report periods; otherwise,
     *         false
     */
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap);

    /**
     * find all ledger balances matching the given criteria within the given fiscal years
     * 
     * @param fieldValues the given field values
     * @param excludedFieldValues the given field values that must not be matched
     * @param fiscalYears the given fiscal years
     * @param balanceTypeList the given balance type code list
     * @param positionObjectGroupCodes the specified position obejct group codes
     * @return all ledger balances matching the given criteria within the given fiscal years
     */
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes);

    /**
     * calculate the fringe benefit amount for the given labor object and salary amount
     * 
     * @param laborLedgerObject the given labor object
     * @param salaryAmount the given salary amount
     * @return the fringe benefit amount for the given labor object and salary amount
     */
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount);

    /**
     * calculate the fringe benefit amount for the given object code and salary amount
     * 
     * @param fiscalYear the year for object code record
     * @param chartCode the chart for object code record
     * @param objectCode the object code
     * @param salaryAmount amount to calculate benefits for
     * @return the fringe benefit amount
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount);

    /**
     * create and approve a salary expense transfer document generated from the given accounting lines
     * 
     * @param documentDescription the description about the generated document
     * @param explanation the explanation for the document
     * @param annotation the annotation as acknowledgement
     * @param adHocRecipients the given ad-hoc recipients who will be acknowledged about the document
     * @param sourceAccoutingLines the given source accounting lines used to populate document
     * @param targetAccoutingLines the given target accounting lines used to populate document
     * @throws WorkflowException occurs if the document is failed to be routed for approval
     */
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccoutingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccoutingLines) throws WorkflowException;

    /**
     * get the document numbers of the pending salary expense transfer documents for the given employee
     * 
     * @param emplid the given employee id
     * @return the document numbers of the pending salary expense transfer documents for the given employee
     */
    public int countPendingSalaryExpenseTransfer(String emplid);

    /**
     * Gets the laborLedgerObjectClass attribute.
     * 
     * @return Returns the laborLedgerObjectClass.
     */
    public Class<? extends LaborLedgerObject> getLaborLedgerObjectClass();

    /**
     * Gets the laborLedgerBalanceClass attribute.
     * 
     * @return Returns the laborLedgerBalanceClass.
     */
    public Class<? extends LaborLedgerBalance> getLaborLedgerBalanceClass();

    /**
     * Gets the laborLedgerEntryClass attribute.
     * 
     * @return Returns the laborLedgerEntryClass.
     */
    public Class<? extends LaborLedgerEntry> getLaborLedgerEntryClass();

    /**
     * Gets the laborLedgerBenefitsCalculationClass attribute.
     * 
     * @return Returns the laborLedgerBenefitsCalculationClass.
     */
    public Class<? extends LaborLedgerBenefitsCalculation> getLaborLedgerBenefitsCalculationClass();

    /**
     * Gets the expenseTransferSourceAccoutingLineClass attribute.
     * 
     * @return Returns the expenseTransferSourceAccoutingLineClass.
     */
    public Class<? extends LaborLedgerExpenseTransferSourceAccountingLine> getExpenseTransferSourceAccountingLineClass();

    /**
     * Gets the expenseTransferTargetAccoutingLineClass attribute.
     * 
     * @return Returns the expenseTransferTargetAccoutingLineClass.
     */
    public Class<? extends LaborLedgerExpenseTransferTargetAccountingLine> getExpenseTransferTargetAccountingLineClass();

    /**
     * Gets the laborLedgerBalanceForEffortCertificationClass attribute. The class is typically used by effort balance lookup
     * 
     * @return Returns the laborLedgerBalanceForEffortCertificationClass.
     */
    public Class<? extends LaborLedgerBalanceForEffortCertification> getLaborLedgerBalanceForEffortCertificationClass();

    /**
     * retrieves a specific LaborLedgerObject from the database using primary key
     * 
     * @param fiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param objectCode the given object code
     * @return a labor object retrieved based on the given information
     */
    public LaborLedgerObject retrieveLaborLedgerObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode);
    
    /**
     * retrieves a specific LaborLedgerObject based on the information of the given financial object
     * 
     * @param financialObject the given financial object
     * @return a labor object retrieved based on the given information
     */
    public LaborLedgerObject retrieveLaborLedgerObject(ObjectCode financialObject);

    /**
     * Retrieves LaborLedgerPositionObjectBenefits for a LaborLedgerObject key
     * 
     * @param fiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param objectCode the given object code
     * @return a labor position object benefit retrieved based on the given information
     */
    public Collection<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode);

    /**
     * Does the given account have any labor ledger entries? It is necessary to check this before closing an account.
     * 
     * @param account the given account
     * @return true if there is a pending entry for the given account; otherwise, return false
     */
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber);

    /**
     * Returns the fringe benefit information associated with a given fiscal year, chart, and object code
     * 
     * @param fiscalYear the fiscal year to find LaborFringeBenefitInformation records for
     * @param chartOfAccountsCode the chart of accounts code to find LaborFringeBenefitInformation records for
     * @param objectCode the object code to find LaborFringeBenefitInformation records for
     * @return a List of fringe benefit information records
     */
    public List<LaborLedgerPositionObjectBenefit> retrieveLaborObjectBenefitInformation(Integer fiscalYear, String chartOfAccountsCode, String objectCode);

    /**
     * Determines whether the given set of accounting lines have object codes that receieve fringe benefits
     * 
     * @param fiscalYear the fiscal year of the document
     * @param chartOfAccountsCode chart of accounts code to check
     * @param financialObjectCode financialObjectCode to check
     * @return true if the lines include lines with labor object codes, false otherwise
     */
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode);

    /**
     * Counts the number of entries in a group
     * 
     * @param the id of an origin entry group
     * @return the count of the entries in that group
     */
    public Integer getLaborOriginEntryGroupCount(Integer groupId);

    /**
     * Retrieves Labor Ledger position object group information, based on a position object group code
     * 
     * @param positionObjectGroupCode the code to find position object group information for
     * @return information about the correct LaborLedgerPositionObjectGroup or null if nothing was found
     */
    public LaborLedgerPositionObjectGroup getLaborLedgerPositionObjectGroup(String positionObjectGroupCode);

    /**
     * Determines where a Labor Ledger postion object group with the given code actually exists.
     * 
     * @param positionObjectGroupCode the code of the position object group to check for existence
     * @return true if the position object group exists, false otherwise
     */
    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode);
}
