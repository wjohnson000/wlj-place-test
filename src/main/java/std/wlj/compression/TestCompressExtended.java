package std.wlj.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public class TestCompressExtended {

    public static void main(String...args) throws Exception {
        System.out.println("\n\n\n==============================================================");
        testJSON();

        System.out.println("\n\n\n==============================================================");
        testJSONDeflatorStream();
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
//        byte[] newBytes = gzip.readAllBytes();
        while ((nread = gzip.read(buffer, 0, buffer.length)) != -1) {
            baosx.write(buffer, 0, nread);
        }
        String newJSON   = new String(baosx.toByteArray());
        
        System.out.println();
        System.out.println("NEW.json=" + newJSON.length());
        System.out.println("NEW.byt=" + buffer.length);

        System.out.println("\n\n" + newJSON);
    }

    static void testJSONDeflatorStream() throws Exception {
        String json       = Util.readInterpJSON();
        byte[] jsonBytes  = json.getBytes();
        System.out.println("LEN.json=" + json.length());
        System.out.println("LEN.byt=" + jsonBytes.length);

        // Write the bytes to a deflater stream ...
        ByteArrayOutputStream baos = new ByteArrayOutputStream(json.length());
        DeflaterOutputStream  defl = new DeflaterOutputStream(baos);
        defl.write(json.getBytes("UTF-8"));
        defl.flush();
        defl.close();
        baos.close();

        byte[] zipBytes = baos.toByteArray();
        System.out.println();
        System.out.println("ZIP.byt=" + zipBytes.length);

        // Read the bytes from an inflater stream
        int nread;
        byte[] buffer = new byte[4096];
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
        InflaterInputStream  infl = new InflaterInputStream(bais);

        ByteArrayOutputStream baosx = new ByteArrayOutputStream();
        while ((nread = infl.read(buffer)) > 0) {
            baosx.write(buffer, 0, nread);
        }
        infl.close();
        baosx.close();
        String newJSON = new String(baosx.toByteArray());

        System.out.println();
        System.out.println("NEW.json=" + newJSON.length());
        System.out.println("NEW.byt=" + buffer.length);

        System.out.println("\n\n" + newJSON);
    }
}
