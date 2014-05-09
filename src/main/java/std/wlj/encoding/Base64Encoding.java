package std.wlj.encoding;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;


public class Base64Encoding {
    public static String encode(byte[] bytes) throws Exception {
        if (bytes == null) {
            return null;
        } else {
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(bytes));
        }
    }

    public static void main(String... arg) throws Exception {
        Path filePath = FileSystems.getDefault().getPath("C:", "tools", "gis-files", "kml-files", "USA_adm0.kmz");
        System.out.println("PATH: " + filePath);
        byte[] lotsOfBytes = Files.readAllBytes(filePath);
        System.out.println("Byte-count: " + lotsOfBytes.length);
        String base64 = encode(lotsOfBytes);
        System.out.println("Byte-count-encoded: " + base64.length());
    }
}
