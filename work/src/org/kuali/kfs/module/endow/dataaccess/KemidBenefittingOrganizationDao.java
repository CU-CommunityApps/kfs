/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;

public interface KemidBenefittingOrganizationDao {

    public List<String> getKemidsByAttribute(String attributeName, List<String> values);
    
    public List<String> getAttributeValues(String attributeName, List<String> values);
    
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids);
    
    public List<KemidBenefittingOrganization> getBenefittingOrganizations(List<String> kemids); 
    
    public List<String> getIdsForMultipleBenefittingOrganizations(List<String> kemids);
}
