/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemKeyConstants.AGENCY_SITES_URL;
import static org.kuali.kfs.module.tem.TemKeyConstants.ENABLE_AGENCY_SITES_URL;
import static org.kuali.kfs.module.tem.TemKeyConstants.PASS_TRIP_ID_TO_AGENCY_SITES;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ClassOfService;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerType;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelDocumentNotificationService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.module.tem.util.GroupTravelerComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.StringValueComparator;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * Abstract Travel Document Base
 */
public abstract class TravelDocumentBase extends AccountingDocumentBase implements TravelDocument, Copyable {

    private TripType tripType;
    private String tripTypeCode;
    private Timestamp tripBegin;
    private Timestamp tripEnd;
    private String tripDescription;
    private Boolean primaryDestinationIndicator = false;
    private Integer primaryDestinationId;
    private PrimaryDestination primaryDestination;
    private String primaryDestinationName;
    private String primaryDestinationCountryState;
    private String primaryDestinationCounty;
    private KualiDecimal rate;
    private KualiDecimal expenseLimit;
    private String mealWithoutLodgingReason;
    private String dummyAppDocStatus;
    
    // Traveler section
    private Integer temProfileId;
    private TEMProfile temProfile;
    private Integer travelerDetailId;
    private TravelerDetail traveler;
    
    protected List<SpecialCircumstances> specialCircumstances = new ArrayList<SpecialCircumstances>();
    protected List<GroupTraveler> groupTravelers = new ArrayList<GroupTraveler>();
    protected List<TravelAdvance> travelAdvances = new ArrayList<TravelAdvance>();
    protected List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
    protected List<ActualExpense> actualExpenses = new ArrayList<ActualExpense>();
    protected List<ImportedExpense> importedExpenses = new ArrayList<ImportedExpense>();
    protected List<HistoricalTravelExpense> historicalTravelExpenses = new ArrayList<HistoricalTravelExpense>();
        
    protected String travelDocumentIdentifier;
    protected Integer travelDocumentLinkIdentifier;
    private Boolean delinquentTRException = false;
        
    @Transient
    private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();

    @Transient
    private Map disabledProperties;
    
    @Transient
    private Boolean taxSelectable = Boolean.TRUE;

    protected TravelDocumentBase() {
        super();  
    }

