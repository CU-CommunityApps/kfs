/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * import the given javascript into the hosting document
 * 
 * @param scriptSourceFileName the given script source file name 
 * @param scriptSourceHolder the script source file name holder that is a key-value pair object. 
 * One script source file name can only have one entry in the object, so it is used to avoid double load scripts.
 */  
function importJavascriptFile(scriptSourceFileName, scriptSourceHolder) {
	if (scriptSourceFileName == "" || scriptSourceHolder[scriptSourceFileName] != null) {
		return;
	}
	
	var scriptElement = document.createElement("script");
	scriptElement.type = "text/javascript";
	scriptElement.src = scriptSourceFileName;
	document.getElementsByTagName("head")[0].appendChild(scriptElement);
	
	scriptSourceHolder[scriptSourceFileName] = scriptSourceFileName;
} 

/**
 * import the script that defines the DWR interface into the hosting document
 * 
 * @param interfaceName the given interface name
 * @param interfaceScriptHolder the script name holder that is a key-value pair object
 */	
function importDWRInterface(interfaceName, interfaceScriptHolder) {
	if (interfaceName == "" || interfaceScriptHolder[interfaceName] != null) {
		return;
	}
	
	var scriptSourceFileName = "dwr/interface/" + interfaceName + ".js";
	importJavascriptFile(scriptSourceFileName, interfaceScriptHolder)
}

/**
 * the constructor of BudgetObjectInfoUpdator object
 */
function BudgetObjectInfoUpdator(){ 
	requestedCsfAmountSuffix = ".appointmentRequestedCsfAmount";
	requestedCsfTimePercentSuffix = ".appointmentRequestedCsfTimePercent";
	
	var interfaceScriptHolder = new Object();
	importDWRInterface("BudgetConstructionAppointmentFundingReasonCodeService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionIntendedIncumbentService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionDurationService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionAdministrativePostService", interfaceScriptHolder);
	importDWRInterface("BudgetConstructionPositionService", interfaceScriptHolder);
	importDWRInterface("SalarySettingService", interfaceScriptHolder);
	importDWRInterface("FinancialSystemUserService", interfaceScriptHolder);
}

/**
 * retrieve duration and open/close the amount and time percent fields 
 */
BudgetObjectInfoUpdator.prototype.loadDurationInfo = function(durationCodeFieldName, durationDescriptionFieldName ) {
    var durationCode = DWRUtil.getValue( durationCodeFieldName );
    var fieldNamePrefix = findElPrefix(durationCodeFieldName);
    var requestedCsfAmountField = document.getElementById(fieldNamePrefix + requestedCsfAmountSuffix);
    var requestedCsfTimePercentField = document.getElementById(fieldNamePrefix + requestedCsfTimePercentSuffix);

	if (durationCode=='') {
		clearRecipients(durationDescriptionFieldName, "");
	}
	else {
		var isDefualtCode = (durationCode == "NONE");								
		if(isDefualtCode){
			requestedCsfAmountField.setAttribute('disabled', 'disabled');
			requestedCsfTimePercentField.setAttribute('disabled', 'disabled');
		}
		else{
			requestedCsfAmountField.removeAttribute('disabled');
			requestedCsfTimePercentField.removeAttribute('disabled');
		} 
		
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {								
				setRecipientValue( durationDescriptionFieldName, data.appointmentDurationDescription);
			} else {
				setRecipientValue( durationDescriptionFieldName, wrapError( "duration not found" ), true );	
				requestedCsfAmountField.setAttribute('disabled', 'disabled');
				requestedCsfTimePercentField.setAttribute('disabled', 'disabled');		
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( durationDescriptionFieldName, wrapError( "duration not found" ), true );
			}
		};
		
		BudgetConstructionDurationService.getByPrimaryId( durationCode, dwrReply );
	}
}

/**
 * retrieve reason code and open/close the reason amount field
 */
BudgetObjectInfoUpdator.prototype.loadReasonCodeInfo = function(reasonAmountFieldName, reasonCodeFieldName, reasonDescriptionFieldName ) {
    var reasonCode = DWRUtil.getValue( reasonCodeFieldName );
    var reasonAmountField = document.getElementById(reasonAmountFieldName);

	if (reasonCode=='') {
		clearRecipients(reasonDescriptionFieldName, "");
		reasonAmountField.setAttribute('disabled', 'disabled');
	} else {
		reasonAmountField.removeAttribute('disabled');
		
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( reasonDescriptionFieldName, data.appointmentFundingReasonDescription);
			} else {
				setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( reasonDescriptionFieldName, wrapError( "reason not found" ), true );
			}
		};
		
		BudgetConstructionAppointmentFundingReasonCodeService.getByPrimaryId( reasonCode, dwrReply );
	}
}

/**
 * retrieve the intended incumbent and administrative post according to the given information
 */
