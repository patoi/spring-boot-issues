package hu.vanio.jaxws.poc;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.annotation.XmlMimeType;

/**
 * MTOM service interface.
 * @author Pato Istvan <istvan.pato@vanio.hu>
 */
@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL) //optional
public interface PocService {

    @WebMethod
    public void fileUpload(String name, @XmlMimeType("application/octet-stream") DataHandler data);
}
