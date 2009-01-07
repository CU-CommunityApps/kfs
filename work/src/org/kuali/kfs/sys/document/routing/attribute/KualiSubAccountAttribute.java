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
package org.kuali.kfs.sys.document.routing.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.MassRuleAttribute;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.ui.Row;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class...
 */
public class KualiSubAccountAttribute implements WorkflowAttribute, MassRuleAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiAccountAttribute.class);

    public static final String FIN_COA_CD_KEY = "fin_coa_cd";

    public static final String ACCOUNT_NBR_KEY = "account_nbr";

    public static final String ORG_CD_KEY = "org_cd";

    public static final String SUB_ACCOUNT_NBR_KEY = "sub_acct_nbr";

    private static final String SUB_ACCOUNT_ATTRIBUTE = "KUALI_SUB_ACCOUNT_ATTRIBUTE";

    private static final String DOCUMENT_SUB_ACCOUNT_VALUES_KEY = "subAccounts";

    // defined here so field creation matches rule errors
    private static final Class SUB_ACCOUNT_NUMBER_FIELD_CLASS = SubAccount.class;
    private static final String SUB_ACCOUNT_NUMBER_FIELD_PROPERTY = KFSPropertyConstants.SUB_ACCOUNT_NUMBER;
    private static final Class ACCOUNT_NUMBER_FIELD_CLASS = SUB_ACCOUNT_NUMBER_FIELD_CLASS;
    private static final String ACCOUNT_NUMBER_FIELD_PROPERTY = KFSPropertyConstants.ACCOUNT_NUMBER;
    private static final Class ORG_CODE_FIELD_CLASS = SUB_ACCOUNT_NUMBER_FIELD_CLASS;
    private static final String ORG_CODE_FIELD_PROPERTY = KFSPropertyConstants.FIN_REPORT_ORGANIZATION_CODE;
    private static final Class CHART_CODE_FIELD_CLASS = SUB_ACCOUNT_NUMBER_FIELD_CLASS;
    private static final String CHART_CODE_FIELD_PROPERTY = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;

    private String finCoaCd;
    private String accountNbr;
    private String subAccountNbr;
    private String orgCd;

    private boolean required;

    private List ruleRows;

    private List routingDataRows;

    /**
     * No arg constructor
     */
    public KualiSubAccountAttribute() {
        ruleRows = new ArrayList();
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(CHART_CODE_FIELD_CLASS, CHART_CODE_FIELD_PROPERTY, FIN_COA_CD_KEY));
        Map fieldConversionMap = new HashMap();
        fieldConversionMap.put(CHART_CODE_FIELD_PROPERTY, FIN_COA_CD_KEY);
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(ACCOUNT_NUMBER_FIELD_CLASS, ACCOUNT_NUMBER_FIELD_PROPERTY, ACCOUNT_NBR_KEY, fieldConversionMap));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(ORG_CODE_FIELD_CLASS, ORG_CODE_FIELD_PROPERTY, ORG_CD_KEY, fieldConversionMap));
        fieldConversionMap.put(ACCOUNT_NUMBER_FIELD_PROPERTY, ACCOUNT_NBR_KEY);
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(SUB_ACCOUNT_NUMBER_FIELD_CLASS, SUB_ACCOUNT_NUMBER_FIELD_PROPERTY, SUB_ACCOUNT_NBR_KEY, fieldConversionMap));

        routingDataRows = new ArrayList();
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(CHART_CODE_FIELD_CLASS, CHART_CODE_FIELD_PROPERTY, FIN_COA_CD_KEY));
        fieldConversionMap = new HashMap();
        fieldConversionMap.put(CHART_CODE_FIELD_PROPERTY, FIN_COA_CD_KEY);
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(ACCOUNT_NUMBER_FIELD_CLASS, ACCOUNT_NUMBER_FIELD_PROPERTY, ACCOUNT_NBR_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(SUB_ACCOUNT_NUMBER_FIELD_CLASS, SUB_ACCOUNT_NUMBER_FIELD_PROPERTY, SUB_ACCOUNT_NBR_KEY));
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getAccountNbr()) || Utilities.isEmpty(getSubAccountNbr())) {
            return "";
        }
        StringBuffer chartCode = new StringBuffer().append("<" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + ">").append(getFinCoaCd()).append("</" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + ">");
        StringBuffer accountNumber = new StringBuffer().append("<" + KFSPropertyConstants.ACCOUNT_NUMBER + ">").append(getAccountNbr()).append("</" + KFSPropertyConstants.ACCOUNT_NUMBER + ">");
        StringBuffer subAccountNumber = new StringBuffer().append("<" + KFSPropertyConstants.SUB_ACCOUNT_NUMBER + ">").append(getSubAccountNbr()).append("</" + KFSPropertyConstants.SUB_ACCOUNT_NUMBER + ">");
        return new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX).append(chartCode).append(accountNumber).append(subAccountNumber).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getRoutingDataRows()
     */
    public List<Row> getRoutingDataRows() {
        return routingDataRows;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getRuleExtensionValues()
     */
    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, getFinCoaCd()));
        if (!StringUtils.isBlank(getAccountNbr())) {
            extensions.add(new RuleExtensionValue(ACCOUNT_NBR_KEY, getAccountNbr()));
        }
        if (!StringUtils.isBlank(getOrgCd())) {
            extensions.add(new RuleExtensionValue(ORG_CD_KEY, getOrgCd()));
        }
        extensions.add(new RuleExtensionValue(SUB_ACCOUNT_NBR_KEY, getSubAccountNbr()));
        return extensions;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getRuleRows()
     */
    public List<Row> getRuleRows() {
        return ruleRows;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#setRequired(boolean)
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {
        return validateSubAccountValues(paramMap);
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        return validateSubAccountValues(paramMap);
    }

    private List validateSubAccountValues(Map paramMap) {
        setAccountNbr(LookupUtils.forceUppercase(SubAccount.class, KFSPropertyConstants.ACCOUNT_NUMBER, (String) paramMap.get(ACCOUNT_NBR_KEY)));
        setOrgCd(LookupUtils.forceUppercase(SubAccount.class, KFSPropertyConstants.FIN_REPORT_ORGANIZATION_CODE, (String) paramMap.get(ORG_CD_KEY)));
        String chartFieldName = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
        if (StringUtils.isBlank(getAccountNbr())) {
            chartFieldName = KFSPropertyConstants.FINANCIAL_REPORT_CHART_CODE;
        }
        setFinCoaCd(LookupUtils.forceUppercase(SubAccount.class, chartFieldName, (String) paramMap.get(FIN_COA_CD_KEY)));
        setSubAccountNbr(LookupUtils.forceUppercase(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, (String) paramMap.get(SUB_ACCOUNT_NBR_KEY)));
        List errors = new ArrayList();
        if ((!isRequired()) && (StringUtils.isBlank(getFinCoaCd()) && StringUtils.isBlank(getSubAccountNbr()) && (StringUtils.isBlank(getAccountNbr()) && StringUtils.isBlank(getOrgCd())))) {
            // attribute is not required and no fields are filled in
            return new ArrayList();
        }
        else if ((isRequired()) && (StringUtils.isBlank(getFinCoaCd()) || StringUtils.isBlank(getSubAccountNbr()) || ((StringUtils.isBlank(getAccountNbr())) && (StringUtils.isBlank(getOrgCd()))))) {
            // attribute is required and at least one needed field is blank
            String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(CHART_CODE_FIELD_CLASS, CHART_CODE_FIELD_PROPERTY) + ", " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(SUB_ACCOUNT_NUMBER_FIELD_CLASS, SUB_ACCOUNT_NUMBER_FIELD_PROPERTY) + ", and one of " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ACCOUNT_NUMBER_FIELD_CLASS, ACCOUNT_NUMBER_FIELD_PROPERTY) + " or " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ORG_CODE_FIELD_CLASS, ORG_CODE_FIELD_PROPERTY) + " is required";
            errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
        }
        else if (StringUtils.isNotBlank(getAccountNbr()) && StringUtils.isNotBlank(getOrgCd())) {
            // you cannot have both fields filled in
            String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(ACCOUNT_NUMBER_FIELD_CLASS, ACCOUNT_NUMBER_FIELD_PROPERTY) + " and " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ORG_CODE_FIELD_CLASS, ORG_CODE_FIELD_PROPERTY) + " cannot be entered together.  You must enter a value for " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ACCOUNT_NUMBER_FIELD_CLASS, ACCOUNT_NUMBER_FIELD_PROPERTY) + " or " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ORG_CODE_FIELD_CLASS, ORG_CODE_FIELD_PROPERTY) + " but not both";
            errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
        }
        else {
            // may or may not be required but we have values to check
            List subAccounts = getSubAccounts(getFinCoaCd(), getAccountNbr(), getOrgCd(), getSubAccountNbr());
            if ((subAccounts == null) || (subAccounts.isEmpty())) {
                if (StringUtils.isNotBlank(getAccountNbr())) {
                    String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(CHART_CODE_FIELD_CLASS, CHART_CODE_FIELD_PROPERTY) + ", " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ACCOUNT_NUMBER_FIELD_CLASS, ACCOUNT_NUMBER_FIELD_PROPERTY) + ", and " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(SUB_ACCOUNT_NUMBER_FIELD_CLASS, SUB_ACCOUNT_NUMBER_FIELD_PROPERTY) + " combination is invalid";
                    errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
                }
                else {
                    String error = KualiWorkflowUtils.getBusinessObjectAttributeLabel(CHART_CODE_FIELD_CLASS, CHART_CODE_FIELD_PROPERTY) + ", " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(ORG_CODE_FIELD_CLASS, ORG_CODE_FIELD_PROPERTY) + ", and " + KualiWorkflowUtils.getBusinessObjectAttributeLabel(SUB_ACCOUNT_NUMBER_FIELD_CLASS, SUB_ACCOUNT_NUMBER_FIELD_PROPERTY) + " combination is invalid";
                    errors.add(new WorkflowServiceErrorImpl(error, "routetemplate.xmlattribute.error", error));
                }
            }
        }
        return errors;
    }

    private List getSubAccounts(String chartCode, String accountNumber, String orgCode, String subAccountNumer) {
        List subAccounts = new ArrayList();
        SubAccountService subAccountService = SpringContext.getBean(SubAccountService.class);
        if (StringUtils.isNotBlank(accountNumber)) {
            SubAccount subAccount = subAccountService.getByPrimaryIdWithCaching(chartCode, accountNumber, subAccountNumer);
            if (subAccount != null) {
                subAccounts.add(subAccount);
            }
        }
        else if (StringUtils.isNotBlank(orgCode)) {
            List testSubAccounts = subAccountService.getSubAccountsByReportsToOrganization(chartCode, orgCode, subAccountNumer);
            if ((testSubAccounts != null) && (!(testSubAccounts.isEmpty()))) {
                subAccounts.addAll(testSubAccounts);
            }
        }
        return subAccounts;
    }

    /**
     * Actual matching logic is handled in filterNonMatchingRules where the List of rules is narrowed down to those that should
     * fire.
     * 
     * @see #filterNonMatchingRules(RouteContext, List)
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#isMatch(java.lang.String, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return true;
    }

    /**
     * Filters the List of Rules by those that will match.
     */
    public List filterNonMatchingRules(RouteContext routeContext, List rules) {
        List filteredRules = new ArrayList();
        DocumentType documentType = routeContext.getDocument().getDocumentType();
        Set subAccountValues = populateFromDocContent(documentType.getName(), routeContext.getDocumentContent(), routeContext);
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            List ruleExtensions = rule.getRuleExtensions();
            setAccountNbr(LookupUtils.forceUppercase(SubAccount.class, KFSPropertyConstants.ACCOUNT_NUMBER, getRuleExtentionValue(ACCOUNT_NBR_KEY, ruleExtensions)));
            setOrgCd(LookupUtils.forceUppercase(SubAccount.class, KFSPropertyConstants.FIN_REPORT_ORGANIZATION_CODE, getRuleExtentionValue(ORG_CD_KEY, ruleExtensions)));
            String chartFieldName = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
            if (StringUtils.isBlank(getAccountNbr())) {
                chartFieldName = KFSPropertyConstants.FINANCIAL_REPORT_CHART_CODE;
            }
            setFinCoaCd(LookupUtils.forceUppercase(SubAccount.class, chartFieldName, getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions)));
            setSubAccountNbr(LookupUtils.forceUppercase(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getRuleExtentionValue(SUB_ACCOUNT_NBR_KEY, ruleExtensions)));
            if (ruleMatches(subAccountValues)) {
                filteredRules.add(rule);
            }
        }
        return filteredRules;
    }

    /**
     * Determines if the given Rule matches the document data by comparing the values.
     */
    protected boolean ruleMatches(Set subAccountValues) {
        for (Iterator iter = subAccountValues.iterator(); iter.hasNext();) {
            SubAccount subAccount = (SubAccount) iter.next();
            if (StringUtils.isNotBlank(getOrgCd())) {
                // check based on org
                if ((StringUtils.equals(subAccount.getFinancialReportChartCode(), getFinCoaCd())) && (StringUtils.equals(subAccount.getFinReportOrganizationCode(), getOrgCd())) && (StringUtils.equals(subAccount.getSubAccountNumber(), getSubAccountNbr()))) {
                    return true;
                }
            }
            else {
                // check based on account
                if ((StringUtils.equals(subAccount.getChartOfAccountsCode(), getFinCoaCd())) && (StringUtils.equals(subAccount.getAccountNumber(), getAccountNbr())) && (StringUtils.equals(subAccount.getSubAccountNumber(), getSubAccountNbr()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getRuleExtentionValue(String key, List ruleExtensions) {
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(this.getClass().getName())) {
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    if (value.getKey().equals(key)) {
                        return value.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * this method will take the document content, and populate a list of SubAccount objects from the document values
     * 
     * @param docContent
     * @return a list of SubAccount objects that are contained in the doc
     */
    protected Set populateFromDocContent(String docTypeName, DocumentContent docContent, RouteContext routeContext) {
        Set subAccountValues = null;
        if (routeContext.getParameters().containsKey(DOCUMENT_SUB_ACCOUNT_VALUES_KEY)) {
            subAccountValues = (Set) routeContext.getParameters().get(DOCUMENT_SUB_ACCOUNT_VALUES_KEY);
        }
        else {
            subAccountValues = new HashSet();
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            try {
                String chart = null;
                String account = null;
                String org = null;
                String subAccount = null;
                String reportMatchAnywhereExpressionPrefix = new StringBuffer(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).toString();
                boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    account = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(KFSPropertyConstants.ACCOUNT_NUMBER).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(KFSPropertyConstants.ORGANIZATION_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    subAccount = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(KFSPropertyConstants.SUB_ACCOUNT_NUMBER).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    String chartFieldName = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                    if (StringUtils.isBlank(account)) {
                        chartFieldName = KFSPropertyConstants.FINANCIAL_REPORT_CHART_CODE;
                    }
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(reportMatchAnywhereExpressionPrefix).append(KualiWorkflowUtils.XPATH_ELEMENT_SEPARATOR).append(chartFieldName).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    subAccountValues.addAll(attemptSubAccountRetrieval(chart, account, org, subAccount));
                }
                else {
                    String xpathExp = null;
                    if (KualiWorkflowUtils.isSourceLineOnly(docTypeName)) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docTypeName)).append("/" + KFSPropertyConstants.SUB_ACCOUNT).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else if (KualiWorkflowUtils.isTargetLineOnly(docTypeName)) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docTypeName)).append("/" + KFSPropertyConstants.SUB_ACCOUNT).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docTypeName)).append("/" + KFSPropertyConstants.SUB_ACCOUNT).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).append(" | ").append(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docTypeName)).append("/" + KFSPropertyConstants.SUB_ACCOUNT).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    NodeList nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node subAccountNode = nodes.item(i);
                        account = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.ACCOUNT_NUMBER, subAccountNode);
                        org = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.FIN_REPORT_ORGANIZATION_CODE, subAccountNode);
                        String chartFieldName = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                        if (StringUtils.isBlank(account)) {
                            chartFieldName = KFSPropertyConstants.FINANCIAL_REPORT_CHART_CODE;
                        }
                        chart = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + chartFieldName, subAccountNode);
                        subAccount = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNode);
                        subAccountValues.addAll(attemptSubAccountRetrieval(chart, account, org, subAccount));
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            routeContext.getParameters().put(DOCUMENT_SUB_ACCOUNT_VALUES_KEY, subAccountValues);
        }
        return subAccountValues;
    }

    private List<SubAccount> attemptSubAccountRetrieval(String chart, String account, String org, String subAccount) {
        List<SubAccount> subAccountValues = new ArrayList();
        if (StringUtils.isNotBlank(chart) && StringUtils.isNotBlank(subAccount) && (StringUtils.isNotBlank(account) || StringUtils.isNotBlank(org))) {
            List subAccounts = getSubAccounts(chart, account, org, subAccount);
            if ((subAccounts == null) || (subAccounts.isEmpty())) {
                throw new RuntimeException("Sub Account declared on the document cannot be found in the system, routing cannot continue.");
            }
            // possibly duplicate add, but this is safe in a HashSet
            for (Iterator iter = subAccounts.iterator(); iter.hasNext();) {
                SubAccount subAccountToAdd = (SubAccount) iter.next();
                subAccountValues.add(subAccountToAdd);
            }
        }
        return subAccountValues;
    }

    /**
     * Gets the accountNbr attribute.
     * 
     * @return Returns the accountNbr.
     */
    public String getAccountNbr() {
        return accountNbr;
    }

    /**
     * Sets the accountNbr attribute value.
     * 
     * @param accountNbr The accountNbr to set.
     */
    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    /**
     * Gets the finCoaCd attribute.
     * 
     * @return Returns the finCoaCd.
     */
    public String getFinCoaCd() {
        return finCoaCd;
    }

    /**
     * Sets the finCoaCd attribute value.
     * 
     * @param finCoaCd The finCoaCd to set.
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * Gets the orgCd attribute.
     * 
     * @return Returns the orgCd.
     */
    public String getOrgCd() {
        return orgCd;
    }

    /**
     * Sets the orgCd attribute value.
     * 
     * @param orgCd The orgCd to set.
     */
    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    /**
     * Gets the subAccountNbr attribute.
     * 
     * @return Returns the subAccountNbr.
     */
    public String getSubAccountNbr() {
        return subAccountNbr;
    }

    /**
     * Sets the subAccountNbr attribute value.
     * 
     * @param subAccountNbr The subAccountNbr to set.
     */
    public void setSubAccountNbr(String subAccountNbr) {
        this.subAccountNbr = subAccountNbr;
    }

    /**
     * Sets the routingDataRows attribute value.
     * 
     * @param routingDataRows The routingDataRows to set.
     */
    public void setRoutingDataRows(List routingDataRows) {
        this.routingDataRows = routingDataRows;
    }

    /**
     * Sets the ruleRows attribute value.
     * 
     * @param ruleRows The ruleRows to set.
     */
    public void setRuleRows(List ruleRows) {
        this.ruleRows = ruleRows;
    }

}
