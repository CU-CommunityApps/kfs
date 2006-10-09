/*
 * Created on Jan 11, 2006
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ReversalDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class ReversalDaoOjb extends PersistenceBrokerDaoSupport implements ReversalDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReversalDaoOjb.class);

    private final static String UNIVERISITY_FISCAL_YEAR = "universityFiscalYear";
    private final static String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private final static String ACCOUNT_NUMBER = "accountNumber";
    private final static String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    private final static String FINANCIAL_OBJECT_CODE = "financialObjectCode";
    private final static String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
    private final static String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
    private final static String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
    private final static String UNIVERISTY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
    private final static String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private final static String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
    private final static String FINANCIAL_DOCUMENT_NUMBER = "financialDocumentNumber";
    private final static String MAX_CONSTANT = "max(financialDocumentNumber)";

    public ReversalDaoOjb() {
        super();
    }

    /**
     * Find the maximum transactionLedgerEntrySequenceNumber in the entry table for a specific transaction. This is used to make
     * sure that rows added have a unique primary key.
     */
    public int getMaxSequenceNumber(Transaction t) {
        LOG.debug("getSequenceNumber() ");

        Criteria crit = new Criteria();
        crit.addEqualTo(UNIVERISITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(UNIVERISTY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
        crit.addEqualTo(FINANCIAL_SYSTEM_ORIGINATION_CODE, t.getFinancialSystemOriginationCode());
        crit.addEqualTo(FINANCIAL_DOCUMENT_NUMBER, t.getFinancialDocumentNumber());

        ReportQueryByCriteria q = QueryFactory.newReportQuery(Entry.class, crit);
        q.setAttributes(new String[] { "max(transactionLedgerEntrySequenceNumber)" });

        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (iter.hasNext()) {
            Object[] data = (Object[]) iter.next();
            BigDecimal max = (BigDecimal) data[0]; // Don't know why OJB returns a BigDecimal, but it does
            if (max == null) {
                return 0;
            }
            else {
                return max.intValue();
            }
        }
        else {
            return 0;
        }
    }

    public Reversal getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, t.getFinancialDocumentReversalDate());
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, t.getFinancialSystemOriginationCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, t.getFinancialDocumentNumber());
        crit.addEqualTo(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, t.getTransactionLedgerEntrySequenceNumber());

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return (Reversal) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public void save(Reversal re) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(re);
    }

    public Iterator getByDate(Date before) {
        LOG.debug("getByDate() started");

        Criteria crit = new Criteria();
        crit.addLessOrEqualThan(PropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, new java.sql.Date(before.getTime()));

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.ReversalDao#getSummaryByDate(java.util.Date)
     */
    public Iterator getSummaryByDate(Date before) {
        LOG.debug("getSummaryByDate() started");

        Criteria crit = new Criteria();
        crit.addLessOrEqualThan(PropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, new java.sql.Date(before.getTime()));
    
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Reversal.class, crit);

        String attributeList[] = { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, "sum(" + PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")", "count(" + PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE + ")" };

        String groupList[] = { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE };

        query.setAttributes(attributeList);
        query.addGroupBy(groupList);

        // add the sorting criteria
        for (int i = 0; i < groupList.length; i++) {
            query.addOrderByAscending(groupList[i]);
        }

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    public void delete(Reversal re) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(re);
    }
}
