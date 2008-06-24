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
<%@page import="java.util.List,org.kuali.kfs.pdp.businessobject.PaymentDetail,org.kuali.kfs.pdp.businessobject.PaymentGroup" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
  <head>
    <html:base />
    <title>Payment Details</title>
  </head>
  <body>
    <h1><strong>Payment Details for Payment Detail ID: <c:out value="${PaymentDetail.id}"/></strong></h1>
    <br>
    <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <br>
          <font color="#800000">
            <html:errors/>&nbsp;
          </font>&nbsp;
          <br>
        </td>
      </tr>
    </table>
    <table width="100%" border=0 cellspacing=0 cellpadding=0>
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <table width="100%" height=40 border=0 cellpadding=0 cellspacing=0>
            <tr>
              <td>Return to:&nbsp; 
            <c:if test="${batchSearchResults != null}" >
              &nbsp; <a href="<%= request.getContextPath().toString() %>/pdp/batchsearch.do?btnBack_batch=param">Batch Search Results</a> 
              &nbsp; ::
              &nbsp; <a href="<%= request.getContextPath().toString() %>/pdp/batchsearch.do?btnBack_batchIndiv=param">Individual Payments in Batch</a>
            </c:if>
            <c:if test="${batchSearchResults == null}" >
              &nbsp; <a href="<%= request.getContextPath().toString() %>/pdp/paymentsearch.do?btnBack_Indiv=param">Individual Search Results</a>
            </c:if>
          </td>
          <td>
            &nbsp;
          </td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
  <tr>
    <td width=20>&nbsp;</td>
    <td>    
      <table width="100%" border=0 cellspacing=0 cellpadding=0>
        <tr>
          <td>
            <table width="100%" border=0 cellpadding=4 cellspacing=0 class="tabtable">
              <tr>
            <c:if test="${btnPressed == 'btnSummaryTab'}" >
              <td class="tab-selected" >
            </c:if>
            <c:if test="${btnPressed != 'btnSummaryTab'}" >
              <td class="tab-notselected" >
            </c:if>
                  <div align="center">
                    <c:if test="${btnPressed != 'btnSummaryTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnSummaryTab=param" class="HdrScrLnk">
                    </c:if>
                    Summary
                    <c:if test="${btnPressed != 'btnSummaryTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${btnPressed == 'btnBatchTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnBatchTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align="center">
                    <c:if test="${btnPressed != 'btnBatchTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnBatchTab=param" class="HdrScrLnk">
                    </c:if>
                    Batch
                    <c:if test="${btnPressed != 'btnBatchTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${btnPressed == 'btnPaymentTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnPaymentTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align="center">
                    <c:if test="${btnPressed != 'btnPaymentTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnPaymentTab=param" class="HdrScrLnk">
                    </c:if>
                    Payment
                    <c:if test="${btnPressed != 'btnPaymentTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${btnPressed == 'btnPayeeTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnPayeeTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align="center">
                    <c:if test="${btnPressed != 'btnPayeeTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnPayeeTab=param" class="HdrScrLnk">
                    </c:if>
                    Payee
                    <c:if test="${btnPressed != 'btnPayeeTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${btnPressed == 'btnAccountingTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnAccountingTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${btnPressed != 'btnAccountingTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnAccountingTab=param" class="HdrScrLnk">
                    </c:if>
                    Accounting
                    <c:if test="${btnPressed != 'btnAccountingTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${btnPressed == 'btnAchInfoTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnAchInfoTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${btnPressed != 'btnAchInfoTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnAchInfoTab=param" class="HdrScrLnk">
                    </c:if>
                    ACH Info
                    <c:if test="${btnPressed != 'btnAchInfoTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>                
                <c:if test="${btnPressed == 'btnHistoryTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnHistoryTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${btnPressed != 'btnHistoryTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnHistoryTab=param" class="HdrScrLnk">
                    </c:if>
                    History
                    <c:if test="${btnPressed != 'btnHistoryTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
                <c:if test="${btnPressed == 'btnNotesTab'}" >
                  <td class="tab-selected" >
                </c:if>
                <c:if test="${btnPressed != 'btnNotesTab'}" >
                  <td class="tab-notselected" >
                </c:if>
                  <div align=center>
                    <c:if test="${btnPressed != 'btnNotesTab'}" >
                       <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?btnNotesTab=param" class="HdrScrLnk">
                    </c:if>
                    Notes
                    <c:if test="${btnPressed != 'btnNotesTab'}" >
                       </a>
                    </c:if>
                  </div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
  <tr>
    <td width="20">&nbsp</td>
    <td>
      <table width="100%" border=0 cellpadding=0 cellspacing=0>
          <!-- SUMMARY TAB -->
          <jsp:include page="summaryTab.jsp" flush="true"/>
          <c:if test="${btnPressed == 'btnBatchTab'}" >
            <!-- BATCH TAB -->
            <jsp:include page="batchTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${btnPressed == 'btnPaymentTab'}" >
            <!-- PAYMENT TAB -->
            <jsp:include page="paymentTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${btnPressed == 'btnPayeeTab'}" >
            <!-- PAYEE TAB -->
            <jsp:include page="payeeTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${btnPressed == 'btnAccountingTab'}" >
            <!-- ACCOUNTING TAB -->
            <jsp:include page="accountingTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${btnPressed == 'btnAchInfoTab'}" >
            <!-- ACH INFO TAB -->
            <jsp:include page="achInfoTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${btnPressed == 'btnHistoryTab'}" >
            <!-- HISTORY TAB -->
            <jsp:include page="historyTab.jsp" flush="true"/>
          </c:if>
          <c:if test="${btnPressed == 'btnNotesTab'}" >
            <!-- NOTES TAB -->
            <jsp:include page="notesTab.jsp" flush="true"/>
          </c:if>
