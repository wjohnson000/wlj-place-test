package std.wlj.general;

import java.util.Arrays;

import org.familysearch.standards.place.data.VariantNameSorter;

public class SetupVariantNameSorter {

    private static String[] nameTypes = {
        "439|CONV|false|en|Conventional Name|Conventional Name",
        "451|LDS_2LTMPL|true|en|LDS 2 letter Temple Code|LDS 2 letter Temple Code",
        "442|US_STABRV|true|en|U.S. State Abbreviation|U.S. State Abbreviation, the standard two letter postal abbreviations. ID, CA, WY, etc.",
        "531|MISSPELL|true|en|Misspelling|A clear misspelling of a place name.",
        "438|NONDC|false|en|Nondiacritic Name|Nondiacritic Name",
        "453|ODM_STD|false|en|ODM Standard Name|Standard Place name used in the Ordinance Data Management system or OLIB",
        "456|ETHNIC|true|en|Demonym|Ethnic variant, demonym",
        "437|FULLN|true|en|Standard Full Name|Standard Full Name",
        "436|SORT|false|en|Sort Name|Sort Name",
        "440|VAR|true|en|Variant Name|Variant Name",
        "503|CHAPMAN CODE|true|en|Chapman Code|Chapman County Code used 1987 by British Standards Institution as a basis for the British Standard 6879 (rev. 1999), & the International Standards Organisation in Geneva when publishing ISO 3166-2.",
        "449|ISONAME|false|en|ISO Name|ISO Name",
        "434|SHORT|true|en|Standard Short Name|Standard Short Name",
        "435|GENERIC|false|en|Generic Name|Generic Name",
        "457|UND|false|en|Unvetted ODM Name Variant|Unvetted ODM Name Variant",
        "454|ODM_VAR|false|en|ODM Variant Name|Variant Place name used in the Ordinance Data Management system",
        "452|LDS_OTHER|false|en|LDS Other Temple Code|LDS Other Temple Code, not the official 2 letter or 5 letter codes.",
        "511|ANGL|true|en|Anglicization|The English version of a foreign name entity, usually removing diacritics and often spelled and pronounced differently than the original name. This will always have a Locale of en and cannot occur on places where en is the default Locale.",
        "455|US_QUALIFIED|false|en|U.S. Qualified Name|NGA U.S. or Territory name with parenthetic qualifier",
        "446|ISOCNTRYCD|true|en|ISO Country Code|ISO Country Code",
        "458|COMMON|true|en|Dominant Name|Dominant Name, the \"trump\" card. This name type is only used to elevate a single instance of a commonly used name. \"Paris\" should return Paris, France and not Paris, Idaho, for example.",
        "444|ABRV|true|en|Abbreviation|Abbreviation",
        "445|DSPLY|false|en|Common Pedigree Display Name|Standard Display Name",
        "447|ISO2LCD|true|en|ISO 2L Code|ISO Second Level Code",
        "448|ISO3LCD|true|en|ISO 3L Code|ISO Third Level Code",
        "450|LDS_5LTMPL|true|en|LDS 5 letter Temple Code|LDS 5 letter Temple Code",
        "543|QANameType|true|en|QA testing nameType|testing name type QA",
        "543|QANameType|true|es|QA testing updated|testing nameType update",
        "441|FULLD|false|en|Full Name Not Verified|Full Name Not Verified",
        "443|OLD_STABRV|false|en|OLD_STATE_ABRV|Old U.S. State Abbreviations",     
    };

    private static String[] namePriorities = {
        "ODM_VAR|1",
        "LDS_OTHER|1",
        "ABRV|5",
        "SORT|2",
        "ISOCNTRYCD|7",
        "FULLD|3",
        "ETHNIC|1",
        "ODM_STD|3",
        "COMMON|10",
        "ISONAME|3",
        "OLD_STABRV|8",
        "LDS_2LTMPL|1",
        "ISO2LCD|7",
        "SHORT|4",
        "DSPLY|5",
        "NONDC|2",
        "GENERIC|2",
        "LDS_5LTMPL|1",
        "ISO3LCD|7",
        "US_STABRV|8",
        "US_QUALIFIED|2",
        "FULLN|5",
        "VAR|3",
        "UND|1",
        "CONV|2",
    };

    public static void main(String...args) {
        VariantNameSorter.initMap(Arrays.asList(nameTypes), Arrays.asList(namePriorities));
        System.out.println("Ready? " + VariantNameSorter.isReady());
    }
}
