/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao.ojb;

import java.lang.Class;

import java.lang.reflect.*;
import java.lang.StringBuffer;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.lang.RuntimeException;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.bo.PersistableBusinessObjectBase;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionDetail;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.dao.FiscalYearMakersCopyAction;
import org.kuali.module.chart.dao.FiscalYearMakersDao;
import org.kuali.module.chart.dao.FiscalYearMakersFieldChangeAction;
import org.kuali.module.chart.dao.FiscalYearMakersFilterAction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.util.KFSUtils;

/*
 *   this stuff is here support the inhibitCascading routine that should probably go into PersistenceStructureService
 */
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException; 
import org.apache.ojb.broker.metadata.CollectionDescriptor; 
import org.apache.ojb.broker.metadata.DescriptorRepository; 
import org.apache.ojb.broker.metadata.ObjectReferenceDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
/****************************************************************************************************************************************************/


public class FiscalYearMakersDaoOjb extends PlatformAwareDaoBaseOjb
implements FiscalYearMakersDao {

/*
 *   These routines are designed to create rows for the next fiscal year for
 *   reference tables, based on the rows in those tables for the current fiscal year.
 *   The idea is to relieve people of the responsibility for typing in hundreds of new 
 *   rows in a maintenance document, and to preclude having to auto-generate reference
 *   rows for x years in the future, maintaining them as things change.
 *   There are two modes used by routines in this module.
 *   (1)  slash-and-burn:  if any rows for the target year exist, they are deleted,
 *                         and replaced with copies of the current year's rows
 *   (2)  warm-and-fuzzy:  any rows for the new year that already exist are left in 
 *                         place, and only those rows whose keys are missing in the
 *                         new fiscal year are copied from the current year 
 *   There are two versions of each method (using overloading).  To get the slash-and_burn
 *   version, one uses the method where there is a second parameter, and passes its
 *   value as the static variable "replaceMode".                                            
 */    

    
    /*  turn on the logger for the persistence broker */
    private static Logger LOG = 
        org.apache.log4j.Logger.getLogger(FiscalYearMakersDaoOjb.class);

   
    private DateTimeService dateTimeService;
    private PersistenceStructureService persistenceStructureService;
    
    private UniversityDate universityDate;
    
    /*
     *   these fields are used for setting up the UniversityDate data
     *   they are here because RI in the database requires that UniversityDate
     *   be treated just like any other table in fiscal year makers
     *   there will be one start for each month
     *   the code will adjust the year from the reference year
     *   these values are intended ONLY to reset the fiscal year beginning date
     */
    private Integer BeginYear;
    private String  FiscalYear;
    private static final Integer ReferenceYear = 1900;
    public static final GregorianCalendar START_JANUARY = 
        new GregorianCalendar(ReferenceYear,Calendar.JANUARY,1);
    public static final GregorianCalendar START_FEBRUARY =
        new GregorianCalendar(ReferenceYear,Calendar.FEBRUARY,1);
    public static final GregorianCalendar START_MARCH =
        new GregorianCalendar(ReferenceYear,Calendar.MARCH,1);
    public static final GregorianCalendar START_APRIL =
        new GregorianCalendar(ReferenceYear,Calendar.APRIL,1);
    public static final GregorianCalendar START_MAY =
        new GregorianCalendar(ReferenceYear,Calendar.MAY,1);
    public static final GregorianCalendar START_JUNE =
        new GregorianCalendar(ReferenceYear,Calendar.JUNE,1);
    public static final GregorianCalendar START_JULY =
        new GregorianCalendar(ReferenceYear,Calendar.JULY,1);
    public static final GregorianCalendar START_AUGUST =
        new GregorianCalendar(ReferenceYear,Calendar.AUGUST,1);
    public static final GregorianCalendar START_SEPTEMBER =
        new GregorianCalendar(ReferenceYear,Calendar.SEPTEMBER,1);
    public static final GregorianCalendar START_OCTOBER =
        new GregorianCalendar(ReferenceYear,Calendar.OCTOBER,1);
    public static final GregorianCalendar START_NOVEMBER =
        new GregorianCalendar(ReferenceYear,Calendar.NOVEMBER,1);
    public static final GregorianCalendar START_DECEMBER =
        new GregorianCalendar(ReferenceYear,Calendar.DECEMBER,1);
    private GregorianCalendar fiscalYearStartDate;


    public static final boolean replaceMode = true;
    
    public void deleteNewYearRows(Integer requestYear)
    {
        // delete all the rows (if any) for the request year for all the
        // classes in the ordered delete list
        // the delete order is set so that referential integrity will not cause 
        // an exception: children first, then parents
        for (Map.Entry<String,Class> classesToDelete : getDeleteOrder().entrySet())
        {
            Integer RequestYear = requestYear;
            if (laggingCopyCycle.contains(classesToDelete.getKey()))
            {
                // this object is copied from LAST PERIOD into the CURRENT PERIOD
                RequestYear = RequestYear - 1;
            }
            deleteNewYearRows(RequestYear,classesToDelete.getValue());
        }
        getPersistenceBrokerTemplate().clearCache();
    }
    
    // this routine gets rid of existing rows for the request year + 1 for
    // the parents of the child passed as a parameter
    // it is uses when, for some classes, we want to create two years' worth
    // of rows on each run
    public void deleteYearAfterNewYearRowsForParents(Integer RequestYear, 
                                                      Class childClass)
    {
        RequestYear = RequestYear+1;
        // first we have to delete the child rows
        deleteNewYearRows(RequestYear,childClass);
        // now we loop through the delete order, and delete each parent in turn
        for (Map.Entry<String,Class> classesToDelete : getDeleteOrder().entrySet())
        {
            // better compare the names just to be safe
            Class deleteClass = classesToDelete.getValue();
            if (isAParentOf(deleteClass.getName(),childClass))
            {
                deleteNewYearRows(RequestYear,
                                  deleteClass);
             }
        }
    }
    
    public boolean isAParentOf(String testClassName, Class childClass)
    {
        ArrayList<Class> parentClasses = childParentMap.get(childClass.getName());
        Iterator<Class> parents = parentClasses.iterator();
        // we compare names to be safe
        while (parents.hasNext())
        {
            if (testClassName.compareTo(parents.next().getName()) == 0)
            {
                return true;
            }
        }
        return false;
    }
    
    public LinkedHashMap<String,FiscalYearMakersCopyAction> 
           setUpRun(Integer BaseYear, boolean replaceMode)
    {
        // this is the routine where you designate which objects
        // should participate and whether they should use customized
        // field setters or customized query filters
        // the objects participating MUST match the object list
        // configured in the XML
        
        //  added October, 2007, to remove all OJB auto-update and auto-delete codes 
        //  which would alter the delete and copy order set by the XML-encoded parent-child relationships
        //  this code changes the settings in memory for this run only
        //  this implies that fiscal year makers should run in its own container, with no
        //  other jobs which might depend on the auto-xxx settings.
        turnOffCascades();
        
        /*************************************************************
         *                 AccountingPeriod                          *
         *************************************************************/
        FiscalYearMakersCopyAction copyActionAcctPrd =
            new FiscalYearMakersCopyAction()
            {
               FiscalYearMakersFieldChangeAction<AccountingPeriod> fieldAction =
                  new FiscalYearMakersFieldChangeAction<AccountingPeriod>()
                  {
                     public void customFieldChangeMethod
                     (Integer currentFiscalYear,
                      Integer newFiscalYear,
                      AccountingPeriod candidateRow)
                     {
                         // there is a four-character year in periods 01 through 12, and a two-character
                         // year in period 13. we need to update these for the new year.  instead of
                         // hard-wiring in the periods, we just try to make both changes
                         Integer startThisYear = currentFiscalYear-1;
                         String startThisYearString = startThisYear.toString();
                         String currentFiscalYearString = currentFiscalYear.toString();
                         String newFiscalYearString = newFiscalYear.toString();
                         String nameString = candidateRow.getUniversityFiscalPeriodName();
                         candidateRow.setUniversityFiscalPeriodName(
                                      updateStringField(newFiscalYearString,
                                                        currentFiscalYearString,
                                                        candidateRow.getUniversityFiscalPeriodName()));
                         candidateRow.setUniversityFiscalPeriodName(
                                      updateStringField(currentFiscalYearString,
                                                        startThisYearString,
                                                        candidateRow.getUniversityFiscalPeriodName()));
                         candidateRow.setUniversityFiscalPeriodName(
                                      updateTwoDigitYear(newFiscalYearString.substring(2,4),
                                                         currentFiscalYearString.substring(2,4),
                                                         candidateRow.getUniversityFiscalPeriodName()));
                         candidateRow.setUniversityFiscalPeriodName(
                                 updateTwoDigitYear(currentFiscalYearString.substring(2,4),
                                                    startThisYearString.substring(2,4),
                                                    candidateRow.getUniversityFiscalPeriodName()));
                         // we have to update the ending date, increasing it by one year
                         candidateRow.setUniversityFiscalPeriodEndDate(
                                 addYearToDate(candidateRow.getUniversityFiscalPeriodEndDate()));
                         // we set all of the fiscal period status codes to "closed" before the
                         // start of the coming year
                         candidateRow.setUniversityFiscalPeriodStatusCode(
                                      KFSConstants.ACCOUNTING_PERIOD_STATUS_OPEN);
                     }
                  };
            
               public void copyMethod(Integer BaseYear, boolean replaceMode)
               {
                 MakersMethods<AccountingPeriod> makersMethod =
                     new MakersMethods<AccountingPeriod>();
                 makersMethod.makeMethod(AccountingPeriod.class,
                                         BaseYear,
                                         replaceMode,
                                         fieldAction);
               }
            };
       addCopyAction(AccountingPeriod.class,copyActionAcctPrd);

       /*************************************************************
        *                    BenefitsCalculation                    *
        *************************************************************/
        FiscalYearMakersCopyAction copyActionBenCalc =
            new FiscalYearMakersCopyAction()
            {
               public void copyMethod(Integer BaseYear, boolean replaceMode)
               {
                 MakersMethods<BenefitsCalculation> makersMethod =
                     new MakersMethods<BenefitsCalculation>();
                 makersMethod.makeMethod(BenefitsCalculation.class,
                                         BaseYear,
                                         replaceMode);
               }
            };
        addCopyAction(BenefitsCalculation.class,copyActionBenCalc); 

        /*************************************************************
         *                     IcrAutomatedEntry                     *
         *************************************************************/
         FiscalYearMakersCopyAction copyActionIcrAuto =
             new FiscalYearMakersCopyAction()
             {
                public void copyMethod(Integer BaseYear, boolean replaceMode)
                {
                  MakersMethods<IcrAutomatedEntry> makersMethod =
                      new MakersMethods<IcrAutomatedEntry>();
                  makersMethod.makeMethod(IcrAutomatedEntry.class,
                                          BaseYear,
                                          replaceMode);
                }
             };
         addCopyAction(IcrAutomatedEntry.class,copyActionIcrAuto); 

        /*************************************************************
         *                       LaborObject                         *
         *************************************************************/
         FiscalYearMakersCopyAction copyActionLabObj =
             new FiscalYearMakersCopyAction()
             {
                public void copyMethod(Integer BaseYear, boolean replaceMode)
                {
                  MakersMethods<LaborObject> makersMethod =
                      new MakersMethods<LaborObject>();
                  makersMethod.makeMethod(LaborObject.class,
                                          BaseYear,
                                          replaceMode);
                }
             };
         addCopyAction(LaborObject.class,copyActionLabObj); 
            
       /************************************************************* 
        *                     ObjectCode                            *
        *************************************************************/
        FiscalYearMakersCopyAction copyActionObjectCode =
            new FiscalYearMakersCopyAction()
            {
              FiscalYearMakersFilterAction filterObjectCode =
                  new FiscalYearMakersFilterAction()
                  {
                     public Criteria customCriteriaMethod()
                     {
                         // this method allows us to add any filters needed on the current
                         // year rows--for example, we might not want any marked deleted.
                         // for ObjectCode, we don't want any invalid objects--UNLESS they
                         // are the dummy object used in budget construction
                         Criteria criteriaID = new Criteria();
                         criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_ACTIVE_CODE,true);
                         Criteria criteriaBdg = new Criteria();
                         criteriaBdg.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                                                BudgetConstructionConstants.OBJECT_CODE_2PLG);
                         criteriaID.addOrCriteria(criteriaBdg);
                         return criteriaID;        
                     }
                  };
            public void copyMethod(Integer BaseYear, boolean replaceMode)
            {
                MakersMethods<ObjectCode> makersMethod =
                    new MakersMethods<ObjectCode>();
                makersMethod.makeMethod(ObjectCode.class,
                                        BaseYear,
                                        replaceMode,
                                        filterObjectCode);
            }
            };
        addCopyAction(ObjectCode.class,copyActionObjectCode);

        /*************************************************************
         *                    OffsetDefinition                       *
         *************************************************************/
         FiscalYearMakersCopyAction copyActionOffDef =
             new FiscalYearMakersCopyAction()
             {
                public void copyMethod(Integer BaseYear, boolean replaceMode)
                {
                  MakersMethods<OffsetDefinition> makersMethod =
                      new MakersMethods<OffsetDefinition>();
                  makersMethod.makeMethod(OffsetDefinition.class,
                                          BaseYear,
                                          replaceMode);
                }
             };
         addCopyAction(OffsetDefinition.class,copyActionOffDef); 
        
        /*************************************************************
         *                        Options                            *
         *************************************************************/
        FiscalYearMakersCopyAction copyActionOptions =
            new FiscalYearMakersCopyAction()
            {
               FiscalYearMakersFieldChangeAction<Options> fieldAction =
                  new FiscalYearMakersFieldChangeAction<Options>()
                  {
                     public void customFieldChangeMethod
                     (Integer currentFiscalYear,
                      Integer newFiscalYear,
                      Options candidateRow)
                     {
                         // some ineffeciency in set up is traded for easier maintenance
                         Integer currentYearStart = currentFiscalYear-1;
                         String endNextYearString   = newFiscalYear.toString();
                         String endCurrentYearString = currentFiscalYear.toString();
                         String startCurrentYearString   = currentYearStart.toString();
                         candidateRow.setUniversityFiscalYearStartYr(currentFiscalYear);
                         // here we allow for a substring of XXXX-YYYY as well as XXXX and YYYY
                         String holdIt = updateStringField(endNextYearString,
                                                           endCurrentYearString,
                                                           candidateRow.getUniversityFiscalYearName());
                         candidateRow.setUniversityFiscalYearName(
                                      updateStringField(endCurrentYearString,
                                                        startCurrentYearString,
                                                        holdIt));
                     }
                  };
            
               public void copyMethod(Integer BaseYear, boolean replaceMode)
               {
                 MakersMethods<Options> makersMethod =
                     new MakersMethods<Options>();
                 makersMethod.makeMethod(Options.class,
                                         BaseYear,
                                         replaceMode,
                                         fieldAction);
               }
            };
        addCopyAction(Options.class,copyActionOptions);

       /*************************************************************
        *                 OrganizationReversion                     *
        *************************************************************/
        FiscalYearMakersCopyAction copyActionOrgRev =
            new FiscalYearMakersCopyAction()
            {
               public void copyMethod(Integer BaseYear, boolean replaceMode)
               {
                 MakersMethods<OrganizationReversion> makersMethod =
                     new MakersMethods<OrganizationReversion>();
                 makersMethod.makeMethod(OrganizationReversion.class,
                                         BaseYear,
                                         replaceMode);
               }
            };
        addCopyAction(OrganizationReversion.class,copyActionOrgRev); 
            
            
        /*************************************************************
         *             OrganizationReversionDetail                   *
         *************************************************************/
        FiscalYearMakersCopyAction copyActionOrgRevDtl =
            new FiscalYearMakersCopyAction()
            {
               public void copyMethod(Integer BaseYear, boolean replaceMode)
               {
                 MakersMethods<OrganizationReversionDetail> makersMethod =
                     new MakersMethods<OrganizationReversionDetail>();
                 makersMethod.makeMethod(OrganizationReversionDetail.class,
                                         BaseYear,
                                         replaceMode);
               }
            };
        addCopyAction(OrganizationReversionDetail.class,copyActionOrgRevDtl);

        /*************************************************************
         *                  PositionObjectBenefit                    *
         *************************************************************/
         FiscalYearMakersCopyAction copyActionPosObjBen =
             new FiscalYearMakersCopyAction()
             {
                public void copyMethod(Integer BaseYear, boolean replaceMode)
                {
                  MakersMethods<PositionObjectBenefit> makersMethod =
                      new MakersMethods<PositionObjectBenefit>();
                  makersMethod.makeMethod(PositionObjectBenefit.class,
                                          BaseYear,
                                          replaceMode);
                }
             };
         addCopyAction(PositionObjectBenefit.class,copyActionPosObjBen); 

         /*************************************************************
          *                          SubObjCd                         *
          *************************************************************/
          FiscalYearMakersCopyAction copyActionSubObjCd =
              new FiscalYearMakersCopyAction()
              {
              /*  not for phase II
              FiscalYearMakersFilterAction filterSubObjectCode =
                  new FiscalYearMakersFilterAction()
                  {
                     public Criteria customCriteriaMethod()
                     {
                         // this method allows us to add any filters needed on the current
                         // year rows--for example, we might not want any marked deleted.
                         // for SubObjectCode, we don't want any invalid objects
                         Criteria criteriaID = new Criteria();
                         criteriaID.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_ACTIVE_INDICATOR,true);
                         return criteriaID;        
                     }
                  };
                 */
                 public void copyMethod(Integer BaseYear, boolean replaceMode)
                 {
                   MakersMethods<SubObjCd> makersMethod =
                       new MakersMethods<SubObjCd>();
                   makersMethod.makeMethod(SubObjCd.class,
                                           BaseYear,
                                           replaceMode);
                   /*  not for phase II
                                           replaceMode,
                                           filterSubObjectCode);
                    */
                 }
              };
          addCopyAction(SubObjCd.class,copyActionSubObjCd); 
        /*******************************************************************
         *                          University Date                        *
         *******************************************************************/
         FiscalYearMakersCopyAction copyActionUniversityDate =
             new FiscalYearMakersCopyAction()
             {
                public void copyMethod(Integer currentFiscalYear, boolean replaceMode)
                {
                    // this is the routine to call to build the new year's university date tables
                    // the start month of the fiscal year is passed in with the reference date
                    // the year in the reference date is updated to the year of the current fiscal
                    // year
                    // if we start on January 1, the year of the beginning date should in fact be 
                    // the year FOLLOWING the currentFiscalYear.  Otherwise, the first date of the
                    // new fiscal year falls within the year in which the current fiscal year ends.
                       if (fiscalYearStartDate.equals(START_JANUARY))
                       {
                           currentFiscalYear = currentFiscalYear+1;
                       }
                    GregorianCalendar newYearStartDate = 
                        new GregorianCalendar(fiscalYearStartDate.get(Calendar.YEAR),
                                     fiscalYearStartDate.get(Calendar.MONTH),
                                     fiscalYearStartDate.get(Calendar.DAY_OF_MONTH));
                    int yearDifference = currentFiscalYear - 
                                         newYearStartDate.get(Calendar.YEAR);
                    newYearStartDate.add(Calendar.YEAR,yearDifference);
                    makeUniversityDate(newYearStartDate);
                }
             };
             addCopyAction(UniversityDate.class,copyActionUniversityDate); 
        /********************************************************************/
        //
        // this is the routine that sets up and and returns the runtime call order
        // addCopyAction must have been called on every object to be copied
        // before this routine is called
           return(getCopyOrder());
    }
    
    /********************************************************************************
     *                University Date Database Access                               *
     ********************************************************************************/
    //  this is the only routine that simply replaces what is there, if anything
    //  but, we have to do a delete--otherwise, we can get an optimistic locking
    //  exception when we try to store a new row on top of something already in 
    //  the database.  we will delete by fiscal year.
    //  the accounting period is assumed to correspond to the month, with the 
    //  month of the start date being the first period and the month of the last
    //  day of the fiscal year being the twelfth.
    //  the fiscal year tag is always the year of the ending date of the fiscal year
    public void makeUniversityDate(GregorianCalendar FiscalYearStartDate)
    {
        // loop through a year's worth of dates for the new year
        GregorianCalendar shunivdate = 
            new GregorianCalendar(FiscalYearStartDate.get(Calendar.YEAR),
                                  FiscalYearStartDate.get(Calendar.MONTH),
                                  FiscalYearStartDate.get(Calendar.DAY_OF_MONTH));
        // set up the end date
        GregorianCalendar enddate  = 
            new GregorianCalendar(FiscalYearStartDate.get(Calendar.YEAR),
                                  FiscalYearStartDate.get(Calendar.MONTH),
                                  FiscalYearStartDate.get(Calendar.DAY_OF_MONTH));
        enddate.add(Calendar.MONTH,12);
        enddate.add(Calendar.DAY_OF_MONTH,-1);
        // the fiscal year is always the year of the ending date of the fiscal year
        Integer nextFiscalYear = (Integer) enddate.get(Calendar.YEAR);
        // get rid of anything already there
        deleteNewYearRows(nextFiscalYear, UniversityDate.class);
        // initialize the period variables 
        int period  = 1;
        String periodString = String.format("%02d",period);
        int compareMonth = shunivdate.get(Calendar.MONTH);
        int currentMonth = shunivdate.get(Calendar.MONTH);
     // loop through the dates until we hit the last one
        while (!(shunivdate.equals(enddate)))
        {   
           //TODO: temporary debugging code 
           LOG.debug(String.format("\n%s %s %tD:%tT", nextFiscalYear, 
                                  periodString, shunivdate, shunivdate));
           // store these values--we will update whatever is there
           UniversityDate universityDate = new UniversityDate();
           universityDate.setUniversityFiscalYear(nextFiscalYear);
           universityDate.setUniversityDate(new Date(shunivdate.getTimeInMillis()));
           universityDate.setUniversityFiscalAccountingPeriod(periodString);
           getPersistenceBrokerTemplate().store(universityDate);
           // next day
           shunivdate.add(Calendar.DAY_OF_MONTH,1);
           // does this kick us into a new month and therefore a new accounting period?
           compareMonth = shunivdate.get(Calendar.MONTH);
           if (currentMonth != compareMonth)
           {
             period = period +1;
             periodString = String.format("%02d",period);
             currentMonth = compareMonth;
             //TODO:  debugging code
             if (period == 13)
             {
                 LOG.warn("the date comparison is not working properly");
                 break;
             }
           }
        }
        // store the end date
        UniversityDate universityDate = new UniversityDate();
        universityDate.setUniversityFiscalYear(nextFiscalYear);
        universityDate.setUniversityDate(new Date(shunivdate.getTimeInMillis()));
        universityDate.setUniversityFiscalAccountingPeriod(periodString);
        getPersistenceBrokerTemplate().store(universityDate);
        //TODO: temporary debugging code 
        LOG.debug(String.format("\n%s %s %tD:%tT\n", nextFiscalYear, 
                               periodString, shunivdate, shunivdate));
 }
    
    // these are private utility methods
    
    private java.sql.Date addYearToDate(Date inDate)
    {
        // OK.  Apparently the JDK is trying to offer a generic calendar to all
        // users.  java.sql.Date (which extends java.util.Date) is trying to create 
        // a DB independent date value.  both have settled on milliseconds since
        // midnight, January 1, 1970, Greenwich Mean Time.  This value is then
        // "normalized" to the local time zone when it is converted to a date.  But,
        // the constructors are based on the original millisecond value, and this
        // value is recoverable via the "time" methods in the classes.
        GregorianCalendar currentCalendarDate = new GregorianCalendar();
        // create a calendar object with no values set
        currentCalendarDate.clear();
        // set the calendar values using the "standard" millisecond value
        // this should represent the java.sql.Date value in the local time zone
        currentCalendarDate.setTimeInMillis(inDate.getTime());
        // add a year to the SQL date
        currentCalendarDate.add(GregorianCalendar.YEAR,1);
        // return the "standardized" value of the orginal date + 1 year
        return (new Date(currentCalendarDate.getTimeInMillis()));
    }
    
    private HashSet<String> buildMapOfExistingKeys(Integer RequestYear,
                                                   Class businessObject)
    {
        // this code builds and returns a hash set containing the composite
        // key string of rows that already exist in the relevant table for the
        // new fiscal year (we assume all the members of the composite key are
        // strings except the fiscal year.
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        // get space for the map
        HashSet<String> returnHash =
            new HashSet<String>(hashObjectSize(businessObject,criteriaID));
        // set up to query for the key fields
        String[] attrib = {""};  // we'll reorient this pointer when we know the size
        attrib = (String[]) 
            persistenceStructureService.getPrimaryKeys(businessObject).toArray(attrib);
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(businessObject,attrib,criteriaID);
        Iterator keyValues =
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (keyValues.hasNext())
        {
            Object[] keyObject = (Object[]) keyValues.next();
            // we assume the fiscal year is an integer, and the other keys are strings
            // OJB always returns BigDecimal for a number (including fiscal year), 
            // but we apply toString directly to the object, so we should be OK.
            StringBuffer concatKey = 
                new StringBuffer(keyObject[0].toString());
            for (int i = 1; i < keyObject.length; i++)
            {
                concatKey = concatKey.append(keyObject[i]);
            }
            returnHash.add(concatKey.toString());
        }
        return returnHash;        
    }
    
    private void deleteNewYearRows(Integer RequestYear, Class businessObject)
    {
        //  this gets rid of all the rows in the new fiscal year
        LOG.warn(String.format("\ndeleting %s for %d",
                               businessObject.getName(),RequestYear));
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,RequestYear);
        QueryByCriteria queryID = new QueryByCriteria(businessObject,criteriaID);
        getPersistenceBrokerTemplate().deleteByQuery(queryID);
        LOG.warn(String.format("\n rows for %d deleted",RequestYear));
        getPersistenceBrokerTemplate().clearCache();
    }
    
    public Integer fiscalYearFromToday()
    {
        //  we look up the fiscal year for today's date, and return it
        //  we return 0 if nothing is found
        Integer currentFiscalYear = new Integer(0);
        Date lookUpDate =
            dateTimeService.getCurrentSqlDateMidnight();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_DATE,
                              lookUpDate);
        String[] attrb = {KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR};
        ReportQueryByCriteria queryID =
            new ReportQueryByCriteria(UniversityDate.class, attrb, criteriaID);
        Iterator resultRow =
        getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (resultRow.hasNext())
        {
            currentFiscalYear = (Integer) ((BigDecimal)
                        ((Object[]) KFSUtils.retrieveFirstAndExhaustIterator(resultRow))[0]).intValue();
        }
        //TODO:
        LOG.debug(String.format("\nreturned from fiscalYearFromToday: %d",
                currentFiscalYear));
        //TODO:
        return currentFiscalYear;
    }
    
    //@@TODO:
    // this code is duplicated from GenesisDaoOjb.  we don't need to overload the
    // hashObjectSize method here
    // if this thing catches on, maybe we should make the hashObjectSize method 
    // a public method in a service
    //
    private Integer hashCapacity(Integer hashSize)
    {
        // this corresponds to a little more than the default load factor of .75
        // a rehash supposedly occurs when the actual number of elements exceeds
        // (load factor)*capacity
        // N rows < .75 capacity ==> capacity > 4N/3 or 1.3333N.  We add a little slop.
        Double tempValue = hashSize.floatValue()*(1.45);
        return (Integer) tempValue.intValue();
    }
    
    private Integer hashObjectSize(Class classID, Criteria criteriaID)
    {
        // this counts all rows
        String[] selectList = new String[] {"COUNT(*)"};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(classID, selectList, criteriaID);
        Iterator resultRows = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        while (resultRows.hasNext())
        {
            return(hashCapacity(((BigDecimal)((Object[]) resultRows.next())[0]).intValue()));
        }
        return (new Integer(1));
    }
  
    private String updateStringField(String newYearString,
                                     String oldYearString,
                                     String currentField)
    {
    /*
     *  this routine is reminiscent of computing in 1970, when disk space was
     *  scarce and every byte was fraught with meaning.  some fields are captions
     *  and titles, and they contain things like the fiscal year.  for the new
     *  year, we have to update these substrings in place, so they don't have to be
     *  updated by hand to display correct information in the application.
     *  we use the regular expression utilities in java
     */    
      Pattern pattern = Pattern.compile(oldYearString);
      Matcher matcher = pattern.matcher(currentField);
      return matcher.replaceAll(newYearString);
    }

    private String updateTwoDigitYear (String newYear,
                                       String oldYear,
                                       String currentString)
    {
    /*
     *  this routine is provided to update string fields which contain two-digit years
     *  that need to be updated for display. it is very specific, but it's necessary.
     *  "two-digit year" means the two numeric characters preceded by a non-numeric character.
     */
        // group 1 is the bounded by the outermost set of parentheses
        // group 2 is the first inner set
        // group 3 is the second inner set--a two-digit year at the beginning of the line
        String regExpString = "(([^0-9]{1}"+oldYear+")|^("+oldYear+"))";
        Pattern pattern = Pattern.compile(regExpString);
        Matcher matcher = pattern.matcher(currentString);
        // start looking for a match
        boolean matched = matcher.find();
        if (!matched)
        {
            // just return if nothing is found
            return currentString;
        }
        // we found something
        // we have to process it
        String returnString = currentString;
        StringBuffer outString = new StringBuffer();
        // is there a match at the beginning of the line (a match with group 3)?
        if (matcher.group(3) != null)
        {
            // there is a two-digit-year string at the beginning of the line
            // we want to replace it
            matcher.appendReplacement(outString,newYear);
            // find the next match if there is one
            matched = matcher.find();
        }
        while (matched)
        { 
           // the new string will no longer match with group 3
           // if there is still a match, it will be with group 2 
           // now we have to prefix the new year string with the same 
           // non-numeric character as the next match (hyphen, space, whatever)
           String newYearString = matcher.group(2).substring(0,1) + newYear;
           matcher.appendReplacement(outString, newYearString);
           matched = matcher.find();
        }
        // dump whatever detritus is left into the new string
        matcher.appendTail(outString);
        return outString.toString();
    }
    
    public void setDateTimeService(DateTimeService dateTimeService)
    {
        this.dateTimeService = dateTimeService;
    }
    
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService)
    {
        this.persistenceStructureService = persistenceStructureService;
    }
    
    
    public void setFiscalYearStartDate(GregorianCalendar fiscalYearStartDate)
    {
        // this routine can be used to reset the default start date for the 
        // fiscal year
        this.fiscalYearStartDate = fiscalYearStartDate;
    }

    // generic class to pass in types to the generic routines
    private class MakersMethods<T> 
    {
        // this is the signature used for an object that requires no
        // special processing
        private void makeMethod(Class ojbMappedClass,
                                Integer currentFiscalYear,
                                boolean replaceMode)
        {
            FiscalYearMakersFieldChangeAction changeAction = null;
            FiscalYearMakersFilterAction      filterAction = null;
            makeMethod(ojbMappedClass,
                    currentFiscalYear,
                    replaceMode,
                    changeAction,
                    filterAction);
        }
        
        // this is the signature used for an object which has special
        // filter criteria in the WHERE clause
        private void makeMethod(Class ojbMappedClass,
                                Integer currentFiscalYear,
                                boolean replaceMode,
                                FiscalYearMakersFilterAction filterAction)
        {
           FiscalYearMakersFieldChangeAction changeAction = null;
           makeMethod(ojbMappedClass,
                      currentFiscalYear,
                      replaceMode,
                      changeAction,
                      filterAction);
        }
        
        // this is the signature used for an object which requires changes
        // to fields other than KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR
        private void makeMethod(Class ojbMappedClass,
                                Integer currentFiscalYear,
                                boolean replaceMode,
                                FiscalYearMakersFieldChangeAction changeAction)
        {
            FiscalYearMakersFilterAction filterAction = null;
            makeMethod(ojbMappedClass,
                    currentFiscalYear,
                    replaceMode,
                    changeAction,
                    filterAction);
        }
        
        // this is the signature used for an object which has both special
        // filter criteria and required changes to additional fields
        private void makeMethod(Class ojbMappedClass,
                                Integer currentFiscalYear,
                                boolean replaceMode,
                                FiscalYearMakersFieldChangeAction changeAction,
                                FiscalYearMakersFilterAction filterAction)
        {
            if (laggingCopyCycle.contains(ojbMappedClass.getName()))
            {
                // this object is copied from LAST PERIOD into the CURRENT PERIOD
                currentFiscalYear = currentFiscalYear - 1;
            }
           if (replaceMode)
           {
               // we will replace any new year rows that exist
               try 
               {
                   genericSlashAndBurn(ojbMappedClass,
                                       currentFiscalYear,
                                       changeAction,
                                       filterAction);
               }
               catch (IllegalAccessException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
               catch (InvocationTargetException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
               catch (NoSuchFieldException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
               catch (NoSuchMethodException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
           }
           else
           {
               // we will only add any new year rows that do not already exist
               try 
               {
                   genericWarmAndFuzzy(ojbMappedClass,
                                       currentFiscalYear,
                                       changeAction,
                                       filterAction);
               }
               catch (IllegalAccessException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
               catch (InvocationTargetException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
               catch (NoSuchFieldException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
               catch (NoSuchMethodException ex)
               {
                   ex.printStackTrace();
                   RuntimeException newEx = 
                       new RuntimeException(String.format("\n failed for %s",
                                                          ojbMappedClass.getName()));
                   throw(newEx);
               }
           }
        }


        // routine to build rows for the coming fiscal year, replacing any that
        // already exist
        
        private void genericSlashAndBurn(Class ojbMappedClass,
                                         Integer currentFiscalYear,
                                         FiscalYearMakersFieldChangeAction changeAction,
                                         FiscalYearMakersFilterAction filterAction)
        throws NoSuchFieldException,
        NoSuchMethodException,
        IllegalAccessException, 
        InvocationTargetException
        {
            Integer newFiscalYear = currentFiscalYear + 1;
            String requestYearString = newFiscalYear.toString();
            Integer rowsRead = new Integer(0);
            Integer rowsWritten = new Integer(0);
            Integer rowsFailingRI = new Integer(0);
            LOG.warn(String.format("\n copying %s from %d to %d",
                                   ojbMappedClass.getName(),
                                   currentFiscalYear,
                                   newFiscalYear));
            // some parents have auto-update other than none in their relationship
            // with a child.  in this case, the child's rows were written when the
            // parent's rows were written, and writing the child's rows again now will
            // cause an optimistic locking exception.  we have already deleted any rows
            // the existed for the target year, so any rows there now have been written
            // by an auto-update, are therefore correct, and should not be recopied.
            // we check here for the existence of rows.
            Criteria checkCriteria = new Criteria();
            checkCriteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                     newFiscalYear);
            if (hashObjectSize(ojbMappedClass,checkCriteria) > 0)
            {
                LOG.warn(String.format("\n    %s rows for %d exist already from an auto-update",
                                       ojbMappedClass.getName(),newFiscalYear));
                return;
            }
            // build the list of parent keys already copied to the new year (if any)
            // the appropriate child foreign keys must exist in each parent
            // if they do not, this means that the parent row in the current fiscal
            // year was filtered out in the copy to the new fiscal year, and therefore
            // the corresponding child rows should not be copied either
            ParentKeyChecker<T> parentKeyChecker =
                new ParentKeyChecker<T>(ojbMappedClass, newFiscalYear);
            // get the rows from the previous year
            Criteria criteriaID = new Criteria();
            criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,currentFiscalYear);
            if (filterAction != null)
            {
                criteriaID.addAndCriteria(filterAction.customCriteriaMethod());
            }
            QueryByCriteria queryID = new QueryByCriteria(ojbMappedClass,criteriaID);
            Iterator<T> oldYearObjects = 
                getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
            while (oldYearObjects.hasNext())
            {
                rowsRead = rowsRead+1;
                T ourBO = oldYearObjects.next();
                // we have to set the fiscal year and the version number
                setCommonFields(ourBO, newFiscalYear);
                // we also set all the custom fields (which presumably are NOT
                // keys).  they may be foreign keys into the parent, and if they
                // are the parent check should reflect the new year values.
                if (!(changeAction == null))
                {
                   changeAction.customFieldChangeMethod(currentFiscalYear,
                                                        newFiscalYear,
                                                        ourBO);
                }
                // check to see if the row exists in all the parents
                if (!parentKeyChecker.childRowSatisfiesRI(ourBO))
                {
                  rowsFailingRI = rowsFailingRI+1;
                  continue;
                }
                // store the result
                getPersistenceBrokerTemplate().store(ourBO);
                rowsWritten = rowsWritten+1;
            }
            LOG.warn(String.format("\n%s:\n%d read = %d\n%d written = %d\nfailed RI = %d",
                    ojbMappedClass.getName(),
                    currentFiscalYear,rowsRead,
                    newFiscalYear,rowsWritten,
                    rowsFailingRI));
            // if a parent has inverse foreign keys into the child, when we copy the
            // parent the child rows will be in the cache as well of auto-retrieve is
            // true and proxy is false.  then, when we build the child rows later these
            // cached rows cause an OJB "optimistic locking" error.  we therefore remove
            // any cached rows after all the rows for each object have been copied to
            // the database
            getPersistenceBrokerTemplate().clearCache();
        }
        
       // routine to only add, not replace, rows for the coming fiscal year
        
        private void genericWarmAndFuzzy(Class ojbMappedClass,
                                         Integer currentFiscalYear,
                                         FiscalYearMakersFieldChangeAction changeAction,
                                         FiscalYearMakersFilterAction filterAction)
        throws NoSuchFieldException,
        NoSuchMethodException,
        IllegalAccessException, 
        InvocationTargetException
        {
           Integer newFiscalYear = currentFiscalYear+1; 
           String requestYearString = newFiscalYear.toString();
           Integer rowsRead = new Integer(0);
           Integer rowsWritten = new Integer(0);
           Integer rowsFailingRI = new Integer(0);
           LOG.warn(String.format("\n copying %s from %d to %d",
                   ojbMappedClass.getName(),
                   currentFiscalYear,
                   newFiscalYear));
           // build the list of parent keys already copied to the new year (if any)
           // the appropriate child foreign keys must exist in each parent
           // if they do not, this means that the parent row in the current fiscal
           // year was filtered out in the copy to the new fiscal year, and therefore
           // the corresponding child rows should not be copied either
           ParentKeyChecker<T> parentKeyChecker =
               new ParentKeyChecker<T>(ojbMappedClass, newFiscalYear);
           // get the hash set of keys of objects which already exist for the new 
           // year and will not be replaced
           HashSet existingKeys = 
               buildMapOfExistingKeys(newFiscalYear,ojbMappedClass);
           //
           String[] keyFields = {""};  // reorient this pointer when we know the size
           keyFields = (String[])
             persistenceStructureService.getPrimaryKeys(ojbMappedClass).toArray(keyFields);
           // get the rows from the previous year
           Criteria criteriaID = new Criteria();
           criteriaID.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,currentFiscalYear);
           if (filterAction != null)
           {
               criteriaID.addAndCriteria(filterAction.customCriteriaMethod());
           }
           QueryByCriteria queryID = new QueryByCriteria(ojbMappedClass,criteriaID);
           Iterator<T> oldYearObjects = 
               getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
           while (oldYearObjects.hasNext())
           {
               rowsRead = rowsRead+1;
               StringBuffer hashChecker = new StringBuffer(requestYearString);
               T ourBO = oldYearObjects.next();
               for (int i = 1; i < keyFields.length; i++)
               {
                   // 10/2007 some primary keys may not be strings: 
                   // we assume that the same value of a non-string will be converted to a string consistently
                   hashChecker.append(PropertyUtils.getSimpleProperty(ourBO,
                                       keyFields[i].toString()));
          }
               //TODO:
               //if (rowsRead%1007 == 1)
               //{
               //  LOG.warn(String.format("\n%s: row %d hash key = %s\n",
               //           ojbMappedClass.getName(),
               //           rowsRead,hashChecker.toString()));
               //}
               //TODO:
               if (existingKeys.contains(hashChecker.toString()))
               {
                   continue;
               }
               // we have to set the fiscal year and the version number
               setCommonFields(ourBO, newFiscalYear);
               // we also set all the custom fields (which presumably are NOT
               // keys).  they may be foreign keys into the parent, and if they
               // are the parent check should reflect the new year values.
               if (!(changeAction == null))
               {
                  changeAction.customFieldChangeMethod(currentFiscalYear,
                                                       newFiscalYear,
                                                       ourBO);
               }
               // check to see if the row exists in all the parents
               if (!parentKeyChecker.childRowSatisfiesRI(ourBO))
               {
                 rowsFailingRI = rowsFailingRI+1;
                 continue;
               }
               // store the result
               getPersistenceBrokerTemplate().store(ourBO);
               rowsWritten = rowsWritten+1;
           }
           LOG.warn(String.format("\n%s:\n%d read = %d\n%d written = %d\nfailed RI = %d",
                   ojbMappedClass.getName(),
                   currentFiscalYear,rowsRead,
                   newFiscalYear,rowsWritten,
                   rowsFailingRI));
           // if a parent has inverse foreign keys into the child, when we copy the
           // parent the child rows will be in the cache as well of auto-retrieve is
           // true and proxy is false.  then, when we build the child rows later these
           // cached rows cause an OJB "optimistic locking" error.  we therefore remove
           // any cached rows after all the rows for each object have been copied to
           // the database
           getPersistenceBrokerTemplate().clearCache();
        }
        
        private void setCommonFields(T ourBO, Integer newFiscalYear)
        throws NoSuchFieldException, 
               IllegalAccessException, 
               NoSuchMethodException,
               InvocationTargetException
        {
            // the compiler doesn't know the class of the object at this point,
            // so we can't use the methods contained in the object
            // the compiler would not be able to find them
            // PropertyUtils uses reflection at run time to find the correct set
            // method
            //
            // set the fiscal year
            PropertyUtils.setSimpleProperty(ourBO,
                                            KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                                            newFiscalYear);
            // set the version number (to avoid running up the meter as the
            // years fly by)
            // the version number is a field of the base class common to all
            // persistable business objects
            PropertyUtils.setSimpleProperty(ourBO,
                                            KFSPropertyConstants.VERSION_NUMBER,
                                            new Long(0));
            //fld =
            //ourBO.getClass().getSuperclass().getDeclaredField(KFSPropertyConstants.VERSION_NUMBER);
            //fld.setAccessible(true);
            //fld.set(ourBO, (Object) (new Long(0)));
        }
        
    };
 
/**********************************************************************************
 *                         This section handles RI                                *
 **********************************************************************************/

    // list of objects (from XML) that need to be copied for a new fiscal period
    private HashMap<String,Class> makerObjectsList = new HashMap<String,Class>(25);
    // list of objects that need to be copied coupled with a list of
    // other objects to be copied on which they have an RI dependency
    private HashMap<String,ArrayList<Class>> childParentMap =
        new HashMap<String,ArrayList<Class>>(25);
    // this is the source of the above map.  it is loaded from XML through a set 
    // method.  if there is no RI, initializing it here should make it empty.
    // since the code works for things that have no parents, the code should
    // still work if there is no RI and everybody is an orphan
    private HashMap<String,Class[]> childParentArrayMap =
            new HashMap<String,Class[]>(25);
    // this is the list of copy actions, one for each object to be copied
    // it is built in the setUp method
    private HashMap<String,FiscalYearMakersCopyAction> classSpecificActions;
    // this is the set of objects which are copied from LAST YEAR to the 
    // current year, instead of from the CURRENT YEAR to the next year
    private HashSet<String> laggingCopyCycle = new HashSet<String>(20);
    
    
    // the list of all the fiscal year makers objects
    public HashMap<String,Class> getMakerObjectsList()
    {
        return this.makerObjectsList;
    }
    public void setMakerObjectsList(HashMap<String,Class> makerObjectsList)
    {
        this.makerObjectsList = makerObjectsList;
        classSpecificActions = 
            new HashMap<String,FiscalYearMakersCopyAction>(makerObjectsList.size());
    }
    // this list of child/parent relationships for the fiscal year makers objects
    public HashMap<String,ArrayList<Class>> getChildParentMap()
    {
        return this.childParentMap;
    }
    
    public void setChildParentArrayMap(HashMap<String,Class[]> childParentArrayMap)
    {
        this.childParentArrayMap = childParentArrayMap;
        // Spring did not do the conversions of the XML necessary to create 
        // HashMap<String,ArrayList<Class>>.  (We got an ArrayList of strings.)
        // Since everything was written
        // for an ArrayList, we will convert the Class[] version (which Spring
        // can handle) to an ArrayList here.  (There is a way to get a "list"
        // view of an array, and this view is an ArrayList.  But we will create
        // a new one, which will be extensible, unlike the view.)
        childParentMap = 
            new HashMap<String,ArrayList<Class>>(childParentArrayMap.size());
        for (Map.Entry<String,Class[]> fromMap: childParentArrayMap.entrySet())
        {
            Class[] sourceArray = fromMap.getValue();
            ArrayList<Class> targetList = new ArrayList<Class>(sourceArray.length);
            for (int i = 0; i < sourceArray.length; i++)
            {
                targetList.add(i,sourceArray[i]);
            }
            childParentMap.put(fromMap.getKey(),targetList);
        }
    }
    
    public void setLaggingCopyCycle (HashSet<String> laggingCopyCycle)
    {
        this.laggingCopyCycle = laggingCopyCycle;
    }
    
    // this is a map of the execution order for the classes
    // the class name is followed by a class ID which contains the copy method
    // it is called at the end of setUp, and uses the classSpecificActions map 
    // built during setUp to get the copy action required
    private LinkedHashMap<String,FiscalYearMakersCopyAction> getCopyOrder()
    throws RuntimeException
    {
        // throw an exception if the lists don't match
        if (findChildParentXMLErrors())
        {
            RuntimeException ex = 
                new RuntimeException("\nXML class list and parent-child list are incompatible");
            throw(ex);
        }
        LinkedHashMap<String,FiscalYearMakersCopyAction> returnMap = 
            new LinkedHashMap<String,FiscalYearMakersCopyAction>(makerObjectsList.size());
        // make a copy of the child/parent map so we can iterate until all the
        // elements in the copy have been added to the copy order list.
        // we have to do a deep copy, not just a clone
        HashMap<String,ArrayList<Class>> childParentWkngMap =
            new HashMap<String,ArrayList<Class>>(childParentMap.size());
        for (Map.Entry<String,ArrayList<Class>> sourceMap: childParentMap.entrySet())
        {
           ArrayList<Class> srceList = sourceMap.getValue();
           ArrayList<Class> targetList = new ArrayList<Class>(srceList.size());
           Iterator<Class> copyIt = srceList.iterator();
           while (copyIt.hasNext())
           {
               targetList.add(copyIt.next());
           }
           childParentWkngMap.put(sourceMap.getKey(),targetList); 
        }
        // first, add to the list all makers objects which aren't children of anything
        for (Map.Entry<String,Class> orphans : makerObjectsList.entrySet())
        {
            if (!childParentWkngMap.containsKey(orphans.getKey()))
            {
                // the object is supposed to be a call back action
                // we will fill it in later
                // for now, we set it to null
                returnMap.put(orphans.getKey(),
                        classSpecificActions.get(orphans.getKey()));
            }
        }   
        //  since a child of parent X should not be a parent of a parent of
        //  parent X, this should work.  if at any pass through the loop
        //  we don't add to the copy order, there has to be a circular
        //  relationship, and we will trow an exception
        Integer numberAdded = new Integer(1);
        // we need to keep track of parentChildMap elements to be removed
        // we cannot do that in the for loop without a "concurrent modification exception"
        HashSet<String> removeList = 
             new HashSet<String>(childParentWkngMap.size());
        while (!childParentWkngMap.isEmpty())
        {
          // nothing was found on the last pass, so nothing will be found on
          // any of the subsequent passes either
          if (numberAdded.intValue() == 0)
          {
              RuntimeException ex =
                  new RuntimeException("child/parent XML map contains circular relationships");
              throw(ex);
          }
          numberAdded = 0;
          for (Map.Entry<String,ArrayList<Class>> parentList : childParentWkngMap.entrySet())
          {
              Iterator<Class> parents = 
                              parentList.getValue().iterator();
              if (!parents.hasNext())
              {
                  // all the parents have been added
                  // add the child to the returnMap and delete this entry
                  numberAdded = numberAdded+1;
                  // we will detect a null copy action later and throw an exception
                  returnMap.put(parentList.getKey(),
                                classSpecificActions.get(parentList.getKey()));
                  removeList.add(parentList.getKey());
                  continue;
              }
              // loop trough the parent list and try to add the parents to the 
              // copy order map
              while (parents.hasNext())
              {
                  Class nextParent = parents.next();
                  // check to see whether this parent is a child of someone else
                  if ((!childParentWkngMap.containsKey(nextParent.getName())) ||
                       (removeList.contains(nextParent.getName())))
                  {
                      // we will detect a null copy action later and throw an exception
                      returnMap.put(nextParent.getName(),
                              classSpecificActions.get(nextParent.getName()));
                      numberAdded = numberAdded+1;
                      parents.remove();
                  }
              }
          }
          // now we want to remove the parents whose children are already
          // in the delete list from the parentChildWkngMap
          Iterator<String> goners = removeList.iterator();
          while (goners.hasNext())
          {
              childParentWkngMap.remove(goners.next());
          }
          removeList.clear();
        }
        // now we have to verify that everything in the map has a valid copy action
        boolean screwedUpCopyOrder = false;
        for (Map.Entry<String,FiscalYearMakersCopyAction> copyActions :
             returnMap.entrySet())
        {
            if (copyActions.getValue() == null)
            {
                LOG.error(String.format("\n%s has no valid copy action",
                          copyActions.getKey()));
                screwedUpCopyOrder = true;
            }
        }
        if (screwedUpCopyOrder)
        {
            RuntimeException rex = new RuntimeException("\ninvalid copy order list");
            throw(rex);
        }
        return returnMap;
    }
    
    // this list specifies the delete order for the objects in the list
    private LinkedHashMap<String,Class> getDeleteOrder() throws RuntimeException
    {
        // throw an exception if the lists don't match
        if (findChildParentXMLErrors())
        {
            RuntimeException ex = 
                new RuntimeException("\nXML class list and parent-child list are incompatible");
            throw(ex);
        }
        // this should only be called once
        // we'll rebuild it on each call
        // first we need a parent child map
        HashMap<String,ArrayList<Class>> parentChildMap =
            createParentChildMap();
        // children must be deleted before parents
        LinkedHashMap<String,Class> returnList = 
            new LinkedHashMap<String,Class>(makerObjectsList.size());
        // first, add to the list all makers objects which aren't parents of anything
        for (Map.Entry<String,Class> childless : makerObjectsList.entrySet())
        {
            if (!parentChildMap.containsKey(childless.getKey()))
            {
                returnList.put(childless.getKey(), childless.getValue());
            }
        }
        // since a child of parent X cannot be a parent to a parent of X,
        // this loop should work.  we continue until the list is empty
        // if there is a pass in which nothing is written, this implies a 
        // circular relationship and we will throw an exception
        Integer numberAdded = new Integer(1);
        // we need to keep track of parentChildMap elements to be removed
        // we cannot do that in the for loop without a "concurrent modification exception"
        HashSet<String> removeList = 
            new HashSet<String>(parentChildMap.size());
        while (!parentChildMap.isEmpty())
        {
            // if the last loop didn't do anything, then the 
            // next pass won't either.  it means the parent/child map has a circular
            // relationship
            if (numberAdded.intValue() == 0)
            {
                RuntimeException ex =
                 new RuntimeException("child/parent XML map contains circular relationships");
                throw(ex);
            }
            // do another iteration over the shrunken list, and see what else
            // we can stick in the delete order list
            numberAdded = 0;
            for(Map.Entry<String,ArrayList<Class>> parents : parentChildMap.entrySet())
            {
                // try to add the children to the return list of classes
                Iterator<Class> children = parents.getValue().iterator();
                // if there are no children, we have already added them all
                // it is safe to simply add the parent to the map and remove the row
                if (!children.hasNext())
                {
                    numberAdded = numberAdded+1;
                    returnList.put(parents.getKey(),
                                   makerObjectsList.get(parents.getKey()));
                    // add this to the remove list
                    removeList.add(parents.getKey());
                    continue;
                }
                while (children.hasNext())
                {
                    Class nextChild = children.next();
                    // if the child is already in the map, remove the child
                    if (returnList.containsKey(nextChild.getName()))
                    {
                        numberAdded = numberAdded + 1;
                        children.remove();
                        continue;
                    }
                    // if the child is not a parent of anything still in the list
                    // we add it to the list and remove it
                    if ((!parentChildMap.containsKey(nextChild.getName()))||
                        (removeList.contains(nextChild.getName())))
                    {
                        numberAdded = numberAdded+1;
                        returnList.put(nextChild.getName(),nextChild);
                        children.remove();
                    }
                }
            }
            // now we want to remove the parents whose children are already
            // in the delete list from the parentChildMap
            Iterator<String> goners = removeList.iterator();
            while (goners.hasNext())
            {
                parentChildMap.remove(goners.next());
            }
            removeList.clear();
        }
        return returnList;
    }
    
    // this is an "action", or callback, class
    // it allows us to build an instance at run time for each child, after the parents
    // have already been built for the coming fiscal period
    // (1) for each parent, store the values that exist for the child's foreign keys
    // (2) provide a method that can be called by each child row read from the base
    //     period.  the method will check that the child has the proper RI relationship
    //     with at least one row from each parent.
    public class ParentKeyChecker<C> 
    {
        private ParentClass<C>[] parentClassList = null;
        public ParentKeyChecker(Class childClass, Integer RequestYear)
        {
            String testString = childClass.getName();
            if (childParentMap.containsKey(testString))
            {
                ArrayList<Class> parentClasses = childParentMap.get(testString);
                parentClassList = 
                    (ParentClass<C>[]) new ParentClass[parentClasses.size()];
                for (int i = 0; i < parentClasses.size(); i++)
                {
                    parentClassList[i] = new ParentClass<C>(parentClasses.get(i),
                                                            childClass, 
                                                            RequestYear);
                }
            }
        }
        public boolean childRowSatisfiesRI (C ourBO)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
        {
            boolean returnValue = true;
            if (parentClassList == null)
            {
                return returnValue;
            }
            for (int i = 0; i < parentClassList.length; i++)
            {
                returnValue = returnValue && parentClassList[i].isInParent(ourBO); 
            }
            return returnValue;            
        }
    };
    

    // this class is used to construct a parent key hashmap, and provide a method
    // to verify that a business object of type C matches on its foreign key 
    // fields with the parent
    public class ParentClass<C>
    {
        private String[] childKeyFields;
        private String[] parentKeyFields;
        private HashSet<String> parentKeys = new HashSet<String>(1);
        
        // the constructor will initialize the key hashmap for this parent object
        // it will also get the foreign key fields from the persistence data structure
        // (the assumption is that the fields names returned are the same in both the
        //  parent class and the child class).
        // try to set this up so that if the parent/child relationship does not exist
        // in OJB, we can issue a warning message and go on, and all the methods
        // will still behave properly
        public ParentClass(Class parentClass, 
                           Class childClass,
                           Integer RequestYear)
        {   
            // fill in the key field names
            //TODO: fix this--we need the child class as well as the parentClass
            ReturnedPair<String[],String[]> keyArrays =
                fetchForeignKeysToParent(childClass,
                                         parentClass);
            childKeyFields = keyArrays.getFirst();
            parentKeyFields = keyArrays.getSecond();
            if (childKeyFields != null)
            {
              // build a query to get the keys already added to the parent
              Criteria criteriaID = new Criteria();
              criteriaID.addEqualTo(KFSConstants.UNIV_FISCAL_YR,RequestYear);
              ReportQueryByCriteria queryID =
                  new ReportQueryByCriteria(parentClass, parentKeyFields,
                                            criteriaID, true);
              // build a hash set of the keys in the parent
              parentKeys = new HashSet<String>(hashCapacity(queryID));
              Iterator parentRows =
                  getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
              while (parentRows.hasNext())
              {  
                  parentKeys.add(buildKeyString((Object[]) parentRows.next()));  
              }
            }
        }
        
        private String buildChildTestKey(C ourBO)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
        {
            StringBuffer returnKey = new StringBuffer("");
            // we will convert all the keys to strings
            for (int i = 0; i < childKeyFields.length; i++)
            {
              returnKey.append(PropertyUtils.getProperty(ourBO,childKeyFields[i].toString()));
            }
            return returnKey.toString();
        }
        
        // method to test whether a key of the child row matches one in parent
        public boolean isInParent(C ourBO)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
        {
            if (childKeyFields == null)
            {
                return false;
            }
            return(parentKeys.contains(buildChildTestKey(ourBO)));
        }
        
    }

/*************************************************************************************
 *                                     private methods
 *************************************************************************************/    

    
    private void addCopyAction(Class objectToCopy,
                               FiscalYearMakersCopyAction copyAction)
    {
        classSpecificActions.put(objectToCopy.getName(),copyAction);
    }
    
    private String buildKeyString (Object[] inKeys)
    {
        StringBuffer stringBuilder = new StringBuffer();
        // we always assume the first key is the fiscal year
        // OJB returns a BigDecimal for this (it's a numeric field, and
        // in some databases--notably Oracle--every numeric field is stored
        // as a number)
        // stringBuilder.append(((Integer)((BigDecimal) inKeys[0]).intValue()).toString());
        // for (int i = 1; i < inKeys.length; i++)
        // {
        //   stringBuilder.append((String)inKeys[i]);
        // }
        //
        // when the parent rows were cached (not yet written to the data base), the
        // loop commented out above gave a java.lang.String cast exception.
        // this seems to indicate that BigDecimal is returned only from Oracle, and
        // not from cached objects.  we therefore made the assumption that toString
        // will work properly for every type of field we are likely to encounter
        for (int i = 0; i < inKeys.length; i++)
        {
            stringBuilder.append(inKeys[i].toString());
        }
        return stringBuilder.toString();
    }
    
    private HashMap<String,ArrayList<Class>> createParentChildMap()
    {
        HashMap<String,ArrayList<Class>> returnMap =
            new HashMap<String,ArrayList<Class>>(makerObjectsList.size());
        // we've checked that every child has a list of unique parents
        // so, all we have to do is read the list of children and reverse
        // the map
        for (Map.Entry<String,ArrayList<Class>> childMap: childParentMap.entrySet())
        {
            String childName = childMap.getKey();
            Class childClass = makerObjectsList.get(childName);
            ArrayList parentArray = childMap.getValue();
            for (int j = 0; j < parentArray.size(); j++)
            {
                String parentName = ((Class) parentArray.get(j)).getName();
                if (returnMap.containsKey(parentName))
                {
                  ArrayList childArray = returnMap.get(parentName);
                  childArray.add(childClass);
                }
                else
                {
                  ArrayList childArray = new ArrayList(3);
                  childArray.add(childClass);
                  returnMap.put(parentName,childArray);
                }
            }
        }
        return returnMap;
    }
    
    private boolean findChildParentXMLErrors ()
    {
        // this routine looks for two types of errors
        // (1) a child or parent is NOT in the makers object list (fatal) from XML
        // (2) some of the child's parents are listed more than once (warning)
        boolean problemsInXML = false;
        for (Map.Entry<String,ArrayList<Class>> childMap : childParentMap.entrySet())
        {
            String childName = childMap.getKey();
            ArrayList parentList = childMap.getValue();
            ArrayList removeList = new ArrayList(parentList.size());
            // check for a problem child
            if (! makerObjectsList.containsKey(childName))
            {
                problemsInXML = true;
                LOG.error(String.format("\nchild %s is not in the fiscal period copy list",
                          childName));
            }
            for (int i = 0; i < parentList.size(); i++)
            {
                String parentName = ((Class) parentList.get(i)).getName();
                if (! makerObjectsList.containsKey(parentName))
                {
                    problemsInXML = true;
                    LOG.error(String.format("\nparent %s of child %s is not in the fiscal period copy list",
                              parentName,childName));
                }
                for (int j = i+1; j < parentList.size(); j++)
                {
                   String anotherParent = ((Class) parentList.get(j)).getName();
                   if (parentName.compareTo(anotherParent)== 0)
                   {
                      // we have a duplicate--add it to the drop list
                      LOG.warn(String.format("\nchild %s has parent %s listed twice in XML\n",
                                             childName,anotherParent));

                      removeList.add(j);
                   }
                }
                // get rid of the duplicate elements
                for (int k = 0; k < removeList.size(); k++)
                {
                   parentList.remove(removeList.get(k));                    
                }
            }
        }
        return problemsInXML;
    }
    
    private ReturnedPair<String[],String[]> fetchForeignKeysToParent(Class childClass,
                                                                     Class parentClass)
    {
     ReturnedPair<String[],String[]> returnObject =
         new ReturnedPair<String[],String[]>();
     // we can have two kinds of relationships in Kuali
     // (1) a child contains a foreign key to the primary keys of its RI parent (a 1:1
     //     relationship in OJB).  The parent object is coded in XML with a 
     //     reference-descriptor
     // (2) a child has a many:1 relationship with its RI parent.  in this case, the
     //     parent has foreign keys into the primary keys of the child.  The child
     //     object is coded in XML with a collection-descriptor
     //  this routine gets the parent keys and the child keys for the relationship,
     //  so we can build a map of all the values for those keys that have already
     //  been copied into the parent for the new year.  as each child row is about
     //  to be copied, we check to see whether its key values match one of the sets
     //  of values in the parent.  if they do not, we skip the child row.  
     //  this routine gives us the key field names we need to accomplish that.
     /*
      *  first look for the 1:1 relationship 
      */       
        returnObject =
            fetchFKToParent(childClass, parentClass);
        if (!(returnObject.getFirst() == null))
        {
          return returnObject;
        }
       /*
        * assume it's a 1:m relationship, and look for a collection-descriptor
        * if we can't find one, we'll issue a warning and return an empty pair
        * of arrays 
        */
        return (fetchFKToChild(parentClass,childClass));
    }

    private ReturnedPair<String[],String[]> fetchFKToChild(Class parentClass,
                                                           Class childClass)
    {
        String[] childKeyFields;
        String[] parentKeyFields;
        ReturnedPair<String[],String[]> returnObject = 
            new ReturnedPair<String[],String[]>();
        // first we have to find the attribute name of the parent reference to the child
        // class
        HashMap<String,Class> collectionObjects = (HashMap<String,Class>)
            persistenceStructureService.listCollectionObjectTypes(parentClass);
        String attributeName = null;
        String childClassID = childClass.getName();
        for (Map.Entry<String,Class> attributeMap: collectionObjects.entrySet())
        {
            if (childClassID.compareTo(attributeMap.getValue().getName()) == 0)
            {
                // the name of the child class matches a collection class
                // this is the attribute we want
                attributeName = attributeMap.getKey();
                break;
            }
        }
        // now we have to use the attribute to look up the inverse foreign keys
        if (attributeName == null)
        {
            // write a warning and return an empty key set
            LOG.warn(String.format("\n%s does not have a collection reference to %s\n",
                     parentClass.getName(),childClassID));
            return returnObject;
        }
        HashMap<String,String> keyMap = (HashMap<String,String>)
          persistenceStructureService.getInverseForeignKeysForCollection(parentClass,
                                                                 attributeName);
        childKeyFields = new String[keyMap.size()];
        parentKeyFields = new String[keyMap.size()];
        // the primary key names refer to child fields
        // the foreign key names refer to parent fields
        // (persistenceStructureService assumes that the child fields match the
        //  parent primary key fields in order, AND that the first child field
        //  corresponds to the first parent primary key field, the second to the
        //  second, etc.  this is apparently OJB's assumption as well.  in a 1:many
        //  relationship, all the parent primary key fields must be used (and 
        //  possibly some other fields)--since the parent row must be unique, but only
        //  some of the child's primary keys will be used)
        int i = 0;
        for (Map.Entry<String,String> fkPkPair: keyMap.entrySet())
        {
            parentKeyFields[i] = fkPkPair.getKey();
            childKeyFields[i] = fkPkPair.getValue();
            i = i + 1;
        }
        returnObject.setFirst(childKeyFields);
        returnObject.setSecond(parentKeyFields);
        return returnObject;
    }

    private ReturnedPair<String[],String[]> fetchFKToParent(Class childClass,
                                                            Class parentClass)
    {
        String[] childKeyFields;
        String[] parentKeyFields;
        ReturnedPair<String[],String[]> returnObject = 
            new ReturnedPair<String[],String[]>();
        // first we have to find the attribute name of the reference to the parent
        // class
        HashMap<String,Class> referenceObjects = (HashMap<String,Class>)
            persistenceStructureService.listReferenceObjectFields(childClass);
        String attributeName = null;
        String parentClassID = parentClass.getName();
        for (Map.Entry<String,Class> attributeMap: referenceObjects.entrySet())
        {
            if (parentClassID.compareTo(attributeMap.getValue().getName()) == 0)
            {
                // the name of the parent class matches a reference class
                // this is the attribute we want
                attributeName = attributeMap.getKey();
                break;
            }
        }
        // now we have to use the attribute to look up the foreign keys
        if (attributeName == null)
        {
            // write a warning and return an empty key set
            LOG.warn(String.format("\n%s does not have a child reference to %s\n",
                     childClass.getName(),parentClassID));
            return returnObject;
        }
        HashMap<String,String> keyMap = (HashMap<String,String>)
          persistenceStructureService.getForeignKeysForReference(childClass,
                                                                 attributeName);
        childKeyFields = new String[keyMap.size()];
        parentKeyFields = new String[keyMap.size()];
        // the primary key names refer to parent fields
        // the foreign key names refer to child fields
        // (persistenceStructureService assumes that the child fields match the
        //  parent primary key fields in order, AND that the first child field
        //  corresponds to the first parent primary key field, the second to the
        //  second, etc.  this is apparently OJB's assumption as well.)
        int i = 0;
        for (Map.Entry<String,String> fkPkPair: keyMap.entrySet())
        {
            childKeyFields[i] = fkPkPair.getKey();
            parentKeyFields[i] = fkPkPair.getValue();
            i = i + 1;
        }
        returnObject.setFirst(childKeyFields);
        returnObject.setSecond(parentKeyFields);
        return returnObject;
    }

    private Integer hashCapacity(ReportQueryByCriteria queryID)
    {
        // this corresponds to a load factor of a little more than the default load factor
        // of .75
        // (a rehash supposedly occurs when the actual number of elements exceeds
        //  hashcapacity*(load factor).  we want to avoid a rehash)
        //  N rows < .75*capacity ==> capacity > 4N/3 or 1.3333N  We add a little slop.
        Integer actualCount = new Integer(getPersistenceBrokerTemplate().getCount(queryID));
        return ((Integer)((Double)(actualCount.floatValue()*(1.45))).intValue());
    }

    private PersistenceStructureWindow persistenceStructureWindow = null;
    
    public void resetCascades()
    {
        // turnOffCascades should always be called, but if it hasn't been, there is no need to call this
        if (persistenceStructureWindow == null)
        {
            return;
        };
        persistenceStructureWindow.restoreCascading();
    }
    
    private void turnOffCascades()
    {
        //
        //  this routine is designed to solve a problem caused by auto-xxx settings in the OJB-repostiory
        //  auto-update or auto-delete settings other than "none" will cause row(s) for a linked object to be written or 
        //  deleted as soon as the row for the linking object is.  this circumvents our parent-child paradigm by which we
        //  ensure deletes and copies are done in an order that will not violate referential integrity constraints.
        //  for example, suppose a parent A is linked to a child B, which has auto-update="object".  B may have an RI 
        //  constraint on C, while A has nothing to do with C.  our copy order will allow A to be copied before C.  auto-update="object"
        //  copies row(s) from B at the same time a row from A is copied.  since no rows from C have been copied yet (C follows A
        //  in the copy order, the attempt to store the rows of B will violate RI--the required rows from C are not in the DB yet.
        //  (an example as of October, 2007 is A = OrganizationReversion, B = OrganizationReversionDetail, and C = ObjectCode)
        //  
        //  this routine dynamically switches off the auto-update and auto-delete in the OJB repository loaded in memory.  this should
        //  affect only the current run, makes no permanent changes, and will not affect the performance of any documents.  the 
        //  assumption is that this code is running in its own Java container, which will go away when the run is complete.
        //
        // set up the window into the OJB persistence structure
        persistenceStructureWindow = new PersistenceStructureWindow(); 
        for (Map.Entry<String,ArrayList<Class>> childMap : childParentMap.entrySet())
        {
           // get the class from the child name 
           Class childClass = makerObjectsList.get(childMap.getKey());
           ArrayList<Class> parentList = childMap.getValue();
           for (int i = 0; i < parentList.size(); i++ )
           {
               Class parentClass = parentList.get(i);
               persistenceStructureWindow.inhibitCascading(childClass, parentClass);
               persistenceStructureWindow.inhibitCascading(parentClass, childClass);
           }
        }
        
    }

/****************************************************************************************************************************************************
 * 
 *   these classes belong in the persistence structure service.  pending that, we use them here because we need them.  we indicate below which
 *   should be public and which should not.  
 *   this should only be used in BATCH, where nothing that needs to auto-update or auto-delete is likely to access the parent-child objects.
 *   for fiscal year makers, this condition is met.  for batch routines that use a plug-in or create and store documents, it may not be.
 * 
 ****************************************************************************************************************************************************/    
    private class PersistenceStructureWindow
    {
      private DescriptorRepository descriptorRepository;

      // these save enough information from the repository so we can restore the auto-xxx fields we change
      // the size of the hashmaps should be more than sufficient to change 16-17 tables
      // we shouldn't have to change anywhere close to that many
      private HashMap<CollectionDescriptor,String[]> collectionStore            = new HashMap<CollectionDescriptor,String[]>(25);
      private HashMap<CollectionDescriptor,String[]> collectionDelete           = new HashMap<CollectionDescriptor,String[]>(25);
      private HashMap<ObjectReferenceDescriptor,String[]> objectReferenceStore  = new HashMap<ObjectReferenceDescriptor,String[]>(25);
      private HashMap<ObjectReferenceDescriptor,String[]> objectReferenceDelete = new HashMap<ObjectReferenceDescriptor,String[]>(25);
      
      public PersistenceStructureWindow ()
      {
          MetadataManager metadataManager = MetadataManager.getInstance();
          descriptorRepository = metadataManager.getGlobalRepository();
          
      }
      
      public void inhibitCascading (Class referencingClass, Class targetClass)
      {
        /* this mehtod should be public in the persistence structure service */
        /* it looks for reference descriptors and collection descriptors in the source class that refer to 
         * the target class and specify an auto-delete or auto-update.  it turns those functions off for the
         * remainder of the batch run.
         */
         /* a given class will not have a 1:1 reference and a 1:m reference to the same target class */ 
         if  (fixReferences(referencingClass, targetClass)) 
         {
             return;
         }
         fixCollections(referencingClass, targetClass);
      }

      private ClassDescriptor getOJBDescriptor (Class boClass)
      {
          ClassDescriptor classDescriptor = null;
          try
          {
            classDescriptor = descriptorRepository.getDescriptorFor(boClass);
          }
          catch (ClassNotPersistenceCapableException ex)
          {
              LOG.warn(String.format("\n\nClass %s is non-presistable\n--> %s",
                       boClass.getName(),ex.getMessage()));
              // we'll just let things go at this point and hope for the best with RI
              return classDescriptor;
          }
            return classDescriptor;
      }
      
      private boolean fixReferences (Class referencingClass, Class targetClass)
      {
          ClassDescriptor classDescriptor = getOJBDescriptor(referencingClass);
          if (classDescriptor == null)
          {
              // class isn't persistable--no reason to continue
              return true;
          }
          Collection<ObjectReferenceDescriptor> referenceIDAttributes = classDescriptor.getObjectReferenceDescriptors(false);
          for (ObjectReferenceDescriptor objReferenceDescriptor : referenceIDAttributes)
          {
             if (targetClass.getName().compareTo(objReferenceDescriptor.getItemClassName()) == 0)
             {
                 LOG.debug(String.format("\n\nfound reference to %s in %s",
                          targetClass.getName(),referencingClass.getName()));
                 
                 if (objReferenceDescriptor.getCascadingDelete() != ObjectReferenceDescriptor.CASCADE_NONE)
                 {
                     // we want to issue a message whenever we toggle--so store three things
                     String[] infoString = {targetClass.getName(),referencingClass.getName(),
                                            objReferenceDescriptor.getCascadeAsString(objReferenceDescriptor.getCascadingDelete())};
                     objectReferenceDelete.put(objReferenceDescriptor,infoString);
                     objReferenceDescriptor.setCascadingDelete(ObjectReferenceDescriptor.CASCADE_NONE);
                     LOG.warn(String.format("\nreset auto-delete = %s\nfor %s in %s",
                              infoString[2],targetClass.getName(),referencingClass.getName()));          
                 }
                 if (objReferenceDescriptor.getCascadingStore() != ObjectReferenceDescriptor.CASCADE_NONE)
                 {
                     // we want to issue a message whenever we toggle--so store three things
                     String[] infoString = {targetClass.getName(),referencingClass.getName(),
                             objReferenceDescriptor.getCascadeAsString(objReferenceDescriptor.getCascadingStore())};
                     objectReferenceStore.put(objReferenceDescriptor,infoString); 
                     objReferenceDescriptor.setCascadingStore(ObjectReferenceDescriptor.CASCADE_NONE);
                     LOG.warn(String.format("\nreset auto-update = %s\nfor %s in %s",
                              infoString[2],targetClass.getName(),referencingClass.getName()));          
                 }
                 return true;
                 // a given class will not have a reference-id and a collection-ref-id to the same target class
             }
          }
          return false;
      }
      
      private boolean fixCollections (Class referencingClass, Class targetClass)
      {
          ClassDescriptor classDescriptor = getOJBDescriptor(referencingClass);
          if (classDescriptor == null)
          {
              // class isn't persistable--no reason to continue
              return true;
          }
          Collection<CollectionDescriptor>  collectionIDAttributes = classDescriptor.getCollectionDescriptors(false);
          for (CollectionDescriptor collectionDescriptor : collectionIDAttributes)
          {
              if (targetClass.getName().compareTo(collectionDescriptor.getItemClassName()) == 0)
              {
                  LOG.debug(String.format("\n\nfound collection reference to %s in %s",
                           targetClass.getName(),referencingClass.getName()));
                  
                  if (collectionDescriptor.getCascadingDelete() != CollectionDescriptor.CASCADE_NONE)
                  {
                      // we want to issue a message whenever we toggle--so store three things
                      String[] infoString = {targetClass.getName(),referencingClass.getName(),
                                             collectionDescriptor.getCascadeAsString(collectionDescriptor.getCascadingDelete())};
                      collectionDelete.put(collectionDescriptor,infoString);
                      collectionDescriptor.setCascadingDelete(CollectionDescriptor.CASCADE_NONE);
                      LOG.warn(String.format("\nreset auto-delete = %s \nfor %s in %s",
                               infoString[2],targetClass.getName(),referencingClass.getName()));          
                  }
                  if (collectionDescriptor.getCascadingStore() != CollectionDescriptor.CASCADE_NONE)
                  {
                      // we want to issue a message whenever we toggle--so store three things
                      String[] infoString = {targetClass.getName(),referencingClass.getName(),
                              collectionDescriptor.getCascadeAsString(collectionDescriptor.getCascadingStore())};
                      collectionStore.put(collectionDescriptor,infoString); 
                      collectionDescriptor.setCascadingStore(CollectionDescriptor.CASCADE_NONE);
                      LOG.warn(String.format("\nreset auto-update = %s \nfor %s in %s",
                               infoString[2],targetClass.getName(),referencingClass.getName()));          
                  }
                  return true;
                  // a given class will not have a reference-id and a collection-ref-id to the same target class
              }
           }
          return false;
      }
      
      public void restoreCascading()
      {
        // auto deletes in collections
        for (Map.Entry<CollectionDescriptor,String[]> restoreTargets : collectionDelete.entrySet())
        {
            String[] infoString = restoreTargets.getValue();
            CollectionDescriptor collectionDesc = restoreTargets.getKey();
            collectionDesc.setCascadingDelete(infoString[2]);
            LOG.warn(String.format("\nauto-delete reset to %s\nfor %s in %s",
                     collectionDesc.getCascadeAsString(collectionDesc.getCascadingDelete()),
                     infoString[0],infoString[1]));
        }
        // auto updates in collections
        for (Map.Entry<CollectionDescriptor,String[]> restoreTargets : collectionStore.entrySet())
        {
            String[] infoString = restoreTargets.getValue();
            CollectionDescriptor collectionDesc = restoreTargets.getKey();
            collectionDesc.setCascadingStore(infoString[2]);
            LOG.warn(String.format("\nauto-update reset to %s\nfor %s in %s",
                     collectionDesc.getCascadeAsString(collectionDesc.getCascadingStore()),
                     infoString[0],infoString[1]));
        }
        // auto deletes in references
        for (Map.Entry<ObjectReferenceDescriptor,String[]> restoreTargets : objectReferenceDelete.entrySet())
        {
            String[] infoString = restoreTargets.getValue();
            ObjectReferenceDescriptor objReferenceDesc = restoreTargets.getKey();
            objReferenceDesc.setCascadingDelete(infoString[2]);
            LOG.warn(String.format("\nauto-delete reset to %s\nfor %s in %s",
                     objReferenceDesc.getCascadeAsString(objReferenceDesc.getCascadingDelete()),
                     infoString[0],infoString[1]));
        }
        // auto updates in collections
        for (Map.Entry<ObjectReferenceDescriptor,String[]> restoreTargets : objectReferenceStore.entrySet())
        {
            String[] infoString = restoreTargets.getValue();
            ObjectReferenceDescriptor objReferenceDesc = restoreTargets.getKey();
            objReferenceDesc.setCascadingStore(infoString[2]);
            LOG.warn(String.format("\nauto-update reset to %s\nfor %s in %s",
                     objReferenceDesc.getCascadeAsString(objReferenceDesc.getCascadingStore()),
                     infoString[0],infoString[1]));
        }
      }
    }
    
//  this is a handy junk inner class that allows us to return two things from a method
 private class ReturnedPair<S,T>
 {
     S firstObject;
     T secondObject;
     public ReturnedPair()
     {
       this.firstObject = null;
       this.secondObject = null;
     }
     public S getFirst()
     {
         return this.firstObject;
     }
     public T getSecond()
     {
         return this.secondObject;
     }
     public void setFirst(S firstObject)
     {
         this.firstObject = firstObject;
     }
     public void setSecond (T secondObject)
     {
         this.secondObject = secondObject;
     }
 }



    
    //@@TODO: remove this test routine
    public void testUpdateTwoDigitYear()
    {
        String oldYear = new String("08");
        String newYear = new String("11");
        String testString = new String("08-09 x 08 07-08 08-09 09 08 08");
        String newString = updateTwoDigitYear(newYear, oldYear, testString);
        LOG.warn(String.format("\n test of updateTwoDigitYear:\n input = %s\n output = %s\n from: %s, to:%s  ",
                 testString, newString, oldYear, newYear));
        testString = new String("x08-09 x 08 07-08 08-09 09 tail");
        newString = updateTwoDigitYear(newYear, oldYear, testString);
        LOG.warn(String.format("\n test of updateTwoDigitYear:\n input = %s\n output = %s\n from: %s, to:%s  ",
                 testString, newString, oldYear, newYear));
        testString = new String(" nada ");
        newString = updateTwoDigitYear(newYear, oldYear, testString);
        LOG.warn(String.format("\n test of updateTwoDigitYear:\n input = %s\n output = %s\n from: %s, to:%s  ",
                 testString, newString, oldYear, newYear));
    }
    
    //TODO: remove these test routines
    
    public void testRIRelationships()  throws NoSuchMethodException,
                                              IllegalAccessException,
                                              InvocationTargetException
    {
        // print the object list
        LOG.warn(String.format("\n\nFiscalYearMakersObjects:\n\n"));
        for (Map.Entry<String,Class> makerObjects :
             makerObjectsList.entrySet())
        {
            LOG.warn(String.format("\nkey: %s, class name: %s",
                     makerObjects.getKey(),makerObjects.getValue().getName()));
        }
        // print the child/parent list
        LOG.warn(String.format("\n\nchild key, parent classes:\n\n"));
        for (Map.Entry<String,ArrayList<Class>> childParents : childParentMap.entrySet())
        {
            Iterator<Class> parents =
                            childParents.getValue().iterator();
            LOG.warn(String.format("\nchild: %s has %d parents",
                     childParents.getKey(),childParents.getValue().size()));
            Class childClass = makerObjectsList.get(childParents.getKey());
            while (parents.hasNext())
            {
                Class parentClass = parents.next();
                LOG.warn(
                String.format("\n       parent class name: %s",
                        parentClass.getName()));
                ReturnedPair<String[],String[]> keySets =
                    fetchForeignKeysToParent(childClass,parentClass);
                String[] childKeys  = keySets.getFirst();
                String[] parentKeys = keySets.getSecond();
                LOG.warn(String.format("\n       ------child keys-----"));
                for (int i = 0; i < childKeys.length; i++)
                {
                   LOG.warn(String.format("\n       %s",childKeys[i])); 
                }
                LOG.warn(String.format("\n       ------parent keys-----"));
                for (int i = 0; i < parentKeys.length; i++)
                {
                   LOG.warn(String.format("\n       %s",parentKeys[i])); 
                }
            }
        }
        // print the delete order
        LOG.warn(String.format("\n\nDelete Order:\n\n"));
        LinkedHashMap<String,Class> deleteOrder = getDeleteOrder();
        for (Map.Entry<String,Class> orderedDeleteList: deleteOrder.entrySet())
        {
            LOG.warn(String.format("\n  key: %s, class name: %s",
                    orderedDeleteList.getKey(),
                    orderedDeleteList.getValue().getName()));
        }
        // print the copy order
        // (this should fail with an exception if not all the objects have been 
        //  added to setUpRun)
        LOG.warn(String.format("\n\nCopy Order:\n\n"));
        LinkedHashMap<String,FiscalYearMakersCopyAction> copyOrder = 
            setUpRun(2010,false);     
        for (Map.Entry<String,FiscalYearMakersCopyAction> copyOrderList: copyOrder.entrySet())
        {   
            if (copyOrderList.getValue() == null)
            {
                LOG.warn(String.format("\n  key: %s, object value is null",
                        copyOrderList.getKey()));
            }
            else
            {
              LOG.warn(String.format("\n  key: %s, object value %s",
                       copyOrderList.getKey(),
                       copyOrderList.getValue().toString()));
            }
        }
        //  reprint this, to make sure it hasn't been corrupted in building
        //  the copy order data structure
        LOG.warn(String.format("\n\nchild key, parent classes:\n\n"));
        for (Map.Entry<String,ArrayList<Class>> childParents : childParentMap.entrySet())
        {
            Iterator<Class> parents =
                            childParents.getValue().iterator();
            LOG.warn(String.format("\nchild: %s has %d parents",
                     childParents.getKey(),childParents.getValue().size()));
            while (parents.hasNext())
            {
                LOG.warn(
                String.format("\n       parent class name: %s",
                        parents.next().getName()));
            }
        }
        // now print the objects which are a year behind
        LOG.warn(String.format("\n\nLagging Copy Cycle (one year behind):\n\n"));
        Iterator<String> laggardList = laggingCopyCycle.iterator();
        while (laggardList.hasNext())
        {
            LOG.warn(String.format("\n   %s",(String)laggardList.next()));
        }
        LOG.warn(String.format("\n\n"));
        // we want to test the code that enforces RI copy integrity by checking
        // that child keys to be copied exist in the (already copied) parent keys
        // we use 2011.  FS_OPTIONS_T exists, but CA_OBJECT_CODE_T does not
        // so, CA_SUB_OBJECT_CD_T should fail, but CA_OBJECT_CODE_T should succeed.
        // we add a third which should work, to test the need to specify the generic
        // class on the constructor
        Integer baseYear = new Integer(2009);
        Integer requestYear = baseYear+1;
        ParentKeyChecker<ObjectCode> objCodeChk =
            new ParentKeyChecker<ObjectCode>(ObjectCode.class,requestYear);
        Criteria yearCriteria = new Criteria();
        yearCriteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,baseYear);
        yearCriteria.addLessThan("ROWNUM",new Integer(3));
        QueryByCriteria queryID =
            new QueryByCriteria(ObjectCode.class,yearCriteria);
        Iterator<ObjectCode> objsReturned =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (objsReturned.hasNext())
        {
            ObjectCode rowReturned = objsReturned.next();
            PropertyUtils.setSimpleProperty(rowReturned,
                              KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, requestYear);
            LOG.warn(String.format("\nObjectCode key in parents? %b",
                                   objCodeChk.childRowSatisfiesRI(rowReturned)));
        }
        // now we're going to do the sub object code
        ParentKeyChecker<SubObjCd> subObjCdChk =
            new ParentKeyChecker<SubObjCd>(SubObjCd.class,requestYear);
        queryID =
            new QueryByCriteria(SubObjCd.class,yearCriteria);
        Iterator<SubObjCd> subObjsReturned =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (subObjsReturned.hasNext())
        {
            SubObjCd rowReturned = subObjsReturned.next();
            PropertyUtils.setSimpleProperty(rowReturned,
                              KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, requestYear);
            LOG.warn(String.format("\nsub object code key in parents? %b",
                                   subObjCdChk.childRowSatisfiesRI(rowReturned)));
        }
        // we need to test OrganizationReversionDetail
        ParentKeyChecker<OrganizationReversionDetail> OrgRevDtlChk =
            new ParentKeyChecker<OrganizationReversionDetail>(OrganizationReversionDetail.class,
                               requestYear-1);
        Criteria laggingYearCriteria = new Criteria();
        laggingYearCriteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                baseYear-1);
        laggingYearCriteria.addLessThan("ROWNUM",new Integer(3));
        queryID =
            new QueryByCriteria(OrganizationReversionDetail.class,laggingYearCriteria);
        Iterator<OrganizationReversionDetail> OrgRevDtlReturned =
            getPersistenceBrokerTemplate().getIteratorByQuery(queryID);
        while (OrgRevDtlReturned.hasNext())
        {
            OrganizationReversionDetail rowReturned = 
                OrgRevDtlReturned.next();
            PropertyUtils.setSimpleProperty(rowReturned,
                              KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                              requestYear-1);
            LOG.warn(String.format("\nOrganizationReversionDetail key in parents? %b",
                                   OrgRevDtlChk.childRowSatisfiesRI(rowReturned)));
        }
        LOG.warn(String.format("\n\nend RI test\n\n"));
    }
    
    
}
