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

<kul:page lookup="true" showDocumentInfo="false"
	htmlFormAction="arCustomerAgingReportLookup"
	headerMenuBar="${KualiForm.lookupable.htmlMenuBar}"
	headerTitle="Lookup" docTitle="" transactionalDocument="false">

	<div class="headerarea-small" id="headerarea-small">
	<h1><c:out value="${KualiForm.lookupable.title}" /> <kul:help
		resourceKey="lookupHelpText" altText="lookup help" /></h1>
	</div>

	<kul:enterKey methodToCall="search" />

	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />

	<kul:errors errorTitle="Errors found in Search Criteria:" />

	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
				height="20" /></td>
			<td>
			<div id="lookup" align="center"><br />
			<br />
			<table class="datatable-100" align="center" cellpadding="0"
				cellspacing="0">
				<c:set var="FormName" value="KualiForm" scope="request" />
				<c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
					scope="request" />
				<c:set var="ActionName" value="glModifiedInquiry.do" scope="request" />
				<c:set var="IsLookupDisplay" value="true" scope="request" />

				<kul:rowDisplay rows="${KualiForm.lookupable.rows}"/>			`

				<tr align=center>
					<td height="30" colspan=2 class="infoline"><html:image
						property="methodToCall.search" value="search"
						src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif" styleClass="tinybutton"
						alt="search" title="search" border="0" /> <html:image
						property="methodToCall.clearValues" value="clearValues"
						src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="tinybutton"
						alt="clear" title="clear" border="0" /> <c:if test="${KualiForm.formKey!=''}">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
						<img src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" class="tinybutton"
							border="0" alt="cancel" title="cancel" /> </a>
					</c:if> <!-- Optional extra button -->
				    <c:if
						test="${not empty KualiForm.lookupable.extraButtonSource}">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.rice.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />'  title="cancel">
						<img
							src='<c:out value="${KualiForm.lookupable.extraButtonSource}" />'
							class="tinybutton"  border="0" alt="cancel"/></a>
					</c:if></td> 
				</tr>
			</table>
			</div>
			 

			<div class="right"><logic-el:present name="KualiForm"
				property="formKey">
				<c:if
					test="${KualiForm.formKey!='' && KualiForm.hideReturnLink != true}">
					<a
						href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
					return with no value </a>
				</c:if>
			</logic-el:present></div>
			
			<c:if test="${!empty reqSearchResultsSize }">
			<c:set var="offset" value="0" />
			<display:table class="datatable-100" 
				cellspacing="0" cellpadding="0" name="${reqSearchResults}" id="row"
				export="true" pagesize="100" offset="${offset}"
				requestURI="arCustomerAgingReportLookup.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">
				<c:forEach items="${row.columns}" var="column" varStatus="status">
					<display:column class="${(column.formatter.implementationClass == 'org.kuali.rice.kns.web.format.CurrencyFormatter') ? 'numbercell' : 'inofocell'}" 
						title="${column.columnTitle}" comparator="${column.comparator}" sortable="${('dummyBusinessObject.linkButtonOption' ne column.propertyName) && column.sortable}">
						<c:choose>
							<c:when test="${column.propertyURL != \"\" && param['d-16544-e'] == null}">
								<a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}" target="blank"><c:out value="${column.propertyValue}" /></a>
							</c:when>
							<c:otherwise>
								<c:out value="${column.propertyValue}" />
							</c:otherwise>
						</c:choose>
					</display:column>
				</c:forEach>
			

				<display:footer>
					<th> <span class="grid">TOTALS:</span> </th>
					<td class="infocell">&nbsp; 5 customers </td>
					<td class="infocell">&nbsp; $54,000 </td>
					<td class="infocell">&nbsp; $40,000 </td>
					<td class="infocell">&nbsp; $30,000 </td>
					<td class="infocell">&nbsp; $20,000 </td>
					<td class="infocell">&nbsp; $10,000 </td>
				</display:footer>
			
			</display:table>

			</td>
			</c:if>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" height="20"
				width="20"></td>
		</tr>
	</table>
	<br />
	<br />
</kul:page>
