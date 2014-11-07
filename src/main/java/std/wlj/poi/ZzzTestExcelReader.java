package std.wlj.poi;

import java.util.Map;

public class ZzzTestExcelReader {
    public static void main(String... args) {
        String inputFile = "C:/temp/TruthSet94_Wayne.xlsx";
        ExcelReader eReader = new ExcelReader(inputFile);
        Map<String,Object> row = eReader.nextData();
        while (row != null) {
            System.out.println("Row: " + row);
            row = eReader.nextData();
        }
        System.exit(0);
    }
}
