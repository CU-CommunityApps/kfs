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

<channel:portalChannelTop channelTitle="Administrative Transactions" />
<div class="body">
  <strong>Capital Asset Builder</strong>
  <ul class="chan">
  	 <li><portal:portalLink displayTitle="true" title="Non-Purchasing/Accounts Payable General Ledger Transactions" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	 <li><portal:portalLink displayTitle="true" title="Purchasing/Accounts Payable Transactions" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableProcessingReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  </ul>

  <strong>Capital Asset Management</strong>
  <ul class="chan">
     <li><portal:portalLink displayTitle="true" title="Asset Payment" url="camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=AssetPaymentDocument" /></li>
  	 <li><portal:portalLink displayTitle="true" title="Barcode Inventory Process" url="uploadBarcodeInventoryFile.do?methodToCall=start&batchUpload.batchInputTypeName=assetBarcodeInventoryInputFileType" /></li>			 
  </ul>

  <strong>Effort Certification</strong>
  <ul class="chan">		
	 <li><portal:portalLink displayTitle="true" title="Effort Certification Recreate" url="effortCertificationRecreate.do?methodToCall=docHandler&command=initiate&docTypeName=EffortCertificationDocument" /></li>
  </ul>
	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Cash Management" url="financialCashManagement.do?methodToCall=docHandler&command=initiate&docTypeName=CashManagementDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Correction Process" url="generalLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GeneralLedgerCorrectionProcessDocument" /></li>									
		<li><portal:portalLink displayTitle="true" title="Journal Voucher" url="financialJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=JournalVoucherDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Non-Check Disbursement" url="financialNonCheckDisbursement.do?methodToCall=docHandler&command=initiate&docTypeName=NonCheckDisbursementDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Service Billing" url="financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=ServiceBillingDocument" /></li>
    </ul>
    <strong>Labor Distribution</strong><br />
    <ul class="chan">
    	<li>
			<portal:portalLink displayTitle="true" title="Labor Journal Voucher"
				url="laborJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=LaborJournalVoucherDocument" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Ledger Correction Process"
				url="laborLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=LaborLedgerCorrectionProcessDocument" />
		</li>
    </ul>
	<strong>System</strong>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Electronic Fund Transfer" url="electronicFundTransfer.do?methodToCall=start" /></li>
	</ul>
</div>
<channel:portalChannelBottom />
