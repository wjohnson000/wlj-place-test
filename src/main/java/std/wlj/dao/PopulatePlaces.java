package std.wlj.dao;

import java.io.*;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.PlaceDAO;
import org.familysearch.standards.place.dao.RepDisplayNameDAO;
import org.familysearch.standards.place.dao.PlaceNameDAO;
import org.familysearch.standards.place.dao.PlaceRepDAO;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.model.DbPlace;
import org.familysearch.standards.place.dao.model.DbPlaceName;
import org.familysearch.standards.place.dao.model.DbPlaceRep;
import org.familysearch.standards.place.dao.model.DbRepDisplayName;

import std.wlj.util.DbConnectionManager;


public class PopulatePlaces {

    private static DataSource ds;
    private static DAOFactory daoFactory;
    private static Random random = new Random();
    private static Map<Integer,String> typeMap = new HashMap<Integer,String>();
    private static Map<Integer,Integer> placeIdMap = new HashMap<Integer,Integer>();
    private static Map<Integer,Integer> placeRepIdMap = new HashMap<Integer,Integer>();
    private static Map<Integer,List<String>> usedNames = new HashMap<Integer,List<String>>();

    private static boolean useDAO = false;
    private static Connection dsConn = null;
    private static PreparedStatement placeStmt = null;
    private static PreparedStatement placeNameStmt = null;
    private static PreparedStatement placeRepStmt = null;
    private static PreparedStatement placeDispNameStmt = null;

    public static void main(String[] args) throws Exception {
        ds = DbConnectionManager.getDataSourceWLJ();
        daoFactory = new DAOFactoryImpl(ds);

        // Load the concepts into a map, "description" --> "concept_id"
        loadConcepts();

        // Clean out the database ...
        dsConn = ds.getConnection();
        Statement stmt = dsConn.createStatement();
        stmt.executeUpdate("DELETE FROM sams_place.plc_disp_name");
        stmt.executeUpdate("DELETE FROM sams_place.plc_name");
        stmt.executeUpdate("DELETE FROM sams_place.plc_rep");
        stmt.executeUpdate("DELETE FROM sams_place.place");
        stmt.close();
        dsConn.close();

        dsConn = ds.getConnection();
        System.out.println("Auto-commit? " + dsConn.getAutoCommit());
        dsConn.setAutoCommit(false);
        System.out.println("Auto-commit? " + dsConn.getAutoCommit());

        long nnow = System.nanoTime();
        int cnt01 = createPlaces("C:/temp/places/place.txt");
        long time01 = System.nanoTime() - nnow;

        nnow = System.nanoTime();
        int cnt02 = createPlaceNames("C:/temp/places/place-name.txt");
        long time02 = System.nanoTime() - nnow;

        nnow = System.nanoTime();
        int cnt03 = createPlaceReps("C:/temp/places/place-rep.txt");
        long time03 = System.nanoTime() - nnow;

        nnow = System.nanoTime();
        int cnt04 = createDisplayNames("C:/temp/places/place-disp-name.txt");
        long time04 = System.nanoTime() - nnow;

        dsConn.close();

        System.out.println("PLACE.count=" + cnt01 + ";  time=" + (time01/1000000.0));
        System.out.println("PLC_NAME.count=" + cnt02 + ";  time=" + (time02/1000000.0));
        System.out.println("PLC_REP.count=" + cnt03 + ";  time=" + (time03/1000000.0));
        System.out.println("PLC_DISP_NAME.count=" + cnt04 + ";  time=" + (time04/1000000.0));
    }

