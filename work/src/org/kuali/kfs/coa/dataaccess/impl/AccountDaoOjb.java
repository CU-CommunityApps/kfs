/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 *
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 *
 * You may obtain a copy of the License at:
 *
 * http://kualiproject.org/license.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.AccountResponsibility;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.dao.AccountDao;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;


/**
 * This class is the OJB implementation of the AccountDao interface.
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
*/

public class AccountDaoOjb extends PersistenceBrokerTemplate
    implements AccountDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDaoOjb.class);

    /**
     * Retrieves account business object by primary key
     * @param chartOfAccountsCode - the FIN_COA_CD of the Chart Code that is part of the composite key of Account
     * @param accountNumber - the ACCOUNT_NBR part of the composite key of Accont
     * @return Account
     * @see AccountDao
     */
    public Account getByPrimaryId(String chartOfAccountsCode,
        String accountNumber) {
        LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);

        return (Account) getObjectByQuery(QueryFactory.newQuery(Account.class,
                criteria));
    }

    /**
     * fetch the accounts that the user is either the fiscal officer
     * or a delegate of the fiscal officer
     * @param kualiUser
     * @return a list of Accounts that the user has responsibility for
     */
    public List getAccountsThatUserIsResponsibleFor(KualiUser kualiUser) {
        LOG.debug("getAccountsThatUserIsResponsibleFor() started");

        List accountResponsibilities = new ArrayList();
        accountResponsibilities.addAll(getFiscalOfficerResponsibilities(kualiUser));
        accountResponsibilities.addAll(getDelegatedResponsibilities(kualiUser));
        return accountResponsibilities;
    }

    /**
     * method to get the fo responsibilities for the account
     */
    private List getFiscalOfficerResponsibilities(KualiUser kualiUser) {
        List fiscalOfficerResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountFiscalOfficerSystemIdentifier", kualiUser.getPersonUniversalIdentifier());
        Collection accounts = getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            Account account = (Account) iter.next();
            AccountResponsibility accountResponsibility = new AccountResponsibility(AccountResponsibility.FISCAL_OFFICER_RESPONSIBILITY, new KualiDecimal("0"), new KualiDecimal("0"),"", account);
            fiscalOfficerResponsibilities.add(accountResponsibility);
        }
        return fiscalOfficerResponsibilities;
    }

    /**
     * method to get the fo delegated responsibilities for the account
     */
    private List getDelegatedResponsibilities(KualiUser kualiUser) {
        List delegatedResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountDelegateSystemId", kualiUser.getPersonUniversalIdentifier());
        Collection accountDelegates = getCollectionByQuery(QueryFactory.newQuery(Delegate.class, criteria));
        for (Iterator iter = accountDelegates.iterator(); iter.hasNext();) {
            Delegate accountDelegate = (Delegate) iter.next();
            if (accountDelegate.isAccountDelegateActiveIndicator()) {
                //	the start_date should never be null in the real world, but there is some test data that 
                // contains null startDates, therefore this check.
                if (ObjectUtils.isNotNull(accountDelegate.getAccountDelegateStartDate())) {
                    if (!accountDelegate.getAccountDelegateStartDate().after(new Date())) {
    	                Account account = getByPrimaryId(accountDelegate.getChartOfAccountsCode(), accountDelegate.getAccount().getAccountNumber());
    	                AccountResponsibility accountResponsibility = new AccountResponsibility(AccountResponsibility.DELEGATED_RESPONSIBILITY, accountDelegate.getFinDocApprovalFromThisAmt(), accountDelegate.getFinDocApprovalToThisAmount(), accountDelegate.getFinancialDocumentTypeCode(), account );
    	                delegatedResponsibilities.add(accountResponsibility);
                    }
                }
            }
        }
        return delegatedResponsibilities;
    }

    public Iterator getAllAccounts() {
        LOG.debug("getAllAccounts() started");

        Criteria criteria = new Criteria();
        return getIteratorByQuery(QueryFactory.newQuery(Account.class, criteria));
    }
}
