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
package org.kuali.module.gl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The reconciliation information corresponding to a whole file
 */
public class ReconciliationBlock {
    public ReconciliationBlock() {
        columns = new ArrayList<ColumnReconciliation>();
    }

    private String tableId;
    private int rowCount;
    private List<ColumnReconciliation> columns;

    /**
     * Gets the columns attribute. Do not modify the list or its contents.
     * 
     * @return Returns the columns.
     */
    public List<ColumnReconciliation> getColumns() {
        return columns;
    }

    /**
     * Adds a column reconciliation definition
     * 
     * @param column
     */
    public void addColumn(ColumnReconciliation column) {
        columns.add(column);
    }

    /**
     * Gets the rowCount attribute.
     * 
     * @return Returns the rowCount.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the rowCount attribute value.
     * 
     * @param rowCount The rowCount to set.
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Gets the tableId attribute.
     * 
     * @return Returns the tableId.
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Sets the tableId attribute value.
     * 
     * @param tableId The tableId to set.
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
