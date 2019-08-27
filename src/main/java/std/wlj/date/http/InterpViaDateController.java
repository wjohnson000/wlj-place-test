/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.http;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

import org.familysearch.standards.date.api.DateService;
import org.familysearch.standards.date.api.DateServiceImpl;
import org.familysearch.standards.date.ws.controller.DatesController;
import org.familysearch.standards.date.ws.model.Dates;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * @author wjohnson000
 *
 */
public class InterpViaDateController {

    public static void main(String...args) throws Exception {
        System.setProperty("max.interp.time.millis", "10000");
        DateService service = new DateServiceImpl();
        DatesController controller = new DatesController();

        Field dateService = controller.getClass().getDeclaredField("dateService");
        dateService.setAccessible(true);
        dateService.set(controller, service);

        interpDate(controller, "12 years 10 months");
        interpDate(controller, "12 yr 10 mo to 14 yr 11 mo 12 days");
    }

    static void interpDate(DatesController controller, String text) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<Dates> response = controller.getDatesInterp(
                headers,
                mock(HttpServletRequest.class),
                text,
                null,
                null,
                null,
                null,
                null,
                null,
                "legacy",
                false,
                "en",
                null);

        Dates dates = response.getBody();
        System.out.println("\n==========================================================================");
        System.out.println(" Text: " + text);
        System.out.println(" Date: " + dates.getDates().get(0));
        System.out.println(" Orig: " + dates.getDates().get(0).getOriginal());
        System.out.println(" Gedx: " + dates.getDates().get(0).getGedcomx());
        System.out.println(" Lang: " + dates.getDates().get(0).getLocalizedDate().getLang());
        System.out.println(" Valu: " + dates.getDates().get(0).getLocalizedDate().getValue());

    }
}
