package std.wlj.hhs.reactor;

public class MainAppRegular {

    static final UserService service = new UserService();

    public static void main(String... args) {
        runScenario();
        runScenario();
        runScenario();
    }

    static void runScenario() {
        System.out.println("\n===================================");

        long time0 = System.nanoTime();
        UserData user = service.getUser();
        long time1 = System.nanoTime();

        boolean visi = service.getVisibility();
        long time2 = System.nanoTime();

        System.out.println("USER: " + (user == null ? "null" : user.username) + " --> " + visi);
        System.out.println("TIME0: " + (time1 - time0) / 1_000_000.0);
        System.out.println("TIME1: " + (time2 - time1) / 1_000_000.0);
        System.out.println("TIMEX: " + (time2 - time0) / 1_000_000.0);
    }
}
