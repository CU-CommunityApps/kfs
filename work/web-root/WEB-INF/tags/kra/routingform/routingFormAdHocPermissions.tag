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

<%@ attribute name="editingMode" required="false" type="java.util.Map"%>

<%-- derive displayReadOnly value --%>
<c:set var="displayReadOnly" value="false" />
<c:set var="routingFormAdHocPersonAttributes" value="${DataDictionary.RoutingFormAdHocPerson.attributes}" />
<c:set var="routingFormAdHocOrgAttributes" value="${DataDictionary.RoutingFormAdHocOrg.attributes}" />
<c:set var="routingFormAdHocWorkgroupAttributes" value="${DataDictionary.RoutingFormAdHocWorkgroup.attributes}" />

<c:if test="${!empty editingMode['viewOnly']}" >
    <c:set var="displayReadOnly" value="true" />
</c:if>

<kul:tabTop tabTitle="Ad Hoc Permissions" defaultOpen="false" tabErrorKey="${Constants.AD_HOC_ROUTE_ERRORS}">
   	<div class="tab-container" align=center>     
			<h3>Ad Hoc Permissions</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit ad hoc permissions">
		<%-- first do the persons --%>
        	<kul:displayIfErrors keyMatch="${Constants.AD_HOC_ROUTE_PERSON_ERRORS}">
			  	<tr>
        			<th colspan=3>
            			<kul:errors keyMatch="${Constants.AD_HOC_ROUTE_PERSON_ERRORS}" />
        			</th>
    		  	</tr>    
		  	</kul:displayIfErrors>
            <tr>
               	<td colspan=6 class="tab-subhead">Ad Hoc People:</td>
            </tr>
          	<tr>
            	<kul:htmlAttributeHeaderCell
                 attributeEntry="${DataDictionary.AdHocRoutePerson.attributes.id}"
                 scope="col"
                />
               	<kul:htmlAttributeHeaderCell
                 attributeEntry="${DataDictionary.Chart.attributes.chartOfAccountsCode}"
                 scope="col"
                 hideRequiredAsterisk="true"
                />
                <kul:htmlAttributeHeaderCell
                 attributeEntry="${DataDictionary.Organization.attributes.organizationCode}"
                 scope="col"
                 hideRequiredAsterisk="true"
                />
                <kul:htmlAttributeHeaderCell
                 attributeEntry="${routingFormAdHocPersonAttributes.permissionCode}"
                 scope="col"
                />
                <c:if test="${not displayReadOnly}">
               		<kul:htmlAttributeHeaderCell
                   	 literalLabel="Actions"
                   	 scope="col"
                   	/>
            	</c:if>
            </tr>
			<c:if test="${!displayReadOnly}">
               	<tr>
                   	<td class="infoline">
                    	<kul:user userIdFieldName="newAdHocRoutePerson.id" 
                    			  userId="${KualiForm.newAdHocRoutePerson.id}" 
                    			  universalIdFieldName=""
                    			  universalId=""
                    			  userNameFieldName="newAdHocRoutePerson.name"
                    			  userName="${KualiForm.newAdHocRoutePerson.name}"
                    			  readOnly="${displayReadOnly}" 
                    			  renderOtherFields="true"
                    			  fieldConversions="principalName:newAdHocRoutePerson.id,name:newAdHocRoutePerson.name" 
                    			  lookupParameters="newAdHocRoutePerson.id:principalName" />
                   	</td>
                   	<td class="infoline"><div align=center>--</div></td>
                   	<td class="infoline"><div align=center>--</div></td>
                   	<td class="infoline">
						<div align="left">
							<kul:htmlControlAttribute property="newAdHocPerson.permissionCode" attributeEntry="${routingFormAdHocPersonAttributes.permissionCode}"/>
						</div>
					</td>
					<c:if test="${not displayReadOnly}">
                   		<td class="infoline" ><div align=center>
                       		<html:image property="methodToCall.insertAdHocPerson" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="Insert Additional Ad Hoc Person" alt="Insert Additional Ad Hoc Person" styleClass="tinybutton"/></div>
                   		</td>
                   	</c:if>
               	</tr>
			</c:if>
			<c:forEach items="${KualiForm.document.routingFormAdHocPeople}" var="person" varStatus="status">
				<tr>
                    <td class="datacell center">
                    	<div align=left>${person.user.principalName}</div>
                    	<html:hidden property="document.routingFormAdHocPerson[${status.index}].user.principalName" />
                    </td>
                    <td>${person.user.campusCode}<html:hidden property="document.routingFormAdHocPerson[${status.index}].user.campusCode" /></td>
                    <td>${person.primaryDepartmentCode}</td>
                    <td>
                    	<c:if test="${displayReadOnly}"><html:hidden property="document.routingFormAdHocPerson[${status.index}].permissionCode" /></c:if>
                    	<kul:htmlControlAttribute property="document.routingFormAdHocPerson[${status.index}].permissionCode" attributeEntry="${routingFormAdHocPersonAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].principalId" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].addedByPerson" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].personAddedTimestamp" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].objectId" />
						<html:hidden property="document.routingFormAdHocPerson[${status.index}].versionNumber" />
					</td>
					<c:if test="${not displayReadOnly}">
	                    <td class="datacell center"><div align=center>
                           	<html:image property="methodToCall.deleteAdHocPerson.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="delete Ad Hoc Person" alt="delete Ad Hoc Person" styleClass="tinybutton"/></div>
                    	</td>
                    </c:if>
                </tr>
			</c:forEach>
			<%-- next do the orgs --%>
            <kul:displayIfErrors keyMatch="newAdHocOrg">
				<tr>
        			<th colspan=3>
            			<kul:errors keyMatch="newAdHocOrg" />
        			</th>
    		  	</tr>    
		  	</kul:displayIfErrors>
            <tr>
				<td colspan=6 class="tab-subhead">Ad Hoc Orgs:</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell
                 literalLabel="* Chart/Org"
                 scope="col"
                 forceRequired="true"
                 colspan="3"
                />
				<kul:htmlAttributeHeaderCell
                 attributeEntry="${routingFormAdHocOrgAttributes.permissionCode}"
                 scope="col"
                />
				<c:if test="${not displayReadOnly}"><th scope=col>Actions</th></c:if>
			</tr>
			
			<c:if test="${not displayReadOnly}">
			<tr>
				<td nowrap class="infoline" colspan="3">
					<c:choose>
						<c:when test="${empty KualiForm.newAdHocOrg.fiscalCampusCode}">(select by org)</c:when>
						<c:otherwise>
							${KualiForm.newAdHocOrg.fiscalCampusCode}/${KualiForm.newAdHocOrg.primaryDepartmentCode}
							<html:hidden property="newAdHocOrg.fiscalCampusCode"/>
							<html:hidden property="newAdHocOrg.primaryDepartmentCode"/>
						</c:otherwise>
					</c:choose>
					&nbsp;&nbsp;
					<kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="chartOfAccounts.chartOfAccountsCode:newAdHocOrg.fiscalCampusCode,organizationCode:newAdHocOrg.primaryDepartmentCode" />
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
				<td class="infoline">
					<div align="left">
						<kul:htmlControlAttribute property="newAdHocOrg.permissionCode" attributeEntry="${routingFormAdHocOrgAttributes.permissionCode}"/>
					</div>
				</td>
				<td class="infoline">
					<div align=center>
						<html:image alt="Add Ad Hoc Organization Permission" title="Add Ad Hoc Organization Permission" property="methodToCall.insertAdHocOrg" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" />
					</div>
				</td>
			</tr>
			</c:if>
			
			<c:forEach items="${KualiForm.document.routingFormAdHocOrgs}" var="org" varStatus="status">
				<tr>
					<td colspan="3">${org.fiscalCampusCode}/${org.primaryDepartmentCode}</td>
					<td>
						<c:if test="${displayReadOnly}"><html:hidden property="document.routingFormAdHocOrg[${status.index}].permissionCode" /></c:if>
						<kul:htmlControlAttribute property="document.routingFormAdHocOrg[${status.index}].permissionCode" attributeEntry="${routingFormAdHocOrgAttributes.permissionCode}" readOnly="${displayReadOnly}"/>
					</td>
					<c:if test="${not displayReadOnly}">
						<td>
							<div align="center">
								<html:image property="methodToCall.deleteOrg.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" title="delete organization" alt="delete organization" disabled="${displayReadOnly}"/>
							</div>
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].fiscalCampusCode" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].primaryDepartmentCode" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].addedByPerson" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].personAddedTimestamp" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].objectId" />
							<html:hidden property="document.routingFormAdHocOrg[${status.index}].versionNumber" />
						</td>
					</c:if>
				</tr>
			</c:forEach>
        </table>
    </div>
</kul:tabTop>

