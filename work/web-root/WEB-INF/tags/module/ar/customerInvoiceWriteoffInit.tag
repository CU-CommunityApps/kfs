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

<c:set var="documentAttributes" value="${DataDictionary.CustomerInvoiceWriteoffDocument.attributes}" />

<kul:tabTop tabTitle="Customer Invoice Writeoff Initiation" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_WRITEOFF_INIT_ERRORS}">
    <div class="tab-container" align=center>
            <h3>Customer Invoice Writeoff Initiation</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Customer Invoice Writeoff Init Section" >
            <tr>
                <th align="right" valign="middle" class="bord-l-b" >
                   <div align="right">
                   	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}" />
                   </div>
                </th>
                <td align="left" valign="middle" class="datacell" style="width: 50%;" >
                   <kul:htmlControlAttribute
                       attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}"
                       property="document.financialDocumentReferenceInvoiceNumber"
                       readOnly="false" />
                </td>
            </tr>
		</table> 
    </div>
</kul:tabTop>
