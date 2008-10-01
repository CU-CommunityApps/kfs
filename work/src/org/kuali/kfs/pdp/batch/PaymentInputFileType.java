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
package org.kuali.kfs.pdp.batch;

import java.io.File;
import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Batch input type for the PDP payment file.
 */
public class PaymentInputFileType extends BatchInputFileTypeBase {
    private DateTimeService dateTimeService;
    private PaymentFileService paymentFileService;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kns.bo.user.UniversalUser, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(UniversalUser user, Object parsedFileContents, String fileUserIdentifer) {
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();

        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        formatter.setLenient(false);
        formatter.format(currentTimestamp, buf, new FieldPosition(0));

        String fileName = PdpConstants.PDP_FILE_UPLOAD_FILE_PREFIX + user.getPersonUserIdentifier().toLowerCase();
        if (StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName += "_" + StringUtils.remove(fileUserIdentifer, " ");
        }
        fileName += "_" + buf.toString();

        return fileName;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return PdpConstants.PAYMENT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        PaymentFileLoad paymentFile = (PaymentFileLoad) parsedFileContents;
        paymentFileService.doPaymentFileValidation(paymentFile, GlobalVariables.getErrorMap());

        return paymentFile.isPassedValidation();
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#checkAuthorization(org.kuali.rice.kns.bo.user.UniversalUser, java.io.File)
     */
    public boolean checkAuthorization(UniversalUser user, File batchFile) {
        String userIdentifier = user.getPersonUserIdentifier();

        return StringUtils.contains(batchFile.getName(), userIdentifier.toLowerCase());
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return PdpKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PAYMENT;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getUploadWorkgroupParameterComponent()
     */
    public Class getUploadWorkgroupParameterComponent() {
        return LoadPaymentsStep.class;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileTypeBase#process(java.lang.String, java.lang.Object)
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {
        PaymentFileLoad paymentFile = (PaymentFileLoad) parsedFileContents;
        if (paymentFile.isPassedValidation()) {
            // collect various information for status of load
            LoadPaymentStatus status = new LoadPaymentStatus();
            status.setErrorMap(new ErrorMap());

            paymentFileService.loadPayments(paymentFile, status, fileName);
            paymentFileService.createOutputFile(status, fileName);
        }
    }

    /**
     * Sets the paymentFileService attribute value.
     * 
     * @param paymentFileService The paymentFileService to set.
     */
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
