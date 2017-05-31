package std.wlj.solrload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CompareSolrLoadOldVsNew {

    private static final String OLD_PATH = "D:/tmp/solr-docs/dev-old";
    private static final String NEW_PATH = "D:/tmp/solr-docs/dev-new";

    static class FileInfo {
        String docId;
        int    repId;
        int    version;
        File   file;
    }

    public static void main(String... args) throws IOException {
        compareRepFiles();
        compareNonRepFiles();
    }

    static void compareRepFiles() throws IOException {
        Map<Integer,FileInfo> oldFiles = getLatestFilesForRep(OLD_PATH);
        Map<Integer,FileInfo> newFiles = getLatestFilesForRep(NEW_PATH);

        Set<Integer> repIds = new TreeSet<>();
        repIds.addAll(oldFiles.keySet());
        repIds.addAll(newFiles.keySet());

        for (Integer repId : repIds) {
            compareFiles(String.valueOf(repId), oldFiles.get(repId), newFiles.get(repId));
        }
    }

    static void compareNonRepFiles() throws IOException {
        Map<String,FileInfo> oldFiles = getLatestFilesForNoRep(OLD_PATH);
        Map<String,FileInfo> newFiles = getLatestFilesForNoRep(NEW_PATH);

        Set<String> docIds = new TreeSet<>();
        docIds.addAll(oldFiles.keySet());
        docIds.addAll(newFiles.keySet());

        for (String docId : docIds) {
            compareFiles(docId, oldFiles.get(docId), newFiles.get(docId));
        }
    }

    static Map<Integer,FileInfo> getLatestFilesForRep(String path) {
        Map<Integer,FileInfo> results = new TreeMap<>();

        File pathDir = new File(path);
        File[] allFiles = pathDir.listFiles();
        for (File aFile : allFiles) {
            FileInfo fileInfo = getFileInfo(aFile);
            if (fileInfo.repId > 0) {
                FileInfo currFileInfo = results.get(fileInfo.repId);
                if (currFileInfo == null) {
                    results.put(fileInfo.repId, fileInfo);
                } else if (fileInfo.version > currFileInfo.version) {
                    results.put(fileInfo.repId, fileInfo);
                }
            }
        }

        return results;
    }

    static Map<String,FileInfo> getLatestFilesForNoRep(String path) {
        Map<String,FileInfo> results = new TreeMap<>();

        File pathDir = new File(path);
        File[] allFiles = pathDir.listFiles();
        for (File aFile : allFiles) {
            FileInfo fileInfo = getFileInfo(aFile);
            if (fileInfo.repId < 0) {
                results.put(fileInfo.docId, fileInfo);
            }
        }

        return results;
    }

    static FileInfo getFileInfo(File aFile) {
        FileInfo fileInfo = null;

        String name = aFile.getName();
        int ndx01 = name.indexOf('-');
        int ndx02 = name.indexOf('.');

        if (ndx02 > 0) {
            String sDocId   = name.substring(0, ndx02);
            fileInfo = new FileInfo();
            fileInfo.docId = sDocId;
            fileInfo.file = aFile;
            fileInfo.repId = -1;
            fileInfo.version = 1;

            char char01 = name.charAt(0);
            if (char01 >= '0'  &&  char01 <= '9') {
                String sRepId   = name.substring(0, ndx01);
                String sVersion = name.substring(ndx01+1, ndx02);
                fileInfo.repId = Integer.parseInt(sRepId);
                fileInfo.version = Integer.parseInt(sVersion);
            }
        } else {
            System.out.println("Bad filename: " + name);
        }

        return fileInfo;
    }
    
    private static void compareFiles(String id, FileInfo fileInfo01, FileInfo fileInfo02) throws IOException {
        if (fileInfo01 == null) {
            System.out.println(id + " --> no 'old' file present");
            return;
        } else if (fileInfo02 == null) {
            System.out.println(id + " --> no 'new' file present");
            return;
        }

        String xml01 = new String(Files.readAllBytes(Paths.get(fileInfo01.file.getAbsolutePath())));
        String xml02 = new String(Files.readAllBytes(Paths.get(fileInfo02.file.getAbsolutePath())));
        if (fileInfo01.version != fileInfo02.version) {
            System.out.println(id + " --> Version issues ... " + fileInfo01.version + " vs. " + fileInfo02.version);
        }
        if (! xml01.equals(xml02)) {
            xml01 = removeUUID(xml01);
            xml02 = removeUUID(xml02);
            if (xml01.equals(xml02)) {
                System.out.println(id + " --> UUID ...");
            } else {
                System.out.println(id + " --> Different ... Sigh ...");
            }
        }
    }

    private static String removeUUID(String someText) {
        String tText = someText;
        int ndx = tText.indexOf("uuid: ");
        if (ndx > 0) {
            String before = someText.substring(0, ndx+6);
            String after  = someText.substring(ndx+42);
            tText = before + " [UUID]" + after;
        }
        return tText;
    }
}
