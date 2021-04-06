package std.wlj.hhs.reactor;

import reactor.core.publisher.Mono;

public class MainAppReactive02 {

    static final UserService service = new UserService();

    public static void main(String... args) {
        MainAppReactive01 app = new MainAppReactive01();
        app.runScenario();
        app.runScenario();
        app.runScenario();
    }

    void runScenario() {
        System.out.println("\n===================================");

        long time0 = System.nanoTime();
        Mono<UserData> userM = service.getUserReactive02();
        long time1 = System.nanoTime();
        System.out.println("userM created ...");

        Mono<Boolean> visiM = service.getVisibilityReactive02();
        long time2 = System.nanoTime();
        System.out.println("visiM created ...");

        UserData user = Mono.zip(userM, visiM)
            .flatMap(agg -> processResults(agg.getT1(), agg.getT2()))
            .block();
        long time3 = System.nanoTime();

        System.out.println("USER: " + (user == null ? "null" : user.username));
        System.out.println("TIME0: " + (time1 - time0) / 1_000_000.0);
        System.out.println("TIME1: " + (time2 - time1) / 1_000_000.0);
        System.out.println("TIME2: " + (time3 - time2) / 1_000_000.0);
    }

    Mono<UserData> processResults(UserData user, Boolean visi) {
        System.out.println("USER: " + (user == null ? "null" : user.username) + " --> " + visi);
        return Mono.just(user);
    }
}
