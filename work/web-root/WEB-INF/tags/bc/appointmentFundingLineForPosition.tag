<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ attribute name="fundingLine" required="true" type="java.lang.Object"
			  description="The funding line object containing the data being displayed"%>
<%@ attribute name="fundingLineName" required="true" description="The name  of the funding line"%>
<%@ attribute name="lineIndex" required="false" description="The index of the funding line"%>
<%@ attribute name="hasBeenAdded" required="false" description="determine if the current funding line has been added"%>
<%@ attribute name="countOfMajorColumns" required="true" description="the number of major columns "%>
<%@ attribute name="readOnly" required="false" description="determine whether the contents can be read only or not"%>
<%@ attribute name="isKeyFieldsLocked" required="false" description="determine whether the key fields can be locked from editing"%>

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
	
	<script type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<script type="text/javascript" src="scripts/budget/objectInfo.js"></script>
	
	<c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="pbcafAttributes" value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />  
<c:set var="intcumbentAttributes" value="${DataDictionary['BudgetConstructionIntendedIncumbent'].attributes}" /> 
<c:set var="adminPostAttributes" value="${DataDictionary['BudgetConstructionAdministrativePost'].attributes}" /> 

<c:set var="colspan" value="${countOfMajorColumns}" />

<html:hidden property="${fundingLineName}.universityFiscalYear" />
<html:hidden property="${fundingLineName}.positionNumber" />
<html:hidden property="${fundingLineName}.positionObjectChangeIndicator" />
<html:hidden property="${fundingLineName}.positionSalaryChangeIndicator" />
<html:hidden property="${fundingLineName}.versionNumber" />  
<html:hidden property="${fundingLineName}.appointmentFundingDeleteIndicator" />

<html:hidden property="${fundingLineName}.persistedDeleteIndicator" /> 
<html:hidden property="${fundingLineName}.newLineIndicator" />

<html:hidden property="${fundingLineName}.excludedFromTotal" />
<html:hidden property="${fundingLineName}.displayOnlyMode" /> 
<html:hidden property="${fundingLineName}.override2PlugMode" />

