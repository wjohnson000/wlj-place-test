package std.wlj.kml;

public class PGModel {
    public int      boundaryId;
    public int      repId;
    public String   name;
    public int      pointCount;
    public int      fromYear;
    public int      toYear;
    public String   boundaryData;
    public String   boundaryDataGeom;
    public boolean  deleteFlag;

    @Override
    public String toString() {
        return boundaryId + " [rep=" + repId + "]: " + name;
    }
}
