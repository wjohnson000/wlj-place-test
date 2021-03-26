/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.familysearch.homelands.admin.parser.helper.ExcelUtility;
import org.familysearch.homelands.admin.parser.model.ItemModel;
import org.familysearch.homelands.core.persistence.model.CategoryType;
import org.familysearch.homelands.core.persistence.model.SubcategoryType;

/**
 * @author wjohnson000
 *
 */
public class TimelineDecadesAnalyzeCategory {

    private static final String BASE_DIR = "C:/D-drive/homelands/Decades-Project";

    private static final Set<String> decadesData = new TreeSet<>();

    public static void main(String... args) {
        getCategorySubcategoryCore();
        getCategorySubcategoryAdmin();
        getCategorySubcategoryDecades();
    }

    static void getCategorySubcategoryCore() {
        System.out.println("\nData from 'homelands-core' project:");

        Map<CategoryType, List<SubcategoryType>> catSubCat = new LinkedHashMap<>();
        Arrays.stream(CategoryType.values())
              .forEach(ct -> catSubCat.put(ct, new ArrayList<>()));

        Arrays.stream(SubcategoryType.values())
              .forEach(st -> catSubCat.get(st.getCategoryType()).add(st));

        for (Map.Entry<CategoryType, List<SubcategoryType>> entry : catSubCat.entrySet()) {
            System.out.println();

            if (entry.getValue().isEmpty()) {
                System.out.println(entry.getKey() + "|");
            } else {
                boolean first = true;
                for (SubcategoryType sc : entry.getValue()) {
                    if (first) {
                        System.out.println(entry.getKey() + "|" + sc);
                    } else {
                        System.out.println("|" + sc);
                    }
                }
            }
        }
    }

    static void getCategorySubcategoryAdmin() {
        System.out.println("\nData from 'homelands-admin' project:");

        System.out.println("Categories:");
        Arrays.stream(ItemModel.CategoryType.values())
               .forEach(System.out::println);

        System.out.println("\nSubcategories:");
        Arrays.stream(ItemModel.SubcategoryType.values())
              .forEach(System.out::println);
    }

    static void getCategorySubcategoryDecades() {
        try {
            for (Path path : Files.newDirectoryStream(Paths.get(BASE_DIR), 
                        path -> path.toFile().isFile())) {
                if (path.getFileName().toString().endsWith(".xlsx")) {
                    byte[] contents = Files.readAllBytes(path);
                    System.out.println(path);
                    Map<Integer, List<List<String>>> rowData = ExcelUtility.loadExcelData(contents, ".xlsx");
                    rowData.values().forEach(rd -> processDecade(rd));
                }
            }
        } catch (Exception ex) {
            System.out.println("Oops -- " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("\nDecades:");
        decadesData.forEach(System.out::println);
    }

    static void processDecade(List<List<String>> rows) {
        int catCol  = -1;
//        int scatCol = -1;
        for (List<String> row : rows) {
            if (catCol == -1) {
                for (int ndx=0;  ndx<row.size();  ndx++) {
                    if (row.get(ndx).equalsIgnoreCase("category")) {
                        catCol = ndx;
//                    } else if (row.get(ndx).equalsIgnoreCase("subcategory")) {
//                        scatCol = ndx;
                    }
                }
            } else if (catCol < row.size()) {
                String pre = "";
                String post = "";
                String catValue  = row.get(catCol).trim();
                CategoryType ctype = CategoryType.fromString(catValue.toUpperCase().replace(' ', '_'));
                SubcategoryType sctype = (ctype != null) ? null : SubcategoryType.fromString(catValue.toUpperCase().replace(' ', '_'));
                if (sctype != null) {
                    pre = "[";
                    post = "]";
                    ctype = sctype.getCategoryType();
                }
                decadesData.add(catValue + "|" + pre + ctype + post + "|" + sctype);
            }
        }
    }
}
