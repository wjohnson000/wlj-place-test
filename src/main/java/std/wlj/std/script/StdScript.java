package std.wlj.std.script;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class that holds the BCP 47 script code for StdLocale. This class is immutable.
 */
public class StdScript implements Serializable {
    /**
     * Serial version identifier for serialization purposes.
     */
    private static final long serialVersionUID = 3058012088013401999L;

    final private String code;

    static public final StdScript HANI = new StdScript("Hani"); // Han, Hanja, Kanji
    static public final StdScript HANS = new StdScript("Hans"); // Han (Simplified)
    static public final StdScript HANT = new StdScript("Hant"); // Han (Traditional)
    static public final StdScript CYRL = new StdScript("Cyrl"); // Cyrillic
    static public final StdScript LATN = new StdScript("Latn"); // Latin or "roman"
    static public final StdScript HRKT = new StdScript("Hrkt"); // A Japanese string containing Hiragana + Katakana
    static public final StdScript JPAN = new StdScript("Jpan"); // Japanese (Kanji + Hiragana + Katakana
    static public final StdScript KANA = new StdScript("Kana"); // Katakana
    static public final StdScript HIRA = new StdScript("Hira"); // Hiragana
    static public final StdScript HANG = new StdScript("Hang"); // Hangul
    static public final StdScript KORE = new StdScript("Kore"); // Korean (Han + Hangul)
    static public final StdScript THAI = new StdScript("Thai"); // Thai
    static public final StdScript KHMR = new StdScript("Khmr"); // Khmer (Cambodian)
    static public final StdScript UNKNOWN = new StdScript("");  // TODO WHAT ABOUT UNKNOWN SCRIPTS??

