package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.data.GroupImpl;
import org.familysearch.standards.place.data.SourceBridge;
import org.familysearch.standards.place.data.SourceImpl;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.TypeImpl;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.util.PlaceHelper;


/**
 * There are values SOLR relies on which are normally stored in a database, which
 * don't fit the standard {@link PlaceRepDoc} format.  However the SOLR schema has
 * been enhanced to include an "appData" field which can be used to store any generic
 * application data.  Nine documents will be included, with the following IDs:
 * 
 * <ul>
 *   <li><strong>ATTRIBUTE-TYPE</strong> - the ATTRIBUTE types data</li>
 *   <li><strong>CITATION-TYPE</strong> - the CITATION types data</li>
 *   <li><strong>EXT-XREF</strong> - the EXT_XREF types data</li>
 *   <li><strong>NAME-TYPE</strong> - the NAME types data</li>
 *   <li><strong>PLACE-TYPE</strong> - the PLACE types data</li>
 *   <li><strong>RESOLUTION-TYPE</strong> - the RESOLUTION types data</li>
 *   <li><strong>SOURCE</strong> - the SOURCE data</li>
 *   <li><strong>GROUP</strong> - the place-type groups, sub-groups and members</li>
 *   <li><strong>NAME-PRIORITY</strong> - the priority for a given name-type</li>
 *   <li><strong>FEEDBACK_STATE</strong> - FEEDBACK status types</li>
 *   <li><strong>FEEDBACK_RESOLUTION</strong> - FEEDBACK resolution types</li>
 *   <li><strong>REP_RELATION</strong> - the alternative jurisdiction (rep-to-rep) types</li>
 * </ul>
 * 
 * This class will load the data from the SOLR repository, cache it, and renew the
 * cache periodically.
 * <p/>
 * 
 * @author wjohnson000
 *
 */
public class AppDataManagerLocal {
    protected static final String MODULE_NAME = "solr";

    public static final String ATTR_TYPE_ID = "ATTRIBUTE-TYPE";
    public static final String CITATION_TYPE_ID = "CITATION-TYPE";
    public static final String EXT_XREF_TYPE_ID = "EXT-XREF-TYPE";
    public static final String NAME_TYPE_ID = "NAME-TYPE";
    public static final String PLACE_TYPE_ID = "PLACE-TYPE";
    public static final String RESOLUTION_TYPE_ID = "RESOLUTION-TYPE";
    public static final String FEEDBACK_RESOLUTION_TYPE_ID = "FEEDBACK-RESOLUTION-TYPE";
    public static final String FEEDBACK_STATUS_TYPE_ID = "FEEDBACK-STATE-TYPE";
    public static final String SOURCE_ID = "SOURCE";
    public static final String GROUP_HIERARCHY_ID = "GROUP-HIERARCHY";
    public static final String NAME_PRIORITY_ID = "NAME-PRIORITY";
    public static final String REP_RELATION_ID = "REP-RELATION";

    public static final String GROUP_DATA_SECTION = "group-data";
    public static final String GROUP_HIER_SECTION = "group-hierarchy-data";
    public static final String GROUP_MEMBER_SECTION = "group-member-data";

    protected static final char DELIMITER = '|';

    /** Keep track of the current version of the app-data documents that we are managing */
    String                                   baseDir = null;
    Map<String,Integer>                      appDataRevMap = new HashMap<>();

    /** Maps which hold all of the data */
    protected Map<Integer,TypeBridge>        allTypes = new HashMap<>();
    protected Map<String,TypeBridge>         allTypesByCode = new HashMap<>();

    protected Map<Integer,GroupBridge>       allGroups = new HashMap<>();
    protected Map<Integer,Set<GroupBridge>>  entityIdToGroups = new HashMap<>();

    protected Map<Integer,SourceBridge>      allSources = new HashMap<>();

    protected Map<Integer,Integer>           namePriority = new HashMap<>();
    protected Map<String,Integer>            namePriorityByCode = new HashMap<>();

    /** Allow for a system property that will prevent the re-load Thread from starting */
    protected boolean isUnitTestEnv = ("true".equalsIgnoreCase(System.getProperty("place2.unit.test.env", "false")));


