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

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>

<c:set var="documentTypeName" value="EffortCertificationDocument"/>
<c:set var="htmlFormAction" value="effortCertificationRecreate"/>

<c:set var="readOnly" value="${empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true" documentTypeName="${documentTypeName}"
	htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">
    
    <kfs:documentOverview editingMode="${KualiForm.editingMode}" />
    
	<kul:tab tabTitle="Effort Detail" defaultOpen="true"
		tabErrorKey="${EffortConstants.EFFORT_DETAIL_IMPORT_ERRORS}">
		
		<div class="tab-container" align=center>
			<h3>Retrieve Data</h3>
			<er:detailLineImport readOnly="${readOnly}" attributes="${documentAttributes}" />				
		</div>
		
		<div class="tab-container-error"><div class="left-errmsg-tab"><kul:errors keyMatch="${EffortConstants.EFFORT_CERTIFICATION_TAB_ERRORS}"/></div></div>
					
		<div class="tab-container" align=center>			
			<h3>Effort Detail Lines</h3>
			
			<er:detailLines detailLines="${detailLines}" attributes="${detailAttributes}"
				detailFieldNames="chartOfAccountsCode,accountNumber,subAccountNumber,financialObjectCode,sourceChartOfAccountsCode,sourceAccountNumber,positionNumber,effortCertificationCalculatedOverallPercent,effortCertificationOriginalPayrollAmount"
				inquirableUrl="${KualiForm.detailLineFieldInquiryUrl}"
				fieldInfo="${KualiForm.fieldInfo}"/>
		</div>		
	</kul:tab>
	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kfs:documentControls transactionalDocument="false" />
</kul:documentPage>
