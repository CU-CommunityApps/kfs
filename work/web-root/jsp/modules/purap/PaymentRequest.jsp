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

<kul:documentPage showDocumentInfo="true"
    documentTypeName="PaymentRequestDocument"
    htmlFormAction="purapPaymentRequest" renderMultipart="true"
    showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>
 
    <c:set var="displayInitTab" value="${KualiForm.editingMode['displayInitTab']}" scope="request" />
    
    <kfs:hiddenDocumentFields excludePostingYear="true" />

	<!--  Display hold message if payment is on hold -->
	<c:if test="${KualiForm.paymentRequestDocument.holdIndicator}">	
		<h4>This Payment Request has been Held by <c:out value="${KualiForm.paymentRequestDocument.lastActionPerformedByPersonName}"/></h4>		
	</c:if>
	
	<c:if test="${KualiForm.paymentRequestDocument.paymentRequestedCancelIndicator}">	
		<h4>This Payment Request has been Requested for Cancel by <c:out value="${KualiForm.paymentRequestDocument.lastActionPerformedByPersonName}"/></h4>		
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
	    <kfs:documentOverview editingMode="${KualiForm.editingMode}"
	        includePostingYear="true"
	        fiscalYearReadOnly="true"
	        postingYearAttributes="${DataDictionary.PaymentRequestDocument.attributes}" >
	        
	    	<purap:purapDocumentDetail
	    	documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	    	detailSectionLabel="Payment Request Detail"
	    	paymentRequest="true" />
	    </kfs:documentOverview>
	</c:if>
    
    <c:if test="${KualiForm.editingMode['displayInitTab']}" > 
    	<purap:paymentRequestInit documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	 		 displayPaymentRequestInitFields="true" />
	</c:if>
	
	<c:if test="${not KualiForm.editingMode['displayInitTab']}" >
		< purap:vendor
	        documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}" 
	        displayPurchaseOrderFields="false" displayPaymentRequestFields="true"/>
		<!--  c:out value="${KualiForm.paymentRequestInitiated}" / -->		
	
		<purap:paymentRequestInvoiceInfo documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
	 		 displayPaymentRequestInvoiceInfoFields="true" />        

		<purap:paymentRequestProcessItems 
			documentAttributes="${DataDictionary.PaymentRequestDocument.attributes}"
			itemAttributes="${DataDictionary.PaymentRequestItem.attributes}"
			accountingLineAttributes="${DataDictionary.PaymentRequestAccount.attributes}" />
		   
	    <purap:summaryaccounts
            itemAttributes="${DataDictionary.PaymentRequestItem.attributes}"
    	    documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" 
    	    isPaymentRequest="true"/>  
	
		<purap:relatedDocuments documentAttributes="${DataDictionary.RelatedDocuments.attributes}"/>
           	
	    <purap:paymentHistory documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    	
        <gl:generalLedgerPendingEntries />

	    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/>
	
	    <kul:adHocRecipients />
	    
	    <kul:routeLog />
    	
	</c:if>
	
    <kul:panelFooter />
	<c:set var="extraButtons" value="${KualiForm.extraButtons}" />
  	<kfs:documentControls 
        transactionalDocument="true"  
        extraButtons="${extraButtons}"  
        suppressRoutingControls="${KualiForm.editingMode['displayInitTab']}"
       	
    />
   
</kul:documentPage>
