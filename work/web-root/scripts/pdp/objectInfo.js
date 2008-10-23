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

function loadAchBankInfo(bankRoutingField) {
    var bankRoutingNumber = DWRUtil.getValue( bankRoutingField.name ).trim();
    var targetFieldName = findElPrefix( primaryKeyField.name ) + ".bankRouting.bankName";

	if (bankRoutingNumber=='') {
		clearRecipients(targetFieldName, "");
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( targetFieldName, data.bankName );
			} else {
				setRecipientValue( targetFieldName, wrapError( "bank not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( targetFieldName, wrapError( "bank not found" ), true );
			}
		};
		
		AchBankService.getByPrimaryId( bankRoutingNumber, dwrReply );
	}
}