/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.common.DateUtil;

/**
 * @author wjohnson000
 *
 */
public class TestDateLots {

//    static String[] textes = {
//        "順帝三年七月七日",
//        "順帝丙寅叄年七月七日",
//        "金世宗大定2年5月5日",
//        "安政5年6月8日",
//        "清世祖順治元年1月1日",
//        "清世祖順治1年1月1日",
//        "陳文帝天嘉年1月1日",
//        "吳大帝嘉禾年1月1日",
//        "民國10年10月10日",
//        "安政5年6月8",
//        "西元1921年11月9日",
//        "宣統三年十二月三十日",
//        "宣統三年十二月三十一日",
//        "光緖丁酉年十一月二十九日",
//        "朝鮮太祖洪武壬申年七月十七日", 
//        "乾隆丙午年二月廿三日未時",
//        "大正五年一月六號",
//        "清世祖順治元年1月1日", 
//    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
//        List<String> textes = Files.readAllLines(Paths.get("C:/temp/zh-dates-more.txt"), StandardCharsets.UTF_8);
        List<String> textes = new ArrayList<>();
        textes.clear();
//        textes.add("11/6/1975");
//        textes.add("1975 11 6");
//        textes.add("1957 1 23 to 1975 11 6");
//        textes.add("1/23/1957 to 11/6/1975");
//        textes.add("二〇〇三年");
//        textes.add("二零零三年二月三");
//        textes.add("二零零三年二月三日");
//        textes.add("30 Floréal AN11");
//        textes.add("28 Brumaire AN04");
//        textes.add("11 Vendémiaire AN04");
//        textes.add("07 Frimaire AN08");
//        textes.add("16 Illisible AN02");
//        textes.add("00 Nivose AN12 ");
        textes.add("民國乙未（四十四）年五月五日");
        textes.add("民國乙未（四十四）五月五日");
        textes.add("民國乙未（四十四年）五月五日");

        for (String text : textes) {

            try {
                DateResult dateResult = DateUtil.interpDate(text, "zh", null, null, null);

                System.out.println("\n" + text);
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }

        }
    }
}