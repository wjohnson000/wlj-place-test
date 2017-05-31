package std.wlj.analysis.dao;

import java.util.List;

import org.familysearch.standards.analysis.dal.impl.ResultDAOImpl;
import org.familysearch.standards.analysis.dal.model.DbResult;

import std.wlj.datasource.DbConnectionManager;

public class InterpResultDAORead {
    public static void main(String...args) {
        ResultDAOImpl interpResultDAO = new ResultDAOImpl(DbConnectionManager.getDataSourceWLJ(), null);

        System.out.println("\nRead by ID ...");
        DbResult dbInterpRes = interpResultDAO.read(1);
        output(dbInterpRes);

        System.out.println("\nRead by MD5-Hash ...");
        dbInterpRes = interpResultDAO.readMd5("hash-for-res-10440420919348");
        output(dbInterpRes);
        
        System.out.println("\nRead by Interp-ID ...");
        List<DbResult> dbInterpRess = interpResultDAO.readByRequest(2);
        output(dbInterpRess);
    }

    static void output(List<DbResult> results) {
        results.stream().forEach(InterpResultDAORead::output);
    }

    static void output(DbResult result) {
        if (result != null) {
            System.out.println("DB-Interp-Res: " + result);
            System.out.println("           id: " + result.getId());
            System.out.println("    interp-id: " + result.getRequestId());
            System.out.println("          md5: " + result.getMd5Hash());
            System.out.println("        count: " + result.getCount());
            System.out.println("         anns: " + result.getAnnotations());
        }
    }
}
