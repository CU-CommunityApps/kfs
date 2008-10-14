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
package org.kuali.kfs.sys.document.workflow;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.WorkflowUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * This class contains static utility methods used by the Kuali Workflow Attribute Classes.
 */
public class KualiWorkflowUtils extends WorkflowUtils {
    private static final Logger LOG = Logger.getLogger(KualiWorkflowUtils.class);

    /*
     * the following is so verbose because most times the match anywhere prefix is used and the verboseness prevents bad matching if
     * a document has an attribute named 'report'
     */
    public static final String XPATH_ELEMENT_SEPARATOR = "/";
    private static final String GENERATED_CONTENT_MAIN_TAG = "generatedContent";
    private static final String GENERATED_CONTENT_SUB_TAG = "report_for_routing_purposes";
    public static final String XML_REPORT_DOC_CONTENT_PREFIX = "<" + GENERATED_CONTENT_MAIN_TAG + "><" + GENERATED_CONTENT_SUB_TAG + ">";
    public static final String XML_REPORT_DOC_CONTENT_SUFFIX = "</" + GENERATED_CONTENT_SUB_TAG + "></" + GENERATED_CONTENT_MAIN_TAG + ">";
    public static final String XML_REPORT_DOC_CONTENT_XPATH_PREFIX = GENERATED_CONTENT_MAIN_TAG + XPATH_ELEMENT_SEPARATOR + GENERATED_CONTENT_SUB_TAG;

    // no trailing slash
    public static final String NEW_MAINTAINABLE_PREFIX_NTS = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "newMaintainableObject/businessObject";
    public static final String OLD_MAINTAINABLE_PREFIX_NTS = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "oldMaintainableObject/businessObject";
    public static final String NEW_MAINTAINABLE_PREFIX = NEW_MAINTAINABLE_PREFIX_NTS + XPATH_ELEMENT_SEPARATOR;
    public static final String OLD_MAINTAINABLE_PREFIX = KualiWorkflowUtils.OLD_MAINTAINABLE_PREFIX_NTS + XPATH_ELEMENT_SEPARATOR;
    public static final String ACCOUNT_DOC_TYPE = "AccountMaintenanceDocument";
    public static final String ACCOUNT_DEL_DOC_TYPE = "DelegateMaintenanceDocument";
    public static final String ACCOUNT_DELEGATE_GLOBAL_DOC_TYPE = "DelegateGlobal";
    public static final String ACCOUNT_CHANGE_DOC_TYPE = "AccountGlobalMaintenanceDocument";
    public static final String SUB_ACCOUNT_DOC_TYPE = "SubAccountMaintenanceDocument";
    public static final String SUB_OBJECT_DOC_TYPE = "SubObjCdMaintenanceDocument";
    public static final String OBJECT_CODE_CHANGE_DOC_TYPE = "ObjectCodeGlobalMaintenanceDocument";
    public static final String INTERNAL_BILLING_DOC_TYPE = "InternalBillingDocument";
    public static final String PRE_ENCUMBRANCE_DOC_TYPE = "PreEncumbranceDocument";
    public static final String DISBURSEMENT_VOCHER_DOC_TYPE = "DisbursementVoucherDocument";
    public static final String NON_CHECK_DISBURSEMENT_DOC_TYPE = "NonCheckDisbursementDocument";
    public static final String PROCUREMENT_CARD_DOC_TYPE = "ProcurementCardDocument";
    public static final String BUDGET_ADJUSTMENT_DOC_TYPE = "BudgetAdjustmentDocument";
    public static final String GENERAL_ERROR_CORRECTION_DOC_TYPE = "GeneralErrorCorrectionDocument";
    public static final String GENERAL_LEDGER_ERROR_CORRECTION_DOC_TYPE = "CorrectionDocument";
    public static final String MAINTENANCE_DOC_TYPE = "KualiMaintenanceDocument";
    public static final String FINANCIAL_DOC_TYPE = "KualiFinancialDocument";
    public static final String FINANCIAL_YEAR_END_DOC_TYPE = "KualiFinancialYearEndDocument";
    public static final String FIS_USER_DOC_TYPE = "KualiUserMaintenanceDocument";
    public static final String ORGANIZATION_DOC_TYPE = "OrgMaintenanceDocument";
    public static final String PROJECT_CODE_DOC_TYPE = "ProjectCodeMaintenanceDocument";
    public static final String KRA_BUDGET_DOC_TYPE = "KualiBudgetDocument";
    public static final String KRA_ROUTING_FORM_DOC_TYPE = "KualiRoutingFormDocument";
    public static final String SIMPLE_MAINTENANCE_DOC_TYPE = "KualiSimpleMaintenanceDocument";
    public static final String SUB_OBJECT_CODE_CHANGE_DOC_TYPE = "SubObjCdGlobalMaintenanceDocument";
    public static final String ORG_REVERSION_CHANGE_DOC_TYPE = "OrganizationReversionGlobalMaintenanceDocument";
    public static final String C_G_AWARD_DOC_TYPE = "AwardMaintenanceDocument";
    public static final String C_G_PROPOSAL_DOC_TYPE = "ProposalMaintenanceDocument";
    public static final String USER_DOC_TYPE = "UniversalUserMaintenanceDocument";
    public static final String CHART_ORG_WORKGROUP_DOC_TYPE = "ChartOrgWorkgroup";
    public static final String ACCOUNTS_PAYABLE_CREDIT_MEMO_DOCUMENT_TYPE = "CreditMemoDocument";
    public static final String ACCOUNTS_PAYABLE_PAYMENT_REQUEST_DOCUMENT_TYPE = "PaymentRequestDocument";
    public static final String FINANCIAL_DOCUMENT_TOTAL_AMOUNT_XPATH = xstreamSafeXPath(XSTREAM_MATCH_ANYWHERE_PREFIX + KFSPropertyConstants.DOCUMENT_HEADER + XPATH_ELEMENT_SEPARATOR + KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT + "/value");
    public static final String ACCOUNT_GLOBAL_DETAILS_XPATH = xstreamSafeXPath(NEW_MAINTAINABLE_PREFIX + "accountGlobalDetails/list/org.kuali.kfs.coa.businessobject.AccountGlobalDetail");
    public static final String ORG_REVERSION_GLOBALS_XPATH = xstreamSafeXPath(NEW_MAINTAINABLE_PREFIX + "organizationReversionGlobalOrganizations/list/org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalOrganization");

