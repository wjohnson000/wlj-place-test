/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestV2KO {

    static String[] textesOK = {
//        // Set 1 -- all match
//        "朝鮮太祖洪武壬申年七月十七日|1392/8/5",
//        "朝鮮太祖洪武壬申年|1392",
//        "朝鮮太祖洪武元年七月卄八日|1392/8/16",
//        "朝鮮太祖洪武七年九月五日|1398/10/14",
//        "朝鮮太祖洪武戊寅年|1398",
//        "朝鮮定宗建文己卯年|1399",
//        "朝鮮定宗建文庚辰年十一月十三日|1400/11/28",
//        "朝鮮定宗建文庚辰年|1400",
//        "朝鮮太宗永樂戊戌年|1418",
//        "朝鮮世宗永樂己亥年|1419",
//        "朝鮮世宗永樂庚子年|1420",
//        "朝鮮世宗永樂辛丑年|1421",
//        "朝鮮世宗永樂壬寅年|1422",
//        "朝鮮世宗永樂癸卯年|1423",
//        "朝鮮世宗景泰庚午年二月十七日|1450/3/30",
//        "朝鮮世宗景泰庚午年|1450",
//        "朝鮮文宗景泰壬申年五月十四日|1452/6/1",
//        "朝鮮文宗景泰壬申年|1452",
//        "朝鮮端宗乙亥年閏六月十一日|1455/6/25",  // V1 -- Invalid day 11 (?)     V2 -- No match
//        "朝鮮端宗乙亥年閏六月十一日|1455/6/25",  // V1 -- Invalid day 11 (?)     V2 -- No match
//        "朝鮮端宗乙亥年|1455",                  // V1 -- 9 matches              V2 -- No match
//
//        // Set 2 -- matches
//        "朝鮮世祖丁亥年九月七日|1467/10/4",
//        "朝鮮世祖丁亥年|1467",
//        "朝鮮成宗甲寅年|1494",
//        "朝鮮高宗庚午年|1870",
//        "高麗太祖戊寅年|918",
//        "高麗惠宗癸卯年|943",        // V1 -- +1003
//        "高麗定宗乙巳年|945",        // V1 -- +1005
//        "高麗光宗己酉年|949",        // V1 -- +1009
//        "高麗景宗乙亥年|975",        // V1 -- +1035
//        "高麗成宗辛巳年|981",        // V1 -- +1041
//        "高麗穆宗丁酉年|997",        // V1 -- +1057
//        "高麗文宗丁酉年|1057",
//        "高麗明宗丁巳年|1197",
//        "高麗神宗丁巳年|1197",       // V1 -- +1257
//        "新羅法興王甲午年|514",      // V1 -- many matches
//        "高句麗東明聖王甲申年|BC37", // V1 -- many matches
//        "新羅善德女王丙午年|646",
//        "新羅孝成大王丁丑年|737",    // V1 -- many matches
//        "新羅景德大王壬午年|742",    // V1 -- many matches
//        "新羅興德大王丙午年|826",    // V1 -- many matches
//        "新羅眞聖女王丁未年|887",
//
//        // Set 3 -- matches
//        "高句麗廣開土王辛卯年|391",
//        "高句麗烽上王壬子年|292",
//        "高句麗山上王丁丑年|197",
//        "高句麗次大王丙戌年|146",
//        "高句麗慕本王戊申年|48",
//        "高句麗大武神王戊寅年|18",
//        "高句麗 瑠璃明王壬寅年|BC19",
//        "百濟毗有王丁卯年|427",
//        "百濟近肖古王丙午年|346",
//        "百濟汾西王戊午年|298",
//        "百濟仇首王甲午年|214",
//        "百濟東城王己未年|479",
//        "新羅武烈王甲寅年|654",
//        "大韓帝國純宗隆熙丁未年|1907",
//        "大韓帝國純宗隆熙丁未年七月二十日|1907/8/28",
//        "大韓帝國高宗光武丁酉年|1897",
//        "大韓帝國高宗光武丁酉年十月十二日|1897/11/6",

        // Set 4 -- Some match, others don't ...
        "泰封|901",
        "태봉|901",
        "弓裔|901",
        "궁예|901"
    };

    static String[] textes = {
    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {

        for (String text : textesOK) {
            String[] chunks = PlaceHelper.split(text, '|');
            try {
                DateResult dateResult = DateUtil.interpDate(chunks[0], "ko", null, null, null);
                
                System.out.println("\n" + chunks[0] + " [" + chunks[1] + "]");
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }
        }
    }
}