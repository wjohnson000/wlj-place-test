package std.wlj.xlit;

import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.familysearch.standards.place.util.PlaceHelper;

public class UnicodeForKM {

    static String[] en2kmConstants = {
//        "English Initial|English Medial|English Final|Khmer Initial|Khmer Medial|Khmer Final",
        "p|p|p|ផ|្ផ|ផ",
        "b, bh|b, bh|b, bh|ព|្ព|ព",
        "t|t|t|ឋ|្ឋ|ឋ",
        "d, dh|d, dh|d, dh|ឍ|្ឍ|ឍ",
        "k, ca, co, cu, cC, q|k, ca, co, cu, q, cC|k, c, ck, q, cq|ខ|្ក|ក",
        "ga, go, gu, gC|ga, go, gu, gC|g, Cg|គ|្គ|គ",
        "f, ph, pf|f, ph, pf|f, ph|ហ្វ៊|្វ|ហ្វ",
        "v|v|v|វ|្វ|វ",
        "th|th|th|ឋ|្ឋ|ឋ",
        "s, ci, ce|s, ci, ce|s, ci, ce|ស|្ស|ស",
        "z, sz|z, sV, sz|z, sz, bs, ds, gs|ហ្ស៊|ហ្ស|ហ្ស៊",
        "sh, sch|sh, sch|sh, sch|ស|្ស|ស",
        "ch, tch, tsh, tr|ch, tch, tsh, tr|ch, tch, tsh, tr|ឆ|្ច|ច",
        "j, ge, gi, d[r]|j, ge, gi, dg, d[r]|j, ge, gi, dg, d[r]|ឈ|្ជ|ជ",
        "h|h|h|ហ|្ហ|",
        "l|l|l|ល|្ល|ល",
        "r|r|r|រ|្រ|រ",
        "m|m|m|ម|្ម|ម",
        "n|n|n|ណ|្ណ|ណ",
        "|ng|ng|ង|្ង|ង",
        "x|x|x|ហ្ស៊|្ក្ស|ក្ស",
        "|ny|ny, ney||្ញ|ញ",
    };

    static String[][] kmAlphabet = {
        { "ក", "្ក", "[kɑː]", "kâ", "[k]", "k" }, 
        { "ខ", "្ខ", "[kʰɑː]", "khâ", "[kʰ]", "kh" }, 
        { "គ", "្គ", "[kɔː]", "kô", "[k]", "k" }, 
        { "ឃ", "្ឃ", "[kʰɔː]", "khô", "[kʰ]", "kh" }, 
        { "ង", "្ង", "[ŋɔː]", "ngô", "[ŋ]", "ng" }, 
        { "ច", "្ច", "[cɑː]", "châ", "[c]", "ch" }, 
        { "ឆ", "្ឆ", "[cʰɑː]", "chhâ", "[cʰ]", "chh" }, 
        { "ជ", "្ជ", "[cɔː]", "chô", "[c]", "ch" }, 
        { "ឈ", "្ឈ", "[cʰɔː]", "chhô", "[cʰ]", "chh" }, 
        { "ញ", "្ញ", "[ɲɔː]", "nhô", "[ɲ]", "nh" }, 
        { "ដ", "្ដ", "[ɗɑː]", "dâ", "[ɗ]", "d" }, 
        { "ឋ", "្ឋ", "[tʰɑː]", "thâ", "[tʰ]", "th" }, 
        { "ឌ", "្ឌ", "[ɗɔː]", "dô", "[ɗ]", "d" }, 
        { "ឍ", "្ឍ", "[tʰɔː]", "thô", "[tʰ]", "th" }, 
        { "ណ", "្ណ", "[nɑː]", "nâ", "[n]", "n" }, 
        { "ត", "្ត", "[tɑː]", "tâ", "[t]", "t" }, 
        { "ថ", "្ថ", "[tʰɑː]", "thâ", "[tʰ]", "th" }, 
        { "ទ", "្ទ", "[tɔː]", "tô", "[t]", "t" }, 
        { "ធ", "្ធ", "[tʰɔː]", "thô", "[tʰ]", "th" }, 
        { "ន", "្ន", "[nɔː]", "nô", "[n]", "n" }, 
        { "ប", "្ប", "[ɓɑː]", "bâ", "[ɓ], [p]", "b, p" }, 
        { "ផ", "្ផ", "[pʰɑː]", "phâ", "[pʰ]", "ph" }, 
        { "ព", "្ព", "[pɔː]", "pô", "[p]", "p" }, 
        { "ភ", "្ភ", "[pʰɔː]", "phô", "[pʰ]", "ph" }, 
        { "ម", "្ម", "[mɔː]", "mô", "[m]", "m" }, 
        { "យ", "្យ", "[jɔː]", "yô", "[j]", "y" }, 
        { "រ", "្រ", "[rɔː]", "rô", "[r]", "r" }, 
        { "ល", "្ល", "[lɔː]", "lô", "[l]", "l" }, 
        { "វ", "្វ", "[ʋɔː]", "vô", "[ʋ]", "v" }, 
        { "ឝ", "្ឝ", "Obsolete; historically used for palatal s", "", "", "" }, 
        { "ឞ", "្ឞ", "Obsolete; historically used for retroflex s", "", "", "" }, 
        { "ស", "្ស", "[sɑː]", "sâ", "[s]", "s" }, 
        { "ហ", "្ហ", "[hɑː]", "hâ", "[h]", "h" }, 
        { "ឡ", "none[6]", "[lɑː]", "lâ", "[l]", "l" }, 
        { "អ", "្អ", "[ʔɑː]", "’â", "[ʔ]", "’" }, 
    };

    public static void main(String...args) {
        System.out.println("===============================================================================");
        for (String em2km : en2kmConstants) {
            String[] chunks = PlaceHelper.split(em2km, '|');
            if (chunks.length > 5) {
                System.out.println(em2km + "|" + getUChars(chunks[3]) + "|" + getUChars(chunks[4]) + "|" + getUChars(chunks[5]));
            }
        }

        System.out.println("===============================================================================");
        for (String[] km : kmAlphabet) {
            System.out.println(km[0] + "|" + getUChars(km[0]) + "|" + km[1] + "|" + getUChars(km[1]) + "|" + km[3] + "|" + km[4] + "|" + km[5]);
        }
    }

    static String getUChars(String text) {
        if (text == null) return "Uknown";
        return text.chars()
                .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
                .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
                .collect(Collectors.joining(" "));
    }
}