    /**
     * Parse the file containing entries for the "place" table and load them.
     * 
     * @param filePath full path to the input file
     * @return number of entries created
     */
    private static int createPlaces(String filePath) {
        List<String> rows = readAll(filePath);
        rows.remove(0);  // Remove the header row ...

        int rCount = 0;
        for (String row : rows) {
            if (rCount % 1000 == 0) System.out.println("PLACES -- " + rCount + " of " + rows.size());
            if (rCount % 500 == 0) try { dsConn.commit(); } catch(Exception ex) { }

            String[] rData = row.split("\\|", 3);
            if (rData.length > 2) {
                Integer placeId = Integer.parseInt(rData[0]);
                Integer fromYr  = getInteger(rData[1]);
                Integer toYr    = getInteger(rData[2]);

                if (placeIdMap.containsKey(placeId)) continue;
                if (fromYr != null  &&  toYr != null  &&  fromYr > toYr) {
                    System.out.println("Invalid from/to range -- id: " + placeId + "  [" + fromYr + " - " + toYr + "]");
                    continue;
                }

                rCount++;
                createPlace(placeId, null, fromYr, toYr);
                placeIdMap.put(placeId, placeId);
            }
        }
        try { dsConn.commit(); } catch(Exception ex) { }

        return rCount;
    }

    /**
     * Parse the file containing entries for the "plc_name" table and load them.
     * 
     * @param filePath full path to the input file
     * @return number of entries created
     */
    private static int createPlaceNames(String filePath) {
        usedNames.clear();
        List<String> rows = readAll(filePath);
        rows.remove(0);  // Remove the header row ...

        int rCount = 0;
        for (String row : rows) {
            if (rCount % 1000 == 0) System.out.println("PLACE-NAMES -- " + rCount + " of " + rows.size());
            if (rCount % 500 == 0) try { dsConn.commit(); } catch(Exception ex) { }

            String[] rData = row.split("\\|", 5);
            if (rData.length > 4) {
                Integer placeId = Integer.parseInt(rData[0]);
                String  locale  = rData[1];
                String  text    = rData[2];
                Integer typeId  = getInteger(rData[3]);

                List<String> names = usedNames.get(placeId);
                if (names == null) {
                    names = new ArrayList<String>();
                    usedNames.put(placeId, names);
                } else if (names.contains(text)) {
                    continue;
                }
                names.add(text);

                rCount++;
                createPlaceName(null, null, text, new StdLocale(locale), typeId, placeId); 
            }
        }
        try { dsConn.commit(); } catch(Exception ex) { }

        return rCount;

    }

    /**
     * Parse the file containing entries for the "plc_rep" table and load them.
     * 
     * @param filePath full path to the input file
     * @return number of entries created
     */
    private static int createPlaceReps(String filePath) {
        List<String> rows = readAll(filePath);
        rows.remove(0);  // Remove the header row ...

        int rCount = 0;
        for (String row : rows) {
            if (rCount % 1000 == 0) System.out.println("PLACE-REPS -- " + rCount + " of " + rows.size());
            if (rCount % 500 == 0) try { dsConn.commit(); } catch(Exception ex) { }

            String[] rData = row.split("\\|", 10);
            if (rData.length > 9) {
                Integer placeId   = getInteger(rData[0]);
                Integer parentId  = getInteger(rData[1]);
                Integer ownerId   = getInteger(rData[2]);
                Double  latitude  = getDouble(rData[3]);
                Double  longitude = getDouble(rData[4]);
                Integer typeId    = getInteger(rData[5]);
                Integer parFromYr = getInteger(rData[6]);
                Integer parToYr   = getInteger(rData[7]);
                Boolean isPub     = (rData[8].length() > 0  &&  rData[8].toLowerCase().startsWith("t"));
                Boolean isVal     = (rData[9].length() > 0  &&  rData[9].toLowerCase().startsWith("t"));

                if (parentId == -1) {
                    parentId = null;
                } else {
                    parentId = placeRepIdMap.get(parentId);
                    if (parentId == null) System.out.println("Couldn't find new parent ID!!! " + rData[1]);
                }
                if (placeRepIdMap.containsKey(placeId)) continue;

                rCount++;
                Integer newRepId = createPlaceRep(null, null, parentId, ownerId, typeId, latitude, longitude, parFromYr, parToYr, isPub, isVal);
                placeRepIdMap.put(placeId, newRepId);
            }
        }
        try { dsConn.commit(); } catch(Exception ex) { }

        return rCount;

    }