    /**
     * This method updates both the workflow and the internal TravelStatus objects
     * 
     * @param status
     */
    public void updateAppDocStatus(String newStatus) {
        debug("new status is: " + newStatus);

        // get current workflow status and compare to status change
        String currStatus = getAppDocStatus();
        if (ObjectUtils.isNull(currStatus) || !newStatus.equalsIgnoreCase(currStatus)) {
            // update
            setAppDocStatus(newStatus);
        }
        if ((this.getDocumentHeader().getWorkflowDocument().stateIsFinal() || getDocumentHeader().getWorkflowDocument().stateIsProcessed()) && (newStatus.equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD)
                || newStatus.equals(TravelAuthorizationStatusCodeKeys.OPEN_REIMB))
                || newStatus.equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)
                || newStatus.equals(TravelAuthorizationStatusCodeKeys.CANCELLED)
                || newStatus.equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION)
                || newStatus.equals(TravelRelocationStatusCodeKeys.RELO_MANAGER_APPROVED)){
            WorkflowDocumentService workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
            try {
                workflowDocumentService.save(this.getDocumentHeader().getWorkflowDocument(), null);
            }
            catch (WorkflowException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
    }
           
    public String getAppDocStatus() {
        String status = getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();
        if (StringUtils.isBlank(status)) {
            status = "Initiated";
        }
        return status;
    }
    
    public void setAppDocStatus(String status) {
        getDocumentHeader().getWorkflowDocument().getRouteHeader().setAppDocStatus(status);
        getDocumentHeader().getWorkflowDocument().getRouteHeader().setAppDocStatusDate(Calendar.getInstance());
    }
 
    /**
     * Gets the dummyAppDocStatus attribute. 
     * @return Returns the dummyAppDocStatus.
     */
    public String getDummyAppDocStatus() {
        return dummyAppDocStatus;
    }

    /**
     * Sets the dummyAppDocStatus attribute value.
     * @param dummyAppDocStatus The dummyAppDocStatus to set.
     */
    public void setDummyAppDocStatus(String dummyAppDocStatus) {
        this.dummyAppDocStatus = dummyAppDocStatus;
    }
    
    /**
     * Gets the primaryDestinationIndicator attribute. 
     * @return Returns the primaryDestinationIndicator.
     */
    public Boolean getPrimaryDestinationIndicator() {
        return primaryDestinationIndicator;
    }

    /**
     * Sets the primaryDestinationIndicator attribute value.
     * @param primaryDestinationIndicator The primaryDestinationIndicator to set.
     */
    public void setPrimaryDestinationIndicator(Boolean primaryDestinationIndicator) {
        this.primaryDestinationIndicator = primaryDestinationIndicator;
    }

    /**
     * This method gets the trip description
     * 
     * @return trip description
     */
    @Column(name = "TRIP_DESC")
    public String getTripDescription() {
        return tripDescription;
    }
    public Integer getPrimaryDestinationId() {
        return primaryDestinationId;
    }

    public void setPrimaryDestinationId(Integer primaryDestinationId) {
        this.primaryDestinationId = primaryDestinationId;
        
    }

    public PrimaryDestination getPrimaryDestination() {
        if (primaryDestination ==  null){
            primaryDestination = new PrimaryDestination();
        }
        return primaryDestination;
    }

    public void setPrimaryDestination(PrimaryDestination primaryDestination) {
        this.primaryDestination = primaryDestination;
        if (primaryDestination !=  null){
            this.setPrimaryDestinationCountryState(primaryDestination.getCountryState());
            this.setPrimaryDestinationCounty(primaryDestination.getCounty());
            this.setPrimaryDestinationName(primaryDestination.getPrimaryDestinationName());
            
        }
        else{
            this.setPrimaryDestinationCountryState(null);
            this.setPrimaryDestinationCounty(null);
            this.setPrimaryDestinationName(null);
        }
    }

    /**
     * Gets the primaryDestinationName attribute. 
     * @return Returns the primaryDestinationName.
     */
    public String getPrimaryDestinationName() {
        return primaryDestinationName;
    }

    /**
     * Sets the primaryDestinationName attribute value.
     * @param primaryDestinationName The primaryDestinationName to set.
     */
    public void setPrimaryDestinationName(String primaryDestinationName) {
        this.primaryDestinationName = primaryDestinationName;
    }

    /**
     * Gets the primaryDestinationCountryState attribute. 
     * @return Returns the primaryDestinationCountryState.
     */
    public String getPrimaryDestinationCountryState() {
        return primaryDestinationCountryState;
    }

    /**
     * Sets the primaryDestinationCountryState attribute value.
     * @param primaryDestinationCountryState The primaryDestinationCountryState to set.
     */
    public void setPrimaryDestinationCountryState(String primaryDestinationCountryState) {
        if (primaryDestinationCountryState != null) {
            primaryDestinationCountryState = primaryDestinationCountryState.toUpperCase();
        }
        
        this.primaryDestinationCountryState = primaryDestinationCountryState;
    }

    /**
     * Gets the primaryDestinationCounty attribute. 
     * @return Returns the primaryDestinationCounty.
     */
    public String getPrimaryDestinationCounty() {
        return primaryDestinationCounty;
    }

    /**
     * Sets the primaryDestinationCounty attribute value.
     * @param primaryDestinationCounty The primaryDestinationCounty to set.
     */
    public void setPrimaryDestinationCounty(String primaryDestinationCounty) {
        this.primaryDestinationCounty = primaryDestinationCounty;
    }

    /**
     * This method sets the trip description for this request
     * 
     * @param tripDescription
     */
    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    /**
     * This method returns the trip type associated with this Travel Request document
     * 
     * @return trip type code
     */
    @ManyToOne
    @JoinColumn(name = "TRIP_TYP_CD")
    public TripType getTripType() {
        return tripType;
    }

    /**
     * This method sets the trip type should only be used by the ojb retrieval
     * 
     * @param tripType
     */
    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    /**
     * This method returns the trip type code associated with the travel request document
     * 
     * @return trip type code
     */
    @Column(name = "TRIP_TYP_CD", length = 3)
    public String getTripTypeCode() {
        return tripTypeCode;
    }

    /**
     * This method returns the trip type code for this travel request document
     * 
     * @param tripTypeCode
     */
    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    @Column(name = "TRVL_ID")
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }


    /**
     * This method gets the begin date for this trip
     * 
     * @return trip begin date
     */
    @Column(name = "TRIP_BGN_DT")
    public Timestamp getTripBegin() {
        return tripBegin;
    }

    /**
     * This method sets the trip begin date for this request
     * 
     * @param tripBegin
     */
    public void setTripBegin(Timestamp tripBegin) {
        this.tripBegin = tripBegin;
    }

    /**
     * This method returns the trip end date for this request
     * 
     * @return trip end date
     */
    @Column(name = "TRIP_END_DT")
    public Timestamp getTripEnd() {
        return tripEnd;
    }


    /**
     * This method sets the trip end date for this request
     * 
     * @param tripEnd
     */
    public void setTripEnd(Timestamp tripEnd) {
        this.tripEnd = tripEnd;
    }

    /**
     * Determines if this document should be able to return to the fiscal officer node again. This can happen
     * if the user has rights to reroute and also if the document is already ENROUTE.
     * 
     * @return true if the doucment is currently enroute and reroutable
     */
    public boolean canReturn() {
        return getDocumentHeader().getWorkflowDocument().stateIsEnroute();
    }

    public Integer getTravelDocumentLinkIdentifier() {
        return travelDocumentLinkIdentifier;
    }

    public void setTravelDocumentLinkIdentifier(Integer travelDocumentLinkIdentifier) {
        this.travelDocumentLinkIdentifier = travelDocumentLinkIdentifier;
    }

    /**
     * Gets the traveler attribute.
     * 
     * @return Returns the traveler.
     */
    @ManyToOne
    @JoinColumn(name = "traveler_dtl_id")
    public TravelerDetail getTraveler() {
        return traveler;
    }

    /**
     * Sets the traveler attribute value.
     * 
     * @param traveler The traveler to set.
     */
    public void setTraveler(TravelerDetail traveler) {
        this.traveler = traveler;
    }


    /**
     * Gets the travelerDetailId attribute.
     * 
     * @return Returns the travelerDetailId.
     */
    @Column(name = "traveler_dtl_id")
    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }

    /**
     * Sets the travelerDetailId attribute value.
     * 
     * @param travelerDetailId The travelerDetailId to set.
     */
    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

    @Column(name = "expenseLimit", precision = 19, scale = 2)
    public KualiDecimal getExpenseLimit() {
        return expenseLimit;
    }

    public void setExpenseLimit(KualiDecimal expenseLimit) {
        this.expenseLimit = expenseLimit;
    }

    /**
     * Sets the propertyChangeListener attribute value.
     * 
     * @param propertyChangeListener The propertyChangeListener to set.
     */
    public void setPropertyChangeListeners(final List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }

    /**
     * Gets the propertyChangeListeners attribute.
     * 
     * @return Returns the propertyChangeListenerDetailId.
     */
    public List<PropertyChangeListener> getPropertyChangeListeners() {
        return this.propertyChangeListeners;
    }

    /**
     * Notify listeners that an event occurred
     *
     * @param event the {@link PropertyChangeEvent}
     */
    protected void notifyChangeListeners(final PropertyChangeEvent event) {
        for (final PropertyChangeListener listener : getPropertyChangeListeners()) {
            listener.propertyChange(event);
        }
    }

    public void setSpecialCircumstances(final List<SpecialCircumstances> specialCircumstances) {
        this.specialCircumstances = specialCircumstances;
    }

    public List<SpecialCircumstances> getSpecialCircumstances() {
        return this.specialCircumstances;
    }

    /**
     * Here we need to generate the travel request number from the SequenceAccessorService as well as set any status for the
     * document
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // Set the identifier and org doc number before anything is done in the super class
        if (ObjectUtils.isNull(getTravelDocumentIdentifier())) {
            // need retrieve the next available TR id to save in GL entries (only do if travel request id is null which should be on
            // first
            // save)
            SequenceAccessorService sas = getSequenceAccessorService();
            Long trSequenceNumber = sas.getNextAvailableSequenceNumber("TRVL_ID_SEQ", this.getClass());
            
            String docId = "T-";
            if(this instanceof TravelEntertainmentDocument) {
            	docId = "E-";
            } else if (this instanceof TravelRelocationDocument) {
            	docId = "M-";
            }
            
            setTravelDocumentIdentifier(docId + trSequenceNumber.toString());
            this.getDocumentHeader().setOrganizationDocumentNumber(getTravelDocumentIdentifier());
        }
        
        //always generate the description in case the traveler changes from the last time it was generated.
        getDocumentHeader().setDocumentDescription(generateDescription());
        
        super.prepareForSave(event);
    }

    /**
     * This method generates a description based on the following information: principal trip begin date (mm/dd/yyy) primary
     * destination and truncate at 40 characters
     * 
     * @return a newly generated description
     */
    protected String generateDescription() {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        PersonService<Person> ps = SpringContext.getBean(PersonService.class);

        Person person = ps.getPerson(getTraveler().getPrincipalId());

        this.getTraveler().refreshReferenceObject(TemPropertyConstants.CUSTOMER);

        AccountsReceivableCustomer customer = getTraveler().getCustomer();
        if (person != null) {
            sb.append(person.getLastName() + ", " + person.getFirstName() + " " + person.getMiddleName() + " ");
        }
        else if (customer != null) {
            sb.append(customer.getCustomerName() + " ");
        }
        else {
            sb.append(getTraveler().getFirstName() + " " + getTraveler().getLastName() + " ");
        }
        
        if(this.getTripBegin() != null) {
            sb.append(format.format(this.getTripBegin()) + " ");
        }
        
        if(this.getPrimaryDestination() != null && StringUtils.isNotEmpty(this.getPrimaryDestination().getPrimaryDestinationName())) {
        	sb.append(this.getPrimaryDestination().getPrimaryDestinationName());
        } else  {
            if (this.getPrimaryDestinationName() != null) {
                sb.append(this.getPrimaryDestinationName());
            }
        }
        
        String tempStr = sb.toString();

        if (getDelinquentAction() != null) {
            tempStr = "(Delinquent) " + tempStr;
        }
        
        if (tempStr.length() > 40) {
            tempStr = tempStr.substring(0, 39);
        }
        
        return tempStr;
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        this.setBoNotes(new ArrayList());
        super.toCopy();
        cleanTravelDocument();

        for (final SpecialCircumstances circumstances : getSpecialCircumstances()) {
            final String sequenceName = getSpecialCircumstancesSequenceName();
            final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, SpecialCircumstances.class);
            circumstances.setId(sequenceNumber);
            circumstances.setDocumentNumber(getDocumentNumber());
        }
        
        // Cleanup Travel Advances ... not part of a copy
        this.setTravelAdvances(new ArrayList<TravelAdvance>());
        // Copy Trip Detail Estimates, Emergency Contacts, Transportation Modes, and Group Travelers
        setTransportationModes(getTravelDocumentService().copyTransportationModeDetails(this.getTransportationModes(), this.getDocumentNumber()));
        setPerDiemExpenses(getTravelDocumentService().copyPerDiemExpenses(this.getPerDiemExpenses(), this.getDocumentNumber()));
        getTraveler().setEmergencyContacts(getTravelDocumentService().copyTravelerDetailEmergencyContact(this.getTraveler().getEmergencyContacts(), this.getDocumentNumber()));
        setGroupTravelers(getTravelDocumentService().copyGroupTravelers(this.getGroupTravelers(), this.getDocumentNumber()));               
        setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(this.getActualExpenses(), this.getDocumentNumber()));
        setImportedExpenses(new ArrayList<ImportedExpense>());
    }

    /**
     * Cleans the Travel Document Identifier, and the notes
     */
    protected void cleanTravelDocument() {
        this.travelDocumentIdentifier = null;
        this.travelerDetailId = null;
        this.getTraveler().setId(null);
        this.getDocumentHeader().setOrganizationDocumentNumber("");
        this.getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);

    }

    protected String getSpecialCircumstancesSequenceName() {
        Class boClass = SpecialCircumstances.class;
        String retval = "";
        try {
            boolean rethrow = true;
            Exception e = null;
            while (rethrow) {
                debug("Looking for id in ", boClass.getName());
                try {
                    final Field idField = boClass.getDeclaredField("id");
                    final SequenceGenerator sequenceInfo = idField.getAnnotation(SequenceGenerator.class);
                    
                    return sequenceInfo.sequenceName();
                }
                catch (Exception ee) {
                    // ignore and try again
                    debug("Could not find id in ", boClass.getName());
                    
                    // At the end. Went all the way up the hierarchy until we got to Object
                    if (Object.class.equals(boClass)) {
                        rethrow = false;
                    }
                    
                    // get the next superclass
                    boClass = boClass.getSuperclass();
                    e = ee;
                }
            }
            
            if (e != null) {
                throw e;
            }
        }
        catch (Exception e) {
            error("Could not get the sequence name for business object ", SpecialCircumstances.class.getSimpleName());
            error(e.getMessage());
            if (logger().isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return retval;
    }
    
    @Transient
    public boolean isValidExpenses(){
        if(this.actualExpenses == null){
            return true;
        }
        
        int counter = 0;
        for(ActualExpense actualExpense: this.actualExpenses){
            if (actualExpense.getExpenseDetails().size() > 0){
                KualiDecimal detailAmount = getTotalDetailExpenseAmount(actualExpense);
                GlobalVariables.getMessageMap().addToErrorPath(KNSPropertyConstants.DOCUMENT);
                GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.ACTUAL_EXPENSES + "[" + counter + "]");
                if(detailAmount.isGreaterThan(actualExpense.getExpenseAmount()) && !actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.MILEAGE)){     
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DETAIL_AMOUNT_EXCEED, detailAmount.toString(), actualExpense.getExpenseAmount().toString());
                    return false;
                }
                GlobalVariables.getMessageMap().clearErrorPath();
            }
            counter++;
        }
        
        return true;
    }

    @Transient
    public KualiDecimal getMealsAndIncidentalsGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getMealsAndIncidentalsGrandTotal(this);
    }

    @Transient
    public KualiDecimal getLodgingGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getLodgingGrandTotal(this);
    }

    @Transient
    public KualiDecimal getMileageTotalGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getMileageTotalGrandTotal(this);
    }

    @Transient
    public KualiDecimal getDailyTotalGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getDailyTotalGrandTotal(this);
    }

    @Transient
    public KualiDecimal getDocumentGrandTotal() {
        KualiDecimal total  = KualiDecimal.ZERO;
        Iterator<String> it = TemConstants.expenseTypes().keySet().iterator();
        while (it.hasNext()){
            TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,it.next());
            total = service.getAllExpenseTotal(this,true).add(total);
        }
        return total;
    }

    @Transient
    public Integer getMilesGrandTotal() {
        PerDiemService service = SpringContext.getBean(PerDiemService.class);
        return service.getMilesGrandTotal(this);
    }

    @OneToMany(mappedBy="documentNumber")
    public List<PerDiemExpense> getPerDiemExpenses() {
        return perDiemExpenses;
    }

    public void setPerDiemExpenses(List<PerDiemExpense> perDiemExpenses) {
        this.perDiemExpenses = perDiemExpenses;
    }
    
    @OneToMany(mappedBy="documentNumber")
    public List<ActualExpense> getActualExpenses() {
        return actualExpenses;
    }

    public void setActualExpenses(List<ActualExpense> actualExpenses) {
        this.actualExpenses = actualExpenses;
    }

    @Transient
    public void enableExpenseTypeSpecificFields(final List<ActualExpense> actualExpenses){
        for(ActualExpense actualExpense: actualExpenses){
            actualExpense.enableExpenseTypeSpecificFields();
        }
    }
    
    /**
     * Returns the pending expense amount after summing up detail rows expense amount
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getTotalPendingAmount(java.util.List, java.lang.Long)
     */
    @Transient
    public KualiDecimal getTotalPendingAmount(ActualExpense actualExpense){
        KualiDecimal expenseAmount = actualExpense.getExpenseAmount();
        KualiDecimal detailTotal = KualiDecimal.ZERO;        
        
        for(TEMExpense expense: actualExpense.getExpenseDetails()){
            detailTotal = detailTotal.add(expense.getExpenseAmount());
        }        
        
        return expenseAmount.subtract(detailTotal);
    }
    
    @Transient
    public KualiDecimal getParentExpenseAmount(List<ActualExpense> actualExpenses, Long id){        
        
        for(ActualExpense actualExpense: actualExpenses){
            if(actualExpense.getId().equals(id)){
                return actualExpense.getExpenseAmount();
            }            
        }
        
        return KualiDecimal.ZERO;
    }
    
    @Transient
    public KualiDecimal getTotalDetailExpenseAmount(ActualExpense actualExpense){
        KualiDecimal totalDetailExpenseAmount = KualiDecimal.ZERO;
        
        for(TEMExpense expense: actualExpense.getExpenseDetails()){
            totalDetailExpenseAmount = totalDetailExpenseAmount.add(expense.getExpenseAmount());          
        }
        
        return totalDetailExpenseAmount;
    }
    
    @Transient
    public ActualExpense getParentExpenseRecord(List<ActualExpense> actualExpenses, Long id){        
        
        for(ActualExpense actualExpense: actualExpenses){
            if(actualExpense.getId().equals(id)){
                return actualExpense;
            }            
        }
        
        return null;
    }
    
    /**
     * Totals up all the other expenses. Needs to multiply the expenseAmount by the currencyRate because the currency
     * could be from another country other than US. If there is no currencyRate, we assume it's US even if the country isn't.
     *
     * @return {@link KualiDecimal} with the total
     */
    @Transient
    public KualiDecimal getActualExpensesTotal() {
        KualiDecimal retval = KualiDecimal.ZERO;

        debug("Getting other expense total");
        
        if(actualExpenses != null){
            for (final ActualExpense expense : actualExpenses) {
                final KualiDecimal expenseAmount = expense.getExpenseAmount().multiply(expense.getCurrencyRate());
                
                debug("Expense amount gotten is ", expenseAmount);
                retval = retval.add(expenseAmount);
            }
        }

        debug("Returning otherExpense Total ", retval);
        return retval;
    }

    //TODO: refactor this to addExpense and addExpenseDetail
    @Transient
    public void addActualExpense(final ActualExpense line) {
        final String sequenceName = line.getSequenceName();
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ActualExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
        line.enableExpenseTypeSpecificFields();
        getActualExpenses().add(line);
    }
    
    @Transient
    public void addExpense(TEMExpense line) {
        final String sequenceName = line.getSequenceName();
        // Because all expense types use the same sequence, it doesn't matter which class grabs the sequence
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ImportedExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);
        
        if (line instanceof ActualExpense){
            getActualExpenses().add((ActualExpense) line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
            ((ActualExpense)line).enableExpenseTypeSpecificFields();
        }
        else{
            getImportedExpenses().add((ImportedExpense) line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, null, line));
        }       
    }
    
    @Transient
    public void addExpenseDetail(TEMExpense line, Integer index) {
        final String sequenceName = line.getSequenceName();
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, ImportedExpense.class);
        line.setId(sequenceNumber);
        line.setDocumentNumber(this.documentNumber);
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, null, line));

        if (line instanceof ActualExpense){
            getActualExpenses().get(index).getExpenseDetails().add((ActualExpense) line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, null, line));
        }
        else{
            getImportedExpenses().get(index).getExpenseDetails().add((ImportedExpense) line);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, null, line));
        }
    }   
    
    /**
     * Adds a new other expense line
     * 
     * @param line
     */
    @Transient
    public void removeActualExpense(final Integer index) {
        final ActualExpense line = getActualExpenses().remove(index.intValue());
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, line, null)); 
        
        //Remove detail lines which are associated with parentId
        int nextIndex = -1;
        while((nextIndex = getNextDetailIndex(line.getId())) !=-1){
            final ActualExpense detailLine = getActualExpenses().remove(nextIndex);
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, detailLine, null)); 
        }               
    }
    
    @Transient
    public void removeExpense(TEMExpense expense, Integer index) {
        TEMExpense line = null;
        if (expense instanceof ActualExpense){
            line = getActualExpenses().remove(index.intValue());
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.ACTUAL_EXPENSES, line, null));
        }
        else{
            line = getImportedExpenses().remove(index.intValue());
            notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.IMPORTED_EXPENSES, line, null));
        }            
    }
      
    @Transient
    public void removeExpenseDetail(TEMExpense expense, Integer index) {
        final TEMExpense line = expense.getExpenseDetails().remove(index.intValue());
        notifyChangeListeners(new PropertyChangeEvent(this, TemPropertyConstants.EXPENSES_DETAILS, line, null));    
    }
    
    @Transient
    private int getNextDetailIndex(Long id){
        int index = 0;
        for(ActualExpense detailLine: getActualExpenses()){            
            if(ObjectUtils.isNotNull(detailLine.getExpenseParentId()) && detailLine.getExpenseParentId().equals(id)){
                return index; 
            }
            index++;
        }
        return -1;
    }
    
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
        getTravelDocumentService().populateDisbursementVoucherFields(disbursementVoucherDocument, this);
    }
    
    public void populateRequisitionFields(RequisitionDocument reqsDoc, TravelDocument document) {
       
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return SpringContext.getBean(SequenceAccessorService.class);
    }

    @Transient
    public List<Map<String, KualiDecimal>> getPerDiemExpenseTotals() {
        return getTravelDocumentService().calculateDailyTotals(this.perDiemExpenses);
    }

    public KualiDecimal getRate() {
        return rate;
    }

    public void setRate(KualiDecimal rate) {
        this.rate = rate;
    }

    /**
     * Gets the disabledProperties attribute. 
     * @return Returns the disabledProperties.
     */
    public Map<String,String> getDisabledProperties() {
        if (disabledProperties == null){
            disabledProperties = new HashMap<String,String>();
        }
        return disabledProperties;
    }

    /**
     * Sets the disabledProperties attribute value.
     * @param disabledProperties The disabledProperties to set.
     */
    public void setDisabledProperties(Map disabledProperties) {
        this.disabledProperties = disabledProperties;
    }
    
    /**
     * Gets the profileId attribute. 
     * @return Returns the profileId.
     */
    public Integer getProfileId() {
        return temProfileId;
    }

    /**
     * Sets the profileId attribute value, looks up the profile and populates the TravelerDetail based on the profile.
     * @param profileId The profileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.temProfileId = profileId;
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, profileId);
        setTemProfile((TEMProfile) getBusinessObjectService().findByPrimaryKey(TEMProfile.class, primaryKeys));
    }    

    /**
     * Gets the temProfileId attribute. 
     * @return Returns the temProfileId.
     */
    public Integer getTemProfileId() {
        return temProfileId;
    }

    /**
     * Sets the temProfileId attribute value.
     * @param temProfileId The temProfileId to set.
     */
    public void setTemProfileId(Integer temProfileId) {
        this.temProfileId = temProfileId;      
    }

    /**
     * Gets the temProfile attribute. 
     * @return Returns the temProfile.
     */
    public TEMProfile getTemProfile() {
        return temProfile;
    }

    /**
     * Sets the temProfile attribute value and populates the TravelerDetail from the TemProfile.
     * @param temProfile The temProfile to set.
     */
    public void setTemProfile(TEMProfile temProfile) {
        this.temProfile = temProfile;
        if(temProfile != null){
            getTravelerService().populateTEMProfile(temProfile);
            if (temProfile.getTravelerType() == null){
                Map<String, Object> fieldValues = new HashMap<String, Object>();
                fieldValues.put("code", temProfile.getTravelerTypeCode());
                List<TravelerType> types = (List<TravelerType>) getBusinessObjectService().findMatching(TravelerType.class, fieldValues);
                temProfile.setTravelerType(types.get(0));
                setTemProfileId(temProfile.getProfileId());
            }
            
            traveler.setDocumentNumber(this.documentNumber);            
            getTravelerService().convertTEMProfileToTravelerDetail(temProfile,(traveler == null?new TravelerDetail():traveler));
        }
    }
    
    /**
     * 
     * This method sets up the traveler from the TravelerDetail if it exists, 
     * otherwise it populates the TravelerDetail from the TemProfile.
     * @param temProfileId
     * @param traveler
     */
    public void setupTraveler(Integer temProfileId, TravelerDetail traveler) {
        
        if (traveler != null && traveler.getId() != null) {
            // There's a traveler, which means it needs to copy the traveler, rather than 
            // setting it up from the profile, which is why setProfileId() is not called here.
            this.temProfileId = temProfileId;
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, temProfileId);
            this.temProfile = (TEMProfile) getBusinessObjectService().findByPrimaryKey(TEMProfile.class, primaryKeys);

            this.traveler = getTravelerService().copyTraveler(traveler, this.documentNumber);
        }
        else {
            setProfileId(temProfileId);
        }
    }
    
    /**
     * 
     * This method wraps {@link #checkTravelerFieldForChanges(String, String, String, String)} for looping purposes.
     * @param o1
     * @param o2
     * @param propertyName
     * @param noteText
     * @param fieldLabel
     * @return
     */
    private String checkTravelerFieldForChanges(Object o1, Object o2, String propertyName, String noteText, String fieldLabel){
        String profileFieldValue = (String) ObjectUtils.getPropertyValue(o1, propertyName);
        String travelerDetailFieldValue = (String) ObjectUtils.getPropertyValue(o2, propertyName);
        
        return checkTravelerFieldForChanges(profileFieldValue != null ? profileFieldValue.trim() : "", travelerDetailFieldValue != null ? travelerDetailFieldValue.trim() : "", noteText, fieldLabel);
    }

    /**
     * 
     * This method compares profile and traveler field values using {@link StringValueComparator#compare(Object, Object)} and returns the formatted noteText accordingly
     * @param profileFieldValue
     * @param travelerDetailFieldValue
     * @param noteText
     * @param fieldLabel
     * @return
     */
    private String checkTravelerFieldForChanges(String profileFieldValue, String travelerDetailFieldValue, String noteText, String fieldLabel) {
        if(StringValueComparator.getInstance().compare(profileFieldValue, travelerDetailFieldValue) == 0){
            return noteText;
        }
        
        return noteText += fieldLabel + ", " ;
    }
       
    public String getDelinquentAction(){
        if(tripEnd != null){
            List<String> delinquentRules = getParameterService().getParameterValues(PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.TravelParameters.NUMBER_OF_TR_DELINQUENT_DAYS);                    
            String action = null;  
                   
            if(delinquentRules != null){
                for(String rule : delinquentRules){
                    String[] arg = rule.split("=");
                    if(arg != null && arg.length == 2){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(arg[1]) * -1);
                        
                        if(tripEnd.before(cal.getTime())){
                            if(action != null){
                                if(TemPropertyConstants.delinquentActionsRank().get(action) < TemPropertyConstants.delinquentActionsRank().get(arg[0])){
                                    action = arg[0];
                                }
                            }else{
                                action = arg[0];
                            }                      
                        }
                    }
                }
            }
            
            return action;
        }
                  
        return null;
    }
    
    public boolean canDisplayAgencySitesUrl(){
        String value = getConfigurationService().getPropertyString(ENABLE_AGENCY_SITES_URL);
        
        if(value== null || value.length() ==0 || !value.equalsIgnoreCase("Y")){
            return false;
        }
        return true;
    }
    
    public String getAgencySitesUrl(){
        return getConfigurationService().getPropertyString(AGENCY_SITES_URL);
    }
    
    public boolean canPassTripIdToAgencySites(){
        String value = getConfigurationService().getPropertyString(PASS_TRIP_ID_TO_AGENCY_SITES);
        
        if(value== null || value.length() ==0 || !value.equalsIgnoreCase("Y")){
            return false;
        }
        return true;
    }
    
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getImportedExpenses());
        managedLists.add(getActualExpenses());
        managedLists.add(getPerDiemExpenses());

        return managedLists;
    }

    public Boolean getDelinquentTRException() {
        return delinquentTRException != null ? delinquentTRException : false;
    }
    
    public void setDelinquentTRException(Boolean delinquentTRException) {
        this.delinquentTRException = delinquentTRException;
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }
    
    protected KualiConfigurationService getConfigurationService() {
        return SpringContext.getBean(KualiConfigurationService.class);
    }

    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }
    
    protected TravelerService getTravelerService() {
        return SpringContext.getBean(TravelerService.class);
    }
    
    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
    
    public void setTaxSelectable(Boolean argTaxSelectable){
        this.taxSelectable = argTaxSelectable;
    }
    
    public Boolean getTaxSelectable(){
        return taxSelectable;
    }

    public Boolean isTaxSelectable(){
        return taxSelectable;
    }
   
    protected boolean requiresAccountApprovalRouting() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (AccountingLine line : ((List<AccountingLine>) this.getSourceAccountingLines())) {
            total = total.add(line.getAmount());
        }
        if (total.isGreaterThan(KualiDecimal.ZERO)) {
            return true;
        }

        return false;
    }

    protected boolean requiresDivisionApprovalRouting() {
        if (getTravelDocumentService().getTotalAuthorizedEncumbrance(this).isGreaterEqual(new KualiDecimal(getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TravelParameters.CUMULATIVE_REIMBURSABLE_AMT_WITHOUT_DIV_APPROVAL)))) {
            return true;
        }
        return false;
    }
    
    protected boolean requiresInternationalTravelReviewRouting() {
        if (ObjectUtils.isNotNull(this.getTripTypeCode()) && getParameterService().getParameterValues(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TravelParameters.INTERNATIONAL_TRIP_TYPE_CODES).contains(this.getTripTypeCode())) {
            return true;
        }
        return false;
    }

    protected boolean requiresTaxManagerApprovalRouting() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (ActualExpense line : this.getActualExpenses()) {
            if(line.getTaxable()){
                return true;
            }
        }

        return false;
    }
    
    protected boolean requiresSeparationOfDutiesRouting(){
        String code = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TravelParameters.SEPARATION_OF_DUTIES_ROUTING_OPTION);

        if (code.equals(TemConstants.SEP_OF_DUTIES_FO)){
            if (!requiresAccountApprovalRouting()){
                return false;
            }
            if (getTraveler().getPrincipalId() != null){
                return getTravelDocumentService().isResponsibleForAccountsOn(this, this.getTraveler().getPrincipalId());
            }
            else{
                return false;
            }
            
        }
        else if (code.equals(TemConstants.SEP_OF_DUTIES_DR)){
            if (!requiresDivisionApprovalRouting()){
                return false;
            }
            if (getTraveler().getPrincipalId() != null){
                RoleService service = SpringContext.getBean(RoleManagementService.class);
                List<String> principalIds = (List<String>) service.getRoleMemberPrincipalIds(PARAM_NAMESPACE, TemConstants.TEMRoleNames.DIVISION_REVIEWER, null);
                for (String id : principalIds){
                    if (id.equals(getTraveler().getPrincipalId())){
                        return true;
                    }
                }
            }
            else{
                return false;
            }
            
        }
        return false;
    }
    
    protected boolean requiresSpecialRequestReviewRouting() {
        if (ObjectUtils.isNotNull(this.getActualExpenses())) {
            for (ActualExpense ae : this.getActualExpenses()) {
                if (checkActualExpenseSpecialRequest(ae)) {
                    return true;
                }
                
                if (ae.getExpenseDetails() != null && !ae.getExpenseDetails().isEmpty()) {
                    for (TEMExpense aeDetail : ae.getExpenseDetails()) {
                        if (checkActualExpenseSpecialRequest(aeDetail)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean checkActualExpenseSpecialRequest(TEMExpense expense) {
        Map<String, String> searchMap = new HashMap<String, String>();
        searchMap.put(KNSPropertyConstants.CODE, expense.getClassOfServiceCode());
                       
        ClassOfService classOfService = (ClassOfService) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ClassOfService.class, searchMap);
        if (classOfService != null) {
            if (classOfService.isApprovalRequired()) {
                return true;
            }
        }
        
        TemTravelExpenseTypeCode expenseTypeCode = expense.getTravelExpenseTypeCode();
        if (expenseTypeCode.getSpecialRequestRequired() != null && expenseTypeCode.getSpecialRequestRequired()) {
            return true;
        }
        
        if (expense.isRentalCar() && expense.getRentalCarInsurance()){
            return true;
        }
        
        return false;
    } 
    
    /**
     * Gets the groupTravelers attribute.
     * 
     * @return Returns the groupTravelers.
     */
    public List<GroupTraveler> getGroupTravelers() {
        Collections.sort(groupTravelers, new GroupTravelerComparator());
        return groupTravelers;
    }
    
    /**
     * Sets the groupTravelers attribute value.
     * 
     * @param groupTravelers The groupTravelers to set.
     */
    public void setGroupTravelers(List<GroupTraveler> groupTravelers) {
        this.groupTravelers = groupTravelers;
    }

    /**
     * This method adds a new group traveler line to the travel doc
     * 
     * @param group traveler line
     */
    public void addGroupTravelerLine(GroupTraveler line) {
        line.setFinancialDocumentLineNumber(this.groupTravelers.size() + 1);
        line.setDocumentNumber(this.documentNumber);
        this.groupTravelers.add(line);
    }

    /**
     * Gets the travelAdvances attribute.
     * 
     * @return Returns the travelAdvances.
     */
    public List<TravelAdvance> getTravelAdvances() {
        return travelAdvances;
    }

    /**
     * Sets the travelAdvances attribute value.
     * 
     * @param travelAdvances The travelAdvances to set.
     */
    public void setTravelAdvances(List<TravelAdvance> travelAdvances) {
        this.travelAdvances = travelAdvances;
    }    
    
    /**
     * This method adds a new travel advance line
     * 
     * @param line
     */
    public void addTravelAdvanceLine(TravelAdvance line) {
        line.setFinancialDocumentLineNumber(this.travelAdvances.size()+1);
        line.setDocumentNumber(this.documentNumber);
        this.travelAdvances.add(line);
    }
    
    /**
     * Given the <code>financialObjectCode</code>, determine the total of the {@link SourceAccountingLine} instances with that
     * <code>financialObjectCode</code>
     * 
     * @param financialObjectCode to search for total on
     * @return @{link KualiDecimal} with total value for {@link AccountingLines} with <code>finanncialObjectCode</code>
     */
    public KualiDecimal getTotalFor(final String financialObjectCode) {
        KualiDecimal retval = KualiDecimal.ZERO;

        debug("Getting total for ", financialObjectCode);

        for (final AccountingLine line : (List<AccountingLine>) getSourceAccountingLines()) {
            try {
                debug("Comparing ", financialObjectCode, " to ", line.getObjectCode().getCode());
                if (line.getObjectCode().getCode().equals(financialObjectCode)) {
                    retval = retval.add(line.getAmount());
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }           
        }

        return retval;
    }
    
    public KualiDecimal getNonReimbursableTotal() {
        KualiDecimal total  = KualiDecimal.ZERO;
        Iterator<String> it = TemConstants.expenseTypes().keySet().iterator();
        while (it.hasNext()){
            TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,it.next());
            total = service.getNonReimbursableExpenseTotal(this).add(total);
        }
        
        return total;              
    }

    /**
     * This method returns total expense amount minus the non-reimbursable
     * 
     * @return
     */
    public KualiDecimal getApprovedAmount() {
        KualiDecimal total  = KualiDecimal.ZERO;
        Iterator<String> it = TemConstants.expenseTypes().keySet().iterator();
        while (it.hasNext()){
            TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,it.next());
            total = service.getAllExpenseTotal(this,false).add(total);
        }
        return total;
    }      
    
    @Override
    public List<TransportationModeDetail> getTransportationModes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTransportationModes(List<TransportationModeDetail> transportationModes) {
        // TODO Auto-generated method stub        
    }

    public String getMealWithoutLodgingReason() {
        return mealWithoutLodgingReason;
    }

    public void setMealWithoutLodgingReason(String mealWithoutLodgingReason) {
        this.mealWithoutLodgingReason = mealWithoutLodgingReason;
    }
    
    public boolean isMealsWithoutLodging(){
        if (perDiemExpenses != null){
            for(PerDiemExpense pde : perDiemExpenses){
                if (checkMealWithoutLodging(pde)) {
                    return true;
                }
            }
        }
        
        if (actualExpenses != null){
            for(ActualExpense actualExpense : actualExpenses){
                if (checkMealWithoutLodging(actualExpense)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public boolean checkMealWithoutLodging(PerDiemExpense pde) {        
        return pde.getMealsTotal().isGreaterThan(KualiDecimal.ZERO) && pde.getLodging().isLessEqual(KualiDecimal.ZERO);
    }
    
    public boolean checkMealWithoutLodging(ActualExpense actualExpense) {
        if (actualExpense.isHostedMeal()) {
            if (actualExpense.getExpenseParentId() != null) {
                ActualExpense parent = getParentExpenseRecord(getActualExpenses(), actualExpense.getExpenseParentId());
                if (!parent.getLodgingIndicator() && !parent.getLodgingAllowanceIndicator()) {
                    return true;
                }
            }
            else {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Gets the importedExpenses attribute. 
     * @return Returns the importedExpenses.
     */
    public List<ImportedExpense> getImportedExpenses() {
        return importedExpenses;
    }

    /**
     * Sets the importedExpenses attribute value.
     * @param importedExpenses The importedExpenses to set.
     */
    public void setImportedExpenses(List<ImportedExpense> importedExpenses) {
        this.importedExpenses = importedExpenses;
    }
    
    public KualiDecimal getCTSTotal() {
        TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.IMPORTED_CTS);
        KualiDecimal lessCtsCharges = service.getAllExpenseTotal(this, false);
        return lessCtsCharges;
    }
    
    public KualiDecimal getCorporateCardTotal() {
        TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.IMPORTED_CORP_CARD);
        KualiDecimal lessCorpCardCharges = service.getAllExpenseTotal(this, false);
        return lessCorpCardCharges;
    }
    
    public AccountingDistributionService getAccountingDistributionService(){
        return SpringContext.getBean(AccountingDistributionService.class);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getHistoricalTravelExpenses()
     */
    @Override
    public List<HistoricalTravelExpense> getHistoricalTravelExpenses() {
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        Map<String,String> fieldValues = new HashMap<String, String>();
        fieldValues.put(TemPropertyConstants.TRIP_ID,this.getTravelDocumentIdentifier());
        historicalTravelExpenses = (List<HistoricalTravelExpense>) service.findMatchingOrderBy(HistoricalTravelExpense.class, fieldValues, TemPropertyConstants.TRANSACTION_POSTING_DATE, true);
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            historicalTravelExpense.refreshReferenceObject("creditCardAgency");
            historicalTravelExpense.refreshReferenceObject("agencyStagingData");
            historicalTravelExpense.refreshReferenceObject("creditCardStagingData");
            historicalTravelExpense.getCreditCardAgency().refreshReferenceObject("creditCardType");
        }
        return historicalTravelExpenses;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setHistoricalTravelExpenses(java.util.List)
     */
    @Override
    public void setHistoricalTravelExpenses(List<HistoricalTravelExpense> historicalTravelExpenses) {
        // TODO Auto-generated method stub
        this.historicalTravelExpenses = historicalTravelExpenses;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> accountingLines = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        if (getSourceAccountingLines() != null) {
            Iterator iter = getSourceAccountingLines().iterator();
            while (iter.hasNext()) {
                TemSourceAccountingLine line = (TemSourceAccountingLine) iter.next();
                if (line.getCardType().equals(TemConstants.NOT_APPLICABLE)){
                    accountingLines.add((GeneralLedgerPendingEntrySourceDetail) line);
                }              
            }
        }
        return accountingLines;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getEncumbranceTotal()
     */
    @Override
    public KualiDecimal getEncumbranceTotal() {
        return KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        this.refreshNonUpdateableReferences();
        SpringContext.getBean(TravelDocumentNotificationService.class).sendNotificationOnChange(this, statusChangeEvent);
        super.doRouteStatusChange(statusChangeEvent);
        
        if (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            //Some docs come here twice.  if the imported expenses for this doc are reconciled, don't process again.
            boolean processImports = true;
            if (this.getHistoricalTravelExpenses() != null 
                    && this.getHistoricalTravelExpenses().size() > 0){
                for (HistoricalTravelExpense historicalTravelExpense : this.getHistoricalTravelExpenses()){
                    if (historicalTravelExpense.getDocumentNumber() != null
                            && historicalTravelExpense.getDocumentNumber().equals(this.getDocumentNumber())
                            && historicalTravelExpense.getReconciled().equals(TemConstants.ReconciledCodes.RECONCILED)){
                        processImports = false;
                        break;
                    }
                }
            }
            if (processImports){
                TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.IMPORTED_CTS);
                service.updateExpense(this);
                service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.IMPORTED_CORP_CARD);
                service.updateExpense(this);
            }            
        }
    }
    
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        TEMExpenseService service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.IMPORTED_CTS);
        service.processExpense(this);
        service = (TEMExpenseService) SpringContext.getBean(TEMExpense.class,TemConstants.TEMExpenseTypes.IMPORTED_CORP_CARD);
        service.processExpense(this);
        return true;
    }
    
    @Override
    public String getDocumentTypeName(){
        return this.getDataDictionaryEntry().getDocumentTypeName();
    }
    
    public boolean canPayDVToVendor() {
        return (getDocumentHeader() != null && !(getDocumentHeader().getWorkflowDocument().stateIsCanceled() || getDocumentHeader().getWorkflowDocument().stateIsInitiated() || getDocumentHeader().getWorkflowDocument().stateIsException() || getDocumentHeader().getWorkflowDocument().stateIsDisapproved() || getDocumentHeader().getWorkflowDocument().stateIsSaved()));
    }

    public boolean canCreateREQSForVendor() {
        return (getDocumentHeader() != null && !(getDocumentHeader().getWorkflowDocument().stateIsCanceled() || getDocumentHeader().getWorkflowDocument().stateIsInitiated() || getDocumentHeader().getWorkflowDocument().stateIsException() || getDocumentHeader().getWorkflowDocument().stateIsDisapproved() || getDocumentHeader().getWorkflowDocument().stateIsSaved()));
    } 
}
