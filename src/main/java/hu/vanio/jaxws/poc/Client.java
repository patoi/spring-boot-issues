/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.vanio.jaxws.poc;

import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import java.net.URL;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

/**
 *
 * @author Pato Istvan <istvan.pato@vanio.hu>
 */
public class Client {

    public static String SERVICE_URL = "http://localhost:9999/ws/pocserver";
    public static String SERVICE_WSDL_URL = "http://localhost:9999/ws/pocserver?wsdl";

    public static void main(String[] args) throws Exception {

        Endpoint.publish("http://localhost:9999/ws/pocserver", new PocServiceImpl());

        // client
        URL url = new URL(SERVICE_WSDL_URL);
        QName qname = new QName("http://poc.jaxws.vanio.hu/", "PocServiceImplService");

        MTOMFeature mtom = new MTOMFeature(1024);
        StreamingAttachmentFeature stf = new StreamingAttachmentFeature("/tmp", true, 4000000L);

        Service service = Service.create(url, qname);
        PocService s = service.getPort(PocService.class, mtom, stf);
        Map<String, Object> ctxt = ((BindingProvider) s).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);

        //enable MTOM in client
        BindingProvider bp = (BindingProvider) s;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);

//        URL xurl = new URL("http://upload.wikimedia.org/wikipedia/commons/1/1c/NGC_6302_Hubble_2009.full.jpg");
        URL xurl = new URL("http://cdimage.ubuntu.com/xubuntu/releases/14.04/release/xubuntu-14.04-desktop-amd64.iso");
        System.out.println("Started...");
        DataHandler dh = new DataHandler(xurl);
        s.fileUpload("hubble-", dh);

        System.exit(0);

    }
}