    /**
     * Parse the file containing entries for the "plc_disp_name" table and load them.
     * 
     * @param filePath full path to the input file
     * @return number of entries created
     */
    private static int createDisplayNames(String filePath) {
        usedNames.clear();
        List<String> rows = readAll(filePath);
        rows.remove(0);  // Remove the header row ...

        int rCount = 0;
        for (String row : rows) {
            if (rCount % 1000 == 0) System.out.println("PLACE-DISPLAY-NAMES -- " + rCount + " of " + rows.size());
            if (rCount % 500 == 0) try { dsConn.commit(); } catch(Exception ex) { }

            String[] rData = row.split("\\|", 3);
            if (rData.length > 2) {
                Integer placeRepId = getInteger(rData[0]);
                String  locale     = rData[1];
                String  text       = rData[2];

                List<String> names = usedNames.get(placeRepId);
                if (names == null) {
                    names = new ArrayList<String>();
                    usedNames.put(placeRepId, names);
                } else if (names.contains(text)) {
                    continue;
                }
                names.add(text);

                Integer newPlaceRepId = placeRepIdMap.get(placeRepId);
                if (newPlaceRepId == null) {
                    System.out.println("Couldn't find new parent ID for name!!! " + placeRepId);
                } else {
                    rCount++;
                    createPlaceDispName(newPlaceRepId, new StdLocale(locale), null, text);
                }
            }
        }
        try { dsConn.commit(); } catch(Exception ex) { }

        return rCount;

    }

    /**
     * Create a new PLACE
     * @param placeId place ID, or null for a new one
     * @param transxId transaction ID, or null to use current value
     * @param fromYear start year
     * @param toYear end year
     * @return new place identifier
     */
    private static Integer createPlace(Integer placeId, Integer transxId, Integer fromYear, Integer toYear) {
        if (useDAO) {
            DbPlace place = new DbPlace();
            if (placeId  != null  &&  placeId > 0) place.setId(placeId);
            if (transxId != null  &&  transxId > 0) place.setTransId(transxId);
            if (fromYear != null) place.setFromYear(fromYear);
            if (toYear   != null) place.setToYear(toYear);

            PlaceDAO placeDao = daoFactory.getPlaceDAO();
            DbPlace cPlace = placeDao.create(place);
            return cPlace.getId();
        } else {
            String query = "INSERT INTO sams_place.place(plc_id, tran_id, from_date, to_date, dlt_id) VALUES(?,?,?,?,null)";
            try {
                if (placeStmt == null) {
                    placeStmt = dsConn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                }
                placeStmt.clearParameters();
                setIntegerParam(placeStmt, 1, placeId);
                setIntegerParam(placeStmt, 2, transxId);
                setIntegerParam(placeStmt, 3, fromYear);
                setIntegerParam(placeStmt, 4, toYear);
                placeStmt.executeUpdate();
                ResultSet rset = placeStmt.getGeneratedKeys();
                if (rset.next()) {
                    return rset.getInt("plc_id");
                }
            } catch(Exception ex) {
                System.out.println("Unable to create PLACE - id=" + placeId);
            } finally {
//                try { placeStmt.close(); } catch(Exception ex) { }
//                placeStmt = null;
            }

            return -1;
        }
    }

