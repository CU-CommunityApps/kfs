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
package org.kuali.kfs.sys.document.routing.attribute;

import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class...
 */
@ConfigureContext
public class KualiAccountAttributeTest extends KualiTestBase {

    public void testGetFiscalOfficerCriteria_TOFOneLiner() throws IOException, InvalidXmlException, XPathExpressionException {

        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.TOF_FEMP_SUBCODE_ONELINER, "TransferOfFundsDocument");

        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        NodeList sourceLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.rice.kns.bo.SourceAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);

        String chart = "";
        String accountNumber = "";

        for (int i = 0; i < sourceLineNodes.getLength(); i++) {
            Node node = sourceLineNodes.item(i);

            chart = xpath.evaluate("./chartOfAccountsCode", node);
            assertEquals("BL", chart);
            accountNumber = xpath.evaluate("./accountNumber", node);
            assertEquals("2823205", accountNumber);
        }
    }

    public void testSearchableAccountAttributeXPathTest() throws IOException, InvalidXmlException, XPathExpressionException {

        final String xpathQuery = "wf:xstreamsafe('//org.kuali.rice.kns.bo.SourceAccountingLine/accountNumber') | wf:xstreamsafe('//org.kuali.rice.kns.bo.TargetAccountingLine/accountNumber')";

        DocumentContent docContent = KualiAttributeTestUtil.getDocumentContentFromXmlFile(KualiAttributeTestUtil.TOF_FEMP_SUBCODE_ONELINER, "TransferOfFundsDocument");

        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        NodeList sourceLineNodes = (NodeList) xpath.evaluate(xpathQuery, docContent.getDocument(), XPathConstants.NODESET);

        for (int i = 0; i < sourceLineNodes.getLength(); i++) {
            Node node = sourceLineNodes.item(i);
            System.err.println("[" + i + "] (" + node.getNodeType() + ") " + node.getNodeName() + " = " + node.getTextContent());
        }
    }

}
