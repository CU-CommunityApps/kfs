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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CustomerInvoiceDocument"
	htmlFormAction="arCustomerInvoiceDocument" renderMultipart="true"
	showTabButtons="true">

	<kfs:hiddenDocumentFields />
	<kfs:accountingLineScriptImports />

	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	
    <ar:customerInvoiceOrganization documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}"  editingMode="${KualiForm.editingMode}"/>	
	
    <ar:customerInvoiceRecurrenceDetails
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" editingMode="${KualiForm.editingMode}" />
        
    <ar:customerInvoiceGeneral
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" editingMode="${KualiForm.editingMode}" />
        
    <ar:customerInvoiceAddresses
        documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" editingMode="${KualiForm.editingMode}" />        
     
	<c:if test="${!empty KualiForm.editingMode['showReceivableFAU']}">
     <ar:customerInvoiceReceivableAccountingLine
      	documentAttributes="${DataDictionary.CustomerInvoiceDocument.attributes}" editingMode="${KualiForm.editingMode}"
      	receivableValuesMap="${KualiForm.document.valuesMap}"  />
    </c:if>
     
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys:accountingLines>
			<sys:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys:accountingLines>
	</kul:tab>
	    
	<gl:generalLedgerPendingEntries />
		            
	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<c:set var="extraButtons" value="${KualiForm.extraButtons}" scope="request"/>
	
	<kfs:documentControls transactionalDocument="true" extraButtons="${extraButtons}"/>

</kul:documentPage>
