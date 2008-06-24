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
package org.kuali.kfs.gl.batch.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatus;
import org.kuali.kfs.sys.Message;

/**
 * A service that is used to provide notification about the status of an enterprise feed. The implementation may use a variety of
 * other services to perform notification, ranging from simply logging data to sending emails, etc.
 */
public interface EnterpriseFeederNotificationService {
    /**
     * Performs notification about the status of the upload (i.e. feeding) of a single file set (i.e. done file, data file, and
     * recon file).
     * 
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *        feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *        value.
     * @param event The event/status of the upload of the file set
     * @param doneFile The done file
     * @param dataFile The data file
     * @param reconFile The recon file
     * @param errorMessages Any error messages for which to provide notification
     */
    public void notifyFileFeedStatus(String feederProcessName, EnterpriseFeederStatus status, File doneFile, File dataFile, File reconFile, List<Message> errorMessages);


    /**
     * Performs notification about the status of the upload (i.e. feeding) of a single file set (i.e. done file, data file, and
     * recon file). This method is useful when the file sets are not <b>NOTE:</b> the CALLER MUST CLOSE all of the input streams
     * that are passed in. In addition, the input streams may be used by implementations of this method, and no assumption about the
     * state of the input streams should be made after this method returns.
     * 
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *        feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *        value.
     * @param event The event/status of the upload of the file set
     * @param doneFileDescription The description of the done file to be output during notification
     * @param doneFileContents An input stream for the contents of the done file. If the implementation does not require the
     *        contents of the file, then <code>null</code> may be passed in.
     * @param dataFileDescription The description of the done file to be output during notification
     * @param dataFileContents An input stream for the contents of the data file. If the implementation does not require the
     *        contents of the file, then <code>null</code> may be passed in.
     * @param reconFileDescription The description of the done file to be output during notification
     * @param reconFileContents An input stream for the contents of the recon file. If the implementation does not require the
     *        contents of the file, then <code>null</code> may be passed in.
     * @param errorMessages Any error messages for which to provide notification
     */
    public void notifyFileFeedStatus(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, InputStream doneFileContents, String dataFileDescription, InputStream dataFileContents, String reconFileDescription, InputStream reconFileContents, List<Message> errorMessages);

    /**
     * Generates the status message that would be generated by a call to notifyFileFeedStatus with the same parameters.
     * 
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *        feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *        value.
     * @param event The event/status of the upload of the file set
     * @param doneFile The done file
     * @param dataFile The data file
     * @param reconFile The recon file
     * @param errorMessages Any error messages for which to provide notification
     */
    public String getFileFeedStatusMessage(String feederProcessName, EnterpriseFeederStatus status, File doneFile, File dataFile, File reconFile, List<Message> errorMessages);

    /**
     * Generates the status message that would be generated by a call to notifyFileFeedStatus with the same parameters. <b>NOTE:</b>
     * the CALLER MUST CLOSE all of the input streams that are passed in. In addition, the input streams may be used by
     * implementations of this method, and no assumption about the state of the input streams should be made after this method
     * returns.
     * 
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *        feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *        value.
     * @param event The event/status of the upload of the file set
     * @param doneFileDescription The description of the done file to be output during notification
     * @param doneFileContents An input stream for the contents of the done file. If the implementation does not require the
     *        contents of the file, then <code>null</code> may be passed in.
     * @param dataFileDescription The description of the done file to be output during notification
     * @param dataFileContents An input stream for the contents of the data file. If the implementation does not require the
     *        contents of the file, then <code>null</code> may be passed in.
     * @param reconFileDescription The description of the done file to be output during notification
     * @param reconFileContents An input stream for the contents of the recon file. If the implementation does not require the
     *        contents of the file, then <code>null</code> may be passed in.
     * @param errorMessages Any error messages for which to provide notification
     */
    public String getFileFeedStatusMessage(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, InputStream doneFileContents, String dataFileDescription, InputStream dataFileContents, String reconFileDescription, InputStream reconFileContents, List<Message> errorMessages);
}
