/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

/**
 * @author wjohnson000
 *
 */
public class CopyDirectory {
    public static void main(String...args) throws Exception {
        copyDirs();
//        copyFiles();
    }

    public static void copyFiles() throws Exception {
        String solrHome = "C:/temp/solr-home/places/data/index";
        String solrIndexSource = "C:/D-drive/solr/backup";

        File fromDir   = new File(solrIndexSource);
        File toDir = new File(solrHome);
        FileUtils.copyDirectory(fromDir, toDir, true);
    }
    public static void copyDirs() {
//        String solrHome = System.getProperty(PROP_SOLR_HOME);
//        String solrIndexSource = System.getProperty("solr.index.source");
        String solrHome = "C:/temp/solr-home";
        String solrIndexSource = "C:/D-drive/solr/backup";
//        logger.info(null, MODULE_NAME, "Solr index copy?",
//                "solrHome", solrHome,
//                "solrIndexSource", solrIndexSource);

        if (solrIndexSource != null  &&  ! solrIndexSource.trim().isEmpty()) {
            Path fromPath = Paths.get(solrIndexSource);
            Path toPath = Paths.get(solrHome, "places", "data", "index");
            try (Stream<Path> stream = Files.walk(fromPath)) {
                stream.forEachOrdered(srcPath -> {
                    Path toFile = toPath.resolve(fromPath.relativize(srcPath));
                    try {
                        Files.copy(srcPath, toFile);
                    } catch(Exception ex) {
                        System.out.println("OOPS!! " + ex.getMessage());
                    }
                });
            } catch(IOException ex) {
                System.out.println("OOPS!!");
            }
        }

    }
}
