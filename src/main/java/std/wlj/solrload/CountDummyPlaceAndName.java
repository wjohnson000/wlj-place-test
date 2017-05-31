package std.wlj.solrload;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.familysearch.standards.place.db.util.FileResultSet;

public class CountDummyPlaceAndName {
	public static void main(String... args) throws Exception {

		String DELIMITER = "\\|";
	    String FILE_PLACE_MAIN = "place-main.txt";
	    String FILE_PLACE_NAME = "place-name.txt";
		File parentDir = new File("C:/temp/place-extract/wlj-one");

		/** FileResultSet instances -- yep, eight of 'em ... */
	    FileResultSet  placeRS = null;
	    FileResultSet  placeNameRS = null;

        File aFile = new File(parentDir, FILE_PLACE_MAIN);
        placeRS = new FileResultSet();
        placeRS.setSeparator(DELIMITER);
        placeRS.openFile(aFile);

        aFile = new File(parentDir, FILE_PLACE_NAME);
        placeNameRS = new FileResultSet();
        placeNameRS.setSeparator(DELIMITER);
        placeNameRS.openFile(aFile);

        // Get some PLACE stats
        Set<Integer> placeIds = new HashSet<>();
        while (placeRS.next()) {
        	int repId = placeRS.getInt("rep_id");
        	int placeId = placeRS.getInt("place_id");
        	if (repId == 0) {
        		if (placeIds.contains(placeId)) {
        			System.out.println("Duplicate 'Dummy' place: " + placeId);
        		}
        		placeIds.add(placeId);
        	}
        }
        System.out.println("'Dummy' place count: " + placeIds.size());

        // Get some NAME stats
        int nameCnt = 0;
        while (placeNameRS.next()) {
        	int repId = placeNameRS.getInt("rep_id");
        	int placeId = placeNameRS.getInt("place_id");
        	if (repId == 0) {
        		nameCnt++;
        		if (! placeIds.contains(placeId)) {
        			int nameId = placeNameRS.getInt("name_id");
        			System.out.println("OUCH -- Name with no place: " + placeId + " --> " + nameId);
        		}
        	}
        }
        System.out.println("'Dummy' place name count: " + nameCnt);
        
        placeRS.close();
        placeNameRS.close();
	}
}
