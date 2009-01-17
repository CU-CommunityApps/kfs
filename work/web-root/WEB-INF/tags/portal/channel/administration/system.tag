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

<channel:portalChannelTop channelTitle="System" />
<div class="body">
	<strong>Identity</strong>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Person" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.Person&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Group (coming soon)" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.group.impl.KimGroupImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Role" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.role.impl.KimRoleImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Permission" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.role.impl.KimPermissionImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Responsibility" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	<strong>Locations</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Campus" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.Campus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Country" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.Country&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="County" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.County&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Postal Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.PostalCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="State" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.State&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	<strong>Reference</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Address Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.AddressTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Affiliation Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.AffiliationTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Campus Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.CampusType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Citizenship Status" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.CitizenshipStatusImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Email Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.EmailTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Employment Status" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.EmploymentStatusImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Employment Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.EmploymentTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Entity Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.EntityTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="External Identifier Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Name Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.EntityNameTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Phone Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.reference.impl.PhoneTypeImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
</div>
<channel:portalChannelBottom />       

