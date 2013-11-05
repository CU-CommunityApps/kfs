/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.dto.SponsorDTO;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.SponsorWebSoapService;
import org.kuali.kra.external.sponsor.service.SponsorWebService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 *
 */

public class SponsorServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SponsorServiceImpl.class);

    protected SponsorWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        SponsorWebService sponsorWebService = (SponsorWebService) GlobalResourceLoader.getService(KcConstants.Sponsor.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (sponsorWebService == null) {
            LOG.warn("Couldn't get SponsorWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            SponsorWebSoapService ss =  null;
            try {
                ss = new SponsorWebSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize SponsorWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize SponsorWebSoapService: " + ex.getMessage());
            }
            sponsorWebService = ss.getSponsorWebServicePort();
        }

        return sponsorWebService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        SponsorDTO dto  = this.getWebService().getSponsor((String)primaryKeys.get("sponsorCode"));
        return new Agency(dto);
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        java.util.List <HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        List<SponsorDTO> result = null;

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.Unit.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val);
                hashMapList.add(hashMapElement);
            }
        }
        try {
          result  = this.getWebService().getMatchingSponsors(hashMapList);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, KfsService.getWebServiceServerName());
        }

        if (result == null) {
            return new ArrayList();
        } else {
            List<Agency> agencies = new ArrayList<Agency>();
            for (SponsorDTO dto : result) {
                agencies.add(new Agency(dto));
            }
            return agencies;
        }
    }

 }
