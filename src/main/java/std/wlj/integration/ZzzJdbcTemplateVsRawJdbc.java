package std.wlj.integration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.dao.dbimpl.DAOImplBase;
import org.familysearch.standards.place.dao.model.DbPlace;
import org.familysearch.standards.place.dao.model.DbPlaceName;
import org.familysearch.standards.place.dao.model.DbPlaceRep;
import org.familysearch.standards.place.dao.model.DbRepDisplayName;
import org.springframework.beans.factory.annotation.Autowired;


public class ZzzJdbcTemplateVsRawJdbc extends DAOImplBase {

    /**
     * @param dataSource
     */
    @Autowired
    public ZzzJdbcTemplateVsRawJdbc(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * @param dataSource
     * @param schemaName
     */
    public ZzzJdbcTemplateVsRawJdbc(DataSource dataSource, String schemaName) {
        super(dataSource, schemaName);
    }

    /* (non-Javadoc)
     * @see org.familysearch.standards.place.dao.postgres.DAOImplBase#getTableName()
     */
    @Override
    protected String getTableName() {
        return "DUMMY";
    }

    // 
    // ============================================================================================
    // Retrieval methods using JdbcTemplate
    // ============================================================================================
    //

    /**
     * Get a "DbPlace" using the JdbcTemplate
     * @param placeId place identifier
     * @return the place, or null if not found
     */
    public DbPlace readPlaceTemplate(int placeId) {
        String query = "SELECT * FROM sams_place.PLACE WHERE place_id = ? ORDER BY tran_id DESC LIMIT 1";
        List<Map<String,Object>> rows = getJdbc().queryForList(query, placeId);
        List<DbPlace> results = createPlaces(rows);
        return (results.size() > 0) ? results.get(0) : null;
    }

    /**
     * Get a list of "DbPlaceName" for a place using the JdbcTemplate
     * @param placeId the place identifier
     * @return list of names
     */
    public List<DbPlaceName> readNamesTemplate(Integer placeId) {
        String query = "SELECT * FROM sams_place.PLACE_NAME WHERE place_id = ? ORDER BY tran_id DESC";
        List<Map<String,Object>> rows = getJdbc().queryForList(query, placeId);
        List<DbPlaceName> results = createNames(rows);
        return results;
    }

    /**
     * Get a "DbPlaceRep" using the JdbcTemplate
     * @param placeRepId place-rep identifier
     * @return the place-rep, or null if not found
     */
    public DbPlaceRep readPlaceRepTemplate(int placeRepId) {
        String query = "SELECT * FROM sams_place.PLACE_REP WHERE rep_id = ? ORDER BY tran_id DESC LIMIT 1";
        List<Map<String,Object>> rows = getJdbc().queryForList(query, placeRepId);
        List<DbPlaceRep> results = createPlaceReps(rows);
        return (results.size() > 0) ? results.get(0) : null;
    }

    /**
     * Get a list of "DbPlaceRep" tied to a place using the JdbcTemplate
     * @param placeId place identifier
     * @return the place-rep, or null if not found
     */
    public List<DbPlaceRep> readPlaceRepByPlaceTemplate(int placeId) {
        String query = "SELECT * FROM sams_place.PLACE_REP WHERE owner_id = ? ORDER BY tran_id DESC";
        List<Map<String,Object>> rows = getJdbc().queryForList(query, placeId);
        List<DbPlaceRep> results = createPlaceReps(rows);
        return results;
    }

    /**
     * Get a list of all display names of a place-rep using the JdbcTemplate
     * @param placeRepId place-rep identifier
     * @return list of names
     */
    public List<DbRepDisplayName> readDisplayNamesTemplate(Integer placeRepId) {
        String query = "SELECT * FROM sams_place.REP_DISPLAY_NAME WHERE rep_id = ? ORDER BY tran_id DESC";
        List<Map<String,Object>> rows = getJdbc().queryForList(query, placeRepId);
        List<DbRepDisplayName> results = createRepDisplayNames(rows);

        // Save the first result for each Locale
        Set<StdLocale> matched = new HashSet<StdLocale>();
        for (Iterator<DbRepDisplayName> iter=results.iterator();  iter.hasNext(); ) {
            DbRepDisplayName placeDisplayName = iter.next();
            if (matched.contains(placeDisplayName.getLocale())) {
                iter.remove();
            } else {
                matched.add(placeDisplayName.getLocale());
            }
        }

        return results;
    }

    // 
    // ============================================================================================
    // Methods using raw JDBC
    // ============================================================================================
    //

    /**
     * Get a "DbPlace" using raw JDBC
     * @param placeId place identifier
     * @return the place, or null if not found
     */
    public DbPlace readPlaceJDBC(int placeId) {
        DbPlace result = null;
        String query = "SELECT * FROM sams_place.PLACE WHERE place_id = ? ORDER BY tran_id DESC LIMIT 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getJdbc().getDataSource().getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, placeId);
            rset = stmt.executeQuery();
            if (rset.next()) {
                result = createPlaceFromRow(rset);
            }
        } catch(Exception ex) {
            System.out.println("Place.SQL-EX: " + ex.getMessage());
        } finally {
            if (rset != null) try { rset.close(); } catch(Exception ex) { }
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
            if (conn != null) try { conn.close(); } catch(Exception ex) { }
        }

        return result;
    }

