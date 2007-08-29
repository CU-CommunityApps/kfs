<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/DocumentTypeService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="bcafAttributes"
	value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="positionAttributes"
	value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="intincAttributes"
	value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" />
<c:set var="bcsfAttributes"
	value="${DataDictionary['BudgetConstructionCalculatedSalaryFoundationTracker'].attributes}" />

<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<%--
                attributeEntry="${intincAttributes.emplid}"
                attributeEntry="${intincAttributes.personName}"
--%>

<kul:tabTop tabTitle="Salary Setting by Incumbent" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_INCUMBENT_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <bc:subheadingWithDetailToggleRow
          columnCount="12"
          subheading="Incumbent" />
        <tr>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.emplid"
                literalLabel="<span class=\"nowrap\">Emplid:</span>"
                horizontal="true" colspan="3" >
              <html:hidden property="returnAnchor" />
              <html:hidden property="returnFormKey" />
              <html:hidden property="universityFiscalYear" />
              <html:hidden property="chartOfAccountsCode" />
              <html:hidden property="accountNumber" />
              <html:hidden property="subAccountNumber" />
              <html:hidden property="financialObjectCode" />
              <html:hidden property="financialSubObjectCode" />
              <html:hidden property="emplid" />
              <html:hidden property="returnFormKey" />
            </kul:htmlAttributeHeaderCell>
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent"
                field="emplid"
                attributes="${intincAttributes}" inquiry="true"
                boClassSimpleName="BudgetConstructionIntendedIncumbent"
                boPackageName="org.kuali.module.budget.bo"
                readOnly="true"
                displayHidden="false"
                accountingLineValuesMap="${KualiForm.budgetConstructionIntendedIncumbent.valuesMap}" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.personName"
                literalLabel="<span class=\"nowrap\">Name:</span>"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionIntendedIncumbent.personName"
                field="personName"
                attributes="${intincAttributes}"
                colSpan="4"
                readOnly="true"
                displayHidden="false" />
            <kul:htmlAttributeHeaderCell
                attributeEntry="${intincAttributes.iuClassificationLevel}"
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.iuClassificationLevel"
                horizontal="true" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionIntendedIncumbent.iuClassificationLevel"
                field="iuClassificationLevel"
                attributes="${intincAttributes}"
                colSpan="2"
                readOnly="true"
                displayHidden="false" />
        </tr>
        <tr>
            <td colspan="12" class="subhead">
            <span class="subhead-left">Funding</span>
            </td>
        </tr>

        <%-- Add line header for newBCAFLine--%>
        <c:if test="${!readOnly}">
                
        <tr>
            <kul:htmlAttributeHeaderCell colspan="2" >
            </kul:htmlAttributeHeaderCell>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.chartOfAccountCode"
                literalLabel="<span class=\"nowrap\">Cht</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.accountNumber"
                literalLabel="<span class=\"nowrap\">Acct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.subAccountNumber"
                literalLabel="<span class=\"nowrap\">SAcct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.financialObjectCode"
                literalLabel="<span class=\"nowrap\">Obj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.financialSubObjectCode"
                literalLabel="<span class=\"nowrap\">SObj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.positionNumber"
                literalLabel="<span class=\"nowrap\">Pos.</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.budgetConstructionPosition.positionDescription"
                literalLabel="<span class=\"nowrap\">Descr</span>"
                colspan="2" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.newBCAFLine.budgetConstructionPosition.positionSalaryPlanDefault"
                literalLabel="<span class=\"nowrap\">SPln</span>" />
<%-- TODO add administrative post table ref to BCAF --%>
            <th>
                AdmPst
            </th>
        </tr>

        <%-- add line data key line --%>
        <tr>
           <kul:htmlAttributeHeaderCell
               scope="row" rowspan="1" colspan="2"
               literalLabel="Add:" >
