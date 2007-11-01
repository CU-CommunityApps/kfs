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
package org.kuali.module.purap.dao.ojb;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.ojb.broker.accesslayer.QueryCustomizerDefaultImpl;

public abstract class KualiQueryCustomizerDefaultImpl extends QueryCustomizerDefaultImpl {
    public Map<String, String> getAttributes() {
        // TODO: ctk ask about changing the other OjbQueryCustomizer to use this since
        // it's the same logic
        // this is necessary since the attributes are not exposed as a list by default
        Field field = null;
        try {
            field = KualiQueryCustomizerDefaultImpl.class.getSuperclass().getDeclaredField("m_attributeList");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        Map<String, String> m_attributeList = null;
        try {
            m_attributeList = (Map) field.get(this);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return m_attributeList;
    }
}
