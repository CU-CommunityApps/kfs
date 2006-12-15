/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/batch/service/impl/GenesisServiceImpl.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License")
;
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
package org.kuali.module.budget.service.impl;

import org.kuali.module.budget.service.*;
import org.kuali.Constants;
import org.kuali.module.budget.dao.*;

import java.util.*;
import java.lang.*;

import org.apache.log4j.Logger;

 

public class GenesisServiceImpl implements GenesisService {
    
    /*  settings for common fields for all document headers for budget construction */
    
      private GenesisDao genesisDao;
        
      public final void stepBudgetConstructionGLLoad (Integer universityFiscalYear)
      {
          
          if (genesisDao.getBudgetConstructionControlFlag(universityFiscalYear,
                  Constants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_GENESIS_RUNNING))
          {
              // wipe out BC HEADER with deleteByQuery
              // wipe out appointment funding GL
              // get a list of all GL BB keys for universityFiscalYear
              // get a list of BB rows, and curse(sic) through the list,
              // getting a new appointment funding GL object at each step and 
              // inserting it
              // use the common method to create BC headers and Doc headers for
              // all the GL BB keys 
              // we want this to be a single transaction, so we should get 
              // a persistence broker and do a start and end
              return;
          };
          if (genesisDao.getBudgetConstructionControlFlag(universityFiscalYear,
                     Constants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE) &&
                     genesisDao.getBudgetConstructionControlFlag(universityFiscalYear,
                      Constants.BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK))
          {   
              // this is the more complicated branch that updates the BC GL
              // there should be a private method called here, as in the first branch
              return;
          }
         /*
          * (1)  Get a persistence broker to maintain a single transaction
          * (2)  Get a hash map of BC HEADER account keys (we may have to build it from what
          *      is returned).
          * (3)  Get a list of GL BB rows, sorted on accounting key (which will be
          *      the same as the sort of the BC HEADER 
          * (4)  Get a list of PBGL rows, sorted on account key
          * (5)  Do a merge in software, building a new header AND a new document
          *      header every time one of the ACCOUNT KEYS in (3) does not match
          *      a key in (2).
          *      
          *  Here are the assumptions.
          *  --we can use a single broker for all the queries.
          *  --we can create a new PBGL object, a new header object, and a new doc
          *    header object, and store them when we need to
          *  --we can update and store the PBGL objects using the setters for the amount
          *  --any PBGL objects we do NOT store will not be involved in the save
          *  --nothing is actually sent to the database until a commit (is closing the 
          *    broker sufficient for that?), and presumably the SQL that does that is 
          *    not too clunky.  Should we use beginTransaction anc commitTransaction in
          *    the Spring PersistenceBrokerImpl as well?
          *  --we can use the p6spy log to see what the thing does, and since we won't
          *    have too many test rows it will be reasonable
          *  --we can just create a default package with a main method and run all this shit
          *  
          *  we want to see whether we can use the apache.ojb addColumnEqualToField in the
          *  Criteria class and a report query in a subselect to replace the code above and
          *  have the query return to us the GL BB rows that exist (or don't) in PBGL?
          *  
          *  Can we use deleteByQuery or something similar to replace our TRUNCATEs, so 
          *  a single DELETE query runs on the data base and cleans out all the tables?
          *  
          *  Are the constants going to be updated?  Some things have a "property" constant
          *  for the field name, and various codes for the value, and some do not?  There
          *  also seems to be a little overlap between the GL constants and the constants.
          *  
          *  What is the purpose of all the interfaces followed by implementations?  I assume
          *  it's not just to be cool--it's probably there to allow people to override the
          *  implementations.  If that is so, are there any rules for which methods should
          *  go in the interface (ALL public methods, say), or is it up to the developer
          *  to decide?              
          */ 
      }


    public void setGenesisDao(GenesisDao genesisDao)
    {
        this.genesisDao = genesisDao;
    }
}
