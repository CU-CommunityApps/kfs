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
package org.kuali.kfs.module.cg.dataaccess;

import java.sql.Date;

import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;

/**
 * Implementations of this interface provide access to persisted Close instances.
 */
public interface CloseDao {

    /**
     * Gets the persisted instance with the latest close date.
     * 
     * @param currentSqlMidnight
     * @return the persisted instance with the latest close date.
     */
    public ProposalAwardCloseDocument getMaxApprovedClose(Date currentSqlMidnight);

    public ProposalAwardCloseDocument getMostRecentClose(Date currentSqlMidnight);

}
