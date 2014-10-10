package hu.vanio.jaxws.poc;

import com.sun.xml.ws.developer.StreamingDataHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

/**
 * Streaming very big file with MTOM.
 * @author Pato Istvan <istvan.pato@vanio.hu>
 */
@MTOM(enabled = true, threshold = 30000)
// ha a memoryThreshold = -1L akkor minden a memóriába megy, ha ettől eltér akkor temp filet készít
// a parseEagerly=true esetén megvárja amíg megérkezik a komplett állomány, és addig nem dolgozza fel, tehát vagy memóriában vagy fileban ott lesz az egész
// a parseEagerly=false esetén automatikusan elkezdi feldolgozni a streamet
// TOTÁLIS STREAMELÉSHEZ EZ AZ ANNOTÁCIÓ NEM KELL!
//@StreamingAttachment(dir = "/tmp/0_tmp", parseEagerly = false, memoryThreshold = -1L)
@WebService(endpointInterface = "hu.vanio.jaxws.poc.PocService")
public class PocServiceImpl implements PocService {

    static final int MB = 1024 * 1024;

    @Override
    public void fileUpload(String name,
            @XmlMimeType("application/octet-stream") DataHandler data1,
            @XmlMimeType("application/octet-stream") DataHandler data2) {
        try {
            System.out.println("upload Start");

            this.saveUpload(name, (StreamingDataHandler) data1);
            this.saveUpload(name, (StreamingDataHandler) data2);

        } catch (Exception e) {
            throw new WebServiceException(e);
        }

    }

    /**
     * Save int a temp file and logging.
     * @param name file name
     * @param dh StreamingDataHandler
     * @throws IOException
     */
    protected void saveUpload(String name, StreamingDataHandler dh) throws IOException {
        System.out.println(name + " upload Start");
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Content-Type: " + dh.getContentType());
        System.out.println("HrefCid: " + dh.getHrefCid());
        System.out.println("Name: " + dh.getName());

        // write to file
        File file = File.createTempFile(name, ".iso");
        FileOutputStream fos = new FileOutputStream(file);

        /**
         * YOU MUST USE readOnce() method.
         * In this case the parseEagerly and memoryThreshold property
         * doesn't used!
         */
        BufferedInputStream is = new BufferedInputStream(dh.readOnce(), 1 * MB);

        /**
         * VERY IMPORTANT! DO NOT USE dh.getInputStream() !!!
         * In that case, read complete file into memory!!!
         * Because it asumptions that, you want to read more than once the
         * file, check parseEagerly and memoryThreshold property.
         */
//          InputStream is = dh.getInputStream();
        // write to other stream (in this case a temp file)
        byte[] buf = new byte[1024];
        long total = 0;
        while (true) {
            int r = is.read(buf);
            if (r == -1) {
                break;
            }
            fos.write(buf, 0, r);
            total += r;

            if (total % (1024 * 1024 * 10) == 0) {
                System.out.println("1. " + (total / MB) + " MB "
                        + (runtime.totalMemory() - runtime.freeMemory()) / MB);
            }
        }

        is.close();
        fos.flush();
        fos.close();
        dh.close();

        System.out.println(name + " upload End\n\n");
    }

}
