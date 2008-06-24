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
package org.kuali.kfs.module.ec.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.module.ec.EffortConstants.SystemParameters;
import org.kuali.kfs.module.ec.batch.EffortCertificationCreateStep;
import org.kuali.kfs.module.ec.batch.EffortCertificationExtractStep;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;

/**
 * A convenient utility that can delegate the calling client to retrieve system parameters of effor certification module.
 */
public class EffortCertificationParameterFinder {
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    /**
     * get the run indicator setup in system paremters
     * 
     * @return the run indicator setup in system paremters
     */
    public static boolean getRunIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationExtractStep.class, SystemParameters.RUN_IND);
    }

    /**
     * get the federal agency type codes setup in system parameters
     * 
     * @return the federal agency type codes setup in system parameters
     */
    public static List<String> getFederalAgencyTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.FEDERAL_AGENCY_TYPE_CODE);
    }

    /**
     * get the fedeal only balance indicatior
     * 
     * @return the fedeal only balance indicatior
     */
    public static boolean getFederalOnlyBalanceIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationExtractStep.class, SystemParameters.FEDERAL_ONLY_BALANCE_IND);
    }

    /**
     * get the fedeal only balance indicatior
     * 
     * @return the fedeal only balance indicatior
     */
    public static List<String> getFederalOnlyBalanceIndicatorAsString() {
        List<String> indicatorList = new ArrayList<String>();
        indicatorList.add(Boolean.toString(getFederalOnlyBalanceIndicator()));
        return indicatorList;
    }

    /**
     * get the account type codes setup in system parameters
     * 
     * @return the account type codes setup in system parameters
     */
    public static List<String> getAccountTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT);
    }

    /**
     * get the report fiscal year setup in system paremters for extract process
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getExtractReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValue(EffortCertificationExtractStep.class, SystemParameters.RUN_FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters for extract process
     * 
     * @return the report number setup in system paremters
     */
    public static String getExtractReportNumber() {
        return parameterService.getParameterValue(EffortCertificationExtractStep.class, SystemParameters.RUN_REPORT_NUMBER);
    }

    /**
     * get the cost share sub account type code setup in system paremters
     * 
     * @return the cost share sub account type code setup in system paremters
     */
    public static List<String> getCostShareSubAccountTypeCode() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE);
    }

    /**
     * get the expense sub account type code setup in system paremters
     * 
     * @return the expense sub account type code setup in system paremters
     */
    public static List<String> getExpenseSubAccountTypeCode() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE);
    }

    /**
     * get the report fiscal year setup in system paremters for create process
     * 
     * @return the report fiscal year setup in system paremters
     */
    public static Integer getCreateReportFiscalYear() {
        return Integer.valueOf(parameterService.getParameterValue(EffortCertificationCreateStep.class, SystemParameters.CREATE_FISCAL_YEAR));
    }

    /**
     * get the report number setup in system paremters for create process
     * 
     * @return the report number setup in system paremters
     */
    public static String getCreateReportNumber() {
        return parameterService.getParameterValue(EffortCertificationCreateStep.class, SystemParameters.CREATE_REPORT_NUMBER);
    }

    /**
     * get the organization routing editable indicator setup in system paremters
     * 
     * @return the organization routing editable indicator setup in system paremters
     */
    public static boolean getOrganizationRoutingEditableIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationDocument.class, SystemParameters.ORGANIZATION_ROUTING_EDITABLE_IND);
    }

    /**
     * get the award routing editable indicator setup in system paremters
     * 
     * @return the award routing editable indicator setup in system paremters
     */
    public static boolean getAwardRoutingEditableIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationDocument.class, SystemParameters.AWARD_ROUTING_EDITABLE_IND);
    }

    /**
     * get the administrator routing editable indicator setup in system paremters
     * 
     * @return the administrator routing editable indicator setup in system paremters
     */
    public static boolean getAdministrationRoutingEditableIndicator() {
        return parameterService.getIndicatorParameter(EffortCertificationDocument.class, SystemParameters.ADMINISTRATOR_ROUTING_EDITABLE_IND);
    }
}
