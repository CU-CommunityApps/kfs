/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.LedgerEntry;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OriginEntryServiceImpl implements OriginEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

    private static final String ENTRY_GROUP_ID = "entryGroupId";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";

    private OriginEntryDao originEntryDao;
    private OriginEntryGroupService originEntryGroupService;
    
    private DateTimeService dateTimeService;

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

    public OriginEntryStatistics getStatistics(Integer groupId) {
        LOG.debug("getStatistics() started");

        OriginEntryStatistics oes = new OriginEntryStatistics();

        oes.setCreditTotalAmount(originEntryDao.getGroupTotal(groupId, true));
        oes.setDebitTotalAmount(originEntryDao.getGroupTotal(groupId, false));
        oes.setRowCount(originEntryDao.getGroupCount(groupId));

        return oes;
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#copyEntries(java.util.Date, java.lang.String, boolean, boolean, boolean, java.util.Collection)
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid,boolean process,boolean scrub,Collection<OriginEntryFull> entries) {
        LOG.debug("copyEntries() started");

        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(date, sourceCode, valid, process, scrub);

        // Create new Entries with newOriginEntryGroup
        for (OriginEntryFull oe : entries) {
            oe.setEntryGroupId(newOriginEntryGroup.getId());
            createEntry(oe, newOriginEntryGroup);
        }

        return newOriginEntryGroup;
    }

    
    /**
     * @see org.kuali.module.gl.service.OriginEntryService#copyEntries(java.sql.Date, java.lang.String, boolean, boolean, boolean, java.util.Iterator)
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Iterator<OriginEntryFull> entries) {
        LOG.debug("copyEntries() started");

        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(date, sourceCode, valid, process, scrub);

        // Create new Entries with newOriginEntryGroup
        while (entries.hasNext()) {
            OriginEntryFull oe = entries.next();
            oe.setEntryGroupId(newOriginEntryGroup.getId());
            createEntry(oe, newOriginEntryGroup);
        }

        return newOriginEntryGroup;
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#delete(org.kuali.module.gl.bo.OriginEntryFull)
     */
    public void delete(OriginEntryFull oe) {
        LOG.debug("deleteEntry() started");

        originEntryDao.deleteEntry(oe);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Collection<OriginEntryFull> getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Collection<OriginEntryFull> results = new ArrayList<OriginEntryFull>();
        Iterator i = originEntryDao.getDocumentsByGroup(oeg);
        while ( i.hasNext() ) {
            Object[] data = (Object[])i.next();
            OriginEntryFull oe = new OriginEntryFull();
            oe.setDocumentNumber((String)data[0]);
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
    public Iterator<OriginEntryFull> getEntriesByGroup(OriginEntryGroup originEntryGroup) {
        LOG.debug("getEntriesByGroup() started");

        return originEntryDao.getEntriesByGroup(originEntryGroup, OriginEntryDao.SORT_DOCUMENT);
    }

    public Iterator<OriginEntryFull> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");

        return originEntryDao.getBadBalanceEntries(groups);
    }

    public Iterator<OriginEntryFull> getEntriesByGroupAccountOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_ACCOUNT);
    }

    public Iterator<OriginEntryFull> getEntriesByGroupReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_REPORT);
    }
    
    public Iterator<OriginEntryFull> getEntriesByGroupListingReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_LISTING_REPORT);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByDocument(org.kuali.module.gl.bo.OriginEntryGroup,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Iterator<OriginEntryFull> getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode, String originCode) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put(ENTRY_GROUP_ID, originEntryGroup.getId());
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.put(FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        criteria.put(FINANCIAL_SYSTEM_ORIGINATION_CODE, originCode);

        return originEntryDao.getMatchingEntries(criteria);
    }

    public void createEntry(Transaction transaction, OriginEntryGroup originEntryGroup) {
        LOG.debug("createEntry() started");

        OriginEntryFull e = new OriginEntryFull(transaction);
        e.setGroup(originEntryGroup);

        originEntryDao.saveOriginEntry(e);
        
        // add 1 to the rows in the origin entry group, so we can unit test against that
        originEntryGroup.setRows(originEntryGroup.getRows().intValue() + 1);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#save(org.kuali.module.gl.bo.OriginEntryFull)
     */
    public void save(OriginEntryFull entry) {
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
                OriginEntryFull e = (OriginEntryFull) i.next();
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

        java.sql.Date groupDate = new java.sql.Date(dateTimeService.getCurrentDate().getTime());
        OriginEntryGroup newGroup = originEntryGroupService.createGroup(groupDate, groupSourceCode, isValid, isProcessed, isScrub);

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = input.readLine()) != null) {
                OriginEntryFull entry = new OriginEntryFull(line);
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
        LOG.debug("getSummaryByGroupId() started");

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
    public static LedgerEntry buildLedgerEntry(Object[] entrySummary) {
        // extract the data from an array and use them to populate a ledger entry
        Object oFiscalYear = entrySummary[0];
        Object oPeriodCode = entrySummary[1];
        Object oBalanceType = entrySummary[2];
        Object oOriginCode = entrySummary[3];
        Object oDebitCreditCode = entrySummary[4];
        Object oAmount = entrySummary[5];
        Object oCount = entrySummary[6];

        Integer fiscalYear = oFiscalYear != null ? new Integer(oFiscalYear.toString()) : null;
        String periodCode = oPeriodCode != null ? oPeriodCode.toString() : GLConstants.getSpaceUniversityFiscalPeriodCode();
        String balanceType = oBalanceType != null ? oBalanceType.toString() : GLConstants.getSpaceBalanceTypeCode();
        String originCode = oOriginCode != null ? oOriginCode.toString() : GLConstants.getSpaceFinancialSystemOriginationCode();
        String debitCreditCode = oDebitCreditCode != null ? oDebitCreditCode.toString() : GLConstants.getSpaceDebitCreditCode();
        KualiDecimal amount = oAmount != null ? new KualiDecimal(oAmount.toString()) : KualiDecimal.ZERO;
        int count = oCount != null ? Integer.parseInt(oCount.toString()) : 0;

        // construct a ledger entry with the information fetched from the given array
        LedgerEntry ledgerEntry = new LedgerEntry(fiscalYear, periodCode, balanceType, originCode);
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setCreditAmount(amount);
            ledgerEntry.setCreditCount(count);
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode)) {
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

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#flatFile(java.lang.Integer, java.io.BufferedOutputStream)
     */
    public void flatFile(Integer groupId, BufferedOutputStream bw) {
        LOG.debug("flatFile() started");
        OriginEntryGroup oeg = new OriginEntryGroup();
        oeg.setId(groupId);
        flatFile(getEntriesByGroup(oeg), bw);
    }
    
    /**
     * This method writes origin entries into a file format.  This particular implementation
     * will use the OriginEntryFull.getLine method to generate the text for this file.
     * 
     * @param entries An iterator of OriginEntries
     * @param bw an opened, ready-for-output bufferedOutputStream.
     */
    public void flatFile(Iterator<OriginEntryFull> entries, BufferedOutputStream bw) {
        try {
            while (entries.hasNext()) {
                OriginEntryFull e = entries.next();
                bw.write((e.getLine() + "\n").getBytes());
            }
        }
        catch (IOException e) {
            LOG.error("flatFile() Error writing to file", e);
            throw new RuntimeException("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection<OriginEntryFull> getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntriesByCollection() started");

        return originEntryDao.getMatchingEntriesByCollection(searchCriteria);
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#getMatchingEntriesByList(java.util.Map)
     */
    public List<OriginEntryFull> getEntriesByGroupId(Integer groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID is null");
        }
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(ENTRY_GROUP_ID, groupId);
        Collection<OriginEntryFull> searchResultAsCollection = getMatchingEntriesByCollection(searchCriteria);
        if (searchResultAsCollection instanceof List) {
            return (List<OriginEntryFull>) searchResultAsCollection;
        }
        else {
            return new ArrayList<OriginEntryFull>(searchResultAsCollection);
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getExactMatchingEntry(java.lang.Integer)
     */
    public OriginEntryFull getExactMatchingEntry(Integer entryId) {
        LOG.debug("getExactMatchingEntry() started");

        return originEntryDao.getExactMatchingEntry(entryId);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryService#getPosterOutputSummaryByGroupId(java.util.Collection)
     */
    public Map<String,PosterOutputSummaryEntry> getPosterOutputSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getPosterOutputSummaryByGroupId() started");

        Map<String,PosterOutputSummaryEntry> output = new HashMap<String,PosterOutputSummaryEntry>();

        if (groupIdList.size() == 0) {
            return output;
        }

        Iterator entrySummaryIterator = originEntryDao.getPosterOutputSummaryByGroupId(groupIdList);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();            
            PosterOutputSummaryEntry posterOutputSummaryEntry = new PosterOutputSummaryEntry();
            int indexOfField = 0;

            Object tempEntry = entrySummary[indexOfField++];
            String entry = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setBalanceTypeCode(entry);

            tempEntry = entrySummary[indexOfField++];
            entry = (tempEntry == null) ? null : tempEntry.toString();
            posterOutputSummaryEntry.setUniversityFiscalYear(new Integer(entry));

            tempEntry = entrySummary[indexOfField++];
            entry = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setFiscalPeriodCode(entry);

            tempEntry = entrySummary[indexOfField++];
            entry = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setFundGroup(entry);

            tempEntry = entrySummary[indexOfField++];
            String objectTypeCode = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setObjectTypeCode(objectTypeCode);

            tempEntry = entrySummary[indexOfField++];
            String debitCreditCode = (tempEntry == null) ? KFSConstants.GL_BUDGET_CODE : tempEntry.toString();

            tempEntry = entrySummary[indexOfField];
            entry = (tempEntry == null) ? "0" : tempEntry.toString();            
            KualiDecimal amount = new KualiDecimal(entry);

            posterOutputSummaryEntry.setAmount(debitCreditCode, objectTypeCode, amount);

            if ( output.containsKey(posterOutputSummaryEntry.getKey()) ) {
                PosterOutputSummaryEntry pose = output.get(posterOutputSummaryEntry.getKey());
                pose.add(posterOutputSummaryEntry);
            } else {
                output.put(posterOutputSummaryEntry.getKey(),posterOutputSummaryEntry);
            }
        }
        return output;
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#clearCache()
     */
    public void clearCache() {
        originEntryDao.clearCache();
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#getGroupCount(java.lang.Integer)
     */
    public Integer getGroupCount(Integer groupId) {
        return originEntryDao.getGroupCount(groupId);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
