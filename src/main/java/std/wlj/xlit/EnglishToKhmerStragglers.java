package std.wlj.xlit;

import java.io.IOException;

public class EnglishToKhmerStragglers {

    static String[] candidateNames = {
//        "Cedric",
//        "Celia",
//        "Cecilia",
//        "Grace",
//        "Vincent",
//        "Spencer",
//        "Cesar",
//        "Caeser",
//        "Lawrence",

//        "Wendy",
//        "Owen",
//        "Maxwell",
//        "Wesley",
//        "Gwen",
//        "Westin",
//        "Bowen",

//        "Yvette",

        "Zola",
        "Zoey",
        "Lorenzo",
        "Enzo",
        "Alonzo",
        "Zoie",
        "Vincenzo",
    };

    public static void main(String...args) throws IOException {
        for (String enName : candidateNames) {
            String[] kmName = GoogleTranslateUtil.enToKm(enName);
            if (! kmName[0].equalsIgnoreCase(kmName[1])) {
                System.out.println(enName + "\t" + kmName[1]);
            }
        }
    }
}
