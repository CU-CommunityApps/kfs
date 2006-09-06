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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ProposalPurpose extends BusinessObjectBase {

    private String proposalPurposeCode;
    private String proposalPurposeDescription;
    private boolean rowActiveIndicator;
    
    /**
     * Default constructor.
     */
    public ProposalPurpose() {

    }

    /**
     * Gets the proposalPurposeCode attribute.
     * 
     * @return - Returns the proposalPurposeCode
     * 
     */
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    /**
     * Sets the proposalPurposeCode attribute.
     * 
     * @param proposalPurposeCode The proposalPurposeCode to set.
     * 
     */
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }


    /**
     * Gets the proposalPurposeDescription attribute.
     * 
     * @return - Returns the proposalPurposeDescription
     * 
     */
    public String getProposalPurposeDescription() {
        return proposalPurposeDescription;
    }

    /**
     * Sets the proposalPurposeDescription attribute.
     * 
     * @param proposalPurposeDescription The proposalPurposeDescription to set.
     * 
     */
    public void setProposalPurposeDescription(String proposalPurposeDescription) {
        this.proposalPurposeDescription = proposalPurposeDescription;
    }

    /**
     * Gets the rowActiveIndicator attribute. 
     * @return Returns the rowActiveIndicator.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator attribute value.
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalPurposeCode", this.proposalPurposeCode);
        return m;
    }
}
