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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.renderers.Renderer;

/**
 * Metadata about something that will be responsible for rendering some total of some accounting line group sometime, or something
 */
public abstract class TotalDefinition extends DataDictionaryDefinitionBase {
    
    /**
     * Returns a renderer which will render the total for this total definition 
     * @return a Renderer which will render a total
     */
    public abstract Renderer getTotalRenderer();
}