    /** Set of all Character-based scripts */
    static private final Set<Character.UnicodeBlock> characterScripts = new HashSet<>();
    static {
        characterScripts.add(Character.UnicodeBlock.BOPOMOFO);
        characterScripts.add(Character.UnicodeBlock.BOPOMOFO_EXTENDED);
        characterScripts.add(Character.UnicodeBlock.CJK_COMPATIBILITY);
        characterScripts.add(Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS);
        characterScripts.add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
        characterScripts.add(Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
        characterScripts.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        characterScripts.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
        characterScripts.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
        characterScripts.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C);
        characterScripts.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D);
        characterScripts.add(Character.UnicodeBlock.HANGUL_JAMO);
        characterScripts.add(Character.UnicodeBlock.HANGUL_SYLLABLES);
        characterScripts.add(Character.UnicodeBlock.HIRAGANA);
        characterScripts.add(Character.UnicodeBlock.KANBUN);
        characterScripts.add(Character.UnicodeBlock.KANGXI_RADICALS);
        characterScripts.add(Character.UnicodeBlock.KATAKANA);
        characterScripts.add(Character.UnicodeBlock.TIBETAN);
    }

    /** Map of UnicodeBlock --> Script */
    static private final Map<Character.UnicodeBlock,StdScript> blockToScript = new HashMap<>();
    static {
        blockToScript.put(Character.UnicodeBlock.ARABIC, new StdScript("Arab"));
        blockToScript.put(Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A, new StdScript("Arab"));
        blockToScript.put(Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B, new StdScript("Arab"));
        blockToScript.put(Character.UnicodeBlock.ARMENIAN, new StdScript("Armn"));
        blockToScript.put(Character.UnicodeBlock.BASIC_LATIN, StdScript.LATN);
        blockToScript.put(Character.UnicodeBlock.BENGALI, new StdScript("Beng"));
        blockToScript.put(Character.UnicodeBlock.BOPOMOFO, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.BOPOMOFO_EXTENDED, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CHEROKEE, new StdScript("Cher"));
        blockToScript.put(Character.UnicodeBlock.CJK_COMPATIBILITY, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.CYRILLIC, StdScript.CYRL);
        blockToScript.put(Character.UnicodeBlock.DEVANAGARI, new StdScript("Deva"));
        blockToScript.put(Character.UnicodeBlock.ETHIOPIC, new StdScript("Ethi"));
        blockToScript.put(Character.UnicodeBlock.GEORGIAN, new StdScript("Geor"));
        blockToScript.put(Character.UnicodeBlock.GREEK, new StdScript("Grek"));
        blockToScript.put(Character.UnicodeBlock.GREEK_EXTENDED, new StdScript("Grek"));
        blockToScript.put(Character.UnicodeBlock.GUJARATI, new StdScript("Gujr"));
        blockToScript.put(Character.UnicodeBlock.GURMUKHI, new StdScript("Guru"));
        blockToScript.put(Character.UnicodeBlock.HANGUL_JAMO, StdScript.HANG);
        blockToScript.put(Character.UnicodeBlock.HANGUL_SYLLABLES, StdScript.HANG);
        blockToScript.put(Character.UnicodeBlock.HEBREW, new StdScript("Hebr"));
        blockToScript.put(Character.UnicodeBlock.HIRAGANA, StdScript.HIRA);
        blockToScript.put(Character.UnicodeBlock.KANNADA, new StdScript("Knda"));
        blockToScript.put(Character.UnicodeBlock.KANBUN, StdScript.JPAN);
        blockToScript.put(Character.UnicodeBlock.KANGXI_RADICALS, StdScript.HANI);
        blockToScript.put(Character.UnicodeBlock.KATAKANA, StdScript.KANA);
        blockToScript.put(Character.UnicodeBlock.KHMER, StdScript.KHMR);
        blockToScript.put(Character.UnicodeBlock.LAO, new StdScript("Laoo"));
        blockToScript.put(Character.UnicodeBlock.LATIN_EXTENDED_A, StdScript.LATN);
        blockToScript.put(Character.UnicodeBlock.LATIN_EXTENDED_B, StdScript.LATN);
        blockToScript.put(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL, StdScript.LATN);
        blockToScript.put(Character.UnicodeBlock.LATIN_1_SUPPLEMENT, StdScript.LATN);
        blockToScript.put(Character.UnicodeBlock.MALAYALAM, new StdScript("Mlym"));
        blockToScript.put(Character.UnicodeBlock.MONGOLIAN, new StdScript("Mong"));
        blockToScript.put(Character.UnicodeBlock.MYANMAR, new StdScript("Mymr"));
        blockToScript.put(Character.UnicodeBlock.OGHAM, new StdScript("Runr"));
        blockToScript.put(Character.UnicodeBlock.ORIYA, new StdScript("Orya"));
        blockToScript.put(Character.UnicodeBlock.RUNIC, new StdScript("Runr"));
        blockToScript.put(Character.UnicodeBlock.SINHALA, new StdScript("Sinh"));
        blockToScript.put(Character.UnicodeBlock.SYRIAC, new StdScript("Syrc"));
        blockToScript.put(Character.UnicodeBlock.TAMIL, new StdScript("Taml"));
        blockToScript.put(Character.UnicodeBlock.TELUGU, new StdScript("Telu"));
        blockToScript.put(Character.UnicodeBlock.THAANA, new StdScript("Thaa"));
        blockToScript.put(Character.UnicodeBlock.THAI, StdScript.THAI);
        blockToScript.put(Character.UnicodeBlock.TIBETAN, StdScript.HANI);
    }

    /**
     * Default constructor sets the code to Code.LATN
     */
    public StdScript() {
        this.code = StdScript.LATN.toString(); // Latin is the default script
    }

    /**
     * Constructor which sets the code to
     *
     * @param code
     */
    public StdScript(String code) {
        this.code = validate(code);
    }

    /**
     * Returns the script code for this script.
     * 
     * @return Returns the script code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the script code of this script.
     * 
     * @return Returns the script code.
     */
    @Override
    public String toString() {
        return code;
    }

    /**
     * Returns true if both instances are ScriptOlds and their codes are equal.
     * 
     * @return Returns true if both instances are ScriptOlds and their codes are equal.
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof StdScript)) return false;

        StdScript stdScript = (StdScript) that;
        return (code.equals(stdScript.code));
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    private String validate(String code) {
        boolean valid = true;
        // todo we should validate the code against acceptable BCP47 script codes.
        if (valid) {
            return code;
        } else {
            return ""; // empty string means the input script code was not valid
        }
    }

    /**
     * Determine the script of a particular codePoint
     *
     * @param codePoint
     * @return return null if no script, a valid ScriptOld
     */
    public static StdScript getScript(int codePoint) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
        StdScript result = null;
        
        if (block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            if ((codePoint >= 0xFF61 && codePoint <= 0xFF64) ||
                    (codePoint >= 0x20000 && codePoint <= 0x2A6D6) ||
                    (codePoint >= 0x2A700 && codePoint <= 0x2B734) ||
                    (codePoint >= 0x2B740 && codePoint <= 0x2B81D)) {
                result = StdScript.HANI;
            } else if ((codePoint >= 0xFF65) && (codePoint <= 0xFF9F)) {
                result = StdScript.KANA;
            } else if ((codePoint >= 0xFFAD) && (codePoint <= 0xFFDC)) {
                result = StdScript.HANG;
            } else {
                result = StdScript.LATN;
            }
        } else {
            result = blockToScript.get(block);
        }

        return (result == null) ? StdScript.UNKNOWN : result;  // Default case?
    }

    /**
     * Determine the ScriptType of the String
     * <p/>
     * Order of this list is important.
     * <p/>
     * ONLY valid characters, if isAllCharValid(String)==true, will come here, or else, this logic is incorrect
     * <p/>
     * Detecting this way seems necessary when we consider that some cultures have multiple scripts, and some other cultures use one script only.
     * <p/>
     * For instance, Japanese uses Hirakana, Katakana, Kanbun, Chinese, and Latin. When a searchText contains *kana, or Kanbun, this is Japanese regardless of the
     * exitance of Chinese or Latin since *kana or Kanbun is unique to Japanese.
     * <p/>
     * Korean uses Hangul, Chinese, and Latin. If a searchText contains Hangul, it is Korean regardless of the exitance of Chinese or Latin since Hangul is unique
     * to Korean.
     * <p/>
     * If a searchText does not contain Japanese, Korean, or any other script except Chinese, we can assume that this searchText is Chinese. However, some
     * cultures like Korea or Japan may write all searchText in Chinese.
     * <p/>
     * Support for UTF-16 surrogate characters (above Unicode Plane 0) has been added.  Specifically, Chinese character extensions B, C & D are supported.
     * Surrogate characters must be represented as two consecutive chars, the first, or high-order character with a value between 0xD800 and 0xDBFF, and the second,
     * or low-order character with a value between 0xDC00 and 0xDFFF.  The surrogates are then converted internally to UTF-32 and range checked to determine script type.
     * The method implemented here is supported in Java 6, and will be compatible with Java 7 without change to this method or any code consuming it.
     *
     * @param text input unicode searchText string
     * @return the ScriptType
     */

 
    public static StdScript getScript(String text) {
        Set<StdScript> scripts = new HashSet<StdScript>();
        int offset = 0;
        int codePoint = text.codePointAt(offset);
        scripts.add(getScript(codePoint));
        while (text.length() > (offset = text.offsetByCodePoints(offset, 1))) {
            StdScript script = getScript(text.codePointAt(offset));
            scripts.add(script);
        }

        // short circuit for japanese
        if (scripts.contains(StdScript.JPAN)) {  // if one of the characters is "general" japanese then call the whole string "general" japanese
            return StdScript.JPAN;
        }


        switch (scripts.size()) {
        case 0:
            return StdScript.UNKNOWN;
        case 1:
            return scripts.iterator().next();
        case 2:
            if (scripts.contains(StdScript.HANS) && scripts.contains(StdScript.HANT)) { // both traditional and simplified Han?
                return StdScript.HANI;
            } else if (scripts.contains(StdScript.HANI) && scripts.contains(StdScript.HANG)) { // Hanja and Hangul?
                return StdScript.KORE;
            } else if (scripts.contains(StdScript.KANA) && scripts.contains(StdScript.HIRA)) { // hiragana and katakana?
                return StdScript.HRKT;
            } else if (scripts.contains(StdScript.HANI) && (scripts.contains(StdScript.KANA) || (scripts.contains(StdScript.HIRA)))) { // hiragana or katakana with kanji
                return StdScript.JPAN;
            }
            return StdScript.UNKNOWN;
        case 3:
            if (scripts.contains(StdScript.HANI) && scripts.contains(StdScript.KANA) && (scripts.contains(StdScript.HIRA))) { // hiragana or katakana with kanji
                return StdScript.JPAN;
            }
            return StdScript.UNKNOWN;
        default:
            return StdScript.UNKNOWN;
        }

    }

    /**
     * Determine if a Character string is Character-based [vs. text-base].
     * 
     * @param str string to test
     * @return TRUE if it's Character-based, FALSE otherwise
     */
    public static boolean hasCharactorOrientedScript(String str) {
        // TODO:  What's the behavior if there is a mixed script?
        // For example, what if there's Latn and Chinese?  Should we only
        // check the first character and make the determination based upon that?
        for (int i=0;  i<str.length();  i++) {
            int codePoint = str.codePointAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            if (characterScripts.contains(block)) {
                return true;
            }
        }

        return false;
    }
}
