/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem;

public class TemWorkflowConstants {
    public static final String SPECIAL_REQUEST = "TravelHasSpecialRequest";
    public static final String INTL_TRAVEL = "TravelIsInternationalTravel";
    public static final String RISK_MANAGEMENT = "TravelNeedsRiskManagement";
    public static final String TRVL_ADV_REQUESTED = "TravelAdvanceRequested";
    public static final String ACCTG_APPROVAL_REQUIRED = "TravelRequiresAccountingReviewer";
    public static final String DIVISION_APPROVAL_REQUIRED = "TravelRequiresDivisionApproval";
    public static final String ACCOUNT_APPROVAL_REQUIRED = "TravelRequiresAccountApproval";
    public static final String TAX_MANAGER = "TaxManager";
    public static final String ENTERTAINMENT_MANAGER = "EntertainmentManager";
    public static final String SEPARATION_OF_DUTIES = "SeparationOfDuties";
    public static final String REQUIRES_SUB_FUND = "RequiresSubFund";
    public static final String REQUIRES_AWARD = "RequiresAward";
    public static final String REQUIRES_AP_TRAVEL = "RequiresAPTravel";
    public static final String REQUIRES_TRAVELER_REVIEW = "TravelRequiresTravelerApproval";
    
    // TEMProfile constants
    public static final String TAX_MANAGER_REQUIRED = "ProfileIsTaxManagerRequired";
    // RELO constants
    public static final String TAX_MANAGER_APPROVAL_REQUIRED = "TaxManagerApproval";

    public static class RouteNodeNames {
        public static final String AP_TRAVEL = "APTravel";
        public static final String TRAVELER_REVIEW = "TravelerReview";
        public static final String ACCOUNT = "Account";
        public static final String ACCOUNTING_REVIEWER = "AccountingReviewer";
        public static final String DIVISION_REVIEWER = "DivisionReviewer";
        public static final String SPECIAL_REQUEST_REVIEWER = "SpecialRequestReviewer";
        public static final String INTERNATIONAL_TRAVEL_REVIEWER = "InternationalTravelReviewer";
        public static final String SUB_FUND = "SubFund";
        public static final String AWARD = "Award";
        public static final String TAX = "Tax";
        public static final String SEPARATION_OF_DUTIES_REVIEWER = "SeparationOfDutiesReviewer";
        public static final String ORGANIZATION_HIERARCHY = "OrganizationHierarchy";
        public static final String RISK_MANAGEMENT = "RiskManagement";
        public static final String EXECUTIVE_APPROVAL = "ExecutiveApproval";
        public static final String MOVING_AND_RELOCATION_MANAGER = "MovingAndRelocationManager";
    }
}
