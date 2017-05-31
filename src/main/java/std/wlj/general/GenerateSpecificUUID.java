package std.wlj.general;

import java.nio.ByteBuffer;
import java.util.UUID;

public class GenerateSpecificUUID {
    public static void main(String... args) {
        byte[] uuidBytes = ByteBuffer.allocate(8).putLong(111111).array();
        UUID uuid = UUID.nameUUIDFromBytes(uuidBytes);
        System.out.println("UUID: " + uuid);

        uuid = new UUID(0L, 111111L);
        System.out.println("UUID: " + uuid);

        String uuidStr = String.format("00000000-0000-0000-0000-%012d", 111111L);
        uuid = UUID.fromString(uuidStr);
        System.out.println("UUID: " + uuid);
    }
}
