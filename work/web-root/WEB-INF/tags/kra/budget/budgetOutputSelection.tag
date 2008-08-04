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

<c:set var="CGConstants" value="${CGConstants}" />

          <table cellpadding=0 cellspacing="0"  summary="">
            <tr>
              <td colspan=2 class="subhead">
                <span class="subhead-left"> Select Report Type </span>
              </td>
            </tr>
            <tr align="center" valign="top">
              <td width="50%" ><div class="floaters" > <strong>
                  <html:radio title="Select Report Type - Generic by Task" property="currentOutputReportType" styleId="currentOutputReportType.genericByTask" value="genericByTask"/><label for="currentOutputReportType.genericByTask">Generic by Task&nbsp&nbsp&nbsp</label>
                  <html:radio title="Select Report Type - Generic by Period" property="currentOutputReportType" styleId="currentOutputReportType.genericByPeriod" value="genericByPeriod"/><label for="currentOutputReportType.genericByPeriod">Generic by Period</label>
                  <br><br>
                  <html:select title="Detail Level" property="currentOutputDetailLevel">
                    <html:option value="">detail level:</html:option>
                    <html:option value="high">high</html:option>
                    <html:option value="medium">medium</html:option>
                    <html:option value="low">low</html:option>
                  </html:select>
                  </strong></div></td>
              <td width="50%" ><label> </label>
                <div class="floaters"> <strong>
                  <html:radio title="Agency" property="currentOutputReportType" styleId="currentOutputReportType.agency" value="agency"/><label for="currentOutputReportType.agency">Agency</label>
                  <br><br>
                  <html:select title="Agency Type" property="currentOutputAgencyType">
                    <html:option value="">type:</html:option>
                    <html:option value="NIH-398">NIH - PHS 398 Form Page 4&amp;5 - Rev 04/06</html:option>
                    <html:option value="NIH-2590">NIH - PHS 2590 - Rev 04/06</html:option>
                    <c:if test="${KualiForm.document.budget.agencyModularIndicator}"><html:option value="NIH-mod">NIH Modular Budget</html:option></c:if>
                    <c:if test="${KualiForm.document.budget.budgetAgency.agencyExtension.agencyNsfOutputIndicator}"><html:option value="NSF-summary">NSF Summary Proposal Budget</html:option></c:if>
                  </html:select>
                  <br>
                  <br>
                  </strong>
                  <html:select title="Agency Period" property="currentOutputAgencyPeriod">
                    <html:option value="">period:</html:option>
                    <c:set var="budgetPeriods" value="${KualiForm.budgetDocument.budget.periods}"/>
                    <html:options collection="budgetPeriods" property="budgetPeriodSequenceNumber" labelProperty="budgetPeriodLabel"/>
                  </html:select>
                  (only for NIH Form 2590)<br>
                  <br>Note: When using budget periods of less than one year with the interim NIH PHS 398 and 2590 Forms, the requested salary amounts are correct but the person-months figure and base salary will need to be manually adjusted in Adobe Acrobat.<br>
                </div></td>
            </tr>
          </table>
          
