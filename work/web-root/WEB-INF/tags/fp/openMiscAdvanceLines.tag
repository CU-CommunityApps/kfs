<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%--
  the miscAdvanceLines.tag is only used to show misc advances that are being paid back - that is, they live in the "money out"
  section of the page
--%>
<c:if test="${!empty KualiForm.document.currentTransaction.openItemsInProcess}">
  <table border="0" cellspacing="0" cellpadding="0" class="datatable">
    <tr>
      <td colspan="7" class="infoline">
        Open Misc. Advances
      </td>
    </tr>
    <fp:miscAdvanceHeader itemInProcessProperty="document.currentTransaction.openItemInProcess[${loopStatus.index}]" creatingItemInProcess="false" />
    <c:forEach var="itemInProcess" items="${KualiForm.document.currentTransaction.openItemsInProcess}" varStatus="loopStatus">
      <fp:miscAdvanceLine itemInProcessProperty="document.currentTransaction.openItemInProcess[${loopStatus.index}]" creatingItemInProcess="false" />
    </c:forEach>
  </table>
</c:if>
