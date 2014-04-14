package org.familysearch.std.wlj.access;

import java.util.*;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbDataService;


public class CreatePlaceAndRep {
    public static void main(String[] args) throws PlaceDataException {
        SolrDataService solrService = new SolrDataService();
        DbDataService dbService = new DbDataService();

        PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

        // Create the name variants and the associated place
        List<PlaceNameDTO> variants = new ArrayList<PlaceNameDTO>();
        variants.add(new PlaceNameDTO(0, "Wayne House", "en", 1038));
        variants.add(new PlaceNameDTO(0, "Wayne Home", "en", 1042));
        variants.add(new PlaceNameDTO(0, "Wayne Abode", "en", 1049));
        variants.add(new PlaceNameDTO(0, "Wayne Maison", "fr", 1038));
        variants.add(new PlaceNameDTO(0, "Wayne Uchi", "ja", 1038));

        PlaceDTO newPlace = new PlaceDTO(0, variants, 1800, 2020, 0, null);

        // Create the display names and the place-representation
        Map<String,String> names = new HashMap<String,String>();
        names.put("en", "Wayne House");
        names.put("fr", "Wayne Maison");
        names.put("ja", "Wayne Uchi");

        PlaceRepresentationDTO newRep = new PlaceRepresentationDTO(
            new int[] { 0 },
            0,
            1800,
            2020,
            502,
            "en",
            names,
            -44.444,
            55.555,
            true,
            true,
            0,
            null,
            null,
            null);

        PlaceRepresentationDTO createdRep = service.create(newPlace, newRep, "wjohnson000");
        System.out.println("C-REP: " + createdRep);

        service.shutdown();
    }
}
