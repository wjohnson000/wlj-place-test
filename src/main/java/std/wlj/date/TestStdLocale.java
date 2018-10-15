/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.core.StdLocale;

/**
 * @author wjohnson000
 *
 */
public class TestStdLocale {

    private static final String[] locales = {
        String.valueOf(StdLocale.ALBANIAN),
        String.valueOf(StdLocale.ARMENIAN),
        String.valueOf(StdLocale.BELARUSIAN),
        String.valueOf(StdLocale.BULGARIAN),
        String.valueOf(StdLocale.CHINESE),
        String.valueOf(StdLocale.CHINESE_CANTONESE),
        String.valueOf(StdLocale.CHINESE_MANDARIN),
        String.valueOf(StdLocale.CHINESE_MANDARIN_SIMPLIFIED),
        String.valueOf(StdLocale.CHINESE_MANDARIN_TRADITIONAL),
        String.valueOf(StdLocale.CHINESE_SIMPLIFIED),
        String.valueOf(StdLocale.CHINESE_TRADITIONAL),
        String.valueOf(StdLocale.CROATIAN),
        String.valueOf(StdLocale.CZECH),
        String.valueOf(StdLocale.DANISH),
        String.valueOf(StdLocale.DUTCH),
        String.valueOf(StdLocale.ENGLISH),
        String.valueOf(StdLocale.ESTONIAN),
        String.valueOf(StdLocale.FIJIAN),
        String.valueOf(StdLocale.FINNISH),
        String.valueOf(StdLocale.FRENCH),
        String.valueOf(StdLocale.GERMAN),
        String.valueOf(StdLocale.GREEK),
        String.valueOf(StdLocale.HAITIAN),
        String.valueOf(StdLocale.HUNGARIAN),
        String.valueOf(StdLocale.ICELANDIC),
        String.valueOf(StdLocale.INDONESIAN),
        String.valueOf(StdLocale.ITALIAN),
        String.valueOf(StdLocale.JAPANESE),
        String.valueOf(StdLocale.JAPANESE_HRKT),
        String.valueOf(StdLocale.JAPANESE_KANA),
        String.valueOf(StdLocale.JAPANESE_KANJI),
        String.valueOf(StdLocale.KHMER),
        String.valueOf(StdLocale.KOREAN),
        String.valueOf(StdLocale.KOREAN_HANGUL),
        String.valueOf(StdLocale.KOREAN_HANJA),
        String.valueOf(StdLocale.LAO),
        String.valueOf(StdLocale.LATVIAN),
        String.valueOf(StdLocale.LITHUANIAN),
        String.valueOf(StdLocale.MACEDONIAN),
        String.valueOf(StdLocale.MALAY),
        String.valueOf(StdLocale.MONGOLIAN),
        String.valueOf(StdLocale.MONGOLIAN_CYRILLIC),
        String.valueOf(StdLocale.MONGOLIAN_MONGOLIAN),
        String.valueOf(StdLocale.NEPALI),
        String.valueOf(StdLocale.NORWEGIAN),
        String.valueOf(StdLocale.POLISH),
        String.valueOf(StdLocale.PORTUGUESE),
        String.valueOf(StdLocale.ROMANIAN),
        String.valueOf(StdLocale.RUSSIAN),
        String.valueOf(StdLocale.SAMOAN),
        String.valueOf(StdLocale.SERBIAN),
        String.valueOf(StdLocale.SINHALA),
        String.valueOf(StdLocale.SLOVAK),
        String.valueOf(StdLocale.SLOVENIAN),
        String.valueOf(StdLocale.SPANISH),
        String.valueOf(StdLocale.SWEDISH),
        String.valueOf(StdLocale.THAI),
        String.valueOf(StdLocale.TONGAN),
        String.valueOf(StdLocale.TURKISH),
        String.valueOf(StdLocale.UKRAINIAN),
        String.valueOf(StdLocale.VIETNAMESE),
    };

    public static void main(String...args) {
        long time0 = System.nanoTime();
        for (int i=0;  i<1_000_000;  i++) {
            int ndx = i % locales.length;
//            StdLocale what = new StdLocale(locales[ndx]);
            StdLocale what = StdLocale.makeLocale(locales[ndx]);
            if (locales[ndx].equals("en")) {
                System.out.println("Locale: " + what + " --> " + (what == StdLocale.ENGLISH));
            }
        }
        long time1 = System.nanoTime();

        System.out.println("TTT: " + (time1 - time0) / 1_000_000.0);
    }
}