<table border="0" cellpadding="0" cellspacing="0" style="width: ${tableWidth}; text-align: left; margin-left: auto; margin-right: auto;">    
	<tr>
	    <kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.appointmentFundingDeleteIndicator}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.chartOfAccountsCode}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.accountNumber}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.subAccountNumber}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.financialObjectCode}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.financialSubObjectCode}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${pbcafAttributes.emplid}"/>
	   	<kul:htmlAttributeHeaderCell attributeEntry="${intcumbentAttributes.iuClassificationLevel}"/>
	    <kul:htmlAttributeHeaderCell attributeEntry="${adminPostAttributes.administrativePost}"/>
	</tr>
	
	<tr> 
	  	<bc:pbglLineDataCell dataCellCssClass="datacell"
	    	accountingLine="${fundingLineName}"
	      	cellProperty="${fundingLineName}.appointmentFundingDeleteIndicator"
		    attributes="${pbcafAttributes}"
		    field="appointmentFundingDeleteIndicator"
		    fieldAlign="center"
		    readOnly="false"
		    rowSpan="1 "dataFieldCssClass="nobord"
		    anchor="salaryexistingLineLineAnchor${lineIndex}" disabled="true"/>
		      
		 <bc:pbglLineDataCell dataCellCssClass="datacell"
		    accountingLine="${fundingLineName}"
		    field="chartOfAccountsCode"
		    detailField="chartOfAccounts.finChartOfAccountDescription" detailFunction="loadChartInfo"
		    attributes="${pbcafAttributes}" lookup="true" inquiry="true"
		    boClassSimpleName="Chart"
		    readOnly="${hasBeenAdded || isKeyFieldsLocked}"
		    displayHidden="false"
		    lookupOrInquiryKeys="chartOfAccountsCode"
		    accountingLineValuesMap="${fundingLine.valuesMap}" />
		      
		 <bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"
			field="accountNumber" detailFunction="loadAccountInfo"
			detailField="account.accountName"
			attributes="${pbcafAttributes}" lookup="true" inquiry="true"
			boClassSimpleName="Account"
			readOnly="${hasBeenAdded || isKeyFieldsLocked}"
			displayHidden="false"
			lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
			accountingLineValuesMap="${fundingLine.valuesMap}" />
	  
		<c:set var="doAccountLookupOrInquiry" value="false"/>
	  	<c:if test="${fundingLine.subAccountNumber ne BCConstants.DASH_SUB_ACCOUNT_NUMBER}">
	      	<c:set var="doAccountLookupOrInquiry" value="true"/>
	  	</c:if>
	  	
		<bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"
			field="subAccountNumber" detailFunction="loadSubAccountInfo"
			detailField="subAccount.subAccountName"
			attributes="${pbcafAttributes}" lookup="true" inquiry="${doAccountLookupOrInquiry}" 
			boClassSimpleName="SubAccount"
			readOnly="${hasBeenAdded || isKeyFieldsLocked}"
			displayHidden="false"
			lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
			accountingLineValuesMap="${fundingLine.valuesMap}" />
	
		<bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"
			field="financialObjectCode" detailFunction="loadObjectInfo"
			detailFunctionExtraParam="${fundingLine.universityFiscalYear}, '${fundingLineName}.financialObject.financialObjectType.name', '${fundingLineName}.financialObject.financialObjectTypeCode',"
			detailField="financialObject.financialObjectCodeShortName"
			attributes="${pbcafAttributes}" lookup="true" inquiry="true"
			boClassSimpleName="ObjectCode"
			readOnly="${hasBeenAdded || isKeyFieldsLocked}"
			displayHidden="false"
			lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
			accountingLineValuesMap="${fundingLine.valuesMap}"
			inquiryExtraKeyValues="universityFiscalYear=${fundingLine.universityFiscalYear}" />
	
	  	<c:set var="doLookupOrInquiry" value="false"/>
	  	<c:if test="${fundingLine.financialSubObjectCode ne BCConstants.DASH_SUB_OBJECT_CODE}">
	      	<c:set var="doLookupOrInquiry" value="true"/>
	  	</c:if>
	  	
	  	<bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"
			field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
			detailFunctionExtraParam="${fundingLine.universityFiscalYear}, "
			detailField="financialSubObject.financialSubObjectCdshortNm"
			attributes="${pbcafAttributes}" lookup="true" inquiry="${doAccountLookupOrInquiry}" 
			boClassSimpleName="SubObjCd"
			readOnly="${hasBeenAdded || isKeyFieldsLocked}"
			displayHidden="false"
			lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
			accountingLineValuesMap="${fundingLine.valuesMap}"
			inquiryExtraKeyValues="universityFiscalYear=${fundingLine.universityFiscalYear}" />
	
		<c:set var="doLookupOrInquiry" value="false"/>
		<c:if test="${fundingLine.emplid ne BCConstants.VACANT_EMPLID}">
			<c:set var="doLookupOrInquiry" value="true"/>
		</c:if>
	
		<bc:pbglLineDataCell dataCellCssClass="datacell"
			accountingLine="${fundingLineName}"
			field="emplid" detailFunction="budgetObjectInfoUpdator.loadIntendedIncumbentInfo"
			detailField="budgetConstructionIntendedIncumbent.personName"
			detailFunctionExtraParam="'${fundingLineName}.positionNumber', 
						'${fundingLineName}.budgetConstructionIntendedIncumbent.iuClassificationLevel', 
						'${fundingLineName}.budgetConstructionAdministrativePost.administrativePost',"
			attributes="${pbcafAttributes}" inquiry="${doLookupOrInquiry}" lookup="true"
			boClassSimpleName="BudgetConstructionIntendedIncumbent"
			boPackageName="org.kuali.kfs.module.bc.businessobject"
			readOnly="${hasBeenAdded}"
			displayHidden="false"
			lookupOrInquiryKeys="emplid"
			accountingLineValuesMap="${fundingLine.valuesMap}"
			inquiryExtraKeyValues="universityFiscalYear=${fundingLine.universityFiscalYear}" />	
	 
        <td class="datacell" rowSpan="1">        		
		    <bc:pbglLineDataCellDetail detailField="budgetConstructionIntendedIncumbent.iuClassificationLevel" 
		    	accountingLine="${fundingLineName}" dataFieldCssClass="nowrap" />
		</td> 
		
		<td class="datacell" rowSpan="1">        		
		    <bc:pbglLineDataCellDetail detailField="budgetConstructionAdministrativePost.administrativePost" 
		    	accountingLine="${fundingLineName}" dataFieldCssClass="nowrap" />
		</td>  		         
	</tr>
	                                
	<tr id="${fundingLineName}">
		<td colspan="${colspan}" class="infoline" style="border-bottom: none;">
			<center><br/>
			<bc:appointmentFundingDetail fundingLine="${fundingLine}" fundingLineName="${fundingLineName}" 
				lineIndex="${lineIndex}" readOnly="${readOnly || fundingLine.displayOnlyMode}"/>
			<br/></center>
		</td>            
	</tr>
	
	<tr>
		<td colspan="${colspan}" class="datacell">
			<div class="right">
				<jsp:doBody/>
			</div>
		</td>            
	</tr>
</table>
