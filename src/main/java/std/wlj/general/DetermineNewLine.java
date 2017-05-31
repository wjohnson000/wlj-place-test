package std.wlj.general;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

public class DetermineNewLine {

    static class  NewLineCount {
        int rnCnt = 0;
        int nrCnt = 0;
        int rCnt  = 0;
        int nCnt  = 0;

        public boolean windowsStyle() {
            return (rnCnt > 0  ||  nrCnt > 0);
        }

        public boolean unixStyle() {
            return (rCnt > 0  ||  nCnt > 0);
        }

        public String toString() {
            return rnCnt + " | " + nrCnt + " | " + rCnt + " | " + nCnt;
        }
    }

    // List of directories to skip
    private static final Set<String> IGNORE_DIR = new TreeSet<String>();
    static {
        IGNORE_DIR.add(".settings");
        IGNORE_DIR.add("target");
        IGNORE_DIR.add("test-output");
        IGNORE_DIR.add("docs");
    }

    // List of all valid extensions to compare ...
    private static final Set<String> OK_EXT = new TreeSet<String>();
    static {
        OK_EXT.add(".bat");
        OK_EXT.add(".cfg");
        OK_EXT.add(".classpath");
        OK_EXT.add(".config");
        OK_EXT.add(".css");
        OK_EXT.add(".csv");
        OK_EXT.add(".data");
        OK_EXT.add(".dtd");
        OK_EXT.add(".git");
        OK_EXT.add(".gitignore");
        OK_EXT.add(".htm");
        OK_EXT.add(".html");
        OK_EXT.add(".java");
        OK_EXT.add(".js");
        OK_EXT.add(".jsp");
        OK_EXT.add(".kml");
        OK_EXT.add(".project");
        OK_EXT.add(".properties");
        OK_EXT.add(".sh");
        OK_EXT.add(".sql");
        OK_EXT.add(".txt");
        OK_EXT.add(".xml");
        OK_EXT.add(".xsd");
        OK_EXT.add(".xsl");
        OK_EXT.add(".xlsx");
        OK_EXT.add(".yml");
    }

    private static final Set<String> IGNORE_EXT = new TreeSet<String>();

    private boolean doVerbose = false;


    // Get an extension for a file, verifying that the file:
    //   -- it is not null
    //   -- it is not a directory
    //   -- it has an "acceptable" file extension
    // If any of these conditions are violated, return a "null" value
    private String getExt(File aFile) {
        if (aFile == null  ||  aFile.isDirectory()) {
            return null;
        }
        int ndx = aFile.getName().lastIndexOf(".");
        if (ndx < 0) {
            return null;
        } else {
            String ext = aFile.getName().substring(ndx).toLowerCase();
            if (OK_EXT.contains(ext)) {
                return ext;
            } else {
                IGNORE_EXT.add(ext);
                return null;
            }
        }
    }

    // Determine if a directory is OK to compare ...
    private boolean isDirOK(File aFile) {
        if (aFile == null) {                            // ensure we have a non-null file
            return false;
        } else if (! aFile.exists()) {                  // ensure we have a file that exists
            return false;
        } else if (! aFile.isDirectory()) {             // ensure we have a directory reference
            return false;
        } else if (aFile.getName().startsWith(".")) {   // don't check "hidden" directories
            return false;
        } else if (aFile.getName().equals("target")) {  // DSpace-specific (MAVEN) directory
            return false;
        } else {
            return true;
        }
    }

    /**
     * For each qualifying file, number the various type of line-ends in each file,
     * recursing down the directory structure.
     * 
     * @param aDir
     */
    private void countDir(File aDir) {
        if (aDir == null) {
            return;
        }

        for (String ignoreDir : IGNORE_DIR) {
            if (aDir.getAbsolutePath().contains(ignoreDir)) {
                System.out.println("Ignore >>" + aDir);
                return;
            }
        }

        if (isDirOK(aDir)) {
            if (doVerbose) System.out.println("Check >>" + aDir.getAbsolutePath() + "<<");

            // First pass run the regular files
            for (String fName : aDir.list()) {
                File fileOne  = new File(aDir, fName);
                if (! fileOne.isDirectory()  &&  getExt(fileOne) != null) {
                    NewLineCount counts = examineContents(fileOne);
                    if (doVerbose  ||  counts.windowsStyle()) {
                        System.out.println(fileOne.getAbsolutePath() + ":  " + counts);
                    }
                }
            }

            // First pass recurse through the directories
            for (String fName : aDir.list()) {
                File fileOne  = new File(aDir, fName);
                if (fileOne.isDirectory()) {
                    countDir(fileOne);
                }
            }
        }
    }

    private NewLineCount examineContents(File aFile) {
        String contents = readContents(aFile);
        NewLineCount nlCount = new NewLineCount();

        byte[] bytes = contents.getBytes();
        int ndx0 = 0;
        while (ndx0 < bytes.length) {
            if (bytes[ndx0] == '\r') {
                ndx0++;
                if (ndx0 < bytes.length  &&  bytes[ndx0] == '\n') {
                    nlCount.rnCnt++;
                } else {
                    nlCount.rCnt++;
                }
            } else if (bytes[ndx0] == '\n') {
                ndx0++;
                if (ndx0 < bytes.length  &&  bytes[ndx0] == '\r') {
                    nlCount.nrCnt++;
                } else {
                    nlCount.nCnt++;
                }
            }
            ndx0++;
        }

        return nlCount;
    }

    // Read the contents of a file ... hopefully a TEXT file
    private String readContents(File aFile) {
        try {
            return new String(Files.readAllBytes(Paths.get(aFile.getAbsolutePath())));
        } catch (Exception e) {
            return "";
        }
    }

    // Get this silly thing a-goin'
    public static void main(String[] args) {
//        String fileOne = "C:/Users/wjohnson000/git/std-ws-place-55";
        String fileOne = "C:/Users/wjohnson000/git";
        DetermineNewLine dcEngine = new DetermineNewLine();
        dcEngine.doVerbose  = false;
        dcEngine.countDir(new File(fileOne));

        System.out.println("\n----------------------------------------------------");
        System.out.println("Ignored Extensions ...");
        for (String ext : IGNORE_EXT) {
            System.out.println("  " + ext);
        }
        System.exit(0);
    }
}
