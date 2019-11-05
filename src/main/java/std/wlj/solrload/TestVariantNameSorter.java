package std.wlj.solrload;

import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.reader.AppDataReader;
import org.familysearch.standards.place.data.VariantNameSorter;

import std.wlj.util.DbConnectionManager;

public class TestVariantNameSorter {

    private static DbHelper dbService;

    private static String[] vNames = {
        "13275|437|zh-Hant|華盛頓州",
        "13276|437|zh-Hans|华盛顿州",
        "13273|434|en|Washington",
        "13292|454|en|WASHINGTN",
        "13293|454|en|WASHNGT",
        "13294|454|en|WA",
        "13274|437|en|State of Washington",
        "13277|437|fr|Washington",
        "13278|437|de|Washington",
        "13279|437|it|Washington",
        "13280|437|ja-Kana|ワシントン州",
        "13281|437|ko|워싱턴 주",
        "13282|437|pt|Washington",
        "13283|437|ru|Вашингтон",
        "13284|437|es|Washington",
        "13285|440|en|Was",
        "13286|440|en|Vasington",
        "13287|440|zh-Hant|華盛頓",
        "13288|440|zh-Hans|华盛顿",
        "13289|454|en|WAHINGTON",
        "13290|454|en|WSHNGTN",
        "13291|454|en|WSHNT",
        "13295|454|en|WSHNGT",
        "13296|454|en|WSHGTN",
        "13297|454|en|WSHTN",
        "13298|454|en|WSHTON",
        "13299|454|en|WASHTN",
        "13300|454|en|WASHN",
        "13301|454|en|WASHINGTON STATE",
        "13302|454|en|WSHT",
        "13309|454|en|WSHGT",
        "13310|454|en|WASHNGTON",
        "13311|454|en|WSHNTN",
        "13312|454|en|WSHINGTON",
        "13313|454|en|WASH",
        "13314|454|en|WSHNG",
        "13315|454|en|WHSN",
        "13316|454|en|WASHINGTO",
        "13317|454|en|WSHNGN",
        "13318|454|en|WASHINTON",
        "13319|454|en|WSHG",
        "13320|454|en|WASHNGTN",
        "13321|436|en|Washington, State of",
        "13322|449|en|Washington",
        "13323|442|en|WA",
        "13324|443|en|Wash",
        "13325|447|en|US-WA",
        "318|458|en|wa",
        "319|458|en|washington",
        "320|458|en|washington state",
        "321|458|en|wash",
        "13303|454|en|WSNGTN",
        "13304|454|en|WSH",
        "13305|454|en|WSHGN",
        "13306|454|en|WAHGT",
        "13307|454|en|WASHNT",
        "13308|454|en|WSHN",
    };

	public static void main(String... args) throws InterruptedException {
	    dbService = new DbHelper(DbConnectionManager.getDataSourceAwsDev());
		loadAppData();
		sortNames();
	}

    private static void loadAppData() {
        AppDataReader appReader = new AppDataReader(dbService);
        appReader.setupVariableNameSorter("|");
    }

    private static void sortNames() {
        List<String> varNames = Arrays.asList(vNames);
        List<String> varNamesX = VariantNameSorter.sortNames(varNames);
        varNamesX.forEach(System.out::println);
    }
}
