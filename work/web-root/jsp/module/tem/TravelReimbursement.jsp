<%--
 Copyright 2007-2008 The Kuali Foundation
 
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

<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />
<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="advancePaymentMode" value="${KualiForm.editingMode['advancePaymentEntry']}" scope="request"/>
<c:set var="expenseTaxableMode" value="${KualiForm.editingMode['expenseTaxableEntry']}" scope="request"/>
<c:set var="blanketTravelMode" value="${KualiForm.editingMode['blanketTravelEntry']}" scope="request"/>
<c:set var="blanketTravelViewMode" value="${KualiForm.editingMode['blanketTravelView']}" scope="request"/>
<c:set var="conversionRateEntryMode" value="${KualiForm.editingMode['conversionRateEntry']}" scope="request"/>
<c:set var="frnEntryMode" value="${canEdit && KualiForm.editingMode['frnEntry']}" scope="request" />
<c:set var="wireEntryMode" value="${canEdit && KualiForm.editingMode['wireEntry']}" scope="request" />

<kul:documentPage showDocumentInfo="true"
    documentTypeName="TR"
    htmlFormAction="temTravelReimbursement" renderMultipart="true"
    showTabButtons="true">
	
	<script language="javascript" src="dwr/interface/TravelDocumentService.js"></script> 
	<script language="javascript" src="dwr/interface/TravelReimbursementService.js"></script>
	<script language="javascript" src="dwr/interface/TravelExpenseService.js"></script>
	<script language="javascript" src="scripts/module/tem/common.js"></script>
	<script language="javascript" src="scripts/module/tem/objectInfo.js"></script>
       
    <sys:documentOverview editingMode="${KualiForm.editingMode}" includeBankCode="true"
	  bankProperty="document.financialDocumentBankCode" 
	  bankObjectProperty="document.bank"
	  disbursementOnly="true" />

    <c:if test="${showReports}">
        <tem-tr:reports />
    </c:if>
	
	<script type="text/javascript">
		function clearSpecialHandlingTab() {
		var prefix = "document.travelPayment.";
		var ctrl;
		
		ctrl = kualiElements[prefix + "specialHandlingCityName"]
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingLine1Addr"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingStateCode"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingLine2Addr"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingZipCode"];
		ctrl.value = "";
		
		ctrl = kualiElements[prefix + "specialHandlingCountryCode"];
		ctrl.value = "";
	   }
	</script>
	<sys:paymentMessages />
	
    <tem-tr:tripOverview />
    <tem:travelAdvances />

    <!-- Expenses related -->
    <c:if test="${not empty KualiForm.document.primaryDestinationName}"> 
		<tem:perDiemExpenses />
    </c:if>
    <tem:expenses /> 
    <tem-tr:expenseTotals />

	<tem:specialCircumstances />
	<tem:groupTravel />

    <tem-tr:history />
    
	<tem:contactInfo/>    
	<tem:travelerCertification/>

    <tem:summaryByObjectCode />
    
    <tem:assignAccounts />
     <c:if test="${KualiForm.displayAccountingLines}">
    	<tem:accountingLines />
    </c:if>
	<tem:travelPayment/>
	<tem:travelPaymentPDPStatus travelPaymentProperty="travelPayment" pdpPaymentDocumentType="${KualiForm.document.achCheckDocumentType}" displayCorporateCardExtraction="${KualiForm.document.corporateCardPayable}"/>
    <gl:generalLedgerPendingEntries />
    <tem:relatedDocuments />
	<tem:agencyLinks/>

	<kul:notes attachmentTypesValuesFinderClass="${DataDictionary.TravelReimbursementDocument.attachmentTypesValuesFinderClass}" />  
    
    <kul:adHocRecipients />

    <kul:routeLog />
	<kul:superUserActions />
    <kul:panelFooter />
    
    <sys:documentControls
    transactionalDocument="${documentEntry.transactionalDocument}"
    extraButtons="${KualiForm.extraButtons}" />

      
</kul:documentPage>
