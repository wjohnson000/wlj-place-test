package std.wlj.general;

import java.util.Base64;


public class CreateAuthentitationValue {
    public static void main(String...args) {
        String what = "wjohnson000" + ":" + "08aefe3f-c45c-44be-a358-6111eb0ba8de-beta";
        System.out.println(Base64.getEncoder().encodeToString(what.getBytes()));
    }
}
