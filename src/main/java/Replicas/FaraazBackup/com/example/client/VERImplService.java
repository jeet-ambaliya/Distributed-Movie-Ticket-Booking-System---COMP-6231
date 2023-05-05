
package Replicas.FaraazBackup.com.example.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "VERImplService", targetNamespace = "http://example.com/ver", wsdlLocation = "http://localhost:8086/ver?wsdl")
public class VERImplService
    extends Service
{

    private final static URL VERIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException VERIMPLSERVICE_EXCEPTION;
    private final static QName VERIMPLSERVICE_QNAME = new QName("http://example.com/ver", "VERImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8086/ver?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        VERIMPLSERVICE_WSDL_LOCATION = url;
        VERIMPLSERVICE_EXCEPTION = e;
    }

    public VERImplService() {
        super(__getWsdlLocation(), VERIMPLSERVICE_QNAME);
    }

    public VERImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), VERIMPLSERVICE_QNAME, features);
    }

    public VERImplService(URL wsdlLocation) {
        super(wsdlLocation, VERIMPLSERVICE_QNAME);
    }

    public VERImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, VERIMPLSERVICE_QNAME, features);
    }

    public VERImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public VERImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns VERImpl
     */
    @WebEndpoint(name = "VERImplPort")
    public VERImpl getVERImplPort() {
        return super.getPort(new QName("http://example.com/ver", "VERImplPort"), VERImpl.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns VERImpl
     */
    @WebEndpoint(name = "VERImplPort")
    public VERImpl getVERImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://example.com/ver", "VERImplPort"), VERImpl.class, features);
    }

    private static URL __getWsdlLocation() {
        if (VERIMPLSERVICE_EXCEPTION!= null) {
            throw VERIMPLSERVICE_EXCEPTION;
        }
        return VERIMPLSERVICE_WSDL_LOCATION;
    }

}
