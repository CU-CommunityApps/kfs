<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	
<kul:documentPage showDocumentInfo="true"
	documentTypeName="SecurityTransferDocument"
	htmlFormAction="endowSecurityTransferDocument" renderMultipart="true" 
	showTabButtons="true">

    <c:if test="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

	<endow:endowmentDocumentOverview editingMode="${KualiForm.editingMode}" 
	                                 endowDocAttributes="${DataDictionary.SecurityTransferDocument.attributes}" />
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />
     
    <endow:endowmentTransactionalDocumentDetails
         documentAttributes="${DataDictionary.SecurityTransferDocument.attributes}" 
         readOnly="${readOnly}" 
         subTypeReadOnly="true"
         tabTitle="Security Transfer Details"
         headingTitle="Security Transfer Details"
         summaryTitle="Security Transfer Details"
         />

    <endow:endowmentSecurityTransactionDetails showTarget="false" showSource="true" showRegistrationCode="true" openTabByDefault="true" />  
                  
	<endow:endowmentTransactionLinesSection hasSource="true" hasTarget="true" hasUnits="true"/> 
                   
    <endow:endowmentTaxLotLine 
    	documentAttributes="${DataDictionary.EndowmentTransactionTaxLotLine.attributes}" 
    	isSource="true"
    	isTarget="true"
    	displayGainLoss="false"
    	readOnly="${readOnly}"
    	showSourceDeleteButton="true"
    	showTargetDeleteButton="false"/>

 	<kul:notes /> 

	<kul:routeLog />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
