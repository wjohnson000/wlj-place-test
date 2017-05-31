package std.wlj.analysis.dao;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.analysis.dal.impl.RequestDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRequest;

import std.wlj.datasource.DbConnectionManager;

public class InterpRequestDAOCreate {
    public static void main(String...args) {
        RequestDAOImpl interpDAO = new RequestDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        Map<String,String> params = new HashMap<>();
        params.put("parent", "USA");
        params.put("type", "CITY");
        params.put("a-key", "a-value");

        DbRequest newRequest = new DbRequest();
        newRequest.setMd5Hash("hash-for-that-" + System.nanoTime());
        newRequest.setCreateDate(new GregorianCalendar(2015,9,25,11,12,13));
        newRequest.setLastUsedDate(Calendar.getInstance());
        newRequest.setPlaceName("Provo, UT, UT");
        newRequest.setParameters(params);

        DbRequest dbInterp = interpDAO.create(newRequest);
        System.out.println("DB-Interp: " + dbInterp);
        System.out.println("       id: " + dbInterp.getId());
        System.out.println("      md5: " + dbInterp.getMd5Hash());
        System.out.println("   c-date: " + dbInterp.getCreateDate().toInstant());
        System.out.println("   u-date: " + dbInterp.getLastUsedDate().toInstant());
        System.out.println("    place: " + dbInterp.getPlaceName());
        System.out.println("   params: " + dbInterp.getParameters());
    }
}