<%-- TODO add the others --%>
                <html:hidden property="newBCAFLine.universityFiscalYear" />
                <html:hidden property="newBCAFLine.emplid" />
                <html:hidden property="newBCAFLine.appointmentFundingDeleteIndicator" />
                <html:hidden property="newBCAFLine.versionNumber" />
                <html:hidden property="newBCAFLine.financialObject.financialObjectTypeCode"/>
                <html:hidden property="newBCAFLine.financialObject.financialObjectType.name"/>
           </kul:htmlAttributeHeaderCell>

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="chartOfAccountsCode" detailFunction="loadChartInfo"
               detailField="chartOfAccounts.finChartOfAccountDescription"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="Chart"
               readOnly="${readOnly}"
               displayHidden="false"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               anchor="salarynewLineLineAnchor" />

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="accountNumber" detailFunction="loadAccountInfo"
               detailField="account.accountName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="Account"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               anchor="salarynewLineLineAccountAnchor" />
           
           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="subAccountNumber" detailFunction="loadSubAccountInfo"
               detailField="subAccount.subAccountName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="SubAccount"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               anchor="salarynewLineLineSubAccountAnchor" />

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="financialObjectCode" detailFunction="loadObjectInfo"
               detailFunctionExtraParam="'${KualiForm.newBCAFLine.universityFiscalYear}', 'newBCAFLine.financialObject.financialObjectType.name', 'newBCAFLine.financialObject.financialObjectTypeCode',"
               detailField="financialObject.financialObjectCodeShortName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="ObjectCode"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${KualiForm.newBCAFLine.universityFiscalYear}"
               anchor="salarynewLineLineObjectCodeAnchor" />

           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
               detailFunctionExtraParam="'${KualiForm.newBCAFLine.universityFiscalYear}', "
               detailField="financialSubObject.financialSubObjectCdshortNm"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="SubObjCd"
               readOnly="${readOnly}"
               displayHidden="false"
               lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${KualiForm.newBCAFLine.universityFiscalYear}"
               anchor="salarynewLineLineSubObjectCodeAnchor" />

<%-- TODO need javascript to implement AJAX like call to refresh positionDescription --%>
           <bc:pbglLineDataCell dataCellCssClass="infoline"
               accountingLine="newBCAFLine"
               field="positionNumber"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="BudgetConstructionPosition"
               boPackageName="org.kuali.module.budget.bo"
               readOnly="${readOnly}"
               lookupOrInquiryKeys="universityFiscalYear"
               displayHidden="false"
               accountingLineValuesMap="${KualiForm.newBCAFLine.valuesMap}"
               anchor="salarynewLineLinePositionNumberAnchor" />

<%-- TODO format name to break into separate lines at comma to save horizontal screen realestate --%>
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                cellProperty="newBCAFLine.budgetConstructionPosition.positionDescription"
                field="positionDescription"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false"
                colSpan="2" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                cellProperty="newBCAFLine.budgetConstructionPosition.positionSalaryPlanDefault"
                field="positionSalaryPlanDefault"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />

