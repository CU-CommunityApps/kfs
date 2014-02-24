/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ClassOfService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ClassOfServiceValuesFinder extends KeyValuesBase {

    private String expenseTypeMetaCategoryCode;
    protected static volatile KeyValuesService keyValuesService;

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        Collection<ClassOfService> bos = null;
        if(ObjectUtils.isNotNull(expenseTypeMetaCategoryCode)){
            Map<String, Object> searchMap = new HashMap<String, Object>();
            searchMap.put(TemPropertyConstants.ClassOfService.EXPENSE_TYPE_META_CATEGORY_CODE, expenseTypeMetaCategoryCode);
            bos = getKeyValuesService().findMatching(ClassOfService.class, searchMap);
        }
        else{
            bos = getKeyValuesService().findAll(ClassOfService.class);
        }

        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        for (ClassOfService typ : bos) {
            keyValues.add(new ConcreteKeyValue(typ.getCode(), typ.getClassOfServiceName()));
        }

        return keyValues;
    }

    public String getExpenseTypeMetaCategoryCode() {
        return expenseTypeMetaCategoryCode;
    }

    public void setExpenseTypeMetaCategoryCode(String expenseTypeMetaCategoryCode) {
        this.expenseTypeMetaCategoryCode = expenseTypeMetaCategoryCode;
    }

    protected KeyValuesService getKeyValuesService() {
        if (keyValuesService == null) {
            keyValuesService = SpringContext.getBean(KeyValuesService.class);
        }
        return keyValuesService;
    }

}