    /**
     * Create a PLACE-REPRESENTATION
     * @param placeRepId place-representation identifier, or null for a new one
     * @param tratransxId transaction identifier, or null to use current valuensxId
     * @param parentId parent identifier, unless this is a top-level place-rep
     * @param ownerId PLACE owner identifier
     * @param typeId typeId of the place
     * @param latitude latitude
     * @param longitude longitude
     * @param parentFromYear from-year of the parent
     * @param parentToYear to-year of the parent
     * @param isPublished isPublished flag
     * @param isValidated isValidated flag
     * @return key of the newly-created instance
     */
    private static Integer createPlaceRep(Integer placeRepId, Integer transxId, Integer parentId,
            Integer ownerId, Integer typeId, Double latitude, Double longitude, Integer parentFromYear,
            Integer parentToYear, Boolean isPublished, Boolean isValidated) {
        if (useDAO) {
            DbPlaceRep placeRep = new DbPlaceRep();
            if (placeRepId != null  &&  placeRepId > 0) placeRep.setId(placeRepId);
            if (transxId != null  &&  transxId > 0) placeRep.setTransId(transxId);
            if (parentId != null  &&  parentId > 0) placeRep.setParentId(parentId);
            if (ownerId != null  &&  ownerId > 0) placeRep.setOwnerId(ownerId);
            if (parentFromYear != null  &&  parentFromYear > 0) placeRep.setParentFromYear(parentFromYear);
            if (parentToYear != null  &&  parentToYear > 0) placeRep.setParentToYear(parentToYear);
            placeRep.setPlaceTypeId(typeId);
            placeRep.setCentroidLongitude(random.nextDouble()*90);
            placeRep.setCentroidLatitude(random.nextDouble()*90);
            placeRep.setPublished(true);
            placeRep.setValidated(true);
            placeRep.setPreferredLocale(new StdLocale("en_US"));
            placeRep.setUuid(UUID.randomUUID());

            PlaceRepDAO placeRepDao = daoFactory.getPlaceRepDAO();
            DbPlaceRep cPlRep = placeRepDao.create(placeRep);
            return cPlRep.getId();
        } else {
            String query =
                  "INSERT INTO sams_place.plc_rep(" +
                  "    rep_id, tran_id, parent, owner, centroid_long, centroid_lattd," +
                  "    plc_tp_id, parent_from_date, parent_to_date, dlt_id, pub_flg," +
                  "    pref_locale, validated_flg, uuid) " +
                  " VALUES(?,?,?,?,?,?,?,?,?,null,?,?,?,?)";
            try {
                if (placeRepStmt == null) {
                    placeRepStmt = dsConn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                }
                placeRepStmt.clearParameters();
                setIntegerParam(placeRepStmt, 1, placeRepId);
                setIntegerParam(placeRepStmt, 2, transxId);
                setIntegerParam(placeRepStmt, 3, parentId);
                setIntegerParam(placeRepStmt, 4, ownerId);
                setDoubleParam(placeRepStmt, 5, longitude);
                setDoubleParam(placeRepStmt, 6, latitude);
                setIntegerParam(placeRepStmt, 7, typeId);
                setIntegerParam(placeRepStmt, 8, parentFromYear);
                setIntegerParam(placeRepStmt, 9, parentToYear);
                setBooleanParam(placeRepStmt, 10, isPublished);
                setStdLocaleParam(placeRepStmt, 11, new StdLocale("en_US"));
                setBooleanParam(placeRepStmt, 12, isValidated);
                setUUIDParam(placeRepStmt, 13, UUID.randomUUID());
                placeRepStmt.executeUpdate();
                ResultSet rset = placeRepStmt.getGeneratedKeys();
                if (rset.next()) {
                    return rset.getInt("rep_id");
                }
            } catch(Exception ex) {
                System.out.println("Unable to create PLACE-REP - id=" + placeRepId);
            } finally {
//                try { placeRepStmt.close(); } catch(Exception ex) { }
//                placeRepStmt = null;
            }
        }

        return -1;
    }

