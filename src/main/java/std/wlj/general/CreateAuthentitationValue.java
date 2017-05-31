package std.wlj.general;

import java.util.Base64;


public class CreateAuthentitationValue {
    public static void main(String...args) {
        String what = "wjohnson000" + ":" + "USYSE730253CBE0E1A6E8D289C68826AE88F";
        System.out.println(Base64.getEncoder().encodeToString(what.getBytes()));
    }
}