    public class RouteLevels {

        public static final int ADHOC = 0;
        public static final int EXCEPTION = -1;
        public static final int ORG_REVIEW = 1;

    }

    public class RouteLevelNames {

        public static final String ACCOUNT_REVIEW = "Account Review";
        public static final String SUB_ACCOUNT_REVIEW = "Sub Account Review";
        public static final String ORG_REVIEW = "Org Review";
        public static final String EMPLOYEE_INDICATOR = "Employee Indicator";
        public static final String TAX_CONTROL_CODE = "Tax Control Code";
        public static final String ALIEN_INDICATOR = "Alien Indicator";
        public static final String PAYMENT_REASON = "Payment Reason";
        public static final String PAYMENT_REASON_CAMPUS = "Payment Reason+Campus Code";
        public static final String CAMPUS_CODE = "Campus Code";
        public static final String ALIEN_INDICATOR_PAYMENT_REASON = "Alien Indicator+Payment Reason";
        public static final String PAYMENT_METHOD = "Payment Method";
        public static final String ACCOUNT_REVIEW_FULL_EDIT = "Account Review Full Edit";

        public static final String PROJECT_DIRECTOR = "Project Director";
        public static final String CG_WORKGROUP = "Award Workgroup";
        public static final String RECREATE_WORKGROUP = "Recreate Workgroup";
    }

    public static final Set SOURCE_LINE_ONLY_DOCUMENT_TYPES = new HashSet();
    static {
        SOURCE_LINE_ONLY_DOCUMENT_TYPES.add(DISBURSEMENT_VOCHER_DOC_TYPE);
    }

    public static final Set TARGET_LINE_ONLY_DOCUMENT_TYPES = new HashSet();
    static {
        TARGET_LINE_ONLY_DOCUMENT_TYPES.add(INTERNAL_BILLING_DOC_TYPE);
        TARGET_LINE_ONLY_DOCUMENT_TYPES.add(PROCUREMENT_CARD_DOC_TYPE);
    }

    public static boolean isSourceLineOnly(String documentTypeName) {
        return SOURCE_LINE_ONLY_DOCUMENT_TYPES.contains(documentTypeName);
    }

    public static boolean isTargetLineOnly(String documentTypeName) {
        return TARGET_LINE_ONLY_DOCUMENT_TYPES.contains(documentTypeName);
    }

