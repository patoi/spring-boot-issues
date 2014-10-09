/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.vanio.jaxws.poc;

import com.sun.xml.ws.developer.StreamingAttachment;
import com.sun.xml.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

/**
 *
 * @author Pato Istvan <istvan.pato@vanio.hu>
 */
@MTOM(enabled = true, threshold = 30000)
@StreamingAttachment(dir = "/tmp/0_tmp", parseEagerly = true, memoryThreshold = 300000L)
@WebService(endpointInterface = "hu.vanio.jaxws.poc.PocService")
public class PocServiceImpl implements PocService {

    @Override
    public void fileUpload(String name,
            @XmlMimeType("application/octet-stream") DataHandler data) {
        try {
            System.out.println("upload Start");

            StreamingDataHandler dh = (StreamingDataHandler) data;
            System.out.println("Content-Type: " + dh.getContentType());
            System.out.println("HrefCid: " + dh.getHrefCid());
            System.out.println("Name: " + dh.getName());

//            File file = File.createTempFile(name, "");
//            dh.moveTo(file);
            // write to file
            File file = File.createTempFile(name, ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = dh.getInputStream();
//            IOUtils.copy(is, fos);

            byte[] buf = new byte[1024];
            long total = 0;
            while (true) {
                int r = is.read(buf);
                if (r == -1) {
                    break;
                }
                fos.write(buf, 0, r);
                total += r;
                
                System.out.println((total / 1000) + " kbytes");
            }

            is.close();
            fos.flush();
            fos.close();
            dh.close();
            
            System.out.println("upload End");
        } catch (Exception e) {
            throw new WebServiceException(e);
        }
        
    }

}
