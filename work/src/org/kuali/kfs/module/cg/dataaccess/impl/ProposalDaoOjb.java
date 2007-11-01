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
package org.kuali.module.cg.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.cg.bo.Close;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.dao.ProposalDao;

/**
 * @see ProposalDao
 */
public class ProposalDaoOjb extends PlatformAwareDaoBaseOjb implements ProposalDao {

    /**
     * @see org.kuali.module.cg.dao.ProposalDao#getProposalsToClose(org.kuali.module.cg.bo.Close)
     */
    public Collection<Proposal> getProposalsToClose(Close close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("proposalClosingDate");
        criteria.addLessOrEqualThan("proposalSubmissionDate", close.getCloseOnOrBeforeDate());

        return (Collection<Proposal>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Proposal.class, criteria));
    }

    /**
     * @see org.kuali.module.cg.dao.ProposalDao#save(org.kuali.module.cg.bo.Proposal)
     */
    public void save(Proposal proposal) {
        getPersistenceBrokerTemplate().store(proposal);
    }

}
