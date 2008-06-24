/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.service;

import java.util.List;

import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.web.struts.BudgetOverviewFormHelper;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Contains core routing form services.
 */
public interface RoutingFormService {

    /**
     * Prepares a Routing Form for save.
     * 
     * @param RoutingFormDocument
     * @throws WorkflowException
     */
    public void prepareRoutingFormForSave(RoutingFormDocument RoutingFormDocument) throws WorkflowException;

    public BudgetDocument retrieveBudgetForLinking(String budgetDocumentNumber) throws WorkflowException;

    public void linkImportBudgetDataToRoutingForm(RoutingFormDocument routingFormDocument, String budgetDocumentHeaderId, List<BudgetOverviewFormHelper> periodOverviews) throws WorkflowException;

    public Long createAndRouteProposalMaintenanceDocument(RoutingFormDocument routingFormDocument);
}
