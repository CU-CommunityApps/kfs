/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




function loadAgencyName( creditCardOrAgencyCodeField , creditCardOrAgencyPropertyName ) {
	var creditCardOrAgencyCodeFieldName = creditCardOrAgencyCodeField.name;
    var agencyCode = dwr.util.getValue( creditCardOrAgencyCodeFieldName );
    var agencyFieldName = findElPrefix(creditCardOrAgencyCodeFieldName) + "." + creditCardOrAgencyPropertyName;
    if (agencyCode == "") {
		clearRecipients(agencyFieldName);    		
	}
	else {
		var dwrReply = {
				callback: function (data) {
				if ( data != null && typeof data == "object" ) {   
					setRecipientValue( agencyFieldName, data.creditCardOrAgencyName );
				}
				else {
					clearRecipients(agencyFieldName); 
				}
			},
			errorHandler:function( errorMessage ) { 
				clearRecipients(agencyFieldName); 
				window.status = errorMessage;
			}
		};
		CreditCardAgencyService.getCreditCardAgencyByCode( agencyCode, dwrReply );    
	}
  
}

// function to call TravelExpenseService#getExpense and manipulate expense based on that information
function loadExpenseTypeObjectCode(expenseTypeCodeField, documentTypeName, travelerTypeCode, tripTypeCode) {
	var expenseTypeCodeFieldName = expenseTypeCodeField.name;
	var expenseTypeCodeValue = dwr.util.getValue( expenseTypeCodeFieldName );
	var lineName = expenseTypeCodeFieldName.replace(/\.expenseTypeCode$/, "");
	
	if (expenseTypeCodeValue != "") {
		var dwrReply = {
			callback: function(data) {
				if (data != null && typeof data == "object") { // we really did succeed!
					// handle taxable
					var taxableCheckbox = document.getElementById(lineName+".taxable");
					if (taxableCheckbox) {
						taxableCheckbox.checked = data.taxable;
					}
					
					var expenseAmountField = document.getElementById(lineName+".expenseAmount");
					if (expenseAmountField) {
						if (data.expenseTypeMetaCategoryCode == "M") { // handle mileage
							expenseAmountField.value = 0.0;
							expenseAmountField.disabled = true;
						} else {
							if (expenseAmountField.disabled) { // what if they were at mileage and they switched?  let's enable the amount field then
								expenseAmountField.disabled = false;
							}
						}
					} 
				}
			},
			errorHandler: function (errorMessage) {
				window.status = errorMessage;
			}
		};
		TravelExpenseService.getExpenseType(expenseTypeCodeValue, documentTypeName, tripTypeCode, travelerTypeCode, dwrReply);
	}
}
