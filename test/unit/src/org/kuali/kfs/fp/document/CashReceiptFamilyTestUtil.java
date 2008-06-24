/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

@ConfigureContext
public class CashReceiptFamilyTestUtil {

    public static SourceAccountingLine buildSourceAccountingLine(String documentNumber, Integer postingYear, Integer sequenceNumber) {
        SourceAccountingLine line = new SourceAccountingLine();
        line.setChartOfAccountsCode("BA");
        line.setAccountNumber("1031400");
        line.setFinancialObjectCode("5000");
        line.setAmount(new KualiDecimal("1.00"));
        line.setPostingYear(postingYear);
        line.setDocumentNumber(documentNumber);
        line.setSequenceNumber(sequenceNumber);
        line.refresh();

        return line;
    }
}
