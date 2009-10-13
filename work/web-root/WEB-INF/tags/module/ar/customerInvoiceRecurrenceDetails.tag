<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<script type='text/javascript' src="dwr/interface/CustomerService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/ar/customerObjectInfo.js"></script>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:set var="customerInvoiceRecurrenceAttributes" value="${DataDictionary.CustomerInvoiceRecurrenceDetails.attributes}" />

<kul:tab tabTitle="Recurrence Details" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_RECURRENCE_DETAILS_ERRORS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="4" class="subhead">Recurrence Details</td>
            </tr>
			<tr>
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceIntervalCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceIntervalCode}" 
                       property="document.customerInvoiceRecurrenceDetails.documentRecurrenceIntervalCode" 
                       readOnly="${readOnly}" />
                </td>          
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentTotalRecurrenceNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute 
                       attributeEntry="${customerInvoiceRecurrenceAttributes.documentTotalRecurrenceNumber}" 
                       property="document.customerInvoiceRecurrenceDetails.documentTotalRecurrenceNumber" 
                       readOnly="${readOnly}" />
                </td>			
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceBeginDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
     		       	<c:choose>
			            <c:when test="${readOnly}">
			                <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceBeginDate}" 
			                	property="document.customerInvoiceRecurrenceDetails.documentRecurrenceBeginDate" readOnly="${readOnly}" />
			            </c:when>
                        <c:otherwise>
		                    <kul:dateInput attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceBeginDate}"
                       			property="document.customerInvoiceRecurrenceDetails.documentRecurrenceBeginDate" />
			            </c:otherwise>
					</c:choose>
                </td>			          
            </tr>    
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceEndDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
     		       	<c:choose>
			            <c:when test="${readOnly}">
			                <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceEndDate}" 
			                	property="document.customerInvoiceRecurrenceDetails.documentRecurrenceEndDate" readOnly="${readOnly}" />
			            </c:when>
                        <c:otherwise>
		                    <kul:dateInput attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceEndDate}"
                       			property="document.customerInvoiceRecurrenceDetails.documentRecurrenceEndDate" />
			            </c:otherwise>
					</c:choose>
                </td>			
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentInitiatorUserIdentifier}" /></th>
				<td class="grid" width="25%">
				<kul:checkErrors keyMatch="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName" />
				<kul:user userIdFieldName="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName" universalIdFieldName="document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier" userNameFieldName="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name" label="User" 
				lookupParameters="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName:principalName,document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier:principalId,document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name:name" 
				fieldConversions="principalName:document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName,principalId:document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier,name:document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name" 
				userId="${KualiForm.document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName}" universalId="${KualiForm.document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier}" userName="${KualiForm.document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name}" renderOtherFields="true" hasErrors="${hasErrors}" readOnly="${readOnly}" />
				</td>
            </tr>    
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.active}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.active}" 
                       property="document.customerInvoiceRecurrenceDetails.active"
                       readOnly="${readOnly}"/>
                </td>
				<th align=right valign=middle class="bord-l-b">
                    &nbsp;
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td> 
            </tr>
        </table>
    </div>
</kul:tab>

