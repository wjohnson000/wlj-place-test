package std.wlj.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

public class ClassPathDuplicateMethodsDirectory {

    static RawByteClassLoader classLoader = new RawByteClassLoader();
    static Map<MethodData, List<String>> methodMap = new TreeMap<>();
    static List<String> pathsFound = new ArrayList<>();

    public static void main(String... args) throws FileNotFoundException, IOException {
        String app = "std-ws-place";
        String jarDir  = "C:/apps/apache-tomcat-8.0.35/webapps/" + app + "/WEB-INF/lib";
        File   jarFile = new File(jarDir);

        String[] jarFiles = jarFile.list();
        for (String jFile : jarFiles) {
            processJAR(new File(jarFile, jFile).getAbsolutePath());
        }

        String classDir = "C:/apps/apache-tomcat-8.0.35/webapps/" + app + "/WEB-INF/classes";
        processPath(classDir);

        Set<List<String>> collisions = new HashSet<>();
        List<String> results = new ArrayList<>();
        results.add("===============================================================================================");
        results.add("Methods defined by more than one class-path entry ... abridged list");
        results.add("===============================================================================================");

        List<String> detailResults = new ArrayList<>();
        detailResults.add("");
        detailResults.add("===============================================================================================");
        detailResults.add("Methods defined by more than one class-path entry ... full list");
        detailResults.add("===============================================================================================");

        for (Map.Entry<MethodData,List<String>> entry : methodMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                detailResults.add(entry.getKey().toString());
                entry.getValue().forEach(path -> detailResults.add("    " + path));
                if (! collisions.contains(entry.getValue())) {
                    collisions.add(entry.getValue());
                    results.add(entry.getKey().toString());
                    entry.getValue().forEach(path -> results.add("    " + path));
                    results.add("");
                }
            }
        }
        results.addAll(detailResults);

        Files.write(Paths.get("C:/temp/dup-method-declaration-webapp.txt"), results);
    }

    private static void processPath(String classPath) throws FileNotFoundException, IOException {
        System.out.println(" PATH: " + classPath);
        if (pathsFound.contains(classPath)) {
            System.out.println("Duplicate path: " + classPath);
        }
        pathsFound.add(classPath);

        classLoader = new RawByteClassLoader();

        File[] files = new File(classPath).listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                try (InputStream inStream = new FileInputStream(file)) {
                    byte[] classBytes = IOUtils.toByteArray(inStream);
                    processClassBytes(file.getAbsolutePath(), file.getAbsolutePath(), classBytes);
                }
            } else if (file.isDirectory()) {
                processPath(file.getAbsolutePath());
            }
        }
    }

    private static void processJAR(String classPath) throws IOException {
        System.out.println(" JJAR: " + classPath);
        if (pathsFound.contains(classPath)) {
            System.out.println("Duplicate JAR: " + classPath);
        }
        pathsFound.add(classPath);

        classLoader = new RawByteClassLoader();

        try (JarFile jar = new JarFile(new File(classPath))) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    byte[] classBytes = IOUtils.toByteArray(jar.getInputStream(jarEntry));
                    processClassBytes(classPath, jarEntry.getName(), classBytes);
                }
            }
        }
    }

    private static void processClassBytes(String absolutePath, String nameSource, byte[] classBytes) {
        String name = nameSource;
        int ndx = name.indexOf("classes");
        if (ndx > 0) {
           name = name.substring(ndx+8);
        }
        name = name.replace('/', '.').replace('\\', '.').replaceAll(".class", "");
        try {
            Class<?> clazz = classLoader.createFrom(name, classBytes);
            processRawClass(absolutePath, clazz);
        } catch(Throwable ex) {
//            System.out.println("    >>> Unable to load class for: " + nameSource + " --> " + ex.getMessage());
        }
    }

    private static void processRawClass(String absolutePath, Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            MethodData methodData = new MethodData();
            methodData.className = clazz.getName();
            methodData.methodName = method.getName();
            methodData.parameters = prettify(method.getParameterTypes());

            List<String> usages = methodMap.get(methodData);
            if (usages == null) {
                usages = new ArrayList<>();
                methodMap.put(methodData, usages);
            }
            if (! usages.contains(absolutePath)) {
                usages.add(absolutePath);
            }
        }
    }

    private static String prettify(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
            .map(cc -> cc.getSimpleName())
            .collect(Collectors.joining(","));
    }
}
