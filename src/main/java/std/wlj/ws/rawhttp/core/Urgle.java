package std.wlj.ws.rawhttp.core;

import java.io.IOException;
import java.nio.file.*;


public class Urgle {
    public static void main(String... args) {
        FileSystem currFS = FileSystems.getDefault();
        System.out.println("FS: " + currFS);
        System.out.println("Sepr: " + currFS.getSeparator());
        System.out.println("Attr: " + currFS.supportedFileAttributeViews());
        System.out.println("Open: " + currFS.isOpen());
        System.out.println("IsRO: " + currFS.isReadOnly());
        System.out.println("Prov: " + currFS.provider());

        for (Path path : currFS.getRootDirectories()) {
            System.out.println("Root: " + path);
        }

        Path temp = currFS.getPath("C:", "temp");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(temp)) {
            for (Path path : ds) {
                System.out.println(path.getFileName() + " --> " + Files.probeContentType(path));
                System.out.println("   ACL: " + Files.readAttributes(path, "acl:*", LinkOption.NOFOLLOW_LINKS));
                System.out.println("   BSC: " + Files.readAttributes(path, "basic:*", LinkOption.NOFOLLOW_LINKS));
                System.out.println("   USR: " + Files.readAttributes(path, "user:*", LinkOption.NOFOLLOW_LINKS));
                System.out.println("   OWN: " + Files.readAttributes(path, "owner:*", LinkOption.NOFOLLOW_LINKS));
                System.out.println("   DOS: " + Files.readAttributes(path, "dos:*", LinkOption.NOFOLLOW_LINKS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }


}
