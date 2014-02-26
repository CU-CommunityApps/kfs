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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.InstrumentType;
import org.kuali.kfs.module.external.kc.dto.AwardTypeDTO;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.AwardTypeWebSoapService;
import org.kuali.kra.external.awardtype.AwardTypeWebService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 *
 */

public class AwardTypeServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardTypeServiceImpl.class);

    protected AwardTypeWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        AwardTypeWebService awardTypeWebService = (AwardTypeWebService) GlobalResourceLoader.getService(KcConstants.AwardType.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (awardTypeWebService == null) {
            LOG.warn("Couldn't get AwardWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            AwardTypeWebSoapService ss =  null;
            try {
                ss = new AwardTypeWebSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize AwardTypeWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize AwardTypeWebSoapService: " + ex.getMessage());
            }
            awardTypeWebService = ss.getAwardTypeWebServicePort();
        }

        return awardTypeWebService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        AwardTypeDTO dto  = this.getWebService().getAwardType((Integer)primaryKeys.get("instrumentTypeCode"));
        return typeFromDTO(dto);
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        java.util.List <HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        List<AwardTypeDTO> result = null;

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            String key = (String) e.getKey();
            String val = (String) e.getValue();

            HashMapElement hashMapElement = new HashMapElement();
            if (StringUtils.equals(key, "instrumentTypeCode")) {
                hashMapElement.setKey("awardTypeCode");
                hashMapElement.setValue(val);
            } else if (StringUtils.equals(key, "instrumentTypeDescription")) {
                hashMapElement.setKey("description");
                hashMapElement.setValue(val);
            }
            hashMapList.add(hashMapElement);
        }
        try {
          result  = this.getWebService().findMatching(hashMapList);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, KfsService.getWebServiceServerName());
        }

        if (result == null) {
            return new ArrayList();
        } else {
            List<InstrumentType> types = new ArrayList<InstrumentType>();
            for (AwardTypeDTO dto : result) {
                types.add(typeFromDTO(dto));
            }
            return types;
        }
    }

    protected InstrumentType typeFromDTO(AwardTypeDTO awardType) {
        InstrumentType type = new InstrumentType();
        type.setInstrumentTypeCode(awardType.getAwardTypeCode().toString());
        type.setInstrumentTypeDescription(awardType.getDescription());
        return type;
    }

 }
