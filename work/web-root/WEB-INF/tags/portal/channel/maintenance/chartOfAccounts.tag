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

<channel:portalChannelTop channelTitle="Chart of Accounts" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Account Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AcctType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Accounting Period" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AccountingPeriod&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="AICPA Function" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AicpaFunction&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Balance Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.BalanceTyp&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Basic Accounting Category" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.BasicAccountingCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Aggregation Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.BudgetAggregationCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Recording Level" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.BudgetRecordingLevel&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Building" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.Building&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Campus" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.Campus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Campus Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.CampusType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Chart" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.Chart&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>	    
        <li><portal:portalLink displayTitle="true" title="Country" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.Country&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Document Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.DocumentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Federal Function" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.FederalFunction&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>				
        <li><portal:portalLink displayTitle="true" title="Federal Funded Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.FederalFundedCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Financial Reporting Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ReportingCodes&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>       
        <li><portal:portalLink displayTitle="true" title="Fund Group" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.FundGroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Higher Education Function" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.HigherEdFunction&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Mandatory Transfer Elimination" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.MandatoryTransferEliminationCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Object Consolidation" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjectCons&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Object Level" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjLevel&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Object Sub-Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjSubTyp&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>					
        <li><portal:portalLink displayTitle="true" title="Object Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjectType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Offset Account" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.OffsetAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Offset Definition" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OffsetDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Reversion" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OrganizationReversion&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Reversion Category" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OrganizationReversionCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OrgType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Origination Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.OriginationCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Postal Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.PostalZipCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Responsibility Center" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ResponsibilityCenter&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Restricted Status" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.RestrictedStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Room" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.Room&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="State" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.State&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Sub-Fund Group" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.SubFundGroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Sub-Fund Group Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.SubFundGroupType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Sufficient Funds Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.SufficientFundsCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Unit Of Measure" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.UnitOfMeasure&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="University Budget Office Function" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.UniversityBudgetOfficeFunction&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>				
        <li><portal:portalLink displayTitle="true" title="University Date" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.UniversityDate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
</div>
<channel:portalChannelBottom />
