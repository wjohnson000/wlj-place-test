package std.wlj.general;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;


public class DirectoryCompare {

    // List of directories to skip
    private static final Set<String> IGNORE_DIR = new TreeSet<String>();
    static {
        IGNORE_DIR.add(".settings");
        IGNORE_DIR.add("target");
        IGNORE_DIR.add("test-output");
//        IGNORE_DIR.add("docs");
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
        OK_EXT.add(".json");
        OK_EXT.add(".jsp");
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
        OK_EXT.add(".iml");
        OK_EXT.add(".ipr");
        OK_EXT.add(".iws");

    }

    private static final Set<String> IGNORE_EXT = new TreeSet<String>();

    private String ignorePath = null;
    private boolean doVerbose = false;
    private boolean ignoreWhiteSpace = false;

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
//      String ext = (ndx < 0) ? "--ignore--" : aFile.getName().substring(ndx).toLowerCase();
//      return OK_EXT.contains(ext) ? ext : null;
    }

    // Determine if a directory is OK to compare ...
    private boolean isDirOK(File aFile) {
        if (aFile == null) {                            // ensure we have a non-null file
            return false;
        } else if (! aFile.exists()) {                  // ensure we have a file that exists
            return false;
        } else if (! aFile.isDirectory()) {             // ensure we have a directory reference
            return false;
//      } else if (aFile.getName().startsWith(".")) {   // don't check "hidden" directories
//          return false;
        } else if (aFile.getName().contains("svn")) {   // don't check any svn directory
            return false;
        } else if (aFile.getName().equals("target")) {  // DSpace-specific (MAVEN) directory
            return false;
        }
        
        return true;
    }

    // Compare two directories ...
    //   -- verify that both arguments are not null
    //   -- verify that both non-null arguments are directories
    private void compareDirs(File dirOne, File dirTwo) {
        if (dirOne == null  ||  dirTwo == null) {
            return;
        }

        for (String ignoreDir : IGNORE_DIR) {
            if (dirOne.getAbsolutePath().contains(ignoreDir)) {
                System.out.println("Ignore >>" + dirOne);
                return;
            }
        }

        if (isDirOK(dirOne)  ||  isDirOK(dirTwo)) {
            boolean printHead = ! doVerbose;
            boolean useDirOne = (dirOne != null  &&  dirOne.isDirectory());
            boolean useDirTwo = (dirTwo != null  &&  dirTwo.isDirectory());
            String dirOnePath = useDirOne ? dirOne.getAbsolutePath() : "<null>";
            String dirTwoPath = useDirTwo ? dirTwo.getAbsolutePath() : "<null>";
            if (doVerbose) System.out.println("Compare >>" + dirOnePath + "<<  with  >>" + dirTwoPath + "<<");

            TreeSet<String> fileSet = new TreeSet<String>();
            if (useDirOne) {
                String[] fileNames = dirOne.list();
                fileSet.addAll(Arrays.asList(scrubDirName(fileNames)));
            }
            if (useDirTwo) {
                String[] fileNames = dirTwo.list();
                fileSet.addAll(Arrays.asList(scrubDirName(fileNames)));
            }

            // First pass ... run the files
            for (String fName : fileSet) {
                File fileOne  = useDirOne ? new File(dirOne, fName) : null;
                File fileTwo  = useDirTwo ? new File(dirTwo, fName) : null;
                String extOne = getExt(fileOne);
                String extTwo = getExt(fileTwo);

                if (fileOne != null  &&  fileTwo != null  &&  fileOne.exists()  &&  fileTwo.exists()) {
                    if (! fileOne.isDirectory()  &&  ! fileTwo.isDirectory()) {
                        if (extOne == null) {
                            if (doVerbose) System.out.println("   " + fName + " ... skipping");
                        } else if (compareFiles(fileOne, fileTwo)) {
                            if (doVerbose) System.out.println("   " + fName + " ... same");
                        } else {
                            if (printHead) System.out.println("Compare >>" + dirOnePath + "<<  with  >>" + dirTwoPath + "<<");
                            printHead = false;
                            System.out.println("   " + fName + " ... different");
                        }
                    }
                } else if (fileOne != null  &&  fileOne.exists()  &&  ! fileOne.isDirectory()) {
                    if (extOne == null) {
                        if (doVerbose) System.out.println("   " + fName + " ... only in " + dirOne.getAbsolutePath());
                    } else {
                        if (printHead) System.out.println("Compare >>" + dirOnePath + "<<  with  >>" + dirTwoPath + "<<");
                        printHead = false;
                        System.out.println("   " + fName + " ... only in " + dirOne.getAbsolutePath());                     
                    }
                } else if (fileTwo != null  &&  fileTwo.exists()  &&  ! fileTwo.isDirectory()) {
                    if (extTwo == null) {
                        if (doVerbose) System.out.println("   " + fName + " ... only in " + dirTwo.getAbsolutePath());
                    } else {
                        if (printHead) System.out.println("Compare >>" + dirOnePath + "<<  with  >>" + dirTwoPath + "<<");
                        printHead = false;
                        System.out.println("   " + fName + " ... only in " + dirTwo.getAbsolutePath());                     
                    }
                }
            }

            // Second pass ... run the directories
            for (String fName : fileSet) {
                File fileOne = null;
                File fileTwo = null;
                if (useDirOne) {
                    fileOne = new File(dirOne, fName);
                    if (! fileOne.exists()) {
                        fileOne = new File(dirOne, fName+ignorePath);
                    }
                }
                if (useDirTwo) {
                    fileTwo = new File(dirTwo, fName);
                    if (! fileTwo.exists()) {
                        fileTwo = new File(dirTwo, fName+ignorePath);
                    }
                }
                compareDirs(fileOne, fileTwo);
            }
        }
    }

    // Scrub file names, removing the "ignorePath" from names
    private String[] scrubDirName(String[] fileNames) {
        if (ignorePath == null  ||  fileNames.length == 0) {
            return fileNames;
        } else {
            String[] newFileNames = new String[fileNames.length];
            for (int i=0;  i<fileNames.length;  i++) {
                String fileName = fileNames[i];
                if (fileName.endsWith(ignorePath)) {
                    fileName = fileName.substring(0, fileName.length() - ignorePath.length());
                }
                newFileNames[i] = fileName;
            }
            return newFileNames;
        }
    }

    // Compare two files ...
    //   -- verify that neither argument is null
    //   -- verify that neither argument is a directory
    //   -- verify that both files have "acceptable" file extensions
    private boolean compareFiles(File fileOne, File fileTwo) {
        String extOne = getExt(fileOne);
        String extTwo = getExt(fileTwo);

        if (extOne != null  &&  extTwo != null) {
            String contentsOne = readContents(fileOne);
            String contentsTwo = readContents(fileTwo);
            contentsOne = (contentsOne == null) ? null : contentsOne.replace("\r\n", "\n");
            contentsTwo = (contentsTwo == null) ? null : contentsTwo.replace("\r\n", "\n");
            if (ignoreWhiteSpace) {
                contentsOne = contentsOne.replace('\n', ' ');
                contentsOne = contentsOne.replace('\r', ' ');
                contentsOne = contentsOne.replace((char)160, ' ');
                contentsOne = contentsOne.replaceAll(" ", "");

                contentsTwo = contentsTwo.replace('\n', ' ');
                contentsTwo = contentsTwo.replace('\r', ' ');
                contentsTwo = contentsTwo.replace((char)160, ' ');
                contentsTwo = contentsTwo.replaceAll(" ", "");
            }
            return contentsOne != null  &&  contentsOne.equals(contentsTwo);
        } else {
            return false;
        }
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
        String fileOne = "C:/Users/wjohnson000/git/std-ws-dbload-55/dbload-webapp-55";
        String fileTwo = "C:/Users/wjohnson000/git/std-ws-place-db/place-db-webapp";
        DirectoryCompare dcEngine = new DirectoryCompare();
        dcEngine.doVerbose  = false;
        dcEngine.ignoreWhiteSpace = true;
        dcEngine.compareDirs(new File(fileTwo), new File(fileOne));

        System.out.println("\n----------------------------------------------------");
        System.out.println("Ignored Extensions ...");
        for (String ext : IGNORE_EXT) {
            System.out.println("  " + ext);
        }
        System.exit(0);
    }
}
