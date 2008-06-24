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
package org.kuali.kfs.gl.batch.dataaccess;

/**
 * A data access object interface, declaring methods needed by the org reversion process to 
 * properly report the unit of work summaries
 */
public interface OrganizationReversionUnitOfWorkDao {

    /**
     * This method...this terrible method...will destroy each and every one of the innocent Org Reversion Unit of Work records.
     */
    public void destroyAllUnitOfWorkSummaries();
}
