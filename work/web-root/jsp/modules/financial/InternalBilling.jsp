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
	htmlFormAction="financialInternalBilling"
	documentTypeName="InternalBillingDocument" renderMultipart="true"
	showTabButtons="true">

	<kfs:hiddenDocumentFields />

	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys:accountingLines>
			<sys:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
			<sys:accountingLineGroup newLinePropertyName="newTargetLine" collectionPropertyName="document.targetAccountingLines" collectionItemPropertyName="document.targetAccountingLine" attributeGroupName="target"/>
		</sys:accountingLines>
	</kul:tab>
	<fin:items editingMode="${KualiForm.editingMode}" />
	
	<c:set var="readOnly" value="${not empty KualiForm.editingMode['viewOnly']}" />
	<fin:capitalAssetEditTab readOnly="${readOnly}"/>
	
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kfs:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
