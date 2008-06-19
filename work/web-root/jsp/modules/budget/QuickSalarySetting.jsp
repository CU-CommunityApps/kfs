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

<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />
<c:set var="readOnly" value="false"/>

<kul:page showDocumentInfo="false" docTitle="Quick Salary Setting" transactionalDocument="false"
	htmlFormAction="budgetQuickSalarySetting" renderMultipart="true" showTabButtons="true">
	
	<c:forEach items="${KualiForm.editingMode}" var="mode">
  		<html:hidden property="editingMode(${mode.key})"/>
	</c:forEach>
    
	<kul:tabTop tabTitle="Quick Salary Setting" defaultOpen="true" tabErrorKey="${KFSConstants.BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS}">
		<div class="tab-container" align=center>
			<bc:expenditureSalaryLine/>	
			
			<br/>
						
			<bc:expenditureSalaryLineDetails readOnly="${readOnly}"/>
		</div>
	</kul:tabTop>

	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_save.gif" 
	        	styleClass="globalbuttons" property="methodToCall.save" title="save" alt="save"/>
	    </c:if>
	    
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" 
        	styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>
</kul:page>