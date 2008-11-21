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

<kul:page headerTitle="Customer Invoice Writeoff Summary" transactionalDocument="false" showDocumentInfo="false" htmlFormAction="arCustomerInvoiceWriteoffLookupSummary" docTitle="Customer Invoice Writeoff Summary">
	
	<table width="100%" border="0"><tr><td>	
	  <kul:errors keyMatch="*"/>
	</td></tr></table>  
	</br>

	<ar:customerInvoiceWriteoffSummaryResults customerInvoiceDocumentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"/>
	
	<kul:panelFooter />

	<div id="globalbuttons" class="globalbuttons">
		<c:if test="${KualiForm.sentToBatch}"> 
		<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_return.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="claim" alt="claim"/>
		</c:if>
		<c:if test="${!KualiForm.sentToBatch}">
		<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_create.gif" styleClass="globalbuttons" property="methodToCall.createCustomerInvoiceWriteoffs" title="claim" alt="claim"/>
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="cancel" alt="cancel"/>
		</c:if>
	</div>
</kul:page>