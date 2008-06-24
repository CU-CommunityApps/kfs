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
package org.kuali.kfs.module.ld.batch.service;

/**
 * The interface defines loading and cleanup methods for nightly batch jobs
 */
public interface LaborNightlyOutService {
    /**
     * Delete all the pending entries that were copied for processing.
     */
    public void deleteCopiedPendingLedgerEntries();

    /**
     * This method copies the approved pending ledger entries to orign entry table
     */
    public void copyApprovedPendingLedgerEntries();

    /**
     * Delete all the labor general ledger entries that were copied for processing.
     */
    public void deleteCopiedLaborGenerealLedgerEntries();

    /**
     * This method copies the labor general ledger entries to GL
     */
    public void copyLaborGenerealLedgerEntries();
}
