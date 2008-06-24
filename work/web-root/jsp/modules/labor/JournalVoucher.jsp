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

<c:set var="journalVoucherAttributes" value="${DataDictionary.KualiLaborJournalVoucherDocument.attributes}" />	
<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiLaborJournalVoucherDocument"
	htmlFormAction="laborJournalVoucher" renderMultipart="true"
	showTabButtons="true">

	<kfs:hiddenDocumentFields />
	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />

	<!-- LABOR JOURNAL VOUCHER SPECIFIC FIELDS -->
	<kul:tab tabTitle="Labor Distribution Journal Voucher Details" defaultOpen="true"
		tabErrorKey="${KFSConstants.EDIT_JOURNAL_VOUCHER_ERRORS}">
		<div class="tab-container" align=center>
		<h3>Labor Distribution Journal Voucher Details</h3>
		
		<table cellpadding=0 class="datatable"
			summary="view/edit ad hoc recipients">
			<tbody>
				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${journalVoucherAttributes.accountingPeriod}"
						useShortLabel="false" /></div>
					</th>
					<td class="datacell-nowrap"><c:if test="${readOnly}">
                        ${KualiForm.accountingPeriod.universityFiscalPeriodName}
                        <html:hidden property="selectedAccountingPeriod" />
					</c:if> <c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedAccountingPeriod() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<select name="selectedAccountingPeriod"
							onchange="submitForChangedAccountingPeriod()">
							<c:forEach items="${KualiForm.accountingPeriods}"
								var="accountingPeriod">
								<c:set var="accountingPeriodCompositeValue"
									value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
								<c:choose>
									<c:when
										test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}">
										<option
											value='<c:out value="${accountingPeriodCompositeValue}"/>'
											selected="selected"><c:out
											value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:when>
									<c:otherwise>
										<option
											value='<c:out value="${accountingPeriodCompositeValue}" />'><c:out
											value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<NOSCRIPT><html:submit value="refresh"
							alt="press this button to refresh the page after changing the accounting period" />
						</NOSCRIPT>
					</c:if></td>
				</tr>
				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${journalVoucherAttributes.balanceTypeCode}"
						useShortLabel="false" /></div>
					</th>
					<td class="datacell-nowrap"><html:hidden
						property="originalBalanceType"
						value="${KualiForm.selectedBalanceType.code}" /> <html:hidden
						property="selectedBalanceType.financialOffsetGenerationIndicator" />
					<c:if test="${readOnly}">
                        ${KualiForm.selectedBalanceType.financialBalanceTypeName}
						<html:hidden property="selectedBalanceType.code" />
						<html:hidden property="selectedBalanceType.name" />
					</c:if> <c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedBalanceType() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<select name="selectedBalanceType.code"
							onchange="submitForChangedBalanceType()">
							<c:forEach items="${KualiForm.balanceTypes}" var="balanceType">
								<c:choose>
									<c:when
										test="${KualiForm.selectedBalanceType.code==balanceType.code}">
										<option value='<c:out value="${balanceType.code}"/>'
											selected="selected"><c:out
											value="${balanceType.code}" /> - <c:out
											value="${balanceType.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${balanceType.code}" />'><c:out
											value="${balanceType.code}" /> - <c:out
											value="${balanceType.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<NOSCRIPT><html:submit value="refresh"
							alt="press this button to refresh the page after changing the balance type" />
						</NOSCRIPT>
						<kul:lookup
							boClassName="org.kuali.kfs.coa.businessobject.BalanceTyp"
							fieldConversions="code:selectedBalanceType.code"
							lookupParameters="selectedBalanceType.code:code"
							fieldLabel="${journalVoucherAttributes.balanceTypeCode.label}" />
					</c:if></td>
				</tr>
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${journalVoucherAttributes.reversalDate}"
						horizontal="true" width="35%" />
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${journalVoucherAttributes.reversalDate}"
						datePicker="true" property="document.reversalDate"
						readOnly="${readOnly}"
						readOnlyAlternateDisplay="${KualiForm.formattedReversalDate}" /></td>
				</tr>
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${journalVoucherAttributes.offsetTypeCode}"
						horizontal="true" width="35%" />
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${journalVoucherAttributes.offsetTypeCode}"
						property="document.offsetTypeCode"
						readOnly="${readOnly}"/></td>
				</tr>
			</tbody>
		</table>
		</div>
	</kul:tab>

	<fin:voucherAccountingLines
		isDebitCreditAmount="${KualiForm.selectedBalanceType.financialOffsetGenerationIndicator}"
		optionalFields="positionNumber,emplid,employeeRecord,earnCode,payGroup,salaryAdministrationPlan,grade,runIdentifier,payPeriodEndDate,payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode,transactionTotalHours,laborLedgerOriginalChartOfAccountsCode,laborLedgerOriginalAccountNumber,laborLedgerOriginalSubAccountNumber,laborLedgerOriginalFinancialObjectCode,laborLedgerOriginalFinancialSubObjectCode,hrmsCompany,encumbranceUpdateCode,setid"
		isOptionalFieldsInNewRow="true"
		displayExternalEncumbranceFields="${KualiForm.selectedBalanceType.finBalanceTypeEncumIndicator}"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		includeObjectTypeCode="true" />
		
	<ld:laborLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kfs:documentControls transactionalDocument="true" />
</kul:documentPage>
