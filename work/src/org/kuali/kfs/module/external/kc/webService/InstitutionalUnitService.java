package org.kuali.kfs.module.external.kc.webService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.UnitDTO;


/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 15:50:58 HST 2010
 * Generated source version: 2.2.10
 * 
 */
 
public interface InstitutionalUnitService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getUnit", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.GetUnit")
    @WebMethod
    @ResponseWrapper(localName = "getUnitResponse", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.GetUnitResponse")
    public UnitDTO getUnit(
        @WebParam(name = "unitNumber", targetNamespace = "")
        java.lang.String unitNumber
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getParentUnits", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.GetParentUnits")
    @WebMethod
    @ResponseWrapper(localName = "getParentUnitsResponse", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.GetParentUnitsResponse")
    public java.util.List<java.lang.String> getParentUnits(
        @WebParam(name = "unitNumber", targetNamespace = "")
        java.lang.String unitNumber
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "lookupUnits", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.LookupUnits")
    @WebMethod
    @ResponseWrapper(localName = "lookupUnitsResponse", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.LookupUnitsResponse")
    public java.util.List<UnitDTO> lookupUnits(
        @WebParam(name = "searchCriteria", targetNamespace = "")
        java.util.List<HashMapElement> searchCriteria
    );
}