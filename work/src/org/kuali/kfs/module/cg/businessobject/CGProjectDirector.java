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

package org.kuali.kfs.module.cg.businessobject;


/**
 * This interface defines all the necessary methods to define a contracts and grants project director object.
 */
public interface CGProjectDirector {

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     */
    public String getPersonUniversalIdentifier();

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier);

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber();

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber);

    /**
     * Gets the project director attribute.
     * 
     * @return the projectDirector.
     */
    public ProjectDirector getProjectDirector();

    /**
     * Sets the projectDirector.
     * 
     * @param projectDirector the projectDirector to set
     * @deprecated required by UniversalUserServiceImpl.isUniversalUserProperty() for PojoPropertyUtilsBean.getPropertyDescriptor()
     */
    public void setProjectDirector(ProjectDirector projectDirector);

}
