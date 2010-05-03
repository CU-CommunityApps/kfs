/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentForm;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;


public class LiabilityIncreaseDocumentAction extends EndowmentTransactionLinesDocumentActionBase 
{
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception 
    {
        super.refresh(mapping, form, request, response);
        
        LiabilityIncreaseDocument liabilityIncreaseDocument = ((LiabilityIncreaseDocumentForm) form).getLiabilityIncreaseDocument();
        
/*        //Target
        //Saving a Trans Line
        EndowmentTransactionLine tranLine = new EndowmentTargetTransactionLine();
        tranLine.setTransactionLineNumber(new KualiInteger("1"));
        tranLine.setKemid("099PLTF013");
        tranLine.setEtranCode("00100");
        tranLine.setTransactionIPIndicatorCode("I");
        tranLine.setTransactionAmount(new KualiDecimal(2.1));
        
        //Setting the transaction line.
        List tranList = new ArrayList();
        tranList.add(tranLine);
        liabilityIncreaseDocument.setTargetTransactionLines(tranList);
        
        
        tranLine = new EndowmentTargetTransactionLine();
        tranLine.setDocumentNumber("4160");
        tranLine.setTransactionLineNumber(new KualiInteger("2"));
        tranLine.setKemid("099PLTF013");
        tranLine.setEtranCode("00100");
        tranLine.setTransactionIPIndicatorCode("I");
        tranLine.setTransactionAmount(new KualiDecimal(2.2));
        
        tranList.add(tranLine);

        //Source
        tranLine = new EndowmentSourceTransactionLine();
        tranLine.setDocumentNumber("4160");
        tranLine.setTransactionLineNumber(new KualiInteger("3"));
        tranLine.setKemid("099PLTF013");
        tranLine.setEtranCode("00100");
        tranLine.setTransactionIPIndicatorCode("I");
        tranLine.setTransactionAmount(new KualiDecimal(2.3));
        
        //Setting the transaction line.
        List stranList = new ArrayList();
        stranList.add(tranLine);
        liabilityIncreaseDocument.setSourceTransactionLines(stranList);

        
        EndowmentTransactionTaxLotLine hldg = new EndowmentTransactionTaxLotLine();
        hldg.setDocumentNumber("4160");
        hldg.setDocumentLineNumber(new KualiInteger("1"));
        hldg.setDocumentLineTypeCode("F");
        hldg.setTransactionHoldingLongTermNumber(new KualiInteger("99"));
        hldg.setLotUnits(new KualiDecimal("22"));
        
        List taxList = new ArrayList();
        taxList.add(hldg);

        EndowmentTransactionTaxLotLine hldg = new EndowmentTransactionTaxLotLine();
        hldg.setDocumentLineNumber(new KualiInteger("2"));
        hldg.setDocumentLineTypeCode("F");
        hldg.setTransactionHoldingLongTermNumber(new KualiInteger("99"));
        hldg.setLotUnits(new KualiDecimal("22"));

        List taxList = new ArrayList();
        taxList.add(hldg);
        
        tranLine.setTaxLotLines(taxList);*/
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
        

}
