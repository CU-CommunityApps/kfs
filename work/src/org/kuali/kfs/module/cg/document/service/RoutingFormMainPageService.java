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
package org.kuali.kfs.module.cg.document.service;

import java.util.List;

import org.kuali.kfs.module.cg.businessobject.DueDateType;
import org.kuali.kfs.module.cg.businessobject.PersonRole;
import org.kuali.kfs.module.cg.businessobject.ProjectType;
import org.kuali.kfs.module.cg.businessobject.ResearchTypeCode;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;

/**
 * This interface defines methods that a RoutingFormMainPageService must provide
 */
public interface RoutingFormMainPageService {

    /**
     * Setup maintainables for Main Page: DueDateType, ProjectType, Purpose, ResearchTypeCode, and PersonRole.
     * 
     * @param routingFormDocument the doc to set up
     */
    public void setupMainPageMaintainables(RoutingFormDocument routingFormDocument);

    /**
     * Returns the complete list of ResearchTypeCodes except for the inactive ones.
     * 
     * @return typed list of ResearchTypeCodes
     */
    public List<ResearchTypeCode> getResearchTypeCodes();

    /**
     * Returns the complete list of ProjectTypes except for the inactive ones.
     * 
     * @return typed list of project types
     */
    public List<ProjectType> getProjectTypes();

    /**
     * Returns the complete list of PersonRoles except for the inactive ones.
     * 
     * @return typed list of person roles
     */
    public List<PersonRole> getPersonRoles();

    /**
     * Returns the complete list of DueDateTypes except for the inactive ones.
     * 
     * @return typed list of due date types
     */
    public List<DueDateType> getDueDateTypes();

    /**
     * Checks if at least one of the people in the list is a CoPd.
     * 
     * @param routingFormPersonnel
     * @return
     */
    public boolean checkCoPdExistance(List<RoutingFormPersonnel> routingFormPersonnel);

    /**
     * Returns the first project director found in a list. If none is found it return null;
     * 
     * @param routingFormPersonnel
     * @return
     */
    public RoutingFormPersonnel getProjectDirector(List<RoutingFormPersonnel> routingFormPersonnel);

    /**
     * Returns the first contact person found in a list. If none is found it return null;
     * 
     * @param routingFormPersonnel
     * @return
     */
    public RoutingFormPersonnel getContactPerson(List<RoutingFormPersonnel> routingFormPersonnel);
}
