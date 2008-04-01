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
package org.kuali.module.budget.dao.jdbc;

import java.util.ArrayList;
import java.lang.Exception;
import java.io.IOException;

import org.kuali.kfs.service.ParameterService;

import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.core.dbplatform.RawSQL;

import org.apache.log4j.Logger;

import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.document.BudgetConstructionDocument;


/**
 * create methods for building SQL useful to all extenders
 */
public class BudgetConstructionDaoJdbcBase extends PlatformAwareDaoBaseJdbc {

    ParameterService parameterService;
    
    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDaoJdbcBase.class);

    
    @RawSQL
    protected void clearTempTableByUnvlId(String tableName, String personUnvlIdColumn, String personUserIdentifier) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + personUnvlIdColumn + " = ?", personUserIdentifier);
    }

    @RawSQL
    protected void clearTempTableBySesId(String tableName, String SesIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + SesIdColumn + " = ?", sessionId);
    }
    
    /**
     * 
     * build a string of placeholders for a parameterized java.sql IN clause
     * @param parameterCount the number of parameters in the IN clause
     * @return the String (?,?,?) with the correct nubmer of parameters
     */       
    @RawSQL
    protected String inString(Integer parameterCount)
    {
        // there should be at least one parameter in the IN string
        // but allow people to screw up and have an IN condition which is never satisfied
        if (parameterCount == 0)
        {
            return new String("('')");
        }
        StringBuffer sb = new StringBuffer(20);
        sb = sb.append("(?");
        for (int i = 1; i < parameterCount; i++)
        {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 
     * build a SQL IN clause from the array of parameters passed in
     * @param inListValues: components of the IN list
     * @return an empty string if the IN list will be empty
     */
    @RawSQL
    private String inString (ArrayList<String> inListValues)
    {
        // the delimiter for strings in the DB is assumed to be a single quote.
        // this is the ANSI-92 standard.
        // if the ArrayList input is empty, IN ('') is returned.
        StringBuffer inBuilder = new StringBuffer(150);
        
        inBuilder.append("('");
        if (! inListValues.isEmpty())
        {
          inBuilder.append(inListValues.get(0));
        }
        else
        {
            // for an empty list, return an empty string
            return new String("");
        }
        for (int idx = 1; idx < inListValues.size(); idx++)
        {
            inBuilder.append("','");
            inBuilder.append(inListValues.get(idx));
        }
        inBuilder.append("')");
        
        return inBuilder.toString();
    }

    /**
     * 
     * return a SQL IN list containing the budget construction expenditure object types
     * @return a null string if the system parameter does not exist or is empty
     */
    protected String getExpenditureINList() throws NoSuchFieldException, IOException
    {
        if (! parameterService.parameterExists(BudgetConstructionDocument.class,BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES))
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s does not exist\n",BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES));
            IOException ioex = new IOException("parameter "+BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES+" does not exist");
            throw (ioex);
        }
        ArrayList<String> expenditureObjectTypes = new ArrayList<String>(parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES));
        if (expenditureObjectTypes.isEmpty())
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s is empty\n",BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES));
            NoSuchFieldException bfex = new NoSuchFieldException("parameter "+BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES+" is empty");
            throw (bfex);
        }
        return inString(expenditureObjectTypes);
    }
    
    /**
     * 
     * return a SQL IN list containing the budget construction revenue object types
     * @return a null string if the system parameter does not exist or is empty
     */
    protected String getRevenueINList() throws NoSuchFieldException, IOException
    {
        if (! parameterService.parameterExists(BudgetConstructionDocument.class,BCParameterKeyConstants.REVENUE_OBJECT_TYPES))
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s does not exist\n",BCParameterKeyConstants.REVENUE_OBJECT_TYPES));
            IOException ioex = new IOException("parameter "+BCParameterKeyConstants.REVENUE_OBJECT_TYPES+" does not exist");
            throw (ioex);
        }
        ArrayList<String>revenueObjectTypes = new ArrayList<String>(parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterKeyConstants.REVENUE_OBJECT_TYPES));
        if (revenueObjectTypes.isEmpty())
        {
            LOG.warn(String.format("\n***Budget Construction Application Error***\nSQL will not be valid\nparameter %s is empty\n",BCParameterKeyConstants.REVENUE_OBJECT_TYPES));
            NoSuchFieldException bfex = new NoSuchFieldException("parameter "+BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES+" is empty");
            throw (bfex);
        }
        return inString(revenueObjectTypes);
    }
    
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    

}