<%-- TODO add adminstrative post ref to BCAF and here --%>
            <td class="infoline">&nbsp;</td>
        </tr>

        <%-- add line amount line header --%>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="5" align="left" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Request" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Leaves Req.CSF" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="Tot.Int." scope="col" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="5" scope="col" />

            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentRequestedAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <fmt:formatNumber value="${KualiForm.newBCAFLine.appointmentRequestedFteQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentRequestedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedFteQuantity"
                fieldAlign="right"
                readOnly="true"
                formattedNumberValue="${formattedNumber}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentRequestedCsfAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <fmt:formatNumber value="${KualiForm.newBCAFLine.appointmentRequestedCsfFteQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentRequestedCsfFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfFteQuantity"
                fieldAlign="right"
                readOnly="true"
                formattedNumberValue="${formattedNumber}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentTotalIntendedAmount"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                colSpan="2" rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentTotalIntendedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="4" scope="col" />

            <td class="infoline">&nbsp;</td>

<%-- TODO figure out handling of reason annotation functionality, like BA monthly? or BC monthly? or? --%>

            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentFundingMonth"
                attributes="${bcafAttributes}"
                field="appointmentFundingMonth"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentRequestedTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedTimePercent"
                fieldTrailerValue="<small>%</small>"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
<%-- TODO need ajax javascript to handle detail field changes --%>
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                field="appointmentFundingDurationCode"
                detailField="budgetConstructionDuration.appointmentDurationDescription"
                attributes="${bcafAttributes}" inquiry="true"
                boClassSimpleName="BudgetConstructionDuration"
                lookupUnkeyedFieldConversions="appointmentDurationCode:newBCAFLine.appointmentFundingDurationCode,"
                readOnly="${readOnly}"
                displayHidden="false"
                accountingLineValuesMap="${newBCAFLine.valuesMap}" />
            <bc:pbglLineDataCell dataCellCssClass="infoline"
                accountingLine="newBCAFLine"
                cellProperty="newBCAFLine.appointmentRequestedCsfTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfTimePercent"
                fieldTrailerValue="<small>%</small>"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <kul:htmlAttributeHeaderCell
                scope="row" rowspan="1" colspan="2" 
                literalLabel="Actions:"
                horizontal="true" />
            <td class="infoline" nowrap>
                <div align="center"><span class=nobord">
                <html:image property="methodToCall.insertBCAFLine.anchorsalarynewLineLineAnchor" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="Add a Salary Setting Line" alt="Add a Salary Setting Line" styleClass="tinybutton"/>
                </span></div>
            </td>

<%--
            <td colspan="3">&nbsp;</td>
--%>
        </tr>
        </c:if>

        <%-- normal datalines --%>
        <c:forEach items="${KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
        <tr>
            <kul:htmlAttributeHeaderCell />
            <kul:htmlAttributeHeaderCell literalLabel="<span class=\"nowrap\">Del&nbsp;</span>" scope="row">
            </kul:htmlAttributeHeaderCell>
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.chartOfAccountCode"
                literalLabel="<span class=\"nowrap\">Cht</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.accountNumber"
                literalLabel="<span class=\"nowrap\">Acct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.subAccountNumber"
                literalLabel="<span class=\"nowrap\">SAcct</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.financialObjectCode"
                literalLabel="<span class=\"nowrap\">Obj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.financialSubObjectCode"
                literalLabel="<span class=\"nowrap\">SObj</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.positionNumber"
                literalLabel="<span class=\"nowrap\">Pos.</span>" />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.budgetConstructionPosition.positionDescription"
                literalLabel="<span class=\"nowrap\">Descr</span>"
                colspan="2"  />
            <kul:htmlAttributeHeaderCell
                labelFor="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding.budgetConstructionIntendedPosition.positionSalaryPlanDefault"
                literalLabel="<span class=\"nowrap\">SPln</span>"  />
<%-- TODO add administrative post table ref to BCAF --%>
            <th>
                AdmPst
            </th>
              
        </tr>
        <tr>
           <kul:htmlAttributeHeaderCell scope="row" rowspan="1">
<%-- TODO add the others --%>
                <html:hidden property="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear" />
                <html:hidden property="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].emplid" />
                <html:hidden property="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].versionNumber" />
           </kul:htmlAttributeHeaderCell>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingDeleteIndicator"
               attributes="${bcafAttributes}"
               field="appointmentFundingDeleteIndicator"
               fieldAlign="center"
               readOnly="${readOnly}"
               rowSpan="1 "dataFieldCssClass="nobord"
               anchor="salaryexistingLineLineAnchor${status.index}" />
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="chartOfAccountsCode"
               detailField="chartOfAccounts.finChartOfAccountDescription"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="Chart"
               readOnly="true"
               displayHidden="false"
               accountingLineValuesMap="${item.valuesMap}" />
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="accountNumber" detailFunction="loadAccountInfo"
               detailField="account.accountName"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="Account"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode"
               accountingLineValuesMap="${item.valuesMap}" />
           
           <c:set var="doAccountLookupOrInquiry" value="false"/>
           <c:if test="${item.subAccountNumber ne Constants.DASHES_SUB_ACCOUNT_NUMBER}">
               <c:set var="doAccountLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="subAccountNumber" detailFunction="loadSubAccountInfo"
               detailField="subAccount.subAccountName"
               attributes="${bcafAttributes}" inquiry="${doAccountLookupOrInquiry}"
               boClassSimpleName="SubAccount"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
               accountingLineValuesMap="${item.valuesMap}" />

           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="financialObjectCode" detailFunction="loadObjectInfo"
               detailFunctionExtraParam="KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear', "
               detailField="financialObject.financialObjectCodeShortName"
               attributes="${bcafAttributes}" lookup="true" inquiry="true"
               boClassSimpleName="ObjectCode"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
               accountingLineValuesMap="${item.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${item.universityFiscalYear}" />

           <c:set var="doLookupOrInquiry" value="false"/>
           <c:if test="${item.financialSubObjectCode ne Constants.DASHES_SUB_OBJECT_CODE}">
               <c:set var="doLookupOrInquiry" value="true"/>
           </c:if>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
               detailFunctionExtraParam="'KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear', "
               detailField="financialSubObject.financialSubObjectCdshortNm"
               attributes="${bcafAttributes}" inquiry="${doLookupOrInquiry}"
               boClassSimpleName="SubObjCd"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
               accountingLineValuesMap="${item.valuesMap}"
               inquiryExtraKeyValues="universityFiscalYear=${item.universityFiscalYear}" />

           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="positionNumber"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="BudgetConstructionPosition"
               boPackageName="org.kuali.module.budget.bo"
               readOnly="true"
               displayHidden="false"
               lookupOrInquiryKeys="universityFiscalYear"
               accountingLineValuesMap="${item.valuesMap}" />

<%-- TODO format description to break into separate lines at comma to save horizontal screen realestate --%>
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionPosition.positionDescription"
                field="positionDescription"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false"
                colSpan="2" />

            <bc:pbglLineDataCell dataCellCssClass="datacell"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionPosition.positionSalaryPlanDefault"
                field="positionSalaryPlanDefault"
                attributes="${positionAttributes}"
                readOnly="true"
                displayHidden="false" />

<%-- TODO add adminstrative post ref to BCAF and here --%>
            <td>&nbsp;</td>
        </tr>
                                        
        <tr>
            <kul:htmlAttributeHeaderCell colspan="2" align="left" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="CSF" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Request" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="2" align="left" literalLabel="Leaves Req.CSF" scope="col" />
            <kul:htmlAttributeHeaderCell colspan="3" align="left" literalLabel="Tot.Int." scope="col" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="2" scope="col" />

            <c:choose>
            <c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfAmount"
                    field="csfAmount"
                    fieldAlign="right"
                    attributes="${bcsfAttributes}"
                    readOnly="true"
                    colSpan="2" dataFieldCssClass="amount"
                    displayHidden="false" />
                <fmt:formatNumber value="${KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[status.index].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                    field="csfFullTimeEmploymentQuantity"
                    fieldAlign="right"
                    attributes="${bcsfAttributes}"
                    readOnly="true"
                    formattedNumberValue="${formattedNumber}"
                    dataFieldCssClass="amount"
                    displayHidden="false" />
            </c:when>
            <c:otherwise>
                <td colspan="2">&nbsp;</td>
                <td>&nbsp;</td>
            </c:otherwise>
            </c:choose>

            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <fmt:formatNumber value="${KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[status.index].appointmentRequestedFteQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedFteQuantity"
                fieldAlign="right"
                readOnly="true"
                formattedNumberValue="${formattedNumber}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedCsfAmount"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <fmt:formatNumber value="${KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[status.index].appointmentRequestedCsfFteQuantity}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedCsfFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfFteQuantity"
                fieldAlign="right"
                readOnly="true"
                formattedNumberValue="${formattedNumber}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentTotalIntendedAmount"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedAmount"
                fieldAlign="right"
                readOnly="${readOnly}"
                colSpan="2" rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentTotalIntendedFteQuantity"
                attributes="${bcafAttributes}"
                field="appointmentTotalIntendedFteQuantity"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell colspan="4" scope="col" />

            <c:choose>
            <c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
                <fmt:formatNumber value="${KualiForm.budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[status.index].bcnCalculatedSalaryFoundationTracker[0].csfTimePercent}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
                <bc:pbglLineDataCell dataCellCssClass="datacell"
                    cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity"
                    field="csfTimePercent"
                    fieldAlign="right"
                    attributes="${bcsfAttributes}"
                    readOnly="true"
                    fieldTrailerValue="<small>%</small>"
                    formattedNumberValue="${formattedNumber}"
                    dataFieldCssClass="amount"
                    displayHidden="false" />
            </c:when>
            <c:otherwise>
                <td>&nbsp;</td>
            </c:otherwise>
            </c:choose>

<%-- TODO figure out handling of reason annotation functionality, like BA monthly? or BC monthly? or? --%>

            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingMonth"
                attributes="${bcafAttributes}"
                field="appointmentFundingMonth"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedTimePercent"
                fieldTrailerValue="<small>%</small>"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />
<%-- TODO need ajax javascript to handle detail field changes --%>
           <bc:pbglLineDataCell dataCellCssClass="datacell"
               accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
               field="appointmentFundingDurationCode"
               detailField="budgetConstructionDuration.appointmentDurationDescription"
               attributes="${bcafAttributes}" inquiry="true"
               boClassSimpleName="BudgetConstructionDuration"
               lookupUnkeyedFieldConversions="appointmentDurationCode:budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingDurationCode,"
               readOnly="${readOnly}"
               displayHidden="false"
               accountingLineValuesMap="${item.valuesMap}" />
            <bc:pbglLineDataCell dataCellCssClass="datacell"
                accountingLine="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}]"
                cellProperty="budgetConstructionIntendedIncumbent.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedCsfTimePercent"
                attributes="${bcafAttributes}"
                field="appointmentRequestedCsfTimePercent"
                fieldTrailerValue="<small>%</small>"
                fieldAlign="right"
                readOnly="${readOnly}"
                rowSpan="1" dataFieldCssClass="amount" />

            <kul:htmlAttributeHeaderCell
                scope="row" rowspan="1" colspan="2" 
                literalLabel="Actions:"
                horizontal="true" />
            <td class="datacell" nowrap>
                <div align="center"><span class=nobord">
                  <html:image property="methodToCall.performReasonAnnotation.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-reason.gif" title="Reason Annotation for Salary Setting Line ${status.index}" alt="Reason Annotation for Salary Setting Line ${status.index}" styleClass="tinybutton" />
                  <br>
                  <c:if test="${!empty item.bcnCalculatedSalaryFoundationTracker && !readOnly}">
                    <html:image property="methodToCall.performPercentAdjustmentSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-percentincdec.gif" title="Percent Adjustment For Line ${status.index}" alt="Percent Adjustment For Line ${status.index}" styleClass="tinybutton" />
                  </c:if>
                </span></div>
            </td>
        </tr>
        </c:forEach>

        <%-- Totals rows --%>
        <tr>
            <kul:htmlAttributeHeaderCell
                scope="row" rowspan="1" colspan="2"
                literalLabel="<span class=\"nowrap\">TOTALS:</span>"
                horizontal="true" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcsfCsfAmountTotal"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="2" />
            <fmt:formatNumber value="${KualiForm.bcsfCsfFullTimeEmploymentQuantityTotal}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcsfCsfFullTimeEmploymentQuantityTotal"
                formattedNumberValue="${formattedNumber}"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="1" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcafAppointmentRequestedAmountTotal"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="1" />
            <fmt:formatNumber value="${KualiForm.bcafAppointmentRequestedFteQuantityTotal}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcafAppointmentRequestedFteQuantityTotal"
                formattedNumberValue="${formattedNumber}"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="1" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcafAppointmentRequestedCsfAmountTotal"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="1" />
            <fmt:formatNumber value="${KualiForm.bcafAppointmentRequestedCsfFteQuantityTotal}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="5" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcafAppointmentRequestedCsfFteQuantityTotal"
                formattedNumberValue="${formattedNumber}"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="1" />
            <kul:htmlAttributeHeaderCell colspan="3" rowspan="2" scope="col" />
        </tr>
        <tr>
            <kul:htmlAttributeHeaderCell
                scope="row" rowspan="1" colspan="4"
                literalLabel="<span class=\"nowrap\">Tot.SHr:</span>"
                horizontal="true" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcsfCsfStandardHoursTotal"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="1" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcafAppointmentRequestedStandardHoursTotal"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="2" />
            <bc:columnTotalCell dataCellCssClass="datacell"
                cellProperty="bcafAppointmentRequestedCsfStandardHoursTotal"
                textStyle="${textStyle}"
                fieldAlign="right"
                colSpan="2" />
        </tr>

    </table>
</div>
</kul:tabTop>