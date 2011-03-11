
/*
 * 
 */

package org.kuali.kfs.module.external.kc.webService;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.impl.KfsService;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 15:50:58 HST 2010
 * Generated source version: 2.2.10
 * 
 */


@WebServiceClient(name = KcConstants.Unit.SOAP_SERVICE_NAME, 
                  wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/institutionalUnitSoapService?wsdl",
                  targetNamespace = KcConstants.KC_NAMESPACE_URI) 
public class InstitutionalUnitSoapService extends KfsService {

    public final static QName InstitutionalUnitServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.Unit.SERVICE_PORT);
    static {
        try {
           getWsdl(KcConstants.Unit.SERVICE); 
         } catch (MalformedURLException e) {
             LOG.warn("Can not initialize the wsdl");
         }
    }

    public InstitutionalUnitSoapService(URL wsdlLocation) {
        super(wsdlLocation, KcConstants.Unit.SERVICE);
    }

    public InstitutionalUnitSoapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public InstitutionalUnitSoapService() {
        super(WSDL_LOCATION, KcConstants.Unit.SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns InstitutionalUnitService
     */
    @WebEndpoint(name = KcConstants.Unit.SERVICE_PORT)
    public InstitutionalUnitService getInstitutionalUnitServicePort() {
        return super.getPort(InstitutionalUnitServicePort, InstitutionalUnitService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns InstitutionalUnitService
     */
    @WebEndpoint(name = KcConstants.Unit.SERVICE_PORT)
    public InstitutionalUnitService getInstitutionalUnitServicePort(WebServiceFeature... features) {
        return super.getPort(InstitutionalUnitServicePort, InstitutionalUnitService.class, features);
    }

}
