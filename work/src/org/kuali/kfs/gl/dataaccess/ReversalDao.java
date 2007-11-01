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
package org.kuali.module.gl.dao;

import java.util.Date;
import java.util.Iterator;

import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;

/**
 * An interface that declares the methods needed for reversal services to interact with the database
 */
public interface ReversalDao {
    /**
     * Saves a reversal record
     * 
     * @param re a reversal to save
     */
    public void save(Reversal re);

    /**
     * Returns
     * 
     * @param t
     * @return
     */
    public int getMaxSequenceNumber(Transaction t);

    /**
     * Looks up the reversal that matches the keys from the given transaction
     * 
     * @param t
     * @return
     */
    public Reversal getByTransaction(Transaction t);

    /**
     * Returns all reversals that should have reversed on or before the given date
     * 
     * @param before the date that reversals retrieved should reverse on or before
     * @return an iterator of reversal records
     */
    public Iterator getByDate(Date before);

    /**
     * Deletes a reversal record
     * 
     * @param re a reversal to delete
     */
    public void delete(Reversal re);
}
