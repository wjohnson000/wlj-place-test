package std.wlj.analysis.dao;

import java.util.List;

import org.familysearch.standards.analysis.dal.impl.RequestDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbRequest;

import std.wlj.datasource.DbConnectionManager;

public class InterpRequestDAORead {
    public static void main(String...args) {
        RequestDAOImpl interpDAO = new RequestDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        System.out.println("\nRead by ID ... 2");
        DbRequest dbInterp = interpDAO.read(2);
        output(dbInterp);

        System.out.println("\nRead by MD5-Hash ... found");
        dbInterp = interpDAO.readMd5("hash-for-that-9990489577425");
        output(dbInterp);

        System.out.println("\nRead by MD5-Hash ... NOT found");
        dbInterp = interpDAO.readMd5("hash-for-that-blah");
        output(dbInterp);

        System.out.println("\nSearch by place-name ... case-match");
        List<DbRequest> dbInterps = interpDAO.search("Provo, UT, UT");
        output(dbInterps);

        System.out.println("\nSearch by place-name ... NO case-match");
        dbInterps = interpDAO.search("provo, ut, UT");
        output(dbInterps);

        System.out.println("\nSearch by place-name ... NOT found");
        dbInterps = interpDAO.search("Provo, UT, UT");
        output(dbInterps);
    }

    static void output(List<DbRequest> results) {
        results.stream().forEach(InterpRequestDAORead::output);
    }

    static void output(DbRequest request) {
        if (request != null) {
            System.out.println("DB-Interp: " + request);
            System.out.println("       id: " + request.getId());
            System.out.println("      md5: " + request.getMd5Hash());
            System.out.println("   c-date: " + request.getCreateDate().toInstant());
            System.out.println("   u-date: " + request.getLastUsedDate().toInstant());
            System.out.println("    place: " + request.getPlaceName());
            System.out.println("   params: " + request.getParameters());
        }
    }

}
