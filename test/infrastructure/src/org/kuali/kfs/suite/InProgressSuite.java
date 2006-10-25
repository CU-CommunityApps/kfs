/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/infrastructure/src/org/kuali/kfs/suite/InProgressSuite.java,v $
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
package org.kuali.test.suite;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * The suite of all test classes or methods which {@link org.kuali.test.suite.RelatesTo} a Kuali JIRA issue in-progress.
 * IDEs or Ant can run this as JUnit tests.
 *
 * @see RelatesTo
 */
public class InProgressSuite {

    private static Collection<String> jiraIssuesInProgress;
    private static RuntimeException initializationException = null;
    // This public Confluence space does not require a login like JIRA does.  There could be a better way to do this in the JIRA API, though.
    private static final String JIRA_FILTER_URL = "https://test.kuali.org/confluence/display/KULDOC/Issue+Filter+for+Unit+Test+Framework";

    private static Collection<String> getNamesOfJiraIssuesInProgress() {
        if (initializationException != null) {
            // fast fail for remaining KualiTestBase tests that use this, after the first one fails to initialize
            throw initializationException;
        }
        if (jiraIssuesInProgress == null) {
            try {
                jiraIssuesInProgress = new HashSet<String>();
                WebResponse response = new WebConversation().getResponse(new GetMethodWebRequest(JIRA_FILTER_URL));
                WebTable results = response.getTableStartingWithPrefix("Kuali: Jira");
                int rowCount = results.getRowCount();
                for (int row = 2; row < rowCount; row++) {
                    jiraIssuesInProgress.add(results.getCellAsText(row, 0));
                }
            }
            catch (Exception e) {
                initializationException = new RuntimeException("test framework cannot get list of in-progress JIRA issues", e);
                throw initializationException;
            }
        }
        return jiraIssuesInProgress;
    }

    /**
     * Filters the JIRA issues which are currently in-progress.
     * The JIRA status is queried once when needed and cached statically for speed.
     * 
     * @param from JIRA issues from which to filter
     * @return any of the given issues that are currently in-progress in JIRA
     */
    public static Set<RelatesTo.JiraIssue> getInProgress(Collection<RelatesTo.JiraIssue> from) {
        HashSet<RelatesTo.JiraIssue> result = new HashSet<RelatesTo.JiraIssue>();
        if (!from.isEmpty()) { // try to avoid the JIRA query
            for (RelatesTo.JiraIssue issue : from) {
                if (getNamesOfJiraIssuesInProgress().contains(issue.toString())) {
                    result.add(issue);
                }
            }
        }
        return result;
    }

    private static boolean hasRelatedIssueInProgress(RelatesTo annotation) {
        return annotation != null && !getInProgress(Arrays.asList(annotation.value())).isEmpty();
    }

    public static TestSuite suite()
        throws Exception
    {
        TestSuiteBuilder.ClassCriteria classCriteria = new TestSuiteBuilder.ClassCriteria() {
            public boolean includes(Class<? extends TestCase> testClass) {
                return hasRelatedIssueInProgress(testClass.getAnnotation(RelatesTo.class));
            }
        };
        TestSuiteBuilder.MethodCriteria methodCriteria = new TestSuiteBuilder.MethodCriteria() {
            public boolean includes(Method method) {
                return hasRelatedIssueInProgress(method.getAnnotation(RelatesTo.class));
            }
        };
        return TestSuiteBuilder.build(classCriteria, methodCriteria);
    }

    /**
     * The suite of all test methods (including those within test class sub-suites)
     * which do not {@link org.kuali.test.suite.RelatesTo} a JIRA issue in-progress.
     * IDEs or Ant can run this as JUnit tests.
     */
    public static class Not {
        public static TestSuite suite()
            throws Exception
        {
            TestSuiteBuilder.MethodCriteria negativeMethodCriteria = new TestSuiteBuilder.MethodCriteria() {
                public boolean includes(Method method) {
                    RelatesTo testClassAnnotation = method.getDeclaringClass().getAnnotation(RelatesTo.class);
                    return !hasRelatedIssueInProgress(testClassAnnotation) && !hasRelatedIssueInProgress(method.getAnnotation(RelatesTo.class));
                }
            };
            return TestSuiteBuilder.build(TestSuiteBuilder.NULL_CRITERIA, negativeMethodCriteria);
        }
    }
}
