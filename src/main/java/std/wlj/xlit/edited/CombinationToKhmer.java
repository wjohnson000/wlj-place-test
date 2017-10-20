package std.wlj.xlit.edited;

import java.util.Map;
import java.util.TreeMap;

public class CombinationToKhmer {

    static final Map<String,String> enToKm = new TreeMap<>();
    static {
        enToKm.put("CHR",  "\u1780\u17D2\u179A");
        enToKm.put("CL",   "\u1780\u17D2\u179B");
        enToKm.put("KL",   "\u1780\u17D2\u179B");
        enToKm.put("KR",   "\u1780\u17D2\u179A");
        enToKm.put("CR",   "\u1780\u17D2\u179A");
        enToKm.put("GL",   "\u1782\u17D2\u179B");
        enToKm.put("GR",   "\u1782\u17D2\u179A");
        enToKm.put("DR",   "\u178C\u17D2\u179A");
        enToKm.put("TR",   "\u178F\u17D2\u179A");
        enToKm.put("TW",   "\u178F\u17D2\u179C");
        enToKm.put("BL",   "\u1794\u17D2\u179B");
        enToKm.put("BR",   "\u1794\u17D2\u179A");
        enToKm.put("SCHR", "\u1785\u17D2\u179A");
        enToKm.put("PL",   "\u1796\u17D2\u179B");
        enToKm.put("PR",   "\u1796\u17D2\u179A");
        enToKm.put("FL",   "\u179C\u17D2\u179B");
        enToKm.put("PHR",  "\u179C\u17D2\u179A");
        enToKm.put("FR",   "\u179C\u17D2\u179A");
        enToKm.put("SC",   "\u179F\u17D2\u1780");
        enToKm.put("SK",   "\u179F\u17D2\u1780");
        enToKm.put("SL",   "\u179F\u17D2\u179B");
        enToKm.put("SM",   "\u179F\u17D2\u1798");
        enToKm.put("SN",   "\u179F\u17D2\u178E");
        enToKm.put("SP",   "\u179F\u17D2\u1796");
        enToKm.put("ST",   "\u179F\u17D2\u178F");
        enToKm.put("SW",   "\u179F\u17D2\u179C");
    }
    
    public static void main(String...args) {
        enToKm.forEach((k, v) -> System.out.println(k + "\t" + v + "\t" + UtilStuff.getUChars(v)));
    }
}