<html:form action="/pdp/paymentupdate">
          <c:if test="${btnPressed != 'btnUpdate'}" >
          <tr valign=middle align=left>
            <td>
              <table cellpadding=3 width="100%" cellspacing=0 border=0  class="bord-r-t">
                <tbody>
                  <tr valign=middle align=left>
                  <th nowrap=nowrap>
                    <c:choose>
                      <c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'OPEN'}">
                         
                            <div align="center">
                              <c:if test="${(SecurityRecord.sysAdminRole == true) or (SecurityRecord.holdRole == true)}">
                                <input type="image" name="btnHold" src="<%= request.getContextPath().toString() %>/pdp/images/button_holdpayment.gif" alt="Hold Payment" align="absmiddle">
                              </c:if>
                              <c:if test="${(SecurityRecord.cancelRole == true) or (SecurityRecord.sysAdminRole == true) or (SecurityRecord.taxHoldersRole == true)}">
                                <input type="image" name="btnCancel" src="<%= request.getContextPath() + "/pdp/images/button_cancelpayment.gif" %>" alt="Cancel Payment" align="absmiddle">
                              </c:if>
                              <c:if test="${(SecurityRecord.processRole == true) or (SecurityRecord.sysAdminRole == true)}">
                                <c:if test="${PaymentDetail.paymentGroup.processImmediate == true}" >
                                  <input type="image" name="btnChangeImmediate" src="<%= request.getContextPath() + "/pdp/images/button_remimmediate.gif" %>" alt="Remove Immediate Print" align="absmiddle"></a>
                                </c:if>
                                <c:if test="${PaymentDetail.paymentGroup.processImmediate == false}" >
                                  <input type="image" name="btnChangeImmediate" src="<%= request.getContextPath() + "/pdp/images/button_setimmediate.gif" %>" alt="Set Immediate Print" align="absmiddle"></a>
                                </c:if>
                              </c:if>
                            </div>
                          
                        
                      </c:when>
                      <c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'HELD'}">
                          
                            <div align="center">
                              <c:if test="${SecurityRecord.holdRole == true}">
                                <input type="image" name="btnRemoveHold" src="<%= request.getContextPath() + "/pdp/images/button_removehold.gif" %>" alt="Remove Hold" align="absmiddle"></a>
                              </c:if>
                              <c:if test="${(SecurityRecord.cancelRole == true) or (SecurityRecord.sysAdminRole == true) or (SecurityRecord.taxHoldersRole == true)}">
                                <input type="image" name="btnCancel" src="<%= request.getContextPath() + "/pdp/images/button_cancelpayment.gif" %>" alt="Cancel Payment" align="absmiddle"></a>
                              </c:if>
                            </div>
                          
                       
                      </c:when>
                      <c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'PACH'}">
                        
                          <div align="center">
                            <c:choose>
                              <c:when test="${(SecurityRecord.cancelRole == true) or (SecurityRecord.sysAdminRole == true)}">
                                <c:choose>
                                  <c:when test="${PaymentDetail.disbursementActionAllowed}">
                                  	<input type="image" name="btnDisbursementCancel" src="<%= request.getContextPath() + "/pdp/images/button_canceldisb.gif" %>" alt="Cancel Disbursement" align="absmiddle">
                                    <input type="image" name="btnReIssueCancel" src="<%= request.getContextPath() + "/pdp/images/button_cancreissuedis.gif" %>" alt="Cancel & Reissue Disbursement" align="absmiddle">
                                  </c:when>
                                  <c:otherwise>
                                    <strong>This disbursement may not be cancelled or cancelled and reissued because the disbursement date is before <fmt:formatDate value="${PaymentDetail.lastDisbursementActionDate}" pattern="MM/dd/yyyy"/>.</strong>
                                  </c:otherwise>
                                </c:choose>
                              </c:when>
                              <c:when test="${SecurityRecord.cancelRole == true}">
                                <strong>You do not have permissions to cancel or cancel and reissue this disbursement.  Please contact the PDP System Administrators to perform the required action.</strong>
                              </c:when>
                            </c:choose>
                          </div>
                        
                      </c:when>
                      <c:when test="${PaymentDetail.paymentGroup.paymentStatus.code == 'EXTR'}">
                        <c:if  test="${SecurityRecord.cancelRole == true}">
                          
                            <div align="center">
                              <c:choose>
                                <c:when test="${not empty PaymentDetail.paymentGroup.disbursementDate}">
                                  <c:choose>
                                    <c:when test="${PaymentDetail.disbursementActionAllowed}">
                                      <input type="image" name="btnDisbursementCancel" src="<%= request.getContextPath() + "/pdp/images/button_canceldisb.gif" %>" alt="Cancel Disbursement" align="absmiddle">
                                      <input type="image" name="btnReIssueCancel" src="<%= request.getContextPath() + "/pdp/images/button_cancreissuedis.gif" %>" alt="Cancel & Reissue Disbursement" align="absmiddle">
                                    </c:when>
                                    <c:otherwise>
                                      <strong>This disbursement may not be cancelled or cancelled and reissued because the disbursement date is before <fmt:formatDate value="${PaymentDetail.lastDisbursementActionDate}" pattern="MM/dd/yyyy"/>.</strong>
                                    </c:otherwise>
                                  </c:choose>
                                </c:when>
                                <c:otherwise>
                                  <strong>This disbursement may not be cancelled or cancelled and reissued because it has not been fully extracted yet.</strong>
                                </c:otherwise>
                              </c:choose>
                            </div>
                         
                        </c:if>
                      </c:when>
                      <c:when test="${(PaymentDetail.paymentGroup.paymentStatus.code == 'HTXE') or (PaymentDetail.paymentGroup.paymentStatus.code == 'HTXN') or (PaymentDetail.paymentGroup.paymentStatus.code == 'HTXB')}">
                        <c:if test="${(SecurityRecord.taxHoldersRole == true)}">
                          
                            <div align="center">
                              <input type="image" name="btnRemoveHold" src="<%= request.getContextPath() + "/pdp/images/button_removehold.gif" %>" alt="Remove Hold" align="absmiddle"></a>
                              <input type="image" name="btnCancel" src="<%= request.getContextPath() + "/pdp/images/button_cancelpayment.gif" %>" alt="Cancel Payment" align="absmiddle"></a>
                            </div>
                          
                        </c:if>
                      </c:when>
                    </c:choose>
                    </th>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </c:if>
        </tbody>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
</table>
<table width="100%">
<tr>
<td>&nbsp;</td>
<td align="right">
  <c:if test="${(SecurityRecord.viewAllRole != true)}">
    <font color="#800000">* Some fields may require masking using Red Asterisks</font>
  </c:if>
</td>
<td>&nbsp;</td>
</tr>
</table>
</html:form>
<c:import url="/backdoor.jsp"/>     
</body>
</html:html>