    /**
     * Factory method to create a {@link PlaceRepDoc} that will hold application
     * data only, and doesn't represent a real place-rep document.
     * 
     * @param id document identifier
     * @param appData application data
     * @return new "PlaceRepDoc" instance.
     */
    public static PlaceRepDoc makeAppDataDoc(String id, List<String> appData) {
        PlaceRepDoc newDoc = new PlaceRepDoc();


        newDoc.setId(id);
        newDoc.setAppData(appData);

        // Set additional dummy data for required fields
        newDoc.setRevision(0);
        newDoc.addDisplayName("---", "en");
        newDoc.addVariantName(-1, -1, "en", "---");
        newDoc.setRepIdChain(new Integer[] { -1 });
        newDoc.setRepId(-1);
        newDoc.setOwnerId(-1);
        newDoc.setParentId(-1);
        newDoc.setType(-1);
        newDoc.setPrefLocale("en");
        newDoc.setPublished(0);
        newDoc.setValidated(0);
        newDoc.setRevision(0);
        newDoc.setCreateDate(new java.util.Date());
        newDoc.setLastUpdateDate(new java.util.Date());

        return newDoc;
    }

    public AppDataManagerLocal(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Determine if the given document is one of the special "App-Data" documents, which
     * don't contain any place or place-rep information.
     * 
     * @param aDoc {@link PlaceRepDoc} instance
     * @return TRUE if it's an app-data document, FALSE otherwise
     */
    public static boolean isAppDataDoc(PlaceRepDoc aDoc) {
        if (aDoc != null) {
            switch(aDoc.getId()) {
                case ATTR_TYPE_ID:
                case CITATION_TYPE_ID:
                case EXT_XREF_TYPE_ID:
                case NAME_TYPE_ID:
                case PLACE_TYPE_ID:
                case RESOLUTION_TYPE_ID:
                case FEEDBACK_STATUS_TYPE_ID:
                case FEEDBACK_RESOLUTION_TYPE_ID:
                case SOURCE_ID:
                case GROUP_HIERARCHY_ID:
                case NAME_PRIORITY_ID:
                case REP_RELATION_ID:
                    return true;
            }
        }
        return false;
    }

    public Set<TypeBridge> getTypes(TypeBridge.TYPE type, boolean noCache) {
        if (noCache) {
            String docId = getIdForType(type);
            if (docId != null) {
                loadAppData(docId);
            }
        }

        // TODO: should we use a "TreeSet"?  If so, 'TypeBridge' needs to be comparable
        Set<TypeBridge> typeBs = new HashSet<>(100);

        for (TypeBridge typeB : allTypes.values()) {
            if (typeB.getType() == type) {
                typeBs.add(typeB);
            }
        }

        return typeBs;
    }

    public TypeBridge getType(TypeBridge.TYPE type, int id, boolean noCache) {
        if (noCache) {
            String docId = getIdForType(type);
            if (docId != null) {
                loadAppData(docId);
            }
        }

        TypeBridge typeB = allTypes.get(id);

        return (typeB == null  ||  typeB.getType() != type) ? null : typeB;
    }

    public TypeBridge getType(TypeBridge.TYPE type, String code, boolean noCache) {
        if (noCache) {
            String docId = getIdForType(type);
            if (docId != null) {
                loadAppData(docId);
            }
        }

        TypeBridge typeB = allTypesByCode.get(code);

        return (typeB == null  ||  typeB.getType() != type) ? null : typeB;
    }

    public void addType(TypeBridge typeB) throws PlaceDataException {
        // Re-load the data to ensure we have the latest, then do the update
        String docId = getIdForType(typeB.getType());
        if (docId != null) {
            loadAppData(docId);
            allTypes.put(typeB.getTypeId(), typeB);
            allTypesByCode.put(typeB.getCode(), typeB);
        }
    }

    public PlaceRepDoc getTypeAppDataDoc(TypeBridge.TYPE typeCat) {
        List<String> appData = new ArrayList<String>();

        Set<TypeBridge> typeBs = new TreeSet<>();

        for (TypeBridge typeB : allTypes.values()) {
            if (typeB.getType() == typeCat) {
                typeBs.add(typeB);
            }
        }

        for (TypeBridge type : typeBs) {
            Map<String,String> names = type.getNames();
            Map<String,String> descr = type.getDescriptions();
            for (Map.Entry<String,String> entry : names.entrySet()) {
                StringBuilder buff = new StringBuilder(64);
                buff.append(type.getTypeId());
                buff.append(DELIMITER).append(type.getCode());
                buff.append(DELIMITER).append(type.isPublished());
                buff.append(DELIMITER).append(entry.getKey());
                buff.append(DELIMITER).append(entry.getValue());
                buff.append(DELIMITER).append(descr.containsKey(entry.getKey()) ? descr.get(entry.getKey()) : "");
                appData.add(buff.toString());
            }
        }

        PlaceRepDoc appDoc = null;
        String docId = getIdForType(typeCat);
        if (docId != null) {
            appDoc = AppDataManagerLocal.makeAppDataDoc(docId, appData);
            incrementRevision(appDoc);
        }

        return appDoc;
    }

    public Set<SourceBridge> getSources(boolean noCache) {
        if (noCache) {
            loadAppData(SOURCE_ID);
        }

        return new HashSet<SourceBridge>(allSources.values());
    }

    public SourceBridge getSource(int id, boolean noCache) {
        if (noCache) {
            loadAppData(SOURCE_ID);
        }

        return allSources.get(id);
    }

    public void addSource(SourceBridge sourceB) throws PlaceDataException {
        // Re-load the data to ensure we have the latest.
        loadAppData(SOURCE_ID);
        allSources.put(sourceB.getSourceId(), sourceB);
    }

    public PlaceRepDoc getSourceAppDataDoc() {
        List<String> appData = new ArrayList<String>();

        for (SourceBridge source : allSources.values()) {
            StringBuilder buff = new StringBuilder(64);
            buff.append(source.getSourceId());
            buff.append(DELIMITER).append(source.getTitle());
            buff.append(DELIMITER).append(source.getDescription() == null ? "" : source.getDescription());
            buff.append(DELIMITER).append(source.isPublished());
            appData.add(buff.toString());
        }

        PlaceRepDoc appDoc = AppDataManagerLocal.makeAppDataDoc(AppDataManagerLocal.SOURCE_ID, appData);
        incrementRevision(appDoc);
        return appDoc;
    }

    public Set<GroupBridge> getGroups(GroupBridge.TYPE groupType, boolean noCache) {
        if (noCache) {
            loadAppData(GROUP_HIERARCHY_ID);
        }

        // Retrieve groups of the given type
        Set<GroupBridge> results = new HashSet<>();

        for (GroupBridge groupB : allGroups.values()) {
            if (groupType.equals(groupB.getType())) {
                results.add(groupB);
            }
        }
        
        return results;
    }

    public GroupBridge getGroup(GroupBridge.TYPE groupType, int id, boolean noCache) {
        if (noCache) {
            loadAppData(GROUP_HIERARCHY_ID);
        }

        // Ensure that the given group -- if found -- is of the correct type
        GroupBridge groupB = allGroups.get(id);
        return (groupB != null  &&  groupType.equals(groupB.getType())) ? groupB : null;
    }

    public Set<GroupBridge> getGroupsByMemberId(GroupBridge.TYPE groupType, int id, boolean noCache) {
        if (noCache) {
            loadAppData(GROUP_HIERARCHY_ID);
        }

        // There is currently only one active group-type, so return all matches
        Set<GroupBridge> results = entityIdToGroups.get(id);
        if (results == null) {
            return new HashSet<>();
        }

        Set<GroupBridge> updatedResults = new HashSet<GroupBridge>();
        for (Iterator<GroupBridge> iter=results.iterator();  iter.hasNext();) {
            GroupBridge groupB = iter.next();
            if (groupType.equals(groupB.getType())) {
                updatedResults.add( groupB );
            }
        }

        return updatedResults;
    }

    public int getNamePriority(int nameTypeId) {
        Integer     priority;

        priority = namePriority.get( nameTypeId );
        if ( priority == null ) {
            priority = 0;
        }
        return priority;
    }

    public int getNamePriority(String code) {
        return (namePriorityByCode.containsKey(code)) ? namePriorityByCode.get(code) : 0;
    }

    public void addGroup(GroupBridge groupB) throws PlaceDataException {
        // Re-load the data to ensure we have the latest.
        loadAppData(GROUP_HIERARCHY_ID);

        allGroups.put(groupB.getGroupId(), groupB);

        for (Integer memberId : groupB.getMembers()) {
            Set<GroupBridge> groups = entityIdToGroups.get(memberId);
            if (groups == null) {
                groups = new HashSet<>();
                entityIdToGroups.put(memberId, groups);
            }
            groups.add(groupB);
        }
    }

    public PlaceRepDoc getGroupAppDataDoc() {
        List<String> appData    = new ArrayList<>();
        List<String> groupData  = new ArrayList<>();
        List<String> hierData   = new ArrayList<>();
        List<String> memberData = new ArrayList<>();

        for (GroupBridge group : allGroups.values()) {
            Map<String,String> names = group.getNames();
            Map<String,String> descr = group.getDescriptions();
            for (Map.Entry<String,String> entry : names.entrySet()) {
                StringBuilder buff = new StringBuilder(64);
                buff.append(group.getGroupId());
                buff.append(DELIMITER).append(group.getType());
                buff.append(DELIMITER).append(entry.getKey());
                buff.append(DELIMITER).append(entry.getValue());
                buff.append(DELIMITER).append(descr.containsKey(entry.getKey()) ? descr.get(entry.getKey()) : "");
                buff.append(DELIMITER).append(group.isPublished());
                groupData.add(buff.toString());
            }

            for (GroupBridge child : group.getDirectSubGroups()) {
                StringBuilder buff = new StringBuilder(32);
                buff.append(group.getGroupId());
                buff.append(DELIMITER).append(child.getGroupId());
                buff.append(DELIMITER).append(false);
                hierData.add(buff.toString());
            }

            for (Integer memberId : group.getDirectMembers()) {
                StringBuilder buff = new StringBuilder();
                buff.append(group.getGroupId());
                buff.append(DELIMITER).append(memberId);
                buff.append(DELIMITER).append(false);
                memberData.add(buff.toString());
            }
        }

        // Assemble the data ...
        appData.add(GROUP_DATA_SECTION);
        appData.addAll(groupData);
        appData.add(GROUP_HIER_SECTION);
        appData.addAll(hierData);
        appData.add(GROUP_MEMBER_SECTION);
        appData.addAll(memberData);

        PlaceRepDoc appDoc = AppDataManagerLocal.makeAppDataDoc(GROUP_HIERARCHY_ID, appData);
        incrementRevision(appDoc);
        return appDoc;
    }

    protected  String getIdForType(TypeBridge.TYPE type) {
        String docId = null;

        switch(type) {
            case ATTRIBUTE:
                docId = ATTR_TYPE_ID;  break;
            case CITATION:
                docId = CITATION_TYPE_ID;  break;
            case EXT_XREF:
                docId = EXT_XREF_TYPE_ID;  break;
            case NAME:
                docId = NAME_TYPE_ID;  break;
            case PLACE:
                docId = PLACE_TYPE_ID;  break;
            case RESOLUTION:
                docId = RESOLUTION_TYPE_ID;  break;
            case FEEDBACK_STATE:
                docId = FEEDBACK_STATUS_TYPE_ID;  break;
            case FEEDBACK_RESOLUTION:
                docId = FEEDBACK_RESOLUTION_TYPE_ID;  break;
            case REP_RELATION:
                docId = REP_RELATION_ID;  break;
        }

        return docId;
    }

    /**
     * Create the TYPE and PLACE-TYPE-GROUP lists, etc., from the specialized "AppData"
     * documents in SOLR.  If no change is detected in the app-data from one load to
     * the next, skip processing that data.
     *
     * @param docId document ID of the data to load, or null to load everything
     */
    protected void loadAppData(String docId) {
        Map<Integer,TypeBridge> typeData;

        // Load the ATTRIBUTE type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(ATTR_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.ATTRIBUTE, ATTR_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.ATTRIBUTE, typeData);
            }
        }

