package org.kuali.kfs.module.ar.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class InvoiceRecurrenceRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceRule.class);
    private InvoiceRecurrence oldInvoiceRecurrence;
    private InvoiceRecurrence newInvoiceRecurrence;

    @Override
    public void setupConvenienceObjects() {
        oldInvoiceRecurrence = (InvoiceRecurrence) super.getOldBo();
        newInvoiceRecurrence = (InvoiceRecurrence) super.getNewBo();
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success;
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        Date currentDate = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

       
        success = checkIfInvoiceIsApproved(newInvoiceRecurrence.getInvoiceNumber());
/*
        success &= checkIfRecurrenceMaintenanceAlreadyExists(newInvoiceRecurrence);
        success &= validateDocumentRecurrenceBeginDate(newInvoiceRecurrence);
*/
        success &= validateDocumentRecurrenceEndDate(newInvoiceRecurrence.getDocumentRecurrenceBeginDate(), 
                                                     newInvoiceRecurrence.getDocumentRecurrenceEndDate());
        success &= validateIfBothEndDateAndTotalRecurrenceNumberAreEntered(newInvoiceRecurrence.getDocumentRecurrenceBeginDate(), 
                                                                           newInvoiceRecurrence.getDocumentRecurrenceEndDate(),
                                                                           newInvoiceRecurrence.getDocumentTotalRecurrenceNumber(),
                                                                           newInvoiceRecurrence.getDocumentRecurrenceIntervalCode());
        success &= validateEndDateOrTotalNumberofRecurrences(newInvoiceRecurrence.getDocumentRecurrenceEndDate(),
                                                             newInvoiceRecurrence.getDocumentTotalRecurrenceNumber());   
        success &= validateMaximumNumberOfRecurrences(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber(),
                                                      newInvoiceRecurrence.getDocumentRecurrenceIntervalCode());
        success &= validateWorkgroup(newInvoiceRecurrence.getWorkgroupName());
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;
    }

    /**
     * Check if an Invoice Recurrence Maintenance document already exists.
     */
    private boolean checkIfRecurrenceMaintenanceAlreadyExists(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("invoiceNumber", newInvoiceRecurrence.getInvoiceNumber());
        InvoiceRecurrence invoiceRecurrence = (InvoiceRecurrence) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(InvoiceRecurrence.class, criteria);
        if (ObjectUtils.isNotNull(invoiceRecurrence) && !(oldInvoiceRecurrence.equals(invoiceRecurrence))) {
            putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArKeyConstants.ERROR_MAINTENANCE_DOCUMENT_ALREADY_EXISTS);
            success = false;
        }
        return success;
    }
    /**
     * Validate if the invoice has an approved status.
     */
    private boolean checkIfInvoiceIsApproved(String recurrenceInvoiceNumber) {
        boolean success = true;
        
        if (ObjectUtils.isNull(recurrenceInvoiceNumber)) {
            return success;
        }
        
        CustomerInvoiceDocument customerInvoiceDocument = null;
        if (!SpringContext.getBean(DocumentService.class).documentExists(recurrenceInvoiceNumber)) {
            putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArKeyConstants.ERROR_INVOICE_DOES_NOT_EXIST);
            success = false;
        }
        else {
            try {
                customerInvoiceDocument = (CustomerInvoiceDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(recurrenceInvoiceNumber);
            } catch (WorkflowException e){
            }
            if (ObjectUtils.isNotNull(customerInvoiceDocument)) {
                KualiWorkflowDocument workflowDocument = customerInvoiceDocument.getDocumentHeader().getWorkflowDocument();
                if (!(workflowDocument.stateIsApproved())) {
                    putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArKeyConstants.ERROR_RECURRING_INVOICE_NUMBER_MUST_BE_APPROVED);
                    success = false;
                }
            }
        }
        return success;
    }
    /**
     * Validate Begin Date.
     */
    private boolean validateDocumentRecurrenceBeginDate(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceBeginDate())) {
            return success;
        }
        Timestamp currentDate = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        Timestamp beginDateTimestamp = new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceBeginDate().getTime());
        if (beginDateTimestamp.before(currentDate) || beginDateTimestamp.equals(currentDate)) {
            putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_BEGIN_DATE, ArKeyConstants.ERROR_INVOICE_RECURRENCE_BEGIN_DATE_EARLIER_THAN_TODAY);
            return false;
        }
        return success;
    }
    /**
     * Validate End Date.
     */
    public boolean validateDocumentRecurrenceEndDate(Date newInvoiceRecurrenceBeginDate, Date newInvoiceRecurrenceEndDate) {
        boolean success = true;
        if (!SpringContext.getBean(InvoiceRecurrenceDocumentService.class).isValidRecurrenceEndDate(newInvoiceRecurrenceBeginDate, newInvoiceRecurrenceEndDate)) {
            putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_END_DATE, ArKeyConstants.ERROR_END_DATE_EARLIER_THAN_BEGIN_DATE);
            return false;
        }
        return success;
    }
    /**
     * This method checks that End Date and Total Recurrence Number are valid when both are entered.
     * 
     * @param document the maintenance document
     * @return
     */
    private boolean validateIfBothEndDateAndTotalRecurrenceNumberAreEntered(Date recurrenceBeginDate, Date recurrenceEndDate, Integer totalRecurrenceNumber, String recurrenceIntervalCode) {

        if (ObjectUtils.isNull(recurrenceBeginDate) || 
            ObjectUtils.isNull(recurrenceIntervalCode) ||
            ObjectUtils.isNull(recurrenceEndDate) ||
            ObjectUtils.isNull(totalRecurrenceNumber)) {
            return true;
        }

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(recurrenceBeginDate);
        Date beginDate = recurrenceBeginDate;
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(recurrenceEndDate);
        Date endDate = recurrenceEndDate;
        Calendar nextCalendar = Calendar.getInstance();
        Date nextDate = beginDate;
                
        int totalRecurrences = 0;
        int addCounter = 0;
        String intervalCode = recurrenceIntervalCode;
        if (intervalCode.equals("M")) {
            addCounter = 1;
        }
        if (intervalCode.equals("Q")) {
            addCounter = 3;
        }
        /* perform this loop while begin_date is less than or equal to end_date */
        while (!(beginDate.after(endDate))){
            beginCalendar.setTime(beginDate);
            beginCalendar.add(Calendar.MONTH, addCounter);
            beginDate = new Date(beginCalendar.getTime().getTime());
            totalRecurrences++;

            nextDate = beginDate;
            nextCalendar.setTime(nextDate);
            nextCalendar.add(Calendar.MONTH, addCounter);
            nextDate = new Date(nextCalendar.getTime().getTime());
            if (endDate.after(beginDate) && endDate.before(nextDate)) {
                totalRecurrences++;
                break;
            }
        }
        if (totalRecurrences != totalRecurrenceNumber.intValue()) {
            putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_END_DATE, ArKeyConstants.ERROR_END_DATE_AND_TOTAL_NUMBER_OF_RECURRENCES_NOT_VALID);
            return false;
        }
        
        return true;
    }
    /**
     * Validate that either End Date or Total Number of Recurrences must be entered.
     */
    private boolean validateEndDateOrTotalNumberofRecurrences(Date recurrenceEndDate, Integer totalRecurrenceNumber) {
        boolean success = true;
        if (ObjectUtils.isNull(recurrenceEndDate) && ObjectUtils.isNull(totalRecurrenceNumber)) {
            putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_END_DATE, ArKeyConstants.ERROR_END_DATE_OR_TOTAL_NUMBER_OF_RECURRENCES);
            return false;
        }
        return success;
    }

    /**
     * Total number of recurrences may not be greater than the number defined in system parameter MAXIMUM_RECURRENCES_BY_INTERVAL.
     */ 
    private boolean validateMaximumNumberOfRecurrences(Integer totalRecurrenceNumber, String recurrenceIntervalCode) {

        if (ObjectUtils.isNull(recurrenceIntervalCode) ||
            ObjectUtils.isNull(totalRecurrenceNumber)) {
            return true;
        }
        boolean success = true;
        Integer maximumRecurrencesByInterval;
        if (ObjectUtils.isNotNull(recurrenceIntervalCode)) {
            List<String> maximumRecurrences = SpringContext.getBean(ParameterService.class).getParameterValues(InvoiceRecurrence.class, ArConstants.MAXIMUM_RECURRENCES_BY_INTERVAL, recurrenceIntervalCode);
            if (maximumRecurrences.size() > 0 && StringUtils.isNotBlank(maximumRecurrences.get(0))) {
                maximumRecurrencesByInterval = Integer.valueOf(maximumRecurrences.get(0));
                if (totalRecurrenceNumber > maximumRecurrencesByInterval) {
                    putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_TOTAL_RECURRENCE_NUMBER, ArKeyConstants.ERROR_TOTAL_NUMBER_OF_RECURRENCES_GREATER_THAN_ALLOWED, maximumRecurrences.get(0));
                    return false;
                }
            }
        }
        return success;
    }
    
    /**
     * Workgroup must be required and valid.
     */ 
    private boolean validateWorkgroup(String workgroupName) {
        if (ObjectUtils.isNotNull(workgroupName)) {
            if (!workgroupExistsAndIsActive(workgroupName)) {
                putFieldError(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_WORKGROUP_NAME, ArKeyConstants.ERROR_INVOICE_RECURRENCE_WORKGROUP_IS_INVALID, workgroupName);
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given workgroup exists and is active.
     * 
     * @param name The name of the workgroup to check.
     * @return Whether the given workgroup exists and is active.
     */
    private static boolean workgroupExistsAndIsActive(String name) {
        try {
            KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, name);
            return group != null && group.isActive();
        }
        catch (Exception e) {
            return false;
        }
    }

    
}
