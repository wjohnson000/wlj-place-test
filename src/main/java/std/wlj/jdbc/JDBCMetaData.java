package std.wlj.jdbc;

import java.sql.*;
import java.util.Arrays;

import std.wlj.util.DbConnectionManager;

public class JDBCMetaData {
    public static void main(String...args) throws SQLException {
        try (Connection conn = DbConnectionManager.getDataSourceSams().getConnection()) {
            DatabaseMetaData dbmd = conn.getMetaData();
            generalStuff(dbmd);
            tableStuff(dbmd);
            viewStuff(dbmd);
            typeStuff(dbmd);
            functionStuff(dbmd);
            procedureStuff(dbmd);
            systemFunctionStuff(dbmd);
            keywordStuff(dbmd);
        }
    }

    static void generalStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("General stuff ...");
        System.out.println("=====================================================================================================");

        System.out.println("Catalog-Term:   " + dbmd.getCatalogTerm());
        System.out.println("Schema-Term:    " + dbmd.getSchemaTerm());
        System.out.println("DB-Prod-Name:   " + dbmd.getDatabaseProductName());
        System.out.println("DB-Prod-Versn:  " + dbmd.getDatabaseProductVersion());
        System.out.println("Driver-Name:    " + dbmd.getDriverName());
        System.out.println("Driver-Version: " + dbmd.getDriverVersion());

        try(ResultSet rset = dbmd.getCatalogs()) {
            handleRSET("catalog", rset, 0);
        }

