package std.wlj.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TestMultivalueMap {
    public static void main(String...arg) {
        MultivaluedMap<String,String> mvMap = new MultivaluedMapImpl();
        mvMap.putSingle("param1", "value-param1");
        mvMap.put("param2", Arrays.asList("one", "two", "three"));
        mvMap.putSingle("param3", "howdy");
        mvMap.putSingle("page-num", "11");
        mvMap.putSingle("page-size", "42");

        for (Map.Entry<String, List<String>> entry : mvMap.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                System.out.println(key + " --> " + value);
            }
        }

//        mvMap.entrySet().stream()
//            .map(mvEntry -> mvEntry.getValue().stream()
//                    .collect(Collectors.toMap(e -> mvEntry.getKey(), e -> e)))
//            .forEach(xx -> System.out.println(xx));

        List<String> params = new ArrayList<>();
        mvMap.forEach((key, list) -> list.forEach(val -> params.add(key + "=" + val)));
        String paramStr = params.stream()
            .filter(pp -> ! pp.startsWith("page-num"))
            .filter(pp -> ! pp.startsWith("page-size")).collect(Collectors.joining("&", "?", ""));
            
        System.out.println("PS: " + paramStr);
    }
}
