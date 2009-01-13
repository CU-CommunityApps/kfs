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

  <c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
  <c:set var="budgetPeriodAttributes" value="${DataDictionary.BudgetPeriod.attributes}" />
  <c:set var="supportsModular" value="${KualiForm.supportsModular}" />
  <c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>
  
<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${CGConstants.PARAMETERS_HEADER_TAB}" altText="page help"/>
</div>

       	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />

        <kra-b:budgetParametersGeneral supportsModular="${supportsModular}" />

        <kra-b:budgetPeriods />

        <kra-b:budgetTasks supportsModular="${supportsModular}" />

         <kul:tab tabTitle="Fringe Benefit Rates" defaultOpen="true" tabErrorKey="document.budget.fringe*">
              <div class="tab-container" id="G02" style="" align="center">
        <kra-b:budgetFringeBenefits />
        
        <c:if test="${KualiForm.numberOfAcademicYearSubdivisions gt 0}">
	        <div class="left-errmsg-tab" style="padding-bottom: 10px;"><kul:errors keyMatch="document.budget.grad*"/></div>
	        <kra-b:budgetGradAssistantFringeRates />
        </c:if>
        
				<br>
				<div class="left-errmsg-tab" style="padding-bottom: 10px;"><kul:errors keyMatch="document.budget.budgetFringeRateDescription" /></div>
				  <h3><kul:htmlAttributeLabel attributeEntry="${budgetAttributes.budgetFringeRateDescription}" skipHelpUrl="true" noColon="true"/></h3>
				<table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
				  <tr align="left">
				    <td height="22" ><div align="center"> <br>
				        <kul:htmlControlAttribute property="document.budget.budgetFringeRateDescription" attributeEntry="${budgetAttributes.budgetFringeRateDescription}" readOnly="${viewOnly}"/> <br>
				        <br>
				      </div></td>
				  </tr>
				</table>        

</div>
</kul:tab>
 		
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
        </div>

  <!-- Various Hidden Fields that need to be kept around -->
  <c:if test="${supportsModular}">
	  <html:hidden property="document.budget.budgetAgency.agencyExtension.agencyNumber" />
	  <html:hidden property="document.budget.budgetAgency.agencyExtension.agencyModularIndicator" />
	  <html:hidden property="document.budget.modularBudget.documentNumber" />
	  <html:hidden property="document.budget.modularBudget.budgetModularIncrementAmount" />
	  <html:hidden property="document.budget.modularBudget.budgetPeriodMaximumAmount" />
	  <html:hidden property="document.budget.modularBudget.versionNumber" />
  </c:if>
  
  <logic:iterate id="budgetIndirectCostLookup" name="KualiForm" property="document.budget.budgetIndirectCostLookups" indexId="i">
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].documentNumber" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetOnCampusIndicator" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetPurposeCode" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetIndirectCostRate" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].objectId" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].versionNumber" />
  </logic:iterate>
