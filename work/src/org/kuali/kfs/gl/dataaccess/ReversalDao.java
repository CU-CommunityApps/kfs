/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao;

import java.util.Date;
import java.util.Iterator;

import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;

/**
 * 
 * 
 */
public interface ReversalDao {
    public void save(Reversal re);

    public int getMaxSequenceNumber(Transaction t);

    public Reversal getByTransaction(Transaction t);

    public Iterator getByDate(Date before);

    public Iterator getSummaryByDate(Date before);

    public void delete(Reversal re);
}
