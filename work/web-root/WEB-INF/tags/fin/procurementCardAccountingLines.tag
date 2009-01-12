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

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map"
              description="Map of Accounts which this user is allowed to edit" %>
<%@ attribute name="editableFields" required="false" type="java.util.Map"
              description="Map of accounting line fields which this user is allowed to edit" %>

<c:forEach items="${editableAccounts}" var="account">
  <html:hidden property="editableAccounts(${account.key})" value="${account.key}"/>
</c:forEach>

<c:forEach items="${editableFields}" var="field">
  <html:hidden property="accountingLineEditableFields(${field.key})"/>
</c:forEach>

<c:set var="columnCountUntilAmount" value="8" />
<c:set var="columnCount" value="${columnCountUntilAmount + 1 + (KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] ? 1 : 0)}" />
<c:set var="accountingLineAttributes" value="${DataDictionary['TargetAccountingLine'].attributes}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN},document.transactionEntries*">
  <c:set var="transactionAttributes" value="${DataDictionary.ProcurementCardTransactionDetail.attributes}" />
  <c:set var="vendorAttributes" value="${DataDictionary.ProcurementCardVendor.attributes}" />
  <c:set var="cardAttributes" value="${DataDictionary.ProcurementCardHolder.attributes}" />
	
  <div class="tab-container" align=center>
  <c:set var="totalNewTargetCtr" value="0"/>
  <c:set var="baseCtr" value="0"/>
  <logic:iterate indexId="ctr" name="KualiForm" property="document.transactionEntries" id="currentTransaction">
    <table cellpadding="0" class="datatable" summary="Transaction Details">
       <html:hidden write="false" property="document.transactionEntries[${ctr}].documentNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].financialDocumentTransactionLineNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPostingDate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionOriginalCurrencyCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionBillingCurrencyCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionOriginalCurrencyAmount"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionCurrencyExchangeRate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionSettlementAmount"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionSalesTaxAmount"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionTaxExemptIndicator"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPurchaseIdentifierIndicator"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPurchaseIdentifierDescription"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionUnitContactName"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionTravelAuthorizationCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionPointOfSaleCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionCycleStartDate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].transactionCycleEndDate"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].versionNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.documentNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.financialDocumentTransactionLineNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorName"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorLine1Address"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorLine2Address"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorCityName"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorStateCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorZipCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.visaVendorIdentifier"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorOrderNumber"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.transactionMerchantCategoryCode"/>
       <html:hidden write="false" property="document.transactionEntries[${ctr}].procurementCardVendor.versionNumber"/>
       
       <%-- write out source (actually from lines) as hiddens since they are not displayed but need repopulated --%>
       <logic:iterate indexId="tCtr" name="KualiForm" property="document.transactionEntries[${ctr}].sourceAccountingLines" id="currentLine">
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].documentNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].financialDocumentTransactionLineNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].sequenceNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].versionNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].chartOfAccountsCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].accountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].postingYear"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].financialObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].balanceTypeCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].amount"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].subAccountNumber"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].financialSubObjectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].projectCode"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].organizationReferenceId"/>
        <html:hidden write="false" property="document.transactionEntries[${ctr}].sourceAccountingLines[${tCtr}].overrideCode"/>
      </logic:iterate>
                                                                                           
       <fin:subheadingWithDetailToggleRow columnCount="4" subheading="Transaction #${currentTransaction.transactionReferenceNumber}"/>
	      <tr>
	        <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.transactionCreditCardNumber}" readOnly="true"/></div></th>
	        <td>
	          <kul:inquiry boClassName="org.kuali.kfs.fp.businessobject.ProcurementCardHolder" 
               keyValues="documentNumber=${currentTransaction.documentNumber}" 
               render="true">
	            <kul:htmlControlAttribute attributeEntry="${cardAttributes.transactionCreditCardNumber}" property="document.procurementCardHolder.transactionCreditCardNumber"
	             readOnly="true" encryptValue="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" displayMaskValue="****************" />
	          </kul:inquiry>
	        </td>
	      </tr>
	      <tr>
	        <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${cardAttributes.cardHolderName}" readOnly="true"/></div></th>
	        <td><kul:htmlControlAttribute attributeEntry="${cardAttributes.cardHolderName}" property="document.procurementCardHolder.cardHolderName" readOnly="true"/></td>
            <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionTotalAmount}"/></div></th>
            <td valign=top><kul:htmlControlAttribute attributeEntry="${transactionAttributes.transactionTotalAmount}" property="document.transactionEntries[${ctr}].transactionTotalAmount" readOnly="true"/></td>
	     </tr>
       <tr>
          <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionDate}"/></div></th>
          <td valign=top><html:hidden write="true" property="document.transactionEntries[${ctr}].transactionDate"/></td>
          <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${transactionAttributes.transactionReferenceNumber}"/></div></th>
          <td valign=top>
            <kul:inquiry boClassName="org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail" 
               keyValues="documentNumber=${currentTransaction.documentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" 
               render="true">
              <html:hidden write="true" property="document.transactionEntries[${ctr}].transactionReferenceNumber"/>
            </kul:inquiry>
          </td>
       </tr>   
       <tr>  
          <th> <div align="right"><kul:htmlAttributeLabel attributeEntry="${vendorAttributes.vendorName}"/></div></th> 
          <td valign=top>
            <kul:inquiry boClassName="org.kuali.kfs.fp.businessobject.ProcurementCardVendor" 
               keyValues="documentNumber=${currentTransaction.documentNumber}&financialDocumentTransactionLineNumber=${currentTransaction.financialDocumentTransactionLineNumber}" 
               render="true">
              <html:hidden write="true" property="document.transactionEntries[${ctr}].procurementCardVendor.vendorName"/>
            </kul:inquiry>  
          </td>
          <th colspan="2"> <div align="left">
          <c:if test="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
            <a href="${KualiForm.disputeURL}" target="_blank"><img src="${ConfigProperties.externalizable.images.url}buttonsmall_dispute.gif"/></a>
          </c:if>
          </div></th>
       </tr>   
    </table>   
	
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<sys:accountingLineGroup newLinePropertyName="newTargetLines[${totalNewTargetCtr}]" collectionPropertyName="document.transactionEntries[${ctr}].targetAccountingLines" collectionItemPropertyName="document.transactionEntries[${ctr}].targetAccountingLine" attributeGroupName="target" />
	</table>
    
    <br/>
    <c:set var="totalNewTargetCtr" value="${totalNewTargetCtr+1}"/>
   </logic:iterate> 
  </div>
  <SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>
</kul:tab>