    public static final boolean isMaintenanceDocument(DocumentType documentType) {
        DocumentEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentType.getName());
        return MaintenanceDocument.class.isAssignableFrom(entry.getDocumentClass());
    }


    /**
     * TODO: remove this method when we upgrade to workflow 2.2 - the problem that this helps with is as follows:
     * StandardWorkflowEngine is not currently setting up the DocumentContent on the RouteContext object. Instead that's being
     * handled by the RequestsNode which, in the case of the BudgetAdjustmentDocument, we never pass through before hitting the
     * first split. So, in that particular case, we have to reference an attribute that gives us the xml string and translate that
     * to a dom document ourselves.
     * 
     * @param xmlDocumentContent
     * @return a dom representation of the xml provided
     * @deprecated
     */
    public static final Document getDocument(String xmlDocumentContent) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(xmlDocumentContent))));
        }
        catch (Exception e) {
            throw new RuntimeException(KualiWorkflowUtils.class.getName() + " encountered an exception while attempting to convert and xmlDocumentContent String into a org.w3c.dom.Document", e);
        }
    }

    /**
     * This method uses the document type name to get the AccountingDocument implementation class from the data dictionary, creates
     * a new instance and uses the getSourceAccountingLine method to get the name of the source accounting line class. It is
     * intended for use by our workflow attributes when building xpath expressions
     * 
     * @param documentTypeName the document type name to use when querying the TransactionalDocumentDataDictionaryService
     * @return the name of the source accounting line class associated with the specified workflow document type name
     */
    public static final String getSourceAccountingLineClassName(String documentTypeName) {
        Class documentClass = SpringContext.getBean(DataDictionaryService.class).getDocumentClassByTypeName(documentTypeName);
        if (!AccountingDocument.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("getSourceAccountingLineClassName method of KualiWorkflowUtils requires a documentTypeName String that corresponds to a class that implments AccountingDocument");
        }
        try {
            Class sourceAccountingLineClass = ((AccountingDocument) documentClass.newInstance()).getSourceAccountingLineClass();
            String sourceAccountingLineClassName = null;
            if (sourceAccountingLineClass != null) {
                sourceAccountingLineClassName = sourceAccountingLineClass.getName();
            }
            else {
                sourceAccountingLineClassName = SourceAccountingLine.class.getName();
            }
            return sourceAccountingLineClassName;
        }
        catch (InstantiationException e) {
            throw new RuntimeException("getSourceAccountingLineClassName method of KualiWorkflowUtils caught InstantiationException while try to create instance of class: " + documentClass);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("getSourceAccountingLineClassName method of KualiWorkflowUtils caught IllegalAccessException while try to create instance of class: " + documentClass);
        }
    }

    /**
     * This method uses the document type name to get the AccountingDocument implementation class from the data dictionary, creates
     * a new instance and uses the getTargetAccountingLine method to get the name of the target accounting line class. It is
     * intended for use by our workflow attributes when building xpath expressions
     * 
     * @param documentTypeName the document type name to use when querying the TransactionalDocumentDataDictionaryService
     * @return the name of the target accounting line class associated with the specified workflow document type name
     */
    public static final String getTargetAccountingLineClassName(String documentTypeName) {
        Class documentClass = SpringContext.getBean(DataDictionaryService.class).getDocumentClassByTypeName(documentTypeName);
        if (!AccountingDocument.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("getTargetAccountingLineClassName method of KualiWorkflowUtils requires a documentTypeName String that corresponds to a class that implments AccountingDocument");
        }
        try {
            Class targetAccountingLineClass = ((AccountingDocument) documentClass.newInstance()).getTargetAccountingLineClass();
            String targetAccountingLineClassName = null;
            if (targetAccountingLineClass != null) {
                targetAccountingLineClassName = targetAccountingLineClass.getName();
            }
            else {
                targetAccountingLineClassName = TargetAccountingLine.class.getName();
            }
            return targetAccountingLineClassName;
        }
        catch (InstantiationException e) {
            throw new RuntimeException("getTargetAccountingLineClassName method of KualiWorkflowUtils caught InstantiationException while try to create instance of class: " + documentClass);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("getTargetAccountingLineClassName method of KualiWorkflowUtils caught IllegalAccessException while try to create instance of class: " + documentClass);
        }
    }

    /**
     * This method gets the document total amount from the DocumentHeader If an XPathExpressionException is thrown, this will be
     * re-thrown within a RuntimeException.
     * 
     * @param routeContext The RouteContext object from the workflow system
     * @return the KualiDecimal value of the total amount from the document's workflow document content or null if the amount value
     *         cannot be found.
     */
    public static KualiDecimal getFinancialDocumentTotalAmount(RouteContext routeContext) {
        Document document = routeContext.getDocumentContent().getDocument();
        return getFinancialDocumentTotalAmount(document);
    }

    /**
     * This method gets the document total amount from the DocumentHeader <br>
     * If an XPathExpressionException is thrown, this will be re-thrown within a RuntimeException.
     * 
     * @param document - the document object from the workflow system
     * @return the KualiDecimal value of the total amount from the document's workflow document content or null if the amount value
     *         cannot be found.
     */
    public static KualiDecimal getFinancialDocumentTotalAmount(Document document) {
        XPath xpath = getXPath(document);
        String docTotalAmount = null;
        String xpathXpression = FINANCIAL_DOCUMENT_TOTAL_AMOUNT_XPATH;
        try {
            docTotalAmount = (String) xpath.evaluate(xpathXpression, document, XPathConstants.STRING);
            if (StringUtils.isEmpty(docTotalAmount)) {
                String message = "Cannot find financial document total amount";
                LOG.warn("getDocumentTotalAmount() " + message);
                return null;
            }
            return new KualiDecimal(docTotalAmount);
        }
        catch (XPathExpressionException xe) {
            String errorMsg = "Error executing XPath expression - '" + xpathXpression + "'";
            LOG.error(errorMsg, xe);
            throw new RuntimeException(errorMsg, xe);
        }
    }
}