        // Load the CITATION type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(CITATION_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.CITATION, CITATION_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.CITATION, typeData);
            }
        }

        // Load the EXT-XREF type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(EXT_XREF_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.EXT_XREF, EXT_XREF_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.EXT_XREF, typeData);
            }
        }

        // Load the NAME type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(NAME_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.NAME, NAME_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.NAME, typeData);
            }
        }

        // Load the PLACE type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(PLACE_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.PLACE, PLACE_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.PLACE, typeData);
            }
        }

        // Load the RESOLUTION type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(RESOLUTION_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.RESOLUTION, RESOLUTION_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.RESOLUTION, typeData);
            }
        }

        // Load the FEEDBACK RESOLUTION type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(FEEDBACK_RESOLUTION_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.FEEDBACK_RESOLUTION, FEEDBACK_RESOLUTION_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.FEEDBACK_RESOLUTION, typeData);
            }
        }

        // Load the FEEDBACK RESOLUTION type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(FEEDBACK_STATUS_TYPE_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.FEEDBACK_STATE, FEEDBACK_STATUS_TYPE_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.FEEDBACK_STATE, typeData);
            }
        }

        // Load the REP_RELATION type stuff, and create the map of CODE --> Type
        if (docId == null  ||  docId.equalsIgnoreCase(REP_RELATION_ID)) {
            typeData = this.loadTypes(TypeBridge.TYPE.REP_RELATION, REP_RELATION_ID);
            if (typeData.size() > 0) {
                updateTypeCodes(TypeBridge.TYPE.REP_RELATION, typeData);
            }
        }

        // Load groups and sources
        if (docId == null  ||  docId.equalsIgnoreCase(GROUP_HIERARCHY_ID)) {
            this.loadGroups();
            this.loadMemberToGroupMapping();
        }
        if (docId == null  ||  docId.equalsIgnoreCase(SOURCE_ID)) {
            this.loadSources();
        }

        // Load the name priorities last, since they depend on other data
        if (docId == null  ||  docId.equalsIgnoreCase(NAME_PRIORITY_ID)) {
            this.loadNamePriorities();
        }
    }

    /**
     * Generate the next revision number for the document, based on the document id.
     * 
     * @param PlaceRepDoc app-doc
     */
    protected void incrementRevision(PlaceRepDoc appDoc) {
        Integer currRev = appDataRevMap.get(appDoc.getId());
        Integer nextRev = (currRev == null) ? 1 : currRev+1;

        appDataRevMap.put(appDoc.getId(), nextRev);
        appDoc.setRevision(nextRev.intValue());
    }

    /**
     * Load a list of TYPE instances from a special SOLR document.  The data will be
     * pipe-delimited, of the form:
     * <ul>
     *   <li><strong>1</strong> - type identifier</li>
     *   <li><strong>2</strong> - code</li>
     *   <li><strong>3</strong> - published, "true" or "false"</li>
     *   <li><strong>4</strong> - locale</li>
     *   <li><strong>5</strong> - name, in the given locale</li>
     *   <li><strong>6</strong> - description, in the given locale</li>
     * </ul>
     * 
     * @param type the TYPE of type
     * @param docId the SOLR document ID
     */
    protected Map<Integer,TypeBridge> loadTypes(TypeBridge.TYPE type, String docId) {
        Map<Integer,TypeBridge>  results = new TreeMap<>();
        List<String>             appData = this.getAppData(docId);
        if (appData.isEmpty()) {
            return results;
        }

        // Save all rows for a given type, so the TypeBridge can be fully created
        Map<Integer,List<String>> typeRowMap = new HashMap<Integer,List<String>>();

        for (String line : appData) {
            String[] values = PlaceHelper.split(line, DELIMITER);
            if (values.length >= 5) {
                int typeId = Integer.parseInt(values[0].trim());
                List<String> rows = typeRowMap.get(typeId);
                if (rows == null) {
                    rows = new ArrayList<String>();
                    typeRowMap.put(typeId, rows);
                }
                rows.add(line);
            }
        }

        // Create a TypeBridge for each collection of lines
        for (Map.Entry<Integer,List<String>> entry : typeRowMap.entrySet()) {
            Map<String,String> names = new HashMap<String,String>();
            Map<String,String> descr = new HashMap<String,String>();

            List<String> rows = entry.getValue();
            String line = rows.get(0);
            String[] values = PlaceHelper.split(line, DELIMITER);
            int typeId = Integer.parseInt(values[0]);
            String code = values[1];
            boolean isPublished = Boolean.parseBoolean(values[2]);

            for (String row : rows) {
                values = PlaceHelper.split(row, DELIMITER);
                names.put(values[3], values[4]);
                descr.put(values[3], (values.length == 5 ? "" : values[5]));
            }
            TypeImpl impl = new TypeImpl(null, type, typeId, code, names, descr, isPublished);
            results.put(typeId,  impl);
        }

        return results;
    }

    /**
     * Load and create all GROUP instances.  The main data file is tab-delimited
     * and valid rows consist of 5 columns:
     * <ul>
     *   <li><strong>1</strong> - group identifier</li>
     *   <li><strong>2</strong> - name</li>
     *   <li><strong>3</strong> - description</li>
     *   <li><strong>4</strong> - published, "true" or "false"</li>
     *   <li><strong>5</strong> - deleted, "true" or "false"</li>
     * </ul>
     * 
     * Rows with fewer than five columns will be skipped.  There is no header row.
     * The 'allGroups' map will be updated in-place.
     * 
     */
    protected void loadGroups() {
        // If there is nothing to process, skip the rest of the process
        List<String> appData = this.getAppData(GROUP_HIERARCHY_ID);
        if (appData.isEmpty()) {
            return;
        }

        List<String> groupDataRows = new ArrayList<String>();
        List<String> groupHierRows = new ArrayList<String>();
        List<String> groupMemberRows = new ArrayList<String>();

        int section = 0;
        
        for (String line : appData) {
            if (GROUP_DATA_SECTION.equals(line)) {
                section = 1;
            } else if (GROUP_HIER_SECTION.equals(line)) {
                section = 2;
            } else if (GROUP_MEMBER_SECTION.equals(line)) {
                section = 3;
            } else if (section == 1) {
                groupDataRows.add(line);
            } else if (section == 2) {
                groupHierRows.add(line);
            } else if (section == 3) {
                groupMemberRows.add(line);
            }
        }

        // Load the sub-groups and group-members
        Map<Integer,Set<Integer>> hierMap = this.loadGroupHierarchy(groupHierRows);
        Map<Integer,Set<Integer>> memberMap = this.loadGroupMembers(groupMemberRows);

        // Save the type-ids, and names and descriptions
        Map<Integer,GroupBridge.TYPE> grpCatMap = new HashMap<>();
        Map<Integer,Map<String,String[]>> nameDescrMap = new HashMap<>();
        for (String line : groupDataRows) {
            String[] values = PlaceHelper.split(line, DELIMITER);
            if (values.length >= 6) {
                int groupId      = Integer.parseInt(values[0]);
                String groupType = values[1];
                String locale    = values[2];
                String name      = values[3];
                String descr     = values[4];

                // Load the name/description even if it's not published
                Map<String,String[]> ndMap = nameDescrMap.get(groupId);
                if (ndMap == null) {
                    ndMap = new HashMap<>();
                    nameDescrMap.put(groupId, ndMap);
                }
                ndMap.put(locale, new String[] { name, descr });
                grpCatMap.put(groupId, GroupBridge.TYPE.valueOf(groupType));
            }
        }

        // Create all of the groups w/ their subgroups and members
        Map<Integer,GroupBridge> newGroups = new TreeMap<Integer,GroupBridge>();
        for ( Map.Entry<Integer,Map<String,String[]>> entry : nameDescrMap.entrySet() ) {
            constructGroup(entry.getKey(), grpCatMap, nameDescrMap, hierMap, memberMap, newGroups);
        }

        // Move the new group definitions to the 'allGroups' collection, removing any
        // groups that are no longer used
        Set<Integer> unusedKeys = new HashSet<>(allGroups.keySet());
        for (Map.Entry<Integer,GroupBridge> entry : newGroups.entrySet()) {
            unusedKeys.remove(entry.getKey());
            allGroups.put(entry.getKey(), entry.getValue());
        }

        for (Integer unusedKey : unusedKeys) {
            allGroups.remove(unusedKey);
        }
    }

    /**
     * Load a list of SOURCE instances from a special SOLR document.  The data will be
     * pipe-delimited, of the form:
     * <ul>
     *   <li><strong>1</strong> - source identifier</li>
     *   <li><strong>2</strong> - title</li>
     *   <li><strong>3</strong> - description</li>
     *   <li><strong>4</strong> - published, TRUE or FALSE</li>
     * </ul>
     */
    protected void loadSources() {
        Map<Integer,SourceBridge> newSources = new TreeMap<Integer,SourceBridge>();

        List<String> appData = this.getAppData(SOURCE_ID);
        if (appData.isEmpty()) {
            return;
        }

        for (String line : appData) {
            String[] values = PlaceHelper.split(line, DELIMITER);
            if (values.length >= 4) {
                int typeId = Integer.parseInt(values[0].trim());
                boolean isPub = (values[3] != null  &&  values[3].equalsIgnoreCase("true"));
                SourceBridge sourceB = new SourceImpl(null, typeId, values[1], values[2], isPub);
                newSources.put(typeId, sourceB);
            }
        }

        // Move the new source definitions to the 'sources' collection, removing any
        // sources that are no longer used
        Set<Integer> unusedKeys = new HashSet<>(allSources.keySet());
        for (Map.Entry<Integer,SourceBridge> entry : newSources.entrySet()) {
            unusedKeys.remove(entry.getKey());
            allSources.put(entry.getKey(), entry.getValue());
        }

        for (Integer unusedKey : unusedKeys) {
            allSources.remove(unusedKey);
        }
    }

    /**
     * Load the name priorities by their associated code value and name-type identifier.
     * The values in the Solr document are pipe-delimited, of the form:
     * <ul>
     *   <li><strong>1</strong> - name-type code</li>
     *   <li><strong>2</strong> - priority</li>
     * </ul>
     * The associated name-type identifier will be found in the "nameTypesByCode" map.
     */
    protected void loadNamePriorities() {
        if (namePriority == null  ||  namePriorityByCode == null) {
            namePriority = new HashMap<Integer,Integer>();
            namePriorityByCode = new HashMap<String,Integer>();
        }

        List<String> appData = this.getAppData(NAME_PRIORITY_ID);
        for (String line : appData) {
            String[] values = PlaceHelper.split(line, DELIMITER);
            if (values.length >= 2) {
                int priority = Integer.parseInt(values[1].trim());
                TypeBridge typeB = this.getType(TypeBridge.TYPE.NAME, values[0].trim(), false);
                if (typeB != null) {
                    namePriority.put(typeB.getTypeId(), priority);
                }
                namePriorityByCode.put(values[0].trim(), priority);
            }
        }
    }

    /**
     * Recursively construct the "GroupBridge" instances, including all sub-groups
     * referenced by this group.  Once created, the Group-Bridge is placed in the
     * final MAP and can be re-used by other parent groups.
     * 
     * @param groupId group identifier
     * @param grpCatMap map of group identifier --> GoupCategory value
     * @param nameAndDescr map of group --> name and description
     * @param hierData map of group --> subgroup identifiers
     * @param memberData map of group --> member identifiers, which will be place types
     * @param resultsMap final map, which is where the new values will be placed
     */
    protected void constructGroup(int groupId, Map<Integer,GroupBridge.TYPE> grpCatMap,
            Map<Integer,Map<String,String[]>> nameAndDescr, Map<Integer,Set<Integer>> hierData,
            Map<Integer,Set<Integer>> memberData, Map<Integer,GroupBridge> resultsMap) {

        // Since this is a recursive call, see if the group has previously been created
        if (! resultsMap.containsKey(groupId)) {
            Set<GroupBridge> childGroups = new HashSet<GroupBridge>();
            if (hierData.containsKey(groupId)) {
                for (Integer subGroupId : hierData.get(groupId)) {
                    constructGroup(subGroupId, grpCatMap, nameAndDescr, hierData, memberData, resultsMap);
                    childGroups.add(resultsMap.get(subGroupId));
                }
            }

            // Collect the set of direct members identifiers
            Set<Integer> memberIds = new HashSet<Integer>();
            if (memberData.containsKey(groupId)) {
                for (Integer memberId : memberData.get(groupId)) {
                    if (grpCatMap.get(groupId) == GroupBridge.TYPE.PLACE_TYPE) {
                        TypeBridge typeB = allTypes.get(memberId);
                        if (typeB == null  ||  typeB.getType() != TypeBridge.TYPE.PLACE) {
                        } else {
                            memberIds.add(memberId);
                        }
                    } else {
                        memberIds.add(memberId);
                    }
                }
            }

            // Construct and save the "GroupBridge"
            Map<String,String> names = new HashMap<>();
            Map<String,String> descr = new HashMap<>();
            Map<String,String[]> localeNameDesc = nameAndDescr.get(groupId);
            if (localeNameDesc != null) {
                for (Map.Entry<String,String[]> entry : localeNameDesc.entrySet()) {
                    String locale = entry.getKey();
                    String name   = entry.getValue()[0];
                    String desc   = entry.getValue()[1];
                    names.put(locale, name);
                    descr.put(locale, desc);
                }
            }

            // Collect the set of ALL member identifiers, which are direct identifiers
            // plus member identifiers of all sub-groups
            Set<Integer> allMemberIds = new HashSet<Integer>(memberIds);
            if (hierData.containsKey(groupId)) {
                List<Integer> subGroupIds = new ArrayList<>(hierData.get(groupId));
                while (subGroupIds.size() > 0) {
                    Integer subGroupId = subGroupIds.remove(0);
                    if (memberData.containsKey(subGroupId)) {
                        allMemberIds.addAll(memberData.get(subGroupId));
                    }
                    if (hierData.containsKey(subGroupId)) {
                        subGroupIds.addAll(hierData.get(subGroupId));
                    }
                }
            }

            // Construct the group if there is at least one name
            if (names.size() > 0) {
                GroupBridge impl = new GroupImpl(null, groupId, grpCatMap.get(groupId), names, descr, true, allMemberIds, memberIds, childGroups);
                resultsMap.put(groupId, impl);
            }
        }
    }

    /**
     * Load the association between parent group-id and its child group ids, ignoring
     * rows marked as deleted.  The rows must be pipe-delimited and valid rows consist
     * of 3 columns:
     * <ul>
     *   <li><strong>1</strong> - parent group identifier</li>
     *   <li><strong>2</strong> - child group identifier</li>
     *   <li><strong>3</strong> - deleted, "true" or "false"</li>
     * </ul>
     * 
     * Rows with fewer than four columns will be skipped.  There is no header row.
     * 
     * @param rawData list of row values
     * @return Map of parent group-id to a set of child group ids
     */
    protected Map<Integer,Set<Integer>> loadGroupHierarchy(List<String> rawData) {
        Map<Integer,Set<Integer>> results = new TreeMap<Integer,Set<Integer>>();

        for (String line : rawData) {
            String[] values = PlaceHelper.split(line, DELIMITER);
            if (values.length >= 3) {
                int parentId = Integer.parseInt(values[0].trim());
                int childId  = Integer.parseInt(values[1].trim());
                if ("false".equals(values[2])  ||  "FALSE".equals(values[2])) {
                    Set<Integer> children = results.get(parentId);
                    if (children == null) {
                        children = new HashSet<Integer>();
                        results.put(parentId, children);
                    }
                    children.add(childId);
                }
            }
        }
        
        // Done!!
        return results;
    }

    /**
     * Load the association between parent group-id and its member ids, ignoring
     * rows marked as deleted.  The rows must be pipe-delimited and valid rows consist
     * of 3 columns:
     * <ul>
     *   <li><strong>1</strong> - group identifier</li>
     *   <li><strong>2</strong> - place type identifier</li>
     *   <li><strong>3</strong> - deleted, "true" or "false"</li>
     * </ul>
     * 
     * Rows with fewer than four columns will be skipped.  There is no header row.
     * 
     * @param rawData list of row values
     * @return Map of group-id to a set of place-type identifiers
     */
    protected Map<Integer,Set<Integer>> loadGroupMembers(List<String> rawData) {
        Map<Integer,Set<Integer>> results = new TreeMap<Integer,Set<Integer>>();
        for (String line : rawData) {
            String[] values = PlaceHelper.split(line, DELIMITER);
            if (values.length >= 3) {
                int groupId = Integer.parseInt(values[0].trim());
                int typeId  = Integer.parseInt(values[1].trim());
                if ("false".equals(values[2])  ||  "FALSE".equals(values[2])) {
                    Set<Integer> types = results.get(groupId);
                    if (types == null) {
                        types = new HashSet<Integer>();
                        results.put(groupId, types);
                    }
                    types.add(typeId);
                }
            }
        }
        
        // Done!!
        return results;
    }

    /**
     * Collect all groups by their members, essentially a reverse look-up.  NOTE:
     * this can inter-mix groups of different types into a single sub-collection
     * which will have to be filtered out later on.
     * 
     * NOTE: This update is done 'in-place' so synchronization is not needed.
     */
    protected void loadMemberToGroupMapping() {
        Map<Integer,Set<GroupBridge>> newIdToGroups = new TreeMap<Integer,Set<GroupBridge>>();

        for (GroupBridge groupB : allGroups.values()) {
            Set<Integer> memberIds = groupB.getMembers();
            for (Integer memberId : memberIds) {
                Set<GroupBridge> set = newIdToGroups.get(memberId);
                if (set == null) {
                    set = new HashSet<GroupBridge>();
                    newIdToGroups.put(memberId, set);
                }
                set.add(groupB);
            }
        }

        // Move the new id-->group definitions to the 'entityIdToGroups' collection,
        // removing any groups that are no longer used
        Set<Integer> unusedKeys = new HashSet<>(entityIdToGroups.keySet());
        for (Map.Entry<Integer,Set<GroupBridge>> entry : newIdToGroups.entrySet()) {
            unusedKeys.remove(entry.getKey());
            entityIdToGroups.put(entry.getKey(), entry.getValue());
        }

        for (Integer unusedKey : unusedKeys) {
            entityIdToGroups.remove(unusedKey);
        }
    }

    /**
     * Retrieve the app-data from a specialized SOLR document, based on the document
     * identifier.  If it's the same version as we currently have, return an empty
     * set of data, which means "don't update the cache!!"
     * 
     * @param docId document ID
     * @return app-data associated app-data
     */
    protected List<String> getAppData(String fileId) {
        try {
            return Files.readAllLines(Paths.get(baseDir, fileId + ".txt"), StandardCharsets.UTF_8);
        } catch(Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Update the given set of types: first add the new type instances; then remove any
     * old types, i.e., any that are no longer referenced.
     * 
     * @param type the TypeBridge "type"
     * @param types Map of Integer key --> TypeBridge instance
     */
    protected void updateTypeCodes(TypeBridge.TYPE type, Map<Integer,TypeBridge> types) {
        // Get a list of all IDs and CODEs for existing entries of the target type
        Set<Integer> unusedIds = new HashSet<>();
        Set<String>  unusedCodes = new HashSet<>();
        for (TypeBridge typeB : allTypes.values()) {
            if (typeB.getType() == type) {
                unusedIds.add(typeB.getTypeId());
                unusedCodes.add(typeB.getCode());
            }
        }

        // Add the new types to the two Maps, indicating that they are NOT unused
        for (TypeBridge typeB : types.values()) {
            allTypes.put(typeB.getTypeId(), typeB);
            allTypesByCode.put(typeB.getCode(), typeB);
            unusedIds.remove(typeB.getTypeId());
            unusedCodes.remove(typeB.getCode());
        }

        // Remove codes that no longer are used
        for (Integer unusedId : unusedIds) {
            allTypes.remove(unusedId);
        }
        for (String unusedCode : unusedCodes) {
            allTypesByCode.remove(unusedCode);
        }
    }

}