BudgetObjectInfoUpdator.prototype.loadIntendedIncumbentInfo = function(positionNumberFieldName, iuClassificationLevelFieldName, administrativePostFieldName, emplidFieldName, personNameFieldName) {
	loadEmplInfo(emplidFieldName, personNameFieldName);
	
	var emplid = DWRUtil.getValue( emplidFieldName );

	if (emplid=='') {
		clearRecipients(iuClassificationLevelFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( iuClassificationLevelFieldName, data.iuClassificationLevel);
			} else {
				clearRecipients(iuClassificationLevelFieldName, "");			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( iuClassificationLevelFieldName, wrapError( "Error" ), true );
			}
		};
		
		BudgetConstructionIntendedIncumbentService.getByPrimaryId( emplid, dwrReply );
	}
	
	budgetObjectInfoUpdator.loadAdministrativePostInfo(emplidFieldName, positionNumberFieldName, administrativePostFieldName);
}

/**
 * retrieve the position and adminstrative post according to the given fiscal year, employee id and position number
 */
BudgetObjectInfoUpdator.prototype.loadPositionInfo = function(universityFiscalYearFieldName, emplidFieldName, 
	iuNormalWorkMonthsFieldName, iuPayMonthsFieldName, positionFullTimeEquivalencyFieldName, administrativePostFieldName, 
	positionNumberFieldName, positionDescriptionFieldName) {
	
	var universityFiscalYear = DWRUtil.getValue( universityFiscalYearFieldName );
	var emplid = DWRUtil.getValue( emplidFieldName );
	var positionNumber = DWRUtil.getValue( positionNumberFieldName );

	if (positionNumber == '' || universityFiscalYear == '') {
		clearRecipients(positionDescriptionFieldName, "");
		clearRecipients(iuNormalWorkMonthsFieldName, "");
		clearRecipients(iuPayMonthsFieldName, "");
		clearRecipients(positionFullTimeEquivalencyFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( positionDescriptionFieldName, data.positionDescription);
				setRecipientValue( iuNormalWorkMonthsFieldName, data.iuNormalWorkMonths);
				setRecipientValue( iuPayMonthsFieldName, data.iuPayMonths);
				setRecipientValue( positionFullTimeEquivalencyFieldName, data.positionFullTimeEquivalency);
			} else {
				setRecipientValue( positionDescriptionFieldName, wrapError( "position not found" ), true );
				clearRecipients(iuNormalWorkMonthsFieldName, "");
				clearRecipients(iuPayMonthsFieldName, "");
				clearRecipients(positionFullTimeEquivalencyFieldName, "");			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( positionDescriptionFieldName, wrapError( "position not found" ), true );
			}
		};
		
		BudgetConstructionPositionService.getByPrimaryId(universityFiscalYear, positionNumber, dwrReply );
	}
	
	budgetObjectInfoUpdator.loadAdministrativePostInfo(emplidFieldName, positionNumberFieldName, administrativePostFieldName);
}

/**
 * retrieve the administrative post according to the given emplid and position number
 */
BudgetObjectInfoUpdator.prototype.loadAdministrativePostInfo = function(emplidFieldName, positionNumberFieldName, administrativePostFieldName) {
	var emplid = DWRUtil.getValue( emplidFieldName );
	var positionNumber = DWRUtil.getValue( positionNumberFieldName );

	if (positionNumber == '' || emplid == '') {
		clearRecipients(administrativePostFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( administrativePostFieldName, data.administrativePost);
			} else {
				clearRecipients(administrativePostFieldName, "");			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( administrativePostFieldName, wrapError( "administrative post not found" ), true );
			}
		};
		
		BudgetConstructionAdministrativePostService.getByPrimaryId(emplid, positionNumber, dwrReply );
	}
}

/**
 * calculate fte quantity based on the given information
 */
BudgetObjectInfoUpdator.prototype.recalculateFTE = function(payMonthsFieldName, fundingMonthsFieldName, fteQuantityFieldName, timePercentFieldName, fteQuantityField ) {
    var timePercent = DWRUtil.getValue(timePercentFieldName);
    var payMonths = DWRUtil.getValue(payMonthsFieldName);
    var fundingMonths = DWRUtil.getValue(fundingMonthsFieldName);

	if (timePercent=='' || payMonths=='' || fundingMonths=='') {
		clearRecipients(fteQuantityFieldName, '');
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null) {
				var formattedFTE = new Number(data).toFixed(4);
				setRecipientValue( fteQuantityFieldName, formattedFTE );
			} else {
				setRecipientValue( fteQuantityFieldName, null );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( fteQuantityFieldName, null );
			}
		};
		
		SalarySettingService.calculateFteQuantity(payMonths, fundingMonths, timePercent, dwrReply );
	}
}

var budgetObjectInfoUpdator = new BudgetObjectInfoUpdator();