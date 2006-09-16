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
package org.kuali.module.gl.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.LedgerEntry;
import org.kuali.module.gl.util.LedgerEntryHolder;

/**
 * @author jsissom
 * @author Laran Evans <lc278@cornell.edu>
 */
public class OriginEntryServiceImpl implements OriginEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

    private static final String ENTRY_GROUP_ID = "entryGroupId";
    private static final String FINANCIAL_DOCUMENT_NUMBER = "financialDocumentNumber";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";

    private OriginEntryDao originEntryDao;
    private OriginEntryGroupService originEntryGroupService;

    /**
     * 
     * @param originEntryDao
     */
    public void setOriginEntryDao(OriginEntryDao originEntryDao) {
        this.originEntryDao = originEntryDao;
    }

    /**
     * 
     * @param originEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * 
     */
    public OriginEntryServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#delete(org.kuali.module.gl.bo.OriginEntry)
     */
    public void delete(OriginEntry oe) {
        LOG.debug("deleteEntry() started");

        originEntryDao.deleteEntry(oe);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Collection<OriginEntry> getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Collection<OriginEntry> results = new ArrayList<OriginEntry>();
        Iterator i = originEntryDao.getDocumentsByGroup(oeg);
        while ( i.hasNext() ) {
            Object[] data = (Object[])i.next();
            OriginEntry oe = new OriginEntry();
            oe.setFinancialDocumentNumber((String)data[0]);
            oe.setFinancialDocumentTypeCode((String)data[1]);
            oe.setFinancialSystemOriginationCode((String)data[2]);
            results.add(oe);
    }

        return results;
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<OriginEntry> getEntriesByGroup(OriginEntryGroup originEntryGroup) {
        LOG.debug("getEntriesByGroup() started");

        return originEntryDao.getEntriesByGroup(originEntryGroup, OriginEntryDao.SORT_DOCUMENT);
    }

    public Iterator<OriginEntry> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");

        return originEntryDao.getBadBalanceEntries(groups);
    }

    public Iterator<OriginEntry> getEntriesByGroupAccountOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_ACCOUNT);
    }

    public Iterator<OriginEntry> getEntriesByGroupReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_REPORT);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByDocument(org.kuali.module.gl.bo.OriginEntryGroup,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Iterator<OriginEntry> getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode, String originCode) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put(ENTRY_GROUP_ID, originEntryGroup.getId());
        criteria.put(FINANCIAL_DOCUMENT_NUMBER, documentNumber);
        criteria.put(FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        criteria.put(FINANCIAL_SYSTEM_ORIGINATION_CODE, originCode);

        return originEntryDao.getMatchingEntries(criteria);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#createEntry(org.kuali.module.gl.bo.Transaction,
     *      org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void createEntry(Transaction transaction, OriginEntryGroup originEntryGroup) {
        LOG.debug("createEntry() started");

        OriginEntry e = new OriginEntry(transaction);
        e.setGroup(originEntryGroup);

        originEntryDao.saveOriginEntry(e);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#save(org.kuali.module.gl.bo.OriginEntry)
     */
    public void save(OriginEntry entry) {
        LOG.debug("save() started");

        originEntryDao.saveOriginEntry(entry);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#exportFlatFile(java.lang.String, java.lang.Integer)
     */
    public void exportFlatFile(String filename, Integer groupId) {
        LOG.debug("exportFlatFile() started");

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filename));

            OriginEntryGroup oeg = new OriginEntryGroup();
            oeg.setId(groupId);
            Iterator i = getEntriesByGroup(oeg);
            while (i.hasNext()) {
                OriginEntry e = (OriginEntry) i.next();
                out.write(e.getLine() + "\n");
            }
        }
        catch (IOException e) {
            LOG.error("exportFlatFile() Error writing to file", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ie) {
                    LOG.error("exportFlatFile() Error closing file", ie);
                }
            }
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#loadFlatFile(java.lang.String, java.lang.String, boolean, boolean,
     *      boolean)
     */
    public void loadFlatFile(String filename, String groupSourceCode, boolean isValid, boolean isProcessed, boolean isScrub) {
        LOG.debug("loadFlatFile() started");

        java.sql.Date groupDate = new java.sql.Date(System.currentTimeMillis());
        OriginEntryGroup newGroup = originEntryGroupService.createGroup(groupDate, groupSourceCode, isValid, isProcessed, isScrub);

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = input.readLine()) != null) {
                OriginEntry entry = new OriginEntry(line);
                createEntry(entry, newGroup);
            }
        }
        catch (Exception ex) {
            LOG.error("performStep() Error reading file", ex);
            throw new IllegalArgumentException("Error reading file");
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex) {
                LOG.error("loadFlatFile() error closing file.", ex);
            }
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getSummaryByGroupId(Collection)
     */
    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList) {
        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        if (groupIdList.size() == 0) {
            return ledgerEntryHolder;
        }

        Iterator entrySummaryIterator = originEntryDao.getSummaryByGroupId(groupIdList);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();
            LedgerEntry ledgerEntry = this.buildLedgerEntry(entrySummary);
            ledgerEntryHolder.insertLedgerEntry(ledgerEntry, true);
        }
        return ledgerEntryHolder;
    }

    // create or update a ledger entry with the array of information from the given entry summary object
    private LedgerEntry buildLedgerEntry(Object[] entrySummary) {
        // extract the data from an array and use them to populate a ledger entry
        Object oFiscalYear = entrySummary[0];
        Object oPeriodCode = entrySummary[1];
        Object oBalanceType = entrySummary[2];
        Object oOriginCode = entrySummary[3];
        Object oDebitCreditCode = entrySummary[4];
        Object oAmount = entrySummary[5];
        Object oCount = entrySummary[6];

        Integer fiscalYear = oFiscalYear != null ? new Integer(oFiscalYear.toString()) : null;
        String periodCode = oPeriodCode != null ? oPeriodCode.toString() : "  ";
        String balanceType = oBalanceType != null ? oBalanceType.toString() : "  ";
        String originCode = oOriginCode != null ? oOriginCode.toString() : "  ";
        String debitCreditCode = oDebitCreditCode != null ? oDebitCreditCode.toString() : " ";
        KualiDecimal amount = oAmount != null ? new KualiDecimal(oAmount.toString()) : KualiDecimal.ZERO;
        int count = oCount != null ? Integer.parseInt(oCount.toString()) : 0;

        // construct a ledger entry with the information fetched from the given array
        LedgerEntry ledgerEntry = new LedgerEntry(fiscalYear, periodCode, balanceType, originCode);
        if (Constants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setCreditAmount(amount);
            ledgerEntry.setCreditCount(count);
        }
        else if (Constants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setDebitAmount(amount);
            ledgerEntry.setDebitCount(count);
        }
        else {
            ledgerEntry.setNoDCAmount(amount);
            ledgerEntry.setNoDCCount(count);
        }
        ledgerEntry.setRecordCount(count);

        return ledgerEntry;
    }

    // TODO
    public void flatFile(String filename, Integer groupId, BufferedOutputStream bw) {


        LOG.debug("exportFlatFile() started");

        try {

            OriginEntryGroup oeg = new OriginEntryGroup();
            oeg.setId(groupId);
            Iterator i = getEntriesByGroup(oeg);
            while (i.hasNext()) {
                OriginEntry e = (OriginEntry) i.next();
                bw.write((e.getLine() + "\n").getBytes());
            }
        }
        catch (IOException e) {
            LOG.error("exportFlatFile() Error writing to file", e);
        }
    }

    public Collection getMatchingEntriesByCollection(Map searchCriteria) {
        return originEntryDao.getMatchingEntriesByCollection(searchCriteria);

    }

    public OriginEntry getExactMatchingEntry(Integer entryId) {
        return originEntryDao.getExactMatchingEntry(entryId);
    }

}
