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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
<c:set var="budgetGraduateFringeRateAttributes" value="${DataDictionary.BudgetGraduateAssistantRate.attributes}" />
<c:set var="businessObjectClass" value="${DataDictionary.GraduateAssistantRate.businessObjectClass}" />
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}"/>

  <a name="GradAsst"></a><h3>Research &amp; Grad Assistant Fringe Benefit Rates</h3>

<table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
  <c:forEach var="i" begin="1" end="${KualiForm.numberOfAcademicYearSubdivisions}">
  <tr>
    <td colspan="3" class="tab-subhead">${KualiForm.academicYearSubdivisionNames[i - 1]} Health Insurance</td>
  </tr>

  <tr>
    <th><div align="left">Position (<kul:htmlAttributeLabel attributeEntry="${DataDictionary.BudgetGraduateAssistantRate.attributes.campusCode}" useShortLabel="true" skipHelpUrl="true" noColon="true"/>)</div></th>
    <th>System Rate</th>
    <th>* Current Rate</th>
  </tr>

  <!-- begin inner loop -->
  <logic:iterate id="graduateAssistantRatesLine" name="KualiForm" property="document.budget.graduateAssistantRates" indexId="ctr"> 
  <tr>
    <td>${graduateAssistantRatesLine.graduateAssistantRate.campus.campusName} ( ${graduateAssistantRatesLine.graduateAssistantRate.campusCode} )</td>
    <td><div align="center"> $<bean:write name="graduateAssistantRatesLine" property="graduateAssistantRate.campusMaximumPeriodRate[${i}]" /> </div></td>
    <td><div align="center"> $ 
    <c:choose> 
    <c:when test="${i eq 1}"> 
    <kul:htmlControlAttribute accessibilityHint=" for ${graduateAssistantRatesLine.graduateAssistantRate.campus.campusName} for ${KualiForm.academicYearSubdivisionNames[i - 1]} Health Insurance" property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod1Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 2}"> 
    <kul:htmlControlAttribute accessibilityHint=" for ${graduateAssistantRatesLine.graduateAssistantRate.campus.campusName} for ${KualiForm.academicYearSubdivisionNames[i - 1]} Health Insurance" property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod2Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 3}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod3Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 4}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod4Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 5}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod5Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    <c:when test="${i eq 6}"> 
    <kul:htmlControlAttribute property="document.budget.graduateAssistantRate[${ctr}].campusMaximumPeriod${i}Rate" attributeEntry="${budgetGraduateFringeRateAttributes.campusMaximumPeriod6Rate}" readOnly="${viewOnly}" styleClass="amount"/> 
    </c:when> 
    </c:choose> 
    </div></td>
  </tr>
  <!-- end Inner loop -->
  </logic:iterate> 
  </c:forEach>
  <c:if test="${not viewOnly}">
  <tr align="left">
    <th height="22">&nbsp;</th>
    <th height="22" colspan="2"><div align="center"><html:image property="methodToCall.copySystemGraduateAssistantLines.anchor${currentTabIndex}" styleClass="tinybutton" src="${ConfigProperties.externalizable.images.url}tinybutton-copsysrates.gif" alt="copy system rate" title="copy system rate"/></div></th>
  </tr>
  </c:if>
</table>
<div align="right">*required&nbsp;&nbsp;&nbsp;<br></div>


