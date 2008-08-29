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
<%@ attribute name="hasRelatedCashControlDocument" required="true"
    description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
    description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>
<%@ attribute name="customerAttributes" required="true"
    description="Attributes of Customer according to the data dictionary" %>
    
<c:set var="nonInvoicedPaymentAttributes" value="${DataDictionary['NonInvoiced'].attributes}" scope="request" />
<c:set var="nonInvoicedAddLine" value="${KualiForm.nonInvoicedAddLine}" scope="request" />

    <kul:tab tabTitle="Non-AR" defaultOpen="true"
        tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">

        <div class="tab-container" align="center">
			<SCRIPT type="text/javascript">
			    var kualiForm = document.forms['KualiForm'];
			    var kualiElements = kualiForm.elements;
			</SCRIPT>
            
			<input type="hidden" value="${document.postingYear}" name="nonInvoicedAddLine.universityFiscalYear"/>
            
            <c:choose>
                <c:when test="${!isCustomerSelected}">
                    No Customer Selected
                </c:when>
                <c:otherwise>
        
            <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
                <tr>
                    <kul:htmlAttributeHeaderCell literalLabel=" "/>             
                    <kul:htmlAttributeHeaderCell literalLabel="Chart"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Account"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Sacc"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Object"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Sobj"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Project"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Amount"/>
                    <kul:htmlAttributeHeaderCell literalLabel="Action"/>
                </tr>
                <tr>
                    <td>
                        add
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            onblur="loadChartInfo(this.name, 'nonInvoicedAddLine.chart.name')"
                            attributeEntry="${nonInvoicedPaymentAttributes.chartOfAccountsCode}"
                            property="nonInvoicedAddLine.chartOfAccountsCode"/>
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Chart"
                            autoSearch="true"
                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode" 
                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode"/>
                        <div id="nonInvoicedAddLine.chart.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.chartOfAccounts.name"/></div>
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            onblur="loadAccountInfo(this.name, 'nonInvoicedAddLine.account.name')"
                            attributeEntry="${nonInvoicedPaymentAttributes.accountNumber}"
                            property="nonInvoicedAddLine.accountNumber"/>
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account" 
                            autoSearch="true"
                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.accountNumber:accountNumber"
                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,accountNumber:nonInvoicedAddLine.accountNumber" />
                        <div id="nonInvoicedAddLine.account.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.account.name"/></div>
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            onblur="loadSubAccountInfo(this.name, 'nonInvoicedAddLine.subAccount.name')"
                            attributeEntry="${nonInvoicedPaymentAttributes.subAccountNumber}"
                            property="nonInvoicedAddLine.subAccountNumber"/>
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubAccount" 
                            autoSearch="true"
                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.accountNumber:accountNumber,nonInvoicedAddLine.subAccountNumber:subAccountNumber"
                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,accountNumber:nonInvoicedAddLine.accountNumber,subAccountNumber:nonInvoicedAddLine.subAccountNumber" />
                        <div id="nonInvoicedAddLine.subAccount.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.subAccountNumber"/></div>
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            onblur="loadObjectInfo('${KualiForm.document.postingYear}', '', '', this.name, 'nonInvoicedAddLine.objectCode.name')"
                            attributeEntry="${nonInvoicedPaymentAttributes.financialObjectCode}"
                            property="nonInvoicedAddLine.financialObjectCode"/>
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" 
                            autoSearch="true"
                            lookupParameters="nonInvoicedAddLine.financialObjectCode:financialObjectCode,nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode"
                            fieldConversions="financialObjectCode:nonInvoicedAddLine.financialObjectCode,chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode" />
                        <div id="nonInvoicedAddLine.objectCode.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.financialObject.financialObjectCodeName"/></div>
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            onblur="loadSubObjectInfo('${KualiForm.document.postingYear}', this.name, 'nonInvoicedAddLine.subObjectCode.name')"
                            attributeEntry="${nonInvoicedPaymentAttributes.financialSubObjectCode}"
                            property="nonInvoicedAddLine.financialSubObjectCode"/>
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.SubObjCd" 
                            autoSearch="true"
                            lookupParameters="nonInvoicedAddLine.financialSubObjectCode:financialSubObjectCode,nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.objectCode:financialObjectCode"
                            fieldConversions="financialSubObjectCode:nonInvoicedAddLine.financialSubObjectCode,chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,financialObjectCode:nonInvoicedAddLine.objectCode" />
                        <div id="nonInvoicedAddLine.subObjectCode.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.financialSubObjectCode"/></div>
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            onblur="loadProjectInfo(this.name, 'nonInvoicedAddLine.projectCode.name')"
                            attributeEntry="${nonInvoicedPaymentAttributes.projectCode}"
                            property="nonInvoicedAddLine.projectCode"/>
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" 
                            autoSearch="true"
                            lookupParameters="nonInvoicedAddLine.chartOfAccountsCode:chartOfAccountsCode,nonInvoicedAddLine.projectCode:code"
                            fieldConversions="chartOfAccountsCode:nonInvoicedAddLine.chartOfAccountsCode,code:nonInvoicedAddLine.projectCode" />
                        <div id="nonInvoicedAddLine.projectCode.name.div"><bean:write name="KualiForm" property="nonInvoicedAddLine.project.name"/></div>
                    </td>
                    <td>
                        <kul:htmlControlAttribute
                            attributeEntry="${nonInvoicedPaymentAttributes.financialDocumentLineAmount}"
                            property="nonInvoicedAddLine.financialDocumentLineAmount"/>
                    </td>
                    <td><html:image property="methodToCall.addNonAr"
                        src="${ConfigProperties.externalizable.images.url}tinybutton-add1.gif"
                        alt="Add" title="Add" styleClass="tinybutton" /></td>
                </tr>
                
                <logic:iterate id="nonInvoicedPayment" name="KualiForm"
	                   property="paymentApplicationDocument.nonInvoicedPayments" indexId="ctr">
                    <html:hidden property="paymentApplicationDocument.nonInvoicedPayment[${ctr}].financialDocumentLineNumber" />
	                <html:hidden property="paymentApplicationDocument.nonInvoicedPayment[${ctr}].documentNumber" />
	                <html:hidden property="paymentApplicationDocument.nonInvoicedPayment[${ctr}].versionNumber" />
	                <html:hidden property="paymentApplicationDocument.nonInvoicedPayment[${ctr}].objectId" />
                    <tr>
                        <td>
                            ${nonInvoicedPayment.financialDocumentLineNumber}
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.chartOfAccountsCode}"
                                property="document.nonInvoicedPayment[${ctr }].chartOfAccountsCode"/>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.accountNumber}"
                                property="paymentApplicationDocument.nonInvoicedPayment[${ctr }].accountNumber"/>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.subAccountNumber}"
                                property="paymentApplicationDocument.nonInvoicedPayment[${ctr }].subAccountNumber"/>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.financialObjectCode}"
                                property="paymentApplicationDocument.nonInvoicedPayment[${ctr }].financialObjectCode"/>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.financialSubObjectCode}"
                                property="paymentApplicationDocument.nonInvoicedPayment[${ctr }].financialSubObjectCode"/>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.projectCode}"
                                property="paymentApplicationDocument.nonInvoicedPayment[${ctr }].projectCode"/>
                        </td>
                        <td>
                            <kul:htmlControlAttribute
                                attributeEntry="${nonInvoicedPaymentAttributes.financialDocumentLineAmount}"
                                property="paymentApplicationDocument.nonInvoicedPayment[${ctr }].financialDocumentLineAmount"/>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                </logic:iterate>
                <tr>
                    <th colspan='6'>&nbsp;</th>
                    <kul:htmlAttributeHeaderCell literalLabel="Non-AR Total"/>
                    <td>${KualiForm.nonArTotal}</td>
                    <td>&nbsp;</td>
                </tr>
            </table>
            </c:otherwise>
            </c:choose>
        </div>
    </kul:tab>