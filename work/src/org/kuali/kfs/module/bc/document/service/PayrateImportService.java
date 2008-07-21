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
package org.kuali.kfs.module.bc.document.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.module.bc.util.ExternalizedMessageWrapper;

import com.lowagie.text.DocumentException;

public interface PayrateImportService {
    
    /**
     * Parses file and creates payrate holding records for each import line
     * 
     * @param fileImportStream
     * @return
     */
    public boolean importFile(InputStream fileImportStream, List<ExternalizedMessageWrapper> messageList, String personUniversalIdentifier);
    
    /**
     * Processes all payrate holding records
     */
    public void update(Integer budgetYear, UniversalUser user, List<ExternalizedMessageWrapper> messageList, String personUniversalIdentifier);
    
    /**
     * Generates the log file
     * 
     * @param errorMessages
     * @param baos
     * @throws DocumentException
     */
    public void generatePdf(List<ExternalizedMessageWrapper> logMessages, ByteArrayOutputStream baos) throws DocumentException;
    
}
