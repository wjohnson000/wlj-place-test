package std.wlj.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Read in an EXCEL spreadsheet containing data for a "Place 2.0" truth-set, including
 * both the search values and the expected results.  The file is expected to consist of
 * the following columns, with the ones we care about highlighted ...
 * <ul>
 *   <li><strong>A - </Strong><strong> ID, sequential number that ties back to an external system</Strong></li>
 *   <li><strong>B - </Strong> Row, row number within a sub-set</li>
 *   <li><strong>C - </Strong>Line, input line number returned by bulk tool</li>
 *   <li><strong>D - </Strong><strong>ID1, identifier that ties together results for a single truth value</strong></li>
 *   <li><strong>E - </Strong><strong>Event place, the string to interpret</strong></li>
 *   <li><strong>F - </Strong>Human score, </li>
 *   <li><strong>G - </Strong><strong>Full display name</strong></li>
 *   <li><strong>H - </Strong>Type name,</li>
 *   <li><strong>I - </Strong><strong>Rep id, place-rep identifier</strong></li>
 *   <li><strong>J - </Strong><strong>Place id, place (owner) identifier</strong></li>
 *   <li><strong>K - </Strong><strong>From Year, jurisdiction start year</strong></li>
 *   <li><strong>L - </Strong><strong>To year, jurisdiction end year</strong></li>
 *   <li><strong>M - </Strong><strong>Latitude</strong></li>
 *   <li><strong>N - </Strong><strong>Longitude</strong></li>
 *   <li><strong>O - </Strong><strong>Parent rep id, parent place-rep identifier</strong></li>
 *   <li><strong>P - </Strong><strong>Default language, preferred locale</strong></li>
 *   <li><strong>Q - </Strong><strong>Type code, place-type code</strong></li>
 *   <li><strong>R - </Strong>Child constraint, if any, returned by the bulk tool</li>
 *   <li><strong>S - </Strong>Raw, score from the bulk-tool</li>
 *   <li><strong>T - </Strong><strong>AnyYear, from/to year range included as part of the search</strong></li>
 *   <li><strong>U - </Strong><strong>ReqParent, required parent place-rep identifier, included as part of the search</strong></li>
 *   <li><strong>V - </Strong>Search criteria, combination of fields used to do a search</li>
 *   <li><strong>W - </Strong>Freq, found in the data sampling for the collection</li>
 *   <li><strong>X - </Strong>Collection, source of the sample for this truth set</li>
 *   <li><strong>Y - </Strong>HighMedLow, not used</li>
 *   <li><strong>Z - </Strong>Region, not used</li>
 *   <li><strong>AA - </Strong>Record country, not used</li>
 *   <li><strong>AB - </Strong>Record sub-country, not used</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class ExcelReader {

    /** Key values for the field names */
    public static final String KEY_EXT_ID = "extId";
    public static final String KEY_EXT_ID1 = "extId1";
    public static final String KEY_SEARCH_STRING = "searchString";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_REP_ID = "repId";
    public static final String KEY_PLACE_ID = "placeId";
    public static final String KEY_FROM_YEAR = "fromYear";
    public static final String KEY_TO_YEAR = "toYear";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_PARENT_REP_ID = "parentRepId";
    public static final String KEY_DEFAULT_LOCALE = "defaultLocale";
    public static final String KEY_TYPE_CODE = "typeCode";
    public static final String KEY_YEAR_RANGE = "yearRange";
    public static final String KEY_REQ_PARENT_REPID = "reqParentRepId";

    /** 0-based cell numbers for the values to be extracted */
    private static final int COL_A_ID = 0;
    private static final int COL_D_ID1 = 3;
    private static final int COL_E_STRING = 4;
    private static final int COL_G_DISPLAY_NAME = 6;
    private static final int COL_I_REP_ID = 8;
    private static final int COL_J_PLACE_ID = 9;
    private static final int COL_K_FROM_YEAR = 10;
    private static final int COL_L_TO_YEAR = 11;
    private static final int COL_M_LATITUDE = 12;
    private static final int COL_N_LONGITUDE = 13;
    private static final int COL_O_PARENT_REP_ID = 14;
    private static final int COL_P_DEFAULT_LOCALE = 15;
    private static final int COL_Q_TYPE_CODE = 16;
    private static final int COL_T_ANY_YEAR = 19;
    private static final int COL_U_REQ_PARENT_ID = 20;


    private int              sheetNum;
    private File             excelFile;
    private FileInputStream  fis;
    private Iterator<Row>    rowIterator;


    /**
     * Constructor that takes the full path to the EXCEL file.
     * 
     * @param filePath path to the EXCEL file
     */
    public ExcelReader(String filePath) {
        this(filePath, 0);
    }

    /**
     * Constructor that takes the full path to the EXCEL file and a sheet number.
     * 
     * @param filePath path to the EXCEL file
     * @param sheetNum sheet number where data is located, 0=first sheet, etc.
     */
    public ExcelReader(String filePath, int sheetNum) {
        this.excelFile = new File(filePath);
        this.sheetNum  = sheetNum;

        // Verify that the file exists and is readable
        if (! excelFile.exists()  ||  ! excelFile.canRead()) {
            throw new IllegalArgumentException(
                    "File '" + filePath + "' doesn't exist, or isn't readable.");
        }

        // Create the ROW iterator
        createIterator();
    }

    /**
     * Retrieve the next set of data, returned as a HashMap with values being of type
     * 'String', 'Integer' or 'Double', as appropriate.
     * 
     * @return next set of data, or NULL if we've reached the end
     */
    public Map<String,Object> nextData() {
        Map<String,Object> results = null;
        if (rowIterator != null) {
            if (rowIterator.hasNext()) {
                results = new HashMap<>();
                Row row = rowIterator.next();
                results.put(KEY_EXT_ID, getInteger(row.getCell(COL_A_ID)));
                results.put(KEY_EXT_ID1, getInteger(row.getCell(COL_D_ID1)));
                results.put(KEY_SEARCH_STRING, getString(row.getCell(COL_E_STRING)));
                results.put(KEY_DISPLAY_NAME, getString(row.getCell(COL_G_DISPLAY_NAME)));
                results.put(KEY_REP_ID, getInteger(row.getCell(COL_I_REP_ID)));
                results.put(KEY_PLACE_ID, getInteger(row.getCell(COL_J_PLACE_ID)));
                results.put(KEY_FROM_YEAR, getInteger(row.getCell(COL_K_FROM_YEAR)));
                results.put(KEY_TO_YEAR, getInteger(row.getCell(COL_L_TO_YEAR)));
                results.put(KEY_LATITUDE, getDouble(row.getCell(COL_M_LATITUDE)));
                results.put(KEY_LONGITUDE, getDouble(row.getCell(COL_N_LONGITUDE)));
                results.put(KEY_PARENT_REP_ID, getInteger(row.getCell(COL_O_PARENT_REP_ID)));
                results.put(KEY_DEFAULT_LOCALE, getString(row.getCell(COL_P_DEFAULT_LOCALE)));
                results.put(KEY_TYPE_CODE, getString(row.getCell(COL_Q_TYPE_CODE)));
                results.put(KEY_YEAR_RANGE, getInteger(row.getCell(COL_T_ANY_YEAR)));
                results.put(KEY_REQ_PARENT_REPID, getInteger(row.getCell(COL_U_REQ_PARENT_ID)));
            } else {
                rowIterator = null;
                try {
                    fis.close();
                } catch (IOException ex) {
                    System.out.println("Unable to close excel file: " + ex.getMessage());
                }
            }
        }

        return results;
    }

    /**
     * Return the contents of a cell as a String value.
     * 
     * @param cell cell with data
     * @return String value of cell, or NULL
     */
    private static String getString(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
        }
        return null;
    }


    /**
     * Return the contents of a cell as a Double value.
     * 
     * @param cell cell with data
     * @return Double value of cell, or NULL
     */
    private static Double getDouble(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch(Exception ex) {
                    ;  // do nothing
                }
        }
        return null;
    }


    /**
     * Return the contents of a cell as an Integer value.
     * 
     * @param cell cell with data
     * @return Integer value of cell, or NULL
     */
    private static Integer getInteger(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return (int)cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                try {
                    Double dbl = Double.parseDouble(cell.getStringCellValue());
                    return dbl.intValue();
                } catch(Exception ex) {
                    ;  // do nothing
                }
        }
        return null;
    }

    /**
     * Set-up to start reading the data from the beginning.
     */
    private void createIterator() {
        // Create Workbook instance holding reference to .xlsx file, get first sheet,
        // and set up the iterator.
        try {
            fis = new FileInputStream(excelFile);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet mySheet = workbook.getSheetAt(sheetNum);
            rowIterator = mySheet.iterator();
        } catch (IOException ex) {
            System.out.println("Unable to re-start the file ...");
            rowIterator = null;
        }
    }

}
