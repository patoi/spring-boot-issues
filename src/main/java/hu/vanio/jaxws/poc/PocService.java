/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.vanio.jaxws.poc;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.annotation.XmlMimeType;

/**
 *
 * @author Pato Istvan <istvan.pato@vanio.hu>
 */
@WebService
@SOAPBinding(style = Style.RPC, use=Use.LITERAL) //optional
public interface PocService {
    
    @WebMethod
    public void fileUpload(String name, @XmlMimeType("application/octet-stream") DataHandler data);
}
