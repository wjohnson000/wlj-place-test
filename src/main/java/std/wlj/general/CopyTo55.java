package std.wlj.general;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Copy source files from the "{regular} to the "{regular}-55" directories.  Affected projects are:
 * <ul>
 *   <li><strong>std-lib-place-55</strong>, from std-lib-place</li>
 *   <li><strong>std-ws-dbload-55</strong>, from std-ws-place-db</li>
 *   <li><strong>std-ws-place-55</strong>, from std-ws-place</li>
 *   <li><strong>std-ws-solr-55</strong>, from std-ws-solr</li>
 *   <li><strong>std-ws-solr-repeater-55</strong>, from std-ws-solr-repeater</li>
 * </ul> 
 * @author wjohnson000
 *
 */
public class CopyTo55 {

    private static final String BASE_DIR = "C:/Users/wjohnson000/git";

    private static Map<String,String> projectMap = new TreeMap<>();
    static {
        projectMap.put("std-lib-place", "std-lib-place-55");
        projectMap.put("std-ws-place", "std-ws-place-55");
        projectMap.put("std-ws-solr", "std-ws-solr-55");
        projectMap.put("std-ws-solr-repeater", "std-ws-solr-repeater-55");
        projectMap.put("std-ws-place-db", "std-ws-dbload-55");
    }

    private static Map<String,String> moduleMap = new TreeMap<>();
    static {
        moduleMap.put("place-db-model", "dbload-model-55");
        moduleMap.put("place-db-service", "dbload-service-55");
        moduleMap.put("place-db-webapp", "dbload-webapp-55");
    }

    public static void main(String...args) {
        processProject("std-lib-place");
//        processProject("std-ws-place");
//        processProject("std-ws-solr");
//        processProject("std-ws-solr-repeater");
//        processProject("std-ws-place-db");
    }

    static void processProject(String project) {
        File fileReg = new File(BASE_DIR, project);
        File file55  = new File(BASE_DIR, projectMap.get(project));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("PRJ: " + project);
        System.out.println("REG: " + fileReg);
        System.out.println(" 55: " + file55);

        Set<String> fromFiles = new TreeSet<>(Arrays.asList(fileReg.list()));
        Set<String> toFiles   = new TreeSet<>(Arrays.asList(file55.list()));
        Set<String> extensions = new TreeSet<>();

        for (String fromFile : fromFiles) {
            String toFile = null;
            if (toFiles.contains(fromFile + "-55")) {
                toFile = fromFile + "-55";
            } else if (moduleMap.containsKey(fromFile)) {
                toFile = moduleMap.get(fromFile);
            }
            if (toFile != null) {
                File srcReg = new File(new File(fileReg, fromFile), "src");
                File src55  = new File(new File(file55, toFile), "src");
                handleDirectory(srcReg.getAbsolutePath(), src55.getAbsolutePath(), extensions);
            }
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        extensions.forEach(System.out::println);
    }

    static void handleDirectory(String dirReg, String dir55, Set<String> extensions) {
        System.out.println("=================================================================================================");
        System.out.println("  Reg: " + dirReg);
        System.out.println("   55: " + dir55);

        File dirRegFile = new File(dirReg);
        if (! dirRegFile.exists()) {
            return;
        }

        Set<String> fromFiles = new TreeSet<>(Arrays.asList(new File(dirReg).list()));
        Set<String> toFiles   = new TreeSet<>(Arrays.asList(new File(dir55).list()));

        Set<String> allFiles = new TreeSet<>();
        allFiles.addAll(fromFiles);
        allFiles.addAll(toFiles);

        for (String aFile : allFiles) {
            if (fromFiles.contains(aFile)  &&  toFiles.contains(aFile)) {
                File fromFile = new File(dirReg, aFile);
                File toFile = new File(dir55, aFile);
                if (fromFile.isDirectory()  &&  toFile.isDirectory()) {
                    handleDirectory(fromFile.getAbsolutePath(), toFile.getAbsolutePath(), extensions);
                } else if (! fromFile.isDirectory()  &&  ! toFile.isDirectory()) {
                    if (fromFile.getAbsolutePath().contains("pom.xml")) {
                        System.out.println("Ignoring the 'POM' file ...");
                        continue;
                    } else if (fromFile.getAbsolutePath().contains("app.properties")) {
                        System.out.println("Ignoring the 'app.properties' file ...");
                        continue;
                    }

                    extensions.add(getExtension(fromFile.getName()));
                    String fromContents = getContents(fromFile.getAbsolutePath());
                    String toContents = getContents(toFile.getAbsolutePath());
                    String tFromContents = fromContents.replace('\n', ' ').replace('\r', ' ').replaceAll(" ", "").trim();
                    String tToContents   = toContents.replace('\n', ' ').replace('\r', ' ').replaceAll(" ", "").trim();
                    if (! tFromContents.equals(tToContents)) {
                        createFileContents(toFile, fromContents);
                    }
                } else {
                    System.out.println("  >>> file vs. directory conflict " + fromFile + " <--> " + toFile);
                }
            } else if (fromFiles.contains(aFile)) {
                File fromFile = new File(dirReg, aFile);
                File toFile = new File(dir55, aFile);
                if (fromFile.isDirectory()) {
                    System.out.println("  >>> directory created? " + toFile.mkdir());
                    handleDirectory(fromFile.getAbsolutePath(), toFile.getAbsolutePath(), extensions);
                } else {
                    extensions.add(getExtension(fromFile.getName()));
                    String fromContents = getContents(fromFile.getAbsolutePath());
                    createFileContents(toFile, fromContents);
                }
            } else {
                File toFile = new File(dir55, aFile);
                deleteFileOrDirectory(toFile);
            }
        }
    }

    static void deleteFileOrDirectory(File theFile) {
        if (theFile.isDirectory()) {
            File[] files = theFile.listFiles();
            for (File file : files) {
                deleteFileOrDirectory(file);
            }
        }
        theFile.delete();
    }

    static void createFileContents(File theFile, String theContents) {
        try {
            deleteFileOrDirectory(theFile);
            Files.write(Paths.get(theFile.getAbsolutePath()), theContents.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            System.out.println("  >>> unable to create file: " + theFile + ";  ex=" + ex.getMessage());
        }
    }

    static String getExtension(String fileName) {
        int ndx = fileName.lastIndexOf('.');
        if (ndx <= 0) {
            System.out.println("  >>> no file extension ... " + fileName);
            return "";
        } else {
            return fileName.substring(ndx);
        }
    }

    static String getContents(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName))).replace("\r\n", "\n").replace("\r", "\n");
        } catch (IOException ex) {
            System.out.println("  >>> Can't read file '" + fileName + "' -- ex=" + ex.getMessage());
            return "";
        }
    }
}
