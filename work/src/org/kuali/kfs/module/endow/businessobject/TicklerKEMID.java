/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class TicklerKEMID extends PersistableBusinessObjectBase implements MutableInactivatable
{
    protected String number;
    protected String kemId;
    protected boolean active;

    protected KEMID kemIdLookup;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getKemId() {
        return kemId;
    }

    public void setKemId(String kemId) {
        this.kemId = kemId;
    }

    public KEMID getKemIdLookup() {
        return kemIdLookup;
    }

    public void setKemIdLookup(KEMID kemIdLookup) {
        this.kemIdLookup = kemIdLookup;
    }

}

