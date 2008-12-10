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
package org.kuali.kfs.gl.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.BalanceTyp;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PersistenceService;

/**
 * Tests the ScrubberService
 */
@ConfigureContext
public class ScrubberServiceTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceTest.class);

    private ScrubberService scrubberService = null;
    private BusinessObjectService businessObjectService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() started");

        scrubberService = SpringContext.getBean(ScrubberService.class);
        scrubberService.setDateTimeService(dateTimeService);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2006);

        // since the cutoff time is set to 10am (KFSP1/Scrubber+cutoff+time+configuration)
        // we want to ensure that the time is always after that time so the cutoff algorithm is not invoked
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        date = c.getTime();
        dateTimeService.setCurrentDate(date);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // return the run date to the current date
        dateTimeService.setCurrentDate(new java.util.Date());
    }

    /**
     * Tests the scrubber considers entries with certain fields blank as errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testMiscellaneousBlankFields() throws Exception {

        String[] stringInput = new String[] { "2007  6044900-----5300---ACEE07CHKDPDBLANKCHAR     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA       -----5300---ACEE07CHKDPDBLANKACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----    ---ACEE07CHKDPDBLANKOBJ      12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----5300---ACEE07    PDBLANKDOCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",
                "2007BA6044900-----5300---ACEE07CHKD  BLANKORIG     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----5300---ACEE07CHKDPD              12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ", };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[12];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.BACKUP, stringInput[i]);
        }

        int c = stringInput.length;
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007BA       -----5300---ACEE07CHKDPDBLANKACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007  6044900-----5300---ACEE07CHKDPDBLANKCHAR     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007BA6044900-----    ---ACEE07CHKDPDBLANKOBJ      12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007BA6044900-----5300---ACEE07CHKDPD              12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007BA6044900-----5300---ACEE07CHKD  BLANKORIG     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007BA6044900-----5300---ACEE07    PDBLANKDOCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ");

        scrub(stringInput);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share encumbrances for pre-encumbrance entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareEncumbrancesForPreEncumbrances() throws Exception {

        // Inputs.
        String[] stringInput = new String[] { "2007BL4631625CS0018000---PEAS07PE  01CSENCPE       00000TP Generated Offset                               1650.00C2006-01-05          ----------                                      D                                ", "2007BL4631625CS0014866---PEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                      D                                " };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[6];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.BACKUP, stringInput[i]);
        }

        // ... add expected output ...
        output[2] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9940---CEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206 FR-BL4631625          1650.00D2006-01-01          ----------                                      D                                ");
        output[3] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9893---CEFB07PE  01CSENCPE       00000GENERATED OFFSET                                  1650.00C2006-01-01          ----------                                                                       ");
        output[4] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0014866---PEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                      D                                ");
        output[5] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---PEAS07PE  01CSENCPE       00000TP Generated Offset                               1650.00C2006-01-05          ----------                                      D                                ");

        scrub(stringInput);

        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share encumbrances for internal encumbrances entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareEncumbrancesForInternalEncumbrances() throws Exception {

        String[] stringInput = new String[] { "2007BL4631618CS0014190---IEEX07PAYE01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                      D                                ", "2007BL4631618CS0018000---IEAS07PAYE01CSENCIE       00000TP Generated Offset                                 40.72D2006-01-05          ----------                                      D                                " };

        // Add inputs to expected output ...
        EntryHolder[] output = new EntryHolder[6];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.BACKUP, stringInput[i]);
        }

        // ... add expected output ...
        output[2] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9940---CEEX07PAYE01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERFR-BL4631618            40.72C2006-01-01          ----------                                      D                                ");
        output[3] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9893---CEFB07PAYE01CSENCIE       00000GENERATED OFFSET                                    40.72D2006-01-01          ----------                                                                       ");
        output[4] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0014190---IEEX07PAYE01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                      D                                ");
        output[5] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---IEAS07PAYE01CSENCIE       00000TP Generated Offset                                 40.72D2006-01-05          ----------                                      D                                ");

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share encumbrances for external encumbrance entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareEncumbrancesForExternalEncumbrances() throws Exception {

        String[] stringInput = new String[] { "2007BL4631601CS0011800---EXIN07EXENLGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                ", "2007BL4631601CS0019041---EXLI07EXENLGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                " };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[4];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.BACKUP, stringInput[i]);
        }

        int c = stringInput.length;
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0011800---EXIN07EXENLGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019041---EXLI07EXENLGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                ");

        scrub(stringInput);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries with cost share encumbrances
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForCostShareEncumbrances() throws Exception {

        String[] stringInput = new String[] { "2007BL4631625CS0018000---CEAS07EXEN01NOCSENCE      00000TP Generated Offset                               1650.00C2006-01-05          ----------                                      D                                ", "2007BL4631625CS0014866---CEEX07EXEN01NOCSENCE      00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                      D                                " };

        EntryHolder output[] = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, stringInput[0]), new EntryHolder(OriginEntrySource.BACKUP, stringInput[1]), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0014866---CEEX07EXEN01NOCSENCE      00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---CEAS07EXEN01NOCSENCE      00000TP Generated Offset                               1650.00C2006-01-05          ----------                                      D                                ") };

        scrub(stringInput);
        assertOriginEntries(4, output);

    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries created by the journal voucher document
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForJournalVoucher() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                      D                                ", "2007BL4631618CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                                 40.72D2006-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                                 40.72D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                      D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                                 40.72D2006-01-05          ----------                                      D                                "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for beginning balance entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForBeginningBalances() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0011800---EXINCBTOPSLGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                ", "2007BL4631601CS0019041---EXLICBTOPSLGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0011800---EXINCBTOPSLGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0019041---EXLICBTOPSLGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0011800---EXINCBTOPSLGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019041---EXLICBTOPSLGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries with a balance type of "actual"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForActuals() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                               1650.00C2006-01-05          ----------                                      D                                ", "2007BL4631625CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                               1650.00C2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                 1650.00C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                  1650.00D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9940---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                 1650.00D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                  1650.00C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                               1650.00C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries with budget balance types
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForBudget() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72 2006-01-05          ----------                                      D                                ", "2007BL4631618CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                                 40.72 2006-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72 2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                                 40.72 2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72 2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                                 40.72 2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries that do not represent expenses
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareForEncumbrancesNonExpenses() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0011800---EXIN07TOPSLGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                ", "2007BL4631601CS0019041---EXLI07TOPSLGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                ", };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0011800---EXIN07TOPSLGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0019041---EXLI07TOPSLGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0011800---EXIN07TOPSLGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                      D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019041---EXLI07TOPSLGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                      D                                ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates miscellaneous cost share entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareOther() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9940---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TRIN"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTrin() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TREX"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTrex() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                      "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9959---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TRAV"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTrav() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9960---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TRAN"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTran() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9959---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "SAAP"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelSaap() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9923---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "RESV"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelResv() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9979---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "PRSA"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelPrsa() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9924---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "PART"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelPart() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9923---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "ICOE"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelIcoe() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9955---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "HRCO"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelHrco() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0013000---ACEX07DI  01CSHRHRCO      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRHRCO      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0013000---ACEX07DI  01CSHRHRCO      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRHRCO      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9930---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0013000---ACEX07DI  01CSHRHRCO      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRHRCO      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "FINA2"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelFina2() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0015800---ACEX07CR  01CSHRFINA2     00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01CSHRFINA2     00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0015800---ACEX07CR  01CSHRFINA2     00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01CSHRFINA2     00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9958---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0015800---ACEX07CR  01CSHRFINA2     00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01CSHRFINA2     00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "FINA1"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelFina1() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0015400---ACEX07DI  EUCSHRFINA1     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHRFINA1     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0015400---ACEX07DI  EUCSHRFINA1     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHRFINA1     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                      "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9954---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0015400---ACEX07DI  EUCSHRFINA1     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHRFINA1     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "CORI"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelCori() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0019912---ACEX07DI  01CSHRCORI      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRCORI      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019912---ACEX07DI  01CSHRCORI      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRCORI      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRCORI      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9912---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019912---ACEX07DI  01CSHRCORI      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "CORE"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelCore() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0019951---ACEX07CR  01CSHRCORE      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01CSHRCORE      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0019951---ACEX07CR  01CSHRCORE      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01CSHRCORE      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01CSHRCORE      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9951---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019951---ACEX07CR  01CSHRCORE      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "CAP"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelCap() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0017000---ACEX07DI  EUCSHRCAP       00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHRCAP       00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0017000---ACEX07DI  EUCSHRCAP       00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHRCAP       00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL9520004-----8610---ACAS07DI  EUCSHRCAP       00000GENERATED CAPITALIZATION                           241.75D2005-11-30          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL9520004-----9899---ACFB07DI  EUCSHRCAP       00000GENERATED CAPITALIZATION                           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9970---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0017000---ACEX07DI  EUCSHRCAP       00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHRCAP       00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "BISA"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelBisa() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0012500---ACEX07DI  01CSHRBISA      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRBISA      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0012500---ACEX07DI  01CSHRBISA      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRBISA      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9925---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0012500---ACEX07DI  01CSHRBISA      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRBISA      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "BENF6"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelBenf6() throws Exception {
        String[] input = new String[] { "2007BL4631625CS0015625---ACEX07DI  EUCSHRBENF6     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       ", "2007BL4631625CS0018000---ACAS07DI  EUCSHRBENF6     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0015625---ACEX07DI  EUCSHRBENF6     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---ACAS07DI  EUCSHRBENF6     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9956---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0015625---ACEX07DI  EUCSHRBENF6     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---ACAS07DI  EUCSHRBENF6     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "BASE"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelBase() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0015509---ACEX07DI  01CSHRBASE      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07DI  01CSHRBASE      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0015509---ACEX07DI  01CSHRBASE      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07DI  01CSHRBASE      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9959---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0015509---ACEX07DI  01CSHRBASE      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07DI  01CSHRBASE      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "ACSA"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelAcsa() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0012000---ACEX07CR  01CSHRACSA      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01CSHRACSA      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0012000---ACEX07CR  01CSHRACSA      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01CSHRACSA      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0019915---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9920---ACTE07TF  CSCSHR01/01     00000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----8000---ACAS07TF  CSCSHR01/01     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0012000---ACEX07CR  01CSHRACSA      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01CSHRACSA      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for entries with certain document types
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForCertainDocumentTypes() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0015000---ACEX07JV  01NOCSHRJV      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---ACLI07JV  01NOCSHRJV      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0015000---ACEX07JV  01NOCSHRJV      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---ACLI07JV  01NOCSHRJV      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0015000---ACEX07JV  01NOCSHRJV      00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---ACLI07JV  01NOCSHRJV      00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for entries that are beginning balance transactions
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForBeginningBalanceTransactions() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for encumbrance entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForEncumbranceTransactions() throws Exception {

        String[] input = new String[] { "2007BL4631625CS0014110---EXEX07EXENEUNOCSHREX      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                      D                                ", "2007BL4631625CS0018000---EXAS07EXENEUNOCSHREX      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0014110---EXEX07EXENEUNOCSHREX      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631625CS0018000---EXAS07EXENEUNOCSHREX      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9940---CEEX07EXENEUNOCSHREX      00000NOV-05 IMU Business Office  FR-BL4631625           241.75D2006-01-01          ----------                                      D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031400-----9893---CEFB07EXENEUNOCSHREX      00000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0014110---EXEX07EXENEUNOCSHREX      00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631625CS0018000---EXAS07EXENEUNOCSHREX      00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                      D                                ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for entries with budget balance types
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForBudgetTransactions() throws Exception {

        String[] input = new String[] { "2007BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                       ", "2007BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for entries that are non-expense
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForNonExpense() throws Exception {

        String[] input = new String[] { "2007BL4631601CS0011800---ACIN07CR  01NOCSHRIN      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       ", "2007BL4631601CS0018000---ACAS07CR  01NOCSHRIN      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0011800---ACIN07CR  01NOCSHRIN      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631601CS0018000---ACAS07CR  01NOCSHRIN      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0011800---ACIN07CR  01NOCSHRIN      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631601CS0018000---ACAS07CR  01NOCSHRIN      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a plant endebtedness entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPlantIndebtedness() throws Exception {

        String[] input = new String[] { "2007BA9020204-----9100---ACLI07SB  01DEBTEDNES     00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                       ", "2007BA9020204-----8000---ACAS07SB  01DEBTEDNES     00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                       ", "2007BA9120657-----9120---ACLI07ST  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                       ", "2007BA9120657-----8000---ACAS07ST  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA9020204-----9100---ACLI07SB  01DEBTEDNES     00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA9020204-----8000---ACAS07SB  01DEBTEDNES     00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120657-----9120---ACLI07ST  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120657-----8000---ACAS07ST  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9020204-----8000---ACAS07SB  01DEBTEDNES     00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9020204-----9100---ACLI07SB  01DEBTEDNES     00000GENERATED TRANSFER TO NET PLANT                     13.77C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9020204-----9899---ACFB07SB  01DEBTEDNES     00000GENERATED TRANSFER TO NET PLANT                     13.77D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9100---ACLI07SB  01DEBTEDNES     00000GENERATED TRANSFER FROM BA 9020204                  13.77D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07SB  01DEBTEDNES     00000GENERATED TRANSFER FROM BA 9020204                  13.77C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9020204-----9100---ACLI07SB  01DEBTEDNES     00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----8000---ACAS07ST  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----9120---ACLI07ST  EUDEBTEDNES     00000GENERATED TRANSFER TO NET PLANT                    620.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----9899---ACFB07ST  EUDEBTEDNES     00000GENERATED TRANSFER TO NET PLANT                    620.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9120---ACLI07ST  EUDEBTEDNES     00000GENERATED TRANSFER FROM BA 9120657                 620.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07ST  EUDEBTEDNES     00000GENERATED TRANSFER FROM BA 9120657                 620.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----9120---ACLI07ST  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for entries with financial sub object == "P2"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForObjectSubTypeP2() throws Exception {

        String[] input = new String[] { "2007BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                       ", "2007BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000GENERATED TRANSFER TO NET PLANT                   3375.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----9899---ACFB07DI  EUNODEBTP2      00000GENERATED TRANSFER TO NET PLANT                   3375.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9100---ACLI07DI  EUNODEBTP2      00000GENERATED TRANSFER FROM BA 9120657                3375.00C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07DI  EUNODEBTP2      00000GENERATED TRANSFER FROM BA 9120657                3375.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for entries with financial sub object == "P1"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForObjectSubTypeP1() throws Exception {

        String[] input = new String[] { "2007BL2231423-----9100---ACIN  CR  PLNODEBTP1      00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                       ", "2007BL2231423-----8000---ACAS  CR  PLNODEBTP1      00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL2231423-----9100---ACIN  CR  PLNODEBTP1      00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL2231423-----8000---ACAS  CR  PLNODEBTP1      00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2006BL2231423-----8000---ACAS07CR  PLNODEBTP1      00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2006BL2231423-----9100---ACIN07CR  PLNODEBTP1      00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for encumbrance entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForEncumbranceEntries() throws Exception {

        String[] input = new String[] { "2007BA9021004-----9120---EXLI07TOPSEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                      D                                ", "2007BA9021004-----8000---EXAS07TOPSEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA9021004-----9120---EXLI07TOPSEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA9021004-----8000---EXAS07TOPSEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9021004-----8000---EXAS07TOPSEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                      D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9021004-----9120---EXLI07TOPSEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                      D                                "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for entries with budget balance types
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForBudgetTransactions() throws Exception {

        String[] input = new String[] { "2007BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                                   13.77 2006-01-05          ----------                                                                       ", "2007BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                                 13.77 2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                                   13.77 2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                                 13.77 2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                                 13.77 2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                                   13.77 2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "CL"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeCL() throws Exception {

        String[] input = new String[] { "2007BA6044900-----7099---ACEE07CD  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       ", "2007BA6044900-----8000---ACAS07CD  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----7099---ACEE07CD  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----8000---ACAS07CD  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9603---ACLI07CD  PDCAPITALCL     00000GENERATED LIABILITY                               1445.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07CD  PDCAPITALCL     00000GENERATED LIABILITY                               1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----7099---ACEE07CD  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACAS07CD  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "LR"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeLR() throws Exception {

        String[] input = new String[] { "2007BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8665---ACAS07GEC 01CAPITALLR     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07GEC 01CAPITALLR     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "LI"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeLI() throws Exception {

        String[] input = new String[] { "2007BA6044906-----7100---ACEE07TOPS01CAPITALLI     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044906-----9041---ACLI07TOPS01CAPITALLI     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----7100---ACEE07TOPS01CAPITALLI     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----9041---ACLI07TOPS01CAPITALLI     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8613---ACAS07TOPS01CAPITALLI     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07TOPS01CAPITALLI     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----7100---ACEE07TOPS01CAPITALLI     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----9041---ACLI07TOPS01CAPITALLI     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "LE"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeLE() throws Exception {

        String[] input = new String[] { "2007BA6044900-----7800---ACEE07CD  PDCAPITALLE     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       ", "2007BA6044900-----8000---ACAS07CD  PDCAPITALLE     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----7800---ACEE07CD  PDCAPITALLE     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----8000---ACAS07CD  PDCAPITALLE     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8608---ACAS07CD  PDCAPITALLE     00000GENERATED CAPITALIZATION                          1445.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07CD  PDCAPITALLE     00000GENERATED CAPITALIZATION                          1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----7800---ACEE07CD  PDCAPITALLE     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACAS07CD  PDCAPITALLE     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "LA"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeLA() throws Exception {

        String[] input = new String[] { "2007BA6044913-----7200---ACEE07GEC 01CAPITALLA     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044913-----9041---ACLI07GEC 01CAPITALLA     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       ", };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----7200---ACEE07GEC 01CAPITALLA     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----9041---ACLI07GEC 01CAPITALLA     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8603---ACAS07GEC 01CAPITALLA     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07GEC 01CAPITALLA     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----7200---ACEE07GEC 01CAPITALLA     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----9041---ACLI07GEC 01CAPITALLA     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "EF"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeEF() throws Exception {

        String[] input = new String[] { "2007BA6044906-----7400---ACEE07TOPS01CAPITALIF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044906-----9041---ACLI07TOPS01CAPITALIF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----7400---ACEE07TOPS01CAPITALIF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----9041---ACLI07TOPS01CAPITALIF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8604---ACAS07TOPS01CAPITALIF     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07TOPS01CAPITALIF     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----7400---ACEE07TOPS01CAPITALIF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----9041---ACLI07TOPS01CAPITALIF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "ES"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeES() throws Exception {

        String[] input = new String[] { "2007BA6044900-----7098---ACEE07CD  PDCAPITALES     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       ", "2007BA6044900-----8000---ACAS07CD  PDCAPITALES     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----7098---ACEE07CD  PDCAPITALES     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----8000---ACAS07CD  PDCAPITALES     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8630---ACAS07CD  PDCAPITALES     00000GENERATED CAPITALIZATION                          1445.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07CD  PDCAPITALES     00000GENERATED CAPITALIZATION                          1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----7098---ACEE07CD  PDCAPITALES     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACAS07CD  PDCAPITALES     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "CF"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeCF() throws Exception {

        String[] input = new String[] { "2007BA6044913-----7030---ACEE07GEC 01CAPITALCF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044913-----9041---ACLI07GEC 01CAPITALCF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----7030---ACEE07GEC 01CAPITALCF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----9041---ACLI07GEC 01CAPITALCF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8611---ACAS07GEC 01CAPITALCF     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07GEC 01CAPITALCF     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----7030---ACEE07GEC 01CAPITALCF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----9041---ACLI07GEC 01CAPITALCF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "CM"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeCM() throws Exception {

        String[] input = new String[] { "2007BA6044906-----7000---ACEE07TOPS01CAPITALCM     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044906-----9041---ACLI07TOPS01CAPITALCM     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----7000---ACEE07TOPS01CAPITALCM     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----9041---ACLI07TOPS01CAPITALCM     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8610---ACAS07TOPS01CAPITALCM     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07TOPS01CAPITALCM     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----7000---ACEE07TOPS01CAPITALCM     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----9041---ACLI07TOPS01CAPITALCM     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "BF"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeBF() throws Exception {

        String[] input = new String[] { "2007BA6044913-----7305---ACEE07GEC 01CAPITALBF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044913-----9041---ACLI07GEC 01CAPITALBF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----7305---ACEE07GEC 01CAPITALBF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----9041---ACLI07GEC 01CAPITALBF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8605---ACAS07GEC 01CAPITALBF     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07GEC 01CAPITALBF     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----7305---ACEE07GEC 01CAPITALBF     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----9041---ACLI07GEC 01CAPITALBF     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "BD"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeBD() throws Exception {

        String[] input = new String[] { "2007BA6044906-----7300---ACEE07TOPS01CAPITALBD     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044906-----9041---ACLI07TOPS01CAPITALBD     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----7300---ACEE07TOPS01CAPITALBD     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----9041---ACLI07TOPS01CAPITALBD     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8601---ACAS07TOPS01CAPITALBD     00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07TOPS01CAPITALBD     00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----7300---ACEE07TOPS01CAPITALBD     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----9041---ACLI07TOPS01CAPITALBD     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "AM"
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeAM() throws Exception {

        String[] input = new String[] { "2007BA6044900-----7677---ACEE07CD  PDCAPITALAM     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       ", "2007BA6044900-----8000---ACAS07CD  PDCAPITALAM     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----7677---ACEE07CD  PDCAPITALAM     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----8000---ACAS07CD  PDCAPITALAM     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----8615---ACAS07CD  PDCAPITALAM     00000GENERATED CAPITALIZATION                          1445.00D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9544900-----9899---ACFB07CD  PDCAPITALAM     00000GENERATED CAPITALIZATION                          1445.00C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----7677---ACEE07CD  PDCAPITALAM     00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACAS07CD  PDCAPITALAM     00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a capitalization entry for entries that occurred in certain periods
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCapitalizationForCertainFiscalPeriods() throws Exception {

        String[] input = new String[] { "2007BA6044900-----7000---ACEECBCD  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ACCDEFGHIJ----------12345678                                                               ", "2007BA6044900-----8000---ACASCBCD  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                               " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----7000---ACEECBCD  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ACCDEFGHIJ----------12345678                                                               "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----8000---ACASCBCD  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----7000---ACEECBCD  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ACCDEFGHIJ----------12345678                                                               "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACASCBCD  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                               "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a capitalization entry for entries with certain document types
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCapitalizationForCertainDocumentTypes() throws Exception {

        String[] input = new String[] { "2007BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       ", "2007BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber does not generate a capitalization entry for encumbrance entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCapitalizationForEncumbranceEntry() throws Exception {

        String[] input = new String[] { "2007BA6044906-----7300---EXEE07TOPSLGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                      D                                ", "2007BA6044906-----9041---EXLI07TOPSLGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----7300---EXEE07TOPSLGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----9041---EXLI07TOPSLGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                      D                                "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----7300---EXEE07TOPSLGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                      D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----9041---EXLI07TOPSLGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                      D                                "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates the correct offset entries, even when there are mulitple period codes involved
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testOffsetGenerationAcrossMultipleFiscalPeriods() throws Exception {

        String[] input = new String[] { "2007BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                       ", "2007BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031497-----8000---ACAS07GEC 01OFFSETPER     00000GENERATED OFFSET                                    40.72D2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000GENERATED OFFSET                                    40.72C2006-01-01          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber generates the correct offset entries, even when there are mulitple reversal dates involved
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testOffsetGenerationAcrossMultipleReversalDates() throws Exception {

        String[] input = new String[] { "2007BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                                 20.00D2006-01-05          ----------                            2005-01-31                                 ", "2007BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                                 20.00C2006-01-05          ----------                            2005-02-01                                 " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                                 20.00D2006-01-05          ----------                            2005-01-31                                 "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                                 20.00C2006-01-05          ----------                            2005-02-01                                 "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                                 20.00D2006-01-05          ----------                            2005-01-31                                 "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----8000---ACAS07CR  01OFFSETREV     00000GENERATED OFFSET                                    20.00C2006-01-01          ----------                            2005-01-31                                 "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                                 20.00C2006-01-05          ----------                            2005-02-01                                 "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044913-----8000---ACAS07CR  01OFFSETREV     00000GENERATED OFFSET                                    20.00D2006-01-01          ----------                            2005-02-01                                 "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    // This test fails in anthill but runs in dev
    // public void testOffsetGenerationAcrossMultipleBalanceTypes() throws Exception {
    //
    // String[] input = new String[] {
    // "2007BA9120656-----4035---EXEX07EXEN01OFFSETBAL 00000pymts recd 12/28/05 25.15C2006-01-05 ---------- D ",
    // "2007BA9120656-----8000---ACAS07TOPS01OFFSETBAL 00000TP Generated Offset 25.15D2006-01-05 ---------- " };
    //
    // EntryHolder[] output = new EntryHolder[] {
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120656-----4035---EXEX07EXEN01OFFSETBAL 00000pymts recd 12/28/05
    // 25.15C2006-01-05 ---------- D "),
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120656-----8000---ACAS07TOPS01OFFSETBAL 00000TP Generated Offset
    // 25.15D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----4035---EXEX07EXEN01OFFSETBAL 00000pymts recd 12/28/05
    // 25.15C2006-01-05 ---------- D "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----9892---EXFB07EXEN01OFFSETBAL 00000GENERATED OFFSET
    // 25.15D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----8000---ACAS07TOPS01OFFSETBAL 00000TP Generated Offset
    // 25.15D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----8000---ACAS07TOPS01OFFSETBAL 00000GENERATED OFFSET
    // 25.15C2006-01-01 ---------- "), };
    //
    // scrub(input);
    // assertOriginEntries(4, output);
    // }

    // This test fails in anthill but runs in dev
    // public void testOffsetGenerationAcrossMultipleSubAccountNumbers() throws Exception {
    //
    // String[] input = new String[] {
    // "2007BL1031400ADV 5000---ACEX07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON ASSOCIATES IN 1200.00D2006-01-05 ---------- ",
    // "2007BL1031400AHD 9041---ACLI07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON ASSOCIATES IN 1200.00C2006-01-05 ---------- "
    // };
    //
    // EntryHolder[] output = new EntryHolder[] {
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BL1031400ADV 5000---ACEX07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON
    // ASSOCIATES IN 1200.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BL1031400AHD 9041---ACLI07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON
    // ASSOCIATES IN 1200.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400ADV 5000---ACEX07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON
    // ASSOCIATES IN 1200.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400ADV 8000---ACAS07TOPSLGOFFSETSAC00000GENERATED OFFSET
    // 1200.00C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400AHD 9041---ACLI07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON
    // ASSOCIATES IN 1200.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400AHD 8000---ACAS07TOPSLGOFFSETSAC00000GENERATED OFFSET
    // 1200.00D2006-01-01 ---------- "),
    // };
    //
    // scrub(input);
    // assertOriginEntries(4,output);
    // }

    // This test fails in anthill but runs in dev
    // public void testOffsetGenerationAcrossMultipleDocumentNumbers() throws Exception {
    //
    // String[] input = new String[] {
    // "2007BA6044913-----1466---ACIC07AVAD01OFFSETDC100000online permit sales for 01/03/06 240.00D2006-01-05 ---------- ",
    // "2007BA6044913-----5000---ACEX07AVAD01OFFSETDC100000online permit sales for 01/03/06 3880.00C2006-01-05 ---------- ",
    // "2007BA6044913-----4100---ACEX07AVAD01OFFSETDC200000online permit sales for 01/03/06 725.00C2006-01-05 ---------- ",
    // "2007BA6044913-----1800---ACIC07AVAD01OFFSETDC200000online permit sales for 01/03/06 3395.00D2006-01-05 ---------- "
    // };
    //
    // EntryHolder[] output = new EntryHolder[] {
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BA6044913-----1466---ACIC07AVAD01OFFSETDC100000online permit sales for
    // 01/03/06 240.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BA6044913-----5000---ACEX07AVAD01OFFSETDC100000online permit sales for
    // 01/03/06 3880.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BA6044913-----4100---ACEX07AVAD01OFFSETDC200000online permit sales for
    // 01/03/06 725.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BA6044913-----1800---ACIC07AVAD01OFFSETDC200000online permit sales for
    // 01/03/06 3395.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BA6044913-----1466---ACIC07AVAD01OFFSETDC100000online permit sales for
    // 01/03/06 240.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BA6044913-----5000---ACEX07AVAD01OFFSETDC100000online permit sales for
    // 01/03/06 3880.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BA6044913-----9897---ACFB07AVAD01OFFSETDC100000GENERATED OFFSET
    // 3640.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BA6044913-----1800---ACIC07AVAD01OFFSETDC200000online permit sales for
    // 01/03/06 3395.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BA6044913-----4100---ACEX07AVAD01OFFSETDC200000online permit sales for
    // 01/03/06 725.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BA6044913-----9897---ACFB07AVAD01OFFSETDC200000GENERATED OFFSET
    // 2670.00C2006-01-01 ---------- "),
    // };
    //
    // scrub(input);
    // assertOriginEntries(4,output);
    // }

    /**
     * Tests that the scrubber generates the correct offset entries, even with there are multiple origin codes involved
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testOffsetGenerationAcrossMultipleOriginCodes() throws Exception {

        String[] input = new String[] { "2007BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                       ", "2007BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----8000---ACAS07DI  01OFFSETORG     00000GENERATED OFFSET                                   294.64C2006-01-01          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044906-----8000---ACAS07DI  EUOFFSETORG     00000GENERATED OFFSET                                   294.64C2006-01-01          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    // This test doesn't work in anthill for some reason
    // public void testOffsetGenerationAcrossMultipleDocumentTypes() throws Exception {
    //
    // String[] input = new String[] {
    // "2007BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT CABLE 44.95C2006-01-05 ---------- ",
    // "2007BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO 540.00C2006-01-05 ---------- ",
    // "2007BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT CABLE 44.95C2006-01-05 ---------- ",
    // "2007BL1031400-----1800---ACIN07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO 547.00D2006-01-05 ---------- "
    // };
    //
    // EntryHolder[] output = new EntryHolder[] {
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT
    // CABLE 44.95C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S
    // FASHIONS FO 540.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT
    // CABLE 44.95C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP,"2007BL1031400-----1800---ACIN07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S
    // FASHIONS FO 547.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400-----1800---ACIN07GEC 01OFFSETDTP00000SOM/MUSIC
    // GENERAL/SULLIVAN S FASHIONS FO 547.00D2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000SOM/MUSIC
    // GENERAL/INSIGHT CABLE 44.95C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400-----8000---ACAS07GEC 01OFFSETDTP00000GENERATED OFFSET
    // 502.05C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC
    // GENERAL/INSIGHT CABLE 44.95C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC
    // GENERAL/SULLIVAN S FASHIONS FO 540.00C2006-01-05 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2007BL1031400-----8000---ACAS07PCDO01OFFSETDTP00000GENERATED OFFSET
    // 584.95D2006-01-01 ---------- "),
    // };
    //
    // scrub(input);
    // assertOriginEntries(4,output);
    // }

    /**
     * Tests that the scrubber considers entries with closed accounts to be valid
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testClosedAccount() throws Exception {

        String[] input = new String[] { "2007BA6044909-----1800---ACIN07CR  UBCLOSACCT      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                               ", "2007BA6044909-----8000---ACAS07CR  UBCLOSACCT      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                               " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044909-----1800---ACIN07CR  UBCLOSACCT      00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044909-----8000---ACAS07CR  UBCLOSACCT      00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----1800---ACIN07CR  UBCLOSACCT      00000AUTO FR BA6044909Poplars Garage Fees                20.00C2006-01-05          ----------                                                                               "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACAS07CR  UBCLOSACCT      00000AUTO FR BA6044909TP Generated Offset                20.00D2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_EXPIRED, "2007BA6044900-----1800---ACIN07CR  UBCLOSACCT      00000AUTO FR BA6044909Poplars Garage Fees                20.00C2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_EXPIRED, "2007BA6044900-----8000---ACAS07CR  UBCLOSACCT      00000AUTO FR BA6044909TP Generated Offset                20.00D2006-01-05          ----------                                                                               "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber considers entries with accounts expired by document type to be valid
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccountByDocumentType() throws Exception {

        String[] input = new String[] { "2007BL4631557-----4100---ACEX07LOCRLGEXPRACTLC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               ", "2007BL4631557-----9041---ACLI07LOCRLGEXPRACTLC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631557-----4100---ACEX07LOCRLGEXPRACTLC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4631557-----9041---ACLI07LOCRLGEXPRACTLC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631557-----4100---ACEX07LOCRLGEXPRACTLC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631557-----9041---ACLI07LOCRLGEXPRACTLC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber considers entries with accounts, expired by the balance type, to be valid
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccountByBalanceType() throws Exception {

        String[] input = new String[] { "2007BL4131407-----4100---EXEX07TOPSLGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                      D                                        ", "2007BL4131407-----9041---EXLI07TOPSLGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                      D                                        " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4131407-----4100---EXEX07TOPSLGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                      D                                        "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4131407-----9041---EXLI07TOPSLGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                      D                                        "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4131407-----4100---EXEX07TOPSLGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                      D                                        "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4131407-----9041---EXLI07TOPSLGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                      D                                        "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber considers entries with accounts expired by origin code to be valid
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccountByOriginCode() throws Exception {

        String[] input = new String[] { "2007BL1031467-----5300---ACEE07DD  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL1031467-----8000---ACAS07DD  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                       ", };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL1031467-----5300---ACEE07DD  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL1031467-----8000---ACAS07DD  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031467-----5300---ACEE07DD  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031467-----8000---ACAS07DD  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber considers entries with expired c&g accounts to be valid
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredContractAndGrantAccount() throws Exception {

        String[] input = new String[] { "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               ", "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               " };
        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Tests that the scrubber considers entries with expired accounts to be valid
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccount() throws Exception {

        String[] input = new String[] { "2007BL1031467-----5300---ACEE07CD  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL1031467-----8000---ACAS07CD  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(OriginEntrySource.BACKUP, "2007BL1031467-----5300---ACEE07CD  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.BACKUP, "2007BL1031467-----8000---ACAS07CD  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031467-----5300---ACEE07CD  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1031467-----8000---ACAS07CD  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    // ************************************************************** Tests for error conditions below.

    /**
     * Tests that the scrubber considers invalid encumbrance update codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidEncumbranceUpdateCode() throws Exception {

        String[] inputTransactions = { "2007BL1031420-----4110---IEEX07PAYEEUINVALENCC     00000NOV-05 IMU Business Office          2224           241.75C2005-11-30          ----------                                      X                                        ", "2007BL1031420-----9892---IEAS07PAYEEUINVALENCC     00000NOV-05 IMU Business Office          2237           241.75D2005-11-30          ----------                                      X                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank encumbrance update codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankEncumbranceUpdateCodeOnEncumbranceRecord() throws Exception {

        String[] inputTransactions = { "2007BL1031400-----4100---PEEX07TF  01BLANKENCC     00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                               ", "2007BL1031400-----9891---PEFB07TF  01BLANKENCC     00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with blank reference numbers but an encumbrance update code that requires a
     * reference document to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankReferenceDocumentNumberWithEncumbranceUpdateCodeOfR() throws Exception {

        String[] inputTransactions = { "2007BA6044900-----1599---EXIN07TOPSLGBLANKRDOC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------        CR  01                   R                                        ", "2007BA6044900-----9041---EXLI07TOPSLDBLANKRDOC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------        CR  01                   R                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with a document number present but no other document data to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testReferenceDocumentNumberPresentWithoutOtherFields() throws Exception {

        String[] inputTransactions = { "2007BA6044906-----5300---ACEE07CHKDPDLONERDOC      12345TEST KUALI SCRUBBER EDITS                         1445.00D2006-01-05ABCDEFGHIJ----------12345678      123456789                                                        ", "2007BA6044906-----8000---ACAS07CHKDPDLONERDOC      12345TEST KUALI SCRUBBER EDITS                         1445.00C2006-01-05ABCDEFGHIG----------12345678      123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank reference origin codes, in an entry with the encumbrance update code requiring
     * reference documents, to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankReferenceOriginCodeWithEncumbranceUpdateCodeOfR() throws Exception {

        String[] inputTransactions = { "2007BL9120656-----5000---ACEX07INV EUBLANKRORG     00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------        DI    123456789                                                        ", "2007BL9120656-----8000---ACAS07INV EUBLANKRORG     00000TP Generated Offset                               3375.00D2006-01-05          ----------        DI    123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid reference origin codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidReferenceOriginCode() throws Exception {

        String[] inputTransactions = { "2007BL2231411-----2400---ACEX07ST  EUINVALRORG     00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------        CD  XX123456789                                                        ", "2007BL2231411-----8000---ACAS07ST  EUINVALRORG     00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------        CD  XX123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank reference document types, in an entry with the encumbrance update code that requiring
     * reference documents, to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankReferenceDocumentTypeWithEncumbranceUpdateCodeOfR() throws Exception {

        String[] inputTransactions = { "2007BL2231408-----4035---ACEX07SB  01BLANKRDTP     00000Biology Stockroom                                   13.77D2006-01-05          ----------            LG123456789                                                        ", "2007BL2231408-----8000---ACAS07SB  01BLANKRDTP     00000TP Generated Offset                                 13.77C2006-01-05          ----------            LG123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid reference document types to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidReferenceDocumentType() throws Exception {
        String[] inputTransactions = { "2007BL1031497-----4190---ACEX07GEC 01INVALRDTP     00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------        XXXXLG123456789                                                        ", "2007BL1031497-----8000---ACAS07GEC 01INVALRDTP     00000TP Generated Offset                                 40.72D2006-01-05          ----------        XXXXLG123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid project codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidProjectCode() throws Exception {
        String[] inputTransactions = { "2007BL9120656-----4035---ACEX07CR  01INVALPROJ     00000pymts recd 12/28/05                                 25.15C2006-01-05          XXXXXXXXX                                                                                ", "2007BL9120656-----8000---ACAS07CR  01INVALPROJ     00000TP Generated Offset                                 25.15D2006-01-05          XXXXXXXXX                                                                                " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid transaction dates to be errors.
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidTransactionDate() throws Exception {
        String[] inputTransactions = { "2007BL1031497-----4100---ACEX07PO  LGINVALDATE     00000Rite Quality Office Supplies Inc.                   43.42D2096-02-11          ----------                                                                               ", "2007BL1031497-----9892---ACFB07PO  LGINVALDATE     00000Rite Quality Office Supplies Inc.                   43.42C1006-12-23          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid debit/credit codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidDebitCreditCode() throws Exception {
        String[] inputTransactions = { "2007BL1031420-----4110---ACEX07DI  EUINVALDBCR     00000NOV-05 IMU Business Office          2224           241.75X2005-11-30          ----------                                                                               ", "2007BL1031420-----8000---ACAS07DI  EUINVALDBCR     00000NOV-05 IMU Business Office          2237           241.75X2005-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers non blank debit/credit codes on entries not requiring offsets to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testDebitCreditCodeOnTransactionNotRequiringOffset() throws Exception {
        String[] inputTransactions = { "2007BL1031400-----4100---MBEX07BA  01WRONGDBCR     00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                               ", "2007BL1031400-----1800---MBLI07BA  01WRONGDBCR     00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank debit/credit codes on entries requiring offsets to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankDebitCreditCodeOnTransactionRequiringOffset() throws Exception {
        String[] inputTransactions = { "2007BA6044913-----1470---ACIN07CR  01BLANKDBCR     00000Poplars Garage Fees                                 20.00 2006-01-05          ----------                                                                               ", "2007BA6044913-----8000---ACAS07CR  01BLANKDBCR     00000TP Generated Offset                                 20.00 2006-01-05          ----------                                                                               " };
        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank document numbers to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankDocumentNumber() throws Exception {
        String[] inputTransactions = { "2007BL2231423-----1800---ACIN  CR  PL              00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                               ", "2007BL2231423-----8000---ACAS  CR  PL              00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid origin codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidOriginCode() throws Exception {
        String[] inputTransactions = { "2007BA9120656-----5000---ACEX07INV XXINVALORIG     00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                               ", };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank origin codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankOriginCode() throws Exception {

        String[] inputTransactions = { "2007BL2231411-----2400---ACEX07ST    BLANKORIG     00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                               ", };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid document types to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidDocumentType() throws Exception {
        String[] inputTransactions = { "2007BL2231408-----4035---ACEX07XXX 01INVALDTYP     00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                               ", "2007BL2231408-----8000---ACAS07XXX 01INVALDTYP     00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank document types to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankDocumentType() throws Exception {
        String[] inputTransactions = { "2007BA6044900-----8000---ACAS07    01BLANKDTYP     00000TP Generated Offset                               1650.00C2006-01-05          ----------                                                                               ", "2007BL6044900-----4866---ACEX07    01BLANKDTYP     00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid fiscal periods to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidFiscalPeriod() throws Exception {
        String[] inputTransactions = { "2007BL1031497-----4190---ACEX14GEC 01INVALPER      00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                               ", "2007BL1031497-----8000---ACASXXGEC 01INVALPER      00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers closed fiscal periods to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testClosedFiscalPeriod() throws Exception {
        String[] inputTransactions = { "2003BA9120656-----4035---ACEX01CR  01CLOSEPER      00000pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                                                               ", "2003BA9120656-----8000---ACAS01CR  01CLOSEPER      00000TP Generated Offset                                 25.15D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid object type codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidObjectType() throws Exception {
        String[] inputTransactions = { "2007BL1031400-----4100---ACXX07PO  LGINVALOBTY     00000Rite Quality Office Supplies Inc.                   43.42D2006-01-05          ----------                                                                               ", "2007BL1031400-----9892---ACFB07PO  LGINVALOBTY     00000Rite Quality Office Supplies Inc.                   43.42C2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid balance type codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidBalanceType() throws Exception {

        String[] inputTransactions = { "2007BL1031420-----4110---XXEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                               ", "2007BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid financial object codes to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidObjectCode() throws Exception {
        String[] inputTransactions = { "2007BL2231423-----XXXX---ACIN  CR  PLINVALOBJ      00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                               ", "2007BL2231423-----8000---ACAS  CR  PLINVALOBJ      00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2006BL2231423-----8000---ACAS07CR  PLINVALOBJ      00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                               "), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2007BL2231423-----XXXX---ACIN  CR  PLINVALOBJ      00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                               ") };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }


    /**
     * Tests that the scrubber considers entries with invalid sub accounts to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidSubAccountNumber() throws Exception {
        String[] inputTransactions = { "2007BL2231408XXXX 4035---ACEX07SB  01INVALSACT     00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                               ", "2007BL2231408XXXX 8000---ACAS07SB  01INVALSACT     00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with inactive sub accounts to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInactiveSubAccountNumber() throws Exception {
        String[] inputTransactions = { "2007BA6044900ARREC8000---ACAS07IB  01INACTSACT     00000TP Generated Offset                               1650.00C2006-01-05          ----------                                                                               ", "2007BL6044900ARREC4866---ACEX07IB  01INACTSACT     00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid account numbers to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidAccountNumber() throws Exception {
        String[] inputTransactions = { "2007EA1234567-----4035---ACEX07CR  01INVALACCT     00000pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                                                               ", "2007EA1234567-----8000---ACAS07CR  01INVALACCT     00000TP Generated Offset                                 25.15D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank account numbers to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankAccountNumber() throws Exception {
        String[] inputTransactions = { "2007IN       -----5000---ACEX07PO  LGBLANKACCT     00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                               ", "2007IN       -----9041---ACLI07PO  LGBLANKACCT     00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid charts to be errors
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidChart() throws Exception {
        String[] inputTransactions = { "2007XX1031420-----4110---ACEX07DI  EUINVALCHAR     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                               ", "2007XX1031420-----8000---ACAS07DI  EUINVALCHAR     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid fiscal years to be errors. Note: this test will malfunction sometime in the year
     * 2019
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidFiscalYear() throws Exception {
        String[] inputTransactions = { "2020BA6044913-----1470---ACIN07CR  01INVALFISC     00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                               ", "2020BA6044913-----8000---ACAS07CR  01INVALFISC     00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Entry with a closed fiscal period/year. These transactions should be marked as errors.
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testClosedFiscalYear() throws Exception {

        String[] inputTransactions = { "2003BA6044906-----4100---ACEX07TOPSLGCLOSEFISC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                               ", "2003BA6044906-----9041---ACLI07TOPSLGCLOSEFISC     00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    /**
     * Entry with a null fiscal year. The fiscal year should be replaced with the default fiscal year. They should not be errors.
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testDefaultFiscalYear() throws Exception {

        String[] inputTransactions = { "    BA6044900-----5300---ACEE07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "    BA6044900-----8000---ACAS07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                       " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2006BA6044900-----5300---ACEE07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2006BA6044900-----8000---ACAS07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                       ") };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

//    /**
//     * Tests that the scrubber considers entries with inactive balance types to be errors
//     * 
//     * @throws Exception thrown if any exception is encountered for any reason
//     */
//    public void testInactiveBalanceType() throws Exception {
//        Map<String, Object> primaryKeys = new HashMap<String, Object>();
//        primaryKeys.put(KFSPropertyConstants.CODE, "AC");
//        this.deactivate(BalanceTyp.class, primaryKeys);
//
//        String[] inputTransactions = { "2007BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                               ", "2007BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                               " };
//
//        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };
//
//        scrub(inputTransactions);
//        assertOriginEntries(4, outputTransactions);
//    }
//
//    /**
//     * Tests that the scrubber considers entries with inactive origination codes to be errors
//     * 
//     * @throws Exception thrown if any exception is encountered for any reason
//     */
//    public void testInactiveOriginCode() throws Exception {
//        Map<String, Object> primaryKeys = new HashMap<String, Object>();
//        primaryKeys.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, "EU");
//        this.deactivate(OriginationCode.class, primaryKeys);
//
//        String[] inputTransactions = { "2007BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                               ", "2007BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                               " };
//
//        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };
//
//        scrub(inputTransactions);
//        assertOriginEntries(4, outputTransactions);
//    }
//
//    /**
//     * Tests that the scrubber considers entries with inactive origination codes to be errors
//     * 
//     * @throws Exception thrown if any exception is encountered for any reason
//     */
//    public void testInactiveObjectCode() throws Exception {
//        Map<String, Object> primaryKeys = new HashMap<String, Object>();
//        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "2007");
//        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
//        primaryKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "4110");
//        this.deactivate(ObjectCode.class, primaryKeys);
//
//        String[] inputTransactions = { "2007BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                               ", "2007BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                               " };
//
//        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };
//
//        scrub(inputTransactions);
//        assertOriginEntries(4, outputTransactions);
//    }
//
//    /**
//     * Tests that the scrubber considers entries with inactive origination codes to be errors
//     * 
//     * @throws Exception thrown if any exception is encountered for any reason
//     */
//    public void testInactiveObjectType() throws Exception {
//        Map<String, Object> primaryKeys = new HashMap<String, Object>();
//        primaryKeys.put(KFSPropertyConstants.CODE, "EX");
//        this.deactivate(ObjectType.class, primaryKeys);
//
//        String[] inputTransactions = { "2007BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                               ", "2007BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                               " };
//
//        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, inputTransactions[0]) };
//
//        scrub(inputTransactions);
//        assertOriginEntries(4, outputTransactions);
//    }

    /**
     * Loads an array of String-formatted entries into the database, and then runs the scrubber on those entries
     * 
     * @param inputTransactions an array of String-formatted entries to scrub
     */
    private void scrub(String[] inputTransactions) {
        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.BACKUP, inputTransactions, date);
        persistenceService.clearCache();
        scrubberService.scrubEntries();
    }

    /**
     * deactivate a business object of the type clazz with the given primary keys. If the business object is not Inactivateable, do
     * nothing on it.
     * 
     * @param <T> the type of the business object to be deactivated
     * @param clazz the class type of the business object to be deactivated
     * @param primaryKeys the primary keys of the business object to be deactivated
     */
    private <T extends PersistableBusinessObject> void deactivate(Class<T> clazz, Map<String, Object> primaryKeys) {
        PersistableBusinessObject bo = businessObjectService.findByPrimaryKey(clazz, primaryKeys);

        if (bo instanceof Inactivateable) {
            Inactivateable inactivatedBO = (Inactivateable) bo;
            inactivatedBO.setActive(false);

            businessObjectService.save(bo);
        }
    }
}
