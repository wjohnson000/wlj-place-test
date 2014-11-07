package std.wlj.poi;

public class ZzzTestDbEngine {
    public static void main(String... args) {
        DbEngine dbEngine = new DbEngine("jdbc:postgresql://localhost:5432/nb", "nb", "nb");

        dbEngine.initialize();
        dbEngine.resetSequences();

        int tsId = dbEngine.addTruthSet("placerep", "WLJ Test Yippee!!", "123.456.A");
        int trId = dbEngine.addTruth(tsId, "Test-String-01", "a=1^b=2^c=3");
        dbEngine.addTruthValue(trId, "Test-String-01-01", "a=1^b=2^c=3", 100);
        dbEngine.addTruthValue(trId, "Test-String-01-02", "a=1^b=2^c=3", 99);

        dbEngine.shutdown();

        System.exit(0);
    }
}