        try(ResultSet rset = dbmd.getSchemas()) {
            handleRSET("schema", rset, 0);
        }
    }

    static void tableStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("Table stuff ...");
        System.out.println("=====================================================================================================");

        try(ResultSet rset = dbmd.getTableTypes()) {
            handleRSET("table-type", rset, 0);
        }

        try(ResultSet rset = dbmd.getTables(null, "sams_place", null, new String[] { "TABLE" })) {
            handleRSET("table", rset, 0);
        }

        try(ResultSet rset = dbmd.getColumns(null, "sams_place", "place_rep", null)) {
            handleRSET("place-rep-table-columns", rset, 0, "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "ORDINAL_POSITION", "COLUMN_SIZE", "DECIMAL_DIGITS", "NULLABLE");
        }
//        try(ResultSet rset = dbmd.getColumnPrivileges(null, "sams_place", "place_rep", null)) {
//            handleRSET("place-rep-column-privilege", rset, 0);
//        }
        try(ResultSet rset = dbmd.getImportedKeys(null, "sams_place", "place_rep")) {
            handleRSET("place-rep-imported-keys", rset, 0, "pktable_schem", "pktable_name", "pkcolumn_name", "fktable_schema", "fktable_name", "fkcolumn_name");
        }
        try(ResultSet rset = dbmd.getIndexInfo(null, "sams_place", "place_rep", false, true)) {
            handleRSET("place-rep-table-index", rset, 0, "table_schem", "table_name", "non_unique", "index_name", "column_name");
        }

        try(ResultSet rset = dbmd.getColumns(null, "sams_place", "citation", null)) {
            handleRSET("citation-table-columns", rset, 0, "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "ORDINAL_POSITION", "COLUMN_SIZE", "DECIMAL_DIGITS", "NULLABLE");
        }
//        try(ResultSet rset = dbmd.getColumnPrivileges(null, "sams_place", "citation", null)) {
//            handleRSET("citation-column-privilege", rset, 0);
//        }
        try(ResultSet rset = dbmd.getImportedKeys(null, "sams_place", "citation")) {
            handleRSET("citation-imported-keys", rset, 0, "pktable_schem", "pktable_name", "pkcolumn_name", "fktable_schema", "fktable_name", "fkcolumn_name");
        }
        try(ResultSet rset = dbmd.getIndexInfo(null, "sams_place", "citation", false, true)) {
            handleRSET("citation-table-index", rset, 0, "table_schem", "table_name", "non_unique", "index_name", "column_name");
        }

        try(ResultSet rset = dbmd.getColumns(null, "sams_place", "rep_boundary", null)) {
            handleRSET("rep-boundary-table-columns", rset, 0, "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "ORDINAL_POSITION", "COLUMN_SIZE", "DECIMAL_DIGITS", "NULLABLE");
        }
//        try(ResultSet rset = dbmd.getColumnPrivileges(null, "sams_place", "rep_boundary", null)) {
//            handleRSET("rep-boundary-column-privilege", rset, 0);
//        }
        try(ResultSet rset = dbmd.getImportedKeys(null, "sams_place", "rep_boundary")) {
            handleRSET("rep-boundary-imported-keys", rset, 0, "pktable_schem", "pktable_name", "pkcolumn_name", "fktable_schema", "fktable_name", "fkcolumn_name");
        }
        try(ResultSet rset = dbmd.getIndexInfo(null, "sams_place", "rep_boundary", false, true)) {
            handleRSET("rep-boundary-table-index", rset, 0, "table_schem", "table_name", "non_unique", "index_name", "column_name");
        }
    }

    static void viewStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("View stuff ...");
        System.out.println("=====================================================================================================");

        try(ResultSet rset = dbmd.getTables(null, null, null, new String[] { "VIEW" })) {
            handleRSET("table", rset, 0);
        }
    }

    static void typeStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("Type stuff ...");
        System.out.println("=====================================================================================================");

        try(ResultSet rset = dbmd.getTypeInfo()) {
            handleRSET("type", rset, 0, "TYPE_NAME", "DATA_TYPE");
        }

        try(ResultSet rset = dbmd.getUDTs(null, null, null, null)) {
            handleRSET("user-defined-type", rset, 0, "TYPE_SCHEMA", "TYPE_NAME", "DATA_TYPE");
        }
    }

    static void functionStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("Function stuff ...");
        System.out.println("=====================================================================================================");
    }

    static void procedureStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("Procedure stuff ...");
        System.out.println("=====================================================================================================");
    }

    static void systemFunctionStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("System Function stuff ...");
        System.out.println("=====================================================================================================");

        System.out.println("\nSystem ...\n==========");
        String funcNames = dbmd.getSystemFunctions();
        Arrays.stream(funcNames.split(",")).forEach(System.out::println);

        System.out.println("\nString ...\n==========");
        funcNames = dbmd.getStringFunctions();
        Arrays.stream(funcNames.split(",")).forEach(System.out::println);

        System.out.println("\nTime-Date ...\n=============");
        funcNames = dbmd.getTimeDateFunctions();
        Arrays.stream(funcNames.split(",")).forEach(System.out::println);
    }

    static void keywordStuff(DatabaseMetaData dbmd) throws SQLException {
        System.out.println("\n=====================================================================================================");
        System.out.println("Keyword stuff ...");
        System.out.println("=====================================================================================================");

        String keywords = dbmd.getSQLKeywords();
        Arrays.stream(keywords.split(",")).forEach(System.out::println);
    }

    private static void handleRSET(String title, ResultSet rset, int max, String... colNames) throws SQLException {
        System.out.println();
        System.out.println(">>> " + title + " <<<");

        ResultSetMetaData rsmd = rset.getMetaData();
        System.out.println("  Col.count=" + rsmd.getColumnCount());
        for (int i=1;  i<=rsmd.getColumnCount();  i++) {
            System.out.println("     [" + i + "]: " + rsmd.getColumnName(i) + " . " + rsmd.getColumnTypeName(i) + " . " + rsmd.getColumnClassName(i));
        }

        System.out.println();
        int count = 0;
        while (rset.next()  &&  (max == 0  ||  count++ < max)) {
            System.out.print("  ");
            for (int i=1;  i<=rsmd.getColumnCount();  i++) {
                if (includeColumn(rsmd.getColumnName(i), colNames)) {
                    System.out.print(rsmd.getColumnName(i) + "=" + rset.getString(rsmd.getColumnName(i)) + "   ");
                }
            }
            System.out.println();
        }
    }

    private static boolean includeColumn(String colName, String... colNames) {
        if (colNames == null) {
            return true;
        } else if (colNames.length == 0) {
            return true;
        } else {
            return Arrays.stream(colNames).anyMatch(cn -> cn.compareToIgnoreCase(colName) == 0);
        }
    }
}
