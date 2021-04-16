/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.familysearch.standards.place.access.validator.MessageFactory;
import org.familysearch.standards.place.access.validator.NameValidator;
import org.familysearch.standards.place.exceptions.PlaceDataException;

/**
 * @author wjohnson000
 *
 */
public class TestJapanNames {

    static final String[][] japaneseNames = {
            { "hi", "जापान" },
            { "ps", "ژاپون" },
            { "pt", "Japão" },
            { "ckb-Arab", "یاپان" },
            { "ja-Kana", "日本" },
            { "bo-Tibt", "རི" },
            { "sd-Latn-x-euro", "Jāpān" },
            { "hr", "Japan" },
            { "hu", "Japán" },
            { "yi", "יאַפּאַן" },
            { "hy", "Ճապոնիա" },
            { "zh-Hant", "日本" },
            { "cau-Cyrl", "Йапони" },
            { "ug-Arab", "ياپونىيه" },
            { "id", "Jepang" },
            { "fil-Latn", "Hapon" },
            { "ja-Hani", "日本" },
            { "tt-Latn", "Yaponiä" },
            { "ce-Cyrl", "Япони" },
            { "is", "Japan" },
            { "it", "Giappone" },
            { "am", "ጃፓን" },
            { "prs-Arab", "جاپان" },
            { "ar", "اليابان" },
            { "lap-Latn", "Japána" },
            { "zu", "iJapani" },
            { "rn", "Ubuyapani" },
            { "ro", "Japonia" },
            { "pap-Latn", "Hapon" },
            { "ru", "Япония" },
            { "be", "Японія" },
            { "rw", "Ubuyapani" },
            { "bg", "Япония" },
            { "el-monoton", "Ιαπωνία" },
            { "nso", "Japane" },
            { "bn", "জাপান" },
            { "kbd-Cyrl", "Японие" },
            { "ka", "იაპონია" },
            { "ne-Latn-x-euro", "Dzāpān" },
            { "sk", "Japonsko" },
            { "sl", "Japonska" },
            { "sm", "Iapani" },
            { "el-polyton", "Ἰαπωνία" },
            { "gd-Latn", "Iapan" },
            { "so", "Jabaan" },
            { "ca", "Japó" },
            { "kk", "Жапония" },
            { "st", "Japane" },
            { "km", "ជប៉ុន" },
            { "az-Cyrl", "Јапонија" },
            { "sv", "Japan" },
            { "ko", "일본" },
            { "sw", "Ujapani" },
            { "tk-Cyrl", "Япония" },
            { "tpi", "Siapan" },
            { "ta", "ஜப்பான்" },
            { "cs", "Japonsko" },
            { "sr-Cyrl", "Јапан" },
            { "te", "జపాన్" },
            { "th", "ญี่ปุ่น" },
            { "la", "Iaponia" },
            { "cy", "Siapan" },
            { "sqj-Latn", "Japoni" },
            { "zza-Arab", "ژاپۆنیا" },
            { "uz-Cyrl", "Япония" },
            { "to", "Siapani" },
            { "ty-Latn", "Tapone" },
            { "da", "Japan" },
            { "tr", "Japonya" },
            { "de", "Japan" },
            { "lo", "ຍີ່ປຸ່ນ" },
            { "sd-Arab", "جاپان" },
            { "lt", "Japonija" },
            { "lv", "Japāna" },
            { "krc-Cyrl", "Япония" },
            { "ha-Latn", "Japan" },
            { "cau-Latn", "Japonî" },
            { "ja-Hira", "にっぽん" },
            { "rm-puter", "Giapun" },
            { "dv", "ޖަޕާން" },
            { "wyy-Latn", "Japani" },
            { "nah-Latn", "Nihpen" },
            { "cv-Cyrl", "Япони" },
            { "uk", "Японія" },
            { "dz", "ཇ་པཱན" },
            { "mg", "Japana" },
            { "scn-Latn", "Giappuni" },
            { "ur", "جاپان" },
            { "mk", "Јапонија" },
            { "ms", "Jepun" },
            { "mt", "Ġappun" },
            { "en", "Japan" },
            { "mn-Cyrl", "Япон" },
            { "my", "ဂ္ယပန္" },
            { "chr-Cher", "ᏂᎰᏅ" },
            { "es", "Japón" },
            { "et", "Jaapan" },
            { "eu", "Japonia" },
            { "vi", "Nhật Bản" },
            { "ne", "जापान" },
            { "fa", "ژاپن" },
            { "nl", "Japan" },
            { "no", "Japan" },
            { "fi", "Japani" },
            { "yue", "日本" },
            { "fr", "Japon" },
            { "ku-Arab", "ژاپان" },
            { "zza-Latn", "Japonya" },
            { "bm-Latn", "Zapɔn" },
            { "ig-Latn", "Japan" },
            { "fy", "Japan" },
            { "ga", "an tSeapáin" },
            { "wa-Latn", "Djapon" },
            { "tg-Arab", "یپانیه" },
            { "lld", "Iapann" },
            { "gl", "Xapón" },
            { "gv", "yn Chapaan" },
            { "pa", "ਜਾਪਾਨ" },
            { "uz-Latn", "Yaponiya" },
            { "oc-Cyrl", "Япон" },
            { "cau-Arab", "ژاپۆنی" },
            { "haw-Latn", "Iāpana" },
            { "pl", "Japonia" },
            { "he", "יפן" },
            { "jv-Latn", "Japan" },
            { "vo-nulik", "Yapän" },
    };

    public static void main(String...args) throws PlaceDataException {
        for (int i=6000;  i<6200;  i++) {
            System.out.println(" " + i + " --> " + Integer.toHexString(i) + " .. " + Character.isAlphabetic(i));
        }
        NameValidator validr = new NameValidator(null, new MessageFactory());
        for (String[] name : japaneseNames) {
            System.out.println("\n" + Arrays.toString(name));
            System.out.println(name[1].codePoints().asLongStream()
                .mapToObj(lung -> "" + lung + " [" + Long.toHexString(lung) + "] [" + Character.isAlphabetic((int)lung) + "]")
                .collect(Collectors.joining("   ", "  ", "")));
                
            validr.validateDisplayName(name[1]);
        }
    }
}
