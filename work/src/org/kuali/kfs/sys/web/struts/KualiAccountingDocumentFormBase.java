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
package org.kuali.kfs.sys.web.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.AccountingLineDecorator;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.format.SimpleBooleanFormatter;

/**
 * This class is the base action form for all financial documents.
 */
public class KualiAccountingDocumentFormBase extends FinancialSystemTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountingDocumentFormBase.class);
    private SourceAccountingLine newSourceLine;
    private TargetAccountingLine newTargetLine;

    private Map editableAccounts;
    private Map forcedLookupOptionalFields;

    // TODO: FormFile isn't Serializable, so mark these fields need as transient or create a Serializable subclass of FormFile
    protected FormFile sourceFile;
    protected FormFile targetFile;
    private boolean hideDetails = false;

    private List<AccountingLineDecorator> sourceLineDecorators;
    private List<AccountingLineDecorator> targetLineDecorators;

    private List baselineSourceAccountingLines;
    private List baselineTargetAccountingLines;

    /**
     * This constructor sets up empty instances for the dependent objects...
     */
    public KualiAccountingDocumentFormBase() {
        super();
        setFormatterType("sourceLineDecorator.revertible", SimpleBooleanFormatter.class);
        setFormatterType("targetLineDecorator.revertible", SimpleBooleanFormatter.class);

        // create an empty editableAccounts map, for safety's sake
        editableAccounts = new HashMap();
        forcedReadOnlyFields = new HashMap();
        forcedLookupOptionalFields = new HashMap();

        // initialize accountingLine lists
        baselineSourceAccountingLines = new ArrayList();
        baselineTargetAccountingLines = new ArrayList();

        // initialize accountingLine decoration lists
        sourceLineDecorators = new ArrayList<AccountingLineDecorator>();
        targetLineDecorators = new ArrayList<AccountingLineDecorator>();
    }


    /**
     * Overrides the parent to call super.populate and then to call the accounting lines populate method that is specific to loading
     * the two select lists on the page.
     * 
     * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        final String methodToCall = this.getMethodToCall();
        final Map parameterMap = request.getParameterMap();

        populateAccountingLinesForResponse(methodToCall, parameterMap);

        setDocTypeName(discoverDocumentTypeName());
    }
    
    /**
     * Populates the accounting lines which need to be updated to successfully complete a response to the request
     * @param methodToCall the method to call in the action to complete this request transaction
     * @param parameterMap the map of parameters which came in with the transaction
     */
    protected void populateAccountingLinesForResponse(String methodToCall, Map parameterMap) {
        populateSourceAccountingLine(getNewSourceLine(), KFSPropertyConstants.NEW_SOURCE_LINE, parameterMap);
        populateTargetAccountingLine(getNewTargetLine(), KFSPropertyConstants.NEW_TARGET_LINE, parameterMap);

        // don't call populateAccountingLines if you are copying or errorCorrecting a document,
        // since you want the accountingLines in the copy to be "identical" to those in the original
        if (!StringUtils.equals(methodToCall, KFSConstants.COPY_METHOD) && !StringUtils.equals(methodToCall, KFSConstants.ERRORCORRECT_METHOD)) {
            populateAccountingLines(parameterMap);
        }
    }

    /**
     * This method iterates over all of the source lines and all of the target lines in a transactional document, and calls
     * prepareAccountingLineForValidationAndPersistence on each one. This is called because a user could have updated already
     * existing accounting lines that had blank values in composite key fields.
     * 
     * @param parameterMap the map of parameters that were sent in with the request
     */
    protected void populateAccountingLines(Map parameterMap) {
        Iterator sourceLines = getFinancialDocument().getSourceAccountingLines().iterator();
        int count = 0;
        while (sourceLines.hasNext()) {
            SourceAccountingLine sourceLine = (SourceAccountingLine) sourceLines.next();
            populateSourceAccountingLine(sourceLine, KFSPropertyConstants.DOCUMENT+"."+KFSPropertyConstants.SOURCE_ACCOUNTING_LINE+"["+count+"]", parameterMap);
            count += 1;
        }

        Iterator targetLines = getFinancialDocument().getTargetAccountingLines().iterator();
        count = 0;
        while (targetLines.hasNext()) {
            TargetAccountingLine targetLine = (TargetAccountingLine) targetLines.next();
            populateTargetAccountingLine(targetLine, KFSPropertyConstants.DOCUMENT+"."+KFSPropertyConstants.TARGET_ACCOUNTING_LINE+"["+count+"]", parameterMap);
            count += 1;
        }
    }

    /**
     * Populates a source accounting line bo using values from the struts form. This is in place to make sure that all of the
     * composite key objects have the correct values in them. This should be overridden by children forms in the situation where
     * document level attributes need to be pushed down into the accounting lines.
     * 
     * @param sourceLine
     * @param accountingLinePropertyName the property path from the form to the accounting line
     * @param parameterMap the map of parameters that were sent in with the request
     */
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine, String accountingLinePropertyName, Map parameterMap) {
        populateAccountingLine(sourceLine, accountingLinePropertyName, parameterMap);
    }

    /**
     * Populates a target accounting line bo using values from the struts form. This is in place to make sure that all of the
     * composite key objects have the correct values in them. This should be overridden by children forms in the situation where
     * document level attributes need to be pushed down into the accounting lines.
     * 
     * @param targetLine
     * @param accountingLinePropertyName the property path from the form to the accounting line
     * @param parameterMap the map of parameters that were sent in with the request
     */
    public void populateTargetAccountingLine(TargetAccountingLine targetLine, String accountingLinePropertyName, Map parameterMap) {
        populateAccountingLine(targetLine, accountingLinePropertyName, parameterMap);
    }

    /**
     * Populates the dependent fields of objects contained within the given accountingLine
     * 
     * @param line
     * @param accountingLinePropertyName the property path from the form to the accounting line
     * @param parameterMap the map of parameters that were sent in with the request
     */
    @SuppressWarnings("deprecation")
    private void populateAccountingLine(AccountingLineBase line, String accountingLinePropertyName, Map parameterMap) {
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(line);

        line.setDocumentNumber(getDocument().getDocumentNumber());

        if (ObjectUtils.isNull(line.getAccount())) {
            line.setAccount(new Account());
        }
        line.getAccount().setChartOfAccountsCode(line.getChartOfAccountsCode());

        if (ObjectUtils.isNull(line.getObjectCode())) {
            line.setObjectCode(new ObjectCode());
        }
        line.getObjectCode().setUniversityFiscalYear(getFinancialDocument().getPostingYear());
        line.getObjectCode().setChartOfAccountsCode(line.getChartOfAccountsCode());

        if (ObjectUtils.isNull(line.getSubAccount())) {
            line.setSubAccount(new SubAccount());
        }
        line.getSubAccount().setChartOfAccountsCode(line.getChartOfAccountsCode());
        line.getSubAccount().setAccountNumber(line.getAccountNumber());

        if (ObjectUtils.isNull(line.getSubObjectCode())) {
            line.setSubObjectCode(new SubObjectCode());
        }
        line.getSubObjectCode().setChartOfAccountsCode(line.getChartOfAccountsCode());
        line.getSubObjectCode().setAccountNumber(line.getAccountNumber());
        line.getSubObjectCode().setFinancialObjectCode(line.getFinancialObjectCode());
        line.getSubObjectCode().setUniversityFiscalYear(getFinancialDocument().getPostingYear());
        
        repopulateOverrides(line, accountingLinePropertyName, parameterMap);

        AccountingLineOverride.populateFromInput(line);
    }
    
    /**
     * This repopulates the override values from the request
     * @param line the line to repopulate override values for
     * @param accountingLinePropertyName the property path from the form to the accounting line
     * @param parameterMap the map of parameters that were sent in with the request
     */
    protected void repopulateOverrides(AccountingLine line, String accountingLinePropertyName, Map parameterMap) {
        AccountingLineOverride.determineNeededOverrides(line);
        if (line.getAccountExpiredOverrideNeeded()) {
            if (parameterMap.containsKey(accountingLinePropertyName+".accountingExpiredOverride.present")) {
                line.setAccountExpiredOverride(parameterMap.containsKey(accountingLinePropertyName+".accountExpiredOverride"));
            }
        } else {
            line.setAccountExpiredOverride(false);
        }
        if (line.isObjectBudgetOverrideNeeded()) {
            if (parameterMap.containsKey(accountingLinePropertyName+".objectBudgetOverride.present")) {
                line.setObjectBudgetOverride(parameterMap.containsKey(accountingLinePropertyName+".objectBudgetOverride"));
            }
        } else {
            line.setObjectBudgetOverride(false);
        }
    }

    /**
     * This method retrieves an instance of the form.
     * 
     * @return
     */
    public AccountingDocument getFinancialDocument() {
        return (AccountingDocument) getDocument();
    }

    /**
     * @return Returns the newTargetLine.
     */
    public TargetAccountingLine getNewTargetLine() {
        if (newTargetLine == null) {
            newTargetLine = createNewTargetAccountingLine(getFinancialDocument());
        }
        return newTargetLine;
    }

    /**
     * @param newExpenseLine The newTargetLine to set.
     */
    public void setNewTargetLine(TargetAccountingLine newExpenseLine) {
        this.newTargetLine = newExpenseLine;
    }

    /**
     * @return Returns the newSourceLine.
     */
    public SourceAccountingLine getNewSourceLine() {
        if (newSourceLine == null) {
            newSourceLine = createNewSourceAccountingLine(getFinancialDocument());
        }
        return newSourceLine;
    }

    /**
     * @param newIncomeLine The newSourceLine to set.
     */
    public void setNewSourceLine(SourceAccountingLine newIncomeLine) {
        this.newSourceLine = newIncomeLine;
    }

    /**
     * @return Returns the sourceFile.
     */
    public FormFile getSourceFile() {
        return sourceFile;
    }

    /**
     * @param sourceFile The sourceFile to set.
     */
    public void setSourceFile(FormFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * @return Returns the targetFile.
     */
    public FormFile getTargetFile() {
        return targetFile;
    }

    /**
     * @param targetFile The targetFile to set.
     */
    public void setTargetFile(FormFile targetFile) {
        this.targetFile = targetFile;
    }


    /**
     * @return current Map of editableAccounts
     */
    public Map getEditableAccounts() {
        return editableAccounts;
    }

    /**
     * @param editableAccounts the account Map to set
     */
    public void setEditableAccounts(Map editableAccounts) {
        this.editableAccounts = editableAccounts;
    }

    /**
     * @return hideDetails attribute
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * @return hideDetails attribute
     * @see #isHideDetails()
     */
    public boolean getHideDetails() {
        return isHideDetails();
    }

    /**
     * @param hideDetails
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }


    /**
     * @return current List of baseline SourceAccountingLines for use in update-event generation
     */
    public List getBaselineSourceAccountingLines() {
        return baselineSourceAccountingLines;
    }

    /**
     * Sets the current List of baseline SourceAccountingLines to the given List
     * 
     * @param baselineSourceAccountingLines
     */
    public void setBaselineSourceAccountingLines(List baselineSourceAccountingLines) {
        this.baselineSourceAccountingLines = baselineSourceAccountingLines;
    }

    /**
     * @param index
     * @return true if a baselineSourceAccountingLine with the given index exists
     */
    public boolean hasBaselineSourceAccountingLine(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineSourceAccountingLines.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty SourceAccountingLines as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return baseline SourceAccountingLine at the given index
     */
    public SourceAccountingLine getBaselineSourceAccountingLine(int index) {
        try {
            while (baselineSourceAccountingLines.size() <= index) {
                baselineSourceAccountingLines.add(getFinancialDocument().getSourceAccountingLineClass().newInstance());
            }
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get new source line instance for document" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get new source line instance for document" + e.getMessage());
        }

        return (SourceAccountingLine) baselineSourceAccountingLines.get(index);
    }


    /**
     * @return current List of baseline TargetAccountingLines for use in update-event generation
     */
    public List getBaselineTargetAccountingLines() {
        return baselineTargetAccountingLines;
    }

    /**
     * Sets the current List of baseline TargetAccountingLines to the given List
     * 
     * @param baselineTargetAccountingLines
     */
    public void setBaselineTargetAccountingLines(List baselineTargetAccountingLines) {
        this.baselineTargetAccountingLines = baselineTargetAccountingLines;
    }


    /**
     * @param index
     * @return true if a baselineTargetAccountingLine with the given index exists
     */
    public boolean hasBaselineTargetAccountingLine(int index) {
        boolean has = false;

        if ((index >= 0) && (index <= baselineTargetAccountingLines.size())) {
            has = true;
        }

        return has;
    }

    /**
     * Implementation creates empty TargetAccountingLines as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return baseline TargetAccountingLine at the given index
     */
    public TargetAccountingLine getBaselineTargetAccountingLine(int index) {
        try {
            while (baselineTargetAccountingLines.size() <= index) {
                baselineTargetAccountingLines.add(getFinancialDocument().getTargetAccountingLineClass().newInstance());
            }
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get new target line instance for document" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get new target line instance for document" + e.getMessage());
        }

        return (TargetAccountingLine) baselineTargetAccountingLines.get(index);
    }


    /**
     * @return current List of SourceAccountingLine decorations
     */
    public List<AccountingLineDecorator> getSourceLineDecorators() {
        return sourceLineDecorators;
    }

    /**
     * @param minSize
     * @return current List of SourceAccountingLine decorations, expanded to have at least minSize elements
     */
    public List<AccountingLineDecorator> getSourceLineDecorators(int minSize) {
        extendSourceLineDecorators(minSize);

        return sourceLineDecorators;
    }

    /**
     * Adds default AccountingLineDecorators to sourceAccountingLineDecorators until it contains at least minSize elements
     * 
     * @param minSize
     */
    private void extendSourceLineDecorators(int minSize) {
        while (sourceLineDecorators.size() < minSize) {
            sourceLineDecorators.add(new AccountingLineDecorator());
        }
    }

    /**
     * Sets the current List of SourceAccountingLine decorators
     * 
     * @param sourceLineDecorators
     */
    public void setSourceLineDecorators(List<AccountingLineDecorator> sourceLineDecorators) {
        this.sourceLineDecorators = sourceLineDecorators;
    }

    /**
     * Implementation creates empty AccountingLineDecorators as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return AccountingLineDecorators for sourceLine at the given index
     */
    public AccountingLineDecorator getSourceLineDecorator(int index) {
        extendSourceLineDecorators(index + 1);

        return sourceLineDecorators.get(index);
    }


    /**
     * @return current List of TargetAccountingLine decorators
     */
    public List<AccountingLineDecorator> getTargetLineDecorators() {
        return targetLineDecorators;
    }

    /**
     * @param minSize
     * @return current List of TargetAccountingLine decorators, expanded to have at least minSize elements
     */
    public List<AccountingLineDecorator> getTargetLineDecorators(int minSize) {
        extendTargetLineDecorators(minSize);

        return targetLineDecorators;
    }

    /**
     * Adds default AccountingLineDecorators to targetAccountingLineDecorators until it contains at least minSize elements
     * 
     * @param minSize
     */
    private void extendTargetLineDecorators(int minSize) {
        while (targetLineDecorators.size() < minSize) {
            targetLineDecorators.add(new AccountingLineDecorator());
        }
    }

    /**
     * Sets the current List of TargetAccountingLine decorators
     * 
     * @param targetLineDecorators
     */
    public void setTargetLineDecorators(List<AccountingLineDecorator> targetLineDecorators) {
        this.targetLineDecorators = targetLineDecorators;
    }

    /**
     * Implementation creates empty AccountingLineDecorators as a side-effect, so that Struts' efforts to set fields of lines which
     * haven't been created will succeed rather than causing a NullPointerException.
     * 
     * @param index
     * @return AccountingLineDecorator for targetLine at the given index
     */
    public AccountingLineDecorator getTargetLineDecorator(int index) {
        extendTargetLineDecorators(index + 1);

        return targetLineDecorators.get(index);
    }


    /**
     * Resets the source accounting line decorators to new and ensures that there are the given number. These decorators take very
     * little memory, there are few of them on the page, and they are rarely reset, so this method does it the simple way.
     * 
     * @param size
     */
    public void resetSourceLineDecorators(int size) {
        sourceLineDecorators.clear();
        extendSourceLineDecorators(size);
    }

    /**
     * Resets the target accounting line decorators to new and ensures that there are the given number. These decorators take very
     * little memory, there are few of them on the page, and they are rarely reset, so this method does it the simple way.
     * 
     * @param size
     */
    public void resetTargetLineDecorators(int size) {
        targetLineDecorators.clear();
        extendTargetLineDecorators(size);
    }

    /**
     * Retrieves the source accounting lines total in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedSourceTotal() {
        return (String) new CurrencyFormatter().format(getFinancialDocument().getSourceTotal());
    }

    /**
     * Retrieves the source accounting lines total in a currency format with commas.
     * 
     * @return String
     */
    public String getCurrencyFormattedTargetTotal() {
        return (String) new CurrencyFormatter().format(getFinancialDocument().getTargetTotal());
    }

    /**
     * @return String
     */
    public String getAccountingLineImportInstructionsUrl() {
        // FIXME: help URLs are all being removed
        return "";//SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY) + SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, KFSConstants.FinancialApcParms.ACCOUNTING_LINE_IMPORT_HELP);
    }

    /**
     * @param financialDocument
     * @return a new source accounting line for the document
     */
    protected SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        try {
            return (SourceAccountingLine) financialDocument.getSourceAccountingLineClass().newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a new source accounting line", e);
        }
    }

    /**
     * @param financialDocument
     * @return a new target accounting line for the documet
     */
    protected TargetAccountingLine createNewTargetAccountingLine(AccountingDocument financialDocument) {
        if (financialDocument == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        try {
            return (TargetAccountingLine) financialDocument.getTargetAccountingLineClass().newInstance();
        }
        catch (Exception e) {
            throw new InfrastructureException("unable to create a new target accounting line", e);
        }
    }

    /**
     * This method finds its appropriate document authorizer and uses that to reset the map of editable accounts, based on the
     * current accounting lines.
     */
    public void refreshEditableAccounts() {
        AccountingDocumentAuthorizer authorizer = (AccountingDocumentAuthorizer) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(this.getDocument());
        this.setEditableAccounts(authorizer.getEditableAccounts(glomBaselineAccountingLines(), GlobalVariables.getUserSession().getPerson()));
    }

    /**
     * This method returns a list made up of accounting line from all baseline accounting line sources.
     * 
     * @return a list of accounting lines, made up of all baseline source and baseline target lines.
     */
    private List<AccountingLine> glomBaselineAccountingLines() {
        List<AccountingLine> lines = new ArrayList<AccountingLine>();
        lines.addAll(harvestAccountingLines(this.getBaselineSourceAccountingLines()));
        lines.addAll(harvestAccountingLines(this.getBaselineTargetAccountingLines()));
        return lines;
    }

    /**
     * This method takes a generic list, hopefully with some AccountingLine objects in it, and returns a list of AccountingLine
     * objects, because Java generics are just so wonderful.
     * 
     * @param lines a list of objects
     * @return a list of the accounting lines that were in the lines parameter
     */
    private List<AccountingLine> harvestAccountingLines(List lines) {
        List<AccountingLine> accountingLines = new ArrayList<AccountingLine>();
        for (Object o : lines) {
            if (o instanceof AccountingLine) {
                accountingLines.add((AccountingLine) o);
            }
        }
        return accountingLines;
    }

    /**
     * A <code>{@link Map}</code> of names of optional accounting line fields that require a quickfinder.
     * 
     * @return a Map of fields
     */
    public void setForcedLookupOptionalFields(Map fieldMap) {
        forcedLookupOptionalFields = fieldMap;
    }

    /**
     * A <code>{@link Map}</code> of names of optional accounting line fields that require a quickfinder.
     * 
     * @return a Map of fields
     */
    public Map getForcedLookupOptionalFields() {
        return forcedLookupOptionalFields;
    }

    /**
     * Adds the accounting line file size to the list of max file sizes.
     * 
     * @see org.kuali.rice.kns.web.struts.pojo.PojoFormBase#customInitMaxUploadSizes()
     */
    @Override
    protected void customInitMaxUploadSizes() {
        super.customInitMaxUploadSizes();
        addMaxUploadSize(SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, KFSConstants.ACCOUNTING_LINE_IMPORT_MAX_FILE_SIZE_PARM_NM));
    }
}
