package std.wlj.dbnew;

import java.util.*;

import javax.sql.DataSource;

import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.service.DbReadableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class creates three places in a hierarchy, and five place-reps, one each for 
 * the first two levels, three for the last level.  We can run updates and deletes
 * for each one ...
 * 
 * To clean up the goo, run the following SQL:
 *    SET SCHEMA 'sams_place';
 * 
 *    DELETE FROM place_name WHERE name_id >= 1000000;
 *    DELETE FROM rep_display_name WHERE rep_id >= 1000000;
 *    DELETE FROM place_rep WHERE rep_id >= 1000000;
 *    DELETE FROM place WHERE place_id >= 1000000;
 * 
 *    ALTER SEQUENCE seq_place RESTART WITH 1000000;
 *    ALTER SEQUENCE seq_place_name RESTART WITH 1000000;
 *    ALTER SEQUENCE seq_place_rep RESTART WITH 1000000;
 * 
 * @author wjohnson000
 *
 */
public class TestWritableService {

    private static DataSource ds;
    private static DbReadableService dataService;
    private static Random random = new Random();


    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context.xml");
        ds = (DataSource)appContext.getBean("dataSource");
        dataService = new DbReadableService(ds);

        createPlacesAndReps();

        ((ClassPathXmlApplicationContext)appContext).close();
        System.exit(0);
    }

    /**
     * Read place-reps by place [parent] id
     */
    private static void createPlacesAndReps() {
        long nnow = System.currentTimeMillis();

        try {
            PlaceRepresentationDTO placeRep01  = dataService.create(getQuillyPlace(), getQuillyPlaceRep(new int[] { }), "wjohnson000");
            PlaceDTO place01 = dataService.getPlaceById(placeRep01.getOwnerId(), null);

            PlaceRepresentationDTO placeRep02  = dataService.create(getQuallyPlace(), getQuallyPlaceRep(placeRep01.getJurisdictionChain()), "wjohnson000");
            PlaceDTO place02 = dataService.getPlaceById(placeRep02.getOwnerId(), null);

            PlaceRepresentationDTO placeRep03a = dataService.create(getNerfPlace(), getNerfPlaceRep01(placeRep02.getJurisdictionChain()), "wjohnson000");
            PlaceDTO place03 = dataService.getPlaceById(placeRep03a.getOwnerId(), null);

            PlaceRepresentationDTO placeRep03b = dataService.create(getNerfPlaceRep02(placeRep02.getJurisdictionChain(), place03.getId()), "wjohnson000");
            PlaceRepresentationDTO placeRep03c = dataService.create(getNerfPlaceRep03(placeRep02.getJurisdictionChain(), place03.getId()), "wjohnson000");

            PlaceRepresentationDTO placeRep01R = dataService.getPlaceRepresentationById(placeRep01.getId(), null);
            PlaceRepresentationDTO placeRep02R = dataService.getPlaceRepresentationById(placeRep02.getId(), null);
            PlaceRepresentationDTO placeRep03aR = dataService.getPlaceRepresentationById(placeRep03a.getId(), null);
            PlaceRepresentationDTO placeRep03bR = dataService.getPlaceRepresentationById(placeRep03b.getId(), null);
            PlaceRepresentationDTO placeRep03cR = dataService.getPlaceRepresentationById(placeRep03c.getId(), null);

            // Update a place
            PlaceDTO place01U = dataService.update(getQuillyPlaceUpdate(place01.getId()), "wjohnson999");
            PlaceDTO place01R = dataService.getPlaceById(place01U.getId(), null);

            // Update a place-rep
            PlaceRepresentationDTO placeRep03aU = dataService.update(getNerfPlaceRep01Update(placeRep03a.getJurisdictionChain()), "wjohnson999");
            PlaceRepresentationDTO placeRep03aUR = dataService.getPlaceRepresentationById(placeRep03a.getId(), null);

            // Retrieve children of PlaceRep01, anyone that has Place01 as the owner
            List<PlaceRepresentationDTO> owner01 = dataService.getPlaceRepresentationsByPlaceId(place01U.getId(), null, true);
            List<PlaceRepresentationDTO> child01 = dataService.getChildren(placeRep01R.getId(), null);

            // Delete a place-rep
            dataService.delete(placeRep03a, placeRep03b.getId(), "wjohnson777");
            PlaceRepresentationDTO placeRep03aRR = dataService.getPlaceRepresentationById(placeRep03a.getId(), null);
            PlaceRepresentationDTO placeRep03bRR = dataService.getPlaceRepresentationById(placeRep03b.getId(), null);

            List<PlaceRepresentationDTO> owner01X = dataService.getPlaceRepresentationsByPlaceId(place01U.getId(), null, true);
            List<PlaceRepresentationDTO> child01X = dataService.getChildren(placeRep01R.getId(), null);

            System.out.println("Place01: Created, Read, Updated, Read, [Deleted] ...");
            output(place01);
            output(place01U);
            output(place01R);

            System.out.println("\nPlace02: Created, Read, Target of Update ...");
            output(place02);

            System.out.println("\nPlace03: Created ...");
            output(place03);

            System.out.println("\nRep01: Created, Read ...");
            output(placeRep01);
            output(placeRep01R);

            System.out.println("\nRep02: Created, Read ...");
            output(placeRep02);
            output(placeRep02R);

            System.out.println("\nRep0a3: Created, Read, Updated, Read ...");
            output(placeRep03a);
            output(placeRep03aR);
            output(placeRep03aU);
            output(placeRep03aUR);
            output(placeRep03aRR);

            System.out.println("\nRep03b: Created, Read ...");
            output(placeRep03b);
            output(placeRep03bR);
            output(placeRep03bRR);

            System.out.println("\nRep03c: Created, Read ...");
            output(placeRep03c);
            output(placeRep03cR);

            System.out.println("\nOwner01: Before ...");
            for (PlaceRepresentationDTO placeRep : owner01) {
                output(placeRep);
            }

            System.out.println("\nOwner01: After ...");
            for (PlaceRepresentationDTO placeRep : owner01X) {
                output(placeRep);
            }

            System.out.println("\nChild01: Before ...");
            for (PlaceRepresentationDTO placeRep : child01) {
                output(placeRep);
            }

            System.out.println("\nChild01: After ...");
            for (PlaceRepresentationDTO placeRep : child01X) {
                output(placeRep);
            }
        } catch (PlaceDataException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Time: " + (System.currentTimeMillis()-nnow));
        System.exit(0);
    }

    /**
     * Create "Quilly", the top-level place in our neat-o new tree
     * @return
     */
    private static PlaceDTO getQuillyPlace() {
        PlaceDTO aPlace = new PlaceDTO(
            -1,
            makePlaceNames("en", "Quilly", "t", "en", "QuillyX", "f", "de", "DE-Quilly", "f", "fr", "FR-Quilly", "f", "es", "es-Quilly", "f"),
            1900,
            null,
            0,
            null
        );
        return aPlace;
    }

    /**
     * Create an updated "Quilly", the top-level place in our neat-o new tree
     * @return
     */
    private static PlaceDTO getQuillyPlaceUpdate(int placeId) {
        PlaceDTO aPlace = new PlaceDTO(
            placeId,
            makePlaceNames("en", "Quilly", "t", "en", "QuillyX", "f", "de", "DE-Quilly", "f", "fr", "FR-Quilly", "t", "dk", "dk-Quilly", "f"),
            1925,
            null,
            0,
            null
        );
        return aPlace;
    }

    /**
     * Create "Qually", the level-2 place in our neat-o new tree
     * @return
     */
    private static PlaceDTO getQuallyPlace() {
        PlaceDTO aPlace = new PlaceDTO(
            -1,
            makePlaceNames("en", "Qually", "t", "en", "QuallyX", "f", "de", "DE-Qually", "f", "fr", "FR-Qually", "f", "es", "es-Qually", "f"),
            1900,
            null,
            0,
            null
        );
        return aPlace;
    }

    /**
     * Create "Nerf", the level-3 place in our neat-o new tree
     * @return
     */
    private static PlaceDTO getNerfPlace() {
        PlaceDTO aPlace = new PlaceDTO(
            -1,
            makePlaceNames("en", "Nerf", "f", "en", "NerfX", "f", "de", "DE-Nerf", "f", "fr", "FR-Nerf", "f", "es", "es-Nerf", "f"),
            1900,
            null,
            0,
            null
        );
        return aPlace;
    }

    /**
     * Create "Quilly" place-rep
     * 
     * @param jurisChain jurisdictionChain
     * @return
     */
    private static PlaceRepresentationDTO getQuillyPlaceRep(int[] jurisChain) {
        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            jurisChain,
            -1,
            1900,
            null,
            random.nextInt(963)+1,
            "en",
            makeRepNames("en", "Quilly", "fr", "FR-Quilly", "de", "DE-Quilly"),
            40.0,
            -111.1,
            true,
            true,
            -1,
            UUID.randomUUID().toString(),
            null,
            null
        );
        return aPlaceRep;
    }

    /**
     * Create "Qually" place-rep
     * 
     * @param jurisChain jurisdictionChain
     * @return
     */
    private static PlaceRepresentationDTO getQuallyPlaceRep(int[] jurisChain) {
        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            jurisChain,
            -1,
            1900,
            null,
            random.nextInt(963)+1,
            "en",
            makeRepNames("en", "Qually", "fr", "FR-Qually", "de", "DE-Qually"),
            40.0,
            -111.2,
            true,
            true,
            -1,
            UUID.randomUUID().toString(),
            null,
            null
        );
        return aPlaceRep;
    }

    /**
     * Create "Nerf" place-rep, first instantiation
     * 
     * @param jurisChain jurisdictionChain
     * @return
     */
    private static PlaceRepresentationDTO getNerfPlaceRep01Update(int[] jurisChain) {
        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            jurisChain,
            9999,
            1910,
            1925,
            random.nextInt(963)+1,
            "en",
            makeRepNames("en", "Nerf Territory", "fr", "FR-Nerf-Terr", "dk", "DK-Nerf-Terr"),
            40.4,
            -111.4,
            true,
            true,
            -1,
            String.valueOf(UUID.randomUUID()),
            null,
            null
        );
        return aPlaceRep;
    }

    /**
     * Create "Nerf" place-rep, first instantiation
     * 
     * @param jurisChain jurisdictionChain
     * @return
     */
    private static PlaceRepresentationDTO getNerfPlaceRep01(int[] jurisChain) {
        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            jurisChain,
            -1,
            1900,
            1925,
            random.nextInt(963)+1,
            "en",
            makeRepNames("en", "Nerf Territory", "fr", "FR-Nerf-Terr", "de", "DE-Nerf-Terr"),
            40.0,
            -111.3,
            true,
            true,
            -1,
            String.valueOf(UUID.randomUUID()),
            null,
            null
        );
        return aPlaceRep;
    }

    /**
     * Create "Nerf" place-rep, first instantiation
     * 
     * @param jurisChain jurisdictionChain
     * @return
     */
    private static PlaceRepresentationDTO getNerfPlaceRep02(int[] jurisChain, int ownerId) {
        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            jurisChain,
            ownerId,
            1925,
            1968,
            random.nextInt(963)+1,
            "en",
            makeRepNames("en", "Nerf Province", "fr", "FR-Nerf-Prov", "de", "DE-Nerf-Prov"),
            40.0,
            -111.3,
            true,
            true,
            -1,
            UUID.randomUUID().toString(),
            null,
            null
        );
        return aPlaceRep;
    }

    /**
     * Create "Nerf" place-rep, first instantiation
     * 
     * @param jurisChain jurisdictionChain
     * @return
     */
    private static PlaceRepresentationDTO getNerfPlaceRep03(int[] jurisChain, int ownerId) {
        PlaceRepresentationDTO aPlaceRep = new PlaceRepresentationDTO(
            jurisChain,
            ownerId,
            1968,
            null,
            random.nextInt(963)+1,
            "en",
            makeRepNames("en", "Nerf State", "fr", "FR-Nerf-State", "de", "DE-Nerf-State"),
            40.0,
            -111.3,
            true,
            true,
            -1,
            UUID.randomUUID().toString(),
            null,
            null
        );
        return aPlaceRep;
    }

    /**
     * Make a bunch of place-names from locale/text/common triplets
     * @param values locale1, text1, is-common1, locale2, text2, is-common2...
     * @return set of PlaceNameDTO instances
     */
    private static List<PlaceNameDTO> makePlaceNames(String... values) {
        List<PlaceNameDTO> names = new ArrayList<PlaceNameDTO>();

        for (int i=0;  i<values.length;  i+=3) {
            PlaceNameDTO nameDTO = new PlaceNameDTO(-1*i, values[i+1], values[i], random.nextInt(21)+1040);
            names.add(nameDTO);
        }

        return names;
    }

    /**
     * Make a bunch of rep-names from locale/text pairs
     * @param values locale1, text1, locale2, text2, ...
     * @return map of locale -> text
     */
    private static Map<String,String> makeRepNames(String... values) {
        Map<String,String> names = new HashMap<String,String>();

        for (int i=0;  i<values.length;  i+=2) {
            names.put(values[i], values[i+1]);
        }

        return names;
    }

    /**
     * Dump out the details of a Place in a sorta' nice format
     * @param placeDTO
     */
    private static void output(PlaceDTO placeDTO) {
        if (placeDTO == null) {
            System.out.println("PLACE: " + null);
        } else {
            System.out.println("PLACE: " + placeDTO.getId() + "|" + placeDTO.getRevision() + "|" +
                    placeDTO.getStartYear() + "|" + placeDTO.getEndYear());
            for (PlaceNameDTO nameDTO : placeDTO.getVariants()) {
                System.out.println("       " + nameDTO.getId() + "|" + nameDTO.getType() + "|" +
                    nameDTO.getName().getLocale() + "|" + nameDTO.getName().get());
            }
        }
    }

    /**
     * Dump out the details of a Place-Rep in a sorta' nice format
     * @param placeRepDTO
     */
    private static void output(PlaceRepresentationDTO placeRepDTO) {
        if (placeRepDTO == null) {
            System.out.println("REP: " + null);
        } else {
            System.out.println("REP: " + niceJurisdiction(placeRepDTO.getJurisdictionChain()) + "|" +
                placeRepDTO.getRevision() + "|" + placeRepDTO.getOwnerId() + "|" + placeRepDTO.getPreferredLocale() + "|" +
                placeRepDTO.getType() + "|" + placeRepDTO.getLatitude() + "|" + placeRepDTO.getLongitude() + "|" +
                placeRepDTO.getJurisdictionFromYear() + "|" + placeRepDTO.getJurisdictionToYear() + "|" + placeRepDTO.isPublished() + "|" +
                placeRepDTO.isValidated() + "|" + placeRepDTO.getUUID() + "|" + placeRepDTO.getTypeGroup());
            for (Map.Entry<String,String> entry : placeRepDTO.getDisplayNames().entrySet()) {
                System.out.println("     " + entry.getKey() + "|" + entry.getValue());
            }
        }
    }

    /**
     * Collect the jurisdiction chain ... 
     * @param jurisChain jurisdiction chain
     * @return
     */
    private static String niceJurisdiction(int[] jurisChain) {
        StringBuilder buff = new StringBuilder();
        for (int id : jurisChain) {
            if (buff.length() > 0) {
                buff.append(",");
            }
            buff.append(id);
        }
        return buff.toString();
    }
}
