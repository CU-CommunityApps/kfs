/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * 
 * Purchasing Item Capital Asset Event
 */
public interface AttributedPurchasingItemCapitalAssetEvent extends AttributedDocumentEvent {

    /**
     * gets an item for the item events
     * 
     * @return an item
     */
    public abstract ItemCapitalAsset getItemCapitalAsset();

}
