/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.*;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.MultiScriptString;
import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.DictionaryFactory;
import org.familysearch.standards.core.parse.ParseContext;
import org.familysearch.standards.core.parse.Token;
import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.date.common.ModifierDictionary;
import org.familysearch.standards.date.common.MonthDictionary;
import org.familysearch.standards.date.parser.CJKDictionarySegmenter;
import org.familysearch.standards.date.parser.GenDateParseConfig;

/**
 * @author wjohnson000
 *
 */
public class TestCJKParse {

    protected static final String NUMBERS = "numbers.xml";
    protected static final String SEXAGENARY_CYCLE = "sexagenary.xml";

    protected static CJKDictionarySegmenter segmenter;
    protected static ParseContext           context;

    public static void main(String...args) {
        setupSegmenter();
        setupParseContext();
        List<String> testStrings = getTestStrings();
        for (String testStr : testStrings) {
            Token testToken = createToken(testStr);
            System.out.println("\n======================================================================");
            System.out.println("TK: " + testToken);
//            List<List<CJKDictionarySegmenter.TokenRange>> listOld = segmenter.getOld(testToken, context);
//            List<List<CJKDictionarySegmenter.TokenRange>> listZh = segmenter.getNew(testToken, context, "zh");
//            List<List<CJKDictionarySegmenter.TokenRange>> listJa = segmenter.getNew(testToken, context, "ja");
//            List<List<CJKDictionarySegmenter.TokenRange>> listKo = segmenter.getNew(testToken, context, "ko");
//            for (List<CJKDictionarySegmenter.TokenRange> lo : listOld) {
//                System.out.println("LO: " + lo);
//            }
//            for (List<CJKDictionarySegmenter.TokenRange> zh : listZh) {
//                System.out.println("ZH: " + zh);
//            }
//            for (List<CJKDictionarySegmenter.TokenRange> ja : listJa) {
//                System.out.println("JA: " + ja);
//            }
//            for (List<CJKDictionarySegmenter.TokenRange> ko : listKo) {
//                System.out.println("KO: " + ko);
//            }
            break;
        }

        System.exit(0);
    }

    static Token createToken(String tokenStr) {
        MultiScriptString str = new MultiScriptString();
        str.add(new LocalizedData<>(tokenStr, null));
        return context.getConfig().getTokenFactory().createToken(str, context);
    }

    static void setupSegmenter() {
        Dictionary cjkDictionary;
        
        Dictionary modifierDictionary = ModifierDictionary.getModifierDictionary();
        Dictionary numbersDictionary = DictionaryFactory.createDictionaryFromXML( MonthDictionary.class.getResource( NUMBERS ) );
        Dictionary sexagenaryDictionary = DictionaryFactory.createDictionaryFromXML( MonthDictionary.class.getResource( SEXAGENARY_CYCLE ) );
        Dictionary imperialDictionary = ImperialDictionary.getImperialDictionary();
        
        cjkDictionary = DictionaryFactory.createEmptyDictionary();
        cjkDictionary.mergeDictionary(imperialDictionary);
        cjkDictionary.mergeDictionary(modifierDictionary);
        cjkDictionary.mergeDictionary(numbersDictionary);
        cjkDictionary.mergeDictionary(sexagenaryDictionary);
        
        segmenter = new CJKDictionarySegmenter(cjkDictionary);
    }

    static void setupParseContext() {
        context = new ParseContext(new GenDateParseConfig(), null);
    }

    static List<String> getTestStrings() {
        List<String> textes = new ArrayList<>(132);

        textes.add("順帝三年七月七日 stuff deceased");
        textes.add("順帝丙寅叄年七月七日");
        textes.add("金世宗大定2年5月5日");
        textes.add("安政5年6月8日");
        textes.add("清世祖順治元年1月1日");
        textes.add("清世祖順治1年1月1日");
        textes.add("陳文帝天嘉年1月1日");
        textes.add("吳大帝嘉禾年1月1日");
        textes.add("民國10年10月10日");
        textes.add("安政5年6月8");
        textes.add("西元1921年11月9日");
        textes.add("宣統三年十二月三十日");
        textes.add("宣統三年十二月三十一日");
        textes.add("光緖丁酉年十一月二十九日");
        textes.add("朝鮮太祖洪武壬申年七月十七日"); 
        textes.add("乾隆丙午年二月廿三日未時");
        textes.add("大正五年一月六號");
        textes.add("清世祖順治元年1月1日"); 
        textes.add("民國乙未（四十四）年五月五日");
        textes.add("民國乙未（四十四）五月五日");
        textes.add("民國乙未（四十四年）五月五日");
        textes.add("千九百二十一年十一月九日");
        textes.add("西元千九百二十一年十一月九日");
        textes.add("千九百二十一年十一月九 - 千九百四十一年三月十九日");
        textes.add("西元千九百二十一年十一月九 - 西元千九百四十一年三月十九日");
        textes.add("遼太祖一年一月九日");    // Chinese
        textes.add("元和一年一月九日");      // Japanese
        textes.add("朝鮮世宗一年一月九日");  // Korean
        textes.add("元延祐1年1月15日");
        textes.add("民國七十三甲子年");
        textes.add("民國七十七戊辰年七月初六日");
        textes.add("民國乙未（四十四）五月五日");
        textes.add("民國甲子");
        textes.add("哀帝建平四年夏");
        textes.add("武成王");
        textes.add("景帝孝");
        textes.add("朝鮮太祖洪武七年九月五日");
        textes.add("중천왕");
        textes.add("朝鮮定宗建文庚辰年十一月十三日");
        textes.add("朝鮮太宗永樂戊戌年");
        textes.add("朝鮮世宗永樂己亥年");
        textes.add("朝鮮世宗永樂庚子年");
        textes.add("朝鮮世宗永樂辛丑年");
        textes.add("朝鮮世宗永樂壬寅年");
        textes.add("朝鮮世宗永樂癸卯年");
        textes.add("民國二十四年七月二十一日");
        textes.add("民國乙亥年七月二十一日");
        textes.add("民國二十四年乙亥年七月二十一日");
        textes.add("庚午年一月");
        textes.add("庚午年正月");
        textes.add("庚午年腊月");
        textes.add("光绪十二年一月");
        textes.add("光绪十二年元月");
        textes.add("光绪十二年正月");
        textes.add("一九五一年元月");
        textes.add("一九五一年十月正");
        textes.add("一九五一年十月正日");
        textes.add("乾隆丙辰年八月三日");
        textes.add("乾隆丙辰年八月初三日");

        return textes;
    }

}
