<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>


<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<%@ attribute name="person" required="true" type="org.kuali.module.kra.budget.bo.BudgetUser"%>
<%@ attribute name="personListIndex" required="true" %>
<%@ attribute name="matchAppointmentType" required="true" %>

<c:set var="budgetUserAttributes" value="${DataDictionary.BudgetUser.attributes}" />
<c:set var="userAppointmentTaskAttributes" value="${DataDictionary.UserAppointmentTask.attributes}" />
<c:set var="userAppointmentTaskPeriodAttributes" value="${DataDictionary.UserAppointmentTaskPeriod.attributes}" />

<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}" />

              <tr>
                <td colspan="14" class="tab-subhead" height="30"><div align="left"><strong>Salary Disbursement - Summer</strong></div></td>
              </tr>

              <tr>
                <th rowspan="2"><b>Period</b></th>
                <th rowspan="2"># of<br>Weeks</th>
                <th rowspan="2"> Prd Salary </th>
                <td colspan="4" class="tab-subhead"><div align="center"><b>Agency Amount Requested</b> </div></td>
                <td colspan="4" class="tab-subhead"><div align="center"><b>Institution CS</b></div></td>
                <th rowspan="2"><b>Total Effort </b></th>
                <th rowspan="2"><b>Total Salary </b></th>
                <th rowspan="2"><b>Total Fringe Benefits</b></th>
              </tr>

              <tr>
                <th> Effort </th>
                <th> Salary </th>
                <th> C&amp;G Fringe Rate </th>
                <th> Fringe Benefits </th>
                <th> Effort </th>
                <th> Salary</th>
                <th> Fringe Rate</th>
                <th> Fringe Benefits</th>
              </tr>

              <logic:iterate id="userAppointmentTask" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTasks" indexId="userAppointmentTaskIndex">
                <c:if test="${userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber  and userAppointmentTask.universityAppointmentTypeCode eq matchAppointmentType}">
                  <logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
  		              <tr>
  		                <td class="datacell"><div class="nowrap" align="center"><strong>${userAppointmentTaskPeriodIndex + 1}</strong><span class="fineprint"><br />
  		                    (<fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodBeginDate}" dateStyle="short"/> - 
  		                     <fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div>
                        </td>
  		                  <td class="datacell">
                        <div align="right">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].personWeeksAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.personWeeksAmount}" readOnly="${viewOnly}" />
                        </div>
                      </td>

  		                  <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.userBudgetPeriodSalaryAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userBudgetPeriodSalaryAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.userBudgetPeriodSalaryAmount}" />
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyPercentEffortAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyPercentEffortAmount}" readOnly="${viewOnly}" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.agencyRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyRequestTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyRequestTotalAmount}" />
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetFringeRate.contractsAndGrantsFringeRateAmount" write="true" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.agencyFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyFringeBenefitTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyFringeBenefitTotalAmount}" />
                        </div>
                      </td>

  		                  <td class="datacell">
                        <div align="right">
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostSharePercentEffortAmount" disabled="${! KualiForm.document.budget.universityCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.universityCostSharePercentEffortAmount}" readOnly="${viewOnly}" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.universityCostShareRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostShareRequestTotalAmount" disabled="${! KualiForm.document.budget.universityCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.universityCostShareRequestTotalAmount}" />
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetFringeRate.universityCostShareFringeRateAmount" write="true" />%
                        </div>
                      </td>

                      <td class="datacell">
                        <div align="right">
                          <fmt:formatNumber value="${userAppointmentTaskPeriod.universityCostShareFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
                          <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostShareFringeBenefitTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.universityCostShareFringeBenefitTotalAmount}" />
                        </div>
                      </td>

  		                <td class="datacell"><div align="right">${userAppointmentTaskPeriod.totalPercentEffort}%</div></td>
  		                <td class="datacell"><div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalSalaryAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
  		                <td class="datacell"><div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalFringeAmount}" type="currency" currencySymbol="" maxFractionDigits="0" /></div></td>
  		              </tr>
                  </logic:iterate>
  	              <tr>
  	                <th class="bord-l-b"><b>TOTAL</b> </th>
  	                <td colspan="3" class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.agencyRequestTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	
  	                <td class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.agencyFringeBenefitTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	                <td class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.universityCostShareRequestTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	                <td class="infoline">&nbsp;</td>
  	                <td class="infoline"><div align="right"><b><fmt:formatNumber value="${userAppointmentTask.universityCostShareFringeBenefitTotalAmountTask}" type="currency" currencySymbol="$" maxFractionDigits="0" /></b> </div></td>
  	
  	                <td colspan="3" class="infoline">&nbsp;</td>
  	              </tr>
  	            </c:if>
  	          </logic:iterate>
