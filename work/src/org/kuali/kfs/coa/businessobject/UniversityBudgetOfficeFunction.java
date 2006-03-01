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

package org.kuali.module.chart.bo;

import org.kuali.core.bo.user.*;
import java.util.LinkedHashMap;
import org.kuali.module.financial.bo.*;
import org.kuali.module.cg.bo.*;
import org.kuali.module.chart.bo.codes.*;
import org.kuali.module.cams.bo.*;
import org.kuali.module.chart.bo.*;
import org.kuali.core.document.*;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.ar.bo.*;
import org.kuali.module.labor.bo.*;
import org.kuali.module.kra.bo.*;
import org.kuali.core.bo.*;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class UniversityBudgetOfficeFunction extends BusinessObjectBase {

	private String financialUniversityBudgetOfficeFunctionCode;
	private String financialUniversityBudgetOfficeFunctionName;

	/**
	 * Default constructor.
	 */
	public UniversityBudgetOfficeFunction() {

	}

	/**
	 * Gets the financialUniversityBudgetOfficeFunctionCode attribute.
	 * 
	 * @return - Returns the financialUniversityBudgetOfficeFunctionCode
	 * 
	 */
	public String getFinancialUniversityBudgetOfficeFunctionCode() { 
		return financialUniversityBudgetOfficeFunctionCode;
	}

	/**
	 * Sets the financialUniversityBudgetOfficeFunctionCode attribute.
	 * 
	 * @param - financialUniversityBudgetOfficeFunctionCode The financialUniversityBudgetOfficeFunctionCode to set.
	 * 
	 */
	public void setFinancialUniversityBudgetOfficeFunctionCode(String financialUniversityBudgetOfficeFunctionCode) {
		this.financialUniversityBudgetOfficeFunctionCode = financialUniversityBudgetOfficeFunctionCode;
	}


	/**
	 * Gets the financialUniversityBudgetOfficeFunctionName attribute.
	 * 
	 * @return - Returns the financialUniversityBudgetOfficeFunctionName
	 * 
	 */
	public String getFinancialUniversityBudgetOfficeFunctionName() { 
		return financialUniversityBudgetOfficeFunctionName;
	}

	/**
	 * Sets the financialUniversityBudgetOfficeFunctionName attribute.
	 * 
	 * @param - financialUniversityBudgetOfficeFunctionName The financialUniversityBudgetOfficeFunctionName to set.
	 * 
	 */
	public void setFinancialUniversityBudgetOfficeFunctionName(String financialUniversityBudgetOfficeFunctionName) {
		this.financialUniversityBudgetOfficeFunctionName = financialUniversityBudgetOfficeFunctionName;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialUniversityBudgetOfficeFunctionCode", this.financialUniversityBudgetOfficeFunctionCode);
	    return m;
    }
}
