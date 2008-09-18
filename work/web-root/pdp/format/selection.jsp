<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<head>
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
<title>Format Disbursements</title>
<script type="text/javascript">
  var formHasAlreadyBeenSubmitted = false;
  var excludeSubmitRestriction = false;
  function hasFormAlreadyBeenSubmitted() {
    if ( document.getElementById( "formComplete" ) ) { 
	  if (formHasAlreadyBeenSubmitted && !excludeSubmitRestriction) {
        alert("Page already being processed by the server.");
        return false;
      } else {
        formHasAlreadyBeenSubmitted = true;
        return true;
      }
      excludeSubmitRestriction = false; 
    } else {
      alert("Page has not finished loading.");
      return false;
    }
  }
</script>
</head>
<body>
  <h1><strong>Format Disbursements</strong></h1><br>
  <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><strong>Your Default Campus Code is ${campus}</strong></td>
              <td>&nbsp;</td>
            </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>
<br>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td width="90">&nbsp;</td>
      <td align="center">
        <table class="bord-r-t" border="1" cellpadding="0" cellspacing="0" width="80%">
          <tbody>
            <tr>
              <th width="25%">Campus</th>
              <th width="25%">Bank</th>
              <th width="25%">Disbursement Type</th>
              <th width="25%">Next Disbursement Number</th>
            </tr>
            <logic:iterate name="ranges" id="range">
            <tr>
              <th><c:out value="${range.physCampusProcCode}"/></th>
              <td><c:out value="${range.bank.bankName}"/></td>
              <td><c:out value="${range.disbursementTypeCode}"/></td>
              <td align="right"><c:out value="${range.lastAssignedDisbNbr}"/></td>
            </tr>
            </logic:iterate>
          </tbody>
        </table>
      </td>
      <td width="90">&nbsp;</td>
    </tr>
  </tbody>
</table>
<br>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><strong>Enter a Pay Date and any other selection criteria required:</strong></td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>

<html:form action="/pdp/formatprepare" onsubmit="return hasFormAlreadyBeenSubmitted();">
<table align="center" border="0" cellpadding="0" cellspacing="0" width="90%">
  <tbody>
  <tr>
    <td width="20">&nbsp;</td>
    <td><html:errors/>&nbsp;</td>
    <td width="20">&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <table class="bord-r-t" border="0" cellpadding="0" cellspacing="0" width="100%">
        <tbody>
          <tr>
            <th align="right" nowrap="nowrap" width="50%">Pay Date:<br><font size="1"> Ex. 11/26/2004</font></th>
            <td class="datacell" align="left"><html:text property="paymentDate" maxlength="10" size="12"/></td>
          </tr>
          <tr>
            <th colspan="2">&nbsp;</th>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements Flagged as Immediate:</th>
            <td class="datacell" align="left"><html:checkbox property="immediate" value="Y"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">All Payment Types:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="A"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with Attachments:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="AY"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with No Attachments:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="AN"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with Special Handling:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="SY"/></td>
          </tr>
          <tr>
            <th align="right" nowrap="nowrap">Only Disbursements with No Special Handling:</th>
            <td class="datacell" align="left"><html:radio property="paymentTypes" value="SN"/></td>
          </tr>
        </tbody>
      </table>
      <br>
      <table class="bord-r-t" border="0" cellpadding="0" cellspacing="0" width="100%">
        <tbody>
          <tr>
            <th>Customer</th>
          </tr>
        </tbody>
      </table>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tbody>
          <tr>
            <td>
              <div align="center">
              <table border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                  <table border="0" cellpadding="2" cellspacing="0">
	                  <logic:iterate name="customers" id="cust" indexId="i">
	                    <tr>
	                      <td><html:checkbox property="customerProfileId[${i}]"/></td>
	                      <td>&nbsp;&nbsp;<c:out value="${cust.chartCode}/${cust.orgCode}/${cust.subUnitCode}"/></td>
	                      <td>&nbsp;&nbsp;&nbsp;<c:out value="${cust.customerDescription}"/></td>
	                    </tr>
	                  </logic:iterate>
                  </table>
                </td>
              </tr>
              </table>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <table border="0" cellpadding="3" cellspacing="0" width="100%" class="bord-r-t">
        <tbody>
          <tr align="left" valign="middle">
            <th nowrap="nowrap">
              <div align="center">
                <input type="image" name="btnContinue" src="<%= request.getContextPath().toString() %>/pdp/images/button_beginformat.gif" alt="Begin Format" align="center">&nbsp;
                <input type="image" name="btnClear" src="<%= request.getContextPath().toString() %>/pdp/images/button_clearfields.gif" alt="Cancel" align="center">
              </div>
            </th>
          </tr>
        </tbody>
      </table>
    </td>
    <td>&nbsp;</td>
  </tr>
  </tbody>
</table>
</html:form>
<div id="formComplete"></div>
<p>&nbsp;</p>
<c:import url="/pdp/backdoor.jsp"/>
</body>
</html:html>

