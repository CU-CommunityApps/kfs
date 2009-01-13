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

<%@ attribute name="supportsModular" required="true" %>

<c:set var="budgetTaskAttributes" value="${DataDictionary.BudgetTask.attributes}" /> <c:set var="CGConstants" value="${CGConstants}" />
<c:set var="businessObjectClass" value="${DataDictionary.BudgetTask.businessObjectClass}" />
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>


<kul:tab 
	tabTitle="Tasks/Components" 
	defaultOpen="true" 
	tabErrorKey="document.budget.task*,newTask*" 
	auditCluster="parametersSoftAuditErrors" 
	tabAuditKey="document.budget.audit.parameters.tasks.negativeIdc*">
        
<div class="tab-container" id="G02" style="" align="center">
              
  <a name="Tasks"></a><h3>Tasks/Components</h3>
<table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
  <!-- Column headers -->
  <tr>
    <th width="2%" scope="row">&nbsp;</th>
    <th><div align="left"><kul:htmlAttributeLabel labelFor="newTask.budgetTaskName" attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskName}" skipHelpUrl="true" noColon="true" /></div></th>
    <th width="5%"><div align="center"><kul:htmlAttributeLabel labelFor="newTask.budgetTaskOnCampus" attributeEntry="${DataDictionary.BudgetTask.attributes.budgetTaskOnCampus}" skipHelpUrl="true" noColon="true" /></div></th>
    <th width="5%"><div align="center"><kul:htmlAttributeLabel labelFor="document.budget.modularBudget.budgetModularTaskNumber" attributeEntry="${DataDictionary.BudgetModular.attributes.budgetModularTaskNumber}" skipHelpUrl="true" noColon="true" /></div></th>
    <c:if test="${not viewOnly}"><th width="2%">Action</th></c:if>
  </tr>
  <!-- Default add line for additional tasks
         shown only if there are < 20 tasks currently attached. -->
  <c:if test="${KualiForm.document.taskListSize lt CGConstants.maximumNumberOfTasks && not viewOnly}">
  <tr>
    <th width="50" align="right" scope="row"><div align="right">add:</div></th>
    <td class="infoline"><html:hidden property="newTask.documentNumber" /> <html:hidden property="newTask.budgetTaskSequenceNumber" /> <html:hidden property="newTask.objectId"/> <html:hidden property="newTask.versionNumber" /> <span> <kul:htmlControlAttribute property="newTask.budgetTaskName" attributeEntry="${budgetTaskAttributes.budgetTaskName}"/> </span> </td>
    <td class="infoline"><div align="center"> <kul:htmlControlAttribute property="newTask.budgetTaskOnCampus" attributeEntry="${budgetTaskAttributes.budgetTaskOnCampus}"/> </div></td>
    <td class="infoline"><div align="center">
        <input type="radio" id="document.budget.modularBudget.budgetModularTaskNumber" name="document.budget.modularBudget.budgetModularTaskNumber" disabled="true" value="-1" /></label>
      </div></td>
    <td class="infoline"><div align="center"><html:image property="methodToCall.insertTaskLine.anchor${currentTabIndex}" styleClass="tinybutton" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="add task line" alt="add task line"/></div></td>
  </tr>
  </c:if>
  <!-- Iterate over currently attached tasks. -->
  <logic:iterate id="taskLine" name="KualiForm" property="document.budget.tasks" indexId="ctr">
  <tr>
    <th width="50" scope="row"><div align="right">${ctr+1}:</div></th>
    <td><kul:htmlControlAttribute property="document.budget.task[${ctr}].budgetTaskName" attributeEntry="${budgetTaskAttributes.budgetTaskName}" readOnly="${viewOnly}"/> <html:hidden property="document.budget.task[${ctr}].documentNumber" /> <html:hidden property="document.budget.task[${ctr}].budgetTaskSequenceNumber" /> <html:hidden property="document.budget.task[${ctr}].objectId"/> <html:hidden property="document.budget.task[${ctr}].versionNumber" /> </td>
    <td><div align="center">
        <label> <kul:htmlControlAttribute property="document.budget.task[${ctr}].budgetTaskOnCampus" attributeEntry="${budgetTaskAttributes.budgetTaskOnCampus}" readOnly="${viewOnly}"/> </label>
      </div></td>
    <td><div align="center">
        <label> <html:radio property="document.budget.modularBudget.budgetModularTaskNumber" value="${taskLine.budgetTaskSequenceNumber}" disabled="${not(supportsModular and KualiForm.document.budget.agencyModularIndicator) || viewOnly}"/> </label>
      </div></td>
    <c:if test="${not viewOnly}"><td><div align="center"> <html:image property="methodToCall.deleteTaskLine.line${ctr}.anchorTasks" styleClass="tinybutton" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="delete" alt="delete"/> </div></td></c:if>
  </tr>
  </logic:iterate>
  <!-- End of tasks table. -->
</table>
<div align="right">*required&nbsp;&nbsp;&nbsp;</div>
</div>
</kul:tab>
