package std.wlj.marshal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.familysearch.standards.place.ws.model.PlaceModel;

public class TestMarshal extends BaseTest {
    public static void main(String... args) throws InterruptedException {
        TestMarshal me = new TestMarshal();
//        me.runPlaceModelOneThread();
        me.runPlaceModelFortyThreadsAsJSON();
    }

    public void runPlaceModel() {
        PlaceModel model = this.getPlaceModel();
        String json  = POJOMarshalUtil.toJSON(model);
        String jsonX = POJOMarshalUtil.toJSONPlain(model);
        String xml   = POJOMarshalUtil.toXML(model);

        System.out.println(json);
        System.out.println(jsonX);
        System.out.println(xml);
    }

    public void runPlaceModelOneThread() {
        PlaceModel model = this.getPlaceModel();
        String xml = POJOMarshalUtil.toXML(model);

        long then = System.nanoTime();
        for (int i=0;  i<5000;  i++) {
            String xmlT = POJOMarshalUtil.toXML(model);
            if (! xml.equals(xmlT)) {
                System.out.println("OOPS!!");
            }
        }
        long nnow = System.nanoTime();
        System.out.println("Time: " + (nnow - then) / 1_000_000.0);
    }

    public void runPlaceModelFortyThreadsAsXML() throws InterruptedException {
        PlaceModel model = this.getPlaceModel();
        String xml = POJOMarshalUtil.toXML(model);

        ExecutorService exService = Executors.newFixedThreadPool(40);
        long then = System.nanoTime();
        for (int i=0;  i<40;  i++) {
            Runnable runn = () -> {
                for (int j=0;  j<1000;  j++) {
                    String xmlT = POJOMarshalUtil.toXML(model);
                    if (! xml.equals(xmlT)) {
                        System.out.println("OOPS!!");
                    }
                }
            };
            exService.submit(runn);
        }

        exService.shutdown();
        exService.awaitTermination(320, TimeUnit.SECONDS);

        long nnow = System.nanoTime();
        System.out.println("Time: " + (nnow - then) / 1_000_000.0);
    }

    public void runPlaceModelFortyThreadsAsJSON() throws InterruptedException {
        PlaceModel model = this.getPlaceModel();
        String xml = POJOMarshalUtil.toJSON(model);

        ExecutorService exService = Executors.newFixedThreadPool(40);
        long then = System.nanoTime();
        for (int i=0;  i<40;  i++) {
            Runnable runn = () -> {
                for (int j=0;  j<1000;  j++) {
                    String xmlT = POJOMarshalUtil.toJSON(model);
                    if (! xml.equals(xmlT)) {
                        System.out.println("OOPS!!");
                    }
                }
            };
            exService.submit(runn);
        }

        exService.shutdown();
        exService.awaitTermination(320, TimeUnit.SECONDS);

        long nnow = System.nanoTime();
        System.out.println("Time: " + (nnow - then) / 1_000_000.0);
    }
}