    /**
     * Create a PLACE-NAME
     * @param nameId name identifier, or null for a new one
     * @param transxId transaction identifier, or null to use current value
     * @param text text of the name
     * @param locale locale of the name
     * @param typeId typeId of the place
     * @param placeId place identifier
     * @return key of the newly-created instance
     */
    private static Integer createPlaceName(Integer nameId, Integer transxId, String text, StdLocale locale,
            Integer typeId, Integer placeId) {
        if (useDAO) {
            DbPlaceName plName = new DbPlaceName();
            if (nameId != null  &&  nameId > 0) plName.setId(nameId);
            if (transxId != null  &&  transxId > 0) plName.setTransId(transxId);
            plName.setText(text);
            plName.setLocale(locale);
            plName.setNameTypeId(typeId);
            plName.setPlaceId(placeId);

            PlaceNameDAO plNameDao = daoFactory.getPlaceNameDAO();
            DbPlaceName cPlName = plNameDao.create(plName);
            return cPlName.getId();
        } else {
            String query = "INSERT INTO sams_place.plc_name(name_id, tran_id, text, locale, type_id, plc_id, dlt_flg) VALUES(?,?,?,?,?,?,null)";
            try {
                if (placeNameStmt == null) {
                    placeNameStmt = dsConn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                }
                placeNameStmt.clearParameters();
                setIntegerParam(placeNameStmt, 1, nameId);
                setIntegerParam(placeNameStmt, 2, transxId);
                setStringParam(placeNameStmt, 3, text);
                setStdLocaleParam(placeNameStmt, 4, locale);
                setIntegerParam(placeNameStmt, 5, typeId);
                setIntegerParam(placeNameStmt, 6, placeId);
                placeNameStmt.executeUpdate();
                ResultSet rset = placeNameStmt.getGeneratedKeys();
                if (rset.next()) {
                    return rset.getInt("name_id");
                }
            } catch(Exception ex) {
                System.out.println("Unable to create PLACE-NAME - id=" + nameId);
            } finally {
//                try { placeNameStmt.close(); } catch(Exception ex) { }
//                placeNameStmt = null;
            }
        }

        return -1;
    }

    /**
     * Create a new PLACE-DISPLAY-NAME
     * @param placeRepId place-representation identifier
     * @param locale locale locale
     * @param transxId transaction ID, or null to use current value
     * @param text display name text
     * @return key of the newly-created instance
     */
    private static Integer createPlaceDispName(Integer placeRepId, StdLocale locale, Integer transxId,
            String text) {
        if (useDAO) {

            DbRepDisplayName plDispName = new DbRepDisplayName();
            if (placeRepId  != null  &&  placeRepId > 0) plDispName.setRepId(placeRepId);
            if (transxId != null  &&  transxId > 0) plDispName.setTransId(transxId);
            plDispName.setLocale(locale);
            plDispName.setText(text);

            RepDisplayNameDAO plDispNameDao = daoFactory.getRepDisplayNameDAO();
            DbRepDisplayName cPlDispName = plDispNameDao.create(plDispName);
            return cPlDispName.getId();
        } else {
            String query = "INSERT INTO sams_place.plc_disp_name(rep_id, locale, tran_id, text) VALUES(?,?,?,?)";
            try {
                if (placeDispNameStmt == null) {
                    placeDispNameStmt = dsConn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                }
                placeDispNameStmt.clearParameters();
                setIntegerParam(placeDispNameStmt, 1, placeRepId);
                setStdLocaleParam(placeDispNameStmt, 2, locale);
                setIntegerParam(placeDispNameStmt, 3, transxId);
                setStringParam(placeDispNameStmt, 4, text);
                placeDispNameStmt.executeUpdate();
                ResultSet rset = placeDispNameStmt.getGeneratedKeys();
                if (rset.next()) {
                    return rset.getInt("rep_id");
                }
            } catch(Exception ex) {
                System.out.println("Unable to create PLACE-DISP-NAME - id=" + placeRepId);
            } finally {
//                try { placeDispNameStmt.close(); } catch(Exception ex) { }
//                placeDispNameStmt = null;
            }

            return -1;
        }
    }

    /**
     * Load all of the concepts into a MAP
     */
    public static void loadConcepts() throws Exception {
        Connection conn = ds.getConnection();
        Statement  stmt = conn.createStatement();
        ResultSet  rset = stmt.executeQuery("SELECT * FROM sams_cv.concept");
        while (rset.next()) {
            int cId = rset.getInt("concept_id");
            String descr = rset.getString("description");
            typeMap.put(cId, descr);
        }

        rset.close();
        stmt.close();
        conn.close();
    }

