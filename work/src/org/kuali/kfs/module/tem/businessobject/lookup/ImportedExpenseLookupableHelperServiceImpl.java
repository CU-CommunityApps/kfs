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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.InputHtmlData;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

public class ImportedExpenseLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        Integer profileID = (Integer) GlobalVariables.getUserSession().retrieveObject(TemPropertyConstants.TEMProfileProperties.PROFILE_ID);
        fieldValues.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, profileID.toString());
        String value = GlobalVariables.getUserSession().retrieveObject(KFSPropertyConstants.DOCUMENT_TYPE_CODE).toString();
        
        
        List<HistoricalTravelExpense> results = (List<HistoricalTravelExpense>) super.getSearchResultsHelper(fieldValues, true);
        List<HistoricalTravelExpense> newResults = new ArrayList<HistoricalTravelExpense>();
        Iterator<HistoricalTravelExpense> it = results.iterator();
        while (it.hasNext()){
            HistoricalTravelExpense historicalTravelExpense = it.next();
            if (value.equals(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT)){
                if (historicalTravelExpense.getAgencyStagingDataId() != null){
                    newResults.add(historicalTravelExpense);
                }
            }
            else{
                newResults.add(historicalTravelExpense);
            }
        }
        CollectionIncomplete collection = null;
        Integer limit = LookupUtils.getSearchResultsLimit(HistoricalTravelExpense.class);
        if (newResults.size() > limit.intValue()){
            collection = new CollectionIncomplete(newResults.subList(0, limit), (long) newResults.size());
        }
        else{
            collection = new CollectionIncomplete(newResults, (long) 0);
        }
        
        return collection;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnInputHtmlData(org.kuali.rice.kns.bo.BusinessObject, java.util.Properties, org.kuali.rice.kns.web.struts.form.LookupForm, java.util.List, org.kuali.rice.kns.authorization.BusinessObjectRestrictions)
     */
    @Override
    protected HtmlData getReturnInputHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        // TODO Auto-generated method stub
        HistoricalTravelExpense expense = (HistoricalTravelExpense)businessObject;
        if (!expense.getAssigned()
                && (expense.getReconciled() == null || expense.getReconciled().equals(TemConstants.ReconciledCodes.UNRECONCILED))){
            InputHtmlData input = (InputHtmlData) super.getReturnInputHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
            input.setAppendDisplayText("</div>");
            input.setPrependDisplayText("<div align=\"center\">");
            return input;
        }
        else{
            InputHtmlData input = new InputHtmlData("","hidden");
            return input;
        }
        
    }
}
