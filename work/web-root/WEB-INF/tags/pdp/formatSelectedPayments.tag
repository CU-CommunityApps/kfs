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
<%@ attribute name="disbursementNumberRangeAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for disbursement number range."%>
	
<%@ attribute name="customerProfileAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for customer profile."%>

<%@ attribute name="formatResultAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for format result."%>
<kul:tabTop tabTitle="Payments Selected for Format Process" defaultOpen="true" tabErrorKey="ranges*">
	<div id="disbursementRanges" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Payments Selected for Format Process">
			<tr>
				<td colspan="4" class="subhead">
					Your Default Campus Code is <kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.physCampusProcCode}" property="campus" readOnly="true" />
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.sortGroupName}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${customerProfileAttributes.customerShortName}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.payments}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.amount}" />
				
			</tr>
			
			<logic:iterate id="result" name="KualiForm" property="formatProcessSummary.processSummaryList" indexId="ctr">
            <tr>
              
               <html:hidden property="formatProcessSummary.processSummary[${ctr}].customer.chartCode" />
               <html:hidden property="formatProcessSummary.processSummary[${ctr}].customer.unitCode" />
               <html:hidden property="formatProcessSummary.processSummary[${ctr}].customer.subUnitCode" />
               <html:hidden property="formatProcessSummary.processSummary[${ctr}].customer.customerShortName" />
               <html:hidden property="formatProcessSummary.processSummary[${ctr}].processTotalCount" />
               <html:hidden property="formatProcessSummary.processSummary[${ctr}].processTotalAmount" />
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.sortGroupName}" property="formatProcessSummary.processSummary[${ctr}].sortGroupName" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${customerProfileAttributes.customerShortName}" property="formatProcessSummary.processSummary[${ctr}].customer.customerShortName" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.payments}" property="formatProcessSummary.processSummary[${ctr}].processTotalCount" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.amount}" property="formatProcessSummary.processSummary[${ctr}].processTotalAmount" readOnly="true" /></td>
            </tr>
            </logic:iterate>
            
         <tr>
            <td class="total-line">&nbsp;</td>
            <td class="total-line">Total</td>
            <html:hidden property="formatProcessSummary.totalCount" />
            <td class="total-line">${KualiForm.formatProcessSummary.totalCount}</td>
            <td class="total-line"><b>${KualiForm.currencyFormattedTotalAmount}</b></td>
            <html:hidden property="formatProcessSummary.totalAmount" />
         </tr>

		</table>
	</div>
</kul:tabTop>
