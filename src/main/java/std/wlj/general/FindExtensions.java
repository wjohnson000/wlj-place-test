package std.wlj.general;

import static java.nio.file.FileVisitResult.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class FindExtensions {
    private static final Map<Path,Set<String>> extensions = new TreeMap<>();

    public static void main(String... args) throws IOException {
        final Path gitPath = Paths.get("C:/Users/wjohnson000/git");

        Files.walkFileTree(gitPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.getParent().equals(gitPath)) {
                    extensions.put(dir, new TreeSet<String>());
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileExt = getFileExtension(file);
                Path   parPath = getParentPath(file);
                if (fileExt != null  &&  parPath != null  &&  includeFile(file)) {
                    extensions.get(parPath).add(fileExt);
                }
                return CONTINUE;
            }
        });

        for (Map.Entry<Path, Set<String>> entry : extensions.entrySet()) {
            System.out.println("======================================================================================");
            System.out.println("Path: " + entry.getKey());
            for (String fileExt : entry.getValue()) {
                System.out.println("    : " + fileExt);
            }
        }
    }

    private static boolean includeFile(Path file) {
        String absName = file.toString();
        if (absName.contains("target")) {
            return false;
        } else if (absName.contains(".git")) {
            return false;
        }
        return true;
    }

    private static String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int ndx = fileName.lastIndexOf('.');
        return (ndx <= 0) ? null :fileName.substring(ndx+1);
    }

    private static Path getParentPath(Path file) {
        Path tPath = file.getParent();
        while (tPath != null) {
            if (extensions.containsKey(tPath)) {
                break;
            }
            tPath = tPath.getParent();
        }
        return tPath;
    }
}
