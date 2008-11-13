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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class GlLineForm extends KualiForm {
    private List<GeneralLedgerEntry> relatedGlEntries;
    private Long primaryGlAccountId;
    private CapitalAssetInformation capitalAssetInformation;
    private boolean selectAllGlEntries;

    public GlLineForm() {
        this.relatedGlEntries = new ArrayList<GeneralLedgerEntry>();
    }

    /**
     * Gets the relatedGlEntries attribute.
     * 
     * @return Returns the relatedGlEntries.
     */
    public List<GeneralLedgerEntry> getRelatedGlEntries() {
        return relatedGlEntries;
    }

    /**
     * Sets the relatedGlEntries attribute value.
     * 
     * @param relatedGlEntries The relatedGlEntries to set.
     */
    public void setRelatedGlEntries(List<GeneralLedgerEntry> relatedGlEntries) {
        this.relatedGlEntries = relatedGlEntries;
    }


    /**
     * Gets the primaryGlAccountId attribute.
     * 
     * @return Returns the primaryGlAccountId.
     */
    public Long getPrimaryGlAccountId() {
        return primaryGlAccountId;
    }

    /**
     * Sets the primaryGlAccountId attribute value.
     * 
     * @param primaryGlAccountId The primaryGlAccountId to set.
     */
    public void setPrimaryGlAccountId(Long primaryGlAccountId) {
        this.primaryGlAccountId = primaryGlAccountId;
    }

    /**
     * Gets the capitalAssetInformation attribute.
     * 
     * @return Returns the capitalAssetInformation.
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     * 
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    /**
     * Initialize index for struts
     * 
     * @param index current
     * @return value
     */
    public GeneralLedgerEntry getRelatedGlEntry(int index) {
        while (getRelatedGlEntries().size() <= index) {
            getRelatedGlEntries().add(new GeneralLedgerEntry());
        }
        return (GeneralLedgerEntry) getRelatedGlEntries().get(index);
    }

    /**
     * Gets the selectAllGlEntries attribute.
     * 
     * @return Returns the selectAllGlEntries.
     */
    public boolean isSelectAllGlEntries() {
        return selectAllGlEntries;
    }

    /**
     * Sets the selectAllGlEntries attribute value.
     * 
     * @param selectAllGlEntries The selectAllGlEntries to set.
     */
    public void setSelectAllGlEntries(boolean selectAllGlEntries) {
        this.selectAllGlEntries = selectAllGlEntries;
    }
}
