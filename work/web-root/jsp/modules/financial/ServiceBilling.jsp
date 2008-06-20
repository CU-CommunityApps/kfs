<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="ServiceBillingDocument"
	htmlFormAction="financialServiceBilling" renderMultipart="true"
	showTabButtons="true">
	<html:hidden property="document.nextItemLineNumber" />
	<kfs:hiddenDocumentFields />

	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		extraSourceRowFields="financialDocumentLineDescription"
		extraTargetRowFields="financialDocumentLineDescription" />
	<fin:items editingMode="${KualiForm.editingMode}" />
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kfs:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