    /**
     * Return the integer value of a string, or null
     * @param intValue String containing an integer value
     * @return integer value of the string
     */
    private static Integer getInteger(String intValue) {
        return (intValue == null  ||  intValue.trim().length() == 0) ? null : Integer.parseInt(intValue);
    }

    /**
     * Return the double value of a string, or null
     * @param dblValue String containing a double value
     * @return double value of the string
     */
    private static Double getDouble(String dblValue) {
        return (dblValue == null  ||  dblValue.trim().length() == 0) ? null : Double.parseDouble(dblValue);
    }

    /**
     * Read all lines from a file, assuming a "UTF-8" character encoding of the data.
     * 
     * @param filePath path to the file
     * @return list of all lines from the file.
     */
    private static List<String> readAll(String filePath) {
        List<String> results = new ArrayList<String>();

        BufferedReader rbuf = null;
        try {
            File inFile = new File(filePath);
            Reader reader = new InputStreamReader(new FileInputStream(inFile), "UTF-8");
            rbuf = new BufferedReader(reader);
            while (rbuf.ready()) {
                results.add(rbuf.readLine());
            }
        } catch(Exception ex) {
            
        } finally {
            if (rbuf != null) try { rbuf.close(); } catch(Exception ex) { }
        }

        return results;
    }

    /**
     * Set a String parameter into a prepared-statement, allowing for null value
     * @param ps prepared statement
     * @param colNum column number (1-based)
     * @param value String value to set, or NULL
     * @throws SQLException
     */
    private static void setStringParam(PreparedStatement ps, int colNum, String value) throws SQLException {
        if (value == null) {
            ps.setNull(colNum, Types.VARCHAR);
        } else {
            ps.setString(colNum, value);
        }
    }

    /**
     * Set an Integer parameter into a prepared-statement, allowing for null value
     * @param ps prepared statement
     * @param colNum column number (1-based)
     * @param value Integer value to set, or NULL
     * @throws SQLException
     */
    private static void setIntegerParam(PreparedStatement ps, int colNum, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(colNum, Types.INTEGER);
        } else {
            ps.setInt(colNum, value);
        }
    }

    /**
     * Set an Double parameter into a prepared-statement, allowing for null value
     * @param ps prepared statement
     * @param colNum column number (1-based)
     * @param value Double value to set, or NULL
     * @throws SQLException
     */
    private static void setDoubleParam(PreparedStatement ps, int colNum, Double value) throws SQLException {
        if (value == null) {
            ps.setNull(colNum, Types.DOUBLE);
        } else {
            ps.setDouble(colNum, value);
        }
    }

    /**
     * Set a StdLocale parameter into a prepared-statement, allowing for null value
     * @param ps prepared statement
     * @param colNum column number (1-based)
     * @param value StdLocale value to set, or NULL
     * @throws SQLException
     */
    private static void setStdLocaleParam(PreparedStatement ps, int colNum, StdLocale value) throws SQLException {
        if (value == null) {
            ps.setNull(colNum, Types.VARCHAR);
        } else {
            ps.setString(colNum, value.getLocaleAsString());
        }
    }

    /**
     * Set a Boolean parameter into a prepared-statement, allowing for null value
     * @param ps prepared statement
     * @param colNum column number (1-based)
     * @param value Boolean value to set, or NULL
     * @throws SQLException
     */
    private static void setBooleanParam(PreparedStatement ps, int colNum, Boolean value) throws SQLException {
        if (value == null) {
            ps.setNull(colNum, Types.BOOLEAN);
        } else {
            ps.setBoolean(colNum, value);
        }
    }

    /**
     * Set a UUID parameter into a prepared-statement, allowing for null value
     * @param ps prepared statement
     * @param colNum column number (1-based)
     * @param value UUID value to set, or NULL
     * @throws SQLException
     */
    private static void setUUIDParam(PreparedStatement ps, int colNum, UUID value) throws SQLException {
        if (value == null) {
            ps.setNull(colNum, Types.VARCHAR);
        } else {
            ps.setString(colNum, value.toString());
        }
    }
}
