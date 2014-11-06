package std.wlj.poi;

import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ExcelReader {
    public static void main(String... arsg) throws Exception {
        String inputFile = "C:/temp/TruthSet94_Wayne.xlsx";

        // Create Workbook instance holding reference to .xlsx file, get first sheet
        FileInputStream fis = new FileInputStream(inputFile);

        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet mySheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        int rowNum = 0;
        Iterator<Row> rowIterator = mySheet.iterator();
        while (rowIterator.hasNext()) {
            rowNum++;
            System.out.print(rowNum + "|");

            // For each row, iterate through each columns
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        System.out.print(cell.getStringCellValue() + "|");
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        System.out.print(formatDbl(cell.getNumericCellValue()) + "|");
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        System.out.print(cell.getBooleanCellValue() + "|");
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        System.out.print("|");
                        break;
                    default :
                        System.out.print(">>>" + cell.getCellType() + "<<<|");
                }
            }
            System.out.println();
        }

        fis.close();
    }

    /**
     * Format a "double" value correctly by dropping the trailing ".0" in numeric
     * values ...
     * 
     * @param value double value
     * @return formatted value
     */
    private static String formatDbl(double value) {
        String res = String.valueOf(value);
        if (res.endsWith(".0")) {
            return res.substring(0, res.length()-2);
        } else {
            return res;
        }
    }
}
