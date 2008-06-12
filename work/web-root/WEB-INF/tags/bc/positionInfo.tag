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

<c:set var="positionAttributes"	value="${DataDictionary['BudgetConstructionPosition'].attributes}" />
<c:set var="budgetConstructionPosition"	value="${KualiForm.budgetConstructionPosition}" />
<c:set var="objectName"	value="budgetConstructionPosition" />

<html:hidden property="${objectName}.positionEffectiveDate" />
<html:hidden property="${objectName}.positionEffectiveStatus" />
<html:hidden property="${objectName}.positionStatus" />
<html:hidden property="${objectName}.budgetedPosition" />
<html:hidden property="${objectName}.confidentialPosition" />
<html:hidden property="${objectName}.positionRegularTemporary" />
<html:hidden property="${objectName}.setidDepartment" />
<html:hidden property="${objectName}.responsibilityCenterCode" />
<html:hidden property="${objectName}.positionUnionCode" />
<html:hidden property="${objectName}.jobCodeDescription" />
<html:hidden property="${objectName}.setidSalary" />
<html:hidden property="${objectName}.iuPositionType" />
<html:hidden property="${objectName}.positionLockUserIdentifier" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
	<tr>
        <td colspan="6" class="subhead">Position</td>
   	</tr>
   	
    <tr>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.universityFiscalYear}"
            horizontal="true" />
            
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.universityFiscalYear"
            field="universityFiscalYear"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />            
        
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionNumber}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.positionNumber"
            field="positionNumber"
            attributes="${posnAttributes}" inquiry="true"
            boClassSimpleName="BudgetConstructionPosition"
            boPackageName="org.kuali.module.budget.bo"
            readOnly="true"
            displayHidden="false"
            lookupOrInquiryKeys="positionNumber"
            accountingLineValuesMap="${KualiForm.budgetConstructionPosition.valuesMap}"/>
            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionDescription}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.positionDescription"
            field="positionDescription"
            attributes="${posnAttributes}" inquiry="true"
            boClassSimpleName="BudgetConstructionPosition"
            boPackageName="org.kuali.module.budget.bo"
            readOnly="true"
            displayHidden="false"/>            
    </tr>
    
    <tr>
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.iuDefaultObjectCode}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
        	accountingLine="${budgetConstructionPosition}"
            cellProperty="budgetConstructionPosition.iuDefaultObjectCode"
            field="iuDefaultObjectCode"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false"/>

        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionDepartmentIdentifier}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionDepartmentIdentifier"
            field="positionDepartmentIdentifier"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false"/>
            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.setidJobCode}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.setidJobCode"
            field="setidJobCode"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
    </tr>
    
    <tr>            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.jobCode}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.jobCode"
            field="jobCode"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />

        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionSalaryPlanDefault}"
            horizontal="true" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionSalaryPlanDefault"
            field="positionSalaryPlanDefault"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
        
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionGradeDefault}"
            horizontal="true" />    
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionGradeDefault"
            field="positionGradeDefault"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
    </tr>
    
    <tr>            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.iuNormalWorkMonths}"
            horizontal="true" /> 
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.iuNormalWorkMonths"
            field="iuNormalWorkMonths"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
       
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.iuPayMonths}"
            horizontal="true" />             
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.iuPayMonths"
            field="iuPayMonths"
            attributes="${positionAttributes}"
            readOnly="true"
            displayHidden="false" />
            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionStandardHoursDefault}"
            horizontal="true" />  
        <fmt:formatNumber value="${budgetConstructionPosition.positionStandardHoursDefault}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionStandardHoursDefault"
            field="positionStandardHoursDefault"
            attributes="${positionAttributes}"
            readOnly="true"
            formattedNumberValue="${formattedNumber}"
            displayHidden="false" dataFieldCssClass="amount" />
    </tr>
    
    <tr>            
        <kul:htmlAttributeHeaderCell
        	attributeEntry="${positionAttributes.positionFullTimeEquivalency}"
            horizontal="true" />
        <fmt:formatNumber value="${KualiForm.budgetConstructionPosition.positionFullTimeEquivalency}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
        <bc:pbglLineDataCell dataCellCssClass="datacell"
            cellProperty="budgetConstructionPosition.positionFullTimeEquivalency"
            field="positionFullTimeEquivalency"
            attributes="${positionAttributes}"
            readOnly="true"
            formattedNumberValue="${formattedNumber}"
            displayHidden="false" />

		<kul:htmlAttributeHeaderCell literalLabel=" " horizontal="true" />
	    <td>&nbsp;</td>
	    
		<kul:htmlAttributeHeaderCell literalLabel=" " horizontal="true" />
	    <td>&nbsp;</td>	     
    </tr>
</table>