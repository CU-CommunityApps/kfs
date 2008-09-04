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
package org.kuali.kfs.sys.document.workflow;

public final class RoutingAccount extends RoutingObject {
    String routingChart;
    String routingAccount;
    
    public RoutingAccount(String routingChart, String routingAccount){
        this.routingChart=routingChart;
        this.routingAccount=routingAccount;
    }
    
    public String getRoutingAccount() {
        return routingAccount;
    }
    public void setRoutingAccount(String routingAccount) {
        this.routingAccount = routingAccount;
    }
    public String getRoutingChart() {
        return routingChart;
    }
    public void setRoutingChart(String routingChart) {
        this.routingChart = routingChart;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((routingAccount == null) ? 0 : routingAccount.hashCode());
        result = PRIME * result + ((routingChart == null) ? 0 : routingChart.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RoutingAccount other = (RoutingAccount) obj;
        if (routingAccount == null) {
            if (other.routingAccount != null)
                return false;
        }
        else if (!routingAccount.equals(other.routingAccount))
            return false;
        if (routingChart == null) {
            if (other.routingChart != null)
                return false;
        }
        else if (!routingChart.equals(other.routingChart))
            return false;
        return true;
    }
}
