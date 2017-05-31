package std.wlj.general;

public class EnvironmentVariables {
    public static void main(String...args) {
        System.getProperties().entrySet().forEach(ee -> System.out.println("PR::" + ee.getKey() + " --> " + ee.getValue()));

        System.getenv().entrySet().forEach(ee -> System.out.println("EV::" + ee.getKey() + " --> " + ee.getValue()));
    }
}
