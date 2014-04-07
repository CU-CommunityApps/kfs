/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;

/**
 * This class returns list of travel expense company value pairs.
 */
public interface TravelExpenseTypeService {

    Map<String, String> getCompanyNameMapFrom(final String expenseTypeCode);

    List<ConcreteKeyValue> getCompanyNamePairsFrom(final String expenseTypeCode);
}
