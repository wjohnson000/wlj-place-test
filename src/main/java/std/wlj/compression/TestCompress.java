package std.wlj.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

public class TestCompress {

    public static void main(String...args) throws Exception {
//        testXML();

        System.out.println("\n\n\n==============================================================");
        testJSON();

//        System.out.println("\n\n\n==============================================================");
//        testJSON2();
    }

    /**
     * NOTE: the "GZIPInputStream.readAllBytes()" method is in Java 9!
     * @throws Exception
     */
    static void testXML() throws Exception {
//        String xml       = Util.readInterpXML();
//        byte[] xmlBytes  = xml.getBytes();
//        System.out.println("LEN.xml=" + xml.length());
//        System.out.println("LEN.byt=" + xmlBytes.length);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(xml.length());
//        GZIPOutputStream gzos = new GZIPOutputStream(baos);
//        gzos.write(xmlBytes, 0, xmlBytes.length);
//        gzos.finish();
//        gzos.flush();
//
//        String zipXML   = baos.toString();
//        byte[] zipBytes = baos.toByteArray();
//        System.out.println();
//        System.out.println("ZIP.xml=" + zipXML.length());
//        System.out.println("ZIP.byt=" + zipBytes.length);
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
//        GZIPInputStream gzip = new GZIPInputStream(bais);
//        byte[] newBytes = gzip.readAllBytes();
//        String newXML   = new String(newBytes);
//
//        System.out.println();
//        System.out.println("NEW.xml=" + newXML.length());
//        System.out.println("NEW.byt=" + newBytes.length);
//
//        System.out.println("\n\n" + newXML);
    }

    static void testJSON() throws Exception {
        String json       = Util.readInterpJSON();
        byte[] jsonBytes  = json.getBytes();
        System.out.println("LEN.json=" + json.length());
        System.out.println("LEN.byt=" + jsonBytes.length);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(json.length());
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(jsonBytes, 0, jsonBytes.length);
        }

        String zipJSON   = baos.toString();
        byte[] zipBytes = baos.toByteArray();
        System.out.println();
        System.out.println("ZIP.json=" + zipJSON.length());
        System.out.println("ZIP.byt=" + zipBytes.length);

        int nread;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream baosx = new ByteArrayOutputStream();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
        GZIPInputStream gzip = new GZIPInputStream(bais);
        while ((nread = gzip.read(buffer, 0, buffer.length)) != -1) {
            baosx.write(buffer, 0, nread);
        }
        String newJSON   = new String(baosx.toByteArray());
        
        System.out.println();
        System.out.println("NEW.json=" + newJSON.length());
        System.out.println("NEW.byt=" + buffer.length);

        System.out.println("\n\n" + newJSON);
    }

    static void testJSON2() throws Exception {
//        String json       = Util.jackson2Json();
//        byte[] jsonBytes  = json.getBytes();
//        System.out.println("LEN.json=" + json.length());
//        System.out.println("LEN.byt=" + jsonBytes.length);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(json.length());
//        GZIPOutputStream gzos = new GZIPOutputStream(baos);
//        gzos.write(jsonBytes, 0, jsonBytes.length);
//        gzos.finish();
//        gzos.flush();
//
//        String zipJSON   = baos.toString();
//        byte[] zipBytes = baos.toByteArray();
//        System.out.println();
//        System.out.println("ZIP.json=" + zipJSON.length());
//        System.out.println("ZIP.byt=" + zipBytes.length);
//        System.out.println(zipJSON);
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
//        GZIPInputStream gzip = new GZIPInputStream(bais);
//        byte[] newBytes = gzip.readAllBytes();
//        String newJSON   = new String(newBytes);
//
//        System.out.println();
//        System.out.println("NEW.json=" + newJSON.length());
//        System.out.println("NEW.byt=" + newBytes.length);
//
//        System.out.println("\n\n" + newJSON);
    }
}
