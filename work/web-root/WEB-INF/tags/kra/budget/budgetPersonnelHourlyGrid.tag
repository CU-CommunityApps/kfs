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

                <th rowspan="2" class="bord-l-b"><b>Period</b></th>
                <th rowspan="2" colspan="2" class="bord-l-b">Hourly<br>Rate<br>($)</th>
                <td colspan="4" class="tab-subhead"><div align="center"><b>Agency Amount Requested</b> </div></td>
                <td colspan="4" class="tab-subhead"><div align="center"><b>Institution CS</b></div></td>
                <th rowspan="2" class="bord-l-b"><b>Total Hours </b></th>

                <th rowspan="2" class="bord-l-b"><b>Total Salary </b></th>
                <th rowspan="2" class="bord-l-b"><b>Total Fringe Benefits</b></th>
              </tr>
              <tr>
                <th class="bord-l-b">Hours<br>Per<br>Period*</th>
                <th class="bord-l-b"> Salary </th>

                <th class="bord-l-b"> C&amp;G Fringe Rate </th>
                <th class="bord-l-b"> Fringe Benefits </th>
                <th class="bord-l-b"> Hours<br>Per<br>Period*</th>
                <th class="bord-l-b"> Salary</th>

                <th class="bord-l-b"> Fringe Rate</th>
                <th class="bord-l-b"> Fringe Benefits</th>
              </tr>
              
              <logic:iterate id="userAppointmentTask" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTasks" indexId="userAppointmentTaskIndex">
                <c:if test="${userAppointmentTask.budgetTaskSequenceNumber eq person.currentTaskNumber  and userAppointmentTask.universityAppointmentTypeCode eq matchAppointmentType}">
	                <logic:iterate id="userAppointmentTaskPeriod" name="KualiForm" property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriods" indexId="userAppointmentTaskPeriodIndex">
			              <tr>
			                <td class="datacell"><div class="nowrap" align="center"><strong>${userAppointmentTaskPeriodIndex + 1}</strong><span class="fineprint"><br />
			                    (<fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodBeginDate}" dateStyle="short"/> - 
			                     <fmt:formatDate value="${userAppointmentTaskPeriod.period.budgetPeriodEndDate}" dateStyle="short"/>)</span></div></td>
			                <td colspan="2" class="datacell"><div align="right">
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userHourlyRate" attributeEntry="${userAppointmentTaskPeriodAttributes.userHourlyRate}" readOnly="${viewOnly}" />
	                    </div></td>
	
	                    <td class="datacell"><div align="right">
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userAgencyHours" attributeEntry="${userAppointmentTaskPeriodAttributes.userAgencyHours}" readOnly="${viewOnly}" />
	                    </div></td>
	
	                    <td class="datacell"><div align="right">
	                        <fmt:formatNumber value="${userAppointmentTaskPeriod.agencyRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyRequestTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyRequestTotalAmount}" />
	                    </div></td>
	
	                    <td class="datacell"><div align="right">
	                        <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetFringeRate.contractsAndGrantsFringeRateAmount" write="true" />%
	                    </div></td>
			                
	                    <td class="datacell"><div align="right">
	                        <fmt:formatNumber value="${userAppointmentTaskPeriod.agencyFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].agencyFringeBenefitTotalAmount" disabled="${! KualiForm.document.budget.universityCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.agencyFringeBenefitTotalAmount}" />
	                    </div></td>
	
			                <td class="datacell"><div align="right">
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].userUniversityHours" disabled="${! KualiForm.document.budget.universityCostShareIndicator}" attributeEntry="${userAppointmentTaskPeriodAttributes.userUniversityHours}" readOnly="${viewOnly}" />
	                    </div></td>
			                
	                    <td class="datacell"><div align="right">
	                        <fmt:formatNumber value="${userAppointmentTaskPeriod.universityCostShareRequestTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostShareRequestTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.universityCostShareRequestTotalAmount}" />
	                    </div></td>
			                
	                    <td class="datacell"><div align="right">
	                        <html:hidden property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].budgetFringeRate.universityCostShareFringeRateAmount" write="true" />%
	                    </div></td>
			                
	                    <td class="datacell"><div align="right">
	                        <fmt:formatNumber value="${userAppointmentTaskPeriod.universityCostShareFringeBenefitTotalAmount}" type="currency" currencySymbol="" maxFractionDigits="0" />
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${personListIndex}].userAppointmentTask[${userAppointmentTaskIndex}].userAppointmentTaskPeriod[${userAppointmentTaskPeriodIndex}].universityCostShareFringeBenefitTotalAmount" attributeEntry="${userAppointmentTaskPeriodAttributes.universityCostShareFringeBenefitTotalAmount}" />
	                    </div></td>
	
			                <td class="datacell"><div align="right"><fmt:formatNumber value="${userAppointmentTaskPeriod.totalPercentEffort}"maxFractionDigits="0" /></div></td>
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