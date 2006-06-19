<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" documentTypeName="KualiIndirectCostAdjustmentDocument" htmlFormAction="financialIndirectCostAdjustment" renderMultipart="true" showTabButtons="true">

		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>
		<kul:hiddenDocumentFields />

        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>

        <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" forcedReadOnlyFields="${KualiForm.forcedReadOnlyFields}"/>

		<kul:generalLedgerPendingEntries/>

		<kul:notes/>
			
		<kul:adHocRecipients editingMode="${KualiForm.editingMode}"/>
			
		<kul:routeLog/>

		<kul:panelFooter/>

		<kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
