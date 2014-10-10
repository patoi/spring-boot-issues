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
 * MTOM streaming app starter.
 * @author Pato Istvan <istvan.pato@vanio.hu>
 */
public class Start {

    public static String SERVICE_URL = "http://localhost:9999/ws/pocserver";
    public static String SERVICE_WSDL_URL = "http://localhost:9999/ws/pocserver?wsdl";

    public static void main(String[] args) throws Exception {
        
        /**
         * HA BEKAPCSOLOD A LOGOLÁST AKKOR MINDENKÉPPEN BETÖLTI MEMÓRIÁBA MIELŐTT FELDOLGOZNÁ.
         */
        // JAX-WS loggolás: http://stackoverflow.com/questions/1945618/tracing-xml-request-responses-with-jax-ws
//        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
//        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
//        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
//        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

        Endpoint.publish("http://localhost:9999/ws/pocserver", new PocServiceImpl());

        // client
        URL url = new URL(SERVICE_WSDL_URL);
        QName qname = new QName("http://poc.jaxws.vanio.hu/", "PocServiceImplService");

        MTOMFeature mtom = new MTOMFeature(1024);
        StreamingAttachmentFeature stf = new StreamingAttachmentFeature("/tmp/0_tmp_client", true, 4000000L);

        Service service = Service.create(url, qname);
        PocService s = service.getPort(PocService.class, mtom, stf);
        Map<String, Object> ctxt = ((BindingProvider) s).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);

        //enable MTOM in client
        BindingProvider bp = (BindingProvider) s;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);

        URL xurl = new URL("http://cms.mecheng.strath.ac.uk/t4/images/slideshow/Space_ASCLslide.jpg?1412640000024");
//        URL lurl = new URL("http://cms.mecheng.strath.ac.uk/t4/images/slideshow/Space_ASCLslide.jpg?1412640000024");
        URL lurl = new URL("http://cdimage.ubuntu.com/xubuntu/releases/14.04/release/xubuntu-14.04-desktop-amd64.iso");
//        URL lurl = new URL("http://cdimage.ubuntu.com/xubuntu/releases/14.04/release/xubuntu-14.04-desktop-amd64.iso");
//        URL lurl = new URL("http://phillw.net/isos/lubuntu/lucid/lubuntu-10.04-desktop-i386.iso");
        System.out.println("Started...");
        DataHandler dh2 = new DataHandler(lurl);
        DataHandler dh1 = new DataHandler(xurl);
        s.fileUpload("hubble-", dh1, dh2);

        System.exit(0);

    }
}
