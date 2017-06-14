package std.wlj.general;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class MavenRepoRepo {

    private static String M2_BASE_DIR = "C:/Users/wjohnson000/.m2/repository";

    public static void main(String... args) {
        File[] files = new File(M2_BASE_DIR).listFiles();
        Arrays.stream(files).forEach(aDir -> processDirectory(aDir));
    }

    static void processDirectory(File aDir) {
        if (aDir.getName().startsWith(".")) {
            return;
        } else if (! aDir.isDirectory()) {
            return;
        }

        File[] files = aDir.listFiles();
        if (isVersionLevel(files)) {
            System.out.println("=======================================================================================================");
            System.out.println("Dir: " + aDir);
            List<File> delFiles = versionsToDelete(files);
            delFiles.forEach(xDir -> deleteDirectory(xDir));
        } else {
            Arrays.stream(files).forEach(aFile -> processDirectory(aFile));
        }
    }

    /**
     * Determine if at least three of the files in the sub-directory are version numbers.
     * 
     * @param files list of files
     * @return
     */
    static boolean isVersionLevel(File[] files) {
        long count = Arrays.stream(files)
            .map(file -> file.getName())
            .filter(name -> ! name.contains("SNAPSHOT"))
            .filter(name -> name.charAt(0) >= '0' && name.charAt(0) <= '9')
            .count();
        return count > 1;
    }

    /**
     * Return the list of file sto
     * @param files
     * @return
     */
    static List<File> versionsToDelete(File[] files) {
        Map<String,File> kvMap = Arrays.stream(files)
            .filter(file -> file.getName().charAt(0) >= '0' && file.getName().charAt(0) <= '9')
            .collect(Collectors.toMap(
                file -> flattenVersion(file),
                Function.identity(),
                (str1, str2) -> str1,
                TreeMap::new));

        if (kvMap.size() < 4) {
            return new ArrayList<>();
        } else {
            return kvMap.values().stream()
                .limit(kvMap.size()-3)
                .collect(Collectors.toList());
        }
    }

    static String flattenVersion(File file) {
        String[] chunks = PlaceHelper.split(file.getName(), '.');
        return Arrays.stream(chunks)
             .map(name -> "000000" + name)
             .map(name -> name.substring(name.length()-6))
             .collect(Collectors.joining("."));
    }

    static void deleteDirectory(File aDir) {
        System.out.println(">>  Delete directory: " + aDir);
//        File[] files = aDir.listFiles();
//        for (File file : files) {
//            if (file.isDirectory()) {
//                deleteDirectory(file);
//            } else {
//                file.delete();
//            }
//        }
//        aDir.delete();
    }
}
