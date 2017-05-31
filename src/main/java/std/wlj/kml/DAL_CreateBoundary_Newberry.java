package std.wlj.kml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.model.DbRepBoundary;
import org.familysearch.standards.place.ws.model.kml.*;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.marshal.POJOMarshalUtil;

public class DAL_CreateBoundary_Newberry {

    static final String kmlFileDir = "D:/postgis/newberry/rep-boundary";

    // thread-safe formatter
    private static DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withResolverStyle(ResolverStyle.SMART);

    public static void main(String... args) throws IOException {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceSams());

        String[] files = new File(kmlFileDir).list();
        Arrays.stream(files)
            .filter(file -> file.contains("-IN-"))
            .filter(file -> file.startsWith("392684"))
            .forEach(file -> loadFile(daoFactory, file));
    }

    static void loadFile(DAOFactory daoFactory, String file) {
        int ndx = file.indexOf('-');
        int repId = Integer.parseInt(file.substring(0, ndx));
        System.out.println(repId + " --> " + file);

        try {
            byte[] kmlByte = Files.readAllBytes(Paths.get(kmlFileDir, file));
            String kmlString = new String(kmlByte);
            if (kmlString.charAt(0) != '<') {
                kmlString = kmlString.substring(1);
            }
            
            KmlModel kmlModel = POJOMarshalUtil.fromXML(kmlString, KmlModel.class);
            
            if (kmlModel.getDocument().getPlacemarksAll().size() == 1) {
                PlacemarkModel pmModel = kmlModel.getDocument().getPlacemarksAll().get(0);
                DbRepBoundary repBoundary = new DbRepBoundary();
                repBoundary.setRepId(repId);
                repBoundary.setPointCount((int)getPointCount(pmModel));
                repBoundary.setFromYear(getFromYear(pmModel));
                repBoundary.setToYear(getToYear(pmModel));
                repBoundary.setGeographyData(getGeography(pmModel));
                daoFactory.getRepBoundaryDAO().create(repBoundary);
            }
        } catch (IOException ex) {
            System.out.println("Unable to create boundary ... yeech!!");
        }
    }

    protected static String getGeography(PlacemarkModel placemark) {
        return placemark.getGeometryAsXML();
    }

    protected static Integer getFromYear(PlacemarkModel placemark) {
        if (placemark.getTimeSpan() == null) {
            return null;
        } else if (placemark.getTimeSpan().getBegin() == null) {
            return null;
        } else {
            return getYearFromDateString(placemark.getTimeSpan().getBegin());
        }
    }

    protected static Integer getToYear(PlacemarkModel placemark) {
        if (placemark.getTimeSpan() == null) {
            return null;
        } else if (placemark.getTimeSpan().getEnd() == null) {
            return null;
        } else {
            return getYearFromDateString(placemark.getTimeSpan().getEnd());
        }
    }

    protected static long getPointCount(PlacemarkModel placemark) {
        return placemark.getGeometry().getPointCount();
    }

    protected static int getYearFromDateString(String dateString) {
        TemporalAccessor tacc = DATE_TIME_FORMATTER.parse(dateString, new ParsePosition(0));
        return tacc.get(ChronoField.YEAR);
    }

}
