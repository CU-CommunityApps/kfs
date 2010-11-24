/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.kfs.module.endow.document.validation.impl.AssetIncreaseDocumentRulesTest;

/**
 * Runs all endowment tests.
 */
public class AllEndowmentTests {

    /**
     * Returns a suite of all the tests in ENDOW that have been added here.
     * 
     * @return a Test suite with most all ENDOW tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(AssetIncreaseDocumentRulesTest.class);

        return suite;
    }

    /**
     * Runs all the tests in the all test test suite
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
