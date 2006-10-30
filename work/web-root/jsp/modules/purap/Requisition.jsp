<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/jsp/modules/purap/Requisition.jsp,v $
 
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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRequisitionDocument"
	htmlFormAction="purapRequisition" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields excludePostingYear="true" />

    <purap:hiddenPurapFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true"
        postingYearAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" >

    	<purap:requisitiondetail
	    	documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />
    </kul:documentOverview>

    <purap:vendor
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}"
        displayRequisitionFields="true" />

    <purap:items
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:paymentinfo
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:delivery
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.KualiRequisitionDocument.attributes}" />

	<purap:statushistory 
		documentAttributes="${DataDictionary.RequisitionStatusHistory.attributes}" />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls transactionalDocument="true" />

</kul:documentPage>
