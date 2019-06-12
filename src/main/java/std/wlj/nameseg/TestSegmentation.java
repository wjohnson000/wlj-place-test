/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.nameseg;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.name.segmentation.Name;
import org.familysearch.standards.name.segmentation.NameIdException;
import org.familysearch.standards.name.segmentation.NameSegmentationAPI;
import org.familysearch.standards.name.segmentation.SegmentationRequest;
import org.familysearch.standards.name.segmentation.SegmentationResponse;

public class TestSegmentation {

    static final String[] names = {
        "Dr. Wayne Johnson",
        "Mr. Wayne Johnson",
        "fuyu Wayne Johnson",
        "Alexander Hamilton III",
        "Fernando Valenzuela",
        "Juan Jose Alvarez-Dominguez",
        "de Gonzaga y San Mauricio",
        "Fernandez de San Salvador y Montiel",
        "Sabaudo De Freitas E Silva",
    };

    public static void main(String...args) throws NameIdException {
        NameSegmentationAPI nsAPI = new NameSegmentationAPI();

        for (String name : names) {
            long time0 = System.nanoTime();
            SegmentationResponse response = nsAPI.process(getRequest(name, StdLocale.ENGLISH));
            long time1 = System.nanoTime();
            System.out.println("\n\nName: " + name + " --> " + (time1 - time0) / 1_000_000.0);
            dumpResponse(response);

            time0 = System.nanoTime();
            response = nsAPI.process(getRequest(name, StdLocale.SPANISH));
            time1 = System.nanoTime();
            System.out.println("\n\nName: " + name + " --> " + (time1 - time0) / 1_000_000.0);
            dumpResponse(response);
        }
    }

    static SegmentationRequest getRequest(String name, StdLocale locale) {
        SegmentationRequest segRequest = new SegmentationRequest();

        Name.Builder builder = new Name.Builder();
        builder.fullname(name, locale);
        builder.locale(locale);
        builder.targetLocale(locale);

        segRequest.setName(builder.build());

        return segRequest;
    }

    static void dumpResponse(SegmentationResponse response) {
        System.out.println("RESP: " + response.getMetaData());
        System.out.println(" nam: " + response.getName());
        System.out.println(" ful: " + response.getName().getFullname().get());
        if (response.getName().getGiven() != null) {
            System.out.println(" gvn: " + response.getName().getGiven().getText().get());
        }
        if (response.getName().getSurname() != null) {
            System.out.println(" sur: " + response.getName().getSurname().getText().get());
        }
        if (response.getName().getPrefix() != null) {
            System.out.println(" pfx: " + response.getName().getPrefix().getText().get());
        }
        if (response.getName().getSuffix() != null) {
            System.out.println(" sfx: " + response.getName().getSuffix().getText().get());
        }
    }
}
