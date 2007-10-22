/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.purap.bo;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Requisition Item Business Object.
 */
public class RequisitionItem extends PurchasingItemBase {

	private boolean itemRestrictedIndicator;

    private RequisitionDocument requisition;
	
	/**
	 * Default constructor.
	 */
	public RequisitionItem() {
	}

    public boolean isItemRestrictedIndicator() {
        return itemRestrictedIndicator;
    }

    public void setItemRestrictedIndicator(boolean itemRestrictedIndicator) {
        this.itemRestrictedIndicator = itemRestrictedIndicator;
    }

    public RequisitionDocument getRequisition() {
        return requisition;
    }

    public void setRequisition(RequisitionDocument requisition) {
        this.requisition = requisition;
    }

    /**
     * @see org.kuali.module.purap.bo.PurchasingItemBase#getAccountingLineClass()
     */
    @Override
    public Class getAccountingLineClass() {
       return RequisitionAccount.class;
    }
    
}