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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;


public interface PurApAccountingLine extends AccountingLine {
    
    public abstract Integer getAccountIdentifier();

    public abstract void setAccountIdentifier(Integer accountIdentifier);

    public abstract Integer getItemIdentifier();

    public abstract void setItemIdentifier(Integer itemIdentifier);

    public abstract BigDecimal getAccountLinePercent();

    public abstract void setAccountLinePercent(BigDecimal accountLinePercent);
    
    public abstract boolean isEmpty();
    
    public abstract PurApAccountingLine createBlankAmountsCopy();
    
    public abstract boolean accountStringsAreEqual(SourceAccountingLine accountingLine);

    public abstract boolean accountStringsAreEqual(PurApAccountingLine accountingLine);
    
    public abstract SourceAccountingLine generateSourceAccountingLine();

    public KualiDecimal getAlternateAmountForGLEntryCreation();

    public void setAlternateAmountForGLEntryCreation(KualiDecimal alternateAmountForGLEntryCreation);
    
}