/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

/**
 * This KemidCurrentCashOpenRecordsService class provides a method to test whether a KEMID has open records in Current Cash:
 * records with values greater or less than zero.
 */
public interface KemidCurrentCashOpenRecordsService {

    /**
     * Checks if KEMID has open records in Current Cash: records with values greater or less than zero.
     * 
     * @param kemid
     * @return true if it has open records, false otherwise
     */
    public boolean hasKemidOpenRecordsInCurrentCash(String kemid);

}
