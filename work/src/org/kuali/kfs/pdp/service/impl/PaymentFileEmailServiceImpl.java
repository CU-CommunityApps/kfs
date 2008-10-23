/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.service.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.batch.ExtractAchPaymentsStep;
import org.kuali.kfs.pdp.batch.LoadPaymentsStep;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @see org.kuali.kfs.pdp.service.PaymentFileEmailService
 */
public class PaymentFileEmailServiceImpl implements PaymentFileEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileEmailServiceImpl.class);

    private CustomerProfileService customerProfileService;
    private KualiConfigurationService kualiConfigurationService;
    private MailService mailService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileEmailService#sendErrorEmail(org.kuali.kfs.pdp.businessobject.PaymentFileLoad,
     *      org.kuali.rice.kns.util.ErrorMap)
     */
    public void sendErrorEmail(PaymentFileLoad paymentFile, ErrorMap errors) {
        LOG.debug("sendErrorEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_FAILURE_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();
        List<String> ccAddresses = parameterService.getParameterValues(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.HARD_EDIT_CC);

        if (paymentFile == null) {
            if (ccAddresses.isEmpty()) {
                LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                return;
            }

            message.getToAddresses().addAll(ccAddresses);

            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_BAD_FILE_PARSE) + "\n\n");
        }
        else {
            CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getOrg(), paymentFile.getSubUnit());
            if (customer == null) {
                LOG.error("sendErrorEmail() Invalid Customer.  Sending email to CC addresses");

                if (ccAddresses.isEmpty()) {
                    LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                    return;
                }

                message.getToAddresses().addAll(ccAddresses);

                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_INVALID_CUSTOMER) + "\n\n");
            }
            else {
                String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
                List<String> toAddressList = Arrays.asList(toAddresses.split(","));

                message.getToAddresses().addAll(toAddressList);
                message.getCcAddresses().addAll(ccAddresses);
            }
        }

        if (paymentFile != null) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_NOT_LOADED) + "\n\n");
            addPaymentFieldsToBody(body, null, paymentFile.getChart(), paymentFile.getOrg(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());
        }

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_ERROR_MESSAGES) + "\n");
        List<ErrorMessage> errorMessages = errors.getMessages(KFSConstants.GLOBAL_ERRORS);
        for (ErrorMessage errorMessage : errorMessages) {
            body.append(getMessage(errorMessage.getErrorKey(), (Object[]) errorMessage.getMessageParameters()) + "\n\n");
        }

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address.  Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileEmailService#sendLoadEmail(org.kuali.kfs.pdp.businessobject.PaymentFileLoad,
     *      java.util.List)
     */
    public void sendLoadEmail(PaymentFileLoad paymentFile, List<String> warnings) {
        LOG.debug("sendLoadEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME));

        List<String> ccAddresses = parameterService.getParameterValues(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.HARD_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);

        CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getOrg(), paymentFile.getSubUnit());
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);

        StringBuffer body = new StringBuffer();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getOrg(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_WARNING_MESSAGES) + "\n");
        for (String warning : warnings) {
            body.append(warning + "\n\n");
        }

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }

        if (paymentFile.isFileThreshold()) {
            sendThresholdEmail(true, paymentFile, customer);
        }

        if (paymentFile.isDetailThreshold()) {
            sendThresholdEmail(false, paymentFile, customer);
        }
    }

    /**
     * Sends email for a payment that was over the customer file threshold or the detail threshold
     * 
     * @param fileThreshold indicates whether the file threshold (true) was violated or the detail threshold (false)
     * @param paymentFile parsed payment file object
     * @param customer payment customer
     */
    protected void sendThresholdEmail(boolean fileThreshold, PaymentFileLoad paymentFile, CustomerProfile customer) {
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_THRESHOLD_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getOrg(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        if (fileThreshold) {
            String toAddresses = StringUtils.deleteWhitespace(customer.getFileThresholdEmailAddress());
            List<String> toAddressList = Arrays.asList(toAddresses.split(","));

            message.getToAddresses().addAll(toAddressList);
            body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD, paymentFile.getPaymentTotalAmount(), customer.getFileThresholdAmount()));
        }
        else {
            String toAddresses = StringUtils.deleteWhitespace(customer.getPaymentThresholdEmailAddress());
            List<String> toAddressList = Arrays.asList(toAddresses.split(","));

            message.getToAddresses().addAll(toAddressList);

            body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_DETAIL_THRESHOLD, customer.getPaymentThresholdAmount()) + "\n\n");
            for (PaymentDetail paymentDetail : paymentFile.getThresholdPaymentDetails()) {
                paymentDetail.refresh();
                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD, paymentDetail.getPaymentGroup().getPayeeName(), paymentDetail.getNetPaymentAmount()) + "\n");
            }
        }

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileEmailService#sendTaxEmail(org.kuali.kfs.pdp.businessobject.PaymentFileLoad)
     */
    public void sendTaxEmail(PaymentFileLoad paymentFile) {
        LOG.debug("sendTaxEmail() starting");

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_THRESHOLD_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();

        String taxEmail = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.TAX_GROUP_EMAIL_ADDRESS);
        if (GeneralUtilities.isStringEmpty(taxEmail)) {
            LOG.error("No Tax E-mail Application Setting found to send notification e-mail");
            return;
        }
        else {
            message.addToAddress(taxEmail);
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_TAX_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getOrg(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_GO_TO_PDP) + "\n");

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileEmailService#sendLoadEmail(org.kuali.kfs.pdp.businessobject.Batch)
     */
    public void sendLoadEmail(Batch batch) {
        LOG.debug("sendLoadEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuffer body = new StringBuffer();

        List<String> ccAddresses = parameterService.getParameterValues(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.HARD_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);

        CustomerProfile customer = batch.getCustomerProfile();
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, batch.getId().intValue(), customer.getChartCode(), customer.getOrgCode(), customer.getSubUnitCode(), batch.getCustomerFileCreateTimestamp(), batch.getPaymentCount().intValue(), batch.getPaymentTotalAmount());

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }
    
    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileEmailService#sendExceedsMaxNotesWarningEmail(java.util.List, java.util.List, int, int)
     */
    public void sendExceedsMaxNotesWarningEmail(List<String> creditMemos, List<String> paymentRequests, int lineTotal, int maxNoteLines) {
        LOG.debug("sendExceedsMaxNotesWarningEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();
        message.setFromAddress(mailService.getBatchMailingList());

        StringBuffer body = new StringBuffer();

        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            message.setSubject(getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_SUBJECT));
        }
        else {
            message.setSubject(environmentCode + "-" + getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_SUBJECT));
        }

        // Get recipient email address
        String toAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.PDP_ERROR_EXCEEDS_NOTE_LIMIT_EMAIL);
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));
        message.getToAddresses().addAll(toAddressList);

        List<String> ccAddresses = parameterService.getParameterValues(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.SOFT_EDIT_CC);
        message.getCcAddresses().addAll(ccAddresses);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_MESSAGE, StringUtils.join(creditMemos, ","), StringUtils.join(paymentRequests, ","), lineTotal, maxNoteLines));
        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendExceedsMaxNotesWarningEmail() Invalid email address. Message not sent", e);
        }
    }
    
    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileEmailService#sendAchSummaryEmail(java.util.Map, java.util.Map, java.util.Date)
     */
    public void sendAchSummaryEmail(Map<String, Integer> unitCounts, Map<String, KualiDecimal> unitTotals, Date disbursementDate) {
        LOG.debug("sendAchSummaryEmail() starting");

        MailMessage message = new MailMessage();

        List<String> toAddressList = parameterService.getParameterValues(ExtractAchPaymentsStep.class, PdpParameterConstants.ACH_SUMMARY_TO_EMAIL_ADDRESS_PARMAETER_NAME);
        message.getToAddresses().addAll(toAddressList);

        message.setFromAddress(mailService.getBatchMailingList());

        String subject = parameterService.getParameterValue(ExtractAchPaymentsStep.class, PdpParameterConstants.ACH_SUMMARY_EMAIL_SUBJECT_PARAMETER_NAME);
        message.setSubject(subject);

        StringBuffer body = new StringBuffer();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_DISB_DATE, disbursementDate) + "\n");

        Integer totalCount = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (String unit : unitCounts.keySet()) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_UNIT_TOTAL, StringUtils.leftPad(unit, 13), StringUtils.leftPad(unitCounts.get(unit).toString(), 10), StringUtils.leftPad(unitTotals.get(unit).toString(), 20)) + "\n");

            totalCount = totalCount + unitCounts.get(unit);
            totalAmount = totalAmount.add(unitTotals.get(unit));
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_EXTRACT_TOTALS, StringUtils.leftPad(totalCount.toString(), 10), StringUtils.leftPad(totalAmount.toString(), 20)) + "\n");
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_COMPLETE));

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendAchSummaryEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * Writes out payment file field labels and values to <code>StringBuffer</code>
     * 
     * @param body <code>StringBuffer</code>
     */
    protected void addPaymentFieldsToBody(StringBuffer body, Integer batchId, String chart, String org, String subUnit, Date createDate, int paymentCount, KualiDecimal paymentTotal) {
        String batchIdLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.BATCH_ID);
        body.append(batchIdLabel + ": " + batchId + "\n");

        String chartLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, KFSPropertyConstants.CHART);
        body.append(chartLabel + ": " + chart + "\n");

        String orgLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, KFSPropertyConstants.ORG);
        body.append(orgLabel + ": " + org + "\n");

        String subUnitLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.SUB_UNIT);
        body.append(subUnitLabel + ": " + subUnit + "\n");

        String createDateLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.CREATION_DATE);
        body.append(createDateLabel + ": " + createDate + "\n");

        String paymentCountLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.PAYMENT_COUNT);
        body.append("\n" + paymentCountLabel + ": " + paymentCount + "\n");

        String paymentTotalLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.PAYMENT_TOTAL_AMOUNT);
        body.append(paymentTotalLabel + ": " + paymentTotal + "\n");
    }

    /**
     * Reads system parameter indicating whether to status emails should be sent
     * 
     * @return true if email should be sent, false otherwise
     */
    protected boolean isPaymentEmailEnabled() {
        boolean noEmail = parameterService.getIndicatorParameter(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL);
        if (noEmail) {
            LOG.debug("sendLoadEmail() sending payment file email is disabled");
            return false;
        }

        return true;
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     * 
     * @param subjectParmaterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParmaterName) {
        String subject = parameterService.getParameterValue(LoadPaymentsStep.class, subjectParmaterName);

        String productionEnvironmentCode = kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }

        return subject;
    }

    /**
     * Helper method to retrieve a message from resources and substitute place holder values
     * 
     * @param messageKey key of message in resource file
     * @param messageParameters parameter for message
     * @return <code>String</code> Message with substituted values
     */
    protected String getMessage(String messageKey, Object... messageParameters) {
        String message = kualiConfigurationService.getPropertyString(messageKey);
        return MessageFormat.format(message, messageParameters);
    }

    /**
     * Sets the customerProfileService attribute value.
     * 
     * @param customerProfileService The customerProfileService to set.
     */
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the mailService attribute value.
     * 
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
