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

<c:set var="budgetPeriodAttributes" value="${DataDictionary.BudgetPeriod.attributes}" /> <c:set var="CGConstants" value="${CGConstants}" />
<c:set var="businessObjectClass" value="${DataDictionary.BudgetPeriod.businessObjectClass}" />
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>

        <kul:tab tabTitle="Budget Periods" defaultOpen="true" tabErrorKey="document.budget.period*,newPeriod**">
              <div class="tab-container" id="G02" style="" align="center">
        

  <h3><a name="Periods"></a>Budget Periods</h3>
<table cellpadding="0" cellspacing="0" class="datatable" summary=""> <tbody>
  <tr>
    <th width="2%"  scope="row">&nbsp;</th>
    <th ><div align="left">* Date Range</div></th>
    <th  width="10%"><div align="center"><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetPeriod.attributes.budgetPeriodParticipantsNumber}" skipHelpUrl="true" noColon="true" /></div></th>
    <c:if test="${not viewOnly}"><th  width="2%">Action</th></c:if>
  </tr>
  <!-- Add new line option is displayed
  			only if the number of periods is less than the maximum. -->
  <c:if test="${KualiForm.document.periodListSize lt CGConstants.maximumNumberOfPeriods && not viewOnly}">
  <tr>
    <th width="2%"  scope="row"><div align="right">add:</div></th>
    <td class="infoline"> 
     <kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetPeriod.attributes.budgetPeriodBeginDate}" skipHelpUrl="true" readOnly="true" /> <kul:htmlControlAttribute property="newPeriod.budgetPeriodBeginDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodBeginDate}" readOnly="${viewOnly}" datePicker="true"/> &nbsp;&nbsp;&nbsp;<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetPeriod.attributes.budgetPeriodEndDate}" skipHelpUrl="true" readOnly="true" /> <kul:htmlControlAttribute property="newPeriod.budgetPeriodEndDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodEndDate}" datePicker="true"/> </td>
    <td class="infoline"><div align="center"><kul:htmlControlAttribute property="newPeriod.budgetPeriodParticipantsNumber" attributeEntry="${budgetPeriodAttributes.budgetPeriodParticipantsNumber}" /></div></td>
    <td class="infoline"><div align="center"> <html:image property="methodToCall.insertPeriodLine.anchor${currentTabIndex}" styleClass="tinybutton" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="add period line" alt="add period line"/> </div></td>
  </tr>
  </c:if>
  <!-- Show all currently attached tasks.  -->
  <logic:iterate id="periodLine" name="KualiForm" property="document.budget.periods" indexId="ctr">
  <tr>
    <th width="2%"  scope="row"><div align="right">${ctr+1}:</div></th>
    <td><kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetPeriod.attributes.budgetPeriodBeginDate}" skipHelpUrl="true" readOnly="true" /> <kul:htmlControlAttribute property="document.budget.period[${ctr}].budgetPeriodBeginDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodBeginDate}" readOnly="${viewOnly}" datePicker="true"/> &nbsp;&nbsp;&nbsp;<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetPeriod.attributes.budgetPeriodEndDate}" skipHelpUrl="true" readOnly="true" /> <kul:htmlControlAttribute property="document.budget.period[${ctr}].budgetPeriodEndDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodEndDate}" readOnly="${viewOnly}" datePicker="true"/> </td>
    <td><div align="center"><kul:htmlControlAttribute property="document.budget.period[${ctr}].budgetPeriodParticipantsNumber" attributeEntry="${budgetPeriodAttributes.budgetPeriodParticipantsNumber}" readOnly="${viewOnly}" /></div></td>
    <c:if test="${not viewOnly}"><td><div align="center"> <c:choose><c:when test="${ctr eq KualiForm.document.periodListSize-1}"> <html:image property="methodToCall.deletePeriodLine.anchorPeriods.line${ctr}" styleClass="tinybutton" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="delete" alt="delete"/> </c:when><c:otherwise>&nbsp;</c:otherwise></c:choose> </div></td></c:if>
  </tr>
  </logic:iterate>
  <!-- End periods list. -->
</tbody></table>
<div align="right">*required&nbsp;&nbsp;&nbsp;</div>
</div>
</kul:tab>
