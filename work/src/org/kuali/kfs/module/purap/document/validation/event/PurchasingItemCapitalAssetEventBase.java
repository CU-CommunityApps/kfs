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
package org.kuali.kfs.module.purap.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemCapitalAsset;

/**
 * Event Base class for Purchasing Item Capital Asset
 * 
 * contains the base methods for item events
 */
public abstract class PurchasingItemCapitalAssetEventBase extends KualiDocumentEventBase implements PurchasingItemCapitalAssetEvent {
    private static final Logger LOG = Logger.getLogger(PurchasingItemCapitalAssetEventBase.class);


    private final PurchasingItemCapitalAsset itemCapitalAsset;

    /**
     * Copies the item and calls the super constructor
     * 
     * @param description the description of the event
     * @param errorPathPrefix the error path
     * @param document the document the event is being called on
     * @param item the item that is having the event called on
     */
    public PurchasingItemCapitalAssetEventBase(String description, String errorPathPrefix, Document document, PurchasingItemCapitalAsset itemCapitalAsset) {
        super(description, errorPathPrefix, document);

        this.itemCapitalAsset = itemCapitalAsset;

        logEvent();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.event.PurchasingItemCapitalAssetEvent#getItemCapitalAsset()
     */
    public PurchasingItemCapitalAsset getItemCapitalAsset() {
        return itemCapitalAsset;
    }


    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getItemCapitalAsset() == null) {
            throw new IllegalArgumentException("invalid (null) item");
        }
    }

    /**
     * Logs the event type and some information about the associated item
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (itemCapitalAsset == null) {
            logMessage.append("null item capital asset");
        }
        else {
            logMessage.append(" item capital asset# ");
            logMessage.append(itemCapitalAsset.getItemCapitalAssetIdentifier());
        }

        LOG.debug(logMessage);
    }
}
