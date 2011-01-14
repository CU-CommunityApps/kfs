
/*
 * 
 */

package org.kuali.kfs.module.external.kc.webService.institutionalUnitService;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 15:50:58 HST 2010
 * Generated source version: 2.2.10
 * 
 */


@WebServiceClient(name = "institutionalUnitSoapService", 
                  wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/institutionalUnitSoapService?wsdl",
                  targetNamespace = "KC") 
public class InstitutionalUnitSoapService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("KC", "institutionalUnitSoapService");
    public final static QName InstitutionalUnitServicePort = new QName("KC", "institutionalUnitServicePort");
    static {
        URL url = null;
        try {
            url = new URL("http://test.kc.kuali.org/kc-trunk/remoting/institutionalUnitSoapService?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://test.kc.kuali.org/kc-dly30/remoting/institutionalUnitSoapService?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public InstitutionalUnitSoapService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public InstitutionalUnitSoapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public InstitutionalUnitSoapService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns InstitutionalUnitService
     */
    @WebEndpoint(name = "institutionalUnitServicePort")
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
    @WebEndpoint(name = "institutionalUnitServicePort")
    public InstitutionalUnitService getInstitutionalUnitServicePort(WebServiceFeature... features) {
        return super.getPort(InstitutionalUnitServicePort, InstitutionalUnitService.class, features);
    }

}
