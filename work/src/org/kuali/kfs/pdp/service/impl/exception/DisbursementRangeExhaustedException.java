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
/*
 * Created on Aug 24, 2004
 *
 */
package org.kuali.kfs.pdp.service.impl.exception;

public class DisbursementRangeExhaustedException extends RuntimeException {

    public DisbursementRangeExhaustedException() {
        super();
    }

    public DisbursementRangeExhaustedException(String arg0) {
        super(arg0);
    }

    public DisbursementRangeExhaustedException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DisbursementRangeExhaustedException(Throwable arg0) {
        super(arg0);
    }
}
