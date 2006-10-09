/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class TravelPerDiem extends BusinessObjectBase {
    private Integer fiscalYear;
    private String perDiemCountryName;
    private KualiDecimal perDiemRate;
    private String perDiemCountryText;

    /**
     * Default no-arg constructor.
     */
    public TravelPerDiem() {

    }

    /**
     * @return Returns the fiscalYear.
     */
    public Integer getFiscalYear() {
        return fiscalYear;
    }

    /**
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * @return Returns the perDiemCountryName.
     */
    public String getPerDiemCountryName() {
        return perDiemCountryName;
    }

    /**
     * @param perDiemCountryName The perDiemCountryName to set.
     */
    public void setPerDiemCountryName(String perDiemCountryName) {
        this.perDiemCountryName = perDiemCountryName;
    }

    /**
     * @return Returns the perDiemCountryText.
     */
    public String getPerDiemCountryText() {
        return perDiemCountryText;
    }

    /**
     * @param perDiemCountryText The perDiemCountryText to set.
     */
    public void setPerDiemCountryText(String perDiemCountryText) {
        this.perDiemCountryText = perDiemCountryText;
    }

    /**
     * @return Returns the perDiemRate.
     */
    public KualiDecimal getPerDiemRate() {
        return perDiemRate;
    }

    /**
     * @param perDiemRate The perDiemRate to set.
     */
    public void setPerDiemRate(KualiDecimal perDiemRate) {
        this.perDiemRate = perDiemRate;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("perDiemCountryName", this.perDiemCountryName);
        return m;
    }
}