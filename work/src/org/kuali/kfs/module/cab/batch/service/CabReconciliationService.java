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
package org.kuali.kfs.module.cab.batch.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;

/**
 * This class declares the service method for CAB Reconciliation service. Expected to be used by {@link CabBatchExtractService}.
 * This service should be not be implemented as singleton.
 */
public interface CabReconciliationService {

    /**
     * Returns the list of duplicate entries found after reconciliation
     * 
     * @return Duplicate GL Entries
     */
    List<Entry> getDuplicateEntries();

    /**
     * Returns the list of account groups that found match to account line history
     * 
     * @return List of valid matched account groups
     */
    List<GlAccountLineGroup> getMatchedGroups();

    /**
     * Returns the list of unmatched account line groups
     * 
     * @return List of mismatches
     */
    List<GlAccountLineGroup> getMisMatchedGroups();

    /**
     * Returns true is a GL entry is already available in CAB
     * 
     * @param glEntry GL Line entry
     * @return true if matching GL entry found in CAB
     */
    boolean isDuplicateEntry(Entry glEntry);

    /**
     * Main reconciliation service which will apply the formula where PURAP transaction amounts are compared using
     * <li>GL_ENTRY_T + GL_PENDING_ENTRY = (AP_PMT_RQST_ACCT_HIST_T or AP_CRDT_MEMO_ACCT_HIST_T) </li>
     * 
     * @param glEntries Purap GL Entries
     * @param pendingGlEntries Purap Pending GL Entries
     * @param purapAcctEntries Purap Account Entries
     */
    void reconcile(Collection<Entry> glEntries, Collection<GeneralLedgerPendingEntry> pendingGlEntries, Collection<?> purapAcctEntries);


}