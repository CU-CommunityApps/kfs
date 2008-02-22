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

<channel:portalChannelTop channelTitle="Effort Certification" />
<div class="body">
	<ul class="chan">
		<!-- 			
		<li>
			<portal:portalLink displayTitle="true" title="Detail Table Search"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationDocumentForLookup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<li>
			<portal:portalLink displayTitle="true" title="Detail Build Lookup"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationDocumentBuild&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		-->
		
		<li>
			<portal:portalLink displayTitle="true" title="Effort Build"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationDetailBuild&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>	
		
		<li>
			<portal:portalLink displayTitle="true" title="Effort Certification Report Definition"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationReportDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		 
		<li>
			<portal:portalLink displayTitle="true" title="Report Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationReportType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<li>
			<portal:portalLink displayTitle="true" title="Report Status Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationPeriodStatusCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<li>
			<portal:portalLink displayTitle="true" title="Report Earn Paygroup"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.EffortCertificationReportEarnPaygroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
	
	<strong>Reports</strong><br />
	
	<ul class="chan">		
		
		<li>
			<portal:portalLink displayTitle="true" title="Outstanding Reports"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.effort.bo.OutstandingReportsByOrganization&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<!-- 
			<portal:portalLink displayTitle="true" title="Effort Certification Document" url="effortEffortCertification.do?methodToCall=docHandler&command=initiate&docTypeName=EffortCertificationDocument" />
		</li>-->
		
	</ul>
	
	<strong>Document</strong><br />	
	<ul class="chan">		
		<li> 
			<portal:portalLink displayTitle="true" title="Effort Certification Recreate" url="effortCertificationRecreate.do?methodToCall=docHandler&command=initiate&docTypeName=EffortCertificationDocument" />
		</li>
		
	</ul>
</div>
<channel:portalChannelBottom />
