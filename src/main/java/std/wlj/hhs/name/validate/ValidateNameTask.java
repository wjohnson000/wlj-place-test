/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name.validate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.familysearch.homelands.admin.parser.helper.JsonUtility;
import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.admin.parser.name.NameParser;

import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.hhs.model.HtmlFormatter;
import std.wlj.hhs.model.NameResource;
import std.wlj.hhs.model.NameVariant;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class ValidateNameTask {


    static String DEV_URL  = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org/";
    static String PROD_URL = "http://core.homelands.service.prod.us-east-1.prod.fslocal.org/";

    static String DATA_PATH = "C:/D-drive/homelands/names/final/";

    private String prodCollection;
    private String devCollection;

    public ValidateNameTask(String prodCollection, String devCollection) {
        this.prodCollection = prodCollection;
        this.devCollection  = devCollection;
    }

    /**
     * Compare name definitions in PROD against those from DEV
     * 
     * @param parser parser for interpreting input file
     * @param inputFile name of the input file
     * @param type name type (FIRST or LAST)
     * @param language language of the names
     * @param count number of names to compares
     * @throws Exception
     */
    public void compareProdVsDev(NameParser parser, String inputFile, String type, String language, int count) throws Exception {
        byte[]     input  = Files.readAllBytes(Paths.get(DATA_PATH, inputFile));
        Map<String, List<NameModel>> rawNames = parser.parse(input);
        Map<String, NameModel> bestNames = parser.generateBestDefinition(rawNames);

        choose(bestNames.values(), count).stream()
                                       .forEach(model -> compareName(model, type, language));

    }

    List<NameModel> choose(Collection<NameModel> domain, int howMany) {
        List<NameModel> chosenNames = new ArrayList<>(domain);

        Random random = new Random();
        while (chosenNames.size() > howMany) {
            int ndx = random.nextInt(chosenNames.size());
            chosenNames.remove(ndx);
        }

        return chosenNames;
    }

    void compareName(NameModel model, String type, String language) {
        String name = model.getText();
        String nameNoSpace = name.replaceAll(" ", "%20");
        String pathExtra = "name?text=" + nameNoSpace + "&type=" + type + "&collection=";

        Map<String, String> headers = Collections.singletonMap("Accept-Language", language);

        String jsonPrd = HttpClientX.doGetJSON(PROD_URL + pathExtra + prodCollection, headers);
        String jsonDev = HttpClientX.doGetJSON(DEV_URL + pathExtra + devCollection, headers);

        System.out.println("\n==================================================================");
        System.out.println("NAME: " + model.getText());
        if (jsonDev == null  &&  jsonPrd == null) {
            System.out.println("  >>>>>>>> Missing from both systems ...");
        } else if (jsonDev == null) {
            System.out.println("  >>>>>>>> Missing from DEV ...");
        } else if (jsonPrd == null) {
            System.out.println("  >>>>>>>> Missing from PROD ...");
        } else {
            try {
                JsonNode nodePrd = JsonUtility.parseJson(jsonPrd);
                JsonNode nodeDev = JsonUtility.parseJson(jsonDev);
                NameResource namePrd = JsonUtility.createObject(nodePrd, NameResource.class);
                NameResource nameDev = JsonUtility.createObject(nodeDev, NameResource.class);

                compareDefinition(model, namePrd, nameDev);
                compareVariants(model, namePrd, nameDev);
            } catch(Exception ex) {
                System.out.println("Unable to parse JSON: " + ex.getMessage());
            }
        }
    }

    void compareDefinition(NameModel model, NameResource namePrd, NameResource nameDev) {
        String prdDef = HtmlFormatter.format(namePrd.getDefinition());
        String devDef = HtmlFormatter.format(nameDev.getDefinition());

        if (prdDef.equals(devDef)) {
//            System.out.println("  >> Definitions match!");
        } else  if (prdDef.length() > devDef.length()) {
            System.out.println("  >> Definition mismatch -- PROD is longer");
        } else if (devDef.length() > prdDef.length()) {
            System.out.println("  >> Definition mismatch -- DEV is longer");
        } else {
            System.out.println("  >> Definitions lengths match!");
        }
    };

    void compareVariants(NameModel model, NameResource nodePrd, NameResource nodeDev) {
        Map<String, List<NameVariant>> prdVar = nodePrd.getVariants();
        Map<String, List<NameVariant>> devVar = nodeDev.getVariants();

        if (! prdVar.isEmpty()  &&  devVar.isEmpty()) {
            System.out.println("  >> Variants mismatch -- PROD has, DEV doesn't have");
        } else if (prdVar.isEmpty()  &&  ! devVar.isEmpty()) {
            System.out.println("  >> Variants mismatch -- PROD doesn't have, DEV has");
        } else if (! prdVar.isEmpty()  &&  ! devVar.isEmpty()) {
            int prdTotal = 0;
            int devTotal = 0;
            Set<String> varTypes = new TreeSet<>();
            varTypes.addAll(prdVar.keySet());
            varTypes.addAll(devVar.keySet());
            for (String varType : varTypes) {
                int prdCnt = prdVar.getOrDefault(varType, Collections.emptyList()).size();
                int devCnt = devVar.getOrDefault(varType, Collections.emptyList()).size();
                System.out.println("  >> [" + varType + "]: " + prdCnt + " .vs. " + devCnt);
                prdTotal += prdCnt;
                devTotal += devCnt;
            }
            System.out.println(" >> Prd.count total: " + prdTotal);
            System.out.println(" >> Dev.count total: " + devTotal);
            System.out.println(" >> Mdl.count total: " + (model.getVariants().size() + model.getReferences().size()));
        } else if (! model.getVariants().isEmpty()) {
            System.out.println(" >>>>>>>> Missing Variants!!");
        }
    }
}
