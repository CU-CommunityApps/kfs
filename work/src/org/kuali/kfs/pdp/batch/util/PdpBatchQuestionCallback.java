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
package org.kuali.kfs.pdp.batch.util;

import org.kuali.rice.kns.bo.user.UniversalUser;

/**
 * PDP Batch Question Callback defines a callback method for post processing handling in the question interface.
 */
public interface PdpBatchQuestionCallback {

    /**
     * Hooks for performing different actions on batch after a question has been performed.
     * 
     * @param batchIdString the id of the batch
     * @param note a note from the user
     * @param user the user that perfoms the action
     * @return true if succesful, false otherwise
     */
    public boolean doPostQuestion(String batchIdString, String note, UniversalUser user);

}