    /**
     * Get a list of "DbPlaceName" for a place using raw JDBC
     * @param placeId the place identifier
     * @return list of names
     */
    public List<DbPlaceName> readNamesJDBC(Integer placeId) {
        List<DbPlaceName> result = new ArrayList<DbPlaceName>();
        String query = "SELECT * FROM sams_place.PLACE_NAME WHERE place_id = ? ORDER BY tran_id DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getJdbc().getDataSource().getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, placeId);
            rset = stmt.executeQuery();
            while (rset.next()) {
                result.add(createNameFromRow(rset));
            }
        } catch(Exception ex) {
            System.out.println("PlaceName.SQL-EX: " + ex.getMessage());
        } finally {
            if (rset != null) try { rset.close(); } catch(Exception ex) { }
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
            if (conn != null) try { conn.close(); } catch(Exception ex) { }
        }

        return result;
    }

    /**
     * Get a "DbPlaceRep" using raw JDBC
     * @param placeRepId place-rep identifier
     * @return the place-rep, or null if not found
     */
    public DbPlaceRep readPlaceRepJDBC(int placeRepId) {
        DbPlaceRep result = null;
        String query = "SELECT * FROM sams_place.PLACE_REP WHERE rep_id = ? ORDER BY tran_id DESC LIMIT 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getJdbc().getDataSource().getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, placeRepId);
            rset = stmt.executeQuery();
            if (rset.next()) {
                result = createPlaceRepFromRow(rset);
            }
        } catch(Exception ex) {
            System.out.println("PlaceRep.SQL-EX: " + ex.getMessage());
        } finally {
            if (rset != null) try { rset.close(); } catch(Exception ex) { }
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
            if (conn != null) try { conn.close(); } catch(Exception ex) { }
        }

        return result;
    }

    /**
     * Get a list of "DbPlaceRep" tied to a place using raw JDBC
     * @param placeId place identifier
     * @return the place-rep, or null if not found
     */
    public List<DbPlaceRep> readPlaceRepByPlaceJDBC(int placeId) {
        List<DbPlaceRep> result = new ArrayList<DbPlaceRep>();
        String query = "SELECT * FROM sams_place.PLACE_REP WHERE owner_id = ? ORDER BY tran_id DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = getJdbc().getDataSource().getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, placeId);
            rset = stmt.executeQuery();
            while (rset.next()) {
                result.add(createPlaceRepFromRow(rset));
            }
        } catch(Exception ex) {
            System.out.println("PlaceRepList.SQL-EX: " + ex.getMessage());
        } finally {
            if (rset != null) try { rset.close(); } catch(Exception ex) { }
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
            if (conn != null) try { conn.close(); } catch(Exception ex) { }
        }

        return result;
    }

    /**
     * Get a list of all display names of a place-rep using raw JDBC
     * @param placeRepId place-rep identifier
     * @return list of names
     */
    public List<DbRepDisplayName> readDisplayNamesJDBC(Integer placeRepId) {
        List<DbRepDisplayName> result = new ArrayList<DbRepDisplayName>();
        String query = "SELECT * FROM sams_place.REP_DISPLAY_NAME WHERE rep_id = ? ORDER BY tran_id DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        // Save the first result for each Locale
        Set<StdLocale> matched = new HashSet<StdLocale>();
        try {
            conn = getJdbc().getDataSource().getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, placeRepId);
            rset = stmt.executeQuery();
            while (rset.next()) {
                DbRepDisplayName repDispName = createRepDisplayNameFromRow(rset);
                if (! matched.contains(repDispName.getLocale())) {
                    result.add(repDispName);
                    matched.add(repDispName.getLocale());
                }
            }
        } catch(Exception ex) {
            System.out.println("PlaceName.SQL-EX: " + ex.getMessage());
        } finally {
            if (rset != null) try { rset.close(); } catch(Exception ex) { }
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
            if (conn != null) try { conn.close(); } catch(Exception ex) { }
        }

        return result;
    }

    // 
    // ============================================================================================
    // Methods to turn JdcbTemplate results into objects
    // ============================================================================================
    //

    /**
     * Create a list of Place instances based on the rows returned from the database
     */
    private List<DbPlace> createPlaces(List<Map<String,Object>> rows) {
        List<DbPlace> results = new ArrayList<DbPlace>();
        for (Map<String,Object> row : rows) {
            results.add(createPlaceFromRow(row));
        }
        return results;
    }

    /**
     * Create a list of PlaceName instances based on the rows returned from the database
     */
    private List<DbPlaceName> createNames(List<Map<String,Object>> rows) {
        List<DbPlaceName> results = new ArrayList<DbPlaceName>();
        for (Map<String,Object> row : rows) {
            results.add(createNameFromRow(row));
        }
        return results;

    }

    /**
     * Create a list of Place instances based on the rows returned from the database
     */
    private List<DbPlaceRep> createPlaceReps(List<Map<String,Object>> rows) {
        List<DbPlaceRep> results = new ArrayList<DbPlaceRep>();
        for (Map<String,Object> row : rows) {
            results.add(createPlaceRepFromRow(row));
        }
        return results;
    }

    /**
     * Create a list of PlaceDisplayName instances based on the rows returned from the database
     */
    private List<DbRepDisplayName> createRepDisplayNames(List<Map<String,Object>> rows) {
        List<DbRepDisplayName> results = new ArrayList<DbRepDisplayName>();
        for (Map<String,Object> row : rows) {
            results.add(createRepDisplayNameFromRow(row));
        }
        return results;
    }

    /**
     * Create a new "Place" instance based on a row of data retrieved from the underlying datastore.
     */
    private DbPlace createPlaceFromRow(Map<String,Object> row) {
        DbPlace place = new DbPlace();

        place.setId(getInteger(row, "place_id"));
        place.setTransId(getInteger(row, "tran_id"));
        place.setFromYear(getInteger(row, "from_year"));
        place.setToYear(getInteger(row, "to_year"));
        place.setDeleteId(getInteger(row, "delete_id"));
        
        return place;
    }

    /**
     * Create a new "PlaceName" instance based on a row of data retrieved from the underlying datastore.
     */
    private DbPlaceName createNameFromRow(Map<String,Object> row) {
        DbPlaceName placeName = new DbPlaceName();

        placeName.setId(getInteger(row, "name_id"));
        placeName.setTransId(getInteger(row, "tran_id"));
        placeName.setText(getString(row, "text"));
        placeName.setLocale(getStdLocale(row, "locale"));
        placeName.setNameTypeId(getInteger(row, "type_id"));
        placeName.setPlaceId(getInteger(row, "place_id"));
        placeName.setDeleted(getBoolean(row, "delete_flag"));
        
        return placeName;
    }

    /**
     * Create a new "Place" instance based on a row of data retrieved from the underlying datastore.
     */
    private DbPlaceRep createPlaceRepFromRow(Map<String,Object> row) {
        DbPlaceRep placeRep = new DbPlaceRep();

        placeRep.setId(getInteger(row, "rep_id"));
        placeRep.setTransId(getInteger(row, "tran_id"));
        placeRep.setParentId(getInteger(row, "parent_id"));
        placeRep.setOwnerId(getInteger(row, "owner_id"));
        placeRep.setCentroidLongitude(getDouble(row, "centroid_long"));
        placeRep.setCentroidLatitude(getDouble(row, "centroid_lattd"));
        placeRep.setPlaceTypeId(getInteger(row, "place_type_id"));
        placeRep.setParentFromYear(getInteger(row, "parent_from_year"));
        placeRep.setParentToYear(getInteger(row, "parent_to_year"));
        placeRep.setDeleteId(getInteger(row, "delete_id"));
        placeRep.setPreferredLocale(getStdLocale(row, "pref_locale"));
        placeRep.setPublished(getBoolean(row, "pub_flag"));
        placeRep.setValidated(getBoolean(row, "validated_flag"));
        placeRep.setUuid(getUUID(row, "uuid"));
        placeRep.setGroupId(getInteger(row, "group_id"));
        
        return placeRep;
    }

    /**
     * Create a new "PlaceDisplayName" instance based on a row of data retrieved from the underlying datastore.
     */
    private DbRepDisplayName createRepDisplayNameFromRow(Map<String,Object> row) {
        DbRepDisplayName repDisplayName = new DbRepDisplayName();

        repDisplayName.setRepId(getInteger(row, "rep_id"));
        repDisplayName.setTransId(getInteger(row, "tran_id"));
        repDisplayName.setLocale(getStdLocale(row, "locale"));
        repDisplayName.setText(getString(row, "text"));
        
        return repDisplayName;
    }

    // 
    // ============================================================================================
    // Methods to turn JDBC [ResultSet] results into objects
    // ============================================================================================
    //

    /**
     * Create a new "Place" instance based on a result-set
     */
    private DbPlace createPlaceFromRow(ResultSet rset) throws SQLException {
        DbPlace place = new DbPlace();

        place.setId(rset.getInt("place_id"));
        place.setTransId(rset.getInt("tran_id"));
        place.setFromYear(rset.getInt("from_year"));    if (rset.wasNull()) place.setFromYear(null);
        place.setToYear(rset.getInt("to_year"));        if (rset.wasNull()) place.setToYear(null);
        place.setDeleteId(rset.getInt("delete_id"));    if (rset.wasNull()) place.setDeleteId(null);
        
        return place;
    }

    /**
     * Create a new "PlaceName" instance based on a result-set
     */
    private DbPlaceName createNameFromRow(ResultSet rset) throws SQLException {
        DbPlaceName placeName = new DbPlaceName();
        placeName.setId(rset.getInt("name_id"));
        placeName.setTransId(rset.getInt("tran_id"));
        placeName.setText(rset.getString("text"));
        placeName.setLocale(getStdLocale(rset, "locale"));
        placeName.setNameTypeId(rset.getInt("type_id"));
        placeName.setPlaceId(rset.getInt("place_id"));
        placeName.setDeleted(rset.getBoolean("delete_flag"));
        
        return placeName;
    }

    /**
     * Create a new "Place" instance based on a result-set
     */
    private DbPlaceRep createPlaceRepFromRow(ResultSet rset) throws SQLException {
        DbPlaceRep placeRep = new DbPlaceRep();

        placeRep.setId(rset.getInt("rep_id"));
        placeRep.setTransId(rset.getInt("tran_id"));
        placeRep.setParentId(rset.getInt("parent_id"));                   if (rset.wasNull()) placeRep.setParentId(null);
        placeRep.setOwnerId(rset.getInt("owner_id"));
        placeRep.setCentroidLongitude(rset.getDouble("centroid_long"));   if (rset.wasNull()) placeRep.setCentroidLongitude(null);
        placeRep.setCentroidLatitude(rset.getDouble("centroid_lattd"));   if (rset.wasNull()) placeRep.setCentroidLatitude(null);
        placeRep.setPlaceTypeId(rset.getInt("place_type_id"));
        placeRep.setParentFromYear(rset.getInt("parent_from_year"));      if (rset.wasNull()) placeRep.setParentFromYear(null);
        placeRep.setParentToYear(rset.getInt("parent_to_year"));          if (rset.wasNull()) placeRep.setParentToYear(null);
        placeRep.setDeleteId(rset.getInt("delete_id"));                   if (rset.wasNull()) placeRep.setDeleteId(null);
        placeRep.setPreferredLocale(getStdLocale(rset, "pref_locale"));
        placeRep.setPublished(rset.getBoolean("pub_flag"));
        placeRep.setValidated(rset.getBoolean("validated_flag"));
        placeRep.setUuid(getUUID(rset, "uuid"));
        placeRep.setGroupId(rset.getInt("group_id"));                     if (rset.wasNull()) placeRep.setGroupId(null);
        
        return placeRep;
    }

    /**
     * Create a new "RepDisplayName" instance based on a result-set
     */
    private DbRepDisplayName createRepDisplayNameFromRow(ResultSet rset) throws SQLException {
        DbRepDisplayName repDisplayName = new DbRepDisplayName();

        repDisplayName.setRepId(rset.getInt("rep_id"));
        repDisplayName.setTransId(rset.getInt("tran_id"));
        repDisplayName.setLocale(getStdLocale(rset, "locale"));
        repDisplayName.setText(rset.getString("text"));
        
        return repDisplayName;
    }

    /**
     * Create a "StdLocale" object from the database's result-set
     */
    private StdLocale getStdLocale(ResultSet rset, String colName) throws SQLException {
        String localeStr = rset.getString(colName);
        return (localeStr == null) ? null : new StdLocale(localeStr);
    }

    /**
     * Create a "UUID" object from the database's result-set
     */
    private UUID getUUID(ResultSet rset, String colName) throws SQLException {
        String uuidStr = rset.getString(colName);
        return (uuidStr == null) ? null : UUID.fromString(uuidStr);
    }
}
