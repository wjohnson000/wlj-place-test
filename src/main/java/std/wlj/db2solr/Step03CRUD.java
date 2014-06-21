package std.wlj.db2solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceDTO;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceNameDTO;
import org.familysearch.standards.place.data.PlaceRepresentationDTO;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbDataService;

import std.wlj.util.DbManager;
import std.wlj.util.SolrManager;

/**
 * Create, Update and Delete lots of stuff ...
 * 
 * @author wjohnson000
 *
 */
public class Step03CRUD {

    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) {
        DbDataService dbService = DbManager.getLocal();
        SolrDataService solrService = SolrManager.getLocalHttp();
        dataService = new PlaceDataServiceImpl(dbService, solrService);

        try {
            createAustralia();
            createBloomington();

            editPlaceRep(5, "eu");
            editPlaceRep(12, "eu");
            editPlaceRep(16, "eu");
            editPlaceRep(19, "eu");

            createAndEditHorsens();

            editProvoPlace();

            editPlaceRep(5, "sl");
            editPlaceRep(12, "sl");
            editPlaceRep(16, "sl");
            editPlaceRep(19, "sl");

            createAndDeleteProvo();

            editPlaceRep(5, "xh");
            editPlaceRep(12, "xh");
            editPlaceRep(16, "xh");
            editPlaceRep(19, "xh");
            
        } catch(PlaceDataException ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }

        dataService.shutdown();
        DbManager.closeAppContext();

        System.exit(0);
    }

    /**
     * Edit a place-rep
     * 
     * @param repId place-rep identifier
     * @param locale a locale for a new name
     * @param iter iteration number, which will control some data
     * @throws PlaceDataException if something bad happens
     */
    private static void editPlaceRep(int repId, String locale) throws PlaceDataException {
        PlaceRepresentationDTO placeRep = dataService.getPlaceRepresentationById(repId, null);

        Double lattd = placeRep.getLatitude();
        Double longt = placeRep.getLongitude();
        if (lattd != null) lattd += 1;
        if (longt != null) longt -= 1;

        Map<String,String> names = placeRep.getDisplayNames();
        String enName = names.get("en");
        if (enName != null) {
            names.put(locale, enName+"." + locale);
            names.put("en", enName+".x");
        }

        PlaceRepresentationDTO updPlaceRep = new PlaceRepresentationDTO(
            placeRep.getJurisdictionChain(),
            placeRep.getOwnerId(),
            placeRep.getFromYear(),
            placeRep.getToYear(),
            placeRep.getType(),
            placeRep.getPreferredLocale(),
            names,
            lattd,
            longt,
            placeRep.isPublished(),
            placeRep.isValidated(),
            placeRep.getRevision(),
            placeRep.getUUID(),
            placeRep.getTypeGroup(),
            placeRep.getCreatedUsingVersion());

        dataService.update(updPlaceRep, wlj);
    }

    /**
     * Create a new "Australia" place and place-rep.
     */
    private static void createAustralia() throws PlaceDataException {
        List<PlaceNameDTO> variants = new ArrayList<PlaceNameDTO>();
        variants.add(makeNameDTO("en", "australia", 458));
        variants.add(makeNameDTO("bi-Latn", "Ostrelia", 458));
        variants.add(makeNameDTO("fr", "Australie", 458));
        variants.add(makeNameDTO("fo", "Avstralia", 437));
        variants.add(makeNameDTO("rn", "Ostraliya", 437));
        variants.add(makeNameDTO("tt-Latn", "Awstraliä", 440));
        variants.add(makeNameDTO("en", "AUSTRAILA", 454));

        PlaceDTO aPlace = new PlaceDTO(0, variants, 1700, null, 0, null);

        Map<String,String> names = new HashMap<>();
        names.put("en", "Australia");
        names.put("es", "Australia");
        names.put("it", "Australia");
        names.put("pl", "Australia");
        names.put("ro", "Australia");
        names.put("sw-Latn", "Australia");

        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            new int[] { },
            0,
            1700,
            null,
            198,
            "en",
            names,
            -25.0,
            135.0,
            true,
            true,
            0,
            null,
            null,
            null);

        dataService.create(aPlace, aPlaceRep, wlj);
    }

    /**
     * Create a new Bloomington ...
     * @throws PlaceDataException
     */
    private static void createBloomington() throws PlaceDataException {
        Map<String,String> names = new HashMap<>();
        names.put("en", "Bloomington");
        names.put("de", "Bloomingdorp");

        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            new int[] { 3 },
            4,
            1820,
            null,
            186,
            "en",
            names,
            -86.526,
            39.162,
            true,
            true,
            0,
            null,
            null,
            null);

        dataService.create(aPlaceRep, wlj);
    }

    /**
     * Create a new "Horsens" place and place-rep.  Edit the place.  Edit the place-rep.
     */
    private static void createAndEditHorsens() throws PlaceDataException {
        List<PlaceNameDTO> variants = new ArrayList<PlaceNameDTO>();
        variants.add(makeNameDTO("en", "Horsens", 458));
        variants.add(makeNameDTO("da", "Horsens", 434));
        variants.add(makeNameDTO("da", "Horsens Kommune", 437));
        variants.add(makeNameDTO("da", "Horsens Sogn", 454));

        PlaceDTO aPlace = new PlaceDTO(0, variants, 1700, null, 0, null);

        Map<String,String> names = new HashMap<>();
        names.put("en", "Horsens");
        names.put("da", "Horsens");

        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            new int[] { 20 },
            0,
            1600,
            null,
            201,
            "da",
            names,
            9.866667,
            55.866667,
            true,
            true,
            0,
            null,
            null,
            null);

        PlaceRepresentationDTO newPlaceRep = dataService.create(aPlace, aPlaceRep, wlj);

        // Get the current place, modify the start-data, save it
        PlaceDTO newPlace = dataService.getPlaceById(newPlaceRep.getOwnerId(), null);
        PlaceDTO updPlace = new PlaceDTO(newPlace.getId(), newPlace.getVariants(), newPlace.getStartYear()-100, newPlace.getEndYear(), 0, null);
        dataService.update(updPlace, wlj);

        // Modify the place-rep, save it
        Double lattd = newPlaceRep.getLatitude();
        Double longt = newPlaceRep.getLongitude();
        if (lattd != null) lattd += 1;
        if (longt != null) longt -= 1;

        names = newPlaceRep.getDisplayNames();
        String enName = names.get("en");
        if (enName != null) {
            names.put("eu", enName+".eu");
        }

        PlaceRepresentationDTO updPlaceRep = new PlaceRepresentationDTO(
            newPlaceRep.getJurisdictionChain(),
            newPlaceRep.getOwnerId(),
            newPlaceRep.getFromYear(),
            newPlaceRep.getToYear(),
            newPlaceRep.getType(),
            newPlaceRep.getPreferredLocale(),
            names,
            lattd,
            longt,
            newPlaceRep.isPublished(),
            newPlaceRep.isValidated(),
            newPlaceRep.getRevision(),
            newPlaceRep.getUUID(),
            newPlaceRep.getTypeGroup(),
            newPlaceRep.getCreatedUsingVersion());

            dataService.update(updPlaceRep, wlj);
    }

    /**
     * Modify "Provo" place, by changing the start-year just a tad ...
     * 
     * @throws PlaceDataException
     */
    private static void editProvoPlace() throws PlaceDataException {
     // Get the current place, modify the start-data, save it
        PlaceDTO aPlace = dataService.getPlaceById(8, null);
        PlaceDTO updPlace = new PlaceDTO(aPlace.getId(), aPlace.getVariants(), 1850, aPlace.getEndYear(), 0, null);
        dataService.update(updPlace, wlj);
    }

    /**
     * Create a new "Provo" place and place-rep.  Delete the place-rep, which should
     * delete the place.
     */
    private static void createAndDeleteProvo() throws PlaceDataException {
        PlaceDTO oldPlace = dataService.getPlaceById(8, null);
        PlaceRepresentationDTO oldPlaceRep = dataService.getPlaceRepresentationById(10, null);

        PlaceRepresentationDTO newPlaceRep = dataService.create(oldPlace, oldPlaceRep, wlj);
//        PlaceDTO newPlace = dataService.getPlaceById(newPlaceRep.getOwnerId(), null);

        dataService.delete(newPlaceRep, 8, wlj);
    }

    /**
     * Construct a new PlaceNameDTO instance.
     * 
     * @param locale locale
     * @param name name
     * @param nameType name type
     * @return new PlaceNameDTO
     */
    private static PlaceNameDTO makeNameDTO(String locale, String name, int nameType) {
        return new PlaceNameDTO(0, name, locale, nameType);
    }
}
