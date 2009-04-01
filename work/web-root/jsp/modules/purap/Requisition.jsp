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
	documentTypeName="RequisitionDocument"
	htmlFormAction="purapRequisition" renderMultipart="true"
	showTabButtons="true">

    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
 
	<sys:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true"
        fiscalYearReadOnly="${not KualiForm.editingMode['allowPostingYearEntry']}"
        postingYearAttributes="${DataDictionary.RequisitionDocument.attributes}" >

    	<purap:purapDocumentDetail
	    	documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
	    	detailSectionLabel="Requisition Detail"
	    	editableFundingSource="true" />
    </sys:documentOverview>

    <purap:delivery
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}" 
        showDefaultBuildingOption="true" />

    <purap:vendor
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" />
 
    <purap:puritems itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	accountingLineAttributes="${DataDictionary.RequisitionAccount.attributes}" 
    	displayRequisitionFields="true"/>
 	<purap:purCams documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
		itemAttributes="${DataDictionary.RequisitionItem.attributes}" 
		camsItemAttributes="${DataDictionary.RequisitionCapitalAssetItem.attributes}" 
		camsSystemAttributes="${DataDictionary.RequisitionCapitalAssetSystem.attributes}"
		camsAssetAttributes="${DataDictionary.RequisitionItemCapitalAsset.attributes}"
		camsLocationAttributes="${DataDictionary.RequisitionCapitalAssetLocation.attributes}" 
		isRequisition="true" />


    <purap:paymentinfo
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" />
         
    <purap:summaryaccounts
        itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />

    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    
    <purap:paymentHistory
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	            
	<kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />
	
	<c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	

	<sys:documentControls transactionalDocument="true" extraButtons="${extraButtons}" />

</kul:documentPage>
