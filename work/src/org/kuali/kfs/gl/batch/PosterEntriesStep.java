/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * The step that runs the poster service on entries.
 */
public class PosterEntriesStep extends AbstractWrappedBatchStep {
    private PosterService posterService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                posterService.postMainEntries();
                return true;
            }
        };
    }

    public void setPosterService(PosterService posterService) {
        this.posterService = posterService;
    }
}
