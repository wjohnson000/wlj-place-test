package std.wlj.hhs.reactor;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class UserService {

    public UserData getUser() {
        System.out.println("Starting get user ...");
        try { Thread.sleep(4000L); } catch(Exception ex) { }
        System.out.println("Ending get user ...");
        return new UserData();
    }

    public Boolean getVisibility() {
        System.out.println("Starting get visibility ...");
        try { Thread.sleep(2500L); } catch(Exception ex) { }
        System.out.println("Ending get visibility ...");
        return true;
    }

    public Mono<UserData> getUserReactive01() {
        return Mono.fromCallable(() -> getUser())
                   .subscribeOn(Schedulers.elastic());
    }

    public Mono<UserData> getUserReactive02() {
        return Mono.defer(() -> Mono.just(getUser()))
                   .subscribeOn(Schedulers.elastic());
    }

    public Mono<Boolean> getVisibilityReactive01() {
        return Mono.fromCallable(() -> getVisibility())
                   .subscribeOn(Schedulers.elastic());
    }

    public Mono<Boolean> getVisibilityReactive02() {
        return Mono.defer(() -> Mono.just(getVisibility()))
                   .subscribeOn(Schedulers.elastic());
    }
}
